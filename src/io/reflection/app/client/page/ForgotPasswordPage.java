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
import io.reflection.app.client.part.Preloader;
import io.reflection.app.client.part.login.ForgotPasswordForm;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
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

	@UiField HTMLPanel reminder;
	@UiField ForgotPasswordForm form;
	@UiField Preloader preloader;
	@UiField InlineHyperlink tryAgainLink;

	public ForgotPasswordPage() {
		initWidget(uiBinder.createAndBindUi(this));

		tryAgainLink.setTargetHistoryToken(PageType.LoginPageType.asTargetHistoryToken("requestinvite"));

		form.setPreloader(preloader); // Assign the preloader reference to the Forgot Password Form
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.Composite#onAttach()
	 */
	@Override
	protected void onAttach() {
		super.onAttach();

		reminder.setVisible(false);
		form.setVisible(true);

		register(DefaultEventBus.get().addHandlerToSource(ForgotPasswordEventHandler.TYPE, SessionController.get(), this));
		register(DefaultEventBus.get().addHandlerToSource(ForgotPasswordEventHandler.TYPE, SessionController.get(), form));

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
			form.setVisible(false);
			reminder.setVisible(true);
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
	public void forgotPasswordFailure(ForgotPasswordRequest input, Throwable caught) {}

}
