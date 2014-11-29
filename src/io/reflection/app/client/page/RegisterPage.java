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
import io.reflection.app.client.page.part.InviteRegisterPanel;
import io.reflection.app.client.page.part.LoginRegisterPanel;
import io.reflection.app.client.part.Preloader;
import io.reflection.app.client.part.register.RegisterForm;
import io.reflection.app.client.part.register.ThankYouRegisterPanel;
import io.reflection.app.datatypes.shared.User;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
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

	public interface Style extends CssResource {
		String mainPanel();
	}

	private static RegisterPageUiBinder uiBinder = GWT.create(RegisterPageUiBinder.class);

	interface RegisterPageUiBinder extends UiBinder<Widget, RegisterPage> {}

	@UiField LoginRegisterPanel loginRegisterPanel;
	@UiField InviteRegisterPanel inviteRegisterPanel;

	@UiField HTMLPanel loginRegisterTabs;
	@UiField HTMLPanel inviteRegisterTabs;

	@UiField InlineHyperlink login;

	@UiField InlineHyperlink register;
	@UiField RegisterForm mRegisterForm;

	@UiField ThankYouRegisterPanel mThankYouRegisterPanel;

	@UiField Style style;
	@UiField Preloader preloader;

	private String username;

	public RegisterPage() {
		initWidget(uiBinder.createAndBindUi(this));

		mRegisterForm.setPreloader(preloader); // Assign the preloader reference to the Register Form
		// String mediaQueries = " @media (max-width: 768px) {." + style.mainPanel() + " {margin-top:20px;}}";
		// StyleInjector.injectAtEnd(mediaQueries);
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

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.user.UserRegisteredEventHandler#userRegistered(java.lang.String)
	 */
	@Override
	public void userRegistered(String email) {
		// final String username = email;
		if (SessionController.get().isLoggedInUserAdmin()) {
			PageType.UsersPageType.show("view");
		} else {
			mRegisterForm.setVisible(false);
			mThankYouRegisterPanel.setVisible(true);
		}
		preloader.hide();
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
		// mRegisterForm.setEnabled(true);
		mRegisterForm.focusFirstActiveField();
		preloader.hide();
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

		mRegisterForm.setVisible(true);
		mThankYouRegisterPanel.setVisible(false);

		String actionCode = null;

		login.setTargetHistoryToken(PageType.LoginPageType.asTargetHistoryToken(FormHelper.REQUEST_INVITE_ACTION_NAME));
		register.setTargetHistoryToken(current.toString());

		if (current.getAction() != null && FormHelper.REQUEST_INVITE_ACTION_NAME.equals(current.getAction())) { // Request invite form
			username = null;
			setRequestInvite(Boolean.TRUE);
		} else { // Register form
			if (current.getAction() != null && FormHelper.COMPLETE_ACTION_NAME.equals(current.getAction())
					&& (actionCode = current.getParameter(FormHelper.CODE_PARAMETER_INDEX)) != null) { // Register after request invite
				setRequestInvite(Boolean.FALSE);
				// mRegisterForm.setEnabled(false);
				preloader.show();
				UserController.get().fetchUser(actionCode);
			} else if (SessionController.get().isLoggedInUserAdmin() && current.getAction() == null) { // Admin - normal registration
				setRequestInvite(Boolean.FALSE);
				mRegisterForm.resetForm();
				mRegisterForm.focusFirstActiveField();
			} else { // Default action - Show request invite form
				username = null;
				setRequestInvite(Boolean.TRUE);
			}
		}
	}

	private void setRequestInvite(boolean requestInvite) {
		mRegisterForm.setRequestInvite(requestInvite);
		inviteRegisterPanel.setVisible(!requestInvite);
		inviteRegisterTabs.setVisible(!requestInvite);
		loginRegisterPanel.setVisible(requestInvite);
		loginRegisterTabs.setVisible(requestInvite);
		if (requestInvite) {
			register.setText("Request invite");
		} else {
			register.setText("Register");
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
			setRequestInvite(Boolean.TRUE);
			mRegisterForm.resetForm();
			PageType.RegisterPageType.show(FormHelper.REQUEST_INVITE_ACTION_NAME);
		}
		preloader.hide();
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
		setRequestInvite(Boolean.TRUE);
		mRegisterForm.resetForm();
		PageType.RegisterPageType.show(FormHelper.REQUEST_INVITE_ACTION_NAME);
		preloader.hide();
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
		} else {
			preloader.hide();
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
		preloader.hide();
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
		preloader.hide();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.user.SessionEventHandler#userLoggedOut()
	 */
	@Override
	public void userLoggedOut() {
		preloader.hide();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.user.SessionEventHandler#userLoginFailed(com.willshex.gson.json.service.shared.Error)
	 */
	@Override
	public void userLoginFailed(Error error) {
		// TODO: check the error code and if it is a permissions issue redirect to the closed beta page
		preloader.hide();
	}

}
