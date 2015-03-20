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
import io.reflection.app.client.helper.FilterHelper;
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
import io.reflection.app.client.part.datatypes.DateRange;
import io.reflection.app.datatypes.shared.Rank;
import io.reflection.app.shared.util.FormattingHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.datepicker.client.CalendarUtil;

/**
 * @author Stefano Capuzzi
 * 
 *         See http://api.highcharts.com/highcharts
 *
 */
public class Chart extends HTMLPanel {

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

	public static final int X_LABELS_DISTANCE = 65;

	private final String id = HTMLPanel.createUniqueId();
	private JavaScriptObject options = JavaScriptObject.createObject();
	private JavaScriptObject chart;
	private Map<String, Option<?>> optionsLookup = new HashMap<String, Option<?>>();
	private YDataType yDataType;
	private RankType rankingType;
	private List<Rank> ranks;
	private DateRange dateRange;
	private boolean showModelPredictions;
	private String currency;
	private final int yMinCeilingDownloads = 100;
	private final int yMinCeilingRevenue = 100;

	// private final int yMinCeilingRanking = 10;

	public Chart() {
		this("");
	}

	public Chart(String html) {
		super(html);
		this.getElement().setId(id);
		getChartOption().setRenderTo(id);
		showModelPredictions = SessionController.get().isLoggedInUserAdmin();

	}

	public Chart inject() {
		chart = NativeHighcharts.nativeChart(options);
		resize();
		Window.addResizeHandler(new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				resize();
				rearrangeXAxisLabels();
			}
		});
		return this;
	}

	public void setDataType(YDataType value) {
		this.yDataType = value;
	}

	public void setRankingType(RankType rankingType) {
		this.rankingType = rankingType;
	}

	public void setData(List<Rank> ranks) {
		List<Rank> outOfRangeRanks = new ArrayList<Rank>();
		for (Rank rank : ranks) {
			if (!withinChartRange(rank)) {
				outOfRangeRanks.add(rank);
			} else {
				rank.date = FilterHelper.normalizeDate(rank.date);
			}
		}
		ranks.removeAll(outOfRangeRanks);
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
		getTooltipOption().setValuePrefix("");
		getYAxis().setReversed(false).setLabelsFormatter(ChartHelper.getNativeLabelFormatter("", "")).setMin(0).setFloor(0).setShowFirstLabel(false)
				.setShowLastLabel(true);
		Date progressiveDate = FilterController.get().getStartDate();
		JsArray<JavaScriptObject> data = JavaScriptObject.createArray().cast();
		if (NativeChart.nativeGet(chart, "revenue") != null) {
			NativeSeries.nativeHide(NativeChart.nativeGet(chart, "revenue"));
		}
		if (NativeChart.nativeGet(chart, "ranking") != null) {
			NativeSeries.nativeHide(NativeChart.nativeGet(chart, "ranking"));
		}
		if (NativeChart.nativeGet(chart, "downloads") == null) {
			for (Rank rank : ranks) {
				// Fill with blank data missing dates
				if (!FilterHelper.equalDate(progressiveDate, rank.date)) {
					while (!FilterHelper.equalDate(progressiveDate, rank.date)
							&& FilterHelper.beforeOrSameDate(progressiveDate, FilterController.get().getEndDate())) {
						data.push(ChartHelper.createPoint(progressiveDate.getTime(), JavaScriptObjectHelper.getNativeNull()));
						CalendarUtil.addDaysToDate(progressiveDate, 1);
					}
				}
				CalendarUtil.addDaysToDate(progressiveDate, 1);
				if (rank.downloads != null && showModelPredictions) {
					if (isPointIsolated(YDataType.DownloadsYAxisDataType, rank)) {
						data.push(ChartHelper.createMarkerPoint(rank.date.getTime(), rank.downloads.intValue()));
					} else {
						data.push(ChartHelper.createPoint(rank.date.getTime(), rank.downloads.intValue()));
					}
				} else {
					data.push(ChartHelper.createPoint(rank.date.getTime(), JavaScriptObjectHelper.getNativeNull()));
				}
			}
			JavaScriptObject seriesDownloads = JavaScriptObject.createObject();
			JavaScriptObjectHelper.setObjectProperty(seriesDownloads, "data", data);
			JavaScriptObjectHelper.setStringProperty(seriesDownloads, "type", ChartHelper.TYPE_AREA);
			JavaScriptObjectHelper.setStringProperty(seriesDownloads, "id", "downloads");
			addSeries(seriesDownloads);
		} else {
			NativeSeries.nativeShow(NativeChart.nativeGet(chart, "downloads"));
		}
		if (NativeAxis.nativeGetDataMax(NativeAxis.nativeGetYAxis(chart, 0)) < yMinCeilingDownloads) {
			getYAxis().setMax(yMinCeilingDownloads);
		} else {
			getYAxis().setMax(JavaScriptObjectHelper.getNativeNull());
		}
		NativeAxis.nativeUpdate(NativeAxis.nativeGetYAxis(chart, 0), getYAxis().getProperty(), true); // update y axis
	}

	private void drawRevenue() {
		getTooltipOption().setValuePrefix(currency + " ");
		getYAxis().setReversed(false).setLabelsFormatter(ChartHelper.getNativeLabelFormatter(currency, "")).setMin(0).setFloor(0).setShowFirstLabel(false)
				.setShowLastLabel(true);
		Date progressiveDate = FilterController.get().getStartDate();
		JsArray<JavaScriptObject> data = JavaScriptObject.createArray().cast();
		if (NativeChart.nativeGet(chart, "downloads") != null) {
			NativeSeries.nativeHide(NativeChart.nativeGet(chart, "downloads"));
		}
		if (NativeChart.nativeGet(chart, "ranking") != null) {
			NativeSeries.nativeHide(NativeChart.nativeGet(chart, "ranking"));
		}
		if (NativeChart.nativeGet(chart, "revenue") == null) {
			for (Rank rank : ranks) {
				// Fill with blank data missing dates
				if (!FilterHelper.equalDate(progressiveDate, rank.date)) {
					while (!FilterHelper.equalDate(progressiveDate, rank.date)
							&& FilterHelper.beforeOrSameDate(progressiveDate, FilterController.get().getEndDate())) {
						data.push(ChartHelper.createPoint(progressiveDate.getTime(), JavaScriptObjectHelper.getNativeNull()));
						CalendarUtil.addDaysToDate(progressiveDate, 1);
					}
				}
				CalendarUtil.addDaysToDate(progressiveDate, 1);
				if (rank.revenue != null && showModelPredictions) {
					if (isPointIsolated(YDataType.RevenueYAxisDataType, rank)) {
						data.push(ChartHelper.createMarkerPoint(rank.date.getTime(), rank.revenue.floatValue()));
					} else {
						data.push(ChartHelper.createPoint(rank.date.getTime(), rank.revenue.floatValue()));
					}
				} else {
					data.push(ChartHelper.createPoint(rank.date.getTime(), JavaScriptObjectHelper.getNativeNull()));
				}
			}

			JavaScriptObject seriesRevenue = JavaScriptObject.createObject();
			JavaScriptObjectHelper.setObjectProperty(seriesRevenue, "data", data);
			JavaScriptObjectHelper.setStringProperty(seriesRevenue, "type", ChartHelper.TYPE_AREA);
			JavaScriptObjectHelper.setStringProperty(seriesRevenue, "id", "revenue");
			addSeries(seriesRevenue);
		} else {
			NativeSeries.nativeShow(NativeChart.nativeGet(chart, "revenue"));
		}
		if (NativeAxis.nativeGetDataMax(NativeAxis.nativeGetYAxis(chart, 0)) < yMinCeilingRevenue) {
			getYAxis().setMax(yMinCeilingRevenue);
		} else {
			getYAxis().setMax(JavaScriptObjectHelper.getNativeNull());
		}
		NativeAxis.nativeUpdate(NativeAxis.nativeGetYAxis(chart, 0), getYAxis().getProperty(), true); // update y axis
	}

	private void drawRanking() {
		getTooltipOption().setValuePrefix("");
		getYAxis().setReversed(true).setLabelsFormatter(ChartHelper.getNativeLabelFormatterRank()).setMin(1).setFloor(1).setShowFirstLabel(true)
				.setShowLastLabel(false);
		Date progressiveDate = FilterController.get().getStartDate();
		JsArray<JavaScriptObject> data = JavaScriptObject.createArray().cast();
		if (NativeChart.nativeGet(chart, "downloads") != null) {
			NativeSeries.nativeHide(NativeChart.nativeGet(chart, "downloads"));
		}
		if (NativeChart.nativeGet(chart, "revenue") != null) {
			NativeSeries.nativeHide(NativeChart.nativeGet(chart, "revenue"));
		}
		if (NativeChart.nativeGet(chart, "ranking") == null) {
			for (Rank rank : ranks) {
				// Fill with blank data missing dates
				if (!FilterHelper.equalDate(progressiveDate, rank.date)) {
					while (!FilterHelper.equalDate(progressiveDate, rank.date)
							&& FilterHelper.beforeOrSameDate(progressiveDate, FilterController.get().getEndDate())) {
						data.push(ChartHelper.createPoint(progressiveDate.getTime(), JavaScriptObjectHelper.getNativeNull()));
						CalendarUtil.addDaysToDate(progressiveDate, 1);
					}
				}
				CalendarUtil.addDaysToDate(progressiveDate, 1);
				if (getRankPosition(rank) != 0) {
					if (isPointIsolated(YDataType.RankingYAxisDataType, rank)) {
						data.push(ChartHelper.createMarkerPoint(rank.date.getTime(), getRankPosition(rank)));
					} else {
						data.push(ChartHelper.createPoint(rank.date.getTime(), getRankPosition(rank)));
					}
				} else {
					data.push(ChartHelper.createPoint(rank.date.getTime(), JavaScriptObjectHelper.getNativeNull()));
				}
			}
			JavaScriptObject seriesRanking = JavaScriptObject.createObject();
			JavaScriptObjectHelper.setObjectProperty(seriesRanking, "data", data);
			JavaScriptObjectHelper.setStringProperty(seriesRanking, "type", ChartHelper.TYPE_LINE);
			JavaScriptObjectHelper.setStringProperty(seriesRanking, "id", "ranking");
			addSeries(seriesRanking);
		} else {
			NativeSeries.nativeShow(NativeChart.nativeGet(chart, "ranking"));
		}
		getYAxis().setMax(JavaScriptObjectHelper.getNativeNull());
		NativeAxis.nativeUpdate(NativeAxis.nativeGetYAxis(chart, 0), getYAxis().getProperty(), true); // update y axis
	}

	/**
	 * Check if x data is contained within the wished range (it can be excluded because of the windowing cache that retrieves bunche)
	 * 
	 * @param rank
	 * @return
	 */
	private boolean withinChartRange(Rank rank) {
		return FilterHelper.afterOrSameDate(rank.date, FilterController.get().getStartDate())
				&& FilterHelper.beforeOrSameDate(rank.date, FilterController.get().getEndDate());
	}

	private boolean isPointIsolated(YDataType yDataType, Rank rank) {
		boolean isolated = false;
		switch (yDataType) {
		case DownloadsYAxisDataType:
			isolated = ranks.size() == 1
					|| ((ranks.indexOf(rank) == 0 && ranks.get(ranks.indexOf(rank) + 1) != null && ranks.get(ranks.indexOf(rank) + 1).downloads == null)
							|| (ranks.indexOf(rank) > 0 && ranks.indexOf(rank) < (ranks.size() - 1) && ranks.get(ranks.indexOf(rank) - 1) != null
									&& ranks.get(ranks.indexOf(rank) - 1).downloads == null && ranks.get(ranks.indexOf(rank) + 1) != null && ranks.get(ranks
									.indexOf(rank) + 1).downloads == null) || (ranks.indexOf(rank) == (ranks.size() - 1)
							&& ranks.get(ranks.indexOf(rank) - 1) != null && ranks.get(ranks.indexOf(rank) - 1).downloads == null));
			break;
		case RevenueYAxisDataType:
			isolated = ranks.size() == 1
					|| ((ranks.indexOf(rank) == 0 && ranks.get(ranks.indexOf(rank) + 1) != null && ranks.get(ranks.indexOf(rank) + 1).revenue == null)
							|| (ranks.indexOf(rank) > 0 && ranks.indexOf(rank) < (ranks.size() - 1) && ranks.get(ranks.indexOf(rank) - 1) != null
									&& ranks.get(ranks.indexOf(rank) - 1).revenue == null && ranks.get(ranks.indexOf(rank) + 1) != null && ranks.get(ranks
									.indexOf(rank) + 1).revenue == null) || (ranks.indexOf(rank) == (ranks.size() - 1)
							&& ranks.get(ranks.indexOf(rank) - 1) != null && ranks.get(ranks.indexOf(rank) - 1).revenue == null));
			break;
		case RankingYAxisDataType:
		default:
			isolated = ranks.size() == 1
					|| ((ranks.indexOf(rank) == 0 && ranks.get(ranks.indexOf(rank) + 1) != null && getRankPosition(ranks.get(ranks.indexOf(rank) + 1)) == 0)
							|| (ranks.indexOf(rank) > 0 && ranks.indexOf(rank) < (ranks.size() - 1) && ranks.get(ranks.indexOf(rank) - 1) != null
									&& getRankPosition(ranks.get(ranks.indexOf(rank) - 1)) == 0 && ranks.get(ranks.indexOf(rank) + 1) != null && getRankPosition(ranks
									.get(ranks.indexOf(rank) + 1)) == 0) || (ranks.indexOf(rank) == (ranks.size() - 1)
							&& ranks.get(ranks.indexOf(rank) - 1) != null && getRankPosition(ranks.get(ranks.indexOf(rank) - 1)) == 0));
			break;
		}
		return isolated;
	}

	public void setLoading(boolean loading) {

		if (loading) {
			removeAllSeries();
			ranks = null;
			if (dateRange == null) {
				dateRange = new DateRange();
			}
			dateRange.setFrom(FilterHelper.normalizeDate(FilterController.get().getStartDate()));
			dateRange.setTo(FilterHelper.normalizeDate(FilterController.get().getEndDate()));
			// TODO SUMMERTIME PROBLEM - e.g. in august the from date is 1 hour earlier, so the 23:00 of the day before
			NativeAxis.nativeSetExtremes(NativeAxis.nativeGetXAxis(chart, 0), dateRange.getFrom().getTime(), dateRange.getTo().getTime(), false, null);
			rearrangeXAxisLabels();
			reflow();
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

	public void resize() {
		if (chart != null) {
			int chartWidth = this.getElement().getClientWidth();
			if (chartWidth > 1920) {
				setSize(chartWidth, 750);
			} else if (chartWidth > 1680) {
				setSize(chartWidth, 650);
			} else if (chartWidth > 1280) {
				setSize(chartWidth, 550);
			} else if (chartWidth > 768) {
				setSize(chartWidth, 450);
			} else if (chartWidth > 480) {
				setSize(chartWidth, 350);
			} else {
				setSize(chartWidth, 250);
			}
		}
	}

	public void rearrangeXAxisLabels() {
		if (chart != null && dateRange != null) {
			double coeff = (double) dateRange.getDays() * X_LABELS_DISTANCE / this.getElement().getClientWidth();
			int step = (int) coeff + 1;

			// JsDate jDateFrom = JsDate.create(dateRange.getFrom().getYear(), dateRange.getFrom().getMonth(), dateRange.getFrom().getDate());
			getXAxis().setLabelsFormatter(ChartHelper.getNativeDatetimeLabelFormatter(chart, step));
			// boolean isSmallRange = dateRange.getDays() <= 4;
			// getXAxis().setShowFirstLabel(isSmallRange).setShowLastLabel(isSmallRange);
			NativeAxis.nativeUpdate(NativeAxis.nativeGetXAxis(chart, 0), getXAxis().getProperty(), true); // update x axis
			reflow();
		}
	}

	public void setSize(int width, int height) {
		NativeChart.nativeSetSize(chart, width, height, false);
	}

	public void setTitle(JavaScriptObject title, JavaScriptObject subtitle) {
		NativeChart.nativeSetTitle(chart, title, subtitle, true);
	}

	public void removeAllSeries() {
		NativeSeries.nativeRemoveAll(chart, true);
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
}
