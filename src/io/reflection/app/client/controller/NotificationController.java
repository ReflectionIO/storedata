//
//  NotificationController.java
//  storedata
//
//  Created by William Shakour (billy1380) on 10 Dec 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.controller;

import io.reflection.app.api.core.client.CoreService;
import io.reflection.app.api.core.shared.call.GetNotificationsRequest;
import io.reflection.app.api.core.shared.call.GetNotificationsResponse;
import io.reflection.app.api.core.shared.call.event.GetNotificationsEventHandler.GetNotificationsFailure;
import io.reflection.app.api.core.shared.call.event.GetNotificationsEventHandler.GetNotificationsSuccess;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.api.shared.datatypes.SortDirectionType;
import io.reflection.app.client.DefaultEventBus;
import io.reflection.app.datatypes.shared.Notification;

import java.util.Collections;

import com.google.gwt.http.client.Request;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author William Shakour (billy1380)
 *
 */
public class NotificationController extends AsyncDataProvider<Notification> implements ServiceConstants {

	private static NotificationController one = null;

	public static NotificationController get() {
		if (one == null) {
			one = new NotificationController();
		}

		return one;
	}

	private static final String SORT_BY_CREATED = "created";

	private Pager pager;
	private Request current;

	private void fetchNotifications() {
		if (current != null) {
			current.cancel();
			current = null;
		}

		CoreService service = ServiceCreator.createCoreService();

		final GetNotificationsRequest input = new GetNotificationsRequest();
		input.accessCode = ACCESS_CODE;

		input.session = SessionController.get().getSessionForApiCall();

		if (pager == null) {
			pager = new Pager().start(Pager.DEFAULT_START).count(STEP).sortBy(SORT_BY_CREATED).sortDirection(SortDirectionType.SortDirectionTypeDescending);
		}

		input.pager = pager;

		current = service.getNotifications(input, new AsyncCallback<GetNotificationsResponse>() {
			@Override
			public void onSuccess(GetNotificationsResponse output) {
				current = null;

				if (output.status == StatusType.StatusTypeSuccess) {
					if (output.pager != null) {
						pager = output.pager;
					}

					updateRowCount(output.notifications == null ? 0 : input.pager.start.intValue() + output.notifications.size(), output.notifications == null);
					updateRowData(input.pager.start.intValue(), output.notifications == null ? Collections.<Notification> emptyList() : output.notifications);
				}

				DefaultEventBus.get().fireEventFromSource(new GetNotificationsSuccess(input, output), NotificationController.this);
			}

			@Override
			public void onFailure(Throwable caught) {
				current = null;

				DefaultEventBus.get().fireEventFromSource(new GetNotificationsFailure(input, caught), NotificationController.this);
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.view.client.AbstractDataProvider#onRangeChanged(com.google.gwt.view.client.HasData)
	 */
	@Override
	protected void onRangeChanged(HasData<Notification> display) {
		Range r = display.getVisibleRange();

		pager = new Pager().start(Long.valueOf(r.getStart())).count(Long.valueOf(r.getLength())).sortBy(SORT_BY_CREATED)
				.sortDirection(SortDirectionType.SortDirectionTypeDescending);

		fetchNotifications();
	}

}
