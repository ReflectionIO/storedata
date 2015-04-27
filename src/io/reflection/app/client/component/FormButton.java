//
//  FormButton.java
//  storedata
//
//  Created by Stefano Capuzzi on 3 Mar 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.component;

import io.reflection.app.client.res.Styles;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;

/**
 * @author Stefano Capuzzi
 *
 */
public class FormButton extends Button {

	public static final String STYLE_LOADING = Styles.STYLES_INSTANCE.reflectionMainStyle().refButtonIsLoading();
	public static final String STYLE_SUCCESS = Styles.STYLES_INSTANCE.reflectionMainStyle().refButtonSuccess();
	public static final String STYLE_ERROR = Styles.STYLES_INSTANCE.reflectionMainStyle().refButtonError();
	public static final int DEFAULT_TIMEOUT = 3000;
	public static final String DEFAULT_ERROR_MESSAGE = "Ops, an error occurred";

	private SpanElement spanElem;
	private String defaultText;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.FocusWidget#onAttach()
	 */
	@Override
	protected void onAttach() {
		super.onAttach();

		defaultText = this.getText();
	}

	public void setSpanClassName(String className) {
		if (spanElem == null) {
			spanElem = Document.get().createSpanElement();
			this.getElement().appendChild(spanElem);
		}
		spanElem.setClassName(className);
	}

	public void setSpanInnerText(String text) {
		if (spanElem == null) {
			spanElem = Document.get().createSpanElement();
			this.getElement().appendChild(spanElem);
		}
		spanElem.setInnerText(text);
	}

	public void setStatusLoading(String loadingText) {
		this.setEnabled(false);
		this.getElement().removeClassName(STYLE_ERROR);
		this.getElement().removeClassName(STYLE_SUCCESS);
		this.getElement().addClassName(STYLE_LOADING);
		if (loadingText != null) {
			this.setText(loadingText);
		}
	}

	public void setStatusLoading() {
		setStatusLoading(null);
	}

	public void setStatusSuccess(String successText, int hideTimeout) {
		this.setEnabled(false);
		this.getElement().removeClassName(STYLE_ERROR);
		this.getElement().removeClassName(STYLE_LOADING);
		this.getElement().addClassName(STYLE_SUCCESS);
		if (successText != null) {
			this.setText(successText);
		}
		if (hideTimeout != 0) {
			Timer t = new Timer() {
				@Override
				public void run() {
					resetStatus();
				}
			};

			t.schedule(hideTimeout);
		}
	}

	public void setStatusSuccess() {
		setStatusSuccess(null, DEFAULT_TIMEOUT);
	}

	public void setStatusSuccess(int hideTimeout) {
		setStatusSuccess(null, hideTimeout);
	}

	public void setStatusSuccess(String successText) {
		setStatusError(successText, DEFAULT_TIMEOUT);
	}

	public void setStatusError(String errorText, int hideTimeout) {
		this.setEnabled(false);
		this.getElement().removeClassName(STYLE_LOADING);
		this.getElement().removeClassName(STYLE_SUCCESS);
		this.getElement().addClassName(STYLE_ERROR);
		if (errorText != null) {
			this.setText(errorText);
		} else {
			this.setText(DEFAULT_ERROR_MESSAGE);
		}
		if (hideTimeout != 0) {
			Timer t = new Timer() {
				@Override
				public void run() {
					resetStatus();
				}
			};

			t.schedule(DEFAULT_TIMEOUT);
		}
	}

	public void setStatusError() {
		setStatusError(null, DEFAULT_TIMEOUT);
	}

	public void setStatusError(int hideTimeout) {
		setStatusError(null, hideTimeout);
	}

	public void setStatusError(String errorText) {
		setStatusError(errorText, DEFAULT_TIMEOUT);
	}

	public void resetStatus() {
		this.setEnabled(true);
		this.getElement().removeClassName(STYLE_ERROR);
		this.getElement().removeClassName(STYLE_LOADING);
		this.getElement().removeClassName(STYLE_SUCCESS);
		this.setText(defaultText);
	}

}
