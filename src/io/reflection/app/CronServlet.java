//
//  CronServlet.java
//  from - jspacecloud
//
//  Created by William Shakour on Jul 1, 2012.
//  Copyright © 2012 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app;

import static io.reflection.app.objectify.PersistenceService.ofy;
import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.apple.ItemPropertyLookupServlet;
import io.reflection.app.datatypes.shared.Item;
import io.reflection.app.datatypes.shared.Rank;
import io.reflection.app.logging.GaeLevel;
import io.reflection.app.pipeline.GatherAllRanks;
import io.reflection.app.pipeline.GatherAllSales;
import io.reflection.app.service.item.ItemServiceProvider;
import io.reflection.app.shared.util.PagerHelper;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.google.appengine.api.taskqueue.TransientFailureException;
import com.google.appengine.tools.pipeline.JobSetting;
import com.google.appengine.tools.pipeline.PipelineService;
import com.google.appengine.tools.pipeline.PipelineServiceFactory;
import com.googlecode.objectify.cmd.QueryKeys;

/**
 * @author William Shakour
 * 
 */
@SuppressWarnings("serial")
public class CronServlet extends HttpServlet {

	private static final Logger LOG = Logger.getLogger(CronServlet.class.getName());

	private static final int DELETE_COUNT = 1000;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String appEngineCron = req.getHeader("X-AppEngine-Cron");
		String appEngineQueue = req.getHeader("X-AppEngine-QueueName");

		if (LOG.isLoggable(GaeLevel.DEBUG)) {
			LOG.log(GaeLevel.DEBUG, String.format("appEngineCron is [%s] and appEngineQueue is [%s]", appEngineCron, appEngineQueue));
		}

		boolean isNotCron = false, isNotQueue = false;

		// bail out if we have not been called by app engine cron
		if ((isNotCron = (appEngineCron == null || !Boolean.parseBoolean(appEngineCron)))
				&& (isNotQueue = (appEngineQueue == null || !"deferred".toLowerCase().equals(appEngineQueue.toLowerCase())))) {
			resp.setStatus(401);
			resp.getOutputStream().print("failure");
			LOG.log(Level.WARNING, "Attempt to run script directly, this is not permitted");
			return;
		}

		if (LOG.isLoggable(GaeLevel.DEBUG)) {
			if (!isNotCron) {
				LOG.log(GaeLevel.DEBUG, "Servelet is being called by Cron");
			}

			if (!isNotQueue) {
				LOG.log(GaeLevel.DEBUG, String.format("Servelet is being called from [%s] queue", appEngineQueue));
			}
		}

		String store = req.getParameter("store");
		String deleteSome = req.getParameter("deletesome");
		String process = req.getParameter("process");
		String tidy = req.getParameter("tidy");

		int count = 0;

		if (store != null) {
			PipelineService service = PipelineServiceFactory.newPipelineService();
			String pipelineId = service.startNewPipeline(new GatherAllRanks(), new JobSetting.OnQueue(JobSetting.OnQueue.DEFAULT));

			if (LOG.isLoggable(Level.INFO)) {
				LOG.info(String.format("Rank gather started successfully and can be tracked at /_ah/pipeline/status.html?root=%s", pipelineId));
			}
		} else if (deleteSome != null) {
			if ("Rank".equals(deleteSome)) {
				int deleteCount = DELETE_COUNT * 10;
				QueryKeys<Rank> query = ofy().load().type(Rank.class).limit(deleteCount).keys();
				ofy().delete().keys(query.iterable());
				count = deleteCount;
			} else if ("Item".equals(deleteSome)) {
				QueryKeys<Item> query = ofy().load().type(Item.class).limit(DELETE_COUNT).keys();
				ofy().delete().keys(query.iterable());
				count = DELETE_COUNT;
			}

			if (LOG.isLoggable(Level.INFO)) {
				LOG.info(String.format("%d %ss deleted successfully", count, deleteSome));
			}
		} else if (process != null) {
			if ("accounts".equals(process)) {
				PipelineService service = PipelineServiceFactory.newPipelineService();

				String pipelineId = service.startNewPipeline(new GatherAllSales(), new JobSetting.OnQueue(JobSetting.OnQueue.DEFAULT));

				if (LOG.isLoggable(Level.INFO)) {
					LOG.info(String.format("Sales gather started successfully and can be tracked at /_ah/pipeline/status.html?root=%s", pipelineId));
				}
			} else if ("itemproperties".equals(process)) {
				List<Long> propertylessItemIds;
				Pager pager = PagerHelper.createInfinitePager();

				try {
					propertylessItemIds = ItemServiceProvider.provide().getPropertylessItemIds(pager);

					if (propertylessItemIds != null) {
						for (Long id : propertylessItemIds) {
							enqueueItemForPropertiesRefresh(id);
						}
					}

				} catch (DataAccessException daEx) {
					throw new RuntimeException(daEx);
				}
			}
		} else if ("item".equals(tidy)) {
			try {
				List<String> itemsWithDuplicates = null;
				// Pager p = new Pager();
				// p.start = Long.valueOf(0);
				// p.count = Long.valueOf(1000);

				Pager p = PagerHelper.createInfinitePager();

				// do {
				itemsWithDuplicates = ItemServiceProvider.provide().getDuplicateItemsInternalId(p);

				for (String internalId : itemsWithDuplicates) {
					ItemPropertyLookupServlet.enqueueItem(internalId, ItemPropertyLookupServlet.REMOVE_DUPLICATES_ACTION);
				}

				// p.start = Long.valueOf(p.start.longValue() + p.count.longValue());
				// } while (itemsWithDuplicates.size() > 0);
			} catch (DataAccessException daEx) {
				throw new RuntimeException(daEx);
			}
		}

		resp.setHeader("Cache-Control", "no-cache");
	}

	public static void enqueueItemForPropertiesRefresh(Long itemId) {
		if (LOG.isLoggable(GaeLevel.TRACE)) {
			LOG.log(GaeLevel.TRACE, "Entering...");
		}

		try {
			Queue queue = QueueFactory.getQueue("refreshitemproperties");

			TaskOptions options = TaskOptions.Builder.withMethod(Method.PULL);
			options.param("itemid", itemId.toString());

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
