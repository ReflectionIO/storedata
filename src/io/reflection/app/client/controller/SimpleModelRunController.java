//
//  SimpleModelRunController.java
//  storedata
//
//  Created by Stefano Capuzzi on 20 Oct 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.controller;

import io.reflection.app.api.admin.client.AdminService;
import io.reflection.app.api.admin.shared.call.GetSimpleModelRunsRequest;
import io.reflection.app.api.admin.shared.call.GetSimpleModelRunsResponse;
import io.reflection.app.api.admin.shared.call.event.GetSimpleModelRunsEventHandler;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.api.shared.datatypes.SortDirectionType;
import io.reflection.app.client.helper.ApiCallHelper;
import io.reflection.app.datatypes.shared.SimpleModelRun;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author Stefano Capuzzi
 *
 */
public class SimpleModelRunController extends AsyncDataProvider<SimpleModelRun> implements ServiceConstants {

	private List<SimpleModelRun> simpleModelRunList = new ArrayList<SimpleModelRun>();
	private long count = -1;
	private Pager pager;

	private static SimpleModelRunController one = null;

	public static SimpleModelRunController get() {
		if (one == null) {
			one = new SimpleModelRunController();
		}

		return one;
	}

	/**
	 * 
	 */
	public void fetchSimpleModelRuns() {
		AdminService service = ServiceCreator.createAdminService();

		final GetSimpleModelRunsRequest input = new GetSimpleModelRunsRequest();
		input.accessCode = ACCESS_CODE;

		input.session = SessionController.get().getSessionForApiCall();

		if (pager == null) {
			pager = new Pager();
			pager.count = STEP;
			pager.start = Long.valueOf(0);
			pager.sortDirection = SortDirectionType.SortDirectionTypeDescending;
		}
		input.pager = pager;

		input.listType = FilterController.get().getListTypes().get(0);
		input.start = FilterController.get().getStartDate();
		input.end = FilterController.get().getEndDate();
		input.country = ApiCallHelper.createCountryForApiCall(FilterController.get().getCountry());
		input.store = ApiCallHelper.createStoreForApiCall(FilterController.get().getStore());
		input.category = FilterController.get().getCategory();

		service.getSimpleModelRuns(input, new AsyncCallback<GetSimpleModelRunsResponse>() {

			@Override
			public void onSuccess(GetSimpleModelRunsResponse output) {
				if (output.status == StatusType.StatusTypeSuccess) {
					if (output.simpleModelRuns != null) {
						simpleModelRunList.addAll(output.simpleModelRuns);
					}

					if (output.pager != null) {
						pager = output.pager;

						if (pager.totalCount != null) {
							count = pager.totalCount.longValue();
						}
					}

					updateRowCount((int) count, true);
					updateRowData(
							input.pager.start.intValue(),
							simpleModelRunList.subList(input.pager.start.intValue(),
									Math.min(input.pager.start.intValue() + input.pager.count.intValue(), pager.totalCount.intValue())));
				}

				EventController.get().fireEventFromSource(new GetSimpleModelRunsEventHandler.GetSimpleModelRunsSuccess(input, output),
						SimpleModelRunController.this);
			}

			@Override
			public void onFailure(Throwable caught) {
				EventController.get().fireEventFromSource(new GetSimpleModelRunsEventHandler.GetSimpleModelRunsFailure(input, caught),
						SimpleModelRunController.this);
			}
		});

	}

	public List<SimpleModelRun> getSimpleModelRuns() {
		return simpleModelRunList;
	}

	public long getSimpleModelRunCount() {
		return count;
	}

	/**
	 * Return true if Categories already fetched
	 * 
	 * @return
	 */
	public boolean simpleModelRunFetched() {
		return count != -1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.view.client.AbstractDataProvider#onRangeChanged(com.google.gwt.view.client.HasData)
	 */
	@Override
	protected void onRangeChanged(HasData<SimpleModelRun> display) {
		Range r = display.getVisibleRange();

		int start = r.getStart();
		int end = start + r.getLength();

		if (!simpleModelRunFetched() || (simpleModelRunFetched() && getSimpleModelRunCount() != simpleModelRunList.size() && end > simpleModelRunList.size())) {
			fetchSimpleModelRuns();
		} else {
			updateRowData(start,
					simpleModelRunList.size() == 0 ? simpleModelRunList : simpleModelRunList.subList(start, Math.min(simpleModelRunList.size(), end)));
		}
	}

	public void reset() {
		simpleModelRunList = new ArrayList<SimpleModelRun>();
		count = -1;
		pager = null;
		updateRowCount(0, false);
	}
}
