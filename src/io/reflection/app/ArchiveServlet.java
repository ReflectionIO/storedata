//
//  ArchiveServlet.java
//  storedata
//
//  Created by William Shakour (billy1380) on 28 Aug 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app;

import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.api.shared.datatypes.SortDirectionType;
import io.reflection.app.datatypes.shared.Rank;
import io.reflection.app.itemrankarchivers.ItemRankArchiverFactory;
import io.reflection.app.logging.GaeLevel;
import io.reflection.app.service.rank.RankServiceProvider;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;

import com.willshex.service.ContextAwareServlet;

/**
 * @author William Shakour (billy1380)
 * 
 */
@SuppressWarnings("serial")
public class ArchiveServlet extends ContextAwareServlet {
	private static final Logger LOGGER = Logger.getLogger(ArchiveServlet.class.getName());

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.willshex.service.ContextAwareServlet#doGet()
	 */
	@Override
	protected void doGet() throws ServletException, IOException {
		String appEngineQueue = REQUEST.get().getHeader("X-AppEngine-QueueName");

		if (LOGGER.isLoggable(GaeLevel.DEBUG)) {
			LOGGER.log(GaeLevel.DEBUG, String.format("appEngineQueue is [%s]", appEngineQueue));
		}

		boolean isNotQueue = false;

		// bail out if we have not been called by app engine cron
		if (isNotQueue = (appEngineQueue == null || !"archive".toLowerCase().equals(appEngineQueue.toLowerCase()))) {
			RESPONSE.get().setStatus(401);
			RESPONSE.get().getOutputStream().print("failure");
			LOGGER.log(Level.WARNING, "Attempt to run script directly, this is not permitted");
			return;
		}

		if (LOGGER.isLoggable(GaeLevel.DEBUG)) {
			if (!isNotQueue) {
				LOGGER.log(GaeLevel.DEBUG, String.format("Servelet is being called from [%s] queue", appEngineQueue));
			}
		}

		String idParameter = REQUEST.get().getParameter("id");
		String type = REQUEST.get().getParameter("type");

		if ("itemrank".equals(type)) {
			String pagerParam = REQUEST.get().getParameter("pager");
			String startParam = REQUEST.get().getParameter("start");
			String countParam = REQUEST.get().getParameter("count");
			String moreParam = REQUEST.get().getParameter("next");

			Boolean pager = pagerParam == null ? null : Boolean.valueOf(pagerParam);

			if (pager != null && pager.booleanValue()) {
				Long start = startParam == null ? null : Long.valueOf(startParam);
				Long count = countParam == null ? null : Long.valueOf(countParam);

				Boolean next = moreParam == null ? Boolean.FALSE : Boolean.valueOf(moreParam);

				if (start == null) {
					start = Pager.DEFAULT_START;
				}

				if (count == null) {
					count = Pager.DEFAULT_COUNT;
				}

				Pager p = new Pager();
				p.start = start;
				p.count = count;
				p.sortBy = "id";
				p.sortDirection = SortDirectionType.SortDirectionTypeAscending;

				ItemRankArchiverFactory.get().enqueuePagerRanks(p, next);

			} else {
				Long id = Long.valueOf(idParameter);

				if (id != null) {
					try {
						Rank rank = RankServiceProvider.provide().getRank(id);
						if (rank != null) {
							ItemRankArchiverFactory.get().archiveRank(rank);
						} else {
							if (LOGGER.isLoggable(Level.INFO)) {
								LOGGER.info("Could not find rank for archiving with id [" + idParameter + "]");
							}
						}
					} catch (DataAccessException daEx) {
						throw new RuntimeException(daEx);
					}
				}
			}
		} else if ("feedfetchranks".equals(type)) {
			Long id = Long.valueOf(idParameter);

			if (id != null) {
				ItemRankArchiverFactory.get().archiveIdFeedFetchRanks(id);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.willshex.service.ContextAwareServlet#doPost()
	 */
	@Override
	protected void doPost() throws ServletException, IOException {
		doGet();
	}
}
