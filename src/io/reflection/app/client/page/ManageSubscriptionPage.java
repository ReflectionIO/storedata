//
//  ManageSubscriptionPage.java
//  storedata
//
//  Created by Stefano Capuzzi (capuzzistefano) on 24 Sep 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page;

import io.reflection.app.client.DefaultEventBus;
import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.controller.NavigationController.Stack;
import io.reflection.app.client.controller.SessionController;
import io.reflection.app.client.handler.NavigationEventHandler;
import io.reflection.app.client.handler.user.UserPowersEventHandler;
import io.reflection.app.client.helper.TooltipHelper;
import io.reflection.app.client.popup.AddLinkedAccountPopup;
import io.reflection.app.client.popup.PremiumPopup;
import io.reflection.app.client.popup.RegisterInterestPopup;
import io.reflection.app.client.res.Styles;
import io.reflection.app.datatypes.shared.Permission;
import io.reflection.app.datatypes.shared.Role;
import io.reflection.app.datatypes.shared.User;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.Widget;
import com.willshex.gson.json.service.shared.Error;

/**
 * @author Stefano Capuzzi (capuzzistefano)
 *
 */
public class ManageSubscriptionPage extends Page implements NavigationEventHandler, UserPowersEventHandler {

	private static ManageSubscriptionPageUiBinder uiBinder = GWT.create(ManageSubscriptionPageUiBinder.class);

	interface ManageSubscriptionPageUiBinder extends UiBinder<Widget, ManageSubscriptionPage> {}

	@UiField Element standardPanel;
	@UiField HTMLPanel standardInfo;
	@UiField Element premiumPanel;
	@UiField HTMLPanel premiumInfoUpgrade;
	@UiField HTMLPanel premiumInfoPlain;
	@UiField Button linkAccountBtn;
	private AddLinkedAccountPopup addLinkedAccountPopup = new AddLinkedAccountPopup();
	@UiField HTMLPanel currentSubscriptionStandard;
	@UiField HTMLPanel currentSubscriptionPremium;
	@UiField HTMLPanel premiumTrialLabel;
	@UiField Button signUpPremium;
	private PremiumPopup premiumPopup = new PremiumPopup();
	@UiField Button registerInterest;
	private RegisterInterestPopup registerInterestPopup = new RegisterInterestPopup();
	@UiField AnchorElement comingSoon;

	@UiField LIElement accountSettingsItem;
	@UiField LIElement manageSubscriptionItem;
	@UiField LIElement usersItem;
	@UiField LIElement notificationsItem;
	@UiField SpanElement usersText;
	@UiField SpanElement notifText;

	@UiField InlineHyperlink accountSettingsLink;
	@UiField InlineHyperlink manageSubscriptionLink;
	@UiField InlineHyperlink usersLink;
	@UiField InlineHyperlink notificationsLink;

	private User currentUser; // User using the system

	public ManageSubscriptionPage() {
		initWidget(uiBinder.createAndBindUi(this));

		User user = SessionController.get().getLoggedInUser();

		usersItem.removeFromParent();
		notificationsItem.removeFromParent();
		// if (!SessionController.get().isAdmin()) {
		// usersText.setInnerHTML("Users <span class=\"text-small\">coming soon</span>");
		// usersItem.addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().isDisabled());
		// usersItem.getStyle().setCursor(Cursor.DEFAULT);
		// notifText.setInnerHTML("Manage Notifications <span class=\"text-small\">coming soon</span>");
		// notificationsItem.addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().isDisabled());
		// notificationsItem.getStyle().setCursor(Cursor.DEFAULT);
		// usersLink.setTargetHistoryToken(NavigationController.get().getStack().toString());
		// notificationsLink.setTargetHistoryToken(NavigationController.get().getStack().toString());
		// } else {
		// if (user != null) {
		// notificationsLink.setTargetHistoryToken(PageType.UsersPageType.asTargetHistoryToken(PageType.NotificationsPageType.toString(),
		// user.id.toString()));
		// }
		// }

		if (user != null) {
			accountSettingsLink
					.setTargetHistoryToken(PageType.UsersPageType.asTargetHistoryToken(PageType.ChangeDetailsPageType.toString(), user.id.toString()));
		}

		// Add click event to LI element so the event is fired when clicking on the whole tab
		Event.sinkEvents(accountSettingsItem, Event.ONCLICK);
		Event.sinkEvents(manageSubscriptionItem, Event.ONCLICK);
		// Event.sinkEvents(notificationsItem, Event.ONCLICK);
		// Event.sinkEvents(usersItem, Event.ONCLICK);
		Event.setEventListener(accountSettingsItem, new EventListener() {

			@Override
			public void onBrowserEvent(Event event) {
				if (Event.ONCLICK == event.getTypeInt()) {
					History.newItem(accountSettingsLink.getTargetHistoryToken());
				}
			}
		});
		Event.setEventListener(manageSubscriptionItem, new EventListener() {

			@Override
			public void onBrowserEvent(Event event) {
				if (Event.ONCLICK == event.getTypeInt()) {
					History.newItem(manageSubscriptionLink.getTargetHistoryToken());
				}
			}
		});
		// Event.setEventListener(notificationsItem, new EventListener() {
		//
		// @Override
		// public void onBrowserEvent(Event event) {
		// if (Event.ONCLICK == event.getTypeInt()) {
		// History.newItem(notificationsLink.getTargetHistoryToken());
		// }
		// }
		// });
		// Event.setEventListener(usersItem, new EventListener() {
		//
		// @Override
		// public void onBrowserEvent(Event event) {
		// if (Event.ONCLICK == event.getTypeInt()) {
		// History.newItem(usersLink.getTargetHistoryToken());
		// }
		// }
		// });

		Event.sinkEvents(comingSoon, Event.ONCLICK);
		Event.setEventListener(comingSoon, new EventListener() {

			@Override
			public void onBrowserEvent(Event event) {
				event.preventDefault();
			}
		});
		TooltipHelper.updateHelperTooltip();
	}

	private boolean isValidStack(Stack current) {
		return (current != null && PageType.UsersPageType.equals(current.getPage()) && current.getAction() != null
				&& PageType.ManageSubscriptionPageType.equals(current.getAction()) && current.getParameter(0) != null && (current.getParameter(0).equals(
				currentUser.id.toString()) || SessionController.get().isAdmin()));
	}

	@UiHandler("linkAccountBtn")
	void onAddLinkedAccountClicked(ClickEvent event) {
		event.preventDefault();
		addLinkedAccountPopup.show("Link Your Appstore Account", "You need to link your iTunes Connect account, it only takes a moment");
	}

	@UiHandler("signUpPremium")
	void onSignUpPremiumClicked(ClickEvent event) {
		event.preventDefault();
		premiumPopup.show(false);
	}

	@UiHandler("registerInterest")
	void onRegsiterInterestClicked(ClickEvent event) {
		event.preventDefault();
		registerInterestPopup.show();
	}

	private void reset() {
		standardPanel.removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().pricingOptionCurrent());
		linkAccountBtn.setVisible(false);
		standardInfo.setVisible(false);
		premiumPanel.removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().pricingOptionCurrent());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.NavigationEventHandler#navigationChanged(io.reflection.app.client.controller.NavigationController.Stack,
	 * io.reflection.app.client.controller.NavigationController.Stack)
	 */
	@Override
	public void navigationChanged(Stack previous, Stack current) { // TODO update page after upgrading or linking the first account as well
		currentUser = SessionController.get().getLoggedInUser();
		if (isValidStack(current)) {
			manageSubscriptionLink.setTargetHistoryToken(current.toString());
			setPricingOption();
		}
	}

	private void setPricingOption() {
		if (!SessionController.get().isAdmin() && !SessionController.get().hasLinkedAccount()) { // NO LINKED ACCOUNT STATE
			setWithLinkedAccount(false);
			currentSubscriptionStandard.setVisible(false);
			premiumInfoPlain.setVisible(true);
			premiumTrialLabel.setVisible(true);
			premiumInfoUpgrade.setVisible(false);
			premiumPanel.addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().pricingOptionWithFooter());
			currentSubscriptionPremium.setVisible(false);
			signUpPremium.setVisible(false);
		} else {
			setWithLinkedAccount(true);
			currentSubscriptionStandard.setVisible(true);
			if (SessionController.get().isPremiumDeveloper()) { // PREMIUM DEV
				premiumInfoPlain.setVisible(false);
				premiumTrialLabel.setVisible(false);
				premiumInfoUpgrade.setVisible(false);
				premiumPanel.removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().pricingOptionWithFooter());
				currentSubscriptionPremium.setVisible(true);
				signUpPremium.setVisible(false);
			} else { // STANDARD DEV
				premiumInfoPlain.setVisible(false);
				premiumTrialLabel.setVisible(false);
				premiumInfoUpgrade.setVisible(true);
				premiumPanel.addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().pricingOptionWithFooter());
				currentSubscriptionPremium.setVisible(false);
				signUpPremium.setVisible(true);
			}
		}
	}

	private void setWithLinkedAccount(boolean hasLinkedAccount) {
		linkAccountBtn.setVisible(!hasLinkedAccount);
		standardInfo.setVisible(!hasLinkedAccount);
		if (hasLinkedAccount) {
			standardPanel.removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().pricingOptionWithFooter());
		} else {
			standardPanel.addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().pricingOptionWithFooter());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.page.Page#onAttach()
	 */
	@Override
	protected void onAttach() {
		super.onAttach();

		register(DefaultEventBus.get().addHandlerToSource(NavigationEventHandler.TYPE, NavigationController.get(), this));
		register(DefaultEventBus.get().addHandlerToSource(UserPowersEventHandler.TYPE, SessionController.get(), this));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.page.Page#onDetach()
	 */
	@Override
	protected void onDetach() {
		super.onDetach();

		reset();
		addLinkedAccountPopup.hide();
		premiumPopup.hide();
		registerInterestPopup.hide();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.user.UserPowersEventHandler#gotUserPowers(io.reflection.app.datatypes.shared.User, java.util.List, java.util.List,
	 * java.lang.Integer)
	 */
	@Override
	public void gotUserPowers(User user, List<Role> roles, List<Permission> permissions, Integer daysSinceRoleAssigned) {
		setPricingOption();
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
