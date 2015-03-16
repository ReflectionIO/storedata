//
//  ForgotPasswordPage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 5 Mar 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page;

import io.reflection.app.api.core.shared.call.ForgotPasswordRequest;
import io.reflection.app.api.core.shared.call.ForgotPasswordResponse;
import io.reflection.app.api.core.shared.call.event.ForgotPasswordEventHandler;
import io.reflection.app.client.DefaultEventBus;
import io.reflection.app.client.controller.SessionController;
import io.reflection.app.client.helper.FormHelper;
import io.reflection.app.client.part.login.ForgotPasswordForm;
import io.reflection.app.client.res.Styles;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.Widget;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author billy1380
 * 
 */
public class ForgotPasswordPage extends Page implements ForgotPasswordEventHandler {

	private static ForgotPasswordPageUiBinder uiBinder = GWT.create(ForgotPasswordPageUiBinder.class);

	interface ForgotPasswordPageUiBinder extends UiBinder<Widget, ForgotPasswordPage> {}

	@UiField ForgotPasswordForm forgotPasswordForm;
	@UiField LIElement tabContentLogin;
	@UiField DivElement submittedSuccessPanel;
	@UiField InlineHyperlink register;
	@UiField InlineHyperlink login;

	public ForgotPasswordPage() {
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
		super.onAttach();

		Document.get().getBody().addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().accountAccessPage());
		Document.get().getBody().addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().loginFormIsShowing());
		Document.get().getBody().addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().resetPasswordFormIsShowing());

		tabContentLogin.removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().tabs__contentIsSubmitted());
		submittedSuccessPanel.removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().isShowing());

		register(DefaultEventBus.get().addHandlerToSource(ForgotPasswordEventHandler.TYPE, SessionController.get(), this));

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
		if (output.status == StatusType.StatusTypeSuccess) {
			if (!tabContentLogin.hasClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().tabs__contentIsSubmitted())) {
				tabContentLogin.addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().tabs__contentIsSubmitted());
			}
			if (!submittedSuccessPanel.hasClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().isShowing())) {
				submittedSuccessPanel.addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().isShowing());
			}
		} else {
			tabContentLogin.removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().tabs__contentIsSubmitted());
			submittedSuccessPanel.removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().isShowing());
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
		tabContentLogin.removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().tabs__contentIsSubmitted());
		submittedSuccessPanel.removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().isShowing());
	}

}
