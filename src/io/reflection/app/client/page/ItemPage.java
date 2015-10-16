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
import io.reflection.app.client.component.DateSelector;
import io.reflection.app.client.component.FilterSwitch;
import io.reflection.app.client.component.Selector;
import io.reflection.app.client.component.ToggleRadioButton;
import io.reflection.app.client.controller.FilterController;
import io.reflection.app.client.controller.FilterController.Filter;
import io.reflection.app.client.controller.ItemController;
import io.reflection.app.client.controller.LinkedAccountController;
import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.controller.NavigationController.Stack;
import io.reflection.app.client.controller.RankController;
import io.reflection.app.client.controller.SessionController;
import io.reflection.app.client.controller.StoreController;
import io.reflection.app.client.handler.NavigationEventHandler;
import io.reflection.app.client.handler.TogglePanelEventHandler;
import io.reflection.app.client.helper.AnimationHelper;
import io.reflection.app.client.helper.ApiCallHelper;
import io.reflection.app.client.helper.ColorHelper;
import io.reflection.app.client.helper.FilterHelper;
import io.reflection.app.client.helper.FormHelper;
import io.reflection.app.client.helper.FormattingHelper;
import io.reflection.app.client.helper.ResponsiveDesignHelper;
import io.reflection.app.client.helper.TooltipHelper;
import io.reflection.app.client.highcharts.Chart;
import io.reflection.app.client.highcharts.ChartHelper.DashStyle;
import io.reflection.app.client.highcharts.ChartHelper.LineType;
import io.reflection.app.client.highcharts.ChartHelper.RankType;
import io.reflection.app.client.highcharts.ChartHelper.XDataType;
import io.reflection.app.client.highcharts.ChartHelper.YAxisPosition;
import io.reflection.app.client.highcharts.ChartHelper.YDataType;
import io.reflection.app.client.part.BootstrapGwtCellTable;
import io.reflection.app.client.part.ErrorPanel;
import io.reflection.app.client.part.NoDataPanel;
import io.reflection.app.client.part.OutOfRankPanel;
import io.reflection.app.client.part.datatypes.AppRevenue;
import io.reflection.app.client.part.datatypes.DateRange;
import io.reflection.app.client.part.navigation.Header.PanelType;
import io.reflection.app.client.res.Styles;
import io.reflection.app.client.res.Styles.ReflectionMainStyles;
import io.reflection.app.datatypes.shared.Item;
import io.reflection.app.datatypes.shared.Rank;
import io.reflection.app.shared.util.DataTypeHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.ButtonElement;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
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
import com.google.gwt.user.cellview.client.SafeHtmlHeader;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.willshex.gson.json.service.shared.StatusType;

public class ItemPage extends Page implements NavigationEventHandler, GetItemRanksEventHandler, GetItemSalesRanksEventHandler,
		GetLinkedAccountItemEventHandler, TogglePanelEventHandler {

	private static ItemPageUiBinder uiBinder = GWT.create(ItemPageUiBinder.class);

	interface ItemPageUiBinder extends UiBinder<Widget, ItemPage> {}

	private ReflectionMainStyles style = Styles.STYLES_INSTANCE.reflectionMainStyle();

	public static final int SELECTED_TAB_PARAMETER_INDEX = 1;

	@UiField HeadingElement title;
	@UiField Image image;
	@UiField Image imageTable;
	@UiField SpanElement creatorName;
	@UiField SpanElement storeName;
	@UiField Button applyFilters;
	@UiField AnchorElement viewInStore;
	@UiField SpanElement price;
	private String iapDescription;

	// Filters
	// @UiField FormSwitch followSwitch;
	@UiField DateSelector dateSelector;
	@UiField Element filtersForm;
	@UiField Selector appStoreSelector;
	@UiField Selector countrySelector;
	@UiField HTMLPanel filtersGroupGraphOptions;
	@UiField(provided = true) FilterSwitch accuracySwitch = new FilterSwitch(true);
	@UiField(provided = true) FilterSwitch eventsSwitch = new FilterSwitch(true);
	@UiField(provided = true) FilterSwitch overlayRevenuesSwitch = new FilterSwitch(true);
	@UiField(provided = true) FilterSwitch overlayDownloadsSwitch = new FilterSwitch(true);
	@UiField(provided = true) FilterSwitch cumulativeChartSwitch = new FilterSwitch(true);
	@UiField(provided = true) FilterSwitch oneMMovingAverageSwitch = new FilterSwitch(true);
	@UiField(provided = true) FilterSwitch overlayAppsSwitch = new FilterSwitch(true);
	@UiField(provided = true) ToggleRadioButton toggleChartDate = new ToggleRadioButton("charttype", "0 0 32 32");
	@UiField(provided = true) ToggleRadioButton toggleChartCountry = new ToggleRadioButton("charttype", "0 0 32 32");

	@UiField InlineHyperlink revenueLink;
	@UiField Element premiumIconRevenue;
	@UiField InlineHyperlink downloadsLink;
	@UiField Element premiumIconDownload;
	@UiField InlineHyperlink rankingLink;
	@UiField SpanElement rankingText;
	@UiField InlineHyperlink appDetailsLink;
	@UiField SpanElement appDetailsText;
	@UiField LIElement revenueItem;
	@UiField LIElement downloadsItem;
	@UiField LIElement rankingItem;
	@UiField LIElement appDetailsItem;

	// @UiField(provided = true) CellTable<ItemRevenue> revenueTable = new CellTable<ItemRevenue>(Integer.MAX_VALUE, BootstrapGwtCellTable.INSTANCE);
	@UiField(provided = true) CellTable<AppRevenue> revenueTable = new CellTable<AppRevenue>(Integer.MAX_VALUE, BootstrapGwtCellTable.INSTANCE);

	private SafeHtmlHeader dateHeader = new SafeHtmlHeader(SafeHtmlUtils.fromTrustedString("Date " + AnimationHelper.getSorterSvg()));
	private SafeHtmlHeader revenueHeader = new SafeHtmlHeader(SafeHtmlUtils.fromTrustedString("Revenue <span class=\"" + style.hiddenForMobile()
			+ "\">Generated</span> " + AnimationHelper.getSorterSvg()));
	private SafeHtmlHeader revenueForPeriodHeader = new SafeHtmlHeader(SafeHtmlUtils.fromTrustedString("% <span class=\"" + style.hiddenForMobile()
			+ "\">of</span> Total <span class=\"" + style.hiddenForMobile() + "\">Revenue</span> for Period " + AnimationHelper.getSorterSvg()));

	private String displayingAppId;
	private String comingPage;
	private RankType rankType;
	// private YAxisDataType dataType;
	private Item displayingApp;
	private Map<String, LIElement> tabs = new HashMap<String, LIElement>();
	private String selectedTab;
	private String previousFilter;
	private AppRevenue appRevenuePlaceholder = new AppRevenue();
	private List<AppRevenue> tablePlaceholder = new ArrayList<AppRevenue>();
	@UiField SpanElement infoDateRange;
	@UiField SpanElement infoTotalRevenue;

	@UiField DivElement graphContainer;
	@UiField HTMLPanel chartContainer;
	@UiField DivElement graphLoadingIndicator;
	@UiField(provided = true) Chart chartRevenue = new Chart(XDataType.DateXAxisDataType);
	@UiField(provided = true) Chart chartDownloads = new Chart(XDataType.DateXAxisDataType);
	@UiField(provided = true) Chart chartRank = new Chart(XDataType.DateXAxisDataType);
	private static final String SERIES_ID_RANK = "rank";
	private static final String SERIES_ID_REVENUE = "revenue";
	private static final String SERIES_ID_DOWNLOAD = "download";
	private static final String SERIES_ID_REVENUE_CUMULATIVE = "revenueCumulative";
	private static final String SERIES_ID_DOWNLOAD_CUMULATIVE = "downloadCumulative";
	private static final String SERIES_ID_REVENUE_SECONDARY = "revenueSecondary";
	private static final String SERIES_ID_DOWNLOAD_SECONDARY = "downloadSecondary";
	private static final String SERIES_ID_REVENUE_CUMULATIVE_SECONDARY = "revenueCumulativeSecondary";
	private static final String SERIES_ID_DOWNLOAD_CUMULATIVE_SECONDARY = "downloadCumulativeSecondary";

	@UiField AnchorElement revealContentFilter;

	@UiField ButtonElement dnwBtn;
	@UiField ButtonElement dnwBtnMobile;
	@UiField DivElement sincePanel;
	@UiField ErrorPanel errorPanel;
	@UiField NoDataPanel noDataPanel;
	@UiField OutOfRankPanel appOutOfTop200Panel;
	@UiField DivElement appDetailsPanel;

	// private LoadingBar loadingBar;

	@UiField Element tablePanel;
	@UiField Element togglePanel;

	private Column<AppRevenue, SafeHtml> dateColumn;
	private Column<AppRevenue, SafeHtml> revenueColumn;
	private Column<AppRevenue, SafeHtml> revenueForPeriodColumn;

	public ItemPage() {
		initWidget(uiBinder.createAndBindUi(this));

		chartContainer.getElement().getStyle().setPosition(Position.RELATIVE);

		tabs.put(REVENUE_CHART_TYPE, revenueItem);
		tabs.put(DOWNLOADS_CHART_TYPE, downloadsItem);
		tabs.put(RANKING_CHART_TYPE, rankingItem);
		tabs.put("appdetails", appDetailsItem);

		appDetailsItem.getStyle().setCursor(Cursor.DEFAULT);

		if (!SessionController.get().isAdmin()) {
			tablePanel.removeFromParent();
			revenueTable.removeFromParent();
			// followSwitch.removeFromParent();
			accuracySwitch.removeFromParent();
			eventsSwitch.removeFromParent();
			oneMMovingAverageSwitch.removeFromParent();
			overlayAppsSwitch.removeFromParent();
			togglePanel.removeFromParent();
			sincePanel.removeFromParent();
			dnwBtn.removeFromParent();
			dnwBtnMobile.removeFromParent();
			dateSelector.disableBefore(ApiCallHelper.getUTCDate(2015, 4, 30));
		} else {
			createColumns();
			// RankController.get().getItemRevenueDataProvider().addDataDisplay(revenueTable);
			RankController.get().getRevenueDataProvider().addDataDisplay(revenueTable);
			revenueTable.setLoadingIndicator(AnimationHelper.getAppRevenueLoadingIndicator(CalendarUtil.getDaysBetween(dateSelector.getValue().getFrom(),
					dateSelector.getValue().getTo()) + 1));
			revenueTable.getTableLoadingSection().addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().tableBodyLoading());
			revenueTable.setRowCount(0, false);
		}

		dnwBtn.setAttribute("data-tooltip", "Download data in CSV file");
		dnwBtn.addClassName("js-tooltip");
		dnwBtnMobile.setAttribute("data-tooltip", "Download data in CSV file");
		dnwBtnMobile.addClassName("js-tooltip");

		// ResponsiveDesignHelper.nativeRevealContent(revealContentStore);
		ResponsiveDesignHelper.nativeRevealContent(revealContentFilter);

		tablePlaceholder.add(appRevenuePlaceholder);

		FilterHelper.addCountries(countrySelector, SessionController.get().isAdmin());
		FilterHelper.addStores(appStoreSelector, true);

		dateSelector.addFixedRanges(FilterHelper.getDefaultDateRanges());

		// Add click event to LI element so the event is fired when clicking on the whole tab
		Event.sinkEvents(revenueItem, Event.ONCLICK);
		Event.sinkEvents(downloadsItem, Event.ONCLICK);
		Event.sinkEvents(rankingItem, Event.ONCLICK);
		Event.sinkEvents(appDetailsItem, Event.ONCLICK);
		Event.setEventListener(revenueItem, new EventListener() {

			@Override
			public void onBrowserEvent(Event event) {
				if (Event.ONCLICK == event.getTypeInt()) {
					History.newItem(revenueLink.getTargetHistoryToken());
				}
			}
		});
		Event.setEventListener(downloadsItem, new EventListener() {

			@Override
			public void onBrowserEvent(Event event) {
				if (Event.ONCLICK == event.getTypeInt()) {
					History.newItem(downloadsLink.getTargetHistoryToken());
				}
			}
		});
		Event.setEventListener(rankingItem, new EventListener() {

			@Override
			public void onBrowserEvent(Event event) {
				if (Event.ONCLICK == event.getTypeInt()) {
					History.newItem(rankingLink.getTargetHistoryToken());
				}
			}
		});
		Event.setEventListener(appDetailsItem, new EventListener() {

			@Override
			public void onBrowserEvent(Event event) {
				if (Event.ONCLICK == event.getTypeInt()) {
					History.newItem(appDetailsLink.getTargetHistoryToken());
				}
			}
		});

		resetAppProperties();

		updateSelectorsFromFilter();

		// loadingBar.show();
		AnimationHelper.nativeImgErrorPlaceholder(image.getElement());
		AnimationHelper.nativeImgErrorPlaceholder(imageTable.getElement());

		TooltipHelper.updateHelperTooltip();
	}

	private void setAppDetails(Item item) {
		if (item.name != null) {
			displayingApp.name = item.name;
		}
		if (item.creatorName != null) {
			displayingApp.creatorName = item.creatorName;
		}
		if (item.smallImage != null) {
			displayingApp.smallImage = item.smallImage;
		}
		if (item.mediumImage != null) {
			displayingApp.mediumImage = item.mediumImage;
		}
		if (item.largeImage != null) {
			displayingApp.largeImage = item.largeImage;
		}

		title.setInnerText(displayingApp.name);
		creatorName.setInnerText(displayingApp.creatorName != null ? "By " + displayingApp.creatorName : "");
		if (displayingApp.mediumImage != null) {
			image.setUrl(displayingApp.mediumImage);
			image.setVisible(true);
		} else {
			image.setVisible(false);
			image.setUrl("");
		}
		if (displayingApp.smallImage != null) {
			imageTable.setUrl(displayingApp.smallImage);
			imageTable.setVisible(true);
		} else {
			imageTable.setVisible(false);
			imageTable.setUrl("");
		}
		iapDescription = DataTypeHelper.itemIapState(displayingApp, " + In App Purchases", "", "");
	}

	private void setAppPriceFromRank(Rank rank) {
		if (rank != null && rank.currency != null && rank.price != null) {
			displayingApp.currency = rank.currency;
			displayingApp.price = rank.price;
			price.setInnerText(FormattingHelper.asPriceString(displayingApp.currency, displayingApp.price.floatValue()) + iapDescription);
		} else {
			price.setInnerSafeHtml(SafeHtmlUtils.fromTrustedString("<span class=\"js-tooltip\" data-tooltip=\"No data available\">-</span>"));
		}
	}

	private void resetAppProperties() {
		title.setInnerText("");
		creatorName.setInnerText("");
		image.setVisible(false);
		image.setUrl("");
		imageTable.setVisible(false);
		imageTable.setUrl("");
		storeName.setInnerSafeHtml(AnimationHelper.getLoaderInlineSafeHTML());
		price.setInnerSafeHtml(AnimationHelper.getLoaderInlineSafeHTML());
	}

	private void setError() {
		displayingApp.currency = null;
		displayingApp.price = null;
		setAppPriceFromRank(null);
		infoTotalRevenue.setInnerSafeHtml(SafeHtmlUtils.fromTrustedString("<span class=\"js-tooltip\" data-tooltip=\"No data available\">-</span>"));
		revenueTable.setRowCount(0, true);
		// loadingBar.hide(false);
		chartContainer.setVisible(false);
		errorPanel.setVisible(true);
		graphLoadingIndicator.removeClassName(style.isLoadingSuccess());
		graphContainer.removeClassName(style.isLoading());
	}

	private void createColumns() {

		// Column<ItemRevenue, ConcreteImageAndText> countryColumn = new Column<ItemRevenue, ConcreteImageAndText>(new ImageAndTextCell<ConcreteImageAndText>())
		// {
		//
		// @Override
		// public ConcreteImageAndText getValue(ItemRevenue object) {
		// return (object.countryFlagStyleName != null && object.countryName != null) ? new ConcreteImageAndText(object.countryFlagStyleName,
		// SafeHtmlUtils.fromSafeConstant(object.countryName)) : new ConcreteImageAndText("", SafeHtmlUtils.fromSafeConstant(""));
		// }
		// };

		// Column<ItemRevenue, PercentageProgress> percentageColumn = new Column<ItemRevenue, PercentageProgress>(new ProgressBarCell<PercentageProgress>()) {
		//
		// @Override
		// public PercentageProgress getValue(ItemRevenue object) {
		// return new PercentageProgress(object.percentage.floatValue());
		// }
		//
		// };

		// Column<ItemRevenue, SafeHtml> paidColumn = new Column<ItemRevenue, SafeHtml>(new SafeHtmlCell()) {
		//
		// @Override
		// public SafeHtml getValue(ItemRevenue object) {
		// return (object.currency != null) ? SafeHtmlUtils
		// .fromSafeConstant(FormattingHelper.asWholeMoneyString(object.currency, object.paid.floatValue())) : AnimationHelper
		// .getLoaderInlineSafeHTML();
		// }
		// };
		//
		// Column<ItemRevenue, SafeHtml> subscriptionColumn = new Column<ItemRevenue, SafeHtml>(new SafeHtmlCell()) {
		//
		// @Override
		// public SafeHtml getValue(ItemRevenue object) {
		// return (object.currency != null) ? SafeHtmlUtils.fromSafeConstant(FormattingHelper.asMoneyString(object.currency, 0.0f)) : AnimationHelper
		// .getLoaderInlineSafeHTML();
		// }
		// };
		//
		// Column<ItemRevenue, SafeHtml> iapColumn = new Column<ItemRevenue, SafeHtml>(new SafeHtmlCell()) {
		//
		// @Override
		// public SafeHtml getValue(ItemRevenue object) {
		// return (object.currency != null && object.iap != null) ? SafeHtmlUtils.fromSafeConstant(FormattingHelper.asWholeMoneyString(object.currency,
		// object.iap.floatValue())) : AnimationHelper.getLoaderInlineSafeHTML();
		// }
		// };

		// Column<ItemRevenue, SafeHtml> totalColumn = new Column<ItemRevenue, SafeHtml>(new SafeHtmlCell()) {
		//
		// @Override
		// public SafeHtml getValue(ItemRevenue object) {
		// return (object.currency != null && object.total != null) ? SafeHtmlUtils.fromSafeConstant(FormattingHelper.asWholeMoneyString(object.currency,
		// object.total.floatValue())) : AnimationHelper.getLoaderInlineSafeHTML();
		// }
		// };

		// revenueTable.addColumn(countryColumn, "Country");
		// TextHeader percentageHeader = new TextHeader("% total revenue");
		// revenue.addColumn(percentageColumn, percentageHeader);
		// revenueTable.addColumn(paidColumn, "Paid");
		// revenueTable.addColumn(subscriptionColumn, "Subscription");
		// revenueTable.addColumn(iapColumn, "IAP");
		// revenueTable.addColumn(totalColumn, "Total");
		// revenueTable.setWidth("100%", true);
		// revenueTable.setColumnWidth(countryColumn, 136.0, Unit.PX);
		// revenueTable.setColumnWidth(paidColumn, 51.0, Unit.PX);
		// revenueTable.setColumnWidth(subscriptionColumn, 51.0, Unit.PX);
		// revenueTable.setColumnWidth(iapColumn, 51.0, Unit.PX);
		// revenueTable.setColumnWidth(totalColumn, 51.0, Unit.PX);

		ListHandler<AppRevenue> columnSortHandler = new ListHandler<AppRevenue>(RankController.get().getRevenueDataProvider().getList()) {
			/*
			 * (non-Javadoc)
			 * 
			 * @see com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler#onColumnSort(com.google.gwt.user.cellview.client.ColumnSortEvent)
			 */
			@Override
			public void onColumnSort(ColumnSortEvent event) {
				super.onColumnSort(event);
				dateHeader.setHeaderStyleNames(style.canBeSorted());
				revenueHeader.setHeaderStyleNames(style.canBeSorted());
				revenueForPeriodHeader.setHeaderStyleNames(style.canBeSorted());
				if (event.getColumn() == dateColumn) {
					dateHeader.setHeaderStyleNames(style.canBeSorted() + " " + (event.isSortAscending() ? style.isAscending() : style.isDescending()));
				} else if (event.getColumn() == revenueColumn) {
					revenueHeader.setHeaderStyleNames(style.canBeSorted() + " " + (event.isSortAscending() ? style.isAscending() : style.isDescending()));
				} else if (event.getColumn() == revenueForPeriodColumn) {
					revenueForPeriodHeader.setHeaderStyleNames(style.canBeSorted() + " "
							+ (event.isSortAscending() ? style.isAscending() : style.isDescending()));
				}
				TooltipHelper.updateHelperTooltip();
			}
		};

		revenueTable.addColumnSortHandler(columnSortHandler);

		dateColumn = new Column<AppRevenue, SafeHtml>(new SafeHtmlCell()) {

			@Override
			public SafeHtml getValue(AppRevenue object) {
				return object.date != null ? SafeHtmlUtils.fromTrustedString(FormattingHelper.DATE_FORMATTER_EEE_DD_MM_YY.format(object.date)) : SafeHtmlUtils
						.fromTrustedString("<span class=\"js-tooltip\" data-tooltip=\"No data available\">-</span>");
			}
		};
		dateColumn.setDefaultSortAscending(false);
		revenueTable.addColumn(dateColumn, dateHeader);
		dateColumn.setCellStyleNames(style.dateValue());
		dateHeader.setHeaderStyleNames(style.canBeSorted());
		dateColumn.setSortable(true);
		columnSortHandler.setComparator(dateColumn, AppRevenue.getDateComparator());

		revenueColumn = new Column<AppRevenue, SafeHtml>(new SafeHtmlCell()) {

			@Override
			public SafeHtml getValue(AppRevenue object) {
				if (object.currency != null && object.total != null) {
					infoTotalRevenue.setInnerText(FormattingHelper.asWholeMoneyString(object.currency, object.total.floatValue()));
				}
				return object.revenue != null ? SafeHtmlUtils.fromTrustedString(FormattingHelper.asWholeMoneyString(object.currency,
						object.revenue.floatValue())) : SafeHtmlUtils
						.fromTrustedString("<span class=\"js-tooltip\" data-tooltip=\"No data available\">-</span>");
			}
		};
		revenueTable.addColumn(revenueColumn, revenueHeader);
		revenueColumn.setCellStyleNames(style.revenueValue());
		revenueHeader.setHeaderStyleNames(style.canBeSorted());
		revenueColumn.setSortable(true);
		columnSortHandler.setComparator(revenueColumn, AppRevenue.getRevenueComparator());

		revenueForPeriodColumn = new Column<AppRevenue, SafeHtml>(new SafeHtmlCell()) {

			@Override
			public SafeHtml getValue(AppRevenue object) {
				SafeHtml value = SafeHtmlUtils.fromSafeConstant("");
				if (object.revenuePercentForPeriod != null) {
					String percentage = (DataTypeHelper.isZero(object.revenuePercentForPeriod.floatValue()) ? "0" : FormattingHelper.TWO_DECIMALS_FORMATTER
							.format(object.revenuePercentForPeriod.floatValue() * 100));
					value = SafeHtmlUtils.fromTrustedString("<span>" + percentage + "%</span><div class=\"" + style.dataBar() + "\"><div style=\"width: "
							+ percentage + "%\"></div></div>");
				}
				return value;
			}
		};
		revenueTable.addColumn(revenueForPeriodColumn, revenueForPeriodHeader);
		revenueForPeriodColumn.setCellStyleNames(style.revenuePercentage());
		revenueForPeriodHeader.setHeaderStyleNames(style.canBeSorted());
		revenueForPeriodColumn.setSortable(true);
		columnSortHandler.setComparator(revenueForPeriodColumn, AppRevenue.getRevenuePercentForPeriodComparator());

		revenueTable.setColumnWidth(dateColumn, 25, Unit.PCT);
		revenueTable.setColumnWidth(revenueColumn, 25, Unit.PCT);
		revenueTable.setColumnWidth(revenueForPeriodColumn, 50, Unit.PCT);
	}

	@UiHandler("accuracySwitch")
	void onAccuracySwitchValueChanged(ValueChangeEvent<Boolean> event) {

	}

	@UiHandler("eventsSwitch")
	void onEventsSwitchValueChanged(ValueChangeEvent<Boolean> event) {

	}

	@UiHandler("overlayRevenuesSwitch")
	void onOverlayRevenuesSwitchValueChanged(ValueChangeEvent<Boolean> event) {
		chartDownloads.setSeriesVisible(SERIES_ID_REVENUE_SECONDARY, event.getValue().booleanValue() && !cumulativeChartSwitch.getValue().booleanValue());
		chartDownloads.setSeriesVisible(SERIES_ID_REVENUE_CUMULATIVE_SECONDARY, event.getValue().booleanValue()
				&& cumulativeChartSwitch.getValue().booleanValue());
		chartRank.setSeriesVisible(SERIES_ID_REVENUE_SECONDARY, SessionController.get().isAdmin() && event.getValue().booleanValue());
	}

	@UiHandler("overlayDownloadsSwitch")
	void onOverlayDownloadsSwitchValueChanged(ValueChangeEvent<Boolean> event) {
		chartRevenue.setSeriesVisible(SERIES_ID_DOWNLOAD_SECONDARY, event.getValue().booleanValue() && !cumulativeChartSwitch.getValue().booleanValue());
		chartRevenue.setSeriesVisible(SERIES_ID_DOWNLOAD_CUMULATIVE_SECONDARY, event.getValue().booleanValue()
				&& cumulativeChartSwitch.getValue().booleanValue());
		chartRank.setSeriesVisible(SERIES_ID_DOWNLOAD_SECONDARY, SessionController.get().isAdmin() && event.getValue().booleanValue());
	}

	@UiHandler("cumulativeChartSwitch")
	void onCumulativeChartSwitchValueChanged(ValueChangeEvent<Boolean> event) {
		chartRevenue.setSeriesVisible(SERIES_ID_REVENUE, !event.getValue().booleanValue());
		chartRevenue.setSeriesVisible(SERIES_ID_REVENUE_CUMULATIVE, event.getValue().booleanValue());
		chartRevenue.setSeriesVisible(SERIES_ID_DOWNLOAD_SECONDARY, overlayDownloadsSwitch.getValue().booleanValue() && !event.getValue().booleanValue());
		chartRevenue.setSeriesVisible(SERIES_ID_DOWNLOAD_CUMULATIVE_SECONDARY, overlayDownloadsSwitch.getValue().booleanValue()
				&& event.getValue().booleanValue());
		chartDownloads.setSeriesVisible(SERIES_ID_DOWNLOAD, !event.getValue().booleanValue());
		chartDownloads.setSeriesVisible(SERIES_ID_DOWNLOAD_CUMULATIVE, event.getValue().booleanValue());
		chartDownloads.setSeriesVisible(SERIES_ID_REVENUE_SECONDARY, overlayRevenuesSwitch.getValue().booleanValue() && !event.getValue().booleanValue());
		chartDownloads.setSeriesVisible(SERIES_ID_REVENUE_CUMULATIVE_SECONDARY, overlayRevenuesSwitch.getValue().booleanValue()
				&& event.getValue().booleanValue());
	}

	@UiHandler("toggleChartDate")
	void onToggleChartGraphSelected(ValueChangeEvent<Boolean> event) {
		setChartGraphsVisible(true);
	}

	@UiHandler("toggleChartCountry")
	void onToggleChartMapSelected(ValueChangeEvent<Boolean> event) {
		setChartGraphsVisible(false);
	}

	private void updateSelectorsFromFilter() {
		FilterController fc = FilterController.get();

		DateRange dateRange = new DateRange();

		dateRange.setFrom(fc.getStartDate());
		dateRange.setTo(fc.getEndDate());
		dateSelector.setValue(dateRange);
		infoDateRange.setInnerText(FormattingHelper.DATE_FORMATTER_DD_MMM_YYYY.format(dateRange.getFrom()) + " - "
				+ FormattingHelper.DATE_FORMATTER_DD_MMM_YYYY.format(dateRange.getTo()));

		appStoreSelector.setSelectedIndex(FormHelper.getItemIndex(appStoreSelector, fc.getFilter().getStoreA3Code()));
		countrySelector.setSelectedIndex(FormHelper.getItemIndex(countrySelector, fc.getFilter().getCountryA2Code()));

	}

	@UiHandler("applyFilters")
	void onApplyFiltersClicked(ClickEvent event) {
		event.preventDefault();
		if (NavigationController.get().getCurrentPage() == PageType.ItemPageType) {
			boolean updateData = false;
			if (updateData = updateData || !FilterController.get().getFilter().getCountryA2Code().equals(countrySelector.getSelectedValue())) {
				FilterController.get().setCountry(countrySelector.getSelectedValue());
			}
			if (updateData = updateData || !FilterController.get().getFilter().getStoreA3Code().equals(appStoreSelector.getSelectedValue())) {
				FilterController.get().setStore(appStoreSelector.getSelectedValue());
			}
			if (updateData = updateData
					|| !CalendarUtil.isSameDate(new Date(FilterController.get().getFilter().getEndTime().longValue()), dateSelector.getDateBoxToValue())
					|| !CalendarUtil.isSameDate(new Date(FilterController.get().getFilter().getStartTime().longValue()), dateSelector.getDateBoxFromValue())) {
				FilterController.get().getFilter().setEndTime(dateSelector.getDateBoxToValue().getTime());
				FilterController.get().getFilter().setStartTime(dateSelector.getDateBoxFromValue().getTime());
				dateSelector.setValue(new DateRange(dateSelector.getDateBoxFromValue(), dateSelector.getDateBoxToValue()), true);
			}
			if (updateData) {
				applyFilters.setEnabled(false);
				PageType.ItemPageType.show(NavigationController.VIEW_ACTION_PARAMETER_VALUE, displayingAppId, selectedTab, comingPage, FilterController.get()
						.asItemFilterString());
			}
			if (errorPanel.isVisible() || noDataPanel.isVisible()) {
				applyFilters.setEnabled(false);
				updateSelectorsFromFilter();
				infoTotalRevenue.setInnerSafeHtml(AnimationHelper.getLoaderInlineSafeHTML());
				displayingApp.currency = null;
				displayingApp.price = null;
				price.setInnerSafeHtml(AnimationHelper.getLoaderInlineSafeHTML());
				errorPanel.setVisible(false);
				noDataPanel.setVisible(false);
				chartContainer.setVisible(true);
				graphContainer.addClassName(style.isLoading());
				chartRevenue.setLoading(true);
				chartDownloads.setLoading(true);
				chartRank.setLoading(true);
				revenueTable.setLoadingIndicator(AnimationHelper.getAppRevenueLoadingIndicator(CalendarUtil.getDaysBetween(dateSelector.getValue().getFrom(),
						dateSelector.getValue().getTo()) + 1));
				revenueTable.setRowCount(0, false);
				dateHeader.setHeaderStyleNames(style.canBeSorted());
				revenueHeader.setHeaderStyleNames(style.canBeSorted());
				revenueForPeriodHeader.setHeaderStyleNames(style.canBeSorted());
				// loadingBar.show();
				getChartData();
				previousFilter = FilterController.get().asItemFilterString();
			}
		}
	}

	@UiHandler({ "countrySelector", "appStoreSelector" })
	void onFiltersChanged(ChangeEvent event) {
		applyFilters.setEnabled(!FilterController.get().getFilter().getCountryA2Code().equals(countrySelector.getSelectedValue())
				|| !FilterController.get().getFilter().getStoreA3Code().equals(appStoreSelector.getSelectedValue())
				|| !CalendarUtil.isSameDate(new Date(FilterController.get().getFilter().getEndTime().longValue()), dateSelector.getDateBoxToValue())
				|| !CalendarUtil.isSameDate(new Date(FilterController.get().getFilter().getStartTime().longValue()), dateSelector.getDateBoxFromValue()));
	}

	@UiHandler("dateSelector")
	void onDateSelectorChanged(ValueChangeEvent<DateRange> event) {
		applyFilters.setEnabled(!FilterController.get().getFilter().getCountryA2Code().equals(countrySelector.getSelectedValue())
				|| !FilterController.get().getFilter().getStoreA3Code().equals(appStoreSelector.getSelectedValue())
				|| !CalendarUtil.isSameDate(new Date(FilterController.get().getFilter().getEndTime().longValue()), dateSelector.getDateBoxToValue())
				|| !CalendarUtil.isSameDate(new Date(FilterController.get().getFilter().getStartTime().longValue()), dateSelector.getDateBoxFromValue()));
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
		register(DefaultEventBus.get().addHandlerToSource(GetItemRanksEventHandler.TYPE, RankController.get(), this));
		register(DefaultEventBus.get().addHandlerToSource(GetItemSalesRanksEventHandler.TYPE, RankController.get(), this));
		register(DefaultEventBus.get().addHandlerToSource(GetLinkedAccountItemEventHandler.TYPE, LinkedAccountController.get(), this));
		register(DefaultEventBus.get().addHandlerToSource(TogglePanelEventHandler.TYPE, NavigationController.get().getHeader(), this));

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.page.Page#onDetach()
	 */
	@Override
	protected void onDetach() {
		super.onDetach();

		resetAppProperties();
		// loadingBar.reset();
		premiumIconRevenue.getStyle().setVisibility(Visibility.HIDDEN);
		premiumIconDownload.getStyle().setVisibility(Visibility.HIDDEN);
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

			premiumIconRevenue.getStyle().setVisibility(RanksPage.COMING_FROM_PARAMETER.equals(comingPage) ? Visibility.VISIBLE : Visibility.HIDDEN);
			premiumIconDownload.getStyle().setVisibility(RanksPage.COMING_FROM_PARAMETER.equals(comingPage) ? Visibility.VISIBLE : Visibility.HIDDEN);

			String newInternalId = current.getParameter(0);
			boolean isNewDataRequired = false;

			// App changed
			if (displayingAppId == null || !displayingAppId.equals(newInternalId)) {
				isNewDataRequired = true;

				displayingAppId = newInternalId;

				if ((displayingApp = ItemController.get().lookupItem(displayingAppId)) != null) { // App details already retrieved
					setAppDetails(displayingApp);
				} else {
					// Coming from a refresh page
					displayingApp = new Item();
					displayingApp.internalId = displayingAppId;
				}
				displayingApp.source = DataTypeHelper.IOS_STORE_A3;

				storeName.setInnerText("View in Appstore");
				viewInStore.setHref(StoreController.get().getExternalUri(displayingApp));

			} else {
				setAppDetails(displayingApp);
			}

			if (previousFilter == null) {
				previousFilter = FilterController.get().asRankFilterString();
			}

			Filter newFilter = FilterController.get().getFilter();

			if (!previousFilter.equals(newFilter.asItemFilterString())) {
				isNewDataRequired = true;
				updateSelectorsFromFilter();
				RankType newRankType = RankType.fromString(newFilter.getListType());
				if (rankType == null || rankType != newRankType) {
					rankType = newRankType;
				}
				previousFilter = newFilter.asItemFilterString();
			}

			switch (newFilter.getListType()) {
			case (FilterController.FREE_LIST_TYPE):
				rankingText.setInnerText("Free rank");
				break;
			case (FilterController.PAID_LIST_TYPE):
				rankingText.setInnerText("Paid rank");
				break;
			case (FilterController.GROSSING_LIST_TYPE):
				rankingText.setInnerText("Grossing rank");
				break;
			default:
				rankingText.setInnerText("Rank");
				break;
			}

			revenueLink.setTargetHistoryToken(PageType.ItemPageType.asTargetHistoryToken(NavigationController.VIEW_ACTION_PARAMETER_VALUE, displayingAppId,
					REVENUE_CHART_TYPE, comingPage, previousFilter));
			downloadsLink.setTargetHistoryToken(PageType.ItemPageType.asTargetHistoryToken(NavigationController.VIEW_ACTION_PARAMETER_VALUE, displayingAppId,
					DOWNLOADS_CHART_TYPE, comingPage, previousFilter));
			rankingLink.setTargetHistoryToken(PageType.ItemPageType.asTargetHistoryToken(NavigationController.VIEW_ACTION_PARAMETER_VALUE, displayingAppId,
					RANKING_CHART_TYPE, comingPage, previousFilter));

			if (SessionController.get().isAdmin() || MyAppsPage.COMING_FROM_PARAMETER.equals(comingPage)) {
				setRevenueDownloadTabsEnabled(true);
			} else {
				setRevenueDownloadTabsEnabled(false);
			}

			if (!SessionController.get().isAdmin() && MyAppsPage.COMING_FROM_PARAMETER.equals(comingPage)) {
				setRankTabEnabled(false);
			} else {
				setRankTabEnabled(true);
			}

			String newSelectedTab = current.getParameter(SELECTED_TAB_PARAMETER_INDEX);
			if (selectedTab == null || !selectedTab.equals(newSelectedTab)) {
				selectedTab = newSelectedTab;
				refreshTabs();
			}

			if (isNewDataRequired) {
				infoTotalRevenue.setInnerSafeHtml(AnimationHelper.getLoaderInlineSafeHTML());
				displayingApp.currency = null;
				displayingApp.price = null;
				price.setInnerSafeHtml(AnimationHelper.getLoaderInlineSafeHTML());
				errorPanel.setVisible(false);
				noDataPanel.setVisible(false);
				appOutOfTop200Panel.setVisible(false);
				chartContainer.setVisible(true);
				graphContainer.addClassName(style.isLoading());
				chartRevenue.setLoading(true);
				chartDownloads.setLoading(true);
				chartRank.setLoading(true);
				revenueTable.setLoadingIndicator(AnimationHelper.getAppRevenueLoadingIndicator(CalendarUtil.getDaysBetween(dateSelector.getValue().getFrom(),
						dateSelector.getValue().getTo()) + 1));
				revenueTable.setRowCount(0, false);
				dateHeader.setHeaderStyleNames(style.canBeSorted());
				revenueHeader.setHeaderStyleNames(style.canBeSorted());
				revenueForPeriodHeader.setHeaderStyleNames(style.canBeSorted());
				// loadingBar.show();
				getChartData();
			}

			setChartGraphsVisible(toggleChartDate.getValue());

			ResponsiveDesignHelper.makeTabsResponsive();

		} else {

			PageType.RanksPageType.show(NavigationController.VIEW_ACTION_PARAMETER_VALUE, OVERALL_LIST_TYPE, FilterController.get().asRankFilterString());
		}
	}

	private void setChartGraphsVisible(boolean visible) {
		if (visible && !appOutOfTop200Panel.isVisible()) {
			// TODO hide map
			switch (YDataType.fromString(selectedTab)) {
			case RevenueYAxisDataType:
				overlayRevenuesSwitch.setVisible(false);
				overlayDownloadsSwitch.setVisible(true);
				cumulativeChartSwitch.setVisible(true);
				filtersGroupGraphOptions.setVisible(SessionController.get().isAdmin() || MyAppsPage.COMING_FROM_PARAMETER.equals(comingPage));
				chartRevenue.setVisible(true);
				chartDownloads.setVisible(false);
				chartRank.setVisible(false);
				break;
			case DownloadsYAxisDataType:
				overlayDownloadsSwitch.setVisible(false);
				overlayRevenuesSwitch.setVisible(true);
				cumulativeChartSwitch.setVisible(true);
				filtersGroupGraphOptions.setVisible(SessionController.get().isAdmin() || MyAppsPage.COMING_FROM_PARAMETER.equals(comingPage));
				chartDownloads.setVisible(true);
				chartRevenue.setVisible(false);
				chartRank.setVisible(false);
				break;
			case RankingYAxisDataType:
				overlayRevenuesSwitch.setVisible(true);
				overlayDownloadsSwitch.setVisible(true);
				cumulativeChartSwitch.setVisible(false);
				filtersGroupGraphOptions.setVisible(SessionController.get().isAdmin());
				chartRank.setVisible(true);
				chartRevenue.setVisible(false);
				chartDownloads.setVisible(false);
				break;
			}
		} else {
			chartRevenue.setVisible(false);
			chartDownloads.setVisible(false);
			chartRank.setVisible(false);
			filtersGroupGraphOptions.setVisible(SessionController.get().isAdmin());
			// TODO show map
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

	private void setRevenueDownloadTabsEnabled(boolean enable) {
		if (enable) {
			revenueItem.removeClassName(style.isDisabled());
			revenueItem.getStyle().setCursor(Cursor.POINTER);
			downloadsItem.removeClassName(style.isDisabled());
			downloadsItem.getStyle().setCursor(Cursor.POINTER);
			appDetailsLink.setTargetHistoryToken(NavigationController.get().getStack().toString());
		} else {
			revenueItem.addClassName(style.isDisabled());
			revenueItem.getStyle().setCursor(Cursor.DEFAULT);
			revenueLink.setTargetHistoryToken(NavigationController.get().getStack().toString());
			downloadsItem.addClassName(style.isDisabled());
			downloadsItem.getStyle().setCursor(Cursor.DEFAULT);
			downloadsLink.setTargetHistoryToken(NavigationController.get().getStack().toString());
			appDetailsLink.setTargetHistoryToken(NavigationController.get().getStack().toString());
			selectedTab = RANKING_CHART_TYPE;
			refreshTabs();
		}
	}

	private void setRankTabEnabled(boolean enable) {
		if (enable) {
			rankingText.setInnerText("Rank");
			rankingItem.removeClassName(style.isDisabled());
			rankingItem.getStyle().setCursor(Cursor.POINTER);
		} else {
			rankingText.setInnerHTML("Rank <span class=\"text-small\">coming soon</span>");
			rankingItem.addClassName(style.isDisabled());
			rankingItem.getStyle().setCursor(Cursor.DEFAULT);
			rankingLink.setTargetHistoryToken(NavigationController.get().getStack().toString());
			selectedTab = DOWNLOADS_CHART_TYPE;
			refreshTabs();
		}
	}

	private void refreshTabs() {
		for (String key : tabs.keySet()) {
			tabs.get(key).removeClassName(style.isActive());
		}

		tabs.get(selectedTab).addClassName(style.isActive());

	}

	private void getChartData() {
		if (displayingApp != null) {
			RankController.get().cancelRequestItemRanks();
			RankController.get().cancelRequestItemSalesRanks();
			if (LinkedAccountController.get().getLinkedAccountItem(displayingApp) != null) {
				if (MyAppsPage.COMING_FROM_PARAMETER.equals(comingPage)) {
					RankController.get().fetchItemSalesRanks(displayingApp);
				} else {
					RankController.get().fetchItemRanks(displayingApp);
				}
			}
		} else {
			// item == null
		}
	}

	private void drawData(final List<Rank> ranks, final List<Date> outOfLeaderboardDates) {
		graphLoadingIndicator.addClassName(style.isLoadingSuccess());
		Timer t = new Timer() {

			@Override
			public void run() {
				graphContainer.removeClassName(style.isLoading());
				dateHeader.setHeaderStyleNames(style.canBeSorted() + " " + style.isDescending());
				revenueTable.getColumnSortList().push(dateColumn);
				chartRank.setRankingType(rankType);
				List<Rank> rankingRanksWithOutOfLeaderboardValues = new ArrayList<Rank>(ranks);
				if (outOfLeaderboardDates != null) {
					for (Date outOfLeaderboardDate : outOfLeaderboardDates) { // Add placeholder rank object with position > 200
						for (int i = 0; i < rankingRanksWithOutOfLeaderboardValues.size(); i++) {
							if (FilterHelper.beforeDate(outOfLeaderboardDate, rankingRanksWithOutOfLeaderboardValues.get(i).date)) {
								Rank outOfLeaderboardRankPlaceholder = new Rank();
								outOfLeaderboardRankPlaceholder.date = outOfLeaderboardDate;
								outOfLeaderboardRankPlaceholder.position = new Integer(300);
								outOfLeaderboardRankPlaceholder.grossingPosition = new Integer(300);
								rankingRanksWithOutOfLeaderboardValues.add(i, outOfLeaderboardRankPlaceholder);
								break;
							} else if (i + 1 == rankingRanksWithOutOfLeaderboardValues.size()) {
								Rank outOfLeaderboardRankPlaceholder = new Rank();
								outOfLeaderboardRankPlaceholder.date = outOfLeaderboardDate;
								outOfLeaderboardRankPlaceholder.position = new Integer(300);
								outOfLeaderboardRankPlaceholder.grossingPosition = new Integer(300);
								rankingRanksWithOutOfLeaderboardValues.add(rankingRanksWithOutOfLeaderboardValues.size(), outOfLeaderboardRankPlaceholder);
								break;
							}
						}
					}
				}
				chartRank.drawSeries(rankingRanksWithOutOfLeaderboardValues, YAxisPosition.PRIMARY, YDataType.RankingYAxisDataType, SERIES_ID_RANK,
						LineType.LINE, DashStyle.SOLID, ColorHelper.getReflectionGreen(), false, false);

				if (MyAppsPage.COMING_FROM_PARAMETER.equals(comingPage) || SessionController.get().isAdmin()) {
					chartRank.drawSeries(ranks, YAxisPosition.SECONDARY, YDataType.RevenueYAxisDataType, SERIES_ID_REVENUE_SECONDARY, LineType.LINE,
							DashStyle.DASH, ColorHelper.getReflectionPurple(), false, !overlayRevenuesSwitch.getValue().booleanValue());
					chartRank.drawSeries(ranks, YAxisPosition.TERTIARY, YDataType.DownloadsYAxisDataType, SERIES_ID_DOWNLOAD_SECONDARY, LineType.LINE,
							DashStyle.DASH, ColorHelper.getReflectionRed(), false, !overlayDownloadsSwitch.getValue().booleanValue());
					chartRevenue.drawSeries(ranks, YAxisPosition.PRIMARY, YDataType.RevenueYAxisDataType, SERIES_ID_REVENUE, LineType.AREA, DashStyle.SOLID,
							ColorHelper.getReflectionPurple(), false, cumulativeChartSwitch.getValue().booleanValue());
					chartDownloads.drawSeries(ranks, YAxisPosition.PRIMARY, YDataType.DownloadsYAxisDataType, SERIES_ID_DOWNLOAD, LineType.AREA,
							DashStyle.SOLID, ColorHelper.getReflectionRed(), false, cumulativeChartSwitch.getValue().booleanValue());
					chartRevenue.drawSeries(ranks, YAxisPosition.PRIMARY, YDataType.RevenueYAxisDataType, SERIES_ID_REVENUE_CUMULATIVE, LineType.AREA,
							DashStyle.SOLID, ColorHelper.getReflectionPurple(), true, !cumulativeChartSwitch.getValue().booleanValue());
					chartDownloads.drawSeries(ranks, YAxisPosition.PRIMARY, YDataType.DownloadsYAxisDataType, SERIES_ID_DOWNLOAD_CUMULATIVE, LineType.AREA,
							DashStyle.SOLID, ColorHelper.getReflectionRed(), true, !cumulativeChartSwitch.getValue().booleanValue());
					chartRevenue.drawSeries(ranks, YAxisPosition.SECONDARY, YDataType.DownloadsYAxisDataType, SERIES_ID_DOWNLOAD_SECONDARY, LineType.LINE,
							DashStyle.DASH, ColorHelper.getReflectionRed(), false, cumulativeChartSwitch.getValue().booleanValue()
									|| !overlayDownloadsSwitch.getValue().booleanValue());
					chartDownloads.drawSeries(ranks, YAxisPosition.SECONDARY, YDataType.RevenueYAxisDataType, SERIES_ID_REVENUE_SECONDARY, LineType.LINE,
							DashStyle.DASH, ColorHelper.getReflectionPurple(), false, cumulativeChartSwitch.getValue().booleanValue()
									|| !overlayRevenuesSwitch.getValue().booleanValue());
					chartRevenue.drawSeries(ranks, YAxisPosition.SECONDARY, YDataType.DownloadsYAxisDataType, SERIES_ID_DOWNLOAD_CUMULATIVE_SECONDARY,
							LineType.LINE, DashStyle.DASH, ColorHelper.getReflectionRed(), true, !cumulativeChartSwitch.getValue().booleanValue()
									|| !overlayDownloadsSwitch.getValue().booleanValue());
					chartDownloads.drawSeries(ranks, YAxisPosition.SECONDARY, YDataType.RevenueYAxisDataType, SERIES_ID_REVENUE_CUMULATIVE_SECONDARY,
							LineType.LINE, DashStyle.DASH, ColorHelper.getReflectionPurple(), true, !cumulativeChartSwitch.getValue().booleanValue()
									|| !overlayRevenuesSwitch.getValue().booleanValue());
				}

				graphLoadingIndicator.removeClassName(style.isLoadingSuccess());
			}
		};
		t.schedule(200);
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
		if (output != null && output.item != null && output.status == StatusType.StatusTypeSuccess) {
			if (output.ranks != null) { // if == null the list is empty

				if (output.outOfLeaderboardDates != null && output.outOfLeaderboardDates.size() == FilterController.get().getDateRange().getDays()) {
					chartContainer.setVisible(false);
					appOutOfTop200Panel.setVisible(true);
					setChartGraphsVisible(false);
				}

				setAppDetails(output.item);
				Rank rankPrice = null;
				for (Rank r : output.ranks) {
					if (r.price != null && r.currency != null) {
						rankPrice = r;
						break;
					}
				}
				setAppPriceFromRank(rankPrice);
				drawData(output.ranks, output.outOfLeaderboardDates);
			} else {
				setAppPriceFromRank(null);
				infoTotalRevenue.setInnerSafeHtml(SafeHtmlUtils.fromTrustedString("<span class=\"js-tooltip\" data-tooltip=\"No data available\">-</span>"));
				revenueTable.setRowCount(0, true);
				chartContainer.setVisible(false);
				noDataPanel.setVisible(true);
				applyFilters.setEnabled(true);
			}
			TooltipHelper.updateHelperTooltip();
			// loadingBar.hide(true);
		} else {
			setError();
			applyFilters.setEnabled(true);
		}
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
		setError();
		applyFilters.setEnabled(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.core.shared.call.event.GetItemSalesRanksEventHandler#getItemSalesRanksSuccess(io.reflection.app.api.core.shared.call.
	 * GetItemSalesRanksRequest, io.reflection.app.api.core.shared.call.GetItemSalesRanksResponse)
	 */
	@Override
	public void getItemSalesRanksSuccess(GetItemSalesRanksRequest input, GetItemSalesRanksResponse output) {
		if (output != null && output.item != null && output.status == StatusType.StatusTypeSuccess) {
			if (output.ranks != null) {
				setAppDetails(output.item);
				Rank rankPrice = null;
				for (Rank r : output.ranks) {
					if (r.price != null && r.currency != null) {
						rankPrice = r;
						break;
					}
				}
				setAppPriceFromRank(rankPrice);
				drawData(output.ranks, null);
				TooltipHelper.updateHelperTooltip();
				// loadingBar.hide(true);
			} else {
				setAppPriceFromRank(null);
				infoTotalRevenue.setInnerSafeHtml(SafeHtmlUtils.fromTrustedString("<span class=\"js-tooltip\" data-tooltip=\"No data available\">-</span>"));
				revenueTable.setRowCount(0, true);
				chartContainer.setVisible(false);
				noDataPanel.setVisible(true);
				applyFilters.setEnabled(true);
			}
		} else {
			setError();
			applyFilters.setEnabled(true);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.core.shared.call.event.GetItemSalesRanksEventHandler#getItemSalesRanksFailure(io.reflection.app.api.core.shared.call.
	 * GetItemSalesRanksRequest, java.lang.Throwable)
	 */
	@Override
	public void getItemSalesRanksFailure(GetItemSalesRanksRequest input, Throwable caught) {
		setError();
		applyFilters.setEnabled(true);
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
				RankController.get().fetchItemRanks(displayingApp);
			} else { // My App
				setAppDetails(output.item);
				if (MyAppsPage.COMING_FROM_PARAMETER.equals(comingPage)) {
					RankController.get().fetchItemSalesRanks(displayingApp);
				} else {
					RankController.get().fetchItemRanks(displayingApp);
				}
			}
		} else {
			setError();
			applyFilters.setEnabled(true);
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
		setError();
		applyFilters.setEnabled(true);
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
			chartRevenue.rearrangeXAxisLabels();
			chartDownloads.rearrangeXAxisLabels();
			chartRank.rearrangeXAxisLabels();
			chartRevenue.resize();
			chartDownloads.resize();
			chartRank.resize();
		}
	}

}
