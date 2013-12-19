//
//  FeedFetchController.java
//  storedata
//
//  Created by William Shakour (billy1380) on 18 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.admin.client.controller;

import io.reflection.app.admin.client.handler.FeedFetchesEventHandler;
import io.reflection.app.admin.client.handler.FilterEventHandler;
import io.reflection.app.api.admin.client.AdminService;
import io.reflection.app.api.admin.shared.call.GetFeedFetchesRequest;
import io.reflection.app.api.admin.shared.call.GetFeedFetchesResponse;
import io.reflection.app.api.admin.shared.call.TriggerIngestRequest;
import io.reflection.app.api.admin.shared.call.TriggerIngestResponse;
import io.reflection.app.api.admin.shared.call.TriggerModelRequest;
import io.reflection.app.api.admin.shared.call.TriggerModelResponse;
import io.reflection.app.api.admin.shared.call.TriggerPredictRequest;
import io.reflection.app.api.admin.shared.call.TriggerPredictResponse;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.shared.datatypes.FeedFetch;

import java.util.ArrayList;
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
public class FeedFetchController extends AsyncDataProvider<FeedFetch> implements ServiceController, FilterEventHandler {
	private static FeedFetchController mOne = null;

	private List<FeedFetch> mRows = null;

	private Pager mPager;

	public static FeedFetchController get() {
		if (mOne == null) {
			mOne = new FeedFetchController();
		}

		return mOne;
	}

	private FeedFetchController() {
		EventController.get().addHandlerToSource(FilterEventHandler.TYPE, FilterController.get(), this);
	}
	
	public void fetchFeedFetches() {
		AdminService service = new AdminService();
		service.setUrl(ADMIN_END_POINT);

		final GetFeedFetchesRequest input = new GetFeedFetchesRequest();
		input.accessCode = ACCESS_CODE;
		
		input.session = SessionController.get().getSessionForApiCall();

		if (mPager == null) {
			mPager = new Pager();
			mPager.count = SHORT_STEP;
			mPager.start = Long.valueOf(0);
		}
		input.pager = mPager;
		
		input.country = FilterController.get().getCountry();
		input.store = FilterController.get().getStore();
		input.listTypes = FilterController.get().getListTypes();

		if (input.country != null && input.store != null && input.listTypes != null) {
			service.getFeedFetches(input, new AsyncCallback<GetFeedFetchesResponse>() {

				@Override
				public void onSuccess(GetFeedFetchesResponse output) {
					if (output.status == StatusType.StatusTypeSuccess) {
						if (output.feedFetches != null) {
							if (output.pager != null) {
								mPager = output.pager;
							}

							EventController.get().fireEventFromSource(new FeedFetchesEventHandler.ReceivedFeedFetches(output.feedFetches),
									FeedFetchController.this);

							if (mRows == null) {
								mRows = new ArrayList<FeedFetch>();
							}

							mRows.addAll(output.feedFetches);

							updateRowData(0, mRows);
							updateRowCount(mPager.totalCount.intValue(), true);
						}
					}
				}

				@Override
				public void onFailure(Throwable caught) {
					Window.alert(caught.getMessage());
				}
			});
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.view.client.AbstractDataProvider#onRangeChanged(com.google.gwt.view.client.HasData)
	 */
	@Override
	protected void onRangeChanged(HasData<FeedFetch> display) {
		Range r = display.getVisibleRange();

		int start = r.getStart();
		int end = start + r.getLength();

		if (mRows == null || end > mRows.size()) {
			fetchFeedFetches();
		} else {
			updateRowData(start, mRows.subList(start, end));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.admin.client.handler.FilterEventHandler#filterParamChanged(java.lang.String, java.lang.Object, java.lang.Object)
	 */
	@Override
	public <T> void filterParamChanged(String name, T currentValue, T previousValue) {
		mPager = null;
		mRows = null;
		
		updateRowData(0, new ArrayList<FeedFetch>());
		updateRowCount(0, false);

		fetchFeedFetches();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.admin.client.handler.FilterEventHandler#filterParamsChanged(java.util.Map, java.util.Map)
	 */
	@Override
	public void filterParamsChanged(Map<String, ?> currentValues, Map<String, ?> previousValues) {
		mPager = null;
		mRows = null;

		updateRowData(0, new ArrayList<FeedFetch>());
		updateRowCount(0, false);
		
		fetchFeedFetches();
	}
	
	public void model(String code) {
		AdminService service = new AdminService();
		service.setUrl(ADMIN_END_POINT);

		final TriggerModelRequest input = new TriggerModelRequest();
		input.accessCode = ACCESS_CODE;
		
		input.session = SessionController.get().getSessionForApiCall();
		
		input.country = FilterController.get().getCountry();
		input.store = FilterController.get().getStore();
		input.listTypes = FilterController.get().getAllListTypes();
		input.code = code;
	
		service.triggerModel(input, new AsyncCallback<TriggerModelResponse>() {
			
			@Override
			public void onSuccess(TriggerModelResponse output) {
				if (output.status == StatusType.StatusTypeFailure && output.error != null) { 
					Window.alert(output.error.message);
				}
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
			}
		});
	}

	/**
	 * @param code
	 */
	public void ingest(String code) {
		AdminService service = new AdminService();
		service.setUrl(ADMIN_END_POINT);

		final TriggerIngestRequest input = new TriggerIngestRequest();
		input.accessCode = ACCESS_CODE;
		
		input.session = SessionController.get().getSessionForApiCall();
		
		input.country = FilterController.get().getCountry();
		input.store = FilterController.get().getStore();
		input.listTypes = FilterController.get().getAllListTypes();
		input.code = code;
	
		service.triggerIngest(input, new AsyncCallback<TriggerIngestResponse>() {
			
			@Override
			public void onSuccess(TriggerIngestResponse output) {
				if (output.status == StatusType.StatusTypeFailure && output.error != null) { 
					Window.alert(output.error.message);
				}
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
			}
		});
		
	}

	/**
	 * @param code
	 */
	public void predict(String code) {
		AdminService service = new AdminService();
		service.setUrl(ADMIN_END_POINT);

		final TriggerPredictRequest input = new TriggerPredictRequest();
		input.accessCode = ACCESS_CODE;
		
		input.session = SessionController.get().getSessionForApiCall();
		
		input.country = FilterController.get().getCountry();
		input.store = FilterController.get().getStore();
		input.listTypes = FilterController.get().getAllListTypes();
		input.code = code;
	
		service.triggerPredict(input, new AsyncCallback<TriggerPredictResponse>() {
			
			@Override
			public void onSuccess(TriggerPredictResponse output) {
				if (output.status == StatusType.StatusTypeFailure && output.error != null) { 
					Window.alert(output.error.message);
				}
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
			}
		});
	}

}
