//  
//  TriggerPredictEventHandler.java
//  reflection.io
//
//  Created by William Shakour on December 27, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.api.admin.shared.call.event;
import io.reflection.app.api.admin.shared.call.TriggerPredictRequest;
import io.reflection.app.api.admin.shared.call.TriggerPredictResponse;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;


public interface TriggerPredictEventHandler extends EventHandler {
	public static final GwtEvent.Type<TriggerPredictEventHandler> TYPE = new GwtEvent.Type<TriggerPredictEventHandler>();

	public void triggerPredictSuccess(final TriggerPredictRequest input, final TriggerPredictResponse output);

	public void triggerPredictFailure(final TriggerPredictRequest input, final Throwable caught);

	public class TriggerPredictSuccess extends GwtEvent<TriggerPredictEventHandler> {
		private TriggerPredictRequest input;
		private TriggerPredictResponse output;

		public TriggerPredictSuccess(final TriggerPredictRequest input, final TriggerPredictResponse output) {
			this.input = input;
			this.output = output;
		}

		@Override
		public GwtEvent.Type<TriggerPredictEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(TriggerPredictEventHandler handler) {
			handler.triggerPredictSuccess(input, output);
		}
	}

	public class TriggerPredictFailure extends GwtEvent<TriggerPredictEventHandler> {
		private TriggerPredictRequest input;
		private Throwable caught;

		public TriggerPredictFailure(final TriggerPredictRequest input, final Throwable caught) {
			this.input = input;
			this.caught = caught;
		}

		@Override
		public GwtEvent.Type<TriggerPredictEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(TriggerPredictEventHandler handler) {
			handler.triggerPredictFailure(input, caught);
		}
	}

}