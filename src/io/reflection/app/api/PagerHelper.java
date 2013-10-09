//
//  PagerHelper.java
//  storedata
//
//  Created by William Shakour (billy1380) on 9 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.api;

import io.reflection.app.api.shared.datatypes.Pager;

import java.util.List;

/**
 * @author billy1380
 * 
 */
public class PagerHelper {

	public static void updatePager(Pager pager, List<?> list) {
		updatePager(pager, list, null);

	}

	public static void updatePager(Pager pager, List<?> list, Long total) {
		pager.start = Long.valueOf(pager.start.longValue() + list.size());

		if (total != null) {
			pager.totalCount = total;
		}
	}
}
