//
//  RegisterPage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 23 Nov 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.page;

import io.reflection.app.client.controller.EventController;
import io.reflection.app.client.controller.UserController;
import io.reflection.app.client.handler.user.UserRegisteredEventHandler;
import io.reflection.app.client.part.AlertBox;
import io.reflection.app.client.part.register.RegisterForm;
import io.reflection.app.client.part.register.ThankYouRegisterPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.willshex.gson.json.service.shared.Error;

/**
 * @author billy1380
 * 
 */
public class RegisterPage extends Page implements UserRegisteredEventHandler {

	private static RegisterPageUiBinder uiBinder = GWT.create(RegisterPageUiBinder.class);

	interface RegisterPageUiBinder extends UiBinder<Widget, RegisterPage> {}

	@UiField HTMLPanel mPanel;

	@UiField RegisterForm mRegisterForm;

	@UiField ThankYouRegisterPanel mThankYouRegisterPanel;

	@UiField AlertBox mAlertBox;

	public RegisterPage() {
		initWidget(uiBinder.createAndBindUi(this));

		
	}

	/**
	 * Fire the register button when pressing the 'enter' key on one of the register form fields
	 * 
	 * @param event
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.Composite#onAttach()
	 */
	@Override
	protected void onAttach() {
		super.onAttach();

		register(EventController.get().addHandlerToSource(UserRegisteredEventHandler.TYPE, UserController.get(), this));

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.user.UserRegisteredEventHandler#userRegistered(java.lang.String)
	 */
	@Override
	public void userRegistered(String email) {
		// final String username = email;

		/*
		 * AlertBoxHelper.configureAlert(mAlertBox, AlertBoxType.SuccessAlertBoxType, false, "Account created",
		 * " - you can now login and use Reflection.io.",false).setVisible(true);
		 * 
		 * Timer t = new Timer() {
		 * 
		 * @Override public void run() { History.newItem("login/" + username); } };
		 * 
		 * t.schedule(2000);
		 */

		mRegisterForm.setVisible(false);
		mThankYouRegisterPanel.setVisible(true);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.user.UserRegisteredEventHandler#userRegistrationFailed(com.willshex.gson.json.service.shared.Error)
	 */
	@Override
	public void userRegistrationFailed(Error error) {
		// AlertBoxHelper.configureAlert(mAlertBox, AlertBoxType.DangerAlertBoxType, false, "An error occured:", "(" + error.code + ") " + error.message,
		// true).setVisible(true);

		// mPanel.setVisible(true);
	}

}
