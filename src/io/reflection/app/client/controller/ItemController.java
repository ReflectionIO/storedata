//
//  ItemController.java
//  storedata
//
//  Created by William Shakour (billy1380) on 28 Jan 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.controller;

import io.reflection.app.api.admin.client.AdminService;
import io.reflection.app.api.admin.shared.call.GetItemsRequest;
import io.reflection.app.api.admin.shared.call.GetItemsResponse;
import io.reflection.app.api.admin.shared.call.event.GetItemsEventHandler.GetItemsFailure;
import io.reflection.app.api.admin.shared.call.event.GetItemsEventHandler.GetItemsSuccess;
import io.reflection.app.api.admin.shared.call.event.SearchForItemEventHandler.SearchForItemFailure;
import io.reflection.app.api.admin.shared.call.event.SearchForItemEventHandler.SearchForItemSuccess;
import io.reflection.app.api.core.client.CoreService;
import io.reflection.app.api.core.shared.call.GetLinkedAccountItemsRequest;
import io.reflection.app.api.core.shared.call.GetLinkedAccountItemsResponse;
import io.reflection.app.api.core.shared.call.SearchForItemRequest;
import io.reflection.app.api.core.shared.call.SearchForItemResponse;
import io.reflection.app.api.core.shared.call.event.GetLinkedAccountItemsEventHandler.GetLinkedAccountItemsFailure;
import io.reflection.app.api.core.shared.call.event.GetLinkedAccountItemsEventHandler.GetLinkedAccountItemsSuccess;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.api.shared.datatypes.SortDirectionType;
import io.reflection.app.client.DefaultEventBus;
import io.reflection.app.client.helper.ApiCallHelper;
import io.reflection.app.datatypes.shared.Item;
import io.reflection.app.datatypes.shared.Store;
import io.reflection.app.shared.util.PagerHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.http.client.Request;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author billy1380
 * 
 */
public class ItemController extends AsyncDataProvider<Item> implements ServiceConstants {

	private static ItemController mOne = null;

	private Map<String, Item> mItemCache = new HashMap<String, Item>(); // Cache items meanwhile user is logged in

	private Map<String, List<Item>> mItemsSearchCache = new HashMap<String, List<Item>>(); // Cache of user searches

	private List<Item> userItemList = new ArrayList<Item>();
	private Map<String, Item> userItemsLookup = new HashMap<String, Item>();
	private long userItemCount = -1;

	// private List<Item> rows;
	// private long count = 0;
	private Pager pager = null;
	private String searchQuery = null;
	private Request current;
	private Request currentLinkedAccountItems;

	// private Pager mLookupPager; // Lookup server calls pager
	private Pager mSearchPager; // User search calls pager

	public static ItemController get() {
		if (mOne == null) {
			mOne = new ItemController();
		}

		return mOne;
	}

	/**
	 * Retrieve the list of items based on the user query
	 * 
	 * @param query
	 *            Search query
	 * @return searchResults Items retrieved after user search
	 */
	public List<Item> searchForItems(String query) {
		List<Item> searchResults = mItemsSearchCache.get(query); // Get, if exists, a cached search

		if (searchResults == null) {
			fetchItemsQuery(query); // If not cached, search in the DB
		}

		return searchResults;
	}

	/**
	 * Retrieve (and cache) items when the users executes a search
	 * 
	 * @param query
	 */
	private void fetchItemsQuery(String query) {
		CoreService service = ServiceCreator.createCoreService();

		final SearchForItemRequest input = new SearchForItemRequest();
		input.accessCode = ACCESS_CODE;

		input.session = SessionController.get().getSessionForApiCall();
		input.query = query;

		if (mSearchPager == null) {
			mSearchPager = new Pager();
			mSearchPager.count = STEP;
			mSearchPager.start = Long.valueOf(0);
		}

		input.pager = mSearchPager;

		service.searchForItem(input, new AsyncCallback<SearchForItemResponse>() {

			@Override
			public void onSuccess(SearchForItemResponse output) {
				if (output != null && output.status == StatusType.StatusTypeSuccess) {

					addItemsToCache(output.items);

					// Add retrieved items into search items cache ( Map<"query", List<Item>> )
					mItemsSearchCache.put(input.query, output.items == null ? new ArrayList<Item>() : output.items);
				}

				DefaultEventBus.get().fireEventFromSource(new SearchForItemSuccess(input, output), ItemController.this);
			}

			@Override
			public void onFailure(Throwable caught) {
				DefaultEventBus.get().fireEventFromSource(new SearchForItemFailure(input, caught), ItemController.this);
			}
		});
	}

	private void fetchItems() {
		fetchItems(searchQuery);
	}

	/**
	 * Retrieves and caches items. This method is only used from the admin system and requires admin privileges to run
	 */
	public void fetchItems(String query) {
		if (current != null) {
			current.cancel();
			current = null;
		}

		AdminService service = ServiceCreator.createAdminService();

		final GetItemsRequest input = new GetItemsRequest();
		input.accessCode = ACCESS_CODE;

		input.session = SessionController.get().getSessionForApiCall();

		if (pager == null) {
			pager = new Pager();
			pager.count = SHORT_STEP;
			pager.start = Long.valueOf(0);
		}
		input.pager = pager;

		input.query = searchQuery = query;

		if ("" == input.query) {
			input.query = searchQuery = null;
		}

		current = service.getItems(input, new AsyncCallback<GetItemsResponse>() {

			@Override
			public void onSuccess(GetItemsResponse output) {
				current = null;
				if (output != null && output.status == StatusType.StatusTypeSuccess) {
					addItemsToCache(output.items);

					if (output.pager != null) {
						pager = output.pager;

						// if (pager.totalCount != null) {
						// count = pager.totalCount.longValue();
						// }
					}

					// if (rows == null) {
					// rows = new ArrayList<Item>();
					// }

					// rows.addAll(output.items);

					updateRowCount(Integer.MAX_VALUE, false);
					updateRowData(input.pager.start.intValue(), output.items == null ? Collections.<Item> emptyList() : output.items);
				}

				DefaultEventBus.get().fireEventFromSource(new GetItemsSuccess(input, output), ItemController.this);
			}

			@Override
			public void onFailure(Throwable caught) {
				current = null;
				DefaultEventBus.get().fireEventFromSource(new GetItemsFailure(input, caught), ItemController.this);
			}
		});
	}

	/**
	 * Fetch the list of Item related to the linked account currently selected in the filter
	 */
	public void fetchLinkedAccountItems() {
		if (currentLinkedAccountItems != null) {
			currentLinkedAccountItems.cancel();
			currentLinkedAccountItems = null;
		}

		CoreService service = ServiceCreator.createCoreService();

		final GetLinkedAccountItemsRequest input = new GetLinkedAccountItemsRequest();
		input.accessCode = ACCESS_CODE;
		input.session = SessionController.get().getSessionForApiCall();
		if (pager == null) {
			pager = new Pager();
			pager.count = STEP;
			pager.start = Long.valueOf(0);
			pager.sortDirection = SortDirectionType.SortDirectionTypeDescending;
		}
		input.pager = pager;

		input.linkedAccount = FilterController.get().getLinkedAccount();

		Store store = new Store();
		store.a3Code = FilterController.get().getFilter().getStoreA3Code();
		input.store = ApiCallHelper.createStoreForApiCall(FilterController.get().getStore());

		input.listType = FilterController.get().getListTypes().get(0);

		currentLinkedAccountItems = service.getLinkedAccountItems(input, new AsyncCallback<GetLinkedAccountItemsResponse>() {

			@Override
			public void onSuccess(GetLinkedAccountItemsResponse output) {
				currentLinkedAccountItems = null;
				DefaultEventBus.get().fireEventFromSource(new GetLinkedAccountItemsSuccess(input, output), ItemController.this);
			}

			@Override
			public void onFailure(Throwable caught) {
				currentLinkedAccountItems = null;
				DefaultEventBus.get().fireEventFromSource(new GetLinkedAccountItemsFailure(input, caught), ItemController.this);
			}
		});
	}

	// /**
	// * @return
	// */
	// private Pager lookupPager() {
	// if (mLookupPager == null) {
	// mLookupPager = new Pager();
	// mLookupPager.start = Long.valueOf(0);
	// mLookupPager.count = Long.valueOf(1);
	// mLookupPager.sortBy = "internalid";
	// mLookupPager.sortDirection = SortDirectionType.SortDirectionTypeAscending;
	// }
	//
	// return mLookupPager;
	// }
	//
	// /**
	// * Retrieve the item, identified by external id, from DB (it happens in case of cache miss)
	// *
	// * @param internalId
	// */
	// private void fetchItem(String internalId) {
	// CoreService service = ServiceCreator.createCoreService();
	//
	// final SearchForItemRequest input = new SearchForItemRequest();
	// input.accessCode = ACCESS_CODE;
	//
	// input.session = SessionController.get().getSessionForApiCall();
	// input.query = internalId;
	//
	// input.pager = lookupPager();
	//
	// service.searchForItem(input, new AsyncCallback<SearchForItemResponse>() {
	//
	// @Override
	// public void onSuccess(SearchForItemResponse output) {
	// if (output != null && output.status == StatusType.StatusTypeSuccess) {
	//
	// if (output.items != null) {
	// for (Item item : output.items) {
	// mItemCache.put(item.internalId, item); // Add item to cache
	// }
	// }
	// }
	//
	// EventController.get().fireEventFromSource(new SearchForItemSuccess(input, output), ItemController.this);
	// }
	//
	// @Override
	// public void onFailure(Throwable caught) {
	// EventController.get().fireEventFromSource(new SearchForItemFailure(input, caught), ItemController.this);
	// }
	// });
	// }

	/**
	 * Clear Items cache when user logs out
	 */
	public void clearItemCache() {
		mItemCache.clear();
	}

	/**
	 * Add the items in the cache, indexing it by the external ID
	 * 
	 * @param items
	 *            Items to cache
	 * 
	 */
	public void addItemsToCache(List<Item> items) {
		if (items != null) {
			for (Item item : items) {
				mItemCache.put(item.internalId, item);
			}
		}
	}

	/**
	 * Retrieve an item, looking first in the cache or in the DB in case of miss
	 * 
	 * @param internalId
	 *            id of the item to retrieve
	 * @return the item
	 */
	public Item lookupItem(String internalId) {
		Item item = mItemCache.get(internalId);

		// if (item == null) {
		// fetchItem(internalId);
		// }

		return item;
	}

	/**
	 * @param item
	 */
	public void addItemToCache(Item item) {
		if (item != null) {
			mItemCache.put(item.internalId, item);
		}
	}

	public Item getUserItem(String itemId) {
		return userItemsLookup.get(itemId);
	}

	/**
	 * @param item
	 */
	public void setUserItem(Item item) {
		if (item != null && item.internalId != null) {
			userItemList.add(item);
			userItemsLookup.put(item.internalId, item);
		}
	}

	public List<Item> getUserItems() {
		return userItemList;
	}

	public long getUserItemsCount() {
		return userItemCount;
	}

	public void setUserItemsCount(long count) {
		userItemCount = count;
	}

	public void resetUserItem() {
		userItemList.clear();
		userItemsLookup.clear();
		userItemCount = -1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.view.client.AbstractDataProvider#onRangeChanged(com.google.gwt.view.client.HasData)
	 */
	@Override
	protected void onRangeChanged(HasData<Item> display) {
		Range r = display.getVisibleRange();

		// int start = r.getStart();
		// int end = start + r.getLength();

		pager = PagerHelper.createDefaultPager();
		pager.start = Long.valueOf(r.getStart());
		pager.count = Long.valueOf(r.getLength());
		// Range r = display.getVisibleRange();
		//
		// int start = r.getStart();
		// int end = start + r.getLength();
		//
		// if (rows == null || end > rows.size()) {
		fetchItems();
		// } else {
		// updateRowData(start, rows.subList(start, end));
		// }
	}

	public void reset() {
		pager = null;
		// count = -1;
		searchQuery = null;

		updateRowCount(0, false);
	}

}
