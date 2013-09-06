//  
//  IRankService.java
//  storedata
//
//  Created by William Shakour on 18 July 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.service.rank;

import io.reflection.app.datatypes.Rank;

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

}