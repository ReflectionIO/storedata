//  
//  UpdateLinkedAccountEventHandler.java
//  reflection.io
//
//  Created by Stefano Capuzzi on April 14, 2014.
//  Copyrights �� 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights �� 2014 reflection.io. All rights reserved.
//
package io.reflection.app.api.core.shared.call.event;

import io.reflection.app.api.core.shared.call.UpdateLinkedAccountRequest;
import io.reflection.app.api.core.shared.call.UpdateLinkedAccountResponse;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface UpdateLinkedAccountEventHandler extends EventHandler {
	public static final GwtEvent.Type<UpdateLinkedAccountEventHandler> TYPE = new GwtEvent.Type<UpdateLinkedAccountEventHandler>();

	public void updateLinkedAccountSuccess(final UpdateLinkedAccountRequest input, final UpdateLinkedAccountResponse output);

	public void updateLinkedAccountFailure(final UpdateLinkedAccountRequest input, final Throwable caught);

	public class UpdateLinkedAccountSuccess extends GwtEvent<UpdateLinkedAccountEventHandler> {
		private UpdateLinkedAccountRequest input;
		private UpdateLinkedAccountResponse output;

		public UpdateLinkedAccountSuccess(final UpdateLinkedAccountRequest input, final UpdateLinkedAccountResponse output) {
			this.input = input;
			this.output = output;
		}

		@Override
		public GwtEvent.Type<UpdateLinkedAccountEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(UpdateLinkedAccountEventHandler handler) {
			handler.updateLinkedAccountSuccess(input, output);
		}
	}

	public class UpdateLinkedAccountFailure extends GwtEvent<UpdateLinkedAccountEventHandler> {
		private UpdateLinkedAccountRequest input;
		private Throwable caught;

		public UpdateLinkedAccountFailure(final UpdateLinkedAccountRequest input, final Throwable caught) {
			this.input = input;
			this.caught = caught;
		}

		@Override
		public GwtEvent.Type<UpdateLinkedAccountEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(UpdateLinkedAccountEventHandler handler) {
			handler.updateLinkedAccountFailure(input, caught);
		}
	}

}