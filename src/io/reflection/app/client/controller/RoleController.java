//
//  RoleController.java
//  storedata
//
//  Created by William Shakour (billy1380) on 4 Dec 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.controller;

import io.reflection.app.api.admin.client.AdminService;
import io.reflection.app.api.admin.shared.call.GetRolesRequest;
import io.reflection.app.api.admin.shared.call.GetRolesResponse;
import io.reflection.app.api.admin.shared.call.event.GetRolesEventHandler.GetRolesFailure;
import io.reflection.app.api.admin.shared.call.event.GetRolesEventHandler.GetRolesSuccess;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.api.shared.datatypes.SortDirectionType;
import io.reflection.app.datatypes.shared.Role;

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
public class RoleController extends AsyncDataProvider<Role> implements ServiceConstants {

	private List<Role> mRoles = new ArrayList<Role>();
	private long mCount = -1;
	private Pager mPager;

	private static RoleController mOne = null;

	public static RoleController get() {
		if (mOne == null) {
			mOne = new RoleController();
		}

		return mOne;
	}

	public void fetchRoles() {

		AdminService service = ServiceCreator.createAdminService();

		final GetRolesRequest input = new GetRolesRequest();
		input.accessCode = ACCESS_CODE;

		input.session = SessionController.get().getSessionForApiCall();

		if (mPager == null) {
			mPager = new Pager();
			mPager.count = STEP;
			mPager.start = Long.valueOf(0);
			mPager.sortDirection = SortDirectionType.SortDirectionTypeDescending;
		}
		input.pager = mPager;

		service.getRoles(input, new AsyncCallback<GetRolesResponse>() {

			@Override
			public void onSuccess(GetRolesResponse output) {
				if (output.status == StatusType.StatusTypeSuccess) {
					if (output.roles != null) {
						mRoles.addAll(output.roles);
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
							mRoles.subList(input.pager.start.intValue(),
									Math.min(input.pager.start.intValue() + input.pager.count.intValue(), mPager.totalCount.intValue())));
				}

				EventController.get().fireEventFromSource(new GetRolesSuccess(input, output), RoleController.this);
			}

			@Override
			public void onFailure(Throwable caught) {
				EventController.get().fireEventFromSource(new GetRolesFailure(input, caught), RoleController.this);
			}
		});
	}

	public List<Role> getRoles() {
		return mRoles;
	}

	public long getRolesCount() {
		return mCount;
	}

	public boolean hasRoles() {
		return mPager != null || mRoles.size() > 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.view.client.AbstractDataProvider#onRangeChanged(com.google.gwt.view.client.HasData)
	 */
	@Override
	protected void onRangeChanged(HasData<Role> display) {
		Range r = display.getVisibleRange();

		int start = r.getStart();
		int end = start + r.getLength();

		if (end > mRoles.size()) {
			fetchRoles();
		} else {
			updateRowData(start, mRoles.subList(start, end));
		}
	}

}
