//
//  ItemRankArchiver.java
//  storedata
//
//  Created by William Shakour (billy1380) on 27 Aug 2014.
//  Copyright © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.itemrankarchivers;

import io.reflection.app.api.exception.DataAccessException;

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
	void archive(Long id) throws DataAccessException;

}
