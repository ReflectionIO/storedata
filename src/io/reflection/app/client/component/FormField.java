//
//  FormField.java
//  storedata
//
//  Created by Stefano Capuzzi on 2 Mar 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.component;

import io.reflection.app.client.res.Styles;
import io.reflection.app.client.res.Styles.ReflectionMainStyles;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasKeyPressHandlers;
import com.google.gwt.event.dom.client.HasKeyUpHandlers;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Stefano Capuzzi
 *
 */
public class FormField extends Composite implements HasClickHandlers, HasKeyPressHandlers, HasKeyUpHandlers, Focusable {

	private static FormFieldUiBinder uiBinder = GWT.create(FormFieldUiBinder.class);

	interface FormFieldUiBinder extends UiBinder<Widget, FormField> {}

	@UiField TextBox textBox;
	@UiField LabelElement label;

	@UiField Anchor infoLink;
	@UiField SpanElement infoLabel;

	private SpanElement note = Document.get().createSpanElement();

	private final ReflectionMainStyles refStyle = Styles.STYLES_INSTANCE.reflectionMainStyle();

	public FormField() {
		initWidget(uiBinder.createAndBindUi(this));

		infoLink.removeFromParent();
		note.addClassName(refStyle.inputHint());
	}

	public void setLabelText(String text) {
		label.setInnerText(text);
	}

	public void setText(String text) {
		if (text != null && text.length() > 0) {
			this.getElement().removeClassName(refStyle.isClosed());
		} else {
			if (!this.getElement().hasClassName(refStyle.isClosed())) {
				this.getElement().addClassName(refStyle.isClosed());
			}
		}
		textBox.setText(text);
	}

	public String getText() {
		return textBox.getText();
	}

	public void setFocus(boolean focused) {
		textBox.setFocus(focused);
	}

	@UiHandler("textBox")
	void onFocused(FocusEvent event) {
		this.getElement().removeClassName(refStyle.isClosed());
	}

	@UiHandler("textBox")
	void onBlurred(BlurEvent event) {
		if (!this.getElement().hasClassName(refStyle.isClosed()) && getText().length() < 1) {
			this.getElement().addClassName(refStyle.isClosed());
		}
	}

	public void setEnabled(boolean enabled) {
		if (enabled) {
			this.getElement().removeClassName(refStyle.formFieldDisabled());
		} else {
			this.getElement().addClassName(refStyle.formFieldDisabled());
		}
		textBox.setEnabled(enabled);
	}

	public boolean isEnabled() {
		return textBox.isEnabled();
	}

	public void showNote(String text, boolean isError) {
		if (isError) {
			if (!this.getElement().hasClassName(refStyle.formFieldError())) {
				this.getElement().addClassName(refStyle.formFieldError());
			}
		}
		note.setInnerText(text);
		label.appendChild(note);
	}

	public void hideNote() {
		this.getElement().removeClassName(refStyle.formFieldError());
		if (label.isOrHasChild(note)) {
			label.removeChild(note);
		}
	}

	public void setInfoHref(SafeUri href) {
		if (!infoLink.isAttached()) {
			this.getElement().appendChild(infoLink.getElement());
		}
		infoLink.setHref(href);
	}

	public void setInfoLabelText(String text) {
		if (!infoLink.isAttached()) {
			this.getElement().appendChild(infoLink.getElement());
		}
		infoLabel.setInnerText(text);
	}

	public void setTabIndex(int index) {
		textBox.setTabIndex(index);
	}

	public void setStyleName(String styleName) {
		this.setStyleName(styleName, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.event.dom.client.HasClickHandlers#addClickHandler(com.google.gwt.event.dom.client.ClickHandler)
	 */
	@Override
	public HandlerRegistration addClickHandler(ClickHandler handler) {
		return addDomHandler(handler, ClickEvent.getType());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.event.dom.client.HasKeyPressHandlers#addKeyPressHandler(com.google.gwt.event.dom.client.KeyPressHandler)
	 */
	@Override
	public HandlerRegistration addKeyPressHandler(KeyPressHandler handler) {
		return addDomHandler(handler, KeyPressEvent.getType());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.event.dom.client.HasKeyUpHandlers#addKeyUpHandler(com.google.gwt.event.dom.client.KeyUpHandler)
	 */
	@Override
	public HandlerRegistration addKeyUpHandler(KeyUpHandler handler) {
		return addDomHandler(handler, KeyUpEvent.getType());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.Focusable#getTabIndex()
	 */
	@Override
	public int getTabIndex() {
		return textBox.getTabIndex();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.Focusable#setAccessKey(char)
	 */
	@Override
	public void setAccessKey(char key) {
		textBox.setAccessKey(key);
	}

}
