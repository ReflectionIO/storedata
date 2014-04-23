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
public class MyAppsController extends AsyncDataProvider<MyApp> implements ServiceConstants {

	private static MyAppsController mOne = null;

	private List<MyApp> rows = null;
	private Pager pager = new Pager();
	private Map<String, MyApp> myAppsLookup = new HashMap<String, MyApp>();

	public static MyAppsController get() {
		if (mOne == null) {
			mOne = new MyAppsController();
			mOne.pager.count = STEP;
			mOne.pager.start = Long.valueOf(0);
			mOne.pager.sortDirection = SortDirectionType.SortDirectionTypeDescending;
		}

		return mOne;
	}

	public void getAllUserItems() {
		if (rows == null) {
			rows = new ArrayList<MyApp>();
		}

		// TODO: this should also check the user type... if the user does not have a full listing permission... there is no point in attempting to get the
		// linked accounts
		if (LinkedAccountController.get().hasLinkedAccounts()) {
			fetchLinkedAccountItems();
		} else {
			fetchLinkedAccounts();
		}
	}

	private void fetchLinkedAccounts() {

		CoreService service = ServiceCreator.createCoreService();

		final GetLinkedAccountsRequest input = new GetLinkedAccountsRequest();
		input.accessCode = ACCESS_CODE;
		input.session = SessionController.get().getSessionForApiCall();
		input.pager = pager;

		service.getLinkedAccounts(input, new AsyncCallback<GetLinkedAccountsResponse>() {
			@Override
			public void onSuccess(GetLinkedAccountsResponse output) {
				if (output.status == StatusType.StatusTypeSuccess) {
					if (output.linkedAccounts != null) {
						LinkedAccountController.get().setLinkedAccounts(output.linkedAccounts);
						fetchLinkedAccountItems();
					} else { // No linked accounts associated with this user
						updateRowCount(0, true);
						// TODO Tell it to the user
					}

				}

				EventController.get().fireEventFromSource(new GetLinkedAccountsEventHandler.GetLinkedAccountsSuccess(input, output), MyAppsController.this);
			}

			@Override
			public void onFailure(Throwable caught) {
				EventController.get().fireEventFromSource(new GetLinkedAccountsEventHandler.GetLinkedAccountsFailure(input, caught), MyAppsController.this);
			}
		});
	}

	private void fetchLinkedAccountItems() {
		CoreService service = ServiceCreator.createCoreService();

		final GetLinkedAccountItemsRequest input = new GetLinkedAccountItemsRequest();
		input.accessCode = ACCESS_CODE;
		input.linkedAccount = LinkedAccountController.get().getLinkedAccounts().get(0); // TODO loop on linked accounts
		input.session = SessionController.get().getSessionForApiCall();
		input.pager = pager;

		service.getLinkedAccountItems(input, new AsyncCallback<GetLinkedAccountItemsResponse>() {

			@Override
			public void onSuccess(GetLinkedAccountItemsResponse output) {
				if (output.status == StatusType.StatusTypeSuccess) {
					if (output.items != null) { // There are items associated with this linked account
						MyApp myApp;

						for (Item item : output.items) {
							rows.add(myApp = new MyApp());
							myApp.item = item;

							myAppsLookup.put(item.externalId == null ? item.internalId : item.externalId, myApp);
						}

						if (myAppsLookup.size() > 0) {
							updateRowData(0, rows);
							fetchSalesRank();
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

	private void fetchSalesRank() {
		CoreService service = ServiceCreator.createCoreService();

		final GetSalesRanksRequest input = new GetSalesRanksRequest();
		input.accessCode = ACCESS_CODE;
		input.session = SessionController.get().getSessionForApiCall();

		input.category = FilterController.get().getCategory();
		input.country = FilterController.get().getCountry();
		input.start = FilterController.get().getStartDate();
		input.end = FilterController.get().getEndDate();

		service.getSalesRanks(input, new AsyncCallback<GetSalesRanksResponse>() {

			@Override
			public void onSuccess(GetSalesRanksResponse output) {
				if (output != null && output.status == StatusType.StatusTypeSuccess && output.ranks != null) {
					ItemController.get().addItemsToCache(output.items);

					// TODO: create some ranks based

					updateRowData(0, rows);
				} else {
					// updateRowCount(0, true);
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

	public void reset() {
		rows = null;
		getAllUserItems();
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
