//
//  Model.java
//  storedata
//
//  Created by William Shakour (billy1380) on 14 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.models;

import java.util.Date;

/**
 * @author billy1380
 * 
 */
public interface Model {
	public static final String ENQUEUE_MODEL_FORMAT = "";

	void enqueue(String store, String country, String type, Date after, Date before);

	void calculate();

	void apply();
}
