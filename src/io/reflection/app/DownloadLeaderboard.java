//
//  DownloadLeaderboard.java
//  storedata
//
//  Created by Stefano Capuzzi (capuzzistefano) on 18 Sep 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app;

import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.datatypes.shared.Category;
import io.reflection.app.datatypes.shared.Country;
import io.reflection.app.datatypes.shared.Item;
import io.reflection.app.datatypes.shared.Rank;
import io.reflection.app.service.item.ItemServiceProvider;
import io.reflection.app.service.rank.RankServiceProvider;
import io.reflection.app.shared.util.DataTypeHelper;

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
		Country country = DataTypeHelper.createCountry(req.getParameter("country"));
		Category category = DataTypeHelper.createCategory(Long.valueOf(req.getParameter("category")));
		String listType = req.getParameter("listType");
		Date date = new Date(Long.valueOf(req.getParameter("date")).longValue());
		List<Rank> ranks;
		try {
			if (listType.contains("all")) {
				if (listType.contains("iph")) {
					ranks = RankServiceProvider.provide().getRanks(country, category, "toppaidapplications", date);
					// ranks2 = RankServiceProvider.provide().getRanks(country, category, "topfreeapplications", date);
					// ranks3 = RankServiceProvider.provide().getRanks(country, category, "topgrossingapplications", date);
				} else {
					ranks = RankServiceProvider.provide().getRanks(country, category, "toppaidipadapplications", date);
					// ranks2 = RankServiceProvider.provide().getRanks(country, category, "topfreeipadapplications", date);
					// ranks3 = RankServiceProvider.provide().getRanks(country, category, "topgrossingipadapplications", date);
				}
			} else {
				ranks = RankServiceProvider.provide().getRanks(country, category, listType, date);
				if (!ranks.isEmpty()) {
					// Get Items
					Set<String> itemIds = new HashSet<String>();
					for (Rank rank : ranks) {
						itemIds.add(rank.itemId);
					}
					List<Item> items = ItemServiceProvider.provide().getInternalIdItemBatch(itemIds);
					Map<String, Item> itemLookup = new HashMap<String, Item>();
					for (Item item : items) {
						itemLookup.put(item.internalId, item);
					}
					// Print CSV
					for (Rank rank : ranks) {
						String position = (listType.contains("grossing") ? rank.grossingPosition.toString() : rank.position.toString());
						String appName = itemLookup.get(rank.itemId).name + "(" + itemLookup.get(rank.itemId).creatorName + ")";
						String price = (rank.currency != null && rank.price != null) ? rank.currency + " " + rank.price.floatValue() : "-";
						String downloads = (rank.downloads != null ? rank.downloads.toString() : null);
						String revenue = ((rank.currency != null && rank.revenue != null) ? rank.currency + " " + rank.revenue.floatValue() : null);
						resp.getWriter().println(
								position + "," + appName + "," + price + "," + (revenue != null ? revenue + "," : "")
										+ (downloads != null ? downloads + "," : ""));
					}
				} else {
					resp.getWriter().print("There's no data to download for the filters you have selected - please adjust them and try again");
				}
			}
		} catch (DataAccessException e) {
			e.printStackTrace();
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
