//  
//  TriggerModelEventHandler.java
//  reflection.io
//
//  Created by William Shakour on December 27, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.api.admin.shared.call.event;

import io.reflection.app.api.admin.shared.call.TriggerModelRequest;
import io.reflection.app.api.admin.shared.call.TriggerModelResponse;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface TriggerModelEventHandler extends EventHandler {
	public static final GwtEvent.Type<TriggerModelEventHandler> TYPE = new GwtEvent.Type<TriggerModelEventHandler>();

	public void triggerModelSuccess(final TriggerModelRequest input, final TriggerModelResponse output);

	public void triggerModelFailure(final TriggerModelRequest input, final Throwable caught);

	public class TriggerModelSuccess extends GwtEvent<TriggerModelEventHandler> {
		private TriggerModelRequest input;
		private TriggerModelResponse output;

		public TriggerModelSuccess(final TriggerModelRequest input, final TriggerModelResponse output) {
			this.input = input;
			this.output = output;
		}

		@Override
		public GwtEvent.Type<TriggerModelEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(TriggerModelEventHandler handler) {
			handler.triggerModelSuccess(input, output);
		}
	}

	public class TriggerModelFailure extends GwtEvent<TriggerModelEventHandler> {
		private TriggerModelRequest input;
		private Throwable caught;

		public TriggerModelFailure(final TriggerModelRequest input, final Throwable caught) {
			this.input = input;
			this.caught = caught;
		}

		@Override
		public GwtEvent.Type<TriggerModelEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(TriggerModelEventHandler handler) {
			handler.triggerModelFailure(input, caught);
		}
	}

}