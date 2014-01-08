//  
//  GetAllTopItemsEventHandler.java
//  reflection.io
//
//  Created by William Shakour on December 27, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.api.core.shared.call.event;

import io.reflection.app.api.core.shared.call.GetAllTopItemsRequest;
import io.reflection.app.api.core.shared.call.GetAllTopItemsResponse;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface GetAllTopItemsEventHandler extends EventHandler {
	public static final GwtEvent.Type<GetAllTopItemsEventHandler> TYPE = new GwtEvent.Type<GetAllTopItemsEventHandler>();

	public void getAllTopItemsSuccess(final GetAllTopItemsRequest input, final GetAllTopItemsResponse output);

	public void getAllTopItemsFailure(final GetAllTopItemsRequest input, final Throwable caught);

	public class GetAllTopItemsSuccess extends GwtEvent<GetAllTopItemsEventHandler> {
		private GetAllTopItemsRequest input;
		private GetAllTopItemsResponse output;

		public GetAllTopItemsSuccess(final GetAllTopItemsRequest input, final GetAllTopItemsResponse output) {
			this.input = input;
			this.output = output;
		}

		@Override
		public GwtEvent.Type<GetAllTopItemsEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(GetAllTopItemsEventHandler handler) {
			handler.getAllTopItemsSuccess(input, output);
		}
	}

	public class GetAllTopItemsFailure extends GwtEvent<GetAllTopItemsEventHandler> {
		private GetAllTopItemsRequest input;
		private Throwable caught;

		public GetAllTopItemsFailure(final GetAllTopItemsRequest input, final Throwable caught) {
			this.input = input;
			this.caught = caught;
		}

		@Override
		public GwtEvent.Type<GetAllTopItemsEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(GetAllTopItemsEventHandler handler) {
			handler.getAllTopItemsFailure(input, caught);
		}
	}

}