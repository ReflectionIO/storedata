//
//  AdditionalPropertiesServlet.java
//  storedata
//
//  Created by William Shakour on 12 Jul 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.apple;

import static com.spacehopperstudios.utility.JsonUtils.fromJsonObject;
import static com.spacehopperstudios.utility.JsonUtils.toJsonObject;
import static io.reflection.app.objectify.PersistenceService.ofy;
import io.reflection.app.collectors.HttpExternalGetter;
import io.reflection.app.datatypes.Item;
import io.reflection.app.logging.GaeLevel;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.urlfetch.HTTPMethod;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
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

				JsonElement value = properties.get("usesIap");

				if (value == null) {
					doCurl = true;
				} else {
					if (LOG.isLoggable(GaeLevel.DEBUG)) {
						LOG.log(GaeLevel.DEBUG, String.format("Item [%d] has properties but no iap", item.id.intValue()));
					}
				}
			}

			if (doCurl) {
				String itemUrl = "https://itunes.apple.com/app/id" + item.internalId;
				String data = HttpExternalGetter.getData(itemUrl, HTTPMethod.GET);
				Boolean usesIap = null;

				if (data != null) {
					usesIap = data.contains("class=\"extra-list in-app-purchases") ? Boolean.TRUE : Boolean.FALSE;

					if (usesIap.booleanValue()) {
						properties.add("usesIap", new JsonPrimitive(usesIap));

						item.properties = fromJsonObject(properties);

						ofy().save().entity(item);
					}

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
