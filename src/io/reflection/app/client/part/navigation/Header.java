//
//  Header.java
//  storedata
//
//  Created by Stefano Capuzzi on 25 Feb 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.part.navigation;

import io.reflection.app.api.shared.datatypes.Session;
import io.reflection.app.client.DefaultEventBus;
import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.controller.NavigationController.Stack;
import io.reflection.app.client.controller.SessionController;
import io.reflection.app.client.handler.NavigationEventHandler;
import io.reflection.app.client.handler.TogglePanelEventHandler;
import io.reflection.app.client.handler.user.SessionEventHandler;
import io.reflection.app.client.handler.user.UserPowersEventHandler;
import io.reflection.app.client.helper.DOMHelper;
import io.reflection.app.client.page.PageType;
import io.reflection.app.client.res.Styles;
import io.reflection.app.client.res.Styles.ReflectionMainStyles;
import io.reflection.app.datatypes.shared.Permission;
import io.reflection.app.datatypes.shared.Role;
import io.reflection.app.datatypes.shared.User;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.Widget;
import com.willshex.gson.json.service.shared.Error;

/**
 * @author Stefano Capuzzi
 *
 */
public class Header extends Composite implements NavigationEventHandler, SessionEventHandler, UserPowersEventHandler {

	private static HeaderUiBinder uiBinder = GWT.create(HeaderUiBinder.class);

	interface HeaderUiBinder extends UiBinder<Widget, Header> {}

	private ReflectionMainStyles style = Styles.STYLES_INSTANCE.reflectionMainStyle();

	public enum PanelType {
		PanelLeftMenuType,
		PanelRightAccountType,
		PanelRightSearchType
	}

	@UiField HTMLPanel hamburgerPanel;
	@UiField Button hamburgerBtn;
	@UiField InlineHyperlink applyBtn;
	@UiField HTMLPanel actionsGroup;
	@UiField Anchor linkLogin;
	@UiField Anchor linkSearch;

	@UiField DivElement tempSearchContainer;

	private boolean panelLeftWasClosed; // Remember if the user closed the panel

	public Header() {
		initWidget(uiBinder.createAndBindUi(this));

		setLoggedIn(false);

		applyBtn.setTargetHistoryToken(PageType.RegisterPageType.asTargetHistoryToken());

		initPanelLeftMenu();
		initPanelsRight();

		tempSearchContainer.removeFromParent();
	}

	private void setLoggedIn(boolean loggedIn) {
		applyBtn.setVisible(!loggedIn);
		linkLogin.setText(loggedIn ? "Settings" : "Log In");
	}

	private void initPanelLeftMenu() {
		// Close left panel on touching the mobile screen
		NavigationController.get().getMainPanel().sinkEvents(Event.ONCLICK);
		NavigationController.get().getMainPanel().addHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (DOMHelper.getHtmlElement().hasClassName("touch") && isPanelLeftMenuOpen() && Window.getClientWidth() < 940) {
					hamburgerBtn.click();
				}
			}
		}, ClickEvent.getType());
	}

	private void initPanelsRight() {

		// Toggle panel
		actionsGroup.sinkEvents(Event.ONCLICK);
		actionsGroup.addHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				closePanelRightSearch();
				if (NavigationController.get().getPanelRightAccount().getElement().hasClassName(style.isShowing())) {
					closePanelRightAccount();
				} else {
					openPanelRightAccount();
				}
			}
		}, ClickEvent.getType());

		// Close panel if clicking outside of it
		Event.sinkEvents(NavigationController.get().getPanelRightAccount().getPanelOverlay(), Event.ONCLICK);
		Event.setEventListener(NavigationController.get().getPanelRightAccount().getPanelOverlay(), new EventListener() {

			@Override
			public void onBrowserEvent(Event event) {
				if (Event.ONCLICK == event.getTypeInt()) {
					closePanelRightAccount();
				}
			}
		});

		Event.sinkEvents(NavigationController.get().getPanelRightSearch().getPanelOverlay(), Event.ONCLICK);
		Event.setEventListener(NavigationController.get().getPanelRightSearch().getPanelOverlay(), new EventListener() {

			@Override
			public void onBrowserEvent(Event event) {
				if (Event.ONCLICK == event.getTypeInt()) {
					closePanelRightSearch();
				}
			}
		});

		if (Document.get().getBody().hasClassName("ie10") && !DOMHelper.isScrollEnabled()) {
			closePanelRightAccount();
			closePanelRightSearch();
		}

	}

	public void openPanelRightAccount() {
		NavigationController.get().getPanelRightAccount().getElement().addClassName(style.isShowing());
		DOMHelper.setScrollEnabled(false);
		actionsGroup.getElement().addClassName(style.isOn());
		if (!linkLogin.getElement().hasClassName(style.isSelected())) {
			linkLogin.getElement().addClassName(style.isSelected());
		}
	}

	public void closePanelRightAccount() {
		NavigationController.get().getPanelRightAccount().getElement().removeClassName(style.isShowing());
		DOMHelper.setScrollEnabled(true);
		actionsGroup.getElement().removeClassName(style.isOn());
		if (linkLogin.getElement().hasClassName(style.isSelected())) {
			linkLogin.getElement().removeClassName(style.isSelected());
			linkLogin.setFocus(false);
		}
	}

	public void openPanelRightSearch() {
		NavigationController.get().getPanelRightSearch().getElement().addClassName(style.isShowing());
		DOMHelper.setScrollEnabled(false);
		if (!linkSearch.getElement().hasClassName(style.isSelected())) {
			linkSearch.getElement().addClassName(style.isSelected());
		}
	}

	public void closePanelRightSearch() {
		NavigationController.get().getPanelRightSearch().getElement().removeClassName(style.isShowing());
		DOMHelper.setScrollEnabled(true);
		if (linkSearch.getElement().hasClassName(style.isSelected())) {
			linkSearch.getElement().removeClassName(style.isSelected());
			linkSearch.setFocus(false);
		}
	}

	public boolean isPanelLeftMenuOpen() {
		return Document.get().getBody().hasClassName(style.panelLeftOpen());
	}

	@UiHandler("hamburgerBtn")
	void onHamburgerClicked(ClickEvent event) {
		if (!isPanelLeftMenuOpen()) {
			closePanelRightAccount();
			closePanelRightSearch();
			Document.get().getBody().addClassName(style.panelLeftOpen());
			panelLeftWasClosed = false;
		} else {
			Document.get().getBody().removeClassName(style.panelLeftOpen());
			panelLeftWasClosed = true;
		}
		DOMHelper.toggleClassName(hamburgerBtn.getElement(), style.isSelected());
		DefaultEventBus.get().fireEventFromSource(
				new TogglePanelEventHandler.ChangedEvent(PanelType.PanelLeftMenuType, !panelLeftWasClosed, isPanelLeftMenuOpen()), this);
	}

	@UiHandler("applyBtn")
	void onApplyClicked(ClickEvent event) {
		closePanelRightAccount();
		closePanelRightSearch();
	}

	@UiHandler("linkSearch")
	void onLinkSearchClicked(ClickEvent event) {
		event.preventDefault();
		closePanelRightAccount();
		DOMHelper.toggleClassName(linkSearch.getElement(), style.isSelected());
		if (NavigationController.get().getPanelRightSearch().getElement().hasClassName(style.isShowing())) {
			closePanelRightSearch();
		} else {
			openPanelRightSearch();
		}
	}

	@UiHandler("linkLogin")
	void onLinkLogInClicked(ClickEvent event) {
		event.preventDefault();
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
		PageType currentPage = NavigationController.get().getCurrentPage();
		if (Window.getClientWidth() > 960 && !panelLeftWasClosed && !PageType.LoadingPageType.equals(currentPage)) {
			Document.get().getBody().addClassName(style.panelLeftOpen());
			hamburgerBtn.getElement().addClassName(style.isSelected());
		} else {
			Document.get().getBody().removeClassName(style.panelLeftOpen());
			hamburgerBtn.getElement().removeClassName(style.isSelected());
		}
		DefaultEventBus.get().fireEventFromSource(
				new TogglePanelEventHandler.ChangedEvent(PanelType.PanelLeftMenuType, !panelLeftWasClosed, isPanelLeftMenuOpen()), this);

		closePanelRightAccount();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.user.SessionEventHandler#userLoggedIn(io.reflection.app.datatypes.shared.User,
	 * io.reflection.app.api.shared.datatypes.Session)
	 */
	@Override
	public void userLoggedIn(User user, Session session) {
		if (!SessionController.get().isSessionRestored()) {
			NavigationController.get().showNext();
		}
	}

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
	 * @see io.reflection.app.client.handler.user.UserPowersEventHandler#gotUserPowers(io.reflection.app.datatypes.shared.User, java.util.List, java.util.List)
	 */
	@Override
	public void gotUserPowers(User user, List<Role> roles, List<Permission> permissions, Integer daysSinceRoleAssigned) {
		setLoggedIn(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.user.UserPowersEventHandler#getGetUserPowersFailed(com.willshex.gson.json.service.shared.Error)
	 */
	@Override
	public void getGetUserPowersFailed(Error error) {}

}
