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
import static io.reflection.app.client.controller.FilterController.OVERALL_LIST_TYPE;
import static io.reflection.app.client.controller.FilterController.PAID_LIST_TYPE;
import io.reflection.app.api.core.client.CoreService;
import io.reflection.app.api.core.shared.call.GetAllTopItemsRequest;
import io.reflection.app.api.core.shared.call.GetAllTopItemsResponse;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.client.cell.AppDetailsAndPredictionCell;
import io.reflection.app.client.cell.LeaderboardDownloadsCell;
import io.reflection.app.client.cell.LeaderboardRevenueCell;
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
import io.reflection.app.client.mixpanel.MixpanelHelper;
import io.reflection.app.client.part.BootstrapGwtCellTable;
import io.reflection.app.client.part.ErrorPanel;
import io.reflection.app.client.part.LoadingIndicator;
import io.reflection.app.client.part.NoDataPanel;
import io.reflection.app.client.part.datatypes.RanksGroup;
import io.reflection.app.client.popup.SignUpPopup;
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
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author billy1380
 * 
 */
public class HomePage extends Page {

	private static HomePageUiBinder uiBinder = GWT.create(HomePageUiBinder.class);

	interface HomePageUiBinder extends UiBinder<Widget, HomePage> {}

	@UiField LIElement allItem;
	@UiField LIElement paidItem;
	@UiField LIElement freeItem;
	@UiField LIElement grossingItem;
	@UiField SpanElement dateFixed;
	@UiField Selector countrySelector;
	private String selectedCountry = "gb";
	@UiField Selector appStoreSelector;
	private String selectedAppStore = "iph";
	@UiField Selector categorySelector;
	private String selectedCategory = "15"; // games
	@UiField Button applyFilters;
	@UiField Anchor viewMoreApps;
	@UiField InlineHyperlink getStarted;
	@UiField InlineHyperlink signUp;
	@UiField ErrorPanel errorPanel;
	@UiField NoDataPanel noDataPanel;
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
	private Column<RanksGroup, Rank> downloadsColumn;
	private Column<RanksGroup, Rank> revenueColumn;
	private Column<RanksGroup, SafeHtml> iapColumn;
	private LoadingIndicator loadingIndicatorAll = AnimationHelper.getLeaderboardAllLoadingIndicator(ServiceConstants.SHORT_STEP_VALUE);
	private LoadingIndicator loadingIndicatorFreeList = AnimationHelper.getLeaderboardListLoadingIndicator(ServiceConstants.SHORT_STEP_VALUE, true);
	private LoadingIndicator loadingIndicatorPaidGrossingList = AnimationHelper.getLeaderboardListLoadingIndicator(ServiceConstants.SHORT_STEP_VALUE, false);

	private HomeRankProvider homeRankProvider = new HomeRankProvider();
	private static String selectedTab = OVERALL_LIST_TYPE;
	private ReflectionMainStyles style = Styles.STYLES_INSTANCE.reflectionMainStyle();
	private SignUpPopup signUpPopup = new SignUpPopup();
	private boolean isStatusError;

	public HomePage() {
		initWidget(uiBinder.createAndBindUi(this));

		applyFilters.getElement().setAttribute("data-tooltip", "Update results");
		if (!SessionController.get().isAdmin()) {
			categorySelector.setTooltip("We're in beta. More categories and countries will be available soon.");
		}
		dateFixed.setInnerText(FormattingHelper.DATE_FORMATTER_DD_MMM_YYYY.format(FilterHelper.getDaysAgo(3)));
		FilterHelper.addCountries(countrySelector, SessionController.get().isAdmin());
		FilterHelper.addStores(appStoreSelector);
		FilterHelper.addCategories(categorySelector, SessionController.get().isAdmin());
		countrySelector.setSelectedIndex(3);
		appStoreSelector.setSelectedIndex(1);
		categorySelector.setSelectedIndex(0);

		leaderboardHomeTable.getTableLoadingSection().addClassName(style.tableBodyLoading());
		homeRankProvider.addDataDisplay(leaderboardHomeTable);

		Event.sinkEvents(allItem, Event.ONCLICK);
		Event.sinkEvents(paidItem, Event.ONCLICK);
		Event.sinkEvents(freeItem, Event.ONCLICK);
		Event.sinkEvents(grossingItem, Event.ONCLICK);
		Event.setEventListener(allItem, new EventListener() {

			@Override
			public void onBrowserEvent(Event event) {
				if (Event.ONCLICK == event.getTypeInt()) {
					paidItem.removeClassName(style.isActive());
					freeItem.removeClassName(style.isActive());
					grossingItem.removeClassName(style.isActive());
					allItem.addClassName(style.isActive());
					removeAllColumns();
					leaderboardHomeTable.setColumnWidth(rankColumn, 10.0, Unit.PCT);
					leaderboardHomeTable.setColumnWidth(paidColumn, 30.0, Unit.PCT);
					leaderboardHomeTable.setColumnWidth(freeColumn, 30.0, Unit.PCT);
					leaderboardHomeTable.setColumnWidth(grossingColumn, 30.0, Unit.PCT);
					leaderboardHomeTable.addColumn(rankColumn, rankHeader);
					leaderboardHomeTable.addColumn(paidColumn, "Paid");
					leaderboardHomeTable.addColumn(freeColumn, "Free");
					leaderboardHomeTable.addColumn(grossingColumn, "Grossing");
					ResponsiveDesignHelper.makeTabsResponsive();
					leaderboardHomeTable.setLoadingIndicator(loadingIndicatorAll);
					leaderboardHomeTable.getElement().removeClassName(style.tableAppGroup());
					leaderboardHomeTable.getElement().addClassName(style.tableOverall());
					TooltipHelper.updateHelperTooltip();
					selectedTab = OVERALL_LIST_TYPE;
				}
			}
		});
		Event.setEventListener(paidItem, new EventListener() {

			@Override
			public void onBrowserEvent(Event event) {
				if (Event.ONCLICK == event.getTypeInt()) {
					allItem.removeClassName(style.isActive());
					freeItem.removeClassName(style.isActive());
					grossingItem.removeClassName(style.isActive());
					paidItem.addClassName(style.isActive());
					removeAllColumns();
					leaderboardHomeTable.setColumnWidth(rankColumn, 10.0, Unit.PCT);
					leaderboardHomeTable.setColumnWidth(paidColumn, 42.0, Unit.PCT);
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
					leaderboardHomeTable.getElement().removeClassName(style.tableOverall());
					leaderboardHomeTable.getElement().addClassName(style.tableAppGroup());
					TooltipHelper.updateHelperTooltip();
					selectedTab = PAID_LIST_TYPE;
				}
			}
		});
		Event.setEventListener(freeItem, new EventListener() {

			@Override
			public void onBrowserEvent(Event event) {
				if (Event.ONCLICK == event.getTypeInt()) {
					allItem.removeClassName(style.isActive());
					paidItem.removeClassName(style.isActive());
					grossingItem.removeClassName(style.isActive());
					freeItem.addClassName(style.isActive());
					removeAllColumns();
					leaderboardHomeTable.setColumnWidth(rankColumn, 10.0, Unit.PCT);
					leaderboardHomeTable.setColumnWidth(freeColumn, 42.0, Unit.PCT);
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
					leaderboardHomeTable.getElement().removeClassName(style.tableOverall());
					leaderboardHomeTable.getElement().addClassName(style.tableAppGroup());
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
					allItem.removeClassName(style.isActive());
					paidItem.removeClassName(style.isActive());
					freeItem.removeClassName(style.isActive());
					grossingItem.addClassName(style.isActive());
					removeAllColumns();
					leaderboardHomeTable.setColumnWidth(rankColumn, 10.0, Unit.PCT);
					leaderboardHomeTable.setColumnWidth(grossingColumn, 42.0, Unit.PCT);
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
					leaderboardHomeTable.getElement().removeClassName(style.tableOverall());
					leaderboardHomeTable.getElement().addClassName(style.tableAppGroup());
					ResponsiveDesignHelper.makeTabsResponsive();
					TooltipHelper.updateHelperTooltip();
					selectedTab = GROSSING_LIST_TYPE;
				}
			}
		});

		createColumns();
		leaderboardHomeTable.setColumnWidth(rankColumn, 10.0, Unit.PCT);
		leaderboardHomeTable.setColumnWidth(paidColumn, 30.0, Unit.PCT);
		leaderboardHomeTable.setColumnWidth(freeColumn, 30.0, Unit.PCT);
		leaderboardHomeTable.setColumnWidth(grossingColumn, 30.0, Unit.PCT);
		leaderboardHomeTable.addColumn(rankColumn, rankHeader);
		leaderboardHomeTable.addColumn(paidColumn, "Paid");
		leaderboardHomeTable.addColumn(freeColumn, "Free");
		leaderboardHomeTable.addColumn(grossingColumn, "Grossing");
		leaderboardHomeTable.setLoadingIndicator(loadingIndicatorAll);

		// Hide Overall tab on mobile and show grossing
		Window.addResizeHandler(new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				if (event.getWidth() <= 719) {
					if (selectedTab.equals(OVERALL_LIST_TYPE)) {
						allItem.removeClassName(style.isActive());
						paidItem.removeClassName(style.isActive());
						freeItem.removeClassName(style.isActive());
						grossingItem.addClassName(style.isActive());
						removeAllColumns();
						leaderboardHomeTable.setColumnWidth(rankColumn, 10.0, Unit.PCT);
						leaderboardHomeTable.setColumnWidth(grossingColumn, 42.0, Unit.PCT);
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
					allItem.getStyle().setDisplay(Display.NONE);
				} else {
					allItem.getStyle().setDisplay(Display.BLOCK);
				}
			}
		});

		TooltipHelper.updateHelperTooltip();

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

		AppDetailsAndPredictionCell appDetailsCell = new AppDetailsAndPredictionCell();

		paidColumn = new Column<RanksGroup, Rank>(appDetailsCell) {

			@Override
			public Rank getValue(RanksGroup object) {
				return object.paid;
			}
		};
		paidColumn.setCellStyleNames(style.mhxte6ciA());

		freeColumn = new Column<RanksGroup, Rank>(appDetailsCell) {

			@Override
			public Rank getValue(RanksGroup object) {
				return object.free;
			}

		};
		freeColumn.setCellStyleNames(style.mhxte6ciA());

		grossingColumn = new Column<RanksGroup, Rank>(appDetailsCell) {

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

		downloadsColumn = new Column<RanksGroup, Rank>(new LeaderboardDownloadsCell()) {

			@Override
			public Rank getValue(RanksGroup object) {
				return rankForListType(object);
			}
		};
		// downloadsColumn.setFieldUpdater(new FieldUpdater<RanksGroup, Rank>() {
		//
		// @Override
		// public void update(int index, RanksGroup object, Rank value) {
		// if (SessionController.get().isStandardDeveloper() && SessionController.get().hasLinkedAccount()) {
		// new PremiumPopup().show(true);
		// } else if (SessionController.get().isValidSession()) {
		// new AddLinkedAccountPopup().show("Link Your Appstore Account",
		// "You need to link your iTunes Connect account to use this feature, it only takes a moment");
		// } else {
		// signUpPopup.show();
		// }
		// }
		// });
		downloadsColumn.setCellStyleNames(style.mhxte6ciA());

		revenueColumn = new Column<RanksGroup, Rank>(new LeaderboardRevenueCell()) {

			@Override
			public Rank getValue(RanksGroup object) {
				return rankForListType(object);
			}
		};
		// revenueColumn.setFieldUpdater(new FieldUpdater<RanksGroup, Rank>() {
		//
		// @Override
		// public void update(int index, RanksGroup object, Rank value) {
		// if (SessionController.get().isStandardDeveloper() && SessionController.get().hasLinkedAccount()) {
		// new PremiumPopup().show(true);
		// } else if (SessionController.get().isValidSession()) {
		// new AddLinkedAccountPopup().show("Link Your Appstore Account",
		// "You need to link your iTunes Connect account to use this feature, it only takes a moment");
		// } else {
		// signUpPopup.show();
		// }
		// }
		// });
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

	public static String getSelectedTab() {
		return selectedTab;
	}

	@UiHandler({ "countrySelector", "appStoreSelector" })
	// "categorySelector"
	void onFiltersChanged(ChangeEvent event) {
		applyFilters.setEnabled(isStatusError || !selectedCountry.equals(countrySelector.getSelectedValue())
				|| !selectedAppStore.equals(appStoreSelector.getSelectedValue()));
		// || !selectedCategory.equals(categorySelector.getSelectedValue())
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
		// if (updateData = updateData || !selectedCategory.equals(categorySelector.getSelectedValue())) {
		// selectedCategory = categorySelector.getSelectedValue();
		// }
		if (updateData || isStatusError) {
			isStatusError = false;
			applyFilters.setEnabled(false);
			errorPanel.setVisible(false);
			noDataPanel.setVisible(false);
			leaderboardHomeTable.setVisible(true);
			homeRankProvider.reset();
			homeRankProvider.fetchHomeTopItems();
		}
	}

	@UiHandler("viewMoreApps")
	void onViewMoreAppsClicked(ClickEvent event) {
		event.preventDefault();
		signUpPopup.show();
	}

	@UiHandler("getStarted")
	void onGetStartedClicked(ClickEvent event) {
		event.preventDefault();
		MixpanelHelper.trackClicked(MixpanelHelper.Event.GO_TO_SIGNUP_PAGE, "home_button_getstarted");
	}

	@UiHandler("signUp")
	void onSignUpClicked(ClickEvent event) {
		event.preventDefault();
		MixpanelHelper.trackClicked(MixpanelHelper.Event.GO_TO_SIGNUP_PAGE, "home_button_signup");
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

		if (Window.getClientWidth() <= 719) {
			if (selectedTab.equals(OVERALL_LIST_TYPE)) {
				allItem.removeClassName(style.isActive());
				paidItem.removeClassName(style.isActive());
				freeItem.removeClassName(style.isActive());
				grossingItem.addClassName(style.isActive());
				removeAllColumns();
				leaderboardHomeTable.setColumnWidth(rankColumn, 10.0, Unit.PCT);
				leaderboardHomeTable.setColumnWidth(grossingColumn, 42.0, Unit.PCT);
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
			allItem.getStyle().setDisplay(Display.NONE);
		} else {
			allItem.getStyle().setDisplay(Display.BLOCK);
		}
	}

	private class HomeRankProvider extends AsyncDataProvider<RanksGroup> implements ServiceConstants {

		private Request currentTopItems;
		private Timer timerFetchTopItems = new Timer() {

			@Override
			public void run() {
				currentTopItems.cancel();
				currentTopItems = null;
				updateRowCount(0, true);
				leaderboardHomeTable.setVisible(false);
				errorPanel.setVisible(true);
				isStatusError = true;
				applyFilters.setEnabled(true);
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

			input.on = FilterHelper.getDaysAgo(3);
			input.store = DataTypeHelper.createStore("ios");
			input.country = DataTypeHelper.createCountry(selectedCountry);
			input.listType = (selectedAppStore.equals(DataTypeHelper.STORE_IPHONE_A3_CODE) ? "topallapplications" : "topallipadapplications");
			input.category = DataTypeHelper.createCategory(Long.valueOf(selectedCategory));

			Pager pager = new Pager();
			pager.count = Long.valueOf(10);
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
						if (output.freeRanks != null) {
							// Caching retrieved items
							ItemController.get().addItemsToCache(output.items); // caching items

							if (output.freeRanks != null) {
								RanksGroup r;
								for (int i = 0; i < 200; i++) {
									rankHomeGroupList.add(r = new RanksGroup());
									r.free = output.freeRanks.get(i);
									r.paid = output.paidRanks.get(i);
									r.grossing = output.grossingRanks.get(i);
								}
							}
						} else {
							leaderboardHomeTable.setVisible(false);
							noDataPanel.setVisible(true);
						}
						updateRowData(0, rankHomeGroupList); // Inform the displays of the new data. @params Start index, data values
					} else {
						leaderboardHomeTable.setVisible(false);
						errorPanel.setVisible(true);
						isStatusError = true;
						applyFilters.setEnabled(true);
					}
					updateRowCount(rankHomeGroupList.size(), true);

					TooltipHelper.updateHelperTooltip();
				}

				@Override
				public void onFailure(Throwable caught) {
					timerFetchTopItems.cancel();
					currentTopItems = null;
					updateRowCount(0, true);
					leaderboardHomeTable.setVisible(false);
					errorPanel.setVisible(true);
					isStatusError = true;
					applyFilters.setEnabled(true);
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
