//
//  DownloadLeaderboard.java
//  storedata
//
//  Created by Stefano Capuzzi (capuzzistefano) on 18 Sep 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app;

import io.reflection.app.api.ValidationHelper;
import io.reflection.app.api.core.Core;
import io.reflection.app.api.core.shared.call.GetAllTopItemsRequest;
import io.reflection.app.api.core.shared.call.GetAllTopItemsResponse;
import io.reflection.app.api.shared.datatypes.Session;
import io.reflection.app.collectors.Collector;
import io.reflection.app.collectors.CollectorFactory;
import io.reflection.app.datatypes.shared.Item;
import io.reflection.app.datatypes.shared.Rank;
import io.reflection.app.datatypes.shared.Store;
import io.reflection.app.service.role.RoleServiceProvider;
import io.reflection.app.shared.util.DataTypeHelper;
import io.reflection.app.shared.util.FormattingHelper;

import java.io.IOException;
import java.sql.Date;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
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
		try {
			// resp.setContentType("application/force-download");
			resp.setContentType("text/csv");
			resp.setCharacterEncoding("UTF-8");
			// resp.setHeader("Content-Transfer-Encoding", "binary");
			GetAllTopItemsRequest getAllTopItemsRequest = new GetAllTopItemsRequest();
			getAllTopItemsRequest.accessCode = "765ea1ba-177d-4a01-bbe9-a4e74d10e83c";
			if (req.getParameter("session") != null) {
				getAllTopItemsRequest.session = new Session();
				getAllTopItemsRequest.session.fromJson(req.getParameter("session"));
			}
			getAllTopItemsRequest.country = DataTypeHelper.createCountry(req.getParameter("country"));
			getAllTopItemsRequest.category = DataTypeHelper.createCategory(Long.valueOf(req.getParameter("category")));
			getAllTopItemsRequest.listType = req.getParameter("listType");
			getAllTopItemsRequest.on = new Date(Long.valueOf(req.getParameter("date")).longValue());
			getAllTopItemsRequest.store = new Store();
			getAllTopItemsRequest.store.a3Code = DataTypeHelper.IOS_STORE_A3;
			
			ValidationHelper.validateAuthorised(getAllTopItemsRequest.session.user, RoleServiceProvider.provide().getRole(3L), RoleServiceProvider.provide()
					.getRole(DataTypeHelper.ROLE_ADMIN_ID));
			// TODO check hla permission if not admin
			// String fileName = "\"" + getAllTopItemsRequest.country.a2Code + "_" + getAllTopItemsRequest.store.a3Code + "_" +
			// getAllTopItemsRequest.on.toString()
			// + ".csv\"";
			// resp.setHeader("Content-Disposition", "attachment; filename=" + fileName);
			Collector collector = CollectorFactory.getCollectorForStore(getAllTopItemsRequest.store.a3Code);
			GetAllTopItemsResponse getAllTopItemsResponse = new Core().getAllTopItems(getAllTopItemsRequest);
			Map<String, Item> itemLookup = new HashMap<String, Item>(); // Populate item details
			if (getAllTopItemsResponse.items != null && !getAllTopItemsResponse.items.isEmpty()) {
				for (Item item : getAllTopItemsResponse.items) {
					itemLookup.put(item.internalId, item);
				}
				if (getAllTopItemsRequest.listType.contains("all")) {
					String currency = FormattingHelper.getCurrencySymbol(getAllTopItemsResponse.paidRanks.get(0).currency);
					resp.getWriter().println(",paid,,,,,free,,,,,grossing,,,,");
					resp.getWriter().println(
							"rank,app_name,developer_name,price (" + currency + "),downloads,iap,app_name,developer_name,price (" + currency
									+ "),downloads,iap,app_name,developer_name,price (" + currency + "),revenue (" + currency + "),iap");
					Iterator<Rank> paidRanksIterator = getAllTopItemsResponse.paidRanks.iterator();
					Iterator<Rank> freeRanksIterator = getAllTopItemsResponse.freeRanks.iterator();
					Iterator<Rank> grossingRanksIterator = getAllTopItemsResponse.grossingRanks.iterator();
					while (paidRanksIterator.hasNext() && freeRanksIterator.hasNext() && grossingRanksIterator.hasNext()) {
						Rank rankPaid = paidRanksIterator.next();
						Rank rankFree = freeRanksIterator.next();
						Rank rankGrossing = grossingRanksIterator.next();
						String position = rankPaid.position.toString();
						// Paid add
						String appNamePaid = itemLookup.get(rankPaid.itemId).name;
						String developerNamePaid = itemLookup.get(rankPaid.itemId).creatorName;
						String pricePaid = "-";
						if (rankPaid.price != null) {
							pricePaid = (DataTypeHelper.isZero(rankPaid.price.floatValue()) ? "free" : String.valueOf(rankPaid.price.floatValue()));
						}
						String downloadsPaid = (rankPaid.downloads != null ? new DecimalFormat(",###").format(rankPaid.downloads) : "");
						String iapPaid = DataTypeHelper.jsonPropertiesIapState(itemLookup.get(rankPaid.itemId).properties, "yes", "no", "");
						// Free app
						String appNameFree = itemLookup.get(rankFree.itemId).name;
						String developerNameFree = itemLookup.get(rankFree.itemId).creatorName;
						String priceFree = "-";
						if (rankFree.price != null) {
							priceFree = (DataTypeHelper.isZero(rankFree.price.floatValue()) ? "free" : String.valueOf(rankFree.price.floatValue()));
						}
						String downloadsFree = (rankFree.downloads != null ? new DecimalFormat(",###").format(rankFree.downloads) : "");
						String iapFree = DataTypeHelper.jsonPropertiesIapState(itemLookup.get(rankFree.itemId).properties, "yes", "no", "");
						// Grossing app
						String appNameGrossing = itemLookup.get(rankGrossing.itemId).name;
						String developerNameGrossing = itemLookup.get(rankGrossing.itemId).creatorName;
						String priceGrossing = "-";
						if (rankGrossing.price != null) {
							priceGrossing = (DataTypeHelper.isZero(rankGrossing.price.floatValue()) ? "free" : String.valueOf(rankGrossing.price.floatValue()));
						}
						String revenueGrossing = ((rankGrossing.currency != null && rankGrossing.revenue != null) ? new DecimalFormat(",###")
								.format(rankGrossing.revenue.floatValue()) : "");
						String iapGrossing = DataTypeHelper.jsonPropertiesIapState(itemLookup.get(rankGrossing.itemId).properties, "yes", "no", "");
						// Print Csv line
						resp.getWriter().println(
								FormattingHelper.escapeCsv(position) + "," + FormattingHelper.escapeCsv(appNamePaid) + ","
										+ FormattingHelper.escapeCsv(developerNamePaid) + "," + FormattingHelper.escapeCsv(pricePaid) + ","
										+ FormattingHelper.escapeCsv(downloadsPaid) + "," + FormattingHelper.escapeCsv(iapPaid) + ","
										+ FormattingHelper.escapeCsv(appNameFree) + "," + FormattingHelper.escapeCsv(developerNameFree) + ","
										+ FormattingHelper.escapeCsv(priceFree) + "," + FormattingHelper.escapeCsv(downloadsFree) + ","
										+ FormattingHelper.escapeCsv(iapFree) + "," + FormattingHelper.escapeCsv(appNameGrossing) + ","
										+ FormattingHelper.escapeCsv(developerNameGrossing) + "," + FormattingHelper.escapeCsv(priceGrossing) + ","
										+ FormattingHelper.escapeCsv(revenueGrossing) + "," + FormattingHelper.escapeCsv(iapGrossing));
					}
				} else if (collector.isPaid(getAllTopItemsRequest.listType)) {
					if (!getAllTopItemsResponse.paidRanks.isEmpty()) {
						String currency = FormattingHelper.getCurrencySymbol(getAllTopItemsResponse.paidRanks.get(0).currency);
						resp.getWriter().println("rank,app_name,developer_name,price (" + currency + "),downloads,iap");
						for (Rank rank : getAllTopItemsResponse.paidRanks) {
							String position = rank.position.toString();
							String appName = itemLookup.get(rank.itemId).name;
							String developerName = itemLookup.get(rank.itemId).creatorName;
							String price = "-";
							if (rank.price != null) {
								price = (DataTypeHelper.isZero(rank.price.floatValue()) ? "free" : String.valueOf(rank.price.floatValue()));
							}
							String downloads = (rank.downloads != null ? new DecimalFormat(",###").format(rank.downloads) : "");
							String iap = DataTypeHelper.jsonPropertiesIapState(itemLookup.get(rank.itemId).properties, "yes", "no", "");
							resp.getWriter().println(
									FormattingHelper.escapeCsv(position) + "," + FormattingHelper.escapeCsv(appName) + ","
											+ FormattingHelper.escapeCsv(developerName) + "," + FormattingHelper.escapeCsv(price) + ","
											+ FormattingHelper.escapeCsv(downloads) + "," + FormattingHelper.escapeCsv(iap));
						}
					}
				} else if (collector.isFree(getAllTopItemsRequest.listType)) {
					if (!getAllTopItemsResponse.freeRanks.isEmpty()) {
						String currency = FormattingHelper.getCurrencySymbol(getAllTopItemsResponse.freeRanks.get(0).currency);
						resp.getWriter().println("rank,app_name,developer_name,price (" + currency + "),downloads,iap");
						for (Rank rank : getAllTopItemsResponse.freeRanks) {
							String position = rank.position.toString();
							String appName = itemLookup.get(rank.itemId).name;
							String developerName = itemLookup.get(rank.itemId).creatorName;
							String price = "-";
							if (rank.price != null) {
								price = (DataTypeHelper.isZero(rank.price.floatValue()) ? "free" : String.valueOf(rank.price.floatValue()));
							}
							String downloads = (rank.downloads != null ? new DecimalFormat(",###").format(rank.downloads) : "");
							String iap = DataTypeHelper.jsonPropertiesIapState(itemLookup.get(rank.itemId).properties, "yes", "no", "");
							resp.getWriter().println(
									FormattingHelper.escapeCsv(position) + "," + FormattingHelper.escapeCsv(appName) + ","
											+ FormattingHelper.escapeCsv(developerName) + "," + FormattingHelper.escapeCsv(price) + ","
											+ FormattingHelper.escapeCsv(downloads) + "," + FormattingHelper.escapeCsv(iap));
						}
					}
				} else if (collector.isGrossing(getAllTopItemsRequest.listType)) {
					if (!getAllTopItemsResponse.grossingRanks.isEmpty()) {
						String currency = FormattingHelper.getCurrencySymbol(getAllTopItemsResponse.grossingRanks.get(0).currency);
						resp.getWriter().println("rank,app_name,developer_name,price (" + currency + "),revenue (" + currency + "),iap");
						for (Rank rank : getAllTopItemsResponse.grossingRanks) {
							String position = rank.grossingPosition.toString();
							String appName = itemLookup.get(rank.itemId).name;
							String developerName = itemLookup.get(rank.itemId).creatorName;
							String price = "-";
							if (rank.price != null) {
								price = (DataTypeHelper.isZero(rank.price.floatValue()) ? "free" : String.valueOf(rank.price.floatValue()));
							}
							String revenue = ((rank.currency != null && rank.revenue != null) ? new DecimalFormat(",###").format(rank.revenue.floatValue())
									: "");
							String iap = DataTypeHelper.jsonPropertiesIapState(itemLookup.get(rank.itemId).properties, "yes", "no", "");
							resp.getWriter().println(
									FormattingHelper.escapeCsv(position) + "," + FormattingHelper.escapeCsv(appName) + ","
											+ FormattingHelper.escapeCsv(developerName) + "," + FormattingHelper.escapeCsv(price) + ","
											+ FormattingHelper.escapeCsv(revenue) + "," + FormattingHelper.escapeCsv(iap));
						}
					}
				}
			} else {
				resp.getWriter().print("There's no data to download for the filters you have selected - please adjust them and try again");
			}
			resp.flushBuffer();
		} catch (Exception e) {
			resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
		}
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
