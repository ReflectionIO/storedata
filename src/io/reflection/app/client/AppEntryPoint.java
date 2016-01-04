//
//  AppEntryPoint.java
//  storedata
//
//  Created by William Shakour (billy1380) on 2 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client;

import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.helper.DOMHelper;
import io.reflection.app.client.helper.ResponsiveDesignHelper;
import io.reflection.app.client.helper.TooltipHelper;
import io.reflection.app.client.helper.UserAgentHelper;
import io.reflection.app.client.mixpanel.MixpanelHelper;
import io.reflection.app.client.page.AppDetails;
import io.reflection.app.client.part.BackToTop;
import io.reflection.app.client.res.Styles;

import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * @author billy1380
 *
 */
public class AppEntryPoint extends ErrorHandlingEntryPoint {

	private HTMLPanel lPageContainer = new HTMLPanel("");

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.core.client.EntryPoint#onModuleLoad()
	 */
	@Override
	public void onModuleLoad() {
		super.onModuleLoad();
		AppDetails.exportAppDetailsResponseHandler();
		AppDetails.exportMoreAppDetailsResponseHandler();
		
		UserAgentHelper.detectBrowser();

		// this registers the newly created singleton, so that
		// fireCurrentHistoryState -> onValueChange -> addPages
		History.addValueChangeHandler(NavigationController.get());

		Styles.STYLES_INSTANCE.reflectionMainStyle().ensureInjected();

		if (UserAgentHelper.checkIECompatibility()) {

			makeContainer();

			MixpanelHelper.init(); // Run Mixpanel with proper token

			// SuperAlertBox.start();

			// add page area

			// the above are just place holders, this kicks of the actual page loading
			History.fireCurrentHistoryState();

			TooltipHelper.updateHelperTooltip();

		}
	}

	private void makeContainer() {
		// PAGE MAINTENANCE RootPanel.get().add(new SiteMaintenance());

		lPageContainer.getElement().setClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().lPageContainer());
		RootPanel.get().add(NavigationController.get().getHeader());
		RootPanel.get().add(NavigationController.get().getPanelLeftMenu());
		RootPanel.get().add(lPageContainer);
		lPageContainer.add(NavigationController.get().getMainPanel());
		RootPanel.get().add(NavigationController.get().getPanelRightAccount());
		RootPanel.get().add(NavigationController.get().getPanelRightSearch());
		RootPanel.get().add(NavigationController.get().getPanelFooter());
		RootPanel.get().add(new BackToTop());
		UserAgentHelper.initCustomScrollbars();
		Document.get().getHead().appendChild(DOMHelper.getJSScriptFromUrl("js/vendor/picturefillFirefox.js"));
		ResponsiveDesignHelper.initTabsResponsive();
	}
}
