//
//  AddLinkedAccountPopup.java
//  storedata
//
//  Created by Stefano Capuzzi (capuzzistefano) on 27 Sep 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.popup;

import io.reflection.app.api.core.shared.call.LinkAccountRequest;
import io.reflection.app.api.core.shared.call.LinkAccountResponse;
import io.reflection.app.api.core.shared.call.event.LinkAccountEventHandler;
import io.reflection.app.api.shared.ApiError;
import io.reflection.app.client.DefaultEventBus;
import io.reflection.app.client.controller.LinkedAccountController;
import io.reflection.app.client.part.linkaccount.IosMacLinkAccountForm;
import io.reflection.app.client.part.linkaccount.LinkedAccountChangeEvent.EVENT_TYPE;
import io.reflection.app.client.part.linkaccount.LinkedAccountChangeEvent.LinkedAccountChangeEventHandler;
import io.reflection.app.client.res.Styles;
import io.reflection.app.datatypes.shared.DataAccount;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author Stefano Capuzzi (capuzzistefano)
 *
 */
public class AddLinkedAccountPopup extends Composite implements LinkAccountEventHandler {

	private static AddLinkedAccountPopupUiBinder uiBinder = GWT.create(AddLinkedAccountPopupUiBinder.class);

	interface AddLinkedAccountPopupUiBinder extends UiBinder<Widget, AddLinkedAccountPopup> {}

	@UiField PopupBase popup;
	@UiField IosMacLinkAccountForm iosMacAddForm;

	public AddLinkedAccountPopup() {
		initWidget(uiBinder.createAndBindUi(this));

		popup.getElement()
				.getFirstChildElement()
				.getFirstChildElement()
				.addClassName(
						Styles.STYLES_INSTANCE.reflectionMainStyle().popupContentLinkAccount() + " "
								+ Styles.STYLES_INSTANCE.reflectionMainStyle().formsMidTheme());

		iosMacAddForm.addLinkedAccountChangeEventHander(new LinkedAccountChangeEventHandler() {

			@Override
			public void onChange(DataAccount dataAccount, EVENT_TYPE eventType) {
				iosMacAddForm.setEnabled(false);
				iosMacAddForm.setStatusLoading("Loading");
				Document.get().getBody().addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().formSubmittedLoading());
				LinkedAccountController.get().linkAccount(iosMacAddForm.getAccountSourceId(), iosMacAddForm.getUsername(), iosMacAddForm.getPassword());
			}
		});
	}

	public void show(String title, String subtitle) {
		if (!this.asWidget().isAttached()) {
			RootPanel.get().add(this);
			DefaultEventBus.get().addHandlerToSource(LinkAccountEventHandler.TYPE, LinkedAccountController.get(), this);
		}
		iosMacAddForm.resetForm();
		iosMacAddForm.setTitleText(title);
		if (subtitle != null) {
			iosMacAddForm.setSubtitleText(subtitle);
		}
		popup.show();
	}

	public void hide() {
		popup.closePopup();
	}

	@UiHandler("popup")
	void onPopupClosed(CloseEvent<PopupBase> event) {
		RootPanel.get().remove(this.asWidget());
		iosMacAddForm.resetForm();
		Document.get().getBody().removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().formSubmittedLoading());
	}

	public void setStatusSuccess() {

		iosMacAddForm.setStatusSuccess("Success!");
		Timer t = new Timer() {

			@Override
			public void run() {
				Document.get().getBody().removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().formSubmittedLoading());
				hide();
			}
		};
		t.schedule(1000);
	}

	public void setStatusError() {
		iosMacAddForm.setStatusError();
	}

	public void setStatusErrorInvalidCredentials() {
		iosMacAddForm.setStatusError("Invalid credentials!");
		iosMacAddForm.setUsernameError("iTunes Connect username or password entered incorrectly");
		iosMacAddForm.setPasswordError("iTunes Connect username or password entered incorrectly");
		iosMacAddForm.setFormErrors();
	}

	public void setStatusErrorDuplicateVendorId() {
		iosMacAddForm.setStatusError("Account already linked!");
		iosMacAddForm.setEnabled(true);
		iosMacAddForm.setUsernameError("The vendor ID you entered is already in use");
		iosMacAddForm.setPasswordError("The vendor ID you entered is already in use");
		iosMacAddForm.setFormErrors();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.core.shared.call.event.LinkAccountEventHandler#linkAccountSuccess(io.reflection.app.api.core.shared.call.LinkAccountRequest,
	 * io.reflection.app.api.core.shared.call.LinkAccountResponse)
	 */
	@Override
	public void linkAccountSuccess(LinkAccountRequest input, LinkAccountResponse output) {
		if (output.status == StatusType.StatusTypeSuccess) {
			setStatusSuccess();
		} else if (output.error != null) {
			if (output.error.code == ApiError.InvalidDataAccountCredentials.getCode()) {
				setStatusErrorInvalidCredentials();
			} else if (output.error.code == ApiError.DuplicateVendorId.getCode()) {
				setStatusErrorDuplicateVendorId();
			} else {
				setStatusError();
			}
			iosMacAddForm.setEnabled(true);
			Document.get().getBody().removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().formSubmittedLoading());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.core.shared.call.event.LinkAccountEventHandler#linkAccountFailure(io.reflection.app.api.core.shared.call.LinkAccountRequest,
	 * java.lang.Throwable)
	 */
	@Override
	public void linkAccountFailure(LinkAccountRequest input, Throwable caught) {
		setStatusError();
	}

}
