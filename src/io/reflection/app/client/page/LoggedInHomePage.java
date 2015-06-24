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
	@UiField AccordionSwitch accordionSwitchMore;

	public LoggedInHomePage() {
		initWidget(uiBinder.createAndBindUi(this));

		populateAccordionSwitches();

	}

	private void populateAccordionSwitches() {
		accordionSwitchUK.addItemComplete("Games");
		accordionSwitchUK.addItemIncomplete("Overall", "");
		accordionSwitchUK.addItemIncomplete("Education", "");
		accordionSwitchUK.addItemIncomplete("Music", "");
		accordionSwitchUK.setSubtitleHtml("(1<span>Category</span>)");

		accordionSwitchDE.addItemIncomplete("Overall", "");
		accordionSwitchDE.addItemIncomplete("Games", "");
		accordionSwitchDE.addItemIncomplete("Education", "");
		accordionSwitchDE.addItemIncomplete("Music", "");

		accordionSwitchFR.addItemIncomplete("Overall", "");
		accordionSwitchFR.addItemIncomplete("Games", "");
		accordionSwitchFR.addItemIncomplete("Education", "");
		accordionSwitchFR.addItemIncomplete("Music", "");

		accordionSwitchIT.addItemIncomplete("Overall", "");
		accordionSwitchIT.addItemIncomplete("Games", "");
		accordionSwitchIT.addItemIncomplete("Education", "");
		accordionSwitchIT.addItemIncomplete("Music", "");

		accordionSwitchES.addItemIncomplete("Overall", "");
		accordionSwitchES.addItemIncomplete("Games", "");
		accordionSwitchES.addItemIncomplete("Education", "");
		accordionSwitchES.addItemIncomplete("Music", "");

		accordionSwitchUS.addItemIncomplete("Overall", "");
		accordionSwitchUS.addItemIncomplete("Games", "");
		accordionSwitchUS.addItemIncomplete("Education", "");
		accordionSwitchUS.addItemIncomplete("Music", "");

		accordionSwitchCA.addItemIncomplete("Overall", "");
		accordionSwitchCA.addItemIncomplete("Games", "");
		accordionSwitchCA.addItemIncomplete("Education", "");
		accordionSwitchCA.addItemIncomplete("Music", "");

		accordionSwitchAU.addItemIncomplete("Overall", "");
		accordionSwitchAU.addItemIncomplete("Games", "");
		accordionSwitchAU.addItemIncomplete("Education", "");
		accordionSwitchAU.addItemIncomplete("Music", "");

		accordionSwitchNZ.addItemIncomplete("Overall", "");
		accordionSwitchNZ.addItemIncomplete("Games", "");
		accordionSwitchNZ.addItemIncomplete("Education", "");
		accordionSwitchNZ.addItemIncomplete("Music", "");

		accordionSwitchIR.addItemIncomplete("Overall", "");
		accordionSwitchIR.addItemIncomplete("Games", "");
		accordionSwitchIR.addItemIncomplete("Education", "");
		accordionSwitchIR.addItemIncomplete("Music", "");

		accordionSwitchMore.addItemIncomplete("Overall", "");
		accordionSwitchMore.addItemIncomplete("Games", "");
		accordionSwitchMore.addItemIncomplete("Education", "");
		accordionSwitchMore.addItemIncomplete("Music", "");

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
