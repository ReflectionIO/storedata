//
//  LoadingBar.java
//  storedata
//
//  Created by Stefano Capuzzi (capuzzistefano) on 9 Jun 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.component;

import io.reflection.app.client.helper.DOMHelper;
import io.reflection.app.client.res.Styles;
import io.reflection.app.client.res.Styles.ReflectionMainStyles;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
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

	private Element container; // Loading bar wrapper
	private boolean isProgressive;
	private boolean isLoading; // Check if the loader is currently active

	private final ReflectionMainStyles style = Styles.STYLES_INSTANCE.reflectionMainStyle();

	public LoadingBar(boolean isProgressive) {
		this(Document.get().getBody(), isProgressive);
	}

	public LoadingBar(Element container, boolean isProgressive) {
		initWidget(uiBinder.createAndBindUi(this));

		this.container = container;
		this.isProgressive = isProgressive;

		if (isProgressive) {
			bar.getElement().addClassName(style.pageLoadingDeterminate());
		} else {
			progressBar.removeFromParent();
		}

		// Check if it's a full-screen loader or a component loader
		if (!container.isOrHasChild(Document.get().getBody())) {
			bar.getElement().addClassName(style.componentLoading());
		}

		// Close error status bar when clicking anywhere
		Event.sinkEvents(Document.get().getBody(), Event.ONCLICK);
		Event.setEventListener(Document.get().getBody(), new EventListener() {

			@Override
			public void onBrowserEvent(Event event) {
				if (bar.getElement().hasClassName(style.isCompleteError()) && Event.ONCLICK == event.getTypeInt()) {
					close();
				}
			}
		});
		Event.sinkEvents(DOMHelper.getHtmlElement(), Event.ONCLICK);
		Event.setEventListener(DOMHelper.getHtmlElement(), new EventListener() {

			@Override
			public void onBrowserEvent(Event event) {
				if (bar.getElement().hasClassName(style.isCompleteError()) && Event.ONCLICK == event.getTypeInt()) {
					close();
				}
			}
		});
	}

	public void setText(String text) {
		textElem.setInnerText(text);
	}

	public void show() {
		this.show("Loading...");
	}

	public void show(String text) {
		if (!isLoading) {
			reset();
			isLoading = true;
			setText(text);
			container.appendChild(bar.getElement());
			bar.getElement().addClassName(style.isOpening());
			Timer timer = new Timer() {
				@Override
				public void run() {
					bar.getElement().removeClassName(style.isOpening());
				}
			};
			timer.schedule(100);
		}
	}

	public void hide(boolean isSuccess) {
		this.hide((isSuccess ? "Done!" : "Oh dear! Something went wrong. Please try again."), isSuccess);
	}

	public void hide(final String closingMessage, final boolean isSuccess) {
		if (container.isOrHasChild(this.getElement())) {
			if (isProgressive) {
				setProgressiveStatus(100.0);
				Timer timerClose = new Timer() {
					@Override
					public void run() {
						close(closingMessage, isSuccess);
					}
				};
				timerClose.schedule(400);
			} else {
				close(closingMessage, isSuccess);
			}
		}
	}

	private void close(String closingMessage, boolean isSuccess) {
		textElem.setInnerText(closingMessage);
		bar.getElement().addClassName((isSuccess ? style.isComplete() : style.isCompleteError()));
		isLoading = false;
		if (isSuccess) {
			Timer timerDone = new Timer() {
				@Override
				public void run() {
					close();
				}
			};
			timerDone.schedule(1000);
		}
		// Added HTML and body click handlers to close it when in error status
	}

	private void close() {
		bar.getElement().addClassName(style.isClosing());
		Timer timerClose = new Timer() {
			@Override
			public void run() {
				reset();
			}
		};
		timerClose.schedule(1000);
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
		bar.getElement().removeClassName(style.isComplete());
		bar.getElement().removeClassName(style.isCompleteError());
		bar.getElement().removeClassName(style.isClosing());
		bar.getElement().removeFromParent();
		isLoading = false;
	}

}
