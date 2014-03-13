//
//  Footer.java
//  storedata
//
//  Created by William Shakour (billy1380) on 3 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.part;

import io.reflection.app.client.res.Images;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
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

	@UiField Image mDownArrow;
	@UiField Image mUpArrow;

	@UiField Image mFacebook;
	@UiField Image mLinkedin;
	@UiField Image mTwitter;

	@SuppressWarnings("deprecation")
	public Footer() {
		initWidget(uiBinder.createAndBindUi(this));

		mYear.setInnerHTML(Integer.toString(1900 + (new Date()).getYear()));
	}

	@UiHandler("mUpArrow")
	void onMouseOverUpArrow(MouseOverEvent event) {
		mUpArrow.setResource(Images.INSTANCE.footerUpArrowHover());
	}

	@UiHandler("mUpArrow")
	void onMouseOutUpArrow(MouseOutEvent event) {
		mUpArrow.setResource(Images.INSTANCE.footerUpArrow());
	}

	@UiHandler("mUpArrow")
	void onClickUpArrow(ClickEvent event) {
		mUpArrow.setVisible(false);
		mDownArrow.setVisible(true);
		mFooter.getElement().setAttribute("style", "bottom: 0px;");
	}

	@UiHandler("mDownArrow")
	void onMouseOverDownArrow(MouseOverEvent event) {
		mDownArrow.setResource(Images.INSTANCE.footerDownArrowHover());
	}

	@UiHandler("mDownArrow")
	void onMouseOutDownArrow(MouseOutEvent event) {
		mDownArrow.setResource(Images.INSTANCE.footerDownArrow());
	}

	@UiHandler("mDownArrow")
	void onClickDownArrow(ClickEvent event) {
		mDownArrow.setVisible(false);
		mUpArrow.setVisible(true);
		mFooter.getElement().setAttribute("style", "bottom: -125px;");
	}

	@UiHandler("mFacebook")
	void onMouseOverFacebook(MouseOverEvent event) {
		mFacebook.setResource(Images.INSTANCE.facebookHover());
	}

	@UiHandler("mFacebook")
	void onMouseOutFacebook(MouseOutEvent event) {
		mFacebook.setResource(Images.INSTANCE.facebook());
	}

	@UiHandler("mLinkedin")
	void onMouseOverLinkedin(MouseOverEvent event) {
		mLinkedin.setResource(Images.INSTANCE.linkedinHover());
	}

	@UiHandler("mLinkedin")
	void onMouseOutLinkedin(MouseOutEvent event) {
		mLinkedin.setResource(Images.INSTANCE.linkedin());
	}

	@UiHandler("mTwitter")
	void onMouseOverTwitter(MouseOverEvent event) {
		mTwitter.setResource(Images.INSTANCE.twitterHover());
	}

	@UiHandler("mTwitter")
	void onMouseOutTwitter(MouseOutEvent event) {
		mTwitter.setResource(Images.INSTANCE.twitter());
	}
}
