//  
//  JoinDataAccountEventHandler.java
//  reflection.io
//
//  Created by William Shakour on October 16, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.api.admin.shared.call.event;

import io.reflection.app.api.admin.shared.call.JoinDataAccountRequest;
import io.reflection.app.api.admin.shared.call.JoinDataAccountResponse;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface JoinDataAccountEventHandler extends EventHandler {
	public static final GwtEvent.Type<JoinDataAccountEventHandler> TYPE = new GwtEvent.Type<JoinDataAccountEventHandler>();

	public void joinDataAccountSuccess(final JoinDataAccountRequest input, final JoinDataAccountResponse output);

	public void joinDataAccountFailure(final JoinDataAccountRequest input, final Throwable caught);

	public class JoinDataAccountSuccess extends GwtEvent<JoinDataAccountEventHandler> {
		private JoinDataAccountRequest input;
		private JoinDataAccountResponse output;

		public JoinDataAccountSuccess(final JoinDataAccountRequest input, final JoinDataAccountResponse output) {
			this.input = input;
			this.output = output;
		}

		@Override
		public GwtEvent.Type<JoinDataAccountEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(JoinDataAccountEventHandler handler) {
			handler.joinDataAccountSuccess(input, output);
		}
	}

	public class JoinDataAccountFailure extends GwtEvent<JoinDataAccountEventHandler> {
		private JoinDataAccountRequest input;
		private Throwable caught;

		public JoinDataAccountFailure(final JoinDataAccountRequest input, final Throwable caught) {
			this.input = input;
			this.caught = caught;
		}

		@Override
		public GwtEvent.Type<JoinDataAccountEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(JoinDataAccountEventHandler handler) {
			handler.joinDataAccountFailure(input, caught);
		}
	}

}