//
//  DataAccountCollectorFactory.java
//  storedata
//
//  Created by William Shakour (billy1380) on 2 Jan 2014.
//  Copyright © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyright © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.accountdatacollectors;

/**
 * @author billy1380
 * 
 */
public class DataAccountCollectorFactory {

	/**
	 * @param source
	 * @return
	 */
	public static DataAccountCollector getCollectorForSource(String source) {
		DataAccountCollector collector = null;

		if ("itc".equalsIgnoreCase(source.toLowerCase())) {
			// itunes connect
			collector = new DataAccountCollectorITunesConnect();
		} else if ("azn".equals(source.toLowerCase())) {
			// amazon store
		} else if ("gpl".equals(source.toLowerCase())) {
			// google play store
		}

		return collector;
	}

}
