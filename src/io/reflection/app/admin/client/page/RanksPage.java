//
//  RanksPage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 3 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.admin.client.page;

import io.reflection.app.admin.client.cell.MiniAppCell;
import io.reflection.app.admin.client.controller.EventController;
import io.reflection.app.admin.client.controller.RankController;
import io.reflection.app.admin.client.event.ReceivedRanks;
import io.reflection.app.admin.client.event.handler.ReceivedRanksEventHandler;
import io.reflection.app.admin.client.part.BootstrapGwtCellTable;
import io.reflection.app.admin.client.part.datatypes.RanksGroup;
import io.reflection.app.shared.datatypes.Item;
import io.reflection.app.shared.datatypes.Rank;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author billy1380
 * 
 */
public class RanksPage extends Composite implements ReceivedRanksEventHandler {

	private static RanksPageUiBinder uiBinder = GWT.create(RanksPageUiBinder.class);

	interface RanksPageUiBinder extends UiBinder<Widget, RanksPage> {}

	@UiField(provided = true) CellTable<RanksGroup> mRanks = new CellTable<RanksGroup>(Integer.MAX_VALUE, BootstrapGwtCellTable.INSTANCE);

	private List<RanksGroup> mRows = null;

	public RanksPage() {
		initWidget(uiBinder.createAndBindUi(this));

		TextColumn<RanksGroup> position = new TextColumn<RanksGroup>() {

			@Override
			public String getValue(RanksGroup object) {
				return object.free.position.toString();
			}

		};
		// position.setCellStyleNames("text-muted");

		Column<RanksGroup, Item> paid = new Column<RanksGroup, Item>(new MiniAppCell()) {

			@Override
			public Item getValue(RanksGroup object) {
				return RankController.get().lookupItem(object.paid.itemId);
			}
		};

		Column<RanksGroup, Item> free = new Column<RanksGroup, Item>(new MiniAppCell()) {

			@Override
			public Item getValue(RanksGroup object) {
				return RankController.get().lookupItem(object.free.itemId);
			}
		};

		Column<RanksGroup, Item> grossing = new Column<RanksGroup, Item>(new MiniAppCell()) {

			@Override
			public Item getValue(RanksGroup object) {
				return RankController.get().lookupItem(object.grossing.itemId);
			}
		};

		TextHeader rankHeader = new TextHeader("Rank");
		rankHeader.setHeaderStyleNames("col-md-1");
		mRanks.addColumn(position, rankHeader);

		TextHeader paidHeader = new TextHeader("Paid");
		paidHeader.setHeaderStyleNames("col-md-3");
		mRanks.addColumn(paid, paidHeader);

		TextHeader freeHeader = new TextHeader("Free");
		freeHeader.setHeaderStyleNames("col-md-3");
		mRanks.addColumn(free, freeHeader);

		TextHeader grossingHeader = new TextHeader("Grossing");
		grossingHeader.setHeaderStyleNames("col-md-3");
		mRanks.addColumn(grossing, grossingHeader);

		EventController.get().addHandlerToSource(ReceivedRanks.TYPE, RankController.get(), this);

		RankController.get().getAllTopItems();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.admin.client.event.handler.ReceivedRanksEventHandler#receivedRanks(java.lang.String, java.util.List)
	 */
	@Override
	public void receivedRanks(String listType, List<Rank> ranks) {
		if (mRows == null) {
			mRows = new ArrayList<RanksGroup>();
		}

		long count = ranks.size();

		String type = listType.replace("iphone", "");
		type.replace("ipad", "");

		for (int i = 0; i < count; i++) {
			if (mRows.size() <= i) {
				mRows.add(new RanksGroup());
			}

			if ("free".equals(type)) {
				mRows.get(i).free = ranks.get(i);
			} else if ("paid".equals(type)) {
				mRows.get(i).paid = ranks.get(i);
			} else if ("grossing".equals(type)) {
				mRows.get(i).grossing = ranks.get(i);
			}

		}

		mRanks.setRowData(mRows);
	}
}
