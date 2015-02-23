//
//  DataAccountIngestorITunesConnect.java
//  storedata
//
//  Created by William Shakour (billy1380) on 22 Jan 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.accountdataingestors;

import io.reflection.app.accountdatacollectors.DataAccountCollector;
import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.apple.ItemPropertyLookupServlet;
import io.reflection.app.datatypes.shared.DataAccountFetch;
import io.reflection.app.datatypes.shared.DataAccountFetchStatusType;
import io.reflection.app.datatypes.shared.Item;
import io.reflection.app.datatypes.shared.Sale;
import io.reflection.app.logging.GaeLevel;
import io.reflection.app.service.dataaccountfetch.DataAccountFetchServiceProvider;
import io.reflection.app.service.sale.SaleServiceProvider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.channels.Channels;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsInputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;

/**
 * @author billy1380
 * 
 */
public class DataAccountIngestorITunesConnect implements DataAccountIngestor {

	private static final Logger LOG = Logger.getLogger(DataAccountIngestorITunesConnect.class.getName());

	@SuppressWarnings("unused") private static final int PROVIDER_INDEX = 0; // seems to always be apple
	@SuppressWarnings("unused") private static final int PROVIDER_COUNTRY_INDEX = 1; // seems to always be US
	private static final int SKU_INDEX = 2;
	private static final int DEVELOPER_INDEX = 3;
	private static final int TITLE_INDEX = 4;
	private static final int VERSION_INDEX = 5;
	private static final int PRODUCT_TYPE_IDENTIFIER_INDEX = 6;
	private static final int UNITS_INDEX = 7;
	private static final int DEVELOPER_PROCEEDS_INDEX = 8;
	private static final int BEGIN_DATE_INDEX = 9;
	private static final int END_DATE_INDEX = 10;
	private static final int CUSTOMER_CURRENCY_INDEX = 11;
	private static final int COUNTRY_CODE_INDEX = 12;
	private static final int CURRENCY_OF_PROCEEDS_INDEX = 13;
	private static final int APPLE_IDENTIFIER_INDEX = 14;
	private static final int CUSTOMER_PRICE_INDEX = 15;
	private static final int PROMO_CODE_INDEX = 16;
	private static final int PARENT_IDENTIFIER_INDEX = 17;
	private static final int SUBSCRIPTION_INDEX = 18;
	private static final int PERIOD_INDEX = 19;
	private static final int CATEGORY_INDEX = 20;

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.accountdataingestors.DataAccountIngestor#ingest(io.reflection.app.datatypes.shared.DataAccountFetch)
	 */
	@Override
	public void ingest(DataAccountFetch fetch) {

		if (fetch.status == DataAccountFetchStatusType.DataAccountFetchStatusTypeGathered) {

			List<Sale> sales = null;
			try {
				sales = convertFetchToSales(fetch);

				Long count = SaleServiceProvider.provide().addSalesBatch(sales);

				if (count != null && count.longValue() > 0) {
					fetch.status = DataAccountFetchStatusType.DataAccountFetchStatusTypeIngested;

					fetch = DataAccountFetchServiceProvider.provide().updateDataAccountFetch(fetch);

					// ArchiverFactory.getItemSaleArchiver().enqueueIdDataAccountFetch(fetch.id);
				}
			} catch (DataAccessException e) {
				LOG.log(GaeLevel.SEVERE, String.format("Exception occured while ingesting file for data account fetch [%d]", fetch.id.longValue()), e);
			} catch (IOException e) {
				LOG.log(GaeLevel.SEVERE, String.format("Exception occured while parsing sales for data account fetch [%d]", fetch.id.longValue()), e);
			}

			// once all is done, push the sales to biq query too
			if (sales != null && sales.size() > 0) {
				try {
					SaleServiceProvider.provideBigQuery().addSalesBatch(sales);
				} catch (DataAccessException e) {
					LOG.log(GaeLevel.WARNING,
							String.format("Exception occured while adding data to big query for data account fetch[%d]", fetch.id.longValue()), e);
				}
			}
		} else {
			LOG.log(GaeLevel.WARNING,
					String.format("Could not ingest data account fetch [%d] because it has status [%s]", fetch.id.longValue(), fetch.status.toString()));
		}
	}

	/**
	 * @param fetch
	 * @return
	 * @throws IOException
	 */
	private List<Sale> convertFetchToSales(DataAccountFetch fetch) throws IOException {

		List<Sale> sales = null;

		String path = fetch.data;
		String bucketName = null;
		String fileName = null;

		String gatherBucketName = System.getProperty(DataAccountCollector.ACCOUNT_DATA_BUCKET_KEY);

		if (path.contains(gatherBucketName)) {
			bucketName = gatherBucketName;
		}

		if (path.startsWith("/gs/")) {
			path = path.replace("/gs/", "");
		}

		fileName = path.replace(bucketName, "");

		if (fileName.startsWith("/")) {
			fileName = fileName.substring(1);
		}

		if (bucketName != null && fileName != null) {
			GcsService fileService = GcsServiceFactory.createGcsService();
			GcsFilename cloudFileName = new GcsFilename(bucketName, fileName);

			String line;
			BufferedReader reader = null;
			GcsInputChannel readChannel = null;
			GZIPInputStream gzipInputStream = null;

			try {
				readChannel = fileService.openReadChannel(cloudFileName, 0);

				gzipInputStream = new GZIPInputStream(Channels.newInputStream(readChannel));

				reader = new BufferedReader(new InputStreamReader(gzipInputStream));

				sales = new ArrayList<Sale>();

				Set<String> items = new HashSet<String>();
				boolean inBody = false;

				while ((line = reader.readLine()) != null) {
					// skip the first line
					if (inBody) {
						Sale sale = convertToSale(line, fetch, items);

						if (sale != null) {
							sales.add(sale);
						}
					} else {
						inBody = true;
					}
				}

				for (String internalItemId : items) {
					ItemPropertyLookupServlet.enqueueItem(internalItemId, ItemPropertyLookupServlet.ADD_IF_NEW_ACTION);
				}

			} finally {
				if (reader != null) {
					try {
						reader.close();
					} catch (IOException e) {
						LOG.log(GaeLevel.WARNING, "Exception occured while closing reader", e);
					}
				}

				if (gzipInputStream != null) {
					try {
						gzipInputStream.close();
					} catch (IOException e) {
						LOG.log(GaeLevel.WARNING, "Exception occured while closing gzip stream", e);
					}
				}

				if (readChannel != null) {
					readChannel.close();
				}
			}

		}

		return sales;
	}

	private Sale convertToSale(String line, DataAccountFetch fetch, Set<String> items) {
		Sale sale = null;
		if (line != null && line.length() > 0) {
			String[] split = line.split("\\t");

			sale = new Sale();

			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);

			sale.item = new Item();
			sale.item.internalId = split[APPLE_IDENTIFIER_INDEX];

			if (sale.item.internalId != null && sale.item.internalId.trim().length() > 0) {
				items.add(sale.item.internalId.trim());
			}

			sale.account = fetch.linkedAccount;
			sale.fetch = fetch;

			// sale.proceeds split[PROVIDER_INDEX];
			// split[PROVIDER_COUNTRY_INDEX];
			sale.sku = split[SKU_INDEX];
			sale.developer = split[DEVELOPER_INDEX];
			sale.title = split[TITLE_INDEX];
			sale.version = split[VERSION_INDEX];
			sale.typeIdentifier = split[PRODUCT_TYPE_IDENTIFIER_INDEX];
			sale.units = Integer.parseInt(split[UNITS_INDEX]);

			Float proceeds = null;
			try {
				proceeds = Float.valueOf(split[DEVELOPER_PROCEEDS_INDEX]);
			} catch (NumberFormatException e) {
				LOG.log(GaeLevel.WARNING, String.format("Exception occured while obtaining file for data account [%d]", fetch.id.longValue()), e);
			}

			sale.proceeds = proceeds;

			try {
				sale.begin = sdf.parse(split[BEGIN_DATE_INDEX]);
				sale.end = sdf.parse(split[END_DATE_INDEX]);
			} catch (ParseException e) {
				LOG.log(GaeLevel.WARNING, String.format("Exception occured while obtaining file for data account [%d]", fetch.id.longValue()), e);
			}

			sale.customerCurrency = split[CUSTOMER_CURRENCY_INDEX];
			sale.country = split[COUNTRY_CODE_INDEX].toLowerCase();
			sale.currency = split[CURRENCY_OF_PROCEEDS_INDEX];

			sale.item = new Item();
			sale.item.internalId = split[APPLE_IDENTIFIER_INDEX];

			Float customerPrice = null;
			try {
				customerPrice = Float.valueOf(split[CUSTOMER_PRICE_INDEX]);
			} catch (NumberFormatException e) {
				LOG.log(GaeLevel.WARNING, String.format("Exception occured while obtaining file for data account [%d]", fetch.id.longValue()), e);
			}

			sale.customerPrice = customerPrice;
			sale.promoCode = split[PROMO_CODE_INDEX];
			sale.parentIdentifier = split[PARENT_IDENTIFIER_INDEX];
			sale.subscription = split[SUBSCRIPTION_INDEX];
			sale.period = split[PERIOD_INDEX];
			sale.category = split[CATEGORY_INDEX];
		}

		return sale;
	}

}
