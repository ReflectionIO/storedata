//  
//  FeedFetchService.java
//  storedata
//
//  Created by William Shakour on September 5, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.service.fetchfeed;

import static com.spacehopperstudios.utility.StringUtils.addslashes;
import static com.spacehopperstudios.utility.StringUtils.stripslashes;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.api.shared.datatypes.SortDirectionType;
import io.reflection.app.repackaged.scphopr.cloudsql.Connection;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseServiceProvider;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseType;
import io.reflection.app.repackaged.scphopr.service.database.IDatabaseService;
import io.reflection.app.service.ServiceType;
import io.reflection.app.shared.datatypes.Country;
import io.reflection.app.shared.datatypes.FeedFetch;
import io.reflection.app.shared.datatypes.Store;

import java.util.ArrayList;
import java.util.List;

import com.google.appengine.api.utils.SystemProperty;
import com.spacehopperstudios.utility.StringUtils;

final class FeedFetchService implements IFeedFetchService {
	public String getName() {
		return ServiceType.ServiceTypeFeedFetch.toString();
	}

	@Override
	public FeedFetch getFeedFetch(Long id) {
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
	 */
	private FeedFetch toFeedFetch(Connection connection) {
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

		return feedFetch;
	}

	@Override
	public FeedFetch addFeedFetch(FeedFetch feedFetch) {
		FeedFetch addedFeedFetch = null;

		final String addFeedFetchQuery = String.format(
				"INSERT INTO `feedfetch` (`country`,`data`,`date`,`store`,`type`,`code`, `oldkey`) VALUES ('%s','%s',FROM_UNIXTIME(%d),'%s','%s','%s', %s)",
				addslashes(feedFetch.country), addslashes(feedFetch.data), feedFetch.date.getTime() / 1000, addslashes(feedFetch.store),
				addslashes(feedFetch.type), addslashes(feedFetch.code),
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
	public FeedFetch updateFeedFetch(FeedFetch feedFetch) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteFeedFetch(FeedFetch feedFetch) {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.fetchfeed.IFeedFetchService#getFeedFetches(io.reflection.app.shared.datatypes.Store,
	 * io.reflection.app.shared.datatypes.Country, java.util.List, io.reflection.app.api.shared.datatypes.Pager)
	 */
	@Override
	public List<FeedFetch> getFeedFetches(Store store, Country country, List<String> types, Pager pager) {
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
	 * @see io.reflection.app.service.fetchfeed.IFeedFetchService#getFeedFetchesCount(io.reflection.app.shared.datatypes.Store,
	 * io.reflection.app.shared.datatypes.Country, java.util.List)
	 */
	@Override
	public Long getFeedFetchesCount(Store store, Country country, List<String> types) {
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
	 * @see io.reflection.app.service.fetchfeed.IFeedFetchService#getIngestedFeedFetches(io.reflection.app.shared.datatypes.Store,
	 * io.reflection.app.shared.datatypes.Country, java.util.List, io.reflection.app.api.shared.datatypes.Pager)
	 */
	@Override
	public List<FeedFetch> getIngestedFeedFetches(Store store, Country country, List<String> types, Pager pager) {
		return getStatusFeedFetches(store, country, types, pager, true);
	}

	/**
	 * @param store
	 * @param country
	 * @param pager
	 * @param b
	 * @return
	 */
	private List<FeedFetch> getStatusFeedFetches(Store store, Country country, List<String> types, Pager pager, boolean ingested) {
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
	 * @see io.reflection.app.service.fetchfeed.IFeedFetchService#getIngestedFeedFetchesCount(io.reflection.app.shared.datatypes.Store,
	 * io.reflection.app.shared.datatypes.Country, java.util.List)
	 */
	@Override
	public Long getIngestedFeedFetchesCount(Store store, Country country, List<String> types) {
		return getStatusFeedFetchesCount(store, country, types, true);
	}

	/**
	 * @param store
	 * @param country
	 * @param types
	 * @param b
	 * @return
	 */
	private Long getStatusFeedFetchesCount(Store store, Country country, List<String> types, boolean ingested) {
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
	 * @see io.reflection.app.service.fetchfeed.IFeedFetchService#getUningestedFeedFetches(io.reflection.app.shared.datatypes.Store,
	 * io.reflection.app.shared.datatypes.Country, java.util.List, io.reflection.app.api.shared.datatypes.Pager)
	 */
	@Override
	public List<FeedFetch> getUningestedFeedFetches(Store store, Country country, List<String> types, Pager pager) {
		return getStatusFeedFetches(store, country, types, pager, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.fetchfeed.IFeedFetchService#getUningestedFeedFetchesCount(io.reflection.app.shared.datatypes.Store,
	 * io.reflection.app.shared.datatypes.Country, java.util.List)
	 */
	@Override
	public Long getUningestedFeedFetchesCount(Store store, Country country, List<String> types) {
		return getStatusFeedFetchesCount(store, country, types, false);
	}

}