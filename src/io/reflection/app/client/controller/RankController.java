//
//  RankController.java
//  storedata
//
//  Created by William Shakour (billy1380) on 3 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.controller;

import io.reflection.app.api.core.client.CoreService;
import io.reflection.app.api.core.shared.call.GetAllTopItemsRequest;
import io.reflection.app.api.core.shared.call.GetAllTopItemsResponse;
import io.reflection.app.api.core.shared.call.GetItemRanksRequest;
import io.reflection.app.api.core.shared.call.GetItemRanksResponse;
import io.reflection.app.api.core.shared.call.event.GetItemRanksEventHandler;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.api.shared.datatypes.SortDirectionType;
import io.reflection.app.client.handler.RanksEventHandler.FetchingRanks;
import io.reflection.app.client.handler.RanksEventHandler.ReceivedRanks;
import io.reflection.app.client.part.datatypes.RanksGroup;
import io.reflection.app.datatypes.shared.Item;

import java.util.ArrayList;
import java.util.List;

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
public class RankController extends AsyncDataProvider<RanksGroup> implements ServiceController {

	private static RankController mOne = null;

	private List<RanksGroup> mRows = null;

	private Pager mPager;

	public static RankController get() {
		if (mOne == null) {
			mOne = new RankController();
		}

		return mOne;
	}

	public void fetchTopItems() {
		CoreService service = new CoreService();
		service.setUrl(CORE_END_POINT);

		final GetAllTopItemsRequest input = new GetAllTopItemsRequest(); // JSON Item request, containing the fields used to query the Item table on the DB
		input.accessCode = ACCESS_CODE;

		input.session = SessionController.get().getSessionForApiCall(); // Get JSON session, create it if not exists and set cookie

		input.country = FilterController.get().getCountry(); // Get country from filter

		input.listType = FilterController.get().getListTypes().get(0); // Get item type (paid, free, grossing)

		input.on = FilterController.get().getStartDate(); // Get start date from filter

		input.category = FilterController.get().getCategory();

		if (mPager == null) {
			mPager = new Pager();
			mPager.count = STEP;
			mPager.start = Long.valueOf(0);
		}
		input.pager = mPager; // Set pager used to retrieve and format the wished items (start, number of elements, sorting order)

		input.store = FilterController.get().getStore(); // Get store (iPhone, iPad ...)

		// Call to retrieve top items from DB. The response contains List<Rank> for the 3 rank types (free, paid, grossing) , a List<Item> and a Pager
		service.getAllTopItems(input, new AsyncCallback<GetAllTopItemsResponse>() {

			@Override
			public void onSuccess(GetAllTopItemsResponse output) {
				
				if (output.status == StatusType.StatusTypeSuccess) {
					if (output.pager != null) {
						mPager = output.pager;// Set pager as the one received from the server
					}

					// Caching retrieved items

					if (output.items != null) {
						ItemController.get().addItemsToCache(output.items); // caching items
					}
					// Fires the ReceivedRanks event to the handlers listening to the event's type. Received ranks assign list type and List<Rank>
					EventController.get().fireEventFromSource(new ReceivedRanks("free" + input.listType, output.freeRanks), RankController.this);
					EventController.get().fireEventFromSource(new ReceivedRanks("paid" + input.listType, output.paidRanks), RankController.this);
					EventController.get().fireEventFromSource(new ReceivedRanks("grossing" + input.listType, output.grossingRanks), RankController.this);

					// Add list of retrieved items
					if (mRows == null) {
						mRows = new ArrayList<RanksGroup>();
					}

					int count = output.freeRanks.size(); // Number of item rows in the rank
					RanksGroup r;
					for (int i = 0; i < count; i++) {
						mRows.add(r = new RanksGroup());
						r.free = output.freeRanks.get(i);
						r.paid = output.paidRanks.get(i);
						r.grossing = output.grossingRanks.get(i);
					}

					updateRowCount(mPager.totalCount.intValue(), true); // Inform the displays of the total number of items that are available. @params the new
					// total row count, true if the count is exact, false if it is an estimate
					updateRowData(0, mRows); // Inform the displays of the new data. @params Start index, data values
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Error");
			}
		});

		EventController.get().fireEventFromSource(new FetchingRanks(), RankController.this);
	}

	public void fetchItemRanks(Item item) {
		CoreService service = new CoreService();
		service.setUrl(CORE_END_POINT);

		final GetItemRanksRequest input = new GetItemRanksRequest();
		input.accessCode = ACCESS_CODE;

		input.session = SessionController.get().getSessionForApiCall();
		input.country = FilterController.get().getCountry();

		input.start = FilterController.get().getStartDate();
		input.end = FilterController.get().getEndDate();

		input.category = FilterController.get().getCategory();

		input.item = new Item();
		input.item.externalId = item.externalId;
		input.item.source = item.source;
		input.listType = FilterController.get().getListTypes().get(0);

		input.pager = new Pager();
		input.pager.start = Long.valueOf(0);
		input.pager.count = Long.valueOf(10000);
		input.pager.sortDirection = SortDirectionType.SortDirectionTypeAscending;
		input.pager.sortBy = "date";

		service.getItemRanks(input, new AsyncCallback<GetItemRanksResponse>() {

			@Override
			public void onSuccess(GetItemRanksResponse output) {
				if (output != null && output.status == StatusType.StatusTypeSuccess && output.item != null) {
					ItemController.get().addItemToCache(output.item);
				}

				EventController.get().fireEventFromSource(new GetItemRanksEventHandler.GetItemRanksSuccess(input, output), RankController.this);
			}

			@Override
			public void onFailure(Throwable caught) {
				EventController.get().fireEventFromSource(new GetItemRanksEventHandler.GetItemRanksFailure(input, caught), RankController.this);
			}
		});

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.view.client.AbstractDataProvider#onRangeChanged(com.google.gwt.view.client.HasData)
	 */
	@Override
	protected void onRangeChanged(HasData<RanksGroup> display) {

		Range r = display.getVisibleRange();

		int start = r.getStart();
		int end = start + r.getLength();

		if (mRows == null || end > mRows.size()) {
			fetchTopItems();
		} else {
			updateRowData(start, mRows.subList(start, end));
		}
	}

	public void reset() {

		mPager = null;
		mRows = null;

		updateRowData(0, new ArrayList<RanksGroup>());
		updateRowCount(0, false);
		
		fetchTopItems();
	}

}
