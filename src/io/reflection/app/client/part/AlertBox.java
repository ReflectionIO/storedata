//
//  AlertBox.java
//  storedata
//
//  Created by William Shakour (billy1380) on 28 Nov 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.part;

import io.reflection.app.client.res.Images;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author billy1380
 * 
 */
public class AlertBox extends Composite implements HasCloseHandlers<AlertBox> {

	private static AlertBoxUiBinder uiBinder = GWT.create(AlertBoxUiBinder.class);

	interface AlertBoxUiBinder extends UiBinder<Widget, AlertBox> {}

	public enum AlertBoxType {
		SuccessAlertBoxType,
		InfoAlertBoxType,
		WarningAlertBoxType,
		DangerAlertBoxType
	}

	private AlertBoxType mType;

	@UiField SpanElement mText;
	@UiField SpanElement mDetail;
	@UiField Button mClose;

	@UiField Image mSpinner;

	public AlertBox() {
		initWidget(uiBinder.createAndBindUi(this));

		mSpinner.setResource(Images.INSTANCE.spinnerInfo());
	}

	public void setCanDismiss(boolean value) {
		mClose.setVisible(value);
	}

	public boolean getCanDismiss() {
		return mClose.isVisible();
	}

	public void setType(AlertBoxType type) {
		mType = type;

		switch (mType) {
		case DangerAlertBoxType:
			getElement().getStyle().setBackgroundColor("#d9534f");
			break;
		case InfoAlertBoxType:
			getElement().getStyle().setBackgroundColor("#d9edf7");
			break;
		case SuccessAlertBoxType:
			getElement().getStyle().setBackgroundColor("#dff0d8");
			break;
		case WarningAlertBoxType:
			getElement().getStyle().setBackgroundColor("#f0ad4e");
			break;
		}
	}

	public AlertBoxType getType() {
		return mType;
	}

	public void setLoading(boolean loading) {
		mSpinner.setVisible(loading);
		if (loading) {
			setType(AlertBoxType.InfoAlertBoxType);
		}
	}

	public boolean getLoading() {
		return mSpinner.isVisible();
	}

	public void setText(String text) {
		mText.setInnerText(text);
	}

	public String getText() {
		return mText.getInnerText();
	}

	public void setDetail(String detail) {
		mDetail.setInnerText(detail);
	}

	public String getDetail() {
		return mDetail.getInnerText();
	}

	@UiHandler("mClose")
	void onCloseClicked(ClickEvent e) {
		setVisible(false);

		CloseEvent.fire(this, this, false);
	}

	public void dismiss() {
		setVisible(false);

		CloseEvent.fire(this, this, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.event.logical.shared.HasCloseHandlers#addCloseHandler(com.google.gwt.event.logical.shared.CloseHandler)
	 */
	@Override
	public HandlerRegistration addCloseHandler(CloseHandler<AlertBox> handler) {
		return this.addHandler(handler, CloseEvent.getType());
	}
}
