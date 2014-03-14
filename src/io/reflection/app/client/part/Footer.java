//
//  Footer.java
//  storedata
//
//  Created by William Shakour (billy1380) on 3 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.part;

import io.reflection.app.client.res.Styles;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author billy1380
 * 
 */
public class Footer extends Composite {

	private static FooterUiBinder uiBinder = GWT.create(FooterUiBinder.class);

	interface FooterUiBinder extends UiBinder<Widget, Footer> {}

	@UiField HTMLPanel mFooter;

	@UiField SpanElement mYear;

	@UiField Image mArrow;

	@SuppressWarnings("deprecation")
	public Footer() {
		initWidget(uiBinder.createAndBindUi(this));

		Styles.INSTANCE.reflection().ensureInjected();

		mFooter.getElement().setAttribute("style", "bottom: -125px");		

		mYear.setInnerHTML(Integer.toString(1900 + (new Date()).getYear()));
	}

	@UiHandler("mArrow")
	void onClickArrow(ClickEvent event) {
		if (mFooter.getElement().getStyle().getBottom().equals("-125px")) {
			mArrow.setStylePrimaryName(Styles.INSTANCE.reflection().footerDownArrow());
			mFooter.getElement().setAttribute("style", "bottom: 0px;");

		} else {
			mArrow.setStylePrimaryName(Styles.INSTANCE.reflection().footerUpArrow());
			mFooter.getElement().setAttribute("style", "bottom: -125px;");
		}

	}

}
