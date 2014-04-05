//  
//  UpdateReplyEventHandler.java
//  reflection.io
//
//  Created by William Shakour on April 5, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.api.forum.shared.call.event;

import io.reflection.app.api.forum.shared.call.UpdateReplyRequest;
import io.reflection.app.api.forum.shared.call.UpdateReplyResponse;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface UpdateReplyEventHandler extends EventHandler {
	public static final GwtEvent.Type<UpdateReplyEventHandler> TYPE = new GwtEvent.Type<UpdateReplyEventHandler>();

	public void updateReplySuccess(final UpdateReplyRequest input, final UpdateReplyResponse output);

	public void updateReplyFailure(final UpdateReplyRequest input, final Throwable caught);

	public class UpdateReplySuccess extends GwtEvent<UpdateReplyEventHandler> {
		private UpdateReplyRequest input;
		private UpdateReplyResponse output;

		public UpdateReplySuccess(final UpdateReplyRequest input, final UpdateReplyResponse output) {
			this.input = input;
			this.output = output;
		}

		@Override
		public GwtEvent.Type<UpdateReplyEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(UpdateReplyEventHandler handler) {
			handler.updateReplySuccess(input, output);
		}
	}

	public class UpdateReplyFailure extends GwtEvent<UpdateReplyEventHandler> {
		private UpdateReplyRequest input;
		private Throwable caught;

		public UpdateReplyFailure(final UpdateReplyRequest input, final Throwable caught) {
			this.input = input;
			this.caught = caught;
		}

		@Override
		public GwtEvent.Type<UpdateReplyEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(UpdateReplyEventHandler handler) {
			handler.updateReplyFailure(input, caught);
		}
	}

}