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
public class AlertBox extends Composite {

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

		mClose.getElement().setInnerHTML("&times;");
		mSpinner.setResource(Images.INSTANCE.spinnerInfo());
	}

	public void setCanDismiss(boolean value) {
		mClose.setVisible(value);

		if (value) {
			addStyleName("alert-dismissable");
		} else {
			removeStyleName("alert-dismissable");
		}
	}

	public boolean getCanDismiss() {
		return mClose.isVisible();
	}

	public void setType(AlertBoxType type) {
		mType = type;

		switch (mType) {
		case DangerAlertBoxType:
			removeStyleName("alert-success");
			addStyleName("alert-danger");
			removeStyleName("alert-warning");
			removeStyleName("alert-info");
			break;
		case InfoAlertBoxType:
			removeStyleName("alert-success");
			removeStyleName("alert-danger");
			removeStyleName("alert-warning");
			addStyleName("alert-info");
			break;
		case SuccessAlertBoxType:
			addStyleName("alert-success");
			removeStyleName("alert-danger");
			removeStyleName("alert-warning");
			removeStyleName("alert-info");
			break;
		case WarningAlertBoxType:
			removeStyleName("alert-success");
			removeStyleName("alert-danger");
			addStyleName("alert-warning");
			removeStyleName("alert-info");
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
	}

}
