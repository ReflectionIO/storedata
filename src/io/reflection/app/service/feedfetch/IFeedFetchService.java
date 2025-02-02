//
//  IFeedFetchService.java
//  storedata
//
//  Created by William Shakour on September 5, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//

package io.reflection.app.service.feedfetch;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.spacehopperstudios.service.IService;

import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.datatypes.shared.Category;
import io.reflection.app.datatypes.shared.Country;
import io.reflection.app.datatypes.shared.FeedFetch;
import io.reflection.app.datatypes.shared.FeedFetchStatusType;
import io.reflection.app.datatypes.shared.Store;

public interface IFeedFetchService extends IService {
	/**
	 * @param id
	 * @return
	 */
	public FeedFetch getFeedFetch(Long id) throws DataAccessException;

	/**
	 * @param feedFetch
	 * @return
	 */
	public FeedFetch addFeedFetch(FeedFetch feedFetch) throws DataAccessException;

	/**
	 * @param feedFetch
	 * @return
	 */
	public FeedFetch updateFeedFetch(FeedFetch feedFetch) throws DataAccessException;

	/**
	 * @param feedFetch
	 */
	public void deleteFeedFetch(FeedFetch feedFetch) throws DataAccessException;

	/**
	 * Get feed fetches
	 *
	 * @param country
	 * @param store
	 * @param category
	 * @param types
	 * @param pager
	 * @return
	 */
	public List<FeedFetch> getFeedFetches(Country country, Store store, Category category, Collection<String> types, Pager pager) throws DataAccessException;

	/**
	 *
	 * @param country
	 * @param store
	 * @param category
	 * @param types
	 * @return
	 */
	public Long getFeedFetchesCount(Country country, Store store, Category category, Collection<String> types) throws DataAccessException;

	/**
	 *
	 * @param country
	 * @param store
	 * @param listType
	 * @param code
	 * @return
	 */
	public List<Long> getIngestableFeedFetchIds(Country country, Store store, String listType, Long code) throws DataAccessException;

	/**
	 *
	 * @param country
	 * @param store
	 * @param types
	 * @param code
	 * @return
	 */
	public List<FeedFetch> getGatherCodeFeedFetches(Country country, Store store, Collection<String> types, Long code) throws DataAccessException;

	/**
	 * Gets a feed fetch code instead of an guid
	 *
	 * @return
	 * @throws DataAccessException
	 */
	public Long getCode() throws DataAccessException;

	/**
	 * Gets the gather for the feed fetch
	 *
	 * @param country
	 * @param store
	 * @param after
	 * @param before
	 * @return
	 * @throws DataAccessException
	 */
	public Long getGatherCode(Country country, Store store, Date after, Date before) throws DataAccessException;

	/**
	 * Get feed fetches within a date range
	 *
	 * @param country
	 * @param store
	 * @param category
	 * @param types
	 * @param after
	 * @param before
	 * @return
	 * @throws DataAccessException
	 */
	public List<FeedFetch> getDatesFeedFetches(Country country, Store store, Category category, Collection<String> types, Date after, Date before)
			throws DataAccessException;

	/**
	 * @param fetchForDate
	 * @param feedfetchstatustypeingested
	 * @return
	 * @throws DataAccessException
	 */
	public List<Long> getFeedFetchIdsForDateWithStatus(Date fetchForDate, FeedFetchStatusType statusType) throws DataAccessException;

	/**
	 * @param startDate
	 * @param endDate
	 * @return
	 * @throws DataAccessException
	 */
	public List<Long> getFeedFetchIdsBetweenDates(Date startDate, Date endDate) throws DataAccessException;

	/**
	 * @param code
	 * @return
	 * @throws DataAccessException
	 */
	public List<Long> getFeedFetchIdsByCode(Long code) throws DataAccessException;

	/**
	 * @param date
	 * @return
	 * @throws DataAccessException
	 */
	public List<FeedFetch> getDatesFeedFetches(Date date) throws DataAccessException;

}