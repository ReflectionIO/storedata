//
//  ChartOption.java
//  storedata
//
//  Created by Stefano Capuzzi on 18 Jan 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.highcharts.options;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayNumber;

/**
 * @author Stefano Capuzzi
 *
 */
public class ChartOption extends Option<ChartOption> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.highcharts.Option#getName()
	 */
	@Override
	public String getName() {
		return "chart";
	}

	public ChartOption setAlignTicks(boolean aligned) {
		return setOption("alignTicks", aligned);
	}

	public ChartOption setAnimation(boolean animated) {
		return setOption("animate", animated);
	}

	public ChartOption setAnimation(JavaScriptObject animation) {
		return setOption("animate", animation);
	}

	public ChartOption setBackgroundColor(String color) {
		return setOption("backgroundColor", color);
	}

	public ChartOption setBackgroundColor(JavaScriptObject property) {
		return setOption("backgroundColor", property);
	}

	public ChartOption setBorderColor(String color) {
		return setOption("borderColor", color);
	}

	public ChartOption setBorderRadius(int radius) {
		return setOption("borderRadius", radius);
	}

	public ChartOption setBorderWidth(int width) {
		return setOption("borderWidth", width);
	}

	public ChartOption setClassName(String className) {
		return setOption("className", className);
	}

	public ChartOption setHeight(int height) {
		return setOption("height", height);
	}

	public ChartOption setIgnoreHiddenSeries(boolean ignore) {
		return setOption("ignoreHiddenSeries", ignore);
	}

	public ChartOption setInverted(boolean invert) {
		return setOption("inverted", invert);
	}

	public ChartOption setMargin(JsArrayNumber marginArray) {
		return setOption("margin", marginArray);
	}

	public ChartOption setMarginBottom(int margin) {
		return setOption("marginBottom", margin);
	}

	public ChartOption setMarginLeft(int margin) {
		return setOption("marginLeft", margin);
	}

	public ChartOption setMarginRight(int margin) {
		return setOption("marginRight", margin);
	}

	public ChartOption setMarginTop(int margin) {
		return setOption("marginTop", margin);
	}

	public ChartOption panKey(String key) {
		return setOption("panKey", key);
	}

	public ChartOption setPanning(boolean pan) {
		return setOption("panning", pan);
	}

	public ChartOption setPinchType(String pinchType) {
		return setOption("pinchType", pinchType);
	}

	public ChartOption setPlotBackgroundColor(String color) {
		return setOption("plotBackgroundColor", color);
	}

	public ChartOption setPlotBackgroundColor(JavaScriptObject property) {
		return setOption("plotBackgroundColor", property);
	}

	public ChartOption setPlotBackgroundImage(String url) {
		return setOption("plotBackgroundImage", url);
	}

	public ChartOption setPlotBorderColor(String color) {
		return setOption("plotBorderColor", color);
	}

	public ChartOption setPlotBorderWidth(int width) {
		return setOption("plotBorderWidth", width);
	}

	public ChartOption setPlotShadow(boolean shadow) {
		return setOption("plotShadow", shadow);
	}

	public ChartOption setPlotShadow(JavaScriptObject property) {
		return setOption("plotShadow", property);
	}

	public ChartOption setPolar(boolean polar) {
		return setOption("polar", polar);
	}

	public ChartOption setReflow(boolean reflow) {
		return setOption("reflow", reflow);
	}

	public ChartOption setRenderTo(String id) {
		return setOption("renderTo", id);
	}

	public ChartOption setSelectionMarkerFill(String color) {
		return setOption("selectionMarkerFill", color);
	}

	public ChartOption setShadow(boolean shadow) {
		return setOption("shadow", shadow);
	}

	public ChartOption setShadow(JavaScriptObject property) {
		return setOption("shadow", property);
	}

	public ChartOption setShowAxes(boolean show) {
		return setOption("showAxes", show);
	}

	public ChartOption setSpacing(JsArrayNumber spacingArray) {
		return setOption("spacing", spacingArray);
	}

	public ChartOption setSpacingBottom(int spacing) {
		return setOption("spacingBottom", spacing);
	}

	public ChartOption setSpacingLeft(int spacing) {
		return setOption("spacingLeft", spacing);
	}

	public ChartOption setSpacingRight(int spacing) {
		return setOption("spacingRight", spacing);
	}

	public ChartOption setSpacingTop(int spacing) {
		return setOption("spacingTop", spacing);
	}

	public ChartOption setStyle(JavaScriptObject style) {
		return setOption("style", style);
	}

	public ChartOption setType(String type) {
		return setOption("type", type);
	}

	public ChartOption setWidth(int width) {
		return setOption("width", width);
	}

	public ChartOption setZoomType(String type) {
		return setOption("zoomType", type);
	}

}
