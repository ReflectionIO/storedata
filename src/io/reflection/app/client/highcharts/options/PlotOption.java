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
	private JavaScriptObject markerStates;
	private JavaScriptObject markerHover;

	private JavaScriptObject states;
	private JavaScriptObject hover;
	private JavaScriptObject halo;

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.highcharts.options.Option#getName()
	 */
	@Override
	public String getName() {
		return "plotOptions";
	}

	public JavaScriptObject getSeries() {
		if (series == null) {
			series = JavaScriptObject.createObject();
			setOption("series", series);
		}
		return series;
	}

	public PlotOption setAllowPointSelect(boolean allow) {
		JavaScriptObjectHelper.setBooleanProperty(getSeries(), "allowPointSelect", allow);
		return this;
	}

	public PlotOption setAnimation(boolean animate) {
		JavaScriptObjectHelper.setBooleanProperty(getSeries(), "animation", animate);
		return this;
	}

	public PlotOption setAnimation(JavaScriptObject animation) {
		JavaScriptObjectHelper.setObjectProperty(getSeries(), "animation", animation);
		return this;
	}

	public PlotOption setColor(String color) {
		JavaScriptObjectHelper.setStringProperty(getSeries(), "color", color);
		return this;
	}

	public PlotOption setConnectNulls(boolean connect) {
		JavaScriptObjectHelper.setBooleanProperty(getSeries(), "connectNulls", connect);
		return this;
	}

	public PlotOption setCropThreshold(int threshold) {
		JavaScriptObjectHelper.setIntegerProperty(getSeries(), "cropThreshold", threshold);
		return this;
	}

	public PlotOption setCursor(String cursor) {
		JavaScriptObjectHelper.setStringProperty(getSeries(), "cursor", cursor);
		return this;
	}

	public PlotOption setDashStyle(String dashStyle) {
		JavaScriptObjectHelper.setStringProperty(getSeries(), "dashStyle", dashStyle);
		return this;
	}

	public JavaScriptObject getDataLabels() {
		if (dataLabels == null) {
			dataLabels = JavaScriptObject.createObject();
			JavaScriptObjectHelper.setObjectProperty(getSeries(), "dataLabels", dataLabels);
		}
		return dataLabels;
	}

	public PlotOption setDataLabelsAlign(String align) {
		JavaScriptObjectHelper.setStringProperty(getDataLabels(), "align", align);
		return this;
	}

	public PlotOption setDataLabelsBackgroundColor(String color) {
		JavaScriptObjectHelper.setStringProperty(getDataLabels(), "backgroundColor", color);
		return this;
	}

	public PlotOption setDataLabelsBorderColor(String color) {
		JavaScriptObjectHelper.setStringProperty(getDataLabels(), "borderColor", color);
		return this;
	}

	public PlotOption setDataLabelsBorderRadius(int borderRadius) {
		JavaScriptObjectHelper.setIntegerProperty(getDataLabels(), "borderRadius", borderRadius);
		return this;
	}

	public PlotOption setDataLabelsBorderWidth(int borderWidth) {
		JavaScriptObjectHelper.setIntegerProperty(getDataLabels(), "borderWidth", borderWidth);
		return this;
	}

	public PlotOption setDataLabelsColor(String color) {
		JavaScriptObjectHelper.setStringProperty(getDataLabels(), "color", color);
		return this;
	}

	public PlotOption setDataLabelsCrop(boolean crop) {
		JavaScriptObjectHelper.setBooleanProperty(getDataLabels(), "crop", crop);
		return this;
	}

	public PlotOption setDataLabelsDefer(boolean defer) {
		JavaScriptObjectHelper.setBooleanProperty(getDataLabels(), "defer", defer);
		return this;
	}

	public PlotOption setDataLabelsEnabled(boolean enable) {
		JavaScriptObjectHelper.setBooleanProperty(getDataLabels(), "enabled", enable);
		return this;
	}

	public PlotOption setDataLabelsFormat(String format) {
		JavaScriptObjectHelper.setStringProperty(getDataLabels(), "format", format);
		return this;
	}

	public PlotOption setDataLabelsFormatter(JavaScriptObject formatterFunction) {
		JavaScriptObjectHelper.setObjectProperty(getDataLabels(), "formatter", formatterFunction);
		return this;
	}

	public PlotOption setDataLabelsInside(boolean inside) {
		JavaScriptObjectHelper.setBooleanProperty(getDataLabels(), "inside", inside);
		return this;
	}

	public PlotOption setDataLabelsOverflow(String overflow) {
		JavaScriptObjectHelper.setStringProperty(getDataLabels(), "overflow", overflow);
		return this;
	}

	public PlotOption setDataLabelsPadding(int padding) {
		JavaScriptObjectHelper.setIntegerProperty(getDataLabels(), "padding", padding);
		return this;
	}

	public PlotOption setDataLabelsRotation(int rotation) {
		JavaScriptObjectHelper.setIntegerProperty(getDataLabels(), "rotation", rotation);
		return this;
	}

	public PlotOption setDataLabelsShadow(boolean shadow) {
		JavaScriptObjectHelper.setBooleanProperty(getDataLabels(), "shadow", shadow);
		return this;
	}

	public PlotOption setDataLabelsShadow(JavaScriptObject shadow) {
		JavaScriptObjectHelper.setObjectProperty(getDataLabels(), "shadow", shadow);
		return this;
	}

	public PlotOption setDataLabelsStyle(JavaScriptObject style) {
		JavaScriptObjectHelper.setObjectProperty(getDataLabels(), "style", style);
		return this;
	}

	public PlotOption setDataLabelsUserHTML(boolean useHTML) {
		JavaScriptObjectHelper.setBooleanProperty(getDataLabels(), "useHTML", useHTML);
		return this;
	}

	public PlotOption setDataLabelsVerticalAlign(String alignment) {
		JavaScriptObjectHelper.setStringProperty(getDataLabels(), "verticalAlign", alignment);
		return this;
	}

	public PlotOption setDataLabelsX(int x) {
		JavaScriptObjectHelper.setIntegerProperty(getDataLabels(), "x", x);
		return this;
	}

	public PlotOption setDataLabelsY(int y) {
		JavaScriptObjectHelper.setIntegerProperty(getDataLabels(), "y", y);
		return this;
	}

	public PlotOption setDataLabelsZIndex(int zIndex) {
		JavaScriptObjectHelper.setIntegerProperty(getDataLabels(), "zIndex", zIndex);
		return this;
	}

	public PlotOption setEnableMouseTracking(boolean enable) {
		JavaScriptObjectHelper.setBooleanProperty(getSeries(), "enableMouseTracking", enable);
		return this;
	}

	public PlotOption setFillColor(String color) {
		JavaScriptObjectHelper.setStringProperty(getSeries(), "fillColor", color);
		return this;
	}

	public PlotOption setFillColor(JavaScriptObject color) {
		JavaScriptObjectHelper.setObjectProperty(getSeries(), "fillColor", color);
		return this;
	}

	public PlotOption setFillOpacity(double opacity) {
		JavaScriptObjectHelper.setDoubleProperty(getSeries(), "fillOpacity", opacity);
		return this;
	}

	public PlotOption setLineColor(String color) {
		JavaScriptObjectHelper.setStringProperty(getSeries(), "lineColor", color);
		return this;
	}

	public PlotOption setLineWidth(int width) {
		JavaScriptObjectHelper.setIntegerProperty(getSeries(), "lineWidth", width);
		return this;
	}

	public PlotOption setLinkedTo(String linkedTo) {
		JavaScriptObjectHelper.setStringProperty(getSeries(), "linkedTo", linkedTo);
		return this;
	}

	public JavaScriptObject getMarker() {
		if (marker == null) {
			marker = JavaScriptObject.createObject();
			JavaScriptObjectHelper.setObjectProperty(getSeries(), "marker", marker);
		}
		return marker;
	}

	public PlotOption setMarkerEnabled(boolean enable) {
		JavaScriptObjectHelper.setBooleanProperty(getMarker(), "enabled", enable);
		return this;
	}

	public PlotOption setMarkerFillColor(String color) {
		JavaScriptObjectHelper.setStringProperty(getMarker(), "fillColor", color);
		return this;
	}

	public PlotOption setMarkerHeight(int height) {
		JavaScriptObjectHelper.setIntegerProperty(getMarker(), "height", height);
		return this;
	}

	public PlotOption setMarkerLineColor(String color) {
		JavaScriptObjectHelper.setStringProperty(getMarker(), "lineColor", color);
		return this;
	}

	public PlotOption setMarkerLineWidth(int width) {
		JavaScriptObjectHelper.setIntegerProperty(getMarker(), "lineWidth", width);
		return this;
	}

	public PlotOption setMarkerRadius(int radius) {
		JavaScriptObjectHelper.setIntegerProperty(getMarker(), "radius", radius);
		return this;
	}

	public JavaScriptObject getMarkerStates() {
		if (markerStates == null) {
			markerStates = JavaScriptObject.createObject();
			JavaScriptObjectHelper.setObjectProperty(getMarker(), "states", markerStates);
		}
		return markerStates;
	}

	public JavaScriptObject getMarkerHover() {
		if (markerHover == null) {
			markerHover = JavaScriptObject.createObject();
			JavaScriptObjectHelper.setObjectProperty(getMarkerStates(), "hover", markerHover);
		}
		return markerHover;
	}

	public PlotOption setMarkerHoverLineWidthPlus(int width) {
		JavaScriptObjectHelper.setIntegerProperty(getMarkerHover(), "lineWidthPlus", width);
		return this;
	}

	public PlotOption setMarkerSymbol(String symbol) {
		JavaScriptObjectHelper.setStringProperty(getMarker(), "symbol", symbol);
		return this;
	}

	public PlotOption setMarkerWidth(int width) {
		JavaScriptObjectHelper.setIntegerProperty(getMarker(), "width", width);
		return this;
	}

	public PlotOption setNegativeColor(String color) {
		JavaScriptObjectHelper.setStringProperty(getSeries(), "negativeColor", color);
		return this;
	}

	public PlotOption setNegativeFillColor(String color) {
		JavaScriptObjectHelper.setStringProperty(getSeries(), "negativeFillColor", color);
		return this;
	}

	// point property

	public PlotOption setPointInterval(double interval) {
		JavaScriptObjectHelper.setDoubleProperty(getSeries(), "pointInterval", interval);
		return this;
	}

	public PlotOption setPointPlacement(String pointPlacement) {
		JavaScriptObjectHelper.setStringProperty(getSeries(), "pointPlacement", pointPlacement);
		return this;
	}

	public PlotOption setPointPlacement(int pointPlacement) {
		JavaScriptObjectHelper.setIntegerProperty(getSeries(), "pointPlacement", pointPlacement);
		return this;
	}

	public PlotOption setPointStart(int pointStart) {
		JavaScriptObjectHelper.setIntegerProperty(getSeries(), "pointStart", pointStart);
		return this;
	}

	public PlotOption setSelected(boolean select) {
		JavaScriptObjectHelper.setBooleanProperty(getSeries(), "selected", select);
		return this;
	}

	public PlotOption setShadow(boolean shadow) {
		JavaScriptObjectHelper.setBooleanProperty(getSeries(), "shadow", shadow);
		return this;
	}

	public PlotOption setShadow(JavaScriptObject shadow) {
		JavaScriptObjectHelper.setObjectProperty(getSeries(), "shadow", shadow);
		return this;
	}

	public PlotOption setShowCheckbox(boolean show) {
		JavaScriptObjectHelper.setBooleanProperty(getSeries(), "showCheckbox", show);
		return this;
	}

	public PlotOption setShowInLegend(boolean show) {
		JavaScriptObjectHelper.setBooleanProperty(getSeries(), "showInLegend", show);
		return this;
	}

	public PlotOption setStacking(String stacking) {
		JavaScriptObjectHelper.setStringProperty(getSeries(), "stacking", stacking);
		return this;
	}

	public JavaScriptObject getStates() {
		if (states == null) {
			states = JavaScriptObject.createObject();
			JavaScriptObjectHelper.setObjectProperty(getSeries(), "states", states);
		}
		return states;
	}

	public JavaScriptObject getHover() {
		if (hover == null) {
			hover = JavaScriptObject.createObject();
			JavaScriptObjectHelper.setObjectProperty(getStates(), "hover", hover);
		}
		return hover;
	}

	public JavaScriptObject getHalo() {
		if (halo == null) {
			halo = JavaScriptObject.createObject();
			JavaScriptObjectHelper.setObjectProperty(getHover(), "halo", halo);
		}
		return halo;
	}

	public PlotOption setHoverHaloOpacity(int opacity) {
		JavaScriptObjectHelper.setIntegerProperty(getHalo(), "opacity", opacity);
		return this;
	}

	public PlotOption setHoverLineWidthPlus(int width) {
		JavaScriptObjectHelper.setIntegerProperty(getHover(), "lineWidthPlus", width);
		return this;
	}

	// states property

	// public PlotOption setStep(boolean step) {
	//
	// }

	public PlotOption setStickyTracking(boolean stickyTracking) {
		JavaScriptObjectHelper.setBooleanProperty(getSeries(), "stickyTracking", stickyTracking);
		return this;
	}

	public PlotOption setThreshold(int threshold) {
		JavaScriptObjectHelper.setIntegerProperty(getSeries(), "threshold", threshold);
		return this;
	}

	public PlotOption setTrackByArea(boolean trackByArea) {
		JavaScriptObjectHelper.setBooleanProperty(getSeries(), "trackByArea", trackByArea);
		return this;
	}

	public PlotOption setTurboThreshold(int turboThreshold) {
		JavaScriptObjectHelper.setIntegerProperty(getSeries(), "turboThreshold", turboThreshold);
		return this;
	}

	public PlotOption setVisible(boolean visible) {
		JavaScriptObjectHelper.setBooleanProperty(getSeries(), "visible", visible);
		return this;
	}

}