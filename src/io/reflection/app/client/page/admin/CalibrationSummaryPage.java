//
//  CalibrationSummaryPage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 8 Mar 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page.admin;

import io.reflection.app.api.admin.shared.call.GetCalibrationSummaryRequest;
import io.reflection.app.api.admin.shared.call.GetCalibrationSummaryResponse;
import io.reflection.app.api.admin.shared.call.event.GetCalibrationSummaryEventHandler;
import io.reflection.app.client.DefaultEventBus;
import io.reflection.app.client.controller.CalibrationSummaryController;
import io.reflection.app.client.controller.CategoryController;
import io.reflection.app.client.controller.CountryController;
import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.controller.NavigationController.Stack;
import io.reflection.app.client.handler.NavigationEventHandler;
import io.reflection.app.client.page.Page;
import io.reflection.app.client.part.Breadcrumbs;
import io.reflection.app.datatypes.shared.CalibrationSummary;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author William Shakour (billy1380)
 *
 */
public class CalibrationSummaryPage extends Page implements NavigationEventHandler, GetCalibrationSummaryEventHandler {

	private static CalibrationSummaryPagePageUiBinder uiBinder = GWT.create(CalibrationSummaryPagePageUiBinder.class);

	interface CalibrationSummaryPagePageUiBinder extends UiBinder<Widget, CalibrationSummaryPage> {}

	private static final int FEED_FETCH_ID_PARAMETER_INDEX = 0;
	public static final String VIEW_ACTION_NAME = "view";

	@UiField Breadcrumbs breadcrumbs;
	private CalibrationSummary summary;

	public CalibrationSummaryPage() {
		initWidget(uiBinder.createAndBindUi(this));

		refreshBreadcrumbs();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.page.Page#onAttach()
	 */
	@Override
	protected void onAttach() {
		super.onAttach();

		register(DefaultEventBus.get().addHandlerToSource(GetCalibrationSummaryEventHandler.TYPE, CalibrationSummaryController.get(), this));
		register(DefaultEventBus.get().addHandlerToSource(NavigationEventHandler.TYPE, NavigationController.get(), this));
	}

	private void refreshBreadcrumbs() {
		breadcrumbs.clear();

		if (summary == null) {
			breadcrumbs.push("...");
		} else {
			breadcrumbs.push(summary.feedFetch.store, CountryController.get().getCountry(summary.feedFetch.country).name,
					CategoryController.get().getCategory(summary.feedFetch.category.id) == null ? summary.feedFetch.category.id.toString() : CategoryController
							.get().getCategory(summary.feedFetch.category.id).name, summary.feedFetch.type);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.NavigationEventHandler#navigationChanged(io.reflection.app.client.controller.NavigationController.Stack,
	 * io.reflection.app.client.controller.NavigationController.Stack)
	 */
	@Override
	public void navigationChanged(Stack previous, Stack current) {
		if (current.getAction() != null) {
			if (VIEW_ACTION_NAME.equals(current.getAction())) {
				String feedFetchParam = current.getParameter(FEED_FETCH_ID_PARAMETER_INDEX);

				if (feedFetchParam != null) {
					Long feedFetchId = Long.valueOf(feedFetchParam);

					// clear the summary
					summary = null;

					CalibrationSummary calibrationSummary = CalibrationSummaryController.get().getCalibrationSummary(feedFetchId);

					// this should never happen
					if (calibrationSummary != null) {
						show(calibrationSummary);
					}
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.reflection.app.api.admin.shared.call.event.GetCalibrationSummaryEventHandler#getCalibrationSummarySuccess(io.reflection.app.api.admin.shared.call.
	 * GetCalibrationSummaryRequest, io.reflection.app.api.admin.shared.call.GetCalibrationSummaryResponse)
	 */
	@Override
	public void getCalibrationSummarySuccess(GetCalibrationSummaryRequest input, GetCalibrationSummaryResponse output) {
		if (output == null || output.status == StatusType.StatusTypeFailure || output.calibrationSummary == null) {
			// show a message
		} else {
			show(output.calibrationSummary);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.reflection.app.api.admin.shared.call.event.GetCalibrationSummaryEventHandler#getCalibrationSummaryFailure(io.reflection.app.api.admin.shared.call.
	 * GetCalibrationSummaryRequest, java.lang.Throwable)
	 */
	@Override
	public void getCalibrationSummaryFailure(GetCalibrationSummaryRequest input, Throwable caught) {
		// show a message
	}

	private void show(CalibrationSummary summary) {
		this.summary = summary;

		refreshBreadcrumbs();
	}
}
