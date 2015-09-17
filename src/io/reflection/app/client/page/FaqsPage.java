//
//  FaqsPage.java
//  storedata
//
//  Created by Stefano Capuzzi (capuzzistefano) on 14 Sep 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page;

import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.helper.AnimationHelper;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Stefano Capuzzi (capuzzistefano)
 *
 */
public class FaqsPage extends Page {

	private static FaqsPageUiBinder uiBinder = GWT.create(FaqsPageUiBinder.class);

	interface FaqsPageUiBinder extends UiBinder<Widget, FaqsPage> {}

	@UiField Element faqsTop;

	@UiField Anchor linkFaq1;
	@UiField Anchor linkFaq2;
	@UiField Anchor linkFaq3;
	@UiField Anchor linkFaq4;
	@UiField Anchor linkFaq5;
	@UiField Anchor linkFaq6;
	@UiField Anchor linkFaq7;
	@UiField Anchor linkFaq8;
	@UiField Anchor linkBackToTop1;
	@UiField Anchor linkBackToTop2;
	@UiField Anchor linkBackToTop3;
	@UiField Anchor linkBackToTop4;
	@UiField Anchor linkBackToTop5;
	@UiField Anchor linkBackToTop6;
	@UiField Anchor linkBackToTop7;
	@UiField Anchor linkBackToTop8;

	public FaqsPage() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiHandler({ "linkFaq1", "linkFaq2", "linkFaq3", "linkFaq4", "linkFaq5", "linkFaq6", "linkFaq7", "linkFaq8" })
	void onLinkFaqClicked(ClickEvent event) {
		event.preventDefault();

	}

	@UiHandler({ "linkBackToTop1", "linkBackToTop2", "linkBackToTop3", "linkBackToTop4", "linkBackToTop5", "linkBackToTop6", "linkBackToTop7", "linkBackToTop8" })
	void onBackToTopClicked(ClickEvent event) {
		event.preventDefault();
		int pageTopBarHeight = NavigationController.get().getHeader().getElement().getClientHeight();
		int scrollTopOfTheAnchor = faqsTop.getOffsetTop();
		AnimationHelper.nativeScrollTop(scrollTopOfTheAnchor - pageTopBarHeight - 20, 300, "swing");

	}

}
