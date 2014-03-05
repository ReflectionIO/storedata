//
//  Footer.java
//  storedata
//
//  Created by William Shakour (billy1380) on 3 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.part;

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

	@UiField HTMLPanel mDownPanel;

	@UiField SpanElement mYear;

	@UiField Image mDownArrow;
	@UiField Image mUpArrow;
	
	@UiField Image mFacebook;
	@UiField Image mFacebookHover;
	
	@UiField Image mLinkedin;
	@UiField Image mLinkedinHover;
	
	@UiField Image mTwitter;
	@UiField Image mTwitterHover;

	@SuppressWarnings("deprecation")
	public Footer() {
		initWidget(uiBinder.createAndBindUi(this));

		mYear.setInnerHTML(Integer.toString(1900 + (new Date()).getYear()));
	}

	@UiHandler("mDownArrow")
	void onDownArrowClick(ClickEvent event) {
		mDownArrow.setVisible(false);
		mUpArrow.setVisible(true);
		mDownPanel.setVisible(false);
	}

	@UiHandler("mUpArrow")
	void onUpArrowClick(ClickEvent event) {
		mUpArrow.setVisible(false);
		mDownArrow.setVisible(true);
		mDownPanel.setVisible(true);
	}

	@UiHandler("mFacebook")
	void onMouseOverFacebook(MouseOverEvent event){
		mFacebook.setVisible(false);
		mFacebookHover.setVisible(true);
	}
	
	@UiHandler("mFacebookHover")
	void onMouseOutFacebook(MouseOutEvent event){
		mFacebookHover.setVisible(false);
		mFacebook.setVisible(true);
	}
	
	@UiHandler("mLinkedin")
	void onMouseOverLinkedin(MouseOverEvent event){
		mLinkedin.setVisible(false);
		mLinkedinHover.setVisible(true);
	}
	
	@UiHandler("mLinkedinHover")
	void onMouseOutLinkedin(MouseOutEvent event){
		mLinkedinHover.setVisible(false);
		mLinkedin.setVisible(true);
	}
	
	@UiHandler("mTwitter")
	void onMouseOverTwitter(MouseOverEvent event){
		mTwitter.setVisible(false);
		mTwitterHover.setVisible(true);
	}
	
	@UiHandler("mTwitterHover")
	void onMouseOutTwitter(MouseOutEvent event){
		mTwitterHover.setVisible(false);
		mTwitter.setVisible(true);
	}
}
