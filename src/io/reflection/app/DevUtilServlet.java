//
//  DevUtilServlet.java
//  storedata
//
//  Created by mamin on 7 Oct 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app;

import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Builder;
import com.google.appengine.api.taskqueue.TaskOptions.Method;

import io.reflection.app.helpers.QueueHelper;
import io.reflection.app.logging.GaeLevel;

@SuppressWarnings("serial")
public class DevUtilServlet extends HttpServlet {
	private transient static final Logger LOG = Logger.getLogger(DevUtilServlet.class.getName());

	public static final String	PARAM_ACTION						= "action";
	public static final String	PARAM_DATA_ACCOUNT_IDS	= "dataaccountids";
	public static final String	PARAM_DATES							= "dates";

	public static final String	ACTION_SUMMARISE	= "summarise";
	public static final String	ACTION_SPLIT_DATA	= "split";
	public static final String	ACTION_MODEL			= "model";

	public static final String QUEUE_SUMMARISE = "summarise";

	public static final String URL_SUMMARISE = "/summarise";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		final String appEngineQueue = req.getHeader("X-AppEngine-QueueName");
		final boolean isNotQueue = appEngineQueue == null || !"deferred".toLowerCase().equals(appEngineQueue.toLowerCase());

		if (isNotQueue && (req.getParameter("defer") == null || req.getParameter("defer").equals("yes"))) {
			final Queue deferredQueue = QueueFactory.getQueue("deferred");
			final String query = req.getQueryString();

			if (query != null) {
				final String dest = req.getParameter("dest");

				String url = null;
				if (dest != null && dest.trim().length() > 0) {
					url = "/" + dest + "?" + query;
				} else {
					url = "/dev/devutil?" + query;
				}

				deferredQueue.add(TaskOptions.Builder.withUrl(url).method(Method.GET));
				LOG.log(GaeLevel.DEBUG, String.format("Added task to deferred queue with url: %s", url));
			} else {
				final TaskOptions options = Builder.withUrl("/dev/devutil");

				@SuppressWarnings("rawtypes")
				final Map params = req.getParameterMap();

				for (final Object param : params.keySet()) {
					options.param((String) param, req.getParameter((String) param));
				}

				deferredQueue.add(options.method(Method.POST));
				LOG.log(GaeLevel.DEBUG, String.format("Requeueing up the task in the defered queue for the devutil servlet"));
			}

			return;
		}     // end of if not from deferred queue then re-route it to the deferred queue

		execute(req, resp);
	}

	/**
	 * @param req
	 * @param resp
	 */
	private String summarise(HttpServletRequest req, HttpServletResponse resp) {
		StringBuilder webResponse = new StringBuilder();

		ArrayList<Integer> dataAccountIds = getIntParameters(req, PARAM_DATA_ACCOUNT_IDS);
		ArrayList<String> dates = getStringParameters(req, PARAM_DATES);

		for (String date : dates) {
			for (Integer dataAccountId : dataAccountIds) {

				QueueHelper.enqueue(QUEUE_SUMMARISE, URL_SUMMARISE, Method.GET,
						new SimpleEntry<String, String>("taskName", "summarise_" + dataAccountId + "_" + date + "-" + System.currentTimeMillis()),
						new SimpleEntry<String, String>("dataaccountid", dataAccountId.toString()),
						new SimpleEntry<String, String>("date", date));

				String logMsg = String.format("Enqueued summarisation for data account %d on %s", dataAccountId, date);

				LOG.log(GaeLevel.DEBUG, logMsg);
				webResponse.append(logMsg).append('\n');
			}
		}

		return webResponse.toString();
	}

	/**
	 * @param req
	 * @param resp
	 */
	private void execute(HttpServletRequest req, HttpServletResponse resp) {
		String action = req.getParameter("action");
		if (action == null) {
			String msg = "No action provided.";
			writeResponse(resp, msg);
			return;
		} else {
			action = action.toLowerCase();
		}

		switch (action) {
		case ACTION_SUMMARISE:
			writeResponse(resp, summarise(req, resp));
			break;
		case ACTION_SPLIT_DATA:
			break;
		case ACTION_MODEL:
			break;
		}
	}

	/**
	 * @param req
	 * @param paramDates
	 * @return
	 */
	private ArrayList<String> getStringParameters(HttpServletRequest req, String paramName) {
		String[] values = req.getParameterValues(paramName);

		ArrayList<String> list = new ArrayList<String>(values.length);

		if (values == null || values.length == 0) return list;

		for (String value : values) {
			if (value != null && value.trim().length() > 0) {
				list.add(value.trim());
			}
		}

		return list;
	}

	/**
	 * @param req
	 * @param paramName
	 * @return
	 */
	private ArrayList<Integer> getIntParameters(HttpServletRequest req, String paramName) {
		String[] values = req.getParameterValues(paramName);

		ArrayList<Integer> list = new ArrayList<Integer>(values.length);

		if (values == null || values.length == 0) return list;

		for (String value : values) {
			try {
				list.add(Integer.parseInt(value));
			} catch (NumberFormatException e) {
			}
		}

		return list;
	}

	/**
	 * @param resp
	 * @param msg
	 */
	private void writeResponse(HttpServletResponse resp, String msg) {
		LOG.log(GaeLevel.DEBUG, msg);
		try {
			resp.getWriter().println(msg);
		} catch (IOException e) {
			LOG.log(Level.WARNING, "Could not write to the servlet response stream", e);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}
}
