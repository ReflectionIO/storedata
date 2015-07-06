//
//  PopupToast.java
//  storedata
//
//  Created by Stefano Capuzzi (capuzzistefano) on 3 Jul 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.component;

import io.reflection.app.client.res.Styles;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.ParagraphElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Stefano Capuzzi (capuzzistefano)
 *
 */
public class PopupToast extends Composite {

	private static PopupToastUiBinder uiBinder = GWT.create(PopupToastUiBinder.class);

	interface PopupToastUiBinder extends UiBinder<Widget, PopupToast> {}

	@UiField DivElement childPanel;
	@UiField HeadingElement titleElem;
	@UiField ParagraphElement descriptionElem;
	@UiField AnchorElement readMoreLink;
	@UiField ImageElement image;

	private boolean isAlert;
	private final int HIDE_TIME = 5000;

	public PopupToast() {
		this(false);
	}

	public PopupToast(boolean isAlert) {
		initWidget(uiBinder.createAndBindUi(this));

		if (this.isAlert = isAlert) {
			childPanel.addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().popupToastAlert());
			image.setSrc("images/icon-warning.png");
		} else {
			image.setSrc("images/icon-bulb.png");
		}
	}

	public void show(String title, String description, String href) {
		titleElem.setInnerText(title);
		descriptionElem.setInnerText(description);
		readMoreLink.setHref(href);

		Document.get().getBody().appendChild(getElement());

		int popupHeight = childPanel.getClientHeight();
		getElement().setAttribute("style", "z-index: 10000; top: calc(100% - " + popupHeight + "px)");

		if (!isAlert) {
			Timer timerClose = new Timer() {
				@Override
				public void run() {
					hide();
				}
			};
			timerClose.schedule(HIDE_TIME);
		}
	}

	public void hide() {
		childPanel.addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().isRead());
		Timer timerRemoveClasses = new Timer() {

			@Override
			public void run() {
				getElement().removeAttribute("style");
				childPanel.removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().isRead());
				Timer timerHide = new Timer() {

					@Override
					public void run() {
						getElement().removeFromParent();
					}
				};
				timerHide.schedule(500);
			}
		};
		timerRemoveClasses.schedule(1000);
	}
}
