//
//  DataAccountFetchController.java
//  storedata
//
//  Created by Stefano Capuzzi on 9 Oct 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.controller;

import io.reflection.app.api.admin.client.AdminService;
import io.reflection.app.api.admin.shared.call.GetDataAccountFetchesRequest;
import io.reflection.app.api.admin.shared.call.GetDataAccountFetchesResponse;
import io.reflection.app.api.admin.shared.call.TriggerDataAccountFetchIngestRequest;
import io.reflection.app.api.admin.shared.call.TriggerDataAccountFetchIngestResponse;
import io.reflection.app.api.admin.shared.call.TriggerDataAccountGatherRequest;
import io.reflection.app.api.admin.shared.call.TriggerDataAccountGatherResponse;
import io.reflection.app.api.admin.shared.call.event.GetDataAccountFetchesEventHandler;
import io.reflection.app.api.admin.shared.call.event.TriggerDataAccountFetchIngestEventHandler;
import io.reflection.app.api.admin.shared.call.event.TriggerDataAccountGatherEventHandler;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.api.shared.datatypes.SortDirectionType;
import io.reflection.app.datatypes.shared.DataAccount;
import io.reflection.app.datatypes.shared.DataAccountFetch;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.http.client.Request;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author Stefano Capuzzi
 * 
 */
public class DataAccountFetchController extends AsyncDataProvider<DataAccountFetch> implements ServiceConstants {

	private List<DataAccountFetch> dataAccountFetchList = new ArrayList<DataAccountFetch>();
	private long count = -1;
	private Pager pager;
	private Request current;

	private Long dataAccountId;

	private static DataAccountFetchController one = null;

	public static DataAccountFetchController get() {
		if (one == null) {
			one = new DataAccountFetchController();
		}

		return one;
	}

	public void fetchDataAccountFetches() {
		if (current != null) {
			current.cancel();
			current = null;
		}

		AdminService service = ServiceCreator.createAdminService();

		final GetDataAccountFetchesRequest input = new GetDataAccountFetchesRequest();
		input.accessCode = ACCESS_CODE;

		input.session = SessionController.get().getSessionForApiCall();

		if (pager == null) {
			pager = new Pager();
			pager.count = STEP;
			pager.start = Long.valueOf(0);
			pager.sortDirection = SortDirectionType.SortDirectionTypeDescending;
		}
		input.pager = pager;

		input.dataAccount = new DataAccount();
		if (dataAccountId != null) {
			input.dataAccount.id = Long.valueOf(dataAccountId);
		}

		input.start = FilterController.get().getStartDate();
		input.end = FilterController.get().getEndDate();

		current = service.getDataAccountFetches(input, new AsyncCallback<GetDataAccountFetchesResponse>() {

			@Override
			public void onSuccess(GetDataAccountFetchesResponse output) {
				current = null;
				if (output.status == StatusType.StatusTypeSuccess) {
					if (output.dataAccountFetches != null) {
						dataAccountFetchList.addAll(output.dataAccountFetches);
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
							dataAccountFetchList.subList(input.pager.start.intValue(),
									Math.min(input.pager.start.intValue() + input.pager.count.intValue(), pager.totalCount.intValue())));
				}

				EventController.get().fireEventFromSource(new GetDataAccountFetchesEventHandler.GetDataAccountFetchesSuccess(input, output),
						DataAccountFetchController.this);
			}

			@Override
			public void onFailure(Throwable caught) {
				current = null;
				EventController.get().fireEventFromSource(new GetDataAccountFetchesEventHandler.GetDataAccountFetchesFailure(input, caught),
						DataAccountFetchController.this);
			}

		});

	}

	/**
	 * Trigger the process of gathering data account
	 * 
	 * @param dataAccount
	 */
	public void gather(DataAccount dataAccount, Date from) {
		AdminService service = ServiceCreator.createAdminService();

		final TriggerDataAccountGatherRequest input = new TriggerDataAccountGatherRequest();
		input.accessCode = ACCESS_CODE;

		input.session = SessionController.get().getSessionForApiCall();

		input.dataAccount = dataAccount;

		input.from = from;

		service.triggerDataAccountGather(input, new AsyncCallback<TriggerDataAccountGatherResponse>() {

			@Override
			public void onSuccess(TriggerDataAccountGatherResponse output) {
				EventController.get().fireEventFromSource(new TriggerDataAccountGatherEventHandler.TriggerDataAccountGatherSuccess(input, output),
						DataAccountFetchController.this);
			}

			@Override
			public void onFailure(Throwable caught) {
				EventController.get().fireEventFromSource(new TriggerDataAccountGatherEventHandler.TriggerDataAccountGatherFailure(input, caught),
						DataAccountFetchController.this);
			}

		});

	}

	/**
	 * Trigger the process of ingesting the data account to obtain fetches
	 * 
	 * @param dataAccountFetch
	 */
	public void ingest(DataAccountFetch dataAccountFetch) {
		AdminService service = ServiceCreator.createAdminService();

		final TriggerDataAccountFetchIngestRequest input = new TriggerDataAccountFetchIngestRequest();
		input.accessCode = ACCESS_CODE;

		input.session = SessionController.get().getSessionForApiCall();

		input.fetch = dataAccountFetch;

		service.triggerDataAccountFetchIngest(input, new AsyncCallback<TriggerDataAccountFetchIngestResponse>() {

			@Override
			public void onSuccess(TriggerDataAccountFetchIngestResponse output) {
				if (output.status == StatusType.StatusTypeSuccess) {

				}
				EventController.get().fireEventFromSource(new TriggerDataAccountFetchIngestEventHandler.TriggerDataAccountFetchIngestSuccess(input, output),
						DataAccountFetchController.this);
			}

			@Override
			public void onFailure(Throwable caught) {
				EventController.get().fireEventFromSource(new TriggerDataAccountFetchIngestEventHandler.TriggerDataAccountFetchIngestFailure(input, caught),
						DataAccountFetchController.this);
			}

		});
	}

	public List<DataAccountFetch> getDataAccountFetches() {
		return dataAccountFetchList;
	}

	public long getDataAccountFetchCount() {
		return count;
	}

	public boolean hasDataAccountFetch() {
		return getDataAccountFetchCount() > 0;
	}

	/**
	 * Return true if DataAccountFetch already fetched
	 * 
	 * @return
	 */
	public boolean dataAccountFetchFetched() {
		return count != -1;
	}

	public void setDataAccountId(Long dataAccountId) {
		this.dataAccountId = dataAccountId;
	}

	public Long getDataAccountId() {
		return dataAccountId;
	}

	public void reset() {
		dataAccountFetchList = new ArrayList<DataAccountFetch>();
		count = -1;
		pager = null;
		dataAccountId = null;
		updateRowCount(0, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.view.client.AbstractDataProvider#onRangeChanged(com.google.gwt.view.client.HasData)
	 */
	@Override
	protected void onRangeChanged(HasData<DataAccountFetch> display) {
		Range r = display.getVisibleRange();

		int start = r.getStart();
		int end = start + r.getLength();

		if (dataAccountFetchFetched()) {
			if (getDataAccountFetchCount() != dataAccountFetchList.size() && end > dataAccountFetchList.size()) {
				fetchDataAccountFetches();
			} else {
				updateRowData(
						start,
						dataAccountFetchList.size() == 0 ? dataAccountFetchList : dataAccountFetchList.subList(start,
								Math.min(dataAccountFetchList.size(), end)));
			}
		}
	}

}
