//  
//  GetRepliesEventHandler.java
//  reflection.io
//
//  Created by William Shakour on April 5, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.api.forum.shared.call.event;

import io.reflection.app.api.forum.shared.call.GetRepliesRequest;
import io.reflection.app.api.forum.shared.call.GetRepliesResponse;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface GetRepliesEventHandler extends EventHandler {
	public static final GwtEvent.Type<GetRepliesEventHandler> TYPE = new GwtEvent.Type<GetRepliesEventHandler>();

	public void getRepliesSuccess(final GetRepliesRequest input, final GetRepliesResponse output);

	public void getRepliesFailure(final GetRepliesRequest input, final Throwable caught);

	public class GetRepliesSuccess extends GwtEvent<GetRepliesEventHandler> {
		private GetRepliesRequest input;
		private GetRepliesResponse output;

		public GetRepliesSuccess(final GetRepliesRequest input, final GetRepliesResponse output) {
			this.input = input;
			this.output = output;
		}

		@Override
		public GwtEvent.Type<GetRepliesEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(GetRepliesEventHandler handler) {
			handler.getRepliesSuccess(input, output);
		}
	}

	public class GetRepliesFailure extends GwtEvent<GetRepliesEventHandler> {
		private GetRepliesRequest input;
		private Throwable caught;

		public GetRepliesFailure(final GetRepliesRequest input, final Throwable caught) {
			this.input = input;
			this.caught = caught;
		}

		@Override
		public GwtEvent.Type<GetRepliesEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(GetRepliesEventHandler handler) {
			handler.getRepliesFailure(input, caught);
		}
	}

}