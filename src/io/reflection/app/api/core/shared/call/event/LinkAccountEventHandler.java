//  
//  LinkAccountEventHandler.java
//  reflection.io
//
//  Created by William Shakour on December 27, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.api.core.shared.call.event;

import io.reflection.app.api.core.shared.call.LinkAccountRequest;
import io.reflection.app.api.core.shared.call.LinkAccountResponse;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface LinkAccountEventHandler extends EventHandler {
	public static final GwtEvent.Type<LinkAccountEventHandler> TYPE = new GwtEvent.Type<LinkAccountEventHandler>();

	public void linkAccountSuccess(final LinkAccountRequest input, final LinkAccountResponse output);

	public void linkAccountFailure(final LinkAccountRequest input, final Throwable caught);

	public class LinkAccountSuccess extends GwtEvent<LinkAccountEventHandler> {
		private LinkAccountRequest input;
		private LinkAccountResponse output;

		public LinkAccountSuccess(final LinkAccountRequest input, final LinkAccountResponse output) {
			this.input = input;
			this.output = output;
		}

		@Override
		public GwtEvent.Type<LinkAccountEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(LinkAccountEventHandler handler) {
			handler.linkAccountSuccess(input, output);
		}
	}

	public class LinkAccountFailure extends GwtEvent<LinkAccountEventHandler> {
		private LinkAccountRequest input;
		private Throwable caught;

		public LinkAccountFailure(final LinkAccountRequest input, final Throwable caught) {
			this.input = input;
			this.caught = caught;
		}

		@Override
		public GwtEvent.Type<LinkAccountEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(LinkAccountEventHandler handler) {
			handler.linkAccountFailure(input, caught);
		}
	}

}