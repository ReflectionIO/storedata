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
import io.reflection.app.client.DefaultEventBus;
import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.controller.NavigationController.Stack;
import io.reflection.app.client.controller.SessionController;
import io.reflection.app.client.controller.UserController;
import io.reflection.app.client.handler.NavigationEventHandler;
import io.reflection.app.client.handler.user.SessionEventHandler;
import io.reflection.app.client.handler.user.UserRegisteredEventHandler;
import io.reflection.app.client.helper.DOMHelper;
import io.reflection.app.client.helper.FormHelper;
import io.reflection.app.client.part.register.RegisterForm;
import io.reflection.app.client.res.Styles;
import io.reflection.app.datatypes.shared.User;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.SpanElement;
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

	@UiField SpanElement welcomeName;
	@UiField DivElement applyPanel;
	@UiField HTMLPanel createPasswordPanel;
	@UiField DivElement accountFormContainer;
	@UiField InlineHyperlink login;
	@UiField InlineHyperlink register;
	@UiField RegisterForm registerForm;

	@UiField LIElement tabContentRegister;
	@UiField DivElement submittedSuccessPanel;

	private String username;

	public RegisterPage() {
		initWidget(uiBinder.createAndBindUi(this));

		setRequestInvite(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.Composite#onAttach()
	 */
	@Override
	protected void onAttach() {
		createPasswordPanel.setVisible(false);
		DOMHelper.addClassName(Document.get().getBody(), Styles.STYLES_INSTANCE.reflectionMainStyle().accountAccessPage());

		super.onAttach();

		register(DefaultEventBus.get().addHandlerToSource(UserRegisteredEventHandler.TYPE, UserController.get(), this));
		register(DefaultEventBus.get().addHandlerToSource(NavigationEventHandler.TYPE, NavigationController.get(), this));
		register(DefaultEventBus.get().addHandlerToSource(GetUserDetailsEventHandler.TYPE, UserController.get(), this));
		register(DefaultEventBus.get().addHandlerToSource(RegisterUserEventHandler.TYPE, UserController.get(), this));
		register(DefaultEventBus.get().addHandlerToSource(SessionEventHandler.TYPE, SessionController.get(), this));

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
		Document.get().getBody().removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().applyFormIsShowing());
		Document.get().getBody().removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().createPasswordFormIsShowing());
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
			PageType.UsersPageType.show();
		} else {
			// show mail animation
			if (!tabContentRegister.hasClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().tabs__contentIsSubmitted())) {
				tabContentRegister.addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().tabs__contentIsSubmitted());
			}
			if (!submittedSuccessPanel.hasClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().isShowing())) {
				submittedSuccessPanel.addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().isShowing());
			}
		}
		registerForm.setButtonSuccess("Application Sent!", 0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.user.UserRegisteredEventHandler#userRegistrationFailed(com.willshex.gson.json.service.shared.Error)
	 */
	@Override
	public void userRegistrationFailed(Error error) {
		registerForm.setEnabled(true);
		registerForm.setButtonError(); // probably the user already exists

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.NavigationEventHandler#navigationChanged(io.reflection.app.client.controller.NavigationController.Stack,
	 * io.reflection.app.client.controller.NavigationController.Stack)
	 */
	@Override
	public void navigationChanged(Stack previous, Stack current) {

		welcomeName.setInnerText("");
		// remove mail animation
		tabContentRegister.removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().tabs__contentIsSubmitted());
		submittedSuccessPanel.removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().isShowing());

		String actionCode = null;

		if (current.getAction() != null && FormHelper.REQUEST_INVITE_ACTION_NAME.equals(current.getAction())) { // Request invite form
			username = null;
			setRequestInvite(true);
		} else { // Register form
			if (current.getAction() != null && FormHelper.COMPLETE_ACTION_NAME.equals(current.getAction())
					&& (actionCode = current.getParameter(FormHelper.CODE_PARAMETER_INDEX)) != null) { // Register after request invite
				setRequestInvite(false);
				UserController.get().fetchUser(actionCode);
				registerForm.setButtonLoading("Getting details ..");
			} else if (SessionController.get().isLoggedInUserAdmin() && current.getAction() == null) { // Admin - normal registration
				setRequestInvite(false);
				registerForm.resetForm();
				registerForm.focusFirstActiveField();
			} else { // Default action - Show request invite form
				username = null;
				setRequestInvite(true);
			}
		}

		login.setTargetHistoryToken(PageType.LoginPageType.asTargetHistoryToken(FormHelper.REQUEST_INVITE_ACTION_NAME));
		register.setTargetHistoryToken(current.toString());
	}

	private void setRequestInvite(boolean requestInvite) {
		registerForm.setRequestInvite(requestInvite);
		setApply(requestInvite);
	}

	private void setApply(boolean apply) {
		if (apply) {
			Document.get().getBody().removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().createPasswordFormIsShowing());
			DOMHelper.addClassName(Document.get().getBody(), Styles.STYLES_INSTANCE.reflectionMainStyle().applyFormIsShowing());
			accountFormContainer.removeAttribute("style");
		} else {
			Document.get().getBody().removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().applyFormIsShowing());
			DOMHelper.addClassName(Document.get().getBody(), Styles.STYLES_INSTANCE.reflectionMainStyle().createPasswordFormIsShowing());
			accountFormContainer.setAttribute("style", "padding: 0px");
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
				welcomeName.setInnerText(output.user.forename + "!");
				registerForm.resetForm();
				registerForm.setUsername(username = output.user.username);
				createPasswordPanel.setVisible(true);
				registerForm.setForename(output.user.forename);
				registerForm.setSurname(output.user.surname);
				registerForm.setCompany(output.user.company);
				registerForm.setActionCode(input.actionCode);
				registerForm.focusFirstActiveField();
			} else {
				registerForm.setButtonError();
			}
		} else {

			username = null;

			PageType.RegisterPageType.show(FormHelper.REQUEST_INVITE_ACTION_NAME);
			if (output.error.code == 100055) {
				// registerForm.setButtonError("User already registered!");
			} else {
				// registerForm.setButtonError();
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
	public void getUserDetailsFailure(GetUserDetailsRequest input, Throwable caught) {
		PageType.RegisterPageType.show(FormHelper.REQUEST_INVITE_ACTION_NAME);
		setRequestInvite(true);
		// registerForm.setButtonError();
		username = null;
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
			PageType.LinkItunesPageType.show();
			SessionController.get().login(username, input.user.password, true);
		} else {
			registerForm.resetButtonStatus();
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
		registerForm.setButtonError();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.user.SessionEventHandler#userLoggedIn(io.reflection.app.datatypes.shared.User,
	 * io.reflection.app.api.shared.datatypes.Session)
	 */
	@Override
	public void userLoggedIn(User user, Session session) {
		registerForm.resetButtonStatus();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.user.SessionEventHandler#userLoggedOut()
	 */
	@Override
	public void userLoggedOut() {
		registerForm.resetButtonStatus();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.user.SessionEventHandler#userLoginFailed(com.willshex.gson.json.service.shared.Error)
	 */
	@Override
	public void userLoginFailed(Error error) {
		// TODO: check the error code and if it is a permissions issue redirect to the closed beta page
		registerForm.setButtonError();
	}

}
