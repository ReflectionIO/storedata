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
import static io.reflection.app.objectify.PersistenceService.ofy;
import io.reflection.app.collectors.HttpExternalGetter;
import io.reflection.app.datatypes.Item;
import io.reflection.app.logging.GaeLevel;

import java.io.IOException;
import java.util.Date;
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
import com.google.appengine.api.urlfetch.HTTPMethod;
import com.google.appengine.api.utils.SystemProperty;
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

	private static final String PROPERTY_IAP = "usesIap";
	private static final String PROPERTY_IAP_ON = "usesIap.on";

	private static final long DURATION_30_DAYS = 30 * 24 * 60 * 60 * 1000;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest
	 * , javax.servlet.http.HttpServletResponse)
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

		if (SystemProperty.environment.value() != SystemProperty.Environment.Value.Production) {
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

		Item item = ofy().load().type(Item.class).id(Long.valueOf(itemId)).now();

		if (item != null) {
			boolean doCurl = false;

			JsonObject properties = toJsonObject(item.properties);

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

			if (doCurl) {
				String itemUrl = "https://itunes.apple.com/app/id" + item.internalId;
				String data = HttpExternalGetter.getData(itemUrl, HTTPMethod.GET);
				Boolean usesIap = null;

				if (data != null) {
					usesIap = data.contains("class=\"extra-list in-app-purchases") ? Boolean.TRUE : Boolean.FALSE;

					properties.add(PROPERTY_IAP, new JsonPrimitive(usesIap));
					properties.add(PROPERTY_IAP_ON, new JsonPrimitive(Long.valueOf(new Date().getTime())));
					item.properties = fromJsonObject(properties);

					ofy().save().entity(item);
				} else {
					if (LOG.isLoggable(Level.WARNING)) {
						LOG.log(Level.WARNING, String.format("Could not get additional data from [%s] for [%s]", itemUrl, itemId));
					}
				}
			}
		} else {
			if (LOG.isLoggable(Level.WARNING)) {
				LOG.log(Level.WARNING, String.format("Could not get find item for [%s]", itemId));
			}
		}

	}
}
