//  
//  ForgotPasswordEventHandler.java
//  reflection.io
//
//  Created by William Shakour on February 18, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.api.core.shared.call.event;

import io.reflection.app.api.core.shared.call.ForgotPasswordRequest;
import io.reflection.app.api.core.shared.call.ForgotPasswordResponse;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface ForgotPasswordEventHandler extends EventHandler {
	public static final GwtEvent.Type<ForgotPasswordEventHandler> TYPE = new GwtEvent.Type<ForgotPasswordEventHandler>();

	public void forgotPasswordSuccess(final ForgotPasswordRequest input, final ForgotPasswordResponse output);

	public void forgotPasswordFailure(final ForgotPasswordRequest input, final Throwable caught);

	public class ForgotPasswordSuccess extends GwtEvent<ForgotPasswordEventHandler> {
		private ForgotPasswordRequest input;
		private ForgotPasswordResponse output;

		public ForgotPasswordSuccess(final ForgotPasswordRequest input, final ForgotPasswordResponse output) {
			this.input = input;
			this.output = output;
		}

		@Override
		public GwtEvent.Type<ForgotPasswordEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(ForgotPasswordEventHandler handler) {
			handler.forgotPasswordSuccess(input, output);
		}
	}

	public class ForgotPasswordFailure extends GwtEvent<ForgotPasswordEventHandler> {
		private ForgotPasswordRequest input;
		private Throwable caught;

		public ForgotPasswordFailure(final ForgotPasswordRequest input, final Throwable caught) {
			this.input = input;
			this.caught = caught;
		}

		@Override
		public GwtEvent.Type<ForgotPasswordEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(ForgotPasswordEventHandler handler) {
			handler.forgotPasswordFailure(input, caught);
		}
	}

}