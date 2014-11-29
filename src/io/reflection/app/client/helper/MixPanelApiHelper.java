//
//  MixPanelApiHelper.java
//  storedata
//
//  Created by William Shakour (billy1380) on 28 Nov 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.helper;

import java.util.Map;

import io.reflection.app.client.controller.NavigationController.Stack;
import io.reflection.app.client.mixpanel.MixPanelApi;
import io.reflection.app.datatypes.shared.User;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author William Shakour (billy1380)
 *
 */
public class MixPanelApiHelper {

	/**
	 * @param value
	 */
	public static void trackNavigation(Stack value) {
		JavaScriptObject o = JavaScriptObject.createObject();

		if (value.hasPage()) {
			JavaScriptObjectHelper.setStringProperty(o, "page", value.getPage().substring(1));
		}

		if (value.hasAction()) {
			JavaScriptObjectHelper.setStringProperty(o, "action", value.getAction());
		}

		int count = value.getParameterCount();
		String param;
		for (int i = 0; i < count; i++) {
			param = value.getParameter(i);

			if (param.contains(":")) {
				String[] encoded = param.split(":");
				String[] values = Stack.decode(encoded[0] + ":", param);

				for (int j = 0; j < values.length; j++) {
					param = values[j];
					JavaScriptObjectHelper.setStringProperty(o, encoded[0] + j, param);
				}
			} else {
				JavaScriptObjectHelper.setStringProperty(o, "param" + i, param);
			}
		}

		MixPanelApi.get().track("navigation", o);
	}

	public static void trackLoginUser(User user) {
		if (user != null) {
			JavaScriptObject o = JavaScriptObject.createObject();

			JavaScriptObjectHelper.setStringProperty(o, "username", user.username);
			JavaScriptObjectHelper.setStringProperty(o, "company", user.company);
			JavaScriptObjectHelper.setBooleanProperty(o, "loggedin", true);

			MixPanelApi.get().register(o);

			MixPanelApi.get().identify(user.username);

			MixPanelApi.get().track("login");
		}
	}

	public static void trackLoggedOut() {
		MixPanelApi.get().track("logout");

		MixPanelApi.get().unregister("loggedin");
	}

	public static void track(String name, Map<String, ?> properties) {
		JavaScriptObject o = JavaScriptObject.createObject();

		JavaScriptObjectHelper.copyToJavaScriptObject(properties, o);

		MixPanelApi.get().track(name, o);
	}
}
