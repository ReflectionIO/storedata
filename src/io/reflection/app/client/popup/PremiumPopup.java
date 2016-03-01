//
//  PremiumPopup.java
//  storedata
//
//  Created by Stefano Capuzzi (capuzzistefano) on 26 Sep 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.popup;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.willshex.gson.json.service.shared.StatusType;

import io.reflection.app.api.core.shared.call.UpgradeAccountRequest;
import io.reflection.app.api.core.shared.call.UpgradeAccountResponse;
import io.reflection.app.api.core.shared.call.event.UpgradeAccountEventHandler;
import io.reflection.app.client.DefaultEventBus;
import io.reflection.app.client.component.LoadingButton;
import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.controller.SessionController;
import io.reflection.app.client.controller.UserController;
import io.reflection.app.client.page.PageType;
import io.reflection.app.client.res.Styles;
import io.reflection.app.datatypes.shared.User;
import io.reflection.app.shared.util.DataTypeHelper;

/**
 * @author Stefano Capuzzi (capuzzistefano)
 *
 */
public class PremiumPopup extends Composite implements UpgradeAccountEventHandler {

	private static PremiumPopupUiBinder uiBinder = GWT.create(PremiumPopupUiBinder.class);

	interface PremiumPopupUiBinder extends UiBinder<Widget, PremiumPopup> {}

	@UiField PopupBase popup;
	@UiField Element badge;
	@UiField Element title;
	@UiField Element subtitle;
	@UiField LoadingButton startFreeTrial;
	@UiField InlineHyperlink letUsKnow;
	@UiField Button continueBrowsing;

	private boolean userUpgraded = false;

	public PremiumPopup() {
		initWidget(uiBinder.createAndBindUi(this));

	}

	public void show(boolean isFeature) {
		if (!asWidget().isAttached()) {
			if (isFeature) {
				badge.getStyle().setDisplay(Display.INLINE);
				title.setInnerText("This is a Premium Feature");
				subtitle.setInnerText(
						"To use this feature click 'Start Free Trial' below and we'll upgrade your account to Developer Premium for free for the duration of the beta.");
			} else {
				badge.getStyle().setDisplay(Display.NONE);
				title.setInnerText("Upgrade For Free");
				subtitle.setInnerText(
						"No extra details required, just confirm below and we’ll instantly upgrade your account to Developer Premium for the duration of the beta absolutely free.");
			}
			popup.setStyleName(Styles.STYLES_INSTANCE.reflectionMainStyle().isSubmittedSuccess(), SessionController.get().isPremiumDeveloper());
			RootPanel.get().add(this);
			DefaultEventBus.get().addHandlerToSource(UpgradeAccountEventHandler.TYPE, UserController.get(), this);
		}
		if (SessionController.get().isPremiumDeveloper()) {
			popup.setStyleName(Styles.STYLES_INSTANCE.reflectionMainStyle().isSubmittedSuccess(), true);
		}
		popup.show();
	}

	public void hide() {
		popup.closePopup();
	}

	@UiHandler("popup")
	void onPopupClosed(CloseEvent<PopupBase> event) {
		if (userUpgraded) {
			if (NavigationController.get().getCurrentPage() == PageType.RanksPageType) {
				Window.Location.reload();
			}
		}

		RootPanel.get().remove(asWidget());
	}

	@UiHandler("startFreeTrial")
	void onStartFreeTrialClicked(ClickEvent event) {
		event.preventDefault();

		User user = SessionController.get().getLoggedInUser();
		if (user != null && user.id != null) {
			startFreeTrial.setStatusLoading(startFreeTrial.getText());
			UserController.get().upgradeAccount(DataTypeHelper.ROLE_PREMIUM_CODE);
		} else {
			popup.closePopup();
			PageType.LoginPageType.show();
		}
	}

	@UiHandler("continueBrowsing")
	void onContinueBrowsingClicked(ClickEvent event) {
		popup.closePopup();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.api.core.shared.call.event.UpgradeAccountEventHandler#upgradeAccountSuccess(io.reflection.app.api.core.shared.call.
	 * UpgradeAccountRequest , io.reflection.app.api.core.shared.call.UpgradeAccountResponse)
	 */
	@Override
	public void upgradeAccountSuccess(UpgradeAccountRequest input, UpgradeAccountResponse output) {
		if (output.status == StatusType.StatusTypeSuccess) {
			popup.setStyleName(Styles.STYLES_INSTANCE.reflectionMainStyle().isSubmittedSuccess(), true);
			startFreeTrial.setStatusSuccess();
			userUpgraded = true;
		} else {
			startFreeTrial.setStatusError();
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.api.core.shared.call.event.UpgradeAccountEventHandler#upgradeAccountFailure(io.reflection.app.api.core.shared.call.
	 * UpgradeAccountRequest , java.lang.Throwable)
	 */
	@Override
	public void upgradeAccountFailure(UpgradeAccountRequest input, Throwable caught) {
		startFreeTrial.setStatusError();
	}

}
