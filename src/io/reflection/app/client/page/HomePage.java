//
//  HomePage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 13 Jan 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page;

import io.reflection.app.client.controller.NavigationController;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.OListElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ScrollEvent;
import com.google.gwt.user.client.Window.ScrollHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author billy1380
 * 
 */
public class HomePage extends Page {

	private static HomePageUiBinder uiBinder = GWT.create(HomePageUiBinder.class);

	interface HomePageUiBinder extends UiBinder<Widget, HomePage> {}

	@UiField DivElement firstPage;
	@UiField DivElement features;
	@UiField TextBox name;
	@UiField TextBox email;
	@UiField TextArea message;
	@UiField Anchor gotoFeatures;
	@UiField OListElement carouselIndicators;
	@UiField DivElement carouselContainer;
	@UiField Anchor carouselRight;
	@UiField Anchor carouselLeft;
	private Timer scrollTimer;

	public HomePage() {
		initWidget(uiBinder.createAndBindUi(this));

		name.getElement().setAttribute("placeholder", "Name");
		email.getElement().setAttribute("placeholder", "Email Address");
		message.getElement().setAttribute("placeholder", "Message");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.page.Page#onAttach()
	 */
	@Override
	protected void onAttach() {
		super.onAttach();

		NavigationController.get().getHeader().getElement().getStyle().setTop(-60, Unit.PX);
		NavigationController.get().getFooter().getElement().getStyle().setHeight(0, Unit.PX);
		NavigationController.get().getPageHolderPanel().getElement().getStyle().setPaddingTop(0, Unit.PX);

		firstPage.getStyle().setHeight(Window.getClientHeight(), Unit.PX);

		register(Window.addWindowScrollHandler(new ScrollHandler() {

			@Override
			public void onWindowScroll(ScrollEvent event) {
				if (event.getScrollTop() > Window.getClientHeight()) {
					NavigationController.get().getHeader().getElement().getStyle().setTop(0, Unit.PX);
					NavigationController.get().getFooter().getElement().getStyle().clearHeight();
				} else {
					NavigationController.get().getHeader().getElement().getStyle().setTop(-60, Unit.PX);
					NavigationController.get().getFooter().getElement().getStyle().setHeight(0, Unit.PX);
				}

				if (event.getScrollTop() < 0) {
					PageType.HomePageType.show();
				}
			}
		}));

		register(Window.addResizeHandler(new ResizeHandler() {

			Timer resizeTimer = new Timer() {
				@Override
				public void run() {
					firstPage.getStyle().setHeight(Window.getClientHeight(), Unit.PX);
				}
			};

			@Override
			public void onResize(ResizeEvent event) {
				resizeTimer.cancel();
				resizeTimer.schedule((int) (1000.0 / 30.0));
			}
		}));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.page.Page#onDetach()
	 */
	@Override
	protected void onDetach() {
		if (scrollTimer != null) {
			scrollTimer.cancel();
		}

		super.onDetach();

		NavigationController.get().getHeader().getElement().getStyle().setTop(0, Unit.PX);
		NavigationController.get().getPageHolderPanel().getElement().getStyle().setPaddingTop(60, Unit.PX);
		NavigationController.get().getFooter().getElement().getStyle().clearHeight();
	}

	@UiHandler({ "gotoFeatures", "carouselLeft", "carouselRight" })
	void onClickHandler(ClickEvent e) {
		if (e.getSource() == gotoFeatures) {
			if (scrollTimer == null) {
				scrollTimer = new Timer() {

					int distance = 0;

					@Override
					public void run() {
						int top = Window.getScrollTop();
						int featureTop = features.getAbsoluteTop() - 60;

						if (top < featureTop) {
							distance = (int) (((double) featureTop - (double) top) / 3.0);

							if (distance < 4) {
								distance = 4;
							}

							Window.scrollTo(0, top + distance);
						} else {
							Window.scrollTo(0, featureTop);
							this.cancel();
						}
					}
				};
			} else {
				scrollTimer.cancel();
			}

			scrollTimer.scheduleRepeating(50);
		} else if (e.getSource() == carouselLeft) {

		} else if (e.getSource() == carouselRight) {

		}
	}

}