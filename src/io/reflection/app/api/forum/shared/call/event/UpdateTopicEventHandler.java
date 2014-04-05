//  
//  UpdateTopicEventHandler.java
//  reflection.io
//
//  Created by William Shakour on April 5, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.api.forum.shared.call.event;

import io.reflection.app.api.forum.shared.call.UpdateTopicRequest;
import io.reflection.app.api.forum.shared.call.UpdateTopicResponse;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface UpdateTopicEventHandler extends EventHandler {
	public static final GwtEvent.Type<UpdateTopicEventHandler> TYPE = new GwtEvent.Type<UpdateTopicEventHandler>();

	public void updateTopicSuccess(final UpdateTopicRequest input, final UpdateTopicResponse output);

	public void updateTopicFailure(final UpdateTopicRequest input, final Throwable caught);

	public class UpdateTopicSuccess extends GwtEvent<UpdateTopicEventHandler> {
		private UpdateTopicRequest input;
		private UpdateTopicResponse output;

		public UpdateTopicSuccess(final UpdateTopicRequest input, final UpdateTopicResponse output) {
			this.input = input;
			this.output = output;
		}

		@Override
		public GwtEvent.Type<UpdateTopicEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(UpdateTopicEventHandler handler) {
			handler.updateTopicSuccess(input, output);
		}
	}

	public class UpdateTopicFailure extends GwtEvent<UpdateTopicEventHandler> {
		private UpdateTopicRequest input;
		private Throwable caught;

		public UpdateTopicFailure(final UpdateTopicRequest input, final Throwable caught) {
			this.input = input;
			this.caught = caught;
		}

		@Override
		public GwtEvent.Type<UpdateTopicEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(UpdateTopicEventHandler handler) {
			handler.updateTopicFailure(input, caught);
		}
	}

}