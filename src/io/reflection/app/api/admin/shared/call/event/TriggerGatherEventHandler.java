//  
//  TriggerGatherEventHandler.java
//  reflection.io
//
//  Created by William Shakour on December 27, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.api.admin.shared.call.event;

import io.reflection.app.api.admin.shared.call.TriggerGatherRequest;
import io.reflection.app.api.admin.shared.call.TriggerGatherResponse;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface TriggerGatherEventHandler extends EventHandler {
	public static final GwtEvent.Type<TriggerGatherEventHandler> TYPE = new GwtEvent.Type<TriggerGatherEventHandler>();

	public void triggerGatherSuccess(final TriggerGatherRequest input, final TriggerGatherResponse output);

	public void triggerGatherFailure(final TriggerGatherRequest input, final Throwable caught);

	public class TriggerGatherSuccess extends GwtEvent<TriggerGatherEventHandler> {
		private TriggerGatherRequest input;
		private TriggerGatherResponse output;

		public TriggerGatherSuccess(final TriggerGatherRequest input, final TriggerGatherResponse output) {
			this.input = input;
			this.output = output;
		}

		@Override
		public GwtEvent.Type<TriggerGatherEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(TriggerGatherEventHandler handler) {
			handler.triggerGatherSuccess(input, output);
		}
	}

	public class TriggerGatherFailure extends GwtEvent<TriggerGatherEventHandler> {
		private TriggerGatherRequest input;
		private Throwable caught;

		public TriggerGatherFailure(final TriggerGatherRequest input, final Throwable caught) {
			this.input = input;
			this.caught = caught;
		}

		@Override
		public GwtEvent.Type<TriggerGatherEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(TriggerGatherEventHandler handler) {
			handler.triggerGatherFailure(input, caught);
		}
	}

}