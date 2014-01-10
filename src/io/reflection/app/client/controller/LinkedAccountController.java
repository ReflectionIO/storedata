//
//  LinkedAccountController.java
//  storedata
//
//  Created by William Shakour (billy1380) on 27 Dec 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.controller;

import io.reflection.app.api.core.client.CoreService;
import io.reflection.app.api.core.shared.call.GetLinkedAccountsRequest;
import io.reflection.app.api.core.shared.call.GetLinkedAccountsResponse;
import io.reflection.app.api.core.shared.call.LinkAccountRequest;
import io.reflection.app.api.core.shared.call.LinkAccountResponse;
import io.reflection.app.api.core.shared.call.event.GetLinkedAccountsEventHandler;
import io.reflection.app.api.core.shared.call.event.LinkAccountEventHandler.LinkAccountFailure;
import io.reflection.app.api.core.shared.call.event.LinkAccountEventHandler.LinkAccountSuccess;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.api.shared.datatypes.SortDirectionType;
import io.reflection.app.datatypes.shared.DataAccount;
import io.reflection.app.datatypes.shared.DataSource;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author billy1380
 * 
 */
public class LinkedAccountController implements ServiceController {

	private List<DataAccount> mLinkedAccounts;
	private long mCount = -1;
	private Pager mPager;

	private static LinkedAccountController mOne = null;

	public static LinkedAccountController get() {
		if (mOne == null) {
			mOne = new LinkedAccountController();
		}

		return mOne;
	}

	public void linkAccount(Long sourceId, String username, String password, String properties) {
		CoreService service = new CoreService();
		service.setUrl(CORE_END_POINT);

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
				EventController.get().fireEventFromSource(new LinkAccountSuccess(input, output), LinkedAccountController.this);
			}

			@Override
			public void onFailure(Throwable caught) {
				EventController.get().fireEventFromSource(new LinkAccountFailure(input, caught), LinkedAccountController.this);
			}
		});

	}

	/**
	 * 
	 */
	public List<DataAccount> getLinkedAccounts() {
		return mLinkedAccounts;
	}

	public long getLinkedAccountsCount() {
		return mCount;
	}

	public void fetchLinkedAccounts() {
		CoreService service = new CoreService();
		service.setUrl(CORE_END_POINT);

		final GetLinkedAccountsRequest input = new GetLinkedAccountsRequest();
		input.accessCode = ACCESS_CODE;

		input.session = SessionController.get().getSessionForApiCall();

		if (mPager == null) {
			mPager = new Pager();
			mPager.count = STEP;
			mPager.start = Long.valueOf(0);
			mPager.sortDirection = SortDirectionType.SortDirectionTypeDescending;
		}
		input.pager = mPager;

		service.getLinkedAccounts(input, new AsyncCallback<GetLinkedAccountsResponse>() {

			@Override
			public void onSuccess(GetLinkedAccountsResponse output) {
				if (output.status == StatusType.StatusTypeSuccess) {
					if (output.linkedAccounts != null) {
						mLinkedAccounts.addAll(output.linkedAccounts);
					}

					if (output.pager != null) {
						mPager = output.pager;

						if (mPager.totalCount != null) {
							mCount = mPager.totalCount.longValue();
						}
					}

//					updateRowCount((int) mCount, true);
//					updateRowData(
//							input.pager.start.intValue(),
//							mLinkedAccounts.subList(input.pager.start.intValue(),
//									Math.min(input.pager.start.intValue() + input.pager.count.intValue(), mPager.totalCount.intValue())));
				}

				EventController.get().fireEventFromSource(new GetLinkedAccountsEventHandler.GetLinkedAccountsSuccess(input, output), LinkedAccountController.this);
			}

			@Override
			public void onFailure(Throwable caught) {

			}
		});
	}

	public boolean hasLinkedAccounts() {
		return mPager != null || mLinkedAccounts.size() > 0;
	}
}
