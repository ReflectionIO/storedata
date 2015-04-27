//
//  LoginPage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 22 Nov 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.page;

import io.reflection.app.api.shared.datatypes.Session;
import io.reflection.app.client.DefaultEventBus;
import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.controller.NavigationController.Stack;
import io.reflection.app.client.controller.SessionController;
import io.reflection.app.client.handler.NavigationEventHandler;
import io.reflection.app.client.handler.user.SessionEventHandler;
import io.reflection.app.client.helper.FormHelper;
import io.reflection.app.client.part.login.LoginForm;
import io.reflection.app.client.res.Styles;
import io.reflection.app.datatypes.shared.User;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.Widget;
import com.willshex.gson.json.service.shared.Error;

/**
 * @author billy1380
 * 
 */
public class LoginPage extends Page implements NavigationEventHandler, SessionEventHandler {

	private static LoginPageUiBinder uiBinder = GWT.create(LoginPageUiBinder.class);

	interface LoginPageUiBinder extends UiBinder<Widget, LoginPage> {}

	public static final String WELCOME_ACTION_NAME = "welcome";
	public static final String TIMEOUT_ACTION_NAME = "timeout";

	// @UiField WelcomePanel mWelcomePanel; // Welcome panel, showed when action 'welcome' is in the stack

	@UiField HTMLPanel mDefaultLogin;

	@UiField InlineHyperlink register;
	@UiField InlineHyperlink login;
	@UiField LoginForm loginForm; // Usual login panel

	// @UiField Preloader preloader;

	public LoginPage() {
		initWidget(uiBinder.createAndBindUi(this));

		login.setTargetHistoryToken(PageType.LoginPageType.asTargetHistoryToken(FormHelper.REQUEST_INVITE_ACTION_NAME));
		register.setTargetHistoryToken(PageType.RegisterPageType.asTargetHistoryToken(FormHelper.REQUEST_INVITE_ACTION_NAME));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.Composite#onAttach()
	 */
	@Override
	protected void onAttach() {

		Document.get().getBody().addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().accountAccessPage());
		Document.get().getBody().addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().loginFormIsShowing());

		super.onAttach();

		register(DefaultEventBus.get().addHandlerToSource(NavigationEventHandler.TYPE, NavigationController.get(), this));
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
		Document.get().getBody().removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().loginFormIsShowing());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.NavigationEventHandler#navigationChanged(io.reflection.app.client.controller.NavigationController.Stack,
	 * io.reflection.app.client.controller.NavigationController.Stack)
	 */
	@Override
	public void navigationChanged(Stack previous, Stack current) {

		if (current != null && current.hasAction()) {
			if (WELCOME_ACTION_NAME.equals(current.getAction())) { // If action == 'welcome', show the Welcome panel
				// mWelcomePanel.setVisible(true);
				mDefaultLogin.setVisible(false);
			} else { // If action == email (user has been just registered to the system) attach him email to field
				String email = loginForm.getEmail(current.getAction());

				if (email == null) {
					email = loginForm.getEmail(current.getParameter(0));
				}

				if (email != null) {
					// mWelcomePanel.setVisible(false);
					mDefaultLogin.setVisible(true);
					loginForm.setUsername(email);
				}
			}
		} else {
			// mWelcomePanel.setVisible(false);
			mDefaultLogin.setVisible(true);

		}

		}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.SessionEventHandler#userLoggedIn(io.reflection.app.shared.datatypes.User,
	 * io.reflection.app.api.shared.datatypes.Session)
	 */
	@Override
	public void userLoggedIn(User user, Session session) {
		NavigationController.get().showNext();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.SessionEventHandler#userLoggedOut()
	 */
	@Override
	public void userLoggedOut() {}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.SessionEventHandler#userLoginFailed(com.willshex.gson.json.service.shared.Error)
	 */
	@Override
	public void userLoginFailed(Error error) {}

}
