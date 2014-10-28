//  
//  AssignPermissionEventHandler.java
//  reflection.io
//
//  Created by William Shakour on September 17, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.api.admin.shared.call.event;

import io.reflection.app.api.admin.shared.call.AssignPermissionRequest;
import io.reflection.app.api.admin.shared.call.AssignPermissionResponse;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface AssignPermissionEventHandler extends EventHandler {
	public static final GwtEvent.Type<AssignPermissionEventHandler> TYPE = new GwtEvent.Type<AssignPermissionEventHandler>();

	public void assignPermissionSuccess(final AssignPermissionRequest input, final AssignPermissionResponse output);

	public void assignPermissionFailure(final AssignPermissionRequest input, final Throwable caught);

	public class AssignPermissionSuccess extends GwtEvent<AssignPermissionEventHandler> {
		private AssignPermissionRequest input;
		private AssignPermissionResponse output;

		public AssignPermissionSuccess(final AssignPermissionRequest input, final AssignPermissionResponse output) {
			this.input = input;
			this.output = output;
		}

		@Override
		public GwtEvent.Type<AssignPermissionEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(AssignPermissionEventHandler handler) {
			handler.assignPermissionSuccess(input, output);
		}
	}

	public class AssignPermissionFailure extends GwtEvent<AssignPermissionEventHandler> {
		private AssignPermissionRequest input;
		private Throwable caught;

		public AssignPermissionFailure(final AssignPermissionRequest input, final Throwable caught) {
			this.input = input;
			this.caught = caught;
		}

		@Override
		public GwtEvent.Type<AssignPermissionEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(AssignPermissionEventHandler handler) {
			handler.assignPermissionFailure(input, caught);
		}
	}

}