//  
//  TopicService.java
//  reflection.io
//
//  Created by William Shakour on April 5, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.service.topic;

import static com.spacehopperstudios.utility.StringUtils.stripslashes;
import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.datatypes.shared.Forum;
import io.reflection.app.datatypes.shared.Topic;
import io.reflection.app.repackaged.scphopr.cloudsql.Connection;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseServiceProvider;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseType;
import io.reflection.app.repackaged.scphopr.service.database.IDatabaseService;
import io.reflection.app.service.ServiceType;
import io.reflection.app.service.user.UserServiceProvider;
import io.reflection.app.shared.util.TagHelper;

final class TopicService implements ITopicService {
	public String getName() {
		return ServiceType.ServiceTypeTopic.toString();
	}

	@Override
	public Topic getTopic(Long id) throws DataAccessException {
		Topic topic = null;

		IDatabaseService databaseService = DatabaseServiceProvider.provide();
		Connection topicConnection = databaseService.getNamedConnection(DatabaseType.DatabaseTypeTopic.toString());

		String getTopicQuery = String.format("SELECT * FROM `topic` WHERE `deleted`='n' AND `id`='%d' LIMIT 1", id.longValue());
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
		topic.created = connection.getCurrentRowDateTime("created");
		topic.deleted = connection.getCurrentRowString("deleted");

		Long authorId = connection.getCurrentRowLong("authorid");
		topic.author = UserServiceProvider.provide().getUser(authorId);

		topic.title = connection.getCurrentRowString("title");
		topic.content = connection.getCurrentRowString("content");
		topic.flagged = connection.getCurrentRowInteger("flagged");

		topic.forum = new Forum();
		topic.forum.id = connection.getCurrentRowLong("forumid");

		topic.heat = connection.getCurrentRowInteger("heat");

		Long lastReplierId = connection.getCurrentRowLong("lastreplierid");

		if (lastReplierId != null) {
			topic.lastReplier = UserServiceProvider.provide().getUser(lastReplierId);
		}

		topic.lastReplied = connection.getCurrentRowDateTime("lastreplied");

		String lockedString = connection.getCurrentRowString("locked");
		topic.locked = lockedString == null ? Boolean.FALSE : (lockedString.equalsIgnoreCase("y") ? Boolean.TRUE : Boolean.FALSE);
		
		topic.numberOfReplies = connection.getCurrentRowInteger("numberofreplies");
		
		String stickyString = connection.getCurrentRowString("sticky");
		topic.sticky = stickyString == null ? Boolean.FALSE : (stickyString.equalsIgnoreCase("y") ? Boolean.TRUE : Boolean.FALSE);
		
		topic.tags = TagHelper.convertToTagList(stripslashes(connection.getCurrentRowString("tags")));

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