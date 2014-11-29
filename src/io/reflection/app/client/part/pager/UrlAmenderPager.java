//
//  UrlAmenderPager.java
//  storedata
//
//  Created by Stefano Capuzzi on 24 Nov 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.part.pager;

import io.reflection.app.api.shared.datatypes.SortDirectionType;
import io.reflection.app.client.part.UrlAmender;

/**
 * @author Stefano Capuzzi
 *
 */
public interface UrlAmenderPager extends UrlAmender {

	public static final String START_KEY = "start";
	public static final String COUNT_KEY = "count";
	public static final String SORT_BY_KEY = "sortby";
	public static final String SORT_DIRECTION_KEY = "sortdirection";
	public static final String TOTAL_COUNT_KEY = "totalcount";

	public static final Long DEFAULT_START = Long.valueOf(0);
	public static final Long DEFAULT_COUNT = Long.valueOf(25);
	public static final String DEFAULT_SORT_BY = "id";
	public static final SortDirectionType DEFAULT_SORT_DIRECTION = SortDirectionType.SortDirectionTypeAscending;

	void setStart(Long start);

	Long getStart();

	void setCount(Long count);

	Long getCount();

	void setSortBy(String sortBy);

	String getSortBy();

	void setSortDirection(SortDirectionType sortDirection);

	SortDirectionType getSortDirection();

	void setTotalCount(Long totalCount);

	Long getTotalCount();

	boolean isPagerParameter(String parameter);

	void setNextPage();

	void setPreviousPage();

	void setFirstPage();

	void setLastPage();

	void setPage(int pageNumber);

}
