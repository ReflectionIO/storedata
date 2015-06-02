//
//  UserAgentHelper.java
//  storedata
//
//  Created by Stefano Capuzzi (capuzzistefano) on 9 Mar 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.helper;

import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.res.Styles;

import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * @author Stefano Capuzzi (capuzzistefano)
 *
 */
public class UserAgentHelper {

	public static final String userAgent = Window.Navigator.getUserAgent();

	public static final String IE_VALUE = "MSIE ";
	public static final String IE_11_VALUE = "Trident/7";

	public static boolean isIE() {
		return userAgent.contains(IE_VALUE) || userAgent.contains(IE_11_VALUE);
	}

	static native boolean nativeIsChrome()/*-{
		return !!window.chrome && !!window.chrome.webstore;
	}-*/;

	static native boolean nativeIsOpera()/*-{
		return !!window.opera || /opera|opr/i.test(navigator.userAgent);
	}-*/;

	public static int getIEVersion() {
		int version = -1;
		if (userAgent.contains(IE_11_VALUE)) {
			version = 11;
		} else if (userAgent.contains(IE_VALUE)) {
			version = Integer.parseInt(userAgent.substring(userAgent.indexOf(IE_VALUE) + 5, userAgent.indexOf(".", userAgent.indexOf(IE_VALUE))), 10);
		}
		return version;
	}

	public static void detectBrowser() {
		if (isIE()) {
			if (!Document.get().getBody().hasClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().isIe())) {
				Document.get().getBody().addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().isIe() + " ie" + getIEVersion());
			}
		} else if (nativeIsChrome()) {
			if (!DOMHelper.getHtmlElement().hasClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().isChrome())) {
				DOMHelper.getHtmlElement().addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().isChrome());
			}
		} else if (nativeIsOpera()) {
			if (!DOMHelper.getHtmlElement().hasClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().isOpera())) {
				DOMHelper.getHtmlElement().addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().isOpera());
			}
		}
	}

	public static void setMainContentWidthForIE() {
		// calc(100%-220px) CSS does work for IE to set the width, but the width
		// transition effect doesn't work for calc'd width in IE
		// calculate the width for IE instead using JavaScript
		if (isIE()) {
			if (Window.getClientWidth() > 960) {
				if (NavigationController.get().getHeader().isPanelLeftMenuOpen()) {
					DOMHelper.getHtmlElement().getStyle().setWidth(Window.getClientWidth() - 220, Unit.PX);
					NavigationController.get().getMainPanel().getElement().getStyle().setWidth(Window.getClientWidth() - 220, Unit.PX);
				} else {
					DOMHelper.getHtmlElement().getStyle().setWidth(100.0, Unit.PCT);
					NavigationController.get().getMainPanel().getElement().getStyle().setWidth(100.0, Unit.PCT);
				}
			} else {
				DOMHelper.getHtmlElement().getStyle().setWidth(100.0, Unit.PCT);
				NavigationController.get().getMainPanel().getElement().getStyle().setWidth(100.0, Unit.PCT);
			}
		}
	}

	public static void initCustomScrollbars() {
		if (getIEVersion() != 8 && getIEVersion() != 10) {
			ScriptInjector.fromUrl("js/vendor/jquery.mCustomScrollbar.concat.min.js").setWindow(ScriptInjector.TOP_WINDOW)
					.setCallback(new Callback<Void, Exception>() {

						@Override
						public void onSuccess(Void result) {
							nativeInitCustomScrollbars();
						}

						@Override
						public void onFailure(Exception reason) {}
					}).inject();

		}
	}

	private static native void nativeInitCustomScrollbars()/*-{
		$wnd.$(".js-custom-scrollbar").mCustomScrollbar({
			scrollInertia : 200
		});
	}-*/;

	public static void initIETweaks() {
		if (isIE()) {
			Window.addResizeHandler(new ResizeHandler() {
				@Override
				public void onResize(ResizeEvent event) {
					setMainContentWidthForIE();
				}
			});
			if (getIEVersion() < 9) {
				HTMLPanel outdatedBrowser = new HTMLPanel(
						SafeHtmlUtils
								.fromTrustedString("<p>Uh oh... Reflection doesn't work in this browser. &nbsp; &nbsp;<a href=\"http://outdatedbrowser.com/en\" target=\"_blank\">Download a compatible browser</a></p>"));
				outdatedBrowser.setStyleName("window-warning");
				RootPanel.get().add(outdatedBrowser);
			}
		}
	}

}
