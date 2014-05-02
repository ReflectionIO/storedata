//
//  TopicPage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 5 Apr 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page.forum;

import io.reflection.app.client.controller.EventController;
import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.controller.NavigationController.Stack;
import io.reflection.app.client.controller.ReplyController;
import io.reflection.app.client.controller.TopicController;
import io.reflection.app.client.handler.NavigationEventHandler;
import io.reflection.app.client.page.Page;
import io.reflection.app.client.page.PageType;
import io.reflection.app.client.page.forum.part.MessageCell;
import io.reflection.app.client.part.BootstrapGwtCellList;
import io.reflection.app.client.part.datatypes.Message;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author billy1380
 * 
 */
public class TopicPage extends Page implements NavigationEventHandler {

	private static TopicPageUiBinder uiBinder = GWT.create(TopicPageUiBinder.class);

	protected final static String VIEW_ACTION_PARAMETER_VALUE = "view";
	protected final static int TOPIC_ID_PARAMETER_INDEX = 0;

	private Long topicId;

	interface TopicPageUiBinder extends UiBinder<Widget, TopicPage> {}

	@UiField(provided = true) CellList<Message> messages = new CellList<Message>(new MessageCell(), BootstrapGwtCellList.INSTANCE);

	public TopicPage() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.page.Page#onAttach()
	 */
	@Override
	protected void onAttach() {
		super.onAttach();

		register(EventController.get().addHandlerToSource(NavigationEventHandler.TYPE, NavigationController.get(), this));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.page.Page#getTitle()
	 */
	@Override
	public String getTitle() {
		return "Reflection.io: Forum";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.NavigationEventHandler#navigationChanged(io.reflection.app.client.controller.NavigationController.Stack,
	 * io.reflection.app.client.controller.NavigationController.Stack)
	 */
	@Override
	public void navigationChanged(Stack previous, Stack current) {

		if (current != null && PageType.ForumTopicPageType.equals(current.getPage())) {
			if (current.getAction() != null && VIEW_ACTION_PARAMETER_VALUE.equals(current.getAction())) {

				String topicIdString;
				if ((topicIdString = current.getParameter(TOPIC_ID_PARAMETER_INDEX)) != null) {
					topicId = Long.valueOf(topicIdString);

					TopicController.get().getTopic(topicId);

					ReplyController.get().getReplies(topicId);
				}
			}
		}
	}

}
