//
//  LinkItunesPage.java
//  storedata
//
//  Created by Stefano Capuzzi on 11 Feb 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page;

import static io.reflection.app.client.controller.FilterController.OVERALL_LIST_TYPE;
import io.reflection.app.api.core.shared.call.LinkAccountRequest;
import io.reflection.app.api.core.shared.call.LinkAccountResponse;
import io.reflection.app.api.core.shared.call.event.LinkAccountEventHandler;
import io.reflection.app.api.shared.ApiError;
import io.reflection.app.client.DefaultEventBus;
import io.reflection.app.client.controller.FilterController;
import io.reflection.app.client.controller.LinkedAccountController;
import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.controller.NavigationController.Stack;
import io.reflection.app.client.controller.SessionController;
import io.reflection.app.client.handler.NavigationEventHandler;
import io.reflection.app.client.part.linkaccount.IosMacLinkAccountForm;
import io.reflection.app.client.part.linkaccount.LinkedAccountChangeEvent.EVENT_TYPE;
import io.reflection.app.client.part.linkaccount.LinkedAccountChangeEvent.LinkedAccountChangeEventHandler;
import io.reflection.app.client.res.Styles;
import io.reflection.app.datatypes.shared.DataAccount;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.Widget;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author stefanocapuzzi
 * 
 */
public class LinkItunesPage extends Page implements NavigationEventHandler, LinkAccountEventHandler {

	private static LinkItunesPageUiBinder uiBinder = GWT.create(LinkItunesPageUiBinder.class);

	interface LinkItunesPageUiBinder extends UiBinder<Widget, LinkItunesPage> {};

	@UiField DivElement accountConnectAnimation;
	@UiField IosMacLinkAccountForm iosMacForm;
	@UiField DivElement panelSuccess;
	@UiField Anchor linkAnotherAccount;
	@UiField InlineHyperlink continueToLoggedInHome;
	@UiField InlineHyperlink leaderboardPanelLink;
	@UiField InlineHyperlink myDataPanelLink;
	@UiField Element plugLogoContainer;

	// private LinkableAccountFields linkableAccount;

	public LinkItunesPage() {
		initWidget(uiBinder.createAndBindUi(this));

		iosMacForm.addLinkedAccountChangeEventHander(new LinkedAccountChangeEventHandler() {

			@Override
			public void onChange(DataAccount dataAccount, EVENT_TYPE eventType) {
				iosMacForm.setEnabled(false);
				iosMacForm.setStatusLoading("Loading");
				Document.get().getBody().addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().formSubmittedLoading());
				LinkedAccountController.get().linkAccount(iosMacForm.getAccountSourceId(), iosMacForm.getUsername(), iosMacForm.getPassword(),
						iosMacForm.getProperties()); // Link account
			}
		});

		continueToLoggedInHome.setTargetHistoryToken(PageType.HomePageType.asTargetHistoryToken());
		leaderboardPanelLink.setTargetHistoryToken(PageType.RanksPageType.asTargetHistoryToken(NavigationController.VIEW_ACTION_PARAMETER_VALUE,
				OVERALL_LIST_TYPE, FilterController.get().asRankFilterString()));

		// plugLogoImg
		// .setAttribute(
		// "xlink:href",
		// "data:image/png;base64,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");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.Composite#onAttach()
	 */
	@Override
	protected void onAttach() {
		Document.get().getBody().addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().accountAccessPage());
		Document.get().getBody().addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().connectAccountIsShowing());
		super.onAttach();

		register(DefaultEventBus.get().addHandlerToSource(NavigationEventHandler.TYPE, NavigationController.get(), this));
		register(DefaultEventBus.get().addHandlerToSource(LinkAccountEventHandler.TYPE, LinkedAccountController.get(), this));

		if (plugLogoContainer.getChildCount() == 2) { // Add blink effect
			plugLogoContainer
					.setInnerSafeHtml(SafeHtmlUtils.fromSafeConstant(plugLogoContainer.getInnerHTML()
							+ " <image class=\"plug-logo-part-glow\" overflow=\"visible\" opacity=\"0.3\" width=\"55\" height=\"57\" xlink:href=\"data:image/png;base64,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\" transform=\"matrix(1 0 0 1 19.1082 16.3411)\"></image>"));
		}

		if (SessionController.get().getLoggedInUser() != null) {
			myDataPanelLink.setTargetHistoryToken(PageType.UsersPageType.asTargetHistoryToken(PageType.MyAppsPageType.toString(), SessionController.get()
					.getLoggedInUser().id.toString(), FilterController.get().asMyAppsFilterString()));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.page.Page#onDetach()
	 */
	@Override
	protected void onDetach() {
		super.onDetach();

		Document.get().getBody().removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().accountAccessPage());
		Document.get().getBody().removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().connectAccountIsShowing());

		// Reset form status
		iosMacForm.resetForm();
		iosMacForm.resetButtonStatus();
		panelSuccess.removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().isShowing());
		accountConnectAnimation.removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().plugsConnected());
		Document.get().getBody().removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().formSubmittedLoading());
		Document.get().getBody().removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().formSubmittedSuccessComplete());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.core.shared.call.event.LinkAccountEventHandler#linkAccountSuccess(io.reflection.app.api.core.shared.call.LinkAccountRequest,
	 * io.reflection.app.api.core.shared.call.LinkAccountResponse)
	 */
	@Override
	public void linkAccountSuccess(LinkAccountRequest input, LinkAccountResponse output) {
		Document.get().getBody().removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().formSubmittedLoading());
		if (output.status == StatusType.StatusTypeSuccess) {
			iosMacForm.setStatusSuccess("Account Linked!", 0);
			Document.get().getBody().addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().formSubmittedSuccessComplete());
			accountConnectAnimation.addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().plugsConnected());
			panelSuccess.addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().isShowing());
		} else if (output.error != null) {
			if (output.error.code.intValue() == ApiError.InvalidDataAccountCredentials.getCode()) {
				iosMacForm.setStatusError("Invalid credentials!");
				iosMacForm.setUsernameError("iTunes Connect username or password entered incorrectly");
				iosMacForm.setPasswordError("iTunes Connect username or password entered incorrectly");
				iosMacForm.setFormErrors();
			} else if (output.error.code.intValue() == ApiError.InvalidDataAccountVendor.getCode()) {
				iosMacForm.setStatusError("Invalid vendor ID!");
				iosMacForm.setVendorError("iTunes Connect vendor number entered incorrectly");
				iosMacForm.setFormErrors();
			} else if (output.error.code == ApiError.DuplicateVendorId.getCode()) {
				iosMacForm.setStatusError("Account already linked!");
				iosMacForm.setVendorError("The vendor ID you entered is already in use");
				iosMacForm.setFormErrors();
			} else {
				iosMacForm.setStatusError();
			}
			iosMacForm.setEnabled(true);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.core.shared.call.event.LinkAccountEventHandler#linkAccountFailure(io.reflection.app.api.core.shared.call.LinkAccountRequest,
	 * java.lang.Throwable)
	 */
	@Override
	public void linkAccountFailure(LinkAccountRequest input, Throwable caught) {
		Document.get().getBody().removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().formSubmittedLoading());
		iosMacForm.setStatusError();
	}

	@UiHandler("linkAnotherAccount")
	void onLinkAnotherAccountClicked(ClickEvent event) {
		event.preventDefault();
		iosMacForm.resetForm();
		iosMacForm.resetButtonStatus();
		panelSuccess.removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().isShowing());
		accountConnectAnimation.removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().plugsConnected());
		Document.get().getBody().removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().formSubmittedSuccessComplete());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.NavigationEventHandler#navigationChanged(io.reflection.app.client.controller.NavigationController.Stack,
	 * io.reflection.app.client.controller.NavigationController.Stack)
	 */
	@Override
	public void navigationChanged(Stack previous, Stack current) {
		iosMacForm.setVisible(true);
		iosMacForm.getFirstToFocus().setFocus(true);
	}

}
