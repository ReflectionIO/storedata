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
import io.reflection.app.api.core.shared.call.GetLinkedAccountItemRequest;
import io.reflection.app.api.core.shared.call.GetLinkedAccountItemResponse;
import io.reflection.app.api.core.shared.call.GetLinkedAccountsRequest;
import io.reflection.app.api.core.shared.call.GetLinkedAccountsResponse;
import io.reflection.app.api.core.shared.call.LinkAccountRequest;
import io.reflection.app.api.core.shared.call.LinkAccountResponse;
import io.reflection.app.api.core.shared.call.UpdateLinkedAccountRequest;
import io.reflection.app.api.core.shared.call.UpdateLinkedAccountResponse;
import io.reflection.app.api.core.shared.call.event.DeleteLinkedAccountEventHandler.DeleteLinkedAccountFailure;
import io.reflection.app.api.core.shared.call.event.DeleteLinkedAccountEventHandler.DeleteLinkedAccountSuccess;
import io.reflection.app.api.core.shared.call.event.GetLinkedAccountItemEventHandler;
import io.reflection.app.api.core.shared.call.event.GetLinkedAccountsEventHandler;
import io.reflection.app.api.core.shared.call.event.LinkAccountEventHandler.LinkAccountFailure;
import io.reflection.app.api.core.shared.call.event.LinkAccountEventHandler.LinkAccountSuccess;
import io.reflection.app.api.core.shared.call.event.UpdateLinkedAccountEventHandler;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.api.shared.datatypes.SortDirectionType;
import io.reflection.app.client.DefaultEventBus;
import io.reflection.app.client.mixpanel.MixpanelHelper;
import io.reflection.app.datatypes.shared.DataAccount;
import io.reflection.app.datatypes.shared.DataSource;
import io.reflection.app.datatypes.shared.Item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.http.client.Request;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author billy1380
 * 
 */
public class LinkedAccountController extends AsyncDataProvider<DataAccount> implements ServiceConstants {

	private List<DataAccount> myDataAccounts = null;
	private List<DataSource> myDataSources = null;
	private Map<String, DataSource> myDataSourceLookup = new HashMap<String, DataSource>();
	private Map<String, DataAccount> myDataAccountLookup = new HashMap<String, DataAccount>();

	private int linkedAccountsCount = -1;
	private boolean linkedAccountsFetched;

	private List<DataAccount> rows = new ArrayList<DataAccount>();
	private Pager pager = null; // THE LINKED ACCOUNTS PAGE IS LIMITLESS
	private Request currentLinkedAccountItem;

	private static LinkedAccountController mOne = null;

	public static LinkedAccountController get() {
		if (mOne == null) {
			mOne = new LinkedAccountController();
		}
		return mOne;
	}

	public void fetchLinkedAccounts() {
		rows.clear();
		CoreService service = ServiceCreator.createCoreService();

		final GetLinkedAccountsRequest input = new GetLinkedAccountsRequest();
		input.accessCode = ACCESS_CODE;
		input.session = SessionController.get().getSessionForApiCall();

		if (pager == null) {
			pager = new Pager();
			pager.count = Long.valueOf(Integer.MAX_VALUE);
			pager.start = Long.valueOf(0);
			pager.sortDirection = SortDirectionType.SortDirectionTypeDescending;
		}
		input.pager = pager;

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
					}

					linkedAccountsFetched = true;

					linkedAccountsCount = rows.size();
					updateRowCount(linkedAccountsCount, true);
					updateRowData(0, rows);

				}

				DefaultEventBus.get().fireEventFromSource(new GetLinkedAccountsEventHandler.GetLinkedAccountsSuccess(input, output),
						LinkedAccountController.this);
			}

			@Override
			public void onFailure(Throwable caught) {
				linkedAccountsCount = -1;
				DefaultEventBus.get().fireEventFromSource(new GetLinkedAccountsEventHandler.GetLinkedAccountsFailure(input, caught),
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
	public void linkAccount(Long sourceId, String username, String password) {
		CoreService service = ServiceCreator.createCoreService();

		final LinkAccountRequest input = new LinkAccountRequest();
		input.accessCode = ACCESS_CODE;

		input.session = SessionController.get().getSessionForApiCall();

		input.username = username;
		input.password = password;

		DataSource source = new DataSource();
		source.id = sourceId;

		input.source = source;

		if (pager == null) { // Link account after request invite registration
			pager = new Pager();
			pager.count = Long.valueOf(Integer.MAX_VALUE);
			pager.start = Long.valueOf(0);
			pager.sortDirection = SortDirectionType.SortDirectionTypeDescending;
			pager.totalCount = Long.valueOf(0);
		}

		service.linkAccount(input, new AsyncCallback<LinkAccountResponse>() {

			@Override
			public void onSuccess(LinkAccountResponse output) {
				if (output.status == StatusType.StatusTypeSuccess) {
					if (output.linkedAccounts != null) {
						for (DataAccount addedAccount : output.linkedAccounts) {
							if (!getAllLinkedAccountIds().contains(addedAccount.id.toString())) { // Avoid to add duplicated
								rows.add(addedAccount);
								addLinkedAccountsToLookup(Arrays.asList(addedAccount));
								addDataSourceToLookup(Arrays.asList(addedAccount.source));
								// pager.totalCount = Long.valueOf(pager.totalCount.longValue() + 1);
							}
						}
						linkedAccountsCount = rows.size();
						updateRowCount(linkedAccountsCount, true);
						updateRowData(0, rows);
						SessionController.get().fetchRoleAndPermissions(); // Load HLA Permission
					}
					MixpanelHelper.track(MixpanelHelper.Event.LINK_ACCOUNT_SUCCESS);
				} else {
					Map<String, Object> properties = new HashMap<String, Object>();
					if (output.error != null && output.error.message != null) {
						properties.put("error_server", output.error.message);
					} else {
						properties.put("error_server", "generic error");
					}
					MixpanelHelper.track(MixpanelHelper.Event.LINK_ACCOUNT_FAILURE, properties);
				}
				DefaultEventBus.get().fireEventFromSource(new LinkAccountSuccess(input, output), LinkedAccountController.this);
			}

			@Override
			public void onFailure(Throwable caught) {
				Map<String, Object> properties = new HashMap<String, Object>();
				properties.put("error_server", caught.getMessage());
				MixpanelHelper.track(MixpanelHelper.Event.LINK_ACCOUNT_FAILURE, properties);
				DefaultEventBus.get().fireEventFromSource(new LinkAccountFailure(input, caught), LinkedAccountController.this);
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
	public void updateLinkedAccont(Long linkedAccountId, String username, String password) {
		CoreService service = ServiceCreator.createCoreService();
		final UpdateLinkedAccountRequest input = new UpdateLinkedAccountRequest();

		input.accessCode = ACCESS_CODE;

		input.session = SessionController.get().getSessionForApiCall();

		input.linkedAccount = new DataAccount();
		input.linkedAccount.id = linkedAccountId;
		input.linkedAccount.username = username;
		input.linkedAccount.password = password;

		// input.linkedAccount.source = new DataSource();
		// input.linkedAccount.source.a3Code = dataSourceLookup.get(dataAccountLookup.get(linkedAccountId.toString()).source.id.toString()).a3Code;

		service.updateLinkedAccount(input, new AsyncCallback<UpdateLinkedAccountResponse>() {

			@Override
			public void onSuccess(UpdateLinkedAccountResponse output) {
				if (output.status == StatusType.StatusTypeSuccess) {
					updateLinkedAccountLookup(input.linkedAccount.id, input.linkedAccount.username, input.linkedAccount.password);
				}

				DefaultEventBus.get().fireEventFromSource(new UpdateLinkedAccountEventHandler.UpdateLinkedAccountSuccess(input, output),
						LinkedAccountController.this);
			}

			@Override
			public void onFailure(Throwable caught) {
				DefaultEventBus.get().fireEventFromSource(new UpdateLinkedAccountEventHandler.UpdateLinkedAccountFailure(input, caught),
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
					rows.remove(myDataAccountLookup.get(input.linkedAccount.id.toString()));
					deleteLinkedAccountLookup(input.linkedAccount.id);
					// pager.totalCount = Long.valueOf(pager.totalCount.longValue() - 1);
					// pager.start = Long.valueOf(0);
					// Delete HLA Permission if there are no more Linked Accounts
					linkedAccountsCount = rows.size();
					updateRowCount(linkedAccountsCount, true);
					updateRowData(0, rows);
					SessionController.get().fetchRoleAndPermissions();
				}
				DefaultEventBus.get().fireEventFromSource(new DeleteLinkedAccountSuccess(input, output), LinkedAccountController.this);
			}

			@Override
			public void onFailure(Throwable caught) {
				DefaultEventBus.get().fireEventFromSource(new DeleteLinkedAccountFailure(input, caught), LinkedAccountController.this);
			}
		});

	}

	/**
	 * 
	 * @return
	 */
	public long getLinkedAccountsCount() {
		return linkedAccountsCount;
	}

	/**
	 * Get all linked accounts
	 * 
	 * @return
	 */
	public List<DataAccount> getAllLinkedAccounts() {
		return myDataAccounts;
	}

	/**
	 * Return true if Linked Accounts already fetched
	 * 
	 * @return
	 */
	public boolean linkedAccountsFetched() {
		return linkedAccountsFetched;
	}

	/**
	 * Return true if there is at least 1 linked account
	 * 
	 * @return
	 */
	public boolean hasLinkedAccounts() {
		return getLinkedAccountsCount() > 0;
	}

	/**
	 * Get linked account
	 * 
	 * @param linkedAccountId
	 * @return
	 */
	public DataAccount getLinkedAccount(Long linkedAccountId) {
		return myDataAccountLookup.get(linkedAccountId.toString());
	}

	public boolean hasLinkedAccount(String username) {
		boolean hasLinkedAccount = false;
		if (getAllLinkedAccounts() != null) {
			for (DataAccount ds : getAllLinkedAccounts()) {
				if (ds.username.equalsIgnoreCase(username)) {
					hasLinkedAccount = true;
				}
			}
		}
		return hasLinkedAccount;
	}

	/**
	 * Get data source
	 * 
	 * @param dataSourceId
	 * @return
	 */
	public DataSource getDataSource(Long dataSourceId) {
		return myDataSourceLookup.get(dataSourceId.toString());
	}

	/**
	 * Add linked account into lookup
	 * 
	 * @param linkedAccounts
	 */
	private void addLinkedAccountsToLookup(List<DataAccount> linkedAccounts) {
		if (myDataAccounts == null) {
			myDataAccounts = new ArrayList<DataAccount>();
		}
		for (DataAccount dataAccount : linkedAccounts) {
			myDataAccountLookup.put(dataAccount.id.toString(), dataAccount);
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
	private void updateLinkedAccountLookup(Long linkedAccountId, String username, String password) {
		myDataAccounts.get(myDataAccounts.indexOf(myDataAccountLookup.get(linkedAccountId.toString()))).username = username;
		myDataAccounts.get(myDataAccounts.indexOf(myDataAccountLookup.get(linkedAccountId.toString()))).password = password;
		myDataAccountLookup.get(linkedAccountId.toString()).username = username;
		myDataAccountLookup.get(linkedAccountId.toString()).password = password;
	}

	/**
	 * Delete linked account from lookup
	 * 
	 * @param linkedAccountId
	 */
	private void deleteLinkedAccountLookup(Long linkedAccountId) {
		myDataAccounts.remove(myDataAccountLookup.get(linkedAccountId.toString()));
		myDataAccountLookup.remove(linkedAccountId.toString());
	}

	/**
	 * 
	 * @param dataSources
	 */
	private void addDataSourceToLookup(List<DataSource> dataSources) {
		if (myDataSources == null) {
			myDataSources = new ArrayList<DataSource>();
		}
		for (DataSource dataSource : dataSources) {
			myDataSourceLookup.put(dataSource.id.toString(), dataSource);
			myDataSources.add(dataSource);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.view.client.AbstractDataProvider#onRangeChanged(com.google.gwt.view.client.HasData)
	 */
	@Override
	protected void onRangeChanged(HasData<DataAccount> display) {
		// Range r = display.getVisibleRange();
		// int start = r.getStart();
		// int end = start + r.getLength();
		if (!linkedAccountsFetched()) {
			fetchLinkedAccounts();
		} else {
			updateRowData(0, rows);
		}

	}

	public void reset() {
		myDataAccounts = null;
		myDataSources = null;

		myDataAccountLookup.clear();
		myDataSourceLookup.clear();

		pager = null;
		linkedAccountsCount = -1;
		rows.clear();
		linkedAccountsFetched = false;
		updateRowData(0, rows);
		updateRowCount(0, false);

	}

	public Item getLinkedAccountItem(Item item) {
		Item lookupItem = null;

		if (item != null && item.internalId != null) {
			if ((lookupItem = ItemController.get().getUserItem(item.internalId)) == null) {
				fetchLinkedAccountItem(item.internalId);
			}

		}

		return lookupItem;
	}

	public void fetchLinkedAccountItem(String itemInternalId) {
		if (currentLinkedAccountItem != null) {
			currentLinkedAccountItem.cancel();
			currentLinkedAccountItem = null;
		}

		CoreService service = ServiceCreator.createCoreService();

		final GetLinkedAccountItemRequest input = new GetLinkedAccountItemRequest();
		input.accessCode = ACCESS_CODE;
		input.session = SessionController.get().getSessionForApiCall();

		input.item = new Item();
		input.item.internalId = itemInternalId;
		input.item.source = FilterController.get().getStore().a3Code;

		currentLinkedAccountItem = service.getLinkedAccountItem(input, new AsyncCallback<GetLinkedAccountItemResponse>() {

			@Override
			public void onSuccess(GetLinkedAccountItemResponse output) {
				currentLinkedAccountItem = null;
				if (output.status == StatusType.StatusTypeSuccess && output.item != null) {
					ItemController.get().setUserItem(output.item);
				}

				DefaultEventBus.get().fireEventFromSource(new GetLinkedAccountItemEventHandler.GetLinkedAccountItemSuccess(input, output),
						LinkedAccountController.this);
			}

			@Override
			public void onFailure(Throwable caught) {
				currentLinkedAccountItem = null;
				DefaultEventBus.get().fireEventFromSource(new GetLinkedAccountItemEventHandler.GetLinkedAccountItemFailure(input, caught),
						LinkedAccountController.this);
			}
		});
	}

	public List<String> getAllLinkedAccountIds() {
		List<String> ids = new ArrayList<String>();
		ids.addAll(myDataAccountLookup.keySet());
		return ids;
	}
}
