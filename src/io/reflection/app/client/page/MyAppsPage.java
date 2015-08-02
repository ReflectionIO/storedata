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
import io.reflection.app.api.core.shared.call.GetSalesRanksRequest;
import io.reflection.app.api.core.shared.call.GetSalesRanksResponse;
import io.reflection.app.api.core.shared.call.event.GetLinkedAccountItemsEventHandler;
import io.reflection.app.api.core.shared.call.event.GetLinkedAccountsEventHandler;
import io.reflection.app.api.core.shared.call.event.GetSalesRanksEventHandler;
import io.reflection.app.client.DefaultEventBus;
import io.reflection.app.client.cell.MiniAppCell;
import io.reflection.app.client.component.DateSelector;
import io.reflection.app.client.component.LoadingBar;
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
import io.reflection.app.client.helper.TooltipHelper;
import io.reflection.app.client.part.BootstrapGwtCellTable;
import io.reflection.app.client.part.datatypes.DateRange;
import io.reflection.app.client.part.datatypes.MyApp;
import io.reflection.app.client.part.myapps.MyAppsEmptyTable;
import io.reflection.app.client.res.Styles;
import io.reflection.app.client.res.Styles.ReflectionMainStyles;
import io.reflection.app.datatypes.shared.Item;
import io.reflection.app.datatypes.shared.User;
import io.reflection.app.shared.util.DataTypeHelper;

import java.util.Map;

import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
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
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.LoadingStateChangeEvent;
import com.google.gwt.user.cellview.client.LoadingStateChangeEvent.LoadingState;
import com.google.gwt.user.cellview.client.SafeHtmlHeader;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author stefanocapuzzi
 * 
 */
public class MyAppsPage extends Page implements FilterEventHandler, NavigationEventHandler, GetLinkedAccountsEventHandler, GetLinkedAccountItemsEventHandler,
		GetSalesRanksEventHandler {

	private static MyAppsPageUiBinder uiBinder = GWT.create(MyAppsPageUiBinder.class);

	interface MyAppsPageUiBinder extends UiBinder<Widget, MyAppsPage> {}

	UserItemProvider userItemProvider = new UserItemProvider();

	@UiField(provided = true) CellTable<MyApp> appsTableDesktop = new CellTable<MyApp>(ServiceConstants.STEP_VALUE, BootstrapGwtCellTable.INSTANCE);
	@UiField(provided = true) CellTable<MyApp> appsTableMobile = new CellTable<MyApp>(ServiceConstants.STEP_VALUE, BootstrapGwtCellTable.INSTANCE);
	private String sorterSvg = "<svg version=\"1.1\" x=\"0px\" y=\"0px\" viewBox=\"0 0 7 10\" enable-background=\"new 0 0 7 10\" xml:space=\"preserve\" class=\"sort-svg\"><path class=\"ascending\" d=\"M0.4,4.1h6.1c0.1,0,0.2,0,0.3-0.1C7,3.9,7,3.8,7,3.7c0-0.1,0-0.2-0.1-0.3L3.8,0.1C3.7,0,3.6,0,3.5,0C3.4,0,3.3,0,3.2,0.1L0.1,3.3C0,3.4,0,3.5,0,3.7C0,3.8,0,3.9,0.1,4C0.2,4.1,0.3,4.1,0.4,4.1z\"></path><path class=\"descending\" d=\"M6.6,5.9H0.4c-0.1,0-0.2,0-0.3,0.1C0,6.1,0,6.2,0,6.3c0,0.1,0,0.2,0.1,0.3l3.1,3.2C3.3,10,3.4,10,3.5,10c0.1,0,0.2,0,0.3-0.1l3.1-3.2C7,6.6,7,6.5,7,6.3C7,6.2,7,6.1,6.9,6C6.8,5.9,6.7,5.9,6.6,5.9z\"></path></svg>";
	private SafeHtmlHeader rankHeader = new SafeHtmlHeader(SafeHtmlUtils.fromTrustedString("Rank " + sorterSvg));
	private SafeHtmlHeader appDetailsHeader = new SafeHtmlHeader(SafeHtmlUtils.fromTrustedString("App Details " + sorterSvg));
	private SafeHtmlHeader priceHeader = new SafeHtmlHeader(SafeHtmlUtils.fromTrustedString("Price " + sorterSvg));
	private SafeHtmlHeader downloadsHeader = new SafeHtmlHeader(SafeHtmlUtils.fromTrustedString("Downloads " + sorterSvg));
	private SafeHtmlHeader revenueHeader = new SafeHtmlHeader(SafeHtmlUtils.fromTrustedString("Revenue " + sorterSvg));

	// @UiField(provided = true) SimplePager simplePager = new SimplePager(false, false);

	@UiField Selector accountName;
	@UiField Selector category;
	@UiField Selector appStore;
	@UiField Selector country;
	@UiField DateSelector dateSelector;
	@UiField HTMLPanel waitingForDataPanel;
	@UiField Button viewAllBtn;
	@UiField SpanElement viewAllSpan;

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

	private LoadingBar loadingBar = new LoadingBar(true);

	public MyAppsPage() {
		initWidget(uiBinder.createAndBindUi(this));

		if (!SessionController.get().isLoggedInUserAdmin()) {
			category.setTooltip("This field is currently locked but will soon be editable as we integrate more data");
			appStore.setTooltip("This field is currently locked but will soon be editable as we integrate more data");
			country.setTooltip("This field is currently locked but will soon be editable as we integrate more data");
		}

		FilterHelper.addCategories(category, SessionController.get().isLoggedInUserAdmin());
		FilterHelper.addStores(appStore, SessionController.get().isLoggedInUserAdmin());
		FilterHelper.addCountries(country, SessionController.get().isLoggedInUserAdmin());

		dateSelector.addFixedRanges(FilterHelper.getDefaultDateRanges());

		// Reset linked account id in filter, to avoid problems after refreshing the page
		FilterController.get().getFilter().setLinkedAccountId(0L);

		FilterController.get().setListType(OVERALL_LIST_TYPE);

		createColumns();

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

		HTMLPanel emptyMobileTableWidget = new HTMLPanel("");
		emptyMobileTableWidget.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		emptyMobileTableWidget.getElement().getStyle().setHeight(100.0, Unit.PX);
		emptyMobileTableWidget.getElement().getStyle().setPaddingTop(35.0, Unit.PX);
		appsTableMobile.setEmptyTableWidget(emptyMobileTableWidget);

		appsTableDesktop.setLoadingIndicator(AnimationHelper.createLoadingIndicator(5, 6));
		appsTableMobile.setLoadingIndicator(new HTMLPanel(AnimationHelper.getLoaderInlineSafeHTML()));
		userItemProvider.addDataDisplay(appsTableDesktop);

		// simplePager.setDisplay(appsTableDesktop);
		// simplePager.setDisplay(appsTableMobile);

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

		TooltipHelper.updateHelperTooltip();

		loadingBar.show();
		if (LinkedAccountController.get().linkedAccountsFetched()) {
			loadingBar.setText("Getting apps...");
		} else {
			loadingBar.setText("Getting linked accounts...");
		}
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
		register(DefaultEventBus.get().addHandlerToSource(GetSalesRanksEventHandler.TYPE, RankController.get(), this));

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.page.Page#onDetach()
	 */
	@Override
	protected void onDetach() {
		super.onDetach();
		loadingBar.reset();
	}

	/**
	 * 
	 */
	private void createColumns() {

		ListHandler<MyApp> columnSortHandler = new ListHandler<MyApp>(userItemProvider.getList()) {
			/*
			 * (non-Javadoc)
			 * 
			 * @see com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler#onColumnSort(com.google.gwt.user.cellview.client.ColumnSortEvent)
			 */
			@Override
			public void onColumnSort(ColumnSortEvent event) {
				super.onColumnSort(event);
				if (!columnAppDetails.isDefaultSortAscending()) {
					columnAppDetails.setDefaultSortAscending(true);
				}
				rankHeader.setHeaderStyleNames(style.canBeSorted() + " " + style.mhxte6cIF());
				appDetailsHeader.setHeaderStyleNames(style.canBeSorted());
				priceHeader.setHeaderStyleNames(style.canBeSorted());
				downloadsHeader.setHeaderStyleNames(style.canBeSorted());
				revenueHeader.setHeaderStyleNames(style.canBeSorted());
				if (event.getColumn() == columnRank) {
					userItemProvider.sortByRank(event.isSortAscending());
					rankHeader.setHeaderStyleNames(style.canBeSorted() + " " + (event.isSortAscending() ? style.isAscending() : style.isDescending()));
				} else if (event.getColumn() == columnAppDetails) {
					userItemProvider.sortByAppDetails(event.isSortAscending());
					appDetailsHeader.setHeaderStyleNames(style.canBeSorted() + " " + (event.isSortAscending() ? style.isAscending() : style.isDescending()));
				} else if (event.getColumn() == columnPrice) {
					userItemProvider.sortByPrice(event.isSortAscending());
					priceHeader.setHeaderStyleNames(style.canBeSorted() + " " + (event.isSortAscending() ? style.isAscending() : style.isDescending()));
				} else if (event.getColumn() == columnDownloads) {
					userItemProvider.sortByDownloads(event.isSortAscending());
					downloadsHeader.setHeaderStyleNames(style.canBeSorted() + " " + (event.isSortAscending() ? style.isAscending() : style.isDescending()));
				} else if (event.getColumn() == columnRevenue) {
					userItemProvider.sortByRevenue(event.isSortAscending());
					revenueHeader.setHeaderStyleNames(style.canBeSorted() + " " + (event.isSortAscending() ? style.isAscending() : style.isDescending()));
				}
				appsTableDesktop.setRowData(0, userItemProvider.getList());
				appsTableMobile.setRowData(0, userItemProvider.getList());
				TooltipHelper.updateHelperTooltip();
			}
		};

		appsTableDesktop.addColumnSortHandler(columnSortHandler);
		appsTableMobile.addColumnSortHandler(columnSortHandler);

		final SafeHtml loaderInline = AnimationHelper.getLoaderInlineSafeHTML();

		columnRank = new Column<MyApp, SafeHtml>(new SafeHtmlCell()) {
			@Override
			public SafeHtml getValue(MyApp object) {
				if (object.overallPosition != null) {
					if (object.overallPosition.equals(MyApp.UNKNOWN_VALUE)) {
						return SafeHtmlUtils.fromTrustedString("<span class=\"js-tooltip\" data-tooltip=\"No data available\">-</span>");
					} else {
						return SafeHtmlUtils.fromSafeConstant(object.overallPosition);
					}
				} else {
					return loaderInline;
				}
			}
		};
		columnRank.setCellStyleNames(style.mhxte6ciA() + " " + style.mhxte6cID());
		rankHeader.setHeaderStyleNames(style.canBeSorted() + " " + style.mhxte6cIF());
		appsTableDesktop.addColumn(columnRank, rankHeader);
		appsTableMobile.addColumn(columnRank, rankHeader);
		columnRank.setSortable(true);

		columnAppDetails = new Column<MyApp, Item>(new MiniAppCell()) {
			@Override
			public Item getValue(MyApp object) {
				return object.item;
			}
		};
		columnAppDetails.setCellStyleNames(style.mhxte6ciA());

		appsTableDesktop.addColumn(columnAppDetails, appDetailsHeader);
		columnAppDetails.setSortable(true);

		columnPrice = new Column<MyApp, SafeHtml>(new SafeHtmlCell()) {
			@Override
			public SafeHtml getValue(MyApp object) {
				if (object.overallPrice != null) {
					if (object.overallPrice.equals(MyApp.UNKNOWN_VALUE)) {
						return SafeHtmlUtils.fromTrustedString("<span class=\"js-tooltip\" data-tooltip=\"No data available\">-</span>");
					} else {
						return SafeHtmlUtils.fromSafeConstant(object.overallPrice);
					}
				} else {
					return loaderInline;
				}
			}
		};
		columnPrice.setCellStyleNames(style.mhxte6ciA());
		priceHeader.setHeaderStyleNames(style.canBeSorted());
		appsTableDesktop.addColumn(columnPrice, priceHeader);
		columnPrice.setSortable(true);

		columnDownloads = new Column<MyApp, SafeHtml>(new SafeHtmlCell()) {
			@Override
			public SafeHtml getValue(MyApp object) {
				if (object.overallDownloads != null) {
					if (object.overallDownloads.equals(MyApp.UNKNOWN_VALUE)) {
						return SafeHtmlUtils.fromTrustedString("<span class=\"js-tooltip\" data-tooltip=\"No data available\">-</span>");
					} else {
						return SafeHtmlUtils.fromSafeConstant(object.overallDownloads);
					}
				} else {
					return loaderInline;
				}
			}
		};
		columnDownloads.setCellStyleNames(style.mhxte6ciA());
		downloadsHeader.setHeaderStyleNames(style.canBeSorted());
		appsTableDesktop.addColumn(columnDownloads, downloadsHeader);
		columnDownloads.setSortable(true);

		columnRevenue = new Column<MyApp, SafeHtml>(new SafeHtmlCell()) {
			@Override
			public SafeHtml getValue(MyApp object) {
				if (object.overallRevenue != null) {
					if (object.overallRevenue.equals(MyApp.UNKNOWN_VALUE)) {
						return SafeHtmlUtils.fromTrustedString("<span class=\"js-tooltip\" data-tooltip=\"No data available\">-</span>");
					} else {
						return SafeHtmlUtils.fromSafeConstant(object.overallRevenue);
					}
				} else {
					return loaderInline;
				}
			}
		};
		columnRevenue.setCellStyleNames(style.mhxte6ciA());
		revenueHeader.setHeaderStyleNames(style.canBeSorted());
		appsTableDesktop.addColumn(columnRevenue, revenueHeader);
		columnRevenue.setSortable(true);

		columnIap = new Column<MyApp, SafeHtml>(new SafeHtmlCell()) {

			private final String IAP_YES_HTML = "<span class=\"" + style.refIconBefore() + " " + style.refIconBeforeCheck() + "\"></span>";
			private final String IAP_NO_HTML = "<span></span>";

			@Override
			public SafeHtml getValue(MyApp object) {
				return (object.item != null) ? SafeHtmlUtils.fromSafeConstant(DataTypeHelper.itemIapState(object.item, IAP_YES_HTML, IAP_NO_HTML,
						"<span class=\"js-tooltip " + style.whatsThisTooltipIcon() + "\" data-tooltip=\"No data available\"></span>")) : loaderInline;
			}

		};
		columnIap.setCellStyleNames(style.mhxte6ciA());
		appsTableDesktop.addColumn(columnIap,
				new SafeHtmlHeader(SafeHtmlUtils.fromTrustedString("<span class=\"js-tooltip\" data-tooltip=\"In App Purchases\">IAP</span>")));

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
		if (accountName.getItemCount() > 0) {
			accountName.clear();
		}
		FilterHelper.addLinkedAccounts(accountName);
	}

	@UiHandler("viewAllBtn")
	void onViewAllButtonClicked(ClickEvent event) {
		if (((Button) event.getSource()).isEnabled()) {
			if (appsTableDesktop.getVisibleItemCount() == ServiceConstants.STEP_VALUE) {
				appsTableDesktop.setVisibleRange(0, Integer.MAX_VALUE);
				appsTableMobile.setVisibleRange(0, Integer.MAX_VALUE);
				viewAllSpan.setInnerText("View Less Apps");
				TooltipHelper.updateHelperTooltip();
			} else {
				appsTableDesktop.setVisibleRange(0, ServiceConstants.STEP_VALUE);
				appsTableMobile.setVisibleRange(0, ServiceConstants.STEP_VALUE);
				viewAllSpan.setInnerText("View All Apps");
			}
		}
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
			// simplePager.setPageStart(0);
			userItemProvider.reset();
			setFiltersEnabled(false);
			viewAllBtn.setVisible(false);
			ItemController.get().fetchLinkedAccountItems();
			loadingBar.setProgressiveStatus("Getting apps...", 33);
			loadingBar.show("Getting apps...");
			rankHeader.setHeaderStyleNames(style.canBeSorted() + " " + style.mhxte6cIF());
			appDetailsHeader.setHeaderStyleNames(style.canBeSorted());
			priceHeader.setHeaderStyleNames(style.canBeSorted());
			downloadsHeader.setHeaderStyleNames(style.canBeSorted());
			revenueHeader.setHeaderStyleNames(style.canBeSorted());
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
			// simplePager.setPageStart(0);
			userItemProvider.reset();
			setFiltersEnabled(false);
			viewAllBtn.setVisible(false);
			ItemController.get().fetchLinkedAccountItems();
			loadingBar.setProgressiveStatus("Getting apps...", 33);
			loadingBar.show("Getting apps...");
			rankHeader.setHeaderStyleNames(style.canBeSorted() + " " + style.mhxte6cIF());
			appDetailsHeader.setHeaderStyleNames(style.canBeSorted());
			priceHeader.setHeaderStyleNames(style.canBeSorted());
			downloadsHeader.setHeaderStyleNames(style.canBeSorted());
			revenueHeader.setHeaderStyleNames(style.canBeSorted());
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
					if (accountName.getItemCount() == 0) {
						fillAccountNameList();
					}
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
			appDetailsHeader.setHeaderStyleNames(style.canBeSorted() + " " + style.isAscending());
			columnAppDetails.setDefaultSortAscending(false);
			if (output.items.size() > ServiceConstants.STEP_VALUE) {
				viewAllBtn.setVisible(true);
			}
			loadingBar.setProgressiveStatus("Getting data...", 66);
		} else {

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.reflection.app.api.core.shared.call.event.GetSalesRanksEventHandler#getSalesRanksSuccess(io.reflection.app.api.core.shared.call.GetSalesRanksRequest,
	 * io.reflection.app.api.core.shared.call.GetSalesRanksResponse)
	 */
	@Override
	public void getSalesRanksSuccess(GetSalesRanksRequest input, GetSalesRanksResponse output) {
		if (output.status == StatusType.StatusTypeSuccess) {
			loadingBar.hide();
		} else {

		}

		if (!userItemProvider.getDataDisplays().contains(appsTableMobile)) { // Avoid initial double call to server
			userItemProvider.addDataDisplay(appsTableMobile);
		}
		TooltipHelper.updateHelperTooltip();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.reflection.app.api.core.shared.call.event.GetSalesRanksEventHandler#getSalesRanksFailure(io.reflection.app.api.core.shared.call.GetSalesRanksRequest,
	 * java.lang.Throwable)
	 */
	@Override
	public void getSalesRanksFailure(GetSalesRanksRequest input, Throwable caught) {
		if (!userItemProvider.getDataDisplays().contains(appsTableMobile)) { // Avoid initial double call to server
			userItemProvider.addDataDisplay(appsTableMobile);
		}
	}

}
