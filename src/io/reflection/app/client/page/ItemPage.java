//
//  ItemPage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 10 Jan 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page;

import static io.reflection.app.client.controller.FilterController.DOWNLOADS_CHART_TYPE;
import static io.reflection.app.client.controller.FilterController.OVERALL_LIST_TYPE;
import static io.reflection.app.client.controller.FilterController.RANKING_CHART_TYPE;
import static io.reflection.app.client.controller.FilterController.REVENUE_CHART_TYPE;
import io.reflection.app.api.core.shared.call.GetItemRanksRequest;
import io.reflection.app.api.core.shared.call.GetItemRanksResponse;
import io.reflection.app.api.core.shared.call.GetItemSalesRanksRequest;
import io.reflection.app.api.core.shared.call.GetItemSalesRanksResponse;
import io.reflection.app.api.core.shared.call.GetLinkedAccountItemRequest;
import io.reflection.app.api.core.shared.call.GetLinkedAccountItemResponse;
import io.reflection.app.api.core.shared.call.event.GetItemRanksEventHandler;
import io.reflection.app.api.core.shared.call.event.GetItemSalesRanksEventHandler;
import io.reflection.app.api.core.shared.call.event.GetLinkedAccountItemEventHandler;
import io.reflection.app.client.DefaultEventBus;
import io.reflection.app.client.cell.ImageAndTextCell;
import io.reflection.app.client.cell.content.ConcreteImageAndText;
import io.reflection.app.client.controller.FilterController;
import io.reflection.app.client.controller.FilterController.Filter;
import io.reflection.app.client.controller.ItemController;
import io.reflection.app.client.controller.LinkedAccountController;
import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.controller.NavigationController.Stack;
import io.reflection.app.client.controller.RankController;
import io.reflection.app.client.controller.SessionController;
import io.reflection.app.client.controller.StoreController;
import io.reflection.app.client.handler.FilterEventHandler;
import io.reflection.app.client.handler.NavigationEventHandler;
import io.reflection.app.client.handler.TogglePanelEventHandler;
import io.reflection.app.client.helper.FormattingHelper;
import io.reflection.app.client.highcharts.Chart;
import io.reflection.app.client.highcharts.ChartHelper;
import io.reflection.app.client.highcharts.ChartHelper.RankType;
import io.reflection.app.client.highcharts.ChartHelper.XDataType;
import io.reflection.app.client.highcharts.ChartHelper.YDataType;
import io.reflection.app.client.page.part.ItemChart.RankingType;
import io.reflection.app.client.page.part.ItemSidePanel;
import io.reflection.app.client.page.part.ItemTopPanel;
import io.reflection.app.client.part.BootstrapGwtCellTable;
import io.reflection.app.client.part.datatypes.ItemRevenue;
import io.reflection.app.client.part.navigation.Header.PanelType;
import io.reflection.app.client.res.Images;
import io.reflection.app.client.res.flags.Styles;
import io.reflection.app.datatypes.shared.Item;
import io.reflection.app.datatypes.shared.Rank;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.Widget;
import com.willshex.gson.json.service.shared.StatusType;

public class ItemPage extends Page implements NavigationEventHandler, GetItemRanksEventHandler, GetItemSalesRanksEventHandler, FilterEventHandler,
		GetLinkedAccountItemEventHandler, TogglePanelEventHandler {

	private static ItemPageUiBinder uiBinder = GWT.create(ItemPageUiBinder.class);

	interface ItemPageUiBinder extends UiBinder<Widget, ItemPage> {}

	public static final int SELECTED_TAB_PARAMETER_INDEX = 1;

	@UiField ItemSidePanel sidePanel;
	@UiField ItemTopPanel topPanel;

	@UiField InlineHyperlink revenueLink;
	@UiField InlineHyperlink downloadsLink;
	@UiField InlineHyperlink rankingLink;
	private InlineHTML comingSoon = new InlineHTML("&nbsp;&nbsp;&nbsp;-&nbsp;&nbsp;&nbsp;Coming soon");
	@UiField LIElement revenueItem;
	@UiField LIElement downloadsItem;
	@UiField LIElement rankingItem;

	// @UiField ItemChart historyChart;

	// @UiField Preloader preloader;

	@UiField(provided = true) CellTable<ItemRevenue> revenueTable = new CellTable<ItemRevenue>(Integer.MAX_VALUE, BootstrapGwtCellTable.INSTANCE);

	private String internalId;
	private String comingPage;

	private RankingType rankingType;
	private RankType rankType;
	// private YAxisDataType dataType;
	private Item item;

	private Map<String, LIElement> tabs = new HashMap<String, LIElement>();

	private String selectedTab;

	private String filterContents;

	private ItemRevenue itemRevenuePlaceholder = new ItemRevenue();
	private List<ItemRevenue> tablePlaceholder = new ArrayList<ItemRevenue>();

	@UiField(provided = true) Chart chartRevenue = new Chart(XDataType.DateXAxisDataType, YDataType.RevenueYAxisDataType);
	@UiField(provided = true) Chart chartDownloads = new Chart(XDataType.DateXAxisDataType, YDataType.DownloadsYAxisDataType);
	@UiField(provided = true) Chart chartRank = new Chart(XDataType.DateXAxisDataType, YDataType.RankingYAxisDataType);

	public ItemPage() {
		initWidget(uiBinder.createAndBindUi(this));

		Styles.INSTANCE.flags().ensureInjected();

		tabs.put(REVENUE_CHART_TYPE, revenueItem);
		tabs.put(DOWNLOADS_CHART_TYPE, downloadsItem);
		tabs.put(RANKING_CHART_TYPE, rankingItem);
		comingSoon.getElement().getStyle().setFontSize(14.0, Unit.PX);
		setRevenueDownloadTabsEnabled(false);

		if (SessionController.get().isLoggedInUserAdmin()) {
			createColumns();
			RankController.get().getItemRevenueDataProvider().addDataDisplay(revenueTable);
		} else {
			revenueTable.removeFromParent();
		}

		tablePlaceholder.add(itemRevenuePlaceholder);

	}

	/**
	 * If enables, set the row with null values to force loading state
	 * 
	 * @param enabled
	 */
	private void setLoadingSpinnerEnabled(boolean enabled) {
		if (SessionController.get().isLoggedInUserAdmin()) {
			if (enabled) {
				revenueTable.setRowData(0, tablePlaceholder);
			} else {
				revenueTable.setRowCount(0, true);
			}
		}
	}

	private void createColumns() {

		final SafeHtml spinnerLoaderHTML = SafeHtmlUtils.fromSafeConstant("<img src=\"" + Images.INSTANCE.spinner().getSafeUri().asString() + "\"/>");

		Column<ItemRevenue, ConcreteImageAndText> countryColumn = new Column<ItemRevenue, ConcreteImageAndText>(new ImageAndTextCell<ConcreteImageAndText>()) {

			@Override
			public ConcreteImageAndText getValue(ItemRevenue object) {
				return (object.countryFlagStyleName != null && object.countryName != null) ? new ConcreteImageAndText(object.countryFlagStyleName,
						SafeHtmlUtils.fromSafeConstant(object.countryName)) : new ConcreteImageAndText("", SafeHtmlUtils.fromSafeConstant(""));
			}
		};

		// Column<ItemRevenue, PercentageProgress> percentageColumn = new Column<ItemRevenue, PercentageProgress>(new ProgressBarCell<PercentageProgress>()) {
		//
		// @Override
		// public PercentageProgress getValue(ItemRevenue object) {
		// return new PercentageProgress(object.percentage.floatValue());
		// }
		//
		// };

		Column<ItemRevenue, SafeHtml> paidColumn = new Column<ItemRevenue, SafeHtml>(new SafeHtmlCell()) {

			@Override
			public SafeHtml getValue(ItemRevenue object) {
				return (object.currency != null) ? SafeHtmlUtils
						.fromSafeConstant(FormattingHelper.asWholeMoneyString(object.currency, object.paid.floatValue())) : spinnerLoaderHTML;
			}
		};

		Column<ItemRevenue, SafeHtml> subscriptionColumn = new Column<ItemRevenue, SafeHtml>(new SafeHtmlCell()) {

			@Override
			public SafeHtml getValue(ItemRevenue object) {
				return (object.currency != null) ? SafeHtmlUtils.fromSafeConstant(FormattingHelper.asMoneyString(object.currency, 0.0f)) : spinnerLoaderHTML;
			}
		};

		Column<ItemRevenue, SafeHtml> iapColumn = new Column<ItemRevenue, SafeHtml>(new SafeHtmlCell()) {

			@Override
			public SafeHtml getValue(ItemRevenue object) {
				return (object.currency != null && object.iap != null) ? SafeHtmlUtils.fromSafeConstant(FormattingHelper.asWholeMoneyString(object.currency,
						object.iap.floatValue())) : spinnerLoaderHTML;
			}
		};

		Column<ItemRevenue, SafeHtml> totalColumn = new Column<ItemRevenue, SafeHtml>(new SafeHtmlCell()) {

			@Override
			public SafeHtml getValue(ItemRevenue object) {
				return (object.currency != null && object.total != null) ? SafeHtmlUtils.fromSafeConstant(FormattingHelper.asWholeMoneyString(object.currency,
						object.total.floatValue())) : spinnerLoaderHTML;
			}
		};

		revenueTable.addColumn(countryColumn, "Country");
		// TextHeader percentageHeader = new TextHeader("% total revenue");
		// revenue.addColumn(percentageColumn, percentageHeader);
		revenueTable.addColumn(paidColumn, "Paid");
		revenueTable.addColumn(subscriptionColumn, "Subscription");
		revenueTable.addColumn(iapColumn, "IAP");
		revenueTable.addColumn(totalColumn, "Total");
		revenueTable.setWidth("100%", true);
		revenueTable.setColumnWidth(countryColumn, 136.0, Unit.PX);
		revenueTable.setColumnWidth(paidColumn, 51.0, Unit.PX);
		revenueTable.setColumnWidth(subscriptionColumn, 51.0, Unit.PX);
		revenueTable.setColumnWidth(iapColumn, 51.0, Unit.PX);
		revenueTable.setColumnWidth(totalColumn, 51.0, Unit.PX);

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
		// register(EventController.get().addHandlerToSource(SearchForItemEventHandler.TYPE, ItemController.get(), this));
		register(DefaultEventBus.get().addHandlerToSource(FilterEventHandler.TYPE, FilterController.get(), this));
		register(DefaultEventBus.get().addHandlerToSource(GetItemRanksEventHandler.TYPE, RankController.get(), this));
		register(DefaultEventBus.get().addHandlerToSource(GetItemSalesRanksEventHandler.TYPE, RankController.get(), this));
		register(DefaultEventBus.get().addHandlerToSource(GetLinkedAccountItemEventHandler.TYPE, LinkedAccountController.get(), this));
		register(DefaultEventBus.get().addHandlerToSource(TogglePanelEventHandler.TYPE, NavigationController.get().getHeader(), this));

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.NavigationEventHandler#navigationChanged(io.reflection.app.client.controller.NavigationController.Stack,
	 * io.reflection.app.client.controller.NavigationController.Stack)
	 */
	@Override
	public void navigationChanged(Stack previous, Stack current) {
		if (isValidStack(current)) {

			comingPage = current.getParameter(2);

			String newInternalId = current.getParameter(0);
			boolean isNewDataRequired = false;

			// Item changed
			if (internalId == null || !internalId.equals(newInternalId)) {
				isNewDataRequired = true;

				internalId = newInternalId;

				if ((item = ItemController.get().lookupItem(internalId)) != null) {
					item.source = StoreController.get().getStore(FilterController.get().getFilter().getStoreA3Code()).a3Code;
					displayItemDetails(null);
				} else {
					// Coming from a refresh page
					item = new Item();
					item.internalId = internalId;
					item.source = StoreController.get().getStore(FilterController.get().getFilter().getStoreA3Code()).a3Code;
				}
			}

			Filter newFilter = FilterController.get().getFilter();
			String newFilterContents = newFilter.asItemFilterString();

			if (filterContents == null || !filterContents.equals(newFilterContents)) {
				filterContents = newFilterContents;

				RankingType newRankingType = RankingType.fromString(newFilter.getListType());
				if (rankingType == null || rankingType != newRankingType) {
					rankingType = newRankingType;
				}
				RankType newRankType = RankType.fromString(newFilter.getListType());
				if (rankType == null || rankType != newRankType) {
					rankType = newRankType;
				}
				isNewDataRequired = true;
			}

			switch (newFilter.getListType()) {
			case (FilterController.FREE_LIST_TYPE):
				rankingLink.setText("Free rank");
				break;
			case (FilterController.PAID_LIST_TYPE):
				rankingLink.setText("Paid rank");
				break;
			case (FilterController.GROSSING_LIST_TYPE):
				rankingLink.setText("Grossing rank");
				break;
			default:
				rankingLink.setText("Rank");
				break;
			}

			// revenueLink.setTargetHistoryToken(PageType.ItemPageType.asTargetHistoryToken(NavigationController.VIEW_ACTION_PARAMETER_VALUE, internalId,
			// REVENUE_CHART_TYPE, comingPage, filterContents));
			// downloadsLink.setTargetHistoryToken(PageType.ItemPageType.asTargetHistoryToken(NavigationController.VIEW_ACTION_PARAMETER_VALUE, internalId,
			// DOWNLOADS_CHART_TYPE, comingPage, filterContents));
			rankingLink.setTargetHistoryToken(PageType.ItemPageType.asTargetHistoryToken(NavigationController.VIEW_ACTION_PARAMETER_VALUE, internalId,
					RANKING_CHART_TYPE, comingPage, filterContents));

			boolean showAllData = true;
			if (!SessionController.get().isLoggedInUserAdmin() && !MyAppsPage.COMING_FROM_PARAMETER.equals(comingPage)) {
				setRevenueDownloadTabsEnabled(false);
				showAllData = false;
			} else {
				setRevenueDownloadTabsEnabled(true);
			}

			if (showAllData || (!showAllData && current.getParameter(1).equals(RANKING_CHART_TYPE))) {
				topPanel.updateFromFilter();
				String newSelectedTab = current.getParameter(SELECTED_TAB_PARAMETER_INDEX);
				// boolean isNewSelectedTab = false;
				if (selectedTab == null || !selectedTab.equals(newSelectedTab)) {
					selectedTab = newSelectedTab;
					refreshTabs();
					// isNewSelectedTab = true;
				}

				// if (isNewSelectedTab && !isNewDataRequired) {
				// chartRevenue.drawData();
				// chartDownloads.drawData();
				// chartRank.drawData();
				// }
				if (isNewDataRequired) {
					setLoadingSpinnerEnabled(true);
					sidePanel.setPriceInnerHTML(null);
					getHistoryChartData();
				}

				// TODO let be handled by tabs
				switch (YDataType.fromString(selectedTab)) {
				case RevenueYAxisDataType:
					chartRevenue.setVisible(true);
					chartDownloads.setVisible(false);
					chartRank.setVisible(false);
					break;
				case DownloadsYAxisDataType:
					chartDownloads.setVisible(true);
					chartRevenue.setVisible(false);
					chartRank.setVisible(false);
					break;
				case RankingYAxisDataType:
					chartRank.setVisible(true);
					chartRevenue.setVisible(false);
					chartDownloads.setVisible(false);
					break;
				}

			}

			// AlertBoxHelper.configureAlert(mAlertBox, AlertBoxType.SuccessAlertBoxType, false, "Item",
			// " - Normally we would display items details for (" + view + ").", false).setVisible(true);

		} else {
			// AlertBoxHelper.configureAlert(mAlertBox, AlertBoxType.DangerAlertBoxType, false, "Item", " - We did not find the requrested item!", false)
			// .setVisible(true);

			PageType.RanksPageType.show(NavigationController.VIEW_ACTION_PARAMETER_VALUE, OVERALL_LIST_TYPE, FilterController.get().asRankFilterString());
		}
	}

	private boolean isValidStack(Stack current) {
		return (current != null
				&& current.getParameterCount() >= 4
				&& PageType.ItemPageType.equals(current.getPage())
				&& NavigationController.VIEW_ACTION_PARAMETER_VALUE.equals(current.getAction())
				&& current.getParameter(0).matches("[0-9]+")
				&& (current.getParameter(1).equals(REVENUE_CHART_TYPE) || current.getParameter(1).equals(DOWNLOADS_CHART_TYPE) || current.getParameter(1)
						.equals(RANKING_CHART_TYPE))
				&& (current.getParameter(2).equals(RanksPage.COMING_FROM_PARAMETER) || current.getParameter(2).equals(MyAppsPage.COMING_FROM_PARAMETER)) && current
				.getParameter(3).startsWith(FilterController.ITEM_FILTER_KEY));
	}

	private void displayItemDetails(Rank rank) {

		sidePanel.setItem(item);

		if (rank != null) {
			sidePanel.setPrice(rank.currency, rank.price);
		} else {
			sidePanel.setPriceInnerHTML(null);
		}
	}

	// /*
	// * (non-Javadoc)
	// *
	// * @see
	// *
	// io.reflection.app.api.admin.shared.call.event.SearchForItemEventHandler#searchForItemSuccess(io.reflection.app.api.core.shared.call.SearchForItemRequest,
	// * io.reflection.app.api.core.shared.call.SearchForItemResponse)
	// */
	// @Override
	// public void searchForItemSuccess(SearchForItemRequest input, SearchForItemResponse output) {
	// boolean found = false;
	//
	// if (mItemInternalId != null && mItemInternalId.equals(input.query) && output.status == StatusType.StatusTypeSuccess) {
	//
	// // for now we don't lookup the item again... because it causes an infinite loop of lookup failure
	// if (output.items != null) {
	// for (Item item : output.items) {
	// if (mItemInternalId.equals(item.internalId)) {
	// displayItemDetails(item);
	//
	// refreshTabs();
	//
	// getHistoryChartData(item);
	//
	// found = true;
	// break;
	// }
	// }
	// }
	// }
	//
	// if (!found) {
	// AlertBoxHelper.configureAlert(mAlertBox, AlertBoxType.DangerAlertBoxType, false, "Item", " - We did not find the requrested item!", false)
	// .setVisible(true);
	// }
	// }
	//
	// /*
	// * (non-Javadoc)
	// *
	// * @see
	// *
	// io.reflection.app.api.admin.shared.call.event.SearchForItemEventHandler#searchForItemFailure(io.reflection.app.api.core.shared.call.SearchForItemRequest,
	// * java.lang.Throwable)
	// */
	// @Override
	// public void searchForItemFailure(SearchForItemRequest input, Throwable caught) {
	// if (mItemInternalId != null && mItemInternalId.equals(input.query)) {
	// AlertBoxHelper.configureAlert(mAlertBox, AlertBoxType.DangerAlertBoxType, false, "Item", " - We could not find the requrested item!", false)
	// .setVisible(true);
	// }
	// }

	private void refreshTabs() {
		for (String key : tabs.keySet()) {
			tabs.get(key).removeClassName("is-active");
		}

		tabs.get(selectedTab).addClassName("is-active");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.reflection.app.api.core.shared.call.event.GetItemRanksEventHandler#getItemRanksSuccess(io.reflection.app.api.core.shared.call.GetItemRanksRequest,
	 * io.reflection.app.api.core.shared.call.GetItemRanksResponse)
	 */
	@Override
	public void getItemRanksSuccess(GetItemRanksRequest input, GetItemRanksResponse output) {
		if (output != null && output.status == StatusType.StatusTypeSuccess) {
			if (output.ranks != null && output.ranks.size() > 0 && output.item != null) {

				setItemInfo(output.item);

				displayItemDetails(output.ranks.get(0));

				chartRank.setRankingType(rankType);
				chartRevenue.drawData(output.ranks, "revenue", ChartHelper.TYPE_AREA);
				chartDownloads.drawData(output.ranks, "downloads", ChartHelper.TYPE_AREA);
				chartRank.drawData(output.ranks, "rank", ChartHelper.TYPE_LINE);

				// historyChart.setMode(rankingType);
				// historyChart.setDataType(dataType);
				// // redraw the graph with the new data
				// historyChart.setData(output.item, output.ranks);

				// mAlertBox.dismiss();
			} else {
				sidePanel.setPriceInnerHTML("-");
				setLoadingSpinnerEnabled(false);
			}
		} else {
			sidePanel.setPriceInnerHTML("-");
			setLoadingSpinnerEnabled(false);
		}
		// preloader.hide();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.reflection.app.api.core.shared.call.event.GetItemRanksEventHandler#getItemRanksFailure(io.reflection.app.api.core.shared.call.GetItemRanksRequest,
	 * java.lang.Throwable)
	 */
	@Override
	public void getItemRanksFailure(GetItemRanksRequest input, Throwable caught) {
		// preloader.hide();
		sidePanel.setPriceInnerHTML("-");
		setLoadingSpinnerEnabled(false);

	}

	private void getHistoryChartData() {
		if (item != null) {
			// historyChart.setLoading(true);
			chartRevenue.setLoading(true);
			chartDownloads.setLoading(true);
			chartRank.setLoading(true);
			// preloader.show(true);
			RankController.get().cancelRequestItemRanks();
			RankController.get().cancelRequestItemSalesRanks();
			if (LinkedAccountController.get().getLinkedAccountItem(item) != null) {
				if (MyAppsPage.COMING_FROM_PARAMETER.equals(comingPage)) {
					RankController.get().fetchItemSalesRanks(item);
				} else {
					RankController.get().fetchItemRanks(item);
				}
			}
		} else {
			// item == null
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.FilterEventHandler#filterParamChanged(java.lang.String, java.lang.Object, java.lang.Object)
	 */
	@Override
	public <T> void filterParamChanged(String name, T currentValue, T previousValue) {
		if (NavigationController.get().getCurrentPage() == PageType.ItemPageType) {
			PageType.ItemPageType.show(NavigationController.VIEW_ACTION_PARAMETER_VALUE, internalId, selectedTab, comingPage, FilterController.get()
					.asItemFilterString());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.FilterEventHandler#filterParamsChanged(io.reflection.app.client.controller.FilterController.Filter, java.util.Map)
	 */
	@Override
	public void filterParamsChanged(Filter currentFilter, Map<String, ?> previousValues) {
		if (NavigationController.get().getCurrentPage() == PageType.ItemPageType) {
			PageType.ItemPageType.show(NavigationController.VIEW_ACTION_PARAMETER_VALUE, internalId, selectedTab, comingPage, FilterController.get()
					.asItemFilterString());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.core.shared.call.event.GetItemSalesRanksEventHandler#getItemSalesRanksSuccess(io.reflection.app.api.core.shared.call.
	 * GetItemSalesRanksRequest, io.reflection.app.api.core.shared.call.GetItemSalesRanksResponse)
	 */
	@Override
	public void getItemSalesRanksSuccess(GetItemSalesRanksRequest input, GetItemSalesRanksResponse output) {
		if (output != null && output.status == StatusType.StatusTypeSuccess) {
			if (output.ranks != null && output.ranks.size() > 0 && output.item != null) {

				setItemInfo(output.item);

				displayItemDetails(output.ranks.get(0));

				// historyChart.setMode(RankingType.PositionRankingType);

				chartRank.setRankingType(RankType.PositionRankingType);
				chartRevenue.drawData(output.ranks, "revenue", ChartHelper.TYPE_AREA);
				chartDownloads.drawData(output.ranks, "downloads", ChartHelper.TYPE_AREA);
				chartRank.drawData(output.ranks, "rank", ChartHelper.TYPE_LINE);

				// historyChart.setDataType(dataType);

				// redraw the graph with the new data
				// historyChart.setData(output.item, output.ranks);

			} else {
				sidePanel.setPriceInnerHTML("-");
				setLoadingSpinnerEnabled(false);
			}
		} else {
			sidePanel.setPriceInnerHTML("-");
			setLoadingSpinnerEnabled(false);
		}
		// preloader.hide();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.core.shared.call.event.GetItemSalesRanksEventHandler#getItemSalesRanksFailure(io.reflection.app.api.core.shared.call.
	 * GetItemSalesRanksRequest, java.lang.Throwable)
	 */
	@Override
	public void getItemSalesRanksFailure(GetItemSalesRanksRequest input, Throwable caught) {
		// preloader.hide();
		sidePanel.setPriceInnerHTML("-");
		setLoadingSpinnerEnabled(false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.core.shared.call.event.GetLinkedAccountItemEventHandler#getLinkedAccountItemSuccess(io.reflection.app.api.core.shared.call.
	 * GetLinkedAccountItemRequest, io.reflection.app.api.core.shared.call.GetLinkedAccountItemResponse)
	 */
	@Override
	public void getLinkedAccountItemSuccess(GetLinkedAccountItemRequest input, GetLinkedAccountItemResponse output) {
		if (output.status == StatusType.StatusTypeSuccess) {
			if (output.item == null) { // Not my app
				RankController.get().fetchItemRanks(item);
			} else {
				setItemInfo(output.item);
				sidePanel.setItem(item);
				if (MyAppsPage.COMING_FROM_PARAMETER.equals(comingPage)) {
					RankController.get().fetchItemSalesRanks(item);
				} else {
					RankController.get().fetchItemRanks(item);
				}
			}
		} else {
			sidePanel.setPriceInnerHTML("-");
			// preloader.hide();
			setLoadingSpinnerEnabled(false);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.core.shared.call.event.GetLinkedAccountItemEventHandler#getLinkedAccountItemFailure(io.reflection.app.api.core.shared.call.
	 * GetLinkedAccountItemRequest, java.lang.Throwable)
	 */
	@Override
	public void getLinkedAccountItemFailure(GetLinkedAccountItemRequest input, Throwable caught) {
		// preloader.hide();
		sidePanel.setPriceInnerHTML("-");
		setLoadingSpinnerEnabled(false);
	}

	private void setItemInfo(Item i) {
		if (i.name != null) {
			item.name = i.name;
		}
		if (i.creatorName != null) {
			item.creatorName = i.creatorName;
		}
		if (i.largeImage != null) {
			item.largeImage = i.largeImage;
		}
	}

	private void setRevenueDownloadTabsEnabled(boolean enable) {
		if (enable) {
			revenueLink.setHTML("Revenue");
			revenueLink.getElement().getStyle().setColor("#0e0e1f");
			revenueLink.getElement().getStyle().setCursor(Cursor.POINTER);
			revenueItem.getStyle().setCursor(Cursor.DEFAULT);
			downloadsLink.setHTML("Downloads");
			downloadsLink.getElement().getStyle().setColor("#0e0e1f");
			downloadsLink.getElement().getStyle().setCursor(Cursor.POINTER);
			downloadsItem.getStyle().setCursor(Cursor.DEFAULT);
			revenueLink.setTargetHistoryToken(PageType.ItemPageType.asTargetHistoryToken(NavigationController.VIEW_ACTION_PARAMETER_VALUE, internalId,
					REVENUE_CHART_TYPE, comingPage, filterContents));
			downloadsLink.setTargetHistoryToken(PageType.ItemPageType.asTargetHistoryToken(NavigationController.VIEW_ACTION_PARAMETER_VALUE, internalId,
					DOWNLOADS_CHART_TYPE, comingPage, filterContents));
			tabs.put(REVENUE_CHART_TYPE, revenueItem);
			tabs.put(DOWNLOADS_CHART_TYPE, downloadsItem);
		} else {
			revenueLink.setHTML("Revenue" + comingSoon);
			revenueLink.getElement().getStyle().setColor("lightgrey");
			revenueLink.getElement().getStyle().setCursor(Cursor.DEFAULT);
			revenueItem.getStyle().setCursor(Cursor.DEFAULT);
			downloadsLink.setHTML("Downloads" + comingSoon);
			downloadsLink.getElement().getStyle().setColor("lightgrey");
			downloadsLink.getElement().getStyle().setCursor(Cursor.DEFAULT);
			downloadsItem.getStyle().setCursor(Cursor.DEFAULT);
			revenueLink.setTargetHistoryToken(NavigationController.get().getStack().toString());
			downloadsLink.setTargetHistoryToken(NavigationController.get().getStack().toString());
			for (String key : tabs.keySet()) {
				tabs.get(key).removeClassName("is-active");
			}
			tabs.get(RANKING_CHART_TYPE).addClassName("is-active");
			tabs.remove(REVENUE_CHART_TYPE);
			tabs.remove(DOWNLOADS_CHART_TYPE);

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.TogglePanelEventHandler#leftPanelToggled(io.reflection.app.client.part.navigation.Header.PanelType, boolean,
	 * boolean)
	 */
	@Override
	public void panelToggled(PanelType panelType, boolean wasOpen, boolean isOpen) {
		if (PanelType.PanelLeftMenuType.equals(panelType)) {
			chartRevenue.rearrangeXAxisDatetimeLabels();
			chartDownloads.rearrangeXAxisDatetimeLabels();
			chartRank.rearrangeXAxisDatetimeLabels();
			chartRevenue.resize();
			chartDownloads.resize();
			chartRank.resize();
		}
	}

}
