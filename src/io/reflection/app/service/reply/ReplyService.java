//  
//  ReplyService.java
//  reflection.io
//
//  Created by William Shakour on April 5, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.service.reply;

import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.datatypes.shared.Reply;
import io.reflection.app.datatypes.shared.Topic;
import io.reflection.app.repackaged.scphopr.cloudsql.Connection;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseServiceProvider;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseType;
import io.reflection.app.repackaged.scphopr.service.database.IDatabaseService;
import io.reflection.app.service.ServiceType;
import io.reflection.app.service.user.UserServiceProvider;

final class ReplyService implements IReplyService {
	public String getName() {
		return ServiceType.ServiceTypeReply.toString();
	}

	@Override
	public Reply getReply(Long id) throws DataAccessException {
		Reply reply = null;

		IDatabaseService databaseService = DatabaseServiceProvider.provide();
		Connection replyConnection = databaseService.getNamedConnection(DatabaseType.DatabaseTypeReply.toString());

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
		throw new UnsupportedOperationException();
	}

	@Override
	public Reply updateReply(Reply reply) throws DataAccessException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteReply(Reply reply) throws DataAccessException {
		throw new UnsupportedOperationException();
	}

}