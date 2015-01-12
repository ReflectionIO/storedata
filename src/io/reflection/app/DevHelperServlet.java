/**
 * 
 */
package io.reflection.app;

import static io.reflection.app.objectify.PersistenceService.ofy;
import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.archivers.ArchiverFactory;
import io.reflection.app.archivers.ItemRankArchiver;
import io.reflection.app.archivers.ItemSaleArchiver;
import io.reflection.app.collectors.CollectorIOS;
import io.reflection.app.datatypes.shared.Category;
import io.reflection.app.datatypes.shared.Country;
import io.reflection.app.datatypes.shared.DataAccount;
import io.reflection.app.datatypes.shared.FeedFetch;
import io.reflection.app.datatypes.shared.ItemRankSummary;
import io.reflection.app.datatypes.shared.Rank;
import io.reflection.app.datatypes.shared.Store;
import io.reflection.app.ingestors.Ingestor;
import io.reflection.app.ingestors.IngestorFactory;
import io.reflection.app.logging.GaeLevel;
import io.reflection.app.pipeline.IosRankPipeline;
import io.reflection.app.service.application.ApplicationServiceProvider;
import io.reflection.app.service.category.CategoryServiceProvider;
import io.reflection.app.service.feedfetch.FeedFetchServiceProvider;
import io.reflection.app.service.rank.RankServiceProvider;
import io.reflection.app.service.sale.SaleServiceProvider;
import io.reflection.app.service.store.StoreServiceProvider;
import io.reflection.app.setup.CountriesInstaller;
import io.reflection.app.setup.StoresInstaller;
import io.reflection.app.shared.util.DataTypeHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.text.View;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.markdown4j.Markdown4jProcessor;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Builder;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.google.appengine.tools.pipeline.PipelineService;
import com.google.appengine.tools.pipeline.PipelineServiceFactory;
import com.googlecode.objectify.cmd.Query;

/**
 * @author William Shakour
 * 
 */
@SuppressWarnings("serial")
public class DevHelperServlet extends HttpServlet {
	private static final Logger LOG = Logger.getLogger(DevHelperServlet.class.getName());

	private static final String RANK_END_200_PLUS = "200+";

	// private static final boolean USE_BACKENDS = false;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String appEngineQueue = req.getHeader("X-AppEngine-QueueName");
		boolean isNotQueue = (appEngineQueue == null || !"deferred".toLowerCase().equals(appEngineQueue.toLowerCase()));

		// if (true) {
		// com.google.appengine.api.datastore.Query query = new com.google.appengine.api.datastore.Query("__GsFileInfo__");
		// PreparedQuery p = DatastoreServiceFactory.getDatastoreService().prepare(query);
		// for (Entity e : p.asIterable()) {
		// String name = e.getKey().getName();
		// String destname = (String) e.getProperty("filename");
		//
		// System.out.println(name + " " + destname);
		// }
		//
		//
		// return;
		// }

		if (isNotQueue && (req.getParameter("defer") == null || req.getParameter("defer").equals("yes"))) {
			Queue deferredQueue = QueueFactory.getQueue("deferred");

			if (req.getParameter("cron") == null) {
				String query = req.getQueryString();

				if (query != null) {
					deferredQueue.add(TaskOptions.Builder.withUrl("/dev/devhelper?" + query).method(Method.GET));
				} else {
					TaskOptions options = Builder.withUrl("/dev/devhelper");

					@SuppressWarnings("rawtypes")
					Map params = req.getParameterMap();

					for (Object param : params.keySet()) {
						options.param((String) param, req.getParameter((String) param));
					}

					deferredQueue.add(options.method(Method.POST));
				}
			} else {
				if (LOG.isLoggable(GaeLevel.DEBUG)) {
					LOG.log(GaeLevel.DEBUG, "Adding gather request to deferred queue");
				}

				// if (SystemProperty.environment.value() == Environment.Value.Development) {
				deferredQueue.add(TaskOptions.Builder.withUrl("/cron?" + req.getQueryString()).method(Method.GET));
				// }
			}

			return;
		}

		String action = req.getParameter("action");
		String object = req.getParameter("object");
		String start = req.getParameter("start");
		String count = req.getParameter("count");
		String all = req.getParameter("all");
		String rankStart = req.getParameter("rankstart");
		String rankEnd = req.getParameter("rankend");
		String feedType = req.getParameter("feedtype");
		String itemId = req.getParameter("itemid");
		String date = req.getParameter("date");
		String ids = req.getParameter("ids");

		boolean success = false;

		String markup = null;
		String csv = null;

		if (action != null) {
			if ("addingested".equalsIgnoreCase(action)) {

				// int i = 0;
				// for (FeedFetch entity : ofy().load().type(FeedFetch.class).offset(Integer.parseInt(start)).limit(Integer.parseInt(count)).iterable()) {
				// entity.ingested = Boolean.FALSE;
				//
				// ofy().save().entity(entity).now();
				//
				// if (LOG.isLoggable(GaeLevel.TRACE)) {
				// LOG.log(GaeLevel.TRACE, String.format("Set entity [%d] ingested to false", entity.id.longValue()));
				// }
				//
				// i++;
				// }
				//
				// if (LOG.isLoggable(GaeLevel.DEBUG)) {
				// LOG.log(GaeLevel.DEBUG, String.format("Processed [%d] entities", i));
				// }

				success = true;
			} else if ("uningest".equalsIgnoreCase(action)) {

				// int i = 0;
				// for (FeedFetch entity : ofy().load().type(FeedFetch.class).offset(Integer.parseInt(start)).limit(Integer.parseInt(count)).iterable()) {
				// entity.ingested = false;
				//
				// ofy().save().entity(entity).now();
				//
				// if (LOG.isLoggable(GaeLevel.TRACE)) {
				// LOG.log(GaeLevel.TRACE, String.format("Set entity [%d] ingested to false", entity.id.longValue()));
				// }
				//
				// i++;
				// }
				//
				// if (LOG.isLoggable(GaeLevel.DEBUG)) {
				// LOG.log(GaeLevel.DEBUG, String.format("Processed [%d] entities", i));
				// }

				success = true;
			} else if ("ingest".equalsIgnoreCase(action)) {

				Ingestor i = IngestorFactory.getIngestorForStore(DataTypeHelper.IOS_STORE_A3);
				i.enqueue(Arrays.asList(Long.valueOf(itemId)));

				success = true;

			} else if ("refreshproperties".equalsIgnoreCase(action)) {
				CronServlet.enqueueItemForPropertiesRefresh(Long.valueOf(itemId));

				success = true;
			} else if ("ingestmulti".equalsIgnoreCase(action)) {

				Ingestor i = IngestorFactory.getIngestorForStore(DataTypeHelper.IOS_STORE_A3);

				String[] feedIdsArray = ids.split(",");

				for (String feedId : feedIdsArray) {
					i.enqueue(Arrays.asList(Long.valueOf(feedId)));
				}

				success = true;
			} else if ("addcode".equalsIgnoreCase(action)) {

				Date startDate = null, endDate = null;
				startDate = DateTime.parse(date, DateTimeFormat.forPattern("yyyy-MM-dd-HH").withZoneUTC()).toDate();
				endDate = (new DateTime(startDate.getTime(), DateTimeZone.UTC)).plusHours(2).toDate();

				if (startDate != null) {
					int i = 0;

					try {
						Long code = FeedFetchServiceProvider.provide().getCode();
						for (FeedFetch feed : ofy().load().type(FeedFetch.class).filter("date >=", startDate).filter("date <", endDate)
								.offset(Integer.parseInt(start)).limit(Integer.parseInt(count)).iterable()) {

							if (feed.code == null) {

								feed.code = code;
								ofy().save().entity(feed);

								if (LOG.isLoggable(GaeLevel.TRACE)) {
									LOG.log(GaeLevel.TRACE, String.format("Added code [%s] to entity [%d]", feed.code, feed.id.longValue()));
								}

								i++;
							} else {
								if (LOG.isLoggable(GaeLevel.TRACE)) {
									LOG.log(GaeLevel.TRACE, String.format("Entity [%d] has code [%s]", feed.id.longValue(), feed.code));
								}
							}

						}
					} catch (DataAccessException dae) {
						LOG.log(GaeLevel.SEVERE, "A database error occured attempting to to get an id code for gather enqueing", dae);
					}

					if (LOG.isLoggable(GaeLevel.DEBUG)) {
						LOG.log(GaeLevel.DEBUG, String.format("Processed [%d] entities", i));
					}

					success = true;
				}
			} else if ("remove".equalsIgnoreCase(action)) {
				if (object != null) {
					DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();

					com.google.appengine.api.datastore.Query query = new com.google.appengine.api.datastore.Query(object);
					PreparedQuery preparedQuery = datastoreService.prepare(query);

					int i = 0;
					for (Entity entity : preparedQuery.asIterable(FetchOptions.Builder.withOffset(Integer.parseInt(start)).limit(Integer.parseInt(count)))) {
						datastoreService.delete(entity.getKey());

						if (LOG.isLoggable(GaeLevel.TRACE)) {
							LOG.log(GaeLevel.TRACE, String.format("Removed entity [%d]", entity.getKey().getId()));
						}

						i++;
					}

					if (LOG.isLoggable(GaeLevel.DEBUG)) {
						LOG.log(GaeLevel.DEBUG, String.format("Processed [%d] entities", i));
					}

					success = true;
				}
			} else if ("countitemrank".equalsIgnoreCase(action)) {
				// int i = 0;
				// Query<Rank> queryRank = ofy().cache(false).load().type(Rank.class).filter("counted =", Boolean.FALSE).offset(Integer.parseInt(start))
				// .limit(Integer.parseInt(count));
				//
				// // Query<Rank> queryRank = ofy().load().type(Rank.class).filter("id =", Long.valueOf(19816));
				//
				// for (Rank rank : queryRank.iterable()) {
				// // get the item rank summary
				// Query<ItemRankSummary> querySummary = PersistenceService.ofy().load().type(ItemRankSummary.class).filter("itemId =", rank.itemId)
				// .filter("type =", rank.type).filter("source =", rank.source);
				//
				// ItemRankSummary itemRankSummary = null;
				//
				// if (querySummary.count() > 0) {
				// // we already have an item for this
				// itemRankSummary = querySummary.list().get(0);
				// } else {
				// itemRankSummary = new ItemRankSummary();
				// itemRankSummary.itemId = rank.itemId;
				// itemRankSummary.type = rank.type;
				// itemRankSummary.source = rank.source;
				// }
				//
				// itemRankSummary.numberOfTimesRanked = Integer.valueOf(itemRankSummary.numberOfTimesRanked.intValue() + 1);
				//
				// if (rank.position.intValue() <= 10) {
				// itemRankSummary.numberOfTimesRankedTop10 = Integer.valueOf(itemRankSummary.numberOfTimesRankedTop10 + 1);
				// }
				//
				// if (rank.position.intValue() <= 25) {
				// itemRankSummary.numberOfTimesRankedTop25 = Integer.valueOf(itemRankSummary.numberOfTimesRankedTop25 + 1);
				// }
				//
				// if (rank.position.intValue() <= 50) {
				// itemRankSummary.numberOfTimesRankedTop50 = Integer.valueOf(itemRankSummary.numberOfTimesRankedTop50 + 1);
				// }
				//
				// if (rank.position.intValue() <= 100) {
				// itemRankSummary.numberOfTimesRankedTop100 = Integer.valueOf(itemRankSummary.numberOfTimesRankedTop100 + 1);
				// }
				//
				// if (rank.position.intValue() <= 200) {
				// itemRankSummary.numberOfTimesRankedTop200 = Integer.valueOf(itemRankSummary.numberOfTimesRankedTop200 + 1);
				// }
				//
				// ofy().save().entity(itemRankSummary).now();
				//
				// if (LOG.isLoggable(GaeLevel.TRACE)) {
				// LOG.log(GaeLevel.TRACE,
				// String.format("Updated item item summary for [%s:%s:%s]", itemRankSummary.source, itemRankSummary.type, itemRankSummary.itemId));
				// }
				//
				// rank.counted = true;
				//
				// ofy().save().entity(rank).now();
				//
				// if (LOG.isLoggable(GaeLevel.TRACE)) {
				// LOG.log(GaeLevel.TRACE, String.format("Updated rank counted for %d", rank.id.longValue()));
				// }
				//
				// i++;
				// }
				//
				// if (LOG.isLoggable(GaeLevel.DEBUG)) {
				// LOG.log(GaeLevel.DEBUG, String.format("Processed [%d] entities", i));
				// }

				success = true;

			} else if ("uncountranks".equalsIgnoreCase(action)) {
				// int i = 0;
				// for (Rank rank : ofy().load().type(Rank.class).offset(Integer.parseInt(start)).limit(Integer.parseInt(count)).iterable()) {
				// rank.counted = false;
				//
				// ofy().save().entity(rank);
				//
				// if (LOG.isLoggable(GaeLevel.TRACE)) {
				// LOG.log(GaeLevel.TRACE, String.format("Uncounted rank [%d]", rank.id.longValue()));
				// }
				//
				// i++;
				// }
				//
				// if (LOG.isLoggable(GaeLevel.DEBUG)) {
				// LOG.log(GaeLevel.DEBUG, String.format("Processed [%d] entities", i));
				// }

				success = true;
			} else if ("appswithrank".equalsIgnoreCase(action)) {
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

				Query<ItemRankSummary> query = ofy().load().type(ItemRankSummary.class).filter("type =", feedType)
						.filter("source =", DataTypeHelper.IOS_STORE_A3);

				if (rankEndValue < 10) {
					query = query.filter("numberOfTimesRankedTop10 >", Integer.valueOf(0)).order("numberOfTimesRankedTop10");
				} else if (rankEndValue < 25) {
					query = query.filter("numberOfTimesRankedTop25 >", Integer.valueOf(0)).order("numberOfTimesRankedTop25");
				} else if (rankEndValue < 50) {
					query = query.filter("numberOfTimesRankedTop50 >", Integer.valueOf(0)).order("numberOfTimesRankedTop50");
				} else if (rankEndValue < 100) {
					query = query.filter("numberOfTimesRankedTop100 >", Integer.valueOf(0)).order("numberOfTimesRankedTop100");
				} else if (rankEndValue < 200) {
					query = query.filter("numberOfTimesRankedTop200 >", Integer.valueOf(0)).order("numberOfTimesRankedTop200");
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
					buffer.append(itemRankSummary.numberOfTimesRankedTop10.intValue());
					buffer.append("</td><td>");
					buffer.append(itemRankSummary.numberOfTimesRankedTop25.intValue());
					buffer.append("</td><td>");
					buffer.append(itemRankSummary.numberOfTimesRankedTop50.intValue());
					buffer.append("</td><td>");
					buffer.append(itemRankSummary.numberOfTimesRankedTop100.intValue());
					buffer.append("</td><td>");
					buffer.append(itemRankSummary.numberOfTimesRankedTop200.intValue());
					buffer.append("</td><td>");
					buffer.append(itemRankSummary.numberOfTimesRanked.intValue());
					buffer.append("</td></tr>");
					i++;
				}

				if (LOG.isLoggable(GaeLevel.DEBUG)) {
					LOG.log(GaeLevel.DEBUG, String.format("Found [%d] entities", i));
				}

				buffer.append("</table>");

				buffer.append("Found ");
				buffer.append(i);
				buffer.append(" items. <br>");

				buffer.append("</body>");
				buffer.append("</html>");

				markup = buffer.toString();

				success = true;
			} else if ("ranksforapp".equalsIgnoreCase(action)) {
				StringBuffer buffer = new StringBuffer();

				buffer.append("# data for item ");
				buffer.append(itemId);
				buffer.append(" extracted from toppaidapplications and topgrossingapplications");
				buffer.append("\n");
				buffer.append("#item id,date,paid position,grossing position,price");
				buffer.append("\n");

				Map<Long, Rank> paid = new HashMap<Long, Rank>();
				Map<Long, Rank> grossing = new HashMap<Long, Rank>();

				List<Long> codes = new ArrayList<Long>();

				Query<Rank> query = ofy().load().type(Rank.class).filter("source =", DataTypeHelper.IOS_STORE_A3).filter("type =", CollectorIOS.TOP_PAID_APPS)
						.filter("date >", new DateTime().minusDays(30).toDate()).filter("itemId = ", itemId).offset(Integer.parseInt(start))
						.limit(Integer.parseInt(count));

				for (Rank rank : query.iterable()) {
					paid.put(rank.code, rank);

					if (!codes.contains(rank.code)) {
						codes.add(rank.code);
					}
				}

				query = ofy().load().type(Rank.class).filter("source =", DataTypeHelper.IOS_STORE_A3).filter("type =", CollectorIOS.TOP_GROSSING_APPS)
						.filter("date >", DateTime.now().toDate()).filter("itemId = ", itemId).offset(Integer.parseInt(start)).limit(Integer.parseInt(count));

				for (Rank rank : query.iterable()) {
					grossing.put(rank.code, rank);

					if (!codes.contains(rank.code)) {
						codes.add(rank.code);
					}
				}

				Rank grossingItem, paidItem;
				Rank masterItem;
				for (Long code : codes) {
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
			} else if ("convertdatatoblobs".equalsIgnoreCase(action)) {
				// will not support this
			} else if ("countitemrankmr".equalsIgnoreCase(action)) {

				// int rankStartValue = Integer.parseInt(rankStart);
				// int rankEndValue = RANK_END_200_PLUS.equals(rankEnd) ? Integer.MAX_VALUE : Integer.parseInt(rankEnd);
				//
				// redirectToPipelineStatus(req, resp, startStatsJob(5, 2, DataTypeHelper.IOS_STORE_A3, "us", feedType, rankStartValue, rankEndValue));
				//
				success = true;
			} else if ("countranksmr".equalsIgnoreCase(action)) {
				// redirectToPipelineStatus(req, resp, startRankCountJob(5, 2, DataTypeHelper.IOS_STORE_A3, "us", feedType));

				success = true;
			} else if ("createcsvofpaidranks".equalsIgnoreCase(action)) {
				// redirectToPipelineStatus(req, resp,
				// startCreateCsvBlobRank(5, 1, CollectorIOS.TOP_PAID_APPS, CollectorIOS.TOP_GROSSING_APPS, DataTypeHelper.IOS_STORE_A3, "us", feedType));

				success = true;
			} else if ("createcsvoffreeranks".equalsIgnoreCase(action)) {
				// redirectToPipelineStatus(req, resp,
				// startCreateCsvBlobRank(5, 1, CollectorIOS.TOP_FREE_APPS, CollectorIOS.TOP_GROSSING_APPS, DataTypeHelper.IOS_STORE_A3, "us", feedType));

				success = true;
			} else if ("addcountries".equalsIgnoreCase(action)) {
				try {
					CountriesInstaller.install();
				} catch (DataAccessException e) {
					throw new RuntimeException(e);
				}
				success = true;
			} else if ("addstores".equalsIgnoreCase(action)) {
				try {
					StoresInstaller.install();
				} catch (DataAccessException e) {
					throw new RuntimeException(e);
				}
				success = true;
			} else if ("getadditionalproperties".equalsIgnoreCase(action)) {

				Store store;
				try {
					store = StoreServiceProvider.provide().getA3CodeStore(DataTypeHelper.IOS_STORE_A3);
				} catch (DataAccessException e) {
					throw new RuntimeException(e);
				}
				List<String> itemIds;
				try {
					itemIds = ApplicationServiceProvider.provide().getStoreIapNaApplicationIds(store);
				} catch (DataAccessException e) {
					throw new RuntimeException(e);
				}

				for (String id : itemIds) {
					Queue itemPropertyLookupQueue = QueueFactory.getQueue("itempropertylookup");
					itemPropertyLookupQueue.add(TaskOptions.Builder.withUrl(String.format("/itempropertylookup?item=%s", id)).method(Method.GET));
				}

				success = true;

			} else if ("cacheranks".equalsIgnoreCase(action)) {
				cacheRanks();

				success = true;
			} else if ("archive".equalsIgnoreCase(action)) {
				if ("rank".equalsIgnoreCase(object)) {
					if (start == null && count == null) {
						try {
							Store store = new Store();
							store.a3Code = "ios";

							Country country = new Country();
							country.a2Code = "us";

							Category allCategory = CategoryServiceProvider.provide().getAllCategory(store);

							ItemRankArchiver ar = ArchiverFactory.getItemRankArchiver();

							List<Long> foundRankIds = RankServiceProvider.provide().getRankIds(country, store, allCategory, new Date(0L), new Date());

							for (Long rankId : foundRankIds) {
								ar.enqueueIdRank(rankId);
							}
						} catch (DataAccessException e) {
							throw new RuntimeException(e);
						}
					} else {
						ItemRankArchiver ar = ArchiverFactory.getItemRankArchiver();
						Pager p = new Pager();
						p.start = start == null ? Pager.DEFAULT_START : Long.valueOf(start);
						p.count = count == null ? Pager.DEFAULT_COUNT : Long.valueOf(count);
						ar.enqueuePagerRanks(p, all == null ? Boolean.FALSE : Boolean.valueOf(all));
					}
				} else if ("sale".equalsIgnoreCase(object)) {
					if (start == null && count == null) {
						try {
							DataAccount linkedAccount = DataTypeHelper.createDataAccount(1L);

							Country country = new Country();
							country.a2Code = "us";

							ItemSaleArchiver ar = ArchiverFactory.getItemSaleArchiver();

							List<Long> foundSaleIds = SaleServiceProvider.provide().getSaleIds(country, linkedAccount, new Date(0L), DateTime.now().toDate());

							for (Long saleId : foundSaleIds) {
								ar.enqueueIdSale(saleId);
							}
						} catch (DataAccessException e) {
							throw new RuntimeException(e);
						}
					} else {
						ItemSaleArchiver ar = ArchiverFactory.getItemSaleArchiver();
						Pager p = new Pager();
						p.start = start == null ? Pager.DEFAULT_START : Long.valueOf(start);
						p.count = count == null ? Pager.DEFAULT_COUNT : Long.valueOf(count);
						ar.enqueuePagerSales(p, all == null ? Boolean.FALSE : Boolean.valueOf(all));
					}
				}

				success = true;
			} else if ("archivemulti".equalsIgnoreCase(action)) {
				if ("rank".equalsIgnoreCase(object)) {
					ItemRankArchiver ar = ArchiverFactory.getItemRankArchiver();

					String[] rankIdsArray = ids.split(",");

					for (String rankId : rankIdsArray) {
						LOG.finer("Enqueueing rank [" + rankId + "]");
						ar.enqueueIdRank(Long.valueOf(rankId));
					}
				} else if ("feedfetchrank".equalsIgnoreCase(object)) {
					ArchiverFactory.getItemRankArchiver().enqueueIdFeedFetch(Long.valueOf(ids));
				} else if ("sale".equalsIgnoreCase(object)) {
					ItemSaleArchiver ar = ArchiverFactory.getItemSaleArchiver();

					String[] saleIdsArray = ids.split(",");

					for (String saleId : saleIdsArray) {
						LOG.finer("Enqueueing sale [" + saleId + "]");
						ar.enqueueIdSale(Long.valueOf(saleId));
					}
				} else if ("dataaccountfetchsale".equalsIgnoreCase(object)) {
					String[] dataAccountFetchIdsArray = ids.split(",");

					for (String id : dataAccountFetchIdsArray) {
						ArchiverFactory.getItemSaleArchiver().enqueueIdDataAccountFetch(Long.valueOf(id));
					}
				}

				success = true;
			} else if ("predict".equalsIgnoreCase(action)) {
				Queue predictQueue = QueueFactory.getQueue("predict");
				predictQueue.add(TaskOptions.Builder.withUrl("/predict?" + req.getQueryString()).method(Method.GET));

				success = true;
			} else if ("pipelinegather".equals(action)) {
				PipelineService service = PipelineServiceFactory.newPipelineService();

				String pipelineId = service.startNewPipeline(new IosRankPipeline.GatherAll());

				markup = (new Markdown4jProcessor()).process("[View " + pipelineId + "](/_ah/pipeline/status.html?root=" + pipelineId
						+ " \"View pipeline status\")");
			} else {
				if (LOG.isLoggable(Level.INFO)) {
					LOG.info(String.format("Action [%s] not supported", action));
				}
			}
		}

		if (LOG.isLoggable(Level.INFO)) {
			LOG.info(String.format("Imported completed status = [%s]", success ? "success" : "failure"));
		}

		resp.setHeader("Cache-Control", "no-cache");

		if (markup != null) {
			resp.getOutputStream().print(markup);
			LOG.info(markup);
		} else if (csv != null) {
			resp.getOutputStream().print(csv);
		} else {
			resp.getOutputStream().print(success ? "success" : "failure");
		}

	}

	// private String startCreateCsvBlobRank(int mapShardCount, int reduceSharedCount, String topType, String grossingType, String source, String country,
	// String type) {
	// DateFormat format = new SimpleDateFormat("yyyy-MM-dd-HHmmss");
	// return MapReduceJob.start(MapReduceSpecification.of("Create Rank csv blob", new DatastoreInput("Rank", mapShardCount), new TopAndGrossingMapper(
	// topType, grossingType, source, country), Marshallers.getStringMarshaller(), Marshallers.getStringMarshaller(), new CsvBlobReducer(topType,
	// grossingType), new GoogleCloudStorageFileOutput("rankmatchoutput", topType + "_" + grossingType + format.format(new Date()) + "_%d.csv",
	// "text/csv", reduceSharedCount)), getSettings());
	// }

	/**
	 * 
	 */
	private void cacheRanks() {

		DateTime dt = DateTime.now(DateTimeZone.UTC).minusHours(12);
		Date end = dt.toDate();
		Date start = dt.minusDays(1).toDate();

		Store s = new Store();
		s.a3Code = DataTypeHelper.IOS_STORE_A3;

		Country c = new Country();
		c.a2Code = "us";

		Long code = null;
		try {
			code = FeedFetchServiceProvider.provide().getGatherCode(c, s, start, end);
		} catch (DataAccessException e) {
			throw new RuntimeException(e);
		}

		if (code != null) {
			CallServiceMethodServlet.enqueueGetAllRanks("us", DataTypeHelper.IOS_STORE_A3, Long.valueOf(24), CollectorIOS.TOP_GROSSING_APPS, code);
		}
	}

	// private String getUrlBase(HttpServletRequest req) throws MalformedURLException {
	// URL requestUrl = new URL(req.getRequestURL().toString());
	// String portString = requestUrl.getPort() == -1 ? "" : ":" + requestUrl.getPort();
	// return requestUrl.getProtocol() + "://" + requestUrl.getHost() + portString + "/";
	// }

	// private void redirectToPipelineStatus(HttpServletRequest req, HttpServletResponse resp, String pipelineId) throws IOException {
	// String destinationUrl = getPipelineStatusUrl(getUrlBase(req), pipelineId);
	// LOG.info("Redirecting to " + destinationUrl);
	// resp.sendRedirect(destinationUrl);
	// }

	// private String getPipelineStatusUrl(String urlBase, String pipelineId) {
	// return urlBase + "_ah/pipeline/status.html?root=" + pipelineId;
	// }

	// @SuppressWarnings("deprecation")
	// private MapReduceSettings getSettings() {
	// MapReduceSettings settings = new MapReduceSettings().setWorkerQueueName("mapreduce-workers").setControllerQueueName("default");
	// if (USE_BACKENDS) {
	// settings.setBackend("worker");
	// }
	// return settings;
	// }

	// private String startStatsJob(int mapShardCount, int reduceShardCount, String source, String country, String type, int start, int end) {
	// return MapReduceJob.start(MapReduceSpecification.of("Item stats", new DatastoreInput("Rank", mapShardCount), new RankCountMapper(source, country, type,
	// start, end), Marshallers.getStringMarshaller(), Marshallers.getLongMarshaller(), new RankCountReducer(),
	// new InMemoryOutput<KeyValue<String, Long>>(reduceShardCount)), getSettings());
	// }
	//
	// private String startRankCountJob(int mapShardCount, int reduceShardCount, String source, String country, String type) {
	// return MapReduceJob.start(MapReduceSpecification.of("Rank count", new DatastoreInput("Rank", mapShardCount), new TotalRankedItemsCountMapper(source,
	// country, type), Marshallers.getStringMarshaller(), Marshallers.getLongMarshaller(), new RankCountReducer(),
	// new InMemoryOutput<KeyValue<String, Long>>(reduceShardCount)), getSettings());
	// }

}
