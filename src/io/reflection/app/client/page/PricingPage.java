//
//  PricingPage.java
//  storedata
//
//  Created by Stefano Capuzzi (capuzzistefano) on 14 Sep 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page;

import io.reflection.app.client.helper.TooltipHelper;
import io.reflection.app.client.popup.RegisterInterestPopup;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
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
	@UiField Button registerInterest;
	private RegisterInterestPopup registerInterestPopup = new RegisterInterestPopup();

	@UiField AnchorElement comingSoon;

	public PricingPage() {
		initWidget(uiBinder.createAndBindUi(this));

		Event.sinkEvents(comingSoon, Event.ONCLICK);
		Event.setEventListener(comingSoon, new EventListener() {

			@Override
			public void onBrowserEvent(Event event) {
				event.preventDefault();
			}
		});
		TooltipHelper.updateWhatsThisTooltip();
	}

	@UiHandler("signUpBtn")
	void onSignUpClicked(ClickEvent event) {
		event.preventDefault();
		PageType.RegisterPageType.show();
	}

	@UiHandler("registerInterest")
	void onRegsiterInterestClicked(ClickEvent event) {
		event.preventDefault();
		registerInterestPopup.show();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.page.Page#onDetach()
	 */
	@Override
	protected void onDetach() {
		super.onDetach();

		registerInterestPopup.hide();
	}
}
