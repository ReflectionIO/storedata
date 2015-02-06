//
//  ChartHelper.java
//  storedata
//
//  Created by Stefano Capuzzi on 18 Jan 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.highcharts;

import io.reflection.app.client.helper.JavaScriptObjectHelper;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayBoolean;
import com.google.gwt.core.client.JsArrayNumber;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.user.client.ui.HTMLPanel;

/**
 * @author Stefano Capuzzi
 *
 */
public class ChartHelper {

	public static final String TYPE_LINE = "line";
	public static final String TYPE_SPLINE = "spline";
	public static final String TYPE_AREA = "area";
	public static final String TYPE_AREASPLINE = "areaspline";
	public static final String TYPE_COLUMN = "column";
	public static final String TYPE_BAR = "bar";
	public static final String TYPE_PIE = "pie";
	public static final String TYPE_SCATTER = "scatter";

	public static final String DASH_STYLE_SOLID = "Solid";
	public static final String DASH_STYLE_SHORT_DASH = "ShortDash";
	public static final String DASH_STYLE_SHORT_DOT = "ShortDot";
	public static final String DASH_STYLE_SHORT_DASH_DOT = "ShortDashDot";
	public static final String DASH_STYLE_DASH_DOT_DOT = "ShortDashDotDot";
	public static final String DASH_STYLE_DOT = "Dot";
	public static final String DASH_STYLE_DASH = "Dash";
	public static final String DASH_STYLE_LONG_DASH = "LongDash";
	public static final String DASH_STYLE_DASH_DOT = "DashDot";
	public static final String DASH_STYLE_LONG_DASH_DOT = "LongDashDot";
	public static final String DASH_STYLE_LONG_DASH_DOT_DOT = "LongDashDotDot";

	public static Chart createAndInjectChart(HTMLPanel container) {
		Chart chart = new Chart();
		container.getElement().appendChild(chart.getElement());
		chart.inject();
		return chart;
	}

	public static Chart createAndInjectChart(DivElement container) {
		Chart chart = new Chart();
		container.appendChild(chart.getElement());
		chart.inject();
		return chart;
	}

	public static JsArrayNumber createMarginsArray(int marginTop, int marginRight, int marginBottom, int marginLeft) {
		JsArrayNumber margins = JavaScriptObject.createArray().cast();
		margins.push(marginTop);
		margins.push(marginRight);
		margins.push(marginBottom);
		margins.push(marginLeft);
		return margins;
	}

	public static JsArrayBoolean createCrosshairsArray(boolean xCrosshair, boolean yCrosshair) {
		JsArrayBoolean crosshairs = JavaScriptObject.createArray().cast();
		crosshairs.push(xCrosshair);
		crosshairs.push(yCrosshair);
		return crosshairs;
	}

	public static JavaScriptObject getJSObjectFromMap(HashMap<String, String> map) {
		JavaScriptObject JSObject = JavaScriptObject.createObject();
		for (Map.Entry<String, String> entry : map.entrySet()) {
			JavaScriptObjectHelper.setStringProperty(JSObject, entry.getKey(), entry.getValue());
		}
		return JSObject;

	}

	public static JavaScriptObject createDateTimeLabelFormat(HashMap<String, String> dateTimeLabelFormatValues) {
		JavaScriptObject dateTimeLabelFormatObject = JavaScriptObject.createObject();
		for (Map.Entry<String, String> entry : dateTimeLabelFormatValues.entrySet()) {
			JavaScriptObjectHelper.setStringProperty(dateTimeLabelFormatObject, entry.getKey(), entry.getValue());
		}
		return dateTimeLabelFormatObject;
	}

	public static JavaScriptObject getDefaultAxisDateTimeLabelFormat() {
		HashMap<String, String> dateTimeLabelFormatValues = new HashMap<String, String>();
		dateTimeLabelFormatValues.put("day", "%e %b");
		return createDateTimeLabelFormat(dateTimeLabelFormatValues);
	}

	public static JavaScriptObject getDefaultTooltipDateTimeLabelFormat() {
		HashMap<String, String> dateTimeLabelFormatValues = new HashMap<String, String>();
		dateTimeLabelFormatValues.put("day", "%e %b %Y");
		return createDateTimeLabelFormat(dateTimeLabelFormatValues);
	}

	public static JavaScriptObject getDefaultAxisStyle() {
		HashMap<String, String> styleValues = new HashMap<String, String>();
		styleValues.put("fontSize", "14px");
		return getJSObjectFromMap(styleValues);
	}

}
