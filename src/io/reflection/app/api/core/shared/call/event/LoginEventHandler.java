//  
//  LoginEventHandler.java
//  reflection.io
//
//  Created by William Shakour on December 27, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.api.core.shared.call.event;

import io.reflection.app.api.core.shared.call.LoginRequest;
import io.reflection.app.api.core.shared.call.LoginResponse;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface LoginEventHandler extends EventHandler {
	public static final GwtEvent.Type<LoginEventHandler> TYPE = new GwtEvent.Type<LoginEventHandler>();

	public void loginSuccess(final LoginRequest input, final LoginResponse output);

	public void loginFailure(final LoginRequest input, final Throwable caught);

	public class LoginSuccess extends GwtEvent<LoginEventHandler> {
		private LoginRequest input;
		private LoginResponse output;

		public LoginSuccess(final LoginRequest input, final LoginResponse output) {
			this.input = input;
			this.output = output;
		}

		@Override
		public GwtEvent.Type<LoginEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(LoginEventHandler handler) {
			handler.loginSuccess(input, output);
		}
	}

	public class LoginFailure extends GwtEvent<LoginEventHandler> {
		private LoginRequest input;
		private Throwable caught;

		public LoginFailure(final LoginRequest input, final Throwable caught) {
			this.input = input;
			this.caught = caught;
		}

		@Override
		public GwtEvent.Type<LoginEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(LoginEventHandler handler) {
			handler.loginFailure(input, caught);
		}
	}

}