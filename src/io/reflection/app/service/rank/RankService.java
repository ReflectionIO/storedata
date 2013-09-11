//  
//  RankService.java
//  storedata
//
//  Created by William Shakour on 18 July 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.service.rank;

import static com.spacehopperstudios.utility.StringUtils.addslashes;
import static com.spacehopperstudios.utility.StringUtils.stripslashes;
import io.reflection.app.api.datatypes.Pager;
import io.reflection.app.api.datatypes.SortDirectionType;
import io.reflection.app.collectors.CollectorFactory;
import io.reflection.app.datatypes.Country;
import io.reflection.app.datatypes.Item;
import io.reflection.app.datatypes.Rank;
import io.reflection.app.datatypes.Store;
import io.reflection.app.repackaged.scphopr.cloudsql.Connection;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseServiceProvider;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseType;
import io.reflection.app.repackaged.scphopr.service.database.IDatabaseService;
import io.reflection.app.service.ServiceType;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

final class RankService implements IRankService {
	public String getName() {
		return ServiceType.ServiceTypeRank.toString();
	}

	@Override
	public Rank getRank(Long id) {
		Rank rank = null;

		IDatabaseService databaseService = DatabaseServiceProvider.provide();
		Connection rankConnection = databaseService.getNamedConnection(DatabaseType.DatabaseTypeRank.toString());

		final String getRankQuery = String.format("SELECT * FROM `rank` WHERE `deleted`='n' AND `id`=%d LIMIT 1", id.longValue());

		try {
			rankConnection.connect();
			rankConnection.executeQuery(getRankQuery);

			if (rankConnection.fetchNextRow()) {
				rank = toRank(rankConnection);
			}
		} finally {
			if (rankConnection != null) {
				rankConnection.disconnect();
			}
		}
		return rank;
	}

	/**
	 * To rank
	 * 
	 * @param connection
	 * @return
	 */
	private Rank toRank(Connection connection) {
		Rank rank = new Rank();

		rank.id = connection.getCurrentRowLong("id");
		rank.created = connection.getCurrentRowDateTime("created");
		rank.deleted = connection.getCurrentRowString("deleted");

		rank.code = stripslashes(connection.getCurrentRowString("code"));
		rank.country = stripslashes(connection.getCurrentRowString("country"));
		rank.currency = stripslashes(connection.getCurrentRowString("currency"));
		rank.date = connection.getCurrentRowDateTime("date");
		rank.grossingPosition = connection.getCurrentRowInteger("grossingposition");
		rank.itemId = stripslashes(connection.getCurrentRowString("itemid"));
		rank.position = connection.getCurrentRowInteger("position");
		rank.price = Float.valueOf((float) connection.getCurrentRowInteger("price").intValue() / 100.0f);
		rank.source = stripslashes(connection.getCurrentRowString("source"));
		rank.type = stripslashes(connection.getCurrentRowString("type"));

		return rank;
	}

	@Override
	public Rank addRank(Rank rank) {
		Rank addedRank = null;

		final String addeRankQuery = String
				.format("INSERT INTO `rank` (`position`,`grossingposition`,`itemid`,`type`,`country`,`date`,`source`,`price`,`currency`,`code`) VALUES (%d,%d,'%s','%s','%s',FROM_UNIXTIME(%d),'%s',%d,'%s','%s')",
						rank.position.longValue(), rank.grossingPosition.longValue(), addslashes(rank.itemId), addslashes(rank.type), addslashes(rank.country),
						rank.date.getTime() / 1000, addslashes(rank.source), (int) (rank.price.floatValue() * 100.0f), addslashes(rank.currency),
						addslashes(rank.code));

		Connection rankConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeRank.toString());

		try {
			rankConnection.connect();
			rankConnection.executeQuery(addeRankQuery);

			if (rankConnection.getAffectedRowCount() > 0) {
				addedRank = getRank(Long.valueOf(rankConnection.getInsertedId()));
				
				if (addedRank == null) {
					addedRank = rank;
					addedRank.id = Long.valueOf(rankConnection.getInsertedId());
				}
			}
		} finally {
			if (rankConnection != null) {
				rankConnection.disconnect();
			}
		}

		return addedRank;
	}

	@Override
	public Rank updateRank(Rank rank) {
		Rank updatedRank = null;

		final String updateRankQuery = String
				.format("UPDATE `rank` SET `position`=%d,`grossingposition`=%d,`itemid`='%s',`type`='%s',`country`='%s',`date`=FROM_UNIXTIME(%d),`source`='%s',`price`=%d,`currency`='%s',`code`='%s' WHERE `id`=%d;",
						rank.position.longValue(), rank.grossingPosition.longValue(), addslashes(rank.itemId), addslashes(rank.type), addslashes(rank.country),
						rank.date.getTime() / 1000, addslashes(rank.source), (int) (rank.price.floatValue() * 100.0f), addslashes(rank.currency),
						addslashes(rank.code), rank.id.longValue());

		Connection rankConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeRank.toString());

		try {
			rankConnection.connect();
			rankConnection.executeQuery(updateRankQuery);

			if (rankConnection.getAffectedRowCount() > 0) {
				updatedRank = getRank(rank.id);
			} else {
				updatedRank = rank;
			}

		} finally {
			if (rankConnection != null) {
				rankConnection.disconnect();
			}
		}

		return updatedRank;
	}

	@Override
	public void deleteRank(Rank rank) {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.rank.IRankService#getItemGatherCodeRank(java.lang.String, java.lang.String)
	 */
	@Override
	public Rank getItemGatherCodeRank(String itemId, String code) {
		Rank rank = null;

		final String getItemGatherCodeRankQuery = String.format("SELECT * FROM `rank` WHERE `itemid`='%s' AND `code`='%s' AND `deleted`='n' LIMIT 1", itemId,
				code);

		Connection rankConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeRank.toString());

		try {
			rankConnection.connect();
			rankConnection.executeQuery(getItemGatherCodeRankQuery);

			if (rankConnection.fetchNextRow()) {
				rank = toRank(rankConnection);
			}
		} finally {
			if (rankConnection != null) {
				rankConnection.disconnect();
			}
		}

		return rank;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.rank.IRankService#getRanks(io.reflection.app.datatypes.Country, io.reflection.app.datatypes.Store, java.lang.String,
	 * java.util.Date, java.util.Date, io.reflection.app.api.datatypes.Pager)
	 */
	@Override
	public List<Rank> getRanks(Country country, Store store, String listType, Date after, Date before, Pager pager) {
		List<Rank> ranks = new ArrayList<Rank>();

		if (CollectorFactory.getCollectorForStore(store.a3Code).isGrossing(listType)) {
			pager.sortBy = "grossingposition";
		} else {
			pager.sortBy = "position";
		}

		String getCountryStoreTypeRanksQuery = String.format(
				"SELECT * FROM `rank` WHERE `type`='%s' AND `country`='%s' AND `source`='%s' AND `date`>=FROM_UNIXTIME(%d) AND `date`<FROM_UNIXTIME(%d) AND `deleted`='n' ORDER BY `%s` %s LIMIT %d,%d",
				addslashes(listType), addslashes(country.a2Code), addslashes(store.a3Code), after.getTime() / 1000, before.getTime() / 1000, pager.sortBy,
				pager.sortDirection == SortDirectionType.SortDirectionTypeAscending ? "asc" : "desc", pager.start, pager.count);

		Connection rankConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeRank.toString());

		try {
			rankConnection.connect();
			rankConnection.executeQuery(getCountryStoreTypeRanksQuery);

			while (rankConnection.fetchNextRow()) {
				Rank rank = toRank(rankConnection);

				if (rank != null) {
					ranks.add(rank);
				}
			}
		} finally {
			if (rankConnection != null) {
				rankConnection.disconnect();
			}
		}

		return ranks;
	}

	/* (non-Javadoc)
	 * @see io.reflection.app.service.rank.IRankService#getItemRanks(io.reflection.app.datatypes.Country, java.lang.String, io.reflection.app.datatypes.Item, java.util.Date, java.util.Date, io.reflection.app.api.datatypes.Pager)
	 */
	@Override
	public List<Rank> getItemRanks(Country country, String listType, Item item, Date after, Date before, Pager pager) {
		List<Rank> ranks = new ArrayList<Rank>();

		String getCountryStoreTypeRanksQuery = String.format(
				"SELECT * FROM `rank` WHERE `type`='%s' AND `country`='%s' AND `itemid`='%s' AND `date`>=FROM_UNIXTIME(%d) AND `date`<FROM_UNIXTIME(%d) AND `deleted`='n' ORDER BY `%s` %s LIMIT %d,%d",
				addslashes(listType), addslashes(country.a2Code), addslashes(item.externalId), after.getTime() / 1000, before.getTime() / 1000, pager.sortBy,
				pager.sortDirection == SortDirectionType.SortDirectionTypeAscending ? "asc" : "desc", pager.start, pager.count);

		Connection rankConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeRank.toString());

		try {
			rankConnection.connect();
			rankConnection.executeQuery(getCountryStoreTypeRanksQuery);

			while (rankConnection.fetchNextRow()) {
				Rank rank = toRank(rankConnection);

				if (rank != null) {
					ranks.add(rank);
				}
			}
		} finally {
			if (rankConnection != null) {
				rankConnection.disconnect();
			}
		}
		
		return ranks;
	}
}