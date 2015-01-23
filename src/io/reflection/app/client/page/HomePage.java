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
import io.reflection.app.client.res.Styles;
import io.reflection.app.datatypes.shared.User;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.LinkElement;
import com.google.gwt.dom.client.ScriptElement;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
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

	@UiField DivElement headerLinks;
	@UiField InlineHyperlink applyNowBtn;
	@UiField InlineHyperlink applyBtn;
	@UiField InlineHyperlink loginBtn;
	@UiField InlineHyperlink logoutBtn;
	@UiField InlineHyperlink leaderboardBtn;

	public static final LinkElement cssCustomIE8 = DOMHelper.getCssLinkFromUrl("css/landing-ie8.1a51cdbd334937176c269c0fe5935208.css");
	public static final LinkElement cssCustomIE9 = DOMHelper.getCssLinkFromUrl("css/landing-ie9.b88a72995eb11c8b63771dacdc057bc8.css");

	private final ScriptElement scriptCustom = DOMHelper.getJSScriptFromUrl("js/scripts.180cd4275030bac3e6c6f190e5c98813.js");
	public static final ScriptElement scriptHtml5Shiv = DOMHelper.getJSScriptFromUrl("js/html5shiv.min.js");
	public static final ScriptElement scriptRespond = DOMHelper.getJSScriptFromUrl("js/respond.min.js");
	public static final ScriptElement scriptPictureFill = DOMHelper.getJSScriptFromUrl("js/picturefill.2.2.0.min.js", "async");

	public HomePage() {

		Styles.INSTANCE.homePageStyle().ensureInjected();

		initWidget(uiBinder.createAndBindUi(this));

		headerLinks.removeAllChildren();

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

		headerLinks.getStyle().setDisplay(Display.INLINE_BLOCK);

		Document.get().getBody().setAttribute("style", "height: auto");
		Document.get().getBody().appendChild(scriptCustom);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.page.Page#onDetach()
	 */
	@Override
	protected void onDetach() {
		super.onDetach();

		Document.get().getBody().removeAttribute("style");
		Document.get().getBody().removeChild(scriptCustom);
	}

	private void setLoggedInHeader(boolean loggedIn) {
		headerLinks.removeAllChildren();
		if (loggedIn) {
			headerLinks.appendChild(leaderboardBtn.getElement());
			headerLinks.appendChild(logoutBtn.getElement());
		} else {
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
