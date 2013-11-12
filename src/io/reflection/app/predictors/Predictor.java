//
//  Predictor.java
//  storedata
//
//  Created by William Shakour (billy1380) on 30 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.predictors;


/**
 * @author billy1380
 * 
 */
public interface Predictor {
	void enqueue(String country, String type, String code);

	/**
	 * @param country
	 * @param type
	 * @param code
	 */
	void predictRevenueAndDownloads(String country, String type, String code);

}
