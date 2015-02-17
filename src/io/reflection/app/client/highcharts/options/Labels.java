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

	private JsArray<JavaScriptObject> labels;

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.highcharts.Option#getName()
	 */
	@Override
	public String getName() {
		return "labels";
	}

	/**
	 * Shared CSS styles for all labels. Defaults to: style: { color: '#3E576F' }
	 * 
	 * @param style
	 * @return
	 */
	public Labels setStyle(JavaScriptObject style) {
		return setOption("style", style);
	}

	public Labels addLabel(String html, JavaScriptObject style) {
		if (labels == null) {
			labels = JavaScriptObject.createArray().cast();
			setOption("items", labels);
		}
		JavaScriptObjectHelper.setStringProperty(labels, "html", html);
		JavaScriptObjectHelper.setObjectProperty(labels, "style", style);
		return this;
	}
}
