//
//  Predictor.java
//  storedata
//
//  Created by William Shakour (billy1380) on 30 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.predictors;

import io.reflection.app.shared.datatypes.ModelRun;
import io.reflection.app.shared.datatypes.Rank;

/**
 * @author billy1380
 * 
 */
public interface Predictor {
	void enqueue(ModelRun modelRun, Rank rank);

	void predictRevenueAndDownloads(ModelRun modelRun, Rank rank);

}
