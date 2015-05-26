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
	@UiField AccordionSwitch accordionSwitchJP;

	public LoggedInHomePage() {
		initWidget(uiBinder.createAndBindUi(this));

		populateAccordionSwitches();

	}

	private void populateAccordionSwitches() {
		accordionSwitchUK.addItemComplete("Games");
		accordionSwitchUK.addItemIncomplete("Overall", "Coming Soon");
		accordionSwitchUK.addItemIncomplete("Education", "Coming Soon");
		accordionSwitchUK.addItemIncomplete("Music", "Coming Soon");

		accordionSwitchDE.addItemIncomplete("Overall", "Coming Soon");
		accordionSwitchDE.addItemIncomplete("Games", "Coming Soon");
		accordionSwitchDE.addItemIncomplete("Education", "Coming Soon");
		accordionSwitchDE.addItemIncomplete("Music", "Coming Soon");

		accordionSwitchFR.addItemIncomplete("Overall", "Coming Soon");
		accordionSwitchFR.addItemIncomplete("Games", "Coming Soon");
		accordionSwitchFR.addItemIncomplete("Education", "Coming Soon");
		accordionSwitchFR.addItemIncomplete("Music", "Coming Soon");

		accordionSwitchIT.addItemIncomplete("Overall", "Coming Soon");
		accordionSwitchIT.addItemIncomplete("Games", "Coming Soon");
		accordionSwitchIT.addItemIncomplete("Education", "Coming Soon");
		accordionSwitchIT.addItemIncomplete("Music", "Coming Soon");

		accordionSwitchES.addItemIncomplete("Overall", "Coming Soon");
		accordionSwitchES.addItemIncomplete("Games", "Coming Soon");
		accordionSwitchES.addItemIncomplete("Education", "Coming Soon");
		accordionSwitchES.addItemIncomplete("Music", "Coming Soon");

		accordionSwitchUS.addItemIncomplete("Overall", "Coming Soon");
		accordionSwitchUS.addItemIncomplete("Games", "Coming Soon");
		accordionSwitchUS.addItemIncomplete("Education", "Coming Soon");
		accordionSwitchUS.addItemIncomplete("Music", "Coming Soon");

		accordionSwitchCA.addItemIncomplete("Overall", "Coming Soon");
		accordionSwitchCA.addItemIncomplete("Games", "Coming Soon");
		accordionSwitchCA.addItemIncomplete("Education", "Coming Soon");
		accordionSwitchCA.addItemIncomplete("Music", "Coming Soon");

		accordionSwitchAU.addItemIncomplete("Overall", "Coming Soon");
		accordionSwitchAU.addItemIncomplete("Games", "Coming Soon");
		accordionSwitchAU.addItemIncomplete("Education", "Coming Soon");
		accordionSwitchAU.addItemIncomplete("Music", "Coming Soon");

		accordionSwitchNZ.addItemIncomplete("Overall", "Coming Soon");
		accordionSwitchNZ.addItemIncomplete("Games", "Coming Soon");
		accordionSwitchNZ.addItemIncomplete("Education", "Coming Soon");
		accordionSwitchNZ.addItemIncomplete("Music", "Coming Soon");

		accordionSwitchIR.addItemIncomplete("Overall", "Coming Soon");
		accordionSwitchIR.addItemIncomplete("Games", "Coming Soon");
		accordionSwitchIR.addItemIncomplete("Education", "Coming Soon");
		accordionSwitchIR.addItemIncomplete("Music", "Coming Soon");
		
		accordionSwitchJP.addItemIncomplete("Overall", "Coming Soon");
		accordionSwitchJP.addItemIncomplete("Games", "Coming Soon");
		accordionSwitchJP.addItemIncomplete("Education", "Coming Soon");
		accordionSwitchJP.addItemIncomplete("Music", "Coming Soon");

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
