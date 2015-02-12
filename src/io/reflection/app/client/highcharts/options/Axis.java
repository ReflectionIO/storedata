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

	public T setDateTimeLabelFormats(JavaScriptObject dateTimeLabelFormats) {
		return setOption("dateTimeLabelFormats", dateTimeLabelFormats);
	}

	public T setEndOnTick(boolean endOnTick) {
		return setOption("endOnTick", endOnTick);
	}

	@SuppressWarnings("unchecked")
	public T setAfterSetExtremes(JavaScriptObject afterSetExtremesFunction) {
		if (events == null) {
			events = JavaScriptObject.createObject();
			setOption("events", events);
		}
		JavaScriptObjectHelper.setObjectProperty(events, "afterSetExtremes", afterSetExtremesFunction);
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T setSetExtremes(JavaScriptObject setExtremesFunction) {
		if (events == null) {
			events = JavaScriptObject.createObject();
			setOption("events", events);
		}
		JavaScriptObjectHelper.setObjectProperty(events, "setExtremes", setExtremesFunction);
		return (T) this;
	}

	public T setFloor(int floor) {
		return setOption("floor", floor);
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

	@SuppressWarnings("unchecked")
	public T setLabelsAlign(String align) {
		if (labels == null) {
			labels = JavaScriptObject.createObject();
			setOption("labels", labels);
		}
		JavaScriptObjectHelper.setStringProperty(labels, "align", align);
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T setLabelsDistance(int distance) {
		if (labels == null) {
			labels = JavaScriptObject.createObject();
			setOption("labels", labels);
		}
		JavaScriptObjectHelper.setIntegerProperty(labels, "distance", distance);
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T setLabelsEnabled(boolean enable) {
		if (labels == null) {
			labels = JavaScriptObject.createObject();
			setOption("labels", labels);
		}
		JavaScriptObjectHelper.setBooleanProperty(labels, "enabled", enable);
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T setLabelsFormat(String format) {
		if (labels == null) {
			labels = JavaScriptObject.createObject();
			setOption("labels", labels);
		}
		JavaScriptObjectHelper.setStringProperty(labels, "format", format);
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T setLabelsFormatter(JavaScriptObject formatterFunction) {
		if (labels == null) {
			labels = JavaScriptObject.createObject();
			setOption("labels", labels);
		}
		JavaScriptObjectHelper.setObjectProperty(labels, "formatter", formatterFunction);
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T setLabelsMaxStaggerLines(int maxStaggerLines) {
		if (labels == null) {
			labels = JavaScriptObject.createObject();
			setOption("labels", labels);
		}
		JavaScriptObjectHelper.setIntegerProperty(labels, "maxStaggerLines", maxStaggerLines);
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T setLabelsOverflow(String overflow) {
		if (labels == null) {
			labels = JavaScriptObject.createObject();
			setOption("labels", labels);
		}
		JavaScriptObjectHelper.setStringProperty(labels, "overflow", overflow);
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T setLabelsRotation(int degreeRotation) {
		if (labels == null) {
			labels = JavaScriptObject.createObject();
			setOption("labels", labels);
		}
		JavaScriptObjectHelper.setIntegerProperty(labels, "rotation", degreeRotation);
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T setLabelsStaggerLines(int staggerLines) {
		if (labels == null) {
			labels = JavaScriptObject.createObject();
			setOption("labels", labels);
		}
		JavaScriptObjectHelper.setIntegerProperty(labels, "staggerLines", staggerLines);
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T setLabelsStep(int step) {
		if (labels == null) {
			labels = JavaScriptObject.createObject();
			setOption("labels", labels);
		}
		JavaScriptObjectHelper.setIntegerProperty(labels, "step", step);
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T setLabelsStyle(JavaScriptObject style) {
		if (labels == null) {
			labels = JavaScriptObject.createObject();
			setOption("labels", labels);
		}
		JavaScriptObjectHelper.setObjectProperty(labels, "style", style);
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T setLabelsUseHTML(boolean useHTML) {
		if (labels == null) {
			labels = JavaScriptObject.createObject();
			setOption("labels", labels);
		}
		JavaScriptObjectHelper.setBooleanProperty(labels, "useHTML", useHTML);
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T setLabelsX(int x) {
		if (labels == null) {
			labels = JavaScriptObject.createObject();
			setOption("labels", labels);
		}
		JavaScriptObjectHelper.setIntegerProperty(labels, "x", x);
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T setLabelsY(int y) {
		if (labels == null) {
			labels = JavaScriptObject.createObject();
			setOption("labels", labels);
		}
		JavaScriptObjectHelper.setIntegerProperty(labels, "y", y);
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T setLabelsZIndex(int zIndex) {
		if (labels == null) {
			labels = JavaScriptObject.createObject();
			setOption("labels", labels);
		}
		JavaScriptObjectHelper.setIntegerProperty(labels, "zIndex", zIndex);
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

	public T setMaxPadding(int maxPadding) {
		return setOption("maxPadding", maxPadding);
	}

	public T setMin(double min) {
		return setOption("min", min);
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

	@SuppressWarnings("unchecked")
	public T setTitleAlign(String align) {
		if (title == null) {
			title = JavaScriptObject.createObject();
			setOption("title", title);
		}
		JavaScriptObjectHelper.setStringProperty(title, "align", align);
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T setTitleMargin(int margin) {
		if (title == null) {
			title = JavaScriptObject.createObject();
			setOption("title", title);
		}
		JavaScriptObjectHelper.setIntegerProperty(title, "margin", margin);
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T setTitleOffset(int offset) {
		if (title == null) {
			title = JavaScriptObject.createObject();
			setOption("title", title);
		}
		JavaScriptObjectHelper.setIntegerProperty(title, "offset", offset);
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T setTitleRotation(int rotation) {
		if (title == null) {
			title = JavaScriptObject.createObject();
			setOption("title", title);
		}
		JavaScriptObjectHelper.setIntegerProperty(title, "rotation", rotation);
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T setTitleStyle(JavaScriptObject style) {
		if (title == null) {
			title = JavaScriptObject.createObject();
			setOption("title", title);
		}
		JavaScriptObjectHelper.setObjectProperty(title, "style", style);
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T setTitleText(String text) {
		if (title == null) {
			title = JavaScriptObject.createObject();
			setOption("title", title);
		}
		JavaScriptObjectHelper.setStringProperty(title, "text", text);
		return (T) this;
	}

	public T setType(String type) {
		return setOption("type", type);
	}

	public T setUnits(JsArrayMixed units) {
		return setOption("units", units);
	}

}
