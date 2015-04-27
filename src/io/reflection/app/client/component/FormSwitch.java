//
//  FormSwitch.java
//  storedata
//
//  Created by Stefano Capuzzi (capuzzistefano) on 2 Apr 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.component;

import io.reflection.app.client.helper.DOMHelper;
import io.reflection.app.client.res.Styles;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.SimpleCheckBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Stefano Capuzzi (capuzzistefano)
 *
 */
public class FormSwitch extends Composite implements HasValue<Boolean> {

	private static FormSwitchUiBinder uiBinder = GWT.create(FormSwitchUiBinder.class);

	interface FormSwitchUiBinder extends UiBinder<Widget, FormSwitch> {}

	@UiField SpanElement label;
	@UiField DivElement checkBoxContainer;
	@UiField SimpleCheckBox checkBox;
	private final String checkBoxId = HTMLPanel.createUniqueId();
	private LabelElement iconSwitchLabel;

	public FormSwitch(boolean isSmall) {

		initWidget(uiBinder.createAndBindUi(this));

		if (isSmall) {
			DOMHelper.addClassName(this.getElement(), Styles.STYLES_INSTANCE.reflectionMainStyle().filterSwitchSmall());
			DOMHelper.addClassName(checkBoxContainer, Styles.STYLES_INSTANCE.reflectionMainStyle().canToggleSizeSmall());
		} else {
			DOMHelper.addClassName(checkBoxContainer, Styles.STYLES_INSTANCE.reflectionMainStyle().canToggleSizeLarge());
		}

		checkBox.getElement().setId(checkBoxId);

		iconSwitchLabel = Document.get().createLabelElement();
		iconSwitchLabel.setHtmlFor(checkBoxId);
		checkBoxContainer.appendChild(iconSwitchLabel);
		DivElement toggle = Document.get().createDivElement();
		toggle.addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().canToggle__switch());
		toggle.setAttribute("data-checked", "Y");
		toggle.setAttribute("data-unchecked", "N");
		iconSwitchLabel.appendChild(toggle);

		DOM.sinkEvents(checkBoxContainer, Event.ONCLICK);
		DOM.setEventListener(checkBoxContainer, new EventListener() {
			public void onBrowserEvent(Event event) {
				if (Event.ONCLICK == event.getTypeInt()) {
					setValue(!getValue(), true);
				}
			}
		});

	}

	public FormSwitch() {
		this(false);
	}

	public void setLabel(String text) {
		label.setInnerText(text);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.event.logical.shared.HasValueChangeHandlers#addValueChangeHandler(com.google.gwt.event.logical.shared.ValueChangeHandler)
	 */
	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Boolean> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.HasValue#getValue()
	 */
	@Override
	public Boolean getValue() {
		return checkBox.getValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.HasValue#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(Boolean value) {
		checkBox.setValue(value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.HasValue#setValue(java.lang.Object, boolean)
	 */
	@Override
	public void setValue(Boolean value, boolean fireEvents) {
		if (value == null) {
			value = Boolean.FALSE;
		}
		Boolean oldValue = fireEvents ? getValue() : null;
		if (fireEvents) {
			ValueChangeEvent.fireIfNotEqual(this, oldValue, value);
		}
		checkBox.setValue(value, false);
	}

}
