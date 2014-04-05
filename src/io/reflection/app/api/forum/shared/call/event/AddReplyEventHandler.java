//  
//  AddReplyEventHandler.java
//  reflection.io
//
//  Created by William Shakour on April 5, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.api.forum.shared.call.event;

import io.reflection.app.api.forum.shared.call.AddReplyRequest;
import io.reflection.app.api.forum.shared.call.AddReplyResponse;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface AddReplyEventHandler extends EventHandler {
	public static final GwtEvent.Type<AddReplyEventHandler> TYPE = new GwtEvent.Type<AddReplyEventHandler>();

	public void addReplySuccess(final AddReplyRequest input, final AddReplyResponse output);

	public void addReplyFailure(final AddReplyRequest input, final Throwable caught);

	public class AddReplySuccess extends GwtEvent<AddReplyEventHandler> {
		private AddReplyRequest input;
		private AddReplyResponse output;

		public AddReplySuccess(final AddReplyRequest input, final AddReplyResponse output) {
			this.input = input;
			this.output = output;
		}

		@Override
		public GwtEvent.Type<AddReplyEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(AddReplyEventHandler handler) {
			handler.addReplySuccess(input, output);
		}
	}

	public class AddReplyFailure extends GwtEvent<AddReplyEventHandler> {
		private AddReplyRequest input;
		private Throwable caught;

		public AddReplyFailure(final AddReplyRequest input, final Throwable caught) {
			this.input = input;
			this.caught = caught;
		}

		@Override
		public GwtEvent.Type<AddReplyEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(AddReplyEventHandler handler) {
			handler.addReplyFailure(input, caught);
		}
	}

}