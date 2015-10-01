//
//  PricingPage.java
//  storedata
//
//  Created by Stefano Capuzzi (capuzzistefano) on 14 Sep 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Stefano Capuzzi (capuzzistefano)
 *
 */
public class PricingPage extends Page {

	private static PricingPageUiBinder uiBinder = GWT.create(PricingPageUiBinder.class);

	interface PricingPageUiBinder extends UiBinder<Widget, PricingPage> {}

	@UiField Button signUpBtn;

	public PricingPage() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiHandler("signUpBtn")
	void onSignUpClicked(ClickEvent event) {
		event.preventDefault();
		PageType.RegisterPageType.show();
	}
}
