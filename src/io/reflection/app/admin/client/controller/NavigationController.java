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
import io.reflection.app.admin.client.page.UsersPage;
import io.reflection.app.admin.client.part.Footer;
import io.reflection.app.admin.client.part.Header;

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
	private UsersPage mUsersPage = null;
	private Header mHeader = null;
	private Footer mFooter = null;	

	private Stack mStack;

	public static class Stack {
		private String mPage;

		private Stack() {}

		public String getPage() {
			return mPage;
		}

		public static Stack parse(String value) {
			Stack s = new Stack();
			String[] split = value.split("/");

			if (split.length > 0) {
				s.mPage = split[0];
			}

			return s;
		}
	}

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

		mHeader.activateRanks();
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

		mHeader.activateFeedBrowser();
	}

	/**
	 * @param value
	 */
	public void addPage(String value) {
		Stack s = null;
		if (value == null || value.length() == 0) {
			value = "ranks";
			s = Stack.parse(value);
		}
		
		s = Stack.parse(value);

		if ("ranks".equals(s.getPage())) {
			addRanksPage();
		} else if ("feedbrowser".equals(s.getPage())) {
			addFeedBrowserPage();			
		} else if ("users".equals(s.getPage())) {
			addUsersPage();
		}

		mStack = s;
	}

	/**
	 * @return
	 */
	public Widget getHeader() {
		if (mHeader == null) {
			mHeader = new Header();
		}
		return mHeader;
	}

	/**
	 * @return
	 */
	public Widget getFooter() {
		if (mFooter == null) {
			mFooter = new Footer();
		}
		return mFooter;
	}

	public String getCurrentPage() {
		return mStack.getPage();
	}

	/**
	 * 
	 */
	public void addUsersPage() {
		if (mUsersPage == null) {
			mUsersPage = new UsersPage();
		}

		if (!mUsersPage.isAttached()) {
			mPanel.clear();
			mPanel.add(mUsersPage);
		} else {}

		mHeader.activateUsers();
		
	}
}
