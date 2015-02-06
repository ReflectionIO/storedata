//
//  PlotOption.java
//  storedata
//
//  Created by Stefano Capuzzi on 2 Feb 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.highcharts.options;

import io.reflection.app.client.helper.JavaScriptObjectHelper;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author Stefano Capuzzi
 *
 */
public class PlotOption extends Option<PlotOption> {

	private JavaScriptObject series;
	private JavaScriptObject dataLabels;
	private JavaScriptObject marker;

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.highcharts.options.Option#getName()
	 */
	@Override
	public String getName() {
		return "plotOptions";
	}

	private void createSeriesIfNotExists() {
		if (series == null) {
			series = JavaScriptObject.createObject();
			setOption("series", series);
		}
	}

	public PlotOption setAllowPointSelect(boolean allow) {
		createSeriesIfNotExists();
		JavaScriptObjectHelper.setBooleanProperty(series, "allowPointSelect", allow);
		return this;
	}

	public PlotOption setAnimation(boolean animate) {
		createSeriesIfNotExists();
		JavaScriptObjectHelper.setBooleanProperty(series, "animation", animate);
		return this;
	}

	public PlotOption setAnimation(JavaScriptObject animation) {
		createSeriesIfNotExists();
		JavaScriptObjectHelper.setObjectProperty(series, "animation", animation);
		return this;
	}

	public PlotOption setColor(String color) {
		createSeriesIfNotExists();
		JavaScriptObjectHelper.setStringProperty(series, "color", color);
		return this;
	}

	public PlotOption setConnectNulls(boolean connect) {
		createSeriesIfNotExists();
		JavaScriptObjectHelper.setBooleanProperty(series, "connectNulls", connect);
		return this;
	}

	public PlotOption setCropThreshold(int threshold) {
		createSeriesIfNotExists();
		JavaScriptObjectHelper.setIntegerProperty(series, "cropThreshold", threshold);
		return this;
	}

	public PlotOption setCursor(String cursor) {
		createSeriesIfNotExists();
		JavaScriptObjectHelper.setStringProperty(series, "cursor", cursor);
		return this;
	}

	public PlotOption setDashStyle(String dashStyle) {
		createSeriesIfNotExists();
		JavaScriptObjectHelper.setStringProperty(series, "dashStyle", dashStyle);
		return this;
	}

	private void createDataLabelsIfNotExists() {
		if (dataLabels == null) {
			dataLabels = JavaScriptObject.createObject();
			JavaScriptObjectHelper.setObjectProperty(series, "dataLabels", dataLabels);
		}
	}

	public PlotOption setDataLabelsAlign(String align) {
		createSeriesIfNotExists();
		createDataLabelsIfNotExists();
		JavaScriptObjectHelper.setStringProperty(dataLabels, "align", align);
		return this;
	}

	public PlotOption setDataLabelsBackgroundColor(String color) {
		createSeriesIfNotExists();
		createDataLabelsIfNotExists();
		JavaScriptObjectHelper.setStringProperty(dataLabels, "backgroundColor", color);
		return this;
	}

	public PlotOption setDataLabelsBorderColor(String color) {
		createSeriesIfNotExists();
		createDataLabelsIfNotExists();
		JavaScriptObjectHelper.setStringProperty(dataLabels, "borderColor", color);
		return this;
	}

	public PlotOption setDataLabelsBorderRadius(int borderRadius) {
		createSeriesIfNotExists();
		createDataLabelsIfNotExists();
		JavaScriptObjectHelper.setIntegerProperty(dataLabels, "borderRadius", borderRadius);
		return this;
	}

	public PlotOption setDataLabelsBorderWidth(int borderWidth) {
		createSeriesIfNotExists();
		createDataLabelsIfNotExists();
		JavaScriptObjectHelper.setIntegerProperty(dataLabels, "borderWidth", borderWidth);
		return this;
	}

	public PlotOption setDataLabelsColor(String color) {
		createSeriesIfNotExists();
		createDataLabelsIfNotExists();
		JavaScriptObjectHelper.setStringProperty(dataLabels, "color", color);
		return this;
	}

	public PlotOption setDataLabelsCrop(boolean crop) {
		createSeriesIfNotExists();
		createDataLabelsIfNotExists();
		JavaScriptObjectHelper.setBooleanProperty(dataLabels, "crop", crop);
		return this;
	}

	public PlotOption setDataLabelsDefer(boolean defer) {
		createSeriesIfNotExists();
		createDataLabelsIfNotExists();
		JavaScriptObjectHelper.setBooleanProperty(dataLabels, "defer", defer);
		return this;
	}

	public PlotOption setDataLabelsEnabled(boolean enable) {
		createSeriesIfNotExists();
		createDataLabelsIfNotExists();
		JavaScriptObjectHelper.setBooleanProperty(dataLabels, "enabled", enable);
		return this;
	}

	public PlotOption setDataLabelsFormat(String format) {
		createSeriesIfNotExists();
		createDataLabelsIfNotExists();
		JavaScriptObjectHelper.setStringProperty(dataLabels, "format", format);
		return this;
	}

	public PlotOption setDataLabelsFormatter(JavaScriptObject formatterFunction) {
		createSeriesIfNotExists();
		createDataLabelsIfNotExists();
		JavaScriptObjectHelper.setObjectProperty(dataLabels, "formatter", formatterFunction);
		return this;
	}

	public PlotOption setDataLabelsInside(boolean inside) {
		createSeriesIfNotExists();
		createDataLabelsIfNotExists();
		JavaScriptObjectHelper.setBooleanProperty(dataLabels, "inside", inside);
		return this;
	}

	public PlotOption setDataLabelsOverflow(String overflow) {
		createSeriesIfNotExists();
		createDataLabelsIfNotExists();
		JavaScriptObjectHelper.setStringProperty(dataLabels, "overflow", overflow);
		return this;
	}

	public PlotOption setDataLabelsPadding(int padding) {
		createSeriesIfNotExists();
		createDataLabelsIfNotExists();
		JavaScriptObjectHelper.setIntegerProperty(dataLabels, "padding", padding);
		return this;
	}

	public PlotOption setDataLabelsRotation(int rotation) {
		createSeriesIfNotExists();
		createDataLabelsIfNotExists();
		JavaScriptObjectHelper.setIntegerProperty(dataLabels, "rotation", rotation);
		return this;
	}

	public PlotOption setDataLabelsShadow(boolean shadow) {
		createSeriesIfNotExists();
		createDataLabelsIfNotExists();
		JavaScriptObjectHelper.setBooleanProperty(dataLabels, "shadow", shadow);
		return this;
	}

	public PlotOption setDataLabelsShadow(JavaScriptObject shadow) {
		createSeriesIfNotExists();
		createDataLabelsIfNotExists();
		JavaScriptObjectHelper.setObjectProperty(dataLabels, "shadow", shadow);
		return this;
	}

	public PlotOption setDataLabelsStyle(JavaScriptObject style) {
		createSeriesIfNotExists();
		createDataLabelsIfNotExists();
		JavaScriptObjectHelper.setObjectProperty(dataLabels, "style", style);
		return this;
	}

	public PlotOption setDataLabelsUserHTML(boolean useHTML) {
		createSeriesIfNotExists();
		createDataLabelsIfNotExists();
		JavaScriptObjectHelper.setBooleanProperty(dataLabels, "useHTML", useHTML);
		return this;
	}

	public PlotOption setDataLabelsVerticalAlign(String alignment) {
		createSeriesIfNotExists();
		createDataLabelsIfNotExists();
		JavaScriptObjectHelper.setStringProperty(dataLabels, "verticalAlign", alignment);
		return this;
	}

	public PlotOption setDataLabelsX(int x) {
		createSeriesIfNotExists();
		createDataLabelsIfNotExists();
		JavaScriptObjectHelper.setIntegerProperty(dataLabels, "x", x);
		return this;
	}

	public PlotOption setDataLabelsY(int y) {
		createSeriesIfNotExists();
		createDataLabelsIfNotExists();
		JavaScriptObjectHelper.setIntegerProperty(dataLabels, "y", y);
		return this;
	}

	public PlotOption setDataLabelsZIndex(int zIndex) {
		createSeriesIfNotExists();
		createDataLabelsIfNotExists();
		JavaScriptObjectHelper.setIntegerProperty(dataLabels, "zIndex", zIndex);
		return this;
	}

	public PlotOption setEnableMouseTracking(boolean enable) {
		createSeriesIfNotExists();
		JavaScriptObjectHelper.setBooleanProperty(series, "enableMouseTracking", enable);
		return this;
	}

	public PlotOption setFillColor(String color) {
		createSeriesIfNotExists();
		JavaScriptObjectHelper.setStringProperty(series, "fillColor", color);
		return this;
	}

	public PlotOption setFillColor(JavaScriptObject color) {
		createSeriesIfNotExists();
		JavaScriptObjectHelper.setObjectProperty(series, "fillColor", color);
		return this;
	}

	public PlotOption setFillOpacity(double opacity) {
		createSeriesIfNotExists();
		JavaScriptObjectHelper.setDoubleProperty(series, "fillOpacity", opacity);
		return this;
	}

	public PlotOption setLineColor(String color) {
		createSeriesIfNotExists();
		JavaScriptObjectHelper.setStringProperty(series, "lineColor", color);
		return this;
	}

	public PlotOption setLineWidth(int width) {
		createSeriesIfNotExists();
		JavaScriptObjectHelper.setIntegerProperty(series, "lineWidth", width);
		return this;
	}

	public PlotOption setLinkedTo(String linkedTo) {
		createSeriesIfNotExists();
		JavaScriptObjectHelper.setStringProperty(series, "linkedTo", linkedTo);
		return this;
	}

	private void createMarkerIfNotExists() {
		if (marker == null) {
			marker = JavaScriptObject.createObject();
			JavaScriptObjectHelper.setObjectProperty(series, "marker", marker);
		}
	}

	public PlotOption setMarkerEnabled(boolean enable) {
		createSeriesIfNotExists();
		createMarkerIfNotExists();
		JavaScriptObjectHelper.setBooleanProperty(marker, "enabled", enable);
		return this;
	}

	public PlotOption setMarkerFillColor(String color) {
		createSeriesIfNotExists();
		createMarkerIfNotExists();
		JavaScriptObjectHelper.setStringProperty(marker, "fillColor", color);
		return this;
	}

	public PlotOption setMarkerHeight(int height) {
		createSeriesIfNotExists();
		createMarkerIfNotExists();
		JavaScriptObjectHelper.setIntegerProperty(marker, "height", height);
		return this;
	}

	public PlotOption setMarkerLineColor(String color) {
		createSeriesIfNotExists();
		createMarkerIfNotExists();
		JavaScriptObjectHelper.setStringProperty(marker, "lineColor", color);
		return this;
	}

	public PlotOption setMarkerLineWidth(int width) {
		createSeriesIfNotExists();
		createMarkerIfNotExists();
		JavaScriptObjectHelper.setIntegerProperty(marker, "lineWidth", width);
		return this;
	}

	public PlotOption setMarkerRadius(int radius) {
		createSeriesIfNotExists();
		createMarkerIfNotExists();
		JavaScriptObjectHelper.setIntegerProperty(marker, "radius", radius);
		return this;
	}

	public PlotOption setMarkerSymbol(String symbol) {
		createSeriesIfNotExists();
		createMarkerIfNotExists();
		JavaScriptObjectHelper.setStringProperty(marker, "symbol", symbol);
		return this;
	}

	public PlotOption setMarkerWidth(int width) {
		createSeriesIfNotExists();
		createMarkerIfNotExists();
		JavaScriptObjectHelper.setIntegerProperty(marker, "width", width);
		return this;
	}

	public PlotOption setNegativeColor(String color) {
		createSeriesIfNotExists();
		JavaScriptObjectHelper.setStringProperty(series, "negativeColor", color);
		return this;
	}

	public PlotOption setNegativeFillColor(String color) {
		createSeriesIfNotExists();
		JavaScriptObjectHelper.setStringProperty(series, "negativeFillColor", color);
		return this;
	}

	// point property

	public PlotOption setPointInterval(int interval) {
		createSeriesIfNotExists();
		JavaScriptObjectHelper.setIntegerProperty(series, "pointInterval", interval);
		return this;
	}

	public PlotOption setPointPlacement(String pointPlacement) {
		createSeriesIfNotExists();
		JavaScriptObjectHelper.setStringProperty(series, "pointPlacement", pointPlacement);
		return this;
	}

	public PlotOption setPointPlacement(int pointPlacement) {
		createSeriesIfNotExists();
		JavaScriptObjectHelper.setIntegerProperty(series, "pointPlacement", pointPlacement);
		return this;
	}

	public PlotOption setPointStart(int pointStart) {
		createSeriesIfNotExists();
		JavaScriptObjectHelper.setIntegerProperty(series, "pointStart", pointStart);
		return this;
	}

	public PlotOption setSelected(boolean select) {
		createSeriesIfNotExists();
		JavaScriptObjectHelper.setBooleanProperty(series, "selected", select);
		return this;
	}

	public PlotOption setShadow(boolean shadow) {
		createSeriesIfNotExists();
		JavaScriptObjectHelper.setBooleanProperty(series, "shadow", shadow);
		return this;
	}

	public PlotOption setShadow(JavaScriptObject shadow) {
		createSeriesIfNotExists();
		JavaScriptObjectHelper.setObjectProperty(series, "shadow", shadow);
		return this;
	}

	public PlotOption setShowCheckbox(boolean show) {
		createSeriesIfNotExists();
		JavaScriptObjectHelper.setBooleanProperty(series, "showCheckbox", show);
		return this;
	}

	public PlotOption setShowInLegend(boolean show) {
		createSeriesIfNotExists();
		JavaScriptObjectHelper.setBooleanProperty(series, "showInLegend", show);
		return this;
	}

	public PlotOption setStacking(String stacking) {
		createSeriesIfNotExists();
		JavaScriptObjectHelper.setStringProperty(series, "stacking", stacking);
		return this;
	}

	// states property

	// public PlotOption setStep(boolean step) {
	//
	// }

	public PlotOption setStickyTracking(boolean stickyTracking) {
		createSeriesIfNotExists();
		JavaScriptObjectHelper.setBooleanProperty(series, "stickyTracking", stickyTracking);
		return this;
	}

	public PlotOption setThreshold(int threshold) {
		createSeriesIfNotExists();
		JavaScriptObjectHelper.setIntegerProperty(series, "threshold", threshold);
		return this;
	}

	public PlotOption setTrackByArea(boolean trackByArea) {
		createSeriesIfNotExists();
		JavaScriptObjectHelper.setBooleanProperty(series, "trackByArea", trackByArea);
		return this;
	}

	public PlotOption setTurboThreshold(int turboThreshold) {
		createSeriesIfNotExists();
		JavaScriptObjectHelper.setIntegerProperty(series, "turboThreshold", turboThreshold);
		return this;
	}

	public PlotOption setVisible(boolean visible) {
		createSeriesIfNotExists();
		JavaScriptObjectHelper.setBooleanProperty(series, "visible", visible);
		return this;
	}

}