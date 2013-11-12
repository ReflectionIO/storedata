//
//  RankFilter.java
//  storedata
//
//  Created by William Shakour (billy1380) on 3 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.admin.client.part;

import io.reflection.app.admin.client.controller.FilterController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ShowRangeEvent;
import com.google.gwt.event.logical.shared.ShowRangeHandler;
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
public class RankFilter extends Composite {

	private static RankFilterUiBinder uiBinder = GWT.create(RankFilterUiBinder.class);

	interface RankFilterUiBinder extends UiBinder<Widget, RankFilter> {}

	@UiField DateBox mDate;
	@UiField ListBox mAppStore;
	@UiField ListBox mListType;
	@UiField ListBox mCountry;

	/**
	 * Because this class has a default constructor, it can be used as a binder template. In other words, it can be used in other *.ui.xml files as follows:
	 * <ui:UiBinder xmlns:ui="urn:ui:com.google.gwt.uibinder" xmlns:g="urn:import:**user's package**"> <g:**UserClassName**>Hello!</g:**UserClassName>
	 * </ui:UiBinder> Note that depending on the widget that is used, it may be necessary to implement HasHTML instead of HasText.
	 */
	public RankFilter() {
		initWidget(uiBinder.createAndBindUi(this));

		BootstrapGwtDatePicker.INSTANCE.styles().ensureInjected();

		mDate.setFormat(new DefaultFormat(DateTimeFormat.getFormat("dd-MM-yyyy")));
		mDate.setValue(new Date());

		final List<Date> dates = new ArrayList<Date>();

		mDate.getDatePicker().addShowRangeHandler(new ShowRangeHandler<Date>() {

			@Override
			public void onShowRange(ShowRangeEvent<Date> event) {
				Date start = event.getStart();
				Date end = event.getEnd();
				Date now = new Date();

				dates.clear();
				Date curr = start;
				while (curr.getTime() < end.getTime()) {
					if (curr.getTime() > now.getTime() || curr.getTime() < 1356998401000l) {
						dates.add(curr);
					}

					curr = new Date(curr.getTime() + 24l * 1000l * 60l * 60l);
				}

				mDate.getDatePicker().setTransientEnabledOnDates(false, dates);
			}
		});
		
		FilterController.get().start();
		FilterController.get().setStore(mAppStore.getValue(mAppStore.getSelectedIndex()));
		FilterController.get().setListType(mListType.getValue(mListType.getSelectedIndex()));
		FilterController.get().setCountry(mCountry.getValue(mCountry.getSelectedIndex()));
		FilterController.get().setStartDate(mDate.getValue());
		FilterController.get().commit();

	}

}
