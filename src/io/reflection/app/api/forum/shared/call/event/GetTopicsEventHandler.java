//  
//  GetTopicsEventHandler.java
//  reflection.io
//
//  Created by William Shakour on April 5, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.api.forum.shared.call.event;

import io.reflection.app.api.forum.shared.call.GetTopicsRequest;
import io.reflection.app.api.forum.shared.call.GetTopicsResponse;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface GetTopicsEventHandler extends EventHandler {
	public static final GwtEvent.Type<GetTopicsEventHandler> TYPE = new GwtEvent.Type<GetTopicsEventHandler>();

	public void getTopicsSuccess(final GetTopicsRequest input, final GetTopicsResponse output);

	public void getTopicsFailure(final GetTopicsRequest input, final Throwable caught);

	public class GetTopicsSuccess extends GwtEvent<GetTopicsEventHandler> {
		private GetTopicsRequest input;
		private GetTopicsResponse output;

		public GetTopicsSuccess(final GetTopicsRequest input, final GetTopicsResponse output) {
			this.input = input;
			this.output = output;
		}

		@Override
		public GwtEvent.Type<GetTopicsEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(GetTopicsEventHandler handler) {
			handler.getTopicsSuccess(input, output);
		}
	}

	public class GetTopicsFailure extends GwtEvent<GetTopicsEventHandler> {
		private GetTopicsRequest input;
		private Throwable caught;

		public GetTopicsFailure(final GetTopicsRequest input, final Throwable caught) {
			this.input = input;
			this.caught = caught;
		}

		@Override
		public GwtEvent.Type<GetTopicsEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(GetTopicsEventHandler handler) {
			handler.getTopicsFailure(input, caught);
		}
	}

}