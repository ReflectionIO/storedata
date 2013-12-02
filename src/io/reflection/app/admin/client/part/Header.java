//
//  Header.java
//  storedata
//
//  Created by William Shakour (billy1380) on 3 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.admin.client.part;

import io.reflection.app.admin.client.controller.EventController;
import io.reflection.app.admin.client.controller.NavigationController;
import io.reflection.app.admin.client.controller.SessionController;
import io.reflection.app.admin.client.controller.NavigationController.Stack;
import io.reflection.app.admin.client.controller.UserController;
import io.reflection.app.admin.client.handler.NavigationEventHandler;
import io.reflection.app.admin.client.handler.SessionEventHandler;
import io.reflection.app.admin.client.handler.UsersEventHandler;
import io.reflection.app.api.shared.datatypes.Session;
import io.reflection.app.shared.datatypes.User;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.UListElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.Widget;
import com.willshex.gson.json.service.shared.Error;

/**
 * @author billy1380
 * 
 */
public class Header extends Composite implements UsersEventHandler, NavigationEventHandler, SessionEventHandler {

	private static HeaderUiBinder uiBinder = GWT.create(HeaderUiBinder.class);

	interface HeaderUiBinder extends UiBinder<Widget, Header> {}

	private static final String ACTIVE_STYLE_NAME = "active";

	@UiField InlineHyperlink mRanksLink;
	@UiField LIElement mRanksItem;

	@UiField UListElement mNavList;

	InlineHyperlink mFeedBrowserLink;
	LIElement mFeedBrowserItem;

	InlineHyperlink mUsersLink;
	LIElement mUsersItem;

	InlineHyperlink mLogoutLink;
	LIElement mLogoutItem;

	InlineHyperlink mLoginLink;
	LIElement mLoginItem;

	SpanElement mTotalUsers;

	public Header() {
		initWidget(uiBinder.createAndBindUi(this));

		mRanksItem.addClassName(ACTIVE_STYLE_NAME);

		EventController.get().addHandlerToSource(UsersEventHandler.TYPE, UserController.get(), this);

		// if (UserController.get().getUsersCount() >= 0) {
		// mTotalUsers.setInnerText(Long.toString(UserController.get().getUsersCount()));
		// } else {
		// UserController.get().fetchUsersCount();
		// }

		EventController.get().addHandlerToSource(NavigationEventHandler.TYPE, NavigationController.get(), this);
		EventController.get().addHandlerToSource(SessionEventHandler.TYPE, SessionController.get(), this);

		addLogin();
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

	private void activateFeedBrowser() {
		activate(mFeedBrowserItem);
		deactivate(mRanksItem);
		deactivate(mUsersItem);
		deactivate(mLoginItem);
	}

	private void activateUsers() {
		deactivate(mFeedBrowserItem);
		deactivate(mRanksItem);
		activate(mUsersItem);
		deactivate(mLoginItem);
	}

	private void activateRanks() {
		activate(mRanksItem);
		deactivate(mFeedBrowserItem);
		deactivate(mUsersItem);
		deactivate(mLoginItem);
	}

	private void activateLogin() {
		deactivate(mRanksItem);
		deactivate(mFeedBrowserItem);
		deactivate(mUsersItem);
		activate(mLoginItem);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.reflection.app.admin.client.event.NavigationChanged.Handler#navigationChanged(io.reflection.app.admin.client.controller.NavigationController.Stack)
	 */
	@Override
	public void navigationChanged(Stack stack) {
		if ("ranks".equals(stack.getPage())) {
			activateRanks();
		} else if ("feedbrowser".equals(stack.getPage())) {
			activateFeedBrowser();
		} else if ("users".equals(stack.getPage())) {
			activateUsers();
		} else if ("login".equals(stack.getPage())) {
			activateLogin();
		} else {
			deactivateAll();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.admin.client.handler.UsersEventHandler#receivedUsers(java.util.List)
	 */
	@Override
	public void receivedUsers(List<User> users) {
		// do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.admin.client.handler.UsersEventHandler#receivedUsersCount(java.lang.Long)
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
	 * @see io.reflection.app.admin.client.handler.SessionEventHandler#userLoggedIn(io.reflection.app.shared.datatypes.User)
	 */
	@Override
	public void userLoggedIn(User user, Session session) {
		addFeedBrowser();
		addUsers();
		removeLogin();
		addLogout();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.admin.client.handler.SessionEventHandler#userLoggedOut()
	 */
	@Override
	public void userLoggedOut() {
		removeFeedBrowser();
		removeUsers();
		removeLogout();
		addLogin();
	}

	/**
	 * 
	 */
	private void addLogout() {
		if (mLogoutItem == null) {
			mLogoutItem = Document.get().createLIElement();

			mLogoutLink = new InlineHyperlink("Sign Out", false, "logout");

			mLogoutItem.appendChild(mLogoutLink.getElement());
		}

		mNavList.appendChild(mLogoutItem);
	}

	/**
	 * 
	 */
	private void addUsers() {
		if (mUsersItem == null) {
			mUsersItem = Document.get().createLIElement();

			mUsersLink = new InlineHyperlink("", "users");

			mTotalUsers = Document.get().createSpanElement();

			if (UserController.get().getUsersCount() >= 0) {
				mTotalUsers.setInnerText(Long.toString(UserController.get().getUsersCount()));
			} else {
				mTotalUsers.setInnerText("1+");

				UserController.get().fetchUsersCount();
			}

			mTotalUsers.addClassName("badge");

			mUsersLink.setText("Users ");
			mUsersLink.getElement().appendChild(mTotalUsers);

			mUsersItem.appendChild(mUsersLink.getElement());
		}

		mNavList.appendChild(mUsersItem);

	}

	/**
	 * 
	 */
	private void addFeedBrowser() {
		if (mFeedBrowserItem == null) {
			mFeedBrowserItem = Document.get().createLIElement();

			mFeedBrowserLink = new InlineHyperlink("Feed Browser", false, "feedbrowser");

			mFeedBrowserItem.appendChild(mFeedBrowserLink.getElement());
		}

		mNavList.appendChild(mFeedBrowserItem);
	}

	/**
	 * 
	 */
	private void addLogin() {
		if (mLoginItem == null) {
			mLoginItem = Document.get().createLIElement();

			mLoginLink = new InlineHyperlink("Sign In", false, "login");

			mLoginItem.appendChild(mLoginLink.getElement());
		}

		mNavList.appendChild(mLoginItem);
	}

	/**
	 * 
	 */
	private void removeLogin() {
		if (mLoginItem != null) {
			mNavList.removeChild(mLoginItem);
		}
	}

	/**
	 * 
	 */
	private void removeLogout() {
		if (mLogoutItem != null) {
			mNavList.removeChild(mLogoutItem);
		}
	}

	/**
	 * 
	 */
	private void removeUsers() {
		if (mUsersItem != null) {
			mNavList.removeChild(mUsersItem);
		}
	}

	/**
	 * 
	 */
	private void removeFeedBrowser() {
		if (mFeedBrowserItem != null) {
			mNavList.removeChild(mFeedBrowserItem);
		}
	}

	private void deactivateAll() {
		deactivate(mRanksItem);
		deactivate(mFeedBrowserItem);
		deactivate(mRanksItem);
		deactivate(mLoginItem);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.admin.client.handler.SessionEventHandler#userLoginFailed(com.willshex.gson.json.service.shared.Error)
	 */
	@Override
	public void userLoginFailed(Error error) {}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.admin.client.handler.UsersEventHandler#userRegistered(java.lang.String)
	 */
	@Override
	public void userRegistered(String email) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.admin.client.handler.UsersEventHandler#userRegistrationFailed(com.willshex.gson.json.service.shared.Error)
	 */
	@Override
	public void userRegistrationFailed(Error error) {
		// TODO Auto-generated method stub

	}

}
