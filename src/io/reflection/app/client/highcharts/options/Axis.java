//
//  Axis.java
//  storedata
//
//  Created by Stefano Capuzzi on 31 Jan 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.highcharts.options;

import io.reflection.app.client.helper.JavaScriptObjectHelper;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayMixed;
import com.google.gwt.core.client.JsArrayNumber;
import com.google.gwt.core.client.JsArrayString;

/**
 * @author Stefano Capuzzi
 *
 */
@SuppressWarnings("rawtypes")
public abstract class Axis<T extends Option> extends Option<T> {

	public static final String TYPE_LINEAR = "linear";
	public static final String TYPE_DATETIME = "datetime";
	public static final String TYPE_LOGARITHMIC = "logarithmic";

	private JavaScriptObject events;
	private JavaScriptObject labels;
	private JavaScriptObject title;

	public T setAllowDecimals(boolean allow) {
		return setOption("allowDecimals", allow);
	}

	public T setAlternateGridColor(String color) {
		return setOption("alternateGridColor", color);
	}

	public T setCategories(JsArrayString categories) {
		return setOption("categories", categories);
	}

	public T setCeiling(int ceiling) {
		return setOption("ceiling", ceiling);
	}

	public T setCeiling(JavaScriptObject nullValue) {
		return setOption("ceiling", nullValue);
	}

	public T setDateTimeLabelFormats(JavaScriptObject dateTimeLabelFormats) {
		return setOption("dateTimeLabelFormats", dateTimeLabelFormats);
	}

	public T setEndOnTick(boolean endOnTick) {
		return setOption("endOnTick", endOnTick);
	}

	public JavaScriptObject getEvents() {
		if (events == null) {
			events = JavaScriptObject.createObject();
			setOption("events", events);
		}
		return events;
	}

	@SuppressWarnings("unchecked")
	public T setAfterSetExtremes(JavaScriptObject afterSetExtremesFunction) {
		JavaScriptObjectHelper.setObjectProperty(getEvents(), "afterSetExtremes", afterSetExtremesFunction);
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T setSetExtremes(JavaScriptObject setExtremesFunction) {
		JavaScriptObjectHelper.setObjectProperty(getEvents(), "setExtremes", setExtremesFunction);
		return (T) this;
	}

	public T setFloor(int floor) {
		return setOption("floor", floor);
	}

	public T setFloor(JavaScriptObject nullValue) {
		return setOption("floor", nullValue);
	}

	public T setGridLineColor(String color) {
		return setOption("gridLineColor", color);
	}

	public T setGridLineDashStyle(String dashStyle) {
		return setOption("gridLineDashStyle", dashStyle);
	}

	public T setGridLineWidth(int width) {
		return setOption("gridLineWidth", width);
	}

	public T setGridZIndex(int index) {
		return setOption("gridZIndex", index);
	}

	public T setId(String id) {
		return setOption("id", id);
	}

	public JavaScriptObject getLabels() {
		if (labels == null) {
			labels = JavaScriptObject.createObject();
			setOption("labels", labels);
		}
		return labels;
	}

	@SuppressWarnings("unchecked")
	public T setLabelsAlign(String align) {
		JavaScriptObjectHelper.setStringProperty(getLabels(), "align", align);
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T setLabelsDistance(int distance) {
		JavaScriptObjectHelper.setIntegerProperty(getLabels(), "distance", distance);
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T setLabelsEnabled(boolean enable) {
		JavaScriptObjectHelper.setBooleanProperty(getLabels(), "enabled", enable);
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T setLabelsFormat(String format) {
		JavaScriptObjectHelper.setStringProperty(getLabels(), "format", format);
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T setLabelsFormatter(JavaScriptObject formatterFunction) {
		JavaScriptObjectHelper.setObjectProperty(getLabels(), "formatter", formatterFunction);
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T setLabelsMaxStaggerLines(int maxStaggerLines) {
		JavaScriptObjectHelper.setIntegerProperty(getLabels(), "maxStaggerLines", maxStaggerLines);
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T setLabelsOverflow(String overflow) {
		JavaScriptObjectHelper.setStringProperty(getLabels(), "overflow", overflow);
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T setLabelsRotation(int degreeRotation) {
		JavaScriptObjectHelper.setIntegerProperty(getLabels(), "rotation", degreeRotation);
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T setLabelsStaggerLines(int staggerLines) {
		JavaScriptObjectHelper.setIntegerProperty(getLabels(), "staggerLines", staggerLines);
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T setLabelsStep(int step) {
		JavaScriptObjectHelper.setIntegerProperty(getLabels(), "step", step);
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T setLabelsStyle(JavaScriptObject style) {
		JavaScriptObjectHelper.setObjectProperty(getLabels(), "style", style);
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T setLabelsUseHTML(boolean useHTML) {
		JavaScriptObjectHelper.setBooleanProperty(getLabels(), "useHTML", useHTML);
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T setLabelsX(int x) {
		JavaScriptObjectHelper.setIntegerProperty(getLabels(), "x", x);
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T setLabelsY(int y) {
		JavaScriptObjectHelper.setIntegerProperty(getLabels(), "y", y);
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T setLabelsZIndex(int zIndex) {
		JavaScriptObjectHelper.setIntegerProperty(getLabels(), "zIndex", zIndex);
		return (T) this;
	}

	public T setLineColor(String color) {
		return setOption("lineColor", color);
	}

	public T setLineWidth(int lineWidth) {
		return setOption("lineWidth", lineWidth);
	}

	public T setLinkedTo(int linkedTo) {
		return setOption("linkedTo", linkedTo);
	}

	public T setMax(double max) {
		return setOption("max", max);
	}

	public T setMax(JavaScriptObject nullValue) {
		return setOption("max", nullValue);
	}

	public T setMaxPadding(int maxPadding) {
		return setOption("maxPadding", maxPadding);
	}

	public T setMin(double min) {
		return setOption("min", min);
	}

	public T setMin(JavaScriptObject nullValue) {
		return setOption("min", nullValue);
	}

	public T setMinPadding(int minPadding) {
		return setOption("minPadding", minPadding);
	}

	public T setMinRange(int minRange) {
		return setOption("minRange", minRange);
	}

	public T setMinTickInterval(int minTickInterval) {
		return setOption("minTickInterval", minTickInterval);
	}

	public T setMinorGridLineColor(String color) {
		return setOption("minorGridLineColor", color);
	}

	public T setMinorGridLineDashStyle(String dashStyle) {
		return setOption("minorGridLineDashStyle", dashStyle);
	}

	public T setMinorGridLineWidth(int width) {
		return setOption("minorGridLineWidth", width);
	}

	public T setMinorTickColor(String color) {
		return setOption("minorTickColor", color);
	}

	public T setMinorTickInterval(String interval) {
		return setOption("minorTickInterval", interval);
	}

	public T setMinorTickInterval(int interval) {
		return setOption("minorTickInterval", interval);
	}

	public T setMinorTickLegth(int length) {
		return setOption("minorTickLength", length);
	}

	public T setMinorTickPosition(String position) {
		return setOption("minorTickPosition", position);
	}

	public T setMinorTickWidth(int width) {
		return setOption("minorTickWidth", width);
	}

	public T setOffset(int offset) {
		return setOption("offset", offset);
	}

	public T setOpposite(boolean opposite) {
		return setOption("opposite", opposite);
	}

	// TODO plotBands and plotLines

	public T setReversed(boolean reversed) {
		return setOption("reversed", reversed);
	}

	public T setShowEmpty(boolean showEmpty) {
		return setOption("showEmpty", showEmpty);
	}

	public T setShowFirstLabel(boolean showFirstLabel) {
		return setOption("showFirstLabel", showFirstLabel);
	}

	public T setShowLastLabel(boolean showLastLabel) {
		return setOption("showLastLabel", showLastLabel);
	}

	public T setStartOfWeek(int startOfWeek) {
		return setOption("startOfWeek", startOfWeek);
	}

	public T setStartOnTick(boolean startOnTick) {
		return setOption("startOnTick", startOnTick);
	}

	public T setTickColor(String color) {
		return setOption("tickColor", color);
	}

	public T setTickInterval(int tickInterval) {
		return setOption("tickInterval", tickInterval);
	}

	public T setTickLength(int tickLength) {
		return setOption("tickLength", tickLength);
	}

	public T setTickPixelInterval(int tickPixelInterval) {
		return setOption("tickPixelInterval", tickPixelInterval);
	}

	public T setTickPosition(String position) {
		return setOption("tickPosition", position);
	}

	public T setTickPositioner(JavaScriptObject tickPositionerFunction) {
		return setOption("tickPositioner", tickPositionerFunction);
	}

	public T setTickPositions(JsArrayNumber positions) {
		return setOption("tickPositions", positions);
	}

	public T setTickWidth(int tickWidth) {
		return setOption("tickWidth", tickWidth);
	}

	public T setTickmarkPlacement(String tickmarkPlacement) {
		return setOption("tickmarkPlacement", tickmarkPlacement);
	}

	public JavaScriptObject getTitle() {
		if (title == null) {
			title = JavaScriptObject.createObject();
			setOption("title", title);
		}
		return title;
	}

	@SuppressWarnings("unchecked")
	public T setTitleAlign(String align) {
		JavaScriptObjectHelper.setStringProperty(getTitle(), "align", align);
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T setTitleMargin(int margin) {
		JavaScriptObjectHelper.setIntegerProperty(getTitle(), "margin", margin);
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T setTitleOffset(int offset) {
		JavaScriptObjectHelper.setIntegerProperty(getTitle(), "offset", offset);
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T setTitleRotation(int rotation) {
		JavaScriptObjectHelper.setIntegerProperty(getTitle(), "rotation", rotation);
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T setTitleStyle(JavaScriptObject style) {
		JavaScriptObjectHelper.setObjectProperty(getTitle(), "style", style);
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T setTitleText(String text) {
		JavaScriptObjectHelper.setStringProperty(getTitle(), "text", text);
		return (T) this;
	}

	public T setType(String type) {
		return setOption("type", type);
	}

	public T setUnits(JsArrayMixed units) {
		return setOption("units", units);
	}

}
