//
//  ResetPasswordPage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 5 Mar 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page;

import io.reflection.app.api.core.shared.call.ChangePasswordRequest;
import io.reflection.app.api.core.shared.call.ChangePasswordResponse;
import io.reflection.app.api.core.shared.call.event.ChangePasswordEventHandler;
import io.reflection.app.client.DefaultEventBus;
import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.controller.NavigationController.Stack;
import io.reflection.app.client.controller.SessionController;
import io.reflection.app.client.handler.NavigationEventHandler;
import io.reflection.app.client.part.Preloader;
import io.reflection.app.client.part.login.ResetPasswordForm;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author billy1380
 * 
 */
public class ResetPasswordPage extends Page implements NavigationEventHandler, ChangePasswordEventHandler {

	private static ResetPasswordPageUiBinder uiBinder = GWT.create(ResetPasswordPageUiBinder.class);

	interface ResetPasswordPageUiBinder extends UiBinder<Widget, ResetPasswordPage> {}

	// @UiField HTMLPanel reminder;
	@UiField ResetPasswordForm form;
	@UiField Preloader preloader;

	public ResetPasswordPage() {
		initWidget(uiBinder.createAndBindUi(this));

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

		form.setVisible(true);

		register(DefaultEventBus.get().addHandlerToSource(NavigationEventHandler.TYPE, NavigationController.get(), this));
		register(DefaultEventBus.get().addHandlerToSource(ChangePasswordEventHandler.TYPE, SessionController.get(), this));
		register(DefaultEventBus.get().addHandlerToSource(ChangePasswordEventHandler.TYPE, SessionController.get(), form));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.NavigationEventHandler#navigationChanged(io.reflection.app.client.controller.NavigationController.Stack,
	 * io.reflection.app.client.controller.NavigationController.Stack)
	 */
	@Override
	public void navigationChanged(Stack previous, Stack current) {
		String resetCode = current.getParameter(0);

		if (resetCode != null && resetCode.length() > 0) {
			form.setResetCode(resetCode);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.reflection.app.api.core.shared.call.event.ChangePasswordEventHandler#changePasswordSuccess(io.reflection.app.api.core.shared.call.ChangePasswordRequest
	 * , io.reflection.app.api.core.shared.call.ChangePasswordResponse)
	 */
	@Override
	public void changePasswordSuccess(ChangePasswordRequest input, ChangePasswordResponse output) {
		if (output.status == StatusType.StatusTypeSuccess) {
			PageType.LoginPageType.show();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.reflection.app.api.core.shared.call.event.ChangePasswordEventHandler#changePasswordFailure(io.reflection.app.api.core.shared.call.ChangePasswordRequest
	 * , java.lang.Throwable)
	 */
	@Override
	public void changePasswordFailure(ChangePasswordRequest input, Throwable caught) {}

}
