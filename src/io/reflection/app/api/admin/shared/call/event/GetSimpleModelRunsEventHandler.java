//  
//  GetSimpleModelRunsEventHandler.java
//  reflection.io
//
//  Created by William Shakour on October 20, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.api.admin.shared.call.event;

import io.reflection.app.api.admin.shared.call.GetSimpleModelRunsRequest;
import io.reflection.app.api.admin.shared.call.GetSimpleModelRunsResponse;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface GetSimpleModelRunsEventHandler extends EventHandler {
	public static final GwtEvent.Type<GetSimpleModelRunsEventHandler> TYPE = new GwtEvent.Type<GetSimpleModelRunsEventHandler>();

	public void getSimpleModelRunsSuccess(final GetSimpleModelRunsRequest input, final GetSimpleModelRunsResponse output);

	public void getSimpleModelRunsFailure(final GetSimpleModelRunsRequest input, final Throwable caught);

	public class GetSimpleModelRunsSuccess extends GwtEvent<GetSimpleModelRunsEventHandler> {
		private GetSimpleModelRunsRequest input;
		private GetSimpleModelRunsResponse output;

		public GetSimpleModelRunsSuccess(final GetSimpleModelRunsRequest input, final GetSimpleModelRunsResponse output) {
			this.input = input;
			this.output = output;
		}

		@Override
		public GwtEvent.Type<GetSimpleModelRunsEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(GetSimpleModelRunsEventHandler handler) {
			handler.getSimpleModelRunsSuccess(input, output);
		}
	}

	public class GetSimpleModelRunsFailure extends GwtEvent<GetSimpleModelRunsEventHandler> {
		private GetSimpleModelRunsRequest input;
		private Throwable caught;

		public GetSimpleModelRunsFailure(final GetSimpleModelRunsRequest input, final Throwable caught) {
			this.input = input;
			this.caught = caught;
		}

		@Override
		public GwtEvent.Type<GetSimpleModelRunsEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(GetSimpleModelRunsEventHandler handler) {
			handler.getSimpleModelRunsFailure(input, caught);
		}
	}

}