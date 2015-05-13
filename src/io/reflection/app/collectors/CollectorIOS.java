/**
 *
 */
package io.reflection.app.collectors;

import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.datatypes.shared.Category;
import io.reflection.app.datatypes.shared.Store;
import io.reflection.app.logging.GaeLevel;
import io.reflection.app.service.category.CategoryServiceProvider;
import io.reflection.app.service.feedfetch.FeedFetchServiceProvider;
import io.reflection.app.service.store.StoreServiceProvider;
import io.reflection.app.shared.util.DataTypeHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
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

	private static final String COUNTRIES_KEY = "gather." + DataTypeHelper.IOS_STORE_A3 + ".countries";
	private static final String GATHER_CATEGORIES_KEY = "gather." + DataTypeHelper.IOS_STORE_A3 + ".categories";

	private static final String KEY_FORMAT = "gather." + DataTypeHelper.IOS_STORE_A3 + ".%s";
	private static final String KEY_CATEGORY_FEED = "gather." + DataTypeHelper.IOS_STORE_A3 + ".category.feed.url";

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
			final String countries = System.getProperty(COUNTRIES_KEY);
			if (LOG.isLoggable(GaeLevel.DEBUG)) {
				LOG.log(GaeLevel.DEBUG, String.format("Found countries [%s].", countries));
			}

			final List<String> splitCountries = new ArrayList<String>();
			Collections.addAll(splitCountries, countries.split(","));

			if (LOG.isLoggable(GaeLevel.DEBUG)) {
				LOG.log(GaeLevel.DEBUG, String.format("[%d] countries to fetch data for", splitCountries.size()));
			}

			final Queue queue = QueueFactory.getQueue("gather");

			if (queue != null) {

				Long code = null;

				try {
					code = FeedFetchServiceProvider.provide().getCode();

					if (code == null && LOG.isLoggable(GaeLevel.DEBUG)) {
						LOG.log(GaeLevel.DEBUG, "Got null code");
					} else {
						LOG.log(GaeLevel.DEBUG, String.format("Got code [%d] from feed fetch service", code.longValue()));
					}

					for (final String countryCode : splitCountries) {
						if (LOG.isLoggable(GaeLevel.DEBUG)) {
							LOG.log(GaeLevel.DEBUG, String.format("Enqueueing gather tasks for country [%s]", countryCode));
						}

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
				} catch (final DataAccessException dae) {
					LOG.log(Level.SEVERE, "A database error occured attempting to to get an id code for gather enqueueing", dae);
				}

				final String strGatherAllCategories = System.getProperty(GATHER_CATEGORIES_KEY);
				final boolean gatherAllCategories = Boolean.valueOf(strGatherAllCategories);
				if (gatherAllCategories) {
					if (code != null) { // when we have gathered all the counties feeds we do the same but for each category
						try {
							final Store store = StoreServiceProvider.provide().getA3CodeStore(DataTypeHelper.IOS_STORE_A3);

							// get the parent category all which references all lower categories
							final Category all = CategoryServiceProvider.provide().getAllCategory(store);

							List<Category> categories = null;

							if (all != null) {
								final Pager p = new Pager();
								p.start = Pager.DEFAULT_START;
								p.sortBy = Pager.DEFAULT_SORT_BY;
								p.count = CategoryServiceProvider.provide().getParentCategoriesCount(all);

								if (p.count != null && p.count.longValue() > 0) {
									categories = CategoryServiceProvider.provide().getParentCategories(all, p);

									if (categories != null && categories.size() > 0) {
										if (LOG.isLoggable(GaeLevel.DEBUG)) {
											LOG.log(GaeLevel.DEBUG, String.format("Found [%d] categories", categories.size()));
										}

										// if we have some categories iterate through all the countries again
										for (final String countryCode : splitCountries) {

											if (LOG.isLoggable(GaeLevel.DEBUG)) {
												LOG.log(GaeLevel.DEBUG, String.format("Enqueueing gather tasks for country [%s]", countryCode));
											}

											// for each category
											for (final Category category : categories) {

												if (LOG.isLoggable(GaeLevel.DEBUG)) {
													LOG.log(GaeLevel.DEBUG, String.format("Enqueueing gather tasks for category [%d]", category.id.longValue()));
												}

												enqueue(queue, countryCode, TOP_FREE_APPS, category.internalId, code);
												enqueue(queue, countryCode, TOP_PAID_APPS, category.internalId, code);
												enqueue(queue, countryCode, TOP_GROSSING_APPS, category.internalId, code);
												enqueue(queue, countryCode, TOP_FREE_IPAD_APPS, category.internalId, code);
												enqueue(queue, countryCode, TOP_PAID_IPAD_APPS, category.internalId, code);
												enqueue(queue, countryCode, TOP_GROSSING_IPAD_APPS, category.internalId, code);

												count++;
											}
										}
									}
								}
							}
						} catch (final DataAccessException dae) {
							LOG.log(Level.SEVERE, "A database error occured attempting to enqueue category top feeds for gathering phase", dae);
						}
					}
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
	 * @see io.reflection.app.collectors.Collector#collect(java.lang.String, java.lang.String, java.lang.String, java.lang.Long)
	 */
	@Override
	public List<Long> collect(String country, String type, String category, Long code) throws DataAccessException {
		if (LOG.isLoggable(GaeLevel.TRACE)) {
			LOG.log(GaeLevel.TRACE, "Entering...");
		}

		String data;
		List<Long> ids = null;
		Long categoryInternalId = null;

		try {

			if (category != null) {
				try {
					categoryInternalId = Long.parseLong(category);
				} catch (final NumberFormatException ne) {
					if (LOG.isLoggable(GaeLevel.DEBUG)) {
						LOG.log(GaeLevel.DEBUG, String.format("Could not parse category to long [%s]", category), ne);
					}
				}
			}

			data = get(country, type, categoryInternalId);

			if (data != null && data.length() > 0) {
				// before storeing the data make sure that it is a valid json object
				final JsonObject parsed = Convert.toJsonObject(data);

				if (parsed == null) throw new RuntimeException("The data could not be parsed or parsing it returned a null json object");

				ids = store(data, country, DataTypeHelper.IOS_STORE_A3, type, categoryInternalId, new Date(), code);
			} else if (LOG.isLoggable(GaeLevel.TRACE)) {
				LOG.log(GaeLevel.TRACE, "Obtained data was empty for country [" + country + "], type [" + type + "] and code [" + code + "]");
			}

		} finally {
			if (LOG.isLoggable(GaeLevel.TRACE)) {
				LOG.log(GaeLevel.TRACE, "Exiting...");
			}
		}

		return ids;
	}

	private String get(String countryCode, String type, Long categoryInternalId) {
		if (LOG.isLoggable(GaeLevel.DEBUG)) {
			LOG.log(GaeLevel.DEBUG, String.format("Getting data for [%s] and type [%s] and category [%s]", countryCode, type,
					categoryInternalId == null ? "null" : categoryInternalId.toString()));
		}

		String key = null;

		if (categoryInternalId == null) {
			key = String.format(KEY_FORMAT, type);
		} else {
			key = KEY_CATEGORY_FEED;
		}

		if (LOG.isLoggable(GaeLevel.DEBUG)) {
			LOG.log(GaeLevel.DEBUG, String.format("key is [%s]", key));
		}

		String endPoint = null;

		final String propertyValue = System.getProperty(key);

		if (categoryInternalId == null) {
			endPoint = String.format(propertyValue, countryCode);
		} else {
			endPoint = String.format(propertyValue, countryCode, type, categoryInternalId.longValue());
		}

		if (LOG.isLoggable(GaeLevel.DEBUG)) {
			LOG.log(GaeLevel.DEBUG, String.format("end point is [%s]", endPoint));
		}

		return HttpExternalGetter.getData(endPoint);
	}

	private void enqueue(Queue queue, String country, String type, Long code) {
		enqueue(queue, country, type, null, code);
	}

	private void enqueue(Queue queue, String country, String type, Long categoryInternalId, Long code) {
		String url = null;

		if (categoryInternalId == null) {
			url = String.format(ENQUEUE_GATHER_FORMAT, DataTypeHelper.IOS_STORE_A3, country, type, code);
		} else {
			url = String.format(ENQUEUE_GATHER_CATEGORY_FORMAT, DataTypeHelper.IOS_STORE_A3, country, type, categoryInternalId.longValue(), code);
		}

		if (LOG.isLoggable(GaeLevel.DEBUG)) {
			LOG.log(GaeLevel.DEBUG, String.format("Enqueueing gather tasks for url [%s]", url));
		}

		try {
			queue.add(TaskOptions.Builder.withUrl(url).method(Method.GET));
		} catch (final TransientFailureException ex) {

			if (LOG.isLoggable(Level.WARNING)) {
				LOG.log(Level.WARNING, String.format("Could not queue a message with url", url), ex);
			}

			// retry once
			try {
				queue.add(TaskOptions.Builder.withUrl(url).method(Method.GET));
			} catch (final TransientFailureException reEx) {
				if (LOG.isLoggable(Level.SEVERE)) {
					LOG.log(Level.SEVERE, String.format("Retry of with url [%s] failed while adding to queue [%s] twice", url, queue.getQueueName()), reEx);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.collectors.Collector#isGrossing(java.lang.String)
	 */
	@Override
	public boolean isGrossing(String type) {
		return type.equalsIgnoreCase(TOP_GROSSING_IPAD_APPS) || type.equalsIgnoreCase(TOP_GROSSING_APPS);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.collectors.Collector#isPaid(java.lang.String)
	 */
	@Override
	public boolean isPaid(String type) {
		return type.equalsIgnoreCase(TOP_PAID_IPAD_APPS) || type.equalsIgnoreCase(TOP_PAID_APPS);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.collectors.Collector#isFree(java.lang.String)
	 */
	@Override
	public boolean isFree(String type) {
		return type.equalsIgnoreCase(TOP_FREE_IPAD_APPS) || type.equalsIgnoreCase(TOP_FREE_APPS);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.collectors.Collector#getCounterpartTypes(java.lang.String)
	 */
	@Override
	public List<String> getCounterpartTypes(String type) {
		switch (type) {
		case TOP_FREE_APPS:
			return Arrays.asList(TOP_GROSSING_APPS);
		case TOP_PAID_APPS:
			return Arrays.asList(TOP_GROSSING_APPS);
		case TOP_GROSSING_APPS:
			return Arrays.asList(TOP_PAID_APPS, TOP_FREE_APPS);
		case TOP_FREE_IPAD_APPS:
			return Arrays.asList(TOP_GROSSING_IPAD_APPS);
		case TOP_PAID_IPAD_APPS:
			return Arrays.asList(TOP_GROSSING_IPAD_APPS);
		case TOP_GROSSING_IPAD_APPS:
			return Arrays.asList(TOP_PAID_IPAD_APPS, TOP_FREE_IPAD_APPS);
		}
		return null;
	}

	@Override
	public List<String> getTypes() {
		return Arrays.asList(TOP_FREE_APPS, TOP_PAID_APPS, TOP_GROSSING_APPS, TOP_FREE_IPAD_APPS, TOP_PAID_IPAD_APPS, TOP_GROSSING_IPAD_APPS);
	}

}
