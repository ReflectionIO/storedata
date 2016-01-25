//
//  RankService.java
//  storedata
//
//  Created by William Shakour on 18 July 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.service.rank;

import static com.spacehopperstudios.utility.StringUtils.*;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.spacehopperstudios.utility.JsonUtils;

import co.spchopr.persistentmap.PersistentMap;
import co.spchopr.persistentmap.PersistentMapFactory;
import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.api.shared.datatypes.SortDirectionType;
import io.reflection.app.datatypes.shared.Category;
import io.reflection.app.datatypes.shared.Country;
import io.reflection.app.datatypes.shared.FormType;
import io.reflection.app.datatypes.shared.Item;
import io.reflection.app.datatypes.shared.Rank;
import io.reflection.app.datatypes.shared.Store;
import io.reflection.app.logging.GaeLevel;
import io.reflection.app.repackaged.scphopr.cloudsql.Connection;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseServiceProvider;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseType;
import io.reflection.app.repackaged.scphopr.service.database.IDatabaseService;
import io.reflection.app.service.ServiceType;
import io.reflection.app.service.feedfetch.FeedFetchService;
import io.reflection.app.shared.util.DataTypeHelper;

public class RankService implements IRankService {
	private static final Logger LOG = Logger.getLogger(RankService.class.getName());
	private final PersistentMap cache = PersistentMapFactory.createObjectify();

	@Override
	public String getName() {
		return ServiceType.ServiceTypeRank.toString();
	}

	@Override
	public Rank getRank(Long id) throws DataAccessException {
		Rank rank = null;

		final IDatabaseService databaseService = DatabaseServiceProvider.provide();
		final Connection rankConnection = databaseService.getNamedConnection(DatabaseType.DatabaseTypeRank.toString());

		final String getRankQuery = String.format("SELECT r.*, rf.group_fetch_code, rf.fetch_date, rf.country, rf.category, rf.type, rf.platform "
				+ " FROM `rank2` r inner join rank_fetch rf on (r.rank_fetch_id = rf.rank_fetch_id) WHERE r.`id`=%d LIMIT 1", id.longValue());

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
	 * @throws DataAccessException
	 */
	private Rank toRank(Connection connection) throws DataAccessException {
		final Rank rank = new Rank();

		rank.id = connection.getCurrentRowLong("id");
		rank.created = connection.getCurrentRowDateTime("fetch_date");
		rank.deleted = "n";

		rank.code = connection.getCurrentRowLong("group_fetch_code");
		rank.country = stripslashes(connection.getCurrentRowString("country"));
		rank.currency = stripslashes(connection.getCurrentRowString("currency"));
		rank.date = connection.getCurrentRowDateTime("fetch_date");
		rank.itemId = stripslashes(connection.getCurrentRowString("itemid"));

		String type = connection.getCurrentRowString("type");

		Integer position = connection.getCurrentRowInteger("position");

		if ("GROSSING".equalsIgnoreCase(type)) {
			rank.grossingPosition = position;
		} else {
			rank.position = position;
		}

		final Integer price = connection.getCurrentRowInteger("price");
		if (price != null) {
			rank.price = Float.valueOf(price.floatValue() / 100.0f);
		}

		rank.source = "ios";
		rank.type = FeedFetchService.getOldTypeFromDBType(type, connection.getCurrentRowString("platform"));

		final Long revenue = connection.getCurrentRowLong("revenue_universal");
		if (revenue != null) {
			rank.revenue = Float.valueOf(revenue.floatValue() / 100.0f);
		}

		rank.downloads = connection.getCurrentRowInteger("downloads_universal");

		final Long categoryId = connection.getCurrentRowLong("category");
		if (categoryId != null) {
			(rank.category = new Category()).id(categoryId);
		}

		return rank;
	}

	@Override
	public Rank updateRank(Rank rank) throws DataAccessException {
		Rank updatedRank = null;

		String updateQuery = "UPDATE rank2 set position=?, itemid=?, price=?, currency=?, revenue_universal=?, downloads_universal=? where id=?";

		// final String updateRankQuery = String
		// .format("UPDATE `rank` SET `position`=%d,`grossingposition`=%d,`itemid`='%s',`type`='%s',`country`='%s',`date`=FROM_UNIXTIME(%d),`source`='%s',`price`=%d,`currency`='%s',`categoryid`=%d,`code2`=%d,`revenue`=%s,`downloads`=%s WHERE `id`=%d;",
		// rank.position.longValue(), rank.grossingPosition.longValue(), addslashes(rank.itemId), addslashes(rank.type), addslashes(rank.country),
		// rank.date.getTime() / 1000, addslashes(rank.source), (int) (rank.price.floatValue() * 100.0f), addslashes(rank.currency),
		// rank.category.id.longValue(), rank.code.longValue(),
		// rank.revenue == null || rank.revenue.isInfinite() ? "NULL" : Integer.toString((int) (rank.revenue.floatValue() * 100.0f)),
		// rank.downloads == null || rank.downloads.intValue() == Integer.MAX_VALUE ? "NULL" : rank.downloads.intValue(), rank.id.longValue());

		final Connection rankConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeRank.toString());

		try (PreparedStatement pstat = rankConnection.getRealConnection().prepareStatement(updateQuery, Statement.NO_GENERATED_KEYS)) {
			rankConnection.connect();
			// rankConnection.executeQuery(updateRankQuery);

			if (rankConnection.getAffectedRowCount() > 0) {
				updatedRank = getRank(rank.id);
			} else {
				updatedRank = rank;
			}

			int price = 0;
			if (rank.price != null) {
				price = (int) (rank.price.floatValue() * 100f);
			}

			pstat.setInt(1, rank.position == null || rank.position == 0 ? rank.grossingPosition : rank.position);
			pstat.setString(2, rank.itemId);
			pstat.setInt(3, price);
			pstat.setString(4, rank.currency);

			if (rank.revenue == null) {
				pstat.setNull(5, Types.BIGINT);
			} else {
				pstat.setLong(5, (long) (rank.revenue * 100));
			}

			if (rank.downloads == null) {
				pstat.setNull(6, Types.INTEGER);
			} else {
				pstat.setInt(6, rank.downloads);
			}

			pstat.setLong(7, rank.id);

			pstat.executeUpdate();

		} catch (SQLException e) {
			LOG.log(Level.SEVERE, "Exception occured while trying to update a rank", e);
		} finally {
			if (rankConnection != null) {
				rankConnection.disconnect();
			}
		}

		return updatedRank;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.service.rank.IRankService#getItemRanks(io.reflection.app.datatypes.Country, java.lang.String, io.reflection.app.datatypes.Item,
	 * java.util.Date, java.util.Date, io.reflection.app.api.datatypes.Pager)
	 */
	@Override
	public List<Rank> getItemRanks(Country country, Category category, String listType, Item item, Date after, Date before, Pager pager)
			throws DataAccessException, SQLException {
		final List<Rank> ranks = new ArrayList<Rank>();

		final String selectQuery = "SELECT  " +
				"    r.*, " +
				"    rf.group_fetch_code, " +
				"    rf.fetch_date, " +
				"    rf.country, " +
				"    rf.category, " +
				"    rf.type, " +
				"    rf.platform " +
				"FROM " +
				"    rank2 r  " +
				"        INNER JOIN " +
				"    rank_fetch rf FORCE INDEX (idx_rank_fetch_search_id) ON (rf.rank_fetch_id = r.rank_fetch_id " +
				"		AND rf.country = ? AND rf.category = ? " +
				"        AND rf.type = ? " +
				"        AND rf.platform = ? " +
				"        AND rf.fetch_date BETWEEN ? AND ? " +
				"        AND rf.fetch_time > '21:00') " +
				"WHERE " +
				"	r.itemid=? " +
				"ORDER BY rf.fetch_date";
		// final String selectQuery = "SELECT r.*, rf.group_fetch_code, rf.fetch_date, rf.country, rf.category, rf.type, rf.platform "
		// + " FROM rank_fetch rf inner join rank2 r on (r.rank_fetch_id = rf.rank_fetch_id and r.itemid=?) WHERE "
		// + " rf.country=? AND rf.category=? AND rf.type=? AND rf.platform=? AND rf.fetch_date BETWEEN ? AND ? and rf.fetch_time > '21:00'";

		final Connection rankConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeRank.toString());

		Calendar cal = Calendar.getInstance();
		cal.setTime(after);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		java.sql.Date afterParam = new java.sql.Date(cal.getTimeInMillis());

		cal.setTime(before);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		java.sql.Date beforeParam = new java.sql.Date(cal.getTimeInMillis());

		try (PreparedStatement pstat = rankConnection.getRealConnection().prepareStatement(selectQuery, Statement.NO_GENERATED_KEYS)) {
			rankConnection.connect();

			int paramCount = 1;
			pstat.setString(paramCount++, country.a2Code);
			pstat.setLong(paramCount++, category.id);
			pstat.setString(paramCount++, FeedFetchService.getDBTypeForFeedFetchType(listType));
			pstat.setString(paramCount++, FeedFetchService.getDBPlatformForFeedFetchType(listType));
			pstat.setDate(paramCount++, afterParam);
			pstat.setDate(paramCount++, beforeParam);
			pstat.setString(paramCount++, item.internalId);

			rankConnection.executePreparedStatement(pstat);

			while (rankConnection.fetchNextRow()) {
				Rank rank = toRank(rankConnection);
				ranks.add(rank);
			}
		} catch (SQLException e) {
			LOG.log(Level.SEVERE, "Exception occured while trying to gather code ranks", e);
			throw e;
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
	 * @see io.reflection.app.service.rank.IRankService#getItemHasGrossingRank(io.reflection.app.datatypes.Item)
	 */
	@Override
	public Boolean getItemHasGrossingRank(Item item) throws DataAccessException {
		Boolean hasGrossingRank = Boolean.FALSE;

		final String getItemHasGrossingRankQuery = String
				.format("select rank_fetch_id from rank_fetch where rank_fetch_id in ( select rank_fetch_id from rank2 where itemid='%s' ) and type='GROSSING' limit 1",
						addslashes(item.internalId));

		final Connection rankConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeRank.toString());

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
	public Long addRanksBatch(Long feedfetchId, Collection<Rank> ranks) throws DataAccessException {
		Long addedRankCount = 0L;

		String insertQuery = "INSERT INTO rank2 (rank_fetch_id, position, itemid, price, currency, revenue_universal, downloads_universal) values (?,?,?,?,?,?,?)";

		// final StringBuffer addRanksBatchQuery = new StringBuffer();
		//
		// addRanksBatchQuery
		// .append("INSERT INTO `rank` (`position`,`grossingposition`,`itemid`,`type`,`country`,`date`,`source`,`price`,`currency`,`categoryid`,`code2`,`revenue`,`downloads`) VALUES ");
		//
		// boolean addComma = false;
		// for (final Rank rank : ranks) {
		// if (addComma) {
		// addRanksBatchQuery.append(",");
		// }
		//
		// addRanksBatchQuery.append(String.format("(%d,%d,'%s','%s','%s',FROM_UNIXTIME(%d),'%s',%d,'%s',%d,%d,%s,%s)", rank.position.longValue(),
		// rank.grossingPosition.longValue(), addslashes(rank.itemId), addslashes(rank.type), addslashes(rank.country), rank.date.getTime() / 1000,
		// addslashes(rank.source), (int) (rank.price.floatValue() * 100.0f), addslashes(rank.currency), rank.category.id.longValue(), rank.code
		// .longValue(), rank.revenue == null ? "NULL" : Integer.toString((int) (rank.revenue.floatValue() * 100.0f)),
		// rank.downloads == null ? "NULL" : rank.downloads.toString()));
		// addComma = true;
		// }

		final Connection rankConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeRank.toString());

		try (PreparedStatement pstat = rankConnection.getRealConnection().prepareStatement(insertQuery, Statement.NO_GENERATED_KEYS)) {
			rankConnection.connect();

			for (Rank rank : ranks) {

				int price = 0;
				if (rank.price != null) {
					price = (int) (rank.price.floatValue() * 100f);
				}
				pstat.setLong(1, feedfetchId);
				pstat.setInt(2, rank.position == null || rank.position == 0 ? rank.grossingPosition : rank.position);
				pstat.setString(3, rank.itemId);
				pstat.setInt(4, price);
				pstat.setString(5, rank.currency);

				if (rank.revenue == null) {
					pstat.setNull(6, Types.BIGINT);
				} else {
					pstat.setLong(6, (long) (rank.revenue * 100));
				}

				if (rank.downloads == null) {
					pstat.setNull(7, Types.INTEGER);
				} else {
					pstat.setInt(7, rank.downloads);
				}

				pstat.addBatch();
			}

			pstat.executeBatch();

			// rankConnection.executeQuery(addRanksBatchQuery.toString());

			if (rankConnection.getAffectedRowCount() > 0) {
				addedRankCount = Long.valueOf(rankConnection.getAffectedRowCount());
			}
		} catch (SQLException e) {
			LOG.log(Level.SEVERE, "Exception occured while trying to execute a statement", e);
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
	 * @see io.reflection.app.service.rank.IRankService#updateRanksBatch(java.util.Collection)
	 */
	@Override
	public Long updateRanksBatch(Collection<Rank> updateRanks) throws DataAccessException {
		long ranksCount = 0;

		String updateQuery = "UPDATE rank2 set position=?, itemid=?, price=?, currency=?, revenue_universal=?, downloads_universal=? where id=?";
		// final String updateRanksBatchQueryFormat = "UPDATE `rank` SET
		// `position`=%d,`grossingposition`=%d,`itemid`='%s',`type`='%s',`country`='%s',`date`=FROM_UNIXTIME(%d),`source`='%s',`price`=%d,`currency`='%s',`categoryid`=%d,`code2`=%d,`revenue`=%s,`downloads`=%s
		// WHERE `id`=%d;";

		final Connection rankConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeRank.toString());

		try (PreparedStatement pstat = rankConnection.getRealConnection().prepareStatement(updateQuery, Statement.NO_GENERATED_KEYS)) {
			rankConnection.connect();

			// String updateRanksBatchQuery;
			for (final Rank rank : updateRanks) {
				// updateRanksBatchQuery = String.format(updateRanksBatchQueryFormat, rank.position.longValue(), rank.grossingPosition.longValue(),
				// addslashes(rank.itemId), addslashes(rank.type), addslashes(rank.country), rank.date.getTime() / 1000, addslashes(rank.source),
				// (int) (rank.price.floatValue() * 100.0f), addslashes(rank.currency), rank.category.id.longValue(), rank.code.longValue(),
				// rank.revenue == null ? "NULL" : Integer.toString((int) (rank.revenue.floatValue() * 100.0f)), rank.downloads == null ? "NULL"
				// : rank.downloads.toString(), rank.id.longValue());

				// rankConnection.executeQuery(updateRanksBatchQuery);

				if (rankConnection.getAffectedRowCount() > 0) {
					ranksCount++;
				}

				pstat.setInt(1, rank.position == null || rank.position == 0 ? rank.grossingPosition : rank.position);
				pstat.setString(2, rank.itemId);
				pstat.setFloat(3, rank.price);
				pstat.setString(4, rank.currency);

				if (rank.revenue == null) {
					pstat.setNull(5, Types.BIGINT);
				} else {
					pstat.setLong(5, (long) (rank.revenue * 100));
				}

				if (rank.downloads == null) {
					pstat.setNull(6, Types.INTEGER);
				} else {
					pstat.setInt(6, rank.downloads);
				}

				pstat.setLong(7, rank.id);
				pstat.addBatch();
			}
			pstat.executeUpdate();
		} catch (SQLException e) {
			LOG.log(Level.SEVERE, "Exception occured while trying to update a rank", e);
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
	public List<Rank> getGatherCodeRanks(Country country, Category category, String listType, Long code) throws DataAccessException {
		return getGatherCodeRanks(country, category, listType, code, true);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.service.rank.IRankService#getGatherCodeRanks(io.reflection.app.shared.datatypes.Country, io.reflection.app.shared.datatypes.Store,
	 * io.reflection.app.shared.datatypes.Category java.lang.String, java.lang.Long, io.reflection.app.api.shared.datatypes.Pager, java.lang.Boolean)
	 */
	@Override
	public List<Rank> getGatherCodeRanks(Country country, Category category, String listType, Long code, boolean useCache) throws DataAccessException {
		if (LOG.isLoggable(GaeLevel.DEBUG)) {
			LOG.log(GaeLevel.DEBUG,
					String.format("getGatherCodeRanks called for country: %s, category: %s, listType: %s, code2: %d", country, category, listType, code));
		}

		List<Rank> ranks = new ArrayList<Rank>(200);

		String ranksString = null;
		final String memcacheKey = getName() + ".getGatherCodeRanks." + code + "." + country.a2Code + "." + category.id.toString() + "." + listType;

		if (useCache) {
			ranksString = (String) cache.get(memcacheKey);
		}

		final String selectQuery = "SELECT r.*, rf.group_fetch_code, rf.fetch_date, rf.country, rf.category, rf.type, rf.platform "
				+ " FROM `rank2` r inner join rank_fetch rf on (r.rank_fetch_id = rf.rank_fetch_id) WHERE "
				+ " rf.rank_fetch_id = (SELECT rank_fetch_id FROM rank_fetch WHERE group_fetch_code = ? AND country=? AND category=? AND type=? AND platform=? LIMIT 1)";

		final Connection rankConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeRank.toString());

		if (ranksString == null) {
			try (PreparedStatement pstat = rankConnection.getRealConnection().prepareStatement(selectQuery, Statement.NO_GENERATED_KEYS)) {
				rankConnection.connect();

				int paramCount = 1;
				pstat.setLong(paramCount++, code);
				pstat.setString(paramCount++, country.a2Code);
				pstat.setLong(paramCount++, category.id);
				pstat.setString(paramCount++, FeedFetchService.getDBTypeForFeedFetchType(listType));
				pstat.setString(paramCount++, FeedFetchService.getDBPlatformForFeedFetchType(listType));

				rankConnection.executePreparedStatement(pstat);

				while (rankConnection.fetchNextRow()) {
					Rank rank = toRank(rankConnection);
					ranks.add(rank);
				}
			} catch (SQLException e) {
				LOG.log(Level.SEVERE, "Exception occured while trying to gather code ranks", e);
			} finally {
				if (rankConnection != null) {
					rankConnection.disconnect();
				}
			}

			if (useCache) {
				if (ranks.size() > 0) {
					final JsonArray jsonArray = new JsonArray();
					for (final Rank rank : ranks) {
						jsonArray.add(rank.toJson());
					}

					try {
						cache.put(memcacheKey, JsonUtils.cleanJson(jsonArray.toString()), DateTime.now(DateTimeZone.UTC).plusDays(20).toDate());
					} catch (final Exception e) {
						LOG.log(Level.WARNING, "Exception occured while trying to store ranks into the cache with key: " + memcacheKey, e);
					}
				}
			}
		} else {
			final JsonArray parsed = (JsonArray) new JsonParser().parse(ranksString);
			Rank rank;
			for (final JsonElement jsonElement : parsed) {
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
	 * @see io.reflection.app.service.rank.IRankService#getRankIds(io.reflection.app.datatypes.shared.Country, io.reflection.app.datatypes.shared.Store,
	 * io.reflection.app.datatypes.shared.Category, java.util.Date, java.util.Date)
	 */
	@Override
	public List<Long> getRankIds(Country country, Store store, Category category, Date start, Date end) throws DataAccessException {
		final List<Long> rankIds = new ArrayList<Long>();

		final String selectQuery = "SELECT r.id FROM rank_fetch rf inner join rank2 r on (r.rank_fetch_id = rf.rank_fetch_id) WHERE "
				+ " rf.country=? AND rf.category=? AND rf.fetch_date BETWEEN ? AND ? and rf.fetch_time > '21:00'";

		final Connection rankConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeRank.toString());

		try (PreparedStatement pstat = rankConnection.getRealConnection().prepareStatement(selectQuery, Statement.NO_GENERATED_KEYS)) {
			rankConnection.connect();

			int paramCount = 1;
			pstat.setString(paramCount++, country.a2Code);
			pstat.setLong(paramCount++, category.id);
			pstat.setDate(paramCount++, new java.sql.Date(start.getTime()));
			pstat.setDate(paramCount++, new java.sql.Date(end.getTime()));

			rankConnection.executePreparedStatement(pstat);

			while (rankConnection.fetchNextRow()) {
				rankIds.add(rankConnection.getCurrentRowLong("id"));
			}
		} catch (SQLException e) {
			LOG.log(Level.SEVERE, "Exception occured while trying to rank ids by country, category and dates", e);
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
	 * @see io.reflection.app.service.rank.IRankService#getRankIds(io.reflection.app.api.shared.datatypes.Pager)
	 */
	@Override
	public List<Long> getRankIds(Pager pager) throws DataAccessException {
		final List<Long> rankIds = new ArrayList<Long>();

		final String selectQuery = "SELECT r.id FROM rank_fetch rf inner join rank2 r on (r.rank_fetch_id = rf.rank_fetch_id) WHERE "
				+ " rf.fetch_time > '21:00' limit ?, ?";

		final Connection rankConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeRank.toString());

		try (PreparedStatement pstat = rankConnection.getRealConnection().prepareStatement(selectQuery, Statement.NO_GENERATED_KEYS)) {
			rankConnection.connect();

			int paramCount = 1;
			pstat.setLong(paramCount++, pager.start);
			pstat.setLong(paramCount++, pager.count);

			rankConnection.executePreparedStatement(pstat);

			while (rankConnection.fetchNextRow()) {
				rankIds.add(rankConnection.getCurrentRowLong("id"));
			}
		} catch (SQLException e) {
			LOG.log(Level.SEVERE, "Exception occured while trying to rank ids by country, category and dates", e);
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
	 * @see io.reflection.app.service.rank.IRankService#getSaleSummaryAndRankForItemAndFormType(java.lang.String, io.reflection.app.datatypes.shared.Country,
	 * io.reflection.app.datatypes.shared.FormType, java.util.Date, java.util.Date)
	 */
	@Override
	public List<Rank> getSaleSummaryAndRankForItemAndFormType(String internalId, Country country, Long categoryId, FormType form, Date start, Date end,
			Pager pager) throws DataAccessException {
		ArrayList<Rank> ranks = new ArrayList<Rank>();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

		final Connection rankConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeRank.toString());

		String platform = form == FormType.FormTypeOther ? "PHONE" : "TABLET";
		String platformIOS = form == FormType.FormTypeOther ? "iphone" : "ipad";
		String sortDirection = pager.sortDirection == SortDirectionType.SortDirectionTypeAscending ? "ASC" : "DESC";


		final String getRanksQuery = String
				.format("SELECT s.date, (%s_app_revenue + %s_iap_revenue) as revenue, %s_downloads as downloads, s.price, s.currency "
						+ " FROM sale_summary s USE INDEX (idx_item_search) "
						+ "	WHERE s.date BETWEEN '%s' AND '%s' AND s.itemid = %s AND s.country = '%s' "
						+ " ORDER BY s.%s %s "
						+ " LIMIT %d, %d",
						platformIOS, platformIOS, platformIOS, dateFormat.format(start), dateFormat.format(end), internalId,
						country.a2Code, pager.sortBy, sortDirection, pager.start, pager.count);

		try {
			rankConnection.connect();
			rankConnection.executeQuery(getRanksQuery);

			while (rankConnection.fetchNextRow()) {
				Rank rank = new Rank();

				rank.country = country.a2Code;
				Double price = rankConnection.getCurrentRowDouble("price");
				rank.price = price == null ? null : (price.floatValue() / 100f);
				rank.currency = rankConnection.getCurrentRowString("currency");
				rank.date = rankConnection.getCurrentRowDateTime("date");
				rank.source = DataTypeHelper.IOS_STORE_A3;
				rank.itemId = internalId;

				// rank.position = rankConnection.getCurrentRowInteger("position");
				// rank.grossingPosition = rankConnection.getCurrentRowInteger("grossing_position");

				rank.downloads = rankConnection.getCurrentRowInteger("downloads");

				Double revenue = rankConnection.getCurrentRowDouble("revenue");
				rank.revenue = revenue == null ? 0 : (revenue.floatValue() / 100f);

				ranks.add(rank);
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
	 * @see io.reflection.app.service.rank.IRankService#getSaleSummaryAndRankForDataAccountAndFormType(java.lang.Long,
	 * io.reflection.app.datatypes.shared.Country, io.reflection.app.datatypes.shared.FormType, java.util.Date, java.util.Date,
	 * io.reflection.app.api.shared.datatypes.Pager)
	 */
	@Override
	public List<Rank> getSaleSummaryAndRankForDataAccountAndFormType(Long dataaccountId, Country country, FormType form, Date start, Date end, Pager pager)
			throws DataAccessException {

		String platformIOS = form == FormType.FormTypeOther ? "iphone" : "ipad";

		ArrayList<Rank> ranks = new ArrayList<Rank>();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

		final Connection rankConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeRank.toString());

		final String getRanksQuery = String
				.format("SELECT s.itemid, s.price, s.currency, SUM(%s_app_revenue + %s_iap_revenue) as revenue, SUM(%s_downloads) as downloads FROM sale_summary s WHERE s.date BETWEEN '%s' AND '%s' AND s.dataaccountid = %s AND s.country = '%s' GROUP BY s.itemid",
						platformIOS, platformIOS, platformIOS, dateFormat.format(start), dateFormat.format(end), dataaccountId, country.a2Code);
		try {
			rankConnection.connect();
			rankConnection.executeQuery(getRanksQuery);

			while (rankConnection.fetchNextRow()) {
				Rank rank = new Rank();

				rank.country = country.a2Code;
				rank.source = DataTypeHelper.IOS_STORE_A3;

				rank.itemId = rankConnection.getCurrentRowString("itemid");

				Double price = rankConnection.getCurrentRowDouble("price");
				rank.price = price == null ? null : (price.floatValue() / 100f);

				rank.currency = rankConnection.getCurrentRowString("currency");

				rank.downloads = rankConnection.getCurrentRowInteger("downloads");

				Double revenue = rankConnection.getCurrentRowDouble("revenue");
				rank.revenue = revenue == null ? 0 : (revenue.floatValue() / 100f);

				if (LOG.isLoggable(GaeLevel.DEBUG)) {
					LOG.log(GaeLevel.DEBUG, String.format("Got a rank -> itemid: %s, downloads: %s, revenue: %s", rank.itemId, rank.downloads, rank.revenue));
				}

				ranks.add(rank);
			}
		} finally {
			if (rankConnection != null) {
				rankConnection.disconnect();
			}
		}

		return ranks;
	}

	@Override
	public List<Rank> getRanks(Country country, Category category, String listType, Date onDate) throws DataAccessException {
		List<Rank> ranks = new ArrayList<Rank>(200);

		// final String memcacheKey = getName() + ".getRanks." + country.a2Code + "." + category.id.toString() + "." + listType + "." + onDate.getTime();

		final String selectQuery = "SELECT r.*, rf.group_fetch_code, rf.fetch_date, rf.country, rf.category, rf.type, rf.platform "
				+ " FROM `rank2` r inner join rank_fetch rf on (r.rank_fetch_id = rf.rank_fetch_id) WHERE "
				+ " rf.rank_fetch_id = (SELECT rank_fetch_id FROM rank_fetch WHERE country=? AND category=? AND type=? AND platform=? AND fetch_date = ? order by fetch_time desc LIMIT 1)";

		final Connection rankConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeRank.toString());

		final String ranksString = null; // (String) cache.get(memcacheKey);

		if (ranksString == null) {
			try (PreparedStatement pstat = rankConnection.getRealConnection().prepareStatement(selectQuery, Statement.NO_GENERATED_KEYS)) {
				rankConnection.connect();

				int paramCount = 1;
				pstat.setString(paramCount++, country.a2Code);
				pstat.setLong(paramCount++, category.id);
				pstat.setString(paramCount++, FeedFetchService.getDBTypeForFeedFetchType(listType));
				pstat.setString(paramCount++, FeedFetchService.getDBPlatformForFeedFetchType(listType));
				pstat.setDate(paramCount++, new java.sql.Date(onDate.getTime()));

				rankConnection.executePreparedStatement(pstat);

				while (rankConnection.fetchNextRow()) {
					Rank rank = toRank(rankConnection);
					ranks.add(rank);
				}
			} catch (SQLException e) {
				LOG.log(Level.SEVERE, "Exception occured while trying to get ranks", e);
			} finally {
				if (rankConnection != null) {
					rankConnection.disconnect();
				}
			}

			if (ranks.size() > 0) {
				final JsonArray jsonArray = new JsonArray();
				for (final Rank rank : ranks) {
					jsonArray.add(rank.toJson());
				}

				// try {
				// cache.put(memcacheKey, JsonUtils.cleanJson(jsonArray.toString()), DateTime.now(DateTimeZone.UTC).plusDays(20).toDate());
				// } catch (final Exception e) {
				// LOG.log(Level.WARNING, "Exception occured while trying to store ranks into the cache with key: " + memcacheKey, e);
				// }
			}
		} else {
			final JsonArray parsed = (JsonArray) new JsonParser().parse(ranksString);
			Rank rank;
			for (final JsonElement jsonElement : parsed) {
				rank = new Rank();
				rank.fromJson(jsonElement.getAsJsonObject());
				ranks.add(rank);
			}
		}

		return ranks;
	}

	@Override
	public Long getRanksCount(Country country, Category category, String listType, Date onDate) throws DataAccessException {
		final String selectQuery = "SELECT count(r.id) as count FROM `rank2` r inner join rank_fetch rf on (r.rank_fetch_id = rf.rank_fetch_id) WHERE "
				+ " rf.rank_fetch_id = (SELECT rank_fetch_id FROM rank_fetch WHERE country=? AND category=? AND type=? AND platform=? AND fetch_date = ? order by fetch_time desc LIMIT 1)";

		final Connection rankConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeRank.toString());

		try (PreparedStatement pstat = rankConnection.getRealConnection().prepareStatement(selectQuery, Statement.NO_GENERATED_KEYS)) {
			rankConnection.connect();

			int paramCount = 1;
			pstat.setString(paramCount++, country.a2Code);
			pstat.setLong(paramCount++, category.id);
			pstat.setString(paramCount++, FeedFetchService.getDBTypeForFeedFetchType(listType));
			pstat.setString(paramCount++, FeedFetchService.getDBPlatformForFeedFetchType(listType));
			pstat.setDate(paramCount++, new java.sql.Date(onDate.getTime()));

			rankConnection.executePreparedStatement(pstat);

			if (rankConnection.fetchNextRow()) return rankConnection.getCurrentRowLong("count");
		} catch (SQLException e) {
			LOG.log(Level.SEVERE, "Exception occured while trying to count ranks", e);
		} finally {
			if (rankConnection != null) {
				rankConnection.disconnect();
			}
		}

		return 0L;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.service.rank.IRankService#getOutOfLeaderboardDates(java.util.List, io.reflection.app.datatypes.shared.Country,
	 * io.reflection.app.datatypes.shared.Category, java.lang.String)
	 */
	@Override
	public List<Date> getOutOfLeaderboardDates(List<Date> missingDates, Country country, Category category, String listType) throws DataAccessException {
		List<Date> outOfLeaderboardDates = new ArrayList<Date>();

		String datesPart = "";
		for (int i = 0; i < missingDates.size(); i++) {
			datesPart += "'" + new java.sql.Date(missingDates.get(i).getTime()).toString() + "'" + (i == missingDates.size() - 1 ? "" : ",");
		}

		final String query = String
				.format("select fetch_date, count(id) from leaderboard where fetch_date IN (%s) and country=? and category=? and type=? and platform=? group by fetch_date order by fetch_date;",
						datesPart);

		final Connection rankConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeRank.toString());

		try (PreparedStatement pstat = rankConnection.getRealConnection().prepareStatement(query, Statement.NO_GENERATED_KEYS)) {
			rankConnection.connect();

			int paramCount = 1;
			pstat.setString(paramCount++, country.a2Code);
			pstat.setLong(paramCount++, category.id);
			pstat.setString(paramCount++, FeedFetchService.getDBTypeForFeedFetchType(listType));
			pstat.setString(paramCount++, FeedFetchService.getDBPlatformForFeedFetchType(listType));

			rankConnection.executePreparedStatement(pstat);

			while (rankConnection.fetchNextRow()) {
				outOfLeaderboardDates.add(rankConnection.getCurrentRowDateTime("fetch_date"));
			}

		} catch (SQLException e) {
			LOG.log(Level.SEVERE, "SQL Exception during identifying out of leaderboard dates", e);
		} finally {
			if (rankConnection != null) {
				rankConnection.disconnect();
			}
		}

		return outOfLeaderboardDates;
	}
}
