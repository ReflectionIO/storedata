//  
//  ChangeUserDetailsEventHandler.java
//  reflection.io
//
//  Created by William Shakour on December 27, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.api.core.shared.call.event;

import io.reflection.app.api.core.shared.call.ChangeUserDetailsRequest;
import io.reflection.app.api.core.shared.call.ChangeUserDetailsResponse;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface ChangeUserDetailsEventHandler extends EventHandler {
	public static final GwtEvent.Type<ChangeUserDetailsEventHandler> TYPE = new GwtEvent.Type<ChangeUserDetailsEventHandler>();

	public void changeUserDetailsSuccess(final ChangeUserDetailsRequest input, final ChangeUserDetailsResponse output);

	public void changeUserDetailsFailure(final ChangeUserDetailsRequest input, final Throwable caught);

	public class ChangeUserDetailsSuccess extends GwtEvent<ChangeUserDetailsEventHandler> {
		private ChangeUserDetailsRequest input;
		private ChangeUserDetailsResponse output;

		public ChangeUserDetailsSuccess(final ChangeUserDetailsRequest input, final ChangeUserDetailsResponse output) {
			this.input = input;
			this.output = output;
		}

		@Override
		public GwtEvent.Type<ChangeUserDetailsEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(ChangeUserDetailsEventHandler handler) {
			handler.changeUserDetailsSuccess(input, output);
		}
	}

	public class ChangeUserDetailsFailure extends GwtEvent<ChangeUserDetailsEventHandler> {
		private ChangeUserDetailsRequest input;
		private Throwable caught;

		public ChangeUserDetailsFailure(final ChangeUserDetailsRequest input, final Throwable caught) {
			this.input = input;
			this.caught = caught;
		}

		@Override
		public GwtEvent.Type<ChangeUserDetailsEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(ChangeUserDetailsEventHandler handler) {
			handler.changeUserDetailsFailure(input, caught);
		}
	}

}