//  
//  TriggerIngestEventHandler.java
//  reflection.io
//
//  Created by William Shakour on December 27, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.api.admin.shared.call.event;

import io.reflection.app.api.admin.shared.call.TriggerIngestRequest;
import io.reflection.app.api.admin.shared.call.TriggerIngestResponse;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface TriggerIngestEventHandler extends EventHandler {
	public static final GwtEvent.Type<TriggerIngestEventHandler> TYPE = new GwtEvent.Type<TriggerIngestEventHandler>();

	public void triggerIngestSuccess(final TriggerIngestRequest input, final TriggerIngestResponse output);

	public void triggerIngestFailure(final TriggerIngestRequest input, final Throwable caught);

	public class TriggerIngestSuccess extends GwtEvent<TriggerIngestEventHandler> {
		private TriggerIngestRequest input;
		private TriggerIngestResponse output;

		public TriggerIngestSuccess(final TriggerIngestRequest input, final TriggerIngestResponse output) {
			this.input = input;
			this.output = output;
		}

		@Override
		public GwtEvent.Type<TriggerIngestEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(TriggerIngestEventHandler handler) {
			handler.triggerIngestSuccess(input, output);
		}
	}

	public class TriggerIngestFailure extends GwtEvent<TriggerIngestEventHandler> {
		private TriggerIngestRequest input;
		private Throwable caught;

		public TriggerIngestFailure(final TriggerIngestRequest input, final Throwable caught) {
			this.input = input;
			this.caught = caught;
		}

		@Override
		public GwtEvent.Type<TriggerIngestEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(TriggerIngestEventHandler handler) {
			handler.triggerIngestFailure(input, caught);
		}
	}

}