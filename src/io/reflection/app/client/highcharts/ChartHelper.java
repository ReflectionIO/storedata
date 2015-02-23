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

	public static Chart createAndInjectChart(HTMLPanel container) {
		Chart chart = new Chart();
		setDefaultOptions(chart);
		container.getElement().appendChild(chart.getElement());
		chart.inject();
		return chart;
	}

	public static Chart createAndInjectChart(DivElement container) {
		Chart chart = new Chart();
		setDefaultOptions(chart);
		container.appendChild(chart.getElement());
		chart.inject();
		return chart;
	}

	private static void setDefaultOptions(Chart chart) {
		chart.getChartOption().setPlotBackgroundColor("#fafafa").setPlotBorderColor("#e7e7e7").setPlotBorderWidth(1).setMargin(createMarginsArray(1, 1, 60, 1));
		chart.getPlotOption().setCursor("default").setFillOpacity(0.6).setFillColor("rgba(232,231,241,0.6)").setLineWidth(2).setMarkerEnabled(false)
				.setMarkerRadius(3).setHoverHaloOpacity(0).setHoverLineWidthPlus(0).setMarkerSymbol("circle").setMarkerHoverLineWidthPlus(0)
				.setPointInterval(86400000);
		chart.setColors(getDefaultColors());
		chart.getCreditsOption().setEnabled(false); // Disable highcharts credits text
		chart.getLegendOption().setEnabled(false); // Disable legend
		chart.getTitleOption().setText(null); // Disable title
		chart.getTooltipOption()
				.setUseHTML(true)
				.setShadow(false)
				.setBackgroundColor("#ffffff")
				.setBorderColor("#dedede")
				.setBorderWidth(1)
				.setBorderRadius(5)
				.setValueDecimals(0)
				.setCrosshairs(true)
				.setDateTimeLabelFormats(getDefaultTooltipDateTimeLabelFormat())
				.setHeaderFormat(
						"<span style=\"font-size: 10px; line-height: 30px; font-weight: bold; color: #81879d; font-family: 'Lato', sans-serif;\">{point.key}</span><br/>")
				.setPointFormat("<span style=\"font-size: 18px; font-weight: regular; color: #363a47; font-family: 'Lato', sans-serif;\">{point.y}</span>");
		chart.getXAxis().setType(Axis.TYPE_DATETIME).setDateTimeLabelFormats(getDefaultAxisDateTimeLabelFormat()).setTickWidth(0).setTickInterval(86400000)
				.setShowFirstLabel(false).setShowLastLabel(false).setLabelsStyle(getDefaultAxisStyle()).setLabelsY(30).setStartOnTick(false)
				.setEndOnTick(false).setMinPadding(0).setMaxPadding(0).setMinorGridLineWidth(0).setLineColor("#e5e5e5").setLabelsMaxStaggerLines(1)
				.setMinRange(86400000);
		chart.getYAxis().setAllowDecimals(false).setTitleText(null).setOffset(-30).setLabelsY(-7).setLabelsStyle(getDefaultAxisStyle()).setLabelsAlign("left")
				.setGridLineColor("#e1e5e8");
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

	public static native JavaScriptObject getNativeTooltipFormatter() /*-{
		return function() {
			return "<div><span style=\"font-size: 10px; font-weight: bold; color: #81879d; font-family: 'Lato', sans-serif;\">"
					+ $wnd.Highcharts.dateFormat('%e %b %Y', this.x, true)
					+ "</span><br/><span style=\"font-size: 18px; font-weight: regular; color: #363a47; font-family: 'Lato', sans-serif;\">"
					+ this.y + '</span></div>';
		}
	}-*/;

	public static native JavaScriptObject getNativeTooltipFormatter(String currency) /*-{
		return function() {
			return "<div class=\"pippo\"><span style=\"font-size: 10px; font-weight: bold; color: #81879d; font-family: 'Lato', sans-serif;\">"
					+ $wnd.Highcharts.dateFormat('%e %b %Y', this.x, true)
					+ "</span><br/><span style=\"font-size: 18px; font-weight: regular; color: #363a47; font-family: 'Lato', sans-serif;\">"
					+ currency
					+ " "
					+ $wnd.Highcharts.numberFormat(this.y, 0, '.', ',')
					+ "</span></div>";
		}
	}-*/;

	public static JavaScriptObject getDefaultAxisStyle() {
		HashMap<String, Object> styleValues = new HashMap<String, Object>();
		styleValues.put("fontSize", "12px");
		styleValues.put("fontWeight", "bold");
		styleValues.put("color", "#63656a");
		styleValues.put("fontFamily", "'Lato', sans-serif");
		return getJSObjectFromMap(styleValues);
	}

	// public static JavaScriptObject getDefaultTooltipStyle() {
	// HashMap<String, Object> styleValues = new HashMap<String, Object>();
	// styleValues.put("padding", 14);
	// return getJSObjectFromMap(styleValues);
	// }

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
