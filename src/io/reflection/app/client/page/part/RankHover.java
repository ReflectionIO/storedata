//
//  RankHover.java
//  storedata
//
//  Created by William Shakour (billy1380) on 6 Feb 2014.
//  Copyright © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page.part;

import static io.reflection.app.client.helper.FormattingHelper.DATE_FORMAT_EEE_DD_MMM_YYYY;
import static io.reflection.app.client.helper.FormattingHelper.WHOLE_NUMBER_FORMAT;
import io.reflection.app.client.helper.FormattingHelper;
import io.reflection.app.client.page.part.ItemChart.XAxisDataType;
import io.reflection.app.client.page.part.ItemChart.YAxisDataType;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.gchart.client.GChart.Curve.Point;
import com.googlecode.gchart.client.HoverUpdateable;

/**
 * @author billy1380
 * 
 */
public class RankHover extends Composite implements HoverUpdateable {

	private static RankHoverUiBinder uiBinder = GWT.create(RankHoverUiBinder.class);

	interface RankHoverUiBinder extends UiBinder<Widget, RankHover> {}

	@UiField HTMLPanel title;
	@UiField HTMLPanel detail;

	private ItemChart.YAxisDataType yDataType;
	private ItemChart.XAxisDataType xDataType;
	private String currency;

	public RankHover() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.googlecode.gchart.client.HoverUpdateable#hoverCleanup(com.googlecode.gchart.client.GChart.Curve.Point)
	 */
	@Override
	public void hoverCleanup(Point hoveredAwayFrom) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.googlecode.gchart.client.HoverUpdateable#hoverUpdate(com.googlecode.gchart.client.GChart.Curve.Point)
	 */
	@Override
	public void hoverUpdate(Point hoveredOver) {
		switch (xDataType) {
		case DateXAxisDataType:
			title.getElement().setInnerHTML(DATE_FORMAT_EEE_DD_MMM_YYYY.format(new Date((long) hoveredOver.getX())));
			break;
		case RankingXAxisDataType:
			if (hoveredOver.getX() == 0) {
				title.getElement().setInnerHTML("Not ranked");
			} else {
				title.getElement().setInnerHTML("Rank " + Double.toString(hoveredOver.getX()));
			}

			break;
		}

		switch (yDataType) {
		case RevenueYAxisDataType:
			detail.getElement().setInnerHTML(FormattingHelper.asWholeMoneyString(currency, (float) hoveredOver.getY()));
			break;
		case DownloadsYAxisDataType:
			detail.getElement().setInnerHTML(WHOLE_NUMBER_FORMAT.format((double) hoveredOver.getY()));
			break;
		case RankingYAxisDataType:
			detail.getElement().setInnerHTML(Integer.toString((int) hoveredOver.getY()));
			break;
		}
	}

	public void setYAxisDataType(YAxisDataType value) {
		yDataType = value;
	}

	public void setXAxisDataType(XAxisDataType value) {
		xDataType = value;
	}

	public void setCurrency(String value) {
		currency = value;
	}

	public void setCssColor(String value) {
		Style s = getElement().getStyle();

		s.setBorderColor(value);
	}
}
