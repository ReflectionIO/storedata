//  
//  TriggerDataAccountGatherEventHandler.java
//  reflection.io
//
//  Created by William Shakour on October 16, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.api.admin.shared.call.event;

import io.reflection.app.api.admin.shared.call.TriggerDataAccountGatherRequest;
import io.reflection.app.api.admin.shared.call.TriggerDataAccountGatherResponse;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface TriggerDataAccountGatherEventHandler extends EventHandler {
	public static final GwtEvent.Type<TriggerDataAccountGatherEventHandler> TYPE = new GwtEvent.Type<TriggerDataAccountGatherEventHandler>();

	public void triggerDataAccountGatherSuccess(final TriggerDataAccountGatherRequest input, final TriggerDataAccountGatherResponse output);

	public void triggerDataAccountGatherFailure(final TriggerDataAccountGatherRequest input, final Throwable caught);

	public class TriggerDataAccountGatherSuccess extends GwtEvent<TriggerDataAccountGatherEventHandler> {
		private TriggerDataAccountGatherRequest input;
		private TriggerDataAccountGatherResponse output;

		public TriggerDataAccountGatherSuccess(final TriggerDataAccountGatherRequest input, final TriggerDataAccountGatherResponse output) {
			this.input = input;
			this.output = output;
		}

		@Override
		public GwtEvent.Type<TriggerDataAccountGatherEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(TriggerDataAccountGatherEventHandler handler) {
			handler.triggerDataAccountGatherSuccess(input, output);
		}
	}

	public class TriggerDataAccountGatherFailure extends GwtEvent<TriggerDataAccountGatherEventHandler> {
		private TriggerDataAccountGatherRequest input;
		private Throwable caught;

		public TriggerDataAccountGatherFailure(final TriggerDataAccountGatherRequest input, final Throwable caught) {
			this.input = input;
			this.caught = caught;
		}

		@Override
		public GwtEvent.Type<TriggerDataAccountGatherEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(TriggerDataAccountGatherEventHandler handler) {
			handler.triggerDataAccountGatherFailure(input, caught);
		}
	}

}