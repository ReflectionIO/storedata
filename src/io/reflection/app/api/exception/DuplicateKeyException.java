//
//  DuplicateKeyException.java
//  storedata
//
//  Created by Stefano Capuzzi on 29 May 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//

package io.reflection.app.api.exception;

/**
 * @author stefanocapuzzi
 * 
 */

@SuppressWarnings("serial")
public class DuplicateKeyException extends DataAccessException {

	/**
	 * @param code
	 * @param message
	 */
	public DuplicateKeyException() {
		super(400001, "A database exception occured - Duplicate key value.");

	}

}
