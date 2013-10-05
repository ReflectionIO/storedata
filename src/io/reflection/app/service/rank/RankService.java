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
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.api.shared.datatypes.SortDirectionType;
import io.reflection.app.collectors.Collector;
import io.reflection.app.collectors.CollectorFactory;
import io.reflection.app.repackaged.scphopr.cloudsql.Connection;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseServiceProvider;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseType;
import io.reflection.app.repackaged.scphopr.service.database.IDatabaseService;
import io.reflection.app.service.ServiceType;
import io.reflection.app.shared.datatypes.Country;
import io.reflection.app.shared.datatypes.Item;
import io.reflection.app.shared.datatypes.Rank;
import io.reflection.app.shared.datatypes.Store;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.spacehopperstudios.utility.StringUtils;

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
	public Rank getItemGatherCodeRank(String itemId, String code, String store, String country, List<String> possibleTypes) {
		Rank rank = null;

		String typesQueryPart = null;
		if (possibleTypes.size() == 1) {
			typesQueryPart = String.format("`type`='%s'", possibleTypes.get(0));
		} else {
			typesQueryPart = "`type` IN ('" + StringUtils.join(possibleTypes, "','") + "')";
		}

		final String getItemGatherCodeRankQuery = String.format(
				"SELECT * FROM `rank` WHERE `itemid`='%s' AND `code`='%s' AND `source`='%s' AND `country`='%s' AND %s AND `deleted`='n' LIMIT 1", itemId, code,
				store, country, typesQueryPart);

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

		String code = getGatherCode(country, store, after, before);

		if (code != null) {
			ranks.addAll(getGatherCodeRanks(country, store, listType, code, pager));
		}

		return ranks;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.rank.IRankService#getItemRanks(io.reflection.app.datatypes.Country, java.lang.String, io.reflection.app.datatypes.Item,
	 * java.util.Date, java.util.Date, io.reflection.app.api.datatypes.Pager)
	 */
	@Override
	public List<Rank> getItemRanks(Country country, Store store, String listType, Item item, Date after, Date before, Pager pager) {
		List<Rank> ranks = new ArrayList<Rank>();

		Collector collector = CollectorFactory.getCollectorForStore(store.a3Code);
		boolean isGrossing = collector.isGrossing(listType);

		List<String> types = new ArrayList<String>();

		if (isGrossing) {
			types.addAll(collector.getCounterpartTypes(listType));
		}

		types.add(addslashes(listType));

		String typesQueryPart = null;
		if (types.size() == 1) {
			typesQueryPart = String.format("`type`='%s'", types.get(0));
		} else {
			typesQueryPart = "`type` IN ('" + StringUtils.join(types, "','") + "')";
		}

		if (isGrossing) {
			pager.sortBy = "grossingposition";
		} else {
			pager.sortBy = "position";
		}

		String getCountryStoreTypeRanksQuery = String
				.format("SELECT * FROM `rank` WHERE %s AND `country`='%s' AND `source`='%s' AND `itemid`='%s' AND %s %s `deleted`='n' ORDER BY `date` ASC, `%s` %s LIMIT %d,%d",
						typesQueryPart, addslashes(country.a2Code), addslashes(store.a3Code), addslashes(item.externalId), beforeAfterQuery(before, after),
						isGrossing ? "`grossingposition`<>0 AND" : "", pager.sortBy,
						pager.sortDirection == SortDirectionType.SortDirectionTypeAscending ? "ASC" : "DESC", pager.start, pager.count);

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

	private String getGatherCode(Country country, Store store, Date after, Date before) {
		String code = null;

		Connection rankConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeRank.toString());

		try {

			rankConnection.connect();
			String getGatherCode = String.format(
					"SELECT `code` FROM `rank` WHERE `country`='%s' AND `source`='%s' AND %s `deleted`='n' ORDER BY `date` DESC LIMIT 1",
					addslashes(country.a2Code), addslashes(store.a3Code), beforeAfterQuery(before, after));

			rankConnection.executeQuery(getGatherCode);

			if (rankConnection.fetchNextRow()) {
				code = rankConnection.getCurrentRowString("code");
			}
		} finally {
			if (rankConnection != null) {
				rankConnection.disconnect();
			}
		}

		return code;
	}

	/**
	 * @param before
	 * @param after
	 * @return
	 */
	private String beforeAfterQuery(Date before, Date after) {
		StringBuffer buffer = new StringBuffer();

		if (after != null) {
			buffer.append("`date`>=FROM_UNIXTIME(");
			buffer.append(after.getTime() / 1000);
			buffer.append(") AND ");
		}

		if (before != null) {
			buffer.append("`date`<FROM_UNIXTIME(");
			buffer.append(before.getTime() / 1000);
			buffer.append(") AND ");
		}

		return buffer.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.rank.IRankService#getRanksCount(java.lang.String, java.lang.String, java.lang.String, java.util.Date, java.util.Date)
	 */
	@Override
	public Long getRanksCount(Country country, Store store, String listType, Date after, Date before) {
		Long ranksCount = Long.valueOf(0);

		String code = getGatherCode(country, store, after, before);

		if (code != null) {
			ranksCount = getGatherCodeRanksCount(country, store, listType, code);
		}

		return ranksCount;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.rank.IRankService#getItemHasGrossingRank(io.reflection.app.datatypes.Item)
	 */
	@Override
	public Boolean getItemHasGrossingRank(Item item) {
		Boolean hasGrossingRank = Boolean.FALSE;

		String getItemHasGrossingRankQuery = String.format("SELECT `id` FROM `rank` WHERE `itemid`='%s' AND `grossingposition`<>0 LIMIT 1",
				addslashes(item.externalId));

		Connection rankConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeRank.toString());

		try {
			rankConnection.connect();
			rankConnection.executeQuery(getItemHasGrossingRankQuery);

			if (rankConnection.fetchNextRow()) {
				hasGrossingRank = Boolean.TRUE;
			}
		} finally {
			if (rankConnection != null) {
				rankConnection.disconnect();
			}
		}

		return hasGrossingRank;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.rank.IRankService#addRanksBatch(java.util.List)
	 */
	@Override
	public Long addRanksBatch(List<Rank> ranks) {
		Long addedRankCount = Long.valueOf(0);

		StringBuffer addRanksBatchQuery = new StringBuffer();

		addRanksBatchQuery
				.append("INSERT INTO `rank` (`position`,`grossingposition`,`itemid`,`type`,`country`,`date`,`source`,`price`,`currency`,`code`) VALUES ");

		boolean addComma = false;
		for (Rank rank : ranks) {
			if (addComma) {
				addRanksBatchQuery.append(",");
			}

			addRanksBatchQuery.append(String.format("(%d,%d,'%s','%s','%s',FROM_UNIXTIME(%d),'%s',%d,'%s','%s')", rank.position.longValue(),
					rank.grossingPosition.longValue(), addslashes(rank.itemId), addslashes(rank.type), addslashes(rank.country), rank.date.getTime() / 1000,
					addslashes(rank.source), (int) (rank.price.floatValue() * 100.0f), addslashes(rank.currency), addslashes(rank.code)));
			addComma = true;
		}

		Connection rankConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeRank.toString());

		try {
			rankConnection.connect();
			rankConnection.executeQuery(addRanksBatchQuery.toString());

			if (rankConnection.getAffectedRowCount() > 0) {
				addedRankCount = Long.valueOf(rankConnection.getAffectedRowCount());
			}
		} finally {
			if (rankConnection != null) {
				rankConnection.disconnect();
			}
		}

		return addedRankCount;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.rank.IRankService#getGatherCodeRanks(io.reflection.app.shared.datatypes.Country, io.reflection.app.shared.datatypes.Store,
	 * java.lang.String, java.lang.String, io.reflection.app.api.shared.datatypes.Pager)
	 */
	@Override
	public List<Rank> getGatherCodeRanks(Country country, Store store, String listType, String code, Pager pager) {
		List<Rank> ranks = new ArrayList<Rank>();

		Connection rankConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeRank.toString());

		Collector collector = CollectorFactory.getCollectorForStore(store.a3Code);
		boolean isGrossing = collector.isGrossing(listType);

		List<String> types = new ArrayList<String>();

		if (isGrossing) {
			types.addAll(collector.getCounterpartTypes(listType));
		}

		types.add(addslashes(listType));

		String typesQueryPart = null;
		if (types.size() == 1) {
			typesQueryPart = String.format("`type`='%s'", types.get(0));
		} else {
			typesQueryPart = "`type` IN ('" + StringUtils.join(types, "','") + "')";
		}

		if (isGrossing) {
			pager.sortBy = "grossingposition";
		} else {
			pager.sortBy = "position";
		}

		String getCountryStoreTypeRanksQuery = String
				.format("SELECT * FROM `rank` WHERE %s AND `country`='%s' AND `source`='%s' AND `code`='%s' AND %s `deleted`='n' ORDER BY `%s` %s,`date` DESC LIMIT %d,%d",
						typesQueryPart, addslashes(country.a2Code), addslashes(store.a3Code), code, isGrossing ? "`grossingposition`<>0 AND" : "",
						pager.sortBy, pager.sortDirection == SortDirectionType.SortDirectionTypeAscending ? "ASC" : "DESC", pager.start, pager.count);

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.rank.IRankService#getGatherCodeRanksCount(io.reflection.app.shared.datatypes.Country,
	 * io.reflection.app.shared.datatypes.Store, java.lang.String, java.lang.String, io.reflection.app.api.shared.datatypes.Pager)
	 */
	@Override
	public Long getGatherCodeRanksCount(Country country, Store store, String listType, String code) {
		Long ranksCount = Long.valueOf(0);

		Collector collector = CollectorFactory.getCollectorForStore(store.a3Code);
		boolean isGrossing = collector.isGrossing(listType);

		List<String> types = new ArrayList<String>();

		if (isGrossing) {
			types.addAll(collector.getCounterpartTypes(listType));
		}

		types.add(addslashes(listType));

		String typesQueryPart = null;
		if (types.size() == 1) {
			typesQueryPart = String.format("`type`='%s'", types.get(0));
		} else {
			typesQueryPart = "`type` IN ('" + StringUtils.join(types, "','") + "')";
		}

		String getRanksCountQuery = String.format(
				"SELECT COUNT(1) AS `count` FROM `rank` WHERE %s AND `country`='%s' AND `source`='%s' AND `code`='%s' AND %s `deleted`='n'", typesQueryPart,
				addslashes(country.a2Code), addslashes(store.a3Code), code, isGrossing ? "`grossingposition`<>0 AND" : "");

		Connection rankConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeRank.toString());

		try {
			rankConnection.connect();
			rankConnection.executeQuery(getRanksCountQuery);

			if (rankConnection.fetchNextRow()) {
				ranksCount = rankConnection.getCurrentRowLong("count");
			}

		} finally {
			if (rankConnection != null) {
				rankConnection.disconnect();
			}
		}

		return ranksCount;
	}

}