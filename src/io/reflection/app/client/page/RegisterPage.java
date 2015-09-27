//
//  RegisterPage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 23 Nov 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.page;

import io.reflection.app.api.shared.datatypes.Session;
import io.reflection.app.client.DefaultEventBus;
import io.reflection.app.client.controller.SessionController;
import io.reflection.app.client.controller.UserController;
import io.reflection.app.client.handler.user.SessionEventHandler;
import io.reflection.app.client.handler.user.UserRegisteredEventHandler;
import io.reflection.app.client.helper.FormHelper;
import io.reflection.app.client.part.register.RegisterForm;
import io.reflection.app.client.res.Styles;
import io.reflection.app.datatypes.shared.User;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.UListElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import com.willshex.gson.json.service.shared.Error;

/**
 * @author billy1380
 * 
 */
public class RegisterPage extends Page implements UserRegisteredEventHandler, SessionEventHandler {

	private static RegisterPageUiBinder uiBinder = GWT.create(RegisterPageUiBinder.class);

	interface RegisterPageUiBinder extends UiBinder<Widget, RegisterPage> {}

	@UiField DivElement applyPanel;
	@UiField RegisterForm registerForm;

	@UiField LIElement tabContentRegister;
	// @UiField InlineHyperlink continueToLeaderboard;
	@UiField UListElement tabsContainer;

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

		Document.get().getBody().addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().accountAccessPage());
		Document.get().getBody().addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().applyFormIsShowing());

		register(DefaultEventBus.get().addHandlerToSource(UserRegisteredEventHandler.TYPE, UserController.get(), this));
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
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.user.UserRegisteredEventHandler#userRegistered(java.lang.String)
	 */
	@Override
	public void userRegistered(String email, String password) {
		// final String username = email;

		if (SessionController.get().isLoggedInUserAdmin()) {
			PageType.UsersPageType.show();
			registerForm.resetForm();
		} else {
			SessionController.get().login(email, password, true);
			registerForm.setButtonLoading("Logging In ..");
			PageType.LinkItunesPageType.show();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.user.UserRegisteredEventHandler#userRegistrationFailed(com.willshex.gson.json.service.shared.Error)
	 */
	@Override
	public void userRegistrationFailed(Error error) {
		registerForm.setEnabled(true);
		registerForm.setButtonError();
		if (error.code == 400000) { // Database error, the user already exists
			registerForm.setEmailError(FormHelper.ERROR_EMAIL_DUPLICATE);
		}
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
		registerForm.setButtonError();
	}

}
