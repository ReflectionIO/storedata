//
//  MyApp.java
//  storedata
//
//  Created by Stefano Capuzzi (stefanocapuzzi) on 24 Mar 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.part.datatypes;

import io.reflection.app.datatypes.shared.Item;
import io.reflection.app.datatypes.shared.Rank;
import io.reflection.app.shared.util.FormattingHelper;

import java.util.List;

/**
 * @author stefanocapuzzi
 * 
 */
public class MyApp {

	private static final String UNKNOWN_VALUE = "-";
	//private static final String NIL_VALUE = "0";

	public Item item;

	public List<Rank> ranks;

	public String overallPosition;
	public String overallDownloads;
	public String overallPrice;
	public String overallRevenue;

	/**
	 * Updates the overall values based on the ranks
	 */
	public void updateOverallValues() {
		if (ranks != null || (ranks != null && ranks.size() == 0)) {
			int downloads = 0;
			float revenue = 0;

			int minPosition = Integer.MAX_VALUE, maxPosition = Integer.MIN_VALUE;
			float minPrice = Float.MAX_VALUE, maxPrice = -Float.MAX_VALUE; // ! Be aware: Float.MIN_VALUE is the smaller POSITIVE value

			int position, grossingPosition;
			float price;

			for (Rank rank : ranks) {
				position = rank.position == null || rank.position.intValue() < 1 ? Integer.MAX_VALUE : rank.position.intValue();
				grossingPosition = rank.grossingPosition == null || rank.grossingPosition.intValue() < 1 ? Integer.MAX_VALUE : rank.grossingPosition.intValue();
				price = rank.price.floatValue();

				// Calculate MIN and MAX positions (free or paid, and grossing together) in the range of ranks - ! It doesn't differentiate the type of position
				if (minPosition > position) {
					minPosition = position;
				}
				if (minPosition > grossingPosition) {
					minPosition = grossingPosition;
				}
				if (maxPosition < position) {
					maxPosition = position;
				}
				if (maxPosition < grossingPosition) {
					maxPosition = grossingPosition;
				}

				// Calculate MIN and MAX prices in the range of ranks
				if (minPrice > price) {
					minPrice = price;
				}
				if (maxPrice < price) {
					maxPrice = price;
				}

				downloads += rank.downloads == null ? 0 : rank.downloads.intValue();

				revenue += rank.revenue == null ? 0 : rank.revenue.floatValue();
			}

			Rank sample = ranks.get(0);
			String symbol = FormattingHelper.getCurrencySymbol(sample.currency);

			overallDownloads = FormattingHelper.getFormattedNumber(downloads);		

			overallRevenue = symbol + " " + FormattingHelper.getFormattedNumber(revenue);
			overallPrice = FormattingHelper.getPriceRange(sample.currency, minPrice, maxPrice);

			if (minPosition == Integer.MAX_VALUE || maxPosition == Integer.MIN_VALUE) {
				overallPosition = UNKNOWN_VALUE;
			} else {
				overallPosition = (minPosition == maxPosition) ? Integer.toString(minPosition) : (Integer.toString(minPosition) + " - " + Integer
						.toString(maxPosition));
			}

		} else {
			overallDownloads = UNKNOWN_VALUE;
			overallRevenue = UNKNOWN_VALUE;
			overallPrice = UNKNOWN_VALUE;
			overallPosition = UNKNOWN_VALUE;
		}
	}
}
