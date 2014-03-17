//  
//  GetPostEventHandler.java
//  reflection.io
//
//  Created by William Shakour on March 16, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.api.blog.shared.call.event;

import io.reflection.app.api.blog.shared.call.GetPostRequest;
import io.reflection.app.api.blog.shared.call.GetPostResponse;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface GetPostEventHandler extends EventHandler {
	public static final GwtEvent.Type<GetPostEventHandler> TYPE = new GwtEvent.Type<GetPostEventHandler>();

	public void getPostSuccess(final GetPostRequest input, final GetPostResponse output);

	public void getPostFailure(final GetPostRequest input, final Throwable caught);

	public class GetPostSuccess extends GwtEvent<GetPostEventHandler> {
		private GetPostRequest input;
		private GetPostResponse output;

		public GetPostSuccess(final GetPostRequest input, final GetPostResponse output) {
			this.input = input;
			this.output = output;
		}

		@Override
		public GwtEvent.Type<GetPostEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(GetPostEventHandler handler) {
			handler.getPostSuccess(input, output);
		}
	}

	public class GetPostFailure extends GwtEvent<GetPostEventHandler> {
		private GetPostRequest input;
		private Throwable caught;

		public GetPostFailure(final GetPostRequest input, final Throwable caught) {
			this.input = input;
			this.caught = caught;
		}

		@Override
		public GwtEvent.Type<GetPostEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(GetPostEventHandler handler) {
			handler.getPostFailure(input, caught);
		}
	}

}