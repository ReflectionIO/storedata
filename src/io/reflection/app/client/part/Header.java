//
//  Header.java
//  storedata
//
//  Created by William Shakour (billy1380) on 3 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.part;

import io.reflection.app.api.shared.datatypes.Session;
import io.reflection.app.client.controller.EventController;
import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.controller.SessionController;
import io.reflection.app.client.controller.UserController;
import io.reflection.app.client.controller.NavigationController.Stack;
import io.reflection.app.client.handler.NavigationEventHandler;
import io.reflection.app.client.handler.user.SessionEventHandler;
import io.reflection.app.client.handler.user.UserPowersEventHandler;
import io.reflection.app.client.handler.user.UsersEventHandler;
import io.reflection.app.datatypes.shared.Permission;
import io.reflection.app.datatypes.shared.Role;
import io.reflection.app.datatypes.shared.User;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.UListElement;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.Widget;
import com.willshex.gson.json.service.shared.Error;

/**
 * @author billy1380
 * 
 */
public class Header extends Composite implements UsersEventHandler, NavigationEventHandler, SessionEventHandler, UserPowersEventHandler {

	private static HeaderUiBinder uiBinder = GWT.create(HeaderUiBinder.class);

	interface HeaderUiBinder extends UiBinder<Widget, Header> {}

	private static final String ACTIVE_STYLE_NAME = "active";

	@UiField InlineHyperlink mRanksLink;
	@UiField LIElement mRanksItem;

	@UiField LIElement mUpgradeAccountItem;
	@UiField InlineHyperlink mUpgradeAccountLink;

	@UiField UListElement mNavList;
	@UiField UListElement mAdminList;
	@UiField UListElement mAccountList;

	@UiField InlineHyperlink mFeedBrowserLink;
	@UiField LIElement mFeedBrowserItem;

	@UiField InlineHyperlink mUsersLink;
	@UiField LIElement mUsersItem;

	@UiField InlineHyperlink mLoginLink;
	@UiField LIElement mLoginItem;

	@UiField InlineHyperlink mRegisterLink;
	@UiField LIElement mRegisterItem;

	@UiField InlineHyperlink mRolesLink;
	@UiField LIElement mRolesItem;

	@UiField InlineHyperlink mPermissionsLink;
	@UiField LIElement mPermissionsItem;

	@UiField SpanElement mTotalUsers;

	@UiField LIElement mAccountDropdown;
	@UiField Anchor mAccountButton;

	@UiField LIElement mAdminDropdown;
	@UiField Anchor mAdminButton;

	@UiField InlineHyperlink mLogoutLink;
	@UiField LIElement mLogoutItem;

	@UiField InlineHyperlink mChangeDetailsLink;
	@UiField LIElement mChangeDetailsItem;

	@UiField InlineHyperlink mChangePasswordLink;
	@UiField LIElement mChangePasswordItem;

	@UiField LIElement mLinkedAccountsItem;
	@UiField InlineHyperlink mLinkedAccountsLink;

	private List<LIElement> mItems;

	public Header() {
		initWidget(uiBinder.createAndBindUi(this));

		mAdminButton.setHTML(mAdminButton.getText() + " <b class=\"caret\"></b>");
		mLogoutLink.setHTML(mLogoutLink.getText() + " <b class=\"glyphicon glyphicon-log-out\"></b>");
		mLoginLink.setHTML(mLoginLink.getText() + " <b class=\"glyphicon glyphicon-log-in\"></b>");

		EventController.get().addHandlerToSource(UsersEventHandler.TYPE, UserController.get(), this);
		EventController.get().addHandlerToSource(NavigationEventHandler.TYPE, NavigationController.get(), this);
		EventController.get().addHandlerToSource(SessionEventHandler.TYPE, SessionController.get(), this);
		EventController.get().addHandlerToSource(UserPowersEventHandler.TYPE, SessionController.get(), this);

		createItemList();

		removeUpgrade();
		removeAccount();
		removeAdmin();

	}

	private void createItemList() {
		if (mItems == null) {
			mItems = new ArrayList<LIElement>();

			mItems.add(mFeedBrowserItem);
			mItems.add(mRanksItem);
			mItems.add(mUsersItem);
			mItems.add(mLoginItem);
			mItems.add(mRegisterItem);
			mItems.add(mRolesItem);
			mItems.add(mPermissionsItem);
			mItems.add(mChangeDetailsItem);
			mItems.add(mChangePasswordItem);
			mItems.add(mUpgradeAccountItem);
			mItems.add(mLinkedAccountsItem);
		}
	}

	private void activate(LIElement item) {
		if (item != null) {
			item.addClassName(ACTIVE_STYLE_NAME);
		}
	}

	private void deactivate(LIElement item) {
		if (item != null) {
			item.removeClassName(ACTIVE_STYLE_NAME);
		}
	}

	private void highlight(LIElement item) {
		for (LIElement c : mItems) {
			if (c == item) {
				activate(c);
			} else {
				deactivate(c);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.reflection.app.client.event.NavigationChanged.Handler#navigationChanged(io.reflection.app.admin.client.controller.NavigationController.Stack)
	 */
	@Override
	public void navigationChanged(Stack stack) {
		if ("ranks".equals(stack.getPage())) {
			highlight(mRanksItem);
		} else if ("feedbrowser".equals(stack.getPage())) {
			highlight(mFeedBrowserItem);
		} else if ("users".equals(stack.getPage())) {
			if (stack.getAction() == null) {
				highlight(mUsersItem);
			} else if ("changedetails".equals(stack.getAction())) {
				highlight(mChangeDetailsItem);
			} else if ("changepassword".equals(stack.getAction())) {
				highlight(mChangePasswordItem);
			} else if ("linkedaccounts".equals(stack.getAction())) {
				highlight(mLinkedAccountsItem);
			}
		} else if ("login".equals(stack.getPage())) {
			highlight(mLoginItem);
		} else if ("register".equals(stack.getPage())) {
			highlight(mRegisterItem);
		} else if ("roles".equals(stack.getPage())) {
			highlight(mRolesItem);
		} else if ("permissions".equals(stack.getPage())) {
			highlight(mPermissionsItem);
		} else if ("upgrade".equals(stack.getPage())) {
			highlight(mUpgradeAccountItem);
		} else {
			highlight(null);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.UsersEventHandler#receivedUsers(java.util.List)
	 */
	@Override
	public void receivedUsers(List<User> users) {
		// do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.UsersEventHandler#receivedUsersCount(java.lang.Long)
	 */
	@Override
	public void receivedUsersCount(Long count) {
		if (mTotalUsers != null) {
			mTotalUsers.setInnerText(count.toString());
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.SessionEventHandler#userLoggedIn(io.reflection.app.shared.datatypes.User)
	 */
	@Override
	public void userLoggedIn(User user, Session session) {

		removeLogin();

		removeRegister();
		
		addAdmin();

		addAccount(user);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.SessionEventHandler#userLoggedOut()
	 */
	@Override
	public void userLoggedOut() {
		removeAdmin();

		removeAccount();

		addLogin();

		addRegister();

		removeUpgrade();
	}

	/**
	 * 
	 */
	private void addLogin() {
		mAccountList.appendChild(mLoginItem);
	}

	/**
	 * 
	 */
	private void removeLogin() {
		mLoginItem.removeFromParent();
	}

	private void addRegister() {
		mAccountList.appendChild(mRegisterItem);
	}

	private void removeRegister() {
		mRegisterItem.removeFromParent();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.SessionEventHandler#userLoginFailed(com.willshex.gson.json.service.shared.Error)
	 */
	@Override
	public void userLoginFailed(Error error) {
		userLoggedOut();
	}

	private void addAdmin() {
		if (SessionController.get().isLoggedInUserAdmin()) {
			if (UserController.get().getUsersCount() >= 0) {
				mTotalUsers.setInnerText(Long.toString(UserController.get().getUsersCount()));
			} else {
				UserController.get().fetchUsersCount();
			}

			mNavList.getParentElement().appendChild(mAdminList);
		}
	}

	private void addAccount(User user) {
		// should really be using DOM so that these objects are not repeatedly created
		mAccountButton.getElement().setInnerHTML(
				"<span>" + SafeHtmlUtils.htmlEscape(user.forename + " " + user.surname) + " <b class=\"caret\"></b></span><img class=\"img-rounded\" src=\""
						+ UriUtils.sanitizeUri("http://www.gravatar.com/avatar/" + user.avatar + "?s=30&d=identicon") + "\" />");

		mChangePasswordLink.setTargetHistoryToken("users/changepassword/" + user.id.toString());
		mChangeDetailsLink.setTargetHistoryToken("users/changedetails/" + user.id.toString());
		mLinkedAccountsLink.setTargetHistoryToken("users/linkedaccounts/" + user.id.toString());

		mAccountList.appendChild(mAccountDropdown);
	}

	private void removeAdmin() {
		mAdminList.removeFromParent();
	}

	private void removeAccount() {
		mAccountDropdown.removeFromParent();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.user.UserPowersEventHandler#gotUserPowers(io.reflection.app.shared.datatypes.User, java.util.List,
	 * java.util.List)
	 */
	@Override
	public void gotUserPowers(User user, List<Role> roles, List<Permission> permissions) {
		addAdmin();

		addUpgrade();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.user.UserPowersEventHandler#getGetUserPowersFailed(com.willshex.gson.json.service.shared.Error)
	 */
	@Override
	public void getGetUserPowersFailed(Error error) {
		removeAdmin();
	}

	public void removeUpgrade() {
		mUpgradeAccountItem.removeFromParent();
	}

	public void addUpgrade() {
		if (SessionController.get().getLoggedInUser().roles == null || SessionController.get().getLoggedInUser().roles.size() == 0) {
			mNavList.appendChild(mUpgradeAccountItem);
		} else {
			removeUpgrade();
		}
	}

}
