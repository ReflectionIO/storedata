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
import io.reflection.app.api.core.shared.call.RegisterUserRequest;
import io.reflection.app.api.core.shared.call.RegisterUserResponse;
import io.reflection.app.api.core.shared.call.event.GetUserDetailsEventHandler;
import io.reflection.app.api.core.shared.call.event.RegisterUserEventHandler;
import io.reflection.app.api.shared.datatypes.Session;
import io.reflection.app.client.controller.EventController;
import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.controller.NavigationController.Stack;
import io.reflection.app.client.controller.SessionController;
import io.reflection.app.client.controller.UserController;
import io.reflection.app.client.handler.NavigationEventHandler;
import io.reflection.app.client.handler.user.SessionEventHandler;
import io.reflection.app.client.handler.user.UserRegisteredEventHandler;
import io.reflection.app.client.helper.FormHelper;
import io.reflection.app.client.part.register.RegisterForm;
import io.reflection.app.client.part.register.ThankYouRegisterPanel;
import io.reflection.app.datatypes.shared.User;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.Widget;
import com.willshex.gson.json.service.shared.Error;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author billy1380
 * 
 */
public class RegisterPage extends Page implements UserRegisteredEventHandler, RegisterUserEventHandler, NavigationEventHandler, GetUserDetailsEventHandler,
		SessionEventHandler {

	private static RegisterPageUiBinder uiBinder = GWT.create(RegisterPageUiBinder.class);

	interface RegisterPageUiBinder extends UiBinder<Widget, RegisterPage> {}

	@UiField HTMLPanel mPanel;

	@UiField InlineHyperlink login;

	@UiField InlineHyperlink register;
	@UiField RegisterForm mRegisterForm;

	@UiField ThankYouRegisterPanel mThankYouRegisterPanel;

	private String username;

	public RegisterPage() {
		initWidget(uiBinder.createAndBindUi(this));
		login.setTargetHistoryToken(PageType.LoginPageType.asTargetHistoryToken(FormHelper.REQUEST_INVITE_ACTION_NAME));
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
		register(EventController.get().addHandlerToSource(RegisterUserEventHandler.TYPE, UserController.get(), this));
		register(EventController.get().addHandlerToSource(SessionEventHandler.TYPE, SessionController.get(), this));

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
		mRegisterForm.clearPasswordValue();
		mRegisterForm.setTermAndCond(Boolean.FALSE);
		mRegisterForm.setEnabled(true);
		mRegisterForm.focusFirstActiveField();

		// TODO User already asked for request invite

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.NavigationEventHandler#navigationChanged(io.reflection.app.client.controller.NavigationController.Stack,
	 * io.reflection.app.client.controller.NavigationController.Stack)
	 */
	@Override
	public void navigationChanged(Stack previous, Stack current) {
		String actionCode = null;

		register.setTargetHistoryToken(current.toString());
		// Request invite form
		if (current.getAction() != null && FormHelper.REQUEST_INVITE_ACTION_NAME.equals(current.getAction())) {
			username = null;
			register.setText("Request invite");
			mRegisterForm.setRequestInvite(Boolean.TRUE);
			// Register form
		} else {

			// Register after request invite
			if (current.getAction() != null && FormHelper.COMPLETE_ACTION_NAME.equals(current.getAction())
					&& (actionCode = current.getParameter(FormHelper.CODE_PARAMETER_INDEX)) != null) {
				register.setText("Register");
				mRegisterForm.setRequestInvite(Boolean.FALSE);
				mRegisterForm.setEnabled(false);
				UserController.get().fetchUser(actionCode);
				// Default register
			} else {
				// mRegisterForm.resetForm();
				// mRegisterForm.focusFirstActiveField();
				username = null;
				register.setText("Request invite");
				mRegisterForm.setRequestInvite(Boolean.TRUE);
			}
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
			mRegisterForm.setVisible(Boolean.TRUE);
			if (output.user != null) {
				mRegisterForm.resetForm();
				mRegisterForm.setUsername(username = output.user.username);
				mRegisterForm.setForename(output.user.forename);
				mRegisterForm.setSurname(output.user.surname);
				mRegisterForm.setCompany(output.user.company);
				mRegisterForm.setActionCode(input.actionCode);
				mRegisterForm.focusFirstActiveField();
			}
		} else {

			// mRegisterForm.focusFirstActiveField();
			if (output.error.code == 100055) {
				// User already registered after invitation request
			}

			username = null;
			register.setText("Request invite");
			mRegisterForm.setRequestInvite(Boolean.TRUE);
			mRegisterForm.resetForm();
			PageType.RegisterPageType.show(FormHelper.REQUEST_INVITE_ACTION_NAME);
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
	public void getUserDetailsFailure(GetUserDetailsRequest input, Throwable caught) {
		// mRegisterForm.resetForm();
		// mRegisterForm.focusFirstActiveField();
		username = null;
		register.setText("Request invite");
		mRegisterForm.setRequestInvite(Boolean.TRUE);
		mRegisterForm.resetForm();
		PageType.RegisterPageType.show(FormHelper.REQUEST_INVITE_ACTION_NAME);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.reflection.app.api.core.shared.call.event.RegisterUserEventHandler#registerUserSuccess(io.reflection.app.api.core.shared.call.RegisterUserRequest,
	 * io.reflection.app.api.core.shared.call.RegisterUserResponse)
	 */
	@Override
	public void registerUserSuccess(RegisterUserRequest input, RegisterUserResponse output) {
		if (output.status == StatusType.StatusTypeSuccess) {
			SessionController.get().login(username, input.user.password, true);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.reflection.app.api.core.shared.call.event.RegisterUserEventHandler#registerUserFailure(io.reflection.app.api.core.shared.call.RegisterUserRequest,
	 * java.lang.Throwable)
	 */
	@Override
	public void registerUserFailure(RegisterUserRequest input, Throwable caught) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.user.SessionEventHandler#userLoggedIn(io.reflection.app.datatypes.shared.User,
	 * io.reflection.app.api.shared.datatypes.Session)
	 */
	@Override
	public void userLoggedIn(User user, Session session) {
		if (user != null && session != null) {
			PageType.LinkItunesPageType.show();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.user.SessionEventHandler#userLoggedOut()
	 */
	@Override
	public void userLoggedOut() {}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.user.SessionEventHandler#userLoginFailed(com.willshex.gson.json.service.shared.Error)
	 */
	@Override
	public void userLoginFailed(Error error) {
		// TODO: check the error code and if it is a permissions issue redirect to the closed beta page
	}

}
