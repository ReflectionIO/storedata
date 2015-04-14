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
import io.reflection.app.client.helper.UserAgentHelper;
import io.reflection.app.client.part.BackToTop;
import io.reflection.app.client.part.SuperAlertBox;
import io.reflection.app.client.res.Styles;

import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
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
		if (UserAgentHelper.isIE()) {
			Window.addResizeHandler(new ResizeHandler() {
				@Override
				public void onResize(ResizeEvent event) {
					UserAgentHelper.setMainContentWidthForIE();
				}
			});
			if (UserAgentHelper.getIEVersion() < 9) {
				HTMLPanel outdatedBrowser = new HTMLPanel(
						SafeHtmlUtils
								.fromTrustedString("<p>Uh oh... Reflection doesn't work in this browser. &nbsp; &nbsp;<a href=\"http://outdatedbrowser.com/en\" target=\"_blank\">Download a compatible browser</a></p>"));
				outdatedBrowser.setStyleName("window-warning");
				RootPanel.get().add(outdatedBrowser);
			}
		}
		lPageContainer.getElement().setClassName("l-page-container");
		RootPanel.get().add(NavigationController.get().getHeader());
		RootPanel.get().add(NavigationController.get().getPanelLeftMenu());
		RootPanel.get().add(lPageContainer);
		lPageContainer.add(NavigationController.get().getMainPanel());
		RootPanel.get().add(NavigationController.get().getPanelRightAccount());
		RootPanel.get().add(NavigationController.get().getPanelRightSearch());
		RootPanel.get().add(new BackToTop());
		UserAgentHelper.initCustomScrollbars();
	}
}
