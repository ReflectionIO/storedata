//  
//  GetCalibrationSummaryEventHandler.java
//  reflection.io
//
//  Created by William Shakour on March 8, 2015.
//  Copyrights © 2015 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2015 reflection.io. All rights reserved.
//
package io.reflection.app.api.admin.shared.call.event;

import io.reflection.app.api.admin.shared.call.GetCalibrationSummaryRequest;
import io.reflection.app.api.admin.shared.call.GetCalibrationSummaryResponse;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface GetCalibrationSummaryEventHandler extends EventHandler {
	public static final GwtEvent.Type<GetCalibrationSummaryEventHandler> TYPE = new GwtEvent.Type<GetCalibrationSummaryEventHandler>();

	public void getCalibrationSummarySuccess(final GetCalibrationSummaryRequest input, final GetCalibrationSummaryResponse output);

	public void getCalibrationSummaryFailure(final GetCalibrationSummaryRequest input, final Throwable caught);

	public class GetCalibrationSummarySuccess extends GwtEvent<GetCalibrationSummaryEventHandler> {
		private GetCalibrationSummaryRequest input;
		private GetCalibrationSummaryResponse output;

		public GetCalibrationSummarySuccess(final GetCalibrationSummaryRequest input, final GetCalibrationSummaryResponse output) {
			this.input = input;
			this.output = output;
		}

		@Override
		public GwtEvent.Type<GetCalibrationSummaryEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(GetCalibrationSummaryEventHandler handler) {
			handler.getCalibrationSummarySuccess(input, output);
		}
	}

	public class GetCalibrationSummaryFailure extends GwtEvent<GetCalibrationSummaryEventHandler> {
		private GetCalibrationSummaryRequest input;
		private Throwable caught;

		public GetCalibrationSummaryFailure(final GetCalibrationSummaryRequest input, final Throwable caught) {
			this.input = input;
			this.caught = caught;
		}

		@Override
		public GwtEvent.Type<GetCalibrationSummaryEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(GetCalibrationSummaryEventHandler handler) {
			handler.getCalibrationSummaryFailure(input, caught);
		}
	}

}