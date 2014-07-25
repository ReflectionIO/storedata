//  
//  DeleteUserEventHandler.java
//  reflection.io
//
//  Created by William Shakour on July 25, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.api.blog.shared.call.event;

import io.reflection.app.api.blog.shared.call.DeleteUserRequest;
import io.reflection.app.api.blog.shared.call.DeleteUserResponse;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface DeleteUserEventHandler extends EventHandler {
	public static final GwtEvent.Type<DeleteUserEventHandler> TYPE = new GwtEvent.Type<DeleteUserEventHandler>();

	public void deleteUserSuccess(final DeleteUserRequest input, final DeleteUserResponse output);

	public void deleteUserFailure(final DeleteUserRequest input, final Throwable caught);

	public class DeleteUserSuccess extends GwtEvent<DeleteUserEventHandler> {
		private DeleteUserRequest input;
		private DeleteUserResponse output;

		public DeleteUserSuccess(final DeleteUserRequest input, final DeleteUserResponse output) {
			this.input = input;
			this.output = output;
		}

		@Override
		public GwtEvent.Type<DeleteUserEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(DeleteUserEventHandler handler) {
			handler.deleteUserSuccess(input, output);
		}
	}

	public class DeleteUserFailure extends GwtEvent<DeleteUserEventHandler> {
		private DeleteUserRequest input;
		private Throwable caught;

		public DeleteUserFailure(final DeleteUserRequest input, final Throwable caught) {
			this.input = input;
			this.caught = caught;
		}

		@Override
		public GwtEvent.Type<DeleteUserEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(DeleteUserEventHandler handler) {
			handler.deleteUserFailure(input, caught);
		}
	}

}