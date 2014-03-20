//
//  SuperAlertBox.java
//  storedata
//
//  Created by William Shakour (billy1380) on 17 Mar 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.part;

import io.reflection.app.client.controller.EventController;
import io.reflection.app.client.helper.AlertBoxHelper;
import io.reflection.app.client.part.AlertBox.AlertBoxType;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.willshex.gson.json.service.client.JsonService;
import com.willshex.gson.json.service.client.JsonServiceCallEventHandler;
import com.willshex.gson.json.service.shared.Request;
import com.willshex.gson.json.service.shared.Response;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author billy1380
 * 
 */
public class SuperAlertBox implements JsonServiceCallEventHandler, CloseHandler<AlertBox> {

	private static SuperAlertBox one = null;

	public static void start() {
		if (one == null) {
			one = new SuperAlertBox();
		}
	}

	private HTMLPanel container;

	List<Request> requests = new ArrayList<Request>();
	List<AlertBox> alertBoxes = new ArrayList<AlertBox>();
	List<HandlerRegistration> registrations = new ArrayList<HandlerRegistration>();

	private SuperAlertBox() {
		addContainer();

		EventController.get().addHandler(JsonServiceCallEventHandler.TYPE, this);
	}

	private void addContainer() {
		container = new HTMLPanel("<!-- super alert box -->");
		container.setStyleName("col-md-4");

		Style s = container.getElement().getStyle();
		s.setZIndex(99);
		s.setPosition(Position.ABSOLUTE);
		s.setTop(80.0, Unit.PX);

		RootPanel.get().add(container);
	}

	private AlertBox addAlertBox(Request request) {
		AlertBox alertBox = new AlertBox();

		requests.add(request);
		alertBoxes.add(alertBox);

		container.add(alertBox);

		return alertBox;
	}

	private void removeAlertBox(AlertBox alertBox) {
		alertBox.dismiss();
	}

	private AlertBox getAlertBox(Request request) {
		int i = requests.indexOf(request);

		AlertBox alertBox = null;
		if (i >= 0) {
			alertBox = alertBoxes.get(i);
		}

		return alertBox;
	}

	public void startCall(Request request, String callName, String endPoint) {
		AlertBox alertBox = addAlertBox(request);
		AlertBoxHelper.configureAlert(alertBox, AlertBoxType.InfoAlertBoxType, true, callName, endPoint, true);
		HandlerRegistration registration = alertBox.addCloseHandler(this);
		registrations.add(registration);
	}

	public void callSuccess(String callName, final Request request, Response response) {
		final AlertBox alertBox = getAlertBox(request);

		if (alertBox != null) {
			AlertBoxHelper.configureAlert(alertBox, AlertBoxType.SuccessAlertBoxType, false, callName, "Returned success!", true);

			Timer t = new Timer() {

				@Override
				public void run() {
					requests.remove(request);
					removeAlertBox(alertBox);
				}
			};

			t.schedule(2000);
		}
	}

	public void callFailure(String callName, Request request, Throwable caught) {
		AlertBox alertBox = getAlertBox(request);

		if (alertBox != null) {
			AlertBoxHelper.configureAlert(alertBox, AlertBoxType.DangerAlertBoxType, false, callName, "Failed with exception [" + caught.toString() + "]!",
					true);
		}
	}

	public void callFailure(String callName, Request request, Response response) {
		AlertBox alertBox = getAlertBox(request);

		if (alertBox != null) {
			AlertBoxHelper.configureAlert(alertBox, AlertBoxType.DangerAlertBoxType, false, callName,
					response.error == null ? "Failed with unknown error, no code or message!" : "Failed with code [" + response.error.code.toString()
							+ "] and message [" + response.error.message + "]!", true);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.willshex.gson.json.service.client.JsonServiceCallEventHandler#jsonServiceCallStart(com.willshex.gson.json.service.client.JsonService,
	 * java.lang.String, com.willshex.gson.json.service.shared.Request, com.google.gwt.http.client.Request)
	 */
	@Override
	public void jsonServiceCallStart(JsonService origin, String callName, Request input, com.google.gwt.http.client.Request handle) {
		startCall(input, callName, origin.getUrl());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.willshex.gson.json.service.client.JsonServiceCallEventHandler#jsonServiceCallSuccess(com.willshex.gson.json.service.client.JsonService,
	 * java.lang.String, com.willshex.gson.json.service.shared.Request, com.willshex.gson.json.service.shared.Response)
	 */
	@Override
	public void jsonServiceCallSuccess(JsonService origin, String callName, Request input, Response output) {
		if (output.status == StatusType.StatusTypeSuccess) {
			callSuccess(callName, input, output);
		} else {
			callFailure(callName, input, output);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.willshex.gson.json.service.client.JsonServiceCallEventHandler#jsonServiceCallFailure(com.willshex.gson.json.service.client.JsonService,
	 * java.lang.String, com.willshex.gson.json.service.shared.Request, java.lang.Throwable)
	 */
	@Override
	public void jsonServiceCallFailure(JsonService origin, String callName, Request input, Throwable caught) {
		callFailure(callName, input, caught);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.event.logical.shared.CloseHandler#onClose(com.google.gwt.event.logical.shared.CloseEvent)
	 */
	@Override
	public void onClose(CloseEvent<AlertBox> event) {
		int i = alertBoxes.indexOf(event.getTarget());

		if (i >= 0) {
			HandlerRegistration registration = registrations.remove(i);
			registration.removeHandler();

			alertBoxes.remove(i);

			container.remove(event.getTarget());
		}
	}

}
