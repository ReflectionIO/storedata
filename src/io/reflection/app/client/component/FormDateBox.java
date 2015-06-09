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
		this(new DatePicker(), FilterHelper.getDaysAgo(2), new DateBox.DefaultFormat(DATE_FORMATTER_DD_MMM_YYYY));
	}

	public FormDateBox(DatePicker picker, Date date, Format format) {
		super(picker, date, format);
		setStyleName("");
	}

}
