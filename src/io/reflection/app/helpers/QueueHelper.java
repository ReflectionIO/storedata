//
//  QueueHelper.java
//  storedata
//
//  Created by William Shakour (billy1380) on 8 Sep 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.helpers;

import io.reflection.app.logging.GaeLevel;

import java.util.AbstractMap.SimpleEntry;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.google.appengine.api.taskqueue.TransientFailureException;

/**
 * @author William Shakour (billy1380)
 * 
 */
public class QueueHelper {

	private static final Logger LOG = Logger.getLogger(QueueHelper.class.getName());

	@SafeVarargs
	public static void enqueue(String queueName, String relativeUrl, Method method, SimpleEntry<String, String>... params) {
		if (LOG.isLoggable(GaeLevel.TRACE)) {
			LOG.log(GaeLevel.TRACE, "Entering...");
		}

		try {
			Queue queue = QueueFactory.getQueue(queueName);

			TaskOptions options = TaskOptions.Builder.withMethod(method);

			if (relativeUrl != null) {
				options.url(relativeUrl);
			}

			for (SimpleEntry<String, String> param : params) {
				options.param(param.getKey(), param.getValue());
			}

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

	@SafeVarargs
	public static void enqueue(String queueName, Method method, SimpleEntry<String, String>... params) {
		enqueue(queueName, null, method, params);
	}

}
