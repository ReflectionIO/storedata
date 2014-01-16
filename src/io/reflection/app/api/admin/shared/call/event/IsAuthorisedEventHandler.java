//  
//  IsAuthorisedEventHandler.java
//  reflection.io
//
//  Created by William Shakour on January 16, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.api.admin.shared.call.event;

import io.reflection.app.api.core.shared.call.IsAuthorisedRequest;
import io.reflection.app.api.core.shared.call.IsAuthorisedResponse;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface IsAuthorisedEventHandler extends EventHandler {
	public static final GwtEvent.Type<IsAuthorisedEventHandler> TYPE = new GwtEvent.Type<IsAuthorisedEventHandler>();

	public void isAuthorisedSuccess(final IsAuthorisedRequest input, final IsAuthorisedResponse output);

	public void isAuthorisedFailure(final IsAuthorisedRequest input, final Throwable caught);

	public class IsAuthorisedSuccess extends GwtEvent<IsAuthorisedEventHandler> {
		private IsAuthorisedRequest input;
		private IsAuthorisedResponse output;

		public IsAuthorisedSuccess(final IsAuthorisedRequest input, final IsAuthorisedResponse output) {
			this.input = input;
			this.output = output;
		}

		@Override
		public GwtEvent.Type<IsAuthorisedEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(IsAuthorisedEventHandler handler) {
			handler.isAuthorisedSuccess(input, output);
		}
	}

	public class IsAuthorisedFailure extends GwtEvent<IsAuthorisedEventHandler> {
		private IsAuthorisedRequest input;
		private Throwable caught;

		public IsAuthorisedFailure(final IsAuthorisedRequest input, final Throwable caught) {
			this.input = input;
			this.caught = caught;
		}

		@Override
		public GwtEvent.Type<IsAuthorisedEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(IsAuthorisedEventHandler handler) {
			handler.isAuthorisedFailure(input, caught);
		}
	}

}