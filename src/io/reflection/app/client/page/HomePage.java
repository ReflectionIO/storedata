//
//  HomePage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 13 Jan 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page;

import static io.reflection.app.client.controller.FilterController.OVERALL_LIST_TYPE;
import io.reflection.app.api.shared.datatypes.Session;
import io.reflection.app.client.DefaultEventBus;
import io.reflection.app.client.controller.FilterController;
import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.controller.NavigationController.Stack;
import io.reflection.app.client.controller.SessionController;
import io.reflection.app.client.handler.NavigationEventHandler;
import io.reflection.app.client.handler.user.SessionEventHandler;
import io.reflection.app.client.helper.DOMHelper;
import io.reflection.app.client.part.Footer;
import io.reflection.app.client.part.Header;
import io.reflection.app.datatypes.shared.User;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.LinkElement;
import com.google.gwt.dom.client.ScriptElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.Widget;
import com.willshex.gson.json.service.shared.Error;

/**
 * @author billy1380
 * 
 */
public class HomePage extends Page implements NavigationEventHandler, SessionEventHandler {

	private static HomePageUiBinder uiBinder = GWT.create(HomePageUiBinder.class);

	interface HomePageUiBinder extends UiBinder<Widget, HomePage> {}

	@UiField InlineHyperlink applyBtn;
	@UiField DivElement headerLinks;
	@UiField InlineHyperlink applyNowBtn;
	@UiField InlineHyperlink loginBtn;
	@UiField InlineHyperlink logoutBtn;
	@UiField InlineHyperlink leaderboardBtn;

	private final LinkElement cssCustom;
	private final LinkElement cssCustomIE8;
	private final LinkElement cssCustomIE9;

	private final ScriptElement scriptCustom;
	private final ScriptElement scriptHtml5Shiv;
	private final ScriptElement scriptRespond;
	private final ScriptElement scriptPictureFill;

	public HomePage() {

		initWidget(uiBinder.createAndBindUi(this));

		// Create scripts to append and remove in this page
		cssCustom = DOMHelper.getCssLinkFromUrl("css/landing.e124e6759d7ee59f4dcd012fab6c5e10.css");
		scriptCustom = DOMHelper.getJSScriptFromUrl("js/scripts.180cd4275030bac3e6c6f190e5c98813.js");
		// Conditional elements
		cssCustomIE8 = DOMHelper.getCssLinkFromUrl("css/landing-ie8.1a51cdbd334937176c269c0fe5935208.css");
		cssCustomIE9 = DOMHelper.getCssLinkFromUrl("css/landing-ie9.b88a72995eb11c8b63771dacdc057bc8.css");
		scriptHtml5Shiv = DOMHelper.getJSScriptFromUrl("js/html5shiv.min.js");
		scriptRespond = DOMHelper.getJSScriptFromUrl("js/respond.min.js");
		scriptPictureFill = DOMHelper.getJSScriptFromUrl("js/picturefill.2.2.0.min.js");
		scriptPictureFill.setAttribute("async", "");

		applyBtn.setTargetHistoryToken(PageType.RegisterPageType.asTargetHistoryToken("requestinvite"));
		applyNowBtn.setTargetHistoryToken(PageType.RegisterPageType.asTargetHistoryToken("requestinvite"));
		loginBtn.setTargetHistoryToken(PageType.LoginPageType.asTargetHistoryToken("requestinvite"));

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
		register(DefaultEventBus.get().addHandlerToSource(SessionEventHandler.TYPE, SessionController.get(), this));

		// Compatibility code
		Document.get().getElementsByTagName("html").getItem(0).setAttribute("style", "height: auto");
		Document.get().getBody().setAttribute("style", "height: auto");
		NavigationController.get().getPageHolderPanel().getElement().setAttribute("style", "padding: 0px 0px 0px 0px;");
		((Footer) NavigationController.get().getFooter()).setVisible(false);
		((Header) NavigationController.get().getHeader()).setVisible(false);

		DOMHelper.appendToHead(cssCustom);
		DOMHelper.appendToBody(scriptCustom);

		String userAgent = Window.Navigator.getUserAgent();
		if (userAgent.contains("MSIE")) { // Internet Explorer
			if (userAgent.contains("MSIE 2") || userAgent.contains("MSIE 3") || userAgent.contains("MSIE 4") || userAgent.contains("MSIE 5")
					|| userAgent.contains("MSIE 6") || userAgent.contains("MSIE 7") || userAgent.contains("MSIE 8")) {
				DOMHelper.appendToHead(cssCustomIE8);
				DOMHelper.appendToHead(scriptHtml5Shiv);
				DOMHelper.appendToHead(scriptRespond);
			} else {
				DOMHelper.appendToHead(scriptPictureFill);
			}
			if (userAgent.contains("MSIE 9")) {
				DOMHelper.appendToHead(cssCustomIE9);
			}
		} else { // Not Internet Explorer
			DOMHelper.appendToHead(scriptPictureFill);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.page.Page#onDetach()
	 */
	@Override
	protected void onDetach() {
		super.onDetach();

		// Compatibility code
		Document.get().getElementsByTagName("html").getItem(0).removeAttribute("style");
		Document.get().getBody().removeAttribute("style");
		NavigationController.get().getPageHolderPanel().getElement().setAttribute("style", "padding: 60px 0px 39px 0px;");
		((Footer) NavigationController.get().getFooter()).setVisible(true);
		((Header) NavigationController.get().getHeader()).setVisible(true);

		DOMHelper.removeFromHead(cssCustom);
		DOMHelper.removeFromBody(scriptCustom);

		String userAgent = Window.Navigator.getUserAgent();
		if (userAgent.contains("MSIE")) { // Internet Explorer
			if (userAgent.contains("MSIE 2") || userAgent.contains("MSIE 3") || userAgent.contains("MSIE 4") || userAgent.contains("MSIE 5")
					|| userAgent.contains("MSIE 6") || userAgent.contains("MSIE 7") || userAgent.contains("MSIE 8")) {
				DOMHelper.removeFromHead(cssCustomIE8);
				DOMHelper.removeFromHead(scriptHtml5Shiv);
				DOMHelper.removeFromHead(scriptRespond);
			} else {
				DOMHelper.removeFromHead(scriptPictureFill);
			}
			if (userAgent.contains("MSIE 9")) {
				DOMHelper.removeFromHead(cssCustomIE9);
			}
		} else { // Not Internet Explorer
			DOMHelper.removeFromHead(scriptPictureFill);
		}

	}

	private void setLoggedInHeader(boolean loggedIn) {
		if (loggedIn) {
			applyBtn.removeFromParent();
			loginBtn.removeFromParent();
			headerLinks.appendChild(leaderboardBtn.getElement());
			headerLinks.appendChild(logoutBtn.getElement());
		} else {
			leaderboardBtn.removeFromParent();
			logoutBtn.removeFromParent();
			headerLinks.appendChild(applyBtn.getElement());
			headerLinks.appendChild(loginBtn.getElement());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.NavigationEventHandler#navigationChanged(io.reflection.app.client.controller.NavigationController.Stack,
	 * io.reflection.app.client.controller.NavigationController.Stack)
	 */
	@Override
	public void navigationChanged(Stack previous, Stack current) {
		setLoggedInHeader(SessionController.get().isValidSession());
		leaderboardBtn.setTargetHistoryToken(PageType.RanksPageType.asTargetHistoryToken(NavigationController.VIEW_ACTION_PARAMETER_VALUE, OVERALL_LIST_TYPE,
				FilterController.get().asRankFilterString()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.user.SessionEventHandler#userLoggedIn(io.reflection.app.datatypes.shared.User,
	 * io.reflection.app.api.shared.datatypes.Session)
	 */
	@Override
	public void userLoggedIn(User user, Session session) {
		setLoggedInHeader(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.user.SessionEventHandler#userLoggedOut()
	 */
	@Override
	public void userLoggedOut() {
		setLoggedInHeader(false);
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

}
