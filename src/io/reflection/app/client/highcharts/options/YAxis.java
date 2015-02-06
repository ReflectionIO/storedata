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

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.highcharts.Option#getName()
	 */
	@Override
	public String getName() {
		return "yAxis";
	}

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

	public YAxis setStackLabelsAlign(String align) {
		if (stackLabels == null) {
			stackLabels = JavaScriptObject.createObject();
			setOption("stackLabels", stackLabels);
		}
		JavaScriptObjectHelper.setStringProperty(stackLabels, "align", align);
		return this;
	}

	public YAxis setStackLabelsEnabled(boolean enable) {
		if (stackLabels == null) {
			stackLabels = JavaScriptObject.createObject();
			setOption("stackLabels", stackLabels);
		}
		JavaScriptObjectHelper.setBooleanProperty(stackLabels, "enabled", enable);
		return this;
	}

	public YAxis setStackLabelsFormat(String format) {
		if (stackLabels == null) {
			stackLabels = JavaScriptObject.createObject();
			setOption("stackLabels", stackLabels);
		}
		JavaScriptObjectHelper.setStringProperty(stackLabels, "format", format);
		return this;
	}

	public YAxis setStackLabelsFormatter(JavaScriptObject formatterFunction) {
		if (stackLabels == null) {
			stackLabels = JavaScriptObject.createObject();
			setOption("stackLabels", stackLabels);
		}
		JavaScriptObjectHelper.setObjectProperty(stackLabels, "formatter", formatterFunction);
		return this;
	}

	public YAxis setStackLabelsRotation(int rotation) {
		if (stackLabels == null) {
			stackLabels = JavaScriptObject.createObject();
			setOption("stackLabels", stackLabels);
		}
		JavaScriptObjectHelper.setIntegerProperty(stackLabels, "rotation", rotation);
		return this;
	}

	public YAxis setStackLabelsStyle(JavaScriptObject style) {
		if (stackLabels == null) {
			stackLabels = JavaScriptObject.createObject();
			setOption("stackLabels", stackLabels);
		}
		JavaScriptObjectHelper.setObjectProperty(stackLabels, "style", style);
		return this;
	}

	public YAxis setStackLabelsTextAlign(String align) {
		if (stackLabels == null) {
			stackLabels = JavaScriptObject.createObject();
			setOption("stackLabels", stackLabels);
		}
		JavaScriptObjectHelper.setStringProperty(stackLabels, "textAlign", align);
		return this;
	}

	public YAxis setStackLabelsUseHTML(boolean useHTML) {
		if (stackLabels == null) {
			stackLabels = JavaScriptObject.createObject();
			setOption("stackLabels", stackLabels);
		}
		JavaScriptObjectHelper.setBooleanProperty(stackLabels, "useHTML", useHTML);
		return this;
	}

	public YAxis setStackLabelsVerticalAlign(String align) {
		if (stackLabels == null) {
			stackLabels = JavaScriptObject.createObject();
			setOption("stackLabels", stackLabels);
		}
		JavaScriptObjectHelper.setStringProperty(stackLabels, "verticalAlign", align);
		return this;
	}

	public YAxis setStackLabelsX(int x) {
		if (stackLabels == null) {
			stackLabels = JavaScriptObject.createObject();
			setOption("stackLabels", stackLabels);
		}
		JavaScriptObjectHelper.setIntegerProperty(stackLabels, "x", x);
		return this;
	}

	public YAxis setStackLabelsY(int y) {
		if (stackLabels == null) {
			stackLabels = JavaScriptObject.createObject();
			setOption("stackLabels", stackLabels);
		}
		JavaScriptObjectHelper.setIntegerProperty(stackLabels, "y", y);
		return this;
	}
}
