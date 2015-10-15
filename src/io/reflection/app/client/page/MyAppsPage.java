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
import io.reflection.app.client.controller.ItemController;
import io.reflection.app.client.controller.LinkedAccountController;
import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.controller.NavigationController.Stack;
import io.reflection.app.client.controller.RankController;
import io.reflection.app.client.controller.ServiceConstants;
import io.reflection.app.client.controller.SessionController;
import io.reflection.app.client.dataprovider.UserItemProvider;
import io.reflection.app.client.handler.NavigationEventHandler;
import io.reflection.app.client.helper.AnimationHelper;
import io.reflection.app.client.helper.ApiCallHelper;
import io.reflection.app.client.helper.FilterHelper;
import io.reflection.app.client.helper.FormHelper;
import io.reflection.app.client.helper.TooltipHelper;
import io.reflection.app.client.part.BootstrapGwtCellTable;
import io.reflection.app.client.part.ErrorPanel;
import io.reflection.app.client.part.datatypes.DateRange;
import io.reflection.app.client.part.datatypes.MyApp;
import io.reflection.app.client.part.myapps.MyAppsComingSoon;
import io.reflection.app.client.part.myapps.MyAppsNoLinkedAccountsPanel;
import io.reflection.app.client.popup.AddLinkedAccountPopup;
import io.reflection.app.client.res.Styles;
import io.reflection.app.client.res.Styles.ReflectionMainStyles;
import io.reflection.app.datatypes.shared.Item;
import io.reflection.app.datatypes.shared.User;
import io.reflection.app.shared.util.DataTypeHelper;

import java.util.Date;

import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author stefanocapuzzi
 * 
 */
public class MyAppsPage extends Page
		implements NavigationEventHandler, GetLinkedAccountsEventHandler, GetLinkedAccountItemsEventHandler, GetSalesRanksEventHandler {

	private static MyAppsPageUiBinder uiBinder = GWT.create(MyAppsPageUiBinder.class);

	interface MyAppsPageUiBinder extends UiBinder<Widget, MyAppsPage> {}

	UserItemProvider userItemProvider = new UserItemProvider();

	@UiField(provided = true) CellTable<MyApp> myAppsTable = new CellTable<MyApp>(ServiceConstants.STEP_VALUE, BootstrapGwtCellTable.INSTANCE);

	private SafeHtmlHeader rankHeader = new SafeHtmlHeader(SafeHtmlUtils.fromTrustedString("Rank " + AnimationHelper.getSorterSvg()));
	private SafeHtmlHeader appDetailsHeader = new SafeHtmlHeader(SafeHtmlUtils.fromTrustedString("App Details " + AnimationHelper.getSorterSvg()));
	private SafeHtmlHeader priceHeader = new SafeHtmlHeader(SafeHtmlUtils.fromTrustedString("Price " + AnimationHelper.getSorterSvg()));
	private SafeHtmlHeader downloadsHeader = new SafeHtmlHeader(SafeHtmlUtils.fromTrustedString("Downloads " + AnimationHelper.getSorterSvg()));
	private SafeHtmlHeader revenueHeader = new SafeHtmlHeader(SafeHtmlUtils.fromTrustedString("Revenue " + AnimationHelper.getSorterSvg()));
	private SafeHtmlHeader iapHeader = new SafeHtmlHeader(
			SafeHtmlUtils.fromTrustedString("<span class=\"js-tooltip\" data-tooltip=\"In App Purchases\">IAP</span>"));

	// @UiField(provided = true) SimplePager simplePager = new SimplePager(false, false);

	@UiField Selector accountName;
	@UiField Selector appStore;
	@UiField Selector country;
	@UiField DateSelector dateSelector;
	@UiField Button applyFilters;

	@UiField Button viewAllBtn;
	@UiField SpanElement viewAllSpan;

	@UiField ErrorPanel errorPanel;
	// @UiField MyAppsNoAppsPanel noAppsPanel;
	@UiField MyAppsNoLinkedAccountsPanel noLinkedAccountsPanel;
	@UiField MyAppsComingSoon comingSoonPanel;

	public static final String COMING_FROM_PARAMETER = "myapps";
	private String previousFilter;
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
	private AddLinkedAccountPopup addLinkedAccountPopup = new AddLinkedAccountPopup();

	public MyAppsPage() {
		initWidget(uiBinder.createAndBindUi(this));

		FilterHelper.addStores(appStore, true);
		FilterHelper.addCountries(country, SessionController.get().isAdmin());

		if (!SessionController.get().isAdmin()) {
			dateSelector.disableBefore(ApiCallHelper.getUTCDate(2015, 4, 30));
		}
		dateSelector.addFixedRanges(FilterHelper.getDefaultDateRanges());

		// Reset linked account id in filter, to avoid problems after refreshing the page
		FilterController.get().getFilter().setLinkedAccountId(0L);

		FilterController.get().setListType(OVERALL_LIST_TYPE);

		createColumns();

		// noAppsPanel.addClickHandler(new ClickHandler() {
		//
		// @Override
		// public void onClick(ClickEvent event) {
		// User user = SessionController.get().getLoggedInUser();
		// if (user != null) {
		// PageType.UsersPageType.show(PageType.LinkedAccountsPageType.toString(), user.id.toString());
		// }
		// }
		// });

		noLinkedAccountsPanel.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				User user = SessionController.get().getLoggedInUser();
				if (user != null) {
					addLinkedAccountPopup.show("Link an Account", null);
				}
			}
		});

		myAppsTable.setLoadingIndicator(AnimationHelper.getMyAppsLoadingIndicator(4));
		myAppsTable.getTableLoadingSection().addClassName(style.tableBodyLoading());

		userItemProvider.addDataDisplay(myAppsTable);
		if (!LinkedAccountController.get().linkedAccountsFetched()) { // After refresh or the user didn't visit the linked accounts page
			loadingBar.show("Getting linked accounts...");
		}

		// simplePager.setDisplay(appsTableDesktop);

		setFiltersEnabled(false);
		accountName.setEnabled(false);

		myAppsTable.addLoadingStateChangeHandler(new LoadingStateChangeEvent.Handler() {

			@Override
			public void onLoadingStateChanged(LoadingStateChangeEvent event) {
				if (event.getLoadingState() == LoadingState.LOADED) {
					myAppsTable.setStyleName(style.tableLinkedAccountsDisabled(), myAppsTable.getRowCount() == 0);
				}
			}
		});

		updateSelectorsFromFilter();

		TooltipHelper.updateHelperTooltip();

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
				priceHeader.setHeaderStyleNames(style.canBeSorted() + " " + style.columnHiddenMobile());
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
					priceHeader.setHeaderStyleNames(style.canBeSorted() + " " + style.columnHiddenMobile() + " "
							+ (event.isSortAscending() ? style.isAscending() : style.isDescending()));
				} else if (event.getColumn() == columnDownloads) {
					userItemProvider.sortByDownloads(event.isSortAscending());
					downloadsHeader.setHeaderStyleNames(style.canBeSorted() + " " + (event.isSortAscending() ? style.isAscending() : style.isDescending()));
				} else if (event.getColumn() == columnRevenue) {
					userItemProvider.sortByRevenue(event.isSortAscending());
					revenueHeader.setHeaderStyleNames(style.canBeSorted() + " " + (event.isSortAscending() ? style.isAscending() : style.isDescending()));
				}
				myAppsTable.setRowData(0, userItemProvider.getList());

				TooltipHelper.updateHelperTooltip();
			}
		};

		myAppsTable.addColumnSortHandler(columnSortHandler);

		final SafeHtml loaderInline = AnimationHelper.getLoaderInlineSafeHTML();

		columnRank = new Column<MyApp, SafeHtml>(new SafeHtmlCell()) {
			@Override
			public SafeHtml getValue(MyApp object) {
				if (object.overallPosition != null) {
					if (object.overallPosition.equals(MyApp.UNKNOWN_VALUE)) {
						return SafeHtmlUtils.fromTrustedString(
								"<span class=\"js-tooltip whats-this-tooltip-icon-static\" data-tooltip=\"No data available\">-</span>");
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
		myAppsTable.addColumn(columnRank, rankHeader);
		columnRank.setSortable(true);

		columnAppDetails = new Column<MyApp, Item>(new MiniAppCell()) {
			@Override
			public Item getValue(MyApp object) {
				return object.item;
			}
		};
		columnAppDetails.setCellStyleNames(style.mhxte6ciA());

		myAppsTable.addColumn(columnAppDetails, appDetailsHeader);
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
		columnPrice.setCellStyleNames(style.mhxte6ciA() + " " + style.columnHiddenMobile());
		priceHeader.setHeaderStyleNames(style.canBeSorted() + " " + style.columnHiddenMobile());
		myAppsTable.addColumn(columnPrice, priceHeader);
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
		myAppsTable.addColumn(columnDownloads, downloadsHeader);
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
		myAppsTable.addColumn(columnRevenue, revenueHeader);
		columnRevenue.setSortable(true);

		columnIap = new Column<MyApp, SafeHtml>(new SafeHtmlCell()) {

			private final String IAP_YES_HTML = "<span class=\"" + style.refIconBefore() + " " + style.refIconBeforeCheck() + "\"></span>";
			private final String IAP_NO_HTML = "<span></span>";

			@Override
			public SafeHtml getValue(MyApp object) {
				return (object.item != null) ? SafeHtmlUtils.fromSafeConstant(DataTypeHelper.itemIapState(object.item, IAP_YES_HTML, IAP_NO_HTML,
						"<span class=\"js-tooltip js-tooltip--right js-tooltip--right--no-pointer-padding " + style.whatsThisTooltipIcon() + "\" data-tooltip=\"No data available\"></span>")) : loaderInline;
			}

		};
		columnIap.setCellStyleNames(style.mhxte6ciA() + " " + style.columnHiddenMobile());
		iapHeader.setHeaderStyleNames(style.columnHiddenMobile());
		myAppsTable.addColumn(columnIap, iapHeader);

		myAppsTable.addColumnStyleName(0, style.rankColumn());
		myAppsTable.addColumnStyleName(1, style.appDetailsColumn());
		myAppsTable.addColumnStyleName(2, style.priceColumn() + " " + style.columnHiddenMobile());
		myAppsTable.addColumnStyleName(3, style.downloadsColumn());
		myAppsTable.addColumnStyleName(4, style.revenueColumn());
		myAppsTable.addColumnStyleName(5, style.iapColumn() + " " + style.columnHiddenMobile());

	}

	/**
	 * Excluded linked account filter
	 * 
	 * @param enabled
	 */
	private void setFiltersEnabled(boolean enabled) {
		country.setEnabled(enabled);
		appStore.setEnabled(enabled);
		dateSelector.setEnabled(enabled);
		applyFilters.setEnabled(enabled);
	}

	public void fillAccountNameList() {
		if (accountName.getItemCount() > 0) {
			accountName.clear();
		}
		FilterHelper.addLinkedAccounts(accountName);
	}

	@UiHandler("accountName")
	void onAccountNameChanged(ChangeEvent event) {
		FilterController.get().getFilter().setLinkedAccountId(Long.valueOf(accountName.getSelectedValue()));
		if (LinkedAccountController.get().hasLinkedAccounts()) {
			PageType.UsersPageType.show(PageType.MyAppsPageType.toString(), user.id.toString(), FilterController.get().asMyAppsFilterString());
		}
	}

	@UiHandler("applyFilters")
	void onApplyFiltersClicked(ClickEvent event) {
		event.preventDefault();
		boolean updateData = false;
		if (updateData = updateData || !FilterController.get().getFilter().getCountryA2Code().equals(country.getSelectedValue())) {
			FilterController.get().setCountry(country.getSelectedValue());
		}
		if (updateData = updateData || !FilterController.get().getFilter().getStoreA3Code().equals(appStore.getSelectedValue())) {
			FilterController.get().setStore(appStore.getSelectedValue());
		}
		if (updateData = updateData
				|| !CalendarUtil.isSameDate(new Date(FilterController.get().getFilter().getEndTime().longValue()), dateSelector.getDateBoxToValue())
				|| !CalendarUtil.isSameDate(new Date(FilterController.get().getFilter().getStartTime().longValue()), dateSelector.getDateBoxFromValue())) {
			FilterController.get().getFilter().setEndTime(dateSelector.getDateBoxToValue().getTime());
			FilterController.get().getFilter().setStartTime(dateSelector.getDateBoxFromValue().getTime());
			dateSelector.setValue(new DateRange(dateSelector.getDateBoxFromValue(), dateSelector.getDateBoxToValue()), false);
		}
		if (updateData && LinkedAccountController.get().hasLinkedAccounts()) {
			PageType.UsersPageType.show(PageType.MyAppsPageType.toString(), user.id.toString(), FilterController.get().asMyAppsFilterString());
		}
	}

	private void updateSelectorsFromFilter() {
		FilterController fc = FilterController.get();
		// if (fc.getFilter().getLinkedAccountId() > 0) {
		accountName.setSelectedIndex(FormHelper.getItemIndex(accountName, fc.getFilter().getLinkedAccountId().toString()));
		// }
		appStore.setSelectedIndex(FormHelper.getItemIndex(appStore, fc.getFilter().getStoreA3Code()));
		DateRange range = new DateRange();
		range.setFrom(fc.getStartDate());
		range.setTo(fc.getEndDate());
		dateSelector.setValue(range);
		country.setSelectedIndex(FormHelper.getItemIndex(country, fc.getFilter().getCountryA2Code()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.NavigationEventHandler#navigationChanged(io.reflection.app.client.controller.NavigationController.Stack,
	 * io.reflection.app.client.controller.NavigationController.Stack)
	 */
	@Override
	public void navigationChanged(Stack previous, Stack current) {

		user = SessionController.get().getLoggedInUser();

		// Linked accounts retrieved in LinkedAccountPage but not here, or Check if Added or deleted a linked account
		if ((linkedAccountsCount == -1 && LinkedAccountController.get().linkedAccountsFetched())
				|| linkedAccountsCount != LinkedAccountController.get().getLinkedAccountsCount()) {
			linkedAccountsCount = LinkedAccountController.get().getLinkedAccountsCount();
			if (linkedAccountsCount > 0) {
				fillAccountNameList();
				FilterController.get().setLinkedAccount(LinkedAccountController.get().getAllLinkedAccounts().get(0).id);
				accountName.setEnabled(true);
				accountName.setSelectedIndex(FormHelper.getItemIndex(accountName, FilterController.get().getFilter().getLinkedAccountId().toString()));
				RankController.get().cancelRequestSalesRanks();
				ItemController.get().fetchLinkedAccountItems();
				loadingBar.show("Getting apps...");
				loadingBar.setProgressiveStatus(33);
				previousFilter = FilterController.get().asMyAppsFilterString();
			} else {
				userItemProvider.reset();
				userItemProvider.updateRowCount(0, true);
			}
		}

		if (previousFilter == null) {
			previousFilter = FilterController.get().asMyAppsFilterString();
		}

		if (linkedAccountsCount > 0) {
			accountName.setEnabled(true);
			if (!previousFilter.equals(FilterController.get().asMyAppsFilterString())) {
				updateSelectorsFromFilter();
				// simplePager.setPageStart(0);
				userItemProvider.reset();
				setFiltersEnabled(false);
				viewAllBtn.setVisible(false);
				RankController.get().cancelRequestSalesRanks();
				ItemController.get().fetchLinkedAccountItems();
				if (loadingBar.getText().equals("Getting linked accounts...")) {
					loadingBar.setText("Getting apps...");
				} else {
					loadingBar.show("Getting apps...");
				}
				loadingBar.setProgressiveStatus(33);
				errorPanel.setVisible(false);
				comingSoonPanel.setVisible(false);
				myAppsTable.setVisible(true);
				rankHeader.setHeaderStyleNames(style.canBeSorted() + " " + style.mhxte6cIF());
				appDetailsHeader.setHeaderStyleNames(style.canBeSorted());
				priceHeader.setHeaderStyleNames(style.canBeSorted() + " " + style.columnHiddenMobile());
				downloadsHeader.setHeaderStyleNames(style.canBeSorted());
				revenueHeader.setHeaderStyleNames(style.canBeSorted());
				// myAccountSidePanel.setUser(user);
				previousFilter = FilterController.get().asMyAppsFilterString();
			}
		} else {
			accountName.setEnabled(false);
			setFiltersEnabled(false);
		}
		noLinkedAccountsPanel.setVisible(linkedAccountsCount == 0);

	}

	@UiHandler("viewAllBtn")
	void onViewAllButtonClicked(ClickEvent event) {
		if (((Button) event.getSource()).isEnabled()) {
			if (myAppsTable.getVisibleItemCount() == ServiceConstants.STEP_VALUE) {
				myAppsTable.setVisibleRange(0, Integer.MAX_VALUE);
				viewAllSpan.setInnerText("View Less Apps");
				TooltipHelper.updateHelperTooltip();
			} else {
				myAppsTable.setVisibleRange(0, ServiceConstants.STEP_VALUE);
				viewAllSpan.setInnerText("View All Apps");
			}
		}
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
			if (accountName.getItemCount() == 0) {
				fillAccountNameList();
			}
			linkedAccountsCount = LinkedAccountController.get().getLinkedAccountsCount();
			if (linkedAccountsCount > 0) {
				FilterController.get().getFilter().setLinkedAccountId(LinkedAccountController.get().getAllLinkedAccounts().get(0).id);
				accountName.setEnabled(true);
				accountName.setSelectedIndex(FormHelper.getItemIndex(accountName, FilterController.get().getFilter().getLinkedAccountId().toString()));
				PageType.UsersPageType.show(PageType.MyAppsPageType.toString(), user.id.toString(), FilterController.get().asMyAppsFilterString());
			} else {
				loadingBar.hide(true);
				userItemProvider.reset();
				userItemProvider.updateRowCount(0, true);
				accountName.setEnabled(false);
				myAppsTable.setVisible(false);
				noLinkedAccountsPanel.setVisible(true);
			}
		} else {
			loadingBar.hide(false);
			userItemProvider.reset();
			userItemProvider.updateRowCount(0, true);
			accountName.setEnabled(false);
			myAppsTable.setVisible(false);
			errorPanel.setVisible(true);
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
		loadingBar.hide(false);
		userItemProvider.reset();
		userItemProvider.updateRowCount(0, true);
		accountName.setEnabled(false);
		myAppsTable.setVisible(false);
		errorPanel.setVisible(true);
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
			if (output.items != null) {
				appDetailsHeader.setHeaderStyleNames(style.canBeSorted() + " " + style.isAscending());
				columnAppDetails.setDefaultSortAscending(false);
				if (output.items.size() > ServiceConstants.STEP_VALUE) {
					viewAllBtn.setVisible(true);
				}
				loadingBar.setProgressiveStatus("Getting data...", 66);
			} else {
				loadingBar.hide(true); // Since there are no Apps to display, show success status
				myAppsTable.setVisible(false);
				comingSoonPanel.setVisible(true);
			}
		} else {
			loadingBar.hide(false);
			errorPanel.setVisible(true);
			myAppsTable.setVisible(false);
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
		loadingBar.hide(false);
		userItemProvider.reset();
		setFiltersEnabled(true);
		myAppsTable.setVisible(false);
		errorPanel.setVisible(true);
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
			loadingBar.hide(true);
		} else {
			loadingBar.hide(false);
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
		loadingBar.hide(false);
	}

}
