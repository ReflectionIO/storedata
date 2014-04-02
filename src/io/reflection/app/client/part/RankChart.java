//
//  RankChart.java
//  storedata
//
//  Created by William Shakour (billy1380) on 6 Feb 2013.
//  Copyright © 2014 Reflection.io ltd. All rights reserved.
//
package io.reflection.app.client.part;

import static io.reflection.app.client.controller.FilterController.DOWNLOADS_CHART_TYPE;
import static io.reflection.app.client.controller.FilterController.FREE_LIST_TYPE;
import static io.reflection.app.client.controller.FilterController.GROSSING_LIST_TYPE;
import static io.reflection.app.client.controller.FilterController.PAID_LIST_TYPE;
import static io.reflection.app.client.controller.FilterController.RANKING_CHART_TYPE;
import static io.reflection.app.client.controller.FilterController.REVENUE_CHART_TYPE;
import io.reflection.app.client.controller.FilterController;
import io.reflection.app.client.res.charts.Images;
import io.reflection.app.datatypes.shared.Item;
import io.reflection.app.datatypes.shared.Rank;

import java.util.List;

import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.googlecode.gchart.client.GChart;

public class RankChart extends GChart {

	private HandlerRegistration resizeRegistration;
	private ResizeHandler resizeHandler = new ResizeHandler() {

		Timer resizeTimer = new Timer() {
			@Override
			public void run() {
				resize();
			}
		};

		@Override
		public void onResize(ResizeEvent event) {
			resizeTimer.cancel();
			resizeTimer.schedule(250);
		}
	};

	private void resize() {
		setChartSize((int) (((double) getParent().getElement().getClientWidth() * (10.0 / 12.0)) - 120.0), 350);
		update();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.Composite#onAttach()
	 */

	@Override
	protected void onAttach() {
		super.onAttach();

		resize();

		resizeRegistration = Window.addResizeHandler(resizeHandler);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.Composite#onDetach()
	 */
	@Override
	protected void onDetach() {
		if (resizeRegistration != null) {
			resizeRegistration.removeHandler();
		}

		super.onDetach();
	}

	public enum YAxisDataType {
		RevenueYAxisDataType,
		DownloadsYAxisDataType,
		RankingYAxisDataType;

		/**
		 * @param value
		 * @return
		 */
		public static YAxisDataType fromString(String value) {
			YAxisDataType dataType = null;

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

	public enum RankingType {
		PositionRankingType,
		GrossingPositionRankingType;

		/**
		 * @param value
		 * @return
		 */
		public static RankingType fromString(String value) {
			RankingType mode = null;

			switch (value) {
			case GROSSING_LIST_TYPE:
				mode = GrossingPositionRankingType;
				break;
			case FREE_LIST_TYPE:
			case PAID_LIST_TYPE:
				mode = PositionRankingType;
				break;
			}

			return mode;
		}
	}

	public enum Colour {
		PurpleColour("#6D69C5", Images.INSTANCE.purpleCirle().getSafeUri().asString()), ;

		private String Colour;
		private String imageUrl;

		private Colour(String Colour, String imageUrl) {
			this.Colour = Colour;
			this.imageUrl = imageUrl;
		}

		public String getImageUrl() {
			return imageUrl;
		}

		public String getColour() {
			return Colour;
		}
	}

	private Curve curve;

	public RankChart() {

		setBorderStyle("none");

		// configure x-axis
		getXAxis().setTickLength(0);

		getYAxis().setHasGridlines(true);
		getYAxis().setTickLength(0);

		getYAxis().setTicksPerGridline(1);
		setGridColor("silver");

		getYAxis().setAxisVisible(false);
		getXAxis().setAxisVisible(false);

		getXAxis().setTickLabelFontColor("gray");
		getYAxis().setTickLabelFontColor("gray");

		getXAxis().setTickLabelPadding(20);
		getYAxis().setTickLabelPadding(20);

		getXAxis().setTickLabelFormat("=(Date)d MMM");

		addCurve();

		curve = getCurve();

		curve.getSymbol().setSymbolType(SymbolType.LINE);

		curve.getSymbol().setBorderColor(Colour.PurpleColour.getColour());
		curve.getSymbol().setBorderStyle("solid");
		curve.getSymbol().setFillThickness(2);

		curve.getSymbol().setBorderWidth(0);
		curve.getSymbol().setWidth(12);
		curve.getSymbol().setHeight(12);

		curve.getSymbol().setImageURL(Colour.PurpleColour.getImageUrl());

		curve.getSymbol().setHoverSelectionWidth(1);
		curve.getSymbol().setHoverSelectionBorderColor("silver");
		// use a vertical line for the selection cursor
		curve.getSymbol().setHoverSelectionSymbolType(SymbolType.XGRIDLINE);
		// with annotation on top of this line (above chart)
		// curve.getSymbol().setHoverAnnotationSymbolType(SymbolType.BOX_EAST);
		curve.getSymbol().setHoverLocation(AnnotationLocation.EAST);

		curve.getSymbol().setHoverYShift(10);
		curve.getSymbol().setHoverXShift(5);

		RankHover hoverWidget = new RankHover();
		hoverWidget.setCssColor(Colour.PurpleColour.getColour());
		hoverWidget.setYAxisDataType(YAxisDataType.RankingYAxisDataType);

		curve.getSymbol().setHoverWidget(hoverWidget);

		// tall brush so it touches independent of mouse y position
		curve.getSymbol().setBrushSize(25, 200);
		// so only point-to-mouse x-distance matters for hit testing
		curve.getSymbol().setDistanceMetric(1, 0);

	}

	public void setData(Item item, List<Rank> ranks, RankingType mode, YAxisDataType dataType) {

		if (curve != null) {
			curve.clearPoints();
		}

		int minY = 10000, maxY = 0;
		int position;
		for (Rank rank : ranks) {
			position = mode == RankingType.PositionRankingType ? rank.position.intValue() : rank.grossingPosition.intValue();

			if (position < minY) {
				minY = position;
			}

			if (position > maxY) {
				maxY = position;
			}

			curve.addPoint(rank.date.getTime(), position);
		}

		int factor = (int) ((float) (maxY - minY) / 7.0f) + 1;

		maxY = minY + (7 * factor);

		getYAxis().setAxisMin(maxY);
		getYAxis().setAxisMax(minY);

		// int diffY = (maxY - minY) + 1;

		getYAxis().setTickCount(8);

		((RankHover) curve.getSymbol().getHoverWidget()).setYAxisDataType(dataType);

		setLoading(false);

	}

	public void setLoading(boolean loading) {

		if (curve != null) {
			curve.setVisible(!loading);

			if (loading) {
				curve.clearPoints();

				getXAxis().setAxisMax(FilterController.get().getEndDate().getTime());
				getXAxis().setAxisMin(FilterController.get().getStartDate().getTime());

				getYAxis().setAxisMax(1);
				getYAxis().setAxisMin(8);
				getYAxis().setTickCount(8);
			}

			update();
		}
	}

}
