//
//  Header.java
//  storedata
//
//  Created by William Shakour (billy1380) on 3 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.part;

import static io.reflection.app.client.controller.FilterController.OVERALL_LIST_TYPE;
import io.reflection.app.api.core.shared.call.ChangeUserDetailsRequest;
import io.reflection.app.api.core.shared.call.ChangeUserDetailsResponse;
import io.reflection.app.api.core.shared.call.event.ChangeUserDetailsEventHandler;
import io.reflection.app.api.shared.datatypes.Session;
import io.reflection.app.client.controller.EventController;
import io.reflection.app.client.controller.FilterController;
import io.reflection.app.client.controller.FilterController.Filter;
import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.controller.NavigationController.Stack;
import io.reflection.app.client.controller.SessionController;
import io.reflection.app.client.controller.UserController;
import io.reflection.app.client.handler.FilterEventHandler;
import io.reflection.app.client.handler.NavigationEventHandler;
import io.reflection.app.client.handler.user.SessionEventHandler;
import io.reflection.app.client.handler.user.UserPowersEventHandler;
import io.reflection.app.client.handler.user.UsersEventHandler;
import io.reflection.app.client.page.PageType;
import io.reflection.app.datatypes.shared.Permission;
import io.reflection.app.datatypes.shared.Role;
import io.reflection.app.datatypes.shared.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.UListElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.willshex.gson.json.service.shared.Error;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author billy1380
 * 
 */
public class Header extends Composite implements UsersEventHandler, NavigationEventHandler, SessionEventHandler, UserPowersEventHandler,
		ChangeUserDetailsEventHandler, FilterEventHandler {

	private static HeaderUiBinder uiBinder = GWT.create(HeaderUiBinder.class);

	interface HeaderUiBinder extends UiBinder<Widget, Header> {}

	private static final String ACTIVE_STYLE_NAME = "active";

	@UiField InlineHyperlink mRanksLink;
	@UiField LIElement mRanksItem;

	@UiField InlineHyperlink blogLink;
	@UiField LIElement blogItem;

	@UiField InlineHyperlink myAppsLink;
	@UiField LIElement myAppsItem;

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

	@UiField InlineHyperlink emailTemplatesLink;
	@UiField LIElement emailTemplatesItem;

	@UiField InlineHyperlink itemsLink;
	@UiField LIElement itemsItem;

	@UiField SpanElement mTotalUsers;

	@UiField LIElement mAccountDropdown;
	@UiField Anchor mAccountButton;

	@UiField LIElement mAdminDropdown;
	@UiField Anchor mAdminButton;

	@UiField InlineHyperlink mLogoutLink;
	@UiField LIElement mLogoutItem;

	@UiField InlineHyperlink myAppsAccountLink;
	@UiField LIElement myAppsAccountItem;

	@UiField InlineHyperlink mAccountSettingsLink;
	@UiField LIElement mAccountSettingsItem;

	@UiField LIElement mLinkedAccountsItem;
	@UiField InlineHyperlink mLinkedAccountsLink;

	@UiField TextBox mQuery;
	@UiField InlineHyperlink mSearch;

	@UiField Anchor featureRequestButton;

	@UiField UListElement mFeatureRequest;

	private List<LIElement> mItems;
	private List<LIElement> highlightedItems = new ArrayList<LIElement>();

	private HandlerRegistration filterChangedRegistration;

	public Header() {
		initWidget(uiBinder.createAndBindUi(this));

		mAdminButton.setHTML(mAdminButton.getText() + " <b class=\"caret\"></b>");
		mLoginLink.setHTML(mLoginLink.getText() + " <b class=\"glyphicon glyphicon-log-in\"></b>");

		mQuery.getElement().setAttribute("placeholder", "Search for any app");
		mSearch.setHTML("<b class=\"glyphicon glyphicon-search\"></b>");

		EventController.get().addHandlerToSource(UsersEventHandler.TYPE, UserController.get(), this);
		EventController.get().addHandlerToSource(NavigationEventHandler.TYPE, NavigationController.get(), this);
		EventController.get().addHandlerToSource(SessionEventHandler.TYPE, SessionController.get(), this);
		EventController.get().addHandlerToSource(UserPowersEventHandler.TYPE, SessionController.get(), this);
		EventController.get().addHandlerToSource(ChangeUserDetailsEventHandler.TYPE, SessionController.get(), this);

		createItemList();

		removeUpgrade();
		removeAccount();
		removeAdmin();
		removeMyApps();

		mRanksLink.setTargetHistoryToken(PageType.RanksPageType.asTargetHistoryToken("view", OVERALL_LIST_TYPE, FilterController.get().asRankFilterString()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.Composite#onAttach()
	 */
	@Override
	protected void onAttach() {
		super.onAttach();

		filterChangedRegistration = EventController.get().addHandlerToSource(FilterEventHandler.TYPE, FilterController.get(), this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.Composite#onDetach()
	 */
	@Override
	protected void onDetach() {
		if (filterChangedRegistration != null) {
			filterChangedRegistration.removeHandler();
		}

		super.onDetach();
	}

	/**
	 * Fire the search button when pressing the 'enter' key on the search field, adding a associated token to the history
	 * 
	 * @param event
	 */
	@UiHandler("mQuery")
	void onEnterKeyPressSearchField(KeyPressEvent event) {
		if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
			History.newItem(mSearch.getTargetHistoryToken());
		}
	}

	@UiHandler("mQuery")
	void onQueryChanged(ChangeEvent event) {
		mSearch.setTargetHistoryToken(PageType.SearchPageType.asTargetHistoryToken("query", mQuery.getText()));
	}

	private void createItemList() {
		if (mItems == null) {
			mItems = new ArrayList<LIElement>();

			mItems.add(mFeedBrowserItem);
			mItems.add(mRanksItem);
			mItems.add(myAppsItem);
			mItems.add(mUsersItem);
			mItems.add(mLoginItem);
			mItems.add(mRegisterItem);
			mItems.add(mRolesItem);
			mItems.add(mPermissionsItem);
			mItems.add(emailTemplatesItem);
			mItems.add(itemsItem);
			mItems.add(mAccountSettingsItem);
			mItems.add(myAppsAccountItem);
			mItems.add(mUpgradeAccountItem);
			mItems.add(mLinkedAccountsItem);
			mItems.add(blogItem);
		}
	}

	private void activate(LIElement item) {
		if (item != null) {
			highlightedItems.add(item);
			item.addClassName(ACTIVE_STYLE_NAME);
		}
	}

	private void deactivate(LIElement item) {
		if (item != null) {
			highlightedItems.remove(item);
			item.removeClassName(ACTIVE_STYLE_NAME);
		}
	}

	private void highlight(LIElement... item) {
		List<LIElement> list = new ArrayList<LIElement>(highlightedItems);
		for (LIElement c : list) {
			deactivate(c);
		}

		if (item != null) {
			for (LIElement c : item) {
				activate(c);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.NavigationEventHandler#navigationChanged(io.reflection.app.client.controller.NavigationController.Stack,
	 * io.reflection.app.client.controller.NavigationController.Stack)
	 */
	@Override
	public void navigationChanged(Stack previous, Stack current) {
		if (PageType.RanksPageType.equals(current.getPage())) {
			highlight(mRanksItem);
		} else if (PageType.FeedBrowserPageType.equals(current.getPage())) {
			highlight(mFeedBrowserItem);
		} else if (PageType.UsersPageType.equals(current.getPage())) {
			if (current.getAction() == null) {
				highlight(mUsersItem);
			} else if (PageType.MyAppsPageType.equals(current.getAction())) {
				highlight(myAppsAccountItem, myAppsItem);
			} else if (PageType.LinkedAccountsPageType.equals(current.getAction())) {
				highlight(mLinkedAccountsItem);
			} else if (PageType.ChangeDetailsPageType.equals(current.getAction())) {
				highlight(mAccountSettingsItem);
			}
		} else if (PageType.LoginPageType.equals(current.getPage())) {
			highlight(mLoginItem);
		} else if (PageType.RegisterPageType.equals(current.getPage())) {
			highlight(mRegisterItem);
		} else if (PageType.RolesPageType.equals(current.getPage())) {
			highlight(mRolesItem);
		} else if (PageType.PermissionsPageType.equals(current.getPage())) {
			highlight(mPermissionsItem);
		} else if (PageType.UpgradePageType.equals(current.getPage())) {
			highlight(mUpgradeAccountItem);
		} else if (PageType.EmailTemplatesPageType.equals(current.getPage())) {
			highlight(emailTemplatesItem);
		} else if (PageType.ItemsPageType.equals(current.getPage())) {
			highlight(itemsItem);
		} else if (PageType.BlogPostsPageType.equals(current.getPage()) || PageType.BlogPostPageType.equals(current.getPage())) {
			highlight(blogItem);
		} else {
			highlight();
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

		// removeRegister();

		addAdmin();

		addAccount(user);

		addLeaderboard();

		addFeatureRequest();
	}

	/**
	 * 
	 */
	private void addFeatureRequest() {
		mFeatureRequest.setAttribute("style", "display: inline");
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

		// addRegister();

		addLogin();

		removeUpgrade();

		removeMyApps();

		removeLeaderboard();

		removeFeatureRequest();
	}

	/**
	 * 
	 */
	private void removeFeatureRequest() {
		mFeatureRequest.setAttribute("style", "display: none");

	}

	private void addLogin() {
		mAccountList.appendChild(mLoginItem);
	}

	private void removeLogin() {
		mLoginItem.removeFromParent();
	}

	// private void addRegister() {
	// mAccountList.appendChild(mRegisterItem);
	// }
	//
	// private void removeRegister() {
	// mRegisterItem.removeFromParent();
	// }

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
		} else {
			removeAdmin();
		}
	}

	private void addLeaderboard() {
		mRanksItem.setAttribute("style", "display: inline");
	}

	private void removeLeaderboard() {
		mRanksItem.setAttribute("style", "display: none");
	}

	private void addAccount(User user) {

		mAccountSettingsLink.setTargetHistoryToken(PageType.UsersPageType.asTargetHistoryToken("changedetails", user.id.toString()));
		myAppsLink.setTargetHistoryToken(PageType.UsersPageType.asTargetHistoryToken("myapps", user.id.toString(), FilterController.get()
				.asMyAppsFilterString()));
		myAppsAccountLink.setTargetHistoryToken(PageType.UsersPageType.asTargetHistoryToken("myapps", user.id.toString(), FilterController.get()
				.asMyAppsFilterString()));
		mLinkedAccountsLink.setTargetHistoryToken(PageType.UsersPageType.asTargetHistoryToken("linkedaccounts", user.id.toString()));

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
	 * @see io.reflection.app.client.handler.user.UserPowersEventHandler#gotUserPowers(io.reflection.app.shared.datatypes.User, java.util.List, java.util.List)
	 */
	@Override
	public void gotUserPowers(User user, List<Role> roles, List<Permission> permissions) {
		addAdmin();

		addUpgrade();

		addMyApps();
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

	public void removeMyApps() {
		myAppsItem.removeFromParent();
	}

	public void addMyApps() {
		mNavList.appendChild(myAppsItem);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.core.shared.call.event.ChangeUserDetailsEventHandler#changeUserDetailsSuccess(io.reflection.app.api.core.shared.call.
	 * ChangeUserDetailsRequest, io.reflection.app.api.core.shared.call.ChangeUserDetailsResponse)
	 */
	@Override
	public void changeUserDetailsSuccess(ChangeUserDetailsRequest input, ChangeUserDetailsResponse output) {
		if (output.status == StatusType.StatusTypeSuccess) {
			addAccount(SessionController.get().getLoggedInUser());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.core.shared.call.event.ChangeUserDetailsEventHandler#changeUserDetailsFailure(io.reflection.app.api.core.shared.call.
	 * ChangeUserDetailsRequest, java.lang.Throwable)
	 */
	@Override
	public void changeUserDetailsFailure(ChangeUserDetailsRequest input, Throwable caught) {}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.FilterEventHandler#filterParamChanged(java.lang.String, java.lang.Object, java.lang.Object)
	 */
	@Override
	public <T> void filterParamChanged(String name, T currentValue, T previousValue) {
		mRanksLink.setTargetHistoryToken(PageType.RanksPageType.asTargetHistoryToken("view", OVERALL_LIST_TYPE, FilterController.get().asRankFilterString()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.FilterEventHandler#filterParamsChanged(io.reflection.app.client.controller.FilterController.Filter, java.util.Map)
	 */
	@Override
	public void filterParamsChanged(Filter currentFilter, Map<String, ?> previousValues) {
		mRanksLink.setTargetHistoryToken(PageType.RanksPageType.asTargetHistoryToken("view", OVERALL_LIST_TYPE, FilterController.get().asRankFilterString()));
	}

}
