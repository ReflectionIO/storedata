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
import io.reflection.app.shared.datatypes.Country;
import io.reflection.app.shared.datatypes.Item;
import io.reflection.app.shared.datatypes.Rank;
import io.reflection.app.shared.datatypes.Store;

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
	public Rank getItemGatherCodeRank(String itemId, String code, String store, String country, List<String> possibleTypes) throws DataAccessException;

	/**
	 * @param country
	 * @param store
	 * @param listType
	 * @param after
	 * @param before
	 * @param pager
	 * @return
	 */
	public List<Rank> getRanks(Country country, Store store, String listType, Date after, Date before, Pager pager) throws DataAccessException;

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
	 * @param listType
	 * @param after
	 * @param before
	 * @return
	 */
	public Long getRanksCount(Country country, Store store, String listType, Date after, Date before) throws DataAccessException;

	/**
	 * @param item
	 * @return
	 */
	public Boolean getItemHasGrossingRank(Item item) throws DataAccessException;

	/**
	 * @param ranks
	 * @return
	 */
	public Long addRanksBatch(List<Rank> ranks) throws DataAccessException;

	/**
	 * 
	 * @param country
	 * @param store
	 * @param listType
	 * @param code
	 * @param pager
	 * @param ignoreGrossingRank
	 * @return
	 */
	public List<Rank> getGatherCodeRanks(Country country, Store store, String listType, String code, Pager pager, boolean ignoreGrossingRank)
			throws DataAccessException;

	/**
	 * 
	 * @param country
	 * @param store
	 * @param listType
	 * @param code
	 * @return
	 */
	public Long getGatherCodeRanksCount(Country country, Store store, String listType, String code) throws DataAccessException;

	/**
	 * @param code
	 * @return
	 */
	public Date getCodeLastRankDate(String code) throws DataAccessException;

	/**
	 * @param updateRanks
	 * @return
	 */
	public Long updateRanksBatch(List<Rank> updateRanks) throws DataAccessException;

}