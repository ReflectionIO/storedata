package io.reflection.app.datatypes.shared;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class LookupItem extends DataType {
	public Integer dataaccountid;
	public Integer	itemid;
	public Integer	parentid;

	public String title;
	public String country;

	public String currency;
	public Integer price;

	public String sku;
	public String parentsku;


	@Override
	public JsonObject toJson() {
		JsonObject object = super.toJson();
		JsonElement jsonDataaccountid = dataaccountid == null ? JsonNull.INSTANCE : new JsonPrimitive(dataaccountid);
		object.add("dataaccountid", jsonDataaccountid);
		JsonElement jsonTitle = title == null ? JsonNull.INSTANCE : new JsonPrimitive(title);
		object.add("title", jsonTitle);
		JsonElement jsonCountry = country == null ? JsonNull.INSTANCE : new JsonPrimitive(country);
		object.add("country", jsonCountry);
		JsonElement jsonCurrency = currency == null ? JsonNull.INSTANCE : new JsonPrimitive(currency);
		object.add("currency", jsonCurrency);
		JsonElement jsonPrice = price == null ? JsonNull.INSTANCE : new JsonPrimitive(price);
		object.add("price", jsonPrice);
		JsonElement jsonparentid = parentid == null ? JsonNull.INSTANCE : new JsonPrimitive(parentid);
		object.add("parentid", jsonparentid);
		JsonElement jsonitemid = itemid == null ? JsonNull.INSTANCE : new JsonPrimitive(itemid);
		object.add("itemid", jsonitemid);
		return object;
	}

	@Override
	public void fromJson(JsonObject jsonObject) {
		super.fromJson(jsonObject);
		if (jsonObject.has("dataaccountid")) {
			JsonElement jsonDataaccountid = jsonObject.get("dataaccountid");
			if (jsonDataaccountid != null) {
				dataaccountid = Integer.valueOf(jsonDataaccountid.getAsInt());
			}
		}
		if (jsonObject.has("title")) {
			JsonElement jsonTitle = jsonObject.get("title");
			if (jsonTitle != null) {
				title = jsonTitle.getAsString();
			}
		}
		if (jsonObject.has("country")) {
			JsonElement jsonCountry = jsonObject.get("country");
			if (jsonCountry != null) {
				country = jsonCountry.getAsString();
			}
		}
		if (jsonObject.has("currency")) {
			JsonElement jsonCurrency = jsonObject.get("currency");
			if (jsonCurrency != null) {
				currency = jsonCurrency.getAsString();
			}
		}
		if (jsonObject.has("price")) {
			JsonElement jsonPrice = jsonObject.get("price");
			if (jsonPrice != null) {
				price = Integer.valueOf(jsonPrice.getAsInt());
			}
		}
		if (jsonObject.has("parentid")) {
			JsonElement jsonparentid = jsonObject.get("parentid");
			if (jsonparentid != null) {
				parentid = Integer.valueOf(jsonparentid.getAsInt());
			}
		}
		if (jsonObject.has("itemid")) {
			JsonElement jsonitemid = jsonObject.get("itemid");
			if (jsonitemid != null) {
				itemid = Integer.valueOf(jsonitemid.getAsInt());
			}
		}
		if (jsonObject.has("sku")) {
			JsonElement jsonsku = jsonObject.get("sku");
			if (jsonsku != null) {
				sku = jsonsku.getAsString();
			}
		}
		if (jsonObject.has("parentsku")) {
			JsonElement jsonparentsku = jsonObject.get("parentsku");
			if (jsonparentsku != null) {
				parentsku = jsonparentsku.getAsString();
			}
		}
	}

	public LookupItem dataaccountid(Integer dataaccountid) {
		this.dataaccountid = dataaccountid;
		return this;
	}

	public LookupItem title(String title) {
		this.title = title;
		return this;
	}

	public LookupItem country(String country) {
		this.country = country;
		return this;
	}

	public LookupItem currency(String currency) {
		this.currency = currency;
		return this;
	}

	public LookupItem price(Integer price) {
		this.price = price;
		return this;
	}

	public LookupItem parentid(Integer parentid) {
		this.parentid = parentid;
		return this;
	}

	public LookupItem itemid(Integer itemid) {
		this.itemid = itemid;
		return this;
	}

	public LookupItem sku(String sku) {
		this.sku = sku;
		return this;
	}

	public LookupItem parentsku(String parentsku) {
		this.parentsku = parentsku;
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((country == null) ? 0 : country.hashCode());
		result = prime * result + ((dataaccountid == null) ? 0 : dataaccountid.hashCode());
		result = prime * result + ((itemid == null) ? 0 : itemid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;

		if (!(obj instanceof LookupItem)) return false;

		LookupItem other = (LookupItem) obj;

		if (dataaccountid == null) {
			if (other.dataaccountid != null) return false;
		} else if (!dataaccountid.equals(other.dataaccountid)) return false;

		if (itemid == null) {
			if (other.itemid != null) return false;
		} else if (!itemid.equals(other.itemid)) return false;

		if (country == null) {
			if (other.country != null) return false;
		} else if (!country.equals(other.country)) return false;

		return true;
	}

	@Override
	public String toString() {
		return "LookupItem [dataaccountid=" + dataaccountid + ", itemid=" + itemid + ", parentid=" + parentid + ", title=" + title + ", country=" + country + ", currency=" + currency + ", price=" + price
				+ ", sku=" + sku + ", parentsku=" + parentsku + "]";
	}
}