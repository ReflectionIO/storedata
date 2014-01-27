//  
//  SearchForItemEventHandler.java
//  reflection.io
//
//  Created by William Shakour on January 27, 2014.
//  Copyright © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyright © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.api.admin.shared.call.event;

import io.reflection.app.api.core.shared.call.SearchForItemRequest;
import io.reflection.app.api.core.shared.call.SearchForItemResponse;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface SearchForItemEventHandler extends EventHandler {
	public static final GwtEvent.Type<SearchForItemEventHandler> TYPE = new GwtEvent.Type<SearchForItemEventHandler>();

	public void searchForItemSuccess(final SearchForItemRequest input, final SearchForItemResponse output);

	public void searchForItemFailure(final SearchForItemRequest input, final Throwable caught);

	public class SearchForItemSuccess extends GwtEvent<SearchForItemEventHandler> {
		private SearchForItemRequest input;
		private SearchForItemResponse output;

		public SearchForItemSuccess(final SearchForItemRequest input, final SearchForItemResponse output) {
			this.input = input;
			this.output = output;
		}

		@Override
		public GwtEvent.Type<SearchForItemEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(SearchForItemEventHandler handler) {
			handler.searchForItemSuccess(input, output);
		}
	}

	public class SearchForItemFailure extends GwtEvent<SearchForItemEventHandler> {
		private SearchForItemRequest input;
		private Throwable caught;

		public SearchForItemFailure(final SearchForItemRequest input, final Throwable caught) {
			this.input = input;
			this.caught = caught;
		}

		@Override
		public GwtEvent.Type<SearchForItemEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(SearchForItemEventHandler handler) {
			handler.searchForItemFailure(input, caught);
		}
	}

}