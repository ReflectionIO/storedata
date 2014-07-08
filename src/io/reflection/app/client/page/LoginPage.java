//
//  LoginPage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 22 Nov 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.page;

import io.reflection.app.api.shared.datatypes.Session;
import io.reflection.app.client.controller.EventController;
import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.controller.NavigationController.Stack;
import io.reflection.app.client.controller.SessionController;
import io.reflection.app.client.handler.NavigationEventHandler;
import io.reflection.app.client.handler.user.SessionEventHandler;
import io.reflection.app.client.helper.FormHelper;
import io.reflection.app.client.part.AlertBox;
import io.reflection.app.client.part.Preloader;
import io.reflection.app.client.part.login.LoginForm;
import io.reflection.app.client.part.login.WelcomePanel;
import io.reflection.app.datatypes.shared.User;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.Widget;
import com.willshex.gson.json.service.client.JsonService;
import com.willshex.gson.json.service.client.JsonServiceCallEventHandler;
import com.willshex.gson.json.service.shared.Error;
import com.willshex.gson.json.service.shared.Request;
import com.willshex.gson.json.service.shared.Response;

/**
 * @author billy1380
 * 
 */
public class LoginPage extends Page implements NavigationEventHandler, SessionEventHandler, JsonServiceCallEventHandler {

	public interface Style extends CssResource {
		String mainPanel();
	}

	private static LoginPageUiBinder uiBinder = GWT.create(LoginPageUiBinder.class);

	interface LoginPageUiBinder extends UiBinder<Widget, LoginPage> {}

	private static final String WELCOME_ACTION_NAME = "welcome";

	@UiField WelcomePanel mWelcomePanel; // Welcome panel, showed when action 'welcome' is in the stack

	@UiField HTMLPanel mDefaultLogin;

	@UiField InlineHyperlink register;
	@UiField InlineHyperlink login;
	@UiField LoginForm mLoginForm; // Usual login panel

	@UiField AlertBox mAlertBox;

	@UiField Style style;

	@UiField Preloader preloader;

	public LoginPage() {
		initWidget(uiBinder.createAndBindUi(this));
		login.setTargetHistoryToken(PageType.LoginPageType.asTargetHistoryToken(FormHelper.REQUEST_INVITE_ACTION_NAME));
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

		register.setText("Request invite");

		register(EventController.get().addHandlerToSource(NavigationEventHandler.TYPE, NavigationController.get(), this));
		register(EventController.get().addHandlerToSource(SessionEventHandler.TYPE, SessionController.get(), this));
		EventController.get().addHandler(JsonServiceCallEventHandler.TYPE, this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.NavigationEventHandler#navigationChanged(io.reflection.app.client.controller.NavigationController.Stack,
	 * io.reflection.app.client.controller.NavigationController.Stack)
	 */
	@Override
	public void navigationChanged(Stack previous, Stack current) {
		Stack s = NavigationController.get().getStack();
		if (s != null && s.hasAction()) {
			if (WELCOME_ACTION_NAME.equals(s.getAction())) { // If action == 'welcome', show the Welcome panel
				mWelcomePanel.setVisible(true);
				mDefaultLogin.setVisible(false);
			} else { // If action == email (user has been just registered to the system) attach him email to field
				String email = getEmail(s.getAction());

				if (email == null) {
					email = getEmail(s.getParameter(0));
				}

				if (email != null) {
					mWelcomePanel.setVisible(false);
					mDefaultLogin.setVisible(true);
					mLoginForm.setUsername(email);
				}
			}
		} else {
			mWelcomePanel.setVisible(false);
			mDefaultLogin.setVisible(true);

		}

		register.setText("Request invite");
		register.setTargetHistoryToken(PageType.RegisterPageType.asTargetHistoryToken(FormHelper.REQUEST_INVITE_ACTION_NAME));

	}

	public String getEmail(String value) {
		String email = null;

		if (value != null && value.length() > 0 && FormHelper.isValidEmail(value)) {
			email = value;
		}

		return email;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.SessionEventHandler#userLoggedIn(io.reflection.app.shared.datatypes.User,
	 * io.reflection.app.api.shared.datatypes.Session)
	 */
	@Override
	public void userLoggedIn(User user, Session session) {
		preloader.hide();
		NavigationController.get().showNext();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.SessionEventHandler#userLoggedOut()
	 */
	@Override
	public void userLoggedOut() {
		preloader.hide();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.SessionEventHandler#userLoginFailed(com.willshex.gson.json.service.shared.Error)
	 */
	@Override
	public void userLoginFailed(Error error) {
		// mLoginForm.setEnabled(true);
		preloader.hide();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.willshex.gson.json.service.client.JsonServiceCallEventHandler#jsonServiceCallStart(com.willshex.gson.json.service.client.JsonService,
	 * java.lang.String, com.willshex.gson.json.service.shared.Request, com.google.gwt.http.client.Request)
	 */
	@Override
	public void jsonServiceCallStart(JsonService origin, String callName, Request input, com.google.gwt.http.client.Request handle) {
		if ("Login".equals(callName)) {
			preloader.show();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.willshex.gson.json.service.client.JsonServiceCallEventHandler#jsonServiceCallSuccess(com.willshex.gson.json.service.client.JsonService,
	 * java.lang.String, com.willshex.gson.json.service.shared.Request, com.willshex.gson.json.service.shared.Response)
	 */
	@Override
	public void jsonServiceCallSuccess(JsonService origin, String callName, Request input, Response output) {
		if ("Login".equals(callName)) {
			preloader.hide();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.willshex.gson.json.service.client.JsonServiceCallEventHandler#jsonServiceCallFailure(com.willshex.gson.json.service.client.JsonService,
	 * java.lang.String, com.willshex.gson.json.service.shared.Request, java.lang.Throwable)
	 */
	@Override
	public void jsonServiceCallFailure(JsonService origin, String callName, Request input, Throwable caught) {
		if ("Login".equals(callName)) {
			preloader.hide();
		}
	}

}
