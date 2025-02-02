//
//  CallServiceMethodServlet.java
//  storedata
//
//  Created by William Shakour (billy1380) on 30 May 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app;

import static io.reflection.app.helpers.ItemPropertyWrapper.IAP_DAY_OFFSET;
import static io.reflection.app.helpers.ItemPropertyWrapper.PROPERTY_IAP;
import static io.reflection.app.helpers.ItemPropertyWrapper.PROPERTY_IAP_ON;
import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.collectors.Collector;
import io.reflection.app.collectors.CollectorFactory;
import io.reflection.app.datatypes.shared.Category;
import io.reflection.app.datatypes.shared.Country;
import io.reflection.app.datatypes.shared.Item;
import io.reflection.app.datatypes.shared.Rank;
import io.reflection.app.datatypes.shared.Store;
import io.reflection.app.helpers.ItemPropertyWrapper;
import io.reflection.app.logging.GaeLevel;
import io.reflection.app.service.ServiceType;
import io.reflection.app.service.category.CategoryServiceProvider;
import io.reflection.app.service.country.CountryServiceProvider;
import io.reflection.app.service.feedfetch.FeedFetchServiceProvider;
import io.reflection.app.service.item.IItemService;
import io.reflection.app.service.item.ItemServiceProvider;
import io.reflection.app.service.rank.RankServiceProvider;
import io.reflection.app.service.store.StoreServiceProvider;
import io.reflection.app.shared.util.DataTypeHelper;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.google.appengine.api.taskqueue.TransientFailureException;

/**
 * @author billy1380
 *
 */
@SuppressWarnings("serial")
public class CallServiceMethodServlet extends HttpServlet {
	private static final Logger LOG = Logger.getLogger(CallServiceMethodServlet.class.getName());

	public static final String GETALLRANKS_SUPPORTED_METHOD = "getAllRanks";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String appEngineQueue = req.getHeader("X-AppEngine-QueueName");

		boolean isNotQueue = false;

		// bail out if we have not been called by app engine cron
		if ((isNotQueue = (appEngineQueue == null || (!"deferred".toLowerCase().equals(appEngineQueue.toLowerCase()) && !"callservicemethod".toLowerCase()
				.equals(appEngineQueue.toLowerCase()))))) {
			resp.setStatus(401);
			resp.getOutputStream().print("failure");
			LOG.warning("Attempt to run script directly, this is not permitted");
			return;
		}

		if (LOG.isLoggable(GaeLevel.DEBUG)) {
			if (!isNotQueue) {
				LOG.log(GaeLevel.DEBUG, String.format("Servelet is being called from [%s] queue", appEngineQueue));
			}
		}

		String service = req.getParameter("service");
		String method = req.getParameter("method");

		if (service != null && ServiceType.fromString(service) == ServiceType.ServiceTypeRank) {
			if (method != null && GETALLRANKS_SUPPORTED_METHOD.equals(method)) {

				try {
					String dateParameter = req.getParameter("date");
					String a2CountryCodeParameter = req.getParameter("country");
					String a3StoreCodeParameter = req.getParameter("store");
					String categoryParameter = req.getParameter("category");
					String listTypeParameter = req.getParameter("listtype");
					String codeParameter = req.getParameter("code");

					Long code = null;

					if (codeParameter != null) {
						code = Long.valueOf(codeParameter);
					}

					Country country = CountryServiceProvider.provide().getA2CodeCountry(a2CountryCodeParameter);
					Store store = StoreServiceProvider.provide().getA3CodeStore(a3StoreCodeParameter);
					Category category = CategoryServiceProvider.provide().getCategory(Long.valueOf(categoryParameter));

					if (code == null) {
						DateTime dt = (new DateTime(Long.valueOf(dateParameter).longValue(), DateTimeZone.UTC)).minusHours(12);
						Date end = dt.toDate();
						Date start = dt.minusDays(1).toDate();

						// RankServiceProvider.provide().getAllRanks(country, store, category, getGrossingListName(store, listTypeParameter), start, end);

						code = FeedFetchServiceProvider.provide().getGatherCode(country, store, start, end);
					}

					Set<String> itemIds = new HashSet<String>();
					List<Rank> ranks;
					List<Rank> grossingRanks = null;
					Collector collector = CollectorFactory.getCollectorForStore(a3StoreCodeParameter);

					// get all the ranks for the list type (we are using an infinite pager with no sorting to allow us to generate a deletion key during
					// prediction)
					ranks = RankServiceProvider.provide().getGatherCodeRanks(country, category, listTypeParameter, code);

					// if the ranks are for a grossing list check that none are free and don't have iaps
					if (collector != null && collector.isGrossing(listTypeParameter)) {
						grossingRanks = ranks;
					}

					for (Rank rank : ranks) {
						itemIds.add(rank.itemId);
					}

					IItemService itemService = ItemServiceProvider.provide();

					List<Item> items = itemService.getInternalIdItemBatch(itemIds);

					if (grossingRanks != null) {
						Map<String, Item> itemLookup = new HashMap<>();
						Item item;
						ItemPropertyWrapper wrapper;
						Boolean usesIap;

						for (Item current : items) {
							itemLookup.put(current.internalId, current);
						}

						for (Rank rank : grossingRanks) {
							if (DataTypeHelper.isZero(rank.price.floatValue())) {
								// app must use iaps... check it

								item = itemLookup.get(rank.itemId);

								if (item != null) {
									wrapper = new ItemPropertyWrapper(item.properties);

									if ((usesIap = wrapper.getBoolean(PROPERTY_IAP, null)) == null || usesIap == Boolean.FALSE) {
										wrapper.setBoolean(PROPERTY_IAP, Boolean.TRUE);
										wrapper.setDate(PROPERTY_IAP_ON, DateTime.now(DateTimeZone.UTC).plusDays(IAP_DAY_OFFSET).toDate());

										item.properties = wrapper.toString();

										itemService.updateItem(item);
									}
								}
							}
						}
					}
				} catch (DataAccessException | NullPointerException ex) {
					LOG.log(Level.SEVERE, "Error while trying to call service method " + GETALLRANKS_SUPPORTED_METHOD, ex);
				}
			}
		}

		resp.setHeader("Cache-Control", "no-cache");
	}

	/**
	 *
	 * @param country
	 * @param store
	 * @param categoryId
	 * @param listType
	 * @param code
	 */
	public static void enqueueGetAllRanks(String country, String store, Long categoryId, String listType, Long code) {

		Queue queue = QueueFactory.getQueue("callservicemethod");

		TaskOptions options = TaskOptions.Builder.withUrl("/callservicemethod");
		options.param("service", ServiceType.ServiceTypeRank.toString());
		options.param("method", GETALLRANKS_SUPPORTED_METHOD);
		options.param("code", code.toString());
		options.param("country", country);
		options.param("store", store);
		options.param("category", categoryId.toString());
		options.param("listtype", listType);
		options.method(Method.GET);

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
					LOG.log(Level.SEVERE, String.format("Retry to insert into [%s] failed twice", queue.getQueueName()), reEx);
				}
			}
		}

	}

}
