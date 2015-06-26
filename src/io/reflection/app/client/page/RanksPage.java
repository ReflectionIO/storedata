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
import io.reflection.app.client.helper.FilterHelper;
import io.reflection.app.client.helper.FormHelper;
import io.reflection.app.client.helper.FormattingHelper;
import io.reflection.app.client.helper.ResponsiveDesignHelper;
import io.reflection.app.client.part.BootstrapGwtCellTable;
import io.reflection.app.client.part.datatypes.RanksGroup;
import io.reflection.app.client.res.Images;
import io.reflection.app.client.res.Styles;
import io.reflection.app.datatypes.shared.Rank;
import io.reflection.app.shared.util.DataTypeHelper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.SpanElement;
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
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
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

	@UiField(provided = true) CellTable<RanksGroup> leaderboardTableOverallDesktop = new CellTable<RanksGroup>(ServiceConstants.STEP_VALUE,
			BootstrapGwtCellTable.INSTANCE);
	@UiField(provided = true) CellTable<RanksGroup> leaderboardTableOverallMobile = new CellTable<RanksGroup>(ServiceConstants.STEP_VALUE,
			BootstrapGwtCellTable.INSTANCE);

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

	private TextColumn<RanksGroup> rankColumn;
	private Column<RanksGroup, Rank> grossingColumn;
	private Column<RanksGroup, Rank> freeColumn;
	private Column<RanksGroup, Rank> paidColumn;
	private TextColumn<RanksGroup> priceColumn;
	private TextColumn<RanksGroup> downloadsColumn;
	private TextColumn<RanksGroup> revenueColumn;
	private Column<RanksGroup, SafeHtml> iapColumn;

	private Map<String, LIElement> tabs = new HashMap<String, LIElement>();

	private TextHeader rankHeader;
	private TextHeader paidHeader;
	private TextHeader freeHeader;
	private TextHeader grossingHeader;
	private TextHeader downloadsHeader;
	private TextHeader revenueHeader;
	private TextHeader iapHeader;
	private TextHeader priceHeader;

	private String selectedTab = OVERALL_LIST_TYPE;

	public RanksPage() {
		initWidget(uiBinder.createAndBindUi(this));

		if (!SessionController.get().isLoggedInUserAdmin()) {
			dailyDataContainer.removeFromParent();
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
		if (SessionController.get().isLoggedInUserAdmin()) {
			tabs.put(PAID_LIST_TYPE, paidItem);
			tabs.put(GROSSING_LIST_TYPE, grossingItem);
		} else {
			paidText.setInnerText("Top Paid - coming soon");
			paidItem.addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().isDisabled());
			grossingText.setInnerText("Top Grossing - coming soon");
			grossingItem.addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().isDisabled());
		}

		HTMLPanel emptyTableWidget = new HTMLPanel("<h6>No ranking data for filter!</h6>");
		leaderboardTableOverallDesktop.setEmptyTableWidget(emptyTableWidget);

		leaderboardTableOverallDesktop.setLoadingIndicator(new Image(Images.INSTANCE.preloader()));
		leaderboardTableOverallMobile.setLoadingIndicator(new Image(Images.INSTANCE.preloader()));

		RankController.get().addDataDisplay(leaderboardTableOverallDesktop);
		RankController.get().addDataDisplay(leaderboardTableOverallMobile);

	}

	private void createColumns() {
		rankColumn = new TextColumn<RanksGroup>() {

			@Override
			public String getValue(RanksGroup object) {
				return (object.free.position != null) ? object.free.position.toString() : "-";
			}

		};
		rankColumn.setCellStyleNames(Styles.STYLES_INSTANCE.reflectionMainStyle().mhxte6ciA() + " " + Styles.STYLES_INSTANCE.reflectionMainStyle().mhxte6cID());

		AppRankCell appRankCell = new AppRankCell();

		paidColumn = new Column<RanksGroup, Rank>(appRankCell) {

			@Override
			public Rank getValue(RanksGroup object) {
				return object.paid;
			}
		};
		paidColumn.setCellStyleNames(Styles.STYLES_INSTANCE.reflectionMainStyle().mhxte6ciA());

		freeColumn = new Column<RanksGroup, Rank>(appRankCell) {

			@Override
			public Rank getValue(RanksGroup object) {
				return object.free;
			}

		};
		freeColumn.setCellStyleNames(Styles.STYLES_INSTANCE.reflectionMainStyle().mhxte6ciA());

		grossingColumn = new Column<RanksGroup, Rank>(appRankCell) {

			@Override
			public Rank getValue(RanksGroup object) {
				return object.grossing;
			}
		};
		grossingColumn.setCellStyleNames(Styles.STYLES_INSTANCE.reflectionMainStyle().mhxte6ciA());

		priceColumn = new TextColumn<RanksGroup>() {

			@Override
			public String getValue(RanksGroup object) {
				Rank rank = rankForListType(object);
				return (rank.currency != null && rank.price != null) ? FormattingHelper.asPriceString(rank.currency, rank.price.floatValue()) : "-";
			}
		};
		priceColumn.setCellStyleNames(Styles.STYLES_INSTANCE.reflectionMainStyle().mhxte6ciA());

		downloadsColumn = new TextColumn<RanksGroup>() {

			@Override
			public String getValue(RanksGroup object) {
				Rank rank = rankForListType(object);
				return (rank.downloads != null) ? WHOLE_NUMBER_FORMATTER.format(rank.downloads) : "-";
			}

		};
		downloadsColumn.setCellStyleNames(Styles.STYLES_INSTANCE.reflectionMainStyle().mhxte6ciA());

		revenueColumn = new TextColumn<RanksGroup>() {

			@Override
			public String getValue(RanksGroup object) {
				Rank rank = rankForListType(object);
				return (rank.currency != null && rank.revenue != null) ? FormattingHelper.asWholeMoneyString(rank.currency, rank.revenue.floatValue()) : "-";
			}

		};
		revenueColumn.setCellStyleNames(Styles.STYLES_INSTANCE.reflectionMainStyle().mhxte6ciA());

		iapColumn = new Column<RanksGroup, SafeHtml>(new SafeHtmlCell()) {

			private final String IAP_DONT_KNOW_HTML = "<span class=\"" + Styles.STYLES_INSTANCE.reflectionMainStyle().refIconBefore() + " "
					+ Styles.STYLES_INSTANCE.reflectionMainStyle().refIconBeforeMinus() + "\"></span>";
			private final String IAP_YES_HTML = "<span class=\"" + Styles.STYLES_INSTANCE.reflectionMainStyle().refIconBefore() + " "
					+ Styles.STYLES_INSTANCE.reflectionMainStyle().refIconBeforeCheck() + "\"></span>";
			private final String IAP_NO_HTML = "<span></span>";

			@Override
			public SafeHtml getValue(RanksGroup object) {
				return SafeHtmlUtils.fromSafeConstant(DataTypeHelper.itemIapState(ItemController.get().lookupItem(rankForListType(object).itemId),
						IAP_YES_HTML, IAP_NO_HTML, IAP_DONT_KNOW_HTML));
			}

		};
		iapColumn.setCellStyleNames(Styles.STYLES_INSTANCE.reflectionMainStyle().mhxte6ciA());

		rankHeader = new TextHeader("Rank");
		rankHeader.setHeaderStyleNames(Styles.STYLES_INSTANCE.reflectionMainStyle().mhxte6cIF());
		paidHeader = new TextHeader("Paid");
		freeHeader = new TextHeader("Free");
		grossingHeader = new TextHeader("Grossing");
		priceHeader = new TextHeader("Price");
		downloadsHeader = new TextHeader("Downloads");
		revenueHeader = new TextHeader("Revenue");
		iapHeader = new TextHeader("IAP");

		leaderboardTableOverallMobile.addColumn(rankColumn, rankHeader);
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
					leaderboardTableOverallDesktop.redraw();
					leaderboardTableOverallMobile.redraw();
				} else {
					RankController.get().reset();
					RankController.get().fetchTopItems();
					setViewMoreVisible(false);
				}

				PageType.RanksPageType.show("view", selectedTab, FilterController.get().asRankFilterString());
			}
		}
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
					leaderboardTableOverallDesktop.redraw();
					leaderboardTableOverallMobile.redraw();
				} else {
					RankController.get().reset();
					RankController.get().fetchTopItems();
					setViewMoreVisible(false);
				}

				PageType.RanksPageType.show("view", selectedTab, FilterController.get().asRankFilterString());
			}
		}

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

		leaderboardTableOverallDesktop.setStyleName(Styles.STYLES_INSTANCE.reflectionMainStyle().tableOverall(), OVERALL_LIST_TYPE.equals(selectedTab));
		leaderboardTableOverallDesktop.setStyleName(Styles.STYLES_INSTANCE.reflectionMainStyle().tableAppGroup(), (FREE_LIST_TYPE.equals(selectedTab)
				|| PAID_LIST_TYPE.equals(selectedTab) || GROSSING_LIST_TYPE.equals(selectedTab)));

		leaderboardTableOverallMobile.setStyleName(Styles.STYLES_INSTANCE.reflectionMainStyle().tableOverall(), OVERALL_LIST_TYPE.equals(selectedTab));
		leaderboardTableOverallMobile.setStyleName(Styles.STYLES_INSTANCE.reflectionMainStyle().tableAppGroup(), (FREE_LIST_TYPE.equals(selectedTab)
				|| PAID_LIST_TYPE.equals(selectedTab) || GROSSING_LIST_TYPE.equals(selectedTab)));

		if (OVERALL_LIST_TYPE.equals(selectedTab)) {
			removeAllColumns();
			leaderboardTableOverallDesktop.setColumnWidth(rankColumn, 10.0, Unit.PCT);
			leaderboardTableOverallDesktop.setColumnWidth(freeColumn, 30.0, Unit.PCT);
			leaderboardTableOverallDesktop.setColumnWidth(paidColumn, 30.0, Unit.PCT);
			leaderboardTableOverallDesktop.setColumnWidth(grossingColumn, 30.0, Unit.PCT);
			addColumn(rankColumn, rankHeader);
			addColumn(freeColumn, freeHeader);
			addColumn(paidColumn, paidHeader);
			addColumn(grossingColumn, grossingHeader);
		} else if (FREE_LIST_TYPE.equals(selectedTab)) {
			removeAllColumns();
			if (SessionController.get().isLoggedInUserAdmin()) {
				leaderboardTableOverallDesktop.setColumnWidth(rankColumn, 10.0, Unit.PCT);
				leaderboardTableOverallDesktop.setColumnWidth(freeColumn, 36.7, Unit.PCT);
				leaderboardTableOverallDesktop.setColumnWidth(priceColumn, 13.6, Unit.PCT);
				leaderboardTableOverallDesktop.setColumnWidth(downloadsColumn, 16.7, Unit.PCT);
				leaderboardTableOverallDesktop.setColumnWidth(revenueColumn, 16.7, Unit.PCT);
				leaderboardTableOverallDesktop.setColumnWidth(iapColumn, 6.3, Unit.PCT);
			} else {
				leaderboardTableOverallDesktop.setColumnWidth(rankColumn, 10.0, Unit.PCT);
				leaderboardTableOverallDesktop.setColumnWidth(freeColumn, 42.0, Unit.PCT);
				leaderboardTableOverallDesktop.setColumnWidth(priceColumn, 19.0, Unit.PCT);
				leaderboardTableOverallDesktop.setColumnWidth(downloadsColumn, 19.0, Unit.PCT);
				leaderboardTableOverallDesktop.setColumnWidth(iapColumn, 10.0, Unit.PCT);
			}
			addColumn(rankColumn, rankHeader);
			addColumn(freeColumn, freeHeader);
			addColumn(priceColumn, priceHeader);
			addColumn(downloadsColumn, downloadsHeader);
			if (SessionController.get().isLoggedInUserAdmin()) {
				addColumn(revenueColumn, revenueHeader);
			}
			addColumn(iapColumn, iapHeader);
		} else if (PAID_LIST_TYPE.equals(selectedTab)) {
			if (SessionController.get().isLoggedInUserAdmin()) {
				removeAllColumns();
				leaderboardTableOverallDesktop.setColumnWidth(rankColumn, 10.0, Unit.PCT);
				leaderboardTableOverallDesktop.setColumnWidth(paidColumn, 36.7, Unit.PCT);
				leaderboardTableOverallDesktop.setColumnWidth(priceColumn, 13.6, Unit.PCT);
				leaderboardTableOverallDesktop.setColumnWidth(downloadsColumn, 16.7, Unit.PCT);
				leaderboardTableOverallDesktop.setColumnWidth(revenueColumn, 16.7, Unit.PCT);
				leaderboardTableOverallDesktop.setColumnWidth(iapColumn, 6.3, Unit.PCT);
				addColumn(rankColumn, rankHeader);
				addColumn(paidColumn, paidHeader);
				addColumn(priceColumn, priceHeader);
				addColumn(downloadsColumn, downloadsHeader);
				addColumn(revenueColumn, revenueHeader);
				addColumn(iapColumn, iapHeader);
			}
		} else if (GROSSING_LIST_TYPE.equals(selectedTab)) {
			if (SessionController.get().isLoggedInUserAdmin()) {
				removeAllColumns();
				leaderboardTableOverallDesktop.setColumnWidth(rankColumn, 10.0, Unit.PCT);
				leaderboardTableOverallDesktop.setColumnWidth(grossingColumn, 36.7, Unit.PCT);
				leaderboardTableOverallDesktop.setColumnWidth(priceColumn, 13.6, Unit.PCT);
				leaderboardTableOverallDesktop.setColumnWidth(downloadsColumn, 16.7, Unit.PCT);
				leaderboardTableOverallDesktop.setColumnWidth(revenueColumn, 16.7, Unit.PCT);
				leaderboardTableOverallDesktop.setColumnWidth(iapColumn, 6.3, Unit.PCT);
				addColumn(rankColumn, rankHeader);
				addColumn(grossingColumn, grossingHeader);
				addColumn(priceColumn, priceHeader);
				addColumn(downloadsColumn, downloadsHeader);
				addColumn(revenueColumn, revenueHeader);
				addColumn(iapColumn, iapHeader);
			}
		}

	}

	private void removeColumn(Column<RanksGroup, ?> column) {
		int currentIndex = leaderboardTableOverallDesktop.getColumnIndex(column);

		if (currentIndex != -1) {
			leaderboardTableOverallDesktop.removeColumn(column);
		}
	}

	private void removeAllColumns() {
		removeColumn(rankColumn);
		removeColumn(freeColumn);
		removeColumn(paidColumn);
		removeColumn(grossingColumn);
		removeColumn(priceColumn);
		removeColumn(revenueColumn);
		removeColumn(downloadsColumn);
		removeColumn(iapColumn);
	}

	private void addColumn(Column<RanksGroup, ?> column, TextHeader header) {
		leaderboardTableOverallDesktop.addColumn(column, header);
	}

	private void refreshTabs() {
		for (String key : tabs.keySet()) {
			tabs.get(key).removeClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().isActive());
		}

		LIElement selected = tabs.get(selectedTab);

		if (selected != null) {
			selected.addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().isActive());
		}

		ResponsiveDesignHelper.makeTabsResponsive();
	}

	@UiHandler("viewAllBtn")
	void onViewAllButtonClicked(ClickEvent event) {
		if (((Button) event.getSource()).isEnabled()) {
			if (leaderboardTableOverallDesktop.getVisibleItemCount() == ServiceConstants.STEP_VALUE) {
				leaderboardTableOverallDesktop.setVisibleRange(0, VIEW_ALL_LENGTH_VALUE);
				leaderboardTableOverallMobile.setVisibleRange(0, VIEW_ALL_LENGTH_VALUE);
				viewAllSpan.setInnerText("View Less Apps");
			} else {
				leaderboardTableOverallDesktop.setVisibleRange(0, ServiceConstants.STEP_VALUE);
				leaderboardTableOverallMobile.setVisibleRange(0, ServiceConstants.STEP_VALUE);
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

			if (leaderboardTableOverallDesktop.getVisibleItemCount() > 0) {
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
					if (SessionController.get().isLoggedInUserAdmin()) {
						paidLink.setTargetHistoryToken(PageType.RanksPageType.asTargetHistoryToken(NavigationController.VIEW_ACTION_PARAMETER_VALUE,
								PAID_LIST_TYPE, currentFilter));
						grossingLink.setTargetHistoryToken(PageType.RanksPageType.asTargetHistoryToken(NavigationController.VIEW_ACTION_PARAMETER_VALUE,
								GROSSING_LIST_TYPE, currentFilter));
					} else {
						paidLink.setTargetHistoryToken(current.toString());
						grossingLink.setTargetHistoryToken(current.toString());
					}
				}

				selectedTab = current.getParameter(SELECTED_TAB_PARAMETER_INDEX);

				refreshTabs();
				refreshRanks();

				if (SessionController.get().isLoggedInUserAdmin()) {
					if (FREE_LIST_TYPE.equals(selectedTab) || PAID_LIST_TYPE.equals(selectedTab) || GROSSING_LIST_TYPE.equals(selectedTab)) {
						setDataFilterVisible(false);
					} else {
						setDataFilterVisible(true);
						selectedTab = OVERALL_LIST_TYPE;
					}
				}

				updateFromFilter();
			}
		}
	}

	private void setDataFilterVisible(boolean visible) {
		if (visible) {
			dailyDataContainer.setVisible(true);
		} else {
			dailyDataContainer.setVisible(false);
		}
	}

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
	 * @see
	 * io.reflection.app.api.core.shared.call.event.GetAllTopItemsEventHandler#getAllTopItemsSuccess(io.reflection.app.api.core.shared.call.GetAllTopItemsRequest
	 * , io.reflection.app.api.core.shared.call.GetAllTopItemsResponse)
	 */
	@Override
	public void getAllTopItemsSuccess(GetAllTopItemsRequest input, GetAllTopItemsResponse output) {
		if (output.status.equals(StatusType.StatusTypeSuccess) && output.freeRanks != null) {
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
		} else {
			setViewMoreVisible(false);
		}
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
