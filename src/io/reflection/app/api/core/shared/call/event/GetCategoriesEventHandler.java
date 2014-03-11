//  
//  GetCategoriesEventHandler.java
//  reflection.io
//
//  Created by William Shakour on March 11, 2014.
//  Copyright © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyright © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.api.core.shared.call.event;

import io.reflection.app.api.core.shared.call.GetCategoriesRequest;
import io.reflection.app.api.core.shared.call.GetCategoriesResponse;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface GetCategoriesEventHandler extends EventHandler {
	public static final GwtEvent.Type<GetCategoriesEventHandler> TYPE = new GwtEvent.Type<GetCategoriesEventHandler>();

	public void getCategoriesSuccess(final GetCategoriesRequest input, final GetCategoriesResponse output);

	public void getCategoriesFailure(final GetCategoriesRequest input, final Throwable caught);

	public class GetCategoriesSuccess extends GwtEvent<GetCategoriesEventHandler> {
		private GetCategoriesRequest input;
		private GetCategoriesResponse output;

		public GetCategoriesSuccess(final GetCategoriesRequest input, final GetCategoriesResponse output) {
			this.input = input;
			this.output = output;
		}

		@Override
		public GwtEvent.Type<GetCategoriesEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(GetCategoriesEventHandler handler) {
			handler.getCategoriesSuccess(input, output);
		}
	}

	public class GetCategoriesFailure extends GwtEvent<GetCategoriesEventHandler> {
		private GetCategoriesRequest input;
		private Throwable caught;

		public GetCategoriesFailure(final GetCategoriesRequest input, final Throwable caught) {
			this.input = input;
			this.caught = caught;
		}

		@Override
		public GwtEvent.Type<GetCategoriesEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(GetCategoriesEventHandler handler) {
			handler.getCategoriesFailure(input, caught);
		}
	}

}