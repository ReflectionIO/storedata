//
//  ToggleRadioButton.java
//  storedata
//
//  Created by Stefano Capuzzi (capuzzistefano) on 2 Apr 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.component;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasName;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.SimpleRadioButton;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Stefano Capuzzi (capuzzistefano)
 *
 */
public class ToggleRadioButton extends Composite implements HasName, HasValue<Boolean> {

	private static ToggleRadioButtonUiBinder uiBinder = GWT.create(ToggleRadioButtonUiBinder.class);

	interface ToggleRadioButtonUiBinder extends UiBinder<Widget, ToggleRadioButton> {}

	@UiField(provided = true) SimpleRadioButton radioButton = new SimpleRadioButton("");
	@UiField LabelElement label;
	@UiField Element svgPath;
	private final String radioButtonId = HTMLPanel.createUniqueId();
	private String name;

	public ToggleRadioButton(String name) {
		initWidget(uiBinder.createAndBindUi(this));

		this.name = name;
		radioButton.setName(name);
		radioButton.getElement().setId(radioButtonId);
		label.setHtmlFor(radioButtonId);

		this.sinkEvents(Event.ONCLICK);
		this.addHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (!getValue().booleanValue()) {
					setValue(Boolean.TRUE, true);
				}
			}
		}, ClickEvent.getType());
	}

	public void setSvgPath(String path) {
		svgPath.setAttribute("d", path);
	}

	public void setName(String name) {
		radioButton.setName(name);
	}

	public void setLabelClassName(String className) {
		label.addClassName(className);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.HasName#getName()
	 */
	@Override
	public String getName() {
		return name;
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
		return radioButton.getValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.HasValue#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(Boolean value) {
		radioButton.setValue(value);
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
		radioButton.setValue(value, false);
	}

}
