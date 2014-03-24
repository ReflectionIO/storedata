//
//  LinkedAccountsPage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 17 Dec 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.page;

import io.reflection.app.api.core.shared.call.GetLinkedAccountsRequest;
import io.reflection.app.api.core.shared.call.GetLinkedAccountsResponse;
import io.reflection.app.api.core.shared.call.LinkAccountRequest;
import io.reflection.app.api.core.shared.call.LinkAccountResponse;
import io.reflection.app.api.core.shared.call.event.GetLinkedAccountsEventHandler;
import io.reflection.app.api.core.shared.call.event.LinkAccountEventHandler;
import io.reflection.app.api.shared.datatypes.Session;
import io.reflection.app.client.controller.EventController;
import io.reflection.app.client.controller.LinkedAccountController;
import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.controller.SessionController;
import io.reflection.app.client.controller.NavigationController.Stack;
import io.reflection.app.client.handler.EnterPressedEventHandler;
import io.reflection.app.client.handler.NavigationEventHandler;
import io.reflection.app.client.handler.user.SessionEventHandler;
import io.reflection.app.client.helper.FormHelper;
import io.reflection.app.client.part.linkaccount.IosMacLinkAccountForm;
import io.reflection.app.client.part.linkaccount.LinkableAccountFields;
import io.reflection.app.datatypes.shared.User;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Button;
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
public class LinkedAccountsPage extends Page implements NavigationEventHandler, LinkAccountEventHandler, GetLinkedAccountsEventHandler, SessionEventHandler {

	private static LinkedAccountsPageUiBinder uiBinder = GWT.create(LinkedAccountsPageUiBinder.class);

	interface LinkedAccountsPageUiBinder extends UiBinder<Widget, LinkedAccountsPage> {}

	@UiField InlineHyperlink mIosMacLink;
	@UiField InlineHyperlink mPlayLink;
	@UiField InlineHyperlink mAmazonLink;
	@UiField InlineHyperlink mWindowsPhoneLink;

	@UiField InlineHyperlink mMyAppsLink;
	@UiField InlineHyperlink mLinkedAccountsLink;

	@UiField FormPanel mForm;
	@UiField HTMLPanel mToolbar;
	@UiField IosMacLinkAccountForm mIosMacForm;

	@UiField Button mLinkAccount;

	// @UiField(provided = true) CellTree mAccounts;

	private LinkableAccountFields mLinkableAccount;

	// private String mAccountType;

	public LinkedAccountsPage() {
		initWidget(uiBinder.createAndBindUi(this));

		mLinkedAccountsLink.setTargetHistoryToken("users/linkedaccounts/" + SessionController.get().getLoggedInUser().id.toString());
		mMyAppsLink.setTargetHistoryToken("users/myapps/" + SessionController.get().getLoggedInUser().id.toString());

		addSoonTag(mPlayLink);

		// mIosMacLink.setTargetHistoryToken("users/linkedaccounts/" +
		// NavigationController.get().getStack().getParameter(0) + "/iosmac");

		showNoLinkedAccounts();

		LinkedAccountController.get().fetchLinkedAccounts();

		// CellTree.Resources res = GWT.create(CellTree.BasicResources.class);
		// mAccounts = new CellTree(LinkedAccountController.get(), null, res);
		// mAccounts.setAnimationEnabled(true);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.Composite#onAttach()
	 */
	@Override
	protected void onAttach() {
		super.onAttach();

		register(EventController.get().addHandlerToSource(NavigationEventHandler.TYPE, NavigationController.get(), this));
		register(EventController.get().addHandlerToSource(LinkAccountEventHandler.TYPE, LinkedAccountController.get(), this));
		register(EventController.get().addHandlerToSource(GetLinkedAccountsEventHandler.TYPE, LinkedAccountController.get(), this));
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
	 * @see io.reflection.app.client.handler.NavigationEventHandler#navigationChanged (io.reflection.app.admin.client.controller.NavigationController.Stack)
	 */
	@Override
	public void navigationChanged(Stack stack) {

		mLinkableAccount = null;

		if (NavigationController.get().getStack().getParameter(0) != null) {

			mIosMacLink.setTargetHistoryToken("users/linkedaccounts/" + NavigationController.get().getStack().getParameter(0) + "/iosmac");

			String accountType;
			if ((accountType = NavigationController.get().getStack().getParameter(1)) != null) {
				if ("iosmac".equals(accountType)) {
					mForm.setVisible(true);
					mLinkableAccount = mIosMacForm;
					mLinkableAccount.setOnEnterPressed(new EnterPressedEventHandler() {
						public void onEnterPressed() {
							mLinkAccount.click();
						}
					});
					mIosMacForm.setVisible(true);
					mLinkableAccount.getFirstToFocus().setFocus(true);
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

			LinkedAccountController.get().linkAccount(mLinkableAccount.getAccountSourceId(), mLinkableAccount.getUsername(), mLinkableAccount.getPassword(),
					mLinkableAccount.getProperties());
		} else {
			mLinkableAccount.setFormErrors();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.core.shared.call.event.LinkAccountEventHandler# linkAccountSuccess (io.reflection.app.api.core.shared.call.LinkAccountRequest,
	 * io.reflection.app.api.core.shared.call.LinkAccountResponse)
	 */
	@Override
	public void linkAccountSuccess(LinkAccountRequest input, LinkAccountResponse output) {
		if (output.status == StatusType.StatusTypeSuccess) {

			LinkedAccountController.get().fetchLinkedAccounts();
		} else {
			showError(output.error);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.core.shared.call.event.LinkAccountEventHandler# linkAccountFailure (io.reflection.app.api.core.shared.call.LinkAccountRequest,
	 * java.lang.Throwable)
	 */
	@Override
	public void linkAccountFailure(LinkAccountRequest input, Throwable caught) {
		showError(FormHelper.convertToError(caught));
	}

	private void showError(Error e) {

		mForm.setVisible(true);
		mToolbar.setVisible(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.core.shared.call.event.GetLinkedAccountsEventHandler #getLinkedAccountsSuccess(io.reflection.app.api.core.shared.call.
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
	 * @see io.reflection.app.api.core.shared.call.event.GetLinkedAccountsEventHandler #getLinkedAccountsFailure(io.reflection.app.api.core.shared.call.
	 * GetLinkedAccountsRequest, java.lang.Throwable)
	 */
	@Override
	public void getLinkedAccountsFailure(GetLinkedAccountsRequest input, Throwable caught) {
		if (LinkedAccountController.get().getLinkedAccounts() == null || LinkedAccountController.get().getLinkedAccounts().size() == 0) {
			showNoLinkedAccounts();
		}
	}

	private void showNoLinkedAccounts() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.user.SessionEventHandler#userLoggedIn(io.reflection.app.datatypes.shared.User,
	 * io.reflection.app.api.shared.datatypes.Session)
	 */
	@Override
	public void userLoggedIn(User user, Session session) {
		// TODO Add My Apps Link

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.user.SessionEventHandler#userLoggedOut()
	 */
	@Override
	public void userLoggedOut() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.user.SessionEventHandler#userLoginFailed(com.willshex.gson.json.service.shared.Error)
	 */
	@Override
	public void userLoginFailed(Error error) {

	}
}
