//  
//  GetTopicEventHandler.java
//  reflection.io
//
//  Created by William Shakour on May 4, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.api.forum.shared.call.event;

import io.reflection.app.api.forum.shared.call.GetTopicRequest;
import io.reflection.app.api.forum.shared.call.GetTopicResponse;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface GetTopicEventHandler extends EventHandler {
	public static final GwtEvent.Type<GetTopicEventHandler> TYPE = new GwtEvent.Type<GetTopicEventHandler>();

	public void getTopicSuccess(final GetTopicRequest input, final GetTopicResponse output);

	public void getTopicFailure(final GetTopicRequest input, final Throwable caught);

	public class GetTopicSuccess extends GwtEvent<GetTopicEventHandler> {
		private GetTopicRequest input;
		private GetTopicResponse output;

		public GetTopicSuccess(final GetTopicRequest input, final GetTopicResponse output) {
			this.input = input;
			this.output = output;
		}

		@Override
		public GwtEvent.Type<GetTopicEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(GetTopicEventHandler handler) {
			handler.getTopicSuccess(input, output);
		}
	}

	public class GetTopicFailure extends GwtEvent<GetTopicEventHandler> {
		private GetTopicRequest input;
		private Throwable caught;

		public GetTopicFailure(final GetTopicRequest input, final Throwable caught) {
			this.input = input;
			this.caught = caught;
		}

		@Override
		public GwtEvent.Type<GetTopicEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(GetTopicEventHandler handler) {
			handler.getTopicFailure(input, caught);
		}
	}

}