//  
//  DeletePostEventHandler.java
//  reflection.io
//
//  Created by William Shakour on March 28, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.api.blog.shared.call.event;

import io.reflection.app.api.blog.shared.call.DeletePostRequest;
import io.reflection.app.api.blog.shared.call.DeletePostResponse;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface DeletePostEventHandler extends EventHandler {
	public static final GwtEvent.Type<DeletePostEventHandler> TYPE = new GwtEvent.Type<DeletePostEventHandler>();

	public void deletePostSuccess(final DeletePostRequest input, final DeletePostResponse output);

	public void deletePostFailure(final DeletePostRequest input, final Throwable caught);

	public class DeletePostSuccess extends GwtEvent<DeletePostEventHandler> {
		private DeletePostRequest input;
		private DeletePostResponse output;

		public DeletePostSuccess(final DeletePostRequest input, final DeletePostResponse output) {
			this.input = input;
			this.output = output;
		}

		@Override
		public GwtEvent.Type<DeletePostEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(DeletePostEventHandler handler) {
			handler.deletePostSuccess(input, output);
		}
	}

	public class DeletePostFailure extends GwtEvent<DeletePostEventHandler> {
		private DeletePostRequest input;
		private Throwable caught;

		public DeletePostFailure(final DeletePostRequest input, final Throwable caught) {
			this.input = input;
			this.caught = caught;
		}

		@Override
		public GwtEvent.Type<DeletePostEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(DeletePostEventHandler handler) {
			handler.deletePostFailure(input, caught);
		}
	}

}