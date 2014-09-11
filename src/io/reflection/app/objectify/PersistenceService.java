//  
//  PersistenceService.java
//  storedata
//
//  Created by William Shakour on 21 Jun 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.objectify;

import io.reflection.app.archivablekeyvalue.peristence.objectify.ArchivableKeyValue;
import io.reflection.app.datatypes.shared.Item;
import io.reflection.app.datatypes.shared.Rank;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;

/**
 * @author billy1380
 * 
 */
public class PersistenceService {
	static {
		// factory().register(Country.class);
		// factory().register(Store.class);
		// factory().register(FeedFetch.class);
		factory().register(Item.class);
		// factory().register(ItemRankSummary.class);
		factory().register(Rank.class);
		// factory().register(Store.class);
		
		factory().register(ArchivableKeyValue.class);
	}

	public static Objectify ofy() {
		return ObjectifyService.ofy();
	}

	private static ObjectifyFactory factory() {
		return ObjectifyService.factory();
	}
}