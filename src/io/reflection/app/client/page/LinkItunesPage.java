//
//  LinkItunesPage.java
//  storedata
//
//  Created by Stefano Capuzzi on 11 Feb 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page;

import io.reflection.app.api.core.shared.call.LinkAccountRequest;
import io.reflection.app.api.core.shared.call.LinkAccountResponse;
import io.reflection.app.api.core.shared.call.event.LinkAccountEventHandler;
import io.reflection.app.api.shared.ApiError;
import io.reflection.app.client.controller.EventController;
import io.reflection.app.client.controller.LinkedAccountController;
import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.controller.NavigationController.Stack;
import io.reflection.app.client.handler.EnterPressedEventHandler;
import io.reflection.app.client.handler.NavigationEventHandler;
import io.reflection.app.client.part.Preloader;
import io.reflection.app.client.part.linkaccount.IosMacLinkAccountForm;
import io.reflection.app.client.part.linkaccount.LinkableAccountFields;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author stefanocapuzzi
 * 
 */
public class LinkItunesPage extends Page implements NavigationEventHandler, LinkAccountEventHandler {

	private static LinkItunesPageUiBinder uiBinder = GWT.create(LinkItunesPageUiBinder.class);

	interface LinkItunesPageUiBinder extends UiBinder<Widget, LinkItunesPage> {}

	@UiField HTMLPanel mPanel;
	@UiField FormPanel form;

	@UiField IosMacLinkAccountForm mIosMacForm;

	@UiField Button mLinkAccount;

	@UiField Preloader preloader;

	private LinkableAccountFields mLinkableAccount;

	public LinkItunesPage() {
		initWidget(uiBinder.createAndBindUi(this));

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
	}

	@UiHandler("mLinkAccount")
	void onLinkAccountClicked(ClickEvent event) {
		if (mLinkableAccount.validate()) {
			mLinkableAccount.setFormErrors();
			// mLinkAccount.setEnabled(false);
			preloader.show();
			LinkedAccountController.get().linkAccount(mLinkableAccount.getAccountSourceId(), mLinkableAccount.getUsername(), mLinkableAccount.getPassword(),
					mLinkableAccount.getProperties()); // Link account
		} else {
			mLinkableAccount.setFormErrors();
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
			mLinkableAccount.resetForm();
			PageType.ReadyToStartPageType.show();
		} else if (output.error != null) {
			if (output.error.code == ApiError.InvalidDataAccountCredentials.getCode()) {
				mLinkableAccount.setUsernameError("AppleConnect account or password entered incorrectly");
				mLinkableAccount.setPasswordError("AppleConnect account or password entered incorrectly");
				mLinkableAccount.setFormErrors();
			} else if (output.error.code == ApiError.InvalidDataAccountVendor.getCode()) {
				mLinkableAccount.setVendorError("AppleConnect vendor number entered incorrectly");
				mLinkableAccount.setFormErrors();
			}
		}
		// mLinkAccount.setEnabled(true);
		preloader.hide();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.core.shared.call.event.LinkAccountEventHandler#linkAccountFailure(io.reflection.app.api.core.shared.call.LinkAccountRequest,
	 * java.lang.Throwable)
	 */
	@Override
	public void linkAccountFailure(LinkAccountRequest input, Throwable caught) {
		mLinkableAccount.resetForm();
		// mLinkAccount.setEnabled(true);
		preloader.hide();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.NavigationEventHandler#navigationChanged(io.reflection.app.client.controller.NavigationController.Stack,
	 * io.reflection.app.client.controller.NavigationController.Stack)
	 */
	@Override
	public void navigationChanged(Stack previous, Stack current) {
		mLinkableAccount = mIosMacForm;

		mLinkableAccount.setOnEnterPressed(new EnterPressedEventHandler() {
			public void onEnterPressed() {
				mLinkAccount.click();
			}
		});

		mIosMacForm.setVisible(true);
		mLinkableAccount.getFirstToFocus().setFocus(true);
	}

}
