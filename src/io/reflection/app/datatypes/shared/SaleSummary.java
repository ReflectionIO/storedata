//
//  SaleSummary.java
//  App
//
//  Created by Mitul Amin on June 1, 2015.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.datatypes.shared;

import java.util.Date;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class SaleSummary extends DataType {
	public Integer dataaccountid;
	public Date date;
	public String itemid;
	public String title;
	public String country;
	public String currency;
	public Integer price;
	public Integer	iphone_app_revenue					= 0;
	public Integer	ipad_app_revenue						= 0;
	public Integer	universal_app_revenue				= 0;
	public Integer	total_app_revenue						= 0;
	public Integer	iap_revenue									= 0;
	public Integer	total_revenue								= 0;
	public Integer iphone_downloads = 0;
	public Integer universal_downloads = 0;
	public Integer ipad_downloads = 0;
	public Integer total_downloads = 0;
	public Integer iphone_updates = 0;
	public Integer universal_updates = 0;
	public Integer ipad_updates = 0;
	public Integer total_updates = 0;
	public Integer total_download_and_updates = 0;
	public Integer free_subs_count = 0;
	public Integer paid_subs_count = 0;
	public Integer subs_revenue = 0;
	@Override
	public JsonObject toJson() {
		JsonObject object = super.toJson();
		JsonElement jsonDataaccountid = dataaccountid == null ? JsonNull.INSTANCE : new JsonPrimitive(dataaccountid);
		object.add("dataaccountid", jsonDataaccountid);
		JsonElement jsonDate = date == null ? JsonNull.INSTANCE : new JsonPrimitive(date.getTime());
		object.add("date", jsonDate);
		JsonElement jsonItemid = itemid == null ? JsonNull.INSTANCE : new JsonPrimitive(itemid);
		object.add("itemid", jsonItemid);
		JsonElement jsonTitle = title == null ? JsonNull.INSTANCE : new JsonPrimitive(title);
		object.add("title", jsonTitle);
		JsonElement jsonCountry = country == null ? JsonNull.INSTANCE : new JsonPrimitive(country);
		object.add("country", jsonCountry);
		JsonElement jsonCurrency = currency == null ? JsonNull.INSTANCE : new JsonPrimitive(currency);
		object.add("currency", jsonCurrency);
		JsonElement jsonPrice = price == null ? JsonNull.INSTANCE : new JsonPrimitive(price);
		object.add("price", jsonPrice);
		JsonElement jsonIphone_app_revenue = iphone_app_revenue == null ? JsonNull.INSTANCE : new JsonPrimitive(iphone_app_revenue);
		object.add("iphone_app_revenue", jsonIphone_app_revenue);
		JsonElement jsonIpad_app_revenue = ipad_app_revenue == null ? JsonNull.INSTANCE : new JsonPrimitive(ipad_app_revenue);
		object.add("ipad_app_revenue", jsonIpad_app_revenue);
		JsonElement jsonUniversal_app_revenue = universal_app_revenue == null ? JsonNull.INSTANCE : new JsonPrimitive(universal_app_revenue);
		object.add("universal_app_revenue", jsonUniversal_app_revenue);
		JsonElement jsonTotal_app_revenue = total_app_revenue == null ? JsonNull.INSTANCE : new JsonPrimitive(total_app_revenue);
		object.add("total_app_revenue", jsonTotal_app_revenue);
		JsonElement jsonIap_revenue = iap_revenue == null ? JsonNull.INSTANCE : new JsonPrimitive(iap_revenue);
		object.add("iap_revenue", jsonIap_revenue);
		JsonElement jsonTotal_revenue = total_revenue == null ? JsonNull.INSTANCE : new JsonPrimitive(total_revenue);
		object.add("total_revenue", jsonTotal_revenue);
		JsonElement jsonIphone_downloads = iphone_downloads == null ? JsonNull.INSTANCE : new JsonPrimitive(iphone_downloads);
		object.add("iphone_downloads", jsonIphone_downloads);
		JsonElement jsonUniversal_downloads = universal_downloads == null ? JsonNull.INSTANCE : new JsonPrimitive(universal_downloads);
		object.add("universal_downloads", jsonUniversal_downloads);
		JsonElement jsonIpad_downloads = ipad_downloads == null ? JsonNull.INSTANCE : new JsonPrimitive(ipad_downloads);
		object.add("ipad_downloads", jsonIpad_downloads);
		JsonElement jsonTotal_downloads = total_downloads == null ? JsonNull.INSTANCE : new JsonPrimitive(total_downloads);
		object.add("total_downloads", jsonTotal_downloads);
		JsonElement jsonIphone_updates = iphone_updates == null ? JsonNull.INSTANCE : new JsonPrimitive(iphone_updates);
		object.add("iphone_updates", jsonIphone_updates);
		JsonElement jsonUniversal_updates = universal_updates == null ? JsonNull.INSTANCE : new JsonPrimitive(universal_updates);
		object.add("universal_updates", jsonUniversal_updates);
		JsonElement jsonIpad_updates = ipad_updates == null ? JsonNull.INSTANCE : new JsonPrimitive(ipad_updates);
		object.add("ipad_updates", jsonIpad_updates);
		JsonElement jsonTotal_updates = total_updates == null ? JsonNull.INSTANCE : new JsonPrimitive(total_updates);
		object.add("total_updates", jsonTotal_updates);
		JsonElement jsonTotal_download_and_updates = total_download_and_updates == null ? JsonNull.INSTANCE : new JsonPrimitive(total_download_and_updates);
		object.add("total_download_and_updates", jsonTotal_download_and_updates);
		JsonElement jsonfree_subs_count = free_subs_count == null ? JsonNull.INSTANCE : new JsonPrimitive(free_subs_count);
		object.add("free_subs_count", jsonfree_subs_count);
		JsonElement jsonpaid_subs_count = paid_subs_count == null ? JsonNull.INSTANCE : new JsonPrimitive(paid_subs_count);
		object.add("paid_subs_count", jsonpaid_subs_count);
		JsonElement jsonsubs_revenue = subs_revenue == null ? JsonNull.INSTANCE : new JsonPrimitive(subs_revenue);
		object.add("subs_revenue", jsonsubs_revenue);
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
		if (jsonObject.has("date")) {
			JsonElement jsonDate = jsonObject.get("date");
			if (jsonDate != null) {
				date = new Date(jsonDate.getAsLong());
			}
		}
		if (jsonObject.has("itemid")) {
			JsonElement jsonItemid = jsonObject.get("itemid");
			if (jsonItemid != null) {
				itemid = jsonItemid.getAsString();
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
		if (jsonObject.has("iphone_app_revenue")) {
			JsonElement jsonIphone_app_revenue = jsonObject.get("iphone_app_revenue");
			if (jsonIphone_app_revenue != null) {
				iphone_app_revenue = Integer.valueOf(jsonIphone_app_revenue.getAsInt());
			}
		}
		if (jsonObject.has("ipad_app_revenue")) {
			JsonElement jsonIpad_app_revenue = jsonObject.get("ipad_app_revenue");
			if (jsonIpad_app_revenue != null) {
				ipad_app_revenue = Integer.valueOf(jsonIpad_app_revenue.getAsInt());
			}
		}
		if (jsonObject.has("universal_app_revenue")) {
			JsonElement jsonUniversal_app_revenue = jsonObject.get("universal_app_revenue");
			if (jsonUniversal_app_revenue != null) {
				universal_app_revenue = Integer.valueOf(jsonUniversal_app_revenue.getAsInt());
			}
		}
		if (jsonObject.has("total_app_revenue")) {
			JsonElement jsonTotal_app_revenue = jsonObject.get("total_app_revenue");
			if (jsonTotal_app_revenue != null) {
				total_app_revenue = Integer.valueOf(jsonTotal_app_revenue.getAsInt());
			}
		}
		if (jsonObject.has("iap_revenue")) {
			JsonElement jsonIap_revenue = jsonObject.get("iap_revenue");
			if (jsonIap_revenue != null) {
				iap_revenue = Integer.valueOf(jsonIap_revenue.getAsInt());
			}
		}
		if (jsonObject.has("total_revenue")) {
			JsonElement jsonTotal_revenue = jsonObject.get("total_revenue");
			if (jsonTotal_revenue != null) {
				total_revenue = Integer.valueOf(jsonTotal_revenue.getAsInt());
			}
		}
		if (jsonObject.has("iphone_downloads")) {
			JsonElement jsonIphone_downloads = jsonObject.get("iphone_downloads");
			if (jsonIphone_downloads != null) {
				iphone_downloads = Integer.valueOf(jsonIphone_downloads.getAsInt());
			}
		}
		if (jsonObject.has("universal_downloads")) {
			JsonElement jsonUniversal_downloads = jsonObject.get("universal_downloads");
			if (jsonUniversal_downloads != null) {
				universal_downloads = Integer.valueOf(jsonUniversal_downloads.getAsInt());
			}
		}
		if (jsonObject.has("ipad_downloads")) {
			JsonElement jsonIpad_downloads = jsonObject.get("ipad_downloads");
			if (jsonIpad_downloads != null) {
				ipad_downloads = Integer.valueOf(jsonIpad_downloads.getAsInt());
			}
		}
		if (jsonObject.has("total_downloads")) {
			JsonElement jsonTotal_downloads = jsonObject.get("total_downloads");
			if (jsonTotal_downloads != null) {
				total_downloads = Integer.valueOf(jsonTotal_downloads.getAsInt());
			}
		}
		if (jsonObject.has("iphone_updates")) {
			JsonElement jsonIphone_updates = jsonObject.get("iphone_updates");
			if (jsonIphone_updates != null) {
				iphone_updates = Integer.valueOf(jsonIphone_updates.getAsInt());
			}
		}
		if (jsonObject.has("universal_updates")) {
			JsonElement jsonUniversal_updates = jsonObject.get("universal_updates");
			if (jsonUniversal_updates != null) {
				universal_updates = Integer.valueOf(jsonUniversal_updates.getAsInt());
			}
		}
		if (jsonObject.has("ipad_updates")) {
			JsonElement jsonIpad_updates = jsonObject.get("ipad_updates");
			if (jsonIpad_updates != null) {
				ipad_updates = Integer.valueOf(jsonIpad_updates.getAsInt());
			}
		}
		if (jsonObject.has("total_updates")) {
			JsonElement jsonTotal_updates = jsonObject.get("total_updates");
			if (jsonTotal_updates != null) {
				total_updates = Integer.valueOf(jsonTotal_updates.getAsInt());
			}
		}
		if (jsonObject.has("total_download_and_updates")) {
			JsonElement jsonTotal_download_and_updates = jsonObject.get("total_download_and_updates");
			if (jsonTotal_download_and_updates != null) {
				total_download_and_updates = Integer.valueOf(jsonTotal_download_and_updates.getAsInt());
			}
		}
		if (jsonObject.has("free_subs_count")) {
			JsonElement jsonfree_subs_count = jsonObject.get("free_subs_count");
			if (jsonfree_subs_count != null) {
				free_subs_count = Integer.valueOf(jsonfree_subs_count.getAsInt());
			}
		}
		if (jsonObject.has("paid_subs_count")) {
			JsonElement jsonpaid_subs_count = jsonObject.get("paid_subs_count");
			if (jsonpaid_subs_count != null) {
				paid_subs_count = Integer.valueOf(jsonpaid_subs_count.getAsInt());
			}
		}
		if (jsonObject.has("subs_revenue")) {
			JsonElement jsonsubs_revenue = jsonObject.get("subs_revenue");
			if (jsonsubs_revenue != null) {
				subs_revenue = Integer.valueOf(jsonsubs_revenue.getAsInt());
			}
		}
	}

	public SaleSummary dataaccountid(int dataaccountid) {
		this.dataaccountid = dataaccountid;
		return this;
	}

	public SaleSummary date(Date date) {
		this.date = date;
		return this;
	}

	public SaleSummary itemid(String itemid) {
		this.itemid = itemid;
		return this;
	}

	public SaleSummary title(String title) {
		this.title = title;
		return this;
	}

	public SaleSummary country(String country) {
		this.country = country;
		return this;
	}

	public SaleSummary currency(String currency) {
		this.currency = currency;
		return this;
	}

	public SaleSummary price(int price) {
		this.price = price;
		return this;
	}

	public SaleSummary iphone_app_revenue(int iphone_app_revenue) {
		this.iphone_app_revenue = iphone_app_revenue;
		return this;
	}

	public SaleSummary ipad_app_revenue(int ipad_app_revenue) {
		this.ipad_app_revenue = ipad_app_revenue;
		return this;
	}

	public SaleSummary universal_app_revenue(int universal_app_revenue) {
		this.universal_app_revenue = universal_app_revenue;
		return this;
	}

	public SaleSummary total_app_revenue(int total_app_revenue) {
		this.total_app_revenue = total_app_revenue;
		return this;
	}

	public SaleSummary iap_revenue(int iap_revenue) {
		this.iap_revenue = iap_revenue;
		return this;
	}

	public SaleSummary total_revenue(int total_revenue) {
		this.total_revenue = total_revenue;
		return this;
	}

	public SaleSummary iphone_downloads(int iphone_downloads) {
		this.iphone_downloads = iphone_downloads;
		return this;
	}

	public SaleSummary universal_downloads(int universal_downloads) {
		this.universal_downloads = universal_downloads;
		return this;
	}

	public SaleSummary ipad_downloads(int ipad_downloads) {
		this.ipad_downloads = ipad_downloads;
		return this;
	}

	public SaleSummary total_downloads(int total_downloads) {
		this.total_downloads = total_downloads;
		return this;
	}

	public SaleSummary iphone_updates(int iphone_updates) {
		this.iphone_updates = iphone_updates;
		return this;
	}

	public SaleSummary universal_updates(int universal_updates) {
		this.universal_updates = universal_updates;
		return this;
	}

	public SaleSummary ipad_updates(int ipad_updates) {
		this.ipad_updates = ipad_updates;
		return this;
	}

	public SaleSummary total_updates(int total_updates) {
		this.total_updates = total_updates;
		return this;
	}

	public SaleSummary total_download_and_updates(int total_download_and_updates) {
		this.total_download_and_updates = total_download_and_updates;
		return this;
	}

	public SaleSummary free_subs_count(int free_subs_count) {
		this.free_subs_count = free_subs_count;
		return this;
	}

	public SaleSummary paid_subs_count(int paid_subs_count) {
		this.paid_subs_count = paid_subs_count;
		return this;
	}

	public SaleSummary subs_revenue(int subs_revenue) {
		this.subs_revenue = subs_revenue;
		return this;
	}
}