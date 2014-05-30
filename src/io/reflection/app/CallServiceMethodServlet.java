//
//  CallServiceMethodServlet.java
//  storedata
//
//  Created by William Shakour (billy1380) on 30 May 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app;

import static io.reflection.app.helpers.ApiHelper.getGrossingListName;
import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.datatypes.shared.Category;
import io.reflection.app.datatypes.shared.Country;
import io.reflection.app.datatypes.shared.Store;
import io.reflection.app.logging.GaeLevel;
import io.reflection.app.service.ServiceType;
import io.reflection.app.service.category.CategoryServiceProvider;
import io.reflection.app.service.country.CountryServiceProvider;
import io.reflection.app.service.rank.RankServiceProvider;
import io.reflection.app.service.store.StoreServiceProvider;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TransientFailureException;
import com.google.appengine.api.taskqueue.TaskOptions.Method;

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
		if ((isNotQueue = (appEngineQueue == null || !"callservicemethod".toLowerCase().equals(appEngineQueue.toLowerCase())))) {
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

					Date date = new Date(Long.valueOf(dateParameter).longValue());
					Country country = CountryServiceProvider.provide().getA2CodeCountry(a2CountryCodeParameter);
					Store store = StoreServiceProvider.provide().getA3CodeStore(a3StoreCodeParameter);
					Category category = CategoryServiceProvider.provide().getCategory(Long.valueOf(categoryParameter));

					Calendar cal = Calendar.getInstance();
					cal.setTime(date);
					cal.set(Calendar.HOUR_OF_DAY, 0);
					cal.set(Calendar.MINUTE, 0);
					cal.set(Calendar.SECOND, 0);
					cal.set(Calendar.MILLISECOND, 1);
					Date end = cal.getTime();
					cal.add(Calendar.DAY_OF_YEAR, -1);
					Date start = cal.getTime();

					RankServiceProvider.provide().getAllRanks(country, store, category, getGrossingListName(store, listTypeParameter), start, end);
				} catch (DataAccessException | NullPointerException ex) {
					LOG.log(GaeLevel.SEVERE, "Error while trying to call service method " + GETALLRANKS_SUPPORTED_METHOD, ex);
				}
			}
		}

		resp.setHeader("Cache-Control", "no-cache");
	}

	/**
	 * @param country
	 * @param iosStoreA3
	 * @param id
	 * @param type
	 * @param key
	 */
	public static void enqueueGetAllRanks(String country, String store, Long categoryId, String listType, Date date) {

		Queue queue = QueueFactory.getQueue("callservicemethod");

		TaskOptions options = TaskOptions.Builder.withUrl("/callservicemethod");
		options.param("service", ServiceType.ServiceTypeRank.toString());
		options.param("method", GETALLRANKS_SUPPORTED_METHOD);
		options.param("date", Long.toString(date.getTime()));
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
