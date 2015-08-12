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
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;

/**
 * @author Stefano Capuzzi
 *
 */
public class LoadingButton extends Button {

	public static final String STYLE_LOADING = Styles.STYLES_INSTANCE.reflectionMainStyle().refButtonIsLoading();
	public static final String STYLE_SUCCESS = Styles.STYLES_INSTANCE.reflectionMainStyle().refButtonSuccess();
	public static final String STYLE_ERROR = Styles.STYLES_INSTANCE.reflectionMainStyle().refButtonError();
	public static final int DEFAULT_TIMEOUT = 2000;
	public static final String DEFAULT_ERROR_MESSAGE = "Oops, Something's Wrong";
	public static final String DEFAULT_LOADING_MESSAGE = "Loading";

	private String originalText;
	private SpanElement progressBar = Document.get().createSpanElement();
	private SpanElement textElem = Document.get().createSpanElement();
	private boolean isProgressive;

	public interface IResetStatusCallback {
		void onResetStatus();
	}

	public LoadingButton() {
		this(false);
	}

	public LoadingButton(boolean isProgressive) {
		this.isProgressive = isProgressive;

		if (isProgressive) {
			progressBar.addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().loadingProgress());
			getElement().appendChild(progressBar);
			textElem.addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().loadingButtonText());
			getElement().appendChild(textElem);
		}
	}

	@Override
	public void setText(String text) {
		if (isProgressive) {
			textElem.setInnerText(text);
		} else {
			super.setText(text);
		}
	}

	public void setStatusLoading(String loadingText) {
		setEnabled(false);
		getElement().removeClassName(STYLE_ERROR);
		getElement().removeClassName(STYLE_SUCCESS);
		getElement().addClassName(STYLE_LOADING);
		if (loadingText != null) {
			setText(loadingText);
		} else {
			setText(DEFAULT_LOADING_MESSAGE);
		}
	}

	public void setStatusLoading() {
		setStatusLoading(null);
	}

	public void setStatusSuccess(String successText, int hideTimeout, final IResetStatusCallback callback) {
		setEnabled(false);
		getElement().removeClassName(STYLE_ERROR);
		getElement().removeClassName(STYLE_LOADING);
		getElement().addClassName(STYLE_SUCCESS);
		if (successText != null) {
			setText(successText);
		}
		if (isProgressive) {
			progressBar.getStyle().setDisplay(Display.NONE);
			setProgressiveStatus(0);
		}
		if (hideTimeout != 0) {
			Timer t = new Timer() {
				@Override
				public void run() {
					resetStatus();
					if (callback != null) {
						callback.onResetStatus();
					}
				}
			};
			t.schedule(hideTimeout);
		}
	}

	public void setStatusSuccess(String successText, int hideTimeout) {
		setStatusSuccess(successText, hideTimeout, null);
	}

	public void setStatusSuccess() {
		setStatusSuccess(null, DEFAULT_TIMEOUT, null);
	}

	public void setStatusSuccess(IResetStatusCallback callback) {
		setStatusSuccess(null, DEFAULT_TIMEOUT, callback);
	}

	public void setStatusSuccess(int hideTimeout) {
		setStatusSuccess(null, hideTimeout, null);
	}

	public void setStatusSuccess(String successText) {
		setStatusSuccess(successText, DEFAULT_TIMEOUT, null);
	}

	public void setStatusSuccess(String successText, IResetStatusCallback callback) {
		setStatusSuccess(successText, DEFAULT_TIMEOUT, callback);
	}

	public void setStatusError(String errorText, int hideTimeout, final IResetStatusCallback callback) {
		setEnabled(false);
		getElement().removeClassName(STYLE_LOADING);
		getElement().removeClassName(STYLE_SUCCESS);
		getElement().addClassName(STYLE_ERROR);
		if (errorText != null) {
			setText(errorText);
		} else {
			setText(DEFAULT_ERROR_MESSAGE);
		}
		if (isProgressive) {
			progressBar.getStyle().setDisplay(Display.NONE);
			setProgressiveStatus(0);
		}
		if (hideTimeout != 0) {
			Timer t = new Timer() {
				@Override
				public void run() {
					resetStatus();
					if (callback != null) {
						callback.onResetStatus();
					}
				}
			};

			t.schedule(DEFAULT_TIMEOUT);
		}
	}

	public void setStatusError(String errorText, int hideTimeout) {
		setStatusError(null, DEFAULT_TIMEOUT, null);
	}

	public void setStatusError() {
		setStatusError(null, DEFAULT_TIMEOUT, null);
	}

	public void setStatusError(IResetStatusCallback callback) {
		setStatusError(null, DEFAULT_TIMEOUT, callback);
	}

	public void setStatusError(int hideTimeout) {
		setStatusError(null, hideTimeout, null);
	}

	public void setStatusError(String errorText) {
		setStatusError(errorText, DEFAULT_TIMEOUT, null);
	}

	public void setStatusError(String errorText, IResetStatusCallback callback) {
		setStatusError(errorText, DEFAULT_TIMEOUT, callback);
	}

	public void setProgressiveStatus(double percentage) {
		setProgressiveStatus(textElem.getInnerText(), percentage);
	}

	public void setProgressiveStatus(String statusText, double percentage) {
		setText(statusText);
		progressBar.getStyle().setWidth(percentage, Unit.PCT);
	}

	public void resetStatus() {
		setEnabled(true);
		getElement().removeClassName(STYLE_ERROR);
		getElement().removeClassName(STYLE_LOADING);
		getElement().removeClassName(STYLE_SUCCESS);
		if (isProgressive) {
			setProgressiveStatus(originalText, 0.0);
			progressBar.getStyle().setDisplay(Display.BLOCK);
		} else {
			setText(originalText);
		}
	}

	public boolean isStatusLoading() {
		return getElement().hasClassName(STYLE_LOADING);
	}

	public boolean isStatusError() {
		return getElement().hasClassName(STYLE_ERROR);
	}

	public boolean isStatusSuccess() {
		return getElement().hasClassName(STYLE_SUCCESS);
	}

	public boolean isStatusDefault() {
		return !isStatusLoading() && !isStatusError() && !isStatusSuccess();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.Button#click()
	 */
	@Override
	public void click() {
		if (this.isStatusDefault()) {
			super.click();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.FocusWidget#onAttach()
	 */
	@Override
	protected void onAttach() {
		super.onAttach();

		if (originalText == null) {
			originalText = (isProgressive ? textElem.getInnerText() : getText());
		}

		if (isProgressive) {
			setStyleName(Styles.STYLES_INSTANCE.reflectionMainStyle().refButtonLoadingDeterminate(), true);
		}
	}

}
