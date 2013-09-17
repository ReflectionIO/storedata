//
//  LookupDetailTypeHelper.java
//  storedata
//
//  Created by William Shakour (billy1380) on 17 Sep 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.api.lookup.helpers;

import io.reflection.app.api.lookup.datatypes.LookupDetailType;

/**
 * @author billy1380
 * 
 */
public class LookupDetailTypeHelper {

	/**
	 * @param detail
	 * @return
	 */
	public static boolean isShort(LookupDetailType detail) {
		return detail == LookupDetailType.LookupDetailTypeDetailed || detail == LookupDetailType.LookupDetailTypeMedium
				|| detail == LookupDetailType.LookupDetailTypeShort;
	}

	/**
	 * @param detail
	 * @return
	 */
	public static boolean isMedium(LookupDetailType detail) {
		return detail == LookupDetailType.LookupDetailTypeDetailed || detail == LookupDetailType.LookupDetailTypeMedium;
	}

	/**
	 * @param detail
	 * @return
	 */
	public static boolean isDetailed(LookupDetailType detail) {
		return detail == LookupDetailType.LookupDetailTypeDetailed;
	}

}
