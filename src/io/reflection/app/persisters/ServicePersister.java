//
//  ServicePersister.java
//  storedata
//
//  Created by William Shakour (billy1380) on 9 Sep 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.persisters;

import io.reflection.app.collectors.CollectorFactory;
import io.reflection.app.logging.GaeLevel;
import io.reflection.app.service.item.IItemService;
import io.reflection.app.service.item.ItemServiceProvider;
import io.reflection.app.service.rank.RankServiceProvider;
import io.reflection.app.shared.datatypes.Item;
import io.reflection.app.shared.datatypes.Rank;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

/**
 * @author billy1380
 * 
 */
public class ServicePersister extends PersisterBase {

	private static final Logger LOG = Logger.getLogger(ServicePersister.class.getName());

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.persisters.PersisterBase#perisist(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected Rank getRankWithParameters(String store, String country, String itemId, String type, Date date, String code) {
		List<String> types = new ArrayList<String>();
		types.addAll(CollectorFactory.getCollectorForStore(store).getCounterpartTypes(type));
		types.add(type);
		return RankServiceProvider.provide().getItemGatherCodeRank(itemId, code, store, country, types);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.persisters.PersisterBase#getRankWithParameters(java.lang.String, java.lang.String)
	 */

	@Override
	public void perisist(HttpServletRequest req) {
		if (LOG.isLoggable(GaeLevel.TRACE)) {
			LOG.log(GaeLevel.TRACE, "Entering...");
		}

		Item item = convertParamsToItem(req);
		Rank rank = convertParamsToRank(req);

		IItemService itemService = ItemServiceProvider.provide();
		if (itemService.getExternalIdItem(item.externalId) == null) {
			itemService.addItem(item);
		}

		if (rank.id == null) {
			RankServiceProvider.provide().addRank(rank);
		} else {
			RankServiceProvider.provide().updateRank(rank);
		}

		if (LOG.isLoggable(GaeLevel.TRACE)) {
			LOG.log(GaeLevel.TRACE, String.format("Saved rank [%s] for", rank.itemId));
		}

		if (LOG.isLoggable(GaeLevel.TRACE)) {
			LOG.log(GaeLevel.TRACE, "Exiting...");
		}
	}

}
