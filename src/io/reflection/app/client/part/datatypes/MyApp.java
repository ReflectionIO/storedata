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
	private static final String NIL_VALUE = "0";

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
		if (ranks != null || ranks.size() == 0) {
			int downloads = 0;
			float revenue = 0;

			int minPosition = Integer.MAX_VALUE, maxPosition = Integer.MIN_VALUE;
			float minPrice = Float.MAX_VALUE, maxPrice = Float.MIN_VALUE;

			int possition, grossingPossion;
			float price;

			for (Rank rank : ranks) {
				possition = rank.position == null || rank.position.intValue() < 1 ? Integer.MAX_VALUE : rank.position.intValue();
				grossingPossion = rank.grossingPosition == null || rank.grossingPosition.intValue() < 1 ? Integer.MAX_VALUE : rank.grossingPosition.intValue();
				price = rank.price.floatValue();

				if (minPosition > possition) {
					minPosition = possition;
				}

				if (minPosition > grossingPossion) {
					minPosition = grossingPossion;
				}

				if (maxPosition < possition) {
					maxPosition = possition;
				}

				if (maxPosition < grossingPossion) {
					maxPosition = grossingPossion;
				}

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

			overallDownloads = Integer.toString(downloads);
			overallRevenue = symbol + " " + Float.toString(revenue);
			overallPrice = FormattingHelper.getPriceRange(sample.currency, minPrice, maxPrice);

			if (minPosition == Integer.MAX_VALUE || maxPosition == Integer.MIN_VALUE) {
				overallPosition = UNKNOWN_VALUE;
			} else {
				overallPosition = (minPosition == maxPosition) ? Integer.toString(minPosition) : (Integer.toString(minPosition) + " " + Integer
						.toString(maxPosition));
			}

		} else {
			overallDownloads = UNKNOWN_VALUE;
			overallRevenue = NIL_VALUE;
			overallPrice = NIL_VALUE;
			overallPosition = UNKNOWN_VALUE;
		}
	}
}
