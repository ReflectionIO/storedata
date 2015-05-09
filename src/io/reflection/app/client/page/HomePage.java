//
//  HomePage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 13 Jan 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page;

import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.helper.DOMHelper;
import io.reflection.app.client.part.Footer;
import io.reflection.app.client.part.Header;
import io.reflection.app.client.res.Images;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.LinkElement;
import com.google.gwt.dom.client.ParagraphElement;
import com.google.gwt.dom.client.ScriptElement;
import com.google.gwt.dom.client.VideoElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author billy1380
 * 
 */
public class HomePage extends Page {

	private static HomePageUiBinder uiBinder = GWT.create(HomePageUiBinder.class);

	interface HomePageUiBinder extends UiBinder<Widget, HomePage> {}

	@UiField InlineHyperlink applyNowBtn;
	@UiField InlineHyperlink applyBtn;
	@UiField InlineHyperlink loginBtn;

	@UiField InlineHyperlink homeBtn;
	@UiField HTMLPanel leaderBoardScreenshot;
	@UiField HTMLPanel analysisScreenshot;

	// elements for IE consitionals
	Element picture1, source1, source2;
	Element picture2, source3, source4;
	Element picture3, source5, source6;

	private static final LinkElement cssCustom = DOMHelper.getCssLinkFromUrl("css/landing.62eddb2b4c6320dc1a0039800cee0bf0.css");

	private static final LinkElement cssCustomIE8 = DOMHelper.getCssLinkFromUrl("css/landing-ie8.659484547a4409f243a1bb7d085c2c52.css");
	private static final LinkElement cssCustomIE9 = DOMHelper.getCssLinkFromUrl("css/landing-ie9.42a5a45361f9f95fe006be82a699c864.css");

	private static final ScriptElement scriptCustom = DOMHelper.getJSScriptFromUrl("js/scripts.180cd4275030bac3e6c6f190e5c98813.js");
	private static final ScriptElement scriptRespond = DOMHelper.getJSScriptFromUrl("js/respond.min.js");

	private static final ScriptElement scriptPictureFill = DOMHelper.getJSScriptFromUrl("js/picturefill.2.2.0.min.js", "async");

	private int toTop = 0;
	private static boolean tweeked = false;

	public HomePage() {
		initWidget(uiBinder.createAndBindUi(this));

		addHomeBtnPicture();
		addLeaderboardScreenshotPicture();
		addAnalysisScreenshotPicture();

		// StyleInjector.injectAtStart(Styles.INSTANCE.homePageStyle().getText());

		applyBtn.setTargetHistoryToken(PageType.RegisterPageType.asTargetHistoryToken("requestinvite"));
		applyNowBtn.setTargetHistoryToken(PageType.RegisterPageType.asTargetHistoryToken("requestinvite"));
		loginBtn.setTargetHistoryToken(PageType.LoginPageType.asTargetHistoryToken("requestinvite"));

		appendConditionalTags();
	}

	public static void applyHomePageTweeks() {
		if (!tweeked) {
			// Compatibility code
			Document.get().getElementsByTagName("html").getItem(0).setAttribute("style", "height: auto");
			NavigationController.get().getPageHolderPanel().getElement().setAttribute("style", "padding: 0px 0px 0px 0px;");
			Document.get().getBody().setAttribute("style", "height: auto");

			// Append to Head
			Document.get().getHead().appendChild(HomePage.cssCustom);
			String userAgent = Window.Navigator.getUserAgent();
			if (userAgent.contains("MSIE")) { // Internet Explorer
				if (userAgent.contains("MSIE 2") || userAgent.contains("MSIE 3") || userAgent.contains("MSIE 4") || userAgent.contains("MSIE 5")
						|| userAgent.contains("MSIE 6") || userAgent.contains("MSIE 7") || userAgent.contains("MSIE 8")) {
					Document.get().getHead().appendChild(HomePage.cssCustomIE8);
					Document.get().getHead().appendChild(HomePage.scriptRespond);
				} else {
					Document.get().getHead().appendChild(HomePage.scriptPictureFill);
				}
				if (userAgent.contains("MSIE 9")) {
					Document.get().getHead().appendChild(HomePage.cssCustomIE9);
				}
			} else { // Not Internet Explorer
				Document.get().getHead().appendChild(HomePage.scriptPictureFill);
			}

			tweeked = true;
		}
	}

	public static void removeHomePageTweeks() {
		if (tweeked) {
			// Compatibility code
			Document.get().getElementsByTagName("html").getItem(0).removeAttribute("style");
			NavigationController.get().getPageHolderPanel().getElement().setAttribute("style", "padding: 60px 0px 39px 0px;");
			Document.get().getBody().removeAttribute("style");

			// Remove from Head
			Document.get().getHead().removeChild(HomePage.cssCustom);
			String userAgent = Window.Navigator.getUserAgent();
			if (userAgent.contains("MSIE")) { // Internet Explorer
				if (userAgent.contains("MSIE 2") || userAgent.contains("MSIE 3") || userAgent.contains("MSIE 4") || userAgent.contains("MSIE 5")
						|| userAgent.contains("MSIE 6") || userAgent.contains("MSIE 7") || userAgent.contains("MSIE 8")) {
					Document.get().getHead().removeChild(HomePage.cssCustomIE8);
					Document.get().getHead().removeChild(HomePage.scriptRespond);
				} else {
					Document.get().getHead().removeChild(HomePage.scriptPictureFill);
				}
				if (userAgent.contains("MSIE 9")) {
					Document.get().getHead().removeChild(HomePage.cssCustomIE9);
				}
			} else { // Not Internet Explorer
				Document.get().getHead().removeChild(HomePage.scriptPictureFill);
			}

			tweeked = false;
		}
	}

	private void addHomeBtnPicture() {
		picture1 = DOM.createElement("picture");

		source1 = DOM.createElement("source");
		source1.setAttribute("srcset", Images.INSTANCE.reflectionLogoBeta().getSafeUri().asString());
		source1.setAttribute("media", "(min-width: 480px)");

		source2 = DOM.createElement("source");
		source2.setAttribute("srcset", Images.INSTANCE.mobileReflectionLogoBeta().getSafeUri().asString());

		Image img = new Image(Images.INSTANCE.reflectionLogoBeta());
		img.setAltText("Reflection logo");

		picture1.appendChild(source1);
		picture1.appendChild(source2);
		picture1.appendChild(img.getElement());

		homeBtn.getElement().appendChild(picture1);
	}

	private void addLeaderboardScreenshotPicture() {
		picture2 = DOM.createElement("picture");

		source3 = DOM.createElement("source");
		source3.setAttribute("srcset", "images/screenshots/Leaderboard_Revenue.png");
		source3.setAttribute("media", "(min-width: 720px)");

		source4 = DOM.createElement("source");
		source4.setAttribute("srcset", "images/screenshots/Leaderboard_Revenue_Mobile.png");

		Image img = new Image("images/screenshots/Leaderboard_Revenue.png");
		img.setAltText("Leaderboard screenshot");

		picture2.appendChild(source3);
		picture2.appendChild(source4);
		picture2.appendChild(img.getElement());

		leaderBoardScreenshot.getElement().appendChild(picture2);
	}

	private void addAnalysisScreenshotPicture() {
		picture3 = DOM.createElement("picture");

		source5 = DOM.createElement("source");
		source5.setAttribute("srcset", "images/screenshots/App_Page-v2.png");
		source5.setAttribute("media", "(min-width: 720px)");

		source6 = DOM.createElement("source");
		source6.setAttribute("srcset", "images/screenshots/App_Page_Mobile.png");

		Image img = new Image("images/screenshots/App_Page-v2.png");
		img.setAltText("Analysis screenshot");

		picture3.appendChild(source5);
		picture3.appendChild(source6);
		picture3.appendChild(img.getElement());

		analysisScreenshot.getElement().appendChild(picture3);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.page.Page#onAttach()
	 */
	@Override
	protected void onAttach() {
		applyHomePageTweeks();

		super.onAttach();

		Window.scrollTo(0, toTop);

		((Footer) NavigationController.get().getFooter()).setVisible(false);
		((Header) NavigationController.get().getHeader()).setVisible(false);

		// Append to Body
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

		((Footer) NavigationController.get().getFooter()).setVisible(true);
		((Header) NavigationController.get().getHeader()).setVisible(true);

		toTop = Window.getScrollTop();

		// Romove from Body
		Document.get().getBody().removeChild(scriptCustom);
		removeHomePageTweeks();

	}

	private void appendConditionalTags() {
		String userAgent = Window.Navigator.getUserAgent();
		if (userAgent.contains("MSIE 2") || userAgent.contains("MSIE 3") || userAgent.contains("MSIE 4") || userAgent.contains("MSIE 5")
				|| userAgent.contains("MSIE 6") || userAgent.contains("MSIE 7")) {
			ParagraphElement outdatedBrowser = Document.get().createPElement(); // TODO the element z-index need to be set up higher
			outdatedBrowser.addClassName("browserupgrade");
			outdatedBrowser
					.setInnerHTML("You are using an <strong>outdated</strong> browser. Please <a href=\"http://browsehappy.com/\">upgrade your browser</a> to improve your experience.");
			this.getElement().insertFirst(outdatedBrowser);
		}
		if (userAgent.contains("MSIE 9")) {
			picture1.removeChild(source1);
			picture1.removeChild(source2);
			picture2.removeChild(source3);
			picture2.removeChild(source4);
			picture3.removeChild(source5);
			picture3.removeChild(source6);
			VideoElement video1 = Document.get().createVideoElement();
			video1.setAttribute("style", "display: none;");
			VideoElement video2 = Document.get().createVideoElement();
			video2.setAttribute("style", "display: none;");
			VideoElement video3 = Document.get().createVideoElement();
			video3.setAttribute("style", "display: none;");
			picture1.insertFirst(video1);
			picture2.insertFirst(video2);
			picture3.insertFirst(video3);
			video1.appendChild(source1);
			video1.appendChild(source2);
			video2.appendChild(source3);
			video2.appendChild(source4);
			video3.appendChild(source5);
			video3.appendChild(source6);
		}
	}

}
