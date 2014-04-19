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
import io.reflection.app.datatypes.shared.Item;
import io.reflection.app.datatypes.shared.Rank;
import io.reflection.app.datatypes.shared.Store;

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
	public Rank addRank(Rank rank) throws DataAccessException;

	/**
	 * @param rank
	 * @return
	 */
	public Rank updateRank(Rank rank) throws DataAccessException;

	/**
	 * @param rank
	 */
	public void deleteRank(Rank rank) throws DataAccessException;

	/**
	 * @param itemId
	 * @param code
	 * @param type
	 * @param country
	 * @param store
	 * @return
	 */
	public Rank getItemGatherCodeRank(String itemId, Long code, String store, String country, Collection<String> possibleTypes) throws DataAccessException;

	/**
	 * @param country
	 * @param store
	 * @param category
	 * @param listType
	 * @param after
	 * @param before
	 * @param pager
	 * @return
	 */
	public List<Rank> getRanks(Country country, Store store, Category category, String listType, Date after, Date before, Pager pager)
			throws DataAccessException;

	/**
	 * @param country
	 * @param store
	 * @param listType
	 * @param item
	 * @param after
	 * @param before
	 * @param pager
	 * @return
	 */
	public List<Rank> getItemRanks(Country country, Store store, String listType, Item item, Date after, Date before, Pager pager) throws DataAccessException;

	/**
	 * @param country
	 * @param store
	 * @param category
	 * @param listType
	 * @param after
	 * @param before
	 * @return
	 */
	public Long getRanksCount(Country country, Store store, Category category, String listType, Date after, Date before) throws DataAccessException;

	/**
	 * @param item
	 * @return
	 */
	public Boolean getItemHasGrossingRank(Item item) throws DataAccessException;

	/**
	 * @param ranks
	 * @return
	 */
	public Long addRanksBatch(Collection<Rank> ranks) throws DataAccessException;

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
	public List<Rank> getGatherCodeRanks(Country country, Store store, Category category, String listType, Long code, Pager pager, boolean ignoreGrossingRank)
			throws DataAccessException;

	/**
	 * 
	 * @param country
	 * @param store
	 * @param category
	 * @param listType
	 * @param code
	 * @return
	 */
	public Long getGatherCodeRanksCount(Country country, Store store, Category category, String listType, Long code) throws DataAccessException;

	/**
	 * @param code
	 * @return
	 */
	public Date getCodeLastRankDate(Long code) throws DataAccessException;

	/**
	 * @param updateRanks
	 * @return
	 */
	public Long updateRanksBatch(Collection<Rank> updateRanks) throws DataAccessException;

	/**
	 * 
	 * @param country
	 * @param store
	 * @param category
	 * @param listType
	 * @param start
	 * @param end
	 * @return
	 * @throws DataAccessException
	 */
	public List<Rank> getAllRanks(Country country, Store store, Category category, String listType, Date start, Date end) throws DataAccessException;

}