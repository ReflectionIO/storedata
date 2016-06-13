//  
//  LinkAccountEventHandler.java
//  reflection.io
//
//  Created by William Shakour on December 27, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.api.core.shared.call.event;

import io.reflection.app.api.core.shared.call.LinkGoogleAccountRequest;
import io.reflection.app.api.core.shared.call.LinkGoogleAccountResponse;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface LinkGoogleAccountEventHandler extends EventHandler {
	public static final GwtEvent.Type<LinkGoogleAccountEventHandler> TYPE = new GwtEvent.Type<LinkGoogleAccountEventHandler>();

	public void linkGoogleAccountSuccess(final LinkGoogleAccountRequest input, final LinkGoogleAccountResponse output);

	public void linkGoogleAccountFailure(final LinkGoogleAccountRequest input, final Throwable caught);

	public class LinkGoogleAccountSuccess extends GwtEvent<LinkGoogleAccountEventHandler> {
		private LinkGoogleAccountRequest input;
		private LinkGoogleAccountResponse output;

		public LinkGoogleAccountSuccess(final LinkGoogleAccountRequest input, final LinkGoogleAccountResponse output) {
			this.input = input;
			this.output = output;
		}

		@Override
		public GwtEvent.Type<LinkGoogleAccountEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(LinkGoogleAccountEventHandler handler) {
			handler.linkGoogleAccountSuccess(input, output);
		}
	}

	public class LinkGoogleAccountFailure extends GwtEvent<LinkGoogleAccountEventHandler> {
		private LinkGoogleAccountRequest input;
		private Throwable caught;

		public LinkGoogleAccountFailure(final LinkGoogleAccountRequest input, final Throwable caught) {
			this.input = input;
			this.caught = caught;
		}

		@Override
		public GwtEvent.Type<LinkGoogleAccountEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(LinkGoogleAccountEventHandler handler) {
			handler.linkGoogleAccountFailure(input, caught);
		}
	}

}