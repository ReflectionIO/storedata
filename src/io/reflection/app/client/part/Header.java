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
import io.reflection.app.api.core.shared.call.DeleteLinkedAccountRequest;
import io.reflection.app.api.core.shared.call.DeleteLinkedAccountResponse;
import io.reflection.app.api.core.shared.call.LinkAccountRequest;
import io.reflection.app.api.core.shared.call.LinkAccountResponse;
import io.reflection.app.api.core.shared.call.event.ChangeUserDetailsEventHandler;
import io.reflection.app.api.core.shared.call.event.DeleteLinkedAccountEventHandler;
import io.reflection.app.api.core.shared.call.event.LinkAccountEventHandler;
import io.reflection.app.api.shared.datatypes.Session;
import io.reflection.app.client.controller.EventController;
import io.reflection.app.client.controller.FilterController;
import io.reflection.app.client.controller.LinkedAccountController;
import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.controller.NavigationController.Stack;
import io.reflection.app.client.controller.SessionController;
import io.reflection.app.client.controller.UserController;
import io.reflection.app.client.handler.NavigationEventHandler;
import io.reflection.app.client.handler.user.SessionEventHandler;
import io.reflection.app.client.handler.user.UserPowersEventHandler;
import io.reflection.app.client.handler.user.UsersEventHandler;
import io.reflection.app.client.page.PageType;
import io.reflection.app.datatypes.shared.Permission;
import io.reflection.app.datatypes.shared.Role;
import io.reflection.app.datatypes.shared.User;
import io.reflection.app.shared.util.DataTypeHelper;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.UListElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.Widget;
import com.willshex.gson.json.service.shared.Error;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author billy1380
 * 
 */
public class Header extends Composite implements UsersEventHandler, NavigationEventHandler, SessionEventHandler, UserPowersEventHandler,
		ChangeUserDetailsEventHandler, LinkAccountEventHandler, DeleteLinkedAccountEventHandler {

	private static HeaderUiBinder uiBinder = GWT.create(HeaderUiBinder.class);

	interface HeaderUiBinder extends UiBinder<Widget, Header> {}

	private static final String ACTIVE_STYLE_NAME = "active";
	// private static final String SHOW_INLINE_STYLE_NAME = "inline";
	// private static final String HIDE_STYLE_NAME = "hide";

	@UiField UListElement navList;

	@UiField DivElement navbarBrandCover;

	@UiField InlineHyperlink ranksLink;
	@UiField LIElement ranksItem;

	@UiField UListElement myAccountList;
	@UiField InlineHyperlink myAppsLink;
	@UiField LIElement myAppsItem;
	@UiField InlineHyperlink linkedAccountsLink;
	@UiField LIElement linkedAccountsItem;
	@UiField InlineHyperlink accountSettingsLink;
	@UiField LIElement accountSettingsItem;

	@UiField LIElement blogItem;

	@UiField LIElement forumItem;

	@UiField UListElement adminList;

	@UiField LIElement usersItem;

	// @UiField LIElement upgradeAccountItem;
	// @UiField InlineHyperlink upgradeAccountLink;

	@UiField LIElement feedBrowserItem;
	@UiField InlineHyperlink feedBrowserLink;

	@UiField LIElement simpleModelRunItem;
	@UiField InlineHyperlink simpleModelRunLink;

	@UiField UListElement login;

	@UiField InlineHyperlink loginLink;
	@UiField LIElement loginItem;

	// @UiField InlineHyperlink mRegisterLink;
	// @UiField LIElement mRegisterItem;

	@UiField LIElement rolesItem;

	@UiField LIElement permissionsItem;

	@UiField LIElement dataAccountsItem;

	@UiField LIElement dataAccountFetchesItem;
	@UiField InlineHyperlink dataAccountFetchesLink;

	@UiField LIElement emailTemplatesItem;

	@UiField LIElement itemsItem;

	@UiField LIElement categoriesItem;

	@UiField LIElement blogAdminItem;

	@UiField SpanElement totalUsers;

	@UiField LIElement myAccountDropdown;
	@UiField Anchor accountButton;

	@UiField LIElement adminDropdown;
	@UiField Anchor adminButton;

	// @UiField TextBox mQuery;
	// @UiField InlineHyperlink mSearch;
	@UiField UListElement search;

	@UiField UListElement userSignOut;
	@UiField InlineHTML userName;
	// @UiField Anchor featureRequestButton;
	// @UiField UListElement mFeatureRequest;

	@UiField DivElement collapsableNavBar;
	@UiField Button collapseButton;

	private List<LIElement> items;
	private List<LIElement> highlightedItems = new ArrayList<LIElement>();

	private HandlerRegistration filterChangedRegistration;

	public Header() {
		initWidget(uiBinder.createAndBindUi(this));

		loginLink.setTargetHistoryToken(PageType.LoginPageType.asTargetHistoryToken("requestinvite"));

		// mQuery.getElement().setAttribute("placeholder", "Search for any app");
		// mSearch.setHTML("<b class=\"glyphicon glyphicon-search\"></b>");

		createItemList();

		removeLeaderboard();
		removeMyAccount();
		removeBlog();
		removeForum();
		removeAdmin();
		// removeFeatureRequest();
		// addRegister();
		// removeUpgrade();
		removeSearch();
		removeUserSignOut();
		addLogin();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.Composite#onAttach()
	 */
	@Override
	protected void onAttach() {
		super.onAttach();

		EventController.get().addHandlerToSource(UsersEventHandler.TYPE, UserController.get(), this);
		EventController.get().addHandlerToSource(NavigationEventHandler.TYPE, NavigationController.get(), this);
		EventController.get().addHandlerToSource(SessionEventHandler.TYPE, SessionController.get(), this);
		EventController.get().addHandlerToSource(UserPowersEventHandler.TYPE, SessionController.get(), this);
		EventController.get().addHandlerToSource(ChangeUserDetailsEventHandler.TYPE, SessionController.get(), this);
		EventController.get().addHandlerToSource(LinkAccountEventHandler.TYPE, LinkedAccountController.get(), this);
		EventController.get().addHandlerToSource(DeleteLinkedAccountEventHandler.TYPE, LinkedAccountController.get(), this);
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

	// /**
	// * Fire the search button when pressing the 'enter' key on the search field, adding a associated token to the history
	// *
	// * @param event
	// */
	// @UiHandler("mQuery")
	// void onEnterKeyPressSearchField(KeyPressEvent event) {
	// if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
	// History.newItem(mSearch.getTargetHistoryToken());
	// }
	// }
	//
	// @UiHandler("mQuery")
	// void onQueryChanged(ChangeEvent event) {
	// mSearch.setTargetHistoryToken(PageType.SearchPageType.asTargetHistoryToken("query", mQuery.getText()));
	// }

	private void createItemList() {
		if (items == null) {
			items = new ArrayList<LIElement>();

			items.add(ranksItem);
			items.add(myAppsItem);
			items.add(linkedAccountsItem);
			items.add(accountSettingsItem);
			items.add(blogItem);
			items.add(forumItem);
			items.add(usersItem);
			items.add(feedBrowserItem);
			items.add(simpleModelRunItem);
			items.add(rolesItem);
			items.add(permissionsItem);
			items.add(dataAccountsItem);
			items.add(dataAccountFetchesItem);
			items.add(emailTemplatesItem);
			items.add(itemsItem);
			items.add(categoriesItem);
			// items.add(upgradeAccountItem);
			items.add(loginItem);
			// items.add(mRegisterItem);
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

	private void highlight(LIElement... item) {
		for (LIElement c : highlightedItems) {
			deactivate(c);
		}
		highlightedItems.clear();

		if (item != null) {
			for (LIElement c : item) {
				activate(c);
				highlightedItems.add(c);
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
			highlight(ranksItem);
		} else if (PageType.UsersPageType.equals(current.getPage())) {
			if (current.getAction() == null) {
				highlight(adminDropdown, usersItem);
			} else if (PageType.MyAppsPageType.equals(current.getAction())) {
				highlight(myAccountDropdown, myAppsItem);
			} else if (PageType.LinkedAccountsPageType.equals(current.getAction())) {
				highlight(myAccountDropdown, linkedAccountsItem);
			} else if (PageType.ChangeDetailsPageType.equals(current.getAction())) {
				highlight(myAccountDropdown, accountSettingsItem);
			}
		} else if (PageType.LoginPageType.equals(current.getPage())) {
			highlight(loginItem);
			// } else if (PageType.RegisterPageType.equals(current.getPage())) {
			// highlight(mRegisterItem);
		} else if (PageType.FeedBrowserPageType.equals(current.getPage())) {
			highlight(adminDropdown, feedBrowserItem);
		} else if (PageType.SimpleModelRunPageType.equals(current.getPage())) {
			highlight(adminDropdown, simpleModelRunItem);
		} else if (PageType.RolesPageType.equals(current.getPage())) {
			highlight(adminDropdown, rolesItem);
		} else if (PageType.PermissionsPageType.equals(current.getPage())) {
			highlight(adminDropdown, permissionsItem);
		} else if (PageType.DataAccountsPageType.equals(current.getPage())) {
			highlight(adminDropdown, dataAccountsItem);
		} else if (PageType.DataAccountFetchesPageType.equals(current.getPage())) {
			highlight(adminDropdown, dataAccountFetchesItem);
			// } else if (PageType.UpgradePageType.equals(current.getPage())) {
			// highlight(upgradeAccountItem);
		} else if (PageType.EmailTemplatesPageType.equals(current.getPage())) {
			highlight(adminDropdown, emailTemplatesItem);
		} else if (PageType.ItemsPageType.equals(current.getPage())) {
			highlight(adminDropdown, itemsItem);
		} else if (PageType.CategoriesPageType.equals(current.getPage())) {
			highlight(adminDropdown, categoriesItem);
		} else if (PageType.BlogAdminPageType.equals(current.getPage())) {
			highlight(adminDropdown, blogAdminItem);
		} else if (PageType.BlogPostsPageType.equals(current.getPage()) || PageType.BlogPostPageType.equals(current.getPage())) {
			highlight(blogItem);
		} else if (PageType.ForumPageType.equals(current.getPage()) || PageType.ForumThreadPageType.equals(current.getPage())
				|| PageType.ForumTopicPageType.equals(current.getPage())) {
			highlight(forumItem);
		} else {
			highlight();
		}

		setNavBarVisible(false);

		ranksLink.setTargetHistoryToken(PageType.RanksPageType.asTargetHistoryToken("view", OVERALL_LIST_TYPE, FilterController.get().asRankFilterString()));
		User user = SessionController.get().getLoggedInUser();
		if (user != null) {
			myAppsLink.setTargetHistoryToken(PageType.UsersPageType.asTargetHistoryToken(PageType.MyAppsPageType.toString(), user.id.toString(),
					FilterController.get().asMyAppsFilterString()));
		}
		feedBrowserLink.setTargetHistoryToken(PageType.FeedBrowserPageType.asTargetHistoryToken("view", FilterController.get().asFeedFilterString()));
		simpleModelRunLink.setTargetHistoryToken(PageType.SimpleModelRunPageType.asTargetHistoryToken(FilterController.get().asFeedFilterString()));
		dataAccountFetchesLink.setTargetHistoryToken(PageType.DataAccountFetchesPageType.asTargetHistoryToken(FilterController.get()
				.asDataAccountFetchFilterString()));

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
		if (totalUsers != null) {
			totalUsers.setInnerText(count.toString());
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.SessionEventHandler#userLoggedIn(io.reflection.app.shared.datatypes.User)
	 */
	@Override
	public void userLoggedIn(User user, Session session) {

		removeUserSignOut();
		addUserSignOut(user);

		removeLogin();
	}

	// private void addFeatureRequest() {
	// mFeatureRequest.setAttribute("style", "display: inline");
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.SessionEventHandler#userLoggedOut()
	 */
	@Override
	public void userLoggedOut() {
		removeLeaderboard();
		removeMyAccount();
		removeBlog();
		removeForum();
		removeAdmin();
		// removeFeatureRequest();
		// addRegister();
		// removeUpgrade();
		removeSearch();
		removeUserSignOut();
		addLogin();
	}

	// private void removeFeatureRequest() {
	// mFeatureRequest.setAttribute("style", "display: none");
	// }

	private void addLogin() {
		collapsableNavBar.appendChild(login);
	}

	private void removeLogin() {
		if (collapsableNavBar.isOrHasChild(login)) {
			collapsableNavBar.removeChild(login);
		}
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
			if (UserController.get().getUsersTotalCount() >= 0) {
				totalUsers.setInnerText(Long.toString(UserController.get().getUsersTotalCount()));
			} else {
				UserController.get().fetchUsersCount();
			}

			navList.getParentElement().appendChild(adminList);
		} else {
			removeAdmin();
		}
	}

	private void addSearch() {
		collapsableNavBar.appendChild(search);
	}

	private void removeSearch() {
		search.removeFromParent();
	}

	private void addUserSignOut(User user) {
		userName.setText(user.forename + "  " + user.surname);
		collapsableNavBar.appendChild(userSignOut);
	}

	private void removeUserSignOut() {
		userName.setText("");
		userSignOut.removeFromParent();
	}

	private void addLeaderboard() {
		navList.appendChild(ranksItem);
	}

	private void removeLeaderboard() {
		ranksItem.removeFromParent();
	}

	private void addBlog() {
		navList.appendChild(blogItem);
	}

	private void removeBlog() {
		blogItem.removeFromParent();
	}

	private void addForum() {
		if (SessionController.get().isLoggedInUserAdmin()) {
			navList.appendChild(forumItem);
		} else {
			removeForum();
		}
	}

	private void removeForum() {
		forumItem.removeFromParent();
	}

	private void addMyAccount(User user) {

		myAppsLink.setTargetHistoryToken(PageType.UsersPageType.asTargetHistoryToken(PageType.MyAppsPageType.toString(), user.id.toString(), FilterController
				.get().asMyAppsFilterString()));
		linkedAccountsLink.setTargetHistoryToken(PageType.UsersPageType.asTargetHistoryToken(PageType.LinkedAccountsPageType.toString(), user.id.toString()));
		accountSettingsLink.setTargetHistoryToken(PageType.UsersPageType.asTargetHistoryToken(PageType.ChangeDetailsPageType.toString(), user.id.toString()));

		navList.appendChild(myAccountList);
	}

	private void removeAdmin() {
		adminList.removeFromParent();
	}

	private void removeMyAccount() {
		myAccountList.removeFromParent();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.user.UserPowersEventHandler#gotUserPowers(io.reflection.app.shared.datatypes.User, java.util.List, java.util.List)
	 */
	@Override
	public void gotUserPowers(User user, List<Role> roles, List<Permission> permissions) {
		attachUserLinks(user);
		// addUpgrade();

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

	// public void removeUpgrade() {
	// upgradeAccountItem.removeFromParent();
	// }
	//
	// public void addUpgrade() {
	// if (SessionController.get().getLoggedInUser().roles == null || SessionController.get().getLoggedInUser().roles.size() == 0) {
	// navList.appendChild(upgradeAccountItem);
	// } else {
	// removeUpgrade();
	// }
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.core.shared.call.event.ChangeUserDetailsEventHandler#changeUserDetailsSuccess(io.reflection.app.api.core.shared.call.
	 * ChangeUserDetailsRequest, io.reflection.app.api.core.shared.call.ChangeUserDetailsResponse)
	 */
	@Override
	public void changeUserDetailsSuccess(ChangeUserDetailsRequest input, ChangeUserDetailsResponse output) {
		if (output.status == StatusType.StatusTypeSuccess) {
			User user = SessionController.get().getLoggedInUser();
			userName.setText(user.forename + "  " + user.surname);
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

	@UiHandler("collapseButton")
	void onCollapseButtonClicked(ClickEvent e) {
		String classNames = collapsableNavBar.getClassName();
		String[] splitClassNames = classNames.split(" ");

		boolean isCollapsed = false;
		for (String className : splitClassNames) {
			if ("collapse".equals(className)) {
				isCollapsed = true;
				break;
			}
		}

		setNavBarVisible(isCollapsed);
	}

	/**
	 * Detach and Attach User Links
	 */
	public void attachUserLinks(User user) {
		removeLeaderboard();
		removeMyAccount();
		removeBlog();
		removeForum();
		removeAdmin();
		removeSearch();
		if (user != null) {
			if (SessionController.get().isLoggedInUserAdmin() || SessionController.get().loggedInUserHas(DataTypeHelper.PERMISSION_HAS_LINKED_ACCOUNT_ID)) {
				addLeaderboard();
				addMyAccount(user);
				addBlog();
			}
			if (SessionController.get().isLoggedInUserAdmin()) {
				addForum();
				addAdmin();
			}
			addSearch();
		}

	}

	private void setNavBarVisible(boolean visible) {
		if (visible) {
			collapsableNavBar.removeClassName("collapse");
		} else {
			collapsableNavBar.addClassName("collapse");
		}
	}

	/**
	 * Show / Hide links and Enable / Disable the logo
	 * 
	 */
	public void setReadOnly(boolean readonly) {
		if (readonly) {
			collapsableNavBar.removeClassName("show");
			collapsableNavBar.addClassName("hidden");
			navbarBrandCover.removeClassName("hidden");
			navbarBrandCover.addClassName("show");
		} else {
			collapsableNavBar.removeClassName("hidden");
			collapsableNavBar.addClassName("show");
			navbarBrandCover.removeClassName("show");
			navbarBrandCover.addClassName("hidden");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.core.shared.call.event.LinkAccountEventHandler#linkAccountSuccess(io.reflection.app.api.core.shared.call.LinkAccountRequest,
	 * io.reflection.app.api.core.shared.call.LinkAccountResponse)
	 */
	@Override
	public void linkAccountSuccess(LinkAccountRequest input, LinkAccountResponse output) {
		attachUserLinks(SessionController.get().getLoggedInUser());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.core.shared.call.event.LinkAccountEventHandler#linkAccountFailure(io.reflection.app.api.core.shared.call.LinkAccountRequest,
	 * java.lang.Throwable)
	 */
	@Override
	public void linkAccountFailure(LinkAccountRequest input, Throwable caught) {}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.core.shared.call.event.DeleteLinkedAccountEventHandler#deleteLinkedAccountSuccess(io.reflection.app.api.core.shared.call.
	 * DeleteLinkedAccountRequest, io.reflection.app.api.core.shared.call.DeleteLinkedAccountResponse)
	 */
	@Override
	public void deleteLinkedAccountSuccess(DeleteLinkedAccountRequest input, DeleteLinkedAccountResponse output) {
		attachUserLinks(SessionController.get().getLoggedInUser());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.core.shared.call.event.DeleteLinkedAccountEventHandler#deleteLinkedAccountFailure(io.reflection.app.api.core.shared.call.
	 * DeleteLinkedAccountRequest, java.lang.Throwable)
	 */
	@Override
	public void deleteLinkedAccountFailure(DeleteLinkedAccountRequest input, Throwable caught) {}
}
