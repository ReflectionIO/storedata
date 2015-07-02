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
import io.reflection.app.client.controller.ItemController;
import io.reflection.app.client.controller.LinkedAccountController;
import io.reflection.app.client.controller.RankController;
import io.reflection.app.client.part.datatypes.MyApp;
import io.reflection.app.datatypes.shared.Item;
import io.reflection.app.datatypes.shared.Rank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author Stefano Capuzzi
 *
 */
public class UserItemProvider extends AsyncDataProvider<MyApp> implements GetLinkedAccountItemsEventHandler, GetSalesRanksEventHandler {

	private List<MyApp> myAppList = new ArrayList<MyApp>();
	private Map<String, MyApp> myAppsLookup = new HashMap<String, MyApp>();

	public List<MyApp> getList() {
		return myAppList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.view.client.AbstractDataProvider#onRangeChanged(com.google.gwt.view.client.HasData)
	 */
	@Override
	protected void onRangeChanged(HasData<MyApp> display) {
		if (LinkedAccountController.get().linkedAccountsFetched()) {
			if (myAppList.isEmpty()) {
				ItemController.get().fetchLinkedAccountItems();
			} else {
				int end = (display.getVisibleRange().getLength() > myAppList.size() ? myAppList.size() : display.getVisibleRange().getLength());
				updateRowData(0, myAppList.subList(0, end));
			}
		} else {
			LinkedAccountController.get().fetchLinkedAccounts(); // After refresh or the user didn't visit the linked accounts page
		}
	}

	public void reset() {
		ItemController.get().resetUserItem();
		myAppList.clear();
		myAppsLookup.clear();
		updateRowData(0, myAppList);
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

			if (output.items != null) { // There are items associated with this linked account

				MyApp myApp;
				for (Item item : output.items) {
					myAppList.add(myApp = new MyApp());
					myApp.item = item;
					myAppsLookup.put(item.internalId, myApp);
				}

				if (ItemController.get().getUserItemsCount() > 0) {
					RankController.get().fetchSalesRanks();
				}

			}
			sortByAppDetails(true);
			updateRowData(
					input.pager.start.intValue(),
					myAppList.subList(input.pager.start.intValue(),
							Math.min(input.pager.start.intValue() + input.pager.count.intValue(), (int) ItemController.get().getUserItemsCount())));
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

				myApp = myAppsLookup.get(rank.itemId);
				if (myApp != null) {

					if (myApp.ranks == null) {
						myApp.ranks = new ArrayList<Rank>();
					}

					myApp.ranks.add(rank);

				}
			}

			for (MyApp myUserItem : myAppList) {
				myUserItem.updateOverallValues(); // Calculate values given the new added Ranks
			}

		} else { // No Ranks available
			for (MyApp myApp : myAppList) {
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

		updateRowData(0, myAppList);
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

	/**
	 * @param sortAscending
	 */
	public void sortByRank(final boolean sortAscending) {
		Collections.sort(myAppList, new Comparator<MyApp>() {

			@Override
			public int compare(MyApp o1, MyApp o2) {
				int res = 0;
				if (!o1.overallPosition.equals(o2.overallPosition)) {
					if (o1.overallPosition.equals("-")) {
						res = 1;
					} else if (o2.overallPosition.equals("-")) {
						res = -1;
					} else {
						res = (Float.parseFloat(o1.overallPosition) < Float.parseFloat(o2.overallPosition) ? 1 : -1);
					}
				}
				return (sortAscending ? res : -res);
			}
		});
	}

	/**
	 * @param sortAscending
	 */
	public void sortByAppDetails(final boolean sortAscending) {
		Collections.sort(myAppList, new Comparator<MyApp>() {

			@Override
			public int compare(MyApp o1, MyApp o2) {
				return (sortAscending ? o1.item.name.compareTo(o2.item.name) : o2.item.name.compareTo(o1.item.name));
			}
		});
	}

	/**
	 * @param sortAscending
	 */
	public void sortByPrice(final boolean sortAscending) {
		Collections.sort(myAppList, new Comparator<MyApp>() {

			@Override
			public int compare(MyApp o1, MyApp o2) {
				int res = 0;
				if (!o1.overallPrice.equals(o2.overallPrice)) {
					if (o1.overallPrice.equals("-")) {
						res = 1;
					} else if (o2.overallPrice.equals("-")) {
						res = -1;
					} else if (o1.overallPrice.equalsIgnoreCase("free")) {
						res = 1;
					} else if (o2.overallPrice.equalsIgnoreCase("free")) {
						res = -1;
					} else {
						res = (Float.parseFloat(o1.overallPrice.replaceAll(",|\\.|$|€|¥|£", "").trim()) < Float.parseFloat(o2.overallPrice.replaceAll(
								",|\\.|$|€|¥|£", "").trim()) ? 1 : -1);
					}
				}
				return (sortAscending ? res : -res);
			}
		});

	}

	/**
	 * @param sortAscending
	 */
	public void sortByDownloads(final boolean sortAscending) {
		Collections.sort(myAppList, new Comparator<MyApp>() {

			@Override
			public int compare(MyApp o1, MyApp o2) {
				int res = 0;
				if (!o1.overallDownloads.equals(o2.overallDownloads)) {
					if (o1.overallDownloads.equals("-")) {
						res = 1;
					} else if (o2.overallDownloads.equals("-")) {
						res = -1;
					} else {
						res = (Integer.parseInt(o1.overallDownloads.replaceAll(",", "")) < Integer.parseInt(o2.overallDownloads.replaceAll(",", "")) ? 1 : -1);
					}
				}
				return (sortAscending ? res : -res);
			}
		});
	}

	/**
	 * @param sortAscending
	 */
	public void sortByRevenue(final boolean sortAscending) {
		Collections.sort(myAppList, new Comparator<MyApp>() {

			@Override
			public int compare(MyApp o1, MyApp o2) {
				int res = 0;
				if (!o1.overallRevenue.equals(o2.overallRevenue)) {
					if (o1.overallRevenue.equals("-")) {
						res = 1;
					} else if (o2.overallRevenue.equals("-")) {
						res = -1;
					} else {
						res = (Float.parseFloat(o1.overallRevenue.replaceAll(",|\\.|$|€|¥|£", "").trim()) < Float.parseFloat(o2.overallRevenue.replaceAll(
								",|\\.|$|€|¥|£", "").trim()) ? 1 : -1);
					}
				}
				return (sortAscending ? res : -res);
			}
		});
	}

}
