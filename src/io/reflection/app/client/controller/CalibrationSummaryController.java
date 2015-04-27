//
//  CalibrationSummaryController.java
//  storedata
//
//  Created by William Shakour (billy1380) on 8 Mar 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.controller;

import io.reflection.app.api.admin.client.AdminService;
import io.reflection.app.api.admin.shared.call.GetCalibrationSummaryRequest;
import io.reflection.app.api.admin.shared.call.GetCalibrationSummaryResponse;
import io.reflection.app.api.admin.shared.call.event.GetCalibrationSummaryEventHandler.GetCalibrationSummaryFailure;
import io.reflection.app.api.admin.shared.call.event.GetCalibrationSummaryEventHandler.GetCalibrationSummarySuccess;
import io.reflection.app.client.DefaultEventBus;
import io.reflection.app.datatypes.shared.CalibrationSummary;
import io.reflection.app.datatypes.shared.FeedFetch;
import io.reflection.app.shared.util.DataTypeHelper;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author William Shakour (billy1380)
 *
 */
public class CalibrationSummaryController implements ServiceConstants {

	private static CalibrationSummaryController one = null;
	private FeedFetch feedFetch;

	public static CalibrationSummaryController get() {
		if (one == null) {
			one = new CalibrationSummaryController();
		}

		return one;
	}

	public void fetchCalibrationSummary() {
		AdminService service = ServiceCreator.createAdminService();

		final GetCalibrationSummaryRequest input = new GetCalibrationSummaryRequest();
		input.accessCode = ACCESS_CODE;
		input.session = SessionController.get().getSessionForApiCall();
		input.feedFetch = feedFetch;

		service.getCalibrationSummary(input, new AsyncCallback<GetCalibrationSummaryResponse>() {

			@Override
			public void onSuccess(GetCalibrationSummaryResponse output) {
				if (output != null && output.status == StatusType.StatusTypeSuccess) {
					if (output.items != null) {
						ItemController.get().addItemsToCache(output.items);
						CountryController.get().addCountryToCache(output.country);
						CategoryController.get().addCategoryToCache(output.category);

						// cannot do this because of the split in the ios store
						// StoreController.get().addStoreToCache(output.store);
					}
				}

				DefaultEventBus.get().fireEventFromSource(new GetCalibrationSummarySuccess(input, output), CalibrationSummaryController.this);
			}

			@Override
			public void onFailure(Throwable caught) {
				DefaultEventBus.get().fireEventFromSource(new GetCalibrationSummaryFailure(input, caught), CalibrationSummaryController.this);
			}
		});

	}

	public CalibrationSummary getCalibrationSummary(Long id) {
		if (id != null) {
			feedFetch = DataTypeHelper.createFeedFetch(id);

			fetchCalibrationSummary();
		}

		return null;
	}
}
