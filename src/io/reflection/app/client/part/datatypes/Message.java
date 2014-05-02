//
//  Message.java
//  storedata
//
//  Created by William Shakour (billy1380) on 2 May 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.part.datatypes;

import io.reflection.app.datatypes.shared.Reply;
import io.reflection.app.datatypes.shared.Topic;
import io.reflection.app.datatypes.shared.User;

/**
 * @author billy1380
 * 
 */
public class Message {
	private Topic topic;
	private Reply reply;

	public void set(Object message) {
		if (message instanceof Topic) {
			topic = (Topic) message;
		} else {
			reply = (Reply) message;
		}
	}

	public String getContent() {
		return topic == null ? reply.content : topic.content;
	}

	public User getAuthor() {
		return topic == null ? reply.author : topic.author;
	}

}
