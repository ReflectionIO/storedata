//  
//  DeleteReplyEventHandler.java
//  reflection.io
//
//  Created by William Shakour on April 5, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.api.forum.shared.call.event;

import io.reflection.app.api.forum.shared.call.DeleteReplyRequest;
import io.reflection.app.api.forum.shared.call.DeleteReplyResponse;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface DeleteReplyEventHandler extends EventHandler {
	public static final GwtEvent.Type<DeleteReplyEventHandler> TYPE = new GwtEvent.Type<DeleteReplyEventHandler>();

	public void deleteReplySuccess(final DeleteReplyRequest input, final DeleteReplyResponse output);

	public void deleteReplyFailure(final DeleteReplyRequest input, final Throwable caught);

	public class DeleteReplySuccess extends GwtEvent<DeleteReplyEventHandler> {
		private DeleteReplyRequest input;
		private DeleteReplyResponse output;

		public DeleteReplySuccess(final DeleteReplyRequest input, final DeleteReplyResponse output) {
			this.input = input;
			this.output = output;
		}

		@Override
		public GwtEvent.Type<DeleteReplyEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(DeleteReplyEventHandler handler) {
			handler.deleteReplySuccess(input, output);
		}
	}

	public class DeleteReplyFailure extends GwtEvent<DeleteReplyEventHandler> {
		private DeleteReplyRequest input;
		private Throwable caught;

		public DeleteReplyFailure(final DeleteReplyRequest input, final Throwable caught) {
			this.input = input;
			this.caught = caught;
		}

		@Override
		public GwtEvent.Type<DeleteReplyEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(DeleteReplyEventHandler handler) {
			handler.deleteReplyFailure(input, caught);
		}
	}

}