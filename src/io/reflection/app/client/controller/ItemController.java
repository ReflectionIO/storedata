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
import io.reflection.app.api.core.shared.call.GetAllTopItemsRequest;
import io.reflection.app.api.core.shared.call.GetAllTopItemsResponse;
import io.reflection.app.api.core.shared.call.SearchForItemRequest;
import io.reflection.app.api.core.shared.call.SearchForItemResponse;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.api.shared.datatypes.SortDirectionType;
import io.reflection.app.client.handler.FilterEventHandler;
import io.reflection.app.client.handler.RanksEventHandler.FetchingRanks;
import io.reflection.app.client.handler.RanksEventHandler.ReceivedRanks;
import io.reflection.app.datatypes.shared.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author billy1380
 * 
 */
public class ItemController extends AsyncDataProvider<Item> implements ServiceController, FilterEventHandler {

	private static ItemController mOne = null;

	private Map<String, Item> mItemLookup = new HashMap<String, Item>();
	private Map<String, List<Item>> mSearchLookup = new HashMap<String, List<Item>>();

	private List<Item> mRows = null;

	private Pager mPager;
	private Pager mLookupPager;
	private Pager mSearchPager;

	public static ItemController get() {
		if (mOne == null) {
			mOne = new ItemController();
		}

		return mOne;
	}

	private ItemController() {
		EventController.get().addHandlerToSource(FilterEventHandler.TYPE, FilterController.get(), this);
	}

	private void fetchItem(String externalId) {
		CoreService service = new CoreService();
		service.setUrl(CORE_END_POINT);

		final SearchForItemRequest input = new SearchForItemRequest();
		input.accessCode = ACCESS_CODE;

		input.session = SessionController.get().getSessionForApiCall();
		input.query = externalId;

		input.pager = lookupPager();

		service.searchForItem(input, new AsyncCallback<SearchForItemResponse>() {

			@Override
			public void onSuccess(SearchForItemResponse output) {
				if (output != null && output.status == StatusType.StatusTypeSuccess) {

					if (output.items != null) {
						for (Item item : output.items) {
							mItemLookup.put(item.externalId, item);
						}
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

					if (output.items != null) {
						for (Item item : output.items) {
							mItemLookup.put(item.externalId, item);
						}

						mSearchLookup.put(input.query, output.items == null ? new ArrayList<Item>() : output.items);
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

	/**
	 * @return
	 */
	private Pager lookupPager() {
		if (mLookupPager == null) {
			mLookupPager = new Pager();
			mLookupPager.start = Long.valueOf(0);
			mLookupPager.count = Long.valueOf(1);
			mLookupPager.sortBy = "externalid";
			mLookupPager.sortDirection = SortDirectionType.SortDirectionTypeAscending;
		}

		return mLookupPager;
	}

	public void fetchTopItems() {
		CoreService service = new CoreService();
		service.setUrl(CORE_END_POINT);

		final GetAllTopItemsRequest input = new GetAllTopItemsRequest();
		input.accessCode = ACCESS_CODE;

		input.session = SessionController.get().getSessionForApiCall();

		input.country = FilterController.get().getCountry();

		input.listType = FilterController.get().getListTypes().get(0);
		input.on = FilterController.get().getStartDate();

		if (mPager == null) {
			mPager = new Pager();
			mPager.count = STEP;
			mPager.start = Long.valueOf(0);
		}
		input.pager = mPager;

		input.store = FilterController.get().getStore();

		service.getAllTopItems(input, new AsyncCallback<GetAllTopItemsResponse>() {

			@Override
			public void onSuccess(GetAllTopItemsResponse output) {
				if (output.status == StatusType.StatusTypeSuccess) {
					if (output.pager != null) {
						mPager = output.pager;
					}

					if (output.items != null) {
						for (Item item : output.items) {
							mItemLookup.put(item.externalId, item);
						}
					}

					EventController.get().fireEventFromSource(new ReceivedRanks("free" + input.listType, output.freeRanks), ItemController.this);
					EventController.get().fireEventFromSource(new ReceivedRanks("paid" + input.listType, output.paidRanks), ItemController.this);
					EventController.get().fireEventFromSource(new ReceivedRanks("grossing" + input.listType, output.grossingRanks), ItemController.this);

					if (mRows == null) {
						mRows = new ArrayList<Item>();
					}

					int count = output.freeRanks.size();

					for (int i = 0; i < count; i++) {
						mRows.add(output.items.get(i));
					}

					updateRowCount(mPager.totalCount.intValue(), true);
					updateRowData(0, mRows);
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Error");
			}
		});

		EventController.get().fireEventFromSource(new FetchingRanks(), ItemController.this);
	}

	public Item lookupItem(String externalId) {
		Item item = mItemLookup.get(externalId);

		if (item == null) {
			fetchItem(externalId);
		}

		return item;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.view.client.AbstractDataProvider#onRangeChanged(com.google.gwt.view.client.HasData)
	 */
	@Override
	protected void onRangeChanged(HasData<Item> display) {

		Range r = display.getVisibleRange();

		int start = r.getStart();
		int end = start + r.getLength();

		if (mRows == null || end > mRows.size()) {
			fetchTopItems();
		} else {
			updateRowData(start, mRows.subList(start, end));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.FilterEventHandler#filterParamChanged(java.lang.String, java.lang.Object, java.lang.Object)
	 */
	@Override
	public <T> void filterParamChanged(String name, T currentValue, T previousValue) {
		mPager = null;
		mRows = null;

		updateRowData(0, new ArrayList<Item>());
		updateRowCount(0, false);

		fetchTopItems();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.FilterEventHandler#filterParamsChanged(java.util.Map, java.util.Map)
	 */
	@Override
	public void filterParamsChanged(Map<String, ?> currentValues, Map<String, ?> previousValues) {
		reset();
	}

	public void reset() {
		mItemLookup.clear();

		mPager = null;
		mRows = null;

		updateRowData(0, new ArrayList<Item>());
		updateRowCount(0, false);

		fetchTopItems();
	}

	/**
	 * @param items
	 */
	public void addItemsToLookup(List<Item> items) {
		for (Item item : items) {
			mItemLookup.put(item.externalId, item);
		}
	}

	/**
	 * @param query
	 * @return
	 */
	public List<Item> searchForItems(String query) {
		List<Item> searchResults = mSearchLookup.get(query);

		if (searchResults == null) {
			fetchItemsQuery(query);
		}

		return searchResults;
	}
}
