//
//  AppRanking.java
//  storedata
//
//  Created by Stefano Capuzzi (capuzzistefano) on 7 Apr 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.part.datatypes;

import java.util.Comparator;
import java.util.Date;

/**
 * @author Stefano Capuzzi (capuzzistefano)
 *
 */
public class AppRevenue {
	public Date date;
	public String currency;
	public Float revenue;
	public Float total;
	public Float revenuePercentForPeriod;

	public static Comparator<AppRevenue> getDateComparator() {
		return new Comparator<AppRevenue>() {

			@Override
			public int compare(AppRevenue o1, AppRevenue o2) {
				return o2.date.compareTo(o1.date);
			}
		};
	}

	public static Comparator<AppRevenue> getRevenueComparator() {
		return new Comparator<AppRevenue>() {

			@Override
			public int compare(AppRevenue o1, AppRevenue o2) {
				return o2.revenue.compareTo(o1.revenue);
			}
		};
	}

	public static Comparator<AppRevenue> getRevenuePercentForPeriodComparator() {
		return new Comparator<AppRevenue>() {

			@Override
			public int compare(AppRevenue o1, AppRevenue o2) {
				return o2.revenuePercentForPeriod.compareTo(o1.revenuePercentForPeriod);
			}
		};
	}
}
