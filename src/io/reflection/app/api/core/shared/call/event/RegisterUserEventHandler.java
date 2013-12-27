//  
//  RegisterUserEventHandler.java
//  reflection.io
//
//  Created by William Shakour on December 27, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.api.core.shared.call.event;

import io.reflection.app.api.core.shared.call.RegisterUserRequest;
import io.reflection.app.api.core.shared.call.RegisterUserResponse;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface RegisterUserEventHandler extends EventHandler {
	public static final GwtEvent.Type<RegisterUserEventHandler> TYPE = new GwtEvent.Type<RegisterUserEventHandler>();

	public void registerUserSuccess(final RegisterUserRequest input, final RegisterUserResponse output);

	public void registerUserFailure(final RegisterUserRequest input, final Throwable caught);

	public class RegisterUserSuccess extends GwtEvent<RegisterUserEventHandler> {
		private RegisterUserRequest input;
		private RegisterUserResponse output;

		public RegisterUserSuccess(final RegisterUserRequest input, final RegisterUserResponse output) {
			this.input = input;
			this.output = output;
		}

		@Override
		public GwtEvent.Type<RegisterUserEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(RegisterUserEventHandler handler) {
			handler.registerUserSuccess(input, output);
		}
	}

	public class RegisterUserFailure extends GwtEvent<RegisterUserEventHandler> {
		private RegisterUserRequest input;
		private Throwable caught;

		public RegisterUserFailure(final RegisterUserRequest input, final Throwable caught) {
			this.input = input;
			this.caught = caught;
		}

		@Override
		public GwtEvent.Type<RegisterUserEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(RegisterUserEventHandler handler) {
			handler.registerUserFailure(input, caught);
		}
	}

}