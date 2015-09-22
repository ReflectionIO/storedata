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
import io.reflection.app.collectors.Collector;
import io.reflection.app.collectors.CollectorFactory;
import io.reflection.app.datatypes.shared.Item;
import io.reflection.app.datatypes.shared.Rank;
import io.reflection.app.datatypes.shared.Store;
import io.reflection.app.shared.util.DataTypeHelper;
import io.reflection.app.shared.util.FormattingHelper;

import java.io.IOException;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

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
		Collector collector = CollectorFactory.getCollectorForStore(getAllTopItemsRequest.store.a3Code);
		GetAllTopItemsResponse getAllTopItemsResponse = new Core().getAllTopItems(getAllTopItemsRequest);
		Map<String, Item> itemLookup = new HashMap<String, Item>(); // Populate item details
		for (Item item : getAllTopItemsResponse.items) {
			itemLookup.put(item.internalId, item);
		}
		if (getAllTopItemsRequest.listType.contains("all")) {
			resp.getWriter().println(",paid,,,,free,,,,grossing,,,");
			resp.getWriter().println(
					"rank,app_name,developer_name,price,downloads,app_name,developer_name,price,downloads,app_name,developer_name,price,revenue");
			
		} else if (collector.isFree(getAllTopItemsRequest.listType)) {
			if (!getAllTopItemsResponse.freeRanks.isEmpty()) {
				resp.getWriter().println("rank,app_name,developer_name,price,downloads");
				for (Rank rank : getAllTopItemsResponse.freeRanks) {
					String position = rank.position.toString();
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
					resp.getWriter().println(
							position + "," + FormattingHelper.escapeCsv(appName) + "," + FormattingHelper.escapeCsv(developerName) + "," + price + ","
									+ downloads);
				}
			} else {
				resp.getWriter().print("There's no data to download for the filters you have selected - please adjust them and try again");
			}
		} else if (collector.isPaid(getAllTopItemsRequest.listType)) {
			if (!getAllTopItemsResponse.paidRanks.isEmpty()) {
				resp.getWriter().println("rank,app_name,developer_name,price,downloads");
				for (Rank rank : getAllTopItemsResponse.paidRanks) {
					String position = rank.position.toString();
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
					resp.getWriter().println(
							position + "," + FormattingHelper.escapeCsv(appName) + "," + FormattingHelper.escapeCsv(developerName) + "," + price + ","
									+ downloads);
				}
			} else {
				resp.getWriter().print("There's no data to download for the filters you have selected - please adjust them and try again");
			}
		} else if (collector.isGrossing(getAllTopItemsRequest.listType)) {
			if (!getAllTopItemsResponse.grossingRanks.isEmpty()) {
				resp.getWriter().println("rank,app_name,developer_name,price,revenue");
				for (Rank rank : getAllTopItemsResponse.grossingRanks) {
					String position = rank.grossingPosition.toString();
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
					String revenue = ((rank.currency != null && rank.revenue != null) ? FormattingHelper.getCurrencySymbol(rank.currency)
							+ rank.price.floatValue() : "");
					resp.getWriter().println(
							position + "," + FormattingHelper.escapeCsv(appName) + "," + FormattingHelper.escapeCsv(developerName) + "," + price + ","
									+ revenue);
				}
			} else {
				resp.getWriter().print("There's no data to download for the filters you have selected - please adjust them and try again");
			}
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
