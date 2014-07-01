//  
//  ReplyService.java
//  reflection.io
//
//  Created by William Shakour on April 5, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.service.reply;

import static com.spacehopperstudios.utility.StringUtils.addslashes;
import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.datatypes.shared.Reply;
import io.reflection.app.datatypes.shared.Topic;
import io.reflection.app.repackaged.scphopr.cloudsql.Connection;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseServiceProvider;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseType;
import io.reflection.app.repackaged.scphopr.service.database.IDatabaseService;
import io.reflection.app.service.ServiceType;
import io.reflection.app.service.topic.TopicServiceProvider;
import io.reflection.app.service.user.UserServiceProvider;

import java.util.ArrayList;
import java.util.List;

final class ReplyService implements IReplyService {
	public String getName() {
		return ServiceType.ServiceTypeReply.toString();
	}

	@Override
	public Reply getReply(Long id) throws DataAccessException {
		Reply reply = null;

		Connection replyConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeReply.toString());

		String getReplyQuery = String.format("select * from `reply` where `deleted`='n' and `id`='%d' limit 1", id.longValue());
		try {
			replyConnection.connect();
			replyConnection.executeQuery(getReplyQuery);

			if (replyConnection.fetchNextRow()) {
				reply = toReply(replyConnection);
			}
		} finally {
			if (replyConnection != null) {
				replyConnection.disconnect();
			}
		}
		return reply;
	}

	/**
	 * To reply
	 * 
	 * @param connection
	 * @return
	 */
	private Reply toReply(Connection connection) throws DataAccessException {
		Reply reply = new Reply();
		reply.id = connection.getCurrentRowLong("id");
		reply.created = connection.getCurrentRowDateTime("created");
		reply.deleted = connection.getCurrentRowString("deleted");

		Long authorId = connection.getCurrentRowLong("authorid");
		reply.author = UserServiceProvider.provide().getUser(authorId);

		reply.content = connection.getCurrentRowString("content");
		reply.flagged = connection.getCurrentRowInteger("flagged");

		String solutionString = connection.getCurrentRowString("solution");
		reply.solution = solutionString == null ? Boolean.FALSE : (solutionString.equalsIgnoreCase("y") ? Boolean.TRUE : Boolean.FALSE);

		reply.topic = new Topic();
		reply.topic.id = connection.getCurrentRowLong("topicid");

		return reply;
	}

	@Override
	public Reply addReply(Reply reply) throws DataAccessException {
		Reply addedReply = null;

		final String addReplyQuery = String.format("INSERT INTO `reply` (`authorid`,`topicid`,`content`,`tags`) VALUES (%d,%d,'%s',NULL)",
				reply.author.id.longValue(), reply.topic.id.longValue(), addslashes(reply.content));

		Connection replyConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeReply.toString());

		try {
			replyConnection.connect();
			replyConnection.executeQuery(addReplyQuery);

			if (replyConnection.getAffectedRowCount() > 0) {
				addedReply = getReply(Long.valueOf(replyConnection.getInsertedId()));

				if (addedReply == null) {
					addedReply = reply;
					addedReply.id = Long.valueOf(replyConnection.getInsertedId());
				}

				addedReply.topic = TopicServiceProvider.provide().addUserReply(addedReply.topic, addedReply.author);
			}
		} finally {
			if (replyConnection != null) {
				replyConnection.disconnect();
			}
		}

		return addedReply;
	}

	@Override
	public Reply updateReply(Reply reply) throws DataAccessException {
		Reply updatedReply = null;
		boolean changed = false;

		IDatabaseService databaseService = DatabaseServiceProvider.provide();
		Connection replyConnection = databaseService.getNamedConnection(DatabaseType.DatabaseTypeReply.toString());

		String updateReplyQuery = String.format("UPDATE `reply` SET `content`='%s' WHERE `id`=%d AND `deleted`='n'", addslashes(reply.content),
				reply.id.longValue());
		try {
			replyConnection.connect();
			replyConnection.executeQuery(updateReplyQuery);

			if (replyConnection.getAffectedRowCount() > 0) {
				changed = true;
			}
		} finally {
			if (replyConnection != null) {
				replyConnection.disconnect();
			}
		}

		if (changed) {
			updatedReply = getReply(reply.id);
		} else {
			updatedReply = reply;
		}

		return updatedReply;
	}

	@Override
	public void deleteReply(Reply reply) throws DataAccessException {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.reply.IReplyService#getReplies(io.reflection.app.datatypes.shared.Topic, io.reflection.app.api.shared.datatypes.Pager)
	 */
	@Override
	public List<Reply> getReplies(Topic topic, Pager pager) throws DataAccessException {
		List<Reply> replies = new ArrayList<Reply>();

		IDatabaseService databaseService = DatabaseServiceProvider.provide();
		Connection replyConnection = databaseService.getNamedConnection(DatabaseType.DatabaseTypeReply.toString());

		String getRepliesQuery = String.format("SELECT * FROM `reply` WHERE `topicid`=%d AND `deleted`='n'", topic.id.longValue());

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

			getRepliesQuery += String.format(" ORDER BY `%s` %s", sortByQuery, sortDirectionQuery);
		}

		if (pager.start != null && pager.count != null) {
			getRepliesQuery += String.format(" LIMIT %d, %d", pager.start.longValue(), pager.count.longValue());
		} else if (pager.count != null) {
			getRepliesQuery += String.format(" LIMIT %d", pager.count.longValue());
		}
		try {
			replyConnection.connect();
			replyConnection.executeQuery(getRepliesQuery);

			while (replyConnection.fetchNextRow()) {
				Reply reply = this.toReply(replyConnection);

				if (reply != null) {
					replies.add(reply);
				}
			}
		} finally {
			if (replyConnection != null) {
				replyConnection.disconnect();
			}
		}

		return replies;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.reply.IReplyService#getRepliesCount(io.reflection.app.datatypes.shared.Topic)
	 */
	@Override
	public Long getRepliesCount(Topic topic) throws DataAccessException {
		Long repliesCount = Long.valueOf(0);

		Connection replyConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeReply.toString());

		String getRepliesCountQuery = String.format("SELECT COUNT(1) AS `replycount` FROM `reply` WHERE `topicid`=%d AND `deleted`='n'", topic.id.longValue());

		try {
			replyConnection.connect();
			replyConnection.executeQuery(getRepliesCountQuery);

			if (replyConnection.fetchNextRow()) {
				repliesCount = replyConnection.getCurrentRowLong("replycount");
			}
		} finally {
			if (replyConnection != null) {
				replyConnection.disconnect();
			}
		}

		return repliesCount;
	}

}