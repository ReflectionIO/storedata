//
//  PostPage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 16 Mar 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page.blog;

import io.reflection.app.api.blog.shared.call.GetPostRequest;
import io.reflection.app.api.blog.shared.call.GetPostResponse;
import io.reflection.app.api.blog.shared.call.event.GetPostEventHandler;
import io.reflection.app.client.controller.EventController;
import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.controller.NavigationController.Stack;
import io.reflection.app.client.controller.PostController;
import io.reflection.app.client.handler.NavigationEventHandler;
import io.reflection.app.client.page.Page;
import io.reflection.app.client.page.blog.part.DisplayTag;
import io.reflection.app.datatypes.shared.Post;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author billy1380
 * 
 */
public class PostPage extends Page implements NavigationEventHandler, GetPostEventHandler {

	private static PostPageUiBinder uiBinder = GWT.create(PostPageUiBinder.class);

	interface PostPageUiBinder extends UiBinder<Widget, PostPage> {}

	private static final int POST_ID_PARAMETER_INDEX = 0;
	private static final String MORE_ACTION_NAME = "view";

	@UiField HeadingElement title;
	@UiField SpanElement date;
	@UiField SpanElement author;

	@UiField HTMLPanel tags;

	@UiField DivElement content;

	private Long postId;

	public PostPage() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.Composite#onAttach()
	 */
	@Override
	protected void onAttach() {
		super.onAttach();

		register(EventController.get().addHandlerToSource(NavigationEventHandler.TYPE, NavigationController.get(), this));
		register(EventController.get().addHandlerToSource(GetPostEventHandler.TYPE, PostController.get(), this));

		setLoading(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.NavigationEventHandler#navigationChanged(io.reflection.app.client.controller.NavigationController.Stack)
	 */
	@Override
	public void navigationChanged(Stack stack) {
		if (stack.getAction() != null) {
			if (MORE_ACTION_NAME.equals(stack.getAction())) {
				String postIdValue = stack.getParameter(POST_ID_PARAMETER_INDEX);

				if (postIdValue != null) {
					postId = null;

					try {
						postId = Long.parseLong(postIdValue);
					} catch (NumberFormatException e) {}

					if (postId != null) {
						Post post = PostController.get().getPost(postId);

						if (post != null) {
							show(post);
						}
					}
				}
			}
		}
	}

	/**
	 * @param post
	 */
	private void show(Post post) {
		title.setInnerText(post.title);
		author.setInnerText(post.author.forename + " " + post.author.surname);

		if (post.published != null) {
			date.setInnerText(DateTimeFormat.getFormat(PredefinedFormat.DATE_FULL).format(post.published));
		} else {
			date.setInnerText("TBD");
		}

		content.setInnerHTML(post.content);

		if (tags.getWidgetCount() > 0) {
			tags.clear();
		}

		if (post.tags != null) {
			DisplayTag displayTag;
			for (String tag : post.tags) {
				displayTag = new DisplayTag();
				displayTag.setName(tag);
				displayTag.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
				tags.add(displayTag);
			}
		}

		setLoading(false);

	}

	private void setLoading(boolean value) {
		// for now just hide the page
		setVisible(!value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.blog.shared.call.event.GetPostEventHandler#getPostSuccess(io.reflection.app.api.blog.shared.call.GetPostRequest,
	 * io.reflection.app.api.blog.shared.call.GetPostResponse)
	 */
	@Override
	public void getPostSuccess(GetPostRequest input, GetPostResponse output) {
		if (output.status == StatusType.StatusTypeSuccess && output.post != null) {
			show(output.post);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.blog.shared.call.event.GetPostEventHandler#getPostFailure(io.reflection.app.api.blog.shared.call.GetPostRequest,
	 * java.lang.Throwable)
	 */
	@Override
	public void getPostFailure(GetPostRequest input, Throwable caught) {}

}
