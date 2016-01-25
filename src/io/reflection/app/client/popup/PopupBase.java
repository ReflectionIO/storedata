//
//  PopupBase.java
//  storedata
//
//  Created by Stefano Capuzzi (capuzzistefano) on 26 Sep 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.popup;

import io.reflection.app.client.helper.AnimationHelper;
import io.reflection.app.client.helper.DOMHelper;
import io.reflection.app.client.res.Styles;

import java.util.Iterator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Stefano Capuzzi (capuzzistefano)
 *
 */
public class PopupBase extends Composite implements HasWidgets, HasCloseHandlers<PopupBase> {

	private static PopupUiBinder uiBinder = GWT.create(PopupUiBinder.class);

	interface PopupUiBinder extends UiBinder<Widget, PopupBase> {}

	@UiField HTMLPanel pagePopup;
	@UiField HTMLPanel popupContent;
	@UiField Anchor closeLink;

	public PopupBase() {
		initWidget(uiBinder.createAndBindUi(this));

		pagePopup.sinkEvents(Event.ONCLICK);
		pagePopup.addHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (Element.as(event.getNativeEvent().getEventTarget()).getClassName().equals(Styles.STYLES_INSTANCE.reflectionMainStyle().pagePopup())) {
					closePopup();
				}
			}
		}, ClickEvent.getType());
	}

	void show() {
		AnimationHelper.nativeFadeIn(getElement(), 200);
		DOMHelper.getHtmlElement().addClassName("no-scroll");
		Timer t = new Timer() {

			@Override
			public void run() {
				popupContent.getElement().addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().isShowing());
			}
		};
		t.schedule(100);

	}

	void closePopup() {
		AnimationHelper.nativeFadeOut(getElement(), 100);
		DOMHelper.getHtmlElement().removeClassName("no-scroll");
		popupContent.getElement().removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().isShowing());
		Timer t = new Timer() {

			@Override
			public void run() {
				fireCloseHandler();
			}
		};
		t.schedule(150);
	}

	private void fireCloseHandler() {
		CloseEvent.fire(this, this, false);
	}

	@Override
	public void setStyleName(String style) {
		getElement().addClassName(style);
	}

	@UiHandler("closeLink")
	void onCloseClicked(ClickEvent event) {
		event.preventDefault();
		closePopup();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.HasWidgets#add(com.google.gwt.user.client.ui.Widget)
	 */
	@Override
	public void add(Widget w) {
		popupContent.add(w);
		popupContent.add(closeLink);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.HasWidgets#clear()
	 */
	@Override
	public void clear() {}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.HasWidgets#iterator()
	 */
	@Override
	public Iterator<Widget> iterator() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.HasWidgets#remove(com.google.gwt.user.client.ui.Widget)
	 */
	@Override
	public boolean remove(Widget w) {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.event.logical.shared.HasCloseHandlers#addCloseHandler(com.google.gwt.event.logical.shared.CloseHandler)
	 */
	@Override
	public HandlerRegistration addCloseHandler(CloseHandler<PopupBase> handler) {
		return addHandler(handler, CloseEvent.getType());
	}

}
