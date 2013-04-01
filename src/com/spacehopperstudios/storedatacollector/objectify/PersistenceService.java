/**
 * 
 */
package com.spacehopperstudios.storedatacollector.objectify;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import com.spacehopperstudios.storedatacollector.datatypes.Item;
import com.spacehopperstudios.storedatacollector.datatypes.Rank;

/**
 * @author billy1380
 * 
 */
public class PersistenceService {
	static {
		ObjectifyService.factory().register(Item.class);
		ObjectifyService.factory().register(Rank.class);
	}

	public static Objectify ofy() {
		return ObjectifyService.ofy();
	}

	public static ObjectifyFactory factory() {
		return ObjectifyService.factory();
	}
}