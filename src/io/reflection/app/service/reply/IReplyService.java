//  
//  IReplyService.java
//  reflection.io
//
//  Created by William Shakour on April 5, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.service.reply;

import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.datatypes.shared.Reply;
import io.reflection.app.datatypes.shared.Topic;

import java.util.List;

import com.spacehopperstudios.service.IService;

public interface IReplyService extends IService {
	/**
	 * @param id
	 * @return
	 */
	public Reply getReply(Long id) throws DataAccessException;

	/**
	 * @param reply
	 * @return
	 */
	public Reply addReply(Reply reply) throws DataAccessException;

	/**
	 * @param reply
	 * @return
	 */
	public Reply updateReply(Reply reply) throws DataAccessException;

	/**
	 * @param reply
	 */
	public void deleteReply(Reply reply) throws DataAccessException;

	/**
	 * @param topic
	 * @param pager
	 * @return
	 */
	public List<Reply> getReplies(Topic topic, Pager pager) throws DataAccessException;

	/**
	 * @param topic
	 * @return
	 */
	public Long getRepliesCount(Topic topic) throws DataAccessException;

}