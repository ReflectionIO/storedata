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
import io.reflection.app.client.component.DateSelector;
import io.reflection.app.client.component.FormFieldSelect;
import io.reflection.app.client.component.FormSwitch;
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
import io.reflection.app.client.handler.FilterEventHandler;
import io.reflection.app.client.handler.NavigationEventHandler;
import io.reflection.app.client.handler.TogglePanelEventHandler;
import io.reflection.app.client.helper.ColorHelper;
import io.reflection.app.client.helper.FilterHelper;
import io.reflection.app.client.helper.FormHelper;
import io.reflection.app.client.helper.FormattingHelper;
import io.reflection.app.client.highcharts.Chart;
import io.reflection.app.client.highcharts.ChartHelper;
import io.reflection.app.client.highcharts.ChartHelper.RankType;
import io.reflection.app.client.highcharts.ChartHelper.XDataType;
import io.reflection.app.client.highcharts.ChartHelper.YDataType;
import io.reflection.app.client.page.part.ItemChart.RankingType;
import io.reflection.app.client.part.BootstrapGwtCellTable;
import io.reflection.app.client.part.datatypes.AppRanking;
import io.reflection.app.client.part.datatypes.DateRange;
import io.reflection.app.client.part.datatypes.ItemRevenue;
import io.reflection.app.client.part.navigation.Header.PanelType;
import io.reflection.app.client.res.Images;
import io.reflection.app.client.res.Styles;
import io.reflection.app.datatypes.shared.Item;
import io.reflection.app.datatypes.shared.Rank;
import io.reflection.app.datatypes.shared.Store;
import io.reflection.app.shared.util.DataTypeHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.SafeHtmlHeader;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.Widget;
import com.willshex.gson.json.service.shared.StatusType;

public class ItemPage extends Page implements NavigationEventHandler, GetItemRanksEventHandler, GetItemSalesRanksEventHandler, FilterEventHandler,
		GetLinkedAccountItemEventHandler, TogglePanelEventHandler {

	private static ItemPageUiBinder uiBinder = GWT.create(ItemPageUiBinder.class);

	interface ItemPageUiBinder extends UiBinder<Widget, ItemPage> {}

	public static final int SELECTED_TAB_PARAMETER_INDEX = 1;

	@UiField HeadingElement title;
	@UiField Image image;
	@UiField Image imageTable;
	@UiField AnchorElement creatorName;
	@UiField SpanElement storeName;
	@UiField AnchorElement viewInStore;
	@UiField SpanElement price;
	@UiField Image spinnerLoader;
	private String iapDescription;

	// Filters
	@UiField FormSwitch followSwitch;
	@UiField DateSelector dateSelector;
	@UiField FormFieldSelect storeSelector;
	@UiField FormFieldSelect countrySelector;
	@UiField(provided = true) FormSwitch accuracySwitch = new FormSwitch(true);
	@UiField(provided = true) FormSwitch eventsSwitch = new FormSwitch(true);
	@UiField(provided = true) FormSwitch overlayRevenuesSwitch = new FormSwitch(true);
	@UiField(provided = true) FormSwitch overlayDownloadsSwitch = new FormSwitch(true);
	@UiField(provided = true) FormSwitch cumulativeChartSwitch = new FormSwitch(true);
	@UiField(provided = true) FormSwitch oneMMovingAverageSwitch = new FormSwitch(true);
	@UiField(provided = true) FormSwitch overlayAppsSwitch = new FormSwitch(true);
	@UiField(provided = true) ToggleRadioButton toggleChartGraph = new ToggleRadioButton("charttype");
	@UiField(provided = true) ToggleRadioButton toggleChartMap = new ToggleRadioButton("charttype");

	@UiField InlineHyperlink revenueLink;
	@UiField SpanElement revenueText;
	@UiField InlineHyperlink downloadsLink;
	@UiField SpanElement downloadsText;
	@UiField InlineHyperlink rankingLink;
	@UiField SpanElement rankingText;
	private InlineHTML comingSoon = new InlineHTML("&nbsp;&nbsp;&nbsp;-&nbsp;&nbsp;&nbsp;Coming soon");
	@UiField LIElement revenueItem;
	@UiField LIElement downloadsItem;
	@UiField LIElement rankingItem;

	// @UiField Preloader preloader;

	@UiField(provided = true) CellTable<ItemRevenue> revenueTable = new CellTable<ItemRevenue>(Integer.MAX_VALUE, BootstrapGwtCellTable.INSTANCE);
	@UiField(provided = true) CellTable<AppRanking> revenueTable2 = new CellTable<AppRanking>(Integer.MAX_VALUE, BootstrapGwtCellTable.INSTANCE);

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
	@UiField SpanElement infoDateRange;
	@UiField SpanElement infoTotalRevenue;

	@UiField(provided = true) Chart chartRevenue = new Chart(XDataType.DateXAxisDataType, YDataType.RevenueYAxisDataType);
	@UiField(provided = true) Chart chartDownloads = new Chart(XDataType.DateXAxisDataType, YDataType.DownloadsYAxisDataType);
	@UiField(provided = true) Chart chartRank = new Chart(XDataType.DateXAxisDataType, YDataType.RankingYAxisDataType);
	private static final String SERIES_ID_RANK = "rank";
	private static final String SERIES_ID_REVENUE = "revenue";
	private static final String SERIES_ID_DOWNLOAD = "download";
	private static final String SERIES_ID_REVENUE_CUMULATIVE = "revenueCumulative";
	private static final String SERIES_ID_DOWNLOAD_CUMULATIVE = "downloadCumulative";
	private static final String SERIES_ID_REVENUE_SECONDARY = "revenueSecondary";
	private static final String SERIES_ID_DOWNLOAD_SECONDARY = "downloadSecondary";
	private static final String SERIES_ID_REVENUE_CUMULATIVE_SECONDARY = "revenueCumulativeSecondary";
	private static final String SERIES_ID_DOWNLOAD_CUMULATIVE_SECONDARY = "downloadCumulativeSecondary";

	public ItemPage() {
		initWidget(uiBinder.createAndBindUi(this));

		tabs.put(REVENUE_CHART_TYPE, revenueItem);
		tabs.put(DOWNLOADS_CHART_TYPE, downloadsItem);
		tabs.put(RANKING_CHART_TYPE, rankingItem);
		comingSoon.getElement().getStyle().setFontSize(14.0, Unit.PX);
		setRevenueDownloadTabsEnabled(false);

		if (SessionController.get().isLoggedInUserAdmin()) {
			createColumns();
			// RankController.get().getItemRevenueDataProvider().addDataDisplay(revenueTable);
			RankController.get().getRankDataProvider().addDataDisplay(revenueTable2);
		} else {
			revenueTable2.removeFromParent();
		}
		revenueTable.removeFromParent();

		tablePlaceholder.add(itemRevenuePlaceholder);

		FilterHelper.addCountries(countrySelector, SessionController.get().isLoggedInUserAdmin());
		FilterHelper.addStores(storeSelector, SessionController.get().isLoggedInUserAdmin());
		dateSelector.addFixedRanges(FilterHelper.getDefaultDateRanges());
		updateFromFilter();
	}

	public void setItem(Item item) {
		title.setInnerText(item.name);
		creatorName.setInnerText(item.creatorName);

		if (item.mediumImage != null) {
			image.setUrl(item.mediumImage);
			image.removeStyleName(io.reflection.app.client.res.Styles.STYLES_INSTANCE.reflection().unknownAppSmall());
		} else {
			image.setUrl("");
			image.addStyleName(io.reflection.app.client.res.Styles.STYLES_INSTANCE.reflection().unknownAppSmall());
		}
		if (item.smallImage != null) {
			imageTable.setUrl(item.smallImage);
			imageTable.removeStyleName(io.reflection.app.client.res.Styles.STYLES_INSTANCE.reflection().unknownAppSmall());
		} else {
			imageTable.setUrl("");
			imageTable.addStyleName(io.reflection.app.client.res.Styles.STYLES_INSTANCE.reflection().unknownAppSmall());
		}

		Store s = StoreController.get().getStore(item.source);
		storeName.setInnerText((s == null || s.name == null || s.name.length() == 0) ? item.source.toUpperCase() : s.name);

		viewInStore.setHref(StoreController.get().getExternalUri(item));

		iapDescription = DataTypeHelper.itemIapState(item, " + In App Purchases", "", "");
	}

	public void setPrice(String currency, Float value) {
		if (currency != null && value != null) {
			setPriceInnerText(FormattingHelper.asPriceString(currency, value.floatValue()) + iapDescription);
		} else {
			setPriceInnerText("-");
		}
	}

	public void setPriceInnerText(String s) {
		if (s != null) {
			spinnerLoader.setVisible(false);
			price.setInnerText(s);
		} else {
			price.setInnerText("");
			spinnerLoader.setVisible(true);
		}
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
				revenueTable2.setRowCount(0, true);
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

		String sorterSvg = "<svg version=\"1.1\" x=\"0px\" y=\"0px\" viewBox=\"0 0 7 10\" enable-background=\"new 0 0 7 10\" xml:space=\"preserve\" class=\"sort-svg\"><path class=\"ascending\" d=\"M0.4,4.1h6.1c0.1,0,0.2,0,0.3-0.1C7,3.9,7,3.8,7,3.7c0-0.1,0-0.2-0.1-0.3L3.8,0.1C3.7,0,3.6,0,3.5,0C3.4,0,3.3,0,3.2,0.1L0.1,3.3C0,3.4,0,3.5,0,3.7C0,3.8,0,3.9,0.1,4C0.2,4.1,0.3,4.1,0.4,4.1z\"></path><path class=\"descending\" d=\"M6.6,5.9H0.4c-0.1,0-0.2,0-0.3,0.1C0,6.1,0,6.2,0,6.3c0,0.1,0,0.2,0.1,0.3l3.1,3.2C3.3,10,3.4,10,3.5,10c0.1,0,0.2,0,0.3-0.1l3.1-3.2C7,6.6,7,6.5,7,6.3C7,6.2,7,6.1,6.9,6C6.8,5.9,6.7,5.9,6.6,5.9z\"></path></svg>";

		TextColumn<AppRanking> dateColumn = new TextColumn<AppRanking>() {

			@Override
			public String getValue(AppRanking object) {
				return object.date != null ? FormattingHelper.DATE_FORMATTER_EEE_DD_MMM_YYYY.format(object.date) : "-";
			}
		};
		SafeHtmlHeader dateHeader = new SafeHtmlHeader(SafeHtmlUtils.fromTrustedString("Revenue Generated " + sorterSvg));
		dateHeader.setHeaderStyleNames(Styles.STYLES_INSTANCE.reflectionMainStyle().canBeSorted());
		revenueTable2.addColumn(dateColumn, dateHeader);
		dateColumn.setCellStyleNames(Styles.STYLES_INSTANCE.reflectionMainStyle().dateValue());

		TextColumn<AppRanking> revenueColumn = new TextColumn<AppRanking>() {

			@Override
			public String getValue(AppRanking object) {
				if (infoTotalRevenue.getInnerText().equals("")) {
					infoTotalRevenue.setInnerText(FormattingHelper.asWholeMoneyString(object.currency, object.total.floatValue()));
				}
				return object.revenue != null ? FormattingHelper.asWholeMoneyString(object.currency, object.revenue.floatValue()) : "-";
			}
		};
		SafeHtmlHeader revenueHeader = new SafeHtmlHeader(SafeHtmlUtils.fromTrustedString("Revenue Generated " + sorterSvg));
		revenueHeader.setHeaderStyleNames(Styles.STYLES_INSTANCE.reflectionMainStyle().canBeSorted());
		revenueTable2.addColumn(revenueColumn, revenueHeader);
		revenueColumn.setCellStyleNames(Styles.STYLES_INSTANCE.reflectionMainStyle().revenueValue());

		Column<AppRanking, SafeHtml> revenueForPeriodColumn = new Column<AppRanking, SafeHtml>(new SafeHtmlCell()) {

			@Override
			public SafeHtml getValue(AppRanking object) {
				SafeHtml value = SafeHtmlUtils.fromSafeConstant("");
				if (object.revenuePercentForPeriod != null) {
					String percentage = FormattingHelper.TWO_DECIMALS_FORMATTER.format(object.revenuePercentForPeriod.floatValue() * 100);
					value = SafeHtmlUtils.fromTrustedString("<span>" + percentage + "%</span><div class=\""
							+ Styles.STYLES_INSTANCE.reflectionMainStyle().dataBar() + "\"><div style=\"width: " + percentage + "%\"></div></div>");
				}
				return value;
			}
		};
		SafeHtmlHeader revenueForPeriodHeader = new SafeHtmlHeader(SafeHtmlUtils.fromTrustedString("% of Total Revenue for Period " + sorterSvg));
		revenueForPeriodHeader.setHeaderStyleNames(Styles.STYLES_INSTANCE.reflectionMainStyle().canBeSorted());
		revenueTable2.addColumn(revenueForPeriodColumn, revenueForPeriodHeader);
		revenueForPeriodColumn.setCellStyleNames(Styles.STYLES_INSTANCE.reflectionMainStyle().revenuePercentage());

		revenueTable2.setColumnWidth(dateColumn, 25.0, Unit.PCT);
		revenueTable2.setColumnWidth(revenueColumn, 25.0, Unit.PCT);
		revenueTable2.setColumnWidth(revenueForPeriodColumn, 50.0, Unit.PCT);

	}

	@UiHandler("storeSelector")
	void onAppStoreValueChanged(ChangeEvent event) {
		FilterController.get().setStore(storeSelector.getValue(storeSelector.getSelectedIndex()));
	}

	@UiHandler("countrySelector")
	void onCountryValueChanged(ChangeEvent event) {
		FilterController.get().setCountry(countrySelector.getValue(countrySelector.getSelectedIndex()));
	}

	@UiHandler("dateSelector")
	void onDateRangeValueChanged(ValueChangeEvent<DateRange> event) {
		FilterController fc = FilterController.get();

		fc.start();
		fc.setEndDate(event.getValue().getTo());
		fc.setStartDate(event.getValue().getFrom());
		fc.commit();
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
	}

	@UiHandler("overlayDownloadsSwitch")
	void onOverlayDownloadsSwitchValueChanged(ValueChangeEvent<Boolean> event) {
		chartRevenue.setSeriesVisible(SERIES_ID_DOWNLOAD_SECONDARY, event.getValue().booleanValue() && !cumulativeChartSwitch.getValue().booleanValue());
		chartRevenue.setSeriesVisible(SERIES_ID_DOWNLOAD_CUMULATIVE_SECONDARY, event.getValue().booleanValue()
				&& cumulativeChartSwitch.getValue().booleanValue());
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

	@UiHandler("toggleChartGraph")
	void onToggleChartGraphSelected(ValueChangeEvent<Boolean> event) {
		setChartGraphsVisible(true);
	}

	@UiHandler("toggleChartMap")
	void onToggleChartMapSelected(ValueChangeEvent<Boolean> event) {
		setChartGraphsVisible(false);
	}

	private void updateFromFilter() {
		FilterController fc = FilterController.get();

		DateRange dateRange = new DateRange();

		dateRange.setFrom(fc.getStartDate());
		dateRange.setTo(fc.getEndDate());
		dateSelector.setValue(dateRange);
		infoDateRange.setInnerText(FormattingHelper.DATE_FORMATTER_DD_MMM_YYYY.format(dateRange.getFrom()) + " - "
				+ FormattingHelper.DATE_FORMATTER_DD_MMM_YYYY.format(dateRange.getTo()));

		storeSelector.setSelectedIndex(FormHelper.getItemIndex(storeSelector, fc.getFilter().getStoreA3Code()));
		countrySelector.setSelectedIndex(FormHelper.getItemIndex(countrySelector, fc.getFilter().getCountryA2Code()));

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
				updateFromFilter();
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
					infoTotalRevenue.setInnerText("");
					setLoadingSpinnerEnabled(true);
					setPriceInnerText(null);
					getChartData();
				}

				setChartGraphsVisible(toggleChartGraph.getValue());

			}

			// AlertBoxHelper.configureAlert(mAlertBox, AlertBoxType.SuccessAlertBoxType, false, "Item",
			// " - Normally we would display items details for (" + view + ").", false).setVisible(true);

		} else {
			// AlertBoxHelper.configureAlert(mAlertBox, AlertBoxType.DangerAlertBoxType, false, "Item", " - We did not find the requrested item!", false)
			// .setVisible(true);

			PageType.RanksPageType.show(NavigationController.VIEW_ACTION_PARAMETER_VALUE, OVERALL_LIST_TYPE, FilterController.get().asRankFilterString());
		}
	}

	private void setChartGraphsVisible(boolean visible) {
		if (visible) {
			// TODO hide map
			switch (YDataType.fromString(selectedTab)) {
			case RevenueYAxisDataType:
				overlayRevenuesSwitch.setVisible(false);
				overlayDownloadsSwitch.setVisible(true);
				cumulativeChartSwitch.setVisible(true);
				chartRevenue.setVisible(true);
				chartDownloads.setVisible(false);
				chartRank.setVisible(false);
				break;
			case DownloadsYAxisDataType:
				overlayDownloadsSwitch.setVisible(false);
				overlayRevenuesSwitch.setVisible(true);
				cumulativeChartSwitch.setVisible(true);
				chartDownloads.setVisible(true);
				chartRevenue.setVisible(false);
				chartRank.setVisible(false);
				break;
			case RankingYAxisDataType:
				overlayRevenuesSwitch.setVisible(true);
				overlayDownloadsSwitch.setVisible(true);
				cumulativeChartSwitch.setVisible(false);
				chartRank.setVisible(true);
				chartRevenue.setVisible(false);
				chartDownloads.setVisible(false);
				break;
			}
		} else {
			chartRevenue.setVisible(false);
			chartDownloads.setVisible(false);
			chartRank.setVisible(false);
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

	private void displayItemDetails(Rank rank) {

		setItem(item);

		if (rank != null) {
			setPrice(rank.currency, rank.price);
		} else {
			setPriceInnerText(null);
		}
	}

	private void refreshTabs() {
		for (String key : tabs.keySet()) {
			tabs.get(key).removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().isActive());
		}

		tabs.get(selectedTab).addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().isActive());
	}

	private void getChartData() {
		if (item != null) {
			chartRevenue.setLoading(true);
			chartDownloads.setLoading(true);
			chartRank.setLoading(true);
			// preloader.show(true);
			RankController.get().cancelRequestItemRanks();
			RankController.get().cancelRequestItemSalesRanks();
			revenueTable2.setRowCount(0, true);
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

	private void drawData(List<Rank> ranks) {
		chartRank.setRankingType(RankType.PositionRankingType);
		chartRank.drawData(ranks, SERIES_ID_RANK, ChartHelper.TYPE_LINE, ColorHelper.getReflectionGreen(), false, false);
		chartRevenue.drawData(ranks, SERIES_ID_REVENUE, ChartHelper.TYPE_AREA, ColorHelper.getReflectionPurple(), false, cumulativeChartSwitch.getValue()
				.booleanValue());
		chartDownloads.drawData(ranks, SERIES_ID_DOWNLOAD, ChartHelper.TYPE_AREA, ColorHelper.getReflectionRed(), false, cumulativeChartSwitch.getValue()
				.booleanValue());
		chartRevenue.drawData(ranks, SERIES_ID_REVENUE_CUMULATIVE, ChartHelper.TYPE_AREA, ColorHelper.getReflectionPurple(), true, !cumulativeChartSwitch
				.getValue().booleanValue());
		chartDownloads.drawData(ranks, SERIES_ID_DOWNLOAD_CUMULATIVE, ChartHelper.TYPE_AREA, ColorHelper.getReflectionRed(), true, !cumulativeChartSwitch
				.getValue().booleanValue());
		chartRevenue.drawOppositeData(ranks, SERIES_ID_DOWNLOAD_SECONDARY, ChartHelper.TYPE_LINE, ColorHelper.getReflectionRed(), false, cumulativeChartSwitch
				.getValue().booleanValue() || !overlayDownloadsSwitch.getValue().booleanValue(), YDataType.DownloadsYAxisDataType);
		chartDownloads.drawOppositeData(ranks, SERIES_ID_REVENUE_SECONDARY, ChartHelper.TYPE_LINE, ColorHelper.getReflectionPurple(), false,
				cumulativeChartSwitch.getValue().booleanValue() || !overlayRevenuesSwitch.getValue().booleanValue(), YDataType.RevenueYAxisDataType);
		chartRevenue.drawOppositeData(ranks, SERIES_ID_DOWNLOAD_CUMULATIVE_SECONDARY, ChartHelper.TYPE_LINE, ColorHelper.getReflectionRed(), true,
				!cumulativeChartSwitch.getValue().booleanValue() || !overlayDownloadsSwitch.getValue().booleanValue(), YDataType.DownloadsYAxisDataType);
		chartDownloads.drawOppositeData(ranks, SERIES_ID_REVENUE_CUMULATIVE_SECONDARY, ChartHelper.TYPE_LINE, ColorHelper.getReflectionPurple(), true,
				!cumulativeChartSwitch.getValue().booleanValue() || !overlayRevenuesSwitch.getValue().booleanValue(), YDataType.RevenueYAxisDataType);
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
				drawData(output.ranks);
			} else {
				setPriceInnerText("-");
				setLoadingSpinnerEnabled(false);
			}
		} else {
			setPriceInnerText("-");
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
		setPriceInnerText("-");
		setLoadingSpinnerEnabled(false);

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
				drawData(output.ranks);
			} else {
				setPriceInnerText("-");
				setLoadingSpinnerEnabled(false);
			}
		} else {
			setPriceInnerText("-");
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
		setPriceInnerText("-");
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
				setItem(item);
				if (MyAppsPage.COMING_FROM_PARAMETER.equals(comingPage)) {
					RankController.get().fetchItemSalesRanks(item);
				} else {
					RankController.get().fetchItemRanks(item);
				}
			}
		} else {
			setPriceInnerText("-");
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
		setPriceInnerText("-");
		setLoadingSpinnerEnabled(false);
	}

	private void setItemInfo(Item i) {
		if (i.name != null) {
			item.name = i.name;
		}
		if (i.creatorName != null) {
			item.creatorName = i.creatorName;
		}
		if (i.smallImage != null) {
			item.smallImage = i.smallImage;
		}
		if (i.mediumImage != null) {
			item.mediumImage = i.mediumImage;
		}
		if (i.largeImage != null) {
			item.largeImage = i.largeImage;
		}
	}

	private void setRevenueDownloadTabsEnabled(boolean enable) {
		if (enable) {
			revenueText.setInnerText("Revenue");
			revenueItem.getStyle().setCursor(Cursor.DEFAULT);
			downloadsText.setInnerText("Downloads");
			downloadsItem.getStyle().setCursor(Cursor.DEFAULT);
			revenueLink.setTargetHistoryToken(PageType.ItemPageType.asTargetHistoryToken(NavigationController.VIEW_ACTION_PARAMETER_VALUE, internalId,
					REVENUE_CHART_TYPE, comingPage, filterContents));
			downloadsLink.setTargetHistoryToken(PageType.ItemPageType.asTargetHistoryToken(NavigationController.VIEW_ACTION_PARAMETER_VALUE, internalId,
					DOWNLOADS_CHART_TYPE, comingPage, filterContents));
			tabs.put(REVENUE_CHART_TYPE, revenueItem);
			tabs.put(DOWNLOADS_CHART_TYPE, downloadsItem);
		} else {
			revenueText.setInnerText("Revenue" + comingSoon);
			revenueItem.getStyle().setCursor(Cursor.DEFAULT);
			downloadsText.setInnerText("Downloads" + comingSoon);
			downloadsItem.getStyle().setCursor(Cursor.DEFAULT);
			revenueLink.setTargetHistoryToken(NavigationController.get().getStack().toString());
			downloadsLink.setTargetHistoryToken(NavigationController.get().getStack().toString());
			for (String key : tabs.keySet()) {
				tabs.get(key).removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().isActive());
			}
			tabs.get(RANKING_CHART_TYPE).addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().isActive());
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
			chartRevenue.rearrangeXAxisLabels();
			chartDownloads.rearrangeXAxisLabels();
			chartRank.rearrangeXAxisLabels();
			chartRevenue.resize();
			chartDownloads.resize();
			chartRank.resize();
		}
	}

}
