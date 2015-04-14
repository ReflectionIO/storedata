//
//  ColorHelper.java
//  storedata
//
//  Created by Stefano Capuzzi (capuzzistefano) on 9 Apr 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.helper;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayMixed;
import com.google.gwt.core.client.JsArrayString;

/**
 * @author Stefano Capuzzi (capuzzistefano)
 *
 */
public class ColorHelper {

	public static String getReflectionPurple() {
		return "#6d69c5";
	}

	public static String getReflectionRed() {
		return "#ff496a";
	}

	public static String getReflectionGreen() {
		return "#1bc79f";
	}

	public static String getDarkGrey1() {
		return "#29292c";
	}

	public static String getDarkGrey2() {
		return "#363a47";
	}

	public static String getMidGrey1() {
		return "#5b5b68";
	}

	public static String getMidGrey2() {
		return "#727686";
	}

	public static String getLightGrey1() {
		return "#81879d";
	}

	public static String getLightGrey2() {
		return "#c4c7d0";
	}

	public static String getWhite() {
		return "#fcfdfd";
	}

	public static String getPanelGrey() {
		return "#f2f2f5";
	}

	public static String getGraphFillGrey() {
		return "#e8e7f1";
	}

	public static String getDividerGrey() {
		return "#e1e5e8";
	}

	public static JsArrayString getColorsAsJSArray(String... colors) {
		JsArrayString colorsJSArray = JavaScriptObject.createArray().cast();
		for (String c : colors) {
			colorsJSArray.push(c);
		}
		return colorsJSArray;
	}

	public static JavaScriptObject getLinearGradientColor(int x1, int y1, int x2, int y2, String color1, String color2) {
		JavaScriptObject backgroundColor = JavaScriptObject.createObject();
		JavaScriptObject linearGradient = JavaScriptObject.createObject();
		JavaScriptObjectHelper.setIntegerProperty(linearGradient, "x1", x1);
		JavaScriptObjectHelper.setIntegerProperty(linearGradient, "y1", y1);
		JavaScriptObjectHelper.setIntegerProperty(linearGradient, "x2", x2);
		JavaScriptObjectHelper.setIntegerProperty(linearGradient, "y2", y2);
		JavaScriptObjectHelper.setObjectProperty(backgroundColor, "linearGradient", linearGradient);
		JsArrayMixed stops = JavaScriptObject.createArray().cast();
		JsArrayMixed stop1 = JavaScriptObject.createArray().cast();
		JsArrayMixed stop2 = JavaScriptObject.createArray().cast();
		stop1.push(0);
		stop2.push(1);
		stop1.push(color1);
		stop2.push(color2);
		stops.push(stop1);
		stops.push(stop2);
		JavaScriptObjectHelper.setObjectProperty(backgroundColor, "stops", stops);
		return backgroundColor;
	}

}
