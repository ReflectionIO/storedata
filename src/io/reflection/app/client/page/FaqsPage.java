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
import io.reflection.app.client.helper.DOMHelper;
import io.reflection.app.client.res.Styles;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ScrollEvent;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Stefano Capuzzi (capuzzistefano)
 *
 */
public class FaqsPage extends Page {

	private static FaqsPageUiBinder uiBinder = GWT.create(FaqsPageUiBinder.class);

	interface FaqsPageUiBinder extends UiBinder<Widget, FaqsPage> {}

	@UiField DivElement faqContainer;
	@UiField Element faqsTop;
	@UiField Anchor linkFaq1;
	@UiField Anchor linkFaq2;
	@UiField Anchor linkFaq3;
	@UiField Anchor linkFaq4;
	@UiField Anchor linkFaq5;
	@UiField Anchor linkFaq6;
	@UiField Anchor linkFaq7;
	@UiField Anchor linkFaq8;
	@UiField Anchor linkFaq9;
	@UiField Anchor linkFaq10;
	@UiField Anchor linkFaq11;
	@UiField Anchor linkFaq12;
	@UiField Anchor linkFaq13;
	@UiField Anchor linkFaq14;
	@UiField Element faqContent1;
	@UiField Element faqContent2;
	@UiField Element faqContent3;
	@UiField Element faqContent4;
	@UiField Element faqContent5;
	@UiField Element faqContent6;
	@UiField Element faqContent7;
	@UiField Element faqContent8;
	@UiField Element faqContent9;
	@UiField Element faqContent10;
	@UiField Element faqContent11;
	@UiField Element faqContent12;
	@UiField Element faqContent13;
	@UiField Element faqContent14;
	@UiField Anchor linkBackToTop1;
	@UiField Anchor linkBackToTop2;
	@UiField Anchor linkBackToTop3;
	@UiField Anchor linkBackToTop4;
	@UiField Anchor linkBackToTop5;
	@UiField Anchor linkBackToTop6;
	@UiField Anchor linkBackToTop7;
	@UiField Anchor linkBackToTop8;
	@UiField Anchor linkBackToTop9;
	@UiField Anchor linkBackToTop10;
	@UiField Anchor linkBackToTop11;
	@UiField Anchor linkBackToTop12;
	@UiField Anchor linkBackToTop13;
	@UiField Anchor linkBackToTop14;
	private final int pageTopBarHeight = NavigationController.get().getHeader().getElement().getClientHeight();
	private int faqContainerTopPosition;
	private Map<Anchor, Element> linkFaqToAnchorMap = new HashMap<Anchor, Element>();

	public FaqsPage() {
		initWidget(uiBinder.createAndBindUi(this));

		linkFaqToAnchorMap.put(linkFaq1, faqContent1);
		linkFaqToAnchorMap.put(linkFaq2, faqContent2);
		linkFaqToAnchorMap.put(linkFaq3, faqContent3);
		linkFaqToAnchorMap.put(linkFaq4, faqContent4);
		linkFaqToAnchorMap.put(linkFaq5, faqContent5);
		linkFaqToAnchorMap.put(linkFaq6, faqContent6);
		linkFaqToAnchorMap.put(linkFaq7, faqContent7);
		linkFaqToAnchorMap.put(linkFaq8, faqContent8);
		linkFaqToAnchorMap.put(linkFaq9, faqContent9);
		linkFaqToAnchorMap.put(linkFaq10, faqContent10);
		linkFaqToAnchorMap.put(linkFaq11, faqContent11);
		linkFaqToAnchorMap.put(linkFaq12, faqContent12);
		linkFaqToAnchorMap.put(linkFaq13, faqContent13);
		linkFaqToAnchorMap.put(linkFaq14, faqContent14);

		Window.addWindowScrollHandler(new Window.ScrollHandler() {

			@Override
			public void onWindowScroll(ScrollEvent event) {
				faqContainerTopPosition = faqsTop.getAbsoluteTop() - pageTopBarHeight;
				if (Window.getClientWidth() > 719 && Window.getClientHeight() > 799 && DOMHelper.getHtmlElement().hasClassName("no-touch")) {
					if (Window.getScrollTop() >= faqContainerTopPosition) {
						if (!faqContainer.hasClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().faqsListContainerFixed())) {
							faqContainer.addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().faqsListContainerFixed());
						}
					} else {
						if (faqContainer.hasClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().faqsListContainerFixed())) {
							faqContainer.removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().faqsListContainerFixed());
						}
					}
				}
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.page.Page#onAttach()
	 */
	@Override
	protected void onAttach() {
		super.onAttach();

		faqContainer.removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().faqsListContainerFixed());
	}

	@UiHandler({ "linkFaq1", "linkFaq2", "linkFaq3", "linkFaq4", "linkFaq5", "linkFaq6", "linkFaq7", "linkFaq8", "linkFaq9", "linkFaq10", "linkFaq11",
			"linkFaq12", "linkFaq13", "linkFaq14" })
	void onLinkFaqClicked(ClickEvent event) {
		event.preventDefault();
		for (Anchor link : linkFaqToAnchorMap.keySet()) {
			link.getElement().removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().isActive());
		}
		Anchor clickedLinkFaq = (Anchor) event.getSource();
		clickedLinkFaq.getElement().addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().isActive());
		int scrollTopOfTheAnchor = linkFaqToAnchorMap.get(clickedLinkFaq).getAbsoluteTop();
		AnimationHelper.nativeScrollTop(scrollTopOfTheAnchor - pageTopBarHeight - 20, 300, "swing");
	}

	@UiHandler({ "linkBackToTop1", "linkBackToTop2", "linkBackToTop3", "linkBackToTop4", "linkBackToTop5", "linkBackToTop6", "linkBackToTop7",
			"linkBackToTop8", "linkBackToTop9", "linkBackToTop10", "linkBackToTop11", "linkBackToTop12", "linkBackToTop13", "linkBackToTop14" })
	void onBackToTopClicked(ClickEvent event) {
		event.preventDefault();
		int scrollTopOfTheAnchor = faqsTop.getAbsoluteTop();
		AnimationHelper.nativeScrollTop(scrollTopOfTheAnchor - pageTopBarHeight - 20, 300, "swing");
	}
}
