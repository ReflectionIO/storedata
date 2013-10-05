//
//  NavigationController.java
//  storedata
//
//  Created by William Shakour (billy1380) on 3 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.admin.client.controller;

import io.reflection.app.admin.client.page.FeedBrowserPage;
import io.reflection.app.admin.client.page.RanksPage;

import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author billy1380
 * 
 */
public class NavigationController {
	private static NavigationController mOne = null;

	private HTMLPanel mPanel = null;
	private RanksPage mRanksPage = null;
	private FeedBrowserPage mFeedBrowserPage = null;

	public static NavigationController get() {
		if (mOne == null) {
			mOne = new NavigationController();
		}

		return mOne;
	}

	/**
	 * @return
	 */
	public Widget getPageHolderPanel() {

		if (mPanel == null) {
			mPanel = new HTMLPanel("<!-- pages go here -->");
			mPanel.setStyleName("container");
			mPanel.getElement().setAttribute("style", "padding: 70px 0px 40px 0px;");
		}

		return mPanel;
	}

	/**
	 * 
	 */
	public void addRanksPage() {
		if (mRanksPage == null) {
			mRanksPage = new RanksPage();
		}

		if (!mRanksPage.isAttached()) {
			mPanel.clear();
			mPanel.add(mRanksPage);
		} else {}

	}

	/**
	 * 
	 */
	public void addFeedBrowserPage() {
		if (mFeedBrowserPage == null) {
			mFeedBrowserPage = new FeedBrowserPage();
		}

		if (!mFeedBrowserPage.isAttached()) {
			mPanel.clear();
			mPanel.add(mFeedBrowserPage);
		} else {}
	}

}
