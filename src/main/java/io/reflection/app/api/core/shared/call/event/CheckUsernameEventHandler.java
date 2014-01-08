//  
//  CheckUsernameEventHandler.java
//  reflection.io
//
//  Created by William Shakour on December 27, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.api.core.shared.call.event;

import io.reflection.app.api.core.shared.call.CheckUsernameRequest;
import io.reflection.app.api.core.shared.call.CheckUsernameResponse;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface CheckUsernameEventHandler extends EventHandler {
	public static final GwtEvent.Type<CheckUsernameEventHandler> TYPE = new GwtEvent.Type<CheckUsernameEventHandler>();

	public void checkUsernameSuccess(final CheckUsernameRequest input, final CheckUsernameResponse output);

	public void checkUsernameFailure(final CheckUsernameRequest input, final Throwable caught);

	public class CheckUsernameSuccess extends GwtEvent<CheckUsernameEventHandler> {
		private CheckUsernameRequest input;
		private CheckUsernameResponse output;

		public CheckUsernameSuccess(final CheckUsernameRequest input, final CheckUsernameResponse output) {
			this.input = input;
			this.output = output;
		}

		@Override
		public GwtEvent.Type<CheckUsernameEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(CheckUsernameEventHandler handler) {
			handler.checkUsernameSuccess(input, output);
		}
	}

	public class CheckUsernameFailure extends GwtEvent<CheckUsernameEventHandler> {
		private CheckUsernameRequest input;
		private Throwable caught;

		public CheckUsernameFailure(final CheckUsernameRequest input, final Throwable caught) {
			this.input = input;
			this.caught = caught;
		}

		@Override
		public GwtEvent.Type<CheckUsernameEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(CheckUsernameEventHandler handler) {
			handler.checkUsernameFailure(input, caught);
		}
	}

}