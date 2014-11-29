//
//  MixPanelApiHelper.java
//  storedata
//
//  Created by William Shakour (billy1380) on 28 Nov 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.helper;

import io.reflection.app.client.controller.NavigationController.Stack;
import io.reflection.app.client.mixpanel.MixPanelApi;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author William Shakour (billy1380)
 *
 */
public class MixPanelApiHelper {

	/**
	 * @param value
	 */
	public static void track(Stack value) {
		JavaScriptObject o = JavaScriptObject.createObject();

		int count = value.getParameterCount();
		String param;
		
		for (int i = 0; i < count; i++) {
			param = value.getParameter(i);
			
			if (i == 0) {
				JavaScriptObjectHelper.setStringProperty(o, "page", param);
			} else {
				JavaScriptObjectHelper.setStringProperty(o, "param" + i, param);
			}
		}

		MixPanelApi.get().track("navigation", o);

	}
}
