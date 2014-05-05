//  
//  TopicService.java
//  reflection.io
//
//  Created by William Shakour on April 5, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.service.topic;

import static com.spacehopperstudios.utility.StringUtils.addslashes;
import static com.spacehopperstudios.utility.StringUtils.join;
import static com.spacehopperstudios.utility.StringUtils.stripslashes;
import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.datatypes.shared.Forum;
import io.reflection.app.datatypes.shared.Topic;
import io.reflection.app.datatypes.shared.User;
import io.reflection.app.logging.GaeLevel;
import io.reflection.app.repackaged.scphopr.cloudsql.Connection;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseServiceProvider;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseType;
import io.reflection.app.repackaged.scphopr.service.database.IDatabaseService;
import io.reflection.app.service.ServiceType;
import io.reflection.app.service.forum.ForumServiceProvider;
import io.reflection.app.service.user.UserServiceProvider;
import io.reflection.app.shared.util.TagHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

final class TopicService implements ITopicService {

	private static final Logger LOG = Logger.getLogger(TopicService.class.getName());

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
		Topic addedTopic = null;

		final String addTopicQuery = String.format(
				"INSERT INTO `topic` (`authorid`,`title`,`content`,`sticky`,`forumid`,`tags`) VALUES (%d,'%s','%s','%s',%d,%s)", topic.author.id.longValue(),
				addslashes(topic.title), addslashes(topic.content), topic.sticky == null || !topic.sticky.booleanValue() ? "n" : "y",
				topic.forum.id.longValue(), topic.tags == null ? "NULL" : "'" + addslashes(join(topic.tags)) + "'");

		Connection topicConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeTopic.toString());

		try {
			topicConnection.connect();
			topicConnection.executeQuery(addTopicQuery);

			if (topicConnection.getAffectedRowCount() > 0) {
				addedTopic = getTopic(Long.valueOf(topicConnection.getInsertedId()));

				if (addedTopic == null) {
					addedTopic = topic;
					addedTopic.id = Long.valueOf(topicConnection.getInsertedId());
				}

				addedTopic.forum = ForumServiceProvider.provide().addTopic(topic.forum);
			}
		} finally {
			if (topicConnection != null) {
				topicConnection.disconnect();
			}
		}

		return addedTopic;
	}

	@Override
	public Topic updateTopic(Topic topic) throws DataAccessException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteTopic(Topic topic) throws DataAccessException {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.topic.ITopicService#getTopics(io.reflection.app.datatypes.shared.Forum, io.reflection.app.api.shared.datatypes.Pager)
	 */
	@Override
	public List<Topic> getTopics(Forum forum, Pager pager) throws DataAccessException {
		List<Topic> topics = new ArrayList<Topic>();

		IDatabaseService databaseService = DatabaseServiceProvider.provide();
		Connection topicConnection = databaseService.getNamedConnection(DatabaseType.DatabaseTypeTopic.toString());

		String getTopicsQuery = String.format("SELECT * FROM `topic` WHERE `forumid`=%d AND `deleted`='n' ORDER BY `sticky` ASC", forum.id.longValue());

		if (pager != null) {
			String sortByQuery = "id";

			if (pager.sortBy != null && ("code".equals(pager.sortBy) || "name".equals(pager.sortBy))) {
				sortByQuery = pager.sortBy;
			}

			String sortDirectionQuery = "DESC";

			if (pager.sortDirection != null) {
				switch (pager.sortDirection) {
				case SortDirectionTypeAscending:
					sortDirectionQuery = "ASC";
					break;
				default:
					break;
				}
			}

			getTopicsQuery += String.format(",`%s` %s", sortByQuery, sortDirectionQuery);
		}

		if (pager.start != null && pager.count != null) {
			getTopicsQuery += String.format(" LIMIT %d, %d", pager.start.longValue(), pager.count.longValue());
		} else if (pager.count != null) {
			getTopicsQuery += String.format(" LIMIT %d", pager.count.longValue());
		}
		try {
			topicConnection.connect();
			topicConnection.executeQuery(getTopicsQuery);

			while (topicConnection.fetchNextRow()) {
				Topic topic = this.toTopic(topicConnection);

				if (topic != null) {
					topics.add(topic);
				}
			}
		} finally {
			if (topicConnection != null) {
				topicConnection.disconnect();
			}
		}

		return topics;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.topic.ITopicService#getTopicsCount(io.reflection.app.datatypes.shared.Forum)
	 */
	@Override
	public Long getTopicsCount(Forum forum) throws DataAccessException {
		Long topicsCount = Long.valueOf(0);

		Connection topicConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeTopic.toString());

		String getTopicsCountQuery = String
				.format("SELECT COUNT(`id`) AS `topiccount` FROM `topic` WHERE `forumid`=%d AND `deleted`='n'", forum.id.longValue());

		try {
			topicConnection.connect();
			topicConnection.executeQuery(getTopicsCountQuery);

			if (topicConnection.fetchNextRow()) {
				topicsCount = topicConnection.getCurrentRowLong("topiccount");
			}
		} finally {
			if (topicConnection != null) {
				topicConnection.disconnect();
			}
		}

		return topicsCount;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.topic.ITopicService#addUserReply(io.reflection.app.datatypes.shared.Topic, io.reflection.app.datatypes.shared.User)
	 */
	@Override
	public Topic addUserReply(Topic topic, User user) throws DataAccessException {
		String addReplyQuery = String.format(
				"UPDATE `topic` SET `numberofreplies`=`numberofreplies`+1, `lastreplierid`=%d, `lastreplied`=NOW() WHERE `id`=%d AND `deleted`='n'",
				topic.id.longValue(), user.id.longValue());

		Connection topicConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeTopic.toString());

		try {
			topicConnection.connect();
			topicConnection.executeQuery(addReplyQuery);

			if (topicConnection.getAffectedRowCount() > 0) {
				if (LOG.isLoggable(GaeLevel.INFO)) {
					LOG.info(String.format("Incremented reply count on topic with id [%d] and reply by user [%d]", topic.id.longValue(), user.id.longValue()));
				}

				if (topic.numberOfReplies != null) {
					int numberOfReplies = topic.numberOfReplies.intValue();
					topic.numberOfReplies = Integer.valueOf(numberOfReplies + 1);

					topic.lastReplier = user;
					topic.lastReplied = new Date();
				}
			} else {
				if (LOG.isLoggable(GaeLevel.INFO)) {
					LOG.warning(String.format("Could not increment reply count on topic with id [%d]", topic.id.longValue()));
				}
			}
		} finally {
			if (topicConnection != null) {
				topicConnection.disconnect();
			}
		}

		return topic;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.topic.ITopicService#removeReply(io.reflection.app.datatypes.shared.Topic)
	 */
	@Override
	public Topic removeReply(Topic topic) throws DataAccessException {
		String removeReplyQuery = String.format("UPDATE `topic` SET `numberorreplies`=`numberofreplies`-1 WHERE `id`=%d AND `deleted`='n'",
				topic.id.longValue());

		Connection topicConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeTopic.toString());

		try {
			topicConnection.connect();
			topicConnection.executeQuery(removeReplyQuery);

			if (topicConnection.getAffectedRowCount() > 0) {
				if (LOG.isLoggable(GaeLevel.INFO)) {
					LOG.info(String.format("Decremented reply count on topic with id [%d]", topic.id.longValue()));
				}

				if (topic.numberOfReplies != null) {
					int numberOfReplies = topic.numberOfReplies.intValue();
					topic.numberOfReplies = Integer.valueOf(numberOfReplies - 1);
				}
			} else {
				if (LOG.isLoggable(GaeLevel.INFO)) {
					LOG.warning(String.format("Could not decrement reply count on topic with id [%d]", topic.id.longValue()));
				}
			}
		} finally {
			if (topicConnection != null) {
				topicConnection.disconnect();
			}
		}

		return topic;
	}

}