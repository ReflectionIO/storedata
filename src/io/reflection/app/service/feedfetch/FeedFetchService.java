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
import static com.spacehopperstudios.utility.StringUtils.stripslashes;
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

import java.util.ArrayList;
import java.util.List;

import com.google.appengine.api.utils.SystemProperty;
import com.spacehopperstudios.utility.StringUtils;

final class FeedFetchService implements IFeedFetchService {
	public String getName() {
		return ServiceType.ServiceTypeFeedFetch.toString();
	}

	@Override
	public FeedFetch getFeedFetch(Long id) throws DataAccessException {
		FeedFetch feedFetch = null;

		IDatabaseService databaseService = DatabaseServiceProvider.provide();
		Connection feedFetchConnection = databaseService.getNamedConnection(DatabaseType.DatabaseTypeFeedFetch.toString());

		String getFeedFetchQuery = String.format("SELECT * FROM `feedfetch` WHERE `deleted`='n' AND `id`='%d' LIMIT 1", id.longValue());
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
		FeedFetch feedFetch = new FeedFetch();

		feedFetch.id = connection.getCurrentRowLong("id");
		feedFetch.created = connection.getCurrentRowDateTime("created");
		feedFetch.deleted = connection.getCurrentRowString("deleted");

		feedFetch.code = stripslashes(connection.getCurrentRowString("code"));
		feedFetch.country = stripslashes(connection.getCurrentRowString("country"));
		feedFetch.data = stripslashes(connection.getCurrentRowString("data"));
		feedFetch.date = connection.getCurrentRowDateTime("date");
		feedFetch.store = stripslashes(connection.getCurrentRowString("store"));
		feedFetch.type = stripslashes(connection.getCurrentRowString("type"));
		feedFetch.status = FeedFetchStatusType.fromString(connection.getCurrentRowString("status"));

		feedFetch.category = new Category();
		feedFetch.category.id = connection.getCurrentRowLong("categoryid");

		return feedFetch;
	}

	@Override
	public FeedFetch addFeedFetch(FeedFetch feedFetch) throws DataAccessException {
		FeedFetch addedFeedFetch = null;

		final String addFeedFetchQuery = String
				.format("INSERT INTO `feedfetch` (`country`,`data`,`date`,`store`,`type`,`categoryid`,`code`, `oldkey`) VALUES ('%s','%s',FROM_UNIXTIME(%d),'%s','%s',%d,'%s',%s)",
						addslashes(feedFetch.country), addslashes(feedFetch.data), feedFetch.date.getTime() / 1000, addslashes(feedFetch.store),
						addslashes(feedFetch.type), feedFetch.category.id.longValue(), addslashes(feedFetch.code),
						SystemProperty.environment.value() == SystemProperty.Environment.Value.Development ? "-1" : "NULL");

		Connection feedFetchConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeFeedFetch.toString());

		try {
			feedFetchConnection.connect();
			feedFetchConnection.executeQuery(addFeedFetchQuery);

			if (feedFetchConnection.getAffectedRowCount() > 0) {
				addedFeedFetch = getFeedFetch(Long.valueOf(feedFetchConnection.getInsertedId()));

				if (addedFeedFetch == null) {
					addedFeedFetch = feedFetch;
					addedFeedFetch.id = Long.valueOf(feedFetchConnection.getInsertedId());
				}
			}
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
				.format("UPDATE `feedfetch` SET `country`='%s',`data`='%s',`date`=FROM_UNIXTIME(%d),`store`='%s',`type`='%s',`categoryid`=%d,`code`='%s',`status`='%s' WHERE `id`=%d",
						addslashes(feedFetch.country), addslashes(feedFetch.data), feedFetch.date.getTime() / 1000, addslashes(feedFetch.store),
						addslashes(feedFetch.type), feedFetch.id.longValue(), addslashes(feedFetch.code), feedFetch.status.toString(), feedFetch.id.longValue());

		Connection feedFetchConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeFeedFetch.toString());

		try {
			feedFetchConnection.connect();
			feedFetchConnection.executeQuery(updateFeedFetchQuery);

			if (feedFetchConnection.getAffectedRowCount() > 0) {
				updatedFeedFetch = getFeedFetch(feedFetch.id);
			} else {
				updatedFeedFetch = feedFetch;
			}
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.fetchfeed.IFeedFetchService#getFeedFetches( io.reflection.app.shared.datatypes.Country,
	 * io.reflection.app.shared.datatypes.Store,java.util.List, io.reflection.app.api.shared.datatypes.Pager)
	 */
	@Override
	public List<FeedFetch> getFeedFetches(Country country, Store store, List<String> types, Pager pager) throws DataAccessException {
		List<FeedFetch> feedFetches = new ArrayList<FeedFetch>();

		String typesQueryPart = null;
		if (types.size() == 1) {
			typesQueryPart = String.format("`type`='%s'", types.get(0));
		} else {
			typesQueryPart = "`type` IN ('" + StringUtils.join(types, "','") + "')";
		}

		String getFeedFetchesQuery = String.format(
				"SELECT * FROM `feedfetch` WHERE `store`='%s' AND `country`='%s' AND %s AND `deleted`='n' ORDER BY `%s` %s LIMIT %d,%d",
				addslashes(store.a3Code), addslashes(country.a2Code), typesQueryPart == null ? "" : typesQueryPart, pager.sortBy,
				pager.sortDirection == SortDirectionType.SortDirectionTypeAscending ? "ASC" : "DESC", pager.start, pager.count);

		Connection feedFetchConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeFeedFetch.toString());

		try {
			feedFetchConnection.connect();
			feedFetchConnection.executeQuery(getFeedFetchesQuery);

			while (feedFetchConnection.fetchNextRow()) {
				FeedFetch feedFetch = toFeedFetch(feedFetchConnection);

				if (feedFetch != null) {
					feedFetches.add(feedFetch);
				}
			}
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
	 * @see io.reflection.app.service.fetchfeed.IFeedFetchService#getFeedFetchesCount( io.reflection.app.shared.datatypes.Country,
	 * io.reflection.app.shared.datatypes.Store,java.util.List)
	 */
	@Override
	public Long getFeedFetchesCount(Country country, Store store, List<String> types) throws DataAccessException {
		Long feedFetchesCount = Long.valueOf(0);

		String typesQueryPart = null;
		if (types.size() == 1) {
			typesQueryPart = String.format("`type`='%s'", types.get(0));
		} else {
			typesQueryPart = "`type` IN ('" + StringUtils.join(types, "','") + "')";
		}

		String getFeedFetchesCountQuery = String.format(
				"SELECT count(1) as `count` FROM `feedfetch` WHERE `store`='%s' AND `country`='%s' AND %s AND `deleted`='n'", addslashes(store.a3Code),
				addslashes(country.a2Code), typesQueryPart == null ? "" : typesQueryPart);

		Connection feedFetchConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeFeedFetch.toString());

		try {
			feedFetchConnection.connect();
			feedFetchConnection.executeQuery(getFeedFetchesCountQuery);

			if (feedFetchConnection.fetchNextRow()) {
				feedFetchesCount = feedFetchConnection.getCurrentRowLong("count");
			}
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
	 * @see io.reflection.app.service.fetchfeed.IFeedFetchService#getIngestedFeedFetches( io.reflection.app.shared.datatypes.Country,
	 * io.reflection.app.shared.datatypes.Store,java.util.List, io.reflection.app.api.shared.datatypes.Pager)
	 */
	@Override
	public List<FeedFetch> getIngestedFeedFetches(Country country, Store store, List<String> types, Pager pager) throws DataAccessException {
		return getStatusFeedFetches(country, store, types, pager, true);
	}

	/**
	 * @param store
	 * @param country
	 * @param pager
	 * @param b
	 * @return
	 * @throws DataAccessException
	 */
	private List<FeedFetch> getStatusFeedFetches(Country country, Store store, List<String> types, Pager pager, boolean ingested) throws DataAccessException {
		List<FeedFetch> feedFetches = new ArrayList<FeedFetch>();

		String typesQueryPart = null;
		if (types.size() == 1) {
			typesQueryPart = String.format("`type`='%s'", types.get(0));
		} else {
			typesQueryPart = "`type` IN ('" + StringUtils.join(types, "','") + "')";
		}

		String getIngestedFeedFetchesQuery = String
				.format("SELECT `f`.* FROM `feedfetch` `f` LEFT JOIN `rank` `r` ON `f`.`code`=`r`.`code` WHERE `f`.`store`='%s' AND `f`.`country`='%s' AND `f`.%s AND `r`.`code` IS%sNULL AND `f`.`deleted`='n' ORDER BY `f`.`date` DESC LIMIT %d,%d",
						store.a3Code, country.a2Code, typesQueryPart, ingested ? " NOT " : " ", pager.start, pager.count);

		Connection feedFetchConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeFeedFetch.toString());

		try {
			feedFetchConnection.connect();
			feedFetchConnection.executeQuery(getIngestedFeedFetchesQuery);

			while (feedFetchConnection.fetchNextRow()) {
				FeedFetch feedFetch = toFeedFetch(feedFetchConnection);

				if (feedFetch != null) {
					feedFetches.add(feedFetch);
				}
			}
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
	 * @see io.reflection.app.service.fetchfeed.IFeedFetchService#getIngestedFeedFetchesCount( io.reflection.app.shared.datatypes.Country,
	 * io.reflection.app.shared.datatypes.Store,java.util.List)
	 */
	@Override
	public Long getIngestedFeedFetchesCount(Country country, Store store, List<String> types) throws DataAccessException {
		return getStatusFeedFetchesCount(country, store, types, true);
	}

	/**
	 * @param store
	 * @param country
	 * @param types
	 * @param b
	 * @return
	 * @throws DataAccessException
	 */
	private Long getStatusFeedFetchesCount(Country country, Store store, List<String> types, boolean ingested) throws DataAccessException {
		Long feedFetchesCount = Long.valueOf(0);

		String typesQueryPart = null;
		if (types.size() == 1) {
			typesQueryPart = String.format("`type`='%s'", types.get(0));
		} else {
			typesQueryPart = "`type` IN ('" + StringUtils.join(types, "','") + "')";
		}

		String getIngestedFeedFetchesQuery = String
				.format("SELECT count(1) as `count` FROM `feedfetch` `f` LEFT JOIN `rank` `r` ON `f`.`code`=`r`.`code` WHERE `f`.`store`='%s' AND `f`.`country`='%s' AND `f`.%s AND `r`.`code` IS%sNULL AND `f`.`deleted`='n'",
						store.a3Code, country.a2Code, typesQueryPart, ingested ? " NOT " : " ");

		Connection feedFetchConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeFeedFetch.toString());

		try {
			feedFetchConnection.connect();
			feedFetchConnection.executeQuery(getIngestedFeedFetchesQuery);

			if (feedFetchConnection.fetchNextRow()) {
				feedFetchesCount = feedFetchConnection.getCurrentRowLong("count");
			}
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
	 * @see io.reflection.app.service.fetchfeed.IFeedFetchService#getUningestedFeedFetches(
	 * io.reflection.app.shared.datatypes.Country,io.reflection.app.shared.datatypes.Store, java.util.List, io.reflection.app.api.shared.datatypes.Pager)
	 */
	@Override
	public List<FeedFetch> getUningestedFeedFetches(Country country, Store store, List<String> types, Pager pager) throws DataAccessException {
		return getStatusFeedFetches(country, store, types, pager, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.fetchfeed.IFeedFetchService#getUningestedFeedFetchesCount( io.reflection.app.shared.datatypes.Country,
	 * io.reflection.app.shared.datatypes.Store,java.util.List)
	 */
	@Override
	public Long getUningestedFeedFetchesCount(Country country, Store store, List<String> types) throws DataAccessException {
		return getStatusFeedFetchesCount(country, store, types, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.fetchfeed.IFeedFetchService#getIngestableFeedFetchIds( io.reflection.app.shared.datatypes.Country,
	 * io.reflection.app.shared.datatypes.Store,java.lang.String, java.lang.String)
	 */
	@Override
	public List<Long> getIngestableFeedFetchIds(Country country, Store store, String type, String code) throws DataAccessException {
		List<Long> feedFetchIds = new ArrayList<Long>();

		final String getIngestableFeedFetchIdsQuery = String.format(
				"SELECT `id` FROM `feedfetch` WHERE `store`='%s' AND `country`='%s' AND `type`='%s' AND `code`='%s'", store.a3Code, country.a2Code, type, code);

		Connection feedFetchConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeFeedFetch.toString());

		try {
			feedFetchConnection.connect();
			feedFetchConnection.executeQuery(getIngestableFeedFetchIdsQuery);

			if (feedFetchConnection.fetchNextRow()) {
				Long id = feedFetchConnection.getCurrentRowLong("id");

				if (id != null) {
					feedFetchIds.add(id);
				}
			}
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
	 * @see io.reflection.app.service.fetchfeed.IFeedFetchService#isReadyToModel( io.reflection.app.shared.datatypes.Country,
	 * io.reflection.app.shared.datatypes.Store,java.util.List, java.lang.String)
	 */
	@Override
	public Boolean isReadyToModel(Country country, Store store, List<String> types, String code) throws DataAccessException {
		Boolean isReadyToModel = Boolean.FALSE;

		String typesQueryPart = null;
		boolean single = false;
		if (types.size() == 1) {
			single = true;
			typesQueryPart = String.format("`type`='%s'", types.get(0));
		} else {
			typesQueryPart = "`type` IN ('" + StringUtils.join(types, "','") + "')";
		}

		String isReadyToModelQuery = String
				.format("SELECT count(1) AS `count` FROM `feedfetch` WHERE `store`='%s' AND `country`='%s' AND %s AND `code`='%s' AND `status`<>'gathered' AND `deleted`='n'",
						stripslashes(store.a3Code), stripslashes(country.a2Code), typesQueryPart, stripslashes(code));

		Connection feedFetchConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeFeedFetch.toString());

		try {
			feedFetchConnection.connect();
			feedFetchConnection.executeQuery(isReadyToModelQuery);

			if (feedFetchConnection.fetchNextRow()) {
				Integer count = feedFetchConnection.getCurrentRowInteger("count");

				if (count != null) {
					if ((count.intValue() == 1 && single) || (count.intValue() == 3 && !single)) {
						isReadyToModel = Boolean.TRUE;
					}
				}
			}
		} finally {
			if (feedFetchConnection != null) {
				feedFetchConnection.disconnect();
			}
		}

		return isReadyToModel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.fetchfeed.IFeedFetchService#getGatherCodeFeedFetches( io.reflection.app.shared.datatypes.Country,
	 * io.reflection.app.shared.datatypes.Store,java.util.List, java.lang.String)
	 */
	@Override
	public List<FeedFetch> getGatherCodeFeedFetches(Country country, Store store, List<String> types, String code) throws DataAccessException {
		List<FeedFetch> feedFetches = new ArrayList<FeedFetch>();

		String typesQueryPart = null;
		if (types.size() == 1) {
			typesQueryPart = String.format("`type`='%s'", types.get(0));
		} else {
			typesQueryPart = "`type` IN ('" + StringUtils.join(types, "','") + "')";
		}

		final String getGatherCodeFeedFetchesQuery = String.format(
				"SELECT * FROM `feedfetch` WHERE `store`='%s' AND `country`='%s' AND %s AND `code`='%s' AND `deleted`='n'", store.a3Code, country.a2Code,
				typesQueryPart, code);

		Connection feedFetchConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeFeedFetch.toString());

		try {
			feedFetchConnection.connect();
			feedFetchConnection.executeQuery(getGatherCodeFeedFetchesQuery);

			while (feedFetchConnection.fetchNextRow()) {
				FeedFetch feedFetch = toFeedFetch(feedFetchConnection);

				if (feedFetch != null) {
					feedFetches.add(feedFetch);
				}
			}

		} finally {
			if (feedFetchConnection != null) {
				feedFetchConnection.disconnect();
			}
		}

		return feedFetches;
	}
}