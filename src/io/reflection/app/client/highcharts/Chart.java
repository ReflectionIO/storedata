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
	private final int yMinCeilingDownloads = 100;
	private final int yMinCeilingRevenue = 100;

	// private final int yMinCeilingRanking = 10;

	public Chart(XDataType xDataType, YDataType yDataType) {
		super(xDataType, yDataType);
	}

	public void drawData(List<Rank> ranks, String id, String seriesType, String color) {
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
						for (Rank rank : ranks) {
							// Fill with blank data missing dates
							if (!FilterHelper.equalDate(progressiveDate, rank.date)) {
								while (!FilterHelper.equalDate(progressiveDate, rank.date) && FilterHelper.beforeOrSameDate(progressiveDate, dateRange.getTo())) {
									data.push(ChartHelper.createPoint(progressiveDate.getTime(), JavaScriptObjectHelper.getNativeNull()));
									CalendarUtil.addDaysToDate(progressiveDate, 1);
								}
							}
							CalendarUtil.addDaysToDate(progressiveDate, 1);
							if (rank.downloads != null && showModelPredictions) {
								Double yPoint = getYPointValue(rank);
								if (isPointIsolated(YDataType.DownloadsYAxisDataType, ranks, rank)) {
									data.push(ChartHelper.createMarkerPoint(rank.date.getTime(), yPoint.doubleValue()));
								} else {
									data.push(ChartHelper.createPoint(rank.date.getTime(), yPoint.doubleValue()));
								}
							} else {
								data.push(ChartHelper.createPoint(rank.date.getTime(), JavaScriptObjectHelper.getNativeNull()));
							}
						}
						addSeries(data, seriesType, id, color);
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
		drawData(ranks, id, seriesType, "");
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
				if (!withinChartRange(rank)) {
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
	private boolean withinChartRange(Rank rank) {
		return FilterHelper.afterOrSameDate(rank.date, dateRange.getFrom()) && FilterHelper.beforeOrSameDate(rank.date, dateRange.getTo());
	}

	private boolean isPointIsolated(YDataType yDataType, List<Rank> ranks, Rank rank) {
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
				rearrangeXAxisDatetimeLabels();
			}
			reflow();
		}

	}

	private int getRankPosition(Rank rank) {
		return rankingType == RankType.PositionRankingType ? rank.position.intValue() : rank.grossingPosition.intValue();
	}

}
