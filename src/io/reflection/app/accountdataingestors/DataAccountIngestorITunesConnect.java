//
//  DataAccountIngestorITunesConnect.java
//  storedata
//
//  Created by William Shakour (billy1380) on 22 Jan 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.accountdataingestors;

import io.reflection.app.api.exception.DataAccessException;
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
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.google.appengine.api.taskqueue.TransientFailureException;
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
	private static final int CUSTOMER_CURRENCY_INDEX = 12;
	private static final int COUNTRY_CODE_INDEX = 13;
	private static final int CURRENCY_OF_PROCEEDS_INDEX = 14;
	private static final int APPLE_IDENTIFIER_INDEX = 15;
	private static final int CUSTOMER_PRICE_INDEX = 16;
	private static final int PROMO_CODE_INDEX = 17;
	private static final int PARENT_IDENTIFIER_INDEX = 18;
	private static final int SUBSCRIPTION_INDEX = 19;
	private static final int PERIOD_INDEX = 20;
	private static final int CATEGORY_INDEX = 21;

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.accountdataingestors.DataAccountIngestor#ingest(io.reflection.app.datatypes.shared.DataAccountFetch)
	 */
	@Override
	public void ingest(DataAccountFetch fetch) {

		if (fetch.status == DataAccountFetchStatusType.DataAccountFetchStatusTypeGathered) {
			try {
				List<Sale> sales = convertFetchToSales(fetch);

				Long count = SaleServiceProvider.provide().addSalesBatch(sales);

				if (count != null && count.longValue() > 0) {
					fetch.status = DataAccountFetchStatusType.DataAccountFetchStatusTypeIngested;

					fetch = DataAccountFetchServiceProvider.provide().updateDataAccountFetch(fetch);
				}

			} catch (IOException e) {
				LOG.log(GaeLevel.SEVERE, String.format("Exception throw while parsing file for data account fetch [%d]", fetch.id.longValue()), e);
			} catch (DataAccessException e) {
				LOG.log(GaeLevel.SEVERE, String.format("Exception throw while ingesting file for data account fetch [%d]", fetch.id.longValue()), e);
			}
		} else {
			LOG.log(GaeLevel.INFO,
					String.format("Could not ingest data account fetch [%d] because it has status", fetch.id.longValue(), fetch.status.toString()));
		}
	}

	/**
	 * @param fetch
	 * @return
	 */
	private List<Sale> convertFetchToSales(DataAccountFetch fetch) throws IOException {

		List<Sale> sales = null;

		String path = fetch.data;
		String bucketName = null;
		String fileName = null;

		if (path.startsWith("/gs/")) {
			path = path.replace("/gs/", "");
		}

		int indexOfLastSlash = path.lastIndexOf('/');

		if (indexOfLastSlash != -1) {
			fileName = path.substring(indexOfLastSlash + 1);

			bucketName = path.substring(0, indexOfLastSlash);
		}

		if (bucketName != null && fileName != null) {
			GcsService fileService = GcsServiceFactory.createGcsService();
			GcsFilename cloudFileName = new GcsFilename(bucketName, fileName);

			String line;
			BufferedReader stream = null;

			try {
				GcsInputChannel channel = fileService.openReadChannel(cloudFileName, 0);

				GZIPInputStream gzip = new GZIPInputStream(Channels.newInputStream(channel));

				BufferedReader csvFile = new BufferedReader(new InputStreamReader(gzip));

				stream = new BufferedReader(csvFile);

				sales = new ArrayList<Sale>();

				while ((line = stream.readLine()) != null) {
					Sale sale = convertToSale(line, fetch);

					if (sale != null) {
						sales.add(sale);
					}
				}

			} finally {
				stream.close();
			}

		}

		return sales;
	}

	private Sale convertToSale(String line, DataAccountFetch fetch) {
		Sale sale = null;
		if (line != null && line.length() > 0) {
			String[] split = line.split("\\t");

			sale = new Sale();

			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);

			sale.item = new Item();
			sale.item.internalId = split[APPLE_IDENTIFIER_INDEX];

			sale.account = fetch.linkedAccount;

			// sale.proceeds split[PROVIDER_INDEX];
			// split[PROVIDER_COUNTRY_INDEX];
			sale.sku = split[SKU_INDEX];
			sale.developer = split[DEVELOPER_INDEX];
			sale.title = split[TITLE_INDEX];
			sale.version = split[VERSION_INDEX];
			sale.typeIdentifier = split[PRODUCT_TYPE_IDENTIFIER_INDEX];
			sale.units = Integer.parseInt(split[UNITS_INDEX]);
			sale.proceeds = Integer.valueOf((int) (100.0f * Float.parseFloat(split[DEVELOPER_PROCEEDS_INDEX])));

			try {
				sale.begin = sdf.parse(split[BEGIN_DATE_INDEX]);
				sale.end = sdf.parse(split[END_DATE_INDEX]);
			} catch (ParseException e) {
				LOG.log(GaeLevel.SEVERE, String.format("Exception throw while obtaining file for data account [%d]", fetch.id.longValue()), e);
			}

			sale.customerCurrency = split[CUSTOMER_CURRENCY_INDEX];
			sale.country = split[COUNTRY_CODE_INDEX];
			sale.currency = split[CURRENCY_OF_PROCEEDS_INDEX];

			sale.item = new Item();
			sale.item.internalId = split[APPLE_IDENTIFIER_INDEX];

			sale.customerPrice = Integer.valueOf((int) (100.0f * Float.parseFloat(split[CUSTOMER_PRICE_INDEX])));
			sale.promoCode = split[PROMO_CODE_INDEX];
			sale.parentIdentifier = split[PARENT_IDENTIFIER_INDEX];
			sale.subscription = split[SUBSCRIPTION_INDEX];
			sale.period = split[PERIOD_INDEX];
			sale.category = split[CATEGORY_INDEX];

		}

		return sale;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.accountdataingestors.DataAccountIngestor#enqueue(io.reflection.app.datatypes.shared.DataAccountFetch)
	 */
	@Override
	public void enqueue(DataAccountFetch fetch) {
		if (LOG.isLoggable(GaeLevel.TRACE)) {
			LOG.log(GaeLevel.TRACE, "Entering...");
		}

		try {
			Queue queue = QueueFactory.getQueue("dataaccountingest");

			TaskOptions options = TaskOptions.Builder.withUrl("/dataaccountingest").method(Method.POST);

			options.param("fetchId", fetch.id.toString());

			try {
				queue.add(options);
			} catch (TransientFailureException ex) {

				if (LOG.isLoggable(Level.WARNING)) {
					LOG.warning(String.format("Could not queue a message because of [%s] - will retry it once", ex.toString()));
				}

				// retry once
				try {
					queue.add(options);
				} catch (TransientFailureException reEx) {
					if (LOG.isLoggable(Level.SEVERE)) {
						LOG.log(Level.SEVERE,
								String.format("Retry of with payload [%s] failed while adding to queue [%s] twice", options.toString(), queue.getQueueName()),
								reEx);
					}
				}

			}
		} finally {
			if (LOG.isLoggable(GaeLevel.TRACE)) {
				LOG.log(GaeLevel.TRACE, "Exiting...");
			}
		}

	}

}
