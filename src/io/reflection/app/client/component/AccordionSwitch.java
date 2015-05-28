//
//  AccordionSwitch.java
//  storedata
//
//  Created by Stefano Capuzzi (capuzzistefano) on 18 Apr 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.component;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.UListElement;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Stefano Capuzzi (capuzzistefano)
 *
 */
public class AccordionSwitch extends Composite {

	private static AccordionSwitchUiBinder uiBinder = GWT.create(AccordionSwitchUiBinder.class);

	interface AccordionSwitchUiBinder extends UiBinder<Widget, AccordionSwitch> {}

	@UiField HTMLPanel container;
	@UiField HeadingElement title;
	@UiField SpanElement subtitle;
	@UiField DivElement accordionContent;
	@UiField UListElement contentList;

	public AccordionSwitch() {
		this(false);
	}

	public AccordionSwitch(boolean isEmpty) {
		initWidget(uiBinder.createAndBindUi(this));

		setEmpty(isEmpty);

		Event.sinkEvents(title, Event.ONCLICK);
		Event.setEventListener(title, new EventListener() {

			@Override
			public void onBrowserEvent(Event event) {
				if (Event.ONCLICK == event.getTypeInt()) {
					if (!title.hasClassName("no-accordion-content")) {
						if (container.getElement().hasClassName("is-closed")) {
							container.getElement().removeClassName("is-closed");
							accordionContent.getStyle().setMarginTop(0, Unit.PX);
						} else {
							container.getElement().addClassName("is-closed");
							accordionContent.getStyle().setMarginTop(-Double.valueOf(accordionContent.getClientHeight()), Unit.PX);
						}
					}
				}
			}
		});

		Window.addResizeHandler(new ResizeHandler() {

			@Override
			public void onResize(ResizeEvent event) {
				if (!title.hasClassName("no-accordion-content")) {
					container.getElement().addClassName("is-closed");
					accordionContent.getStyle().setMarginTop(-Double.valueOf(accordionContent.getClientHeight()), Unit.PX);
				}
			}
		});

	}

	public void setEmpty(boolean isEmpty) {
		if (isEmpty) {
			title.addClassName("no-accordion-content");
			accordionContent.removeFromParent();
		}
	}

	public void setFeatureComplete(boolean complete) {
		container.setStyleName("feature-complete", complete);
	}

	public void setTitleText(String text) {
		subtitle.removeFromParent();
		title.setInnerText(text);
		title.appendChild(subtitle);
	}

	public void setSubtitleText(String text) {
		subtitle.setInnerText(text);
	}

	public void setSubtitleLinkHref(String href) {
		AnchorElement emailLink = Document.get().createAnchorElement();
		emailLink.setInnerText(subtitle.getInnerText());
		emailLink.setHref(href);
		subtitle.setInnerText("");
		subtitle.appendChild(emailLink);
	}

	public void setHighlighted(boolean highlight) {
		container.setStyleName("accordion-item-highlighted", highlight);
	}

	public void addItemIncomplete(String titleText, String subtitleText) {
		LIElement liElem = Document.get().createLIElement();
		liElem.setInnerText(titleText);
		SpanElement subtitle = Document.get().createSpanElement();
		subtitle.setInnerText(subtitleText);
		liElem.appendChild(subtitle);
		contentList.appendChild(liElem);
	}

	public void addItemComplete(String titleText) {
		LIElement liElem = Document.get().createLIElement();
		liElem.addClassName("list-item-complete");
		liElem.setInnerText(titleText);
		contentList.appendChild(liElem);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.Composite#onAttach()
	 */
	@Override
	protected void onAttach() {
		super.onAttach();

		if (!title.hasClassName("no-accordion-content")) {
			container.getElement().addClassName("is-closed");
		}
		Timer timer = new Timer() {
			@Override
			public void run() {
				accordionContent.getStyle().setMarginTop(-Double.valueOf(accordionContent.getClientHeight()), Unit.PX);
			}
		};
		timer.schedule(100);

	}
}
