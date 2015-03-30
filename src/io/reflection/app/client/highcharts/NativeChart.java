//
//  ChartNative.java
//  storedata
//
//  Created by Stefano Capuzzi on 30 Jan 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.highcharts;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author Stefano Capuzzi
 *
 */
class NativeChart {

	static native void nativeAddAxis(JavaScriptObject chart, JavaScriptObject options, boolean isX, boolean redraw, boolean animate) /*-{
		chart.addAxis(options, isX, redraw, animate);
	}-*/;

	static native void nativeAddAxis(JavaScriptObject chart, JavaScriptObject options, boolean isX, boolean redraw, JavaScriptObject animation) /*-{
		chart.addAxis(options, isX, redraw, animation);
	}-*/;

	public static native void nativeAddSeries(JavaScriptObject chart, JavaScriptObject series, boolean redraw, boolean animate) /*-{
		chart.addSeries(series, redraw, animate);
	}-*/;

	public static native void nativeAddSeries(JavaScriptObject chart, JavaScriptObject series, boolean redraw, JavaScriptObject animation) /*-{
		chart.addSeries(series, redraw, animation);
	}-*/;

	static native void nativeAddSeriesAsDrilldown(JavaScriptObject point, JavaScriptObject series) /*-{
		chart.addSeriesAsDrilldown(point, series);
	}-*/;

	static native void nativeDestroy(JavaScriptObject chart) /*-{
		chart.destroy();
	}-*/;

	static native void nativeDrillUp(JavaScriptObject chart) /*-{
		chart.drillUp();
	}-*/;

	static native JavaScriptObject nativeGet(JavaScriptObject chart, String id) /*-{
		return chart.get(id);
	}-*/;

	static native void nativeHideLoading(JavaScriptObject chart) /*-{
		chart.hideLoading();
	}-*/;

	static native void nativeRedraw(JavaScriptObject chart) /*-{
		chart.redraw();
	}-*/;

	static native void nativeReflow(JavaScriptObject chart) /*-{
		chart.reflow();
	}-*/;

	static native void nativeSetSize(JavaScriptObject chart, int width, int height, boolean animate) /*-{
		chart.setSize(width, height, animate);
	}-*/;

	static native void nativeSetSize(JavaScriptObject chart, int width, int height, JavaScriptObject animation) /*-{
		chart.setSize(width, height, animation);
	}-*/;

	static native void nativeSetTitle(JavaScriptObject chart, JavaScriptObject title, JavaScriptObject subtitle, boolean redraw) /*-{
		chart.setTitle(title, subtitle, redraw);
	}-*/;

	static native void nativeShowLoading(JavaScriptObject chart, String loadingText) /*-{
		chart.showLoading(loadingText);
	}-*/;

	static native void nativeShowLoading(JavaScriptObject chart) /*-{
		chart.showLoading();
	}-*/;

	public static native void nativeUpdateTooltipFormatter(JavaScriptObject chart, JavaScriptObject formatterFunction)/*-{
		chart.tooltip.options.formatter = formatterFunction;
	}-*/;

}
