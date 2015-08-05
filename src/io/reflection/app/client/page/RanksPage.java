//
//  RanksPage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 3 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.page;

import static io.reflection.app.client.controller.FilterController.CATEGORY_KEY;
import static io.reflection.app.client.controller.FilterController.COUNTRY_KEY;
import static io.reflection.app.client.controller.FilterController.DAILY_DATA_KEY;
import static io.reflection.app.client.controller.FilterController.DOWNLOADS_DAILY_DATA_TYPE;
import static io.reflection.app.client.controller.FilterController.END_DATE_KEY;
import static io.reflection.app.client.controller.FilterController.FREE_LIST_TYPE;
import static io.reflection.app.client.controller.FilterController.GROSSING_LIST_TYPE;
import static io.reflection.app.client.controller.FilterController.OVERALL_LIST_TYPE;
import static io.reflection.app.client.controller.FilterController.PAID_LIST_TYPE;
import static io.reflection.app.client.controller.FilterController.REVENUE_DAILY_DATA_TYPE;
import static io.reflection.app.client.controller.FilterController.STORE_KEY;
import static io.reflection.app.client.helper.FormattingHelper.WHOLE_NUMBER_FORMATTER;
import io.reflection.app.api.core.shared.call.GetAllTopItemsRequest;
import io.reflection.app.api.core.shared.call.GetAllTopItemsResponse;
import io.reflection.app.api.core.shared.call.event.GetAllTopItemsEventHandler;
import io.reflection.app.client.DefaultEventBus;
import io.reflection.app.client.cell.AppRankCell;
import io.reflection.app.client.component.FormDateBox;
import io.reflection.app.client.component.LoadingBar;
import io.reflection.app.client.component.Selector;
import io.reflection.app.client.component.ToggleRadioButton;
import io.reflection.app.client.controller.FilterController;
import io.reflection.app.client.controller.FilterController.Filter;
import io.reflection.app.client.controller.ItemController;
import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.controller.NavigationController.Stack;
import io.reflection.app.client.controller.RankController;
import io.reflection.app.client.controller.ServiceConstants;
import io.reflection.app.client.controller.SessionController;
import io.reflection.app.client.handler.FilterEventHandler;
import io.reflection.app.client.handler.NavigationEventHandler;
import io.reflection.app.client.helper.AnimationHelper;
import io.reflection.app.client.helper.FilterHelper;
import io.reflection.app.client.helper.FormHelper;
import io.reflection.app.client.helper.FormattingHelper;
import io.reflection.app.client.helper.ResponsiveDesignHelper;
import io.reflection.app.client.helper.TooltipHelper;
import io.reflection.app.client.part.BootstrapGwtCellTable;
import io.reflection.app.client.part.datatypes.RanksGroup;
import io.reflection.app.client.res.Styles;
import io.reflection.app.client.res.Styles.ReflectionMainStyles;
import io.reflection.app.datatypes.shared.Rank;
import io.reflection.app.shared.util.DataTypeHelper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ShowRangeEvent;
import com.google.gwt.event.logical.shared.ShowRangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
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
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author billy1380
 * 
 */
public class RanksPage extends Page implements FilterEventHandler, // SessionEventHandler, IsAuthorisedEventHandler,
		NavigationEventHandler, GetAllTopItemsEventHandler {

	private static RanksPageUiBinder uiBinder = GWT.create(RanksPageUiBinder.class);

	interface RanksPageUiBinder extends UiBinder<Widget, RanksPage> {}

	interface RanksPageStyle extends CssResource {

		String emptyTableContainer();

		String emptyTableHeading();

	}

	public static final int SELECTED_TAB_PARAMETER_INDEX = 0;
	public static final int VIEW_ALL_LENGTH_VALUE = Integer.MAX_VALUE;
	public static final String ALL_TEXT = "Overview / All";
	public static final String COMING_FROM_PARAMETER = "leaderboard";

	interface AllAdminCodeTemplate extends SafeHtmlTemplates {
		AllAdminCodeTemplate INSTANCE = GWT.create(AllAdminCodeTemplate.class);

		@Template(ALL_TEXT
				+ " <span style=\"background-color: #474949;color: #fff;float: right;display: inline-block;padding: 0px 3px; margin-left: 20px;\">{0}</span>")
		SafeHtml code(Long code);
	}

	@UiField(provided = true) CellTable<RanksGroup> leaderboardTable = new CellTable<RanksGroup>(ServiceConstants.STEP_VALUE, BootstrapGwtCellTable.INSTANCE);

	private HTMLPanel loadingIndicatorAll = AnimationHelper.createLoadingIndicator(5, 4);
	private HTMLPanel loadingIndicatorList = AnimationHelper.createLoadingIndicator(5, 5);

	@UiField DivElement dateSelectContainer;
	@UiField FormDateBox dateBox;
	Date currentDate = FilterHelper.getToday();
	@UiField Selector appStoreListBox;
	// @UiField ListBox mListType;
	@UiField Selector countryListBox;
	@UiField Selector categoryListBox;
	@UiField(provided = true) ToggleRadioButton toggleRevenue = new ToggleRadioButton("dailydatatoggle");
	@UiField(provided = true) ToggleRadioButton toggleDownloads = new ToggleRadioButton("dailydatatoggle");
	@UiField HTMLPanel dailyDataContainer;

	@UiField InlineHyperlink allLink;
	@UiField SpanElement overviewAllText;
	@UiField SpanElement paidText;
	@UiField SpanElement grossingText;
	@UiField InlineHyperlink freeLink;
	@UiField InlineHyperlink grossingLink;
	@UiField InlineHyperlink paidLink;

	@UiField LIElement allItem;
	@UiField LIElement freeItem;
	@UiField LIElement grossingItem;
	@UiField LIElement paidItem;

	@UiField Button viewAllBtn;
	@UiField SpanElement viewAllSpan;
	@UiField InlineHyperlink redirect;

	private Column<RanksGroup, SafeHtml> rankColumn;
	private Column<RanksGroup, Rank> grossingColumn;
	private Column<RanksGroup, Rank> freeColumn;
	private Column<RanksGroup, Rank> paidColumn;
	private Column<RanksGroup, SafeHtml> priceColumn;
	private Column<RanksGroup, SafeHtml> downloadsColumn;
	private Column<RanksGroup, SafeHtml> revenueColumn;
	private Column<RanksGroup, SafeHtml> iapColumn;

	@SuppressWarnings("rawtypes") private Column lastOrderedColumn = rankColumn;

	private Map<String, LIElement> tabs = new HashMap<String, LIElement>();

	private String sorterSvg = "<svg version=\"1.1\" x=\"0px\" y=\"0px\" viewBox=\"0 0 7 10\" enable-background=\"new 0 0 7 10\" xml:space=\"preserve\" class=\"sort-svg\"><path class=\"ascending\" d=\"M0.4,4.1h6.1c0.1,0,0.2,0,0.3-0.1C7,3.9,7,3.8,7,3.7c0-0.1,0-0.2-0.1-0.3L3.8,0.1C3.7,0,3.6,0,3.5,0C3.4,0,3.3,0,3.2,0.1L0.1,3.3C0,3.4,0,3.5,0,3.7C0,3.8,0,3.9,0.1,4C0.2,4.1,0.3,4.1,0.4,4.1z\"></path><path class=\"descending\" d=\"M6.6,5.9H0.4c-0.1,0-0.2,0-0.3,0.1C0,6.1,0,6.2,0,6.3c0,0.1,0,0.2,0.1,0.3l3.1,3.2C3.3,10,3.4,10,3.5,10c0.1,0,0.2,0,0.3-0.1l3.1-3.2C7,6.6,7,6.5,7,6.3C7,6.2,7,6.1,6.9,6C6.8,5.9,6.7,5.9,6.6,5.9z\"></path></svg>";
	private SafeHtmlHeader downloadsHeader = new SafeHtmlHeader(SafeHtmlUtils.fromTrustedString("Downloads " + sorterSvg));
	private SafeHtmlHeader revenueHeader = new SafeHtmlHeader(SafeHtmlUtils.fromTrustedString("Revenue " + sorterSvg));
	private TextHeader rankHeader = new TextHeader("Rank");
	private TextHeader paidHeader = new TextHeader("Paid");
	private TextHeader freeHeader = new TextHeader("Free");
	private TextHeader grossingHeader = new TextHeader("Grossing");
	private TextHeader priceHeader = new TextHeader("Price");
	private SafeHtmlHeader iapHeader = new SafeHtmlHeader(
			SafeHtmlUtils.fromTrustedString("<span class=\"js-tooltip\" data-tooltip=\"In App Purchases\">IAP</span>"));

	private String selectedTab = OVERALL_LIST_TYPE;
	private LoadingBar loadingBar = new LoadingBar(false);
	private ReflectionMainStyles style = Styles.STYLES_INSTANCE.reflectionMainStyle();

	public RanksPage() {
		initWidget(uiBinder.createAndBindUi(this));

		// if (!SessionController.get().isLoggedInUserAdmin()) {
		dailyDataContainer.removeFromParent();
		// }

		if (!SessionController.get().isLoggedInUserAdmin()) {
			countryListBox.setTooltip("This field is currently locked but will soon be editable as we integrate more data");
			appStoreListBox.setTooltip("This field is currently locked but will soon be editable as we integrate more data");
			categoryListBox.setTooltip("This field is currently locked but will soon be editable as we integrate more data");
		}

		dateBox.getDatePicker().addShowRangeHandler(new ShowRangeHandler<Date>() {

			@Override
			public void onShowRange(ShowRangeEvent<Date> event) {
				FilterHelper.disableFutureDates(dateBox.getDatePicker());
			}
		});

		FilterHelper.addStores(appStoreListBox, SessionController.get().isLoggedInUserAdmin());
		FilterHelper.addCountries(countryListBox, SessionController.get().isLoggedInUserAdmin());
		FilterHelper.addCategories(categoryListBox, SessionController.get().isLoggedInUserAdmin());

		// set the overall tab title (this is because it is modified for admins to contain the gather code)
		overviewAllText.setInnerText(ALL_TEXT);

		createColumns();

		tabs.put(OVERALL_LIST_TYPE, allItem);
		tabs.put(FREE_LIST_TYPE, freeItem);
		tabs.put(PAID_LIST_TYPE, paidItem);
		tabs.put(GROSSING_LIST_TYPE, grossingItem);

		// Add click event to LI element so the event is fired when clicking on the whole tab
		Event.sinkEvents(allItem, Event.ONCLICK);
		Event.sinkEvents(freeItem, Event.ONCLICK);
		Event.sinkEvents(paidItem, Event.ONCLICK);
		Event.sinkEvents(grossingItem, Event.ONCLICK);
		Event.setEventListener(allItem, new EventListener() {

			@Override
			public void onBrowserEvent(Event event) {
				if (Event.ONCLICK == event.getTypeInt()) {
					History.newItem(allLink.getTargetHistoryToken());
				}
			}
		});
		Event.setEventListener(freeItem, new EventListener() {

			@Override
			public void onBrowserEvent(Event event) {
				if (Event.ONCLICK == event.getTypeInt()) {
					History.newItem(freeLink.getTargetHistoryToken());
				}
			}
		});
		Event.setEventListener(paidItem, new EventListener() {

			@Override
			public void onBrowserEvent(Event event) {
				if (Event.ONCLICK == event.getTypeInt()) {
					History.newItem(paidLink.getTargetHistoryToken());
				}
			}
		});
		Event.setEventListener(grossingItem, new EventListener() {

			@Override
			public void onBrowserEvent(Event event) {
				if (Event.ONCLICK == event.getTypeInt()) {
					History.newItem(grossingLink.getTargetHistoryToken());
				}
			}
		});

		HTMLPanel emptyTableWidget = new HTMLPanel("<h6>No ranking data for filter!</h6>");
		emptyTableWidget.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		emptyTableWidget.getElement().getStyle().setHeight(100.0, Unit.PX);
		emptyTableWidget.getElement().getStyle().setPaddingTop(35.0, Unit.PX);
		leaderboardTable.setEmptyTableWidget(emptyTableWidget);

		leaderboardTable.setLoadingIndicator(loadingIndicatorAll);

		RankController.get().addDataDisplay(leaderboardTable);

		dateSelectContainer.addClassName("js-tooltip");
		dateSelectContainer.setAttribute("data-tooltip", "Select a date");
		TooltipHelper.updateHelperTooltip();

		loadingBar.show();
	}

	private void createColumns() {
		ListHandler<RanksGroup> columnSortHandler = new ListHandler<RanksGroup>(RankController.get().getList()) {
			/*
			 * (non-Javadoc)
			 * 
			 * @see com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler#onColumnSort(com.google.gwt.user.cellview.client.ColumnSortEvent)
			 */
			@Override
			public void onColumnSort(ColumnSortEvent event) {
				super.onColumnSort(event);
				downloadsHeader.setHeaderStyleNames(style.canBeSorted());
				revenueHeader.setHeaderStyleNames(style.canBeSorted());
				if (event.getColumn() == downloadsColumn) {
					if (lastOrderedColumn == downloadsColumn && event.isSortAscending()) {
						RankController.get().sortByRank(selectedTab, false);
						downloadsHeader.setHeaderStyleNames(style.canBeSorted());
						lastOrderedColumn = rankColumn;
						leaderboardTable.getColumnSortList().push(rankColumn);
					} else {
						RankController.get().sortByDownloads(selectedTab, event.isSortAscending());
						downloadsHeader.setHeaderStyleNames(style.canBeSorted() + " " + (event.isSortAscending() ? style.isAscending() : style.isDescending()));
						lastOrderedColumn = downloadsColumn;
					}
				} else if (event.getColumn() == revenueColumn) {
					if (lastOrderedColumn == revenueColumn && event.isSortAscending()) {
						RankController.get().sortByRank(selectedTab, false);
						revenueHeader.setHeaderStyleNames(style.canBeSorted());
						lastOrderedColumn = rankColumn;
						leaderboardTable.getColumnSortList().push(rankColumn);
					} else {
						RankController.get().sortByRevenue(selectedTab, event.isSortAscending());
						revenueHeader.setHeaderStyleNames(style.canBeSorted() + " " + (event.isSortAscending() ? style.isAscending() : style.isDescending()));
						lastOrderedColumn = revenueColumn;
					}
				}
				leaderboardTable.setRowData(0, RankController.get().getList());

				TooltipHelper.updateHelperTooltip();

			}
		};

		leaderboardTable.addColumnSortHandler(columnSortHandler);

		rankColumn = new Column<RanksGroup, SafeHtml>(new SafeHtmlCell()) {

			@Override
			public SafeHtml getValue(RanksGroup object) {
				return (object.free.position != null) ? SafeHtmlUtils.fromTrustedString(object.free.position.toString()) : SafeHtmlUtils
						.fromTrustedString("<span class=\"js-tooltip\" data-tooltip=\"No data available\">-</span>");
			}

		};
		rankColumn.setCellStyleNames(style.mhxte6ciA() + " " + style.mhxte6cID());

		AppRankCell appRankCell = new AppRankCell();

		paidColumn = new Column<RanksGroup, Rank>(appRankCell) {

			@Override
			public Rank getValue(RanksGroup object) {
				return object.paid;
			}
		};
		paidColumn.setCellStyleNames(style.mhxte6ciA());

		freeColumn = new Column<RanksGroup, Rank>(appRankCell) {

			@Override
			public Rank getValue(RanksGroup object) {
				return object.free;
			}

		};
		freeColumn.setCellStyleNames(style.mhxte6ciA());

		grossingColumn = new Column<RanksGroup, Rank>(appRankCell) {

			@Override
			public Rank getValue(RanksGroup object) {
				return object.grossing;
			}
		};
		grossingColumn.setCellStyleNames(style.mhxte6ciA());

		priceColumn = new Column<RanksGroup, SafeHtml>(new SafeHtmlCell()) {

			@Override
			public SafeHtml getValue(RanksGroup object) {
				Rank rank = rankForListType(object);
				return (rank.currency != null && rank.price != null) ? SafeHtmlUtils.fromSafeConstant(FormattingHelper.asPriceString(rank.currency,
						rank.price.floatValue())) : SafeHtmlUtils.fromTrustedString("<span class=\"js-tooltip\" data-tooltip=\"No data available\">-</span>");
			}
		};
		priceColumn.setCellStyleNames(style.mhxte6ciA());

		downloadsColumn = new Column<RanksGroup, SafeHtml>(new SafeHtmlCell()) {

			@Override
			public SafeHtml getValue(RanksGroup object) {
				Rank rank = rankForListType(object);
				return (rank.downloads != null) ? SafeHtmlUtils.fromSafeConstant(WHOLE_NUMBER_FORMATTER.format(rank.downloads)) : SafeHtmlUtils
						.fromTrustedString("<span class=\"js-tooltip\" data-tooltip=\"No data available\">-</span>");
			}

		};
		downloadsColumn.setCellStyleNames(style.mhxte6ciA());
		downloadsColumn.setSortable(true);
		downloadsHeader.setHeaderStyleNames(style.canBeSorted());

		revenueColumn = new Column<RanksGroup, SafeHtml>(new SafeHtmlCell()) {

			@Override
			public SafeHtml getValue(RanksGroup object) {
				Rank rank = rankForListType(object);
				return (rank.currency != null && rank.revenue != null) ? SafeHtmlUtils.fromSafeConstant(FormattingHelper.asWholeMoneyString(rank.currency,
						rank.revenue.floatValue())) : SafeHtmlUtils.fromTrustedString("<span class=\"js-tooltip\" data-tooltip=\"No data available\">-</span>");
			}

		};
		revenueColumn.setCellStyleNames(style.mhxte6ciA());
		revenueColumn.setSortable(true);
		revenueHeader.setHeaderStyleNames(style.canBeSorted());

		iapColumn = new Column<RanksGroup, SafeHtml>(new SafeHtmlCell()) {

			private final String IAP_YES_HTML = "<span class=\"" + style.refIconBefore() + " " + style.refIconBeforeCheck() + "\"></span>";
			private final String IAP_NO_HTML = "<span></span>";

			@Override
			public SafeHtml getValue(RanksGroup object) {
				return SafeHtmlUtils
						.fromSafeConstant(DataTypeHelper.itemIapState(ItemController.get().lookupItem(rankForListType(object).itemId), IAP_YES_HTML,
								IAP_NO_HTML, "<span class=\"js-tooltip " + style.whatsThisTooltipIcon() + "\" data-tooltip=\"No data available\"></span>"));
			}

		};
		iapColumn.setCellStyleNames(style.mhxte6ciA());

		rankHeader.setHeaderStyleNames(style.mhxte6cIF());

		leaderboardTable.getColumnSortList().push(rankColumn);
	}

	/**
	 * @param object
	 * @return
	 */
	protected Rank rankForListType(RanksGroup object) {
		Rank rank = object.grossing;

		if (FREE_LIST_TYPE.equals(selectedTab)) {
			rank = object.free;
		} else if (PAID_LIST_TYPE.equals(selectedTab)) {
			rank = object.paid;
		}

		return rank;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.FilterEventHandler#filterParamChanged(java.lang.String, java.lang.Object, java.lang.Object)
	 */
	@Override
	public <T> void filterParamChanged(String name, T currentValue, T previousValue) {
		if (NavigationController.get().getCurrentPage() == PageType.RanksPageType) {
			boolean foundDailyData = false;
			if (name != null
					&& (COUNTRY_KEY.equals(name) || STORE_KEY.equals(name) || CATEGORY_KEY.equals(name) || END_DATE_KEY.equals(name) || (foundDailyData = DAILY_DATA_KEY
							.equals(name)))) {

				if (foundDailyData) {
					leaderboardTable.redraw();
				} else {
					loadingBar.show();
					RankController.get().reset();
					RankController.get().fetchTopItems();
					setViewMoreVisible(false);
				}

				PageType.RanksPageType.show("view", selectedTab, FilterController.get().asRankFilterString());
			}
		}
		downloadsHeader.setHeaderStyleNames(style.canBeSorted());
		revenueHeader.setHeaderStyleNames(style.canBeSorted());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.FilterEventHandler#filterParamsChanged(io.reflection.app.client.controller.FilterController.Filter, java.util.Map)
	 */
	@Override
	public void filterParamsChanged(Filter currentFilter, Map<String, ?> previousValues) {
		if (NavigationController.get().getCurrentPage() == PageType.RanksPageType) {

			boolean foundDailyData = false;

			if (previousValues.get(COUNTRY_KEY) != null || previousValues.get(STORE_KEY) != null || previousValues.get(CATEGORY_KEY) != null
					|| previousValues.get(END_DATE_KEY) != null || (foundDailyData = (previousValues.get(DAILY_DATA_KEY) != null))) {

				if (foundDailyData) {
					leaderboardTable.redraw();
				} else {
					loadingBar.show();
					RankController.get().reset();
					RankController.get().fetchTopItems();
					setViewMoreVisible(false);
				}

				PageType.RanksPageType.show("view", selectedTab, FilterController.get().asRankFilterString());
			}
		}
		downloadsHeader.setHeaderStyleNames(style.canBeSorted());
		revenueHeader.setHeaderStyleNames(style.canBeSorted());
	}

	@UiHandler("appStoreListBox")
	void onAppStoreValueChanged(ChangeEvent event) {
		FilterController.get().setStore(appStoreListBox.getValue(appStoreListBox.getSelectedIndex()));
	}

	// @UiHandler("mListType")
	// void onListTypeValueChanged(ChangeEvent event) {
	// FilterController.get().setListType(mListType.getValue(mListType.getSelectedIndex()));
	// }

	@UiHandler("countryListBox")
	void onCountryValueChanged(ChangeEvent event) {
		FilterController.get().setCountry(countryListBox.getValue(countryListBox.getSelectedIndex()));
	}

	@UiHandler("dateBox")
	void onDateValueChanged(ValueChangeEvent<Date> event) {
		if (event.getValue().after(FilterHelper.getToday())) { // Restore previously selected date
			dateBox.setValue(currentDate);
		} else {
			currentDate.setTime(dateBox.getValue().getTime());
			FilterController fc = FilterController.get();
			fc.start();
			fc.setEndDate(event.getValue());
			Date startDate = fc.getEndDate();
			CalendarUtil.addDaysToDate(startDate, -30);
			fc.setStartDate(startDate);
			fc.commit();
		}
	}

	@UiHandler("categoryListBox")
	void onCategoryListBoxValueChanged(ChangeEvent event) {
		FilterController.get().setCategory(Long.valueOf(categoryListBox.getValue(categoryListBox.getSelectedIndex())));
	}

	@UiHandler("toggleRevenue")
	void onDailyDataRevenueValueChanged(ValueChangeEvent<Boolean> event) {
		FilterController.get().setDailyData(REVENUE_DAILY_DATA_TYPE);
	}

	@UiHandler("toggleDownloads")
	void onDailyDataDownloadsValueChanged(ValueChangeEvent<Boolean> event) {
		FilterController.get().setDailyData(DOWNLOADS_DAILY_DATA_TYPE);
	}

	private void updateFromFilter() {
		FilterController fc = FilterController.get();

		long endTime = fc.getFilter().getEndTime().longValue();
		Date endDate = new Date(endTime);
		dateBox.setValue(endDate);
		currentDate.setTime(endDate.getTime());
		if (SessionController.get().isLoggedInUserAdmin()) {
			appStoreListBox.setSelectedIndex(FormHelper.getItemIndex(appStoreListBox, fc.getFilter().getStoreA3Code()));
			countryListBox.setSelectedIndex(FormHelper.getItemIndex(countryListBox, fc.getFilter().getCountryA2Code()));
			categoryListBox.setSelectedIndex(FormHelper.getItemIndex(categoryListBox, fc.getFilter().getCategoryId().toString()));
		} else {
			appStoreListBox.setSelectedIndex(0);
			countryListBox.setSelectedIndex(0);
			categoryListBox.setSelectedIndex(0);
		}

		String dailyDataType = fc.getFilter().getDailyData();
		if (REVENUE_DAILY_DATA_TYPE.equals(dailyDataType)) {
			toggleRevenue.setValue(true);
		} else {
			toggleDownloads.setValue(true);
		}
	}

	private void refreshRanks() {

		leaderboardTable.setStyleName(style.tableOverall(), OVERALL_LIST_TYPE.equals(selectedTab));
		leaderboardTable.setStyleName(style.tableAppGroup(),
				(FREE_LIST_TYPE.equals(selectedTab) || PAID_LIST_TYPE.equals(selectedTab) || GROSSING_LIST_TYPE.equals(selectedTab)));

		switch (selectedTab) {
		case OVERALL_LIST_TYPE:
			removeAllColumns();
			leaderboardTable.setColumnWidth(rankColumn, 10.0, Unit.PCT);
			leaderboardTable.setColumnWidth(freeColumn, 30.0, Unit.PCT);
			leaderboardTable.setColumnWidth(paidColumn, 30.0, Unit.PCT);
			leaderboardTable.setColumnWidth(grossingColumn, 30.0, Unit.PCT);
			leaderboardTable.addColumn(rankColumn, rankHeader);
			leaderboardTable.addColumn(freeColumn, freeHeader);
			leaderboardTable.addColumn(paidColumn, paidHeader);
			leaderboardTable.addColumn(grossingColumn, grossingHeader);
			leaderboardTable.setLoadingIndicator(loadingIndicatorAll);
			break;
		case FREE_LIST_TYPE:
			removeAllColumns();
			leaderboardTable.setColumnWidth(rankColumn, 10.0, Unit.PCT);
			leaderboardTable.setColumnWidth(freeColumn, 42.0, Unit.PCT);
			leaderboardTable.setColumnWidth(priceColumn, 19.0, Unit.PCT);
			leaderboardTable.setColumnWidth(downloadsColumn, 19.0, Unit.PCT);
			leaderboardTable.setColumnWidth(iapColumn, 10.0, Unit.PCT);
			leaderboardTable.addColumn(rankColumn, rankHeader);
			leaderboardTable.addColumn(freeColumn, freeHeader);
			leaderboardTable.addColumn(priceColumn, priceHeader);
			leaderboardTable.addColumn(downloadsColumn, downloadsHeader);
			leaderboardTable.addColumn(iapColumn, iapHeader);
			leaderboardTable.setLoadingIndicator(loadingIndicatorList);
			priceHeader.setHeaderStyleNames(style.columnHiddenMobile());
			priceColumn.setCellStyleNames(style.mhxte6ciA() + " " + style.columnHiddenMobile());
			leaderboardTable.addColumnStyleName(2, style.columnHiddenMobile());
			break;
		case PAID_LIST_TYPE:
			removeAllColumns();
			leaderboardTable.setColumnWidth(rankColumn, 10.0, Unit.PCT);
			leaderboardTable.setColumnWidth(paidColumn, 42.0, Unit.PCT);
			leaderboardTable.setColumnWidth(priceColumn, 19.0, Unit.PCT);
			leaderboardTable.setColumnWidth(downloadsColumn, 19.0, Unit.PCT);
			leaderboardTable.setColumnWidth(iapColumn, 10.0, Unit.PCT);
			leaderboardTable.addColumn(rankColumn, rankHeader);
			leaderboardTable.addColumn(paidColumn, paidHeader);
			leaderboardTable.addColumn(priceColumn, priceHeader);
			leaderboardTable.addColumn(downloadsColumn, downloadsHeader);
			leaderboardTable.addColumn(iapColumn, iapHeader);
			leaderboardTable.setLoadingIndicator(loadingIndicatorList);
			iapHeader.setHeaderStyleNames(style.columnHiddenMobile());
			iapColumn.setCellStyleNames(style.mhxte6ciA() + " " + style.columnHiddenMobile());
			leaderboardTable.addColumnStyleName(4, style.columnHiddenMobile());
			break;
		case GROSSING_LIST_TYPE:
			removeAllColumns();
			leaderboardTable.setColumnWidth(rankColumn, 10.0, Unit.PCT);
			leaderboardTable.setColumnWidth(grossingColumn, 42.0, Unit.PCT);
			leaderboardTable.setColumnWidth(priceColumn, 19.0, Unit.PCT);
			leaderboardTable.setColumnWidth(revenueColumn, 19.0, Unit.PCT);
			leaderboardTable.setColumnWidth(iapColumn, 10.0, Unit.PCT);
			leaderboardTable.addColumn(rankColumn, rankHeader);
			leaderboardTable.addColumn(grossingColumn, grossingHeader);
			leaderboardTable.addColumn(priceColumn, priceHeader);
			leaderboardTable.addColumn(revenueColumn, revenueHeader);
			leaderboardTable.addColumn(iapColumn, iapHeader);
			leaderboardTable.setLoadingIndicator(loadingIndicatorList);
			iapHeader.setHeaderStyleNames(style.columnHiddenMobile());
			iapColumn.setCellStyleNames(style.mhxte6ciA() + " " + style.columnHiddenMobile());
			leaderboardTable.addColumnStyleName(4, style.columnHiddenMobile());
			break;
		}

		TooltipHelper.updateHelperTooltip();

	}

	private void removeColumn(Column<RanksGroup, ?> column) {
		int currentIndex = leaderboardTable.getColumnIndex(column);

		if (currentIndex != -1) {
			leaderboardTable.removeColumn(column);
		}
	}

	private void removeAllColumns() {
		// Reset styles for mobile
		priceHeader.setHeaderStyleNames("");
		priceColumn.setCellStyleNames(style.mhxte6ciA());
		iapHeader.setHeaderStyleNames("");
		iapColumn.setCellStyleNames(style.mhxte6ciA());
		for (int i = 0; i < leaderboardTable.getColumnCount(); i++) {
			leaderboardTable.removeColumnStyleName(i, style.columnHiddenMobile());
		}

		removeColumn(rankColumn);
		removeColumn(freeColumn);
		removeColumn(paidColumn);
		removeColumn(grossingColumn);
		removeColumn(priceColumn);
		removeColumn(revenueColumn);
		removeColumn(downloadsColumn);
		removeColumn(iapColumn);
	}

	private void refreshTabs() {
		for (String key : tabs.keySet()) {
			tabs.get(key).removeClassName(style.isActive());
		}

		LIElement selected = tabs.get(selectedTab);

		if (selected != null) {
			selected.addClassName(style.isActive());
		}

		ResponsiveDesignHelper.makeTabsResponsive();

		// downloadsHeader.setHeaderStyleNames(style.canBeSorted());
		// revenueHeader.setHeaderStyleNames(style.canBeSorted());
		// RankController.get().sortByRank(selectedTab, false);
	}

	@UiHandler("viewAllBtn")
	void onViewAllButtonClicked(ClickEvent event) {
		if (((Button) event.getSource()).isEnabled()) {
			if (leaderboardTable.getVisibleItemCount() == ServiceConstants.STEP_VALUE) {
				leaderboardTable.setVisibleRange(0, VIEW_ALL_LENGTH_VALUE);
				viewAllSpan.setInnerText("View Less Apps");

				TooltipHelper.updateHelperTooltip();

			} else {
				leaderboardTable.setVisibleRange(0, ServiceConstants.STEP_VALUE);
				viewAllSpan.setInnerText("View All Apps");
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.NavigationEventHandler#navigationChanged(io.reflection.app.client.controller.NavigationController.Stack,
	 * io.reflection.app.client.controller.NavigationController.Stack)
	 */
	@Override
	public void navigationChanged(Stack previous, Stack current) {

		if (PageType.RanksPageType.equals(current.getPage())) {

			if (leaderboardTable.getVisibleItemCount() > 0) {
				setViewMoreVisible(true);
			}

			boolean hasPermission = SessionController.get().loggedInUserHas(DataTypeHelper.PERMISSION_FULL_RANK_VIEW_CODE);

			if (hasPermission) {
				redirect.removeFromParent();
				viewAllBtn.getParent().getElement().appendChild(viewAllBtn.getElement());
			} else {
				viewAllBtn.removeFromParent();
				redirect.getParent().getElement().appendChild(redirect.getElement());
			}

			if (current.getAction() == null || !NavigationController.VIEW_ACTION_PARAMETER_VALUE.equals(current.getAction())) {
				PageType.RanksPageType.show(NavigationController.VIEW_ACTION_PARAMETER_VALUE, OVERALL_LIST_TYPE, FilterController.get().asRankFilterString());
			} else {
				String currentFilter = FilterController.get().asRankFilterString();

				if (currentFilter != null && currentFilter.length() > 0) {
					allLink.setTargetHistoryToken(PageType.RanksPageType.asTargetHistoryToken(NavigationController.VIEW_ACTION_PARAMETER_VALUE,
							OVERALL_LIST_TYPE, currentFilter));
					freeLink.setTargetHistoryToken(PageType.RanksPageType.asTargetHistoryToken(NavigationController.VIEW_ACTION_PARAMETER_VALUE,
							FREE_LIST_TYPE, currentFilter));
					paidLink.setTargetHistoryToken(PageType.RanksPageType.asTargetHistoryToken(NavigationController.VIEW_ACTION_PARAMETER_VALUE,
							PAID_LIST_TYPE, currentFilter));
					grossingLink.setTargetHistoryToken(PageType.RanksPageType.asTargetHistoryToken(NavigationController.VIEW_ACTION_PARAMETER_VALUE,
							GROSSING_LIST_TYPE, currentFilter));
				}

				selectedTab = current.getParameter(SELECTED_TAB_PARAMETER_INDEX);

				refreshTabs();
				refreshRanks();

				// if (SessionController.get().isLoggedInUserAdmin()) {
				// if (FREE_LIST_TYPE.equals(selectedTab) || PAID_LIST_TYPE.equals(selectedTab) || GROSSING_LIST_TYPE.equals(selectedTab)) {
				// setDataFilterVisible(false);
				// } else {
				// setDataFilterVisible(true);
				// selectedTab = OVERALL_LIST_TYPE;
				// }
				// }

				updateFromFilter();
			}
		}
	}

	// private void setDataFilterVisible(boolean visible) {
	// if (visible) {
	// dailyDataContainer.setVisible(true);
	// } else {
	// dailyDataContainer.setVisible(false);
	// }
	// }

	private void setViewMoreVisible(boolean visible) {
		if (viewAllBtn.isAttached()) {
			viewAllBtn.setVisible(visible);
		}
		if (redirect.isAttached()) {
			redirect.setVisible(visible);
		}

	}

	// private void checkPermissions() {
	// List<Permission> permissions = new ArrayList<Permission>();
	//
	// Permission p = new Permission();
	// // p.code = "FRV";
	// p.id = Long.valueOf(1);
	// permissions.add(p);
	//
	// SessionController.get().fetchAuthorisation(null, permissions);
	//
	// redirect.setTargetHistoryToken(PageType.UpgradePageType.toString());
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.Composite#onAttach()
	 */
	@Override
	protected void onAttach() {
		super.onAttach();

		register(DefaultEventBus.get().addHandlerToSource(FilterEventHandler.TYPE, FilterController.get(), this));
		// register(EventController.get().addHandlerToSource(SessionEventHandler.TYPE, SessionController.get(), this));
		// register(EventController.get().addHandlerToSource(IsAuthorisedEventHandler.TYPE, SessionController.get(), this));
		register(DefaultEventBus.get().addHandlerToSource(NavigationEventHandler.TYPE, NavigationController.get(), this));
		register(DefaultEventBus.get().addHandlerToSource(GetAllTopItemsEventHandler.TYPE, RankController.get(), this));

		updateFromFilter();

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.reflection.app.api.core.shared.call.event.GetAllTopItemsEventHandler#getAllTopItemsSuccess(io.reflection.app.api.core.shared.call.GetAllTopItemsRequest
	 * , io.reflection.app.api.core.shared.call.GetAllTopItemsResponse)
	 */
	@Override
	public void getAllTopItemsSuccess(GetAllTopItemsRequest input, GetAllTopItemsResponse output) {
		if (output.status.equals(StatusType.StatusTypeSuccess)) {
			if (output.freeRanks != null) {
				setViewMoreVisible(true);

				if (SessionController.get().isLoggedInUserAdmin()) {
					if (output.freeRanks != null && output.freeRanks.size() > 0 && output.freeRanks.get(0).code != null) {
						overviewAllText.setInnerSafeHtml(AllAdminCodeTemplate.INSTANCE.code(output.freeRanks.get(0).code));
					} else if (output.paidRanks != null && output.paidRanks.size() > 0 && output.paidRanks.get(0).code != null) {
						overviewAllText.setInnerSafeHtml(AllAdminCodeTemplate.INSTANCE.code(output.paidRanks.get(0).code));
					} else if (output.grossingRanks != null && output.grossingRanks.size() > 0 && output.grossingRanks.get(0).code != null) {
						overviewAllText.setInnerSafeHtml(AllAdminCodeTemplate.INSTANCE.code(output.grossingRanks.get(0).code));
					} else {
						overviewAllText.setInnerText(ALL_TEXT);
					}
				}
				loadingBar.hide();
			} else {
				setViewMoreVisible(false);
			}
		} else {
			setViewMoreVisible(false);
		}

		TooltipHelper.updateHelperTooltip();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.reflection.app.api.core.shared.call.event.GetAllTopItemsEventHandler#getAllTopItemsFailure(io.reflection.app.api.core.shared.call.GetAllTopItemsRequest
	 * , java.lang.Throwable)
	 */
	@Override
	public void getAllTopItemsFailure(GetAllTopItemsRequest input, Throwable caught) {
		setViewMoreVisible(false);
	}
}
