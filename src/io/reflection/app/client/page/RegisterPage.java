//
//  RegisterPage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 23 Nov 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.page;

import io.reflection.app.api.core.shared.call.GetUserDetailsRequest;
import io.reflection.app.api.core.shared.call.GetUserDetailsResponse;
import io.reflection.app.api.core.shared.call.event.GetUserDetailsEventHandler;
import io.reflection.app.client.controller.EventController;
import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.controller.NavigationController.Stack;
import io.reflection.app.client.controller.UserController;
import io.reflection.app.client.handler.NavigationEventHandler;
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
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author billy1380
 * 
 */
public class RegisterPage extends Page implements UserRegisteredEventHandler, NavigationEventHandler, GetUserDetailsEventHandler {

	private static RegisterPageUiBinder uiBinder = GWT.create(RegisterPageUiBinder.class);

	interface RegisterPageUiBinder extends UiBinder<Widget, RegisterPage> {}

	private static final String COMPLETE_ACTION_NAME = "complete";
	private static final int CODE_PARAMETER_INDEX = 1;

	@UiField HTMLPanel mPanel;

	@UiField RegisterForm mRegisterForm;

	@UiField ThankYouRegisterPanel mThankYouRegisterPanel;

	@UiField AlertBox mAlertBox;

	public RegisterPage() {
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

		register(EventController.get().addHandlerToSource(UserRegisteredEventHandler.TYPE, UserController.get(), this));
		register(EventController.get().addHandlerToSource(NavigationEventHandler.TYPE, NavigationController.get(), this));
		register(EventController.get().addHandlerToSource(GetUserDetailsEventHandler.TYPE, UserController.get(), this));

		mRegisterForm.setVisible(true);
		mThankYouRegisterPanel.setVisible(false);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.NavigationEventHandler#navigationChanged(io.reflection.app.client.controller.NavigationController.Stack)
	 */
	@Override
	public void navigationChanged(Stack stack) {
		String code = null;

		if (COMPLETE_ACTION_NAME.equals(stack.getAction()) && (code = stack.getParameter(CODE_PARAMETER_INDEX)) != null) {
			mRegisterForm.setEnabled(true);
			
			UserController.get().fetchUser(code);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.reflection.app.api.core.shared.call.event.GetUserDetailsEventHandler#getUserDetailsSuccess(io.reflection.app.api.core.shared.call.GetUserDetailsRequest
	 * , io.reflection.app.api.core.shared.call.GetUserDetailsResponse)
	 */
	@Override
	public void getUserDetailsSuccess(GetUserDetailsRequest input, GetUserDetailsResponse output) {
		if (output.status == StatusType.StatusTypeSuccess) {
			mRegisterForm.setVisible(true);

			if (output.user != null) {
				mRegisterForm.setEnabled(true);
				
				mRegisterForm.setUsername(output.user.username);
				mRegisterForm.setForename(output.user.forename);
				mRegisterForm.setSurname(output.user.surname);
				mRegisterForm.setCompany(output.user.company);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.reflection.app.api.core.shared.call.event.GetUserDetailsEventHandler#getUserDetailsFailure(io.reflection.app.api.core.shared.call.GetUserDetailsRequest
	 * , java.lang.Throwable)
	 */
	@Override
	public void getUserDetailsFailure(GetUserDetailsRequest input, Throwable caught) {}

}
