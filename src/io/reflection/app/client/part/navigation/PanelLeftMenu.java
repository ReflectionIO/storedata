//
//  PanelLeftMenu.java
//  storedata
//
//  Created by Stefano Capuzzi on 25 Feb 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.part.navigation;

import static io.reflection.app.client.controller.FilterController.OVERALL_LIST_TYPE;
import io.reflection.app.api.core.shared.call.DeleteLinkedAccountRequest;
import io.reflection.app.api.core.shared.call.DeleteLinkedAccountResponse;
import io.reflection.app.api.core.shared.call.LinkAccountRequest;
import io.reflection.app.api.core.shared.call.LinkAccountResponse;
import io.reflection.app.api.core.shared.call.event.DeleteLinkedAccountEventHandler;
import io.reflection.app.api.core.shared.call.event.LinkAccountEventHandler;
import io.reflection.app.api.shared.datatypes.Session;
import io.reflection.app.client.DefaultEventBus;
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
import io.reflection.app.client.res.Styles;
import io.reflection.app.datatypes.shared.Permission;
import io.reflection.app.datatypes.shared.Role;
import io.reflection.app.datatypes.shared.User;
import io.reflection.app.shared.util.DataTypeHelper;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.UListElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.Widget;
import com.willshex.gson.json.service.shared.Error;

/**
 * @author Stefano Capuzzi
 *
 */
public class PanelLeftMenu extends Composite implements UsersEventHandler, NavigationEventHandler, SessionEventHandler, UserPowersEventHandler,
		LinkAccountEventHandler, DeleteLinkedAccountEventHandler {

	private static PanelLeftMenuUiBinder uiBinder = GWT.create(PanelLeftMenuUiBinder.class);

	interface PanelLeftMenuUiBinder extends UiBinder<Widget, PanelLeftMenu> {}

	public static final String IS_OPEN = Styles.STYLES_INSTANCE.reflectionMainStyle().isOpen();
	public static final String IS_SELECTED = Styles.STYLES_INSTANCE.reflectionMainStyle().isSelected();

	@UiField UListElement itemList;
	@UiField LIElement leaderboardItem;
	@UiField LIElement myAppsItem;
	@UiField LIElement blogItem;
	@UiField LIElement forumItem;
	@UiField LIElement adminItem;
	@UiField LIElement adminFeedBrowserItem;
	@UiField LIElement adminSimpleModelRunItem;
	@UiField LIElement adminItemsItem;
	@UiField LIElement adminCategoriesItem;
	@UiField LIElement adminUsersItem;
	@UiField LIElement adminRolesItem;
	@UiField LIElement adminPermissionsItem;
	@UiField LIElement adminDataAccountsItem;
	@UiField LIElement adminDataAccountFetchesItem;
	@UiField LIElement adminEventsItem;
	@UiField LIElement adminEventSubscriptionsItem;
	@UiField LIElement adminSendNotificationItem;
	@UiField LIElement adminBlogItem;
	@UiField Anchor adminLink;
	@UiField InlineHyperlink leaderboardLink;
	@UiField InlineHyperlink myAppsLink;
	@UiField InlineHyperlink adminFeedBrowserLink;
	@UiField InlineHyperlink adminSimpleModelRunLink;
	@UiField InlineHyperlink adminDataAccountFetchesLink;
	@UiField SpanElement usersCount;

	private List<LIElement> items;
	private List<LIElement> highlightedItems = new ArrayList<LIElement>();

	public PanelLeftMenu() {
		initWidget(uiBinder.createAndBindUi(this));

		leaderboardItem.removeFromParent();
		myAppsItem.removeFromParent();
		adminItem.removeFromParent();
		createItemList();

	}

	private void attachUserLinks(User user) {
		if (user != null) {
			if (SessionController.get().isLoggedInUserAdmin() || SessionController.get().loggedInUserHas(DataTypeHelper.PERMISSION_HAS_LINKED_ACCOUNT_CODE)) {
				if (!itemList.isOrHasChild(myAppsItem)) {
					itemList.insertFirst(myAppsItem);
				}
				if (!itemList.isOrHasChild(leaderboardItem)) {
					itemList.insertFirst(leaderboardItem);
				}
			} else {
				leaderboardItem.removeFromParent();
				myAppsItem.removeFromParent();
			}
			if (SessionController.get().isLoggedInUserAdmin()) {
				itemList.appendChild(adminItem);
				UListElement ulAdminElem = adminItem.getElementsByTagName("ul").getItem(0).cast(); // Close admin menu
				ulAdminElem.getStyle().setMarginTop(-(ulAdminElem.getClientHeight()), Unit.PX);
				UserController.get().fetchUsersCount();
			} else {
				adminItem.removeFromParent();
			}
		}
	}

	private void createItemList() {
		if (items == null) {
			items = new ArrayList<LIElement>();
			items.add(leaderboardItem);
			items.add(myAppsItem);
			items.add(blogItem);
			items.add(forumItem);
			items.add(adminItem);
			items.add(adminFeedBrowserItem);
			items.add(adminSimpleModelRunItem);
			items.add(adminItemsItem);
			items.add(adminCategoriesItem);
			items.add(adminUsersItem);
			items.add(adminRolesItem);
			items.add(adminPermissionsItem);
			items.add(adminDataAccountsItem);
			items.add(adminDataAccountFetchesItem);
			items.add(adminEventsItem);
			items.add(adminEventSubscriptionsItem);
			items.add(adminEventSubscriptionsItem);
			items.add(adminSendNotificationItem);
			items.add(adminBlogItem);
		}
	}

	private void activate(LIElement item) {
		if (item != null) {
			item.addClassName(IS_SELECTED);
		}
	}

	private void deactivate(LIElement item) {
		if (item != null) {
			item.removeClassName(IS_SELECTED);
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
	 * @see com.google.gwt.user.client.ui.Composite#onAttach()
	 */
	@Override
	protected void onAttach() {
		super.onAttach();
		DefaultEventBus.get().addHandlerToSource(UsersEventHandler.TYPE, UserController.get(), this);
		DefaultEventBus.get().addHandlerToSource(NavigationEventHandler.TYPE, NavigationController.get(), this);
		DefaultEventBus.get().addHandlerToSource(SessionEventHandler.TYPE, SessionController.get(), this);
		DefaultEventBus.get().addHandlerToSource(UserPowersEventHandler.TYPE, SessionController.get(), this);
		DefaultEventBus.get().addHandlerToSource(LinkAccountEventHandler.TYPE, LinkedAccountController.get(), this);
		DefaultEventBus.get().addHandlerToSource(DeleteLinkedAccountEventHandler.TYPE, LinkedAccountController.get(), this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.NavigationEventHandler#navigationChanged(io.reflection.app.client.controller.NavigationController.Stack,
	 * io.reflection.app.client.controller.NavigationController.Stack)
	 */
	@Override
	public void navigationChanged(Stack previous, Stack current) {
		leaderboardLink.setTargetHistoryToken(PageType.RanksPageType.asTargetHistoryToken(NavigationController.VIEW_ACTION_PARAMETER_VALUE, OVERALL_LIST_TYPE,
				FilterController.get().asRankFilterString()));
		User user = SessionController.get().getLoggedInUser();
		if (user != null) {
			myAppsLink.setTargetHistoryToken(PageType.UsersPageType.asTargetHistoryToken(PageType.MyAppsPageType.toString(), user.id.toString(),
					FilterController.get().asMyAppsFilterString()));
		}
		adminFeedBrowserLink.setTargetHistoryToken(PageType.FeedBrowserPageType.asTargetHistoryToken(NavigationController.VIEW_ACTION_PARAMETER_VALUE,
				FilterController.get().asFeedFilterString()));
		adminSimpleModelRunLink.setTargetHistoryToken(PageType.SimpleModelRunPageType.asTargetHistoryToken(FilterController.get().asFeedFilterString()));
		adminDataAccountFetchesLink.setTargetHistoryToken(PageType.DataAccountFetchesPageType.asTargetHistoryToken(FilterController.get()
				.asDataAccountFetchFilterString()));

		// Highlight selected items
		if (PageType.RanksPageType.equals(current.getPage())) {
			highlight(leaderboardItem);
			closeDropDownItem(adminItem);
		} else if (PageType.UsersPageType.equals(current.getPage()) && current.getAction() != null && PageType.MyAppsPageType.equals(current.getAction())) {
			highlight(myAppsItem);
		} else if (PageType.BlogPostsPageType.equals(current.getPage()) || PageType.BlogPostPageType.equals(current.getPage())) {
			highlight(blogItem);
			closeDropDownItem(adminItem);
		} else if (PageType.ForumPageType.equals(current.getPage()) || PageType.ForumThreadPageType.equals(current.getPage())
				|| PageType.ForumTopicPageType.equals(current.getPage())) {
			highlight(forumItem);
			closeDropDownItem(adminItem);
		} else if (PageType.FeedBrowserPageType.equals(current.getPage())) {
			highlight(adminItem, adminFeedBrowserItem);
			openDropDownItem(adminItem);
		} else if (PageType.SimpleModelRunPageType.equals(current.getPage())) {
			highlight(adminItem, adminSimpleModelRunItem);
			openDropDownItem(adminItem);
		} else if (PageType.ItemsPageType.equals(current.getPage())) {
			highlight(adminItem, adminItemsItem);
			openDropDownItem(adminItem);
		} else if (PageType.CategoriesPageType.equals(current.getPage())) {
			highlight(adminItem, adminCategoriesItem);
			openDropDownItem(adminItem);
		} else if (PageType.UsersPageType.equals(current.getPage()) && current.getAction() == null) {
			highlight(adminItem, adminUsersItem);
			openDropDownItem(adminItem);
		} else if (PageType.RolesPageType.equals(current.getPage())) {
			highlight(adminItem, adminRolesItem);
			openDropDownItem(adminItem);
		} else if (PageType.PermissionsPageType.equals(current.getPage())) {
			highlight(adminItem, adminPermissionsItem);
			openDropDownItem(adminItem);
		} else if (PageType.DataAccountsPageType.equals(current.getPage())) {
			highlight(adminItem, adminDataAccountsItem);
			openDropDownItem(adminItem);
		} else if (PageType.DataAccountFetchesPageType.equals(current.getPage())) {
			highlight(adminItem, adminDataAccountFetchesItem);
			openDropDownItem(adminItem);
		} else if (PageType.EventsPageType.equals(current.getPage())) {
			highlight(adminItem, adminEventsItem);
			openDropDownItem(adminItem);
		} else if (PageType.EventSubscriptionsPageType.equals(current.getPage())) {
			highlight(adminItem, adminEventSubscriptionsItem);
			openDropDownItem(adminItem);
		} else if (PageType.SendNotificationPageType.equals(current.getPage())) {
			highlight(adminItem, adminSendNotificationItem);
			openDropDownItem(adminItem);
		} else if (PageType.BlogAdminPageType.equals(current.getPage()) || PageType.BlogEditPostPageType.equals(current.getPage())) {
			highlight(adminItem, adminBlogItem);
			openDropDownItem(adminItem);
		} else {
			highlight();
			closeDropDownItem(adminItem);
		}

	}

	private void toggleDropDownItem(LIElement liElem) {
		UListElement ulElem = liElem.getElementsByTagName("ul").getItem(0).cast();
		if (liElem.hasClassName(IS_OPEN)) {
			ulElem.getStyle().setMarginTop(-(ulElem.getClientHeight()), Unit.PX);
			liElem.removeClassName(IS_OPEN);
			liElem.removeClassName(IS_SELECTED);
		} else {
			ulElem.getStyle().setMarginTop(0, Unit.PX);
			liElem.addClassName(IS_OPEN);
			liElem.addClassName(IS_SELECTED);
			leaderboardItem.removeClassName(IS_SELECTED);
			blogItem.removeClassName(IS_SELECTED);
			forumItem.removeClassName(IS_SELECTED);
		}
	}

	private void openDropDownItem(LIElement liElem) {
		if (!liElem.hasClassName(IS_OPEN)) {
			UListElement ulElem = liElem.getElementsByTagName("ul").getItem(0).cast();
			ulElem.getStyle().setMarginTop(0, Unit.PX);
			liElem.addClassName(IS_OPEN);
			// liElem.addClassName(IS_SELECTED);
			leaderboardItem.removeClassName(IS_SELECTED);
			blogItem.removeClassName(IS_SELECTED);
			forumItem.removeClassName(IS_SELECTED);
		}
	}

	private void closeDropDownItem(LIElement liElem) {
		if (liElem.hasClassName(IS_OPEN)) {
			UListElement ulElem = liElem.getElementsByTagName("ul").getItem(0).cast();
			ulElem.getStyle().setMarginTop(-(ulElem.getClientHeight()), Unit.PX);
			liElem.removeClassName(IS_OPEN);
			liElem.removeClassName(IS_SELECTED);
		}
	}

	@UiHandler("adminLink")
	void onAdminLinkClicked(ClickEvent event) {
		event.preventDefault();
		toggleDropDownItem(adminItem);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.user.UsersEventHandler#receivedUsers(java.util.List)
	 */
	@Override
	public void receivedUsers(List<User> users) {}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.user.UsersEventHandler#receivedUsersCount(java.lang.Long)
	 */
	@Override
	public void receivedUsersCount(Long count) {
		usersCount.setInnerText(count.toString());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.user.SessionEventHandler#userLoggedIn(io.reflection.app.datatypes.shared.User,
	 * io.reflection.app.api.shared.datatypes.Session)
	 */
	@Override
	public void userLoggedIn(User user, Session session) {}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.user.SessionEventHandler#userLoggedOut()
	 */
	@Override
	public void userLoggedOut() {
		leaderboardItem.removeFromParent();
		adminItem.removeFromParent();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.user.SessionEventHandler#userLoginFailed(com.willshex.gson.json.service.shared.Error)
	 */
	@Override
	public void userLoginFailed(Error error) {
		userLoggedOut();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.user.UserPowersEventHandler#gotUserPowers(io.reflection.app.datatypes.shared.User, java.util.List, java.util.List)
	 */
	@Override
	public void gotUserPowers(User user, List<Role> roles, List<Permission> permissions) {
		attachUserLinks(user);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.user.UserPowersEventHandler#getGetUserPowersFailed(com.willshex.gson.json.service.shared.Error)
	 */
	@Override
	public void getGetUserPowersFailed(Error error) {
		adminItem.removeFromParent();
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
