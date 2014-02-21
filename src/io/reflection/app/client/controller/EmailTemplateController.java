//
//  EmailTemplateController.java
//  storedata
//
//  Created by William Shakour (billy1380) on 18 Feb 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.controller;

import io.reflection.app.api.admin.client.AdminService;
import io.reflection.app.api.admin.shared.call.GetEmailTemplatesRequest;
import io.reflection.app.api.admin.shared.call.GetEmailTemplatesResponse;
import io.reflection.app.api.admin.shared.call.event.GetEmailTemplatesEventHandler.GetEmailTemplatesFailure;
import io.reflection.app.api.admin.shared.call.event.GetEmailTemplatesEventHandler.GetEmailTemplatesSuccess;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.api.shared.datatypes.SortDirectionType;
import io.reflection.app.datatypes.shared.EmailTemplate;

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
public class EmailTemplateController extends AsyncDataProvider<EmailTemplate> implements ServiceController {

	private List<EmailTemplate> mEmailTemplates = new ArrayList<EmailTemplate>();
	private long mCount = -1;
	private Pager mPager;

	private static EmailTemplateController mOne = null;

	public static EmailTemplateController get() {
		if (mOne == null) {
			mOne = new EmailTemplateController();
		}

		return mOne;
	}

	private void fetchEmailTemplates() {

		AdminService service = new AdminService();
		service.setUrl(ADMIN_END_POINT);

		final GetEmailTemplatesRequest input = new GetEmailTemplatesRequest();
		input.accessCode = ACCESS_CODE;

		input.session = SessionController.get().getSessionForApiCall();

		if (mPager == null) {
			mPager = new Pager();
			mPager.count = STEP;
			mPager.start = Long.valueOf(0);
			mPager.sortDirection = SortDirectionType.SortDirectionTypeDescending;
		}
		input.pager = mPager;

		service.getEmailTemplates(input, new AsyncCallback<GetEmailTemplatesResponse>() {

			@Override
			public void onSuccess(GetEmailTemplatesResponse output) {
				if (output.status == StatusType.StatusTypeSuccess) {
					if (output.templates != null) {
						mEmailTemplates.addAll(output.templates);
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
							mEmailTemplates.subList(input.pager.start.intValue(),
									Math.min(input.pager.start.intValue() + input.pager.count.intValue(), mPager.totalCount.intValue())));

					EventController.get().fireEventFromSource(new GetEmailTemplatesSuccess(input, output), EmailTemplateController.this);
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				EventController.get().fireEventFromSource(new GetEmailTemplatesFailure(input, caught), EmailTemplateController.this);
			}
		});
	}

	public List<EmailTemplate> getEmailTemplates() {
		return mEmailTemplates;
	}

	public long getEmailTemplatesCount() {
		return mCount;
	}

	public boolean hasEmailTemplates() {
		return mPager != null || mEmailTemplates.size() > 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.view.client.AbstractDataProvider#onRangeChanged(com.google.gwt.view.client.HasData)
	 */
	@Override
	protected void onRangeChanged(HasData<EmailTemplate> display) {
		Range r = display.getVisibleRange();

		int start = r.getStart();
		int end = start + r.getLength();

		if (end > mEmailTemplates.size()) {
			fetchEmailTemplates();
		} else {
			updateRowData(start, mEmailTemplates.subList(start, end));
		}
	}

}