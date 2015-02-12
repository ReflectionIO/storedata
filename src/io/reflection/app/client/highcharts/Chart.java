//
//  Chart.java
//  storedata
//
//  Created by Stefano Capuzzi on 14 Jan 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.highcharts;

import static io.reflection.app.client.controller.FilterController.DOWNLOADS_CHART_TYPE;
import static io.reflection.app.client.controller.FilterController.FREE_LIST_TYPE;
import static io.reflection.app.client.controller.FilterController.GROSSING_LIST_TYPE;
import static io.reflection.app.client.controller.FilterController.PAID_LIST_TYPE;
import static io.reflection.app.client.controller.FilterController.RANKING_CHART_TYPE;
import static io.reflection.app.client.controller.FilterController.REVENUE_CHART_TYPE;
import io.reflection.app.client.controller.FilterController;
import io.reflection.app.client.controller.SessionController;
import io.reflection.app.client.helper.JavaScriptObjectHelper;
import io.reflection.app.client.highcharts.options.Axis;
import io.reflection.app.client.highcharts.options.ChartOption;
import io.reflection.app.client.highcharts.options.Colors;
import io.reflection.app.client.highcharts.options.Credits;
import io.reflection.app.client.highcharts.options.Data;
import io.reflection.app.client.highcharts.options.Labels;
import io.reflection.app.client.highcharts.options.Legend;
import io.reflection.app.client.highcharts.options.Loading;
import io.reflection.app.client.highcharts.options.NoData;
import io.reflection.app.client.highcharts.options.Option;
import io.reflection.app.client.highcharts.options.PlotOption;
import io.reflection.app.client.highcharts.options.Subtitle;
import io.reflection.app.client.highcharts.options.Title;
import io.reflection.app.client.highcharts.options.Tooltip;
import io.reflection.app.client.highcharts.options.XAxis;
import io.reflection.app.client.highcharts.options.YAxis;
import io.reflection.app.datatypes.shared.Rank;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayMixed;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.datepicker.client.CalendarUtil;

/**
 * @author Stefano Capuzzi
 * 
 *         See http://api.highcharts.com/highcharts
 *
 */
public class Chart extends Composite {

	// Option types
	public static final String OPTION_CHART = "chart";
	public static final String OPTION_COLORS = "colors";
	public static final String OPTION_CREDITS = "credits";
	public static final String OPTION_DATA = "data";
	public static final String OPTION_LABELS = "labels";
	public static final String OPTION_LEGEND = "legend";
	public static final String OPTION_LOADING = "loading";
	public static final String OPTION_NO_DATA = "noData";
	public static final String OPTION_PLOT_OPTIONS = "plotOptions";
	public static final String OPTION_SUBTITLE = "subtitle";
	public static final String OPTION_TITLE = "title";
	public static final String OPTION_TOOLTIP = "tooltip";
	public static final String OPTION_X_AXIS = "xAxis";
	public static final String OPTION_Y_AXIS = "yAxis";

	private final HTMLPanel chartDiv;
	private final String id = HTMLPanel.createUniqueId();
	private JavaScriptObject options;
	private JavaScriptObject chart;
	private Map<String, Option<?>> optionsLookup = new HashMap<String, Option<?>>();
	private YDataType yDataType;
	private RankType rankingType;
	private List<Rank> ranks;
	private boolean showModelPredictions;

	public Chart() {
		chartDiv = new HTMLPanel("");
		chartDiv.getElement().setId(id);
		initWidget(chartDiv);
		options = JavaScriptObject.createObject();
		getChartOption().setRenderTo(id);
		setDefaultChart();
		showModelPredictions = SessionController.get().isLoggedInUserAdmin();
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

	private native JavaScriptObject createGraph(JavaScriptObject options) /*-{
		return new $wnd.Highcharts.Chart(options);
	}-*/;

	public Chart inject() {
		chart = createGraph(options);
		return this;
	}

	public ChartOption getChartOption() {
		if (optionsLookup.get(OPTION_CHART) == null) {
			optionsLookup.put(OPTION_CHART, new ChartOption());
			JavaScriptObjectHelper.setObjectProperty(options, OPTION_CHART, optionsLookup.get(OPTION_CHART).getProperty());
		}
		return (ChartOption) optionsLookup.get(OPTION_CHART);
	}

	public Colors getColorsOption() {
		if (optionsLookup.get(OPTION_COLORS) == null) {
			optionsLookup.put(OPTION_COLORS, new Colors());
			JavaScriptObjectHelper.setObjectProperty(options, OPTION_COLORS, optionsLookup.get(OPTION_COLORS).getProperty());
		}
		return (Colors) optionsLookup.get(OPTION_COLORS);
	}

	public Credits getCreditsOption() {
		if (optionsLookup.get(OPTION_CREDITS) == null) {
			optionsLookup.put(OPTION_CREDITS, new Credits());
			JavaScriptObjectHelper.setObjectProperty(options, OPTION_CREDITS, optionsLookup.get(OPTION_CREDITS).getProperty());
		}
		return (Credits) optionsLookup.get(OPTION_CREDITS);
	}

	public Data getDataOption() {
		if (optionsLookup.get(OPTION_DATA) == null) {
			optionsLookup.put(OPTION_DATA, new Data());
			JavaScriptObjectHelper.setObjectProperty(options, OPTION_DATA, optionsLookup.get(OPTION_DATA).getProperty());
		}
		return (Data) optionsLookup.get(OPTION_DATA);
	}

	public Labels getLabelsOption() {
		if (optionsLookup.get(OPTION_LABELS) == null) {
			optionsLookup.put(OPTION_LABELS, new Labels());
			JavaScriptObjectHelper.setObjectProperty(options, OPTION_LABELS, optionsLookup.get(OPTION_LABELS).getProperty());
		}
		return (Labels) optionsLookup.get(OPTION_LABELS);
	}

	public Legend getLegendOption() {
		if (optionsLookup.get(OPTION_LEGEND) == null) {
			optionsLookup.put(OPTION_LEGEND, new Legend());
			JavaScriptObjectHelper.setObjectProperty(options, OPTION_LEGEND, optionsLookup.get(OPTION_LEGEND).getProperty());
		}
		return (Legend) optionsLookup.get(OPTION_LEGEND);
	}

	public Loading getLoadingOption() {
		if (optionsLookup.get(OPTION_LOADING) == null) {
			optionsLookup.put(OPTION_LOADING, new Loading());
			JavaScriptObjectHelper.setObjectProperty(options, OPTION_LOADING, optionsLookup.get(OPTION_LOADING).getProperty());
		}
		return (Loading) optionsLookup.get(OPTION_LOADING);
	}

	public NoData getNoDataOption() {
		if (optionsLookup.get(OPTION_NO_DATA) == null) {
			optionsLookup.put(OPTION_NO_DATA, new NoData());
			JavaScriptObjectHelper.setObjectProperty(options, OPTION_NO_DATA, optionsLookup.get(OPTION_NO_DATA).getProperty());
		}
		return (NoData) optionsLookup.get(OPTION_NO_DATA);
	}

	public PlotOption getPlotOption() {
		if (optionsLookup.get(OPTION_PLOT_OPTIONS) == null) {
			optionsLookup.put(OPTION_PLOT_OPTIONS, new PlotOption());
			JavaScriptObjectHelper.setObjectProperty(options, OPTION_PLOT_OPTIONS, optionsLookup.get(OPTION_PLOT_OPTIONS).getProperty());
		}
		return (PlotOption) optionsLookup.get(OPTION_PLOT_OPTIONS);
	}

	public Subtitle getSubtitleOption() {
		if (optionsLookup.get(OPTION_SUBTITLE) == null) {
			optionsLookup.put(OPTION_SUBTITLE, new Subtitle());
			JavaScriptObjectHelper.setObjectProperty(options, OPTION_SUBTITLE, optionsLookup.get(OPTION_SUBTITLE).getProperty());
		}
		return (Subtitle) optionsLookup.get(OPTION_SUBTITLE);
	}

	public Title getTitleOption() {
		if (optionsLookup.get(OPTION_TITLE) == null) {
			optionsLookup.put(OPTION_TITLE, new Title());
			JavaScriptObjectHelper.setObjectProperty(options, OPTION_TITLE, optionsLookup.get(OPTION_TITLE).getProperty());
		}
		return (Title) optionsLookup.get(OPTION_TITLE);
	}

	public Tooltip getTooltipOption() {
		if (optionsLookup.get(OPTION_TOOLTIP) == null) {
			optionsLookup.put(OPTION_TOOLTIP, new Tooltip());
			JavaScriptObjectHelper.setObjectProperty(options, OPTION_TOOLTIP, optionsLookup.get(OPTION_TOOLTIP).getProperty());
		}
		return (Tooltip) optionsLookup.get(OPTION_TOOLTIP);
	}

	public XAxis getXAxis() {
		if (optionsLookup.get(OPTION_X_AXIS) == null) {
			optionsLookup.put(OPTION_X_AXIS, new XAxis());
			JavaScriptObjectHelper.setObjectProperty(options, OPTION_X_AXIS, optionsLookup.get(OPTION_X_AXIS).getProperty());
		}
		return (XAxis) optionsLookup.get(OPTION_X_AXIS);
	}

	public YAxis getYAxis() {
		if (optionsLookup.get(OPTION_Y_AXIS) == null) {
			optionsLookup.put(OPTION_Y_AXIS, new YAxis());
			JavaScriptObjectHelper.setObjectProperty(options, OPTION_Y_AXIS, optionsLookup.get(OPTION_Y_AXIS).getProperty());
		}
		return (YAxis) optionsLookup.get(OPTION_Y_AXIS);
	}

	public Chart setDefaultChart() {
		getChartOption().setType(ChartHelper.TYPE_AREA).setPlotBackgroundColor("#fafafa").setPlotBorderColor("#e5e5e5").setPlotBorderWidth(1)
				.setMargin(ChartHelper.createMarginsArray(0, 0, 50, 0)).setHeight(450);
		// getColorsOption().setColors(colors); TODO set default colors
		getCreditsOption().setEnabled(false); // Disable highcharts credits text
		getLegendOption().setEnabled(false); // Disable legend
		getPlotOption().setCursor("default").setFillOpacity(0.2).setLineColor("#6a64c4").setLineWidth(2).setMarkerEnabled(false).setMarkerFillColor("#6a64c4")
				.setMarkerHeight(10).setMarkerLineColor("#6a64c4").setMarkerWidth(10).setMarkerLineWidth(0);
		getTitleOption().setText(null); // Disable title
		getTooltipOption().setBackgroundColor("#ffffff").setBorderColor("#ffffff").setBorderRadius(2).setBorderWidth(0).setCrosshairs(true)
				.setDateTimeLabelFormats(ChartHelper.getDefaultTooltipDateTimeLabelFormat()).setValueDecimals(2);
		getXAxis().setType(Axis.TYPE_DATETIME).setDateTimeLabelFormats(ChartHelper.getDefaultAxisDateTimeLabelFormat()).setTickWidth(0)
				.setTickInterval(86400000).setShowFirstLabel(false).setShowLastLabel(false).setLabelsStyle(ChartHelper.getDefaultAxisStyle()).setLabelsY(30)
				.setStartOnTick(false).setEndOnTick(false).setMinPadding(0).setMaxPadding(0);
		getYAxis().setAllowDecimals(false).setTitleText(null).setOffset(-30).setLineWidth(0).setLabelsY(-7).setLabelsStyle(ChartHelper.getDefaultAxisStyle())
				.setShowLastLabel(false).setLabelsAlign("left").setLabelsFormat("£ {value}");
		return this;
	}

	public void setDataType(YDataType value) {
		this.yDataType = value;
	}

	public void setRankingType(RankType rankingType) {
		this.rankingType = rankingType;
	}

	public void setData(List<Rank> ranks) {
		this.ranks = ranks;
		drawData();
	}

	public void drawData() {
		if (ranks != null && yDataType != null) {

			removeAllSeries();

			Date progressiveDate = FilterController.get().getStartDate();

			JsArray<JsArrayMixed> values = JavaScriptObject.createArray().cast();

			JavaScriptObject yAxisOptions = JavaScriptObject.createObject();
			int yMin = Integer.MAX_VALUE;
			int yMax = 0;
			switch (yDataType) {
			case DownloadsYAxisDataType:
				for (Rank rank : ranks) {
					if (withinChartRange(rank)) {
						// Fill with blank values missing dates
						if (!CalendarUtil.isSameDate(progressiveDate, rank.date)) {
							while (!CalendarUtil.isSameDate(progressiveDate, rank.date)
									&& (progressiveDate.before(CalendarUtil.copyDate(FilterController.get().getEndDate())) || CalendarUtil.isSameDate(
											progressiveDate, FilterController.get().getEndDate()))) {
								JsArrayMixed emptyPoint = JavaScriptObject.createArray().cast();
								emptyPoint.push(progressiveDate.getTime());
								emptyPoint.push(JavaScriptObjectHelper.getNativeNull());
								values.push(emptyPoint);
								CalendarUtil.addDaysToDate(progressiveDate, 1);
							}
						}
						CalendarUtil.addDaysToDate(progressiveDate, 1);

						JsArrayMixed point = JavaScriptObject.createArray().cast();
						point.push(rank.date.getTime());
						if (rank.downloads != null && showModelPredictions) {
							if (rank.downloads.intValue() < yMin) {
								yMin = rank.downloads.intValue();
							}
							if (rank.downloads.intValue() > yMax) {
								yMax = rank.downloads.intValue();
							}
							point.push(rank.downloads.intValue());
						} else {
							point.push(JavaScriptObjectHelper.getNativeNull());
						}
						values.push(point);
					}
				}
				JavaScriptObjectHelper.setBooleanProperty(yAxisOptions, "reversed", false);
				break;
			case RevenueYAxisDataType:
				for (Rank rank : ranks) {
					if (withinChartRange(rank)) {
						// Fill with blank values missing dates
						if (!CalendarUtil.isSameDate(progressiveDate, rank.date)) {
							while (!CalendarUtil.isSameDate(progressiveDate, rank.date)
									&& (progressiveDate.before(CalendarUtil.copyDate(FilterController.get().getEndDate())) || CalendarUtil.isSameDate(
											progressiveDate, FilterController.get().getEndDate()))) {
								JsArrayMixed emptyPoint = JavaScriptObject.createArray().cast();
								emptyPoint.push(progressiveDate.getTime());
								emptyPoint.push(JavaScriptObjectHelper.getNativeNull());
								values.push(emptyPoint);
								CalendarUtil.addDaysToDate(progressiveDate, 1);
							}
						}
						CalendarUtil.addDaysToDate(progressiveDate, 1);

						JsArrayMixed point = JavaScriptObject.createArray().cast();
						point.push(rank.date.getTime());
						if (rank.revenue != null && showModelPredictions) {
							if (rank.revenue.intValue() < yMin) {
								yMin = rank.revenue.intValue();
							}
							if (rank.revenue.intValue() > yMax) {
								yMax = rank.revenue.intValue();
							}
							point.push(rank.revenue.floatValue());
						} else {
							point.push(JavaScriptObjectHelper.getNativeNull());
						}
						values.push(point);
					}
				}
				JavaScriptObjectHelper.setBooleanProperty(yAxisOptions, "reversed", false);
				break;
			case RankingYAxisDataType:
			default:
				int position;
				for (Rank rank : ranks) {
					if (withinChartRange(rank)) {

						// Fill with blank values missing dates
						if (!CalendarUtil.isSameDate(progressiveDate, rank.date)) {
							while (!CalendarUtil.isSameDate(progressiveDate, rank.date)
									&& (progressiveDate.before(CalendarUtil.copyDate(FilterController.get().getEndDate())) || CalendarUtil.isSameDate(
											progressiveDate, FilterController.get().getEndDate()))) {
								JsArrayMixed emptyPoint = JavaScriptObject.createArray().cast();
								emptyPoint.push(progressiveDate.getTime());
								emptyPoint.push(JavaScriptObjectHelper.getNativeNull());
								values.push(emptyPoint);
								CalendarUtil.addDaysToDate(progressiveDate, 1);
							}
						}
						CalendarUtil.addDaysToDate(progressiveDate, 1);

						JsArrayMixed point = JavaScriptObject.createArray().cast();
						point.push(rank.date.getTime());
						if ((position = getPosition(rank)) != 0) {
							if (position < yMin) {
								yMin = position;
							}
							if (position > yMax) {
								yMax = position;
							}
							point.push(position);
						} else {
							point.push(JavaScriptObjectHelper.getNativeNull());
						}
						values.push(point);
					}
				}
				JavaScriptObjectHelper.setBooleanProperty(yAxisOptions, "reversed", true);
				break;
			}
			JavaScriptObject yAxis = NativeAxis.nativeGetYAxis(chart, 0);
			NativeAxis.nativeUpdate(yAxis, yAxisOptions, true); // update y axis
			// NativeAxis.nativeSetExtremes(yAxis, yMin, yMax, true, true);

			JavaScriptObject series = JavaScriptObject.createObject();
			JavaScriptObjectHelper.setObjectProperty(series, "data", values);

			addSeries(series);
		}
	}

	/**
	 * Check if x data is contained within the wished range (it can be excluded because of the windowing cache that retrieves bunche)
	 * 
	 * @param rank
	 * @return
	 */
	private boolean withinChartRange(Rank rank) {
		return rank.date.getTime() >= FilterController.get().getStartDate().getTime() && rank.date.getTime() <= FilterController.get().getEndDate().getTime();
	}

	// Save some typing in the getExtremes() method
	private Number getNumberFromJSONObject(JSONObject jsonObject, String key) {
		JSONValue jsonValue = jsonObject.get(key);
		if (jsonValue != null && jsonValue.isNumber() != null) { return jsonValue.isNumber().doubleValue(); }
		return null;
	}

	public void setLoading(boolean loading) {

		// if (curve != null) {
		// curve.setVisible(!loading);
		//
		if (loading) {
			removeAllSeries();
			NativeAxis.nativeSetExtremes(NativeAxis.nativeGetXAxis(chart, 0), FilterController.get().getStartDate().getTime(), FilterController.get()
					.getEndDate().getTime(), true, true);
			//
			// getYAxis().setAxisMax(1);
			// getYAxis().setAxisMin(8);
			// getYAxis().setTickCount(8);
		}
		//
		// update();
		// }
	}

	private int getPosition(Rank rank) {
		return rankingType == RankType.PositionRankingType ? rank.position.intValue() : rank.grossingPosition.intValue();
	}

	public void addAxis(JavaScriptObject options, boolean isX) {
		NativeChart.nativeAddAxis(chart, options, isX, true, true);
	}

	public void addSeries(JavaScriptObject series) {
		if (chart != null) { // Check if the chart has been already created and injected
			NativeChart.nativeAddSeries(chart, series, true, true);
		}
	}

	public void destroy() {
		NativeChart.nativeDestroy(chart);
	}

	public void get(String id) {
		NativeChart.nativeGet(chart, id);
	}

	public void hideLoading() {
		NativeChart.nativeHideLoading(chart);
	}

	public void redraw() {
		NativeChart.nativeRedraw(chart);
	}

	public void reflow() {
		NativeChart.nativeReflow(chart);
	}

	public void setSize(int width, int height) {
		NativeChart.nativeSetSize(chart, width, height, true);
	}

	public void setTitle(JavaScriptObject title, JavaScriptObject subtitle) {
		NativeChart.nativeSetTitle(chart, title, subtitle, true);
	}

	public void showLoading(String loadingText) {
		NativeChart.nativeShowLoading(chart, loadingText);
	}

	public void removeAllSeries() {
		NativeSeries.nativeRemoveAll(chart, true);
	}

}
