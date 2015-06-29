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
import io.reflection.app.api.core.shared.call.GetItemSalesRanksRequest;
import io.reflection.app.api.core.shared.call.GetItemSalesRanksResponse;
import io.reflection.app.api.core.shared.call.GetSalesRanksRequest;
import io.reflection.app.api.core.shared.call.GetSalesRanksResponse;
import io.reflection.app.api.core.shared.call.event.GetAllTopItemsEventHandler.GetAllTopItemsFailure;
import io.reflection.app.api.core.shared.call.event.GetAllTopItemsEventHandler.GetAllTopItemsSuccess;
import io.reflection.app.api.core.shared.call.event.GetItemRanksEventHandler;
import io.reflection.app.api.core.shared.call.event.GetItemSalesRanksEventHandler.GetItemSalesRanksFailure;
import io.reflection.app.api.core.shared.call.event.GetItemSalesRanksEventHandler.GetItemSalesRanksSuccess;
import io.reflection.app.api.core.shared.call.event.GetSalesRanksEventHandler;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.api.shared.datatypes.SortDirectionType;
import io.reflection.app.client.DefaultEventBus;
import io.reflection.app.client.helper.ApiCallHelper;
import io.reflection.app.client.helper.FilterHelper;
import io.reflection.app.client.part.datatypes.AppRevenue;
import io.reflection.app.client.part.datatypes.ItemRevenue;
import io.reflection.app.client.part.datatypes.RanksGroup;
import io.reflection.app.datatypes.shared.Item;
import io.reflection.app.datatypes.shared.Rank;
import io.reflection.app.shared.util.DataTypeHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.http.client.Request;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.datepicker.client.CalendarUtil;
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

	private List<RanksGroup> rows = new ArrayList<RanksGroup>();
	private Pager pager;
	private Request currentTopItems;
	private Request currentItemRanks;
	private Request currentItemSalesRanks;

	private ListDataProvider<ItemRevenue> itemRevenueData = new ListDataProvider<ItemRevenue>();
	private ListDataProvider<AppRevenue> appRevenueDataProvider = new ListDataProvider<AppRevenue>();

	public static RankController get() {
		if (mOne == null) {
			mOne = new RankController();
		}

		return mOne;
	}

	public void fetchTopItems() {
		if (currentTopItems != null) {
			currentTopItems.cancel();
			currentTopItems = null;
		}

		CoreService service = ServiceCreator.createCoreService();

		final GetAllTopItemsRequest input = new GetAllTopItemsRequest(); // JSON Item request, containing the fields used to query the Item table on the DB
		input.accessCode = ACCESS_CODE;

		input.dailyData = FilterController.get().getFilter().getDailyData();

		input.session = SessionController.get().getSessionForApiCall(); // Get JSON session, create it if not exists and set cookie

		input.country = ApiCallHelper.createCountryForApiCall(FilterController.get().getCountry()); // Get country from filter

		input.listType = FilterController.get().getListTypes().get(0);

		input.on = FilterController.get().getEndDate(); // Get start date from filter

		Date today = FilterHelper.getToday();
		if (CalendarUtil.isSameDate(input.on, today)) {
			input.on = today;
		}
		
		input.category = FilterController.get().getCategory();

		if (pager == null) {
			pager = new Pager();
			pager.count = STEP;
			pager.start = Long.valueOf(0);
			pager.boundless = Boolean.TRUE;
		}

		input.pager = pager; // Set pager used to retrieve and format the wished items (start, number of elements, sorting order)

		input.store = ApiCallHelper.createStoreForApiCall(FilterController.get().getStore());

		// Call to retrieve top items from DB. The response contains List<Rank> for the 3 rank types (free, paid, grossing) , a List<Item> and a Pager
		currentTopItems = service.getAllTopItems(input, new AsyncCallback<GetAllTopItemsResponse>() {

			@Override
			public void onSuccess(GetAllTopItemsResponse output) {
				currentTopItems = null;
				if (output.status == StatusType.StatusTypeSuccess) {
					if (output.pager != null) {
						pager = output.pager;// Set pager as the one received from the server
						if (pager != null && pager.totalCount == null && output.freeRanks != null && output.freeRanks.size() > 0) {
							pager.totalCount = Long.valueOf(output.freeRanks.size());
						}
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
						rows.add(r = new RanksGroup());
						r.free = output.freeRanks.get(i);
						r.paid = output.paidRanks.get(i);
						r.grossing = output.grossingRanks.get(i);
					}

					updateRowData(0, rows); // Inform the displays of the new data. @params Start index, data values
				}
				updateRowCount(rows.size(), true);

				DefaultEventBus.get().fireEventFromSource(new GetAllTopItemsSuccess(input, output), RankController.this);
			}

			@Override
			public void onFailure(Throwable caught) {
				currentTopItems = null;
				updateRowCount(0, true);
				DefaultEventBus.get().fireEventFromSource(new GetAllTopItemsFailure(input, caught), RankController.this);
			}
		});

		// EventController.get().fireEventFromSource(new FetchingRanks(), RankController.this);
	}

	/**
     * 
     */
	public void fetchSalesRanks() {
		CoreService service = ServiceCreator.createCoreService();

		final GetSalesRanksRequest input = new GetSalesRanksRequest();
		input.accessCode = ACCESS_CODE;
		input.session = SessionController.get().getSessionForApiCall();

		input.linkedAccount = FilterController.get().getLinkedAccount();

		input.category = FilterController.get().getCategory();
		input.country = FilterController.get().getCountry();
		input.start = FilterController.get().getStartDate();
		input.end = FilterController.get().getEndDate();
		input.listType = DataTypeHelper.STORE_IPAD_A3_CODE.equals(FilterController.get().getFilter().getStoreA3Code()) ? "ipad" : "";

		service.getSalesRanks(input, new AsyncCallback<GetSalesRanksResponse>() {

			@Override
			public void onSuccess(GetSalesRanksResponse output) {
				DefaultEventBus.get().fireEventFromSource(new GetSalesRanksEventHandler.GetSalesRanksSuccess(input, output), RankController.this);
			}

			@Override
			public void onFailure(Throwable caught) {
				DefaultEventBus.get().fireEventFromSource(new GetSalesRanksEventHandler.GetSalesRanksFailure(input, caught), RankController.this);
			}
		});
	}

	/**
	 * Retrieve real data
	 * 
	 * @param item
	 */
	public void fetchItemSalesRanks(final Item item) {
		cancelRequestItemSalesRanks();

		CoreService service = ServiceCreator.createCoreService();

		final GetItemSalesRanksRequest input = new GetItemSalesRanksRequest();
		input.accessCode = ACCESS_CODE;

		input.session = SessionController.get().getSessionForApiCall();
		input.country = ApiCallHelper.createCountryForApiCall(FilterController.get().getCountry());

		input.start = FilterController.get().getStartDate();
		input.end = FilterController.get().getEndDate();

		input.category = FilterController.get().getCategory();

		input.listType = FilterController.get().getListTypes().get(0);

		input.item = new Item();
		input.item.internalId = item.internalId;
		input.item.source = ApiCallHelper.storeCodeForApiCall(item.source);

		input.pager = new Pager();
		input.pager.start = Long.valueOf(0);
		input.pager.count = Long.valueOf(10000);
		input.pager.sortDirection = SortDirectionType.SortDirectionTypeAscending;
		input.pager.sortBy = "date";

		currentItemSalesRanks = service.getItemSalesRanks(input, new AsyncCallback<GetItemSalesRanksResponse>() {

			@Override
			public void onSuccess(GetItemSalesRanksResponse output) {
				currentItemSalesRanks = null;
				if (output.status == StatusType.StatusTypeSuccess) {
					ItemController.get().addItemToCache(output.item);

					float paid = 0, iap = 0;
					ItemRevenue itemRevenue = null;
					appRevenueDataProvider.getList().clear();
					AppRevenue appRanking = null;

					if (itemRevenueData.getList().size() == 0) {
						itemRevenue = new ItemRevenue();
						itemRevenueData.getList().add(itemRevenue);
					} else {
						itemRevenue = itemRevenueData.getList().get(0);
					}

					float rankPaid = 0;

					if (output.ranks != null && output.ranks.size() > 0 && output.item.price != null) {
						for (Rank rank : output.ranks) {
							if (rank.downloads != null && rank.revenue != null) {
								appRanking = new AppRevenue();
								appRanking.date = rank.date;
								appRanking.revenue = rank.revenue;
								appRevenueDataProvider.getList().add(appRanking);
								paid += (rankPaid = (float) rank.downloads.intValue() * output.item.price.floatValue());
								iap += (rank.revenue.floatValue() - rankPaid);
							}
						}
					}

					itemRevenue.countryFlagStyleName = CountryController.get().getCountryFlag(input.country.a2Code);
					itemRevenue.countryName = CountryController.get().getCountry(input.country.a2Code).name;

					if (output.ranks != null && output.ranks.size() > 0) {
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

					for (AppRevenue ar : appRevenueDataProvider.getList()) {
						ar.currency = itemRevenue.currency;
						ar.total = Float.valueOf(iap + paid);
						ar.revenuePercentForPeriod = (ar.total.floatValue() > 0 ? ar.revenue.floatValue() / ar.total.floatValue() : 0);
					}

					itemRevenueData.refresh();
					appRevenueDataProvider.refresh();
				}

				DefaultEventBus.get().fireEventFromSource(new GetItemSalesRanksSuccess(input, output), RankController.this);
			}

			@Override
			public void onFailure(Throwable caught) {
				currentItemSalesRanks = null;
				DefaultEventBus.get().fireEventFromSource(new GetItemSalesRanksFailure(input, caught), RankController.this);
			}
		});
	}

	/**
	 * Retrieve predictions
	 * 
	 * @param item
	 */
	public void fetchItemRanks(final Item item) {

		cancelRequestItemRanks();

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

		currentItemRanks = service.getItemRanks(input, new AsyncCallback<GetItemRanksResponse>() {

			@Override
			public void onSuccess(GetItemRanksResponse output) {
				currentItemRanks = null;
				if (output != null && output.status == StatusType.StatusTypeSuccess && output.item != null) {
					ItemController.get().addItemToCache(output.item);

					float paid = 0, iap = 0;
					ItemRevenue itemRevenue = null;
					appRevenueDataProvider.getList().clear();
					AppRevenue appRanking = null;

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
								appRanking = new AppRevenue();
								appRanking.date = rank.date;
								appRanking.revenue = rank.revenue;
								appRevenueDataProvider.getList().add(appRanking);
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

					for (AppRevenue ar : appRevenueDataProvider.getList()) {
						ar.currency = itemRevenue.currency;
						ar.total = Float.valueOf(iap + paid);
						ar.revenuePercentForPeriod = (ar.total.floatValue() > 0 ? ar.revenue.floatValue() / ar.total.floatValue() : 0);
					}

					itemRevenueData.refresh();
					appRevenueDataProvider.refresh();
				}

				DefaultEventBus.get().fireEventFromSource(new GetItemRanksEventHandler.GetItemRanksSuccess(input, output), RankController.this);
			}

			@Override
			public void onFailure(Throwable caught) {
				currentItemRanks = null;
				DefaultEventBus.get().fireEventFromSource(new GetItemRanksEventHandler.GetItemRanksFailure(input, caught), RankController.this);
			}
		});

	}

	public void cancelRequestItemRanks() {
		if (currentItemRanks != null) {
			currentItemRanks.cancel();
			currentItemRanks = null;
		}
	}

	public void cancelRequestItemSalesRanks() {
		if (currentItemSalesRanks != null) {
			currentItemSalesRanks.cancel();
			currentItemSalesRanks = null;
		}
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

		if (rows == null || pager == null || pager.totalCount == null || (end > rows.size() && rows.size() != pager.totalCount.intValue())) {
			fetchTopItems();
		} else {
			updateRowData(start, rows.subList(start, Math.min(end, rows.size())));
		}
	}

	public void reset() {

		pager = null;
		rows.clear();

		updateRowData(0, rows);
		updateRowCount(0, false);

	}

	/**
	 * @return
	 */
	public ListDataProvider<ItemRevenue> getItemRevenueDataProvider() {
		return itemRevenueData;
	}

	public ListDataProvider<AppRevenue> getRevenueDataProvider() {
		return appRevenueDataProvider;
	}

}
