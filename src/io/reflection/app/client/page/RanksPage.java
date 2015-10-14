//
//  RanksPage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 3 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.page;

import static io.reflection.app.client.controller.FilterController.FREE_LIST_TYPE;
import static io.reflection.app.client.controller.FilterController.GROSSING_LIST_TYPE;
import static io.reflection.app.client.controller.FilterController.OVERALL_LIST_TYPE;
import static io.reflection.app.client.controller.FilterController.PAID_LIST_TYPE;
import static io.reflection.app.client.controller.FilterController.REVENUE_DAILY_DATA_TYPE;
import io.reflection.app.api.core.shared.call.GetAllTopItemsRequest;
import io.reflection.app.api.core.shared.call.GetAllTopItemsResponse;
import io.reflection.app.api.core.shared.call.event.GetAllTopItemsEventHandler;
import io.reflection.app.client.DefaultEventBus;
import io.reflection.app.client.cell.LeaderboardAppDetailsCell;
import io.reflection.app.client.cell.LeaderboardDownloadsCell;
import io.reflection.app.client.cell.LeaderboardRevenueCell;
import io.reflection.app.client.component.FormDateBox;
import io.reflection.app.client.component.LoadingBar;
import io.reflection.app.client.component.LoadingButton;
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
import io.reflection.app.client.handler.NavigationEventHandler;
import io.reflection.app.client.helper.AnimationHelper;
import io.reflection.app.client.helper.ApiCallHelper;
import io.reflection.app.client.helper.FilterHelper;
import io.reflection.app.client.helper.FormHelper;
import io.reflection.app.client.helper.FormattingHelper;
import io.reflection.app.client.helper.ResponsiveDesignHelper;
import io.reflection.app.client.helper.TooltipHelper;
import io.reflection.app.client.part.BootstrapGwtCellTable;
import io.reflection.app.client.part.ErrorPanel;
import io.reflection.app.client.part.LoadingIndicator;
import io.reflection.app.client.part.NoDataPanel;
import io.reflection.app.client.part.datatypes.RanksGroup;
import io.reflection.app.client.popup.AddLinkedAccountPopup;
import io.reflection.app.client.popup.PremiumPopup;
import io.reflection.app.client.popup.SignUpPopup;
import io.reflection.app.client.res.Styles;
import io.reflection.app.client.res.Styles.ReflectionMainStyles;
import io.reflection.app.datatypes.shared.Rank;
import io.reflection.app.shared.util.DataTypeHelper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.ShowRangeEvent;
import com.google.gwt.event.logical.shared.ShowRangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
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
import com.google.gwt.user.client.Window;
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
public class RanksPage extends Page implements NavigationEventHandler, GetAllTopItemsEventHandler {

	private static RanksPageUiBinder uiBinder = GWT.create(RanksPageUiBinder.class);

	interface RanksPageUiBinder extends UiBinder<Widget, RanksPage> {}

	public static final int SELECTED_TAB_PARAMETER_INDEX = 0;
	public static final int VIEW_ALL_LENGTH_VALUE = Integer.MAX_VALUE;
	public static final String ALL_TEXT = "Overview";
	public static final String COMING_FROM_PARAMETER = "leaderboard";

	interface AllAdminCodeTemplate extends SafeHtmlTemplates {
		AllAdminCodeTemplate INSTANCE = GWT.create(AllAdminCodeTemplate.class);

		@Template(ALL_TEXT
				+ " <span style=\"background-color: #474949;color: #fff;float: right;display: inline-block;padding: 0px 3px; margin-left: 20px;\">{0}</span>")
		SafeHtml code(Long code);
	}

	@UiField(provided = true) CellTable<RanksGroup> leaderboardTable = new CellTable<RanksGroup>(ServiceConstants.STEP_VALUE, BootstrapGwtCellTable.INSTANCE);

	private LoadingIndicator loadingIndicatorAll = AnimationHelper.getLeaderboardAllLoadingIndicator(25);
	private LoadingIndicator loadingIndicatorFreeList = AnimationHelper.getLeaderboardListLoadingIndicator(25, true);
	private LoadingIndicator loadingIndicatorPaidGrossingList = AnimationHelper.getLeaderboardListLoadingIndicator(25, false);

	@UiField(provided = true) ToggleRadioButton toggleListView = new ToggleRadioButton("viewtype", "0 0 20 20");
	@UiField(provided = true) ToggleRadioButton toggleCompactView = new ToggleRadioButton("viewtype", "0 0 20 20");
	@UiField LoadingButton downloadLeaderboard;
	@UiField DivElement dateSelectContainer;
	@UiField FormDateBox dateBox;
	@UiField Selector appStoreSelector;
	// @UiField ListBox mListType;
	@UiField Selector countrySelector;
	@UiField Selector categorySelector;
	@UiField(provided = true) ToggleRadioButton toggleRevenue = new ToggleRadioButton("dailydatatoggle", "0 0 32 32");
	@UiField(provided = true) ToggleRadioButton toggleDownloads = new ToggleRadioButton("dailydatatoggle", "0 0 32 32");
	@UiField HTMLPanel dailyDataContainer;
	@UiField Button applyFilters;
	@UiField Button resetFilters;

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
	// @UiField InlineHyperlink redirect;
	@UiField ErrorPanel errorPanel;
	@UiField NoDataPanel noDataPanel;

	private Column<RanksGroup, SafeHtml> rankColumn;
	private Column<RanksGroup, Rank> grossingColumn;
	private Column<RanksGroup, Rank> freeColumn;
	private Column<RanksGroup, Rank> paidColumn;
	private Column<RanksGroup, SafeHtml> priceColumn;
	private Column<RanksGroup, Rank> downloadsColumn;
	private Column<RanksGroup, Rank> revenueColumn;
	private Column<RanksGroup, SafeHtml> iapColumn;

	@SuppressWarnings("rawtypes") private Column lastOrderedColumn = rankColumn;

	private Map<String, LIElement> tabs = new HashMap<String, LIElement>();

	private SafeHtmlHeader downloadsHeader = new SafeHtmlHeader(SafeHtmlUtils.fromTrustedString("Downloads " + AnimationHelper.getSorterSvg()));
	private SafeHtmlHeader revenueHeader = new SafeHtmlHeader(SafeHtmlUtils.fromTrustedString("Revenue " + AnimationHelper.getSorterSvg()));
	private TextHeader rankHeader = new TextHeader("Rank");
	private TextHeader paidHeader = new TextHeader("Paid");
	private TextHeader freeHeader = new TextHeader("Free");
	private TextHeader grossingHeader = new TextHeader("Grossing");
	private TextHeader priceHeader = new TextHeader("Price");
	private SafeHtmlHeader iapHeader = new SafeHtmlHeader(
			SafeHtmlUtils
					.fromTrustedString("<span>IAP</span><span class=\"js-tooltip js-tooltip--right js-tooltip--right--no-pointer-padding whats-this-tooltip-icon-static\" data-tooltip=\"In App Purchases\"></span>"));

	private String selectedTab = OVERALL_LIST_TYPE;
	private String previousFilter;
	private LoadingBar loadingBar = new LoadingBar(false);
	private ReflectionMainStyles style = Styles.STYLES_INSTANCE.reflectionMainStyle();
	private SignUpPopup signUpPopup = new SignUpPopup();
	private PremiumPopup premiumPopup = new PremiumPopup();
	private AddLinkedAccountPopup addLinkedAccountPopup = new AddLinkedAccountPopup();

	public RanksPage() {
		initWidget(uiBinder.createAndBindUi(this));

		dailyDataContainer.removeFromParent();

		if (!SessionController.get().isAdmin()) {
			categorySelector.setTooltip("This field is currently locked but will soon be editable as we integrate more data");
		}

		dateBox.getDatePicker().addShowRangeHandler(new ShowRangeHandler<Date>() {

			@Override
			public void onShowRange(ShowRangeEvent<Date> event) {
				FilterHelper.disableOutOfRangeDates(dateBox.getDatePicker(),
						(SessionController.get().isAdmin() ? null : ApiCallHelper.getUTCDate(2015, 4, 30)),
						(SessionController.get().isAdmin() ? FilterHelper.getToday() : FilterHelper.getDaysAgo(2)));
			}
		});

		FilterHelper.addStores(appStoreSelector, SessionController.get().isAdmin());
		FilterHelper.addCountries(countrySelector, SessionController.get().isAdmin());
		FilterHelper.addCategories(categorySelector, SessionController.get().isAdmin());

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

		leaderboardTable.setLoadingIndicator(loadingIndicatorAll);
		leaderboardTable.getTableLoadingSection().addClassName(Styles.STYLES_INSTANCE.reflectionMainStyle().tableBodyLoading());

		RankController.get().addDataDisplay(leaderboardTable);

		dateSelectContainer.addClassName("js-tooltip");
		dateSelectContainer.setAttribute("data-tooltip", "Select a date");
		TooltipHelper.updateHelperTooltip();

		loadingBar.show();

		// Hide Overall tab on mobile and go to grossing link
		Window.addResizeHandler(new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				if (event.getWidth() <= 719) {
					if (tabs.get(OVERALL_LIST_TYPE).hasClassName(style.isActive())) {
						History.replaceItem(grossingLink.getTargetHistoryToken());
					}
					allItem.getStyle().setDisplay(Display.NONE);
				} else {
					allItem.getStyle().setDisplay(Display.BLOCK);
				}
			}
		});

		updateSelectorsFromFilter();

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

		LeaderboardAppDetailsCell appDetailsCell = new LeaderboardAppDetailsCell();

		paidColumn = new Column<RanksGroup, Rank>(appDetailsCell) {

			@Override
			public Rank getValue(RanksGroup object) {
				return object.paid;
			}
		};
		paidColumn.setFieldUpdater(new FieldUpdater<RanksGroup, Rank>() {

			@Override
			public void update(int index, RanksGroup object, Rank value) {
				if (SessionController.get().isValidSession() && !SessionController.get().hasLinkedAccount()) {
					addLinkedAccountPopup.show("Link Your Appstore Account",
							"You need to link your iTunes Connect account to use this feature, it only takes a moment");
				} else {
					signUpPopup.show();
				}
			}
		});
		paidColumn.setCellStyleNames(style.mhxte6ciA());

		freeColumn = new Column<RanksGroup, Rank>(appDetailsCell) {

			@Override
			public Rank getValue(RanksGroup object) {
				return object.free;
			}

		};
		freeColumn.setFieldUpdater(new FieldUpdater<RanksGroup, Rank>() {

			@Override
			public void update(int index, RanksGroup object, Rank value) {
				if (SessionController.get().isValidSession() && !SessionController.get().hasLinkedAccount()) {
					addLinkedAccountPopup.show("Link Your Appstore Account",
							"You need to link your iTunes Connect account to use this feature, it only takes a moment");
				} else {
					signUpPopup.show();
				}
			}
		});
		freeColumn.setCellStyleNames(style.mhxte6ciA());

		grossingColumn = new Column<RanksGroup, Rank>(appDetailsCell) {

			@Override
			public Rank getValue(RanksGroup object) {
				return object.grossing;
			}
		};
		grossingColumn.setFieldUpdater(new FieldUpdater<RanksGroup, Rank>() {

			@Override
			public void update(int index, RanksGroup object, Rank value) {
				if (SessionController.get().isValidSession() && !SessionController.get().hasLinkedAccount()) {
					addLinkedAccountPopup.show("Link Your Appstore Account",
							"You need to link your iTunes Connect account to use this feature, it only takes a moment");
				} else {
					signUpPopup.show();
				}
			}
		});
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

		downloadsColumn = new Column<RanksGroup, Rank>(new LeaderboardDownloadsCell()) {

			@Override
			public Rank getValue(RanksGroup object) {
				return rankForListType(object);
			}

		};
		downloadsColumn.setFieldUpdater(new FieldUpdater<RanksGroup, Rank>() {

			@Override
			public void update(int index, RanksGroup object, Rank value) {
				if (SessionController.get().isStandardDeveloper() && SessionController.get().hasLinkedAccount()) {
					premiumPopup.show(true);
				} else if (SessionController.get().isValidSession()) {
					addLinkedAccountPopup.show("Link Your Appstore Account",
							"You need to link your iTunes Connect account to use this feature, it only takes a moment");
				} else {
					signUpPopup.show();
				}
			}
		});
		downloadsColumn.setCellStyleNames(style.mhxte6ciA());
		downloadsColumn.setSortable(true);
		downloadsHeader.setHeaderStyleNames(style.canBeSorted());

		revenueColumn = new Column<RanksGroup, Rank>(new LeaderboardRevenueCell()) {

			@Override
			public Rank getValue(RanksGroup object) {
				return rankForListType(object);
			}

		};
		revenueColumn.setFieldUpdater(new FieldUpdater<RanksGroup, Rank>() {

			@Override
			public void update(int index, RanksGroup object, Rank value) {
				if (SessionController.get().isValidSession() && !SessionController.get().hasLinkedAccount()) {
					addLinkedAccountPopup.show("Link Your Appstore Account",
							"You need to link your iTunes Connect account to use this feature, it only takes a moment");
				} else {
					signUpPopup.show();
				}
			}
		});
		revenueColumn.setCellStyleNames(style.mhxte6ciA());
		revenueColumn.setSortable(true);
		revenueHeader.setHeaderStyleNames(style.canBeSorted());

		iapColumn = new Column<RanksGroup, SafeHtml>(new SafeHtmlCell()) {

			private final String IAP_YES_HTML = "<span class=\"" + style.refIconBefore() + " " + style.refIconBeforeCheck() + "\"></span>";
			private final String IAP_NO_HTML = "<span></span>";

			@Override
			public SafeHtml getValue(RanksGroup object) {
				return SafeHtmlUtils.fromSafeConstant(DataTypeHelper.itemIapState(ItemController.get().lookupItem(rankForListType(object).itemId),
						IAP_YES_HTML, IAP_NO_HTML,
						"<span class=\"js-tooltip js-tooltip--right js-tooltip--right--no-pointer-padding " + style.whatsThisTooltipIconStatic()
								+ "\" data-tooltip=\"No data available\"></span>"));
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

	@UiHandler("applyFilters")
	void onApplyFiltersClicked(ClickEvent event) {
		event.preventDefault();
		if (NavigationController.get().getCurrentPage() == PageType.RanksPageType) {
			boolean updateData = false;
			if (updateData = updateData || !FilterController.get().getFilter().getCountryA2Code().equals(countrySelector.getSelectedValue())) {
				FilterController.get().setCountry(countrySelector.getSelectedValue());
			}
			if (updateData = updateData || !FilterController.get().getFilter().getStoreA3Code().equals(appStoreSelector.getSelectedValue())) {
				FilterController.get().setStore(appStoreSelector.getSelectedValue());
			}
			if (updateData = updateData || !FilterController.get().getFilter().getCategoryId().toString().equals(categorySelector.getSelectedValue())) {
				FilterController.get().setCategory(Long.valueOf(categorySelector.getSelectedValue()));
			}
			if (updateData = updateData || !CalendarUtil.isSameDate(new Date(FilterController.get().getFilter().getEndTime().longValue()), dateBox.getValue())) {
				FilterController.get().setEndDate(dateBox.getValue());
				Date startDate = new Date(FilterController.get().getFilter().getEndTime());
				CalendarUtil.addDaysToDate(startDate, -30);
				FilterController.get().getFilter().setStartTime(startDate.getTime());
			}
			if (updateData) {
				applyFilters.setEnabled(false);
				PageType.RanksPageType.show("view", selectedTab, FilterController.get().asRankFilterString());
			}
			if (errorPanel.isVisible()) {
				applyFilters.setEnabled(false);
				updateSelectorsFromFilter();
				loadingBar.show();
				RankController.get().reset();
				RankController.get().fetchTopItems();
				viewAllBtn.setVisible(false);
				errorPanel.setVisible(false);
				noDataPanel.setVisible(false);
				leaderboardTable.setVisible(true);
				downloadsHeader.setHeaderStyleNames(style.canBeSorted());
				revenueHeader.setHeaderStyleNames(style.canBeSorted());
				previousFilter = FilterController.get().asRankFilterString();
			}
		}
	}

	@UiHandler({ "countrySelector", "appStoreSelector", "categorySelector" })
	void onFiltersChanged(ChangeEvent event) {
		applyFilters.setEnabled(!FilterController.get().getFilter().getCountryA2Code().equals(countrySelector.getSelectedValue())
				|| !FilterController.get().getFilter().getStoreA3Code().equals(appStoreSelector.getSelectedValue())
				|| !FilterController.get().getFilter().getCategoryId().toString().equals(categorySelector.getSelectedValue())
				|| !CalendarUtil.isSameDate(new Date(FilterController.get().getFilter().getEndTime().longValue()), dateBox.getValue()));
	}

	@UiHandler("dateBox")
	void onDateChanged(ValueChangeEvent<Date> event) {
		applyFilters.setEnabled(!FilterController.get().getFilter().getCountryA2Code().equals(countrySelector.getSelectedValue())
				|| !FilterController.get().getFilter().getStoreA3Code().equals(appStoreSelector.getSelectedValue())
				|| !FilterController.get().getFilter().getCategoryId().toString().equals(categorySelector.getSelectedValue())
				|| !CalendarUtil.isSameDate(new Date(FilterController.get().getFilter().getEndTime().longValue()), dateBox.getValue()));
	}

	@UiHandler("resetFilters")
	void onResetFiltersClicked(ClickEvent event) {
		event.preventDefault();
		// TODO will use user preferences
		countrySelector.setSelectedIndex(FormHelper.getItemIndex(countrySelector, "gb"));
		appStoreSelector.setSelectedIndex(FormHelper.getItemIndex(appStoreSelector, "iph"));
		categorySelector.setSelectedIndex(FormHelper.getItemIndex(categorySelector, "15"));
		dateBox.setValue(FilterHelper.getDaysAgo(2));
		applyFilters.setEnabled(!FilterController.get().getFilter().getCountryA2Code().equals(countrySelector.getSelectedValue())
				|| !FilterController.get().getFilter().getStoreA3Code().equals(appStoreSelector.getSelectedValue())
				|| !FilterController.get().getFilter().getCategoryId().toString().equals(categorySelector.getSelectedValue())
				|| !CalendarUtil.isSameDate(new Date(FilterController.get().getFilter().getEndTime().longValue()), dateBox.getValue()));
	}

	/**
	 * Update selectors from URL if coming from a page refresh
	 */
	private void updateSelectorsFromFilter() {
		FilterController fc = FilterController.get();

		long endTime = fc.getFilter().getEndTime().longValue();
		Date endDate = new Date(endTime);
		dateBox.setValue(endDate, false);
		if (SessionController.get().isAdmin()) {
			categorySelector.setSelectedIndex(FormHelper.getItemIndex(categorySelector, fc.getFilter().getCategoryId().toString()));
		} else {
			categorySelector.setSelectedIndex(0);
		}
		appStoreSelector.setSelectedIndex(FormHelper.getItemIndex(appStoreSelector, fc.getFilter().getStoreA3Code()));
		countrySelector.setSelectedIndex(FormHelper.getItemIndex(countrySelector, fc.getFilter().getCountryA2Code()));

		String dailyDataType = fc.getFilter().getDailyData();
		if (REVENUE_DAILY_DATA_TYPE.equals(dailyDataType)) {
			toggleRevenue.setValue(true);
		} else {
			toggleDownloads.setValue(true);
		}

	}

	private void appendTableColumns() {

		leaderboardTable.setStyleName(style.tableOverall(), OVERALL_LIST_TYPE.equals(selectedTab));
		leaderboardTable.setStyleName(style.tableAppGroup(),
				(FREE_LIST_TYPE.equals(selectedTab) || PAID_LIST_TYPE.equals(selectedTab) || GROSSING_LIST_TYPE.equals(selectedTab)));

		switch (selectedTab) {
		case OVERALL_LIST_TYPE:
			removeAllColumns();
			leaderboardTable.setColumnWidth(rankColumn, 10.0, Unit.PCT);
			leaderboardTable.setColumnWidth(paidColumn, 30.0, Unit.PCT);
			leaderboardTable.setColumnWidth(freeColumn, 30.0, Unit.PCT);
			leaderboardTable.setColumnWidth(grossingColumn, 30.0, Unit.PCT);
			leaderboardTable.addColumn(rankColumn, rankHeader);
			leaderboardTable.addColumn(paidColumn, paidHeader);
			leaderboardTable.addColumn(freeColumn, freeHeader);
			leaderboardTable.addColumn(grossingColumn, grossingHeader);
			leaderboardTable.setLoadingIndicator(loadingIndicatorAll);
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
			iapHeader.setHeaderStyleNames(style.columnHiddenMobile());
			iapColumn.setCellStyleNames(style.mhxte6ciA() + " " + style.columnHiddenMobile());
			leaderboardTable.addColumnStyleName(4, style.columnHiddenMobile());
			leaderboardTable.setLoadingIndicator(loadingIndicatorPaidGrossingList);
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
			priceHeader.setHeaderStyleNames(style.columnHiddenMobile());
			priceColumn.setCellStyleNames(style.mhxte6ciA() + " " + style.columnHiddenMobile());
			leaderboardTable.addColumnStyleName(2, style.columnHiddenMobile());
			leaderboardTable.setLoadingIndicator(loadingIndicatorFreeList);
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
			iapHeader.setHeaderStyleNames(style.columnHiddenMobile());
			iapColumn.setCellStyleNames(style.mhxte6ciA() + " " + style.columnHiddenMobile());
			leaderboardTable.addColumnStyleName(4, style.columnHiddenMobile());
			leaderboardTable.setLoadingIndicator(loadingIndicatorPaidGrossingList);
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
		removeColumn(paidColumn);
		removeColumn(freeColumn);
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

	@UiHandler("toggleListView")
	void onToggleViewListSelected(ValueChangeEvent<Boolean> event) {
		getElement().removeClassName(style.compactView());
		// getElement().removeClassName(style.list);
	}

	@UiHandler("toggleCompactView")
	void onToggleViewCompactSelected(ValueChangeEvent<Boolean> event) {
		// getElement().removeClassName(style.listview);
		getElement().addClassName(style.compactView());
	}

	@UiHandler("downloadLeaderboard")
	void onDownloadLeaderboardClicked(ClickEvent event) {
		event.preventDefault();
		if ((SessionController.get().isPremiumDeveloper() && SessionController.get().hasLinkedAccount()) || SessionController.get().isAdmin()) {
			downloadLeaderboard.setStatusLoading("Downloading");
			Filter filter = FilterController.get().getFilter();
			String listType;
			if (filter.getStoreA3Code().equals("iph")) {
				switch (selectedTab) {
				case (PAID_LIST_TYPE):
					listType = "toppaidapplications";
					break;
				case (FREE_LIST_TYPE):
					listType = "topfreeapplications";
					break;
				case (GROSSING_LIST_TYPE):
					listType = "topgrossingapplications";
					break;
				default:
					listType = "topallapplications";
					break;
				}
			} else {
				switch (selectedTab) {
				case (PAID_LIST_TYPE):
					listType = "toppaidipadapplications";
					break;
				case (FREE_LIST_TYPE):
					listType = "topfreeipadapplications";
					break;
				case (GROSSING_LIST_TYPE):
					listType = "topgrossingipadapplications";
					break;
				default:
					listType = "topallipadapplications";
					break;
				}
			}
			String country = filter.getCountryA2Code();
			String category = filter.getCategoryId().toString();
			String date = String.valueOf(ApiCallHelper.getUTCDate(FilterController.get().getEndDate()).getTime());

			String sessionParam = SessionController.get().getSession().toString();

			String requestData = "listType=" + listType + "&country=" + country + "&category=" + category + "&date=" + date + "&session=" + sessionParam;

			RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, URL.encode(Window.Location.getProtocol() + "//" + Window.Location.getHost()
					+ "/downloadleaderboard"));

			builder.setHeader("Content-Type", "application/x-www-form-urlencoded");

			try {
				builder.sendRequest(requestData, new RequestCallback() {

					@Override
					public void onError(Request request, Throwable exception) {
						downloadLeaderboard.setStatusError();
					}

					@Override
					public void onResponseReceived(Request request, Response response) {
						if (response.getStatusCode() == Response.SC_FORBIDDEN) { // User doesn't have the required role, probably premium role is expired
							downloadLeaderboard.setStatusError();
							// Refresh credentials
							SessionController.get().fetchRolesAndPermissions();
						} else {
							String csvContent = "data:text/csv;charset=utf-8," + response.getText();
							Window.open(URL.encode(csvContent), "_self", "");
							downloadLeaderboard.resetStatus();
						}
					}
				});
			} catch (Exception e) {
				downloadLeaderboard.setStatusError();
			}
		} else if (!SessionController.get().canSeePredictions()) {
			addLinkedAccountPopup
					.show("Link Your Appstore Account", "You need to link your iTunes Connect account to use this feature, it only takes a moment");
		} else if (SessionController.get().isValidSession()) {
			premiumPopup.show(true);
		} else {
			signUpPopup.show();
		}
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
		// FilterController.get().asRankFilterString() == current.getParameter(1)

		if (PageType.RanksPageType.equals(current.getPage()) && current.getAction() != null
				&& NavigationController.VIEW_ACTION_PARAMETER_VALUE.equals(current.getAction()) && current.getParameter(1) != null) {

			if (leaderboardTable.getVisibleItemCount() > 0) {
				viewAllBtn.setVisible(true);
			}

			if (previousFilter == null) {
				previousFilter = FilterController.get().asRankFilterString();
			}

			// Check if the filter is changed
			if (!previousFilter.equals(FilterController.get().asRankFilterString())) {
				updateSelectorsFromFilter();
				loadingBar.show();
				RankController.get().reset();
				RankController.get().fetchTopItems();
				viewAllBtn.setVisible(false);
				errorPanel.setVisible(false);
				noDataPanel.setVisible(false);
				leaderboardTable.setVisible(true);
				downloadsHeader.setHeaderStyleNames(style.canBeSorted());
				revenueHeader.setHeaderStyleNames(style.canBeSorted());
				previousFilter = FilterController.get().asRankFilterString();
			}

			String currentFilter = FilterController.get().asRankFilterString();

			if (currentFilter != null && currentFilter.length() > 0) {
				allLink.setTargetHistoryToken(PageType.RanksPageType.asTargetHistoryToken(NavigationController.VIEW_ACTION_PARAMETER_VALUE, OVERALL_LIST_TYPE,
						currentFilter));
				freeLink.setTargetHistoryToken(PageType.RanksPageType.asTargetHistoryToken(NavigationController.VIEW_ACTION_PARAMETER_VALUE, FREE_LIST_TYPE,
						currentFilter));
				paidLink.setTargetHistoryToken(PageType.RanksPageType.asTargetHistoryToken(NavigationController.VIEW_ACTION_PARAMETER_VALUE, PAID_LIST_TYPE,
						currentFilter));
				grossingLink.setTargetHistoryToken(PageType.RanksPageType.asTargetHistoryToken(NavigationController.VIEW_ACTION_PARAMETER_VALUE,
						GROSSING_LIST_TYPE, currentFilter));
			}

			selectedTab = current.getParameter(SELECTED_TAB_PARAMETER_INDEX);

			refreshTabs();
			appendTableColumns();

			if (Window.getClientWidth() <= 719) {
				if (tabs.get(OVERALL_LIST_TYPE).hasClassName(style.isActive())) {
					History.replaceItem(grossingLink.getTargetHistoryToken());
				}
				allItem.getStyle().setDisplay(Display.NONE);
			} else {
				allItem.getStyle().setDisplay(Display.BLOCK);
			}

		} else {
			PageType.RanksPageType.show(NavigationController.VIEW_ACTION_PARAMETER_VALUE, OVERALL_LIST_TYPE, FilterController.get().asRankFilterString());
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
		register(DefaultEventBus.get().addHandlerToSource(GetAllTopItemsEventHandler.TYPE, RankController.get(), this));

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
		signUpPopup.hide();
		premiumPopup.hide();
		addLinkedAccountPopup.hide();
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
				viewAllBtn.setVisible(true);

				if (SessionController.get().isAdmin()) {
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
				leaderboardTable.setVisible(false);
				noDataPanel.setVisible(true);
				viewAllBtn.setVisible(false);
			}
			loadingBar.hide(true);
		} else {
			loadingBar.hide(false);
			viewAllBtn.setVisible(false);
			leaderboardTable.setVisible(false);
			errorPanel.setVisible(true);
			applyFilters.setEnabled(true);
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
		loadingBar.hide(false);
		viewAllBtn.setVisible(false);
		leaderboardTable.setVisible(false);
		errorPanel.setVisible(true);
		applyFilters.setEnabled(true);
	}
}
