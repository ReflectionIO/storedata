//  
//  CreateTopicEventHandler.java
//  reflection.io
//
//  Created by William Shakour on April 5, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.api.forum.shared.call.event;

import io.reflection.app.api.forum.shared.call.CreateTopicRequest;
import io.reflection.app.api.forum.shared.call.CreateTopicResponse;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface CreateTopicEventHandler extends EventHandler {
	public static final GwtEvent.Type<CreateTopicEventHandler> TYPE = new GwtEvent.Type<CreateTopicEventHandler>();

	public void createTopicSuccess(final CreateTopicRequest input, final CreateTopicResponse output);

	public void createTopicFailure(final CreateTopicRequest input, final Throwable caught);

	public class CreateTopicSuccess extends GwtEvent<CreateTopicEventHandler> {
		private CreateTopicRequest input;
		private CreateTopicResponse output;

		public CreateTopicSuccess(final CreateTopicRequest input, final CreateTopicResponse output) {
			this.input = input;
			this.output = output;
		}

		@Override
		public GwtEvent.Type<CreateTopicEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(CreateTopicEventHandler handler) {
			handler.createTopicSuccess(input, output);
		}
	}

	public class CreateTopicFailure extends GwtEvent<CreateTopicEventHandler> {
		private CreateTopicRequest input;
		private Throwable caught;

		public CreateTopicFailure(final CreateTopicRequest input, final Throwable caught) {
			this.input = input;
			this.caught = caught;
		}

		@Override
		public GwtEvent.Type<CreateTopicEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(CreateTopicEventHandler handler) {
			handler.createTopicFailure(input, caught);
		}
	}

}