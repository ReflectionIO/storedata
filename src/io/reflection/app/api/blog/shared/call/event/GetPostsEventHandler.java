//  
//  GetPostsEventHandler.java
//  reflection.io
//
//  Created by William Shakour on March 16, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.api.blog.shared.call.event;

import io.reflection.app.api.blog.shared.call.GetPostsRequest;
import io.reflection.app.api.blog.shared.call.GetPostsResponse;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface GetPostsEventHandler extends EventHandler {
	public static final GwtEvent.Type<GetPostsEventHandler> TYPE = new GwtEvent.Type<GetPostsEventHandler>();

	public void getPostsSuccess(final GetPostsRequest input, final GetPostsResponse output);

	public void getPostsFailure(final GetPostsRequest input, final Throwable caught);

	public class GetPostsSuccess extends GwtEvent<GetPostsEventHandler> {
		private GetPostsRequest input;
		private GetPostsResponse output;

		public GetPostsSuccess(final GetPostsRequest input, final GetPostsResponse output) {
			this.input = input;
			this.output = output;
		}

		@Override
		public GwtEvent.Type<GetPostsEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(GetPostsEventHandler handler) {
			handler.getPostsSuccess(input, output);
		}
	}

	public class GetPostsFailure extends GwtEvent<GetPostsEventHandler> {
		private GetPostsRequest input;
		private Throwable caught;

		public GetPostsFailure(final GetPostsRequest input, final Throwable caught) {
			this.input = input;
			this.caught = caught;
		}

		@Override
		public GwtEvent.Type<GetPostsEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(GetPostsEventHandler handler) {
			handler.getPostsFailure(input, caught);
		}
	}

}