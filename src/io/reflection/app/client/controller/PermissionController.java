//
//  PermissionController.java
//  storedata
//
//  Created by William Shakour (billy1380) on 4 Dec 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.controller;

import io.reflection.app.api.admin.client.AdminService;
import io.reflection.app.api.admin.shared.call.GetPermissionsRequest;
import io.reflection.app.api.admin.shared.call.GetPermissionsResponse;
import io.reflection.app.api.admin.shared.call.event.GetPermissionsEventHandler.GetPermissionsFailure;
import io.reflection.app.api.admin.shared.call.event.GetPermissionsEventHandler.GetPermissionsSuccess;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.api.shared.datatypes.SortDirectionType;
import io.reflection.app.datatypes.shared.Permission;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author billy1380
 * 
 */
public class PermissionController extends AsyncDataProvider<Permission> implements ServiceConstants {

	public static final long FULL_RANK_VIEW_PERMISSION_ID = 1;
	public static final long HAS_LINKED_ACCOUNT_PERMISSION_ID = 20;
	public static final String HAS_LINKED_ACCOUNT_PERMISSION_CODE = "HLA";

	private List<Permission> mPermissions = new ArrayList<Permission>();
	private long mCount = -1;
	private Pager mPager;

	private static PermissionController mOne = null;

	public static PermissionController get() {
		if (mOne == null) {
			mOne = new PermissionController();
		}

		return mOne;
	}

	public void fetchPermissions() {

		AdminService service = ServiceCreator.createAdminService();

		final GetPermissionsRequest input = new GetPermissionsRequest();
		input.accessCode = ACCESS_CODE;

		input.session = SessionController.get().getSessionForApiCall();

		if (mPager == null) {
			mPager = new Pager();
			mPager.count = STEP;
			mPager.start = Long.valueOf(0);
			mPager.sortDirection = SortDirectionType.SortDirectionTypeDescending;
		}
		input.pager = mPager;

		service.getPermissions(input, new AsyncCallback<GetPermissionsResponse>() {

			@Override
			public void onSuccess(GetPermissionsResponse output) {
				if (output.status == StatusType.StatusTypeSuccess) {
					if (output.permissions != null) {
						mPermissions.addAll(output.permissions);
					}

					if (output.pager != null) {
						mPager = output.pager;

						if (mPager.totalCount != null) {
							mCount = mPager.totalCount.longValue();
						}
					}

					updateRowCount((int) mCount, true);
					updateRowData(
							input.pager.start.intValue(),
							mPermissions.subList(input.pager.start.intValue(),
									Math.min(input.pager.start.intValue() + input.pager.count.intValue(), mPager.totalCount.intValue())));
				}

				EventController.get().fireEventFromSource(new GetPermissionsSuccess(input, output), PermissionController.this);
			}

			@Override
			public void onFailure(Throwable caught) {
				EventController.get().fireEventFromSource(new GetPermissionsFailure(input, caught), PermissionController.this);
			}
		});
	}

	public List<Permission> getPermissions() {
		return mPermissions;
	}

	public long getPermissionsCount() {
		return mCount;
	}

	public boolean hasPermissions() {
		return mPager != null || mPermissions.size() > 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.view.client.AbstractDataProvider#onRangeChanged(com.google.gwt.view.client.HasData)
	 */
	@Override
	protected void onRangeChanged(HasData<Permission> display) {
		Range r = display.getVisibleRange();

		int start = r.getStart();
		int end = start + r.getLength();

		if (end > mPermissions.size()) {
			fetchPermissions();
		} else {
			updateRowData(start, mPermissions.subList(start, end));
		}
	}

}
