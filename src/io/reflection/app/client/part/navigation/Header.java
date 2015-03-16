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
import io.reflection.app.client.handler.user.SessionEventHandler;
import io.reflection.app.client.handler.user.UserPowersEventHandler;
import io.reflection.app.client.helper.DOMHelper;
import io.reflection.app.client.helper.UserAgentHelper;
import io.reflection.app.client.page.PageType;
import io.reflection.app.client.res.Styles;
import io.reflection.app.client.res.Styles.ReflectionMainStyles;
import io.reflection.app.datatypes.shared.Permission;
import io.reflection.app.datatypes.shared.Role;
import io.reflection.app.datatypes.shared.User;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.VideoElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.DOM;
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

	@UiField HTMLPanel hamburgerPanel;
	@UiField Button hamburgerBtn;
	@UiField AnchorElement homeBtn;
	@UiField DivElement actionsPanel;
	@UiField InlineHyperlink applyBtn;
	@UiField HTMLPanel actionsGroupPanel;
	@UiField DivElement actionsGroupContent;
	@UiField Anchor linkLogin;
	@UiField Anchor linkSearch;

	private boolean panelLeftWasClosed;

	Element picture, source1, source2;

	public Header() {
		initWidget(uiBinder.createAndBindUi(this));

		setLoggedIn(false);

		applyBtn.setTargetHistoryToken(PageType.RegisterPageType.asTargetHistoryToken("requestinvite"));
		addHomeBtnPicture();
		appendConditionalTags();

		initPanelLeftMenu();
		initPanelRightAccount();
		initPanelRightSearch();

	}

	private void addHomeBtnPicture() {
		picture = DOM.createElement("picture");

		source1 = DOM.createElement("source");
		source1.setAttribute("srcset", "images/logo-reflection-header.png");
		source1.setAttribute("media", "(min-width: 720px)");

		source2 = DOM.createElement("source");
		source2.setAttribute("srcset", "images/logo-reflection-header-mobile.png");

		Element img = DOM.createImg();
		img.setAttribute("src", "images/logo-reflection-header.png");
		img.setAttribute("alt", "Reflection logo");

		picture.appendChild(source1);
		picture.appendChild(source2);
		picture.appendChild(img);

		homeBtn.appendChild(picture);
	}

	private void appendConditionalTags() {
		if (UserAgentHelper.isIE() && UserAgentHelper.getIEVersion() == 9) {
			picture.removeChild(source1);
			picture.removeChild(source2);
			VideoElement video = Document.get().createVideoElement();
			video.setAttribute("style", "display: none;");
			picture.insertFirst(video);
			video.appendChild(source1);
			video.appendChild(source2);
		}
	}

	private void setLoggedIn(boolean loggedIn) {
		if (loggedIn) {
			applyBtn.removeFromParent();
			linkLogin.setText("My Account");
		} else {
			if (!actionsPanel.isOrHasChild(applyBtn.getElement())) {
				actionsPanel.insertFirst(applyBtn.getElement());
			}
			linkLogin.setText("Log In");
		}
	}

	private void initPanelLeftMenu() {
		// Close left panel on touching the screen and right panels on clicking
		NavigationController.get().getMainPanel().sinkEvents(Event.ONCLICK);
		NavigationController.get().getMainPanel().addHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (isPanelLeftMenuOpen()) {
					if (DOMHelper.getHtmlElement().hasClassName(style.touch()) && Window.getClientWidth() < 940) {
						hamburgerBtn.click();
					}
				}
			}
		}, ClickEvent.getType());
	}

	private void initPanelRightAccount() {
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
	}

	private void initPanelRightSearch() {
		// Close panel if clicking outside of it
		Event.sinkEvents(NavigationController.get().getPanelRightSearch().getPanelOverlay(), Event.ONCLICK);
		Event.setEventListener(NavigationController.get().getPanelRightSearch().getPanelOverlay(), new EventListener() {

			@Override
			public void onBrowserEvent(Event event) {
				if (Event.ONCLICK == event.getTypeInt()) {
					closePanelRightSearch();
				}
			}
		});
	}

	public void closePanelRightAccount() {
		NavigationController.get().getPanelRightAccount().getElement().removeClassName(style.isShowing());
		if (linkLogin.getElement().hasClassName(style.isSelected())) {
			linkLogin.getElement().removeClassName(style.isSelected());
			linkLogin.setFocus(false);
		}
		DOMHelper.setScrollEnabled(true);
	}

	public void closePanelRightSearch() {
		NavigationController.get().getPanelRightSearch().getElement().removeClassName(style.isShowing());
		if (linkSearch.getElement().hasClassName(style.isSelected())) {
			linkSearch.getElement().removeClassName(style.isSelected());
			linkSearch.setFocus(false);
		}
		DOMHelper.setScrollEnabled(true);
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
		UserAgentHelper.setMainContentWidthForIE();
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
		if (!NavigationController.get().getPanelRightSearch().getElement().hasClassName(style.isShowing())) {
			NavigationController.get().getPanelRightSearch().getElement().addClassName(style.isShowing());
			DOMHelper.setScrollEnabled(false);
		} else {
			NavigationController.get().getPanelRightSearch().getElement().removeClassName(style.isShowing());
			DOMHelper.setScrollEnabled(true);
		}
	}

	@UiHandler("linkLogin")
	void onLinkLogInClicked(ClickEvent event) {
		event.preventDefault();
		closePanelRightSearch();
		if (!linkLogin.getElement().hasClassName(style.isSelected())) {
			linkLogin.getElement().addClassName(style.isSelected());
		} else {
			linkLogin.getElement().removeClassName(style.isSelected());
			linkLogin.setFocus(false);
		}
		if (!NavigationController.get().getPanelRightAccount().getElement().hasClassName(style.isShowing())) {
			NavigationController.get().getPanelRightAccount().getElement().addClassName(style.isShowing());
			DOMHelper.setScrollEnabled(false);
		} else {
			NavigationController.get().getPanelRightAccount().getElement().removeClassName(style.isShowing());
			DOMHelper.setScrollEnabled(true);
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
		if (Window.getClientWidth() > 960
				&& !panelLeftWasClosed
				&& currentPage != null
				&& !PageType.LinkItunesPageType.equals(currentPage)
				&& (currentPage.requiresLogin() || PageType.BlogPostsPageType.equals(currentPage) || PageType.BlogPostPageType.equals(currentPage)
						|| PageType.BlogTagPageType.equals(currentPage) || PageType.ForumEditTopicPageType.equals(currentPage)
						|| PageType.ForumPageType.equals(currentPage) || PageType.ForumThreadPageType.equals(currentPage) || PageType.ForumTopicPageType
							.equals(currentPage))) {
			if (!isPanelLeftMenuOpen()) {
				Document.get().getBody().addClassName(style.panelLeftOpen());
			}
			if (!hamburgerBtn.getElement().hasClassName(style.isSelected())) {
				hamburgerBtn.getElement().addClassName(style.isSelected());
			}
		} else {
			Document.get().getBody().removeClassName(style.panelLeftOpen());
			hamburgerBtn.getElement().removeClassName(style.isSelected());
		}

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
	public void gotUserPowers(User user, List<Role> roles, List<Permission> permissions) {
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
