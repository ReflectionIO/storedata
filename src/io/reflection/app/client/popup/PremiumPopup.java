//
//  PremiumPopup.java
//  storedata
//
//  Created by Stefano Capuzzi (capuzzistefano) on 26 Sep 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.popup;

import io.reflection.app.api.admin.shared.call.AssignRoleRequest;
import io.reflection.app.api.admin.shared.call.AssignRoleResponse;
import io.reflection.app.api.admin.shared.call.event.AssignRoleEventHandler;
import io.reflection.app.client.DefaultEventBus;
import io.reflection.app.client.component.LoadingButton;
import io.reflection.app.client.controller.SessionController;
import io.reflection.app.client.controller.UserController;
import io.reflection.app.client.page.PageType;
import io.reflection.app.client.res.Styles;
import io.reflection.app.datatypes.shared.User;
import io.reflection.app.shared.util.DataTypeHelper;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author Stefano Capuzzi (capuzzistefano)
 *
 */
public class PremiumPopup extends Composite implements AssignRoleEventHandler {

	private static PremiumPopupUiBinder uiBinder = GWT.create(PremiumPopupUiBinder.class);

	interface PremiumPopupUiBinder extends UiBinder<Widget, PremiumPopup> {}

	@UiField PopupBase popup;
	@UiField LoadingButton startFreeTrial;
	@UiField Button notRightNow;
	@UiField InlineHyperlink letUsKnow;
	@UiField Button continueBrowsing;

	public PremiumPopup() {
		initWidget(uiBinder.createAndBindUi(this));

	}

	public void show() {
		if (!this.asWidget().isAttached()) {
			RootPanel.get().add(this);
			DefaultEventBus.get().addHandlerToSource(AssignRoleEventHandler.TYPE, UserController.get(), this);
		}
		if (SessionController.get().loggedInUserHas(DataTypeHelper.ROLE_PREMIUM_CODE)) {
			popup.setStyleName(Styles.STYLES_INSTANCE.reflectionMainStyle().isSubmittedSuccess(), true);
		}
		popup.show();
	}

	@UiHandler("popup")
	void onPopupClosed(CloseEvent<PopupBase> event) {
		RootPanel.get().remove(this.asWidget());
	}

	@UiHandler({ "notRightNow", "letUsKnow", "continueBrowsing" })
	void onCloseLinksClicked(ClickEvent event) {
		event.preventDefault();
		popup.closePopup();
	}

	@UiHandler("startFreeTrial")
	void onStartFreeTrialClicked(ClickEvent event) {
		event.preventDefault();

		User user = SessionController.get().getLoggedInUser();
		if (user != null && user.id != null) {
			startFreeTrial.setStatusLoading(startFreeTrial.getText());
			// UserController.get().assignUserRoleCode(user.id, DataTypeHelper.ROLE_PREMIUM_CODE); // TODO add trial boolean
		} else {
			popup.closePopup();
			PageType.LoginPageType.show();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.admin.shared.call.event.AssignRoleEventHandler#assignRoleSuccess(io.reflection.app.api.admin.shared.call.AssignRoleRequest,
	 * io.reflection.app.api.admin.shared.call.AssignRoleResponse)
	 */
	@Override
	public void assignRoleSuccess(AssignRoleRequest input, AssignRoleResponse output) {
		if (output.status == StatusType.StatusTypeSuccess) {
			popup.setStyleName(Styles.STYLES_INSTANCE.reflectionMainStyle().isSubmittedSuccess(), true);
		} else {
			startFreeTrial.setStatusError();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.admin.shared.call.event.AssignRoleEventHandler#assignRoleFailure(io.reflection.app.api.admin.shared.call.AssignRoleRequest,
	 * java.lang.Throwable)
	 */
	@Override
	public void assignRoleFailure(AssignRoleRequest input, Throwable caught) {
		startFreeTrial.setStatusError();
	}

}
