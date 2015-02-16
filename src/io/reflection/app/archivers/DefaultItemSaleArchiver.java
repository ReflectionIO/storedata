//
//  DefaultItemSaleArchiver.java
//  storedata
//
//  Created by William Shakour (billy1380) on 27 Aug 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.archivers;

import static io.reflection.app.service.sale.ISaleService.FREE_OR_PAID_APP_IPAD_IOS;
import static io.reflection.app.service.sale.ISaleService.FREE_OR_PAID_APP_IPHONE_AND_IPOD_TOUCH_IOS;
import static io.reflection.app.service.sale.ISaleService.FREE_OR_PAID_APP_UNIVERSAL_IOS;
import static io.reflection.app.service.sale.ISaleService.INAPP_PURCHASE_PURCHASE_IOS;
import static io.reflection.app.service.sale.ISaleService.INAPP_PURCHASE_SUBSCRIPTION_IOS;
import static io.reflection.app.service.sale.ISaleService.UPDATE_IPAD_IOS;
import static io.reflection.app.service.sale.ISaleService.UPDATE_IPHONE_AND_IPOD_TOUCH_IOS;
import static io.reflection.app.service.sale.ISaleService.UPDATE_UNIVERSAL_IOS;
import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.archivablekeyvalue.peristence.ValueAppender;
import io.reflection.app.archivablekeyvalue.peristence.objectify.ArchivableKeyValue;
import io.reflection.app.archivablekeyvalue.peristence.objectify.KeyValueArchiveManager;
import io.reflection.app.datatypes.shared.Country;
import io.reflection.app.datatypes.shared.DataAccount;
import io.reflection.app.datatypes.shared.DataAccountFetch;
import io.reflection.app.datatypes.shared.FormType;
import io.reflection.app.datatypes.shared.Item;
import io.reflection.app.datatypes.shared.Rank;
import io.reflection.app.datatypes.shared.Sale;
import io.reflection.app.logging.GaeLevel;
import io.reflection.app.service.dataaccountfetch.DataAccountFetchServiceProvider;
import io.reflection.app.service.item.ItemServiceProvider;
import io.reflection.app.service.sale.SaleServiceProvider;
import io.reflection.app.shared.util.DataTypeHelper;
import io.reflection.app.shared.util.PagerHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.google.appengine.api.taskqueue.TransientFailureException;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.spacehopperstudios.utility.JsonUtils;
import com.spacehopperstudios.utility.StringUtils;

/**
 * @author billy1380
 * 
 */
public class DefaultItemSaleArchiver implements ItemSaleArchiver {

	private static final Logger LOG = Logger.getLogger(DefaultItemSaleArchiver.class.getName());

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.itemsalearchivers.ItemSaleArchiver#enqueue(java.lang.Long)
	 */
	@Override
	public void enqueueIdSale(Long id) {
		if (LOG.isLoggable(GaeLevel.TRACE)) {
			LOG.log(GaeLevel.TRACE, "Entering...");
		}

		try {
			Queue queue = QueueFactory.getQueue("archive");

			TaskOptions options = TaskOptions.Builder.withUrl("/archive").method(Method.POST);
			options.param("type", "itemsale");
			options.param("id", id.toString());

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.itemsalearchivers.ItemSaleArchiver#archive(java.lang.Long)
	 */
	@Override
	public void archiveIdSale(Long id) throws DataAccessException {
		archiveSale(SaleServiceProvider.provide().getSale(id));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.itemsalearchivers.ItemSaleArchiver#archive(io.reflection.app.datatypes.shared.Sale)
	 */
	@Override
	public void archiveSale(Sale value) throws DataAccessException {
//		Country country = DataTypeHelper.createCountry(value.country);
		Item item = getSaleItem(value);

		Map<String, Object> other = new HashMap<String, Object>();

		if (item != null) {
			other.put("item", item);
		}

		List<FormType> forms = getSaleFormTypes(value);
//		Long slice = Long.valueOf(SliceHelper.offset(value.end));
//
//		KeyValueArchiveManager.get().setAppender(Sale.class, new ValueAppender<Sale>() {
//
//			@Override
//			public String getNewValue(String key, String currentValue, Sale value, Map<String, Object> other) {
//				String newValue = currentValue;
//
//				Item item = (Item) other.get("item");
//
//				// if we have a new sale and we can find the item
//				if (value != null && item != null) {
//					JsonElement jsonElement = currentValue == null ? null : (new JsonParser()).parse(currentValue);
//					JsonArray newJsonArray = new JsonArray();
//
//					boolean found = false;
//
//					if (jsonElement != null && jsonElement.isJsonArray()) {
//						JsonArray jsonArray = jsonElement.getAsJsonArray();
//						Rank existingRank;
//
//						for (JsonElement jsonRank : jsonArray) {
//							existingRank = new Rank();
//							existingRank.fromJson(jsonRank.getAsJsonObject());
//
//							if (!found && existingRank.itemId.equalsIgnoreCase(item.internalId) && value.end.getTime() == existingRank.date.getTime()) {
//								found = true;
//								addDownloadsAndRevenue(existingRank, value);
//							}
//
//							newJsonArray.add(existingRank.toJson());
//						}
//					}
//
//					if (!found) {
//						Rank newRank = new Rank();
//
//						newRank.revenue = Float.valueOf(0.0f);
//						newRank.downloads = Integer.valueOf(0);
//
//						newRank.country = value.country;
//						newRank.currency = value.customerCurrency;
//						newRank.date = value.end;
//						newRank.created = DateTime.now().toDate();
//						newRank.itemId = item.internalId;
//						newRank.source = item.source;
//
//						addDownloadsAndRevenue(newRank, value);
//
//						newJsonArray.add(newRank.toJson());
//					}
//
//					newValue = JsonUtils.cleanJson(newJsonArray.toString());
//				}
//
//				return newValue;
//			}
//
//			private void addDownloadsAndRevenue(Rank rank, Sale value) {
//				rank.revenue += Math.abs(value.units.floatValue()) * value.customerPrice.floatValue();
//
//				if (value.typeIdentifier.equals(FREE_OR_PAID_APP_IPHONE_AND_IPOD_TOUCH_IOS) || value.typeIdentifier.equals(FREE_OR_PAID_APP_UNIVERSAL_IOS)
//						|| value.typeIdentifier.equals(FREE_OR_PAID_APP_IPAD_IOS)) {
//					rank.downloads += value.units.intValue();
//					// Ignore price if the Sale is a refund or a promotion
//					if (rank.price == null && value.units.intValue() > 0 && value.promoCode.equals(" ")) {
//						rank.price = value.customerPrice;
//					}
//				}
//			}
//		});

		KeyValueArchiveManager.get().setAppender(Item.class, new ValueAppender<Item>() {

			@Override
			public String getNewValue(String key, String currentValue, Item value, Map<String, Object> other) {
				String newValue = currentValue;

				if (value != null) {
					JsonElement jsonElement = currentValue == null ? null : (new JsonParser()).parse(currentValue);
					JsonArray newJsonArray = new JsonArray();

					boolean found = false;

					if (jsonElement != null && jsonElement.isJsonArray()) {
						JsonArray jsonArray = jsonElement.getAsJsonArray();

						String existingItemId;
						for (JsonElement jsonItemId : jsonArray) {
							existingItemId = jsonItemId.getAsString();

							if (existingItemId.equalsIgnoreCase(value.internalId)) {
								found = true;
							}

							newJsonArray.add(jsonItemId);
						}
					}

					if (!found) {
						newJsonArray.add(new JsonPrimitive(value.internalId));
					}

					newValue = JsonUtils.cleanJson(newJsonArray.toString());
				}

				return newValue;
			}
		});

		for (FormType form : forms) {
//			KeyValueArchiveManager.get().appendToValue(createRanksKey(slice, value.account, country, form), value, other);

			if (item != null) {
//				KeyValueArchiveManager.get().appendToValue(createItemRanksKey(slice, item, country, form), value, other);
				KeyValueArchiveManager.get().appendToValue(createItemsKey(value.account, form), item);
			}
		}
	}

	/**
	 * Returns the item for a sale either by item id or based on the parent id and the item sku for iaps
	 * 
	 * @param sale
	 * @return
	 */
	private Item getSaleItem(Sale sale) {
		Item item = null;

		try {
			String internalId = null;

			if (INAPP_PURCHASE_PURCHASE_IOS.equals(sale.typeIdentifier) || INAPP_PURCHASE_SUBSCRIPTION_IOS.equals(sale.typeIdentifier)) {
				internalId = SaleServiceProvider.provide().getSkuItemId(sale.parentIdentifier);
			} else {
				internalId = sale.item.internalId;
			}

			if (internalId != null && internalId.length() != 0) {
				item = ItemServiceProvider.provide().getInternalIdItem(internalId);

				if (item == null) {
					item = new Item();
					item.internalId = internalId;
				}
			}
		} catch (DataAccessException daEx) {
			throw new RuntimeException(daEx);
		}

		return item;
	}

	/**
	 * Returns the form factors of devices for which this sale is to be added
	 * 
	 * @param sale
	 * @return
	 */
	private List<FormType> getSaleFormTypes(Sale sale) {
		List<FormType> forms = new ArrayList<FormType>();

		if (FREE_OR_PAID_APP_UNIVERSAL_IOS.equals(sale.typeIdentifier) || UPDATE_UNIVERSAL_IOS.equals(sale.typeIdentifier)
				|| FREE_OR_PAID_APP_IPHONE_AND_IPOD_TOUCH_IOS.equals(sale.typeIdentifier) || UPDATE_IPHONE_AND_IPOD_TOUCH_IOS.equals(sale.typeIdentifier)
				|| INAPP_PURCHASE_PURCHASE_IOS.equals(sale.typeIdentifier) || INAPP_PURCHASE_SUBSCRIPTION_IOS.equals(sale.typeIdentifier)) {
			forms.add(FormType.FormTypeOther);
		}

		if (FREE_OR_PAID_APP_UNIVERSAL_IOS.equals(sale.typeIdentifier) || UPDATE_UNIVERSAL_IOS.equals(sale.typeIdentifier)
				|| FREE_OR_PAID_APP_IPAD_IOS.equals(sale.typeIdentifier) || UPDATE_IPAD_IOS.equals(sale.typeIdentifier)
				|| INAPP_PURCHASE_PURCHASE_IOS.equals(sale.typeIdentifier) || INAPP_PURCHASE_SUBSCRIPTION_IOS.equals(sale.typeIdentifier)) {
			forms.add(FormType.FormTypeTablet);
		}

		return forms;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.archivers.ItemSaleArchiver#getRanks(java.lang.String)
	 */
	@Override
	public List<Rank> getRanks(String key) {
		List<Rank> ranks = null;
		ArchivableKeyValue value = KeyValueArchiveManager.get().getArchiveKeyValue(key);

		if (value != null && value.value != null && value.value.length() > 0) {
			JsonElement jsonElement = (new JsonParser()).parse(value.value);

			if (jsonElement.isJsonArray()) {
				JsonArray jsonArray = jsonElement.getAsJsonArray();

				Rank rank;
				for (JsonElement jsonArrayElement : jsonArray) {
					if (jsonArrayElement.isJsonObject()) {
						rank = new Rank();
						rank.fromJson(jsonArrayElement.getAsJsonObject());

						if (ranks == null) {
							ranks = new ArrayList<Rank>();
						}

						ranks.add(rank);
					}
				}
			}
		}

		if (ranks != null) {
			Collections.sort(ranks, new Comparator<Rank>() {

				@Override
				public int compare(Rank o1, Rank o2) {
					int compare = 0;

					if (o1.date.getTime() > o2.date.getTime()) {
						compare = 1;
					} else if (o1.date.getTime() < o2.date.getTime()) {
						compare = -1;
					}

					return compare;
				}
			});
		}

		return ranks;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.itemsalearchivers.ItemSaleArchiver#enqueue(io.reflection.app.api.shared.datatypes.Pager, java.lang.Boolean)
	 */
	@Override
	public void enqueuePagerSales(Pager pager, Boolean next) {
		try {
			List<Long> saleIds = SaleServiceProvider.provide().getAllSaleIds(pager);

			if (saleIds != null) {
				for (Long saleId : saleIds) {
					enqueueIdSale(saleId);
				}

				if (next != null && next.booleanValue() && pager.count.intValue() == saleIds.size()) {
					enqueueNext(pager);
				}
			}
		} catch (DataAccessException daEx) {
			throw new RuntimeException(daEx);
		}
	}

	private void enqueueNext(Pager pager) {
		if (LOG.isLoggable(GaeLevel.TRACE)) {
			LOG.log(GaeLevel.TRACE, "Entering...");
		}

		try {
			Queue queue = QueueFactory.getQueue("archive");

			TaskOptions options = TaskOptions.Builder.withUrl("/archive").method(Method.POST);
			options.param("type", "itemsale");
			options.param("pager", Boolean.TRUE.toString());
			options.param("next", Boolean.TRUE.toString());
			options.param("start", Long.toString(pager.start.longValue() + pager.count.longValue()));
			options.param("count", pager.count.toString());

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.itemsalearchivers.ItemSaleArchiver#enqueueFeedFetch(java.lang.Long)
	 */
	@Override
	public void archiveIdDataAccountFetchSales(Long id) {
		try {
			DataAccountFetch dataAccount = DataAccountFetchServiceProvider.provide().getDataAccountFetch(id);

			List<Sale> sales = SaleServiceProvider.provide().getDataAccountFetchSales(dataAccount, PagerHelper.createInfinitePager());

			for (Sale sale : sales) {
				archiveSale(sale);
			}
		} catch (DataAccessException daEx) {
			throw new RuntimeException(daEx);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.itemsalearchivers.ItemSaleArchiver#enqueueIdFeedFetch(java.lang.Long)
	 */
	@Override
	public void enqueueIdDataAccountFetch(Long id) {
		List<Long> saleIds = null;
		try {
			saleIds = SaleServiceProvider.provide().getDataAccountFetchSaleIds(DataTypeHelper.createDataAccountFetch(id), PagerHelper.createInfinitePager());
		} catch (DataAccessException daEx) {
			throw new RuntimeException(daEx);
		}

		if (saleIds != null) {
			for (Long saleId : saleIds) {
				enqueueIdSale(saleId);
			}
		}
		// if (LOG.isLoggable(GaeLevel.TRACE)) {
		// LOG.log(GaeLevel.TRACE, "Entering...");
		// }
		//
		// try {
		// Queue queue = QueueFactory.getQueue("archive");
		//
		// TaskOptions options = TaskOptions.Builder.withUrl("/archive").method(Method.POST);
		// options.param("type", "dataaccountfetchsales");
		// options.param("id", id.toString());
		//
		// try {
		// queue.add(options);
		// } catch (TransientFailureException ex) {
		// if (LOG.isLoggable(Level.WARNING)) {
		// LOG.warning(String.format("Could not queue a message because of [%s] - will retry it once", ex.toString()));
		// }
		//
		// // retry once
		// try {
		// queue.add(options);
		// } catch (TransientFailureException reEx) {
		// if (LOG.isLoggable(Level.SEVERE)) {
		// LOG.log(Level.SEVERE,
		// String.format("Retry of with payload [%s] failed while adding to queue [%s] twice", options.toString(), queue.getQueueName()),
		// reEx);
		// }
		// }
		// }
		// } finally {
		// if (LOG.isLoggable(GaeLevel.TRACE)) {
		// LOG.log(GaeLevel.TRACE, "Exiting...");
		// }
		// }
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.archivers.ItemSaleArchiver#createItemsKey(io.reflection.app.datatypes.shared.DataAccount,
	 * io.reflection.app.datatypes.shared.FormType)
	 */
	@Override
	public String createItemsKey(DataAccount dataAccount, FormType form) {
		return StringUtils.join(Arrays.asList("archiver", "sale", "items", dataAccount.id.toString(), form.toString()), ".");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.archivers.ItemSaleArchiver#getItemIds(java.lang.String)
	 */
	@Override
	public List<String> getItemsIds(String key) {
		List<String> items = null;
		ArchivableKeyValue value = KeyValueArchiveManager.get().getArchiveKeyValue(key);

		if (value != null && value.value != null && value.value.length() > 0) {
			JsonElement jsonElement = (new JsonParser()).parse(value.value);

			if (jsonElement.isJsonArray()) {
				JsonArray jsonArray = jsonElement.getAsJsonArray();

				String itemInternalId;
				for (JsonElement jsonArrayElement : jsonArray) {
					if (jsonArrayElement.isJsonPrimitive()) {
						itemInternalId = jsonArrayElement.getAsString();

						if (items == null) {
							items = new ArrayList<String>();
						}

						items.add(itemInternalId);
					}
				}
			}
		}

		return items;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.archivers.ItemSaleArchiver#createItemRanksKey(java.lang.Long, io.reflection.app.datatypes.shared.Item,
	 * io.reflection.app.datatypes.shared.Country, io.reflection.app.datatypes.shared.FormType)
	 */
	@Override
	public String createItemRanksKey(Long slice, Item item, Country country, FormType form) {
		return StringUtils.join(
				Arrays.asList("archiver", "item", "sale", "rank", slice.toString(), item.internalId == null ? item.id.toString() : item.internalId,
						form.toString(), country.a2Code), ".");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.archivers.ItemSaleArchiver#createRanksKey(java.lang.Long, io.reflection.app.datatypes.shared.DataAccount,
	 * io.reflection.app.datatypes.shared.Country, io.reflection.app.datatypes.shared.FormType)
	 */
	@Override
	public String createRanksKey(Long slice, DataAccount dataAccount, Country country, FormType form) {
		return StringUtils.join(Arrays.asList("archiver", "sale", "rank", slice.toString(), dataAccount.id.toString(), form.toString(), country.a2Code), ".");
	}

}
