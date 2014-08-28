//
//  ItemRankArchiver.java
//  storedata
//
//  Created by William Shakour (billy1380) on 27 Aug 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.itemrankarchivers;

import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.datatypes.shared.Rank;

/**
 * @author billy1380
 * 
 */
public interface ItemRankArchiver {
	/**
	 * 
	 * @param id
	 */
	void enqueue(Long id);

	/**
	 * 
	 * @param id
	 * @throws DataAccessException
	 */
	void archiveRank(Long id) throws DataAccessException;

	/**
	 * 
	 * @param rank
	 * @throws DataAccessException
	 */
	void archive(Rank rank) throws DataAccessException;

}
