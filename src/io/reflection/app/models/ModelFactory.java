//
//  ModelFactory.java
//  storedata
//
//  Created by William Shakour (billy1380) on 15 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.models;

/**
 * @author billy1380
 * 
 */
public class ModelFactory {

	/**
	 * @param store
	 * @return
	 */
	public static Model getModelForStore(String store) {
		Model model = null;

		if ("ios".equals(store.toLowerCase())) {
			// ios store
			model = new ModelIOS();
		} else if ("azn".equals(store.toLowerCase())) {
			// amazon store
		} else if ("gpl".equals(store.toLowerCase())) {
			// google play store
		}

		return model;
	}

}
