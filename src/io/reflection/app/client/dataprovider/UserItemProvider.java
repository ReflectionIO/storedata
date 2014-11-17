//
//  MyAppProvider.java
//  storedata
//
//  Created by Stefano Capuzzi on 15 Nov 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.dataprovider;

import io.reflection.app.api.core.shared.call.GetLinkedAccountItemsRequest;
import io.reflection.app.api.core.shared.call.GetLinkedAccountItemsResponse;
import io.reflection.app.api.core.shared.call.GetSalesRanksRequest;
import io.reflection.app.api.core.shared.call.GetSalesRanksResponse;
import io.reflection.app.api.core.shared.call.event.GetLinkedAccountItemsEventHandler;
import io.reflection.app.api.core.shared.call.event.GetSalesRanksEventHandler;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.client.controller.ItemController;
import io.reflection.app.client.controller.LinkedAccountController;
import io.reflection.app.client.controller.RankController;
import io.reflection.app.client.part.datatypes.MyApp;
import io.reflection.app.datatypes.shared.Item;
import io.reflection.app.datatypes.shared.Rank;

import java.util.ArrayList;

import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author Stefano Capuzzi
 *
 */
public class UserItemProvider extends AsyncDataProvider<MyApp> implements GetLinkedAccountItemsEventHandler, GetSalesRanksEventHandler {

	private Pager pager = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.view.client.AbstractDataProvider#onRangeChanged(com.google.gwt.view.client.HasData)
	 */
	@Override
	protected void onRangeChanged(HasData<MyApp> display) {
		if (LinkedAccountController.get().linkedAccountsFetched()) {

			Range r = display.getVisibleRange();
			int start = r.getStart();
			int end = start + r.getLength();
			if (end > ItemController.get().getUsetItems().size()) {
				// MyApps page always uses filterParamChanged to call fetchLinkedAccountItems because of the LinkedAccounts TextBox
			} else {
				updateRowData(start, ItemController.get().getUsetItems().subList(start, end)); // Paging with all data already retrieved
			}
		} else {
			LinkedAccountController.get().fetchLinkedAccounts(); // After refresh or the user didn't visit the linked accounts page
		}
	}

	public void reset() {
		ItemController.get().resetUserItem();
		pager = null;
		updateRowData(0, ItemController.get().getUsetItems());
		updateRowCount(0, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.core.shared.call.event.GetLinkedAccountItemsEventHandler#getLinkedAccountItemsSuccess(io.reflection.app.api.core.shared.call.
	 * GetLinkedAccountItemsRequest, io.reflection.app.api.core.shared.call.GetLinkedAccountItemsResponse)
	 */
	@Override
	public void getLinkedAccountItemsSuccess(GetLinkedAccountItemsRequest input, GetLinkedAccountItemsResponse output) {
		if (output.status == StatusType.StatusTypeSuccess) {

			if (output.pager != null) {
				pager = output.pager;

				if (pager.totalCount != null) {
					ItemController.get().setUserItemsCount(pager.totalCount.longValue());
				}
			}

			if (output.items != null) { // There are items associated with this linked account

				ItemController.get().addItemsToCache(output.items);

				for (Item item : output.items) {
					ItemController.get().setUserItem(item);
				}

				if (ItemController.get().getUserItemsCount() > 0) {
					RankController.get().fetchSalesRanks();
				}
			}

			updateRowData(
					input.pager.start.intValue(),
					ItemController
							.get()
							.getUsetItems()
							.subList(input.pager.start.intValue(),
									Math.min(input.pager.start.intValue() + input.pager.count.intValue(), pager.totalCount.intValue())));
		}
		updateRowCount((int) ItemController.get().getUserItemsCount(), true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.core.shared.call.event.GetLinkedAccountItemsEventHandler#getLinkedAccountItemsFailure(io.reflection.app.api.core.shared.call.
	 * GetLinkedAccountItemsRequest, java.lang.Throwable)
	 */
	@Override
	public void getLinkedAccountItemsFailure(GetLinkedAccountItemsRequest input, Throwable caught) {
		updateRowCount(0, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.reflection.app.api.core.shared.call.event.GetSalesRanksEventHandler#getSalesRanksSuccess(io.reflection.app.api.core.shared.call.GetSalesRanksRequest,
	 * io.reflection.app.api.core.shared.call.GetSalesRanksResponse)
	 */
	@Override
	public void getSalesRanksSuccess(GetSalesRanksRequest input, GetSalesRanksResponse output) {
		if (output != null && output.status == StatusType.StatusTypeSuccess && output.ranks != null) { // Ranks available

			MyApp myApp;
			// Add Rank to related Item
			for (Rank rank : output.ranks) {
				myApp = new MyApp();
				myApp.item = ItemController.get().getUserItem(rank.itemId);

				if (myApp != null) {

					if (myApp.ranks == null) {
						myApp.ranks = new ArrayList<Rank>();
					}

					myApp.ranks.add(rank);

				}
			}

			for (MyApp myItem : ItemController.get().getUsetItems()) {
				myItem.updateOverallValues(); // Calculate values given the new added Ranks
			}

		} else { // No Ranks available
			for (MyApp myApp : ItemController.get().getUsetItems()) {
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
		}

		updateRowData(0, ItemController.get().getUsetItems());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.reflection.app.api.core.shared.call.event.GetSalesRanksEventHandler#getSalesRanksFailure(io.reflection.app.api.core.shared.call.GetSalesRanksRequest,
	 * java.lang.Throwable)
	 */
	@Override
	public void getSalesRanksFailure(GetSalesRanksRequest input, Throwable caught) {}

}
