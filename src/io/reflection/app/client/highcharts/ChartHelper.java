//
//  ChartHelper.java
//  storedata
//
//  Created by Stefano Capuzzi on 18 Jan 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.highcharts;

import static io.reflection.app.client.controller.FilterController.DOWNLOADS_CHART_TYPE;
import static io.reflection.app.client.controller.FilterController.FREE_LIST_TYPE;
import static io.reflection.app.client.controller.FilterController.GROSSING_LIST_TYPE;
import static io.reflection.app.client.controller.FilterController.PAID_LIST_TYPE;
import static io.reflection.app.client.controller.FilterController.RANKING_CHART_TYPE;
import static io.reflection.app.client.controller.FilterController.REVENUE_CHART_TYPE;
import io.reflection.app.client.helper.JavaScriptObjectHelper;
import io.reflection.app.client.highcharts.options.Axis;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayBoolean;
import com.google.gwt.core.client.JsArrayMixed;
import com.google.gwt.core.client.JsArrayNumber;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.dom.client.Style.Cursor;

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

	public static final String CHART_BACKGROUND_COLOR = "#f2f2f5";
	public static final String PLOT_BACKGROUND_COLOR_TOP = "rgba(109,105,197,0.14)";
	public static final String PLOT_BACKGROUND_COLOR_BOTTOM = "rgba(27,199,159,0.14)";
	public static final String BORDER_COLOR = "#f2f2f5";
	public static final String GRID_COLOR = "#e1e5e8";
	public static final int ONE_DAY_INTERVAL = 86400000;
	public static final int ONE_WEEK_INTERVAL = 604800000;

	public enum XDataType {
		DateXAxisDataType,
		RankingXAxisDataType;
	}

	public enum YDataType {
		RevenueYAxisDataType,
		DownloadsYAxisDataType,
		RankingYAxisDataType;

		/**
		 * @param value
		 * @return
		 */
		public static YDataType fromString(String value) {
			YDataType dataType = null;

			switch (value) {
			case RANKING_CHART_TYPE:
				dataType = RankingYAxisDataType;
				break;
			case DOWNLOADS_CHART_TYPE:
				dataType = DownloadsYAxisDataType;
				break;
			case REVENUE_CHART_TYPE:
				dataType = RevenueYAxisDataType;
				break;
			}

			return dataType;
		}
	}

	public enum RankType {
		PositionRankingType,
		GrossingPositionRankingType;

		/**
		 * @param value
		 * @return
		 */
		public static RankType fromString(String value) {
			RankType rankType = null;

			switch (value) {
			case FREE_LIST_TYPE:
			case PAID_LIST_TYPE:
				rankType = PositionRankingType;
				break;
			case GROSSING_LIST_TYPE:
			default:
				rankType = GrossingPositionRankingType;
				break;
			}

			return rankType;
		}
	}

	public static void setDefaultOptions(BaseChart chart) {
		chart.getChartOption().setBackgroundColor(getLinearGradientColor(0, 1, 0, 0, "#ffffff", CHART_BACKGROUND_COLOR))
				.setPlotBackgroundColor(CHART_BACKGROUND_COLOR).setPlotBorderColor(BORDER_COLOR).setPlotBorderWidth(1)
				.setSpacing(createMarginsArray(40, 1, 1, 1));
		chart.getPlotOption().setCursor(Cursor.DEFAULT.getCssName()).setMarkerEnabled(false).setMarkerRadius(4).setMarkerHoverRadius(4)
				.setMarkerHoverRadiusPlus(0).setMarkerHoverLineWidthPlus(0).setMarkerLineWidth(0).setHoverHaloOpacity(0.2).setHoverHaloSize(12)
				.setHoverLineWidthPlus(0).setMarkerSymbol("circle");
		chart.getPlotOption()
				.setFillColor(getLinearGradientColor(0, 1, 0, 0, PLOT_BACKGROUND_COLOR_BOTTOM, PLOT_BACKGROUND_COLOR_TOP),
						chart.getPlotOption().getAreaSeriesType()).setLineWidth(3, chart.getPlotOption().getAreaSeriesType());
		chart.getPlotOption().setLineWidth(3, chart.getPlotOption().getLineSeriesType());
		JsArrayString colors = JavaScriptObject.createArray().cast();
		colors.push("#6a64c4");
		chart.setColors(colors);
		chart.getCreditsOption().setEnabled(false); // Disable credits text
		chart.getLegendOption().setEnabled(false); // Disable legend
		chart.getTitleOption().setText(null); // Disable title
		chart.getTooltipOption().setUseHTML(true).setShadow(false).setBackgroundColor("#ffffff").setBorderColor("#dedede").setBorderWidth(1).setBorderRadius(0)
				.setValueDecimals(0).setCrosshairs(getDefaultCrosshairStyle()).setDateTimeLabelFormats(getDefaultTooltipDateTimeLabelFormat())
				.setFormatter(getNativeTooltipFormatter());
		// chart.getTooltipOption().setFormatter(ChartHelper.getNativeTooltipFormatter("asd"));
		// .setHeaderFormat(
		// "<span style=\"font-size: 10px; line-height: 30px; font-weight: bold; color: #81879d; font-family: 'Lato', sans-serif;\">{point.key}</span><br/>")
		// .setPointFormat("<span style=\"font-size: 18px; font-weight: regular; color: #363a47; font-family: 'Lato', sans-serif;\">{point.y}</span>");
		chart.getXAxis().setTickWidth(1).setTickLength(10).setTickColor("#e7e7e7").setLabelsStyle(getDefaultXAxisLabelStyle()).setLabelsY(30)
				.setStartOnTick(true).setEndOnTick(true).setMinPadding(0).setMaxPadding(0).setLineColor("#e5e5e5").setLabelsMaxStaggerLines(1)
				.setLabelsPadding(30).setLabelsUseHTML(false).setLabelsAlign("center");
		chart.getYAxis().setAllowDecimals(false).setTitleText(null).setOffset(-30).setLabelsY(5).setLabelsStyle(getDefaultYAxisLabelStyle())
				.setLabelsAlign("left").setGridLineColor(GRID_COLOR).setLabelsUseHTML(false);
	}

	public static void setDefaultXAxisOptions(BaseChart chart, XDataType xDataType) {
		switch (xDataType) {
		case DateXAxisDataType:
			chart.getPlotOption().setPointInterval(ONE_DAY_INTERVAL);
			chart.getXAxis().setType(Axis.TYPE_DATETIME).setMinTickInterval(ONE_DAY_INTERVAL).setTickInterval(ONE_DAY_INTERVAL).setMinRange(ONE_DAY_INTERVAL);
			break;
		case RankingXAxisDataType:
			chart.getXAxis().setType(Axis.TYPE_LINEAR);
			break;
		}
	}

	public static void setDefaultYAxisOptions(BaseChart chart, YDataType yDataType) {
		switch (yDataType) {
		case RevenueYAxisDataType:
			chart.getYAxis().setMin(0).setFloor(0).setShowFirstLabel(false);
			break;
		case DownloadsYAxisDataType:
			chart.getYAxis().setMin(0).setFloor(0).setShowFirstLabel(false);
			break;
		case RankingYAxisDataType:
			chart.getYAxis().setReversed(true).setLabelsFormatter(getNativeLabelFormatterRank()).setMin(1).setFloor(1).setShowLastLabel(false);
			break;
		}
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

	public static JavaScriptObject getJSObjectFromMap(HashMap<String, Object> map) {
		JavaScriptObject JSObject = JavaScriptObject.createObject();
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			if (entry.getValue() instanceof String) {
				JavaScriptObjectHelper.setStringProperty(JSObject, entry.getKey(), (String) entry.getValue());
			}
			if (entry.getValue() instanceof Integer) {
				JavaScriptObjectHelper.setIntegerProperty(JSObject, entry.getKey(), (Integer) entry.getValue());
			}
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

	public static JsArrayString getDefaultColors() {
		JsArrayString colors = JavaScriptObject.createArray().cast();
		colors.push("#6a64c4");
		colors.push("#ff496a");
		colors.push("#1bc79f");
		colors.push("#29292c");
		return colors;
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

	public static native JavaScriptObject getNativeLabelFormatter(String prefix, String suffix) /*-{
		return function() {
			return prefix + this.axis.defaultLabelFormatter.call(this) + suffix;
		}
	}-*/;

	public static native JavaScriptObject getNativeLabelFormatterRank() /*-{
		return function() {
			if (this.isFirst)
				return 1;
			return this.value;
		}
	}-*/;

	public static native JavaScriptObject getNativeDatetimeLabelFormatter(JavaScriptObject chart, int step) /*-{
		return function() {
			var from = chart.xAxis[0].getExtremes().min;
			var diffMs = this.value - from;
			var diffDays = diffMs / 86400000; // Days between first X-Axis value and current one
			if (step == 1 || diffDays % step == 0) {
				return $wnd.Highcharts.dateFormat('%e %b', this.value, true);
			} else {
				return null;
			}
		}
	}-*/;

	public static native JavaScriptObject getNativeTooltipFormatter() /*-{
		return function() {
			return "<div><span style=\"font-size: 10px; font-weight: bold; color: #81879d; font-family: 'Lato', sans-serif;\">"
					+ $wnd.Highcharts.dateFormat('%e %b %Y', this.x, true)
					+ "</span><br/><span style=\"font-size: 18px; font-weight: regular; color: #363a47; font-family: 'Lato', sans-serif;\">"
					+ $wnd.Highcharts.numberFormat(this.y, 0, '.', ',')
					+ '</span></div>';
		}
	}-*/;

	public static native JavaScriptObject getNativeTooltipFormatter(String currency) /*-{
		return function() {
			return "<div><span style=\"font-size: 10px; font-weight: bold; color: #81879d; font-family: 'Lato', sans-serif;\">"
					+ $wnd.Highcharts.dateFormat('%e %b %Y', this.x, true)
					+ "</span><br/><span style=\"font-size: 18px; font-weight: regular; color: #363a47; font-family: 'Lato', sans-serif;\">"
					+ currency
					+ " "
					+ $wnd.Highcharts.numberFormat(this.y, 0, '.', ',')
					+ "</span></div>";
		}
	}-*/;

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

	public static JavaScriptObject getDefaultXAxisLabelStyle() {
		HashMap<String, Object> styleValues = new HashMap<String, Object>();
		styleValues.put("fontSize", "13px");
		styleValues.put("fontWeight", "regular");
		styleValues.put("color", "#63656a");
		styleValues.put("fontFamily", "'Lato', sans-serif");
		styleValues.put("padingLeft", "20px");
		styleValues.put("padingRight", "20px");
		return getJSObjectFromMap(styleValues);
	}

	public static JavaScriptObject getDefaultYAxisLabelStyle() {
		HashMap<String, Object> styleValues = new HashMap<String, Object>();
		styleValues.put("fontSize", "13px");
		styleValues.put("fontWeight", "regular");
		styleValues.put("color", "#81879d");
		styleValues.put("fontFamily", "'Lato', sans-serif");
		return getJSObjectFromMap(styleValues);
	}

	public static JavaScriptObject getDefaultTooltipStyle() {
		HashMap<String, Object> styleValues = new HashMap<String, Object>();
		// styleValues.put("overflow", "visible");
		return getJSObjectFromMap(styleValues);
	}

	public static JavaScriptObject getDefaultCrosshairStyle() {
		HashMap<String, Object> styleValues = new HashMap<String, Object>();
		styleValues.put("width", 1);
		styleValues.put("color", "#ff466a");
		return getJSObjectFromMap(styleValues);
	}

	public static JavaScriptObject getDefaultLoadingStyle() {
		HashMap<String, Object> styleValues = new HashMap<String, Object>();
		styleValues.put("backgroundImage", "url('http://jsfiddle.net/img/logo.png')");
		styleValues.put("display", "block");
		return getJSObjectFromMap(styleValues);
	}

	public static JsArrayMixed createPoint(double x, double y) {
		JsArrayMixed point = JavaScriptObject.createArray().cast();
		point.push(x);
		point.push(y);
		return point;
	}

	public static JsArrayMixed createPoint(double x, JavaScriptObject y) {
		JsArrayMixed point = JavaScriptObject.createArray().cast();
		point.push(x);
		point.push(y);
		return point;
	}

	public static JavaScriptObject createMarkerPoint(double x, double y) {
		JavaScriptObject point = JavaScriptObject.createObject();
		JavaScriptObjectHelper.setDoubleProperty(point, "x", x);
		JavaScriptObjectHelper.setDoubleProperty(point, "y", y);
		JavaScriptObject marker = JavaScriptObject.createObject();
		JavaScriptObjectHelper.setBooleanProperty(marker, "enabled", true);
		JavaScriptObjectHelper.setIntegerProperty(marker, "radius", 4);
		JavaScriptObjectHelper.setObjectProperty(point, "marker", marker);
		return point;
	}

}
