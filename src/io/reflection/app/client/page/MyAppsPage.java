//
//  MyAppsPage.java
//  storedata
//
//  Created by Stefano Capuzzi on 7 Mar 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page;

import static io.reflection.app.client.controller.FilterController.OVERALL_LIST_TYPE;
import io.reflection.app.api.core.shared.call.event.GetLinkedAccountsEventHandler;
import io.reflection.app.client.cell.MiniAppCell;
import io.reflection.app.client.controller.EventController;
import io.reflection.app.client.controller.FilterController;
import io.reflection.app.client.controller.FilterController.Filter;
import io.reflection.app.client.controller.LinkedAccountController;
import io.reflection.app.client.controller.MyAppsController;
import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.controller.NavigationController.Stack;
import io.reflection.app.client.controller.ServiceConstants;
import io.reflection.app.client.controller.SessionController;
import io.reflection.app.client.handler.FilterEventHandler;
import io.reflection.app.client.handler.NavigationEventHandler;
import io.reflection.app.client.page.part.MyAppsTopPanel;
import io.reflection.app.client.part.BootstrapGwtCellTable;
import io.reflection.app.client.part.BootstrapGwtDatePicker;
import io.reflection.app.client.part.SimplePager;
import io.reflection.app.client.part.datatypes.MyApp;
import io.reflection.app.client.res.Images;
import io.reflection.app.client.res.Styles;
import io.reflection.app.datatypes.shared.Item;
import io.reflection.app.datatypes.shared.User;

import java.util.Map;

import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author stefanocapuzzi
 * 
 */
public class MyAppsPage extends Page implements FilterEventHandler, NavigationEventHandler {

	private static MyAppsPageUiBinder uiBinder = GWT.create(MyAppsPageUiBinder.class);

	interface MyAppsPageUiBinder extends UiBinder<Widget, MyAppsPage> {}

	@UiField(provided = true) CellTable<MyApp> appsTable = new CellTable<MyApp>(ServiceConstants.STEP_VALUE, BootstrapGwtCellTable.INSTANCE);
	@UiField SimplePager pager;

	@UiField MyAppsTopPanel topPanel;

	@UiField InlineHyperlink mLinkedAccountsLink;
	@UiField InlineHyperlink mMyAppsLink;

	// Columns
	private TextColumn<MyApp> columnRank;
	private Column<MyApp, Item> columnAppDetails;
	private TextColumn<MyApp> columnPrice;
	private TextColumn<MyApp> columnDownloads;
	private TextColumn<MyApp> columnRevenue;
	private Column<MyApp, ImageResource> columnIap;

	public MyAppsPage() {
		initWidget(uiBinder.createAndBindUi(this));

		BootstrapGwtDatePicker.INSTANCE.styles().ensureInjected();

		Styles.INSTANCE.reflection().ensureInjected();

		FilterController.get().setListType(OVERALL_LIST_TYPE);

		createColumns();

		MyAppsController.get().addDataDisplay(appsTable);
		pager.setDisplay(appsTable);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.Composite#onAttach()
	 */
	@Override
	protected void onAttach() {
		super.onAttach();

		register(EventController.get().addHandlerToSource(NavigationEventHandler.TYPE, NavigationController.get(), this));
		register(EventController.get().addHandlerToSource(FilterEventHandler.TYPE, FilterController.get(), this));
		register(EventController.get().addHandlerToSource(GetLinkedAccountsEventHandler.TYPE, LinkedAccountController.get(), MyAppsController.get()));

		// boolean hasPermission = SessionController.get().loggedInUserHas(SessionController.);

		// pager.setVisible(hasPermission);
		// redirect.setVisible(!hasPermission);

		User user = SessionController.get().getLoggedInUser();

		if (user != null) {
			mLinkedAccountsLink.setTargetHistoryToken(PageType.UsersPageType.asTargetHistoryToken(PageType.LinkedAccountsPageType.toString(),
					user.id.toString()));
			mMyAppsLink.setTargetHistoryToken(PageType.UsersPageType.asTargetHistoryToken(PageType.MyAppsPageType.toString(), user.id.toString(),
					FilterController.get().asMyAppsFilterString()));
		}
	}

	/**
	 * 
	 */
	private void createColumns() {

		columnRank = new TextColumn<MyApp>() {
			@Override
			public String getValue(MyApp object) {
				return (object.ranks != null) ? object.overallPosition : "-";
			}
		};
		appsTable.addColumn(columnRank, "Rank");

		columnAppDetails = new Column<MyApp, Item>(new MiniAppCell()) {
			@Override
			public Item getValue(MyApp object) {
				return object.item;
			}
		};
		appsTable.addColumn(columnAppDetails, "App Details");

		columnPrice = new TextColumn<MyApp>() {
			@Override
			public String getValue(MyApp object) {
				return object.overallPrice;
			}
		};
		appsTable.addColumn(columnPrice, "Price");

		columnDownloads = new TextColumn<MyApp>() {
			@Override
			public String getValue(MyApp object) {
				return (object.ranks != null) ? object.overallDownloads : "-";
			}
		};
		appsTable.addColumn(columnDownloads, "Downloads");

		columnRevenue = new TextColumn<MyApp>() {
			@Override
			public String getValue(MyApp object) {
				return (object.ranks != null) ? object.overallRevenue : "-";
			}
		};
		appsTable.addColumn(columnRevenue, "Revenue");

		ImageResourceCell imgIapCell = new ImageResourceCell();
		columnIap = new Column<MyApp, ImageResource>(imgIapCell) {
			@Override
			public ImageResource getValue(MyApp object) {
				// if (object.item.properties.){ TODO IAP image
				return Images.INSTANCE.greenTick();
				// }else{
				// return Images.INSTANCE.;
				// }

			}
		};
		appsTable.addColumn(columnIap, "IAP");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.FilterEventHandler#filterParamChanged(java.lang.String, java.lang.Object, java.lang.Object)
	 */
	@Override
	public <T> void filterParamChanged(String name, T currentValue, T previousValue) {
		appsTable.setVisibleRangeAndClearData(appsTable.getVisibleRange(), false);
		MyAppsController.get().reset();
		PageType.MyAppsPageType.show("view", "all", FilterController.get().asMyAppsFilterString());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.FilterEventHandler#filterParamsChanged(io.reflection.app.client.controller.FilterController.Filter, java.util.Map)
	 */
	@Override
	public void filterParamsChanged(Filter currentFilter, Map<String, ?> previousValues) {
		appsTable.setVisibleRangeAndClearData(appsTable.getVisibleRange(), false);
		MyAppsController.get().reset();
		PageType.MyAppsPageType.show("view", "all", FilterController.get().asMyAppsFilterString());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.NavigationEventHandler#navigationChanged(io.reflection.app.client.controller.NavigationController.Stack,
	 * io.reflection.app.client.controller.NavigationController.Stack)
	 */
	@Override
	public void navigationChanged(Stack previous, Stack current) {}

}
