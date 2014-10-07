//  
//  UpdateEmailTemplateEventHandler.java
//  reflection.io
//
//  Created by William Shakour on October 6, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.api.admin.shared.call.event;

import io.reflection.app.api.admin.shared.call.UpdateEmailTemplateRequest;
import io.reflection.app.api.admin.shared.call.UpdateEmailTemplateResponse;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface UpdateEmailTemplateEventHandler extends EventHandler {
	public static final GwtEvent.Type<UpdateEmailTemplateEventHandler> TYPE = new GwtEvent.Type<UpdateEmailTemplateEventHandler>();

	public void updateEmailTemplateSuccess(final UpdateEmailTemplateRequest input, final UpdateEmailTemplateResponse output);

	public void updateEmailTemplateFailure(final UpdateEmailTemplateRequest input, final Throwable caught);

	public class UpdateEmailTemplateSuccess extends GwtEvent<UpdateEmailTemplateEventHandler> {
		private UpdateEmailTemplateRequest input;
		private UpdateEmailTemplateResponse output;

		public UpdateEmailTemplateSuccess(final UpdateEmailTemplateRequest input, final UpdateEmailTemplateResponse output) {
			this.input = input;
			this.output = output;
		}

		@Override
		public GwtEvent.Type<UpdateEmailTemplateEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(UpdateEmailTemplateEventHandler handler) {
			handler.updateEmailTemplateSuccess(input, output);
		}
	}

	public class UpdateEmailTemplateFailure extends GwtEvent<UpdateEmailTemplateEventHandler> {
		private UpdateEmailTemplateRequest input;
		private Throwable caught;

		public UpdateEmailTemplateFailure(final UpdateEmailTemplateRequest input, final Throwable caught) {
			this.input = input;
			this.caught = caught;
		}

		@Override
		public GwtEvent.Type<UpdateEmailTemplateEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(UpdateEmailTemplateEventHandler handler) {
			handler.updateEmailTemplateFailure(input, caught);
		}
	}

}