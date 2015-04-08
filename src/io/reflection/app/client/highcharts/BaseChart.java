//
//  BaseChart.java
//  storedata
//
//  Created by Stefano Capuzzi (capuzzistefano) on 26 Mar 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.highcharts;

import io.reflection.app.client.controller.SessionController;
import io.reflection.app.client.helper.JavaScriptObjectHelper;
import io.reflection.app.client.highcharts.ChartHelper.RankType;
import io.reflection.app.client.highcharts.ChartHelper.XDataType;
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

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;

/**
 * @author Stefano Capuzzi (capuzzistefano)
 *
 */
public abstract class BaseChart extends Composite {

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

	protected JavaScriptObject chart;
	protected XDataType xDataType;
	protected YDataType yDataType;
	protected RankType rankingType;
	protected DateRange dateRange;
	protected final boolean showModelPredictions = SessionController.get().isLoggedInUserAdmin();
	private final HTMLPanel chartWrapper = new HTMLPanel("");
	private final String id = HTMLPanel.createUniqueId();
	private JavaScriptObject options = JavaScriptObject.createObject();
	private Map<String, Option<?>> optionsLookup = new HashMap<String, Option<?>>();

	public BaseChart(XDataType xDataType, YDataType yDataType) {
		chartWrapper.getElement().setId(id);
		getChartOption().setRenderTo(id);
		this.xDataType = xDataType;
		this.yDataType = yDataType;
		ChartHelper.setDefaultOptions(this);
		ChartHelper.setDefaultXAxisOptions(this, xDataType);
		ChartHelper.setDefaultYAxisOptions(this, yDataType);

		initWidget(chartWrapper);
	}

	private void inject() {
		chart = NativeHighcharts.nativeChart(options);
		resize();
		Window.addResizeHandler(new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				resize();
				rearrangeXAxisLabels();
			}
		});
	}

	public void setXDataType(XDataType xDataType) {
		this.xDataType = xDataType;
	}

	public void setYDataType(YDataType yDataType) {
		this.yDataType = yDataType;
	}

	public void addAxis(JavaScriptObject options, boolean isX) {
		NativeChart.nativeAddAxis(chart, options, isX, true, false);
	}

	public void addSeries(JavaScriptObject series) {
		NativeChart.nativeAddSeries(chart, series, true, false);
	}

	public void addSeries(JavaScriptObject data, String seriesType, String id, String color) {
		JavaScriptObject series = JavaScriptObject.createObject();
		JavaScriptObjectHelper.setObjectProperty(series, "data", data);
		JavaScriptObjectHelper.setStringProperty(series, "type", seriesType);
		JavaScriptObjectHelper.setStringProperty(series, "id", id);
		JavaScriptObjectHelper.setStringProperty(series, "name", id);
		JavaScriptObjectHelper.setStringProperty(series, "color", color);
		addSeries(series);
	}

	public void addSeries(JavaScriptObject data, String seriesType, String id) {
		addSeries(data, seriesType, id, "");
	}

	public void showSeries(String id) {
		if (NativeChart.nativeGet(chart, id) != null) {
			NativeSeries.nativeShow(NativeChart.nativeGet(chart, id));
		}
	}

	public void hideSeries(String id) {
		if (NativeChart.nativeGet(chart, id) != null) {
			NativeSeries.nativeHide(NativeChart.nativeGet(chart, id));
		}
	}

	public void destroy() {
		NativeChart.nativeDestroy(chart);
	}

	public JavaScriptObject get(String id) {
		return NativeChart.nativeGet(chart, id);
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
		switch (xDataType) {
		case DateXAxisDataType:
			rearrangeXAxisDatetimeLabels();
			break;

		case RankingXAxisDataType:
			// TODO
			break;
		}
	}

	protected void rearrangeXAxisDatetimeLabels() {
		if (chart != null && dateRange != null) {
			double coeff = (double) dateRange.getDays() * X_LABELS_DISTANCE / this.getElement().getClientWidth();
			int step = (int) coeff + 1;
			// JsDate jDateFrom = JsDate.create(dateRange.getFrom().getYear(), dateRange.getFrom().getMonth(), dateRange.getFrom().getDate());
			getXAxis().setLabelsFormatter(ChartHelper.getNativeDatetimeLabelFormatter(chart, step));
			// boolean isSmallRange = dateRange.getDays() <= 4;
			// getXAxis().setShowFirstLabel(isSmallRange).setShowLastLabel(isSmallRange);
			NativeAxis.nativeUpdate(NativeAxis.nativeGetXAxis(chart, 0), getXAxis().getProperty(), true); // update x axis
			// reflow();
		}
	}

	public void redraw() {
		NativeChart.nativeRedraw(chart);
	}

	public void reflow() {
		NativeChart.nativeReflow(chart);
	}

	public void removeAllSeries() {
		NativeSeries.nativeRemoveAll(chart, true);
	}

	public void setColors(JsArrayString colors) {
		JavaScriptObjectHelper.setObjectProperty(options, OPTION_COLORS, colors);
	}

	public void setRankingType(RankType rankingType) {
		this.rankingType = rankingType;
	}

	public void setXAxisExtremes(double min, double max) {
		NativeAxis.nativeSetExtremes(NativeAxis.nativeGetXAxis(chart, 0), min, max, true, false);
	}

	public void setXAxisMin(double min) {
		NativeAxis.nativeSetExtremes(NativeAxis.nativeGetXAxis(chart, 0), min, NativeAxis.nativeGetMax(NativeAxis.nativeGetXAxis(chart, 0)), true, false);
	}

	public double getXAxisMin() {
		return NativeAxis.nativeGetMin(NativeAxis.nativeGetXAxis(chart, 0));
	}

	public void resetXAxisMin() {
		NativeAxis.nativeSetExtremes(NativeAxis.nativeGetXAxis(chart, 0), JavaScriptObjectHelper.getNativeNull(),
				NativeAxis.nativeGetMax(NativeAxis.nativeGetXAxis(chart, 0)), true, false);
	}

	public void setXAxisMax(double max) {
		NativeAxis.nativeSetExtremes(NativeAxis.nativeGetXAxis(chart, 0), NativeAxis.nativeGetMin(NativeAxis.nativeGetXAxis(chart, 0)), max, true, false);
	}

	public double getXAxisMax() {
		return NativeAxis.nativeGetMax(NativeAxis.nativeGetXAxis(chart, 0));
	}

	public void resetXAxisMax() {
		NativeAxis.nativeSetExtremes(NativeAxis.nativeGetXAxis(chart, 0), NativeAxis.nativeGetMin(NativeAxis.nativeGetXAxis(chart, 0)),
				JavaScriptObjectHelper.getNativeNull(), true, false);
	}

	public void setSize(int width, int height) {
		NativeChart.nativeSetSize(chart, width, height, false);
	}

	public void setTitle(JavaScriptObject title, JavaScriptObject subtitle) {
		NativeChart.nativeSetTitle(chart, title, subtitle, true);
	}

	public void setVisible(boolean visible) {
		super.setVisible(visible);
		resize();
		rearrangeXAxisLabels();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.Widget#onAttach()
	 */
	@Override
	protected void onAttach() {
		super.onAttach();
		if (chart == null) {
			inject();
		}
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
