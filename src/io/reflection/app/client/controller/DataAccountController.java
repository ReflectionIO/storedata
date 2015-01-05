//
//  DataAccountController.java
//  storedata
//
//  Created by Stefano Capuzzi on 7 Oct 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.controller;

import io.reflection.app.api.admin.client.AdminService;
import io.reflection.app.api.admin.shared.call.GetDataAccountsRequest;
import io.reflection.app.api.admin.shared.call.GetDataAccountsResponse;
import io.reflection.app.api.admin.shared.call.JoinDataAccountRequest;
import io.reflection.app.api.admin.shared.call.JoinDataAccountResponse;
import io.reflection.app.api.admin.shared.call.event.GetDataAccountsEventHandler.GetDataAccountsFailure;
import io.reflection.app.api.admin.shared.call.event.GetDataAccountsEventHandler.GetDataAccountsSuccess;
import io.reflection.app.api.admin.shared.call.event.JoinDataAccountEventHandler.JoinDataAccountFailure;
import io.reflection.app.api.admin.shared.call.event.JoinDataAccountEventHandler.JoinDataAccountSuccess;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.api.shared.datatypes.SortDirectionType;
import io.reflection.app.client.DefaultEventBus;
import io.reflection.app.datatypes.shared.DataAccount;

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
public class DataAccountController extends AsyncDataProvider<DataAccount> implements ServiceConstants {

	private List<DataAccount> dataAccountList = new ArrayList<DataAccount>();
	private long count = -1;
	private Pager pager;

	private static DataAccountController one = null;

	public static DataAccountController get() {
		if (one == null) {
			one = new DataAccountController();
		}

		return one;
	}

	private void fetchDataAccounts() {
		AdminService service = ServiceCreator.createAdminService();

		final GetDataAccountsRequest input = new GetDataAccountsRequest();
		input.accessCode = ACCESS_CODE;

		input.session = SessionController.get().getSessionForApiCall();

		if (pager == null) {
			pager = new Pager();
			pager.count = STEP;
			pager.start = Long.valueOf(0);
			pager.sortDirection = SortDirectionType.SortDirectionTypeDescending;
		}
		input.pager = pager;

		service.getDataAccounts(input, new AsyncCallback<GetDataAccountsResponse>() {

			@Override
			public void onSuccess(GetDataAccountsResponse output) {
				if (output.status == StatusType.StatusTypeSuccess) {
					if (output.dataAccounts != null) {
						dataAccountList.addAll(output.dataAccounts);
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
							dataAccountList.subList(input.pager.start.intValue(),
									Math.min(input.pager.start.intValue() + input.pager.count.intValue(), pager.totalCount.intValue())));
				}

				DefaultEventBus.get().fireEventFromSource(new GetDataAccountsSuccess(input, output), DataAccountController.this);
			}

			@Override
			public void onFailure(Throwable caught) {
				DefaultEventBus.get().fireEventFromSource(new GetDataAccountsFailure(input, caught), DataAccountController.this);
			}
		});
	}

	public void joinDataAccount(DataAccount dataAccount) {
		AdminService service = ServiceCreator.createAdminService();

		final JoinDataAccountRequest input = new JoinDataAccountRequest();
		input.accessCode = ACCESS_CODE;

		input.session = SessionController.get().getSessionForApiCall();

		input.dataAccount = dataAccount;

		service.joinDataAccount(input, new AsyncCallback<JoinDataAccountResponse>() {

			@Override
			public void onSuccess(JoinDataAccountResponse output) {
				if (output.status == StatusType.StatusTypeSuccess) {

				}
				DefaultEventBus.get().fireEventFromSource(new JoinDataAccountSuccess(input, output), DataAccountController.this);
			}

			@Override
			public void onFailure(Throwable caught) {
				DefaultEventBus.get().fireEventFromSource(new JoinDataAccountFailure(input, caught), DataAccountController.this);
			}

		});
	}

	public List<DataAccount> getDataAccounts() {
		return dataAccountList;
	}

	public long getDataAccountsCount() {
		return count;
	}

	/**
	 * Return true if Data Accounts already fetched
	 * 
	 * @return
	 */
	public boolean dataAccountsFetched() {
		return count != -1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.view.client.AbstractDataProvider#onRangeChanged(com.google.gwt.view.client.HasData)
	 */
	@Override
	protected void onRangeChanged(HasData<DataAccount> display) {
		Range r = display.getVisibleRange();

		int start = r.getStart();
		int end = start + r.getLength();

		if (!dataAccountsFetched() || (dataAccountsFetched() && getDataAccountsCount() != dataAccountList.size() && end > dataAccountList.size())) {
			fetchDataAccounts();
		} else {
			updateRowData(start, dataAccountList.size() == 0 ? dataAccountList : dataAccountList.subList(start, Math.min(dataAccountList.size(), end)));
		}
	}

}
