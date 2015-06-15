//
//  Checkbox.java
//  storedata
//
//  Created by Stefano Capuzzi on 2 Mar 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.component;

import io.reflection.app.client.res.Styles;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.user.client.ui.CheckBox;

/**
 * @author Stefano Capuzzi
 *
 */
public class FormCheckbox extends CheckBox {

	private LabelElement firstLabel;
	private LabelElement secondLabel = Document.get().createLabelElement();
	private SpanElement errorSpan = Document.get().createSpanElement();

	// private final String checkboxId = HTMLPanel.createUniqueId();

	@UiConstructor
	public FormCheckbox(String label) {
		super(label);

		this.setStyleName("");
		firstLabel = this.getElement().getElementsByTagName("label").getItem(0).cast();
		firstLabel.setClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().checkboxLabel());
		secondLabel.setHtmlFor(firstLabel.getHtmlFor());
		secondLabel.setClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().checkboxLabelVisible());
		secondLabel.setInnerText(label);
		this.getElement().appendChild(secondLabel);

		errorSpan.addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().blockErrorMessage());
	}

	public void showError(String text) {
		getElement().getParentElement().addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().isErrorMessageShowing());
		errorSpan.setInnerText(text);
		secondLabel.insertFirst(errorSpan);
	}

	public void hideError() {
		getElement().getParentElement().removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().isErrorMessageShowing());
		errorSpan.removeFromParent();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.CheckBox#setHTML(java.lang.String)
	 */
	@Override
	public void setHTML(String html) {
		super.setHTML(html);
		if (secondLabel != null) {
			secondLabel.setInnerHTML(html);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.CheckBox#setText(java.lang.String)
	 */
	@Override
	public void setText(String text) {
		super.setText(text);
		if (secondLabel != null) {
			secondLabel.setInnerText(text);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.HasEnabled#setEnabled(boolean)
	 */
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		if (enabled) {
			removeStyleDependentName("disabled");
			firstLabel.setClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().checkboxLabel());
			secondLabel.setClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().checkboxLabelVisible());
		} else {
			firstLabel.setClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().checkboxLabelDisabled());
			secondLabel.setClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().checkboxLabelVisible() + " "
					+ Styles.STYLES_INSTANCE.reflectionMainStyle().checkboxLabelVisibleDisabled());
			addStyleDependentName("disabled");
		}
	}

}
