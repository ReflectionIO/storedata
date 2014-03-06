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
import io.reflection.app.client.controller.EventController;
import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.controller.NavigationController.Stack;
import io.reflection.app.client.controller.SessionController;
import io.reflection.app.client.handler.NavigationEventHandler;
import io.reflection.app.client.part.login.ResetPasswordForm;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Widget;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author billy1380
 * 
 */
public class ResetPasswordPage extends Page implements ChangePasswordEventHandler, NavigationEventHandler {

	private static ResetPasswordPageUiBinder uiBinder = GWT.create(ResetPasswordPageUiBinder.class);

	interface ResetPasswordPageUiBinder extends UiBinder<Widget, ResetPasswordPage> {}

	// @UiField HTMLPanel reminder;
	@UiField ResetPasswordForm form;

	public ResetPasswordPage() {
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

		// reminder.setVisible(false);
		form.setVisible(true);

		register(EventController.get().addHandlerToSource(NavigationEventHandler.TYPE, NavigationController.get(), this));
		register(EventController.get().addHandlerToSource(ChangePasswordEventHandler.TYPE, SessionController.get(), this));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.NavigationEventHandler#navigationChanged(io.reflection.app.client.controller.NavigationController.Stack)
	 */
	@Override
	public void navigationChanged(Stack stack) {
		String resetCode = stack.getParameter(0);

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
			History.newItem("login");
		} else {
			// TODO: the error panel
			form.enableForm();
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
	public void changePasswordFailure(ChangePasswordRequest input, Throwable caught) {
		// TODO: the error panel
		form.enableForm();
	}

}
