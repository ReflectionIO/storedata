//
//  FeedFetchController.java
//  storedata
//
//  Created by William Shakour (billy1380) on 18 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.controller;

import io.reflection.app.api.admin.client.AdminService;
import io.reflection.app.api.admin.shared.call.GetFeedFetchesRequest;
import io.reflection.app.api.admin.shared.call.GetFeedFetchesResponse;
import io.reflection.app.api.admin.shared.call.TriggerIngestRequest;
import io.reflection.app.api.admin.shared.call.TriggerIngestResponse;
import io.reflection.app.api.admin.shared.call.TriggerModelRequest;
import io.reflection.app.api.admin.shared.call.TriggerModelResponse;
import io.reflection.app.api.admin.shared.call.TriggerPredictRequest;
import io.reflection.app.api.admin.shared.call.TriggerPredictResponse;
import io.reflection.app.api.admin.shared.call.event.GetFeedFetchesEventHandler.GetFeedFetchesFailure;
import io.reflection.app.api.admin.shared.call.event.GetFeedFetchesEventHandler.GetFeedFetchesSuccess;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.client.DefaultEventBus;
import io.reflection.app.client.helper.ApiCallHelper;
import io.reflection.app.datatypes.shared.FeedFetch;
import io.reflection.app.datatypes.shared.ModelTypeType;
import io.reflection.app.datatypes.shared.SimpleModelRun;
import io.reflection.app.shared.util.DataTypeHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gwt.http.client.Request;
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
public class FeedFetchController extends AsyncDataProvider<FeedFetch> implements ServiceConstants {
	private static FeedFetchController mOne = null;

	private List<FeedFetch> mRows = new ArrayList<FeedFetch>();
	private Pager mPager;
	private long count = 0;
	private Request current;

	public static FeedFetchController get() {
		if (mOne == null) {
			mOne = new FeedFetchController();
		}

		return mOne;
	}

	public void fetchFeedFetches() {
		if (current != null) {
			current.cancel();
			current = null;
		}

		AdminService service = ServiceCreator.createAdminService();

		final GetFeedFetchesRequest input = new GetFeedFetchesRequest();
		input.accessCode = ACCESS_CODE;

		input.session = SessionController.get().getSessionForApiCall();

		if (mPager == null) {
			mPager = new Pager();
			mPager.count = SHORT_STEP;
			mPager.start = Long.valueOf(0);
		}

		input.pager = mPager;

		input.country = ApiCallHelper.createCountryForApiCall(FilterController.get().getCountry());
		input.store = ApiCallHelper.createStoreForApiCall(FilterController.get().getStore());
		input.category = FilterController.get().getCategory();
		input.listTypes = FilterController.get().getListTypes();

		if (input.country != null && input.store != null && input.listTypes != null) {
			current = service.getFeedFetches(input, new AsyncCallback<GetFeedFetchesResponse>() {

				@Override
				public void onSuccess(GetFeedFetchesResponse output) {
					current = null;
					if (output.status == StatusType.StatusTypeSuccess) {
						if (output.feedFetches != null) {
							if (mRows == null) {
								mRows = new ArrayList<FeedFetch>();
							}

							mRows.addAll(output.feedFetches);
						}

						if (output.pager != null) {
							mPager = output.pager;

							if (mPager.totalCount != null) {
								count = mPager.totalCount.longValue();
							}
						}

						updateRowCount((int) count, true);
						updateRowData(
								input.pager.start.intValue(),
								mRows == null ? new ArrayList<FeedFetch>() : mRows.subList(input.pager.start.intValue(), Math.min(input.pager.start.intValue()
										+ input.pager.count.intValue(), count == 0 ? (mRows == null ? 0 : mRows.size()) : (int) count)));
					}

					DefaultEventBus.get().fireEventFromSource(new GetFeedFetchesSuccess(input, output), FeedFetchController.this);
				}

				@Override
				public void onFailure(Throwable caught) {
					current = null;
					DefaultEventBus.get().fireEventFromSource(new GetFeedFetchesFailure(input, caught), FeedFetchController.this);
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

	public void reset() {
		mPager = null;

		mRows = null;

		updateRowData(0, new ArrayList<FeedFetch>());
		updateRowCount(0, false);

		fetchFeedFetches();
	}

	/**
	 * 
	 * @param feedFetch
	 */
	public void model(FeedFetch feedFetch) {
		AdminService service = ServiceCreator.createAdminService();

		final TriggerModelRequest input = new TriggerModelRequest();
		input.accessCode = ACCESS_CODE;

		input.session = SessionController.get().getSessionForApiCall();

		// input.country = DataTypeHelper.createCountry(feedFetch.country);
		// input.store = DataTypeHelper.createStore(feedFetch.store);
		// input.category = ApiCallHelper.createCategoryForApiCall(feedFetch.category);
		// input.code = feedFetch.code;
		input.modelType = ModelTypeType.ModelTypeTypeSimple;
		// input.listTypes = Arrays.asList(feedFetch.type);
		input.feedFetch = ApiCallHelper.createFeedFetchForApiCall(feedFetch);

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
	 * 
	 * @param feedFetch
	 */
	public void ingest(FeedFetch feedFetch) {
		AdminService service = ServiceCreator.createAdminService();

		final TriggerIngestRequest input = new TriggerIngestRequest();
		input.accessCode = ACCESS_CODE;

		input.session = SessionController.get().getSessionForApiCall();

		input.country = DataTypeHelper.createCountry(feedFetch.country);
		input.store = DataTypeHelper.createStore(feedFetch.store);
		input.category = ApiCallHelper.createCategoryForApiCall(feedFetch.category);
		input.code = feedFetch.code;
		input.listTypes = Arrays.asList(feedFetch.type);

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
	 * @param feedFetch
	 */
	public void predict(FeedFetch feedFetch) {
		AdminService service = ServiceCreator.createAdminService();
		final TriggerPredictRequest input = new TriggerPredictRequest();
		input.accessCode = ACCESS_CODE;

		input.session = SessionController.get().getSessionForApiCall();

		// input.country = DataTypeHelper.createCountry(feedFetch.country);
		// input.store = DataTypeHelper.createStore(feedFetch.store);
		// input.category = ApiCallHelper.createCategoryForApiCall(feedFetch.category);
		// input.code = feedFetch.code;
		input.modelType = ModelTypeType.ModelTypeTypeSimple;
		// input.listTypes = Arrays.asList(feedFetch.type);
		input.simpleModelRun = new SimpleModelRun();
		input.simpleModelRun.feedFetch = ApiCallHelper.createFeedFetchForApiCall(feedFetch);

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
