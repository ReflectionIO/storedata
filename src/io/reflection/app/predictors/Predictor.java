//
//  Predictor.java
//  storedata
//
//  Created by William Shakour (billy1380) on 30 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.predictors;

import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.datatypes.shared.ModelRun;
import io.reflection.app.datatypes.shared.SimpleModelRun;

/**
 * @author billy1380
 * 
 */
public interface Predictor {
	void enqueue(String country, String type, Long code);
	
	void enqueue(SimpleModelRun simpleModelRun);
	
	void enqueue(ModelRun modelRun);

	/**
	 * Predict revenue and downloads
	 * 
	 * @param country
	 * @param type
	 * @param code
	 * @param categorId
	 * @throws DataAccessException
	 */
	void predictRevenueAndDownloads(String country, String type, Long code, Long categorId) throws DataAccessException;

}
