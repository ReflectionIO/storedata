//
//  MyAccountSidePanel.java
//  storedata
//
//  Created by Stefano Capuzzi on 16 May 2014.
//  Copyright ?? 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page.part;

import io.reflection.app.client.controller.FilterController;
import io.reflection.app.client.controller.SessionController;
import io.reflection.app.client.page.PageType;
import io.reflection.app.datatypes.shared.User;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author stefanocapuzzi
 * 
 */
public class MyAccountSidePanel extends Composite {

	private static MyAccountSidePanelUiBinder uiBinder = GWT.create(MyAccountSidePanelUiBinder.class);

	interface MyAccountSidePanelUiBinder extends UiBinder<Widget, MyAccountSidePanel> {}

	private static final String ACTIVE_STYLE_NAME = "active";

	@UiField HeadingElement creatorName;

	@UiField InlineHyperlink myAppsLink;
	@UiField LIElement myAppsListItem;
	@UiField InlineHyperlink linkedAccountsLink;
	@UiField LIElement linkedAccountsListItem;
	@UiField InlineHyperlink accountSettingsLink;
	@UiField LIElement accountSettingsListItem;

	@UiField InlineHyperlink notificationsLink;
	@UiField LIElement notificationsListItem;
	@UiField LIElement notificationDivider;

	public MyAccountSidePanel() {
		initWidget(uiBinder.createAndBindUi(this));

		if (!SessionController.get().isLoggedInUserAdmin()) {
			notificationsListItem.removeFromParent();
			notificationDivider.removeFromParent();
		}
	}

	private void setMyAppsLinkActive() {
		deactivate(linkedAccountsListItem);
		deactivate(accountSettingsListItem);
		activate(myAppsListItem);
		deactivate(notificationsListItem);
	}

	private void setLinkedAccountsLinkActive() {
		deactivate(myAppsListItem);
		deactivate(accountSettingsListItem);
		activate(linkedAccountsListItem);
		deactivate(notificationsListItem);
	}

	private void setPersonalDetailsLinkActive() {
		deactivate(myAppsListItem);
		deactivate(linkedAccountsListItem);
		activate(accountSettingsListItem);
		deactivate(notificationsListItem);
	}

	private void setChangePasswordLinkActive() {
		deactivate(myAppsListItem);
		deactivate(linkedAccountsListItem);
		deactivate(accountSettingsListItem);
		deactivate(notificationsListItem);
	}

	private void setNotificationsLinkActive() {
		deactivate(myAppsListItem);
		deactivate(linkedAccountsListItem);
		deactivate(accountSettingsListItem);
		activate(notificationsListItem);
	}

	public void setActive(PageType page) {
		switch (page) {
		case MyAppsPageType:
			setMyAppsLinkActive();
			break;
		case LinkedAccountsPageType:
			setLinkedAccountsLinkActive();
			break;
		case ChangeDetailsPageType:
			setPersonalDetailsLinkActive();
			break;
		case NotificationsPageType:
			setNotificationsLinkActive();
			break;
		case ChangePasswordPageType:
			setChangePasswordLinkActive();
			break;
		default:
			break;
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

	/**
	 * @param user
	 */
	public void setUser(User user) {
		creatorName.setInnerText(user.company == null ? "-" : user.company);

		String currentFilter = FilterController.get().asMyAppsFilterString();
		if (currentFilter != null && currentFilter.length() > 0) {
			myAppsLink
					.setTargetHistoryToken(PageType.UsersPageType.asTargetHistoryToken(PageType.MyAppsPageType.toString(), user.id.toString(), currentFilter));
		} else {
			myAppsLink.setTargetHistoryToken(PageType.UsersPageType.asTargetHistoryToken(PageType.MyAppsPageType.toString(), user.id.toString()));
		}

		linkedAccountsLink.setTargetHistoryToken(PageType.UsersPageType.asTargetHistoryToken(PageType.LinkedAccountsPageType.toString(), user.id.toString()));
		accountSettingsLink.setTargetHistoryToken(PageType.UsersPageType.asTargetHistoryToken(PageType.ChangeDetailsPageType.toString(), user.id.toString()));
		notificationsLink.setTargetHistoryToken(PageType.UsersPageType.asTargetHistoryToken(PageType.NotificationsPageType.toString(), user.id.toString()));
	}

}
