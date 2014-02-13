//
//  RankChart.java
//  storedata
//
//  Created by William Shakour (billy1380) on 6 Feb 2013.
//  Copyright © 2014 Reflection.io ltd. All rights reserved.
//
package io.reflection.app.client.part;

import io.reflection.app.client.controller.FilterController;
import io.reflection.app.datatypes.shared.Item;
import io.reflection.app.datatypes.shared.Rank;

import java.util.List;

import com.googlecode.gchart.client.GChart;

public class RankChart extends GChart {

	public enum Mode {
		PositionMode,
		GrossingPositionMode;

		/**
		 * @param value
		 * @return
		 */
		public static Mode fromString(String value) {
			Mode mode = null;

			if ("grossing".equals(value)) {
				mode = GrossingPositionMode;
			} else if ("free".equals(value) || "paid".equals(value)) {
				mode = PositionMode;
			}

			return mode;
		}
	}

	private final int WIDTH = 780;
	private final int HEIGHT = 350;

	private Curve curve;

	public RankChart() {
		setChartSize(WIDTH, HEIGHT);

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

		curve.getSymbol().setBorderColor("#6D69C5");
		curve.getSymbol().setBorderStyle("solid");
		curve.getSymbol().setFillThickness(2);

		curve.getSymbol().setBorderWidth(2);
		curve.getSymbol().setWidth(15);
		curve.getSymbol().setHeight(15);

		curve.getSymbol().setHoverSelectionWidth(1);
		curve.getSymbol().setHoverSelectionBorderColor("silver");
		// use a vertical line for the selection cursor
		curve.getSymbol().setHoverSelectionSymbolType(SymbolType.XGRIDLINE);
		// with annotation on top of this line (above chart)
		// curve.getSymbol().setHoverAnnotationSymbolType(SymbolType.BOX_EAST);
		curve.getSymbol().setHoverLocation(AnnotationLocation.EAST);

		curve.getSymbol().setHoverYShift(10);
		curve.getSymbol().setHoverXShift(5);

		curve.getSymbol().setHoverWidget(new RankHover());

		// tall brush so it touches independent of mouse y position
		curve.getSymbol().setBrushSize(25, 200);
		// so only point-to-mouse x-distance matters for hit testing
		curve.getSymbol().setDistanceMetric(1, 0);

	}

	public void setData(Item item, List<Rank> ranks, Mode mode) {

		int minY = 400, maxY = 0;
		int position;
		for (Rank rank : ranks) {
			position = mode == Mode.PositionMode ? rank.position.intValue() : rank.grossingPosition.intValue();

			if (position < minY) {
				minY = position;
			}

			if (position > maxY) {
				maxY = position;
			}

			curve.addPoint(rank.date.getTime(), position);
		}

		if (minY == maxY) {
			minY -= 4;
			maxY += 3;
		}

		if (minY < 1) {
			maxY += (4 - minY);
			minY = 1;
		}

		getYAxis().setAxisMin(maxY);
		getYAxis().setAxisMax(minY);

		// int diffY = (maxY - minY) + 1;

		getYAxis().setTickCount(8);

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
				getYAxis().setAxisMin(400);
				getYAxis().setTickCount(8);
			}

			update();
		}
	}
}
