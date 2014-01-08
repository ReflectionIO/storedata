//  
//  SetPasswordEventHandler.java
//  reflection.io
//
//  Created by William Shakour on December 27, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.api.admin.shared.call.event;

import io.reflection.app.api.admin.shared.call.SetPasswordRequest;
import io.reflection.app.api.admin.shared.call.SetPasswordResponse;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface SetPasswordEventHandler extends EventHandler {
	public static final GwtEvent.Type<SetPasswordEventHandler> TYPE = new GwtEvent.Type<SetPasswordEventHandler>();

	public void setPasswordSuccess(final SetPasswordRequest input, final SetPasswordResponse output);

	public void setPasswordFailure(final SetPasswordRequest input, final Throwable caught);

	public class SetPasswordSuccess extends GwtEvent<SetPasswordEventHandler> {
		private SetPasswordRequest input;
		private SetPasswordResponse output;

		public SetPasswordSuccess(final SetPasswordRequest input, final SetPasswordResponse output) {
			this.input = input;
			this.output = output;
		}

		@Override
		public GwtEvent.Type<SetPasswordEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(SetPasswordEventHandler handler) {
			handler.setPasswordSuccess(input, output);
		}
	}

	public class SetPasswordFailure extends GwtEvent<SetPasswordEventHandler> {
		private SetPasswordRequest input;
		private Throwable caught;

		public SetPasswordFailure(final SetPasswordRequest input, final Throwable caught) {
			this.input = input;
			this.caught = caught;
		}

		@Override
		public GwtEvent.Type<SetPasswordEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(SetPasswordEventHandler handler) {
			handler.setPasswordFailure(input, caught);
		}
	}

}