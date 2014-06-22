//
//  EditTopicPage.java
//  storedata
//
//  Created by William Shakour (donsasikumar) on 19 Jun 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page.forum;

import io.reflection.app.api.forum.shared.call.UpdateReplyRequest;
import io.reflection.app.api.forum.shared.call.UpdateReplyResponse;
import io.reflection.app.api.forum.shared.call.event.UpdateReplyEventHandler;
import io.reflection.app.client.controller.EventController;
import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.controller.NavigationController.Stack;
import io.reflection.app.client.controller.ReplyController;
import io.reflection.app.client.handler.NavigationEventHandler;
import io.reflection.app.client.page.Page;
import io.reflection.app.client.page.PageType;
import io.reflection.app.client.part.text.RichTextToolbar;
import io.reflection.app.datatypes.shared.Reply;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.Widget;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author donsasikumar
 * 
 */
public class EditTopicPage extends Page implements NavigationEventHandler, UpdateReplyEventHandler {

	private static EditTopicPageUiBinder uiBinder = GWT.create(EditTopicPageUiBinder.class);

	interface EditTopicPageUiBinder extends UiBinder<Widget, EditTopicPage> {}

	private Long topicId;
	private Long replyId;
	Reply reply;
	String replyContent;
	@UiField RichTextToolbar editToolbar;
	@UiField RichTextArea editText;

	public EditTopicPage() {
		initWidget(uiBinder.createAndBindUi(this));
		editToolbar.setRichText(editText);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.page.Page#onAttach()
	 */
	@Override
	protected void onAttach() {
		// TODO Auto-generated method stub
		super.onAttach();
		register(EventController.get().addHandlerToSource(NavigationEventHandler.TYPE, NavigationController.get(), this));
		register(EventController.get().addHandlerToSource(UpdateReplyEventHandler.TYPE, ReplyController.get(), this));

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.NavigationEventHandler#navigationChanged(io.reflection.app.client.controller.NavigationController.Stack,
	 * io.reflection.app.client.controller.NavigationController.Stack)
	 */
	@Override
	public void navigationChanged(Stack previous, Stack current) {
		// TODO Auto-generated method stub
		if (current != null && PageType.ForumEditTopicPageType.equals(current.getPage())) {
			String topicIdString = current.getParameter(0);
			String selectedMessageId = current.getParameter(2);

			if (topicIdString != null) {
				topicId = null;

				try {
					topicId = Long.parseLong(topicIdString);
					replyId = Long.parseLong(selectedMessageId);
				} catch (NumberFormatException e) {}

				if (topicId != null) {
					List<Reply> replyList = ReplyController.get().getRepliesList(topicId);
					reply = ReplyController.get().getReply(replyId);
					replyContent = reply.content.toString();

					if (reply != null) {
						show();
					}
				}
			}
		}
	}

	/**
	 * @param reply
	 */
	private void show() {
		editText.setFocus(true);
		Document.get().setScrollTop(editText.getAbsoluteTop());
		editText.setText(replyContent);

	}

	@UiHandler("submit")
	void onSubmit(ClickEvent e) {

		if (validate()) {
			if (replyId != null) {
				ReplyController.get().updateReply(replyId, editText.getHTML());
			} else {
				ReplyController.get().addReply(replyId, editText.getHTML());
			}
		}
	}

	private boolean validate() {
		return true;
	}

	
	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.forum.shared.call.event.UpdateReplyEventHandler#updateReplySuccess(io.reflection.app.api.forum.shared.call.UpdateReplyRequest,
	 * io.reflection.app.api.forum.shared.call.UpdateReplyResponse)
	 */
	@Override
	public void updateReplySuccess(UpdateReplyRequest input, UpdateReplyResponse output) {
		if (output.status == StatusType.StatusTypeSuccess && replyId != null) {
			PageType.ForumThreadPageType.show("view", replyId.toString());
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.forum.shared.call.event.UpdateReplyEventHandler#updateReplyFailure(io.reflection.app.api.forum.shared.call.UpdateReplyRequest,
	 * java.lang.Throwable)
	 */
	@Override
	public void updateReplyFailure(UpdateReplyRequest input, Throwable caught) {
		// TODO Auto-generated method stub

	}

	
}
