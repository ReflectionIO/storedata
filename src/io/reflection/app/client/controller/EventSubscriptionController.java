//
//  EventSubscriptionController.java
//  storedata
//
//  Created by William Shakour (billy1380) on 22 Dec 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.controller;

import io.reflection.app.api.admin.client.AdminService;
import io.reflection.app.api.admin.shared.call.GetEventSubscriptionsRequest;
import io.reflection.app.api.admin.shared.call.GetEventSubscriptionsResponse;
import io.reflection.app.api.admin.shared.call.event.GetEventSubscriptionsEventHandler.GetEventSubscriptionsFailure;
import io.reflection.app.api.admin.shared.call.event.GetEventSubscriptionsEventHandler.GetEventSubscriptionsSuccess;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.api.shared.datatypes.SortDirectionType;
import io.reflection.app.client.DefaultEventBus;
import io.reflection.app.datatypes.shared.Event;
import io.reflection.app.datatypes.shared.EventSubscription;
import io.reflection.app.datatypes.shared.User;
import io.reflection.app.shared.util.PagerHelper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
public class EventSubscriptionController extends AsyncDataProvider<EventSubscription> implements ServiceConstants {

	private Pager pager;
	private Request current;
	String searchQuery;

	private static EventSubscriptionController one = null;

	public static EventSubscriptionController get() {
		if (one == null) {
			one = new EventSubscriptionController();
		}

		return one;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.view.client.AbstractDataProvider#onRangeChanged(com.google.gwt.view.client.HasData)
	 */
	@Override
	protected void onRangeChanged(HasData<EventSubscription> display) {
		Range r = display.getVisibleRange();

		pager = PagerHelper.createDefaultPager();
		pager.start = Long.valueOf(r.getStart());
		pager.count = Long.valueOf(r.getLength());

		fetchEventSubscriptions(searchQuery);
	}

	public void reset() {
		pager = null;
		searchQuery = null;

		updateRowCount(0, false);
	}

	private void fetchEventSubscriptions(String query) {
		if (current != null) {
			current.cancel();
			current = null;
		}

		AdminService service = ServiceCreator.createAdminService();

		final GetEventSubscriptionsRequest input = new GetEventSubscriptionsRequest();
		input.accessCode = ACCESS_CODE;

		input.session = SessionController.get().getSessionForApiCall();

		if (pager == null) {
			pager = new Pager().count(STEP).start(Long.valueOf(0)).sortDirection(SortDirectionType.SortDirectionTypeDescending);
		}

		input.pager = pager;
		input.query = searchQuery = query;

		if ("" == input.query) {
			input.query = searchQuery = null;
		}

		current = service.getEventSubscriptions(input, new AsyncCallback<GetEventSubscriptionsResponse>() {
			@Override
			public void onSuccess(GetEventSubscriptionsResponse output) {
				current = null;

				if (output.status == StatusType.StatusTypeSuccess) {
					if (output.eventSubscriptions != null) {}

					if (output.pager != null) {
						pager = output.pager;
					}

					updateRowCount(output.eventSubscriptions == null ? 0 : input.pager.start.intValue() + output.eventSubscriptions.size(),
							output.eventSubscriptions == null);
					updateRowData(input.pager.start.intValue(), output.eventSubscriptions == null ? Collections.<EventSubscription> emptyList()
							: output.eventSubscriptions);
				}

				DefaultEventBus.get().fireEventFromSource(new GetEventSubscriptionsSuccess(input, output), EventSubscriptionController.this);
			}

			@Override
			public void onFailure(Throwable caught) {
				current = null;

				DefaultEventBus.get().fireEventFromSource(new GetEventSubscriptionsFailure(input, caught), EventSubscriptionController.this);
			}
		});
	}

	public void addEventSubscription(Event event, User user, User evesDroppingOn) {

	}

	public void deleteSubscriptions(EventSubscription... subscription) {
		deleteSubscriptions(Arrays.asList(subscription));
	}

	public void deleteSubscriptions(List<EventSubscription> subscriptions) {

	}

}