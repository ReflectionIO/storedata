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
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.client.handler.FilterEventHandler;
import io.reflection.app.client.handler.RanksEventHandler.FetchingRanks;
import io.reflection.app.client.handler.RanksEventHandler.ReceivedRanks;
import io.reflection.app.client.part.datatypes.RanksGroup;
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
public class RankController extends AsyncDataProvider<RanksGroup> implements ServiceController, FilterEventHandler {

	private static RankController mOne = null;

	private Map<String, Item> mItemLookup = new HashMap<String, Item>();

	private List<RanksGroup> mRows = null;

	private Pager mPager;

	public static RankController get() {
		if (mOne == null) {
			mOne = new RankController();
		}

		return mOne;
	}

	private RankController() {
		EventController.get().addHandlerToSource(FilterEventHandler.TYPE, FilterController.get(), this);
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

					EventController.get().fireEventFromSource(new ReceivedRanks("free" + input.listType, output.freeRanks), RankController.this);
					EventController.get().fireEventFromSource(new ReceivedRanks("paid" + input.listType, output.paidRanks), RankController.this);
					EventController.get().fireEventFromSource(new ReceivedRanks("grossing" + input.listType, output.grossingRanks), RankController.this);

					if (mRows == null) {
						mRows = new ArrayList<RanksGroup>();
					}

					int count = output.freeRanks.size();
					RanksGroup r;
					for (int i = 0; i < count; i++) {
						mRows.add(r = new RanksGroup());
						r.free = output.freeRanks.get(i);
						r.paid = output.paidRanks.get(i);
						r.grossing = output.grossingRanks.get(i);
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

		EventController.get().fireEventFromSource(new FetchingRanks(), RankController.this);
	}

	public Item lookupItem(String externalId) {
		return mItemLookup.get(externalId);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.FilterEventHandler#filterParamChanged(java.lang.String, java.lang.Object, java.lang.Object)
	 */
	@Override
	public <T> void filterParamChanged(String name, T currentValue, T previousValue) {
		mPager = null;
		mRows = null;

		updateRowData(0, new ArrayList<RanksGroup>());
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

		updateRowData(0, new ArrayList<RanksGroup>());
		updateRowCount(0, false);

		fetchTopItems();
	}
}
