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
import io.reflection.app.api.core.shared.call.GetLinkedAccountsRequest;
import io.reflection.app.api.core.shared.call.GetLinkedAccountsResponse;
import io.reflection.app.api.core.shared.call.GetSalesRanksRequest;
import io.reflection.app.api.core.shared.call.GetSalesRanksResponse;
import io.reflection.app.api.core.shared.call.event.GetLinkedAccountItemsEventHandler.GetLinkedAccountItemsFailure;
import io.reflection.app.api.core.shared.call.event.GetLinkedAccountItemsEventHandler.GetLinkedAccountItemsSuccess;
import io.reflection.app.api.core.shared.call.event.GetLinkedAccountsEventHandler;
import io.reflection.app.api.core.shared.call.event.GetSalesRanksEventHandler;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.api.shared.datatypes.SortDirectionType;
import io.reflection.app.client.part.datatypes.MyApp;
import io.reflection.app.datatypes.shared.Item;
import io.reflection.app.datatypes.shared.Rank;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author stefanocapuzzi
 * 
 */
public class MyAppsController extends AsyncDataProvider<MyApp> implements ServiceConstants, GetLinkedAccountsEventHandler {

	private static MyAppsController mOne = null;

	private List<MyApp> rows = null;
	private Pager pager = null;
	private List<MyApp> userItems = null;
	private Map<String, MyApp> userItemsLookup = new HashMap<String, MyApp>();

	public static MyAppsController get() {
		if (mOne == null) {
			mOne = new MyAppsController();
		}

		return mOne;
	}

	public void showAllUserItems() {
		updateRowCount(0, false); // Clear table and force loader
		// TODO: this should also check the user type... if the user does not have a full listing permission... there is no point in attempting to get the
		// linked accounts
		if (LinkedAccountController.get().isLinkedAccountFetched()) { // FetchLinkedAccount already called
			if (LinkedAccountController.get().hasLinkedAccounts()) { // Linked accounts have been retrieved
				if (hasUserItems()) {
					updateRowData(0, rows);
				} else {
					fetchLinkedAccountItems();
				}
			} else {
				updateRowCount(0, true);
			}
		} else {
			LinkedAccountController.get().fetchLinkedAccounts();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.core.shared.call.event.GetLinkedAccountsEventHandler#getLinkedAccountsSuccess(io.reflection.app.api.core.shared.call.
	 * GetLinkedAccountsRequest, io.reflection.app.api.core.shared.call.GetLinkedAccountsResponse)
	 */
	@Override
	public void getLinkedAccountsSuccess(GetLinkedAccountsRequest input, GetLinkedAccountsResponse output) {
		if (output.status == StatusType.StatusTypeSuccess) {

			if (LinkedAccountController.get().hasLinkedAccounts()) {
				fetchLinkedAccountItems();
			} else { // No linked accounts associated with this user
				updateRowCount(0, true);
			}

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.core.shared.call.event.GetLinkedAccountsEventHandler#getLinkedAccountsFailure(io.reflection.app.api.core.shared.call.
	 * GetLinkedAccountsRequest, java.lang.Throwable)
	 */
	@Override
	public void getLinkedAccountsFailure(GetLinkedAccountsRequest input, Throwable caught) {
		// TODO Auto-generated method stub

	}

	private void fetchLinkedAccountItems() {
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

		if (rows == null) {
			rows = new ArrayList<MyApp>();
		}

		if (userItems == null) {
			userItems = new ArrayList<MyApp>();
		}

		input.linkedAccount = LinkedAccountController.get().getLinkedAccounts().get(0); // TODO loop on linked accounts

		service.getLinkedAccountItems(input, new AsyncCallback<GetLinkedAccountItemsResponse>() {

			@Override
			public void onSuccess(GetLinkedAccountItemsResponse output) {
				if (output.status == StatusType.StatusTypeSuccess) {

					if (output.items != null) { // There are items associated with this linked account

						MyApp myApp;

						for (Item item : output.items) {
							rows.add(myApp = new MyApp());
							myApp.item = item;

							userItemsLookup.put(item.externalId == null ? item.internalId : item.externalId, myApp);
							userItems.add(myApp);
						}

						if (userItemsLookup.size() > 0) {
							updateRowData(0, rows);
							fetchSalesRanks();
						}
					} else { // No items associated with this linked account
						updateRowCount(0, true);
					}
				}

				EventController.get().fireEventFromSource(new GetLinkedAccountItemsSuccess(input, output), MyAppsController.this);
			}

			@Override
			public void onFailure(Throwable caught) {
				EventController.get().fireEventFromSource(new GetLinkedAccountItemsFailure(input, caught), MyAppsController.this);
			}
		});
	}

	private void fetchSalesRanks() {
		CoreService service = ServiceCreator.createCoreService();

		final GetSalesRanksRequest input = new GetSalesRanksRequest();
		input.accessCode = ACCESS_CODE;
		input.session = SessionController.get().getSessionForApiCall();

		input.linkedAccount = LinkedAccountController.get().getLinkedAccounts().get(0); // TODO loop on linked accounts

		input.category = FilterController.get().getCategory();
		input.country = FilterController.get().getCountry();
		input.start = FilterController.get().getStartDate();
		input.end = FilterController.get().getEndDate();
		input.listType = StoreController.IPAD_A3_CODE.equals(FilterController.get().getFilter().getStoreA3Code()) ? "ipad" : "";

		service.getSalesRanks(input, new AsyncCallback<GetSalesRanksResponse>() {

			@Override
			public void onSuccess(GetSalesRanksResponse output) {
				if (output != null && output.status == StatusType.StatusTypeSuccess && output.ranks != null) {
					ItemController.get().addItemsToCache(output.items);

					List<MyApp> appList = new ArrayList<MyApp>();

					MyApp app;
					// ranks belong to various apps and each app can have multiple ranks depending on the time span (a rank per day)
					for (Rank rank : output.ranks) {
						app = userItemsLookup.get(rank.itemId);

						if (app != null) {
							if (app.ranks == null) {
								app.ranks = new ArrayList<Rank>();
							}

							app.ranks.add(rank);
							appList.add(app);
						}
					}

					for (MyApp myApp : appList) {
						myApp.updateOverallValues();
					}

					updateRowData(0, rows);
				} else {
					// leave the rows as they are
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
	public List<MyApp> getUserItems() {
		return userItems;
	}

	public boolean hasUserItems() {
		return !userItemsLookup.isEmpty();
	}

	public void reset() {
		rows = null;
		userItems = null;
		userItemsLookup.clear();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.view.client.AbstractDataProvider#onRangeChanged(com.google.gwt.view.client.HasData)
	 */
	@Override
	protected void onRangeChanged(HasData<MyApp> display) {

		/*
		 * Range r = display.getVisibleRange();
		 * 
		 * int start = r.getStart(); int end = start + r.getLength();
		 * 
		 * if (end > rows.size()) { getAllUserItems(); } else { updateRowData(start, rows.subList(start, end)); }
		 */
	}

}
