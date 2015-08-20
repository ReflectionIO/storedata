//
//  LoginPage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 22 Nov 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.page;

import io.reflection.app.api.core.shared.call.ForgotPasswordRequest;
import io.reflection.app.api.core.shared.call.ForgotPasswordResponse;
import io.reflection.app.api.core.shared.call.event.ForgotPasswordEventHandler;
import io.reflection.app.api.shared.ApiError;
import io.reflection.app.client.DefaultEventBus;
import io.reflection.app.client.controller.SessionController;
import io.reflection.app.client.helper.FormHelper;
import io.reflection.app.client.part.login.ForgotPasswordForm;
import io.reflection.app.client.part.login.LoginForm;
import io.reflection.app.client.res.Styles;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.Widget;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author billy1380
 * 
 */
public class LoginPage extends Page implements ForgotPasswordEventHandler {

	private static LoginPageUiBinder uiBinder = GWT.create(LoginPageUiBinder.class);

	interface LoginPageUiBinder extends UiBinder<Widget, LoginPage> {}

	@UiField InlineHyperlink register;
	@UiField InlineHyperlink login;
	@UiField LoginForm loginForm;
	@UiField ForgotPasswordForm forgotPasswordForm;
	@UiField DivElement formSubmittedSuccessPanel;

	public LoginPage() {
		initWidget(uiBinder.createAndBindUi(this));

		login.setTargetHistoryToken(PageType.LoginPageType.asTargetHistoryToken(FormHelper.REQUEST_INVITE_ACTION_NAME));
		register.setTargetHistoryToken(PageType.RegisterPageType.asTargetHistoryToken(FormHelper.REQUEST_INVITE_ACTION_NAME));

		loginForm.getResetPasswordLink().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				event.preventDefault();
				forgotPasswordForm.setEmail(loginForm.getEmail());
				if (Window.getClientWidth() <= 720) {
					// TODO scroll to form top
				}

				loginForm.getElement().getParentElement().addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().showResetPasswordForm());
				loginForm.getElement().getParentElement().removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().willShow());
				Document.get().getBody().addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().resetPasswordFormIsShowing());

				Timer t = new Timer() {

					@Override
					public void run() {
						loginForm.getElement().getStyle().setVisibility(Visibility.HIDDEN);
						loginForm.getElement().getStyle().setPosition(Position.ABSOLUTE);
						forgotPasswordForm.getElement().getStyle().setVisibility(Visibility.VISIBLE);
						forgotPasswordForm.getElement().getStyle().setPosition(Position.RELATIVE);
						loginForm.getElement().getParentElement().removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().willShow());
					}
				};
				t.schedule(150);

			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.Composite#onAttach()
	 */
	@Override
	protected void onAttach() {
		super.onAttach();
		DefaultEventBus.get().addHandlerToSource(ForgotPasswordEventHandler.TYPE, SessionController.get(), this);

		Document.get().getBody().addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().accountAccessPage());
		Document.get().getBody().addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().loginFormIsShowing());

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
		Document.get().getBody().removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().resetPasswordFormIsShowing());

		loginForm.getElement().getParentElement().removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().tabs__contentIsSubmitted());
		formSubmittedSuccessPanel.removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().isShowing());
		loginForm.getElement().getParentElement().removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().showResetPasswordForm());
		loginForm.getElement().removeAttribute("style");
		forgotPasswordForm.getElement().removeAttribute("style");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.reflection.app.api.core.shared.call.event.ForgotPasswordEventHandler#forgotPasswordSuccess(io.reflection.app.api.core.shared.call.ForgotPasswordRequest
	 * , io.reflection.app.api.core.shared.call.ForgotPasswordResponse)
	 */
	@Override
	public void forgotPasswordSuccess(ForgotPasswordRequest input, ForgotPasswordResponse output) {
		if (forgotPasswordForm.isStatusLoading()) {
			if (output.status == StatusType.StatusTypeSuccess) {

				forgotPasswordForm.getElement().getStyle().setVisibility(Visibility.HIDDEN); // TODO add in CSS

				forgotPasswordForm.setStatusSuccess();
				formSubmittedSuccessPanel.addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().isShowing());
				loginForm.getElement().getParentElement().addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().tabs__contentIsSubmitted());
			} else if (output.status == StatusType.StatusTypeFailure && output.error != null && output.error.code == ApiError.UserNotFound.getCode()) {
				forgotPasswordForm.setStatusError("Invalid email address");
			} else {
				forgotPasswordForm.setStatusError();
				loginForm.getElement().getParentElement().removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().tabs__contentIsSubmitted());
				formSubmittedSuccessPanel.removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().isShowing());
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.reflection.app.api.core.shared.call.event.ForgotPasswordEventHandler#forgotPasswordFailure(io.reflection.app.api.core.shared.call.ForgotPasswordRequest
	 * , java.lang.Throwable)
	 */
	@Override
	public void forgotPasswordFailure(ForgotPasswordRequest input, Throwable caught) {
		if (forgotPasswordForm.isStatusLoading()) {
			forgotPasswordForm.setStatusError();
			loginForm.getElement().getParentElement().removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().tabs__contentIsSubmitted());
			formSubmittedSuccessPanel.removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().isShowing());
		}
	}

}
