/**
 * 
 */
package io.reflection.app.ingestors;

import io.reflection.app.datatypes.shared.Item;
import io.reflection.app.logging.GaeLevel;
import io.reflection.app.shared.util.DataTypeHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.willshex.gson.json.shared.Convert;

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
	private static final String KEY_ARTIST = "im:artist";
	private static final String KEY_IMAGE = "im:image";
	private static final String KEY_HEIGHT = "height";

	
	/* (non-Javadoc)
	 * @see io.reflection.app.ingestors.Parser#parse(java.lang.String, java.lang.Long, java.lang.String)
	 */
	@Override
	public List<Item> parse(String country, Long category, String data) {

		if (LOG.isLoggable(GaeLevel.DEBUG)) {
			LOG.log(GaeLevel.DEBUG, "Started parsing data");
		}

		List<Item> items = new ArrayList<Item>();

		JsonObject jsonRss = Convert.toJsonObject(data);
		JsonElement entriesElement = jsonRss.get(KEY_FEED).getAsJsonObject().get(KEY_ENTRY);

		if (entriesElement != null) {
			if (entriesElement.isJsonArray()) {
				JsonArray entries = entriesElement.getAsJsonArray();

				for (JsonElement entry : entries) {
					addItem(country, entry.getAsJsonObject(), items);
				}
			} else {
				addItem(country, entriesElement.getAsJsonObject(), items);
			}
		} else {
			if (LOG.isLoggable(Level.WARNING)) {
				LOG.warning(String.format("Skipping attempt to parse data with no entries", items.size()));
			}
		}

		if (LOG.isLoggable(GaeLevel.DEBUG)) {
			LOG.log(GaeLevel.DEBUG, String.format("Found [%d] items", items.size()));
		}

		return items;
	}

	/**
	 * @param entry
	 * @param items
	 */
	private void addItem(String country, JsonObject jsonItem, List<Item> items) {
		Item item = new Item();
		item.name = jsonItem.get(KEY_NAME).getAsJsonObject().get(KEY_LABEL).getAsString();

		JsonObject attributes = jsonItem.get(KEY_PRICE).getAsJsonObject().get(KEY_ATTRIBUTES).getAsJsonObject();
		item.price = Float.valueOf(attributes.get(KEY_AMOUNT).getAsFloat());
		item.currency = attributes.get(KEY_CURRENCY).getAsString();
		item.country = country;

		attributes = jsonItem.get(KEY_ID).getAsJsonObject().get(KEY_ATTRIBUTES).getAsJsonObject();
		item.internalId = attributes.get(KEY_INTERNAL_ID).getAsString();
		item.externalId = attributes.get(KEY_BUNDLE_ID).getAsString();
		item.type = "Application"; // LATER this can be obtained from the data
		item.source = DataTypeHelper.IOS_STORE_A3;
		item.creatorName = jsonItem.get(KEY_ARTIST).getAsJsonObject().get(KEY_LABEL).getAsString();

		JsonArray images = jsonItem.get(KEY_IMAGE).getAsJsonArray();

		for (JsonElement jsonElement : images) {
			attributes = jsonElement.getAsJsonObject().get(KEY_ATTRIBUTES).getAsJsonObject();

			if (attributes.get(KEY_HEIGHT).getAsString().equals("53")) {
				item.smallImage = jsonElement.getAsJsonObject().get(KEY_LABEL).getAsString();
			} else if (attributes.get(KEY_HEIGHT).getAsString().equals("75")) {
				item.mediumImage = jsonElement.getAsJsonObject().get(KEY_LABEL).getAsString();
			} else if (attributes.get(KEY_HEIGHT).getAsString().equals("100")) {
				item.largeImage = jsonElement.getAsJsonObject().get(KEY_LABEL).getAsString();
			}
		}
		
		if (LOG.isLoggable(GaeLevel.TRACE)) {
			LOG.log(GaeLevel.TRACE, String.format("Found item [%s]", item.name));
		}

		items.add(item);
	}

}
