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

	private static final Pager infinitePager = new Pager();

	public static void updatePager(Pager pager, List<?> list) {
		updatePager(pager, list, null);

	}

	public static void updatePager(Pager pager, List<?> list, Long total) {
		if (list != null) {
			pager.start = Long.valueOf(pager.start.longValue() + (list.size()));
		} else {
			// list is null so do nothing
		}

		if (total != null) {
			pager.totalCount = total;
		}
	}
	
	public static Pager infinitePager () {
		infinitePager.start = Long.valueOf(0);
		infinitePager.count = Long.valueOf(Long.MAX_VALUE);
		
		return infinitePager;
	}
}
