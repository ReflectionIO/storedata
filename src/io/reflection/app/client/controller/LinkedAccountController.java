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
import java.util.Arrays;
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
	private List<DataAccount> myAllDataAccounts = null;
	private Map<String, DataSource> dataSourceLookup = new HashMap<String, DataSource>();
	private Map<String, DataAccount> dataAccountLookup = new HashMap<String, DataAccount>();

	private long mCount = -1;

	private List<DataAccount> rows = new ArrayList<DataAccount>();
	private Pager pager = null;
	private Pager pagerMaxCount = null;

	private static LinkedAccountController mOne = null;

	public static LinkedAccountController get() {
		if (mOne == null) {
			mOne = new LinkedAccountController();
		}
		return mOne;
	}

	private void fetchLinkedAccounts() {

		CoreService service = ServiceCreator.createCoreService();

		final GetLinkedAccountsRequest input = new GetLinkedAccountsRequest();
		input.accessCode = ACCESS_CODE;
		input.session = SessionController.get().getSessionForApiCall();

		if (pager == null) {
			pager = new Pager();
			pager.count = SHORT_STEP;
			pager.start = Long.valueOf(0);
			pager.sortDirection = SortDirectionType.SortDirectionTypeDescending;
		}
		input.pager = pager;

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
					if (output.linkedAccounts != null) {
						addLinkedAccountsToLookup(output.linkedAccounts);
						addDataSourceToLookup(output.dataSources);
						DataAccount myLinkedAccount;
						for (DataAccount da : output.linkedAccounts) {
							myLinkedAccount = new DataAccount();
							myLinkedAccount = da;
							myLinkedAccount.source = getDataSource(da.source.id);
							rows.add(myLinkedAccount);
						}
					}

					if (output.pager != null) {
						pager = output.pager;

						if (pager.totalCount != null) {
							mCount = pager.totalCount.longValue();
						}

						if (hasAllLinkedAccounts()) {
							if (myAllDataAccounts == null) {
								myAllDataAccounts = new ArrayList<DataAccount>();
								myAllDataAccounts.addAll(rows);
								pagerMaxCount = new Pager();
								pagerMaxCount.count = (long) Integer.MAX_VALUE;
								pagerMaxCount.start = Long.valueOf(0);
								pagerMaxCount.sortDirection = SortDirectionType.SortDirectionTypeDescending;
								pagerMaxCount.totalCount = pager.totalCount.longValue();
							}
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

	public void fetchAllLinkedAccounts() {

		CoreService service = ServiceCreator.createCoreService();

		final GetLinkedAccountsRequest input = new GetLinkedAccountsRequest();
		input.accessCode = ACCESS_CODE;
		input.session = SessionController.get().getSessionForApiCall();

		pagerMaxCount = new Pager();
		pagerMaxCount.count = (long) Integer.MAX_VALUE;
		pagerMaxCount.start = Long.valueOf(0);
		pagerMaxCount.sortDirection = SortDirectionType.SortDirectionTypeDescending;

		input.pager = pagerMaxCount;

		myAllDataAccounts = new ArrayList<DataAccount>();

		service.getLinkedAccounts(input, new AsyncCallback<GetLinkedAccountsResponse>() {

			@Override
			public void onSuccess(GetLinkedAccountsResponse output) {
				if (output.status == StatusType.StatusTypeSuccess) {

					if (output.linkedAccounts != null) {
						DataAccount myLinkedAccount;
						for (DataAccount da : output.linkedAccounts) {
							myLinkedAccount = new DataAccount();
							myLinkedAccount = da;
							myLinkedAccount.source = getDataSource(da.source.id);
							myAllDataAccounts.add(myLinkedAccount);
						}
					}

					if (output.pager != null) {
						pagerMaxCount = output.pager;
					}

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
					if (myAllDataAccounts != null && pagerMaxCount != null) {
						myAllDataAccounts.add(output.account);
						pagerMaxCount.totalCount = pagerMaxCount.totalCount + 1;
						pagerMaxCount.start = Long.valueOf(0);
					}
					rows.add(output.account);
					addLinkedAccountsToLookup(Arrays.asList(output.account));
					addDataSourceToLookup(Arrays.asList(output.account.source));
					pager.totalCount = pager.totalCount + 1;
					pager.start = Long.valueOf(0);
					mCount = pager.totalCount;
					updateRowCount((int) mCount, true);
					updateRowData(0, rows.subList(0, Math.min(pager.count.intValue(), pager.totalCount.intValue())));
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
					if (myAllDataAccounts != null) {
						myAllDataAccounts.get(myAllDataAccounts.indexOf(dataAccountLookup.get(input.linkedAccount.id.toString()))).password = input.linkedAccount.password;
						myAllDataAccounts.get(myAllDataAccounts.indexOf(dataAccountLookup.get(input.linkedAccount.id.toString()))).properties = input.linkedAccount.properties;
					}
					updateLinkedAccountLookup(input.linkedAccount.id, input.linkedAccount.password, input.linkedAccount.properties);
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
					if (myAllDataAccounts != null) {
						myAllDataAccounts.remove(dataAccountLookup.get(input.linkedAccount.id.toString()));
						pagerMaxCount.totalCount = pagerMaxCount.totalCount - 1;
						pagerMaxCount.start = Long.valueOf(0);
					}
					rows.remove(dataAccountLookup.get(input.linkedAccount.id.toString()));
					deleteLinkedAccountLookup(input.linkedAccount.id);
					pager.totalCount = pager.totalCount - 1;
					pager.start = Long.valueOf(0);
					mCount = pager.totalCount;
					updateRowCount((int) mCount, true);
					updateRowData(0, rows.subList(0, Math.min(pager.count.intValue(), pager.totalCount.intValue())));
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
	public List<DataAccount> getAllLinkedAccounts() {
		return myAllDataAccounts;
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
	 * Add linked account into lookup
	 * 
	 * @param linkedAccounts
	 */
	private void addLinkedAccountsToLookup(List<DataAccount> linkedAccounts) {
		for (DataAccount dataAccount : linkedAccounts) {
			dataAccountLookup.put(dataAccount.id.toString(), dataAccount);
			myDataAccounts.add(dataAccount);
		}
	}

	/**
	 * Update linked account from lookup
	 * 
	 * @param linkedAccountId
	 * @param password
	 * @param properties
	 */
	private void updateLinkedAccountLookup(Long linkedAccountId, String password, String properties) {
		myDataAccounts.get(myDataAccounts.indexOf(dataAccountLookup.get(linkedAccountId.toString()))).password = password;
		myDataAccounts.get(myDataAccounts.indexOf(dataAccountLookup.get(linkedAccountId.toString()))).properties = properties;
		dataAccountLookup.get(linkedAccountId.toString()).password = password;
		dataAccountLookup.get(linkedAccountId.toString()).properties = properties;
	}

	/**
	 * Delete linked account from lookup
	 * 
	 * @param linkedAccountId
	 * @param password
	 * @param properties
	 */
	private void deleteLinkedAccountLookup(Long linkedAccountId) {
		myDataAccounts.remove(dataAccountLookup.get(linkedAccountId.toString()));
		dataAccountLookup.remove(linkedAccountId.toString());
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
		if (end > rows.size()) {
			fetchLinkedAccounts();
		} else {
			updateRowData(start, rows.subList(start, end));
		}
	}

	public boolean hasAllLinkedAccounts() {
		return (pager != null && pager.totalCount == rows.size()) || myAllDataAccounts != null;
	}

	public void reset() {
		myDataAccounts = null;
		myDataSources = null;

		myAllDataAccounts = null;
		pagerMaxCount = null;

		dataAccountLookup.clear();
		dataSourceLookup.clear();

		pager = null;

		rows.clear();
		
		updateRowData(0, rows);
		updateRowCount(0, false);
	}
}
