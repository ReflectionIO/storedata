//  
//  GetForumsEventHandler.java
//  reflection.io
//
//  Created by William Shakour on April 5, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.api.forum.shared.call.event;

import io.reflection.app.api.forum.shared.call.GetForumsRequest;
import io.reflection.app.api.forum.shared.call.GetForumsResponse;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface GetForumsEventHandler extends EventHandler {
	public static final GwtEvent.Type<GetForumsEventHandler> TYPE = new GwtEvent.Type<GetForumsEventHandler>();

	public void getForumsSuccess(final GetForumsRequest input, final GetForumsResponse output);

	public void getForumsFailure(final GetForumsRequest input, final Throwable caught);

	public class GetForumsSuccess extends GwtEvent<GetForumsEventHandler> {
		private GetForumsRequest input;
		private GetForumsResponse output;

		public GetForumsSuccess(final GetForumsRequest input, final GetForumsResponse output) {
			this.input = input;
			this.output = output;
		}

		@Override
		public GwtEvent.Type<GetForumsEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(GetForumsEventHandler handler) {
			handler.getForumsSuccess(input, output);
		}
	}

	public class GetForumsFailure extends GwtEvent<GetForumsEventHandler> {
		private GetForumsRequest input;
		private Throwable caught;

		public GetForumsFailure(final GetForumsRequest input, final Throwable caught) {
			this.input = input;
			this.caught = caught;
		}

		@Override
		public GwtEvent.Type<GetForumsEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(GetForumsEventHandler handler) {
			handler.getForumsFailure(input, caught);
		}
	}

}