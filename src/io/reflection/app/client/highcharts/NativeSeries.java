//
//  NativeSeries.java
//  storedata
//
//  Created by Stefano Capuzzi on 11 Feb 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.highcharts;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayMixed;

/**
 * @author Stefano Capuzzi
 *
 */
public class NativeSeries {

	static native JavaScriptObject nativeGetSeries(JavaScriptObject chart, int index)/*-{
		return chart.series[index];
	}-*/;

	static native void nativeRemove(JavaScriptObject series, boolean redraw) /*-{
		series.remove(redraw);
	}-*/;

	static native void nativeRemoveAll(JavaScriptObject chart, boolean redraw) /*-{
		while (chart.series.length > 0) {
			chart.series[0].remove(redraw);
		}
	}-*/;

	public static native void nativeAddPoint(JavaScriptObject series, JavaScriptObject point, boolean redraw, boolean shift, boolean animate) /*-{
		series.addPoint(point, redraw, shift, animate);
	}-*/;

	public static native void nativeAddPoint(JavaScriptObject series, JavaScriptObject point, boolean redraw, boolean shift, JavaScriptObject animation) /*-{
		series.addPoint(point, redraw, shift, animation);
	}-*/;

	public static native void nativeAddPoint(JavaScriptObject series, JsArrayMixed point, boolean redraw, boolean shift, boolean animate) /*-{
		series.addPoint(point, redraw, shift, animate);
	}-*/;

	public static native void nativeAddPoint(JavaScriptObject series, JsArrayMixed point, boolean redraw, boolean shift, JavaScriptObject animation) /*-{
		series.addPoint(point, redraw, shift, animation);
	}-*/;

}
