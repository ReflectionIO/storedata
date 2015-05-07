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
import io.reflection.app.client.helper.DOMHelper;
import io.reflection.app.client.part.login.ResetPasswordForm;
import io.reflection.app.client.res.Styles;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
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

	@UiField ResetPasswordForm form;
	@UiField DivElement formSubmittedSuccessPanel;
	@UiField AnchorElement continueToSiteLink;

	public ResetPasswordPage() {
		initWidget(uiBinder.createAndBindUi(this));

		continueToSiteLink.setHref(PageType.LoginPageType.asHref("requestinvite"));	
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.Composite#onAttach()
	 */
	@Override
	protected void onAttach() {
		super.onAttach();

		DOMHelper.addClassName(Document.get().getBody(), Styles.STYLES_INSTANCE.reflectionMainStyle().accountAccessPage());
		DOMHelper.addClassName(Document.get().getBody(), Styles.STYLES_INSTANCE.reflectionMainStyle().connectAccountIsShowing());

		register(DefaultEventBus.get().addHandlerToSource(NavigationEventHandler.TYPE, NavigationController.get(), this));
		register(DefaultEventBus.get().addHandlerToSource(ChangePasswordEventHandler.TYPE, SessionController.get(), this));
		register(DefaultEventBus.get().addHandlerToSource(ChangePasswordEventHandler.TYPE, SessionController.get(), form));

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
		Document.get().getBody().removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().connectAccountIsShowing());
		Document.get().getBody().removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().formSubmittedSuccessComplete());
		formSubmittedSuccessPanel.removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().isShowing());

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
			DOMHelper.addClassName(Document.get().getBody(), Styles.STYLES_INSTANCE.reflectionMainStyle().formSubmittedSuccessComplete());
			form.setStatusSuccess();
			DOMHelper.addClassName(formSubmittedSuccessPanel, Styles.STYLES_INSTANCE.reflectionMainStyle().isShowing());
		} else {

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
