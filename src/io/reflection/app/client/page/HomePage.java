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
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ScrollEvent;
import com.google.gwt.user.client.Window.ScrollHandler;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author billy1380
 * 
 */
public class HomePage extends Page {

	private static HomePageUiBinder uiBinder = GWT.create(HomePageUiBinder.class);

	interface HomePageUiBinder extends UiBinder<Widget, HomePage> {}

	@UiField DivElement firstPage;
	private HandlerRegistration scrollHandlerRegistration;
	private HandlerRegistration resizeHandlerRegistration;

	public HomePage() {
		initWidget(uiBinder.createAndBindUi(this));
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

		scrollHandlerRegistration = Window.addWindowScrollHandler(new ScrollHandler() {

			@Override
			public void onWindowScroll(ScrollEvent event) {
				if (event.getScrollTop() > Window.getClientHeight()) {
					NavigationController.get().getHeader().getElement().getStyle().setTop(0, Unit.PX);
					NavigationController.get().getFooter().getElement().getStyle().clearHeight();
				} else {
					NavigationController.get().getHeader().getElement().getStyle().setTop(-60, Unit.PX);
					NavigationController.get().getFooter().getElement().getStyle().setHeight(0, Unit.PX);
				}
			}
		});

		resizeHandlerRegistration = Window.addResizeHandler(new ResizeHandler() {

			Timer resizeTimer = new Timer() {
				@Override
				public void run() {
					firstPage.getStyle().setHeight(Window.getClientHeight(), Unit.PX);
				}
			};

			@Override
			public void onResize(ResizeEvent event) {
				resizeTimer.cancel();
				resizeTimer.schedule(250);
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.page.Page#onDetach()
	 */
	@Override
	protected void onDetach() {
		super.onDetach();

		NavigationController.get().getHeader().getElement().getStyle().setTop(0, Unit.PX);
		NavigationController.get().getPageHolderPanel().getElement().getStyle().setPaddingTop(60, Unit.PX);
		NavigationController.get().getFooter().getElement().getStyle().clearHeight();

		scrollHandlerRegistration.removeHandler();
		resizeHandlerRegistration.removeHandler();
	}

}