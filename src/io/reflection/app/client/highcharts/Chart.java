//
//  Chart.java
//  storedata
//
//  Created by Stefano Capuzzi on 14 Jan 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.highcharts;

import io.reflection.app.client.controller.FilterController;
import io.reflection.app.client.helper.FilterHelper;
import io.reflection.app.client.helper.JavaScriptObjectHelper;
import io.reflection.app.client.highcharts.ChartHelper.LineType;
import io.reflection.app.client.highcharts.ChartHelper.RankType;
import io.reflection.app.client.highcharts.ChartHelper.XDataType;
import io.reflection.app.client.highcharts.ChartHelper.YAxisPosition;
import io.reflection.app.client.highcharts.ChartHelper.YDataType;
import io.reflection.app.client.highcharts.options.YAxis;
import io.reflection.app.client.part.datatypes.DateRange;
import io.reflection.app.datatypes.shared.Rank;
import io.reflection.app.shared.util.FormattingHelper;

import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.user.datepicker.client.CalendarUtil;

/**
 * @author Stefano Capuzzi
 * 
 *         See http://api.highcharts.com/highcharts
 *
 */
public class Chart extends BaseChart {

	private String currency;
	private final int yMinCeilingDownloads = 1;
	private final int yMinCeilingRevenue = 1;

	// private final int yMinCeilingRanking = 10;

	public Chart(XDataType xDataType) {
		super(xDataType);
	}

	public void drawData(List<Rank> ranks, YAxisPosition yAxisPosition, YDataType yDataType, String seriesId, LineType lineType, String color,
			boolean isCumulative, boolean hide) {

		if (ranks != null && ranks.size() > 0) {
			boolean isOpposite = (yAxisPosition != YAxisPosition.PRIMARY);

			// Get axis options to use
			YAxis yAxis = getPrimaryAxis();
			switch (yAxisPosition) {
			case PRIMARY:
				break;
			case SECONDARY:
				yAxis = getSecondaryYAxis();
				break;
			case TERTIARY:
				yAxis = getTertiaryYAxis();
				break;
			}

			String seriesName = seriesId;

			JavaScriptObject tooltip = JavaScriptObject.createObject();

			// Set series names and currency
			switch (yDataType) {
			case DownloadsYAxisDataType:
				yAxis.setLabelsStyle(ChartHelper.getYAxisLabelsStyle(color));
				seriesName = "Downloads";
				tooltip = ChartHelper.getTooltipSeriesOption();
				break;

			case RevenueYAxisDataType:
				yAxis.setLabelsStyle(ChartHelper.getYAxisLabelsStyle(color));
				// Set currency
				for (Rank rank : ranks) {
					if (currency == null && rank.currency != null) {
						setCurrency(FormattingHelper.getCurrencySymbol(rank.currency));
						break;
					}
				}
				if (currency == null) {
					setCurrency(FormattingHelper.getCurrencySymbol(""));
				}
				yAxis.setLabelsFormatter(ChartHelper.getNativeLabelFormatter(currency, ""));
				seriesName = "Revenue";
				tooltip = ChartHelper.getTooltipRevenueSeriesOption(currency);
				break;

			case RankingYAxisDataType:
				seriesName = "Ranking";
				tooltip = ChartHelper.getTooltipSeriesOption();
				break;
			}

			JsArray<JavaScriptObject> data = JavaScriptObject.createArray().cast();

			switch (xDataType) {
			case DateXAxisDataType:
				if (get(seriesId) == null && dateRange != null) {
					double cumulative = 0;
					Date progressiveDate = dateRange.getFrom();
					for (Rank rank : ranks) {
						// Fill with blank data next range of missing dates
						if (!CalendarUtil.isSameDate(progressiveDate, rank.date)) {
							while (!CalendarUtil.isSameDate(progressiveDate, rank.date) && FilterHelper.beforeDate(progressiveDate, dateRange.getTo())) {
								data.push(createConnectingPoint(progressiveDate.getTime(), JavaScriptObjectHelper.getNativeNull()));
								CalendarUtil.addDaysToDate(progressiveDate, 1);
							}
						}
						CalendarUtil.addDaysToDate(progressiveDate, 1);
						Double yPoint = getYPointValue(rank, yDataType);
						if (yPoint != null) {
							if (isPointIsolated(yDataType, ranks, rank)) {
								data.push(createMarkerPoint(rank.date.getTime(), (isCumulative ? cumulative += yPoint.doubleValue() : yPoint.doubleValue())));
							} else {
								data.push(createConnectingPoint(rank.date.getTime(), (isCumulative ? cumulative += yPoint.doubleValue() : yPoint.doubleValue())));
							}
						} else {
							data.push(createConnectingPoint(rank.date.getTime(), JavaScriptObjectHelper.getNativeNull()));
						}
					}
					addSeries(data, lineType, seriesId, seriesName, color, yAxisPosition, tooltip);
				}
				break;

			case RankingXAxisDataType:
				if (get(seriesId) == null) {
					for (Rank rank : ranks) {
						Double yPoint = getYPointValue(rank, yDataType);
						if (yPoint != null) {
							data.push(createMarkerPoint(rank.position.doubleValue(), yPoint));
						} else {
							data.push(createConnectingPoint(rank.position.doubleValue(), JavaScriptObjectHelper.getNativeNull()));
						}

					}
					addSeries(data, lineType, seriesId, seriesName, color, yAxisPosition, tooltip);
				}
				break;
			}

			// Set y axis extremes
			switch (yDataType) {
			case DownloadsYAxisDataType:
				yAxis.setMin(0).setFloor(0).setShowFirstLabel(false);
				if (!isOpposite) {
					if (NativeAxis.nativeGetDataMax(get(yAxisPosition.toString())) < yMinCeilingDownloads) {
						yAxis.setMax(yMinCeilingDownloads);
					} else {
						yAxis.setMax(JavaScriptObjectHelper.getNativeNull());
					}
				}
				break;

			case RevenueYAxisDataType:
				yAxis.setMin(0).setFloor(0).setShowFirstLabel(false);
				if (!isOpposite) {
					// NativeChart.nativeUpdateTooltipFormatter(chart, ChartHelper.getNativeTooltipFormatter(currency));
					if (NativeAxis.nativeGetDataMax(get(yAxisPosition.toString())) < yMinCeilingRevenue) {
						yAxis.setMax(yMinCeilingRevenue);
					} else {
						yAxis.setMax(JavaScriptObjectHelper.getNativeNull());
					}
				}
				break;

			case RankingYAxisDataType:
				yAxis.setReversed(true).setLabelsFormatter(ChartHelper.getNativeLabelFormatterRank()).setMin(1).setFloor(1).setShowLastLabel(false);
				if (!isOpposite) {
					yAxis.setMax(JavaScriptObjectHelper.getNativeNull());
				}
				break;
			}
			NativeAxis.nativeUpdate(get(yAxisPosition.toString()), yAxis.getOptions(), true); // update secondary y axis

			resize();
			reflow();
			if (hide) {
				setSeriesVisible(seriesId, false);
			}

		}
	}

	private Double getYPointValue(Rank rank, YDataType yDataType) {
		Double yPointValue = null;
		switch (yDataType) {
		case DownloadsYAxisDataType:
			if (rank.downloads != null) {
				yPointValue = Double.valueOf(rank.downloads.intValue());
			}
			break;

		case RevenueYAxisDataType:
			if (rank.revenue != null) {
				yPointValue = Double.valueOf(rank.revenue.floatValue());
			}
			break;

		case RankingYAxisDataType:
			Double position = Double.valueOf(getRankPosition(rank));
			if (position >= 1) {
				yPointValue = position;
			}
			break;
		}
		return yPointValue;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getCurrency() {
		return currency;
	}

	// Currently working for DateTime X Axis
	private boolean isPointIsolated(YDataType yDataType, List<Rank> ranks, Rank rank) {
		boolean isolated = false;
		int rankIndex = ranks.indexOf(rank);
		int lastIndex = ranks.size() - 1;
		switch (yDataType) {
		case DownloadsYAxisDataType:
			if ((ranks.size() == 1)
					|| (rankIndex == 0 && (Math.abs(CalendarUtil.getDaysBetween(rank.date, ranks.get(1).date)) > 1 || ranks.get(1).downloads == null))
					|| (rankIndex > 0 && rankIndex < lastIndex && Math.abs(CalendarUtil.getDaysBetween(rank.date, ranks.get(rankIndex - 1).date)) > 1
							&& Math.abs(CalendarUtil.getDaysBetween(rank.date, ranks.get(rankIndex + 1).date)) > 1
							&& ranks.get(rankIndex - 1).downloads != null && ranks.get(rankIndex + 1).downloads != null)
					|| (rankIndex == lastIndex && (Math.abs(CalendarUtil.getDaysBetween(rank.date, ranks.get(lastIndex - 1).date)) > 1 || ranks
							.get(lastIndex - 1).downloads == null))) {
				isolated = true;
			}
			break;

		case RevenueYAxisDataType:
			if ((ranks.size() == 1)
					|| (rankIndex == 0 && (Math.abs(CalendarUtil.getDaysBetween(rank.date, ranks.get(1).date)) > 1 || ranks.get(1).revenue == null))
					|| (rankIndex > 0 && rankIndex < lastIndex && Math.abs(CalendarUtil.getDaysBetween(rank.date, ranks.get(rankIndex - 1).date)) > 1
							&& Math.abs(CalendarUtil.getDaysBetween(rank.date, ranks.get(rankIndex + 1).date)) > 1 && ranks.get(rankIndex - 1).revenue != null && ranks
							.get(rankIndex + 1).revenue != null)
					|| (rankIndex == lastIndex && (Math.abs(CalendarUtil.getDaysBetween(rank.date, ranks.get(lastIndex - 1).date)) > 1 || ranks
							.get(lastIndex - 1).revenue == null))) {
				isolated = true;
			}
			break;

		case RankingYAxisDataType:
			if ((ranks.size() == 1)
					|| (rankIndex == 0 && (Math.abs(CalendarUtil.getDaysBetween(rank.date, ranks.get(1).date)) > 1 || getRankPosition(ranks.get(1)) == 0))
					|| (rankIndex > 0
							&& rankIndex < lastIndex
							&& (Math.abs(CalendarUtil.getDaysBetween(rank.date, ranks.get(rankIndex - 1).date)) > 1 || getRankPosition(ranks.get(rankIndex - 1)) == 0) && (Math
							.abs(CalendarUtil.getDaysBetween(rank.date, ranks.get(rankIndex + 1).date)) > 1 || getRankPosition(ranks.get(rankIndex + 1)) == 0))
					|| (rankIndex == lastIndex && (Math.abs(CalendarUtil.getDaysBetween(rank.date, ranks.get(lastIndex - 1).date)) > 1 || getRankPosition(ranks
							.get(lastIndex - 1)) == 0))) {
				isolated = true;
			}
			break;
		}

		return isolated;
	}

	public void setLoading(boolean loading) {

		if (loading) { // Reset chart
			currency = null;
			if (XDataType.DateXAxisDataType.equals(xDataType)) {
				if (dateRange == null) {
					dateRange = new DateRange();
				}
				dateRange.setFrom(FilterHelper.normalizeDateUTC(FilterController.get().getStartDate()));
				dateRange.setTo(FilterHelper.normalizeDateUTC(FilterController.get().getEndDate()));
				setXAxisExtremes((double) dateRange.getFrom().getTime(), (double) dateRange.getTo().getTime());
				// TODO reset y axis extremes
				// getPrimaryAxis().setMin(JavaScriptObjectHelper.getNativeNull());
				// getPrimaryAxis().setMax(JavaScriptObjectHelper.getNativeNull());
				// NativeAxis.nativeUpdate(NativeAxis.nativeGetYAxis(chart, 0), getPrimaryAxis().getOptions(), true); // update y axis
			}
			removeAllSeries();
			reflow();
		}

	}

	private int getRankPosition(Rank rank) {
		return rankingType == RankType.PositionRankingType ? rank.position.intValue() : rank.grossingPosition.intValue();
	}

}
