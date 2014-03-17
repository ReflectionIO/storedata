//  
//  CreatePostEventHandler.java
//  reflection.io
//
//  Created by William Shakour on March 16, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.api.blog.shared.call.event;

import io.reflection.app.api.blog.shared.call.CreatePostRequest;
import io.reflection.app.api.blog.shared.call.CreatePostResponse;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface CreatePostEventHandler extends EventHandler {
	public static final GwtEvent.Type<CreatePostEventHandler> TYPE = new GwtEvent.Type<CreatePostEventHandler>();

	public void createPostSuccess(final CreatePostRequest input, final CreatePostResponse output);

	public void createPostFailure(final CreatePostRequest input, final Throwable caught);

	public class CreatePostSuccess extends GwtEvent<CreatePostEventHandler> {
		private CreatePostRequest input;
		private CreatePostResponse output;

		public CreatePostSuccess(final CreatePostRequest input, final CreatePostResponse output) {
			this.input = input;
			this.output = output;
		}

		@Override
		public GwtEvent.Type<CreatePostEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(CreatePostEventHandler handler) {
			handler.createPostSuccess(input, output);
		}
	}

	public class CreatePostFailure extends GwtEvent<CreatePostEventHandler> {
		private CreatePostRequest input;
		private Throwable caught;

		public CreatePostFailure(final CreatePostRequest input, final Throwable caught) {
			this.input = input;
			this.caught = caught;
		}

		@Override
		public GwtEvent.Type<CreatePostEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(CreatePostEventHandler handler) {
			handler.createPostFailure(input, caught);
		}
	}

}