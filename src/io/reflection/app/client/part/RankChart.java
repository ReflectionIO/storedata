//
//  RankChart.java
//  storedata
//
//  Created by William Shakour (billy1380) on 6 Feb 2013.
//  Copyright © 2014 Reflection.io ltd. All rights reserved.
//
package io.reflection.app.client.part;

import io.reflection.app.datatypes.shared.Rank;

import java.util.List;

import com.googlecode.gchart.client.GChart;

public class RankChart extends GChart {

	private final int WIDTH = 780;
	private final int HEIGHT = 350;

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

		getCurve().getSymbol().setSymbolType(SymbolType.LINE);

		getCurve().getSymbol().setBorderColor("#9780E4");
		getCurve().getSymbol().setBorderStyle("solid");
		getCurve().getSymbol().setFillThickness(2);

		getCurve().getSymbol().setBorderWidth(2);
		getCurve().getSymbol().setWidth(15);
		getCurve().getSymbol().setHeight(15);

		getCurve().getSymbol().setHoverSelectionWidth(1);
		getCurve().getSymbol().setHoverSelectionBorderColor("silver");
		// use a vertical line for the selection cursor
		getCurve().getSymbol().setHoverSelectionSymbolType(SymbolType.XGRIDLINE);
		// with annotation on top of this line (above chart)
		// getCurve().getSymbol().setHoverAnnotationSymbolType(SymbolType.BOX_EAST);
		getCurve().getSymbol().setHoverLocation(AnnotationLocation.EAST);

		getCurve().getSymbol().setHoverYShift(10);
		getCurve().getSymbol().setHoverXShift(5);

		getCurve().getSymbol().setHoverWidget(new RankHover());

		// tall brush so it touches independent of mouse y position
		getCurve().getSymbol().setBrushSize(25, 200);
		// so only point-to-mouse x-distance matters for hit testing
		getCurve().getSymbol().setDistanceMetric(1, 0);

	}

	public void setData(List<Rank> ranks) {
		
		Curve c = getCurve();
		
		for (Rank rank : ranks) {
			c.addPoint(rank.date.getTime(), rank.position.intValue());	
		}
		
		update();
		
		setLoading(false);
	}

	public void setLoading(boolean loading) {

	}
}
