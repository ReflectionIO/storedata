//
//  PredictorFactory.java
//  storedata
//
//  Created by William Shakour (billy1380) on 30 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.predictors;

import io.reflection.app.shared.util.DataTypeHelper;


/**
 * @author billy1380
 *
 */
public class PredictorFactory {

		/**
		 * @param store
		 * @return
		 */
		public static Predictor getPredictorForStore(String store) {
			Predictor predictor = null;

			if (DataTypeHelper.IOS_STORE_A3.equals(store.toLowerCase())) {
				// ios store
				predictor = new SimplePredictorIOS();
			} else if ("azn".equals(store.toLowerCase())) {
				// amazon store
			} else if ("gpl".equals(store.toLowerCase())) {
				// google play store
			}

			return predictor;
		}

	}
