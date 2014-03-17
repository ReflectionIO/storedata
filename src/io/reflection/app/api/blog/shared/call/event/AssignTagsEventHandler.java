//  
//  AssignTagsEventHandler.java
//  reflection.io
//
//  Created by William Shakour on March 16, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.api.blog.shared.call.event;

import io.reflection.app.api.blog.shared.call.AssignTagsRequest;
import io.reflection.app.api.blog.shared.call.AssignTagsResponse;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface AssignTagsEventHandler extends EventHandler {
	public static final GwtEvent.Type<AssignTagsEventHandler> TYPE = new GwtEvent.Type<AssignTagsEventHandler>();

	public void assignTagsSuccess(final AssignTagsRequest input, final AssignTagsResponse output);

	public void assignTagsFailure(final AssignTagsRequest input, final Throwable caught);

	public class AssignTagsSuccess extends GwtEvent<AssignTagsEventHandler> {
		private AssignTagsRequest input;
		private AssignTagsResponse output;

		public AssignTagsSuccess(final AssignTagsRequest input, final AssignTagsResponse output) {
			this.input = input;
			this.output = output;
		}

		@Override
		public GwtEvent.Type<AssignTagsEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(AssignTagsEventHandler handler) {
			handler.assignTagsSuccess(input, output);
		}
	}

	public class AssignTagsFailure extends GwtEvent<AssignTagsEventHandler> {
		private AssignTagsRequest input;
		private Throwable caught;

		public AssignTagsFailure(final AssignTagsRequest input, final Throwable caught) {
			this.input = input;
			this.caught = caught;
		}

		@Override
		public GwtEvent.Type<AssignTagsEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(AssignTagsEventHandler handler) {
			handler.assignTagsFailure(input, caught);
		}
	}

}