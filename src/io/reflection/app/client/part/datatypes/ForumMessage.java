//
//  ForumMessage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 2 May 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.part.datatypes;

import io.reflection.app.client.controller.SessionController;
import io.reflection.app.datatypes.shared.Reply;
import io.reflection.app.datatypes.shared.Topic;
import io.reflection.app.datatypes.shared.User;

import java.util.Date;

/**
 * This is a wrapper object around Topic and Reply.
 * It's needed in order to treat the content from a topic or reply as a single entity from the perspective of displaying them in a list.
 * Ideally in the future ForumMessage will replace Reply so that editing the content of a topic vs editing the content of a reply etc,
 * will have a single pathway (and all the other operations on both of them). ~Daniel
 * 
 * @author billy1380
 * 
 */
public class ForumMessage {

	private Topic topic;
	private Reply reply;
	Long currentuserId = SessionController.get().getLoggedInUser().id;

	public Long getId() {
		return reply == null ? topic.id : reply.id;
	}

	public Long getTopicId() {
		return topic.id;
	}

	public ForumMessage(Topic topic, Reply reply) {
		this.topic = topic;
		this.reply = reply;
	}
	
	public ForumMessage(Topic topic){
		this.topic = topic;
	}

	public String getContent() {
		return reply == null ? topic.content : reply.content;
	}

	public User getAuthor() {
		return reply == null ? topic.author : reply.author;
	}

	public Date getCreated() {
		return reply == null ? topic.created : reply.created;
	}

	public boolean canFlag() {
		return currentuserId != getId() ? true : false;
	}

	public boolean canEdit() {
		return currentuserId == getId() ? true : false;
	}

	/**
	 * @return true if the author is the same as the user, false otherwise
	 */
	public boolean belongsToCurrentUser() {
		return getAuthor().id.equals(currentuserId) ;
	}

}
