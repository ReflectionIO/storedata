//
//  StickyButton.java
//  storedata
//
//  Created by daniel on 15 Aug 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page.forum.part;

import io.reflection.app.client.controller.TopicController;
import io.reflection.app.datatypes.shared.Topic;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;

public class StickyButton extends Button implements ClickHandler {
	private long topicId;

	public StickyButton(long topicId) {
		super("");
		this.topicId = topicId;
		addClickHandler(this);
		getStyleElement().addClassName("btn");
		getStyleElement().addClassName("btn-default");
		setName();

	}

	public void setName() {
		Topic topic = TopicController.get().getTopic(topicId);
		if (!topic.sticky) {
			setText("Make sticky");
		} else {
			setText("Make unsticky");
		}
	}

	@Override
	public void onClick(ClickEvent event) {
		Topic topic = TopicController.get().getTopic(topicId);
		topic.sticky = !topic.sticky;
		TopicController.get().updateTopic(topic);

		setName();
	}
}