//
//  Chart.java
//  storedata
//
//  Created by Stefano Capuzzi on 14 Jan 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.highcharts;

import io.reflection.app.client.controller.FilterController;
import io.reflection.app.client.controller.SessionController;
import io.reflection.app.client.helper.JavaScriptObjectHelper;
import io.reflection.app.client.highcharts.ChartHelper.RankType;
import io.reflection.app.client.highcharts.ChartHelper.YDataType;
import io.reflection.app.client.highcharts.options.ChartOption;
import io.reflection.app.client.highcharts.options.Credits;
import io.reflection.app.client.highcharts.options.Data;
import io.reflection.app.client.highcharts.options.Labels;
import io.reflection.app.client.highcharts.options.Legend;
import io.reflection.app.client.highcharts.options.Loading;
import io.reflection.app.client.highcharts.options.NoData;
import io.reflection.app.client.highcharts.options.Option;
import io.reflection.app.client.highcharts.options.PlotOption;
import io.reflection.app.client.highcharts.options.Series;
import io.reflection.app.client.highcharts.options.Subtitle;
import io.reflection.app.client.highcharts.options.Title;
import io.reflection.app.client.highcharts.options.Tooltip;
import io.reflection.app.client.highcharts.options.XAxis;
import io.reflection.app.client.highcharts.options.YAxis;
import io.reflection.app.datatypes.shared.Rank;
import io.reflection.app.shared.util.FormattingHelper;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayMixed;
import com.google.gwt.core.client.JsArrayString;
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
	public static final String OPTION_SERIES = "series";
	public static final String OPTION_SUBTITLE = "subtitle";
	public static final String OPTION_TITLE = "title";
	public static final String OPTION_TOOLTIP = "tooltip";
	public static final String OPTION_X_AXIS = "xAxis";
	public static final String OPTION_Y_AXIS = "yAxis";

	private final HTMLPanel chartDiv;
	private final String id = HTMLPanel.createUniqueId();
	private JavaScriptObject options = JavaScriptObject.createObject();
	private JavaScriptObject chart;
	private Map<String, Option<?>> optionsLookup = new HashMap<String, Option<?>>();
	private YDataType yDataType;
	private RankType rankingType;
	private List<Rank> ranks;
	private boolean showModelPredictions;
	private String currency;

	public Chart() {
		chartDiv = new HTMLPanel("");
		chartDiv.getElement().setId(id);
		getChartOption().setRenderTo(id);
		showModelPredictions = SessionController.get().isLoggedInUserAdmin();

		initWidget(chartDiv);
	}

	public Chart inject() {
		chart = NativeHighcharts.nativeChart(options);
		return this;
	}

	public ChartOption getChartOption() {
		if (optionsLookup.get(OPTION_CHART) == null) {
			optionsLookup.put(OPTION_CHART, new ChartOption());
			JavaScriptObjectHelper.setObjectProperty(options, OPTION_CHART, optionsLookup.get(OPTION_CHART).getProperty());
		}
		return (ChartOption) optionsLookup.get(OPTION_CHART);
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

	public Series getSeriesOption() {
		if (optionsLookup.get(OPTION_SERIES) == null) {
			optionsLookup.put(OPTION_SERIES, new Series());
			JavaScriptObjectHelper.setObjectProperty(options, OPTION_SERIES, optionsLookup.get(OPTION_SERIES).getProperty());
		}
		return (Series) optionsLookup.get(OPTION_SERIES);
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

	public void setDataType(YDataType value) {
		this.yDataType = value;
	}

	public void setRankingType(RankType rankingType) {
		this.rankingType = rankingType;
	}

	public void setData(List<Rank> ranks) {
		this.ranks = ranks;
		if (this.ranks != null && this.ranks.size() > 0) {
			currency = FormattingHelper.getCurrencySymbol(this.ranks.get(0).currency);
		}
		drawData();
	}

	public void drawData() {

		setLoading(false);

		if (ranks != null && yDataType != null) {
			switch (yDataType) {
			case DownloadsYAxisDataType:
				drawDownloads();
				break;

			case RevenueYAxisDataType:
				drawRevenue();
				break;

			case RankingYAxisDataType:
			default:
				drawRanking();
				break;
			}
		}
	}

	private void drawDownloads() {
		Date progressiveDate = FilterController.get().getStartDate();
		JsArray<JsArrayMixed> values = JavaScriptObject.createArray().cast();
		int yMin = Integer.MAX_VALUE;
		int yMax = 0;
		getTooltipOption().setValuePrefix("");
		getYAxis().setReversed(false).setLabelsFormatter(ChartHelper.getNativeLabelFormatter("", ""));
		if (NativeChart.nativeGet(chart, "revenue") != null) {
			NativeSeries.nativeHide(NativeChart.nativeGet(chart, "revenue"));
		}
		if (NativeChart.nativeGet(chart, "ranking") != null) {
			NativeSeries.nativeHide(NativeChart.nativeGet(chart, "ranking"));
		}
		if (NativeChart.nativeGet(chart, "downloads") == null) {
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
			JavaScriptObject seriesDownloads = JavaScriptObject.createObject();
			JavaScriptObjectHelper.setObjectProperty(seriesDownloads, "data", values);
			JavaScriptObjectHelper.setStringProperty(seriesDownloads, "type", ChartHelper.TYPE_AREA);
			JavaScriptObjectHelper.setStringProperty(seriesDownloads, "id", "downloads");
			addSeries(seriesDownloads);
		} else {
			NativeSeries.nativeShow(NativeChart.nativeGet(chart, "downloads"));
		}
		NativeAxis.nativeUpdate(NativeAxis.nativeGetYAxis(chart, 0), getYAxis().getProperty(), true); // update y axis
	}

	private void drawRevenue() {
		Date progressiveDate = FilterController.get().getStartDate();
		JsArray<JsArrayMixed> values = JavaScriptObject.createArray().cast();
		int yMin = Integer.MAX_VALUE;
		int yMax = 0;
		getTooltipOption().setValuePrefix(currency + " ");
		getYAxis().setReversed(false).setLabelsFormatter(ChartHelper.getNativeLabelFormatter(currency + " ", ""));
		if (NativeChart.nativeGet(chart, "downloads") != null) {
			NativeSeries.nativeHide(NativeChart.nativeGet(chart, "downloads"));
		}
		if (NativeChart.nativeGet(chart, "ranking") != null) {
			NativeSeries.nativeHide(NativeChart.nativeGet(chart, "ranking"));
		}
		if (NativeChart.nativeGet(chart, "revenue") == null) {
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
			JavaScriptObject seriesRevenue = JavaScriptObject.createObject();
			JavaScriptObjectHelper.setObjectProperty(seriesRevenue, "data", values);
			JavaScriptObjectHelper.setStringProperty(seriesRevenue, "type", ChartHelper.TYPE_AREA);
			JavaScriptObjectHelper.setStringProperty(seriesRevenue, "id", "revenue");
			addSeries(seriesRevenue);
		} else {
			NativeSeries.nativeShow(NativeChart.nativeGet(chart, "revenue"));
		}
		NativeAxis.nativeUpdate(NativeAxis.nativeGetYAxis(chart, 0), getYAxis().getProperty(), true); // update y axis
	}

	private void drawRanking() {
		Date progressiveDate = FilterController.get().getStartDate();
		JsArray<JsArrayMixed> values = JavaScriptObject.createArray().cast();
		int yMin = Integer.MAX_VALUE;
		int yMax = 0;
		getTooltipOption().setValuePrefix("");
		getYAxis().setReversed(true).setLabelsFormatter(ChartHelper.getNativeLabelFormatter("", ""));
		if (NativeChart.nativeGet(chart, "downloads") != null) {
			NativeSeries.nativeHide(NativeChart.nativeGet(chart, "downloads"));
		}
		if (NativeChart.nativeGet(chart, "revenue") != null) {
			NativeSeries.nativeHide(NativeChart.nativeGet(chart, "revenue"));
		}
		if (NativeChart.nativeGet(chart, "ranking") == null) {
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
					if ((position = getRankPosition(rank)) != 0) {
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
			JavaScriptObject seriesRanking = JavaScriptObject.createObject();
			JavaScriptObjectHelper.setObjectProperty(seriesRanking, "data", values);
			JavaScriptObjectHelper.setStringProperty(seriesRanking, "type", ChartHelper.TYPE_LINE);
			JavaScriptObjectHelper.setStringProperty(seriesRanking, "id", "ranking");
			addSeries(seriesRanking);
		} else {
			NativeSeries.nativeShow(NativeChart.nativeGet(chart, "ranking"));
		}
		NativeAxis.nativeUpdate(NativeAxis.nativeGetYAxis(chart, 0), getYAxis().getProperty(), true); // update y axis
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

	@SuppressWarnings("deprecation")
	public void setLoading(boolean loading) {

		if (loading) {
			removeAllSeries();
			ranks = null;
			Date startDate = FilterController.get().getStartDate();
			Date endDate = FilterController.get().getEndDate();
			NativeAxis.nativeSetExtremes(NativeAxis.nativeGetXAxis(chart, 0),
					new Date(startDate.getYear(), startDate.getMonth(), startDate.getDate()).getTime(),
					new Date(endDate.getYear(), endDate.getMonth(), endDate.getDate()).getTime(), true, null);

		}

	}

	private int getRankPosition(Rank rank) {
		return rankingType == RankType.PositionRankingType ? rank.position.intValue() : rank.grossingPosition.intValue();
	}

	public void addAxis(JavaScriptObject options, boolean isX) {
		if (chart != null) { // Check if the chart has been already created and injected
			NativeChart.nativeAddAxis(chart, options, isX, true, true);
		}
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

	public void redraw() {
		NativeChart.nativeRedraw(chart);
	}

	public void reflow() {
		NativeChart.nativeReflow(chart);
	}

	public void setColors(JsArrayString colors) {
		JavaScriptObjectHelper.setObjectProperty(options, OPTION_COLORS, colors);
	}

	public void setSize(int width, int height) {
		NativeChart.nativeSetSize(chart, width, height, true);
	}

	public void setTitle(JavaScriptObject title, JavaScriptObject subtitle) {
		NativeChart.nativeSetTitle(chart, title, subtitle, true);
	}

	public void removeAllSeries() {
		NativeSeries.nativeRemoveAll(chart, true);
	}

}
