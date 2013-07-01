//  
//  PersistenceService.java
//  storedata
//
//  Created by William Shakour on 21 Jun 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package com.spacehopperstudios.storedata.objectify;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import com.spacehopperstudios.storedata.datatypes.Country;
import com.spacehopperstudios.storedata.datatypes.FeedFetch;
import com.spacehopperstudios.storedata.datatypes.Item;
import com.spacehopperstudios.storedata.datatypes.ItemRankSummary;
import com.spacehopperstudios.storedata.datatypes.Rank;
import com.spacehopperstudios.storedata.datatypes.Store;

/**
 * @author billy1380
 * 
 */
public class PersistenceService {
	static {
		factory().register(Country.class);
		factory().register(Store.class);
		factory().register(FeedFetch.class);
		factory().register(Item.class);
		factory().register(Rank.class);
		factory().register(ItemRankSummary.class);
	}

	public static Objectify ofy() {
		return ObjectifyService.ofy();
	}

	public static ObjectifyFactory factory() {
		return ObjectifyService.factory();
	}
}