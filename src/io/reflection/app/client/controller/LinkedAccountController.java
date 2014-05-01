//
//  LinkedAccountController.java
//  storedata
//
//  Created by William Shakour (billy1380) on 27 Dec 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.controller;

import io.reflection.app.api.core.client.CoreService;
import io.reflection.app.api.core.shared.call.DeleteLinkedAccountRequest;
import io.reflection.app.api.core.shared.call.DeleteLinkedAccountResponse;
import io.reflection.app.api.core.shared.call.GetLinkedAccountsRequest;
import io.reflection.app.api.core.shared.call.GetLinkedAccountsResponse;
import io.reflection.app.api.core.shared.call.LinkAccountRequest;
import io.reflection.app.api.core.shared.call.LinkAccountResponse;
import io.reflection.app.api.core.shared.call.UpdateLinkedAccountRequest;
import io.reflection.app.api.core.shared.call.UpdateLinkedAccountResponse;
import io.reflection.app.api.core.shared.call.event.DeleteLinkedAccountEventHandler.DeleteLinkedAccountFailure;
import io.reflection.app.api.core.shared.call.event.DeleteLinkedAccountEventHandler.DeleteLinkedAccountSuccess;
import io.reflection.app.api.core.shared.call.event.GetLinkedAccountsEventHandler;
import io.reflection.app.api.core.shared.call.event.LinkAccountEventHandler.LinkAccountFailure;
import io.reflection.app.api.core.shared.call.event.LinkAccountEventHandler.LinkAccountSuccess;
import io.reflection.app.api.core.shared.call.event.UpdateLinkedAccountEventHandler;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.api.shared.datatypes.SortDirectionType;
import io.reflection.app.datatypes.shared.DataAccount;
import io.reflection.app.datatypes.shared.DataSource;
import io.reflection.app.datatypes.shared.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import com.google.gwt.view.client.TreeViewModel;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author billy1380
 * 
 */
public class LinkedAccountController extends AsyncDataProvider<DataAccount> implements ServiceConstants, TreeViewModel {

	private List<DataAccount> myDataAccounts = null;
	private List<DataSource> myDataSources = null;

	private Map<String, DataSource> dataSourceLookup = new HashMap<String, DataSource>();
	private Map<String, DataAccount> dataAccountLookup = new HashMap<String, DataAccount>();

	private long mCount = -1;

	private List<DataAccount> rows = null;
	private Pager pager = null;

	private static LinkedAccountController mOne = null;

	public static LinkedAccountController get() {
		if (mOne == null) {
			mOne = new LinkedAccountController();
		}
		return mOne;
	}

	/**
	 * 
	 */
	public void showLinkedAccounts() {

		if (isLinkedAccountFetched()) { // Retrieved previously
			updateRowCount((int) mCount, true);
			updateRowData(0, rows.subList(0, Math.min(STEP.intValue(), pager.totalCount.intValue())));
		} else {
			fetchLinkedAccounts();
		}

	}

	public void fetchLinkedAccounts() {

		CoreService service = ServiceCreator.createCoreService();

		final GetLinkedAccountsRequest input = new GetLinkedAccountsRequest();
		input.accessCode = ACCESS_CODE;
		input.session = SessionController.get().getSessionForApiCall();

		if (pager == null) {
			pager = new Pager();
			pager.count = STEP;
			pager.start = Long.valueOf(0);
			pager.sortDirection = SortDirectionType.SortDirectionTypeDescending;
		}
		input.pager = pager;

		if (rows == null) {
			rows = new ArrayList<DataAccount>();
		}

		if (myDataAccounts == null) {
			myDataAccounts = new ArrayList<DataAccount>();
		}

		if (myDataSources == null) {
			myDataSources = new ArrayList<DataSource>();
		}

		service.getLinkedAccounts(input, new AsyncCallback<GetLinkedAccountsResponse>() {

			@Override
			public void onSuccess(GetLinkedAccountsResponse output) {
				if (output.status == StatusType.StatusTypeSuccess) {

					if (output.pager != null) {
						pager = output.pager;

						if (pager.totalCount != null) {
							mCount = pager.totalCount.longValue();
						}
					}

					if (output.linkedAccounts != null) {

						addLinkedAccountToLookup(output.linkedAccounts);
						addDataSourceToLookup(output.dataSources);

						DataAccount myLinkedAccount;
						for (DataAccount da : output.linkedAccounts) {
							myLinkedAccount = new DataAccount();
							myLinkedAccount = da;
							myLinkedAccount.source = getDataSource(da.source.id);
							rows.add(myLinkedAccount);
						}

					}

					updateRowCount((int) mCount, true);
					updateRowData(
							input.pager.start.intValue(),
							rows.subList(input.pager.start.intValue(),
									Math.min(input.pager.start.intValue() + input.pager.count.intValue(), pager.totalCount.intValue())));

				}

				EventController.get().fireEventFromSource(new GetLinkedAccountsEventHandler.GetLinkedAccountsSuccess(input, output),
						LinkedAccountController.this);
			}

			@Override
			public void onFailure(Throwable caught) {
				EventController.get().fireEventFromSource(new GetLinkedAccountsEventHandler.GetLinkedAccountsFailure(input, caught),
						LinkedAccountController.this);
			}
		});
	}

	/**
	 * Add new linked account
	 * 
	 * @param sourceId
	 * @param username
	 * @param password
	 * @param properties
	 */
	public void linkAccount(Long sourceId, String username, String password, String properties) {
		CoreService service = ServiceCreator.createCoreService();

		final LinkAccountRequest input = new LinkAccountRequest();
		input.accessCode = ACCESS_CODE;

		input.session = SessionController.get().getSessionForApiCall();

		input.username = username;
		input.password = password;
		input.properties = properties;

		DataSource source = new DataSource();
		source.id = sourceId;

		input.source = source;

		service.linkAccount(input, new AsyncCallback<LinkAccountResponse>() {

			@Override
			public void onSuccess(LinkAccountResponse output) {
				if (output.status == StatusType.StatusTypeSuccess) {
					reset();
				}
				EventController.get().fireEventFromSource(new LinkAccountSuccess(input, output), LinkedAccountController.this);
			}

			@Override
			public void onFailure(Throwable caught) {
				EventController.get().fireEventFromSource(new LinkAccountFailure(input, caught), LinkedAccountController.this);
			}
		});

	}

	/**
	 * Change the linked account password and properties - the account user name can't be changed
	 * 
	 * @param accountUsername
	 * @param password
	 * @param vendorNumber
	 */
	public void updateLinkedAccont(Long linkedAccountId, String password, String properties) {
		CoreService service = ServiceCreator.createCoreService();
		final UpdateLinkedAccountRequest input = new UpdateLinkedAccountRequest();
		input.accessCode = ACCESS_CODE;

		input.session = SessionController.get().getSessionForApiCall();

		input.linkedAccount = new DataAccount();

		input.linkedAccount.id = linkedAccountId;

		input.linkedAccount.password = password;

		input.linkedAccount.properties = properties;

		// input.linkedAccount.source = new DataSource();
		// input.linkedAccount.source.a3Code = dataSourceLookup.get(dataAccountLookup.get(linkedAccountId.toString()).source.id.toString()).a3Code;

		service.updateLinkedAccount(input, new AsyncCallback<UpdateLinkedAccountResponse>() {

			@Override
			public void onSuccess(UpdateLinkedAccountResponse output) {
				if (output.status == StatusType.StatusTypeSuccess) {
					reset();
				}

				EventController.get().fireEventFromSource(new UpdateLinkedAccountEventHandler.UpdateLinkedAccountSuccess(input, output),
						LinkedAccountController.this);
			}

			@Override
			public void onFailure(Throwable caught) {
				EventController.get().fireEventFromSource(new UpdateLinkedAccountEventHandler.UpdateLinkedAccountFailure(input, caught),
						LinkedAccountController.this);
			}
		});
	}

	/**
	 * Delete the linked account identified by id
	 * 
	 * @param linkedAccountId
	 */
	public void deleteLinkedAccount(DataAccount linkedAccount) {

		CoreService service = ServiceCreator.createCoreService();
		final DeleteLinkedAccountRequest input = new DeleteLinkedAccountRequest();
		input.accessCode = ACCESS_CODE;

		input.session = SessionController.get().getSessionForApiCall();

		input.linkedAccount = linkedAccount;

		service.deleteLinkedAccount(input, new AsyncCallback<DeleteLinkedAccountResponse>() {

			@Override
			public void onSuccess(DeleteLinkedAccountResponse output) {
				if (output.status == StatusType.StatusTypeSuccess) {
					reset();
				}
				EventController.get().fireEventFromSource(new DeleteLinkedAccountSuccess(input, output), LinkedAccountController.this);
			}

			@Override
			public void onFailure(Throwable caught) {
				EventController.get().fireEventFromSource(new DeleteLinkedAccountFailure(input, caught), LinkedAccountController.this);
			}
		});

	}

	/**
	 * Get all linked accounts
	 * 
	 * @return
	 */
	public List<DataAccount> getLinkedAccounts() {
		return myDataAccounts;
	}

	/**
	 * 
	 * @return
	 */
	public long getLinkedAccountsCount() {
		return mCount;
	}

	/**
	 * Get linked account
	 * 
	 * @param linkedAccountId
	 * @return
	 */
	public DataAccount getLinkedAccount(Long linkedAccountId) {
		return dataAccountLookup.get(linkedAccountId.toString());
	}

	/**
	 * Get data source
	 * 
	 * @param dataSourceId
	 * @return
	 */
	public DataSource getDataSource(Long dataSourceId) {
		return dataSourceLookup.get(dataSourceId.toString());
	}

	/**
	 * 
	 * @param linkedAccounts
	 */
	private void addLinkedAccountToLookup(List<DataAccount> linkedAccounts) {
		for (DataAccount dataAccount : linkedAccounts) {
			dataAccountLookup.put(dataAccount.id.toString(), dataAccount);
			myDataAccounts.add(dataAccount);
		}
	}

	/**
	 * 
	 * @param dataSources
	 */
	private void addDataSourceToLookup(List<DataSource> dataSources) {
		for (DataSource dataSource : dataSources) {
			dataSourceLookup.put(dataSource.id.toString(), dataSource);
			myDataSources.add(dataSource);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.view.client.TreeViewModel#getNodeInfo(java.lang.Object)
	 */
	@Override
	public <T> NodeInfo<?> getNodeInfo(T value) {
		if (value == null) {
			// return new DefaultNodeInfo<DataAccount>();
		} else if (value instanceof DataAccount) {
			// return new DefaultNodeInfo<Item>();
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.view.client.TreeViewModel#isLeaf(java.lang.Object)
	 */
	@Override
	public boolean isLeaf(Object value) {
		return value != null && value instanceof Item;
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

		if (rows != null) {
			if (end > rows.size()) {
				fetchLinkedAccounts();
			} else {
				updateRowData(start, rows.subList(start, end));
			}
		}

	}

	/**
	 * In case the Linked Account are already present because of visited MyApps page
	 * 
	 * @return
	 */
	public boolean isLinkedAccountFetched() {
		return myDataAccounts != null && myDataSources != null && rows != null && pager != null;
	}

	public boolean hasLinkedAccounts() {
		return !dataAccountLookup.isEmpty() && !dataSourceLookup.isEmpty();
	}

	public void reset() {
		myDataAccounts = null;
		myDataSources = null;
		pager = null;
		rows = null;

		dataAccountLookup.clear();
		dataSourceLookup.clear();

		updateRowCount(0, false); // Clear table and force loader

	}

}
