//
//  AdditionalPropertiesServlet.java
//  storedata
//
//  Created by William Shakour on 12 Jul 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.apple;

import static com.willshex.gson.json.shared.Convert.fromJsonObject;
import static com.willshex.gson.json.shared.Convert.toJsonObject;
import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.api.lookup.shared.datatypes.LookupDetailType;
import io.reflection.app.collectors.HttpExternalGetter;
import io.reflection.app.datatypes.shared.Application;
import io.reflection.app.datatypes.shared.Item;
//import io.reflection.app.collectors.HttpExternalGetter;
import io.reflection.app.logging.GaeLevel;
import io.reflection.app.service.application.ApplicationServiceProvider;
import io.reflection.app.service.item.IItemService;
//import io.reflection.app.service.item.IItemService;
import io.reflection.app.service.item.ItemServiceProvider;
import io.reflection.app.service.rank.RankServiceProvider;
import io.reflection.app.shared.util.DataTypeHelper;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.google.appengine.api.taskqueue.TransientFailureException;
import com.google.appengine.api.urlfetch.HTTPMethod;
//import com.google.appengine.api.urlfetch.HTTPMethod;
import com.google.appengine.api.utils.SystemProperty;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * @author William Shakour
 * 
 */
@SuppressWarnings("serial")
public class ItemPropertyLookupServlet extends HttpServlet {
	private static final Logger LOG = Logger.getLogger(ItemPropertyLookupServlet.class.getName());

	public static final String PROPERTY_IAP = "usesIap";
	private static final String PROPERTY_IAP_ON = "usesIap.on";

	private static final long DURATION_30_DAYS = 30 * 24 * 60 * 60 * 1000;

	public static final String ADD_IF_NEW_ACTION = "addIfNew";
	public static final String REMOVE_DUPLICATES_ACTION = "removeDuplicates";

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest , javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String appEngineQueue = req.getHeader("X-AppEngine-QueueName");

		if (LOG.isLoggable(GaeLevel.DEBUG)) {
			LOG.log(GaeLevel.DEBUG, String.format("appEngineQueue is [%s]", appEngineQueue));
		}

		boolean isNotQueue = false;

		// bail out if we have not been called by app engine cron
		if (isNotQueue = (appEngineQueue == null || !"itempropertylookup".toLowerCase().equals(appEngineQueue.toLowerCase()))) {
			resp.setStatus(401);
			resp.getOutputStream().print("failure");
			LOG.log(Level.WARNING, "Attempt to run script directly, this is not permitted");
			return;
		}

		if (LOG.isLoggable(GaeLevel.DEBUG)) {
			if (!isNotQueue) {
				LOG.log(GaeLevel.DEBUG, String.format("Servelet is being called from [%s] queue", appEngineQueue));
			}
		}

		String itemId = req.getParameter("item");
		String action = req.getParameter("action");

		if (action == null || "".equals(action)) {
			if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Development) {
				if (LOG.isLoggable(Level.SEVERE)) {
					LOG.severe("This message should only be displayed on the developement server because it does not respect queue configuration");
				}

				MemcacheService memCache = MemcacheServiceFactory.getMemcacheService();
				String key = "io.reflection.app.apple.ItemPropertyLookupServlet.runAt";
				Date date = (Date) memCache.get(key);
				Date now = new Date();

				if (date != null && now.getTime() - date.getTime() < 60000) {
					Queue itemPropertyLookupQueue = QueueFactory.getQueue("itempropertylookup");
					itemPropertyLookupQueue.add(TaskOptions.Builder.withUrl(String.format("/itempropertylookup?item=%s", itemId)).method(Method.GET));

					return;
				}

				memCache.put(key, now);
			}

			Item item;
			try {
				item = ItemServiceProvider.provide().getInternalIdItem(itemId);
			} catch (DataAccessException e) {
				throw new RuntimeException(e);
			}

			if (item != null) {
				boolean doCurl = false;

				JsonObject properties = toJsonObject(item.properties == null || item.properties.equalsIgnoreCase("null") ? null : item.properties);

				if (properties == null) {
					if (LOG.isLoggable(GaeLevel.DEBUG)) {
						LOG.log(GaeLevel.DEBUG, String.format("Creating properties for item [%d]", item.id.intValue()));
					}

					properties = new JsonObject();
					doCurl = true;
				} else {
					JsonElement value = properties.get(PROPERTY_IAP);

					if (value == null) {
						doCurl = true;
					} else {
						value = properties.get(PROPERTY_IAP_ON);

						if (value == null) {
							doCurl = true;
						} else {
							Date on = new Date(value.getAsLong());
							Date now = new Date();

							if (now.getTime() - on.getTime() > DURATION_30_DAYS) {
								doCurl = true;
							}
						}
					}
				}

				Boolean usesIap = null;
				if (doCurl && item.price == 0) {
					try {
						if ((usesIap = RankServiceProvider.provide().getItemHasGrossingRank(item)).booleanValue()) {
							doCurl = false;

							setIap(item, properties, usesIap);

						}
					} catch (DataAccessException e) {
						throw new RuntimeException(e);
					}
				}

				// if (doCurl) {
				// String itemUrl = "https://itunes.apple.com/app/id" + itemId;
				// String data = HttpExternalGetter.getData(itemUrl, HTTPMethod.GET);
				//
				// if (data != null) {
				// usesIap = data.contains("class=\"extra-list in-app-purchases") ? Boolean.TRUE : Boolean.FALSE;
				//
				// setIap(item, properties, usesIap);
				// } else {
				// if (LOG.isLoggable(Level.WARNING)) {
				// LOG.log(Level.WARNING, String.format("Could not get additional data from [%s] for [%s]", itemUrl, itemId));
				// }
				// }
				// }
			} else {
				if (LOG.isLoggable(Level.WARNING)) {
					LOG.log(Level.WARNING, String.format("Could not get find item for [%s]", itemId));
				}
			}
		} else if (ADD_IF_NEW_ACTION.equals(action)) {
			try {
				Item item = ItemServiceProvider.provide().getInternalIdItem(itemId);

				if (item == null) {
					String itemUrl = "http://itunes.apple.com/lookup?id=" + itemId;
					String data = HttpExternalGetter.getData(itemUrl, HTTPMethod.GET);

					if (data != null && data.length() > 0) {
						JsonObject jsonObject = toJsonObject(data);

						JsonArray results = jsonObject.get("results").getAsJsonArray();

						JsonElement firstResult;
						JsonObject jsonItem;
						if (results != null && (firstResult = results.get(0)) != null && (jsonItem = firstResult.getAsJsonObject()) != null) {
							item = new Item();
							item.added = new Date();
							item.country = null;
							item.creatorName = jsonItem.get("artistName").getAsString();
							item.price = Float.valueOf(jsonItem.get("price").getAsFloat());
							item.currency = jsonItem.get("currency").getAsString();
							item.internalId = itemId;
							item.externalId = jsonItem.get("bundleId").getAsString();
							item.name = jsonItem.get("trackName").getAsString();
							item.source = "ios";
							item.type = "Application";

							String imageUrl = jsonItem.get("artworkUrl100").getAsString();

							if (imageUrl != null) {
								int lastIndexOfPng = imageUrl.lastIndexOf(".png");
								imageUrl = imageUrl.substring(0, lastIndexOfPng);

								item.smallImage = imageUrl.concat(".53x53-50.png");
								item.mediumImage = imageUrl.concat(".75x75-65.png.png");
								item.largeImage = imageUrl.concat(".100x100-75.png");
							}

							item = ItemServiceProvider.provide().addItem(item);

							if (item.id != null) {
								if (LOG.isLoggable(Level.INFO)) {
									LOG.log(Level.INFO, String.format("Added item internal id [%s] with id [%d]", item.id.longValue()));
								}
							}
						} else {
							if (LOG.isLoggable(Level.WARNING)) {
								LOG.log(Level.WARNING, String.format("Could not find element results in item [%s] json", itemId));
							}
						}
					} else {
						if (LOG.isLoggable(Level.WARNING)) {
							LOG.log(Level.WARNING, String.format("Could not get GET json data for item [%s] with url", itemId, itemUrl));
						}
					}
				} else {
					if (LOG.isLoggable(Level.INFO)) {
						LOG.log(Level.INFO, String.format("Item [%s] already exists with id [%d]", itemId, item.id.longValue()));
					}
				}
			} catch (DataAccessException e) {
				throw new RuntimeException(e);
			}
		} else if (REMOVE_DUPLICATES_ACTION.equals(action)) {
			try {
				IItemService itemService = ItemServiceProvider.provide();

				// for remove duplicates the item is expected to be the internal item id
				List<Item> itemAndDuplicates = itemService.getInternalIdItemAndDuplicates(itemId);

				if (itemAndDuplicates != null && itemAndDuplicates.size() > 1) {

					// sort items (newest first)
					DataTypeHelper.sortItemsByDate(itemAndDuplicates);

					Item newestItem = itemAndDuplicates.get(0);

					// find newest item with iap (if any)
					Item iapItem = findIapItem(itemAndDuplicates);

					// find newest item with us or gb
					Item englishItem = findUsOrGbItem(itemAndDuplicates);

					// consolidate into single item (probably the first item)
					if (iapItem != null && iapItem != newestItem) {
						newestItem.properties = iapItem.properties;
					}

					if (englishItem != null && englishItem != newestItem) {
						newestItem.creatorName = englishItem.creatorName;
						newestItem.name = englishItem.name;
					}

					// create list of the others
					for (Item item : itemAndDuplicates) {
						if (item == newestItem) {
							itemService.updateItem(item);
						} else {
							itemService.deleteItem(item);
						}
					}
				}

			} catch (DataAccessException e) {
				throw new RuntimeException(e);
			}
		}

	}

	/**
	 * @param item
	 * @param usesIap
	 * @throws DataAccessException
	 */
	private void setIap(Item item, JsonObject properties, Boolean usesIap) throws DataAccessException {
		properties.add(PROPERTY_IAP, new JsonPrimitive(usesIap));
		properties.add(PROPERTY_IAP_ON, new JsonPrimitive(Long.valueOf(new Date().getTime())));

		item.properties = fromJsonObject(properties);

		ItemServiceProvider.provide().updateItem(item);

		List<Application> applications = ApplicationServiceProvider.provide().lookupInternalIdsApplication(Arrays.asList(item.internalId),
				LookupDetailType.LookupDetailTypeShort);

		if (applications != null && applications.size() > 0) {
			ApplicationServiceProvider.provide().setApplicationIap(applications.get(0), usesIap);
		}

	}

	/**
	 * 
	 * @param items
	 * @return
	 */
	private Item findIapItem(Collection<Item> items) {
		Item iapItem = null;
		for (Item item : items) {
			if (item.properties != null && !"".equals(item.properties) && !"null".equals(item.properties)) {
				iapItem = item;
				break;
			}
		}

		return iapItem;
	}

	/**
	 * @param items
	 * @return
	 */
	private Item findUsOrGbItem(Collection<Item> items) {
		Item usOrGbItem = null;

		for (Item item : items) {
			if ("USD".equals(item.currency) || "GBP".equals(item.currency)) {
				usOrGbItem = item;
				break;
			}
		}

		return usOrGbItem;
	}

	public static void enqueueItem(String internalId, String action) {
		if (LOG.isLoggable(GaeLevel.TRACE)) {
			LOG.log(GaeLevel.TRACE, "Entering...");
		}

		try {
			Queue queue = QueueFactory.getQueue("itempropertylookup");

			TaskOptions options = TaskOptions.Builder.withUrl("/itempropertylookup").method(Method.POST);

			options.param("item", internalId);
			options.param("action", action);

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
