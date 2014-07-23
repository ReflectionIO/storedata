//
//  RankHover.java
//  storedata
//
//  Created by William Shakour (billy1380) on 6 Feb 2014.
//  Copyright © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page.part;

import io.reflection.app.client.page.part.ItemChart.YAxisDataType;
import io.reflection.app.shared.util.FormattingHelper;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.i18n.client.DateTimeFormat;
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

    @UiField HTMLPanel date;
    @UiField HTMLPanel detail;

    private ItemChart.YAxisDataType dataType;
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
        date.getElement().setInnerHTML(DateTimeFormat.getFormat(FormattingHelper.DATE_FORMAT_EEE_DD_MMM_YYYY).format(new Date((long) hoveredOver.getX())));
        switch (dataType) {
        case RevenueYAxisDataType:
            detail.getElement().setInnerHTML(currency + " " + Double.toString(hoveredOver.getY()));
            break;
        case DownloadsYAxisDataType:
            detail.getElement().setInnerHTML(Double.toString(hoveredOver.getY()));
            break;
        case RankingYAxisDataType:
            detail.getElement().setInnerHTML(Double.toString(hoveredOver.getY()));
            break;
        }
    }

    public void setYAxisDataType(YAxisDataType value) {
        dataType = value;
    }

    public void setCurrency(String value) {
        currency = value;
    }

    public void setCssColor(String value) {
        Style s = getElement().getStyle();

        s.setBorderColor(value);
    }
}
