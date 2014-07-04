//  
//  GetReplyEventHandler.java
//  reflection.io
//
//  Created by William Shakour on July 3, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.api.forum.shared.call.event;

import io.reflection.app.api.forum.shared.call.GetReplyRequest;
import io.reflection.app.api.forum.shared.call.GetReplyResponse;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface GetReplyEventHandler extends EventHandler {
	public static final GwtEvent.Type<GetReplyEventHandler> TYPE = new GwtEvent.Type<GetReplyEventHandler>();

	public void getReplySuccess(final GetReplyRequest input, final GetReplyResponse output);

	public void getReplyFailure(final GetReplyRequest input, final Throwable caught);

	public class GetReplySuccess extends GwtEvent<GetReplyEventHandler> {
		private GetReplyRequest input;
		private GetReplyResponse output;

		public GetReplySuccess(final GetReplyRequest input, final GetReplyResponse output) {
			this.input = input;
			this.output = output;
		}

		@Override
		public GwtEvent.Type<GetReplyEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(GetReplyEventHandler handler) {
			handler.getReplySuccess(input, output);
		}
	}

	public class GetReplyFailure extends GwtEvent<GetReplyEventHandler> {
		private GetReplyRequest input;
		private Throwable caught;

		public GetReplyFailure(final GetReplyRequest input, final Throwable caught) {
			this.input = input;
			this.caught = caught;
		}

		@Override
		public GwtEvent.Type<GetReplyEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(GetReplyEventHandler handler) {
			handler.getReplyFailure(input, caught);
		}
	}

}