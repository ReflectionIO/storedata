//
//  BaseChart.java
//  storedata
//
//  Created by Stefano Capuzzi (capuzzistefano) on 26 Mar 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.highcharts;

import io.reflection.app.client.helper.JavaScriptObjectHelper;
import io.reflection.app.client.highcharts.ChartHelper.LineType;
import io.reflection.app.client.highcharts.ChartHelper.RankType;
import io.reflection.app.client.highcharts.ChartHelper.XDataType;
import io.reflection.app.client.highcharts.ChartHelper.YAxisPosition;
import io.reflection.app.client.highcharts.options.ChartOption;
import io.reflection.app.client.highcharts.options.Credits;
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
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayMixed;
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
	public static final String OPTION_PRIMARY_Y_AXIS = "primaryYAxis";
	public static final String OPTION_SECONDARY_Y_AXIS = "secondaryYAxis";
	public static final String OPTION_TERTIARY_Y_AXIS = "tertiaryYAxis";

	public static final int X_LABELS_DISTANCE = 65;

	protected JavaScriptObject chart;
	protected XDataType xDataType;
	protected RankType rankingType;
	protected DateRange dateRange;
	private final HTMLPanel chartWrapper = new HTMLPanel("");
	private final String id = HTMLPanel.createUniqueId();
	private JavaScriptObject options = JavaScriptObject.createObject();
	private Map<String, Option<?>> optionsLookup = new HashMap<String, Option<?>>();
	JsArray<JavaScriptObject> yAxisJSArray = JavaScriptObject.createArray().cast(); // list of y axis

	public BaseChart(XDataType xDataType) {
		chartWrapper.getElement().setId(id);
		getChartOption().setRenderTo(id);
		this.xDataType = xDataType;
		ChartHelper.setDefaultOptions(this);
		ChartHelper.setDefaultXAxisOptions(this, xDataType);

		initWidget(chartWrapper);
	}

	private void inject() {
		chart = NativeHighcharts.nativeChart(options);
		// NativeHighcharts.nativeSetUTC(options, false);
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

	public void addAxis(JavaScriptObject options, boolean isX) {
		NativeChart.nativeAddAxis(chart, options, isX, true, false);
	}

	public void addSeries(JavaScriptObject data, LineType lineType, String seriesId, String name, String color, YAxisPosition axisId, JavaScriptObject tooltip) {
		JavaScriptObject series = JavaScriptObject.createObject();
		JavaScriptObjectHelper.setObjectProperty(series, "data", data); // Add points to draw
		JavaScriptObjectHelper.setObjectProperty(series, "tooltip", tooltip); // Set custom tooltip options
		JavaScriptObjectHelper.setStringProperty(series, "type", lineType.toString());
		JavaScriptObjectHelper.setStringProperty(series, "id", seriesId); // Unique series id
		JavaScriptObjectHelper.setStringProperty(series, "name", name);
		JavaScriptObjectHelper.setStringProperty(series, "color", color);
		JavaScriptObjectHelper.setStringProperty(series, "yAxis", axisId.toString()); // Link series with proper axis

		NativeChart.nativeAddSeries(chart, series, true, false);
	}

	public JsArrayMixed createConnectingPoint(double x, double y) {
		JsArrayMixed point = JavaScriptObject.createArray().cast();
		point.push(x);
		point.push(y);
		return point;
	}

	public JsArrayMixed createConnectingPoint(double x, JavaScriptObject y) {
		JsArrayMixed point = JavaScriptObject.createArray().cast();
		point.push(x);
		point.push(y);
		return point;
	}

	public JavaScriptObject createMarkerPoint(double x, double y) {
		JavaScriptObject point = JavaScriptObject.createObject();
		JavaScriptObjectHelper.setDoubleProperty(point, "x", x);
		JavaScriptObjectHelper.setDoubleProperty(point, "y", y);
		JavaScriptObject marker = JavaScriptObject.createObject();
		JavaScriptObjectHelper.setBooleanProperty(marker, "enabled", true);
		JavaScriptObjectHelper.setIntegerProperty(marker, "radius", 4);
		JavaScriptObjectHelper.setObjectProperty(point, "marker", marker);
		return point;
	}

	public JavaScriptObject get(String id) {
		return NativeChart.nativeGet(chart, id);
	}

	public void setSeriesVisible(String id, boolean visible) {
		if (get(id) != null) {
			if (visible) {
				NativeSeries.nativeShow(get(id));
			} else {
				NativeSeries.nativeHide(get(id));
			}
		}
	}

	public void destroy() {
		NativeChart.nativeDestroy(chart);
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
			NativeAxis.nativeUpdate(NativeAxis.nativeGetXAxis(chart, 0), getXAxis().getOptions(), true); // update x axis
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

	public void removeAllYAxis() {
		NativeAxis.nativeRemoveAllY(chart, true);
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
			JavaScriptObjectHelper.setObjectProperty(options, OPTION_CHART, optionsLookup.get(OPTION_CHART).getOptions());
		}
		return (ChartOption) optionsLookup.get(OPTION_CHART);
	}

	public Credits getCreditsOption() {
		if (optionsLookup.get(OPTION_CREDITS) == null) {
			optionsLookup.put(OPTION_CREDITS, new Credits());
			JavaScriptObjectHelper.setObjectProperty(options, OPTION_CREDITS, optionsLookup.get(OPTION_CREDITS).getOptions());
		}
		return (Credits) optionsLookup.get(OPTION_CREDITS);
	}

	public Labels getLabelsOption() {
		if (optionsLookup.get(OPTION_LABELS) == null) {
			optionsLookup.put(OPTION_LABELS, new Labels());
			JavaScriptObjectHelper.setObjectProperty(options, OPTION_LABELS, optionsLookup.get(OPTION_LABELS).getOptions());
		}
		return (Labels) optionsLookup.get(OPTION_LABELS);
	}

	public Legend getLegendOption() {
		if (optionsLookup.get(OPTION_LEGEND) == null) {
			optionsLookup.put(OPTION_LEGEND, new Legend());
			JavaScriptObjectHelper.setObjectProperty(options, OPTION_LEGEND, optionsLookup.get(OPTION_LEGEND).getOptions());
		}
		return (Legend) optionsLookup.get(OPTION_LEGEND);
	}

	public Loading getLoadingOption() {
		if (optionsLookup.get(OPTION_LOADING) == null) {
			optionsLookup.put(OPTION_LOADING, new Loading());
			JavaScriptObjectHelper.setObjectProperty(options, OPTION_LOADING, optionsLookup.get(OPTION_LOADING).getOptions());
		}
		return (Loading) optionsLookup.get(OPTION_LOADING);
	}

	public NoData getNoDataOption() {
		if (optionsLookup.get(OPTION_NO_DATA) == null) {
			optionsLookup.put(OPTION_NO_DATA, new NoData());
			JavaScriptObjectHelper.setObjectProperty(options, OPTION_NO_DATA, optionsLookup.get(OPTION_NO_DATA).getOptions());
		}
		return (NoData) optionsLookup.get(OPTION_NO_DATA);
	}

	public PlotOption getPlotOption() {
		if (optionsLookup.get(OPTION_PLOT_OPTIONS) == null) {
			optionsLookup.put(OPTION_PLOT_OPTIONS, new PlotOption());
			JavaScriptObjectHelper.setObjectProperty(options, OPTION_PLOT_OPTIONS, optionsLookup.get(OPTION_PLOT_OPTIONS).getOptions());
		}
		return (PlotOption) optionsLookup.get(OPTION_PLOT_OPTIONS);
	}

	public Series getSeriesOption() {
		if (optionsLookup.get(OPTION_SERIES) == null) {
			optionsLookup.put(OPTION_SERIES, new Series());
			JavaScriptObjectHelper.setObjectProperty(options, OPTION_SERIES, optionsLookup.get(OPTION_SERIES).getOptions());
		}
		return (Series) optionsLookup.get(OPTION_SERIES);
	}

	public Subtitle getSubtitleOption() {
		if (optionsLookup.get(OPTION_SUBTITLE) == null) {
			optionsLookup.put(OPTION_SUBTITLE, new Subtitle());
			JavaScriptObjectHelper.setObjectProperty(options, OPTION_SUBTITLE, optionsLookup.get(OPTION_SUBTITLE).getOptions());
		}
		return (Subtitle) optionsLookup.get(OPTION_SUBTITLE);
	}

	public Title getTitleOption() {
		if (optionsLookup.get(OPTION_TITLE) == null) {
			optionsLookup.put(OPTION_TITLE, new Title());
			JavaScriptObjectHelper.setObjectProperty(options, OPTION_TITLE, optionsLookup.get(OPTION_TITLE).getOptions());
		}
		return (Title) optionsLookup.get(OPTION_TITLE);
	}

	public Tooltip getTooltipOption() {
		if (optionsLookup.get(OPTION_TOOLTIP) == null) {
			optionsLookup.put(OPTION_TOOLTIP, new Tooltip());
			JavaScriptObjectHelper.setObjectProperty(options, OPTION_TOOLTIP, optionsLookup.get(OPTION_TOOLTIP).getOptions());
		}
		return (Tooltip) optionsLookup.get(OPTION_TOOLTIP);
	}

	public XAxis getXAxis() {
		if (optionsLookup.get(OPTION_X_AXIS) == null) {
			optionsLookup.put(OPTION_X_AXIS, new XAxis());
			JavaScriptObjectHelper.setObjectProperty(options, OPTION_X_AXIS, optionsLookup.get(OPTION_X_AXIS).getOptions());
		}
		return (XAxis) optionsLookup.get(OPTION_X_AXIS);
	}

	public YAxis getPrimaryAxis() {
		if (optionsLookup.get(OPTION_PRIMARY_Y_AXIS) == null) {
			optionsLookup.put(OPTION_PRIMARY_Y_AXIS, new YAxis());
			yAxisJSArray.push(optionsLookup.get(OPTION_PRIMARY_Y_AXIS).getOptions());
			JavaScriptObjectHelper.setObjectProperty(options, OPTION_Y_AXIS, yAxisJSArray);
		}
		return (YAxis) optionsLookup.get(OPTION_PRIMARY_Y_AXIS);
	}

	public YAxis getSecondaryYAxis() {
		if (optionsLookup.get(OPTION_SECONDARY_Y_AXIS) == null) {
			optionsLookup.put(OPTION_SECONDARY_Y_AXIS, new YAxis());
			yAxisJSArray.push(optionsLookup.get(OPTION_SECONDARY_Y_AXIS).getOptions());
			JavaScriptObjectHelper.setObjectProperty(options, OPTION_Y_AXIS, yAxisJSArray);
		}
		return (YAxis) optionsLookup.get(OPTION_SECONDARY_Y_AXIS);
	}

	public YAxis getTertiaryYAxis() {
		if (optionsLookup.get(OPTION_TERTIARY_Y_AXIS) == null) {
			optionsLookup.put(OPTION_TERTIARY_Y_AXIS, new YAxis());
			yAxisJSArray.push(optionsLookup.get(OPTION_TERTIARY_Y_AXIS).getOptions());
			JavaScriptObjectHelper.setObjectProperty(options, OPTION_Y_AXIS, yAxisJSArray);
		}
		return (YAxis) optionsLookup.get(OPTION_TERTIARY_Y_AXIS);
	}
}
