//
//  NavigationController.java
//  storedata
//
//  Created by William Shakour (billy1380) on 3 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.controller;

import io.reflection.app.client.handler.NavigationEventHandler;
import io.reflection.app.client.page.ChangeDetailsPage;
import io.reflection.app.client.page.ChangePasswordPage;
import io.reflection.app.client.page.FeedBrowserPage;
import io.reflection.app.client.page.HomePage;
import io.reflection.app.client.page.ItemPage;
import io.reflection.app.client.page.LinkItunesPage;
import io.reflection.app.client.page.LinkedAccountsPage;
import io.reflection.app.client.page.LoginPage;
import io.reflection.app.client.page.PermissionsPage;
import io.reflection.app.client.page.RanksPage;
import io.reflection.app.client.page.RegisterPage;
import io.reflection.app.client.page.RequestInvitePage;
import io.reflection.app.client.page.RolesPage;
import io.reflection.app.client.page.SearchPage;
import io.reflection.app.client.page.ThankYouPage;
import io.reflection.app.client.page.UpgradePage;
import io.reflection.app.client.page.UsersPage;
import io.reflection.app.client.page.WelcomePage;
import io.reflection.app.client.part.Footer;
import io.reflection.app.client.part.Header;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author billy1380
 * 
 */
public class NavigationController implements ValueChangeHandler<String> {
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
	private ChangeDetailsPage mChangeDetailsPage = null;
	private UpgradePage mUpgradePage = null;
	private LinkedAccountsPage mLinkedAccountsPage = null;
	private SearchPage mSearchPage = null;
	private ItemPage mItemPage = null;
	private HomePage mHomePage = null;
	private RequestInvitePage mRequestInvitePage = null;
	private ThankYouPage mThankYouPage = null;
	private WelcomePage mWelcomePage = null;
	private LinkItunesPage mLinkItunesPage = null;

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

	public void addChangeDetailsPage() {
		if (mChangeDetailsPage == null) {
			mChangeDetailsPage = new ChangeDetailsPage();
		}

		if (!mChangeDetailsPage.isAttached()) {
			mPanel.clear();
			mPanel.add(mChangeDetailsPage);
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

	public void addUpgradePage() {
		if (mUpgradePage == null) {
			mUpgradePage = new UpgradePage();
		}

		if (!mUpgradePage.isAttached()) {
			mPanel.clear();
			mPanel.add(mUpgradePage);
		}
	}

	public void addLinkAccountsPage() {
		if (mLinkedAccountsPage == null) {
			mLinkedAccountsPage = new LinkedAccountsPage();
		}

		if (!mLinkedAccountsPage.isAttached()) {
			mPanel.clear();
			mPanel.add(mLinkedAccountsPage);
		}
	}

	public void addSearchPage() {
		if (mSearchPage == null) {
			mSearchPage = new SearchPage();
		}

		if (!mSearchPage.isAttached()) {
			mPanel.clear();
			mPanel.add(mSearchPage);
		}
	}

	public void addItemPage() {
		if (mItemPage == null) {
			mItemPage = new ItemPage();
		}

		if (!mItemPage.isAttached()) {
			mPanel.clear();
			mPanel.add(mItemPage);
		}
	}

	public void addHomePage() {
		if (mHomePage == null) {
			mHomePage = new HomePage();
		}

		if (!mHomePage.isAttached()) {
			mPanel.clear();
			mPanel.add(mHomePage);
		}
	}
	
	public void addRequestInvitePage() {
		if (mRequestInvitePage == null) {
			mRequestInvitePage = new RequestInvitePage();
		}

		if (!mRequestInvitePage.isAttached()) {
			mPanel.clear();
			mPanel.add(mRequestInvitePage);
		}
	}
	
	public void addThankYouPage() {
		if (mThankYouPage == null) {
			mThankYouPage = new ThankYouPage();
		}

		if (!mThankYouPage.isAttached()) {
			mPanel.clear();
			mPanel.add(mThankYouPage);
		}
	}

	public void addWelcomePage() {
		if (mWelcomePage == null) {
			mWelcomePage = new WelcomePage();
		}

		if (!mWelcomePage.isAttached()) {
			mPanel.clear();
			mPanel.add(mWelcomePage);
		}
	}	

	public void addLinkItunesPage() {
		if (mLinkItunesPage == null) {
			mLinkItunesPage = new LinkItunesPage();
		}

		if (!mLinkItunesPage.isAttached()) {
			mPanel.clear();
			mPanel.add(mLinkItunesPage);
		}
	}	
	
	/**
	 * @param value
	 */
	public void addPage(String value) {

		if (value == null || value.length() == 0) {
			value = "home";
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
			} else if ("changedetails".equals(mStack.getAction())) {
				addChangeDetailsPage();
			} else if ("linkedaccounts".equals(mStack.getAction())) {
				addLinkAccountsPage();
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
		} else if ("upgrade".equals(mStack.getPage())) {
			addUpgradePage();
		} else if ("search".equals(mStack.getPage())) {
			addSearchPage();
		} else if ("item".equals(mStack.getPage())) {
			addItemPage();
		} else if ("requestinvite".equals(mStack.getPage())) {
			addRequestInvitePage();
		} else if ("thankyou".equals(mStack.getPage())) {
			addThankYouPage();
		} else if ("welcome".equals(mStack.getPage())) {
			addWelcomePage();		
		} else if ("linkitunes".equals(mStack.getPage())) {
			addLinkItunesPage();			
		} else {
			addHomePage();
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.event.logical.shared.ValueChangeHandler#onValueChange(com.google.gwt.event.logical.shared.ValueChangeEvent)
	 */
	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		addPage(event.getValue());
	}
}
