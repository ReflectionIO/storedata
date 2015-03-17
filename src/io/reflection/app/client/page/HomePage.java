//
//  HomePage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 13 Jan 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page;

import io.reflection.app.client.helper.DOMHelper;
import io.reflection.app.client.helper.UserAgentHelper;
import io.reflection.app.client.res.Styles;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ScriptElement;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.dom.client.VideoElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ScrollEvent;
import com.google.gwt.user.client.ui.Anchor;
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
	@UiField Anchor linkScrollToAnchor;
	@UiField Element sectionMain;
	@UiField Element sectionSizeShadowLayer;
	@UiField Element sectionHowIntelHelps;
	@UiField Anchor linkToPageTop;
	// @UiField InlineHyperlink homeBtn;
	@UiField HTMLPanel leaderBoardScreenshot;
	@UiField HTMLPanel analysisScreenshot;

	// elements for IE consitionals
	Element picture1, source1, source2;
	Element picture2, source3, source4;
	Element picture3, source5, source6;

	// private static final ScriptElement scriptGoogleMap = DOMHelper
	// .getJSScriptFromUrl("https://maps.googleapis.com/maps/api/js?key=AIzaSyD7mXBIrN4EgMflWKxUOK6C9rfoDMa5zyo&callback=generateMap");
	private static final ScriptElement scriptRespond = DOMHelper.getJSScriptFromUrl("js/respond.min.js");

	private static final ScriptElement scriptPictureFill = DOMHelper.getJSScriptFromUrl("js/picturefill.2.2.0.min.js", "async");

	private int toTop = 0;
	private static boolean tweeked = false;

	public HomePage() {
		initWidget(uiBinder.createAndBindUi(this));

		addHomeBtnPicture();
		addLeaderboardScreenshotPicture();
		addAnalysisScreenshotPicture();

		StyleInjector.injectAtStart(Styles.STYLES_INSTANCE.homePageStyle().getText());

		applyNowBtn.setTargetHistoryToken(PageType.RegisterPageType.asTargetHistoryToken("requestinvite"));

		appendConditionalTags();

		initScrollEffect();

	}

	public static void applyHomePageTweeks() {
		if (!tweeked) {
			// Append to Head
			if (UserAgentHelper.isIE()) {
				if (UserAgentHelper.getIEVersion() < 9) {
					Document.get().getHead().appendChild(HomePage.scriptRespond);
				} else {
					Document.get().getHead().appendChild(HomePage.scriptPictureFill);
				}
			} else { // Not Internet Explorer
				Document.get().getHead().appendChild(HomePage.scriptPictureFill);
			}

			tweeked = true;
		}
	}

	public static void removeHomePageTweeks() {
		if (tweeked) {
			// Remove from Head
			if (UserAgentHelper.isIE()) {
				if (UserAgentHelper.getIEVersion() < 9) {
					Document.get().getHead().removeChild(HomePage.scriptRespond);
				} else {
					Document.get().getHead().removeChild(HomePage.scriptPictureFill);
				}
			} else { // Not Internet Explorer
				Document.get().getHead().removeChild(HomePage.scriptPictureFill);
			}

			tweeked = false;
		}
	}

	private void addHomeBtnPicture() {
		// picture1 = DOM.createElement("picture");
		//
		// source1 = DOM.createElement("source");
		// source1.setAttribute("srcset", Images.INSTANCE.reflectionLogoBeta().getSafeUri().asString());
		// source1.setAttribute("media", "(min-width: 480px)");
		//
		// source2 = DOM.createElement("source");
		// source2.setAttribute("srcset", Images.INSTANCE.mobileReflectionLogoBeta().getSafeUri().asString());
		//
		// Image img = new Image(Images.INSTANCE.reflectionLogoBeta());
		// img.setAltText("Reflection logo");
		//
		// picture1.appendChild(source1);
		// picture1.appendChild(source2);
		// picture1.appendChild(img.getElement());

		// homeBtn.getElement().appendChild(picture1);
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

	@UiHandler("linkScrollToAnchor")
	void onLinkScrollToAnchorClicked(ClickEvent event) {
		event.preventDefault();
		// String href = linkScrollToAnchor.getElement().getAttribute("href");
		int navHeight = 60;
		int targetTop = sectionHowIntelHelps.getOffsetTop();
		DOMHelper.nativeScrollTop(targetTop - navHeight, 455, "swing");
	}

	@UiHandler("linkToPageTop")
	void onLinkToPageTopClicked(ClickEvent event) {
		event.preventDefault();
		DOMHelper.nativeScrollTop(0, "slow", "swing");
	}

	private void initScrollEffect() {
		if (DOMHelper.getHtmlElement().hasClassName("csstransforms") && DOMHelper.getHtmlElement().hasClassName("no-touch")) {
			final int breakpointVertical = 680;
			final int breakpointHorizontal = 980;
			Window.addWindowScrollHandler(new Window.ScrollHandler() {
				@Override
				public void onWindowScroll(ScrollEvent event) {
					int windowHeight = Window.getClientHeight();
					int windowWidth = Window.getClientWidth();
					int scrollTop = Window.getScrollTop();
					if (windowHeight >= breakpointVertical && windowWidth >= breakpointHorizontal) {
						double scrollAsPercentageOfWindowHeight = ((double) scrollTop / windowHeight) * 50.0;
						double scale = 1 - (scrollAsPercentageOfWindowHeight / 100.0);
						double opacityScale = 1 - ((scrollAsPercentageOfWindowHeight * 2.5) / 100.0);
						double opacityScaleShadowLayer = 0 + ((scrollAsPercentageOfWindowHeight * 2.0) / 85.0);
						sectionMain.setAttribute("style", "-ms-transform: scale(" + scale + "); -webkit-transform : scale(" + scale + "); transform : scale("
								+ scale + "); top: " + -(double) scrollTop / 4.3 + "px; opacity: " + opacityScale);
						sectionSizeShadowLayer.getStyle().setOpacity(opacityScaleShadowLayer);
					} else {
						sectionMain.setAttribute("style", "-ms-transform: scale(1); -webkit-transform : scale(1); transform : scale(1); top: 0px; opacity: 1");
						sectionSizeShadowLayer.getStyle().setOpacity(0);
					}
				}
			});

		}
	}

	private void initGoogleMap() {
		// nativeCalleGenerateMap();
		ScriptInjector.fromUrl("js/scripts.180cd4275030bac3e6c6f190e5c98813.js").setWindow(ScriptInjector.TOP_WINDOW).inject();
		// ScriptInjector.fromUrl("https://maps.googleapis.com/maps/api/js?key=AIzaSyD7mXBIrN4EgMflWKxUOK6C9rfoDMa5zyo&callback=generateMap")
		// .setWindow(ScriptInjector.TOP_WINDOW).inject();
		nativeCalleGenerateMap();
	}

	native void nativeCalleGenerateMap() /*-{
		$wnd.$
				.getScript(
						"https://maps.googleapis.com/maps/api/js?key=AIzaSyD7mXBIrN4EgMflWKxUOK6C9rfoDMa5zyo&callback=generateMap")
				.done(function(script, textStatus) {
					$wnd.generateMap();
				}).fail(function(jqxhr, settings, exception) {
				});
	}-*/;

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.page.Page#onAttach()
	 */
	@Override
	protected void onAttach() {
		super.onAttach();

		Window.scrollTo(0, toTop);

		initGoogleMap();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.page.Page#onDetach()
	 */
	@Override
	protected void onDetach() {
		super.onDetach();

		// ((Header) NavigationController.get().getHeader()).setVisible(true);

		toTop = Window.getScrollTop();

		// Document.get().getHead().removeChild(scriptGoogleMap);
	}

	private void appendConditionalTags() {
		if (UserAgentHelper.isIE() && UserAgentHelper.getIEVersion() == 9) {
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
