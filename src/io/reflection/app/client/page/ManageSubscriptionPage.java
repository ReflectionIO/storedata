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
import io.reflection.app.client.popup.PremiumPopup;
import io.reflection.app.client.res.Styles;
import io.reflection.app.datatypes.shared.User;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Stefano Capuzzi (capuzzistefano)
 *
 */
public class ManageSubscriptionPage extends Page implements NavigationEventHandler {

	private static ManageSubscriptionPageUiBinder uiBinder = GWT.create(ManageSubscriptionPageUiBinder.class);

	interface ManageSubscriptionPageUiBinder extends UiBinder<Widget, ManageSubscriptionPage> {}

	@UiField Element currentStandard;
	@UiField Element currentPremium;
	@UiField Button signUpBtn;
	@UiField Button signUpPremium;
	private PremiumPopup premiumPopup = new PremiumPopup();

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

		if (!SessionController.get().isAdmin()) {
			usersText.setInnerHTML("Users <span class=\"text-small\">coming soon</span>");
			usersItem.addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().isDisabled());
			usersItem.getStyle().setCursor(Cursor.DEFAULT);
			notifText.setInnerHTML("Manage Notifications <span class=\"text-small\">coming soon</span>");
			notificationsItem.addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().isDisabled());
			notificationsItem.getStyle().setCursor(Cursor.DEFAULT);
			usersLink.setTargetHistoryToken(NavigationController.get().getStack().toString());
			notificationsLink.setTargetHistoryToken(NavigationController.get().getStack().toString());
		} else {
			if (user != null) {
				notificationsLink.setTargetHistoryToken(PageType.UsersPageType.asTargetHistoryToken(PageType.NotificationsPageType.toString(),
						user.id.toString()));
			}
		}

		if (user != null) {
			accountSettingsLink
					.setTargetHistoryToken(PageType.UsersPageType.asTargetHistoryToken(PageType.ChangeDetailsPageType.toString(), user.id.toString()));
		}

		// Add click event to LI element so the event is fired when clicking on the whole tab
		Event.sinkEvents(accountSettingsItem, Event.ONCLICK);
		Event.sinkEvents(manageSubscriptionItem, Event.ONCLICK);
		Event.sinkEvents(notificationsItem, Event.ONCLICK);
		Event.sinkEvents(usersItem, Event.ONCLICK);
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
		Event.setEventListener(notificationsItem, new EventListener() {

			@Override
			public void onBrowserEvent(Event event) {
				if (Event.ONCLICK == event.getTypeInt()) {
					History.newItem(notificationsLink.getTargetHistoryToken());
				}
			}
		});
		Event.setEventListener(usersItem, new EventListener() {

			@Override
			public void onBrowserEvent(Event event) {
				if (Event.ONCLICK == event.getTypeInt()) {
					History.newItem(usersLink.getTargetHistoryToken());
				}
			}
		});
	}

	private boolean isValidStack(Stack current) {
		return (current != null && PageType.UsersPageType.equals(current.getPage()) && current.getAction() != null
				&& PageType.ManageSubscriptionPageType.equals(current.getAction()) && current.getParameter(0) != null && (current.getParameter(0).equals(
				currentUser.id.toString()) || SessionController.get().isAdmin()));
	}

	@UiHandler("signUpPremium")
	void onSignUpPremiumClicked(ClickEvent event) {
		event.preventDefault();
		premiumPopup.show(false);
	}

	private void reset() {
		currentStandard.removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().pricingOptionCurrent());
		signUpBtn.getElement().removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().refButtonPositive());
		signUpBtn.setText("Sign Up for Free Now");
		currentPremium.removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().pricingOptionCurrent());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.NavigationEventHandler#navigationChanged(io.reflection.app.client.controller.NavigationController.Stack,
	 * io.reflection.app.client.controller.NavigationController.Stack)
	 */
	@Override
	public void navigationChanged(Stack previous, Stack current) {
		currentUser = SessionController.get().getLoggedInUser(); // Update user using the system
		if (isValidStack(current)) {
			manageSubscriptionLink.setTargetHistoryToken(current.toString());

			if (SessionController.get().isPremiumDeveloper()) {
				currentPremium.addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().pricingOptionCurrent());
			} else { // Standard developer
				currentStandard.addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().pricingOptionCurrent());
				signUpBtn.getElement().addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().refButtonPositive());
			}
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
		premiumPopup.hide();
	}

}
