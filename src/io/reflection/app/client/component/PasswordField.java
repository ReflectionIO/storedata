//
//  FormFieldPassword.java
//  storedata
//
//  Created by Stefano Capuzzi on 3 Mar 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.component;

import io.reflection.app.client.helper.FormHelper;
import io.reflection.app.client.res.Styles;
import io.reflection.app.client.res.Styles.ReflectionMainStyles;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasKeyDownHandlers;
import com.google.gwt.event.dom.client.HasKeyUpHandlers;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Stefano Capuzzi
 *
 */
public class PasswordField extends Composite implements HasClickHandlers, HasKeyDownHandlers, HasKeyUpHandlers, Focusable {

	private static FormFieldPasswordUiBinder uiBinder = GWT.create(FormFieldPasswordUiBinder.class);

	interface FormFieldPasswordUiBinder extends UiBinder<Widget, PasswordField> {}

	@UiField TextBox passwordTextBox;
	@UiField LabelElement label;
	@UiField DivElement strengthIndicatorPanel;
	@UiField SpanElement strengthDescription;
	@UiField SpanElement strengthIndicator;
	private boolean isStrengthIndicator;
	private SpanElement note = Document.get().createSpanElement();
	private final ReflectionMainStyles refStyle = Styles.STYLES_INSTANCE.reflectionMainStyle();

	public PasswordField() {
		this(false);
	}

	public PasswordField(boolean isStrengthIndicator) {
		initWidget(uiBinder.createAndBindUi(this));

		this.isStrengthIndicator = isStrengthIndicator;
		if (!isStrengthIndicator) {
			strengthIndicatorPanel.removeFromParent();
		}

		note.addClassName(refStyle.inputHint());
	}

	public void setLabelText(String text) {
		strengthIndicatorPanel.removeFromParent();
		label.setInnerText(text);
		if (isStrengthIndicator) {
			label.appendChild(strengthIndicatorPanel);
		}
	}

	public String getText() {
		return passwordTextBox.getText();
	}

	@UiHandler("passwordTextBox")
	void onEnterKeyPressForgotPassword(KeyPressEvent event) {
		if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
			event.preventDefault();
		}
	}

	public void clear() {
		passwordTextBox.setText("");
		hideNote();
		this.getElement().addClassName(refStyle.isClosed());
		if (isStrengthIndicator) {
			strengthDescription.setInnerText("Strength");
			strengthIndicator.setClassName("");
		}
		passwordTextBox.setFocus(false);
	}

	public void setFocus(boolean focused) {
		passwordTextBox.setFocus(focused);
	}

	public void setEnabled(boolean enabled) {
		this.setStyleName(refStyle.formFieldDisabled(), !enabled);
		passwordTextBox.setEnabled(enabled);
	}

	public boolean isEnabled() {
		return passwordTextBox.isEnabled();
	}

	public void showNote(String text, boolean isError) {
		if (isError) {
			this.getElement().addClassName(refStyle.formFieldError());
		}
		note.setInnerText(text);
		if (!label.isOrHasChild(note)) {
			label.appendChild(note);
		}
	}

	public void hideNote() {
		this.getElement().removeClassName(refStyle.formFieldError());
		if (label.isOrHasChild(note)) {
			label.removeChild(note);
		}
	}

	public void setTabIndex(int index) {
		passwordTextBox.setTabIndex(index);
	}

	public void setStyleName(String styleName) {
		this.setStyleName(styleName, true);
	}

	@UiHandler("passwordTextBox")
	void onFocused(FocusEvent event) {
		this.getElement().removeClassName(refStyle.isClosed());
	}

	@UiHandler("passwordTextBox")
	void onBlurred(BlurEvent event) {
		if (getText().length() < 1) {
			this.getElement().addClassName(refStyle.isClosed());
		}
	}

	@UiHandler("passwordTextBox")
	void onFieldModified(KeyUpEvent event) {
		if (isStrengthIndicator) {
			if (passwordTextBox.getText().length() < 1) {
				strengthDescription.setInnerText("Strength");
				strengthIndicator.setClassName("");
			} else {
				switch (FormHelper.getPasswordStrength(passwordTextBox.getText())) {
				case 0:
					strengthDescription.setInnerText("Weak");
					strengthIndicator.setClassName(refStyle.isPathetic());
					break;
				case 1:
					strengthDescription.setInnerText("Ok");
					strengthIndicator.setClassName(refStyle.isOk());
					break;
				case 2:
					strengthDescription.setInnerText("Strongish");
					strengthIndicator.setClassName(refStyle.isStrong());
					break;
				case 3:
				case 4:
					strengthDescription.setInnerText("Impressive");
					strengthIndicator.setClassName(refStyle.isImpressive());
					break;
				}
			}
		}
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
		return passwordTextBox.getTabIndex();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.Focusable#setAccessKey(char)
	 */
	@Override
	public void setAccessKey(char key) {
		passwordTextBox.setAccessKey(key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.event.dom.client.HasKeyDownHandlers#addKeyDownHandler(com.google.gwt.event.dom.client.KeyDownHandler)
	 */
	@Override
	public HandlerRegistration addKeyDownHandler(KeyDownHandler handler) {
		return addDomHandler(handler, KeyDownEvent.getType());
	}

}
