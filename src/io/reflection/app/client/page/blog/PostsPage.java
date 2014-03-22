//
//  PostsPage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 16 Mar 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page.blog;

import java.util.List;

import io.reflection.app.api.blog.shared.call.GetPostsRequest;
import io.reflection.app.api.blog.shared.call.GetPostsResponse;
import io.reflection.app.api.blog.shared.call.event.GetPostsEventHandler;
import io.reflection.app.client.controller.EventController;
import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.controller.NavigationController.Stack;
import io.reflection.app.client.controller.PostController;
import io.reflection.app.client.handler.NavigationEventHandler;
import io.reflection.app.client.page.Page;
import io.reflection.app.client.page.blog.part.PostSummary;
import io.reflection.app.datatypes.shared.Post;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author billy1380
 * 
 */
public class PostsPage extends Page implements NavigationEventHandler, GetPostsEventHandler {

	private static PostsPageUiBinder uiBinder = GWT.create(PostsPageUiBinder.class);

	interface PostsPageUiBinder extends UiBinder<Widget, PostsPage> {}

	@UiField HTMLPanel posts;

	public PostsPage() {
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
		register(EventController.get().addHandlerToSource(GetPostsEventHandler.TYPE, PostController.get(), this));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.NavigationEventHandler#navigationChanged(io.reflection.app.client.controller.NavigationController.Stack)
	 */
	@Override
	public void navigationChanged(Stack stack) {
		showPosts();
	}

	/**
	 * 
	 */
	private void showPosts() {
		posts.clear();

		List<Post> data;
		if ((data = PostController.get().getPosts()) != null) {
			for (Post post : data) {
				// if (post.published != null && post.visible == Boolean.TRUE) {
				PostSummary summary = new PostSummary();
				summary.setPost(post);

				posts.add(summary);
				// }
			}
		} else {}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.blog.shared.call.event.GetPostsEventHandler#getPostsSuccess(io.reflection.app.api.blog.shared.call.GetPostsRequest,
	 * io.reflection.app.api.blog.shared.call.GetPostsResponse)
	 */
	@Override
	public void getPostsSuccess(GetPostsRequest input, GetPostsResponse output) {
		showPosts();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.blog.shared.call.event.GetPostsEventHandler#getPostsFailure(io.reflection.app.api.blog.shared.call.GetPostsRequest,
	 * java.lang.Throwable)
	 */
	@Override
	public void getPostsFailure(GetPostsRequest input, Throwable caught) {}

}
