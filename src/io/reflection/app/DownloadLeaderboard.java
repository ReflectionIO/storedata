//
//  DownloadLeaderboard.java
//  storedata
//
//  Created by Stefano Capuzzi (capuzzistefano) on 18 Sep 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app;

import io.reflection.app.api.core.Core;
import io.reflection.app.api.core.shared.call.GetAllTopItemsRequest;
import io.reflection.app.api.core.shared.call.GetAllTopItemsResponse;
import io.reflection.app.datatypes.shared.Item;
import io.reflection.app.datatypes.shared.Rank;
import io.reflection.app.datatypes.shared.Store;
import io.reflection.app.shared.util.DataTypeHelper;
import io.reflection.app.shared.util.FormattingHelper;

import java.io.IOException;
import java.sql.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Stefano Capuzzi (capuzzistefano)
 *
 */
@SuppressWarnings("serial")
public class DownloadLeaderboard extends HttpServlet {

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/csv");
		Core service = new Core();
		GetAllTopItemsRequest getAllTopItemsRequest = new GetAllTopItemsRequest();
		getAllTopItemsRequest.accessCode = "765ea1ba-177d-4a01-bbe9-a4e74d10e83c";
		if (req.getParameter("session") != null) {
			getAllTopItemsRequest.fromJson(req.getParameter("session"));
		}
		getAllTopItemsRequest.country = DataTypeHelper.createCountry(req.getParameter("country"));
		getAllTopItemsRequest.category = DataTypeHelper.createCategory(Long.valueOf(req.getParameter("category")));
		getAllTopItemsRequest.listType = req.getParameter("listType");
		getAllTopItemsRequest.on = new Date(Long.valueOf(req.getParameter("date")).longValue());
		getAllTopItemsRequest.store = new Store();
		getAllTopItemsRequest.store.a3Code = DataTypeHelper.IOS_STORE_A3;
		GetAllTopItemsResponse r = service.getAllTopItems(getAllTopItemsRequest);

		List<Rank> ranks = r.paidRanks;

		if (!ranks.isEmpty()) {
			// Create items lookup
			Set<String> itemIds = new HashSet<String>();
			for (Rank rank : ranks) {
				itemIds.add(rank.itemId);
			}
			Map<String, Item> itemLookup = new HashMap<String, Item>();
			for (Item item : r.items) {
				itemLookup.put(item.internalId, item);
			}
			// Print CSV
			resp.getWriter().println("rank,app_name,developer_name,price,revenue,downloads");
			for (Rank rank : ranks) {
				String position = (getAllTopItemsRequest.listType.contains("grossing") ? rank.grossingPosition.toString() : rank.position.toString());
				String appName = itemLookup.get(rank.itemId).name;
				String developerName = itemLookup.get(rank.itemId).creatorName;
				String price = "-";
				if (rank.currency != null && rank.price != null) {
					if (DataTypeHelper.isZero(rank.price.floatValue())) {
						price = "free";
					} else {
						price = FormattingHelper.getCurrencySymbol(rank.currency) + rank.price.floatValue();
					}
				}
				String downloads = (rank.downloads != null ? rank.downloads.toString() : "");
				String revenue = ((rank.currency != null && rank.revenue != null) ? FormattingHelper.getCurrencySymbol(rank.currency) + rank.price.floatValue()
						: "");
				resp.getWriter().println(
						position + "," + FormattingHelper.escapeCsv(appName) + "," + FormattingHelper.escapeCsv(developerName) + "," + price + "," + revenue
								+ "," + downloads + ",");
			}
		} else {
			resp.getWriter().print("There's no data to download for the filters you have selected - please adjust them and try again");
		}
		resp.flushBuffer();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}
}
