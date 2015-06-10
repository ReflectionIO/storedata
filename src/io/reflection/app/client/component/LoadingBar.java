//
//  LoadingBar.java
//  storedata
//
//  Created by Stefano Capuzzi (capuzzistefano) on 9 Jun 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.component;

import io.reflection.app.client.res.Styles;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Stefano Capuzzi (capuzzistefano)
 *
 */
public class LoadingBar extends Composite {

	private static LoadingBarUiBinder uiBinder = GWT.create(LoadingBarUiBinder.class);

	interface LoadingBarUiBinder extends UiBinder<Widget, LoadingBar> {}

	@UiField HTMLPanel bar;
	@UiField SpanElement textElem;
	@UiField DivElement progressBar;

	private Element container;
	private boolean isProgressive;
	private boolean isLoading; // Check if the loader is currently active

	public LoadingBar(Element container) {
		this(container, false);
	}

	public LoadingBar(Element container, boolean isProgressive) {
		initWidget(uiBinder.createAndBindUi(this));

		this.container = container;
		this.isProgressive = isProgressive;

		if (isProgressive) {
			bar.getElement().addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().pageLoadingDeterminate());
		} else {
			progressBar.removeFromParent();
		}

		// Check if it's a full-screen loader or a component loader
		if (!container.isOrHasChild(Document.get().getBody())) {
			bar.getElement().addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().componenetLoading());
		}
	}

	public void setText(String text) {
		textElem.setInnerText(text);
	}

	public void show(String text) {
		if (!isLoading) {
			isLoading = true;
			setText(text);
			container.appendChild(bar.getElement());
			bar.getElement().addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().isOpening());
			Timer timer = new Timer() {
				@Override
				public void run() {
					bar.getElement().removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().isOpening());
				}
			};
			timer.schedule(100);
		}
	}

	public void hide() {
		if (isProgressive) {
			setProgressiveStatus(100.0);
			Timer timerClose = new Timer() {
				@Override
				public void run() {
					close();
				}
			};
			timerClose.schedule(400);
		} else {
			close();
		}
	}

	private void close() {
		textElem.setInnerText("Done!");
		bar.getElement().addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().isComplete());
		Timer timerDone = new Timer() {
			@Override
			public void run() {
				bar.getElement().addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().isClosing());
				Timer timerClose = new Timer() {
					@Override
					public void run() {
						reset();
						bar.getElement().removeFromParent();
						isLoading = false;
					}
				};
				timerClose.schedule(1000);
			}
		};
		timerDone.schedule(500);
	}

	public void setError() {
		// TODO
	}

	public void setProgressiveStatus(double percentage) {
		setProgressiveStatus(textElem.getInnerText(), percentage);
	}

	public void setProgressiveStatus(String statusText, double percentage) {
		setText(statusText);
		progressBar.getStyle().setWidth(percentage, Unit.PCT);
	}

	public void reset() {
		textElem.setInnerText("");
		if (isProgressive) {
			setProgressiveStatus(0.0);
		}
		bar.getElement().removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().isComplete());
		bar.getElement().removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().isClosing());

	}

}
