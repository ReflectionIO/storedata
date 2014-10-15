//  
//  GetDataAccountFetchesEventHandler.java
//  reflection.io
//
//  Created by William Shakour on October 10, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.api.admin.shared.call.event;

import io.reflection.app.api.admin.shared.call.GetDataAccountFetchesRequest;
import io.reflection.app.api.admin.shared.call.GetDataAccountFetchesResponse;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface GetDataAccountFetchesEventHandler extends EventHandler {
	public static final GwtEvent.Type<GetDataAccountFetchesEventHandler> TYPE = new GwtEvent.Type<GetDataAccountFetchesEventHandler>();

	public void getDataAccountFetchesSuccess(final GetDataAccountFetchesRequest input, final GetDataAccountFetchesResponse output);

	public void getDataAccountFetchesFailure(final GetDataAccountFetchesRequest input, final Throwable caught);

	public class GetDataAccountFetchesSuccess extends GwtEvent<GetDataAccountFetchesEventHandler> {
		private GetDataAccountFetchesRequest input;
		private GetDataAccountFetchesResponse output;

		public GetDataAccountFetchesSuccess(final GetDataAccountFetchesRequest input, final GetDataAccountFetchesResponse output) {
			this.input = input;
			this.output = output;
		}

		@Override
		public GwtEvent.Type<GetDataAccountFetchesEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(GetDataAccountFetchesEventHandler handler) {
			handler.getDataAccountFetchesSuccess(input, output);
		}
	}

	public class GetDataAccountFetchesFailure extends GwtEvent<GetDataAccountFetchesEventHandler> {
		private GetDataAccountFetchesRequest input;
		private Throwable caught;

		public GetDataAccountFetchesFailure(final GetDataAccountFetchesRequest input, final Throwable caught) {
			this.input = input;
			this.caught = caught;
		}

		@Override
		public GwtEvent.Type<GetDataAccountFetchesEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(GetDataAccountFetchesEventHandler handler) {
			handler.getDataAccountFetchesFailure(input, caught);
		}
	}

}