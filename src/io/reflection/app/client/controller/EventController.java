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
import io.reflection.app.shared.util.PagerHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle.MultiWordSuggestion;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author billy1380
 * 
 */
public class EventController extends AsyncDataProvider<Event> implements ServiceConstants {

	interface OracleTemplates extends SafeHtmlTemplates {
		OracleTemplates INSTANCE = GWT.create(OracleTemplates.class);

		@Template("{0}: {1}")
		SafeHtml suggestionDescription(String code, String name);
	}

	private SuggestOracle.Request request;
	private SuggestOracle.Callback callback;
	private Oracle oracle;

	class Oracle extends MultiWordSuggestOracle {

		@Override
		public void requestSuggestions(SuggestOracle.Request request, final SuggestOracle.Callback callback) {
			EventController.this.reset();
			EventController.this.request = request;
			EventController.this.callback = callback;
			EventController.this.fetchEvents(request.getQuery());
		}
	}

	// private List<Event> mEvents = new ArrayList<Event>();
	private Map<Long, Event> eventLookup = new HashMap<Long, Event>();
	// private long count = -1;
	private Pager pager;
	private String searchQuery = null;
	private Request current;

	private static EventController one = null;

	public static EventController get() {
		if (one == null) {
			one = new EventController();
		}

		return one;
	}

	private void fetchEvents(String query) {
		if (current != null) {
			current.cancel();
			current = null;
		}

		AdminService service = ServiceCreator.createAdminService();

		final GetEventsRequest input = new GetEventsRequest();
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

		current = service.getEvents(input, new AsyncCallback<GetEventsResponse>() {
			@Override
			public void onSuccess(GetEventsResponse output) {
				current = null;

				if (output.status == StatusType.StatusTypeSuccess) {
					if (output.events != null) {
						// mEvents.addAll(output.events);
						for (Event event : output.events) {
							eventLookup.put(event.id, event);
						}
					}

					if (output.pager != null) {
						pager = output.pager;

						// if (pager.totalCount != null) {
						// count = pager.totalCount.longValue();
						// }
					}

					// send the oracle response if we have any
					if (output.events != null && oracle != null && EventController.this.request != null && EventController.this.callback != null) {
						SuggestOracle.Response response = new SuggestOracle.Response();
						List<MultiWordSuggestion> events = new ArrayList<MultiWordSuggestOracle.MultiWordSuggestion>();

						for (Event event : output.events) {
							String description = EventController.this.getOracleEventDescription(event);
							events.add(new MultiWordSuggestion(description, description));
						}

						response.setSuggestions(events);
						EventController.this.callback.onSuggestionsReady(EventController.this.request, response);
					}

					updateRowCount(output.events == null ? 0 : input.pager.start.intValue() + output.events.size(), output.events == null);
					updateRowData(input.pager.start.intValue(), output.events == null ? Collections.<Event> emptyList() : output.events);
				}

				DefaultEventBus.get().fireEventFromSource(new GetEventsSuccess(input, output), EventController.this);
			}

			@Override
			public void onFailure(Throwable caught) {
				current = null;

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
					// do nothing
				}

				DefaultEventBus.get().fireEventFromSource(new UpdateEventEventHandler.UpdateEventSuccess(input, output), EventController.this);
			}

			@Override
			public void onFailure(Throwable caught) {
				DefaultEventBus.get().fireEventFromSource(new UpdateEventEventHandler.UpdateEventFailure(input, caught), EventController.this);
			}

		});
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

		pager = PagerHelper.createDefaultPager();
		pager.start = Long.valueOf(r.getStart());
		pager.count = Long.valueOf(r.getLength());

		fetchEvents(null);
	}

	public void reset() {
		pager = null;
		// count = -1;
		searchQuery = null;
		request = null;
		callback = null;

		updateRowCount(0, false);
	}

	/**
	 * @return
	 */
	public Oracle oracle() {
		if (oracle == null) {
			oracle = new Oracle();
		}

		return oracle;
	}

	/**
	 * @return
	 */
	public String getQuery() {
		return searchQuery;
	}

	public String getOracleEventDescription(Event event) {
		return OracleTemplates.INSTANCE.suggestionDescription(event.code, event.name).asString();
	}

}