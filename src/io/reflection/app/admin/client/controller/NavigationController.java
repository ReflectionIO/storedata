//
//  NavigationController.java
//  storedata
//
//  Created by William Shakour (billy1380) on 3 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.admin.client.controller;

import io.reflection.app.admin.client.handler.NavigationEventHandler;
import io.reflection.app.admin.client.page.ChangePasswordPage;
import io.reflection.app.admin.client.page.FeedBrowserPage;
import io.reflection.app.admin.client.page.LoginPage;
import io.reflection.app.admin.client.page.RanksPage;
import io.reflection.app.admin.client.page.RegisterPage;
import io.reflection.app.admin.client.page.UsersPage;
import io.reflection.app.admin.client.part.Footer;
import io.reflection.app.admin.client.part.Header;

import com.google.gwt.user.client.History;
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
	private LoginPage mLoginPage = null;
	private RegisterPage mRegisterPage = null;
	private ChangePasswordPage mChangePasswordPage = null;

	private Header mHeader = null;
	private Footer mFooter = null;

	private Stack mStack;

	public static class Stack {
		private String[] mParts;

		private Stack(String value) {
			mParts = value.split("/");
		}

		public String getPage() {
			return mParts.length > 0 ? mParts[0] : null;
		}

		public String getAction() {
			return mParts.length > 1 ? mParts[1] : null;
		}

		public String getParameter(int index) {
			return mParts.length > (2 + index) ? mParts[2 + index] : null;
		}

		public static Stack parse(String value) {
			return new Stack(value);
		}

		/**
		 * @return
		 */
		public boolean hasAction() {
			return getAction() != null;
		}

		public boolean hasPage() {
			return getPage() != null;
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

	public void addLoginPage() {
		if (mLoginPage == null) {
			mLoginPage = new LoginPage();
		}

		if (!mLoginPage.isAttached()) {
			mPanel.clear();
			mPanel.add(mLoginPage);
		} else {}

	}

	public void addRegisterPage() {
		if (mRegisterPage == null) {
			mRegisterPage = new RegisterPage();
		}

		if (!mRegisterPage.isAttached()) {
			mPanel.clear();
			mPanel.add(mRegisterPage);
		} else {}
	}

	public void addChangePasswordPage() {
		if (mChangePasswordPage == null) {
			mChangePasswordPage = new ChangePasswordPage();
		}

		if (!mChangePasswordPage.isAttached()) {
			mPanel.clear();
			mPanel.add(mChangePasswordPage);
		}
	}

	/**
	 * @param value
	 */
	public void addPage(String value) {
		Stack s = null;
		if (value == null || value.length() == 0) {
			value = "ranks";
		}

		s = Stack.parse(value);

		if ("ranks".equals(s.getPage())) {
			addRanksPage();
		} else if ("feedbrowser".equals(s.getPage())) {
			addFeedBrowserPage();
		} else if ("users".equals(s.getPage())) {
			if (s.getAction() == null) {
				addUsersPage();
			} else if ("changepassword".equals(s.getAction())) {
				addChangePasswordPage();
			} else if ("assignrole".equals(s.getAction())) {
				String userId = s.getParameter(0);
				String roleName = s.getParameter(1);
				
				if (userId != null) {
					if (roleName.equalsIgnoreCase("admin")) {
						UserController.get().makeAdmin(Long.valueOf(userId));
					}
				}
				
				History.newItem("users");
				return;
			}
		} else if ("login".equals(s.getPage())) {
			addLoginPage();
		} else if ("register".equals(s.getPage())) {
			addRegisterPage();
		} else if ("logout".equals(s.getPage())) {
			SessionController.get().logout();
			History.newItem("login");
			return;
		}

		mStack = s;

		EventController.get().fireEventFromSource(new NavigationEventHandler.ChangedEvent(s), NavigationController.this);
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

	}

	public Stack getStack() {
		return mStack;
	}
}
