package io.reflection.app.api.core.shared.call.event;

import io.reflection.app.api.core.shared.call.UpgradeAccountRequest;
import io.reflection.app.api.core.shared.call.UpgradeAccountResponse;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface UpgradeAccountEventHandler extends EventHandler {
	public static final GwtEvent.Type<UpgradeAccountEventHandler> TYPE = new GwtEvent.Type<UpgradeAccountEventHandler>();

	public void upgradeAccountSuccess(final UpgradeAccountRequest input, final UpgradeAccountResponse output);

	public void upgradeAccountFailure(final UpgradeAccountRequest input, final Throwable caught);

	public class UpgradeAccountSuccess extends GwtEvent<UpgradeAccountEventHandler> {
		private UpgradeAccountRequest input;
		private UpgradeAccountResponse output;

		public UpgradeAccountSuccess(final UpgradeAccountRequest input, final UpgradeAccountResponse output) {
			this.input = input;
			this.output = output;
		}

		@Override
		public GwtEvent.Type<UpgradeAccountEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(UpgradeAccountEventHandler handler) {
			handler.upgradeAccountSuccess(input, output);
		}
	}

	public class UpgradeAccountFailure extends GwtEvent<UpgradeAccountEventHandler> {
		private UpgradeAccountRequest input;
		private Throwable caught;

		public UpgradeAccountFailure(final UpgradeAccountRequest input, final Throwable caught) {
			this.input = input;
			this.caught = caught;
		}

		@Override
		public GwtEvent.Type<UpgradeAccountEventHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(UpgradeAccountEventHandler handler) {
			handler.upgradeAccountFailure(input, caught);
		}
	}

}