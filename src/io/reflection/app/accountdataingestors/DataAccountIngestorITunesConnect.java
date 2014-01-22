//
//  DataAccountIngestorITunesConnect.java
//  storedata
//
//  Created by William Shakour (billy1380) on 22 Jan 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.accountdataingestors;

import io.reflection.app.datatypes.shared.DataAccountFetch;
import io.reflection.app.logging.GaeLevel;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.google.appengine.api.taskqueue.TransientFailureException;

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
		// TODO Auto-generated method stub

		LOG.finest("do the actual ingest");
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
