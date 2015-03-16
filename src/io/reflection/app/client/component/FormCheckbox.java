//
//  Checkbox.java
//  storedata
//
//  Created by Stefano Capuzzi on 2 Mar 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.component;

import io.reflection.app.client.res.Styles;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Stefano Capuzzi
 *
 */
public class FormCheckbox extends Composite implements HasValue<Boolean>, Focusable, HasEnabled {

	private static CheckboxUiBinder uiBinder = GWT.create(CheckboxUiBinder.class);

	interface CheckboxUiBinder extends UiBinder<Widget, FormCheckbox> {}

	@UiField InputElement input;
	@UiField LabelElement label;
	@UiField LabelElement labelVisible;
	private final String checkboxId = HTMLPanel.createUniqueId();

	public FormCheckbox() {
		initWidget(uiBinder.createAndBindUi(this));
		input.setId(checkboxId);
		label.setAttribute("for", checkboxId);
		labelVisible.setAttribute("for", checkboxId);
	}

	public void setText(String text) {
		label.setInnerText(text);
		labelVisible.setInnerText(text);
	}

	public void setInnerHtml(String html) {
		label.setInnerHTML(html);
		labelVisible.setInnerHTML(html);
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
		if (isAttached()) {
			return input.isChecked();
		} else {
			return input.isDefaultChecked();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.HasValue#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(Boolean value) {
		setValue(value, false);
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
		Boolean oldValue = getValue();
		input.setChecked(value);
		input.setDefaultChecked(value);
		if (value.equals(oldValue)) { return; }
		if (fireEvents) {
			ValueChangeEvent.fire(this, value);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.Focusable#getTabIndex()
	 */
	@Override
	public int getTabIndex() {
		return input.getTabIndex();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.Focusable#setAccessKey(char)
	 */
	@Override
	public void setAccessKey(char key) {
		input.setAccessKey("" + key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.Focusable#setFocus(boolean)
	 */
	@Override
	public void setFocus(boolean focused) {
		if (focused) {
			input.focus();
		} else {
			input.blur();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.Focusable#setTabIndex(int)
	 */
	@Override
	public void setTabIndex(int index) {
		// Need to guard against call to setTabIndex before inputElem is
		// initialized. This happens because FocusWidget's (a superclass of
		// CheckBox) setElement method calls setTabIndex before inputElem is
		// initialized. See CheckBox's protected constructor for more information.
		if (input != null) {
			input.setTabIndex(index);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.HasEnabled#isEnabled()
	 */
	@Override
	public boolean isEnabled() {
		return !input.isDisabled();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.HasEnabled#setEnabled(boolean)
	 */
	@Override
	public void setEnabled(boolean enabled) {
		input.setDisabled(!enabled);
		if (enabled) {
			removeStyleDependentName("disabled");
			label.setClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().checkboxLabel());
			labelVisible.setClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().checkboxLabelVisible());
		} else {
			label.setClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().checkboxLabelDisabled());
			labelVisible.setClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().checkboxLabelVisible() + " "
					+ Styles.STYLES_INSTANCE.reflectionMainStyle().checkboxLabelVisibleDisabled());
			addStyleDependentName("disabled");
		}
	}

}
