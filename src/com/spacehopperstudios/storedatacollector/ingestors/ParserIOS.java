/**
 * 
 */
package com.spacehopperstudios.storedatacollector.ingestors;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.spacehopperstudios.storedatacollector.datatypes.Item;

/**
 * @author billy1380
 * 
 */
public class ParserIOS implements Parser {

	private static final Logger LOG = Logger.getLogger(ParserIOS.class.getName());

	private static final String KEY_FEED = "feed";
	private static final String KEY_ENTRY = "entry";
	private static final String KEY_NAME = "im:name";
	private static final String KEY_LABEL = "label";
	private static final String KEY_PRICE = "im:price";
	private static final String KEY_ATTRIBUTES = "attributes";
	private static final String KEY_AMOUNT = "amount";
	private static final String KEY_CURRENCY = "currency";
	private static final String KEY_ID = "id";
	private static final String KEY_INTERNAL_ID = "im:id";
	private static final String KEY_BUNDLE_ID = "im:bundleId";

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.spacehopperstudios.storedatacollector.ingestors.Parser#parse(java.lang.String)
	 */
	@Override
	public List<Item> parse(String data) {

		if (LOG.isLoggable(Level.FINE)) {
			LOG.fine("Started parsing data");
		}

		List<Item> items = new ArrayList<Item>();

		JsonObject jsonRss = (new JsonParser()).parse(data).getAsJsonObject();
		JsonArray entries = jsonRss.get(KEY_FEED).getAsJsonObject().get(KEY_ENTRY).getAsJsonArray();

		for (JsonElement entry : entries) {
			JsonObject jsonItem = entry.getAsJsonObject();

			Item item = new Item();
			item.name = jsonItem.get(KEY_NAME).getAsJsonObject().get(KEY_LABEL).getAsString();

			JsonObject attributes = jsonItem.get(KEY_PRICE).getAsJsonObject().get(KEY_ATTRIBUTES).getAsJsonObject();
			item.price = attributes.get(KEY_AMOUNT).getAsFloat();
			item.currency = attributes.get(KEY_CURRENCY).getAsString();

			attributes = jsonItem.get(KEY_ID).getAsJsonObject().get(KEY_ATTRIBUTES).getAsJsonObject();
			item.internalId = attributes.get(KEY_INTERNAL_ID).getAsString();
			item.externalId = attributes.get(KEY_BUNDLE_ID).getAsString();
			item.type = "Application"; // TODO: this can be obtained from the data
			item.source = "ios";

			if (LOG.isLoggable(Level.FINER)) {
				LOG.finer(String.format("Found item [%s]", item.name));
			}

			items.add(item);
		}

		if (LOG.isLoggable(Level.FINE)) {
			LOG.fine(String.format("Found [%d] items", items.size()));
		}

		return items;
	}

}
