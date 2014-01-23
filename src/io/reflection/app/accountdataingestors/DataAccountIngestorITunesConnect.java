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

			SimpleDateFormat sdf = new SimpleDateFormat("mm/dd/yyyy", Locale.ENGLISH);

			sale.item = new Item();
			sale.item.externalId = split[14];

			sale.account = fetch.linkedAccount;

			sale.country = split[1];
			sale.sku = split[2];
			sale.developer = split[3];
			sale.title = split[4];
			sale.version = split[5];
			sale.typeIdentifier = split[6];
			sale.units = split[7];
			// sale.proceeds=Integer.parseInt(split[8)]; //is it an integer?
			sale.proceeds = 1; // TODO temporary

			try {
				sale.begin = sdf.parse(split[9]);
				sale.end = sdf.parse(split[10]);
			} catch (ParseException e) {
				LOG.log(GaeLevel.SEVERE, String.format("Exception throw while obtaining file for data account [%d]", fetch.id.longValue()), e);
			}

			sale.currency = split[11];
			// sale.xxx=split[12]; //TODO temporary, it's the country code but the data are not present in the db
			sale.customerCurrency = split[13]; // TODO temporary, I'm not sure

			// sale.customerPrice=Integer.parseInt(split[15)]; //is the number integer?
			sale.customerPrice = 1; // TODO temporary
			sale.promoCode = split[15];
			sale.parentIdentifier = split[17];
			sale.subscription = split[18];
			sale.period = split[19];
			sale.category = split[20];
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
