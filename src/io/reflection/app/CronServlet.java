//  CronServlet.java
//
//  from - jspacecloud
//
//  Created by William Shakour on Jul 1, 2012.
//  Copyright © 2012 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app;

import static io.reflection.app.objectify.PersistenceService.*;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.AbstractMap.SimpleEntry;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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
import com.googlecode.objectify.cmd.QueryKeys;

import io.reflection.app.accountdatacollectors.ITunesReporterCollector;
import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.apple.ItemPropertyLookupServlet;
import io.reflection.app.collectors.CollectorAmazon;
import io.reflection.app.collectors.CollectorIOS;
import io.reflection.app.datatypes.shared.DataAccount;
import io.reflection.app.datatypes.shared.DataAccountFetch;
import io.reflection.app.datatypes.shared.DataAccountFetchStatusType;
import io.reflection.app.datatypes.shared.FeedFetch;
import io.reflection.app.datatypes.shared.FeedFetchStatusType;
import io.reflection.app.datatypes.shared.Item;
import io.reflection.app.datatypes.shared.Rank;
import io.reflection.app.helpers.ApiHelper;
import io.reflection.app.helpers.AppleReporterHelper;
import io.reflection.app.helpers.AppleReporterHelper.AppleReporterException;
import io.reflection.app.helpers.AppleReporterHelper.DateType;
import io.reflection.app.helpers.DataAccountPropertiesHelper;
import io.reflection.app.logging.GaeLevel;
import io.reflection.app.modellers.Modeller;
import io.reflection.app.modellers.ModellerFactory;
import io.reflection.app.service.dataaccount.DataAccountServiceProvider;
import io.reflection.app.service.dataaccount.IDataAccountService;
import io.reflection.app.service.dataaccountfetch.DataAccountFetchServiceProvider;
import io.reflection.app.service.dataaccountfetch.IDataAccountFetchService;
import io.reflection.app.service.feedfetch.FeedFetchServiceProvider;
import io.reflection.app.service.item.ItemServiceProvider;
import io.reflection.app.shared.util.DataTypeHelper;
import io.reflection.app.shared.util.PagerHelper;

/**
 * @author William Shakour
 *
 */
@SuppressWarnings("serial")
public class CronServlet extends HttpServlet {

	private static final Logger	LOG															= Logger.getLogger(CronServlet.class.getName());

	private static final int		DELETE_COUNT										= 1000;

	private static final String	TEST_DATA_ACCOUNT_USERNAME_KEY	= "gather.dataaccount.testaccount.username";
	private static final String	TEST_DATA_ACCOUNT_SOURCEID_KEY	= "gather.dataaccount.testaccount.sourceid";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		final String appEngineCron = req.getHeader("X-AppEngine-Cron");
		final String appEngineQueue = req.getHeader("X-AppEngine-QueueName");

		if (LOG.isLoggable(GaeLevel.DEBUG)) {
			LOG.log(GaeLevel.DEBUG, String.format("appEngineCron is [%s] and appEngineQueue is [%s]", appEngineCron, appEngineQueue));
		}

		boolean isNotCron = false, isNotQueue = false;

		// bail out if we have not been called by app engine cron
		if ((isNotCron = appEngineCron == null || !Boolean.parseBoolean(appEngineCron))
				&& (isNotQueue = appEngineQueue == null || !"deferred".toLowerCase().equals(appEngineQueue.toLowerCase()))) {
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

		final String store = req.getParameter("store");
		final String deleteSome = req.getParameter("deletesome");
		final String process = req.getParameter("process");
		final String tidy = req.getParameter("tidy");

		int count = 0;

		if (store != null) {
			if (DataTypeHelper.IOS_STORE_A3.equals(store.toLowerCase())) {
				// ios app store
				count = new CollectorIOS().enqueue();
			} else if ("amazon".equals(store.toLowerCase())) {
				// amazon store
				count = new CollectorAmazon().enqueue();
			} else if ("play".equals(store.toLowerCase())) {
				// google play store
			}

			if (LOG.isLoggable(Level.INFO)) {
				LOG.info(String.format("%d Tasks added successfully", count));
			}
		} else if (deleteSome != null) {
			if ("Rank".equals(deleteSome)) {
				final int deleteCount = DELETE_COUNT * 10;
				final QueryKeys<Rank> query = ofy().load().type(Rank.class).limit(deleteCount).keys();
				ofy().delete().keys(query.iterable());
				count = deleteCount;
			} else if ("Item".equals(deleteSome)) {
				final QueryKeys<Item> query = ofy().load().type(Item.class).limit(DELETE_COUNT).keys();
				ofy().delete().keys(query.iterable());
				count = DELETE_COUNT;
			}

			if (LOG.isLoggable(Level.INFO)) {
				LOG.info(String.format("%d %ss deleted successfully", count, deleteSome));
			}
		} else if (process != null) {
			if ("accounts".equals(process)) {
				if (LOG.isLoggable(GaeLevel.DEBUG)) {
					LOG.log(GaeLevel.DEBUG, "Processing accounts");
				}
				try {
					final IDataAccountFetchService dataAccountFetchService = DataAccountFetchServiceProvider.provide();
					final IDataAccountService dataAccountService = DataAccountServiceProvider.provide();

					final int daysToGoBackBy = Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < 17 ? -2 : -1;

					final Calendar dayToFetchAccountDataFor = Calendar.getInstance();
					dayToFetchAccountDataFor.add(Calendar.DATE, daysToGoBackBy);

					final Date lastSalesFetchDate = ApiHelper.removeTime(dayToFetchAccountDataFor.getTime());

					if (LOG.isLoggable(GaeLevel.DEBUG)) {
						LOG.log(GaeLevel.DEBUG, String.format("Last sales fetch date is: %s", lastSalesFetchDate));
					}

					final String dataAccountToTestUsername = System.getProperty(TEST_DATA_ACCOUNT_USERNAME_KEY);
					final String dataAccountToTestSourceID = System.getProperty(TEST_DATA_ACCOUNT_SOURCEID_KEY);

					if (LOG.isLoggable(GaeLevel.DEBUG)) {
						LOG.log(GaeLevel.DEBUG, String.format("Data account to test: %s", dataAccountToTestUsername));
					}
					final DataAccount dataAccountToTest = dataAccountService.getDataAccount(dataAccountToTestUsername, Long.valueOf(dataAccountToTestSourceID));

					if (LOG.isLoggable(GaeLevel.DEBUG)) {
						LOG.log(GaeLevel.DEBUG, String.format("Loaded data account with ID: %s", dataAccountToTest.id));
					}

					/*
					 * Check whether we have already collected sales data for yesterday (or day before if we are checking before 5pm)
					 */
					final DataAccountFetch testAccountFetch = dataAccountFetchService.getDateDataAccountFetch(dataAccountToTest, lastSalesFetchDate);
					if (testAccountFetch != null && testAccountFetch.status != null) {
						// we have already started a fetch for this account for this date.
						if (LOG.isLoggable(GaeLevel.DEBUG)) {
							LOG.log(GaeLevel.DEBUG, String.format(
									"A sales gather has already been run for %s. Checking to see if the gathers are done so we can model against their rank.",
									lastSalesFetchDate));
						}

						/*
						 * Check if all the sales data has been ingested, if there are no gathers left in the gathering status, fire off the modeling jobs
						 */

						final Long dataAccountFetchesGatheredCount = dataAccountFetchService.getDataAccountFetchesWithStatusCount(lastSalesFetchDate,
								DataAccountFetchStatusType.DataAccountFetchStatusTypeGathered);
						final Long dataAccountFetchesIngestedCount = dataAccountFetchService.getDataAccountFetchesWithStatusCount(lastSalesFetchDate,
								DataAccountFetchStatusType.DataAccountFetchStatusTypeIngested);

						if (dataAccountFetchesGatheredCount == 0 && dataAccountFetchesIngestedCount > 0) {
							if (LOG.isLoggable(GaeLevel.DEBUG)) {
								LOG.log(GaeLevel.DEBUG, String.format(
										"There are no dataAccountFetches in progress (gathered) but there are at least some injested (%d). Checking availability of ranks that have been ingested",
										dataAccountFetchesIngestedCount));
							}

							Date processFeedsFrom = lastSalesFetchDate;
							final String processFeedsFromStr = req.getParameter("processFeedsFrom");
							if (processFeedsFromStr != null && processFeedsFromStr.trim().length() > 0) {
								try {
									processFeedsFrom = new SimpleDateFormat("yyyy-MM-dd").parse(processFeedsFromStr);
								} catch (final ParseException e) {
									if (LOG.isLoggable(GaeLevel.DEBUG)) {
										LOG.log(GaeLevel.DEBUG, "Could not parse the date from var processFeedsFrom: " + processFeedsFromStr);
									}
								}
							}

							while (processFeedsFrom.compareTo(lastSalesFetchDate) < 1) {
								final List<Long> ingestedFeedFetchIds = FeedFetchServiceProvider.provide().getFeedFetchIdsForDateWithStatus(processFeedsFrom,
										FeedFetchStatusType.FeedFetchStatusTypeIngested);

								if (ingestedFeedFetchIds != null && ingestedFeedFetchIds.size() > 0) {
									for (final Long feedFetchId : ingestedFeedFetchIds) {
										final FeedFetch fetch = FeedFetchServiceProvider.provide().getFeedFetch(feedFetchId);

										// this is just a sanity check. we expect that the query for getting the IDs only gave us feed fetches that exist and
										// have
										// the ingested status.
										if (fetch != null && fetch.status == FeedFetchStatusType.FeedFetchStatusTypeIngested) {
											if (LOG.isLoggable(GaeLevel.DEBUG)) {
												LOG.log(GaeLevel.DEBUG, String.format(
														"Enquing feed fetch id %d for modelling. Country: %s, category: %s, type: %s", feedFetchId,
														fetch.country, fetch.category.id, fetch.type));
											}

											final Modeller modeller = ModellerFactory.getModellerForStore(DataTypeHelper.IOS_STORE_A3);

											// once the feed fetch status is updated model the list
											modeller.enqueue(fetch);
										}
									}
								} else {
									if (LOG.isLoggable(GaeLevel.DEBUG)) {
										LOG.log(GaeLevel.DEBUG,
												"Could not find any rank feed fetches that matched the date and the ingested status. Can't fire off the modelling process.");
									}
								}

								final Calendar cal = Calendar.getInstance();
								cal.setTime(processFeedsFrom);
								cal.add(Calendar.DAY_OF_YEAR, 1);
								processFeedsFrom = cal.getTime();
							}

						} else {
							if (LOG.isLoggable(GaeLevel.DEBUG)) {
								LOG.log(GaeLevel.DEBUG,
										String.format(
												"Can't kick off the modelling process as the sales gathered count is %d and ingested count is %d. We need all the gathered to move on to ingested to kick off the sales process",
												dataAccountFetchesGatheredCount, dataAccountFetchesIngestedCount));
							}
						}

						resp.setHeader("Cache-Control", "no-cache");
						return;
					}

					/*
					 * Check whether the sales data is available for collection for yesterday (or the day before if we are checking before 5pm)
					 */
					try {
						SimpleEntry<String, String> accountAndVendorId = ITunesReporterCollector.getInstance().getPrimaryAccountAndVendorIdsFromProperties(dataAccountToTest);
						AppleReporterHelper.getReport(dataAccountToTest.username, dataAccountToTest.password, accountAndVendorId.getKey(), accountAndVendorId.getValue(), DateType.DAILY, lastSalesFetchDate);
					} catch (final AppleReporterException e) {
						if (e.getErrorCode() == 210 || e.getErrorCode() == 211) {
							LOG.warning("Sales report are not ready yet for " + lastSalesFetchDate);
						} else {
							LOG.log(Level.WARNING, "There was a problem trying to get test for sales report availability for " + lastSalesFetchDate, e);
						}

						resp.setHeader("Cache-Control", "no-cache");
						return;
					}

					if (LOG.isLoggable(GaeLevel.DEBUG)) {
						LOG.log(GaeLevel.DEBUG, "We have not collected sales data for this date and it is now available. Firing off all the sales gathers");
					}

					// get all accounts
					final Pager pager = PagerHelper.createInfinitePager();
					final List<DataAccount> dataAccounts = dataAccountService.getActiveDataAccounts(pager);

					HashMap<String, DataAccount> dataAccountsByVendorId = new HashMap<>();

					for (final DataAccount dataAccount : dataAccounts) {
						String vendorId = DataAccountPropertiesHelper.getPrimaryVendorId(dataAccount.properties);
						if (vendorId == null) {
							continue;
						}

						if (dataAccountsByVendorId.containsKey(vendorId)) {
							continue;
						}

						dataAccountsByVendorId.put(vendorId, dataAccount);
					}

					for (String vendorId : dataAccountsByVendorId.keySet()) {
						DataAccount dataAccount = dataAccountsByVendorId.get(vendorId);
						dataAccountService.triggerDataAccountFetch(dataAccount);

						// go through all the failed attempts and get them too (failed attempts = less than 30 days old)
						final List<DataAccountFetch> failedDataAccountFetches = dataAccountFetchService.getFailedDataAccountFetches(dataAccount, PagerHelper.createInfinitePager());

						for (final DataAccountFetch dataAccountFetch : failedDataAccountFetches) {
							dataAccountService.triggerSingleDateDataAccountFetch(dataAccount, dataAccountFetch.date);
						}
					}

				} catch (final DataAccessException daEx) {
					throw new RuntimeException(daEx);
				}
			} else if ("itemproperties".equals(process)) {
				List<Long> propertylessItemIds;
				final Pager pager = PagerHelper.createInfinitePager();

				try {
					propertylessItemIds = ItemServiceProvider.provide().getPropertylessItemIds(pager);

					if (propertylessItemIds != null) {
						for (final Long id : propertylessItemIds) {
							enqueueItemForPropertiesRefresh(id);
						}
					}

				} catch (final DataAccessException daEx) {
					throw new RuntimeException(daEx);
				}
			}
		} else if ("item".equals(tidy)) {
			try {
				List<String> itemsWithDuplicates = null;
				// Pager p = new Pager();
				// p.start = Long.valueOf(0);
				// p.count = Long.valueOf(1000);

				final Pager p = PagerHelper.createInfinitePager();

				// do {
				itemsWithDuplicates = ItemServiceProvider.provide().getDuplicateItemsInternalId(p);

				for (final String internalId : itemsWithDuplicates) {
					ItemPropertyLookupServlet.enqueueItem(internalId, ItemPropertyLookupServlet.REMOVE_DUPLICATES_ACTION);
				}

				// p.start = Long.valueOf(p.start.longValue() + p.count.longValue());
				// } while (itemsWithDuplicates.size() > 0);
			} catch (final DataAccessException daEx) {
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
			final Queue queue = QueueFactory.getQueue("refreshitemproperties");

			final TaskOptions options = TaskOptions.Builder.withMethod(Method.PULL);
			options.param("itemid", itemId.toString());

			try {
				queue.add(options);
			} catch (final TransientFailureException ex) {

				if (LOG.isLoggable(Level.WARNING)) {
					LOG.warning(String.format("Could not queue a message because of [%s] - will retry it once", ex.toString()));
				}

				// retry once
				try {
					queue.add(options);
				} catch (final TransientFailureException reEx) {
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
