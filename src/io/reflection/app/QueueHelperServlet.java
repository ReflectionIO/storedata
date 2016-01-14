//
//  DevUtilServlet.java
//  storedata
//
//  Created by mamin on 7 Oct 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.api.client.http.HttpStatusCodes;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;

import io.reflection.app.logging.GaeLevel;

@SuppressWarnings("serial")
public class QueueHelperServlet extends HttpServlet {
	private transient static final Logger LOG = Logger.getLogger(QueueHelperServlet.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String authToken = req.getParameter("auth_token");
		if (!"cde2c784-8f96-11e5-b77c-b6cfec323676".equals(authToken)) {
			LOG.severe("Queue Helper called with incorrect auth_token: " + authToken);

			resp.sendError(HttpStatusCodes.STATUS_CODE_FORBIDDEN);
			return;
		}

		final String query = req.getParameter("queryString") != null ? URLDecoder.decode(req.getParameter("queryString"), "UTF-8") : "";

		final String destinationQueueName = req.getParameter("queueName");

		if (destinationQueueName == null || destinationQueueName.trim().length() == 0) {
			LOG.warning("No destination provided for queue helper!");
			return;
		}

		final Queue queue = QueueFactory.getQueue(destinationQueueName);
		String url = "/" + destinationQueueName + "?" + query;

		queue.add(TaskOptions.Builder.withUrl(url).method(Method.GET));
		LOG.log(GaeLevel.DEBUG, String.format("Added task to %s queue with url: %s", destinationQueueName, url));
		return;
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}
}
