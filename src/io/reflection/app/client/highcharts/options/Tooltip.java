//
//  Tooltip.java
//  storedata
//
//  Created by Stefano Capuzzi on 24 Jan 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.highcharts.options;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayBoolean;

/**
 * @author Stefano Capuzzi
 *
 */
public class Tooltip extends Option<Tooltip> {

	public Tooltip setAnimation(boolean animated) {
		return setOption("animate", animated);
	}

	public Tooltip setBackgroundColor(String rgb) {
		return setOption("backgroundColor", rgb);
	}

	public Tooltip setBorderColor(String rgb) {
		return setOption("borderColor", rgb);
	}

	public Tooltip setBorderRadius(int radius) {
		return setOption("borderRadius", radius);
	}

	public Tooltip setBorderWidth(int width) {
		return setOption("borderWidth", width);
	}

	public Tooltip setCrosshairs(boolean enable) {
		return setOption("crosshairs", enable);
	}

	public Tooltip setCrosshairs(JsArrayBoolean crosshairsArray) {
		return setOption("crosshairs", crosshairsArray);
	}

	public Tooltip setCrosshairs(JavaScriptObject crosshair) {
		return setOption("crosshairs", crosshair);
	}

	public Tooltip setCrosshairs(JsArray<JavaScriptObject> crosshairsArray) {
		return setOption("crosshairs", crosshairsArray);
	}

	public Tooltip setDateTimeLabelFormats(JavaScriptObject formats) {
		return setOption("dateTimeLabelFormats", formats);
	}

	public Tooltip setEnabled(boolean enabled) {
		return setOption("enabled", enabled);
	}

	public Tooltip setFollowPointer(boolean followPointer) {
		return setOption("followPointer", followPointer);
	}

	public Tooltip setFollowTouchMove(boolean followTouchMove) {
		return setOption("followTouchMove", followTouchMove);
	}

	public Tooltip setFooterFormat(String format) {
		return setOption("footerFormat", format);
	}

	public Tooltip setFormatter(JavaScriptObject formatterFunction) {
		return setOption("formatter", formatterFunction);
	}

	public Tooltip setHeaderFormat(String format) {
		return setOption("headerFormat", format);
	}

	public Tooltip setHideDelay(int delay) {
		return setOption("hideDelay", delay);
	}

	public Tooltip setPointFormat(String format) {
		return setOption("pointFormat", format);
	}

	public Tooltip setPositioner(JavaScriptObject positionerFunction) {
		return setOption("positioner", positionerFunction);
	}

	public Tooltip setShadow(boolean shadow) {
		return setOption("shadow", shadow);
	}

	public Tooltip setShape(String shape) {
		return setOption("shape", shape);
	}

	public Tooltip setShared(boolean shared) {
		return setOption("shared", shared);
	}

	public Tooltip setSnap(int snap) {
		return setOption("snap", snap);
	}

	public Tooltip setStyle(JavaScriptObject style) {
		return setOption("style", style);
	}

	public Tooltip setUseHTML(boolean useHTML) {
		return setOption("useHTML", useHTML);
	}

	public Tooltip setValueDecimals(int valueDecimals) {
		return setOption("valueDecimals", valueDecimals);
	}

	public Tooltip setValuePrefix(String valuePrefix) {
		return setOption("valuePrefix", valuePrefix);
	}

	public Tooltip setValueSuffix(String valueSuffix) {
		return setOption("valueSuffix", valueSuffix);
	}

	public Tooltip setXDateFormat(String xDateFormat) {
		return setOption("xDateFormat", xDateFormat);
	}

}
