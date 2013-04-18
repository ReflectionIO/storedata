/**
 * 
 */
package com.spacehopperstudios.storedatacollector;

import static com.spacehopperstudios.storedatacollector.objectify.PersistenceService.ofy;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
import com.spacehopperstudios.storedatacollector.collectors.DataCollectorIOS;
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

	private static final String RANK_END_200_PLUS = "200+";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String action = req.getParameter("action");
		String object = req.getParameter("object");
		String start = req.getParameter("start");
		String count = req.getParameter("count");
		String rankStart = req.getParameter("rankstart");
		String rankEnd = req.getParameter("rankend");
		String feedType = req.getParameter("feedtype");
		String itemId = req.getParameter("itemid");
		String date = req.getParameter("date");

		boolean success = false;

		String markup = null;
		String csv = null;

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
			} else if ("addcode".toUpperCase().equals(action.toUpperCase())) {
				DateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH");
				Date startDate = null, endDate = null;
				try {
					startDate = format.parse(date);
					Calendar cal = Calendar.getInstance();
					cal.setTime(startDate);
					cal.add(Calendar.HOUR, 2);
					endDate = cal.getTime();

				} catch (ParseException e) {
					LOG.error("Error parsing date", e);
				}

				if (startDate != null) {
					int i = 0;

					String code = UUID.randomUUID().toString();
					for (FeedFetch entity : ofy().load().type(FeedFetch.class).filter("date >=", startDate).filter("date <", endDate)
							.offset(Integer.parseInt(start)).limit(Integer.parseInt(count)).iterable()) {

						if (entity.code == null || entity.code.isEmpty()) {

							entity.code = code;
							ofy().save().entity(entity);

							if (LOG.isTraceEnabled()) {
								LOG.trace(String.format("Added code [%s] to entity [%d]", entity.code, entity.id.longValue()));
							}

							i++;
						} else {
							if (LOG.isTraceEnabled()) {
								LOG.trace(String.format("Entity [%d] has code [%s]", entity.id.longValue(), entity.code));
							}
						}

					}

					if (LOG.isDebugEnabled()) {
						LOG.debug(String.format("Processed [%d] entities", i));
					}

					success = true;
				}
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
			} else if ("appswithrank".toUpperCase().equals(action.toUpperCase())) {
				int rankStartValue = Integer.parseInt(rankStart);
				int rankEndValue = RANK_END_200_PLUS.equals(rankEnd) ? Integer.MAX_VALUE : Integer.parseInt(rankEnd);

				StringBuffer buffer = new StringBuffer();

				buffer.append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");

				buffer.append("<html>");
				buffer.append("<head>");
				buffer.append("<meta name=\"generator\" content=\"HTML Tidy, see www.w3.org\">");
				buffer.append("<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\">");
				buffer.append("<title>Store Collector - Helper functions</title>");
				buffer.append("</head>");
				buffer.append("<body>");
				buffer.append("<table>");
				buffer.append("<tr><th>Appliation</th><th>top 10</th><th>top 25</th><th>top 50</th><th>top 100</th><th>top 200</th><th>total</th></tr>");

				Query<ItemRankSummary> query = ofy().load().type(ItemRankSummary.class).filter("type =", feedType).filter("source =", "ios");

				if (rankEndValue < 10) {
					query = query.filter("numberOfTimesRankedTop10 >", Integer.valueOf(0));
				} else if (rankEndValue < 25) {
					query = query.filter("numberOfTimesRankedTop25 >", Integer.valueOf(0));
				} else if (rankEndValue < 50) {
					query = query.filter("numberOfTimesRankedTop50 >", Integer.valueOf(0));
				} else if (rankEndValue < 100) {
					query = query.filter("numberOfTimesRankedTop100 >", Integer.valueOf(0));
				} else if (rankEndValue < 200) {
					query = query.filter("numberOfTimesRankedTop200 >", Integer.valueOf(0));
				} else if (rankEndValue >= 200) {
					// every ranked item should appear at least once
					// query = query.filter("numberOfTimesRanked >", Integer.valueOf(0));
				}

				if (rankStartValue >= 10) {
					query = query.filter("numberOfTimesRankedTop10 =", Integer.valueOf(0));

					if (rankStartValue >= 25) {
						query = query.filter("numberOfTimesRankedTop25 =", Integer.valueOf(0));

						if (rankStartValue >= 50) {
							query = query.filter("numberOfTimesRankedTop50 =", Integer.valueOf(0));

							if (rankStartValue >= 100) {
								query = query.filter("numberOfTimesRankedTop100 =", Integer.valueOf(0));

								if (rankStartValue >= 200) {
									query = query.filter("numberOfTimesRankedTop200 =", Integer.valueOf(0));
								}
							}
						}
					}
				}

				query = query.offset(Integer.parseInt(start)).limit(Integer.parseInt(count));

				int i = 0;
				for (ItemRankSummary itemRankSummary : query.iterable()) {
					buffer.append("<tr><td>");
					buffer.append(itemRankSummary.itemId);
					buffer.append("</td><td>");
					buffer.append(itemRankSummary.numberOfTimesRankedTop10);
					buffer.append("</td><td>");
					buffer.append(itemRankSummary.numberOfTimesRankedTop25);
					buffer.append("</td><td>");
					buffer.append(itemRankSummary.numberOfTimesRankedTop50);
					buffer.append("</td><td>");
					buffer.append(itemRankSummary.numberOfTimesRankedTop100);
					buffer.append("</td><td>");
					buffer.append(itemRankSummary.numberOfTimesRankedTop200);
					buffer.append("</td><td>");
					buffer.append(itemRankSummary.numberOfTimesRanked);
					buffer.append("</td></tr>");
					i++;
				}

				buffer.append("</table>");

				buffer.append("Found ");
				buffer.append(i);
				buffer.append(" items. <br>");

				buffer.append("</body>");
				buffer.append("</html>");

				markup = buffer.toString();

				success = true;
			} else if ("ranksforapp".toUpperCase().equals(action.toUpperCase())) {
				StringBuffer buffer = new StringBuffer();

				buffer.append("# data for item ");
				buffer.append(itemId);
				buffer.append(" extracted from toppaidapplications and topgrossingapplications");
				buffer.append("\n");
				buffer.append("# item id, date, paid possition, grossing possition, price");
				buffer.append("\n");

				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.DAY_OF_YEAR, -30);

				Map<String, Rank> paid = new HashMap<String, Rank>();
				Map<String, Rank> grossing = new HashMap<String, Rank>();

				List<String> codes = new ArrayList<String>();

				Query<Rank> query = ofy().load().type(Rank.class).filter("source =", "ios").filter("type =", DataCollectorIOS.TOP_PAID_APPS)
						.filter("date >", cal.getTime()).filter("itemId = ", itemId).offset(Integer.parseInt(start)).limit(Integer.parseInt(count));

				for (Rank rank : query.iterable()) {
					paid.put(rank.code, rank);

					if (!codes.contains(rank.code)) {
						codes.add(rank.code);
					}
				}

				query = ofy().load().type(Rank.class).filter("source =", "ios").filter("type =", DataCollectorIOS.TOP_GROSSING_APPS)
						.filter("date >", cal.getTime()).filter("itemId = ", itemId).offset(Integer.parseInt(start)).limit(Integer.parseInt(count));

				for (Rank rank : query.iterable()) {
					grossing.put(rank.code, rank);

					if (!codes.contains(rank.code)) {
						codes.add(rank.code);
					}
				}

				Rank grossingItem, paidItem;
				Rank masterItem;
				for (String code : codes) {
					grossingItem = grossing.get(code);
					paidItem = paid.get(code);

					if (grossingItem != null) {
						masterItem = grossingItem;
					} else {
						masterItem = paidItem;
					}
					
					buffer.append(masterItem.itemId);
					buffer.append(",");
					buffer.append(masterItem.date);
					buffer.append(",");
					
					if (paidItem != null) {
						buffer.append(paidItem.position + 1);
					}
					buffer.append(",");

					if (grossingItem != null) {
						buffer.append(grossingItem.position + 1);
					}
					buffer.append(",");
					
					buffer.append(masterItem.price);
					buffer.append("\n");
				}

				csv = buffer.toString();

				success = true;
			} else {
				if (LOG.isInfoEnabled()) {
					LOG.info(String.format("Action [%s] not supported", action));
				}
			}
		}

		if (LOG.isInfoEnabled()) {
			LOG.info(String.format("Imported completed status = [%s]", success ? "success" : "failure"));
		}

		resp.setHeader("Cache-Control", "no-cache");

		if (markup != null) {
			resp.getOutputStream().print(markup);
		} else if (csv != null) {
			resp.getOutputStream().print(csv);
		} else {
			resp.getOutputStream().print(success ? "success" : "failure");
		}

	}
}
