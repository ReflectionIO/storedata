//
//  MyAppsPage.java
//  storedata
//
//  Created by Stefano Capuzzi on 7 Mar 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page;

import static io.reflection.app.client.controller.FilterController.OVERALL_LIST_TYPE;
import io.reflection.app.api.core.shared.call.GetLinkedAccountItemsRequest;
import io.reflection.app.api.core.shared.call.GetLinkedAccountItemsResponse;
import io.reflection.app.api.core.shared.call.GetLinkedAccountsRequest;
import io.reflection.app.api.core.shared.call.GetLinkedAccountsResponse;
import io.reflection.app.api.core.shared.call.event.GetLinkedAccountItemsEventHandler;
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
import io.reflection.app.client.page.part.MyAccountSidePanel;
import io.reflection.app.client.page.part.MyAppsTopPanel;
import io.reflection.app.client.part.BootstrapGwtCellTable;
import io.reflection.app.client.part.BootstrapGwtDatePicker;
import io.reflection.app.client.part.SimplePager;
import io.reflection.app.client.part.datatypes.MyApp;
import io.reflection.app.client.res.Styles;
import io.reflection.app.datatypes.shared.Item;
import io.reflection.app.datatypes.shared.User;
import io.reflection.app.shared.util.DataTypeHelper;

import java.util.Map;

import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author stefanocapuzzi
 * 
 */
public class MyAppsPage extends Page implements FilterEventHandler, NavigationEventHandler, GetLinkedAccountsEventHandler, GetLinkedAccountItemsEventHandler {

	private static MyAppsPageUiBinder uiBinder = GWT.create(MyAppsPageUiBinder.class);

	interface MyAppsPageUiBinder extends UiBinder<Widget, MyAppsPage> {}

	interface MyAppsPageStyle extends CssResource {
		String red();

		String green();

		String silver();
	}

	@UiField MyAppsPageStyle style;

	@UiField(provided = true) CellTable<MyApp> appsTable = new CellTable<MyApp>(ServiceConstants.STEP_VALUE, BootstrapGwtCellTable.INSTANCE);
	@UiField SimplePager simplePager;

	@UiField MyAppsTopPanel myAppsTopPanel;
	@UiField MyAccountSidePanel myAccountSidePanel;

	private User user = SessionController.get().getLoggedInUser();

	long linkedAccountsCount = -1;

	// Columns
	private TextColumn<MyApp> columnRank;
	private Column<MyApp, Item> columnAppDetails;
	private TextColumn<MyApp> columnPrice;
	private TextColumn<MyApp> columnDownloads;
	private TextColumn<MyApp> columnRevenue;
	private Column<MyApp, SafeHtml> columnIap;

	public MyAppsPage() {
		initWidget(uiBinder.createAndBindUi(this));

		BootstrapGwtDatePicker.INSTANCE.styles().ensureInjected();

		Styles.INSTANCE.reflection().ensureInjected();

		FilterController.get().setListType(OVERALL_LIST_TYPE);

		createColumns();

		appsTable.setEmptyTableWidget(new HTMLPanel("No Apps found!"));
		MyAppsController.get().addDataDisplay(appsTable);
		simplePager.setDisplay(appsTable);
		FilterController.get().reset();
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
		register(EventController.get().addHandlerToSource(GetLinkedAccountsEventHandler.TYPE, LinkedAccountController.get(), this));

		// boolean hasPermission = SessionController.get().loggedInUserHas(SessionController.);

		// pager.setVisible(hasPermission);
		// redirect.setVisible(!hasPermission);

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
				return (object.overallPrice != null) ? object.overallPrice : "-";
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

		columnIap = new Column<MyApp, SafeHtml>(new SafeHtmlCell()) {

			private final String IAP_DONT_KNOW_HTML = "<span class=\"glyphicon glyphicon-question-sign " + style.silver() + "\"></span>";
			private final String IAP_YES_HTML = "<span class=\"glyphicon glyphicon-ok-sign " + style.green() + "\"></span>";
			private final String IAP_NO_HTML = "<span class=\"glyphicon glyphicon-remove-sign " + style.red() + "\"></span>";

			@Override
			public SafeHtml getValue(MyApp object) {
				return SafeHtmlUtils.fromSafeConstant(DataTypeHelper.itemIapState(object.item, IAP_YES_HTML, IAP_NO_HTML, IAP_DONT_KNOW_HTML));
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
		if (LinkedAccountController.get().hasLinkedAccounts() && LinkedAccountController.get().getLinkedAccountsCount() > 0) {
			MyAppsController.get().reset();
			MyAppsController.get().fetchLinkedAccountItems();
		}
		myAccountSidePanel.getMyAppsLink().setTargetHistoryToken(
				PageType.UsersPageType.asTargetHistoryToken(PageType.MyAppsPageType.toString(), user.id.toString(), FilterController.get()
						.asMyAppsFilterString()));
		PageType.UsersPageType.show(PageType.MyAppsPageType.toString(), user.id.toString(), FilterController.get().asMyAppsFilterString());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.FilterEventHandler#filterParamsChanged(io.reflection.app.client.controller.FilterController.Filter, java.util.Map)
	 */
	@Override
	public void filterParamsChanged(Filter currentFilter, Map<String, ?> previousValues) {
		if (LinkedAccountController.get().hasLinkedAccounts() && LinkedAccountController.get().getLinkedAccountsCount() > 0) {
			MyAppsController.get().reset();
			MyAppsController.get().fetchLinkedAccountItems();
		}
		myAccountSidePanel.getMyAppsLink().setTargetHistoryToken(
				PageType.UsersPageType.asTargetHistoryToken(PageType.MyAppsPageType.toString(), user.id.toString(), FilterController.get()
						.asMyAppsFilterString()));
		PageType.UsersPageType.show(PageType.MyAppsPageType.toString(), user.id.toString(), FilterController.get().asMyAppsFilterString());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.NavigationEventHandler#navigationChanged(io.reflection.app.client.controller.NavigationController.Stack,
	 * io.reflection.app.client.controller.NavigationController.Stack)
	 */
	@Override
	public void navigationChanged(Stack previous, Stack current) {

		myAccountSidePanel.setMyAppsLinkActive();

		user = SessionController.get().getLoggedInUser();

		if (user != null) {
			myAccountSidePanel.getLinkedAccountsLink().setTargetHistoryToken(
					PageType.UsersPageType.asTargetHistoryToken(PageType.LinkedAccountsPageType.toString(), user.id.toString()));

			myAccountSidePanel.getCreatorNameLink().setInnerText(user.company);

			myAccountSidePanel.getPersonalDetailsLink().setTargetHistoryToken(
					PageType.UsersPageType.asTargetHistoryToken(PageType.ChangeDetailsPageType.toString(), user.id.toString()));

			myAccountSidePanel.getChangePasswordLink().setTargetHistoryToken(
					PageType.UsersPageType.asTargetHistoryToken(PageType.ChangePasswordPageType.toString(), user.id.toString()));
		}

		String currentFilter = FilterController.get().asMyAppsFilterString();
		if (currentFilter != null && currentFilter.length() > 0) {
			if (user != null) {
				myAccountSidePanel.getMyAppsLink().setTargetHistoryToken(
						PageType.UsersPageType.asTargetHistoryToken(PageType.MyAppsPageType.toString(), user.id.toString(), FilterController.get()
								.asMyAppsFilterString()));
			}
		}

		// Linked accounts retrieved in LinkedAccountPage but not here, or Check if Added or deleted a linked account
		if ((linkedAccountsCount == -1 && LinkedAccountController.get().hasLinkedAccounts())
				|| linkedAccountsCount != LinkedAccountController.get().getLinkedAccountsCount()) {
			myAppsTopPanel.fillAccountNameList();
			linkedAccountsCount = LinkedAccountController.get().getLinkedAccountsCount();
			if (LinkedAccountController.get().getLinkedAccountsCount() > 0) {
				FilterController.get().setLinkedAccount(LinkedAccountController.get().getAllLinkedAccounts().get(0).id);
			} else {
				MyAppsController.get().reset();
			}
		}

		myAppsTopPanel.updateFromFilter();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.core.shared.call.event.GetLinkedAccountsEventHandler#getLinkedAccountsSuccess(io.reflection.app.api.core.shared.call.
	 * GetLinkedAccountsRequest, io.reflection.app.api.core.shared.call.GetLinkedAccountsResponse)
	 */
	@Override
	public void getLinkedAccountsSuccess(GetLinkedAccountsRequest input, GetLinkedAccountsResponse output) {
		if (output.status == StatusType.StatusTypeSuccess) {
			if (output.pager.totalCount != null) {
				if (LinkedAccountController.get().hasLinkedAccounts()) {
					myAppsTopPanel.fillAccountNameList();
					linkedAccountsCount = LinkedAccountController.get().getLinkedAccountsCount();
					if (LinkedAccountController.get().getLinkedAccountsCount() > 0) {
						FilterController.get().setLinkedAccount(LinkedAccountController.get().getAllLinkedAccounts().get(0).id);
					} else {
						MyAppsController.get().reset();
					}
				} else { // No linked accounts associated with this user
					MyAppsController.get().reset();
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.core.shared.call.event.GetLinkedAccountsEventHandler#getLinkedAccountsFailure(io.reflection.app.api.core.shared.call.
	 * GetLinkedAccountsRequest, java.lang.Throwable)
	 */
	@Override
	public void getLinkedAccountsFailure(GetLinkedAccountsRequest input, Throwable caught) {
		MyAppsController.get().reset();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.core.shared.call.event.GetLinkedAccountItemsEventHandler#getLinkedAccountItemsSuccess(io.reflection.app.api.core.shared.call.
	 * GetLinkedAccountItemsRequest, io.reflection.app.api.core.shared.call.GetLinkedAccountItemsResponse)
	 */
	@Override
	public void getLinkedAccountItemsSuccess(GetLinkedAccountItemsRequest input, GetLinkedAccountItemsResponse output) {
		if (output.status == StatusType.StatusTypeSuccess) {
			if (MyAppsController.get().getUserItemsCount() > output.pager.count) {
				simplePager.setVisible(true);
			} else {
				simplePager.setVisible(false);
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.core.shared.call.event.GetLinkedAccountItemsEventHandler#getLinkedAccountItemsFailure(io.reflection.app.api.core.shared.call.
	 * GetLinkedAccountItemsRequest, java.lang.Throwable)
	 */
	@Override
	public void getLinkedAccountItemsFailure(GetLinkedAccountItemsRequest input, Throwable caught) {
		MyAppsController.get().reset();

	}

}
