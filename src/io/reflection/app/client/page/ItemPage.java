//
//  ItemPage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 10 Jan 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page;

import java.util.HashMap;
import java.util.Map;

import io.reflection.app.api.admin.shared.call.event.SearchForItemEventHandler;
import io.reflection.app.api.core.shared.call.SearchForItemRequest;
import io.reflection.app.api.core.shared.call.SearchForItemResponse;
import io.reflection.app.client.controller.EventController;
import io.reflection.app.client.controller.ItemController;
import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.controller.NavigationController.Stack;
import io.reflection.app.client.handler.NavigationEventHandler;
import io.reflection.app.client.helper.AlertBoxHelper;
import io.reflection.app.client.part.AlertBox;
import io.reflection.app.client.part.AlertBox.AlertBoxType;
import io.reflection.app.client.part.ItemSidePanel;
import io.reflection.app.datatypes.shared.Item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.Widget;
import com.willshex.gson.json.service.shared.StatusType;

public class ItemPage extends Composite implements NavigationEventHandler, SearchForItemEventHandler {

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

		EventController.get().addHandlerToSource(NavigationEventHandler.TYPE, NavigationController.get(), this);
		EventController.get().addHandlerToSource(SearchForItemEventHandler.TYPE, ItemController.get(), this);
	}

	@Override
	public void navigationChanged(Stack stack) {

		if (stack != null && "item".equals(stack.getPage())) {

			if ("view".equals(stack.getAction()) && (mItemExternalId = stack.getParameter(0)) != null) {

				Item item = null;

				mRevenue.setTargetHistoryToken("item/view/" + mItemExternalId);
				mDownloads.setTargetHistoryToken("item/view/" + mItemExternalId);
				mRanking.setTargetHistoryToken("item/view/" + mItemExternalId);

				if ((item = ItemController.get().lookupItem(mItemExternalId)) != null) {
					displayItemDetails(item);
				} else {
					AlertBoxHelper.configureAlert(mAlertBox, AlertBoxType.InfoAlertBoxType, true, "Getting details", " - This will only take a few seconds...",
							false).setVisible(true);
				}

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

		mSidePanel.setName(item.name);
		mSidePanel.setImage(item.largeImage);
		mSidePanel.setCreatorName(item.creatorName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.reflection.app.api.admin.shared.call.event.SearchForItemEventHandler#searchForItemSuccess(io.reflection.app.api.core.shared.call.SearchForItemRequest,
	 * io.reflection.app.api.core.shared.call.SearchForItemResponse)
	 */
	@Override
	public void searchForItemSuccess(SearchForItemRequest input, SearchForItemResponse output) {
		boolean found = false;

		if (mItemExternalId != null && mItemExternalId.equals(input.query) && output.status == StatusType.StatusTypeSuccess) {

			// for now we don't lookup the item again... because it causes an infinite loop of lookup failure
			if (output.items != null) for (Item item : output.items) {
				if (mItemExternalId.equals(item.externalId)) {
					displayItemDetails(item);
					
					refreshTabs();
					
					found = true;
					break;
				}
			}

		}

		if (!found) {
			AlertBoxHelper.configureAlert(mAlertBox, AlertBoxType.DangerAlertBoxType, false, "Item", " - We did not find the requrested item!", false)
					.setVisible(true);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.reflection.app.api.admin.shared.call.event.SearchForItemEventHandler#searchForItemFailure(io.reflection.app.api.core.shared.call.SearchForItemRequest,
	 * java.lang.Throwable)
	 */
	@Override
	public void searchForItemFailure(SearchForItemRequest input, Throwable caught) {
		if (mItemExternalId != null && mItemExternalId.equals(input.query)) {
			AlertBoxHelper.configureAlert(mAlertBox, AlertBoxType.DangerAlertBoxType, false, "Item", " - We could not find the requrested item!", false)
					.setVisible(true);
		}
	}

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

}
