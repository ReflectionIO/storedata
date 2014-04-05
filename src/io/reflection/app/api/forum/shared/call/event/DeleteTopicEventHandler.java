//  
//  DeleteTopicEventHandler.java
//  reflection.io
//
//  Created by William Shakour on April 5, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.api.forum.shared.call.event;

import io.reflection.app.api.forum.shared.call.DeleteTopicRequest;
import io.reflection.app.api.forum.shared.call.DeleteTopicResponse;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface DeleteTopicEventHandler extends EventHandler {
	public static final GwtEvent.Type<DeleteTopicEventHandler> TYPE = new GwtEvent.Type<DeleteTopicEventHandler>();

	public void deleteTopicSuccess(final DeleteTopicRequest input, final DeleteTopicResponse output);

	public void deleteTopicFailure(final DeleteTopicRequest input, final Throwable caught);

	public class DeleteTopicSuccess extends GwtEvent<DeleteTopicEventHandler> {
		private DeleteTopicRequest input;
		private DeleteTopicResponse output;

		public DeleteTopicSuccess(final DeleteTopicRequest input, final DeleteTopicResponse output) {
			this.input = input;
			this.output = output;
		}

		@Override
		public GwtEvent.Type<DeleteTopicEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(DeleteTopicEventHandler handler) {
			handler.deleteTopicSuccess(input, output);
		}
	}

	public class DeleteTopicFailure extends GwtEvent<DeleteTopicEventHandler> {
		private DeleteTopicRequest input;
		private Throwable caught;

		public DeleteTopicFailure(final DeleteTopicRequest input, final Throwable caught) {
			this.input = input;
			this.caught = caught;
		}

		@Override
		public GwtEvent.Type<DeleteTopicEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(DeleteTopicEventHandler handler) {
			handler.deleteTopicFailure(input, caught);
		}
	}

}