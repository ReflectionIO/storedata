//
//  ForumMessage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 2 May 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.part.datatypes;

import io.reflection.app.datatypes.shared.Reply;
import io.reflection.app.datatypes.shared.Topic;
import io.reflection.app.datatypes.shared.User;

import java.util.Date;

/**
 * @author billy1380
 * 
 */
public class ForumMessage {
	private Topic topic;
	private Reply reply;

	public ForumMessage(Reply reply) {
		this.reply = reply;
	}

	public ForumMessage(Topic topic) {
		this.topic = topic;
	}

	public String getContent() {
		return topic == null ? reply.content : topic.content;
	}

	public User getAuthor() {
		return topic == null ? reply.author : topic.author;
	}

	public Date getCreated() {
		return topic == null ? reply.created : topic.created;
	}

}
