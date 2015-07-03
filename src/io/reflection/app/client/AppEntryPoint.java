//
//  AppEntryPoint.java
//  storedata
//
//  Created by William Shakour (billy1380) on 2 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client;

import io.reflection.app.client.charts.GwtCanvasBasedCanvasFactory;
import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.helper.DOMHelper;
import io.reflection.app.client.helper.ResponsiveDesignHelper;
import io.reflection.app.client.helper.UserAgentHelper;
import io.reflection.app.client.part.BackToTop;
import io.reflection.app.client.part.SuperAlertBox;
import io.reflection.app.client.res.Styles;

import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.googlecode.gchart.client.GChart;

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

		UserAgentHelper.detectBrowser();

		GChart.setCanvasFactory(new GwtCanvasBasedCanvasFactory());

		// this registers the newly created singleton, so that
		// fireCurrentHistoryState -> onValueChange -> addPages
		History.addValueChangeHandler(NavigationController.get());

		makeContainer();
		Styles.STYLES_INSTANCE.reflectionMainStyle().ensureInjected();
		if (UserAgentHelper.isIE() && UserAgentHelper.getIEVersion() < 9) {
			Styles.STYLES_INSTANCE.reflectionMainIE8Style().ensureInjected();
		}

		SuperAlertBox.start();

		// add page area

		// the above are just place holders, this kicks of the actual page loading
		History.fireCurrentHistoryState();
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
		RootPanel.get().add(new BackToTop());
		UserAgentHelper.initCustomScrollbars();
		Document.get().getHead().appendChild(DOMHelper.getJSScriptFromUrl("js/vendor/picturefillFirefox.js"));
		UserAgentHelper.initIETweaks();
		ResponsiveDesignHelper.initTabsResponsive();

	}
}
