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
import io.reflection.app.api.core.shared.call.event.GetSalesRanksEventHandler;
import io.reflection.app.client.DefaultEventBus;
import io.reflection.app.client.cell.MiniAppCell;
import io.reflection.app.client.component.DateSelector;
import io.reflection.app.client.component.Selector;
import io.reflection.app.client.controller.FilterController;
import io.reflection.app.client.controller.FilterController.Filter;
import io.reflection.app.client.controller.ItemController;
import io.reflection.app.client.controller.LinkedAccountController;
import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.controller.NavigationController.Stack;
import io.reflection.app.client.controller.RankController;
import io.reflection.app.client.controller.ServiceConstants;
import io.reflection.app.client.controller.SessionController;
import io.reflection.app.client.dataprovider.UserItemProvider;
import io.reflection.app.client.handler.FilterEventHandler;
import io.reflection.app.client.handler.NavigationEventHandler;
import io.reflection.app.client.helper.AnimationHelper;
import io.reflection.app.client.helper.FilterHelper;
import io.reflection.app.client.helper.FormHelper;
import io.reflection.app.client.part.BootstrapGwtCellTable;
import io.reflection.app.client.part.SimplePager;
import io.reflection.app.client.part.datatypes.DateRange;
import io.reflection.app.client.part.datatypes.MyApp;
import io.reflection.app.client.part.myapps.MyAppsEmptyTable;
import io.reflection.app.client.res.Images;
import io.reflection.app.client.res.Styles;
import io.reflection.app.client.res.Styles.ReflectionMainStyles;
import io.reflection.app.datatypes.shared.Item;
import io.reflection.app.datatypes.shared.User;
import io.reflection.app.shared.util.DataTypeHelper;

import java.util.Map;

import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.LoadingStateChangeEvent;
import com.google.gwt.user.cellview.client.LoadingStateChangeEvent.LoadingState;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author stefanocapuzzi
 * 
 */
public class MyAppsPage extends Page implements FilterEventHandler, NavigationEventHandler, GetLinkedAccountsEventHandler, GetLinkedAccountItemsEventHandler {

	private static MyAppsPageUiBinder uiBinder = GWT.create(MyAppsPageUiBinder.class);

	interface MyAppsPageUiBinder extends UiBinder<Widget, MyAppsPage> {}

	UserItemProvider userItemProvider = new UserItemProvider();

	@UiField(provided = true) CellTable<MyApp> appsTableDesktop = new CellTable<MyApp>(ServiceConstants.STEP_VALUE, BootstrapGwtCellTable.INSTANCE);
	@UiField(provided = true) CellTable<MyApp> appsTableMobile = new CellTable<MyApp>(ServiceConstants.STEP_VALUE, BootstrapGwtCellTable.INSTANCE);
	@UiField(provided = true) SimplePager simplePager = new SimplePager(false, false);

	@UiField Selector accountName;
	@UiField Selector category;
	@UiField Selector appStore;
	@UiField Selector country;
	@UiField DateSelector dateSelector;

	public static final String COMING_FROM_PARAMETER = "myapps";

	private MyAppsEmptyTable myAppsEmptyTable = new MyAppsEmptyTable();

	private User user;

	long linkedAccountsCount = -1;

	// Columns
	private Column<MyApp, SafeHtml> columnRank;
	private Column<MyApp, Item> columnAppDetails;
	private Column<MyApp, SafeHtml> columnPrice;
	private Column<MyApp, SafeHtml> columnDownloads;
	private Column<MyApp, SafeHtml> columnRevenue;
	private Column<MyApp, SafeHtml> columnIap;

	private ReflectionMainStyles style = Styles.STYLES_INSTANCE.reflectionMainStyle();

	public MyAppsPage() {
		initWidget(uiBinder.createAndBindUi(this));

		FilterHelper.addCategories(category, SessionController.get().isLoggedInUserAdmin());
		FilterHelper.addStores(appStore, SessionController.get().isLoggedInUserAdmin());
		FilterHelper.addCountries(country, SessionController.get().isLoggedInUserAdmin());

		dateSelector.addFixedRanges(FilterHelper.getDefaultDateRanges());

		// Reset linked account id in filter, to avoid problems after refreshing the page
		FilterController.get().getFilter().setLinkedAccountId(0L);

		FilterController.get().setListType(OVERALL_LIST_TYPE);

		createColumns();

		myAppsEmptyTable = new MyAppsEmptyTable();
		myAppsEmptyTable.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				User user = SessionController.get().getLoggedInUser();
				if (user != null) {
					PageType.UsersPageType.show(PageType.LinkedAccountsPageType.toString(), user.id.toString());
				}
			}
		});
		appsTableDesktop.setEmptyTableWidget(myAppsEmptyTable);

		appsTableDesktop.setLoadingIndicator(new Image(Images.INSTANCE.preloader()));
		userItemProvider.addDataDisplay(appsTableDesktop);
		userItemProvider.addDataDisplay(appsTableMobile);

		simplePager.setDisplay(appsTableDesktop);
		simplePager.setDisplay(appsTableMobile);

		setFiltersEnabled(false);
		accountName.setEnabled(false);

		appsTableDesktop.addLoadingStateChangeHandler(new LoadingStateChangeEvent.Handler() {

			@Override
			public void onLoadingStateChanged(LoadingStateChangeEvent event) {
				if (event.getLoadingState() == LoadingState.LOADED) {
					appsTableDesktop.setStyleName(style.tableLinkedAccountsDisabled(), appsTableDesktop.getRowCount() == 0);
				}
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.Composite#onAttach()
	 */
	@Override
	protected void onAttach() {
		super.onAttach();

		register(DefaultEventBus.get().addHandlerToSource(NavigationEventHandler.TYPE, NavigationController.get(), this));
		register(DefaultEventBus.get().addHandlerToSource(FilterEventHandler.TYPE, FilterController.get(), this));
		register(DefaultEventBus.get().addHandlerToSource(GetLinkedAccountsEventHandler.TYPE, LinkedAccountController.get(), this));
		register(DefaultEventBus.get().addHandlerToSource(GetLinkedAccountItemsEventHandler.TYPE, ItemController.get(), this));
		register(DefaultEventBus.get().addHandlerToSource(GetLinkedAccountItemsEventHandler.TYPE, ItemController.get(), userItemProvider));
		register(DefaultEventBus.get().addHandlerToSource(GetSalesRanksEventHandler.TYPE, RankController.get(), userItemProvider));

		// boolean hasPermission = SessionController.get().loggedInUserHas(SessionController.);

		// pager.setVisible(hasPermission);
		// redirect.setVisible(!hasPermission);

	}

	/**
	 * 
	 */
	private void createColumns() {

		final SafeHtml loaderInline = AnimationHelper.getLoaderInlineSafeHTML();

		columnRank = new Column<MyApp, SafeHtml>(new SafeHtmlCell()) {
			@Override
			public SafeHtml getValue(MyApp object) {
				return (object.overallPosition != null) ? SafeHtmlUtils.fromSafeConstant(object.overallPosition) : loaderInline;
			}
		};
		columnRank.setCellStyleNames(Styles.STYLES_INSTANCE.reflectionMainStyle().mhxte6ciA() + " " + Styles.STYLES_INSTANCE.reflectionMainStyle().mhxte6cID());
		TextHeader rankHeader = new TextHeader("Rank");
		rankHeader.setHeaderStyleNames(Styles.STYLES_INSTANCE.reflectionMainStyle().mhxte6cIF());
		appsTableDesktop.addColumn(columnRank, rankHeader);
		appsTableMobile.addColumn(columnRank, rankHeader);

		columnAppDetails = new Column<MyApp, Item>(new MiniAppCell()) {
			@Override
			public Item getValue(MyApp object) {
				return object.item;
			}
		};
		columnAppDetails.setCellStyleNames(Styles.STYLES_INSTANCE.reflectionMainStyle().mhxte6ciA());
		appsTableDesktop.addColumn(columnAppDetails, "App Details");

		columnPrice = new Column<MyApp, SafeHtml>(new SafeHtmlCell()) {
			@Override
			public SafeHtml getValue(MyApp object) {
				return (object.overallPrice != null) ? SafeHtmlUtils.fromSafeConstant(object.overallPrice) : loaderInline;
			}
		};
		columnPrice.setCellStyleNames(Styles.STYLES_INSTANCE.reflectionMainStyle().mhxte6ciA());
		appsTableDesktop.addColumn(columnPrice, "Price");

		columnDownloads = new Column<MyApp, SafeHtml>(new SafeHtmlCell()) {
			@Override
			public SafeHtml getValue(MyApp object) {
				return (object.overallDownloads != null) ? SafeHtmlUtils.fromSafeConstant(object.overallDownloads) : loaderInline;
			}
		};
		columnDownloads.setCellStyleNames(Styles.STYLES_INSTANCE.reflectionMainStyle().mhxte6ciA());
		appsTableDesktop.addColumn(columnDownloads, "Downloads");

		columnRevenue = new Column<MyApp, SafeHtml>(new SafeHtmlCell()) {
			@Override
			public SafeHtml getValue(MyApp object) {
				return (object.overallRevenue != null) ? SafeHtmlUtils.fromSafeConstant(object.overallRevenue) : loaderInline;
			}
		};
		columnRevenue.setCellStyleNames(Styles.STYLES_INSTANCE.reflectionMainStyle().mhxte6ciA());
		appsTableDesktop.addColumn(columnRevenue, "Revenue");

		columnIap = new Column<MyApp, SafeHtml>(new SafeHtmlCell()) {

			private final String IAP_DONT_KNOW_HTML = "<span class=\"" + Styles.STYLES_INSTANCE.reflectionMainStyle().refIconBefore() + " "
					+ Styles.STYLES_INSTANCE.reflectionMainStyle().refIconBeforeMinus() + "\"></span>";
			private final String IAP_YES_HTML = "<span class=\"" + Styles.STYLES_INSTANCE.reflectionMainStyle().refIconBefore() + " "
					+ Styles.STYLES_INSTANCE.reflectionMainStyle().refIconBeforeCheck() + "\"></span>";
			private final String IAP_NO_HTML = "<span></span>";

			@Override
			public SafeHtml getValue(MyApp object) {
				return (object.item != null) ? SafeHtmlUtils.fromSafeConstant(DataTypeHelper.itemIapState(object.item, IAP_YES_HTML, IAP_NO_HTML,
						IAP_DONT_KNOW_HTML)) : loaderInline;
			}

		};
		columnIap.setCellStyleNames(Styles.STYLES_INSTANCE.reflectionMainStyle().mhxte6ciA());
		appsTableDesktop.addColumn(columnIap, "IAP");

		appsTableDesktop.addColumnStyleName(0, style.rankColumn());
		appsTableDesktop.addColumnStyleName(1, style.appDetailsColumn());
		appsTableDesktop.addColumnStyleName(2, style.priceColumn());
		appsTableDesktop.addColumnStyleName(3, style.downloadsColumn());
		appsTableDesktop.addColumnStyleName(4, style.revenueColumn());
		appsTableDesktop.addColumnStyleName(5, style.iapColumn());

		appsTableMobile.addColumnStyleName(0, style.rankColumn());

	}

	@UiHandler("accountName")
	void onAccountNameChanged(ChangeEvent event) {
		FilterController.get().setLinkedAccount(Long.valueOf(accountName.getValue(accountName.getSelectedIndex())));
	}

	@UiHandler("category")
	void onCategoryValueChanged(ChangeEvent event) {
		FilterController.get().setCategory(Long.valueOf(category.getValue(category.getSelectedIndex())));
	}

	@UiHandler("appStore")
	void onAppStoreValueChanged(ChangeEvent event) {
		FilterController.get().setStore(appStore.getValue(appStore.getSelectedIndex()));
	}

	@UiHandler("country")
	void onCountryValueChanged(ChangeEvent event) {
		FilterController.get().setCountry(country.getValue(country.getSelectedIndex()));
	}

	private void updateFromFilter() {
		FilterController fc = FilterController.get();

		category.setSelectedIndex(FormHelper.getItemIndex(category, fc.getFilter().getCategoryId().toString()));
		appStore.setSelectedIndex(FormHelper.getItemIndex(appStore, fc.getFilter().getStoreA3Code()));
		DateRange range = new DateRange();

		range.setFrom(fc.getStartDate());
		range.setTo(fc.getEndDate());
		dateSelector.setValue(range);

		country.setSelectedIndex(FormHelper.getItemIndex(country, fc.getFilter().getCountryA2Code()));

		if (fc.getFilter().getLinkedAccountId() > 0) {
			accountName.setSelectedIndex(FormHelper.getItemIndex(accountName, fc.getFilter().getLinkedAccountId().toString()));
		}

	}

	@UiHandler("dateSelector")
	void onDateRangeValueChanged(ValueChangeEvent<DateRange> event) {
		FilterController fc = FilterController.get();

		fc.start();
		fc.setEndDate(event.getValue().getTo());
		fc.setStartDate(event.getValue().getFrom());
		fc.commit();
	}

	public void fillAccountNameList() {
		accountName.clear();
		FilterHelper.addLinkedAccounts(accountName);
	}

	/**
	 * Excluded linked account filter
	 * 
	 * @param enabled
	 */
	private void setFiltersEnabled(boolean enabled) {
		// TODO delete condition when the user can select the filters
		if (SessionController.get().isLoggedInUserAdmin()) {
			category.setEnabled(enabled);
			appStore.setEnabled(enabled);
			country.setEnabled(enabled);
		}
		dateSelector.setEnabled(enabled);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.FilterEventHandler#filterParamChanged(java.lang.String, java.lang.Object, java.lang.Object)
	 */
	@Override
	public <T> void filterParamChanged(String name, T currentValue, T previousValue) {
		if (LinkedAccountController.get().hasLinkedAccounts()) { // There are linked accounts
			myAppsEmptyTable.setNoDataAccounts(false);
			simplePager.setPageStart(0);
			userItemProvider.reset();
			setFiltersEnabled(false);
			ItemController.get().fetchLinkedAccountItems();
		}
		// myAccountSidePanel.setUser(user);
		PageType.UsersPageType.show(PageType.MyAppsPageType.toString(), user.id.toString(), FilterController.get().asMyAppsFilterString());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.FilterEventHandler#filterParamsChanged(io.reflection.app.client.controller.FilterController.Filter, java.util.Map)
	 */
	@Override
	public void filterParamsChanged(Filter currentFilter, Map<String, ?> previousValues) {
		if (LinkedAccountController.get().hasLinkedAccounts()) { // There are linked accounts
			myAppsEmptyTable.setNoDataAccounts(false);
			simplePager.setPageStart(0);
			userItemProvider.reset();
			setFiltersEnabled(false);
			ItemController.get().fetchLinkedAccountItems();
		}
		// myAccountSidePanel.setUser(user);
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

		// myAccountSidePanel.setActive(getPageType());
		//
		user = SessionController.get().getLoggedInUser();
		//
		// if (user != null) {
		// myAccountSidePanel.setUser(user);
		// }

		// Linked accounts retrieved in LinkedAccountPage but not here, or Check if Added or deleted a linked account
		if ((linkedAccountsCount == -1 && LinkedAccountController.get().linkedAccountsFetched())
				|| linkedAccountsCount != LinkedAccountController.get().getLinkedAccountsCount()) {
			fillAccountNameList();
			linkedAccountsCount = LinkedAccountController.get().getLinkedAccountsCount();
			if (LinkedAccountController.get().getLinkedAccountsCount() > 0) {
				FilterController.get().setLinkedAccount(LinkedAccountController.get().getAllLinkedAccounts().get(0).id);
			} else {
				userItemProvider.reset();
				userItemProvider.updateRowCount(0, true);
			}
		}

		if (linkedAccountsCount > 0) {
			myAppsEmptyTable.setNoDataAccounts(false);
			accountName.setEnabled(true);
		} else {
			myAppsEmptyTable.setNoDataAccounts(true);
			accountName.setEnabled(false);
			setFiltersEnabled(false);
		}

		updateFromFilter();

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
				if (LinkedAccountController.get().linkedAccountsFetched()) {
					fillAccountNameList();
					linkedAccountsCount = LinkedAccountController.get().getLinkedAccountsCount();
					if (LinkedAccountController.get().getLinkedAccountsCount() > 0) {
						FilterController.get().setLinkedAccount(LinkedAccountController.get().getAllLinkedAccounts().get(0).id);
						accountName.setEnabled(true);
					} else {
						userItemProvider.reset();
						userItemProvider.updateRowCount(0, true);
						accountName.setEnabled(false);
					}
				} else { // No linked accounts associated with this user
					userItemProvider.reset();
				}
			}
		} else {
			userItemProvider.reset();
			userItemProvider.updateRowCount(0, true);
			accountName.setEnabled(false);
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
		userItemProvider.reset();
		userItemProvider.updateRowCount(0, true);
		accountName.setEnabled(false);
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
			if (ItemController.get().getUserItemsCount() > output.pager.count.longValue()) {
				simplePager.setVisible(true);
			} else {
				simplePager.setVisible(false);
			}
		}
		setFiltersEnabled(true);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.core.shared.call.event.GetLinkedAccountItemsEventHandler#getLinkedAccountItemsFailure(io.reflection.app.api.core.shared.call.
	 * GetLinkedAccountItemsRequest, java.lang.Throwable)
	 */
	@Override
	public void getLinkedAccountItemsFailure(GetLinkedAccountItemsRequest input, Throwable caught) {
		userItemProvider.reset();
		setFiltersEnabled(true);
	}

}
