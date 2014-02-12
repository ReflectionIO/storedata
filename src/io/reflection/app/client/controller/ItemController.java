//
//  ItemController.java
//  storedata
//
//  Created by William Shakour (billy1380) on 28 Jan 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.controller;

import io.reflection.app.api.admin.shared.call.event.SearchForItemEventHandler.SearchForItemFailure;
import io.reflection.app.api.admin.shared.call.event.SearchForItemEventHandler.SearchForItemSuccess;
import io.reflection.app.api.core.client.CoreService;
import io.reflection.app.api.core.shared.call.SearchForItemRequest;
import io.reflection.app.api.core.shared.call.SearchForItemResponse;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.datatypes.shared.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author billy1380
 * 
 */
public class ItemController implements ServiceController {

	private static ItemController mOne = null;

	private Map<String, Item> mItemCache = new HashMap<String, Item>(); // Cache items meanwhile user is logged in

	private Map<String, List<Item>> mItemsSearchCache = new HashMap<String, List<Item>>(); // Cache of user searches

//	private Pager mLookupPager; // Lookup server calls pager
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
		CoreService service = new CoreService();
		service.setUrl(CORE_END_POINT);

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

					// Add retrieved items into item cache
					if (output.items != null) {
						for (Item item : output.items) {
							mItemCache.put(item.externalId, item);
						}
						// Add retrieved items into search items cache ( Map<"query", List<Item>> )
						mItemsSearchCache.put(input.query, output.items == null ? new ArrayList<Item>() : output.items);
					}
				}

				EventController.get().fireEventFromSource(new SearchForItemSuccess(input, output), ItemController.this);
			}

			@Override
			public void onFailure(Throwable caught) {
				EventController.get().fireEventFromSource(new SearchForItemFailure(input, caught), ItemController.this);
			}
		});
	}

//	/**
//	 * @return
//	 */
//	private Pager lookupPager() {
//		if (mLookupPager == null) {
//			mLookupPager = new Pager();
//			mLookupPager.start = Long.valueOf(0);
//			mLookupPager.count = Long.valueOf(1);
//			mLookupPager.sortBy = "externalid";
//			mLookupPager.sortDirection = SortDirectionType.SortDirectionTypeAscending;
//		}
//
//		return mLookupPager;
//	}
//
//	/**
//	 * Retrieve the item, identified by external id, from DB (it happens in case of cache miss)
//	 * 
//	 * @param externalId
//	 */
//	private void fetchItem(String externalId) {
//		CoreService service = new CoreService();
//		service.setUrl(CORE_END_POINT);
//
//		final SearchForItemRequest input = new SearchForItemRequest();
//		input.accessCode = ACCESS_CODE;
//
//		input.session = SessionController.get().getSessionForApiCall();
//		input.query = externalId;
//
//		input.pager = lookupPager();
//
//		service.searchForItem(input, new AsyncCallback<SearchForItemResponse>() {
//
//			@Override
//			public void onSuccess(SearchForItemResponse output) {
//				if (output != null && output.status == StatusType.StatusTypeSuccess) {
//
//					if (output.items != null) {
//						for (Item item : output.items) {
//							mItemCache.put(item.externalId, item); // Add item to cache
//						}
//					}
//				}
//
//				EventController.get().fireEventFromSource(new SearchForItemSuccess(input, output), ItemController.this);
//			}
//
//			@Override
//			public void onFailure(Throwable caught) {
//				EventController.get().fireEventFromSource(new SearchForItemFailure(input, caught), ItemController.this);
//			}
//		});
//	}

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
		for (Item item : items) {
			mItemCache.put(item.externalId, item);
		}
	}

	/**
	 * Retrieve an item, looking first in the cache or in the DB in case of miss
	 * 
	 * @param externalId
	 *            id of the item to retrieve
	 * @return the item
	 */
	public Item lookupItem(String externalId) {
		Item item = mItemCache.get(externalId);

		// if (item == null) {
		// fetchItem(externalId);
		// }

		return item;
	}

	/**
	 * @param item
	 */
	public void addItemToCache(Item item) {
		if (item != null) {
			mItemCache.put(item.externalId, item);
		}

	}

}
