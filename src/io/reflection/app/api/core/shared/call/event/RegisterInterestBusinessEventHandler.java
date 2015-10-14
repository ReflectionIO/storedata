//
//  RegisterInterestBusinessEventHandler.java
//  storedata
//
//  Created by Stefano Capuzzi (capuzzistefano) on 12 Oct 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.api.core.shared.call.event;

import io.reflection.app.api.core.shared.call.RegisterInterestBusinessRequest;
import io.reflection.app.api.core.shared.call.RegisterInterestBusinessResponse;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * @author Stefano Capuzzi (capuzzistefano)
 *
 */
public interface RegisterInterestBusinessEventHandler extends EventHandler {
	public static final GwtEvent.Type<RegisterInterestBusinessEventHandler> TYPE = new GwtEvent.Type<RegisterInterestBusinessEventHandler>();

	public void registerInterestBusinessSuccess(final RegisterInterestBusinessRequest input, final RegisterInterestBusinessResponse output);

	public void registerInterestBusinessFailure(final RegisterInterestBusinessRequest input, final Throwable caught);

	public class RegisterInterestBusinessSuccess extends GwtEvent<RegisterInterestBusinessEventHandler> {
		private RegisterInterestBusinessRequest input;
		private RegisterInterestBusinessResponse output;

		public RegisterInterestBusinessSuccess(final RegisterInterestBusinessRequest input, final RegisterInterestBusinessResponse output) {
			this.input = input;
			this.output = output;
		}

		@Override
		public GwtEvent.Type<RegisterInterestBusinessEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(RegisterInterestBusinessEventHandler handler) {
			handler.registerInterestBusinessSuccess(input, output);
		}
	}

	public class RegisterInterestBusinessFailure extends GwtEvent<RegisterInterestBusinessEventHandler> {
		private RegisterInterestBusinessRequest input;
		private Throwable caught;

		public RegisterInterestBusinessFailure(final RegisterInterestBusinessRequest input, final Throwable caught) {
			this.input = input;
			this.caught = caught;
		}

		@Override
		public GwtEvent.Type<RegisterInterestBusinessEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(RegisterInterestBusinessEventHandler handler) {
			handler.registerInterestBusinessFailure(input, caught);
		}
	}
}
