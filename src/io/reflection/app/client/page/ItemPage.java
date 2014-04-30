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
import io.reflection.app.api.core.shared.call.event.GetItemRanksEventHandler;
import io.reflection.app.client.cell.ImageAndTextCell;
import io.reflection.app.client.cell.ProgressBarCell;
import io.reflection.app.client.cell.content.ConcreteImageAndText;
import io.reflection.app.client.cell.content.PercentageProgress;
import io.reflection.app.client.controller.EventController;
import io.reflection.app.client.controller.FilterController;
import io.reflection.app.client.controller.FilterController.Filter;
import io.reflection.app.client.controller.ItemController;
import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.controller.NavigationController.Stack;
import io.reflection.app.client.controller.RankController;
import io.reflection.app.client.handler.FilterEventHandler;
import io.reflection.app.client.handler.NavigationEventHandler;
import io.reflection.app.client.page.part.ItemSidePanel;
import io.reflection.app.client.page.part.ItemTopPanel;
import io.reflection.app.client.page.part.RankChart;
import io.reflection.app.client.page.part.RankChart.RankingType;
import io.reflection.app.client.page.part.RankChart.YAxisDataType;
import io.reflection.app.client.part.BootstrapGwtCellTable;
import io.reflection.app.client.part.CircleProgressBar;
import io.reflection.app.client.part.datatypes.ItemRevenue;
import io.reflection.app.client.res.flags.Styles;
import io.reflection.app.datatypes.shared.Item;
import io.reflection.app.shared.util.FormattingHelper;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.Widget;
import com.willshex.gson.json.service.shared.StatusType;

public class ItemPage extends Page implements NavigationEventHandler, GetItemRanksEventHandler, FilterEventHandler {

	private static ItemPageUiBinder uiBinder = GWT.create(ItemPageUiBinder.class);

	interface ItemPageUiBinder extends UiBinder<Widget, ItemPage> {}

	public static final int SELECTED_TAB_PARAMETER_INDEX = 1;

	// @UiField AlertBox mAlertBox;
	@UiField ItemSidePanel mSidePanel;
	@UiField ItemTopPanel mTopPanel;

	@UiField InlineHyperlink mRevenue;
	@UiField InlineHyperlink mDownloads;
	@UiField InlineHyperlink mRanking;

	@UiField LIElement mRevenueItem;
	@UiField LIElement mDownloadsItem;
	@UiField LIElement mRankingItem;

	@UiField RankChart historyChart;
	@UiField CircleProgressBar loader;

	@UiField(provided = true) CellTable<ItemRevenue> revenue = new CellTable<ItemRevenue>(Integer.MAX_VALUE, BootstrapGwtCellTable.INSTANCE);

	private String mItemInternalId;

	private RankingType rankingType;
	private YAxisDataType dataType;
	private Item item;

	private Map<String, LIElement> mTabs = new HashMap<String, LIElement>();

	private String selectedTab = RANKING_CHART_TYPE;

	public ItemPage() {
		initWidget(uiBinder.createAndBindUi(this));

		Styles.INSTANCE.flags().ensureInjected();

		createColumns();

		mTabs.put(REVENUE_CHART_TYPE, mRevenueItem);
		mTabs.put(DOWNLOADS_CHART_TYPE, mDownloadsItem);
		mTabs.put(RANKING_CHART_TYPE, mRankingItem);

		RankController.get().getItemRevenueDataProvider().addDataDisplay(revenue);

	}

	private void createColumns() {

		Column<ItemRevenue, ConcreteImageAndText> countryColumn = new Column<ItemRevenue, ConcreteImageAndText>(new ImageAndTextCell<ConcreteImageAndText>()) {

			@Override
			public ConcreteImageAndText getValue(ItemRevenue object) {
				return new ConcreteImageAndText(object.countryFlagStyleName, object.countryName);
			}
		};

		Column<ItemRevenue, PercentageProgress> percentageColumn = new Column<ItemRevenue, PercentageProgress>(new ProgressBarCell<PercentageProgress>()) {

			@Override
			public PercentageProgress getValue(ItemRevenue object) {
				return new PercentageProgress(object.percentage.floatValue());
			}

		};

		TextColumn<ItemRevenue> paidColumn = new TextColumn<ItemRevenue>() {

			@Override
			public String getValue(ItemRevenue object) {
				return FormattingHelper.getCurrencySymbol(object.currency) + " " + Double.toString(object.paid.doubleValue() / 100.0);
			}
		};

		TextColumn<ItemRevenue> iapColumn = new TextColumn<ItemRevenue>() {

			@Override
			public String getValue(ItemRevenue object) {
				return FormattingHelper.getCurrencySymbol(object.currency) + " " + Double.toString(object.iap.doubleValue() / 100.0);
			}
		};

		TextColumn<ItemRevenue> totalColumn = new TextColumn<ItemRevenue>() {

			@Override
			public String getValue(ItemRevenue object) {
				return FormattingHelper.getCurrencySymbol(object.currency) + " " + Double.toString(object.total.doubleValue() / 100.0);
			}
		};

		TextHeader countryHeader = new TextHeader("Country");
		revenue.addColumn(countryColumn, countryHeader);

		TextHeader percentageHeader = new TextHeader("% total revenue");
		revenue.addColumn(percentageColumn, percentageHeader);

		TextHeader paidHeader = new TextHeader("Paid");
		revenue.addColumn(paidColumn, paidHeader);

		TextHeader iapHeader = new TextHeader("IAP");
		revenue.addColumn(iapColumn, iapHeader);

		TextHeader totalHeader = new TextHeader("Total");
		revenue.addColumn(totalColumn, totalHeader);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.Composite#onAttach()
	 */
	@Override
	protected void onAttach() {
		super.onAttach();

		register(EventController.get().addHandlerToSource(NavigationEventHandler.TYPE, NavigationController.get(), this));
		// register(EventController.get().addHandlerToSource(SearchForItemEventHandler.TYPE, ItemController.get(), this));
		register(EventController.get().addHandlerToSource(FilterEventHandler.TYPE, FilterController.get(), this));
		register(EventController.get().addHandlerToSource(GetItemRanksEventHandler.TYPE, RankController.get(), this));

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.NavigationEventHandler#navigationChanged(io.reflection.app.client.controller.NavigationController.Stack,
	 * io.reflection.app.client.controller.NavigationController.Stack)
	 */
	@Override
	public void navigationChanged(Stack previous, Stack current) {
		if (current != null && PageType.ItemPageType.equals(current.getPage())) {
			if ("view".equals(current.getAction()) && (mItemInternalId = current.getParameter(0)) != null) {
				item = null;

				Filter f = FilterController.get().getFilter();
				String currentFilter = f.asItemFilterString();

				if (currentFilter != null && currentFilter.length() > 0) {
					mRevenue.setTargetHistoryToken(PageType.ItemPageType.asTargetHistoryToken("view", mItemInternalId, REVENUE_CHART_TYPE, currentFilter));
					mDownloads.setTargetHistoryToken(PageType.ItemPageType.asTargetHistoryToken("view", mItemInternalId, DOWNLOADS_CHART_TYPE, currentFilter));
					mRanking.setTargetHistoryToken(PageType.ItemPageType.asTargetHistoryToken("view", mItemInternalId, RANKING_CHART_TYPE, currentFilter));
				}

				if ((item = ItemController.get().lookupItem(mItemInternalId)) != null) {
					displayItemDetails();
				} else {
					item = new Item();
					item.internalId = mItemInternalId;
					item.source = FilterController.get().getFilter().getStoreA3Code();

					// AlertBoxHelper.configureAlert(mAlertBox, AlertBoxType.InfoAlertBoxType, true, "Getting details",
					// " - This will only take a few seconds...",
					// false).setVisible(true);
				}

				selectedTab = current.getParameter(SELECTED_TAB_PARAMETER_INDEX);

				refreshTabs();

				rankingType = RankingType.fromString(f.getListType());
				dataType = YAxisDataType.fromString(selectedTab);

				mTopPanel.updateFromFilter();

				getHistoryChartData();

				// AlertBoxHelper.configureAlert(mAlertBox, AlertBoxType.SuccessAlertBoxType, false, "Item",
				// " - Normally we would display items details for (" + view + ").", false).setVisible(true);

			} else {
				// AlertBoxHelper.configureAlert(mAlertBox, AlertBoxType.DangerAlertBoxType, false, "Item", " - We did not find the requrested item!", false)
				// .setVisible(true);

				PageType.RanksPageType.show("view", OVERALL_LIST_TYPE, FilterController.get().asRankFilterString());
			}
		}

	}

	private void displayItemDetails() {
		// mAlertBox.dismiss();

		mSidePanel.setItem(item);
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
		for (String key : mTabs.keySet()) {
			mTabs.get(key).removeClassName("active");
		}

		mTabs.get(selectedTab).addClassName("active");
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
			if (output.ranks != null && output.ranks.size() > 0) {
				item = output.item;

				displayItemDetails();

				historyChart.setData(output.item, output.ranks, rankingType, dataType);
				mSidePanel.setPrice(output.ranks.get(0).currency, output.ranks.get(0).price);

				// mAlertBox.dismiss();
			}
			// else {
			// AlertBoxHelper.configureAlert(mAlertBox, AlertBoxType.WarningAlertBoxType, false, "Warning", " - Item rank history could not be obtained!",
			// false).setVisible(true);
			// }
		} else {
			// do nothing
		}

		loader.setVisible(false);
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
		// AlertBoxHelper.configureAlert(mAlertBox, AlertBoxType.DangerAlertBoxType, false, "Error", " - An error occured fetching item history!", false)
		// .setVisible(true);
	}

	private void getHistoryChartData() {
		if (item != null) {
			// AlertBoxHelper.configureAlert(mAlertBox, AlertBoxType.InfoAlertBoxType, true, "Getting History",
			// " - Please wait while we fetch the rank history for the selected item", false).setVisible(true);

			historyChart.setLoading(true);
			loader.setVisible(true);

			RankController.get().fetchItemRanks(item);
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
			PageType.ItemPageType.show("view", mItemInternalId, selectedTab, FilterController.get().asItemFilterString());
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
			PageType.ItemPageType.show("view", mItemInternalId, selectedTab, FilterController.get().asItemFilterString());
		}
	}

}
