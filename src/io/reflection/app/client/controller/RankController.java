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
import io.reflection.app.api.core.shared.call.event.GetAllTopItemsEventHandler.GetAllTopItemsFailure;
import io.reflection.app.api.core.shared.call.event.GetAllTopItemsEventHandler.GetAllTopItemsSuccess;
import io.reflection.app.api.core.shared.call.event.GetItemRanksEventHandler;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.api.shared.datatypes.SortDirectionType;
import io.reflection.app.client.helper.ApiCallHelper;
import io.reflection.app.client.part.datatypes.ItemRevenue;
import io.reflection.app.client.part.datatypes.RanksGroup;
import io.reflection.app.datatypes.shared.Item;
import io.reflection.app.datatypes.shared.Rank;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.Range;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author billy1380
 * 
 */
public class RankController extends AsyncDataProvider<RanksGroup> implements ServiceConstants {

	private static RankController mOne = null;

	private List<RanksGroup> mRows = new ArrayList<RanksGroup>();
	private Pager mPager;

	private ListDataProvider<ItemRevenue> itemRevenueData = new ListDataProvider<ItemRevenue>();

	public static RankController get() {
		if (mOne == null) {
			mOne = new RankController();
		}

		return mOne;
	}

	public void fetchTopItems() {
		CoreService service = ServiceCreator.createCoreService();

		final GetAllTopItemsRequest input = new GetAllTopItemsRequest(); // JSON Item request, containing the fields used to query the Item table on the DB
		input.accessCode = ACCESS_CODE;

		input.dailyData = FilterController.get().getFilter().getDailyData();

		input.session = SessionController.get().getSessionForApiCall(); // Get JSON session, create it if not exists and set cookie

		input.country = ApiCallHelper.createCountryForApiCall(FilterController.get().getCountry()); // Get country from filter

		input.listType = FilterController.get().getListTypes().get(0); // Get item type (paid, free, grossing)

		input.on = FilterController.get().getEndDate(); // Get start date from filter

		input.category = FilterController.get().getCategory();

		if (mPager == null) {
			mPager = new Pager();
			mPager.count = STEP;
			mPager.start = Long.valueOf(0);
			mPager.boundless = Boolean.TRUE;
		}

		input.pager = mPager; // Set pager used to retrieve and format the wished items (start, number of elements, sorting order)

		input.store = ApiCallHelper.createStoreForApiCall(FilterController.get().getStore()); // Get store (iPhone, iPad ...)

		// Call to retrieve top items from DB. The response contains List<Rank> for the 3 rank types (free, paid, grossing) , a List<Item> and a Pager
		service.getAllTopItems(input, new AsyncCallback<GetAllTopItemsResponse>() {

			@Override
			public void onSuccess(GetAllTopItemsResponse output) {

				if (output.status == StatusType.StatusTypeSuccess) {
					if (output.pager != null) {
						mPager = output.pager;// Set pager as the one received from the server
					}

					// Caching retrieved items
					ItemController.get().addItemsToCache(output.items); // caching items

					// // Fires the ReceivedRanks event to the handlers listening to the event's type. Received ranks assign list type and List<Rank>
					// EventController.get().fireEventFromSource(new ReceivedRanks("free" + input.listType, output.freeRanks), RankController.this);
					// EventController.get().fireEventFromSource(new ReceivedRanks("paid" + input.listType, output.paidRanks), RankController.this);
					// EventController.get().fireEventFromSource(new ReceivedRanks("grossing" + input.listType, output.grossingRanks), RankController.this);

					int count = 0;

					if (output.freeRanks != null) {
						count = output.freeRanks.size(); // Number of item rows in the rank
					}

					RanksGroup r;
					for (int i = 0; i < count; i++) {
						mRows.add(r = new RanksGroup());
						r.free = output.freeRanks.get(i);
						r.paid = output.paidRanks.get(i);
						r.grossing = output.grossingRanks.get(i);
					}

					// updateRowCount(mPager.totalCount.intValue(), true); // Inform the displays of the total number of items that are available. @params the
					// new
					// total row count, true if the count is exact, false if it is an estimate
					updateRowData(0, mRows); // Inform the displays of the new data. @params Start index, data values
				}

				EventController.get().fireEventFromSource(new GetAllTopItemsSuccess(input, output), RankController.this);
			}

			@Override
			public void onFailure(Throwable caught) {
				EventController.get().fireEventFromSource(new GetAllTopItemsFailure(input, caught), RankController.this);
			}
		});

		// EventController.get().fireEventFromSource(new FetchingRanks(), RankController.this);
	}

	public void fetchItemRanks(final Item item) {
		CoreService service = ServiceCreator.createCoreService();

		final GetItemRanksRequest input = new GetItemRanksRequest();
		input.accessCode = ACCESS_CODE;

		input.dailyData = FilterController.get().getFilter().getDailyData();

		input.session = SessionController.get().getSessionForApiCall();
		input.country = ApiCallHelper.createCountryForApiCall(FilterController.get().getCountry());

		input.start = FilterController.get().getStartDate();
		input.end = FilterController.get().getEndDate();

		input.category = FilterController.get().getCategory();

		input.item = new Item();
		input.item.internalId = item.internalId;
		input.item.source = ApiCallHelper.storeCodeForApiCall(item.source);
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

					float paid = 0, iap = 0;
					ItemRevenue itemRevenue = null;

					if (itemRevenueData.getList().size() == 0) {
						itemRevenue = new ItemRevenue();
						itemRevenueData.getList().add(itemRevenue);
					} else {
						itemRevenue = itemRevenueData.getList().get(0);
					}

					float rankPaid = 0;

					if (output.ranks != null) {
						for (Rank rank : output.ranks) {
							if (rank.downloads != null && rank.revenue != null) {
								paid += (rankPaid = (float) rank.downloads.intValue() * rank.price.floatValue());
								iap += (rank.revenue.floatValue() - rankPaid);
							}
						}
					}

					itemRevenue.countryFlagStyleName = CountryController.get().getCountryFlag(input.country.a2Code);
					itemRevenue.countryName = CountryController.get().getCountry(input.country.a2Code).name;

					if (output.ranks != null) {
						itemRevenue.currency = output.ranks.get(0).currency;
					} else if (output.item.currency != null) {
						itemRevenue.currency = output.item.currency;
					} else if (item.currency != null) {
						itemRevenue.currency = item.currency;
					}

					itemRevenue.iap = Float.valueOf(iap);
					itemRevenue.paid = Float.valueOf(paid);
					itemRevenue.percentage = Float.valueOf(100.0f);
					itemRevenue.total = Float.valueOf(iap + paid);

					itemRevenueData.refresh();
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
		mRows.clear();

		updateRowData(0, mRows);
		updateRowCount(0, false);

		fetchTopItems();
	}

	/**
	 * @return
	 */
	public ListDataProvider<ItemRevenue> getItemRevenueDataProvider() {
		return itemRevenueData;
	}

}
