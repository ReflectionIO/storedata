//
//  LoggedInHomePage.java
//  storedata
//
//  Created by Stefano Capuzzi (capuzzistefano) on 21 Apr 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page;

import io.reflection.app.client.component.AccordionSwitch;
import io.reflection.app.client.controller.SessionController;
import io.reflection.app.datatypes.shared.User;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Stefano Capuzzi (capuzzistefano)
 *
 */
public class LoggedInHomePage extends Page {

	private static LoggedInHomePageUiBinder uiBinder = GWT.create(LoggedInHomePageUiBinder.class);

	interface LoggedInHomePageUiBinder extends UiBinder<Widget, LoggedInHomePage> {}

	@UiField SpanElement userName;

	@UiField AccordionSwitch accordionSwitchUK;
	@UiField AccordionSwitch accordionSwitchDE;
	@UiField AccordionSwitch accordionSwitchFR;
	@UiField AccordionSwitch accordionSwitchIT;
	@UiField AccordionSwitch accordionSwitchES;
	@UiField AccordionSwitch accordionSwitchUS;
	@UiField AccordionSwitch accordionSwitchCA;
	@UiField AccordionSwitch accordionSwitchAU;
	@UiField AccordionSwitch accordionSwitchNZ;
	@UiField AccordionSwitch accordionSwitchIR;

	public LoggedInHomePage() {
		initWidget(uiBinder.createAndBindUi(this));

		populateAccordionSwitches();

	}

	private void populateAccordionSwitches() {
		accordionSwitchUK.addItemComplete("Overall");
		accordionSwitchUK.addItemComplete("Games");
		accordionSwitchUK.addItemComplete("Health");
		accordionSwitchUK.addItemComplete("Productivity");

		accordionSwitchDE.addItemComplete("Overall");
		accordionSwitchDE.addItemComplete("Games");
		accordionSwitchDE.addItemIncomplete("Health", "Coming Soon");
		accordionSwitchDE.addItemIncomplete("Productivity", "Coming Soon");

		accordionSwitchFR.addItemComplete("Overall");
		accordionSwitchFR.addItemComplete("Games");
		accordionSwitchFR.addItemIncomplete("Health", "Coming Soon");
		accordionSwitchFR.addItemIncomplete("Productivity", "Coming Soon");

		accordionSwitchIT.addItemComplete("Overall");
		accordionSwitchIT.addItemComplete("Games");
		accordionSwitchIT.addItemIncomplete("Health", "Coming Soon");
		accordionSwitchIT.addItemIncomplete("Productivity", "Coming Soon");

		accordionSwitchES.addItemComplete("Overall");
		accordionSwitchES.addItemComplete("Games");
		accordionSwitchES.addItemIncomplete("Health", "Coming Soon");
		accordionSwitchES.addItemIncomplete("Productivity", "Coming Soon");

		accordionSwitchUS.addItemComplete("Overall");
		accordionSwitchUS.addItemComplete("Games");
		accordionSwitchUS.addItemIncomplete("Health", "Coming Soon");
		accordionSwitchUS.addItemIncomplete("Productivity", "Coming Soon");

		accordionSwitchCA.addItemComplete("Overall");
		accordionSwitchCA.addItemComplete("Games");
		accordionSwitchCA.addItemIncomplete("Health", "Coming Soon");
		accordionSwitchCA.addItemIncomplete("Productivity", "Coming Soon");

		accordionSwitchAU.addItemComplete("Overall");
		accordionSwitchAU.addItemComplete("Games");
		accordionSwitchAU.addItemIncomplete("Health", "Coming Soon");
		accordionSwitchAU.addItemIncomplete("Productivity", "Coming Soon");

		accordionSwitchNZ.addItemComplete("Overall");
		accordionSwitchNZ.addItemComplete("Games");
		accordionSwitchNZ.addItemIncomplete("Health", "Coming Soon");
		accordionSwitchNZ.addItemIncomplete("Productivity", "Coming Soon");

		accordionSwitchIR.addItemComplete("Overall");
		accordionSwitchIR.addItemComplete("Games");
		accordionSwitchIR.addItemIncomplete("Health", "Coming Soon");
		accordionSwitchIR.addItemIncomplete("Productivity", "Coming Soon");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.page.Page#onAttach()
	 */
	@Override
	protected void onAttach() {
		super.onAttach();

		User user = SessionController.get().getLoggedInUser();
		if (user != null) {
			userName.setInnerText(user.forename + ",");
		}
	}

}
