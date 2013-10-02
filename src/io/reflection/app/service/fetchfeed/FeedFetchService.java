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

import com.google.appengine.api.utils.SystemProperty;

import io.reflection.app.repackaged.scphopr.cloudsql.Connection;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseServiceProvider;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseType;
import io.reflection.app.repackaged.scphopr.service.database.IDatabaseService;
import io.reflection.app.service.ServiceType;
import io.reflection.app.shared.datatypes.FeedFetch;

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

}