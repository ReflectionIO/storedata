//  
//  UpdatePostEventHandler.java
//  reflection.io
//
//  Created by William Shakour on March 16, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.api.blog.shared.call.event;

import io.reflection.app.api.blog.shared.call.UpdatePostRequest;
import io.reflection.app.api.blog.shared.call.UpdatePostResponse;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface UpdatePostEventHandler extends EventHandler {
	public static final GwtEvent.Type<UpdatePostEventHandler> TYPE = new GwtEvent.Type<UpdatePostEventHandler>();

	public void updatePostSuccess(final UpdatePostRequest input, final UpdatePostResponse output);

	public void updatePostFailure(final UpdatePostRequest input, final Throwable caught);

	public class UpdatePostSuccess extends GwtEvent<UpdatePostEventHandler> {
		private UpdatePostRequest input;
		private UpdatePostResponse output;

		public UpdatePostSuccess(final UpdatePostRequest input, final UpdatePostResponse output) {
			this.input = input;
			this.output = output;
		}

		@Override
		public GwtEvent.Type<UpdatePostEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(UpdatePostEventHandler handler) {
			handler.updatePostSuccess(input, output);
		}
	}

	public class UpdatePostFailure extends GwtEvent<UpdatePostEventHandler> {
		private UpdatePostRequest input;
		private Throwable caught;

		public UpdatePostFailure(final UpdatePostRequest input, final Throwable caught) {
			this.input = input;
			this.caught = caught;
		}

		@Override
		public GwtEvent.Type<UpdatePostEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(UpdatePostEventHandler handler) {
			handler.updatePostFailure(input, caught);
		}
	}

}