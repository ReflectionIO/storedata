//
//  MyAccountSidePanel.java
//  storedata
//
//  Created by Stefano Capuzzi on 16 May 2014.
//  Copyright ?? 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page.part;

import io.reflection.app.client.page.PageType;

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

	@UiField InlineHyperlink accountNotificationsLink;
	@UiField LIElement accountNotificationsListItem;

	public MyAccountSidePanel() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public HeadingElement getCreatorNameLink() {
		return creatorName;
	}

	public InlineHyperlink getMyAppsLink() {
		return myAppsLink;
	}

	public void setMyAppsLinkActive() {
		deactivate(linkedAccountsListItem);
		deactivate(accountSettingsListItem);
		activate(myAppsListItem);
		deactivate(accountNotificationsListItem);
	}

	public InlineHyperlink getLinkedAccountsLink() {
		return linkedAccountsLink;
	}

	public void setLinkedAccountsLinkActive() {
		deactivate(myAppsListItem);
		deactivate(accountSettingsListItem);
		activate(linkedAccountsListItem);
		deactivate(accountNotificationsListItem);
	}

	public InlineHyperlink getPersonalDetailsLink() {
		return accountSettingsLink;
	}

	public void setPersonalDetailsLinkActive() {
		deactivate(myAppsListItem);
		deactivate(linkedAccountsListItem);
		activate(accountSettingsListItem);
		deactivate(accountNotificationsListItem);
	}

	public void setChangePasswordLinkActive() {
		deactivate(myAppsListItem);
		deactivate(linkedAccountsListItem);
		deactivate(accountSettingsListItem);
		deactivate(accountNotificationsListItem);
	}

	public void setAccountNotificationsLinkActive() {
		deactivate(myAppsListItem);
		deactivate(linkedAccountsListItem);
		deactivate(accountSettingsListItem);
		activate(accountNotificationsListItem);
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
			setNotificationPageTypeLinkActive();
			break;
		case ChangePasswordPageType:
			setChangePasswordLinkActive();
			break;
		default:
			break;
		}
	}

	/**
	 * 
	 */
	private void setNotificationPageTypeLinkActive() {
		// TODO Auto-generated method stub

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

}
