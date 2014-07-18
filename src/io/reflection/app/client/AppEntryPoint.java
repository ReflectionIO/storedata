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
import io.reflection.app.client.part.SuperAlertBox;
import io.reflection.app.client.res.Styles;

import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.googlecode.gchart.client.GChart;

/**
 * @author billy1380
 * 
 */
public class AppEntryPoint extends ErrorHandlingEntryPoint {

	private HTMLPanel mContainer;

	static {
		Styles.INSTANCE.reflection().ensureInjected();
		String mediaQueries = " @media (max-width: 1024px) {."
				+ Styles.INSTANCE.reflection().footer()
				+ " {display:none;} .navbar-fixed-top {position:relative;} .navbar {margin-bottom:0px;} .container-fluid{padding-top:0px !important; padding-bottom: 0px !important}}"
				+ "@media (min-width: 992px) {html,body,#content,.container-fluid,#content>.container-fluid>.row{height: 100%;} .sidepanel{height: 100%;margin-bottom:0px;}}";
		StyleInjector.injectAtEnd(mediaQueries);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.core.client.EntryPoint#onModuleLoad()
	 */
	@Override
	public void onModuleLoad() {
		super.onModuleLoad();

		GChart.setCanvasFactory(new GwtCanvasBasedCanvasFactory());

		History.addValueChangeHandler(NavigationController.get());

		makeContainer();

		SuperAlertBox.start();

		// add header
		mContainer.add(NavigationController.get().getHeader());

		// add page area
		mContainer.add(NavigationController.get().getPageHolderPanel());

		// add footer
		mContainer.add(NavigationController.get().getFooter());

		History.fireCurrentHistoryState();

	}

	private void makeContainer() {
		Styles.INSTANCE.reflection().ensureInjected();

		mContainer = new HTMLPanel("");
		mContainer.getElement().setId("content");
		RootPanel.get().add(mContainer);
	}

}
