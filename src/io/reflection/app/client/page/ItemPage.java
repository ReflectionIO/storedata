//
//  ItemPage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 10 Jan 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page;

import io.reflection.app.api.core.shared.call.GetItemRanksRequest;
import io.reflection.app.api.core.shared.call.GetItemRanksResponse;
import io.reflection.app.api.core.shared.call.event.GetItemRanksEventHandler;
import io.reflection.app.client.controller.EventController;
import io.reflection.app.client.controller.FilterController;
import io.reflection.app.client.controller.ItemController;
import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.controller.NavigationController.Stack;
import io.reflection.app.client.controller.RankController;
import io.reflection.app.client.handler.NavigationEventHandler;
import io.reflection.app.client.helper.AlertBoxHelper;
import io.reflection.app.client.part.AlertBox;
import io.reflection.app.client.part.AlertBox.AlertBoxType;
import io.reflection.app.client.part.ItemSidePanel;
import io.reflection.app.client.part.RankChart;
import io.reflection.app.datatypes.shared.Item;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.willshex.gson.json.service.shared.StatusType;

public class ItemPage extends Page implements NavigationEventHandler, GetItemRanksEventHandler {

	private static ItemPageUiBinder uiBinder = GWT.create(ItemPageUiBinder.class);

	interface ItemPageUiBinder extends UiBinder<Widget, ItemPage> {}

	@UiField AlertBox mAlertBox;
	@UiField ItemSidePanel mSidePanel;

	@UiField InlineHyperlink mRevenue;
	@UiField InlineHyperlink mDownloads;
	@UiField InlineHyperlink mRanking;

	@UiField LIElement mRevenueItem;
	@UiField LIElement mDownloadsItem;
	@UiField LIElement mRankingItem;

	@UiField RankChart historyChart;

	private String mItemExternalId;

	private String mChartType = REVENUE_CHART_TYPE;

	private static final String REVENUE_CHART_TYPE = "revenue";
	private static final String DOWNLOADS_CHART_TYPE = "downloads";
	private static final String RANKING_CHART_TYPE = "ranking";

	private Map<String, LIElement> mTabs = new HashMap<String, LIElement>();

	public ItemPage() {
		initWidget(uiBinder.createAndBindUi(this));

		mTabs.put(REVENUE_CHART_TYPE, mRevenueItem);
		mTabs.put(DOWNLOADS_CHART_TYPE, mDownloadsItem);
		mTabs.put(RANKING_CHART_TYPE, mRankingItem);
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
		register(EventController.get().addHandlerToSource(GetItemRanksEventHandler.TYPE, RankController.get(), this));
	}

	@Override
	public void navigationChanged(Stack stack) {
		if (stack != null && "item".equals(stack.getPage())) {
			if ("view".equals(stack.getAction()) && (mItemExternalId = stack.getParameter(0)) != null) {
				FilterController.get().start();
				Date startDate = FilterController.get().getStartDate();
				FilterController.get().setEndDate(startDate);
				Date newStartDate = new Date(startDate.getTime());
				CalendarUtil.addDaysToDate(newStartDate, -10);
				FilterController.get().setStartDate(newStartDate);
				FilterController.get().setListType(stack.getParameter(1));
				FilterController.get().commit();

				Item item = null;

				mRevenue.setTargetHistoryToken("item/view/" + mItemExternalId);
				mDownloads.setTargetHistoryToken("item/view/" + mItemExternalId);
				mRanking.setTargetHistoryToken("item/view/" + mItemExternalId);

				if ((item = ItemController.get().lookupItem(mItemExternalId)) != null) {
					displayItemDetails(item);
				} else {
					item = new Item();
					item.externalId = mItemExternalId;
					item.source = FilterController.get().getStore().a3Code;
					
					AlertBoxHelper.configureAlert(mAlertBox, AlertBoxType.InfoAlertBoxType, true, "Getting details", " - This will only take a few seconds...",
							false).setVisible(true);
				}
				
				getHistoryChartData(item);

				// AlertBoxHelper.configureAlert(mAlertBox, AlertBoxType.SuccessAlertBoxType, false, "Item",
				// " - Normally we would display items details for (" + view + ").", false).setVisible(true);
			} else {
				AlertBoxHelper.configureAlert(mAlertBox, AlertBoxType.DangerAlertBoxType, false, "Item", " - We did not find the requrested item!", false)
						.setVisible(true);
			}
		}

	}

	private void displayItemDetails(Item item) {
		mAlertBox.setVisible(false);

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
	// if (mItemExternalId != null && mItemExternalId.equals(input.query) && output.status == StatusType.StatusTypeSuccess) {
	//
	// // for now we don't lookup the item again... because it causes an infinite loop of lookup failure
	// if (output.items != null) {
	// for (Item item : output.items) {
	// if (mItemExternalId.equals(item.externalId)) {
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
	// if (mItemExternalId != null && mItemExternalId.equals(input.query)) {
	// AlertBoxHelper.configureAlert(mAlertBox, AlertBoxType.DangerAlertBoxType, false, "Item", " - We could not find the requrested item!", false)
	// .setVisible(true);
	// }
	// }

	@UiHandler({ "mRevenue", "mDownloads", "mRanking" })
	void onClicked(ClickEvent e) {
		boolean changed = false;

		if (e.getSource() == mRevenue && !REVENUE_CHART_TYPE.equals(mChartType)) {
			mChartType = REVENUE_CHART_TYPE;
			changed = true;
		} else if (e.getSource() == mDownloads && !DOWNLOADS_CHART_TYPE.equals(mChartType)) {
			mChartType = DOWNLOADS_CHART_TYPE;
			changed = true;
		} else if (e.getSource() == mRanking && !RANKING_CHART_TYPE.equals(mChartType)) {
			mChartType = RANKING_CHART_TYPE;
			changed = true;
		}

		if (changed) {
			refreshTabs();
		}
	}

	private void refreshTabs() {
		for (String key : mTabs.keySet()) {
			mTabs.get(key).removeClassName("active");
		}

		mTabs.get(mChartType).addClassName("active");
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
		if (output != null && output.status == StatusType.StatusTypeSuccess && output.ranks != null && output.ranks.size() > 0) {
			displayItemDetails(output.item);

			refreshTabs();

			historyChart.setData(output.item, output.ranks);

			mAlertBox.setVisible(false);
		} else {
			AlertBoxHelper.configureAlert(mAlertBox, AlertBoxType.WarningAlertBoxType, false, "Warning", " - Item rank history could not be obtained!", false)
					.setVisible(true);
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
		AlertBoxHelper.configureAlert(mAlertBox, AlertBoxType.DangerAlertBoxType, false, "Error", " - An error occured fetching item history!", false)
				.setVisible(true);
	}

	private void getHistoryChartData(Item item) {
		AlertBoxHelper.configureAlert(mAlertBox, AlertBoxType.InfoAlertBoxType, true, "Getting History",
				" - Please wait while we fetch the rank history for the selected item", false).setVisible(true);

		RankController.get().fetchItemRanks(item);

		historyChart.setLoading(true);

	}

}
