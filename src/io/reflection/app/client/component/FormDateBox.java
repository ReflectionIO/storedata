//
//  FormDateBox.java
//  storedata
//
//  Created by Stefano Capuzzi (capuzzistefano) on 31 Mar 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.component;

import static io.reflection.app.client.helper.FormattingHelper.DATE_FORMATTER_DD_MMM_YYYY;
import io.reflection.app.client.helper.FilterHelper;

import java.util.Date;

import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.user.datepicker.client.DatePicker;

/**
 * @author Stefano Capuzzi (capuzzistefano)
 *
 */
public class FormDateBox extends DateBox {

	public FormDateBox() {
		this(new DatePicker(), FilterHelper.getDaysAgo(3), new DateBox.DefaultFormat(DATE_FORMATTER_DD_MMM_YYYY));
	}

	public FormDateBox(DatePicker picker, Date date, Format format) {
		super(picker, date, format);
		setStyleName("");
		getElement().setAttribute("readonly", "true");
	}

	// @Override
	// public void setValue(Date date) {
	// if (SessionController.get().isAdmin()
	// || (FilterHelper.afterOrSameDate(date, ApiCallHelper.getUTCDate(2015, 9, 1)) && FilterHelper.beforeOrSameDate(date, FilterHelper.getDaysAgo(3)))) {
	// super.setValue(date, false);
	// }
	// }

	// @Override
	// public void setValue(Date date, boolean fireEvents) {
	// if (SessionController.get().isAdmin()
	// || (FilterHelper.afterOrSameDate(date, ApiCallHelper.getUTCDate(2015, 9, 1)) && FilterHelper.beforeOrSameDate(date, FilterHelper.getDaysAgo(3)))) {
	// super.setValue(date, fireEvents);
	// }
	// }

	public void setTooltip(String text) {
		getElement().addClassName("js-tooltip");
		getElement().setAttribute("data-tooltip", text);
	}

}
