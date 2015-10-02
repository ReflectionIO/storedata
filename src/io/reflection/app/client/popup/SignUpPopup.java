//
//  SignUpPopup.java
//  storedata
//
//  Created by Stefano Capuzzi (capuzzistefano) on 25 Sep 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.popup;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Stefano Capuzzi (capuzzistefano)
 *
 */
public class SignUpPopup extends Composite {

	private static SignUpPopupUiBinder uiBinder = GWT.create(SignUpPopupUiBinder.class);

	interface SignUpPopupUiBinder extends UiBinder<Widget, SignUpPopup> {}

	@UiField PopupBase popup;
	@UiField InlineHyperlink signUpLink;
	@UiField InlineHyperlink logInLink;

	public SignUpPopup() {
		initWidget(uiBinder.createAndBindUi(this));

	}

	public void show() {
		if (!this.asWidget().isAttached()) {
			RootPanel.get().add(this);
		}
		popup.show();
	}

	public void hide() {
		popup.closePopup();
	}

	@UiHandler("popup")
	void onPopupClosed(CloseEvent<PopupBase> event) {
		RootPanel.get().remove(this.asWidget());
	}

	@UiHandler({ "signUpLink", "logInLink" })
	void onCloseLinksClicked(ClickEvent event) {
		event.preventDefault();
		popup.closePopup();
	}

}
