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
import io.reflection.app.client.handler.NavigationEventHandler;
import io.reflection.app.client.part.linkaccount.IosMacLinkAccountForm;
import io.reflection.app.client.part.linkaccount.LinkableAccountFields;
import io.reflection.app.client.part.linkaccount.LinkedAccountChangeEvent.EVENT_TYPE;
import io.reflection.app.client.part.linkaccount.LinkedAccountChangeEvent.LinkedAccountChangeEventHandler;
import io.reflection.app.client.res.Styles;
import io.reflection.app.datatypes.shared.DataAccount;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ClickEvent;
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
	@UiField InlineHyperlink continueToLeaderboard;

	private LinkableAccountFields linkableAccount;

	public LinkItunesPage() {
		initWidget(uiBinder.createAndBindUi(this));

		iosMacForm.addLinkedAccountChangeEventHander(new LinkedAccountChangeEventHandler() {

			@Override
			public void onChange(DataAccount dataAccount, EVENT_TYPE eventType) {
				iosMacForm.setEnabled(false);
				iosMacForm.setStatusLoading("Linking");
				LinkedAccountController.get().linkAccount(linkableAccount.getAccountSourceId(), linkableAccount.getUsername(), linkableAccount.getPassword(),
						linkableAccount.getProperties()); // Link account
			}
		});

		continueToLeaderboard.setTargetHistoryToken(PageType.RanksPageType.asTargetHistoryToken(NavigationController.VIEW_ACTION_PARAMETER_VALUE,
				OVERALL_LIST_TYPE, FilterController.get().asRankFilterString()));
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
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.core.shared.call.event.LinkAccountEventHandler#linkAccountSuccess(io.reflection.app.api.core.shared.call.LinkAccountRequest,
	 * io.reflection.app.api.core.shared.call.LinkAccountResponse)
	 */
	@Override
	public void linkAccountSuccess(LinkAccountRequest input, LinkAccountResponse output) {
		if (output.status == StatusType.StatusTypeSuccess) {
			iosMacForm.setStatusSuccess("Account Linked!", 0);
			Document.get().getBody().addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().formSubmittedSuccessComplete());
			accountConnectAnimation.addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().plugsConnected());
			panelSuccess.addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().isShowing());
			// TODO tell the user the email will come

		} else if (output.error != null) {
			if (output.error.code == ApiError.InvalidDataAccountCredentials.getCode()) {
				iosMacForm.setStatusError("Invalid credentials!");
				linkableAccount.setUsernameError("iTunes Connect username or password entered incorrectly");
				linkableAccount.setPasswordError("iTunes Connect username or password entered incorrectly");
				linkableAccount.setFormErrors();
			} else if (output.error.code == ApiError.InvalidDataAccountVendor.getCode()) {
				iosMacForm.setStatusError("Invalid vendor ID!");
				iosMacForm.setVendorError("iTunes Connect vendor number entered incorrectly");
				linkableAccount.setFormErrors();
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
		// mLinkAccount.setEnabled(true);
		iosMacForm.setStatusError();
	}

	@UiHandler("linkAnotherAccount")
	void onLinkAnotherAccountClicked(ClickEvent event) {
		event.preventDefault();
		linkableAccount.resetForm();
		panelSuccess.removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().isShowing());
		accountConnectAnimation.removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().plugsConnected());
		Document.get().getBody().removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().formSubmittedSuccessComplete());
		// linkAccountBtn.resetStatus(); // TODO CHECK
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.NavigationEventHandler#navigationChanged(io.reflection.app.client.controller.NavigationController.Stack,
	 * io.reflection.app.client.controller.NavigationController.Stack)
	 */
	@Override
	public void navigationChanged(Stack previous, Stack current) {
		linkableAccount = iosMacForm;
		// mLinkableAccount.resetForm();
		iosMacForm.setVisible(true);
		linkableAccount.getFirstToFocus().setFocus(true);
	}

}
