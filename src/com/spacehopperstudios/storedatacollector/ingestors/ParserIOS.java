/**
 * 
 */
package com.spacehopperstudios.storedatacollector.ingestors;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

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

	private static final Logger LOG = Logger.getLogger(ParserIOS.class);
	
	private static final String KEY_FEED = "feed";
	private static final String KEY_ENTRY = "entry";
	private static final String KEY_NAME = "im:name";
	private static final String KEY_LABEL = "label";
	private static final String KEY_PRICE = "im:price";
	private static final String KEY_ATTRIBUTES = "attributes";
	private static final String KEY_AMOUNT = "amount";
//	private static final String KEY_CURRENCY = "currency";
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
		List<Item> items = new ArrayList<Item>();

		JsonObject jsonRss = (new JsonParser()).parse(data).getAsJsonObject();
		JsonArray entries = jsonRss.get(KEY_FEED).getAsJsonObject().get(KEY_ENTRY).getAsJsonArray();
		
		for (JsonElement entry : entries) {
			JsonObject jsonItem = entry.getAsJsonObject();
			
			Item item = new Item();
			item.name = jsonItem.get(KEY_NAME).getAsJsonObject().get(KEY_LABEL).getAsString();
			
			JsonObject attributes = jsonItem.get(KEY_PRICE).getAsJsonObject().get(KEY_ATTRIBUTES).getAsJsonObject();
			item.price = attributes.get(KEY_AMOUNT).getAsFloat();
			
			attributes = jsonItem.get(KEY_ID).getAsJsonObject().get(KEY_ATTRIBUTES).getAsJsonObject();
			item.internalId = attributes.get(KEY_INTERNAL_ID).getAsString();
			item.externalId = attributes.get(KEY_BUNDLE_ID).getAsString();
			item.type = "Application"; // TODO: this can be obtained from the data
			item.source = "ios";
			
			items.add(item);
		}
		

		return items;
	}

}
