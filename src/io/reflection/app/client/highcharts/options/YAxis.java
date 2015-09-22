//
//  YAxis.java
//  storedata
//
//  Created by Stefano Capuzzi on 25 Jan 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.highcharts.options;

import io.reflection.app.client.helper.JavaScriptObjectHelper;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author Stefano Capuzzi
 *
 */
public class YAxis extends Axis<YAxis> {

	private JavaScriptObject stackLabels;

	public YAxis setGridLineInterpolation(String gridLineInterpolation) {
		return setOption("gridLineInterpolation", gridLineInterpolation);
	}

	public YAxis setMaxColor(String color) {
		return setOption("maxColor", color);
	}

	public YAxis setMinColor(String color) {
		return setOption("minColor", color);
	}

	public YAxis setReversedStacks(boolean reversedStacks) {
		return setOption("reversedStacks", reversedStacks);
	}

	public JavaScriptObject getStackLabels() {
		if (stackLabels == null) {
			stackLabels = JavaScriptObject.createObject();
			setOption("stackLabels", stackLabels);
		}
		return stackLabels;
	}

	public YAxis setStackLabelsAlign(String align) {
		JavaScriptObjectHelper.setStringProperty(getStackLabels(), "align", align);
		return this;
	}

	public YAxis setStackLabelsEnabled(boolean enable) {
		JavaScriptObjectHelper.setBooleanProperty(getStackLabels(), "enabled", enable);
		return this;
	}

	public YAxis setStackLabelsFormat(String format) {
		JavaScriptObjectHelper.setStringProperty(getStackLabels(), "format", format);
		return this;
	}

	public YAxis setStackLabelsFormatter(JavaScriptObject formatterFunction) {
		JavaScriptObjectHelper.setObjectProperty(getStackLabels(), "formatter", formatterFunction);
		return this;
	}

	public YAxis setStackLabelsRotation(int rotation) {
		JavaScriptObjectHelper.setIntegerProperty(getStackLabels(), "rotation", rotation);
		return this;
	}

	public YAxis setStackLabelsStyle(JavaScriptObject style) {
		JavaScriptObjectHelper.setObjectProperty(getStackLabels(), "style", style);
		return this;
	}

	public YAxis setStackLabelsTextAlign(String align) {
		JavaScriptObjectHelper.setStringProperty(getStackLabels(), "textAlign", align);
		return this;
	}

	public YAxis setStackLabelsUseHTML(boolean useHTML) {
		JavaScriptObjectHelper.setBooleanProperty(getStackLabels(), "useHTML", useHTML);
		return this;
	}

	public YAxis setStackLabelsVerticalAlign(String align) {
		JavaScriptObjectHelper.setStringProperty(getStackLabels(), "verticalAlign", align);
		return this;
	}

	public YAxis setStackLabelsX(int x) {
		JavaScriptObjectHelper.setIntegerProperty(getStackLabels(), "x", x);
		return this;
	}

	public YAxis setStackLabelsY(int y) {
		JavaScriptObjectHelper.setIntegerProperty(getStackLabels(), "y", y);
		return this;
	}
}
