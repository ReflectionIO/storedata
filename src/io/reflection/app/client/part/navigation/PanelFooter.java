//
//  PanelFooter.java
//  storedata
//
//  Created by Jamie Gilman on 09 Nov 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.part.navigation;

import static io.reflection.app.client.controller.FilterController.OVERALL_LIST_TYPE;
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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.UListElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.Widget;
import com.willshex.gson.json.service.shared.Error;

/**
 * @author Jamie Gilman
 *
 */
public class PanelFooter extends Composite implements UsersEventHandler, NavigationEventHandler, SessionEventHandler, UserPowersEventHandler {

	private static PanelFooterUiBinder uiBinder = GWT.create(PanelFooterUiBinder.class);

	interface PanelFooterUiBinder extends UiBinder<Widget, PanelFooter> {}
	
	public static final String IS_SELECTED = Styles.STYLES_INSTANCE.reflectionMainStyle().isSelected();

	@UiField LIElement leaderboardItem;
	@UiField LIElement productItem;
	@UiField LIElement pricingItem;
	
	@UiField InlineHyperlink pricingSecondaryLink;
	@UiField UListElement secondaryFooterList;
	@UiField InlineHyperlink leaderboardLink;

	// private List<LIElement> items;
	private List<LIElement> highlightedItems = new ArrayList<LIElement>();

	public PanelFooter() {
		initWidget(uiBinder.createAndBindUi(this));
		
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

		this.setVisible(!PageType.ContactPageType.equals(current.getPage()));

		// Highlight selected items
		if (PageType.RanksPageType.equals(current.getPage())) {
			highlight(leaderboardItem);
		} else if (PageType.ProductPageType.equals(current.getPage())) {
			highlight(productItem);
		} else if (PageType.PricingPageType.equals(current.getPage())) {
			highlight(pricingItem);
		}
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
		if (user != null) {
			pricingSecondaryLink.setTargetHistoryToken(PageType.UsersPageType.asTargetHistoryToken(PageType.ManageSubscriptionPageType.toString(),
					user.id.toString()));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.user.UserPowersEventHandler#getGetUserPowersFailed(com.willshex.gson.json.service.shared.Error)
	 */
	@Override
	public void getGetUserPowersFailed(Error error) {
	}

}
