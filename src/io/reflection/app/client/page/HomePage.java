//
//  HomePage.java
//  storedata
//
//  Created by Stefano Capuzzi on 23 Sep 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page;

import static io.reflection.app.client.controller.FilterController.FREE_LIST_TYPE;
import static io.reflection.app.client.controller.FilterController.GROSSING_LIST_TYPE;
import static io.reflection.app.client.controller.FilterController.PAID_LIST_TYPE;
import static io.reflection.app.client.helper.FormattingHelper.WHOLE_NUMBER_FORMATTER;
import io.reflection.app.api.core.client.CoreService;
import io.reflection.app.api.core.shared.call.GetAllTopItemsRequest;
import io.reflection.app.api.core.shared.call.GetAllTopItemsResponse;
import io.reflection.app.api.core.shared.call.event.GetAllTopItemsEventHandler;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.client.DefaultEventBus;
import io.reflection.app.client.cell.AppRankCell;
import io.reflection.app.client.component.Selector;
import io.reflection.app.client.controller.FilterController;
import io.reflection.app.client.controller.ItemController;
import io.reflection.app.client.controller.ServiceConstants;
import io.reflection.app.client.controller.ServiceCreator;
import io.reflection.app.client.controller.SessionController;
import io.reflection.app.client.helper.AnimationHelper;
import io.reflection.app.client.helper.FilterHelper;
import io.reflection.app.client.helper.FormattingHelper;
import io.reflection.app.client.helper.ResponsiveDesignHelper;
import io.reflection.app.client.helper.TooltipHelper;
import io.reflection.app.client.part.BootstrapGwtCellTable;
import io.reflection.app.client.part.ErrorPanel;
import io.reflection.app.client.part.LoadingIndicator;
import io.reflection.app.client.part.datatypes.RanksGroup;
import io.reflection.app.client.res.Styles;
import io.reflection.app.client.res.Styles.ReflectionMainStyles;
import io.reflection.app.datatypes.shared.Rank;
import io.reflection.app.shared.util.DataTypeHelper;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.http.client.Request;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.SafeHtmlHeader;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author billy1380
 * 
 */
public class HomePage extends Page implements GetAllTopItemsEventHandler {

	private static HomePageUiBinder uiBinder = GWT.create(HomePageUiBinder.class);

	interface HomePageUiBinder extends UiBinder<Widget, HomePage> {}

	@UiField LIElement paidItem;
	@UiField LIElement freeItem;
	@UiField LIElement grossingItem;
	@UiField SpanElement dateFixed;
	@UiField Selector countrySelector;
	private String selectedCountry = "gb";
	@UiField Selector appStoreSelector;
	private String selectedAppStore = "iph";
	@UiField Button applyFilters;
	@UiField ErrorPanel errorPanel;
	@UiField(provided = true) CellTable<RanksGroup> leaderboardHomeTable = new CellTable<RanksGroup>(ServiceConstants.SHORT_STEP_VALUE,
			BootstrapGwtCellTable.INSTANCE);
	private TextHeader rankHeader = new TextHeader("Rank");
	private TextHeader priceHeader = new TextHeader("Price");
	private SafeHtmlHeader iapHeader = new SafeHtmlHeader(
			SafeHtmlUtils.fromTrustedString("<span class=\"js-tooltip\" data-tooltip=\"In App Purchases\">IAP</span>"));
	private Column<RanksGroup, SafeHtml> rankColumn;
	private Column<RanksGroup, Rank> paidColumn;
	private Column<RanksGroup, Rank> freeColumn;
	private Column<RanksGroup, Rank> grossingColumn;
	private Column<RanksGroup, SafeHtml> priceColumn;
	private Column<RanksGroup, SafeHtml> downloadsColumn;
	private Column<RanksGroup, SafeHtml> revenueColumn;
	private Column<RanksGroup, SafeHtml> iapColumn;
	private LoadingIndicator loadingIndicatorFreeList = AnimationHelper.getLeaderboardListLoadingIndicator(ServiceConstants.SHORT_STEP_VALUE, true);
	private LoadingIndicator loadingIndicatorPaidGrossingList = AnimationHelper.getLeaderboardListLoadingIndicator(ServiceConstants.SHORT_STEP_VALUE, false);

	private HomeRankProvider homeRankProvider = new HomeRankProvider();
	private String selectedTab = PAID_LIST_TYPE;
	private ReflectionMainStyles style = Styles.STYLES_INSTANCE.reflectionMainStyle();

	public HomePage() {
		initWidget(uiBinder.createAndBindUi(this));

		dateFixed.setInnerText(FormattingHelper.DATE_FORMATTER_DD_MMM_YYYY.format(FilterHelper.getDaysAgo(2)));
		FilterHelper.addCountries(countrySelector, false);
		FilterHelper.addStores(appStoreSelector, false);
		countrySelector.setSelectedIndex(3);
		appStoreSelector.setSelectedIndex(1);

		leaderboardHomeTable.getTableLoadingSection().addClassName(style.tableBodyLoading());
		homeRankProvider.addDataDisplay(leaderboardHomeTable);

		Event.sinkEvents(paidItem, Event.ONCLICK);
		Event.sinkEvents(freeItem, Event.ONCLICK);
		Event.sinkEvents(grossingItem, Event.ONCLICK);
		Event.setEventListener(paidItem, new EventListener() {

			@Override
			public void onBrowserEvent(Event event) {
				if (Event.ONCLICK == event.getTypeInt()) {
					freeItem.removeClassName(style.isActive());
					grossingItem.removeClassName(style.isActive());
					paidItem.addClassName(style.isActive());
					removeAllColumns();
					leaderboardHomeTable.setColumnWidth(rankColumn, 10.0, Unit.PCT);
					leaderboardHomeTable.setColumnWidth(paidColumn, 30.0, Unit.PCT);
					leaderboardHomeTable.setColumnWidth(priceColumn, 19.0, Unit.PCT);
					leaderboardHomeTable.setColumnWidth(downloadsColumn, 19.0, Unit.PCT);
					leaderboardHomeTable.setColumnWidth(iapColumn, 10.0, Unit.PCT);
					leaderboardHomeTable.addColumn(rankColumn, rankHeader);
					leaderboardHomeTable.addColumn(paidColumn, "App Name");
					leaderboardHomeTable.addColumn(priceColumn, priceHeader);
					leaderboardHomeTable.addColumn(downloadsColumn, "Downloads");
					leaderboardHomeTable.addColumn(iapColumn, iapHeader);
					leaderboardHomeTable.addColumnStyleName(4, style.columnHiddenMobile());
					iapHeader.setHeaderStyleNames(style.columnHiddenMobile());
					iapColumn.setCellStyleNames(style.mhxte6ciA() + " " + style.columnHiddenMobile());
					ResponsiveDesignHelper.makeTabsResponsive();
					leaderboardHomeTable.setLoadingIndicator(loadingIndicatorPaidGrossingList);
					TooltipHelper.updateHelperTooltip();
					selectedTab = PAID_LIST_TYPE;
				}
			}
		});
		Event.setEventListener(freeItem, new EventListener() {

			@Override
			public void onBrowserEvent(Event event) {
				if (Event.ONCLICK == event.getTypeInt()) {
					paidItem.removeClassName(style.isActive());
					grossingItem.removeClassName(style.isActive());
					freeItem.addClassName(style.isActive());
					removeAllColumns();
					leaderboardHomeTable.setColumnWidth(rankColumn, 10.0, Unit.PCT);
					leaderboardHomeTable.setColumnWidth(freeColumn, 30.0, Unit.PCT);
					leaderboardHomeTable.setColumnWidth(priceColumn, 19.0, Unit.PCT);
					leaderboardHomeTable.setColumnWidth(downloadsColumn, 19.0, Unit.PCT);
					leaderboardHomeTable.setColumnWidth(iapColumn, 10.0, Unit.PCT);
					leaderboardHomeTable.addColumn(rankColumn, rankHeader);
					leaderboardHomeTable.addColumn(freeColumn, "App Name");
					leaderboardHomeTable.addColumn(priceColumn, priceHeader);
					leaderboardHomeTable.addColumn(downloadsColumn, "Downloads");
					leaderboardHomeTable.addColumn(iapColumn, iapHeader);
					priceHeader.setHeaderStyleNames(style.columnHiddenMobile());
					priceColumn.setCellStyleNames(style.mhxte6ciA() + " " + style.columnHiddenMobile());
					leaderboardHomeTable.addColumnStyleName(2, style.columnHiddenMobile());
					leaderboardHomeTable.setLoadingIndicator(loadingIndicatorFreeList);
					ResponsiveDesignHelper.makeTabsResponsive();
					TooltipHelper.updateHelperTooltip();
					selectedTab = FREE_LIST_TYPE;
				}
			}
		});
		Event.setEventListener(grossingItem, new EventListener() {

			@Override
			public void onBrowserEvent(Event event) {
				if (Event.ONCLICK == event.getTypeInt()) {
					paidItem.removeClassName(style.isActive());
					freeItem.removeClassName(style.isActive());
					grossingItem.addClassName(style.isActive());
					removeAllColumns();
					leaderboardHomeTable.setColumnWidth(rankColumn, 10.0, Unit.PCT);
					leaderboardHomeTable.setColumnWidth(grossingColumn, 30.0, Unit.PCT);
					leaderboardHomeTable.setColumnWidth(priceColumn, 19.0, Unit.PCT);
					leaderboardHomeTable.setColumnWidth(revenueColumn, 19.0, Unit.PCT);
					leaderboardHomeTable.setColumnWidth(iapColumn, 10.0, Unit.PCT);
					leaderboardHomeTable.addColumn(rankColumn, rankHeader);
					leaderboardHomeTable.addColumn(grossingColumn, "App Name");
					leaderboardHomeTable.addColumn(priceColumn, priceHeader);
					leaderboardHomeTable.addColumn(revenueColumn, "Revenue");
					leaderboardHomeTable.addColumn(iapColumn, iapHeader);
					iapHeader.setHeaderStyleNames(style.columnHiddenMobile());
					iapColumn.setCellStyleNames(style.mhxte6ciA() + " " + style.columnHiddenMobile());
					leaderboardHomeTable.addColumnStyleName(4, style.columnHiddenMobile());
					leaderboardHomeTable.setLoadingIndicator(loadingIndicatorPaidGrossingList);
					ResponsiveDesignHelper.makeTabsResponsive();
					TooltipHelper.updateHelperTooltip();
					selectedTab = GROSSING_LIST_TYPE;
				}
			}
		});

		createColumns();
		leaderboardHomeTable.setColumnWidth(rankColumn, 10.0, Unit.PCT);
		leaderboardHomeTable.setColumnWidth(paidColumn, 30.0, Unit.PCT);
		leaderboardHomeTable.setColumnWidth(priceColumn, 19.0, Unit.PCT);
		leaderboardHomeTable.setColumnWidth(downloadsColumn, 19.0, Unit.PCT);
		leaderboardHomeTable.setColumnWidth(iapColumn, 10.0, Unit.PCT);
		leaderboardHomeTable.addColumn(rankColumn, rankHeader);
		leaderboardHomeTable.addColumn(paidColumn, "App Name");
		leaderboardHomeTable.addColumn(priceColumn, priceHeader);
		leaderboardHomeTable.addColumn(downloadsColumn, "Downloads");
		leaderboardHomeTable.addColumn(iapColumn, iapHeader);
		leaderboardHomeTable.addColumnStyleName(4, style.columnHiddenMobile());
		iapHeader.setHeaderStyleNames(style.columnHiddenMobile());
		iapColumn.setCellStyleNames(style.mhxte6ciA() + " " + style.columnHiddenMobile());
		TooltipHelper.updateHelperTooltip();
		leaderboardHomeTable.setLoadingIndicator(loadingIndicatorPaidGrossingList);
	}

	private void createColumns() {
		rankColumn = new Column<RanksGroup, SafeHtml>(new SafeHtmlCell()) {

			@Override
			public SafeHtml getValue(RanksGroup object) {
				return (object.free.position != null) ? SafeHtmlUtils.fromTrustedString(object.free.position.toString()) : SafeHtmlUtils
						.fromTrustedString("<span class=\"js-tooltip\" data-tooltip=\"No data available\">-</span>");
			}

		};
		rankColumn.setCellStyleNames(style.mhxte6ciA() + " " + style.mhxte6cID());
		rankHeader.setHeaderStyleNames(style.mhxte6cIF());

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
				SafeHtml value;
				Rank rank = rankForListType(object);
				int position = (rank.position.intValue() > 0 ? rank.position.intValue() : rank.grossingPosition.intValue());
				if (!SessionController.get().isLoggedInUserAdmin() && position <= 5 && position > 0) {
					value = SafeHtmlUtils
							.fromSafeConstant("<span style=\"color: #81879d; font-size: 13px\">Coming Soon</span><span class=\"js-tooltip js-tooltip--right js-tooltip--right--no-pointer-padding "
									+ style.whatsThisTooltipIconStatic()
									+ "\" data-tooltip=\"We are upgrading our model to improve accuracy for the Top 5. It will be implemented soon.\" style=\"padding: 0px 0px 5px 7px\"></span>");
				} else {
					if (rank.downloads != null) {
						value = SafeHtmlUtils.fromSafeConstant(WHOLE_NUMBER_FORMATTER.format(rank.downloads));
					} else {
						value = (SessionController.get().isValidSession() || position <= 10 ? SafeHtmlUtils
								.fromTrustedString("<span class=\"js-tooltip\" data-tooltip=\"No data available\">-</span>") : SafeHtmlUtils.EMPTY_SAFE_HTML);
					}
				}
				return value;
			}
		};
		downloadsColumn.setCellStyleNames(style.mhxte6ciA());

		revenueColumn = new Column<RanksGroup, SafeHtml>(new SafeHtmlCell()) {

			@Override
			public SafeHtml getValue(RanksGroup object) {
				SafeHtml value;
				Rank rank = rankForListType(object);
				int position = (rank.position.intValue() > 0 ? rank.position.intValue() : rank.grossingPosition.intValue());
				if (!SessionController.get().isLoggedInUserAdmin() && position <= 5 && position > 0) {
					value = SafeHtmlUtils
							.fromSafeConstant("<span style=\"color: #81879d; font-size: 13px\">Coming Soon</span><span class=\"js-tooltip js-tooltip--right js-tooltip--right--no-pointer-padding "
									+ style.whatsThisTooltipIconStatic()
									+ "\" data-tooltip=\"We are upgrading our model to improve accuracy for the Top 5. It will be implemented soon.\" style=\"padding: 0px 0px 5px 7px\"></span>");
				} else {
					if (rank.currency != null && rank.revenue != null) {
						value = SafeHtmlUtils.fromSafeConstant(FormattingHelper.asWholeMoneyString(rank.currency, rank.revenue.floatValue()));
					} else {
						value = (SessionController.get().isValidSession() || position <= 10 ? SafeHtmlUtils
								.fromTrustedString("<span class=\"js-tooltip\" data-tooltip=\"No data available\">-</span>") : SafeHtmlUtils.EMPTY_SAFE_HTML);
					}
				}
				return value;
			}
		};
		revenueColumn.setCellStyleNames(style.mhxte6ciA());

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

	protected void removeAllColumns() {
		priceHeader.setHeaderStyleNames("");
		priceColumn.setCellStyleNames(style.mhxte6ciA());
		iapHeader.setHeaderStyleNames("");
		iapColumn.setCellStyleNames(style.mhxte6ciA());
		for (int i = 0; i < leaderboardHomeTable.getColumnCount(); i++) {
			leaderboardHomeTable.removeColumnStyleName(i, style.columnHiddenMobile());
		}
		removeColumn(rankColumn);
		removeColumn(paidColumn);
		removeColumn(freeColumn);
		removeColumn(grossingColumn);
		removeColumn(priceColumn);
		removeColumn(downloadsColumn);
		removeColumn(revenueColumn);
		removeColumn(iapColumn);
	}

	private void removeColumn(Column<RanksGroup, ?> column) {
		if (leaderboardHomeTable.getColumnIndex(column) != -1) {
			leaderboardHomeTable.removeColumn(column);
		}
	}

	@UiHandler({ "countrySelector", "appStoreSelector" })
	void onFiltersChanged(ChangeEvent event) {
		applyFilters.setEnabled(!selectedCountry.equals(countrySelector.getSelectedValue()) || !selectedAppStore.equals(appStoreSelector.getSelectedValue()));
	}

	@UiHandler("applyFilters")
	void onApplyFiltersClicked(ClickEvent event) {
		event.preventDefault();
		boolean updateData = false;// TODO check with previous value
		if (updateData = updateData || !selectedCountry.equals(countrySelector.getSelectedValue())) {
			selectedCountry = countrySelector.getSelectedValue();
		}
		if (updateData = updateData || !selectedAppStore.equals(appStoreSelector.getSelectedValue())) {
			selectedAppStore = appStoreSelector.getSelectedValue();
		}
		if (updateData) {
			applyFilters.setEnabled(false);
			errorPanel.setVisible(false);
			leaderboardHomeTable.setVisible(true);
			homeRankProvider.reset();
			homeRankProvider.fetchHomeTopItems();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.page.Page#onAttach()
	 */
	@Override
	protected void onAttach() {
		super.onAttach();

		ResponsiveDesignHelper.makeTabsResponsive();

		register(DefaultEventBus.get().addHandlerToSource(GetAllTopItemsEventHandler.TYPE, homeRankProvider, this));
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
		TooltipHelper.updateHelperTooltip();
		if (output.status.equals(StatusType.StatusTypeSuccess)) {

		} else {
			leaderboardHomeTable.setVisible(false);
			errorPanel.setVisible(true);
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
		leaderboardHomeTable.setVisible(false);
		errorPanel.setVisible(true);
	}

	private class HomeRankProvider extends AsyncDataProvider<RanksGroup> implements ServiceConstants {

		private Request currentTopItems;
		private Timer timerFetchTopItems = new Timer() {

			@Override
			public void run() {
				currentTopItems.cancel();
				currentTopItems = null;
				updateRowCount(0, true);
				DefaultEventBus.get().fireEventFromSource(new GetAllTopItemsFailure(new GetAllTopItemsRequest(), new Exception()), this);
			}
		};

		private List<RanksGroup> rankHomeGroupList = new ArrayList<RanksGroup>();

		// public List<RanksGroup> getList() {
		// return rankHomeGroupList;
		// }

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.google.gwt.view.client.AbstractDataProvider#onRangeChanged(com.google.gwt.view.client.HasData)
		 */
		@Override
		protected void onRangeChanged(HasData<RanksGroup> display) {
			fetchHomeTopItems();
		}

		public void fetchHomeTopItems() {
			timerFetchTopItems.cancel();

			if (currentTopItems != null) {
				currentTopItems.cancel();
				currentTopItems = null;
			}

			CoreService service = ServiceCreator.createCoreService();

			final GetAllTopItemsRequest input = new GetAllTopItemsRequest(); // JSON Item request, containing the fields used to query the Item table on the DB
			input.accessCode = ACCESS_CODE;

			input.dailyData = FilterController.REVENUE_DAILY_DATA_TYPE;

			input.session = null; // public call

			input.on = FilterHelper.getDaysAgo(2);
			input.country = DataTypeHelper.createCountry(countrySelector.getSelectedValue());
			input.store = DataTypeHelper.createStore(appStoreSelector.getSelectedValue());

			input.listType = (appStoreSelector.getSelectedValue().equals(DataTypeHelper.STORE_IPHONE_A3_CODE) ? "topallapplications" : "topallipadapplications");

			input.category = DataTypeHelper.createCategory(15L);

			Pager pager = new Pager();
			pager.count = 10L;
			pager.start = Long.valueOf(0);
			// pager.boundless = Boolean.FALSE;

			input.pager = pager; // Set pager used to retrieve and format the wished items (start, number of elements, sorting order)

			// Call to retrieve top items from DB. The response contains List<Rank> for the 3 rank types (free, paid, grossing) , a List<Item> and a Pager
			currentTopItems = service.getAllTopItems(input, new AsyncCallback<GetAllTopItemsResponse>() {

				@Override
				public void onSuccess(GetAllTopItemsResponse output) {
					timerFetchTopItems.cancel();
					currentTopItems = null;
					if (output.status == StatusType.StatusTypeSuccess) {

						// Caching retrieved items
						ItemController.get().addItemsToCache(output.items); // caching items

						RanksGroup r;
						for (int i = 0; i < output.paidRanks.size(); i++) {
							rankHomeGroupList.add(r = new RanksGroup());
							r.free = output.freeRanks.get(i);
							r.paid = output.paidRanks.get(i);
							r.grossing = output.grossingRanks.get(i);
						}

						updateRowData(0, rankHomeGroupList); // Inform the displays of the new data. @params Start index, data values
					}
					updateRowCount(rankHomeGroupList.size(), true);

					DefaultEventBus.get().fireEventFromSource(new GetAllTopItemsSuccess(input, output), this);
				}

				@Override
				public void onFailure(Throwable caught) {
					timerFetchTopItems.cancel();
					currentTopItems = null;
					updateRowCount(0, true);
					DefaultEventBus.get().fireEventFromSource(new GetAllTopItemsFailure(input, caught), this);
				}
			});

			timerFetchTopItems.schedule(12000);
		}

		public void reset() {
			rankHomeGroupList.clear();
			updateRowData(0, rankHomeGroupList);
			updateRowCount(0, false);
		}

	}

}
