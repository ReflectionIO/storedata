//  
//  ChangePasswordEventHandler.java
//  reflection.io
//
//  Created by William Shakour on December 27, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.api.core.shared.call.event;

import io.reflection.app.api.core.shared.call.ChangePasswordRequest;
import io.reflection.app.api.core.shared.call.ChangePasswordResponse;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface ChangePasswordEventHandler extends EventHandler {
	public static final GwtEvent.Type<ChangePasswordEventHandler> TYPE = new GwtEvent.Type<ChangePasswordEventHandler>();

	public void changePasswordSuccess(final ChangePasswordRequest input, final ChangePasswordResponse output);

	public void changePasswordFailure(final ChangePasswordRequest input, final Throwable caught);

	public class ChangePasswordSuccess extends GwtEvent<ChangePasswordEventHandler> {
		private ChangePasswordRequest input;
		private ChangePasswordResponse output;

		public ChangePasswordSuccess(final ChangePasswordRequest input, final ChangePasswordResponse output) {
			this.input = input;
			this.output = output;
		}

		@Override
		public GwtEvent.Type<ChangePasswordEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(ChangePasswordEventHandler handler) {
			handler.changePasswordSuccess(input, output);
		}
	}

	public class ChangePasswordFailure extends GwtEvent<ChangePasswordEventHandler> {
		private ChangePasswordRequest input;
		private Throwable caught;

		public ChangePasswordFailure(final ChangePasswordRequest input, final Throwable caught) {
			this.input = input;
			this.caught = caught;
		}

		@Override
		public GwtEvent.Type<ChangePasswordEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(ChangePasswordEventHandler handler) {
			handler.changePasswordFailure(input, caught);
		}
	}

}