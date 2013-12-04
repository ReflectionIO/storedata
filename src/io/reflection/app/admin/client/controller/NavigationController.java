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
import io.reflection.app.admin.client.page.PermissionsPage;
import io.reflection.app.admin.client.page.RanksPage;
import io.reflection.app.admin.client.page.RegisterPage;
import io.reflection.app.admin.client.page.RolesPage;
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
	private RolesPage mRolesPage = null;
	private PermissionsPage mPermissionsPage = null;

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

	public void addRolesPage() {
		if (mRolesPage == null) {
			mRolesPage = new RolesPage();
		}

		if (!mRolesPage.isAttached()) {
			mPanel.clear();
			mPanel.add(mRolesPage);
		}
	}

	public void addPermissionsPage() {
		if (mPermissionsPage == null) {
			mPermissionsPage = new PermissionsPage();
		}

		if (!mPermissionsPage.isAttached()) {
			mPanel.clear();
			mPanel.add(mPermissionsPage);
		}
	}

	/**
	 * @param value
	 */
	public void addPage(String value) {

		if (value == null || value.length() == 0) {
			value = "ranks";
		}

		mStack = Stack.parse(value);

		if ("ranks".equals(mStack.getPage())) {
			addRanksPage();
		} else if ("feedbrowser".equals(mStack.getPage())) {
			addFeedBrowserPage();
		} else if ("users".equals(mStack.getPage())) {
			if (mStack.getAction() == null) {
				addUsersPage();
			} else if ("changepassword".equals(mStack.getAction())) {
				addChangePasswordPage();
			} else if ("assignrole".equals(mStack.getAction())) {
				String userId = mStack.getParameter(0);
				String roleName = mStack.getParameter(1);

				if (userId != null) {
					if (roleName.equalsIgnoreCase("admin")) {
						UserController.get().makeAdmin(Long.valueOf(userId));
					}
				}

				History.newItem("users");
				return;
			}
		} else if ("login".equals(mStack.getPage())) {
			addLoginPage();
		} else if ("register".equals(mStack.getPage())) {
			addRegisterPage();
		} else if ("logout".equals(mStack.getPage())) {
			SessionController.get().logout();
			History.newItem("login");
			return;
		} else if ("roles".equals(mStack.getPage())) {
			addRolesPage();
		} else if ("permissions".equals(mStack.getPage())) {
			addPermissionsPage();
		}

		EventController.get().fireEventFromSource(new NavigationEventHandler.ChangedEvent(mStack), NavigationController.this);
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
