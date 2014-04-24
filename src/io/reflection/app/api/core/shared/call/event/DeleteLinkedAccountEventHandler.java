//  
//  DeleteLinkedAccountEventHandler.java
//  reflection.io
//
//  Created by Stefano Capuzzi on April 14, 2014.
//  Copyrights �� 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights �� 2014 reflection.io. All rights reserved.
//
package io.reflection.app.api.core.shared.call.event;

import io.reflection.app.api.core.shared.call.DeleteLinkedAccountRequest;
import io.reflection.app.api.core.shared.call.DeleteLinkedAccountResponse;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface DeleteLinkedAccountEventHandler extends EventHandler {
	public static final GwtEvent.Type<DeleteLinkedAccountEventHandler> TYPE = new GwtEvent.Type<DeleteLinkedAccountEventHandler>();

	public void deleteLinkedAccountSuccess(final DeleteLinkedAccountRequest input, final DeleteLinkedAccountResponse output);

	public void deleteLinkedAccountFailure(final DeleteLinkedAccountRequest input, final Throwable caught);

	public class DeleteLinkedAccountSuccess extends GwtEvent<DeleteLinkedAccountEventHandler> {
		private DeleteLinkedAccountRequest input;
		private DeleteLinkedAccountResponse output;

		public DeleteLinkedAccountSuccess(final DeleteLinkedAccountRequest input, final DeleteLinkedAccountResponse output) {
			this.input = input;
			this.output = output;
		}

		@Override
		public GwtEvent.Type<DeleteLinkedAccountEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(DeleteLinkedAccountEventHandler handler) {
			handler.deleteLinkedAccountSuccess(input, output);
		}
	}

	public class DeleteLinkedAccountFailure extends GwtEvent<DeleteLinkedAccountEventHandler> {
		private DeleteLinkedAccountRequest input;
		private Throwable caught;

		public DeleteLinkedAccountFailure(final DeleteLinkedAccountRequest input, final Throwable caught) {
			this.input = input;
			this.caught = caught;
		}

		@Override
		public GwtEvent.Type<DeleteLinkedAccountEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(DeleteLinkedAccountEventHandler handler) {
			handler.deleteLinkedAccountFailure(input, caught);
		}
	}

}