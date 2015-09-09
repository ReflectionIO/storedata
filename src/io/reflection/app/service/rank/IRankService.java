//
//  IRankService.java
//  storedata
//
//  Created by William Shakour on 18 July 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.service.rank;

import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.datatypes.shared.Category;
import io.reflection.app.datatypes.shared.Country;
import io.reflection.app.datatypes.shared.FormType;
import io.reflection.app.datatypes.shared.Item;
import io.reflection.app.datatypes.shared.Rank;
import io.reflection.app.datatypes.shared.Store;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.spacehopperstudios.service.IService;

public interface IRankService extends IService {
	/**
	 * @param id
	 * @return
	 */
	public Rank getRank(Long id) throws DataAccessException;

	/**
	 * @param rank
	 * @return
	 */
	public Rank updateRank(Rank rank) throws DataAccessException;

	/**
	 * @param country
	 * @param category
	 * @param listType
	 * @param item
	 * @param after
	 * @param before
	 * @param pager
	 * @return
	 */
	public List<Rank> getItemRanks(Country country, Category category, String listType, Item item, Date after, Date before, Pager pager)
			throws DataAccessException, SQLException;

	/**
	 *
	 * @param country
	 * @param store
	 * @param category
	 * @param listType
	 * @param code
	 * @param pager
	 * @param ignoreGrossingRank
	 * @return
	 */
	public List<Rank> getGatherCodeRanks(Country country, Category category, String listType, Long code) throws DataAccessException;

	/**
	 * @param country
	 * @param category
	 * @param type
	 * @param code
	 * @param b
	 * @return
	 * @throws DataAccessException
	 */
	public List<Rank> getGatherCodeRanks(Country country, Category category, String type, Long code, boolean useCache) throws DataAccessException;

	/**
	 * @param item
	 * @return
	 */
	public Boolean getItemHasGrossingRank(Item item) throws DataAccessException;

	/**
	 * @param feedfetchId
	 * @param ranks
	 * @return
	 */
	public Long addRanksBatch(Long feedfetchId, Collection<Rank> ranks) throws DataAccessException;

	/**
	 * @param updateRanks
	 * @return
	 */
	public Long updateRanksBatch(Collection<Rank> updateRanks) throws DataAccessException;

	/**
	 * Get Rank Ids
	 *
	 * @param country
	 * @param store
	 * @param category
	 * @param start
	 * @param end
	 * @return
	 * @throws DataAccessException
	 */
	public List<Long> getRankIds(Country country, Store store, Category category, Date start, Date end) throws DataAccessException;

	public List<Long> getRankIds(Pager pager) throws DataAccessException;

	/**
	 * @param internalId
	 * @param country
	 * @param categoryId
	 * @param form
	 * @param start
	 * @param end
	 * @param pager
	 * @return
	 * @throws DataAccessException
	 */
	public List<Rank> getSaleSummaryAndRankForItemAndFormType(String internalId, Country country, Long categoryId, FormType form, Date start, Date end,
			Pager pager) throws DataAccessException;

	/**
	 * @param id
	 * @param country
	 * @param form
	 * @param start
	 * @param end
	 * @param pager
	 * @return
	 * @throws DataAccessException
	 */
	public List<Rank> getSaleSummaryAndRankForDataAccountAndFormType(Long id, Country country, FormType form, Date start, Date end, Pager pager)
			throws DataAccessException;

	public List<Rank> getRanks(Country country, Category category, String listType, Date onDate) throws DataAccessException;

	public Long getRanksCount(Country country, Category category, String listType, Date onDate) throws DataAccessException;

	public List<Date> getOutOfLeaderboardDates(List<Date> missingDates, Country country, Category category, String listType) throws DataAccessException;

}