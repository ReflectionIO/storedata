//  
//  GetFeedFetchesEventHandler.java
//  reflection.io
//
//  Created by William Shakour on December 27, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.api.admin.shared.call.event;

import io.reflection.app.api.admin.shared.call.GetFeedFetchesRequest;
import io.reflection.app.api.admin.shared.call.GetFeedFetchesResponse;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface GetFeedFetchesEventHandler extends EventHandler {
	public static final GwtEvent.Type<GetFeedFetchesEventHandler> TYPE = new GwtEvent.Type<GetFeedFetchesEventHandler>();

	public void getFeedFetchesSuccess(final GetFeedFetchesRequest input, final GetFeedFetchesResponse output);

	public void getFeedFetchesFailure(final GetFeedFetchesRequest input, final Throwable caught);

	public class GetFeedFetchesSuccess extends GwtEvent<GetFeedFetchesEventHandler> {
		private GetFeedFetchesRequest input;
		private GetFeedFetchesResponse output;

		public GetFeedFetchesSuccess(final GetFeedFetchesRequest input, final GetFeedFetchesResponse output) {
			this.input = input;
			this.output = output;
		}

		@Override
		public GwtEvent.Type<GetFeedFetchesEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(GetFeedFetchesEventHandler handler) {
			handler.getFeedFetchesSuccess(input, output);
		}
	}

	public class GetFeedFetchesFailure extends GwtEvent<GetFeedFetchesEventHandler> {
		private GetFeedFetchesRequest input;
		private Throwable caught;

		public GetFeedFetchesFailure(final GetFeedFetchesRequest input, final Throwable caught) {
			this.input = input;
			this.caught = caught;
		}

		@Override
		public GwtEvent.Type<GetFeedFetchesEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(GetFeedFetchesEventHandler handler) {
			handler.getFeedFetchesFailure(input, caught);
		}
	}

}