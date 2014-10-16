//  
//  TriggerDataAccountFetchIngestEventHandler.java
//  reflection.io
//
//  Created by William Shakour on October 16, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.api.admin.shared.call.event;

import io.reflection.app.api.admin.shared.call.TriggerDataAccountFetchIngestRequest;
import io.reflection.app.api.admin.shared.call.TriggerDataAccountFetchIngestResponse;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface TriggerDataAccountFetchIngestEventHandler extends EventHandler {
	public static final GwtEvent.Type<TriggerDataAccountFetchIngestEventHandler> TYPE = new GwtEvent.Type<TriggerDataAccountFetchIngestEventHandler>();

	public void triggerDataAccountFetchIngestSuccess(final TriggerDataAccountFetchIngestRequest input, final TriggerDataAccountFetchIngestResponse output);

	public void triggerDataAccountFetchIngestFailure(final TriggerDataAccountFetchIngestRequest input, final Throwable caught);

	public class TriggerDataAccountFetchIngestSuccess extends GwtEvent<TriggerDataAccountFetchIngestEventHandler> {
		private TriggerDataAccountFetchIngestRequest input;
		private TriggerDataAccountFetchIngestResponse output;

		public TriggerDataAccountFetchIngestSuccess(final TriggerDataAccountFetchIngestRequest input, final TriggerDataAccountFetchIngestResponse output) {
			this.input = input;
			this.output = output;
		}

		@Override
		public GwtEvent.Type<TriggerDataAccountFetchIngestEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(TriggerDataAccountFetchIngestEventHandler handler) {
			handler.triggerDataAccountFetchIngestSuccess(input, output);
		}
	}

	public class TriggerDataAccountFetchIngestFailure extends GwtEvent<TriggerDataAccountFetchIngestEventHandler> {
		private TriggerDataAccountFetchIngestRequest input;
		private Throwable caught;

		public TriggerDataAccountFetchIngestFailure(final TriggerDataAccountFetchIngestRequest input, final Throwable caught) {
			this.input = input;
			this.caught = caught;
		}

		@Override
		public GwtEvent.Type<TriggerDataAccountFetchIngestEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(TriggerDataAccountFetchIngestEventHandler handler) {
			handler.triggerDataAccountFetchIngestFailure(input, caught);
		}
	}

}