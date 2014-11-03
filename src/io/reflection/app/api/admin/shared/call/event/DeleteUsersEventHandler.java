//  
//  DeleteUsersEventHandler.java
//  reflection.io
//
//  Created by William Shakour on October 30, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.api.admin.shared.call.event;

import io.reflection.app.api.admin.shared.call.DeleteUsersRequest;
import io.reflection.app.api.admin.shared.call.DeleteUsersResponse;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface DeleteUsersEventHandler extends EventHandler {
	public static final GwtEvent.Type<DeleteUsersEventHandler> TYPE = new GwtEvent.Type<DeleteUsersEventHandler>();

	public void deleteUsersSuccess(final DeleteUsersRequest input, final DeleteUsersResponse output);

	public void deleteUsersFailure(final DeleteUsersRequest input, final Throwable caught);

	public class DeleteUsersSuccess extends GwtEvent<DeleteUsersEventHandler> {
		private DeleteUsersRequest input;
		private DeleteUsersResponse output;

		public DeleteUsersSuccess(final DeleteUsersRequest input, final DeleteUsersResponse output) {
			this.input = input;
			this.output = output;
		}

		@Override
		public GwtEvent.Type<DeleteUsersEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(DeleteUsersEventHandler handler) {
			handler.deleteUsersSuccess(input, output);
		}
	}

	public class DeleteUsersFailure extends GwtEvent<DeleteUsersEventHandler> {
		private DeleteUsersRequest input;
		private Throwable caught;

		public DeleteUsersFailure(final DeleteUsersRequest input, final Throwable caught) {
			this.input = input;
			this.caught = caught;
		}

		@Override
		public GwtEvent.Type<DeleteUsersEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(DeleteUsersEventHandler handler) {
			handler.deleteUsersFailure(input, caught);
		}
	}

}