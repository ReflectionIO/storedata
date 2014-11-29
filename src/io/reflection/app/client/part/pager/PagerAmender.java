//
//  PagerAmender.java
//  storedata
//
//  Created by Stefano Capuzzi on 21 Nov 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.part.pager;

import io.reflection.app.api.shared.datatypes.SortDirectionType;
import io.reflection.app.client.controller.NavigationController.Stack;

import java.util.HashMap;

/**
 * @author Stefano Capuzzi
 *
 */
@SuppressWarnings("serial")
public class PagerAmender extends HashMap<String, Object> implements UrlAmenderPager {

	private String pagerName;

	public PagerAmender(String name) {
		setName(name);
		setStart(DEFAULT_START);
		setCount(DEFAULT_COUNT);
		setSortBy(DEFAULT_SORT_BY);
		setSortDirection(DEFAULT_SORT_DIRECTION);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.part.UrlAmender#setName(java.lang.String)
	 */
	@Override
	public void setName(String name) {
		pagerName = name + ":";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.part.UrlAmender#getName()
	 */
	@Override
	public String getName() {
		return pagerName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.part.UrlAmender#setProperty(java.lang.Object)
	 */
	@Override
	public void setProperty(String key, Object value) {
		put(key, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.part.UrlAmender#getProperty()
	 */
	@Override
	public Object getProperty(String key) {
		return get(key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.part.UrlAmender#updateFromString(java.lang.String)
	 */
	@Override
	public void updateFromString(String encoded) {
		String[] splitDecoded;
		if ((splitDecoded = Stack.decode(pagerName, encoded)) != null && splitDecoded.length == 4) {
			setStart(Long.valueOf(splitDecoded[0]));
			setCount(Long.valueOf(splitDecoded[1]));
			setSortBy(splitDecoded[2]);
			setSortDirection(splitDecoded[3].equals(SortDirectionType.SortDirectionTypeAscending) ? SortDirectionType.SortDirectionTypeAscending
					: SortDirectionType.SortDirectionTypeDescending);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.part.UrlAmender#asUrlAmenderString()
	 */
	@Override
	public String asUrlAmenderString() {
		return Stack.encode(pagerName, getStart().toString(), getCount().toString(), getSortBy(), getSortDirection().toString());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.part.UrlAmenderPager#setStart(java.lang.Long)
	 */
	@Override
	public void setStart(Long start) {
		setProperty(START_KEY, start);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.part.UrlAmenderPager#getStart()
	 */
	@Override
	public Long getStart() {
		return (Long) getProperty(START_KEY);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.part.UrlAmenderPager#setCount(java.lang.Long)
	 */
	@Override
	public void setCount(Long count) {
		setProperty(COUNT_KEY, count);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.part.UrlAmenderPager#getCount()
	 */
	@Override
	public Long getCount() {
		return (Long) getProperty(COUNT_KEY);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.part.UrlAmenderPager#setSortBy(java.lang.String)
	 */
	@Override
	public void setSortBy(String sortBy) {
		setProperty(SORT_BY_KEY, sortBy);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.part.UrlAmenderPager#getSortBy()
	 */
	@Override
	public String getSortBy() {
		return (String) getProperty(SORT_BY_KEY);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.part.UrlAmenderPager#setSortDirection(io.reflection.app.api.shared.datatypes.SortDirectionType)
	 */
	@Override
	public void setSortDirection(SortDirectionType sortDirection) {
		setProperty(SORT_DIRECTION_KEY, sortDirection);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.part.UrlAmenderPager#getSortDirection()
	 */
	@Override
	public SortDirectionType getSortDirection() {
		return (SortDirectionType) getProperty(SORT_DIRECTION_KEY);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.part.UrlAmenderPager#setTotalCount(java.lang.Long)
	 */
	@Override
	public void setTotalCount(Long totalCount) {
		setProperty(TOTAL_COUNT_KEY, totalCount);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.part.UrlAmenderPager#getTotalCount()
	 */
	@Override
	public Long getTotalCount() {
		return (Long) getProperty(TOTAL_COUNT_KEY);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.part.UrlAmenderPager#isPagerParameter(java.lang.String)
	 */
	@Override
	public boolean isPagerParameter(String parameter) {
		return parameter != null && parameter.length() > 0 && parameter.startsWith(pagerName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.part.UrlAmenderPager#setNextPage()
	 */
	@Override
	public void setNextPage() {
		setStart(getStart() + getCount());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.part.UrlAmenderPager#setPreviousPage()
	 */
	@Override
	public void setPreviousPage() {
		if (getStart() >= getCount()) {
			setStart(getStart() - getCount());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.part.UrlAmenderPager#setFirstPage()
	 */
	@Override
	public void setFirstPage() {
		setStart(Long.valueOf(0));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.part.UrlAmenderPager#setLastPage()
	 */
	@Override
	public void setLastPage() {
		if (getTotalCount() != null && getTotalCount() > getCount()) {
			Long lastPage = Long.valueOf(getTotalCount().longValue() / getCount().longValue());
			setStart(lastPage);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.part.UrlAmenderPager#setPage(int)
	 */
	@Override
	public void setPage(int pageNumber) {
		setStart(Long.valueOf(pageNumber));
	}

}
