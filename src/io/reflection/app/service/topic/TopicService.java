//  
//  TopicService.java
//  reflection.io
//
//  Created by William Shakour on April 5, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.service.topic;

import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.datatypes.shared.Topic;
import io.reflection.app.repackaged.scphopr.cloudsql.Connection;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseServiceProvider;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseType;
import io.reflection.app.repackaged.scphopr.service.database.IDatabaseService;
import io.reflection.app.service.ServiceType;

final class TopicService implements ITopicService {
	public String getName() {
		return ServiceType.ServiceTypeTopic.toString();
	}

	@Override
	public Topic getTopic(Long id) throws DataAccessException {
		Topic topic = null;

		IDatabaseService databaseService = DatabaseServiceProvider.provide();
		Connection topicConnection = databaseService.getNamedConnection(DatabaseType.DatabaseTypeTopic.toString());

		String getTopicQuery = String.format("select * from `topic` where `deleted`='n' and `id`='%d' limit 1", id.longValue());
		try {
			topicConnection.connect();
			topicConnection.executeQuery(getTopicQuery);

			if (topicConnection.fetchNextRow()) {
				topic = toTopic(topicConnection);
			}
		} finally {
			if (topicConnection != null) {
				topicConnection.disconnect();
			}
		}
		return topic;
	}

	/**
	 * To topic
	 * 
	 * @param connection
	 * @return
	 */
	private Topic toTopic(Connection connection) throws DataAccessException {
		Topic topic = new Topic();
		topic.id = connection.getCurrentRowLong("id");
		return topic;
	}

	@Override
	public Topic addTopic(Topic topic) throws DataAccessException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Topic updateTopic(Topic topic) throws DataAccessException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteTopic(Topic topic) throws DataAccessException {
		throw new UnsupportedOperationException();
	}

}