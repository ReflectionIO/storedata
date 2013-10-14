//
//  ModelIOS.java
//  storedata
//
//  Created by William Shakour (billy1380) on 14 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.models;

import io.reflection.app.renjin.RenjinRModelBase;

import java.util.Date;

/**
 * @author billy1380
 * 
 */
public class ModelIOS extends RenjinRModelBase implements Model {

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.models.Model#enqueue(java.util.List)
	 */
	@Override
	public void enqueue(String store, String country, String type, Date before, Date after) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.models.Model#calculateShapeAndSize()
	 */
	@Override
	public void calculate() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.models.Model#applyToRanks()
	 */
	@Override
	public void apply() {

	}

}
