//  
//  GetModelOutcomeEventHandler.java
//  reflection.io
//
//  Created by William Shakour on December 27, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.api.admin.shared.call.event;

import io.reflection.app.api.admin.shared.call.GetModelOutcomeRequest;
import io.reflection.app.api.admin.shared.call.GetModelOutcomeResponse;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface GetModelOutcomeEventHandler extends EventHandler {
	public static final GwtEvent.Type<GetModelOutcomeEventHandler> TYPE = new GwtEvent.Type<GetModelOutcomeEventHandler>();

	public void getModelOutcomeSuccess(final GetModelOutcomeRequest input, final GetModelOutcomeResponse output);

	public void getModelOutcomeFailure(final GetModelOutcomeRequest input, final Throwable caught);

	public class GetModelOutcomeSuccess extends GwtEvent<GetModelOutcomeEventHandler> {
		private GetModelOutcomeRequest input;
		private GetModelOutcomeResponse output;

		public GetModelOutcomeSuccess(final GetModelOutcomeRequest input, final GetModelOutcomeResponse output) {
			this.input = input;
			this.output = output;
		}

		@Override
		public GwtEvent.Type<GetModelOutcomeEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(GetModelOutcomeEventHandler handler) {
			handler.getModelOutcomeSuccess(input, output);
		}
	}

	public class GetModelOutcomeFailure extends GwtEvent<GetModelOutcomeEventHandler> {
		private GetModelOutcomeRequest input;
		private Throwable caught;

		public GetModelOutcomeFailure(final GetModelOutcomeRequest input, final Throwable caught) {
			this.input = input;
			this.caught = caught;
		}

		@Override
		public GwtEvent.Type<GetModelOutcomeEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(GetModelOutcomeEventHandler handler) {
			handler.getModelOutcomeFailure(input, caught);
		}
	}

}