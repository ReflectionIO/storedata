//
//  DataStorePersister.java
//  storedata
//
//  Created by William Shakour (billy1380) on 9 Sep 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.persisters;

import static io.reflection.app.objectify.PersistenceService.ofy;
import io.reflection.app.collectors.CollectorFactory;
import io.reflection.app.logging.GaeLevel;
import io.reflection.app.shared.datatypes.Item;
import io.reflection.app.shared.datatypes.Rank;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

/**
 * @author billy1380
 * 
 */
public class DataStorePersister extends PersisterBase {

	private static final Logger LOG = Logger.getLogger(DataStorePersister.class.getName());



	/* (non-Javadoc)
	 * @see io.reflection.app.persisters.PersisterBase#getRankWithParameters(java.lang.String, java.lang.String)
	 */
	@Override
	protected Rank getRankWithParameters(String store, String country, String itemId, String type, Date date, String code) {
		Rank rank = null;

		List<String> counterpartType = CollectorFactory.getCollectorForStore(store).getCounterpartTypes(type);

		Date startDate, endDate;

		Calendar c = Calendar.getInstance();

		c.setTime(date);
		c.add(Calendar.HOUR, -2);
		startDate = c.getTime();

		c.setTime(date);
		c.add(Calendar.HOUR, 2);
		endDate = c.getTime();

		List<Rank> foundRanks = ofy().cache(false).load().type(Rank.class).filter("source =", store).filter("country =", country).filter("itemId =", itemId)
				.filter("date >=", startDate).filter("date <", endDate).filter("type in", counterpartType).list();

		if (foundRanks != null) {
			for (Rank found : foundRanks) {
				// if one of the items of the counter types within the four hour window matches the gather code then that is the right row quit

				if (code.equals(found.code)) {
					rank = found;
					break;
				}
			}
		}

		return rank;
	}





	/* (non-Javadoc)
	 * @see io.reflection.app.persisters.PersisterBase#perisist(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	public void perisist(HttpServletRequest req) {
		if (LOG.isLoggable(GaeLevel.TRACE)) {
			LOG.log(GaeLevel.TRACE, "Entering...");
		}

		Item item = convertParamsToItem(req);
		Rank rank = convertParamsToRank(req);

		if (ofy().load().type(Item.class).filter("externalId =", item.externalId).count() == 0) {
			ofy().save().entity(item).now();
		}

		ofy().save().entity(rank).now();

		if (LOG.isLoggable(GaeLevel.TRACE)) {
			LOG.log(GaeLevel.TRACE, String.format("Saved rank [%s] for", rank.itemId));
		}

		if (LOG.isLoggable(GaeLevel.TRACE)) {
			LOG.log(GaeLevel.TRACE, "Exiting...");
		}
		
	}

}
