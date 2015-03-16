//
//  FormRadioButton.java
//  storedata
//
//  Created by Stefano Capuzzi (capuzzistefano) on 11 Mar 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.component;

import io.reflection.app.client.res.Styles;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.user.client.ui.RadioButton;

/**
 * @author Stefano Capuzzi (capuzzistefano)
 *
 */
public class FormRadioButton extends RadioButton {

	private LabelElement firstLabel;
	private LabelElement secondLabel;

	/**
	 * @param name
	 */
	@UiConstructor
	public FormRadioButton(String name, String label) {
		super(name, label);

		this.setStyleName("");
		firstLabel = this.getElement().getElementsByTagName("label").getItem(0).cast();
		firstLabel.setClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().radioButtonLabel());
		secondLabel = Document.get().createLabelElement();
		secondLabel.setHtmlFor(firstLabel.getHtmlFor());
		secondLabel.setClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().radioButtonLabelVisible());
		secondLabel.setInnerText(label);
		this.getElement().appendChild(secondLabel);
	}

}
