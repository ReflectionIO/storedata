//
//  EventController.java
//  storedata
//
//  Created by William Shakour (billy1380) on 18 Feb 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.controller;

import io.reflection.app.api.admin.client.AdminService;
import io.reflection.app.api.admin.shared.call.GetEventsRequest;
import io.reflection.app.api.admin.shared.call.GetEventsResponse;
import io.reflection.app.api.admin.shared.call.UpdateEventRequest;
import io.reflection.app.api.admin.shared.call.UpdateEventResponse;
import io.reflection.app.api.admin.shared.call.event.GetEventsEventHandler.GetEventsFailure;
import io.reflection.app.api.admin.shared.call.event.GetEventsEventHandler.GetEventsSuccess;
import io.reflection.app.api.admin.shared.call.event.UpdateEventEventHandler;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.api.shared.datatypes.SortDirectionType;
import io.reflection.app.client.DefaultEventBus;
import io.reflection.app.datatypes.shared.Event;

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
public class EventController extends AsyncDataProvider<Event> implements ServiceConstants {

	private List<Event> mEvents = new ArrayList<Event>();
	private Map<Long, Event> eventLookup = new HashMap<Long, Event>();
	private long count = -1;
	private Pager pager;

	private static EventController mOne = null;

	public static EventController get() {
		if (mOne == null) {
			mOne = new EventController();
		}

		return mOne;
	}

	private void fetchEvents() {

		AdminService service = ServiceCreator.createAdminService();

		final GetEventsRequest input = new GetEventsRequest();
		input.accessCode = ACCESS_CODE;

		input.session = SessionController.get().getSessionForApiCall();

		if (pager == null) {
			pager = new Pager();
			pager.count = STEP;
			pager.start = Long.valueOf(0);
			pager.sortDirection = SortDirectionType.SortDirectionTypeDescending;
		}
		input.pager = pager;

		service.getEvents(input, new AsyncCallback<GetEventsResponse>() {

			@Override
			public void onSuccess(GetEventsResponse output) {
				if (output.status == StatusType.StatusTypeSuccess) {
					if (output.events != null) {
						mEvents.addAll(output.events);
						for (Event event : output.events) {
							eventLookup.put(event.id, event);
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
							mEvents.subList(input.pager.start.intValue(),
									Math.min(input.pager.start.intValue() + input.pager.count.intValue(), pager.totalCount.intValue())));
				}

				DefaultEventBus.get().fireEventFromSource(new GetEventsSuccess(input, output), EventController.this);
			}

			@Override
			public void onFailure(Throwable caught) {
				DefaultEventBus.get().fireEventFromSource(new GetEventsFailure(input, caught), EventController.this);
			}
		});
	}

	/**
	 * Change the email template format and text
	 * 
	 * @param eventId
	 * @param format
	 * @param body
	 */
	public void updateEvent(Event event) {

		AdminService service = ServiceCreator.createAdminService();
		final UpdateEventRequest input = new UpdateEventRequest();

		input.accessCode = ACCESS_CODE;

		input.session = SessionController.get().getSessionForApiCall();

		input.event = event;

		service.updateEvent(input, new AsyncCallback<UpdateEventResponse>() {

			@Override
			public void onSuccess(UpdateEventResponse output) {
				if (output.status == StatusType.StatusTypeSuccess) {
					updateRowData(0, mEvents.subList(0, (mEvents.size() < STEP_VALUE ? mEvents.size() : STEP_VALUE)));
				}

				DefaultEventBus.get().fireEventFromSource(new UpdateEventEventHandler.UpdateEventSuccess(input, output), EventController.this);
			}

			@Override
			public void onFailure(Throwable caught) {
				DefaultEventBus.get().fireEventFromSource(new UpdateEventEventHandler.UpdateEventFailure(input, caught), EventController.this);
			}

		});
	}

	public List<Event> getEvents() {
		return mEvents;
	}

	public long getEventsCount() {
		return count;
	}

	public boolean hasEvents() {
		return getEventsCount() > 0;
	}

	/**
	 * Return true if Events already fetched
	 * 
	 * @return
	 */
	public boolean eventsFetched() {
		return count != -1;
	}

	/**
	 * Get email template
	 * 
	 * @param emailTeplateId
	 * @return
	 */
	public Event getEvent(Long eventId) {
		return eventLookup.get(eventId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.view.client.AbstractDataProvider#onRangeChanged(com.google.gwt.view.client.HasData)
	 */
	@Override
	protected void onRangeChanged(HasData<Event> display) {
		Range r = display.getVisibleRange();

		int start = r.getStart();
		int end = start + r.getLength();

		if (!eventsFetched() || (eventsFetched() && getEventsCount() != mEvents.size() && end > mEvents.size())) {
			fetchEvents();
		} else {
			updateRowData(start, mEvents.size() == 0 ? mEvents : mEvents.subList(start, Math.min(mEvents.size(), end)));
		}
	}

}