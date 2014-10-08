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
import io.reflection.app.api.admin.shared.call.UpdateEmailTemplateRequest;
import io.reflection.app.api.admin.shared.call.UpdateEmailTemplateResponse;
import io.reflection.app.api.admin.shared.call.event.GetEmailTemplatesEventHandler.GetEmailTemplatesFailure;
import io.reflection.app.api.admin.shared.call.event.GetEmailTemplatesEventHandler.GetEmailTemplatesSuccess;
import io.reflection.app.api.admin.shared.call.event.UpdateEmailTemplateEventHandler;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.api.shared.datatypes.SortDirectionType;
import io.reflection.app.datatypes.shared.EmailTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author billy1380
 * 
 */
public class EmailTemplateController extends AsyncDataProvider<EmailTemplate> implements ServiceConstants {

	private List<EmailTemplate> mEmailTemplates = new ArrayList<EmailTemplate>();
	private Map<Long, EmailTemplate> emailTemplateLookup = new HashMap<Long, EmailTemplate>();
	private long count = -1;
	private Pager pager;

	private static EmailTemplateController mOne = null;

	public static EmailTemplateController get() {
		if (mOne == null) {
			mOne = new EmailTemplateController();
		}

		return mOne;
	}

	private void fetchEmailTemplates() {

		AdminService service = ServiceCreator.createAdminService();

		final GetEmailTemplatesRequest input = new GetEmailTemplatesRequest();
		input.accessCode = ACCESS_CODE;

		input.session = SessionController.get().getSessionForApiCall();

		if (pager == null) {
			pager = new Pager();
			pager.count = STEP;
			pager.start = Long.valueOf(0);
			pager.sortDirection = SortDirectionType.SortDirectionTypeDescending;
		}
		input.pager = pager;

		service.getEmailTemplates(input, new AsyncCallback<GetEmailTemplatesResponse>() {

			@Override
			public void onSuccess(GetEmailTemplatesResponse output) {
				if (output.status == StatusType.StatusTypeSuccess) {
					if (output.templates != null) {
						mEmailTemplates.addAll(output.templates);
						for (EmailTemplate template : output.templates) {
							emailTemplateLookup.put(template.id, template);
						}
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
							mEmailTemplates.subList(input.pager.start.intValue(),
									Math.min(input.pager.start.intValue() + input.pager.count.intValue(), pager.totalCount.intValue())));
				}

				EventController.get().fireEventFromSource(new GetEmailTemplatesSuccess(input, output), EmailTemplateController.this);
			}

			@Override
			public void onFailure(Throwable caught) {
				EventController.get().fireEventFromSource(new GetEmailTemplatesFailure(input, caught), EmailTemplateController.this);
			}
		});
	}

	/**
	 * Change the email template format and text
	 * 
	 * @param emailTemplateId
	 * @param format
	 * @param body
	 */
	public void updateEmailTemplate(EmailTemplate emailTemplate) {

		AdminService service = ServiceCreator.createAdminService();
		final UpdateEmailTemplateRequest input = new UpdateEmailTemplateRequest();

		input.accessCode = ACCESS_CODE;

		input.session = SessionController.get().getSessionForApiCall();

		input.emailTemplate = emailTemplate;

		service.updateEmailTemplate(input, new AsyncCallback<UpdateEmailTemplateResponse>() {

			@Override
			public void onSuccess(UpdateEmailTemplateResponse output) {
				if (output.status == StatusType.StatusTypeSuccess) {
					updateRowData(0, mEmailTemplates.subList(0, (mEmailTemplates.size() < STEP_VALUE ? mEmailTemplates.size() : STEP_VALUE)));
				}

				EventController.get().fireEventFromSource(new UpdateEmailTemplateEventHandler.UpdateEmailTemplateSuccess(input, output),
						EmailTemplateController.this);
			}

			@Override
			public void onFailure(Throwable caught) {
				EventController.get().fireEventFromSource(new UpdateEmailTemplateEventHandler.UpdateEmailTemplateFailure(input, caught),
						EmailTemplateController.this);
			}

		});
	}

	public List<EmailTemplate> getEmailTemplates() {
		return mEmailTemplates;
	}

	public long getEmailTemplatesCount() {
		return count;
	}

	public boolean hasEmailTemplates() {
		return getEmailTemplatesCount() > 0;
	}

	/**
	 * Return true if EmailTemplates already fetched
	 * 
	 * @return
	 */
	public boolean emailTemplatesFetched() {
		return count != -1;
	}

	/**
	 * Get email template
	 * 
	 * @param emailTeplateId
	 * @return
	 */
	public EmailTemplate getEmailTemplate(Long emailTemplateId) {
		return emailTemplateLookup.get(emailTemplateId);
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

		if (!emailTemplatesFetched() || (emailTemplatesFetched() && getEmailTemplatesCount() != mEmailTemplates.size() && end > mEmailTemplates.size())) {
			fetchEmailTemplates();
		} else {
			updateRowData(start, mEmailTemplates.size() == 0 ? mEmailTemplates : mEmailTemplates.subList(start, Math.min(mEmailTemplates.size(), end)));
		}
	}

}