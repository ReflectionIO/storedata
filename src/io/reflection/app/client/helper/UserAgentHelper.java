//
//  UserAgentHelper.java
//  storedata
//
//  Created by Stefano Capuzzi (capuzzistefano) on 9 Mar 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.helper;

import io.reflection.app.client.res.Styles;

import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.dom.client.Document;
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

	static native boolean nativeIsFirefox()/*-{
		return navigator.userAgent.toLowerCase().indexOf('firefox') > -1;
	}-*/;

	static native boolean nativeIsSafari()/*-{
		return navigator.userAgent.toLowerCase().indexOf('safari/') > -1;
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
			Document.get().getBody().addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().isIe() + " ie" + getIEVersion());
		} else if (nativeIsChrome()) {
			DOMHelper.getHtmlElement().addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().isChrome());
		} else if (nativeIsOpera()) {
			DOMHelper.getHtmlElement().addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().isOpera());
		} else if (nativeIsFirefox()) {
			DOMHelper.getHtmlElement().addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().isFirefox());
		} else if (nativeIsSafari()) {
			DOMHelper.getHtmlElement().addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().isSafari());
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

	public static boolean checkIECompatibility() {
		if (isIE() && getIEVersion() < 9) {
			HTMLPanel outdatedBrowser = new HTMLPanel(
					SafeHtmlUtils
							.fromTrustedString("<p>Uh oh... Reflection doesn't work in this browser. &nbsp; &nbsp;<a href=\"http://outdatedbrowser.com/en\" target=\"_blank\">Download a compatible browser</a></p>"));
			outdatedBrowser.setStyleName("window-warning");
			RootPanel.get().add(outdatedBrowser);
			return false;
		}
		return true;
	}

}
