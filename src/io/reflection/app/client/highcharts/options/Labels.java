//
//  Labels.java
//  storedata
//
//  Created by Stefano Capuzzi on 25 Jan 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.highcharts.options;

import io.reflection.app.client.helper.JavaScriptObjectHelper;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

/**
 * @author Stefano Capuzzi
 *
 */
public class Labels extends Option<Labels> {

	private JsArray<JavaScriptObject> items;

	/**
	 * Shared CSS styles for all labels. Defaults to: style: { color: '#3E576F' }
	 * 
	 * @param style
	 * @return
	 */
	public Labels setStyle(JavaScriptObject style) {
		return setOption("style", style);
	}

	public void createItems() {
		if (items == null) {
			items = JavaScriptObject.createArray().cast();
			setOption("items", items);
		}
	}

	public Labels addLabel(String html, JavaScriptObject style) {
		createItems();
		JavaScriptObject item = JavaScriptObject.createObject();
		JavaScriptObjectHelper.setStringProperty(item, "html", html);
		JavaScriptObjectHelper.setObjectProperty(item, "style", style);

		return this;
	}

	public Labels addLabel(String html) {
		createItems();
		JavaScriptObject item = JavaScriptObject.createObject();
		JavaScriptObjectHelper.setStringProperty(item, "html", html);
		items.push(item);
		return this;
	}
}
