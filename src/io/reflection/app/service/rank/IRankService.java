//  
//  IRankService.java
//  storedata
//
//  Created by William Shakour on 18 July 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.service.rank;

import java.util.Date;
import java.util.List;

import io.reflection.app.api.datatypes.Pager;
import io.reflection.app.datatypes.Country;
import io.reflection.app.datatypes.Rank;
import io.reflection.app.datatypes.Store;

import com.spacehopperstudios.service.IService;

public interface IRankService extends IService {
	/**
	 * @param id
	 * @return
	 */
	public Rank getRank(Long id);

	/**
	 * @param rank
	 * @return
	 */
	public Rank addRank(Rank rank);

	/**
	 * @param rank
	 * @return
	 */
	public Rank updateRank(Rank rank);

	/**
	 * @param rank
	 */
	public void deleteRank(Rank rank);

	/**
	 * @param itemId
	 * @param code
	 * @return
	 */
	public Rank getItemGatherCodeRank(String itemId, String code);

	/**
	 * @param country
	 * @param store
	 * @param listType
	 * @param after
	 * @param before
	 * @param pager
	 * @return
	 */
	public List<Rank> getRanks(Country country, Store store, String listType, Date after, Date before, Pager pager);

}