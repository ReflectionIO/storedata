//
//  BackToTop.java
//  storedata
//
//  Created by Stefano Capuzzi (stefanocapuzzi) on 23 Jul 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.part;

import io.reflection.app.client.helper.DOMHelper;
import io.reflection.app.client.res.Styles;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ScrollEvent;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Stefano Capuzzi (stefanocapuzzi)
 * 
 */
public class BackToTop extends Composite {

	private static BackToTopUiBinder uiBinder = GWT.create(BackToTopUiBinder.class);

	interface BackToTopUiBinder extends UiBinder<Widget, BackToTop> {}

	@UiField Anchor link;

	public BackToTop() {
		initWidget(uiBinder.createAndBindUi(this)); // TODO isShowing should be isDisplaying

		Window.addWindowScrollHandler(new Window.ScrollHandler() {
			@Override
			public void onWindowScroll(ScrollEvent event) {
				if (event.getScrollTop() > (Document.get().getBody().getOffsetHeight() / 3)) {
					link.getElement().addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().isShowing());
				} else {
					link.getElement().removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().isShowing());
				}
			}
		});
	}

	@UiHandler("link")
	void onBackToTopClicked(ClickEvent event) {
		DOMHelper.nativeScrollTop(0, 300, "swing");
	}

}
