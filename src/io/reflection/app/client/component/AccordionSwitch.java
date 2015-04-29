//
//  AccordionSwitch.java
//  storedata
//
//  Created by Stefano Capuzzi (capuzzistefano) on 18 Apr 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.component;

import io.reflection.app.client.helper.DOMHelper;
import io.reflection.app.client.res.Styles;
import io.reflection.app.client.res.Styles.ReflectionMainStyles;

import com.google.gwt.core.client.GWT;
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

	ReflectionMainStyles style = Styles.STYLES_INSTANCE.reflectionMainStyle();

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
					if (!title.hasClassName(style.noAccordionContent())) {
						if (container.getElement().hasClassName(style.isClosed())) {
							container.getElement().removeClassName(style.isClosed());
							accordionContent.getStyle().setMarginTop(0, Unit.PX);
						} else {
							container.getElement().addClassName(style.isClosed());
							accordionContent.getStyle().setMarginTop(-Double.valueOf(accordionContent.getClientHeight()), Unit.PX);
						}
					}
				}
			}
		});

		Window.addResizeHandler(new ResizeHandler() {

			@Override
			public void onResize(ResizeEvent event) {
				container.getElement().addClassName("is-closed");
				accordionContent.getStyle().setMarginTop(-Double.valueOf(accordionContent.getClientHeight()), Unit.PX);
			}
		});

	}

	public void setEmpty(boolean isEmpty) {
		if (isEmpty) {
			title.addClassName(style.noAccordionContent());
			subtitle.removeFromParent();
			accordionContent.removeFromParent();
		} else {

		}
	}

	public void setFeatureComplete(boolean complete) {
		if (complete) {
			DOMHelper.addClassName(this.getElement(), style.featureComplete());
		} else {
			this.getElement().removeClassName(style.featureComplete());
		}
	}

	public void setTitleText(String text) {
		boolean hasSubtitle;
		if (hasSubtitle = title.isOrHasChild(subtitle)) {
			subtitle.removeFromParent();
		}
		title.setInnerText(text);
		if (hasSubtitle) {
			title.appendChild(subtitle);
		}
	}

	public void setSubtitleText(String text) {
		subtitle.setInnerText(text);
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
		liElem.addClassName(style.listItemComplete());
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

		if (!title.hasClassName(style.noAccordionContent())) {
			if (!container.getElement().hasClassName(style.isClosed())) {
				container.getElement().addClassName(style.isClosed());
			}
			accordionContent.getStyle().setMarginTop(-Double.valueOf(accordionContent.getClientHeight()), Unit.PX);
		}
	}

}
