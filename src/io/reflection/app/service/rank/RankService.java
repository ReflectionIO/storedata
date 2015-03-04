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
import static io.reflection.app.helpers.SqlQueryHelper.beforeAfterQuery;
import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.api.shared.datatypes.SortDirectionType;
import io.reflection.app.collectors.Collector;
import io.reflection.app.collectors.CollectorFactory;
import io.reflection.app.datatypes.shared.Category;
import io.reflection.app.datatypes.shared.Country;
import io.reflection.app.datatypes.shared.Item;
import io.reflection.app.datatypes.shared.Rank;
import io.reflection.app.datatypes.shared.Store;
import io.reflection.app.helpers.ApiHelper;
import io.reflection.app.repackaged.scphopr.cloudsql.Connection;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseServiceProvider;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseType;
import io.reflection.app.repackaged.scphopr.service.database.IDatabaseService;
import io.reflection.app.service.ServiceType;
import io.reflection.app.service.feedfetch.FeedFetchServiceProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import co.spchopr.persistentmap.PersistentMap;
import co.spchopr.persistentmap.PersistentMapFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.spacehopperstudios.utility.JsonUtils;
import com.spacehopperstudios.utility.StringUtils;

final class RankService implements IRankService {

	private PersistentMap cache = PersistentMapFactory.createObjectify();

	public String getName() {
		return ServiceType.ServiceTypeRank.toString();
	}

	@Override
	public Rank getRank(Long id) throws DataAccessException {
		Rank rank = null;

		String memcacheKey = getName() + ".id." + id;
		String jsonString = (String) cache.get(memcacheKey);

		if (jsonString == null) {
			IDatabaseService databaseService = DatabaseServiceProvider.provide();
			Connection rankConnection = databaseService.getNamedConnection(DatabaseType.DatabaseTypeRank.toString());

			final String getRankQuery = String.format("SELECT * FROM `rank` WHERE `deleted`='n' AND `id`=%d LIMIT 1", id.longValue());

			try {
				rankConnection.connect();
				rankConnection.executeQuery(getRankQuery);

				if (rankConnection.fetchNextRow()) {
					rank = toRank(rankConnection);

					// if (rank != null) {
					// cal.setTime(new Date());
					// cal.add(Calendar.DAY_OF_MONTH, 20);
					// cache.put(memcacheKey, rank.toString(), cal.getTime());
					// }
				}
			} finally {
				if (rankConnection != null) {
					rankConnection.disconnect();
				}
			}
		} else {
			rank = new Rank();
			rank.fromJson(jsonString);
		}

		return rank;
	}

	/**
	 * To rank
	 * 
	 * @param connection
	 * @return
	 * @throws DataAccessException
	 */
	private Rank toRank(Connection connection) throws DataAccessException {
		Rank rank = new Rank();

		rank.id = connection.getCurrentRowLong("id");
		rank.created = connection.getCurrentRowDateTime("created");
		rank.deleted = connection.getCurrentRowString("deleted");

		rank.code = connection.getCurrentRowLong("code2");
		rank.country = stripslashes(connection.getCurrentRowString("country"));
		rank.currency = stripslashes(connection.getCurrentRowString("currency"));
		rank.date = connection.getCurrentRowDateTime("date");
		rank.grossingPosition = connection.getCurrentRowInteger("grossingposition");
		rank.itemId = stripslashes(connection.getCurrentRowString("itemid"));
		rank.position = connection.getCurrentRowInteger("position");

		Integer price = connection.getCurrentRowInteger("price");
		if (price != null) {
			rank.price = Float.valueOf(price.floatValue() / 100.0f);
		}

		rank.source = stripslashes(connection.getCurrentRowString("source"));
		rank.type = stripslashes(connection.getCurrentRowString("type"));

		Long revenue = connection.getCurrentRowLong("revenue");
		if (revenue != null) {
			rank.revenue = Float.valueOf(revenue.floatValue() / 100.0f);
		}

		rank.downloads = connection.getCurrentRowInteger("downloads");

		Long categoryId = connection.getCurrentRowLong("categoryid");
		if (categoryId != null) {
			(rank.category = new Category()).id(categoryId);
		}

		return rank;
	}

	@Override
	public Rank addRank(Rank rank) throws DataAccessException {
		Rank addedRank = null;

		final String addeRankQuery = String
				.format("INSERT INTO `rank` (`position`,`grossingposition`,`itemid`,`type`,`country`,`date`,`source`,`price`,`currency`,`categoryid`,`code2`,`revenue`,`downloads`) VALUES (%d,%d,'%s','%s','%s',FROM_UNIXTIME(%d),'%s',%d,'%s',%d,%d,%s,%s)",
						rank.position.longValue(), rank.grossingPosition.longValue(), addslashes(rank.itemId), addslashes(rank.type), addslashes(rank.country),
						rank.date.getTime() / 1000, addslashes(rank.source), (int) (rank.price.floatValue() * 100.0f), addslashes(rank.currency),
						rank.category.id.longValue(), rank.code.longValue(),
						rank.revenue == null ? "NULL" : Integer.toString((int) (rank.revenue.floatValue() * 100.0f)), rank.downloads == null ? "NULL"
								: rank.downloads.toString());

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
	public Rank updateRank(Rank rank) throws DataAccessException {
		Rank updatedRank = null;

		final String updateRankQuery = String
				.format("UPDATE `rank` SET `position`=%d,`grossingposition`=%d,`itemid`='%s',`type`='%s',`country`='%s',`date`=FROM_UNIXTIME(%d),`source`='%s',`price`=%d,`currency`='%s',`categoryid`=%d,`code2`=%d,`revenue`=%s,`downloads`=%s WHERE `id`=%d;",
						rank.position.longValue(), rank.grossingPosition.longValue(), addslashes(rank.itemId), addslashes(rank.type), addslashes(rank.country),
						rank.date.getTime() / 1000, addslashes(rank.source), (int) (rank.price.floatValue() * 100.0f), addslashes(rank.currency),
						rank.category.id.longValue(), rank.code.longValue(),
						rank.revenue == null || rank.revenue.isInfinite() ? "NULL" : Integer.toString((int) (rank.revenue.floatValue() * 100.0f)),
						rank.downloads == null || rank.downloads.intValue() == Integer.MAX_VALUE ? "NULL" : rank.downloads.intValue(), rank.id.longValue());

		Connection rankConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeRank.toString());

		try {
			rankConnection.connect();
			rankConnection.executeQuery(updateRankQuery);

			if (rankConnection.getAffectedRowCount() > 0) {
				String memcacheKey = getName() + ".id." + rank.id;
				cache.delete(memcacheKey);

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
	public Rank getItemGatherCodeRank(String itemId, Long code, String store, String country, Collection<String> possibleTypes) throws DataAccessException {
		Rank rank = null;

		String memcacheKey = getName() + ".itemgathercoderank." + itemId + country + "." + store + "." + StringUtils.join(possibleTypes, ".") + "." + code;
		String jsonString = (String) cache.get(memcacheKey);

		if (jsonString == null) {

			String typesQueryPart = null;
			if (possibleTypes.size() == 1) {
				typesQueryPart = String.format("CAST(`type` AS BINARY)=CAST('%s' AS BINARY)", possibleTypes.iterator().next());
			} else {
				typesQueryPart = "CAST(`type` AS BINARY) IN (CAST('" + StringUtils.join(possibleTypes, "' AS BINARY),CAST('") + "' AS BINARY))";
			}

			final String getItemGatherCodeRankQuery = String
					.format("SELECT * FROM `rank` WHERE `itemid`='%s' AND `code2`=%d AND CAST(`source` AS BINARY)=CAST('%s' AS BINARY) AND CAST(`country` AS BINARY)=CAST('%s' AS BINARY) AND %s AND `deleted`='n' LIMIT 1",
							itemId, code.longValue(), store, country, typesQueryPart);

			Connection rankConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeRank.toString());

			try {
				rankConnection.connect();
				rankConnection.executeQuery(getItemGatherCodeRankQuery);

				if (rankConnection.fetchNextRow()) {
					rank = toRank(rankConnection);

					// if (rank != null) {
					// cal.setTime(new Date());
					// cal.add(Calendar.DAY_OF_MONTH, 20);
					// cache.put(memcacheKey, rank.toString(), cal.getTime());
					// }
				}
			} finally {
				if (rankConnection != null) {
					rankConnection.disconnect();
				}
			}
		} else {
			rank = new Rank();
			rank.fromJson(jsonString);
		}

		return rank;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.rank.IRankService#getRanks(io.reflection.app.datatypes.Country, io.reflection.app.datatypes.Store,
	 * io.reflection.app.datatypes.Category, java.lang.String, java.util.Date, java.util.Date, io.reflection.app.api.datatypes.Pager)
	 */
	@Override
	public List<Rank> getRanks(Country country, Store store, Category category, String listType, Date after, Date before, Pager pager)
			throws DataAccessException {
		List<Rank> ranks = new ArrayList<Rank>();

		Long code = FeedFetchServiceProvider.provide().getGatherCode(store, after, before);

		if (code != null) {
			ranks.addAll(getGatherCodeRanks(country, store, category, listType, code, pager, Boolean.FALSE));
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
	public List<Rank> getItemRanks(Country country, Store store, String listType, Item item, Date after, Date before, Pager pager) throws DataAccessException {
		List<Rank> ranks = new ArrayList<Rank>();

		Collector collector = CollectorFactory.getCollectorForStore(store.a3Code);
		boolean isGrossing = collector.isGrossing(listType);

		List<String> types = new ArrayList<String>();

		if (isGrossing) {
			types.addAll(collector.getCounterpartTypes(listType));
		}

		types.add(addslashes(listType));

		if (isGrossing) {
			pager.sortBy = "grossingposition";
		} else {
			pager.sortBy = "position";
		}

		String memcacheKey = getName() + ".itemranks." + item.id.toString() + "." + country.a2Code + "." + store.a3Code + "." + StringUtils.join(types, ".")
				+ "." + (before == null ? "none" : before.getTime()) + "." + (after == null ? "none" : after.getTime()) + "." + pager.start + "." + pager.count
				+ "." + pager.sortDirection + "." + pager.sortBy;
		String itemRanksString = (String) cache.get(memcacheKey);

		if (itemRanksString == null) {
			String typesQueryPart = null;
			if (types.size() == 1) {
				typesQueryPart = String.format("CAST(`type` AS BINARY)=CAST('%s' AS BINARY)", types.get(0));
			} else {
				typesQueryPart = "CAST(`type` AS BINARY) IN (CAST('" + StringUtils.join(types, "' AS BINARY),CAST('") + "' AS BINARY))";
			}

			String getCountryStoreTypeRanksQuery = String
					.format("SELECT * FROM `rank` WHERE %s AND CAST(`country` AS BINARY)=CAST('%s' AS BINARY) AND CAST(`source` AS BINARY)=CAST('%s' AS BINARY) AND `itemid`='%s' AND `categoryid`=24 AND %s AND %s `deleted`='n' ORDER BY `date` ASC, `%s` %s LIMIT %d,%d",
							typesQueryPart, addslashes(country.a2Code), addslashes(store.a3Code), addslashes(item.internalId), beforeAfterQuery(before, after),
							isGrossing ? "`grossingposition`<>0 AND" : "", pager.sortBy,
							pager.sortDirection == SortDirectionType.SortDirectionTypeAscending ? "ASC" : "DESC", pager.start, pager.count);

			Connection rankConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeRank.toString());

			try {
				rankConnection.connect();
				rankConnection.executeQuery(getCountryStoreTypeRanksQuery);

				Map<Date, Rank> ranksLookup = new HashMap<Date, Rank>();
				Date date;

				while (rankConnection.fetchNextRow()) {
					// strip out duplicates for date
					date = ApiHelper.removeTime(rankConnection.getCurrentRowDateTime("date"));

					if (ranksLookup.get(date) == null) {
						Rank rank = toRank(rankConnection);

						if (rank != null) {
							rank.date = date;
							ranks.add(rank);
							ranksLookup.put(date, rank);
						}
					}
				}

				if (ranks.size() > 0) {
					JsonArray jsonArray = new JsonArray();
					for (Rank rank : ranks) {
						jsonArray.add(rank.toJson());
					}
					cache.put(memcacheKey, JsonUtils.cleanJson(jsonArray.toString()), DateTime.now(DateTimeZone.UTC).plusDays(20).toDate());
				}
			} finally {
				if (rankConnection != null) {
					rankConnection.disconnect();
				}
			}
		} else {
			JsonArray parsed = (JsonArray) new JsonParser().parse(itemRanksString);
			Rank rank;
			for (JsonElement jsonElement : parsed) {
				rank = new Rank();
				rank.fromJson(jsonElement.getAsJsonObject());
				ranks.add(rank);
			}
		}

		return ranks;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.rank.IRankService#getRanksCount(java.lang.String, java.lang.String, java.lang.String, java.util.Date, java.util.Date)
	 */
	@Override
	public Long getRanksCount(Country country, Store store, Category category, String listType, Date after, Date before) throws DataAccessException {
		Long ranksCount = Long.valueOf(0);

		Long code = FeedFetchServiceProvider.provide().getGatherCode(store, after, before);

		if (code != null) {
			ranksCount = getGatherCodeRanksCount(country, store, category, listType, code);
		}

		return ranksCount;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.rank.IRankService#getItemHasGrossingRank(io.reflection.app.datatypes.Item)
	 */
	@Override
	public Boolean getItemHasGrossingRank(Item item) throws DataAccessException {
		Boolean hasGrossingRank = Boolean.FALSE;

		String getItemHasGrossingRankQuery = String.format("SELECT `id` FROM `rank` WHERE itemid`='%s' AND `grossingposition`<>0 LIMIT 1",
				addslashes(item.internalId));

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
	 * @see io.reflection.app.service.rank.IRankService#addRanksBatch(java.util.Collection)
	 */
	@Override
	public Long addRanksBatch(Collection<Rank> ranks) throws DataAccessException {
		Long addedRankCount = Long.valueOf(0);

		StringBuffer addRanksBatchQuery = new StringBuffer();

		addRanksBatchQuery
				.append("INSERT INTO `rank` (`position`,`grossingposition`,`itemid`,`type`,`country`,`date`,`source`,`price`,`currency`,`categoryid`,`code2`,`revenue`,`downloads`) VALUES ");

		boolean addComma = false;
		for (Rank rank : ranks) {
			if (addComma) {
				addRanksBatchQuery.append(",");
			}

			addRanksBatchQuery.append(String.format("(%d,%d,'%s','%s','%s',FROM_UNIXTIME(%d),'%s',%d,'%s',%d,%d,%s,%s)", rank.position.longValue(),
					rank.grossingPosition.longValue(), addslashes(rank.itemId), addslashes(rank.type), addslashes(rank.country), rank.date.getTime() / 1000,
					addslashes(rank.source), (int) (rank.price.floatValue() * 100.0f), addslashes(rank.currency), rank.category.id.longValue(), rank.code
							.longValue(), rank.revenue == null ? "NULL" : Integer.toString((int) (rank.revenue.floatValue() * 100.0f)),
					rank.downloads == null ? "NULL" : rank.downloads.toString()));
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
	 * @see io.reflection.app.service.rank.IRankService#getGatherCodeRanksCount(io.reflection.app.shared.datatypes.Country,
	 * io.reflection.app.shared.datatypes.Store, io.reflection.app.shared.datatypes.Category, java.lang.String, java.lang.Long)
	 */
	@Override
	public Long getGatherCodeRanksCount(Country country, Store store, Category category, String listType, Long code) throws DataAccessException {
		Long ranksCount = null;

		Collector collector = CollectorFactory.getCollectorForStore(store.a3Code);
		boolean isGrossing = collector.isGrossing(listType);

		List<String> types = new ArrayList<String>();

		if (isGrossing) {
			types.addAll(collector.getCounterpartTypes(listType));
		}

		types.add(addslashes(listType));

		String memcacheKey = getName() + ".gathercoderankscount." + code.toString() + "." + country.a2Code + "." + store.a3Code + "." + category.id.toString()
				+ "." + StringUtils.join(types, ".");
		ranksCount = (Long) cache.get(memcacheKey);

		if (ranksCount == null) {
			String typesQueryPart = null;
			if (types.size() == 1) {
				typesQueryPart = String.format("CAST(`type` AS BINARY)=CAST('%s' AS BINARY)", types.get(0));
			} else {
				typesQueryPart = "CAST(`type` AS BINARY) IN (CAST('" + StringUtils.join(types, "' AS BINARY),CAST('") + "' AS BINARY))";
			}

			String getRanksCountQuery = String
					.format("SELECT COUNT(1) AS `count` FROM `rank` WHERE %s AND CAST(`country` AS BINARY)=CAST('%s' AS BINARY) AND CAST(`source` AS BINARY)=CAST('%s' AS BINARY) AND `categoryid`=%d AND `code2`=%d AND %s `deleted`='n'",
							typesQueryPart, addslashes(country.a2Code), addslashes(store.a3Code), category.id.longValue(), code.longValue(),
							isGrossing ? "`grossingposition`<>0 AND" : "");

			Connection rankConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeRank.toString());

			try {
				rankConnection.connect();
				rankConnection.executeQuery(getRanksCountQuery);

				if (rankConnection.fetchNextRow()) {
					ranksCount = rankConnection.getCurrentRowLong("count");
					cache.put(memcacheKey, ranksCount, DateTime.now(DateTimeZone.UTC).plusDays(20).toDate());
				}

			} finally {
				if (rankConnection != null) {
					rankConnection.disconnect();
				}
			}
		}

		return ranksCount;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.rank.IRankService#getCodeLastRankDate(java.lang.Long)
	 */
	@Override
	public Date getCodeLastRankDate(Long code) throws DataAccessException {
		Date date = null;

		String getCodeLastRankDateQuery = String.format("SELECT `date` FROM `rank` WHERE `code2`=%d ORDER BY `date` DESC LIMIT 1", code.longValue());

		Connection rankConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeRank.toString());

		try {
			rankConnection.connect();
			rankConnection.executeQuery(getCodeLastRankDateQuery);

			if (rankConnection.fetchNextRow()) {
				date = rankConnection.getCurrentRowDateTime("date");
			}
		} finally {
			if (rankConnection != null) {
				rankConnection.disconnect();
			}
		}

		return date;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.rank.IRankService#updateRanksBatch(java.util.Collection)
	 */
	@Override
	public Long updateRanksBatch(Collection<Rank> updateRanks) throws DataAccessException {
		long ranksCount = 0;

		final String updateRanksBatchQueryFormat = "UPDATE `rank` SET `position`=%d,`grossingposition`=%d,`itemid`='%s',`type`='%s',`country`='%s',`date`=FROM_UNIXTIME(%d),`source`='%s',`price`=%d,`currency`='%s',`categoryid`=%d,`code2`=%d,`revenue`=%s,`downloads`=%s WHERE `id`=%d;";

		Connection rankConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeRank.toString());

		try {
			rankConnection.connect();

			String updateRanksBatchQuery;
			for (Rank rank : updateRanks) {
				updateRanksBatchQuery = String.format(updateRanksBatchQueryFormat, rank.position.longValue(), rank.grossingPosition.longValue(),
						addslashes(rank.itemId), addslashes(rank.type), addslashes(rank.country), rank.date.getTime() / 1000, addslashes(rank.source),
						(int) (rank.price.floatValue() * 100.0f), addslashes(rank.currency), rank.category.id.longValue(), rank.code.longValue(),
						rank.revenue == null ? "NULL" : Integer.toString((int) (rank.revenue.floatValue() * 100.0f)), rank.downloads == null ? "NULL"
								: rank.downloads.toString(), rank.id.longValue());

				rankConnection.executeQuery(updateRanksBatchQuery);

				if (rankConnection.getAffectedRowCount() > 0) {
					String memcacheKey = getName() + ".id." + rank.id;
					cache.delete(memcacheKey);

					ranksCount++;
				}
			}
		} finally {
			if (rankConnection != null) {
				rankConnection.disconnect();
			}
		}

		return Long.valueOf(ranksCount);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.rank.IRankService#getGatherCodeRanks(io.reflection.app.shared.datatypes.Country, io.reflection.app.shared.datatypes.Store,
	 * io.reflection.app.shared.datatypes.Category java.lang.String, java.lang.Long, io.reflection.app.api.shared.datatypes.Pager, java.lang.Boolean)
	 */
	@Override
	public List<Rank> getGatherCodeRanks(Country country, Store store, Category category, String listType, Long code, Pager pager, Boolean ignoreGrossingRank)
			throws DataAccessException {
		List<Rank> ranks = new ArrayList<Rank>();

		Connection rankConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeRank.toString());

		Collector collector = CollectorFactory.getCollectorForStore(store.a3Code);
		boolean isGrossing = collector.isGrossing(listType);

		if (isGrossing) {
			pager.sortBy = "grossingposition";
		} else {
			pager.sortBy = "position";
		}

		if (pager.sortDirection == null) {
			pager.sortDirection = SortDirectionType.SortDirectionTypeAscending;
		}

		List<String> types = new ArrayList<String>();

		if (isGrossing) {
			types.addAll(collector.getCounterpartTypes(listType));
		}

		types.add(addslashes(listType));

		if (code != null) {
			String memcacheKey = getName() + ".gathercoderanks." + code.toString() + "." + country.a2Code + "." + store.a3Code + "." + category.id.toString()
					+ "." + StringUtils.join(types, ".") + "." + pager.start + "." + pager.count + "." + pager.sortDirection + "." + pager.sortBy;

			String ranksString = (String) cache.get(memcacheKey);

			if (ranksString == null) {
				String typesQueryPart = null;
				if (types.size() == 1) {
					typesQueryPart = String.format("CAST(`type` AS BINARY)=CAST('%s' AS BINARY)", types.get(0));
				} else {
					typesQueryPart = "CAST(`type` AS BINARY) IN (CAST('" + StringUtils.join(types, "' AS BINARY),CAST('") + "' AS BINARY))";
				}

				String getCountryStoreTypeRanksQuery = String
						.format("SELECT * FROM `rank` WHERE %s AND CAST(`country` AS BINARY)=CAST('%s' AS BINARY) AND CAST(`source` AS BINARY)=CAST('%s' AS BINARY) AND `categoryid`=%d AND `code2`=%d AND %s `deleted`='n' ORDER BY `%s` %s,`date` DESC LIMIT %d,%d",
								typesQueryPart, addslashes(country.a2Code), addslashes(store.a3Code), category.id.longValue(), code.longValue(), isGrossing
										|| !ignoreGrossingRank.booleanValue() ? "`grossingposition`<>0 AND" : "", pager.sortBy,
								pager.sortDirection == SortDirectionType.SortDirectionTypeAscending ? "ASC" : "DESC", pager.start, pager.count);

				try {
					rankConnection.connect();
					rankConnection.executeQuery(getCountryStoreTypeRanksQuery);

					while (rankConnection.fetchNextRow()) {
						Rank rank = toRank(rankConnection);

						if (rank != null) {
							ranks.add(rank);
						}
					}

					if (ranks.size() > 0) {
						JsonArray jsonArray = new JsonArray();
						for (Rank rank : ranks) {
							jsonArray.add(rank.toJson());
						}

						cache.put(memcacheKey, JsonUtils.cleanJson(jsonArray.toString()), DateTime.now(DateTimeZone.UTC).plusDays(20).toDate());
					}
				} finally {
					if (rankConnection != null) {
						rankConnection.disconnect();
					}
				}
			} else {
				JsonArray parsed = (JsonArray) new JsonParser().parse(ranksString);
				Rank rank;
				for (JsonElement jsonElement : parsed) {
					rank = new Rank();
					rank.fromJson(jsonElement.getAsJsonObject());
					ranks.add(rank);
				}
			}
		}

		return ranks;
	}

	// /*
	// * (non-Javadoc)
	// *
	// * @see io.reflection.app.service.rank.IRankService#getAllRanks(io.reflection.app.datatypes.shared.Country, io.reflection.app.datatypes.shared.Store,
	// * io.reflection.app.datatypes.shared.Category, java.lang.String, java.util.Date, java.util.Date)
	// */
	// @Override
	// public List<Rank> getAllRanks(Country country, Store store, Category category, String listType, Date start, Date end) throws DataAccessException {
	// List<Rank> ranks = new ArrayList<Rank>();
	//
	// Collector collector = CollectorFactory.getCollectorForStore(store.a3Code);
	// boolean isGrossing = collector.isGrossing(listType);
	//
	// List<String> types = new ArrayList<String>();
	//
	// if (isGrossing) {
	// types.addAll(collector.getCounterpartTypes(listType));
	// }
	//
	// types.add(addslashes(listType));
	//
	// String memcacheKey = getName() + ".allranks." + country.a2Code + "." + store.a3Code + "." + category.id.toString() + "." + StringUtils.join(types, ".")
	// + "." + (start == null ? "none" : start.getTime()) + "." + (end == null ? "none" : end.getTime());
	// String allRanksString = (String) cache.get(memcacheKey);
	//
	// if (allRanksString == null) {
	// String typesQueryPart = null;
	// if (types.size() == 1) {
	// typesQueryPart = String.format("CAST(`type` AS BINARY)=CAST('%s' AS BINARY)", types.get(0));
	// } else {
	// typesQueryPart = "CAST(`type` AS BINARY) IN (CAST('" + StringUtils.join(types, "' AS BINARY),CAST('") + "' AS BINARY))";
	// }
	//
	// Long code = FeedFetchServiceProvider.provide().getGatherCode(country, store, start, end);
	//
	// if (code != null) {
	// Connection rankConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeRank.toString());
	// String getCountryStoreTypeRanksQuery = String
	// .format("SELECT * FROM `rank` WHERE %s AND CAST(`country` AS BINARY)=CAST('%s' AS BINARY) AND CAST(`source` AS BINARY)=CAST('%s' AS BINARY) AND `categoryid`=%d AND `code2`=%d AND `deleted`='n'",
	// typesQueryPart, addslashes(country.a2Code), addslashes(store.a3Code), category.id.longValue(), code.longValue());
	//
	// try {
	// rankConnection.connect();
	// rankConnection.executeQuery(getCountryStoreTypeRanksQuery);
	//
	// while (rankConnection.fetchNextRow()) {
	// Rank rank = toRank(rankConnection);
	//
	// if (rank != null) {
	// ranks.add(rank);
	// }
	// }
	//
	// if (ranks.size() > 0) {
	// JsonArray jsonArray = new JsonArray();
	// for (Rank rank : ranks) {
	// jsonArray.add(rank.toJson());
	// }
	//
	// cal.setTime(new Date());
	// cal.add(Calendar.DAY_OF_MONTH, 20);
	// cache.put(memcacheKey, JsonUtils.cleanJson(jsonArray.toString()), cal.getTime());
	// }
	//
	// } finally {
	// if (rankConnection != null) {
	// rankConnection.disconnect();
	// }
	// }
	// }
	// } else {
	// JsonArray parsed = (JsonArray) new JsonParser().parse(allRanksString);
	// Rank rank;
	// for (JsonElement jsonElement : parsed) {
	// rank = new Rank();
	// rank.fromJson(jsonElement.getAsJsonObject());
	// ranks.add(rank);
	// }
	// }
	//
	// return ranks;
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.rank.IRankService#getRankIds(io.reflection.app.datatypes.shared.Country, io.reflection.app.datatypes.shared.Store,
	 * io.reflection.app.datatypes.shared.Category, java.util.Date, java.util.Date)
	 */
	@Override
	public List<Long> getRankIds(Country country, Store store, Category category, Date start, Date end) throws DataAccessException {
		List<Long> rankIds = new ArrayList<Long>();

		Connection rankConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeRank.toString());
		String getCountryStoreTypeRanksQuery = String
				.format("SELECT `id` FROM `rank` WHERE CAST(`country` AS BINARY)=CAST('%s' AS BINARY) AND CAST(`source` AS BINARY)=CAST('%s' AS BINARY) AND `categoryid`=%d AND %s AND `deleted`='n'",
						addslashes(country.a2Code), addslashes(store.a3Code), category.id.longValue(), beforeAfterQuery(end, start));

		try {
			rankConnection.connect();
			rankConnection.executeQuery(getCountryStoreTypeRanksQuery);

			while (rankConnection.fetchNextRow()) {
				Long id = rankConnection.getCurrentRowLong("id");

				if (id != null) {
					rankIds.add(id);
				}
			}
		} finally {
			if (rankConnection != null) {
				rankConnection.disconnect();
			}
		}

		return rankIds;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.rank.IRankService#getRanksCount()
	 */
	@Override
	public List<Rank> getRanksCount() throws DataAccessException {
		throw new UnsupportedOperationException("Ranks is an exteremely large table and it rows should not be counted like this");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.rank.IRankService#getRanks(io.reflection.app.api.shared.datatypes.Pager)
	 */
	@Override
	public List<Rank> getRanks(Pager pager) throws DataAccessException {
		List<Rank> ranks = new ArrayList<Rank>();

		Connection rankConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeRank.toString());

		if (pager.sortDirection == null) {
			pager.sortDirection = SortDirectionType.SortDirectionTypeAscending;
		}

		switch (pager.sortBy) {
		case "id":
		default:
			pager.sortBy = "id";
			break;
		}

		String getRanksQuery = String.format("SELECT * FROM `rank` WHERE `deleted`='n' ORDER BY `%s` %s LIMIT %d, %d", pager.sortBy,
				pager.sortDirection == SortDirectionType.SortDirectionTypeAscending ? "ASC" : "DESC", pager.start, pager.count);

		try {
			rankConnection.connect();
			rankConnection.executeQuery(getRanksQuery);

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
	 * @see io.reflection.app.service.rank.IRankService#getRankIds(io.reflection.app.api.shared.datatypes.Pager)
	 */
	@Override
	public List<Long> getRankIds(Pager pager) throws DataAccessException {
		List<Long> rankIds = new ArrayList<Long>();

		Connection rankConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeRank.toString());

		String getRankIdsQuery = String.format("SELECT `id` FROM `rank` WHERE `deleted`='n' LIMIT %d, %d", pager.start, pager.count);

		try {
			rankConnection.connect();
			rankConnection.executeQuery(getRankIdsQuery);

			while (rankConnection.fetchNextRow()) {
				Long rankId = rankConnection.getCurrentRowLong("id");

				if (rankId != null) {
					rankIds.add(rankId);
				}
			}

		} finally {
			if (rankConnection != null) {
				rankConnection.disconnect();
			}
		}

		return rankIds;
	}
}