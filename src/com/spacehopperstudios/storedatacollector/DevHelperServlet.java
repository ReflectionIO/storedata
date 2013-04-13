/**
 * 
 */
package com.spacehopperstudios.storedatacollector;

import static com.spacehopperstudios.storedatacollector.objectify.PersistenceService.ofy;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.googlecode.objectify.cmd.Query;
import com.spacehopperstudios.storedatacollector.datatypes.FeedFetch;
import com.spacehopperstudios.storedatacollector.datatypes.ItemRankSummary;
import com.spacehopperstudios.storedatacollector.datatypes.Rank;
import com.spacehopperstudios.storedatacollector.objectify.PersistenceService;

/**
 * @author William Shakour
 * 
 */
@SuppressWarnings("serial")
public class DevHelperServlet extends HttpServlet {
	private static final Logger LOG = Logger.getLogger(DevHelperServlet.class);

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String action = req.getParameter("action");
		String object = req.getParameter("object");
		String start = req.getParameter("start");
		String count = req.getParameter("count");

		boolean success = false;

		if (action != null) {
			if ("addingested".toUpperCase().equals(action.toUpperCase())) {

				int i = 0;
				for (FeedFetch entity : ofy().load().type(FeedFetch.class).offset(Integer.parseInt(start)).limit(Integer.parseInt(count)).iterable()) {
					entity.ingested = false;

					ofy().save().entity(entity);

					if (LOG.isTraceEnabled()) {
						LOG.trace(String.format("Set entity [%d] ingested to false", entity.id.longValue()));
					}

					i++;
				}

				if (LOG.isDebugEnabled()) {
					LOG.debug(String.format("Processed [%d] entities", i));
				}

				success = true;
			} else if ("uningest".toUpperCase().equals(action.toUpperCase())) {

				int i = 0;
				for (FeedFetch entity : ofy().load().type(FeedFetch.class).offset(Integer.parseInt(start)).limit(Integer.parseInt(count)).iterable()) {
					entity.ingested = false;

					ofy().save().entity(entity).now();

					if (LOG.isTraceEnabled()) {
						LOG.trace(String.format("Set entity [%d] ingested to false", entity.id.longValue()));
					}

					i++;
				}

				if (LOG.isDebugEnabled()) {
					LOG.debug(String.format("Processed [%d] entities", i));
				}

				success = true;
			} else if ("remove".toUpperCase().equals(action.toUpperCase())) {
				if (object != null) {
					DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();

					com.google.appengine.api.datastore.Query query = new com.google.appengine.api.datastore.Query(object);
					PreparedQuery preparedQuery = datastoreService.prepare(query);

					int i = 0;
					for (Entity entity : preparedQuery.asIterable(FetchOptions.Builder.withOffset(Integer.parseInt(start)).limit(Integer.parseInt(count)))) {
						datastoreService.delete(entity.getKey());

						if (LOG.isTraceEnabled()) {
							LOG.trace(String.format("Removed entity [%d]", entity.getKey().getId()));
						}

						i++;
					}

					if (LOG.isDebugEnabled()) {
						LOG.debug(String.format("Processed [%d] entities", i));
					}

					success = true;
				}
			} else if ("countitemrank".toUpperCase().equals(action.toUpperCase())) {
				int i = 0;
				Query<Rank> queryRank = ofy().load().type(Rank.class).filter("counted =", Boolean.FALSE).offset(Integer.parseInt(start))
						.limit(Integer.parseInt(count));

				// Query<Rank> queryRank = ofy().load().type(Rank.class).filter("id =", Long.valueOf(19816));

				for (Rank rank : queryRank.iterable()) {
					// get the item rank summary
					Query<ItemRankSummary> querySummary = PersistenceService.ofy().load().type(ItemRankSummary.class).filter("itemId =", rank.itemId)
							.filter("type =", rank.type).filter("source =", rank.source);

					ItemRankSummary itemRankSummary = null;

					if (querySummary.count() > 0) {
						// we already have an item for this
						itemRankSummary = querySummary.list().get(0);
					} else {
						itemRankSummary = new ItemRankSummary();
						itemRankSummary.itemId = rank.itemId;
						itemRankSummary.type = rank.type;
						itemRankSummary.source = rank.source;
					}

					itemRankSummary.numberOfTimesRanked++;

					if (rank.position < 10) {
						itemRankSummary.numberOfTimesRankedTop10++;
					}

					if (rank.position < 25) {
						itemRankSummary.numberOfTimesRankedTop25++;
					}

					if (rank.position < 50) {
						itemRankSummary.numberOfTimesRankedTop50++;
					}

					if (rank.position < 100) {
						itemRankSummary.numberOfTimesRankedTop100++;
					}

					if (rank.position < 200) {
						itemRankSummary.numberOfTimesRankedTop200++;
					}

					ofy().save().entity(itemRankSummary).now();

					if (LOG.isTraceEnabled()) {
						LOG.trace(String.format("Updated item item summary for [%s:%s:%s]", itemRankSummary.source, itemRankSummary.type,
								itemRankSummary.itemId));
					}

					rank.counted = true;

					ofy().save().entity(rank).now();

					if (LOG.isTraceEnabled()) {
						LOG.trace(String.format("Updated rank counted for %d", rank.id.longValue()));
					}

					i++;
				}

				if (LOG.isDebugEnabled()) {
					LOG.debug(String.format("Processed [%d] entities", i));
				}

				success = true;

			} else if ("uncountranks".toUpperCase().equals(action.toUpperCase())) {

				int i = 0;
				for (Rank rank : ofy().load().type(Rank.class).offset(Integer.parseInt(start)).limit(Integer.parseInt(count)).iterable()) {
					rank.counted = false;

					ofy().save().entity(rank);

					if (LOG.isTraceEnabled()) {
						LOG.trace(String.format("Uncounted rank [%d]", rank.id.longValue()));
					}

					i++;
				}

				if (LOG.isDebugEnabled()) {
					LOG.debug(String.format("Processed [%d] entities", i));
				}

				success = true;
			}
		}

		if (LOG.isInfoEnabled()) {
			LOG.info(String.format("Imported completed status = [%s]", success ? "success" : "failure"));
		}

		resp.setHeader("Cache-Control", "no-cache");
		resp.getOutputStream().print(success ? "success" : "failure");

	}
}
