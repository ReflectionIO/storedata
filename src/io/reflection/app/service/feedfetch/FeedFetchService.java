//
//  FeedFetchService.java
//  storedata
//
//  Created by William Shakour on September 5, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.service.feedfetch;

import static com.spacehopperstudios.utility.StringUtils.addslashes;
import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.api.shared.datatypes.SortDirectionType;
import io.reflection.app.datatypes.shared.Category;
import io.reflection.app.datatypes.shared.Country;
import io.reflection.app.datatypes.shared.FeedFetch;
import io.reflection.app.datatypes.shared.FeedFetchStatusType;
import io.reflection.app.datatypes.shared.Store;
import io.reflection.app.repackaged.scphopr.cloudsql.Connection;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseServiceProvider;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseType;
import io.reflection.app.repackaged.scphopr.service.database.IDatabaseService;
import io.reflection.app.service.ServiceType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.utils.SystemProperty;

final class FeedFetchService implements IFeedFetchService {
	private static final Logger LOG = Logger.getLogger(FeedFetchService.class.getName());

	// private Calendar cal = Calendar.getInstance();
	// private PersistentMap cache = PersistentMapFactory.createObjectify();

	@Override
	public String getName() {
		return ServiceType.ServiceTypeFeedFetch.toString();
	}

	@Override
	public FeedFetch getFeedFetch(Long id) throws DataAccessException {
		FeedFetch feedFetch = null;

		final IDatabaseService databaseService = DatabaseServiceProvider.provide();
		final Connection feedFetchConnection = databaseService.getNamedConnection(DatabaseType.DatabaseTypeFeedFetch.toString());

		final String getFeedFetchQuery = String.format("SELECT * FROM `rank_fetch` WHERE `rank_fetch_id`=%d LIMIT 1", id.longValue());
		try {
			feedFetchConnection.connect();
			feedFetchConnection.executeQuery(getFeedFetchQuery);

			if (feedFetchConnection.fetchNextRow()) {
				feedFetch = toFeedFetch(feedFetchConnection);
			}
		} finally {
			if (feedFetchConnection != null) {
				feedFetchConnection.disconnect();
			}
		}
		return feedFetch;
	}

	/**
	 * To feedFetch
	 *
	 * @param connection
	 * @return
	 * @throws DataAccessException
	 */
	private FeedFetch toFeedFetch(Connection connection) throws DataAccessException {
		final FeedFetch feedFetch = new FeedFetch();

		//		feedFetch.id = connection.getCurrentRowLong("id");
		//		feedFetch.created = connection.getCurrentRowDateTime("created");
		//		feedFetch.deleted = connection.getCurrentRowString("deleted");
		//
		//		feedFetch.code = connection.getCurrentRowLong("code2");
		//		feedFetch.country = stripslashes(connection.getCurrentRowString("country"));
		//		feedFetch.data = stripslashes(connection.getCurrentRowString("data"));
		//		feedFetch.date = connection.getCurrentRowDateTime("date");
		//		feedFetch.store = stripslashes(connection.getCurrentRowString("store"));
		//		feedFetch.type = stripslashes(connection.getCurrentRowString("type"));
		//		feedFetch.status = FeedFetchStatusType.fromString(connection.getCurrentRowString("status"));
		//
		//		feedFetch.category = new Category();
		//		feedFetch.category.id = connection.getCurrentRowLong("categoryid");


		feedFetch.id = connection.getCurrentRowLong("rank_fetch_id");
		feedFetch.created = connection.getCurrentRowDateTime("fetch_date");
		feedFetch.date = feedFetch.created;

		feedFetch.category = new Category();
		feedFetch.category.id = connection.getCurrentRowLong("category");

		feedFetch.country = connection.getCurrentRowString("country");
		feedFetch.data = connection.getCurrentRowString("url");
		feedFetch.store="ios";
		feedFetch.type = getOldTypeFromDBType(connection.getCurrentRowString("type"), connection.getCurrentRowString("platform"));
		feedFetch.status = FeedFetchStatusType.fromString(connection.getCurrentRowString("status"));
		feedFetch.code = connection.getCurrentRowLong("group_fetch_code");

		return feedFetch;
	}

	@Override
	public FeedFetch addFeedFetch(FeedFetch feedFetch) throws DataAccessException {
		FeedFetch addedFeedFetch = null;

		final String addFeedFetchQuery = String
				.format("INSERT INTO `feedfetch` (`country`,`data`,`date`,`store`,`type`,`categoryid`,`code2`, `oldkey`) VALUES ('%s','%s',FROM_UNIXTIME(%d),'%s','%s',%d,%d,%s)",
						addslashes(feedFetch.country), addslashes(feedFetch.data), feedFetch.date.getTime() / 1000, addslashes(feedFetch.store),
						addslashes(feedFetch.type), feedFetch.category.id.longValue(), feedFetch.code.longValue(),
						SystemProperty.environment.value() == SystemProperty.Environment.Value.Development ? "-1" : "NULL");

		final Connection feedFetchConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeFeedFetch.toString());

		String insertQuery = "INSERT INTO `rank_fetch` (`group_fetch_code`, `fetch_date`, `fetch_time`, `country`, `category`, `type`, `platform`, "
				+ "`url`, `data_format`, `status`) VALUES (?,?,?, ?,?,?,?, ?,?,?)";

		try (PreparedStatement stat = feedFetchConnection.getRealConnection().prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
			feedFetchConnection.connect();
			feedFetchConnection.executeQuery(addFeedFetchQuery);

			String status = feedFetch.status == null ? null : feedFetch.status.toString().toUpperCase();

			stat.setLong(1, feedFetch.code);
			stat.setDate(2, new java.sql.Date(feedFetch.date.getTime()));
			stat.setTime(3, new Time(feedFetch.date.getTime()));
			stat.setString(4, feedFetch.country);
			stat.setLong(5, feedFetch.category.id);
			stat.setString(6, getDBTypeForFeedFetchType(feedFetch.type));
			stat.setString(7, getDBPlatformForFeedFetchType(feedFetch.type));
			stat.setString(8, feedFetch.data);
			stat.setString(9, "JSON");
			stat.setString(10, status);

			stat.executeUpdate();
			ResultSet generatedKeys = stat.getGeneratedKeys();
			if (generatedKeys != null) {
				if (generatedKeys.next()) {
					feedFetch.id = generatedKeys.getLong(1);
				}
			}

			addedFeedFetch = feedFetch;
		} catch (SQLException e) {
			LOG.log(Level.SEVERE, "Exception occured while executing prepared statement", e);
		} finally {
			if (feedFetchConnection != null) {
				feedFetchConnection.disconnect();
			}
		}

		return addedFeedFetch;
	}

	@Override
	public FeedFetch updateFeedFetch(FeedFetch feedFetch) throws DataAccessException {
		FeedFetch updatedFeedFetch = null;

		final String updateFeedFetchQuery = String
				.format("UPDATE `feedfetch` SET `country`='%s',`data`='%s',`date`=FROM_UNIXTIME(%d),`store`='%s',`type`='%s',`categoryid`=%d,`code2`=%d,`status`='%s' WHERE `id`=%d",
						addslashes(feedFetch.country), addslashes(feedFetch.data), feedFetch.date.getTime() / 1000, addslashes(feedFetch.store),
						addslashes(feedFetch.type), feedFetch.category.id.longValue(), feedFetch.code.longValue(), feedFetch.status.toString(),
						feedFetch.id.longValue());

		final Connection feedFetchConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeFeedFetch.toString());

		String updateQuery = "UPDATE `rank_fetch` set `group_fetch_code`=?, `fetch_date`=?, `fetch_time`=?, `country`=?, `category`=?, `type`=?, `platform`=?, "
				+ "`url`=?, `status`=? where `rank_fetch_id`=?";

		try (PreparedStatement stat = feedFetchConnection.getRealConnection().prepareStatement(updateQuery, Statement.NO_GENERATED_KEYS)) {

			feedFetchConnection.connect();
			feedFetchConnection.executeQuery(updateFeedFetchQuery);

			stat.setLong(1, feedFetch.code);
			stat.setDate(2, new java.sql.Date(feedFetch.date.getTime()));
			stat.setTime(3, new Time(feedFetch.date.getTime()));
			stat.setString(4, feedFetch.country);
			stat.setLong(5, feedFetch.category.id);
			stat.setString(6, getDBTypeForFeedFetchType(feedFetch.type));
			stat.setString(7, getDBPlatformForFeedFetchType(feedFetch.type));
			stat.setString(8, feedFetch.data);
			stat.setString(9, feedFetch.status.toString().toUpperCase());
			stat.setLong(10, feedFetch.id);

			stat.executeUpdate();

			updatedFeedFetch = feedFetch;

		} catch (SQLException e) {
			LOG.log(Level.SEVERE, "Exception occured while executing prepared statement", e);
		} finally {
			if (feedFetchConnection != null) {
				feedFetchConnection.disconnect();
			}
		}

		return updatedFeedFetch;
	}

	@Override
	public void deleteFeedFetch(FeedFetch feedFetch) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<FeedFetch> getFeedFetches(Country country, Store store, Category category, Collection<String> types, Pager pager) throws DataAccessException {
		final List<FeedFetch> feedFetches = new ArrayList<FeedFetch>();

		StringBuilder typeQueryParts = new StringBuilder();
		for (int i = 0; i < types.size(); i++) {
			if (i > 0) {
				typeQueryParts.append(" OR ");
			}

			typeQueryParts.append("(type=? and platform=?)");
		}

		String selectQuery = String.format("SELECT * FROM rank_fetch WHERE country=? AND category=? AND ( %s ) ORDER BY ? ? LIMIT ? ?", typeQueryParts);

		final Connection feedFetchConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeFeedFetch.toString());

		try (PreparedStatement stat = feedFetchConnection.getRealConnection().prepareStatement(selectQuery, ResultSet.TYPE_FORWARD_ONLY,
				ResultSet.CONCUR_READ_ONLY)) {
			feedFetchConnection.connect();

			int paramCount = 1;

			stat.setString(paramCount++, country.a2Code);
			stat.setLong(paramCount++, category.id);

			for (String type : types) {
				String dbType = getDBTypeForFeedFetchType(type);
				String dbPlatform = getDBPlatformForFeedFetchType(type);

				stat.setString(paramCount++, dbType);
				stat.setString(paramCount++, dbPlatform);
			}

			stat.setString(paramCount++, pager.sortBy == null ? "rank_fetch_id" : pager.sortBy);
			stat.setString(paramCount++, pager.sortDirection == null ? "ASC" : pager.sortDirection == SortDirectionType.SortDirectionTypeAscending ? "ASC"
					: "DESC");

			stat.setLong(paramCount++, pager.start == null ? 0 : pager.start);
			stat.setLong(paramCount++, pager.count == null ? 10 : pager.count);

			feedFetchConnection.executePreparedStatement(stat);

			while (feedFetchConnection.fetchNextRow()) {
				final FeedFetch feedFetch = toFeedFetch(feedFetchConnection);

				if (feedFetch != null) {
					feedFetches.add(feedFetch);
				}
			}
		} catch (SQLException e) {
			LOG.log(Level.SEVERE, "Exception occured while executing prepared statement", e);
		} finally {
			if (feedFetchConnection != null) {
				feedFetchConnection.disconnect();
			}
		}

		return feedFetches;
	}

	@Override
	public Long getFeedFetchesCount(Country country, Store store, Category category, Collection<String> types) throws DataAccessException {
		Long feedFetchesCount = Long.valueOf(0);

		StringBuilder typeQueryParts = new StringBuilder();
		for (int i = 0; i < types.size(); i++) {
			if (i > 0) {
				typeQueryParts.append(" OR ");
			}

			typeQueryParts.append("(type=? and platform=?)");
		}

		String selectQuery = String.format("SELECT count(*) as count FROM rank_fetch WHERE country=? AND category=? AND ( %s )", typeQueryParts);

		final Connection feedFetchConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeFeedFetch.toString());

		try (PreparedStatement stat = feedFetchConnection.getRealConnection().prepareStatement(selectQuery, ResultSet.TYPE_FORWARD_ONLY,
				ResultSet.CONCUR_READ_ONLY)) {
			feedFetchConnection.connect();

			int paramCount = 1;

			stat.setString(paramCount++, country.a2Code);
			stat.setLong(paramCount++, category.id);

			for (String type : types) {
				String dbType = getDBTypeForFeedFetchType(type);
				String dbPlatform = getDBPlatformForFeedFetchType(type);

				stat.setString(paramCount++, dbType);
				stat.setString(paramCount++, dbPlatform);
			}

			feedFetchConnection.executePreparedStatement(stat);

			if (feedFetchConnection.fetchNextRow()) return feedFetchConnection.getCurrentRowLong("count");
		} catch (SQLException e) {
			LOG.log(Level.SEVERE, "Exception occured while executing prepared statement", e);
		} finally {
			if (feedFetchConnection != null) {
				feedFetchConnection.disconnect();
			}
		}

		return feedFetchesCount;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.service.fetchfeed.IFeedFetchService#getIngestableFeedFetchIds( io.reflection.app.shared.datatypes.Country,
	 * io.reflection.app.shared.datatypes.Store,java.lang.String, java.lang.Long)
	 */
	@Override
	public List<Long> getIngestableFeedFetchIds(Country country, Store store, String type, Long code) throws DataAccessException {
		final List<Long> feedFetchIds = new ArrayList<Long>();

		String selectQuery = "SELECT rank_fetch_id from rank_fetch where group_fetch_code=? AND country=? AND type=? AND platform=?";

		final Connection feedFetchConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeFeedFetch.toString());

		try (PreparedStatement pstat = feedFetchConnection.getRealConnection().prepareStatement(selectQuery, ResultSet.TYPE_FORWARD_ONLY,
				ResultSet.CONCUR_READ_ONLY)) {
			feedFetchConnection.connect();

			pstat.setLong(1, code == null ? 0L : code);
			pstat.setString(2, country.a2Code);
			pstat.setString(3, getDBTypeForFeedFetchType(type));
			pstat.setString(4, getDBPlatformForFeedFetchType(type));

			feedFetchConnection.executePreparedStatement(pstat);

			while (feedFetchConnection.fetchNextRow()) {
				final Long id = feedFetchConnection.getCurrentRowLong("rank_fetch_id");

				if (id != null) {
					feedFetchIds.add(id);
				}
			}
		} catch (SQLException e) {
			LOG.log(Level.SEVERE, "Exception occured while executing prepared statement", e);
		} finally {
			if (feedFetchConnection != null) {
				feedFetchConnection.disconnect();
			}
		}

		return feedFetchIds;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.service.feedfetch.IFeedFetchService#getFeedFetchIdsForDateWithStatus(java.util.Date,
	 * io.reflection.app.datatypes.shared.FeedFetchStatusType)
	 */
	@Override
	public List<Long> getFeedFetchIdsForDateWithStatus(Date fetchForDate, FeedFetchStatusType statusType) throws DataAccessException {
		final List<Long> feedFetchIds = new ArrayList<Long>();

		String selectQuery = "SELECT rank_fetch_id from rank_fetch where fetch_date=? AND status=?";

		final Connection feedFetchConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeFeedFetch.toString());

		try (PreparedStatement pstat = feedFetchConnection.getRealConnection().prepareStatement(selectQuery, ResultSet.TYPE_FORWARD_ONLY,
				ResultSet.CONCUR_READ_ONLY)) {
			feedFetchConnection.connect();

			pstat.setDate(1, new java.sql.Date(fetchForDate.getTime()));
			pstat.setString(2, statusType.toString().toUpperCase());

			feedFetchConnection.executePreparedStatement(pstat);

			while (feedFetchConnection.fetchNextRow()) {
				final Long id = feedFetchConnection.getCurrentRowLong("rank_fetch_id");

				if (id != null) {
					feedFetchIds.add(id);
				}
			}
		} catch (SQLException e) {
			LOG.log(Level.SEVERE, "Exception occured while executing prepared statement", e);
		} finally {
			if (feedFetchConnection != null) {
				feedFetchConnection.disconnect();
			}
		}

		return feedFetchIds;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.service.fetchfeed.IFeedFetchService#getGatherCodeFeedFetches( io.reflection.app.shared.datatypes.Country,
	 * io.reflection.app.shared.datatypes.Store,java.util.Collection, java.lang.Long)
	 */
	@Override
	public List<FeedFetch> getGatherCodeFeedFetches(Country country, Store store, Collection<String> types, Long code) throws DataAccessException {
		final List<FeedFetch> feedFetches = new ArrayList<FeedFetch>();

		StringBuilder typeQueryParts = new StringBuilder();
		for (int i = 0; i < types.size(); i++) {
			if (i > 0) {
				typeQueryParts.append(" OR ");
			}

			typeQueryParts.append("(type=? and platform=?)");
		}

		String selectQuery = String.format("SELECT * FROM rank_fetch WHERE group_fetch_code=? AND country=? AND ( %s )", typeQueryParts);

		final Connection feedFetchConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeFeedFetch.toString());

		try (PreparedStatement stat = feedFetchConnection.getRealConnection().prepareStatement(selectQuery, ResultSet.TYPE_FORWARD_ONLY,
				ResultSet.CONCUR_READ_ONLY)) {
			feedFetchConnection.connect();

			int paramCount = 1;

			stat.setLong(paramCount++, code);
			stat.setString(paramCount++, country.a2Code);

			for (String type : types) {
				String dbType = getDBTypeForFeedFetchType(type);
				String dbPlatform = getDBPlatformForFeedFetchType(type);

				stat.setString(paramCount++, dbType);
				stat.setString(paramCount++, dbPlatform);
			}

			feedFetchConnection.executePreparedStatement(stat);

			while (feedFetchConnection.fetchNextRow()) {
				final FeedFetch feedFetch = toFeedFetch(feedFetchConnection);

				if (feedFetch != null) {
					feedFetches.add(feedFetch);
				}
			}
		} catch (SQLException e) {
			LOG.log(Level.SEVERE, "Exception occured while executing prepared statement", e);
		} finally {
			if (feedFetchConnection != null) {
				feedFetchConnection.disconnect();
			}
		}

		return feedFetches;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.service.feedfetch.IFeedFetchService#getCode()
	 */
	@Override
	public Long getCode() throws DataAccessException {
		Long code = null;

		final String getCodeQuery = "INSERT INTO `feedfetchcode` (`id`) VALUES (DEFAULT)";

		final Connection feedFetchConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeFeedFetch.toString());

		try {
			feedFetchConnection.connect();
			feedFetchConnection.executeQuery(getCodeQuery);

			if (feedFetchConnection.getAffectedRowCount() > 0) {
				code = Long.valueOf(feedFetchConnection.getInsertedId());
			}
		} finally {
			if (feedFetchConnection != null) {
				feedFetchConnection.disconnect();
			}
		}

		return code;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.service.feedfetch.IFeedFetchService#getGatherCode(io.reflection.app.datatypes.shared.Country,
	 * io.reflection.app.datatypes.shared.Store, java.util.Date, java.util.Date)
	 */
	@Override
	public Long getGatherCode(Country country, Store store, Date after, Date before) throws DataAccessException {
		String selectQuery = "SELECT group_fetch_code FROM rank_fetch where fetch_date BETWEEN ? AND ? AND country=? LIMIT 1";

		final Connection feedFetchConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeFeedFetch.toString());

		try (PreparedStatement pstat = feedFetchConnection.getRealConnection().prepareStatement(selectQuery, ResultSet.TYPE_FORWARD_ONLY,
				ResultSet.CONCUR_READ_ONLY)) {
			feedFetchConnection.connect();

			pstat.setDate(1, new java.sql.Date(after.getTime()));
			pstat.setDate(2, new java.sql.Date(before.getTime()));
			pstat.setString(3, country.a2Code);

			feedFetchConnection.executePreparedStatement(pstat);

			if (feedFetchConnection.fetchNextRow()) return feedFetchConnection.getCurrentRowLong("group_fetch_code");
		} catch (SQLException e) {
			LOG.log(Level.SEVERE, "Exception occured while executing prepared statement", e);
		} finally {
			if (feedFetchConnection != null) {
				feedFetchConnection.disconnect();
			}
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see io.reflection.app.service.feedfetch.IFeedFetchService#getDatesFeedFetches(io.reflection.app.datatypes.shared.Country,
	 * io.reflection.app.datatypes.shared.Store, io.reflection.app.datatypes.shared.Category, java.util.Collection, java.util.Date, java.util.Date)
	 */
	@Override
	public List<FeedFetch> getDatesFeedFetches(Country country, Store store, Category category, Collection<String> types, Date after, Date before)
			throws DataAccessException {
		final List<FeedFetch> feedFetches = new ArrayList<FeedFetch>();

		StringBuilder typeQueryParts = new StringBuilder();
		for (int i = 0; i < types.size(); i++) {
			if (i > 0) {
				typeQueryParts.append(" OR ");
			}

			typeQueryParts.append("(type=? and platform=?)");
		}

		String selectQuery = String.format("SELECT * FROM rank_fetch WHERE fetch_date BETWEEN ? AND ? AND country=? AND category=? AND ( %s )", typeQueryParts);

		final Connection feedFetchConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeFeedFetch.toString());

		try (PreparedStatement stat = feedFetchConnection.getRealConnection().prepareStatement(selectQuery, ResultSet.TYPE_FORWARD_ONLY,
				ResultSet.CONCUR_READ_ONLY)) {
			feedFetchConnection.connect();

			int paramCount = 1;

			stat.setDate(paramCount++, new java.sql.Date(after.getTime()));
			stat.setDate(paramCount++, new java.sql.Date(before.getTime()));
			stat.setString(paramCount++, country.a2Code);
			stat.setLong(paramCount++, category.id);

			for (String type : types) {
				String dbType = getDBTypeForFeedFetchType(type);
				String dbPlatform = getDBPlatformForFeedFetchType(type);

				stat.setString(paramCount++, dbType);
				stat.setString(paramCount++, dbPlatform);
			}

			feedFetchConnection.executePreparedStatement(stat);

			while (feedFetchConnection.fetchNextRow()) {
				final FeedFetch feedFetch = toFeedFetch(feedFetchConnection);

				if (feedFetch != null) {
					feedFetches.add(feedFetch);
				}
			}
		} catch (SQLException e) {
			LOG.log(Level.SEVERE, "Exception occured while executing prepared statement", e);
		} finally {
			if (feedFetchConnection != null) {
				feedFetchConnection.disconnect();
			}
		}

		return feedFetches;
	}


	public static String getDBTypeForFeedFetchType(String type) {
		if (type == null) return "FREE";

		if (type.contains("free")) return "FREE";
		else if (type.contains("paid")) return "PAID";
		else return "GROSSING";
	}

	public static String getDBPlatformForFeedFetchType(String type) {
		if (type == null) return "PHONE";

		if (type.contains("ipad")) return "TABLET";
		else return "PHONE";
	}

	private String getOldTypeFromDBType(String type, String platform) {
		if ("PHONE".equalsIgnoreCase(platform)) {
			if ("FREE".equalsIgnoreCase(type)) return "topfreeapplications";
			else if ("PAID".equalsIgnoreCase(type)) return "toppaidapplications";
			else return "topgrossingapplications";
		} else {
			if ("FREE".equalsIgnoreCase(type)) return "topfreeipadapplications";
			else if ("PAID".equalsIgnoreCase(type)) return "toppaidipadapplications";
			else return "topgrossingipadapplications";
		}
	}
}
