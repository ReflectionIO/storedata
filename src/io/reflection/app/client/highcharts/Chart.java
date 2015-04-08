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
import io.reflection.app.client.highcharts.ChartHelper.RankType;
import io.reflection.app.client.highcharts.ChartHelper.XDataType;
import io.reflection.app.client.highcharts.ChartHelper.YDataType;
import io.reflection.app.client.part.datatypes.DateRange;
import io.reflection.app.datatypes.shared.Rank;
import io.reflection.app.shared.util.FormattingHelper;

import java.util.ArrayList;
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

	public Chart(XDataType xDataType, YDataType yDataType) {
		super(xDataType, yDataType);
	}

	public void drawData(List<Rank> ranks, String id, String seriesType, String color, boolean isCumulative, boolean hide) {
		setLoading(false);

		if (ranks != null && yDataType != null) {

			if (YDataType.RevenueYAxisDataType.equals(yDataType)) {
				for (Rank rank : ranks) {
					if (currency == null) {
						setCurrency(FormattingHelper.getCurrencySymbol(rank.currency));
						break;
					}
				}
			}

			JsArray<JavaScriptObject> data = JavaScriptObject.createArray().cast();

			switch (xDataType) {
			case DateXAxisDataType:
				if (dateRange != null) {
					removeOutOfRangeRanks(ranks);
					Date progressiveDate = dateRange.getFrom();
					if (NativeChart.nativeGet(chart, id) == null) {
						double cumulative = 0;
						for (Rank rank : ranks) {
							// Fill with blank data missing dates
							if (!FilterHelper.equalDate(progressiveDate, rank.date)) {
								while (!FilterHelper.equalDate(progressiveDate, rank.date) && FilterHelper.beforeOrSameDate(progressiveDate, dateRange.getTo())) {
									data.push(ChartHelper.createPoint(progressiveDate.getTime(), JavaScriptObjectHelper.getNativeNull()));
									CalendarUtil.addDaysToDate(progressiveDate, 1);
								}
							}
							CalendarUtil.addDaysToDate(progressiveDate, 1);
							Double yPoint = getYPointValue(rank);
							if (yPoint != null && showModelPredictions) {
								if (isPointIsolated(yDataType, ranks, rank)) {
									data.push(ChartHelper.createMarkerPoint(rank.date.getTime(),
											(isCumulative ? cumulative += yPoint.doubleValue() : yPoint.doubleValue())));
								} else {
									data.push(ChartHelper.createPoint(rank.date.getTime(),
											(isCumulative ? cumulative += yPoint.doubleValue() : yPoint.doubleValue())));
								}
							} else {
								data.push(ChartHelper.createPoint(rank.date.getTime(), JavaScriptObjectHelper.getNativeNull()));
							}
						}
						addSeries(data, seriesType, id, color);
						if (hide) {
							hideSeries(id);
						}
					}
				}
				break;

			case RankingXAxisDataType:
				if (NativeChart.nativeGet(chart, id) == null) {
					for (Rank rank : ranks) {
						Double yPoint = getYPointValue(rank);
						data.push(ChartHelper.createMarkerPoint(rank.position.doubleValue(), yPoint));
					}
					addSeries(data, seriesType, id, color);
				}
				break;
			}

			setOptions();
			resize();
		}
	}

	public void drawData(List<Rank> ranks, String id, String seriesType) {
		drawData(ranks, id, seriesType, "", false, false);
	}

	public void drawData(List<Rank> ranks, String id, String seriesType, boolean isCumulative) {
		drawData(ranks, id, seriesType, "", isCumulative, false);
	}

	public void drawData(List<Rank> ranks, String id, String seriesType, String color) {
		drawData(ranks, id, seriesType, color, false, false);
	}

	public void drawData(List<Rank> ranks, String id, String seriesType, String color, boolean isCumulative) {
		drawData(ranks, id, seriesType, color, isCumulative, false);
	}

	public void drawData(List<Rank> ranks, String id, String seriesType, boolean isCumulative, boolean hide) {
		drawData(ranks, id, seriesType, "", isCumulative, hide);
	}

	private Double getYPointValue(Rank rank) {
		Double yPointValue = null;
		switch (yDataType) {
		case DownloadsYAxisDataType:
			yPointValue = Double.valueOf(rank.downloads.intValue());
			break;

		case RevenueYAxisDataType:
			yPointValue = Double.valueOf(rank.revenue);
			break;

		case RankingYAxisDataType:
			yPointValue = Double.valueOf(getRankPosition(rank));
			break;
		}
		return yPointValue;
	}

	/**
	 * Set options dependent by dinamic values
	 */
	private void setOptions() {
		switch (yDataType) {
		case DownloadsYAxisDataType:
			if (NativeAxis.nativeGetDataMax(NativeAxis.nativeGetYAxis(chart, 0)) < yMinCeilingDownloads) {
				getYAxis().setMax(yMinCeilingDownloads);
			} else {
				getYAxis().setMax(JavaScriptObjectHelper.getNativeNull());
			}
			break;

		case RevenueYAxisDataType:
			getYAxis().setLabelsFormatter(ChartHelper.getNativeLabelFormatter(currency, ""));
			NativeChart.nativeUpdateTooltipFormatter(chart, ChartHelper.getNativeTooltipFormatter(currency));
			if (NativeAxis.nativeGetDataMax(NativeAxis.nativeGetYAxis(chart, 0)) < yMinCeilingRevenue) {
				getYAxis().setMax(yMinCeilingRevenue);
			} else {
				getYAxis().setMax(JavaScriptObjectHelper.getNativeNull());
			}
			break;

		case RankingYAxisDataType:
			getYAxis().setMax(JavaScriptObjectHelper.getNativeNull());
			break;
		}
		NativeAxis.nativeUpdate(NativeAxis.nativeGetYAxis(chart, 0), getYAxis().getProperty(), true); // update y axis
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	private void removeOutOfRangeRanks(List<Rank> ranks) {
		List<Rank> outOfRangeRanks = new ArrayList<Rank>();
		for (Rank rank : ranks) {
			if (XDataType.DateXAxisDataType.equals(xDataType) && dateRange != null) {
				if (!withinChartDateRange(rank)) {
					outOfRangeRanks.add(rank);
				} else {
					rank.date = FilterHelper.normalizeDate(rank.date);
				}
			}
		}
		ranks.removeAll(outOfRangeRanks);
	}

	/**
	 * Check if x data is contained within the wished range (it can be excluded because of the windowing cache that retrieves bunche)
	 * 
	 * @param rank
	 * @return
	 */
	private boolean withinChartDateRange(Rank rank) {
		return FilterHelper.afterOrSameDate(rank.date, dateRange.getFrom()) && FilterHelper.beforeOrSameDate(rank.date, dateRange.getTo());
	}

	// Currently working for DateTime X Axis
	private boolean isPointIsolated(YDataType yDataType, List<Rank> ranks, Rank rank) {
		boolean isolated = false;
		if (ranks.size() == 1) {
			isolated = true;
		} else {
			int rankIndex = ranks.indexOf(rank);
			int lastIndex = ranks.size() - 1;
			switch (yDataType) {
			case DownloadsYAxisDataType:
				isolated = (rankIndex == 0 && (ranks.get(1).downloads == null || CalendarUtil.getDaysBetween(rank.date, ranks.get(1).date) > 1))
						|| (rankIndex > 0 && rankIndex < lastIndex && ((ranks.get(rankIndex - 1).downloads == null && ranks.get(rankIndex + 1).downloads == null) || (CalendarUtil
								.getDaysBetween(rank.date, ranks.get(rankIndex - 1).date) > 1 && CalendarUtil.getDaysBetween(rank.date,
								ranks.get(rankIndex + 1).date) > 1)))
						|| (rankIndex == lastIndex && (ranks.get(lastIndex - 1).downloads == null || CalendarUtil.getDaysBetween(rank.date,
								ranks.get(lastIndex - 1).date) > 1));
				break;
			case RevenueYAxisDataType:
				isolated = (rankIndex == 0 && (ranks.get(1).revenue == null || CalendarUtil.getDaysBetween(rank.date, ranks.get(1).date) > 1))
						|| (rankIndex > 0 && rankIndex < lastIndex && ((ranks.get(rankIndex - 1).revenue == null && ranks.get(rankIndex + 1).revenue == null) || (CalendarUtil
								.getDaysBetween(rank.date, ranks.get(rankIndex - 1).date) > 1 && CalendarUtil.getDaysBetween(rank.date,
								ranks.get(rankIndex + 1).date) > 1)))
						|| (rankIndex == lastIndex && (ranks.get(lastIndex - 1).revenue == null || CalendarUtil.getDaysBetween(rank.date,
								ranks.get(lastIndex - 1).date) > 1));
				break;
			case RankingYAxisDataType:
			default:
				isolated = (rankIndex == 0 && (getRankPosition(ranks.get(1)) == 0 || CalendarUtil.getDaysBetween(rank.date, ranks.get(1).date) > 1))
						|| (rankIndex > 0 && rankIndex < lastIndex && ((getRankPosition(ranks.get(rankIndex - 1)) == 0 && getRankPosition(ranks
								.get(rankIndex + 1)) == 0) || (CalendarUtil.getDaysBetween(rank.date, ranks.get(rankIndex - 1).date) > 1 && CalendarUtil
								.getDaysBetween(rank.date, ranks.get(rankIndex + 1).date) > 1)))
						|| (rankIndex == lastIndex && (getRankPosition(ranks.get(lastIndex - 1)) == 0 || CalendarUtil.getDaysBetween(rank.date,
								ranks.get(lastIndex - 1).date) > 1));
				break;
			}
		}
		return isolated;
	}

	public void setLoading(boolean loading) {

		if (loading) { // Reset chart
			removeAllSeries();
			currency = null;
			if (XDataType.DateXAxisDataType.equals(xDataType)) {
				if (dateRange == null) {
					dateRange = new DateRange();
				}
				dateRange.setFrom(FilterHelper.normalizeDate(FilterController.get().getStartDate()));
				dateRange.setTo(FilterHelper.normalizeDate(FilterController.get().getEndDate()));
				// TODO SUMMERTIME PROBLEM - e.g. in august the from date is 1 hour earlier, so the 23:00 of the day before
				setXAxisExtremes(dateRange.getFrom().getTime(), dateRange.getTo().getTime());
				rearrangeXAxisLabels();
			}
			reflow();
		}

	}

	private int getRankPosition(Rank rank) {
		return rankingType == RankType.PositionRankingType ? rank.position.intValue() : rank.grossingPosition.intValue();
	}

}
