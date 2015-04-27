//
//  PanelRightAccount.java
//  storedata
//
//  Created by Stefano Capuzzi on 25 Feb 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.part.navigation;

import io.reflection.app.api.core.shared.call.ChangeUserDetailsRequest;
import io.reflection.app.api.core.shared.call.ChangeUserDetailsResponse;
import io.reflection.app.api.core.shared.call.event.ChangeUserDetailsEventHandler;
import io.reflection.app.api.shared.datatypes.Session;
import io.reflection.app.client.DefaultEventBus;
import io.reflection.app.client.controller.FilterController;
import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.controller.NavigationController.Stack;
import io.reflection.app.client.controller.SessionController;
import io.reflection.app.client.handler.NavigationEventHandler;
import io.reflection.app.client.handler.user.SessionEventHandler;
import io.reflection.app.client.handler.user.UserPowersEventHandler;
import io.reflection.app.client.page.PageType;
import io.reflection.app.client.part.login.LoginForm;
import io.reflection.app.client.res.Styles;
import io.reflection.app.datatypes.shared.Permission;
import io.reflection.app.datatypes.shared.Role;
import io.reflection.app.datatypes.shared.User;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.Widget;
import com.willshex.gson.json.service.shared.Error;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author Stefano Capuzzi
 *
 */
public class PanelRightAccount extends Composite implements NavigationEventHandler, UserPowersEventHandler, SessionEventHandler, ChangeUserDetailsEventHandler {

	private static PanelRightAccountUiBinder uiBinder = GWT.create(PanelRightAccountUiBinder.class);

	interface PanelRightAccountUiBinder extends UiBinder<Widget, PanelRightAccount> {}

	public static final String IS_OPEN = Styles.STYLES_INSTANCE.reflectionMainStyle().isOpen();
	public static final String IS_SELECTED = Styles.STYLES_INSTANCE.reflectionMainStyle().isSelected();
	public static final String IS_SHOWING = Styles.STYLES_INSTANCE.reflectionMainStyle().isShowing();

	@UiField DivElement accessPanelContainer;

	@UiField DivElement quotePanel;
	@UiField LoginForm loginForm;
	@UiField DivElement panelOverlay;
	@UiField DivElement userDetailsPanel;
	@UiField Element accountMenu;
	@UiField HeadingElement userName;
	@UiField HeadingElement userCompany;
	@UiField LIElement myAppsItem;
	@UiField LIElement linkedAccountsItem;
	@UiField LIElement accountSettingsItem;
	@UiField LIElement notificationItem;
	@UiField InlineHyperlink myAppsLink;
	@UiField InlineHyperlink linkedAccountsLink;
	@UiField InlineHyperlink accountSettingsLink;
	@UiField InlineHyperlink notificationLink;

	private List<LIElement> items;
	private List<LIElement> highlightedItems = new ArrayList<LIElement>();

	public PanelRightAccount() {
		initWidget(uiBinder.createAndBindUi(this));

		setLoggedIn(false);

		createItemList();

	}

	public void setLoggedIn(boolean loggedIn) {
		if (loggedIn) {
			loginForm.getElement().removeFromParent();
			quotePanel.removeFromParent();
			if (!accessPanelContainer.isOrHasChild(userDetailsPanel)) {
				accessPanelContainer.appendChild(userDetailsPanel);
			}
			if (!accessPanelContainer.isOrHasChild(accountMenu)) {
				accessPanelContainer.appendChild(accountMenu);
			}
		} else {
			userDetailsPanel.removeFromParent();
			accountMenu.removeFromParent();
			if (!accessPanelContainer.isOrHasChild(loginForm.getElement())) {
				accessPanelContainer.appendChild(loginForm.getElement());
			}
			if (!accessPanelContainer.isOrHasChild(quotePanel)) {
				accessPanelContainer.appendChild(quotePanel);
			}
		}
	}

	private void createItemList() {
		if (items == null) {
			items = new ArrayList<LIElement>();
			items.add(myAppsItem);
			items.add(linkedAccountsItem);
			items.add(accountSettingsItem);
			items.add(notificationItem);
		}
	}

	private void attachUserLinks(User user) {
		if (user != null) {
			userName.setInnerText(user.forename + " " + user.surname);
			userCompany.setInnerText(user.company);

			myAppsLink.setTargetHistoryToken(PageType.UsersPageType.asTargetHistoryToken(PageType.MyAppsPageType.toString(), user.id.toString(),
					FilterController.get().asMyAppsFilterString()));
			linkedAccountsLink
					.setTargetHistoryToken(PageType.UsersPageType.asTargetHistoryToken(PageType.LinkedAccountsPageType.toString(), user.id.toString()));
			accountSettingsLink
					.setTargetHistoryToken(PageType.UsersPageType.asTargetHistoryToken(PageType.ChangeDetailsPageType.toString(), user.id.toString()));
			notificationLink.setTargetHistoryToken(PageType.UsersPageType.asTargetHistoryToken(PageType.NotificationsPageType.toString(), user.id.toString()));
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

	public DivElement getPanelOverlay() {
		return panelOverlay;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.Composite#onAttach()
	 */
	@Override
	protected void onAttach() {
		super.onAttach();
		DefaultEventBus.get().addHandlerToSource(NavigationEventHandler.TYPE, NavigationController.get(), this);
		DefaultEventBus.get().addHandlerToSource(UserPowersEventHandler.TYPE, SessionController.get(), this);
		DefaultEventBus.get().addHandlerToSource(SessionEventHandler.TYPE, SessionController.get(), this);
		DefaultEventBus.get().addHandlerToSource(ChangeUserDetailsEventHandler.TYPE, SessionController.get(), this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.NavigationEventHandler#navigationChanged(io.reflection.app.client.controller.NavigationController.Stack,
	 * io.reflection.app.client.controller.NavigationController.Stack)
	 */
	@Override
	public void navigationChanged(Stack previous, Stack current) {
		User user = SessionController.get().getLoggedInUser();
		if (user != null) {
			myAppsLink.setTargetHistoryToken(PageType.UsersPageType.asTargetHistoryToken(PageType.MyAppsPageType.toString(), user.id.toString(),
					FilterController.get().asMyAppsFilterString()));
		}

		// Highlight selected items
		if (PageType.UsersPageType.equals(current.getPage()) && current.getAction() != null && PageType.MyAppsPageType.equals(current.getAction())) {
			highlight(myAppsItem);
		} else if (PageType.UsersPageType.equals(current.getPage()) && current.getAction() != null
				&& PageType.LinkedAccountsPageType.equals(current.getAction())) {
			highlight(linkedAccountsItem);
		} else if (PageType.UsersPageType.equals(current.getPage()) && current.getAction() != null
				&& PageType.ChangeDetailsPageType.equals(current.getAction())) {
			highlight(accountSettingsItem);
		} else if (PageType.UsersPageType.equals(current.getPage()) && current.getAction() != null
				&& PageType.NotificationsPageType.equals(current.getAction())) {
			highlight(notificationItem);
		} else {
			highlight();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.user.UserPowersEventHandler#gotUserPowers(io.reflection.app.datatypes.shared.User, java.util.List, java.util.List)
	 */
	@Override
	public void gotUserPowers(User user, List<Role> roles, List<Permission> permissions) {
		attachUserLinks(user);
		setLoggedIn(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.user.UserPowersEventHandler#getGetUserPowersFailed(com.willshex.gson.json.service.shared.Error)
	 */
	@Override
	public void getGetUserPowersFailed(Error error) {}

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
		setLoggedIn(false);
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
	 * @see io.reflection.app.api.core.shared.call.event.ChangeUserDetailsEventHandler#changeUserDetailsSuccess(io.reflection.app.api.core.shared.call.
	 * ChangeUserDetailsRequest, io.reflection.app.api.core.shared.call.ChangeUserDetailsResponse)
	 */
	@Override
	public void changeUserDetailsSuccess(ChangeUserDetailsRequest input, ChangeUserDetailsResponse output) {
		if (output.status == StatusType.StatusTypeSuccess) {
			User user = SessionController.get().getLoggedInUser();
			userName.setInnerText(user.forename + " " + user.surname);
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

}
