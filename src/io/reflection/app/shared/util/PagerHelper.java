//
//  PagerHelper.java
//  storedata
//
//  Created by William Shakour (billy1380) on 9 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.shared.util;

import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.api.shared.datatypes.SortDirectionType;

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

	public static Pager infinitePager() {
		infinitePager.start = Pager.DEFAULT_START;
		infinitePager.count = Long.valueOf(Long.MAX_VALUE);

		return infinitePager;
	}

	public static Pager newDefaultPager() {
		Pager pager = new Pager();

		pager.start = Pager.DEFAULT_START;
		pager.count = Pager.DEFAULT_COUNT;
		pager.sortBy = Pager.DEFAULT_SORT_BY;
		pager.sortDirection = SortDirectionType.SortDirectionTypeDescending;

		return pager;
	}

	public static Pager moveForward(Pager pager) {
		if (pager != null) {
			if (pager.start == null) {
				pager.start = Pager.DEFAULT_START;
			}

			if (pager.count == null) {
				pager.count = Pager.DEFAULT_COUNT;
			}

			pager.start = Long.valueOf(pager.start.longValue() + pager.count.longValue());

			if (pager.totalCount != null && pager.totalCount.longValue() < pager.start.longValue()) {
				pager.start = Long.valueOf(pager.totalCount.longValue() - pager.count.longValue());
			}

			if (pager.start < 0) {
				pager.start = Pager.DEFAULT_START;
			}
		}

		return pager;
	}

	public static Pager moveBackward(Pager pager) {
		if (pager != null) {
			if (pager.start == null) {
				pager.start = Pager.DEFAULT_START;
			}

			if (pager.count == null) {
				pager.count = Pager.DEFAULT_COUNT;
			}

			if (pager.start.longValue() > pager.count.longValue()) {
				pager.start = Long.valueOf(pager.start.longValue() - pager.count.longValue());
			} else {
				pager.start = Pager.DEFAULT_START;
			}
		}

		return pager;
	}
}
