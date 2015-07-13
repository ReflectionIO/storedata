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
import io.reflection.app.client.helper.ColorHelper;
import io.reflection.app.client.helper.JavaScriptObjectHelper;
import io.reflection.app.client.highcharts.options.Axis;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayBoolean;
import com.google.gwt.core.client.JsArrayNumber;
import com.google.gwt.dom.client.Style.Cursor;

/**
 * @author Stefano Capuzzi
 *
 */
public class ChartHelper {

	public static final String PLOT_BACKGROUND_COLOR_TOP = "rgba(109,105,197,0.14)";
	public static final String PLOT_BACKGROUND_COLOR_BOTTOM = "rgba(27,199,159,0.14)";
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

	public enum YAxisPosition {
		PRIMARY("yAxisPrimary"),
		SECONDARY("yAxisSecondary"),
		TERTIARY("yAxisTertiary");

		private final String position;

		/**
		 * @param text
		 */
		private YAxisPosition(final String position) {
			this.position = position;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Enum#toString()
		 */
		@Override
		public String toString() {
			return position;
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

	public enum LineType {
		LINE("line"),
		SPLINE("spline"),
		AREA("area"),
		AREASPLINE("areaspline"),
		COLUMN("column"),
		BAR("bar"),
		PIE("pie"),
		SCATTER("scatter");

		private final String type;

		/**
		 * @param text
		 */
		private LineType(final String type) {
			this.type = type;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Enum#toString()
		 */
		@Override
		public String toString() {
			return type;
		}
	}

	public enum DashStyle {
		SOLID("Solid"),
		SHORT_DASH("ShortDash"),
		SHORT_DOT("ShortDot"),
		SHORT_DASH_DOT("ShortDashDot"),
		DASH_DOT_DOT("ShortDashDotDot"),
		DOT("Dot"),
		DASH("Dash"),
		LONG_DASH("LongDash"),
		DASH_DOT("DashDot"),
		LONG_DASH_DOT("LongDashDot"),
		LONG_DASH_DOT_DOT("LongDashDotDot");

		private final String style;

		/**
		 * @param text
		 */
		private DashStyle(final String style) {
			this.style = style;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Enum#toString()
		 */
		@Override
		public String toString() {
			return style;
		}
	}

	public static void setDefaultOptions(BaseChart chart) {
		chart.getChartOption().setBackgroundColor(ColorHelper.getLinearGradientColor(0, 1, 0, 0, "#ffffff", ColorHelper.getPanelGrey()))
				.setPlotBackgroundColor(ColorHelper.getPanelGrey()).setPlotBorderColor(ColorHelper.getPanelGrey()).setPlotBorderWidth(1)
				.setSpacing(createMarginsArray(40, 1, 1, 1));
		chart.getPlotOption().setCursor(Cursor.DEFAULT.getCssName()).setMarkerEnabled(false).setMarkerRadius(4).setMarkerHoverRadius(4)
				.setMarkerHoverRadiusPlus(0).setMarkerHoverLineWidthPlus(0).setMarkerLineWidth(0).setHoverHaloOpacity(0.2).setHoverHaloSize(12)
				.setHoverLineWidthPlus(0).setMarkerSymbol("circle");
		chart.getPlotOption()
				.setFillColor(ColorHelper.getLinearGradientColor(0, 1, 0, 0, PLOT_BACKGROUND_COLOR_BOTTOM, PLOT_BACKGROUND_COLOR_TOP),
						chart.getPlotOption().getAreaSeriesType()).setLineWidth(3, chart.getPlotOption().getAreaSeriesType());
		chart.getPlotOption().setLineWidth(3, chart.getPlotOption().getLineSeriesType());
		chart.setColors(ColorHelper.getColorsAsJSArray(ColorHelper.getReflectionPurple()));
		chart.getCreditsOption().setEnabled(false); // Disable credits text
		chart.getLegendOption().setEnabled(false); // Disable legend
		chart.getTitleOption().setText(null); // Disable title
		chart.getTooltipOption().setUseHTML(true).setShared(true).setShadow(false).setBackgroundColor("#ffffff").setBorderColor("#dedede").setBorderWidth(1)
				.setBorderRadius(0).setValueDecimals(0).setCrosshairs(getCrosshairStyle()).setDateTimeLabelFormats(getDefaultTooltipDateTimeLabelFormat());
		// .setHeaderFormat(
		// "<span style=\"font-size: 10px; line-height: 30px; font-weight: bold; color: #81879d; font-family: 'Lato', sans-serif;\">{point.key}</span><br/>")
		// .setPointFormat("<span style=\"font-size: 18px; font-weight: regular; color: #363a47; font-family: 'Lato', sans-serif;\">{point.y}</span>");
		chart.getXAxis().setTickWidth(1).setTickLength(10).setTickColor("#e7e7e7").setLabelsStyle(getXAxisLabelsStyle()).setLabelsY(30).setStartOnTick(true)
				.setEndOnTick(true).setMinPadding(0).setMaxPadding(0).setLineColor("#e5e5e5").setLabelsMaxStaggerLines(1).setLabelsPadding(30)
				.setLabelsUseHTML(false).setLabelsAlign("center");
		chart.getPrimaryAxis().setId(YAxisPosition.PRIMARY.toString()).setAllowDecimals(false).setTitleText(null).setOffset(-30).setLabelsY(5)
				.setLabelsStyle(getYAxisLabelsStyle("#81879d")).setLabelsAlign("left").setGridLineColor(ColorHelper.getDividerGrey()).setLabelsUseHTML(false);
		chart.getSecondaryYAxis().setId(YAxisPosition.SECONDARY.toString()).setOpposite(true).setAllowDecimals(false).setTitleText(null).setOffset(-30)
				.setLabelsY(5).setLabelsAlign("right").setGridLineColor(ColorHelper.getDividerGrey()).setLabelsUseHTML(false);
		chart.getTertiaryYAxis().setId(YAxisPosition.TERTIARY.toString()).setOpposite(true).setAllowDecimals(false).setTitleText(null).setOffset(-90)
				.setLabelsY(5).setLabelsAlign("right").setGridLineColor(ColorHelper.getDividerGrey()).setLabelsUseHTML(false);
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
			if (entry.getValue() instanceof Boolean) {
				JavaScriptObjectHelper.setBooleanProperty(JSObject, entry.getKey(), (Boolean) entry.getValue());
			}
		}
		return JSObject;

	}

	public static JavaScriptObject getDateTimeAxisLabelFormat() {
		HashMap<String, Object> dateTimeLabelFormatValues = new HashMap<String, Object>();
		dateTimeLabelFormatValues.put("day", "%e %b");
		return getJSObjectFromMap(dateTimeLabelFormatValues);
	}

	public static JavaScriptObject getDefaultTooltipDateTimeLabelFormat() {
		HashMap<String, Object> dateTimeLabelFormatValues = new HashMap<String, Object>();
		dateTimeLabelFormatValues.put("day", "%e %b %Y");
		return getJSObjectFromMap(dateTimeLabelFormatValues);
	}

	public static JavaScriptObject getTooltipRevenueSeriesOption(String currency) {
		HashMap<String, Object> tooltipRevenueSeriesValues = new HashMap<String, Object>();
		tooltipRevenueSeriesValues.put("valuePrefix", currency);
		return getJSObjectFromMap(tooltipRevenueSeriesValues);
	}

	public static JavaScriptObject getTooltipSeriesOption() {
		return JavaScriptObject.createObject(); // TODO
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

	public static JavaScriptObject getXAxisLabelsStyle() {
		HashMap<String, Object> styleValues = new HashMap<String, Object>();
		styleValues.put("fontSize", "13px");
		styleValues.put("fontWeight", "regular");
		styleValues.put("color", "#63656a");
		styleValues.put("fontFamily", "'Lato', sans-serif");
		styleValues.put("padingLeft", "20px");
		styleValues.put("padingRight", "20px");
		return getJSObjectFromMap(styleValues);
	}

	public static JavaScriptObject getYAxisLabelsStyle(String labelColor) {
		HashMap<String, Object> styleValues = new HashMap<String, Object>();
		styleValues.put("fontSize", "13px");
		styleValues.put("fontWeight", "regular");
		styleValues.put("color", labelColor);
		styleValues.put("fontFamily", "'Lato', sans-serif");
		return getJSObjectFromMap(styleValues);
	}

	public static JavaScriptObject getTooltipStyle() {
		HashMap<String, Object> styleValues = new HashMap<String, Object>();
		// styleValues.put("overflow", "visible");
		return getJSObjectFromMap(styleValues);
	}

	public static JavaScriptObject getCrosshairStyle() {
		HashMap<String, Object> styleValues = new HashMap<String, Object>();
		styleValues.put("width", 1);
		styleValues.put("color", "#ff466a");
		return getJSObjectFromMap(styleValues);
	}

	public static JavaScriptObject getLoadingStyle() {
		HashMap<String, Object> styleValues = new HashMap<String, Object>();
		styleValues.put("backgroundImage", "url('http://jsfiddle.net/img/logo.png')");
		styleValues.put("display", "block");
		return getJSObjectFromMap(styleValues);
	}

}
