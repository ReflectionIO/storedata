//
//  DeveloperPage.java
//  storedata
//
//  Created by Jamie Gilman on 13 Jan 2016.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.LoadingStateChangeEvent;
import com.google.gwt.user.cellview.client.LoadingStateChangeEvent.LoadingState;
import com.google.gwt.user.client.ui.Widget;

import io.reflection.app.client.controller.ServiceConstants;
import io.reflection.app.client.helper.AnimationHelper;
import io.reflection.app.client.part.BootstrapGwtCellTable;
import io.reflection.app.client.part.datatypes.MyApp;
import io.reflection.app.client.res.Styles;
import io.reflection.app.client.res.Styles.ReflectionMainStyles;

/**
 * @author Jamie Gilman
 *
 */
public class DeveloperPage extends Page {

	private static DeveloperPageUiBinder uiBinder = GWT.create(DeveloperPageUiBinder.class);
	private final ReflectionMainStyles style = Styles.STYLES_INSTANCE.reflectionMainStyle();

	interface DeveloperPageUiBinder extends UiBinder<Widget, DeveloperPage> {}

	@UiField(provided = true) CellTable<MyApp> appsTable = new CellTable<MyApp>(ServiceConstants.STEP_VALUE, BootstrapGwtCellTable.INSTANCE);

	public DeveloperPage() {
		initWidget(uiBinder.createAndBindUi(this));

		createColumns();
		appsTable.setLoadingIndicator(AnimationHelper.getMyAppsLoadingIndicator(4));
		appsTable.getTableLoadingSection().addClassName(style.tableBodyLoading());

		appsTable.addLoadingStateChangeHandler(new LoadingStateChangeEvent.Handler() {

			@Override
			public void onLoadingStateChanged(LoadingStateChangeEvent event) {
				if (event.getLoadingState() == LoadingState.LOADED) {
					appsTable.setStyleName(style.tableLinkedAccountsDisabled(), appsTable.getRowCount() == 0);
				}
			}
		});
	}

	private void createColumns() {

	}
}