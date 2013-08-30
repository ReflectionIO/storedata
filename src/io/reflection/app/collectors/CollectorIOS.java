/**
 * 
 */
package io.reflection.app.collectors;

import io.reflection.app.logging.GaeLevel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.google.appengine.api.taskqueue.TransientFailureException;
import com.google.gson.JsonObject;
import com.willshex.gson.json.shared.Convert;

/**
 * @author billy1380
 * 
 */
public class CollectorIOS extends StoreCollector implements Collector {

	private static final Logger LOG = Logger.getLogger(CollectorIOS.class.getName());

	private static final String IOS_STORE_A3 = "ios";
	private static final String COUNTRIES_KEY = "gather.ios.countries";

	private static final String KEY_FORMAT = "gather.ios.%s";

	public static final String TOP_FREE_APPS = "topfreeapplications";
	public static final String TOP_PAID_APPS = "toppaidapplications";
	public static final String TOP_GROSSING_APPS = "topgrossingapplications";
	public static final String TOP_FREE_IPAD_APPS = "topfreeipadapplications";
	public static final String TOP_PAID_IPAD_APPS = "toppaidipadapplications";
	public static final String TOP_GROSSING_IPAD_APPS = "topgrossingipadapplications";

	// public static final String NEW_APPS = "newapplications";
	// public static final String NEW_FREE_APPS = "newfreeapplications";
	// public static final String NEW_PAID_APPS = "newpaidapplications";

	@Override
	public int enqueue() {
		if (LOG.isLoggable(GaeLevel.TRACE)) {
			LOG.log(GaeLevel.TRACE, "Entering...");
		}

		int count = 0;

		try {
			String countries = System.getProperty(COUNTRIES_KEY);
			if (LOG.isLoggable(GaeLevel.DEBUG)) {
				LOG.log(GaeLevel.DEBUG, String.format("Found countries [%s].", countries));
			}

			List<String> splitCountries = new ArrayList<String>();
			Collections.addAll(splitCountries, countries.split(","));

			if (LOG.isLoggable(GaeLevel.DEBUG)) {
				LOG.log(GaeLevel.DEBUG, String.format("[%d] countries to fetch data for", splitCountries.size()));
			}

			Queue queue = QueueFactory.getQueue("gather");

			if (queue != null) {

				String code = UUID.randomUUID().toString();
				for (String countryCode : splitCountries) {

					enqueue(queue, countryCode, TOP_FREE_APPS, code);
					enqueue(queue, countryCode, TOP_PAID_APPS, code);
					enqueue(queue, countryCode, TOP_GROSSING_APPS, code);
					enqueue(queue, countryCode, TOP_FREE_IPAD_APPS, code);
					enqueue(queue, countryCode, TOP_PAID_IPAD_APPS, code);
					enqueue(queue, countryCode, TOP_GROSSING_IPAD_APPS, code);
					// enqueue(queue, countryCode, NEW_APPS, code);
					// enqueue(queue, countryCode, NEW_FREE_APPS, code);
					// enqueue(queue, countryCode, NEW_PAID_APPS, code);

					count++;
				}

			}
		} finally {
			if (LOG.isLoggable(GaeLevel.TRACE)) {
				LOG.log(GaeLevel.TRACE, "Exiting...");
			}
		}

		return count;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.appcollector.collectors.DataCollector#collect()
	 */
	@Override
	public List<Long> collect(String country, String type, String code) {
		if (LOG.isLoggable(GaeLevel.TRACE)) {
			LOG.log(GaeLevel.TRACE, "Entering...");
		}

		String data;
		List<Long> ids = null;

		try {

			data = get(country, type);

			if (data != null && data.length() > 0) {
				// before storeing the data make sure that it is a valid json object
				JsonObject parsed = Convert.toJsonObject(data);

				if (parsed == null) {
					throw new RuntimeException("The data could not be parsed or parsing it returned a null json object");
				}

				ids = store(data, country, IOS_STORE_A3, type, new Date(), code);
			} else {
				if (LOG.isLoggable(GaeLevel.TRACE)) {
					LOG.log(GaeLevel.TRACE, "Obtained data was empty for country [" + country + "], type [" + type + "] and code [" + code + "]");
				}
			}

		} finally {
			if (LOG.isLoggable(GaeLevel.TRACE)) {
				LOG.log(GaeLevel.TRACE, "Exiting...");
			}
		}

		return ids;
	}

	private String get(String countryCode, String type) {
		if (LOG.isLoggable(GaeLevel.DEBUG)) {
			LOG.log(GaeLevel.DEBUG, String.format("Getting data for [%s] and type [%s]", countryCode, type));
		}

		String key = String.format(KEY_FORMAT, type);

		if (LOG.isLoggable(GaeLevel.DEBUG)) {
			LOG.log(GaeLevel.DEBUG, String.format("key is [%s]", key));
		}

		String endpoint = String.format(System.getProperty(key), countryCode);

		return HttpExternalGetter.getData(endpoint);
	}

	private void enqueue(Queue queue, String country, String type, String code) {
		try {
			queue.add(TaskOptions.Builder.withUrl(String.format(ENQUEUE_GATHER_FORMAT, IOS_STORE_A3, country, type, code)).method(Method.GET));
		} catch (TransientFailureException ex) {

			if (LOG.isLoggable(Level.WARNING)) {
				LOG.warning(String.format("Could not queue a message because of [%s] - will retry it once", ex.toString()));
			}

			// retry once
			try {
				queue.add(TaskOptions.Builder.withUrl(String.format(ENQUEUE_GATHER_FORMAT, IOS_STORE_A3, country, type, code)).method(Method.GET));
			} catch (TransientFailureException reEx) {
				if (LOG.isLoggable(Level.SEVERE)) {
					LOG.log(Level.SEVERE, String.format("Retry of with parameters country [%s] type [%s] code [%s] failed while adding to queue [%s] twice",
							country, type, code, queue.getQueueName()), reEx);
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see io.reflection.app.collectors.Collector#isGrossing(java.lang.String)
	 */
	@Override
	public boolean isGrossing(String type) {
		return type.equalsIgnoreCase(TOP_GROSSING_IPAD_APPS) || type.equalsIgnoreCase(TOP_GROSSING_APPS);
	}

}
