//
//  LinkedAccountsPage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 17 Dec 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.admin.client.page;

import io.reflection.app.admin.client.controller.EventController;
import io.reflection.app.admin.client.controller.LinkedAccountController;
import io.reflection.app.admin.client.controller.NavigationController;
import io.reflection.app.admin.client.controller.NavigationController.Stack;
import io.reflection.app.admin.client.handler.NavigationEventHandler;
import io.reflection.app.admin.client.helper.AlertBoxHelper;
import io.reflection.app.admin.client.helper.FormHelper;
import io.reflection.app.admin.client.part.AlertBox;
import io.reflection.app.admin.client.part.AlertBox.AlertBoxType;
import io.reflection.app.admin.client.part.linkaccount.IosMacLinkAccountForm;
import io.reflection.app.admin.client.part.linkaccount.LinkableAccountFields;
import io.reflection.app.api.core.shared.call.GetLinkedAccountsRequest;
import io.reflection.app.api.core.shared.call.GetLinkedAccountsResponse;
import io.reflection.app.api.core.shared.call.LinkAccountRequest;
import io.reflection.app.api.core.shared.call.LinkAccountResponse;
import io.reflection.app.api.core.shared.call.event.GetLinkedAccountsEventHandler;
import io.reflection.app.api.core.shared.call.event.LinkAccountEventHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.Widget;
import com.willshex.gson.json.service.shared.Error;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author billy1380
 * 
 */
public class LinkedAccountsPage extends Composite implements NavigationEventHandler, LinkAccountEventHandler, GetLinkedAccountsEventHandler {

	private static LinkedAccountsPageUiBinder uiBinder = GWT.create(LinkedAccountsPageUiBinder.class);

	interface LinkedAccountsPageUiBinder extends UiBinder<Widget, LinkedAccountsPage> {}

	@UiField AlertBox mAlertBox;

	@UiField InlineHyperlink mIosMacLink;
	@UiField InlineHyperlink mPlayLink;
	@UiField InlineHyperlink mAmazonLink;
	@UiField InlineHyperlink mWindowsPhoneLink;

	@UiField FormPanel mForm;
	@UiField HTMLPanel mToolbar;
	@UiField IosMacLinkAccountForm mIosMacForm;

	private LinkableAccountFields mLinkableAccount;

	// private String mAccountType;

	public LinkedAccountsPage() {
		initWidget(uiBinder.createAndBindUi(this));

		addSoonTag(mPlayLink);

		// mIosMacLink.setTargetHistoryToken("users/linkedaccounts/" + NavigationController.get().getStack().getParameter(0) + "/iosmac");

		EventController.get().addHandlerToSource(NavigationEventHandler.TYPE, NavigationController.get(), this);
		EventController.get().addHandlerToSource(LinkAccountEventHandler.TYPE, LinkedAccountController.get(), this);
		EventController.get().addHandlerToSource(GetLinkedAccountsEventHandler.TYPE, LinkedAccountController.get(), this);

		showNoLinkedAccounts();

		LinkedAccountController.get().fetchLinkedAccounts();
	}

	/**
	 * @param link
	 */
	private void addSoonTag(InlineHyperlink link) {
		SpanElement s = DOM.createSpan().cast();
		s.setInnerText("Coming Soon!");
		s.addClassName("label");
		s.addClassName("label-danger");
		s.getStyle().setMarginLeft(10.0, Unit.PX);
		link.getElement().appendChild(s);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.reflection.app.admin.client.handler.NavigationEventHandler#navigationChanged(io.reflection.app.admin.client.controller.NavigationController.Stack)
	 */
	@Override
	public void navigationChanged(Stack stack) {

		mLinkableAccount = null;

		if (NavigationController.get().getStack().getParameter(0) != null) {

			mIosMacLink.setTargetHistoryToken("users/linkedaccounts/" + NavigationController.get().getStack().getParameter(0) + "/iosmac");

			String accountType;
			if ((accountType = NavigationController.get().getStack().getParameter(1)) != null) {
				if ("iosmac".equals(accountType)) {
					mIosMacForm.setVisible(true);
					mLinkableAccount = mIosMacForm;
					mForm.setVisible(true);
				} else {
					mIosMacForm.setVisible(false);
					mForm.setVisible(false);
				}
			} else {
				mIosMacForm.setVisible(false);
				mForm.setVisible(false);
			}
		}

	}

	@UiHandler("mLinkAccount")
	void onLinkAccount(ClickEvent event) {
		if (mLinkableAccount.validate()) {
			mForm.setVisible(false);
			mToolbar.setVisible(false);

			AlertBoxHelper.configureAlert(mAlertBox, AlertBoxType.InfoAlertBoxType, true, "Please wait",
					" - linking " + mLinkableAccount.getAccountSourceName() + " account...", false).setVisible(true);

			LinkedAccountController.get().linkAccount(mLinkableAccount.getAccountSourceId(), mLinkableAccount.getUsername(), mLinkableAccount.getPassword(),
					mLinkableAccount.getProperties());
		} else {

		}
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
			AlertBoxHelper.configureAlert(mAlertBox, AlertBoxType.InfoAlertBoxType, true, "Account added, please wait",
					" - getting updating linked accounts. Please note that linked account data will not be available immediatly please check back regularly.",
					false).setVisible(true);

			LinkedAccountController.get().fetchLinkedAccounts();
		} else {
			showError(output.error);
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
		showError(FormHelper.convertToError(caught));
	}

	private void showError(Error e) {
		AlertBoxHelper.showError(mAlertBox, e);

		mForm.setVisible(true);
		mToolbar.setVisible(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.core.shared.call.event.GetLinkedAccountsEventHandler#getLinkedAccountsSuccess(io.reflection.app.api.core.shared.call.
	 * GetLinkedAccountsRequest, io.reflection.app.api.core.shared.call.GetLinkedAccountsResponse)
	 */
	@Override
	public void getLinkedAccountsSuccess(GetLinkedAccountsRequest input, GetLinkedAccountsResponse output) {
		if (output.status == StatusType.StatusTypeSuccess) {
			if (LinkedAccountController.get().getLinkedAccountsCount() == 0) {
				showNoLinkedAccounts();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.core.shared.call.event.GetLinkedAccountsEventHandler#getLinkedAccountsFailure(io.reflection.app.api.core.shared.call.
	 * GetLinkedAccountsRequest, java.lang.Throwable)
	 */
	@Override
	public void getLinkedAccountsFailure(GetLinkedAccountsRequest input, Throwable caught) {
		if (LinkedAccountController.get().getLinkedAccounts() == null || LinkedAccountController.get().getLinkedAccounts().size() == 0) {
			showNoLinkedAccounts();
		}
	}

	private void showNoLinkedAccounts() {
		AlertBoxHelper.configureAlert(mAlertBox, AlertBoxType.WarningAlertBoxType, false, "No accounts found", " - You currently have no linked accounts.",
				false).setVisible(true);
	}
}
