//  
//  ITopicService.java
//  reflection.io
//
//  Created by William Shakour on April 5, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.service.topic;

import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.datatypes.shared.Forum;
import io.reflection.app.datatypes.shared.Topic;

import java.util.List;

import com.spacehopperstudios.service.IService;

public interface ITopicService extends IService {
	/**
	 * @param id
	 * @return
	 */
	public Topic getTopic(Long id) throws DataAccessException;

	/**
	 * @param topic
	 * @return
	 */
	public Topic addTopic(Topic topic) throws DataAccessException;

	/**
	 * @param topic
	 * @return
	 */
	public Topic updateTopic(Topic topic) throws DataAccessException;

	/**
	 * @param topic
	 */
	public void deleteTopic(Topic topic) throws DataAccessException;

	/**
	 * @param forum
	 * @param pager
	 * @return
	 */
	public List<Topic> getTopics(Forum forum, Pager pager) throws DataAccessException;

	/**
	 * @param forum
	 * @return
	 */
	public Long getTopicsCount(Forum forum) throws DataAccessException;

}