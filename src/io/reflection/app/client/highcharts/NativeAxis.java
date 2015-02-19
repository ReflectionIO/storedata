//
//  NativeAxis.java
//  storedata
//
//  Created by Stefano Capuzzi on 30 Jan 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.highcharts;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;

/**
 * @author Stefano Capuzzi
 *
 */
class NativeAxis {

	static native void nativeAddPlotBand(JavaScriptObject axis, JavaScriptObject plotBand) /*-{
		axis.addPlotBand(plotBand);
	}-*/;

	static native void nativeAddPlotLine(JavaScriptObject axis, JavaScriptObject plotLine)/*-{
		axis.addPlotLine(plotLine);
	}-*/;

	static native JavaScriptObject nativeGetExtremes(JavaScriptObject axis)/*-{
		return axis.getExtremes();
	}-*/;

	static native double nativeGetMin(JavaScriptObject axis)/*-{
		return axis.getExtremes().min;
	}-*/;

	static native double nativeGetMax(JavaScriptObject axis)/*-{
		return axis.getExtremes().max;
	}-*/;

	static native double nativeGetDataMin(JavaScriptObject axis)/*-{
		return axis.getExtremes().dataMin;
	}-*/;

	static native double nativeGetDataMax(JavaScriptObject axis)/*-{
		return axis.getExtremes().dataMax;
	}-*/;

	static native JavaScriptObject nativeGetXAxis(JavaScriptObject chart, int index)/*-{
		return chart.xAxis[index];
	}-*/;

	static native JavaScriptObject nativeGetYAxis(JavaScriptObject chart, int index)/*-{
		return chart.yAxis[index];
	}-*/;

	static native void nativeRemove(JavaScriptObject axis)/*-{
		axis.remove();
	}-*/;

	static native void nativeRemoveAllY(JavaScriptObject chart, boolean redraw) /*-{
		while (chart.yAxis.length > 0) {
			chart.yAxis[0].remove(redraw);
		}
	}-*/;

	static native void nativeRemovePlotBand(JavaScriptObject axis, String id) /*-{
		axis.removePlotBand(id);
	}-*/;

	static native void nativeRemovePlotLine(JavaScriptObject axis, String id) /*-{
		axis.removePlotLine(id);
	}-*/;

	static native void nativeSetCategories(JavaScriptObject axis, JsArrayString categories) /*-{
		axis.setCategories(categories);
	}-*/;

	static native void nativeSetExtremes(JavaScriptObject axis, double min, double max, boolean redraw, boolean animate) /*-{
		axis.setExtremes(min, max, redraw, animate);
	}-*/;

	static native void nativeSetExtremes(JavaScriptObject axis, double min, double max, boolean redraw, JavaScriptObject animation) /*-{
		axis.setExtremes(min, max, redraw, animation);
	}-*/;

	static native void nativeSetTitle(JavaScriptObject axis, JavaScriptObject title, boolean redraw)/*-{
		axis.setTitle(title, redraw);
	}-*/;

	static native void nativeUpdate(JavaScriptObject axis, JavaScriptObject options, boolean redraw)/*-{
		axis.update(options, redraw);
	}-*/;

}
