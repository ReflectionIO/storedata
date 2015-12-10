//
//  PanelLeftMenu.java
//  storedata
//
//  Created by Stefano Capuzzi on 25 Feb 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.part.navigation;

import static io.reflection.app.client.controller.FilterController.OVERALL_LIST_TYPE;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.UListElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.HasMouseOutHandlers;
import com.google.gwt.event.dom.client.HasMouseOverHandlers;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.Widget;
import com.willshex.gson.json.service.shared.Error;

import io.reflection.app.api.shared.datatypes.Session;
import io.reflection.app.client.DefaultEventBus;
import io.reflection.app.client.controller.FilterController;
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

/**
 * @author Stefano Capuzzi
 *
 */
public class PanelLeftMenu extends Composite
		implements UsersEventHandler, NavigationEventHandler, SessionEventHandler, UserPowersEventHandler, HasMouseOverHandlers, HasMouseOutHandlers, HasHTML {

	private static PanelLeftMenuUiBinder uiBinder = GWT.create(PanelLeftMenuUiBinder.class);

	interface PanelLeftMenuUiBinder extends UiBinder<Widget, PanelLeftMenu> {}

	public static final String IS_OPEN = Styles.STYLES_INSTANCE.reflectionMainStyle().isOpen();
	public static final String IS_SELECTED = Styles.STYLES_INSTANCE.reflectionMainStyle().isSelected();

	@UiField UListElement itemList;
	@UiField Anchor myDataLink;
	@UiField LIElement myDataItem;
	@UiField LIElement myAppsItem;
	@UiField LIElement linkedAccountsItem;
	@UiField LIElement leaderboardItem;
	@UiField LIElement productItem;
	@UiField LIElement pricingItem;
	@UiField LIElement blogItem;
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
	@UiField InlineHyperlink myAppsLink;
	@UiField InlineHyperlink linkedAccountsLink;
	@UiField InlineHyperlink leaderboardLink;
	@UiField InlineHyperlink adminFeedBrowserLink;
	@UiField InlineHyperlink adminSimpleModelRunLink;
	@UiField InlineHyperlink adminDataAccountFetchesLink;
	@UiField SpanElement usersCount;
	@UiField FocusPanel leftPanel;

	// private List<LIElement> items;
	private List<LIElement> highlightedItems = new ArrayList<LIElement>();

	public PanelLeftMenu() {
		initWidget(uiBinder.createAndBindUi(this));

		myDataItem.removeFromParent();
		adminItem.removeFromParent();		
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
		
		this.addMouseEvents();
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
			linkedAccountsLink
					.setTargetHistoryToken(PageType.UsersPageType.asTargetHistoryToken(PageType.LinkedAccountsPageType.toString(), user.id.toString()));
		}
		adminFeedBrowserLink.setTargetHistoryToken(PageType.FeedBrowserPageType.asTargetHistoryToken(NavigationController.VIEW_ACTION_PARAMETER_VALUE,
				FilterController.get().asFeedFilterString()));
		adminSimpleModelRunLink.setTargetHistoryToken(PageType.SimpleModelRunPageType.asTargetHistoryToken(FilterController.get().asFeedFilterString()));
		adminDataAccountFetchesLink
				.setTargetHistoryToken(PageType.DataAccountFetchesPageType.asTargetHistoryToken(FilterController.get().asDataAccountFetchFilterString()));

		// Highlight selected items
		if (PageType.UsersPageType.equals(current.getPage()) && current.getAction() != null && PageType.MyAppsPageType.equals(current.getAction())) {
			highlight(myDataItem, myAppsItem);
			closeDropDownItem(adminItem);
			openDropDownItem(myDataItem);
		} else if (PageType.UsersPageType.equals(current.getPage()) && current.getAction() != null
				&& PageType.LinkedAccountsPageType.equals(current.getAction())) {
			highlight(myDataItem, linkedAccountsItem);
			closeDropDownItem(adminItem);
			openDropDownItem(myDataItem);
		} else if (PageType.RanksPageType.equals(current.getPage())) {
			highlight(leaderboardItem);
			closeDropDownItem(myDataItem);
			closeDropDownItem(adminItem);
		} else if (PageType.ProductPageType.equals(current.getPage())) {
			highlight(productItem);
			closeDropDownItem(myDataItem);
			closeDropDownItem(adminItem);
		} else if (PageType.PricingPageType.equals(current.getPage())) {
			highlight(pricingItem);
			closeDropDownItem(myDataItem);
			closeDropDownItem(adminItem);
		} else if (PageType.BlogPostsPageType.equals(current.getPage()) || PageType.BlogPostPageType.equals(current.getPage())) {
			highlight(blogItem);
			closeDropDownItem(myDataItem);
			closeDropDownItem(adminItem);
			// } else if (PageType.ForumPageType.equals(current.getPage()) || PageType.ForumThreadPageType.equals(current.getPage())
			// || PageType.ForumTopicPageType.equals(current.getPage())) {
			// highlight(forumItem);
			// closeDropDownItem(adminItem);
		} else if (PageType.FeedBrowserPageType.equals(current.getPage())) {
			highlight(adminItem, adminFeedBrowserItem);
			closeDropDownItem(myDataItem);
			openDropDownItem(adminItem);
		} else if (PageType.SimpleModelRunPageType.equals(current.getPage())) {
			highlight(adminItem, adminSimpleModelRunItem);
			closeDropDownItem(myDataItem);
			openDropDownItem(adminItem);
		} else if (PageType.ItemsPageType.equals(current.getPage())) {
			highlight(adminItem, adminItemsItem);
			closeDropDownItem(myDataItem);
			openDropDownItem(adminItem);
		} else if (PageType.CategoriesPageType.equals(current.getPage())) {
			highlight(adminItem, adminCategoriesItem);
			closeDropDownItem(myDataItem);
			openDropDownItem(adminItem);
		} else if (PageType.UsersPageType.equals(current.getPage()) && current.getAction() == null) {
			highlight(adminItem, adminUsersItem);
			closeDropDownItem(myDataItem);
			openDropDownItem(adminItem);
		} else if (PageType.RolesPageType.equals(current.getPage())) {
			highlight(adminItem, adminRolesItem);
			closeDropDownItem(myDataItem);
			openDropDownItem(adminItem);
		} else if (PageType.PermissionsPageType.equals(current.getPage())) {
			highlight(adminItem, adminPermissionsItem);
			closeDropDownItem(myDataItem);
			openDropDownItem(adminItem);
		} else if (PageType.DataAccountsPageType.equals(current.getPage())) {
			highlight(adminItem, adminDataAccountsItem);
			closeDropDownItem(myDataItem);
			openDropDownItem(adminItem);
		} else if (PageType.DataAccountFetchesPageType.equals(current.getPage())) {
			highlight(adminItem, adminDataAccountFetchesItem);
			closeDropDownItem(myDataItem);
			openDropDownItem(adminItem);
		} else if (PageType.EventsPageType.equals(current.getPage())) {
			highlight(adminItem, adminEventsItem);
			closeDropDownItem(myDataItem);
			openDropDownItem(adminItem);
		} else if (PageType.EventSubscriptionsPageType.equals(current.getPage())) {
			highlight(adminItem, adminEventSubscriptionsItem);
			closeDropDownItem(myDataItem);
			openDropDownItem(adminItem);
		} else if (PageType.SendNotificationPageType.equals(current.getPage())) {
			highlight(adminItem, adminSendNotificationItem);
			closeDropDownItem(myDataItem);
			openDropDownItem(adminItem);
		} else if (PageType.BlogAdminPageType.equals(current.getPage()) || PageType.BlogEditPostPageType.equals(current.getPage())) {
			highlight(adminItem, adminBlogItem);
			closeDropDownItem(myDataItem);
			openDropDownItem(adminItem);
		} else {
			highlight();
			closeDropDownItem(myDataItem);
			closeDropDownItem(adminItem);
		}

	}

	private void toggleDropDownItem(LIElement liElem) {
		UListElement ulElem = liElem.getElementsByTagName("ul").getItem(0).cast();
		if (liElem.hasClassName(IS_OPEN)) {
			ulElem.getStyle().setMarginTop(-(ulElem.getClientHeight()), Unit.PX);
			liElem.removeClassName(IS_OPEN);
		} else {
			ulElem.getStyle().setMarginTop(0, Unit.PX);
			liElem.addClassName(IS_OPEN);
		}
	}

	private void openDropDownItem(LIElement liElem) {
		if (!liElem.hasClassName(IS_OPEN)) {
			UListElement ulElem = liElem.getElementsByTagName("ul").getItem(0).cast();
			ulElem.getStyle().setMarginTop(0, Unit.PX);
			liElem.addClassName(IS_OPEN);
		}
	}
	
	private void openSelectedDropDownItem(LIElement liElem) {
		if (!liElem.hasClassName(IS_OPEN) && liElem.hasClassName(IS_SELECTED)) {
			UListElement ulElem = liElem.getElementsByTagName("ul").getItem(0).cast();
			ulElem.getStyle().setMarginTop(0, Unit.PX);
			liElem.addClassName(IS_OPEN);
		}
	}

	private void closeDropDownItem(LIElement liElem) {
		if (liElem.hasClassName(IS_OPEN)) {
			UListElement ulElem = liElem.getElementsByTagName("ul").getItem(0).cast();
			ulElem.getStyle().setMarginTop(-(ulElem.getClientHeight()), Unit.PX);
			liElem.removeClassName(IS_OPEN);
		}
	}
	
	private void addMouseEvents() {
		if(DOM.getElementById("thehtml").getAttribute("class").indexOf("no-touch") > 0) { // No Touch Browser
			leftPanel.addMouseOverHandler(new MouseOverHandler() {

				@Override
				public void onMouseOver(MouseOverEvent event) {
					openSelectedDropDownItem(myDataItem);
					openSelectedDropDownItem(adminItem);
				}
			});
			
			leftPanel.addMouseOutHandler(new MouseOutHandler() {

				@Override
				public void onMouseOut(MouseOutEvent event) {
					closeDropDownItem(myDataItem);
					closeDropDownItem(adminItem);
				}
			});
		} else { // Touch Device
			openSelectedDropDownItem(myDataItem);
			openSelectedDropDownItem(adminItem);
		}
	}

	@UiHandler("myDataLink")
	void onMyDataLinkClicked(ClickEvent event) {
		event.preventDefault();
		toggleDropDownItem(myDataItem);
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
		closeDropDownItem(myDataItem);
		myDataItem.removeFromParent();
		closeDropDownItem(adminItem);
		adminItem.removeFromParent();
		itemList.insertBefore(productItem, blogItem);
		itemList.insertBefore(pricingItem, blogItem);
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
	public void gotUserPowers(User user, List<Role> roles, List<Permission> permissions, Integer daysSinceRoleAssigned) {
		// Since is attached once, close dropdowns when the panel is created
		productItem.removeFromParent();
		pricingItem.removeFromParent();
		if (user != null) {
			if (!itemList.isOrHasChild(myDataItem)) {
				itemList.insertFirst(myDataItem);
				UListElement ulElem = myDataItem.getElementsByTagName("ul").getItem(0).cast();
				ulElem.getStyle().setMarginTop(-(ulElem.getClientHeight()), Unit.PX);
			}
			if (SessionController.get().isAdmin() && !itemList.isOrHasChild(adminItem)) {
				itemList.appendChild(adminItem);
				UListElement ulElem = adminItem.getElementsByTagName("ul").getItem(0).cast();
				ulElem.getStyle().setMarginTop(-(ulElem.getClientHeight()), Unit.PX);
				UserController.get().fetchUsersCount();
			} else {
				adminItem.removeFromParent();
			}
		}
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

	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.HasText#getText()
	 */
	@Override
	public String getText() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.HasText#setText(java.lang.String)
	 */
	@Override
	public void setText(String text) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.HasHTML#getHTML()
	 */
	@Override
	public String getHTML() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.HasHTML#setHTML(java.lang.String)
	 */
	@Override
	public void setHTML(String html) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.dom.client.HasMouseOutHandlers#addMouseOutHandler(com.google.gwt.event.dom.client.MouseOutHandler)
	 */
	@Override
	public HandlerRegistration addMouseOutHandler(MouseOutHandler handler) {
		return addDomHandler(handler, MouseOutEvent.getType());
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.dom.client.HasMouseOverHandlers#addMouseOverHandler(com.google.gwt.event.dom.client.MouseOverHandler)
	 */
	@Override
	public HandlerRegistration addMouseOverHandler(MouseOverHandler handler) {
		return addDomHandler(handler, MouseOverEvent.getType());
	}
}
