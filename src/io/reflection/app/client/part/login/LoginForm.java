//
//  LoginForm.java
//  storedata
//
//  Created by Stefano Capuzzi on 14 Feb 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.part.login;

import io.reflection.app.api.core.shared.call.LoginRequest;
import io.reflection.app.api.core.shared.call.LoginResponse;
import io.reflection.app.api.core.shared.call.event.LoginEventHandler;
import io.reflection.app.api.shared.datatypes.Session;
import io.reflection.app.client.DefaultEventBus;
import io.reflection.app.client.component.FormButton;
import io.reflection.app.client.component.FormCheckbox;
import io.reflection.app.client.component.FormField;
import io.reflection.app.client.component.FormFieldPassword;
import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.controller.NavigationController.Stack;
import io.reflection.app.client.controller.SessionController;
import io.reflection.app.client.handler.NavigationEventHandler;
import io.reflection.app.client.handler.user.SessionEventHandler;
import io.reflection.app.client.handler.user.UserPowersEventHandler;
import io.reflection.app.client.helper.FormHelper;
import io.reflection.app.client.page.LoginPage;
import io.reflection.app.client.res.Styles;
import io.reflection.app.datatypes.shared.Permission;
import io.reflection.app.datatypes.shared.Role;
import io.reflection.app.datatypes.shared.User;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.willshex.gson.json.service.shared.Error;

/**
 * @author stefanocapuzzi
 * 
 */
public class LoginForm extends Composite implements LoginEventHandler, UserPowersEventHandler, NavigationEventHandler, SessionEventHandler {

    private static LoginFormUiBinder uiBinder = GWT.create(LoginFormUiBinder.class);

    interface LoginFormUiBinder extends UiBinder<Widget, LoginForm> {}

	@UiField FormField emailFormField;
	private String EmailNote = null;
	@UiField FormFieldPassword passwordFormField;
	private String passwordNote = null;
	@UiField FormCheckbox rememberMe;
	@UiField FormButton loginBtn;

    public LoginForm() {
        initWidget(uiBinder.createAndBindUi(this));
		this.getElement().setAttribute("autocomplete", "off");

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.gwt.user.client.ui.Composite#onAttach()
     */
    @Override
    protected void onAttach() {
        super.onAttach();

		DefaultEventBus.get().addHandlerToSource(LoginEventHandler.TYPE, SessionController.get(), this);
		DefaultEventBus.get().addHandlerToSource(UserPowersEventHandler.TYPE, SessionController.get(), this);
		DefaultEventBus.get().addHandlerToSource(NavigationEventHandler.TYPE, NavigationController.get(), this);
		DefaultEventBus.get().addHandlerToSource(SessionEventHandler.TYPE, SessionController.get(), this);

		Stack s = NavigationController.get().getStack();
		if (s != null && s.hasAction()) {
			if (LoginPage.TIMEOUT_ACTION_NAME.equals(s.getAction())) {
				String email = getEmail(s.getParameter(0));
				if (email != null) {
					setUsername(email);
				}
        }

    }

    }

    public void setUsername(String value) {
        if (value != null && value.length() > 0) {
			emailFormField.getElement().getParentElement().removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().isClosed());
			passwordFormField.getElement().getParentElement().removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().isClosed());
			emailFormField.setText(value);
			passwordFormField.setFocus(true);
        }
    }

    /**
     * Fire the button when pressing the 'enter' key on one of the form fields
     * 
     * @param event
     */
	@UiHandler({ "emailFormField", "passwordFormField" })
    void onEnterKeyPressFields(KeyPressEvent event) {
        if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
			loginBtn.click();
        }
    }

	@UiHandler("loginBtn")
    void onLoginClicked(ClickEvent event) {
        if (validate()) {
            clearErrors();
			setEnabled(false);
			loginBtn.setStatusLoading("Logging in ...");
			SessionController.get().login(emailFormField.getText(), passwordFormField.getText(), rememberMe.getValue().booleanValue()); // Execute user login
        } else {
			if (EmailNote != null) {
				emailFormField.showNote(EmailNote, true);
            } else {
				emailFormField.hideNote();
            }
			if (passwordNote != null) {
				passwordFormField.showNote(passwordNote, true);
            } else {
				passwordFormField.hideNote();
            }
        }
    }

    private boolean validate() {

        boolean validated = true;
        // Retrieve fields to validate
		String emailText = emailFormField.getText();
		String passwordText = passwordFormField.getText();

        // Check fields constraints
		if (emailText == null || emailText.length() == 0) {
			EmailNote = "Cannot be empty";
            validated = false;
		} else if (emailText.length() < 6) {
			EmailNote = "Too short (minimum 6 characters)";
            validated = false;
		} else if (emailText.length() > 255) {
			EmailNote = "Too long (maximum 255 characters)";
            validated = false;
		} else if (!FormHelper.isValidEmail(emailText)) {
			EmailNote = "Invalid email address";
            validated = false;
        } else {
			EmailNote = null;
            validated = validated && true;
        }

		if (passwordText == null || passwordText.length() == 0) {
			passwordNote = "Cannot be empty";
            validated = false;
		} else if (passwordText.length() < 6) {
			passwordNote = "Too short (minimum 6 characters)";
            validated = false;
		} else if (passwordText.length() > 64) {
			passwordNote = "Too long (maximum 64 characters)";
            validated = false;
		} else if (!FormHelper.isTrimmed(passwordText)) {
			passwordNote = "Whitespaces not allowed either before or after the string";
            validated = false;
        } else {
			passwordNote = null;
            validated = validated && true;
        }

        return validated;
    }

	// private void resetForm() {
	// // setEnabled(true);
	// emailFormField.setText("");
	// password.setText("");
	// if (rememberMe.getValue().equals(Boolean.FALSE)) {
	// rememberMe.setValue(Boolean.FALSE);
	// } else {
	// rememberMe.setValue(Boolean.TRUE);
	// }
	// clearErrors();
	// emailFormField.setFocus(true);
	// }

	private void clearErrors() {
		emailFormField.hideNote();
		passwordFormField.hideNote();
	}

	public void setEnabled(boolean enabled) {
		emailFormField.setEnabled(enabled);
		rememberMe.setEnabled(enabled);
		passwordFormField.setEnabled(enabled);
		emailFormField.setFocus(enabled);

		// if (!value) {
		// rememberMe.setFocus(false);
		// passwordFormField.setFocus(false);
		// }
	}

	public String getEmail(String value) {
		String email = null;
		if (value != null && value.length() > 0 && FormHelper.isValidEmail(value)) {
			email = value;
		}
		return email;
	}

	public void setLoggedIn(boolean loggedIn) {

        }

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.user.UserPowersEventHandler#gotUserPowers(io.reflection.app.datatypes.shared.User, java.util.List, java.util.List)
	 */
	@Override
	public void gotUserPowers(User user, List<Role> roles, List<Permission> permissions) {
		loginBtn.setStatusSuccess("Logged in !", 0);
    }

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.user.UserPowersEventHandler#getGetUserPowersFailed(com.willshex.gson.json.service.shared.Error)
	 */
	@Override
	public void getGetUserPowersFailed(Error error) {
		loginBtn.setStatusError("Oops, something went wrong");
		setEnabled(true);
    }

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.NavigationEventHandler#navigationChanged(io.reflection.app.client.controller.NavigationController.Stack,
	 * io.reflection.app.client.controller.NavigationController.Stack)
	 */
	@Override
	public void navigationChanged(Stack previous, Stack current) {
		if (SessionController.get().getLastUsername() != null) {
			setUsername(SessionController.get().getLastUsername());
		}
		if (SessionController.get().isValidSession()) { // TODO test
			passwordFormField.clear();
			setEnabled(false);
			loginBtn.setStatusSuccess("Logged in !", 0);
		}
		if (current != null && current.hasAction() && !LoginPage.WELCOME_ACTION_NAME.equals(current.getAction())) {
			String email = getEmail(current.getAction());
			if (email == null) {
				email = getEmail(current.getParameter(0));
			}
			if (email != null) {
				setUsername(email);
			}
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see io.reflection.app.api.core.shared.call.event.LoginEventHandler#loginSuccess(io.reflection.app.api.core.shared.call.LoginRequest,
     * io.reflection.app.api.core.shared.call.LoginResponse)
     */
    @Override
    public void loginSuccess(LoginRequest input, LoginResponse output) {

    }

    /*
     * (non-Javadoc)
     * 
     * @see io.reflection.app.api.core.shared.call.event.LoginEventHandler#loginFailure(io.reflection.app.api.core.shared.call.LoginRequest,
     * java.lang.Throwable)
     */
    @Override
    public void loginFailure(LoginRequest input, Throwable caught) {
		loginBtn.setStatusError("Oops, something went wrong");
		setEnabled(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.user.SessionEventHandler#userLoggedIn(io.reflection.app.datatypes.shared.User,
	 * io.reflection.app.api.shared.datatypes.Session)
	 */
	@Override
	public void userLoggedIn(User user, Session session) {
		passwordFormField.clear();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.user.SessionEventHandler#userLoggedOut()
	 */
	@Override
	public void userLoggedOut() {
		setEnabled(true);
		loginBtn.resetStatus();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.user.SessionEventHandler#userLoginFailed(com.willshex.gson.json.service.shared.Error)
	 */
	@Override
	public void userLoginFailed(Error error) {
		loginBtn.setStatusError("Oops, something went wrong");
		setEnabled(true);
    }

}
