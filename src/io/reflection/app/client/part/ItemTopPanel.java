//
//  ItemTopPanel.java
//  storedata
//
//  Created by William Shakour (billy1380) on 29 Jan 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.part;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.user.datepicker.client.DateBox.DefaultFormat;

/**
 * @author billy1380
 * 
 */
public class ItemTopPanel extends Composite {

	private static ItemTopPanelUiBinder uiBinder = GWT.create(ItemTopPanelUiBinder.class);

	interface ItemTopPanelUiBinder extends UiBinder<Widget, ItemTopPanel> {}

	@UiField DateBox mDate;
	@UiField ListBox mAppStore;
	// @UiField ListBox mListType;
	@UiField ListBox mCountry;

	public ItemTopPanel() {
		initWidget(uiBinder.createAndBindUi(this));

		BootstrapGwtDatePicker.INSTANCE.styles().ensureInjected();

		mDate.setFormat(new DefaultFormat(DateTimeFormat.getFormat("dd-MM-yyyy")));
		mDate.setValue(new Date());

	}

}
