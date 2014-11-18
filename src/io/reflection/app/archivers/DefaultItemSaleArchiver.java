//
//  DefaultItemSaleArchiver.java
//  storedata
//
//  Created by William Shakour (billy1380) on 27 Aug 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.archivers;

import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.archivablekeyvalue.peristence.ValueAppender;
import io.reflection.app.archivablekeyvalue.peristence.objectify.ArchivableKeyValue;
import io.reflection.app.archivablekeyvalue.peristence.objectify.KeyValueArchiveManager;
import io.reflection.app.datatypes.shared.Category;
import io.reflection.app.datatypes.shared.Country;
import io.reflection.app.datatypes.shared.FeedFetch;
import io.reflection.app.datatypes.shared.FormType;
import io.reflection.app.datatypes.shared.Item;
import io.reflection.app.datatypes.shared.Sale;
import io.reflection.app.datatypes.shared.Store;
import io.reflection.app.helpers.ApiHelper;
import io.reflection.app.helpers.SliceHelper;
import io.reflection.app.logging.GaeLevel;
import io.reflection.app.modellers.ModellerFactory;
import io.reflection.app.service.category.CategoryServiceProvider;
import io.reflection.app.service.feedfetch.FeedFetchServiceProvider;
import io.reflection.app.service.sale.SaleServiceProvider;
import io.reflection.app.shared.util.DataTypeHelper;
import io.reflection.app.shared.util.PagerHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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
	public void archiveSale(Sale sale) throws DataAccessException {
		KeyValueArchiveManager.get().setAppender(Sale.class, new ValueAppender<Sale>() {

			@Override
			public String getNewValue(String currentValue, Sale object) {
				String newValue = currentValue;

				if (object != null) {
					JsonElement jsonElement = currentValue == null ? null : (new JsonParser()).parse(currentValue);
					JsonArray newJsonArray = new JsonArray();

					if (jsonElement != null && jsonElement.isJsonArray()) {
						JsonArray jsonArray = jsonElement.getAsJsonArray();

						Sale existingSale;
						for (JsonElement jsonSale : jsonArray) {
							existingSale = new Sale();
							existingSale.fromJson(jsonSale.getAsJsonObject());

							if (existingSale.id.longValue() != object.id.longValue()) {
								newJsonArray.add(jsonSale);
							}
						}
					}

					newJsonArray.add(object.toJson());
					newValue = JsonUtils.cleanJson(newJsonArray.toString());
				}

				return newValue;
			}
		});

		Category allCategory = null;

		if (sale.category == null) {
			if (allCategory == null) {
				allCategory = CategoryServiceProvider.provide().getAllCategory(DataTypeHelper.createStore(sale.source));
			}

			sale.category = allCategory;
		}

		KeyValueArchiveManager.get().appendToValue(createArchiveKey(sale), sale);
	}

	private String createArchiveKey(Sale sale) {
		Item item = new Item();
		item.internalId = sale.itemId;

		Store store = DataTypeHelper.createStore(sale.source);

		Country country = DataTypeHelper.createCountry(sale.country);

		FormType form = ModellerFactory.getModellerForStore(store.a3Code).getForm(sale.type);

		return createKey(Long.valueOf(SliceHelper.offset(sale.date)), item, form, store, country, sale.category);
	}

	@Override
	public String createKey(Long slice, Item item, FormType form, Store store, Country country, Category category) {
		return StringUtils.join(Arrays.asList("archiver", "item", "sale", slice.toString(), item.internalId == null ? item.id.toString() : item.internalId,
				form.toString(), store.a3Code, country.a2Code, category.id.toString()), ".");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.itemsalearchivers.ItemSaleArchiver#getItemSales(java.lang.String)
	 */
	@Override
	public List<Sale> getItemSales(String key) {
		List<Sale> sales = null;
		Map<Date, Sale> salesLookup = new HashMap<Date, Sale>();
		ArchivableKeyValue value = KeyValueArchiveManager.get().getArchiveKeyValue(key);

		// Date date;

		if (value != null && value.value != null && value.value.length() > 0) {
			JsonElement jsonElement = (new JsonParser()).parse(value.value);

			if (jsonElement.isJsonArray()) {
				JsonArray jsonArray = jsonElement.getAsJsonArray();

				Sale sale, existingSale;
				for (JsonElement jsonArrayElement : jsonArray) {
					if (jsonArrayElement.isJsonObject()) {
						sale = new Sale();
						sale.fromJson(jsonArrayElement.getAsJsonObject());

						if (sales == null) {
							sales = new ArrayList<Sale>();
						}

						sale.date = ApiHelper.removeTime(sale.date);

						if ((existingSale = salesLookup.get(sale.date)) == null) {
							sales.add(sale);
							salesLookup.put(sale.date, sale);
						} else {
							if (sale.code.longValue() == existingSale.code.longValue()) {
								if ((existingSale.position == null || existingSale.position.intValue() == 0) && sale.position != null
										&& sale.position.intValue() != 0) {
									existingSale.position = sale.position;
								}

								if ((existingSale.grossingPosition == null || existingSale.grossingPosition.intValue() == 0) && sale.grossingPosition != null
										&& sale.grossingPosition.intValue() != 0) {
									existingSale.grossingPosition = sale.grossingPosition;
								}
							}
						}
					}
				}
			}
		}

		if (sales != null) {
			Collections.sort(sales, new Comparator<Sale>() {

				@Override
				public int compare(Sale o1, Sale o2) {
					int compare = 0;

					if (o1.begin.getTime() > o2.begin.getTime()) {
						compare = 1;
					} else if (o1.begin.getTime() < o2.begin.getTime()) {
						compare = -1;
					}

					return compare;
				}
			});
		}

		return sales;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.itemsalearchivers.ItemSaleArchiver#enqueue(io.reflection.app.api.shared.datatypes.Pager, java.lang.Boolean)
	 */
	@Override
	public void enqueuePagerSales(Pager pager, Boolean next) {
		try {
			List<Long> saleIds = SaleServiceProvider.provide().getSaleIds(pager);

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
			FeedFetch feedFetch = FeedFetchServiceProvider.provide().getFeedFetch(id);

			List<Sale> sales = SaleServiceProvider.provide().getGatherCodeSales(DataTypeHelper.createCountry(feedFetch.country),
					DataTypeHelper.createStore(feedFetch.store), feedFetch.category, feedFetch.type, feedFetch.code, PagerHelper.createInfinitePager(),
					Boolean.TRUE);

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
		if (LOG.isLoggable(GaeLevel.TRACE)) {
			LOG.log(GaeLevel.TRACE, "Entering...");
		}

		try {
			Queue queue = QueueFactory.getQueue("archive");

			TaskOptions options = TaskOptions.Builder.withUrl("/archive").method(Method.POST);
			options.param("type", "dataaccountfetchsales");
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
}
