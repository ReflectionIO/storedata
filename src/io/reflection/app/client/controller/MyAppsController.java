//
//  MyAppsController.java
//  storedata
//
//  Created by Stefano Capuzzi on 24 Mar 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.controller;

import io.reflection.app.api.core.client.CoreService;
import io.reflection.app.api.core.shared.call.GetLinkedAccountItemsRequest;
import io.reflection.app.api.core.shared.call.GetLinkedAccountItemsResponse;
import io.reflection.app.api.core.shared.call.GetSalesRanksRequest;
import io.reflection.app.api.core.shared.call.GetSalesRanksResponse;
import io.reflection.app.api.core.shared.call.event.GetLinkedAccountItemsEventHandler.GetLinkedAccountItemsFailure;
import io.reflection.app.api.core.shared.call.event.GetLinkedAccountItemsEventHandler.GetLinkedAccountItemsSuccess;
import io.reflection.app.api.core.shared.call.event.GetSalesRanksEventHandler;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.api.shared.datatypes.SortDirectionType;
import io.reflection.app.client.helper.ApiCallHelper;
import io.reflection.app.client.part.datatypes.MyApp;
import io.reflection.app.datatypes.shared.Item;
import io.reflection.app.datatypes.shared.Rank;
import io.reflection.app.datatypes.shared.Store;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author stefanocapuzzi
 * 
 */
public class MyAppsController extends AsyncDataProvider<MyApp> implements ServiceConstants {

	private static MyAppsController mOne = null;

	private List<MyApp> rows = new ArrayList<MyApp>();
	private Pager pager = null;
	private List<MyApp> userItems = null;
	private Map<String, MyApp> userItemsLookup = new HashMap<String, MyApp>();
	private long mCount = -1;

	public static MyAppsController get() {
		if (mOne == null) {
			mOne = new MyAppsController();
		}

		return mOne;
	}

	/**
	 * Fetch the list of Item related to the linked account currently selected in the filter
	 */
	public void fetchLinkedAccountItems() {

		updateRowCount(0, false);

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

		if (userItems == null) {
			userItems = new ArrayList<MyApp>();
		}

		input.linkedAccount = FilterController.get().getLinkedAccount();

		Store store = new Store();
		store.a3Code = FilterController.get().getFilter().getStoreA3Code();
		input.store = ApiCallHelper.createStoreForApiCall(FilterController.get().getStore()); // Get store (iPhone, iPad ...)

		input.listType = FilterController.get().getListTypes().get(0);

		service.getLinkedAccountItems(input, new AsyncCallback<GetLinkedAccountItemsResponse>() {

			@Override
			public void onSuccess(GetLinkedAccountItemsResponse output) {
				if (output.status == StatusType.StatusTypeSuccess) {

					if (output.pager != null) {
						pager = output.pager;

						if (pager.totalCount != null) {
							mCount = pager.totalCount.longValue();
						}
					}

					if (output.items != null) { // There are items associated with this linked account

						ItemController.get().addItemsToCache(output.items);

						MyApp myApp;

						for (Item item : output.items) {
							rows.add(myApp = new MyApp());
							myApp.item = item;

							userItemsLookup.put(item.internalId, myApp);
							userItems.add(myApp);
						}

						if (userItemsLookup.size() > 0) {
							fetchSalesRanks();
						}
					}

					updateRowCount((int) mCount, true);
					updateRowData(
							input.pager.start.intValue(),
							rows.subList(input.pager.start.intValue(),
									Math.min(input.pager.start.intValue() + input.pager.count.intValue(), pager.totalCount.intValue())));
				}

				EventController.get().fireEventFromSource(new GetLinkedAccountItemsSuccess(input, output), MyAppsController.this);
			}

			@Override
			public void onFailure(Throwable caught) {
				EventController.get().fireEventFromSource(new GetLinkedAccountItemsFailure(input, caught), MyAppsController.this);
			}
		});
	}

	/**
     * 
     */
	private void fetchSalesRanks() {
		CoreService service = ServiceCreator.createCoreService();

		final GetSalesRanksRequest input = new GetSalesRanksRequest();
		input.accessCode = ACCESS_CODE;
		input.session = SessionController.get().getSessionForApiCall();

		input.linkedAccount = FilterController.get().getLinkedAccount();

		input.category = FilterController.get().getCategory();
		input.country = FilterController.get().getCountry();
		input.start = FilterController.get().getStartDate();
		input.end = FilterController.get().getEndDate();
		input.listType = StoreController.IPAD_A3_CODE.equals(FilterController.get().getFilter().getStoreA3Code()) ? "ipad" : "";

		service.getSalesRanks(input, new AsyncCallback<GetSalesRanksResponse>() {

			@Override
			public void onSuccess(GetSalesRanksResponse output) {
				if (output != null && output.status == StatusType.StatusTypeSuccess && output.ranks != null) { // Ranks available

					// List<MyApp> myAppList = new ArrayList<MyApp>();

					MyApp myApp;
					// Add Rank to related Item
					for (Rank rank : output.ranks) {
						myApp = userItemsLookup.get(rank.itemId);

						if (myApp != null) {

							if (myApp.ranks == null) {
								myApp.ranks = new ArrayList<Rank>();
							}

							myApp.ranks.add(rank);

						}
					}

					for (MyApp myItem : userItems) {
						myItem.updateOverallValues(); // Calculate values given the new added Ranks
					}

					updateRowData(0, rows);
				} else { // No Ranks available
					for (MyApp myApp : rows) {
						if (myApp.overallDownloads == null) {
							myApp.overallDownloads = "-";
						}
						if (myApp.overallPosition == null) {
							myApp.overallPosition = "-";
						}
						if (myApp.overallPrice == null) {
							myApp.overallPrice = "-";
						}
						if (myApp.overallRevenue == null) {
							myApp.overallRevenue = "-";
						}
					}
					updateRowData(0, rows);
				}

				EventController.get().fireEventFromSource(new GetSalesRanksEventHandler.GetSalesRanksSuccess(input, output), MyAppsController.this);
			}

			@Override
			public void onFailure(Throwable caught) {
				EventController.get().fireEventFromSource(new GetSalesRanksEventHandler.GetSalesRanksFailure(input, caught), MyAppsController.this);
			}
		});
	}

	// private void fetchItemRanks(final MyApp myApp) {
	// CoreService service = ServiceCreator.createCoreService();
	// final GetItemRanksRequest input = new GetItemRanksRequest();
	// input.accessCode = ACCESS_CODE;
	// input.session = SessionController.get().getSessionForApiCall();
	// // input.pager = pager;
	// // input.category = FilterController.get().getCategory();
	//
	// input.country = ApiCallHelper.createCountryForApiCall(FilterController.get().getCountry());
	//
	// input.listType = FilterController.get().getListTypes().get(0);
	//
	// input.item = myApp.item;
	// input.start = FilterController.get().getStartDate();
	//
	// input.end = FilterController.get().getEndDate();
	//
	// service.getItemRanks(input, new AsyncCallback<GetItemRanksResponse>() {
	//
	// @Override
	// public void onFailure(Throwable caught) {
	// EventController.get().fireEventFromSource(new GetItemRanksEventHandler.GetItemRanksFailure(input, caught), MyAppsController.this);
	// }
	//
	// @Override
	// public void onSuccess(GetItemRanksResponse output) {
	//
	// if (output != null && output.status == StatusType.StatusTypeSuccess && output.item != null) {
	// ItemController.get().addItemToCache(output.item);
	// if (output.ranks != null) {
	// myApp.rank = output.ranks.get(0); // TODO loop on ranks
	// }
	// updateRowData(0, rows);
	// } else {
	// // updateRowCount(0, true);
	// }
	//
	// EventController.get().fireEventFromSource(new GetItemRanksEventHandler.GetItemRanksSuccess(input, output), MyAppsController.this);
	// }
	//
	// });
	//
	// }

	/**
	 * 
	 * @return
	 */
	public long getUserItemsCount() {
		return mCount;
	}

	/**
	 * 
	 * @return
	 */
	public List<MyApp> getUserItems() {
		return userItems;
	}

	public boolean hasUserItems() {
		return mCount != -1;
	}

	public void reset() {
		userItems = null;
		userItemsLookup.clear();

		pager = null;
		mCount = -1;
		rows.clear();

		updateRowData(0, rows);
		updateRowCount(0, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.view.client.AbstractDataProvider#onRangeChanged(com.google.gwt.view.client.HasData)
	 */
	@Override
	protected void onRangeChanged(HasData<MyApp> display) {
		if (LinkedAccountController.get().hasLinkedAccounts()) {

			Range r = display.getVisibleRange();
			int start = r.getStart();
			int end = start + r.getLength();
			if (end > rows.size()) {
				if (LinkedAccountController.get().getLinkedAccountsCount() > 0 && pager != null) {
					fetchLinkedAccountItems();
				} else {
					updateRowCount(0, true); // No Apps available
				}
			} else {
				updateRowData(start, rows.subList(start, end)); // Paging with all data already retrieved
			}
		} else {
			LinkedAccountController.get().fetchLinkedAccounts(); // After refresh or the user didn't visit the linked accounts page
		}
	}

	public Item getUserItem(String itemId) {
		MyApp app = userItemsLookup.get(itemId);
		return app == null ? null : app.item;
	}

	/**
	 * @param item
	 */
	public void setUserItem(Item item) {
		if (item != null && item.internalId != null) {
			MyApp app = new MyApp();
			app.item = item;

			userItemsLookup.put(item.internalId, app);
		}
	}

}
