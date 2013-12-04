//
//  PermissionController.java
//  storedata
//
//  Created by William Shakour (billy1380) on 4 Dec 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.admin.client.controller;

import io.reflection.app.admin.client.handler.PermissionsEventHandler.ReceivedPermissions;
import io.reflection.app.api.admin.client.AdminService;
import io.reflection.app.api.admin.shared.call.GetPermissionsRequest;
import io.reflection.app.api.admin.shared.call.GetPermissionsResponse;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.api.shared.datatypes.SortDirectionType;
import io.reflection.app.shared.datatypes.Permission;

import java.util.ArrayList;
import java.util.List;

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
public class PermissionController extends AsyncDataProvider<Permission> implements ServiceController {

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

	private void fetchPermissions() {

		AdminService service = new AdminService();
		service.setUrl(ADMIN_END_POINT);

		final GetPermissionsRequest input = new GetPermissionsRequest();
		input.accessCode = ACCESS_CODE;

		if (mPager == null) {
			mPager = new Pager();
			mPager.count = STEP;
			mPager.start = Long.valueOf(0);
			mPager.sortDirection = SortDirectionType.SortDirectionTypeDescending;
		}
		input.pager = mPager;

		service.getPermissions(input, new AsyncCallback<GetPermissionsResponse>() {

			@Override
			public void onSuccess(GetPermissionsResponse result) {
				if (result.status == StatusType.StatusTypeSuccess) {
					if (result.permissions != null) {
						mPermissions.addAll(result.permissions);
					}

					if (result.pager != null) {
						mPager = result.pager;

						if (mPager.totalCount != null) {
							mCount = mPager.totalCount.longValue();
						}
					}

					updateRowCount((int) mCount, true);
					updateRowData(
							input.pager.start.intValue(),
							mPermissions.subList(input.pager.start.intValue(),
									Math.min(input.pager.start.intValue() + input.pager.count.intValue(), mPager.totalCount.intValue())));

					EventController.get().fireEventFromSource(new ReceivedPermissions(result.permissions), PermissionController.this);
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Error");
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
