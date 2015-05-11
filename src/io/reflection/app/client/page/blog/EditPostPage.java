//
//  EditPostPage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 16 Mar 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page.blog;

import io.reflection.app.api.blog.shared.call.CreatePostRequest;
import io.reflection.app.api.blog.shared.call.CreatePostResponse;
import io.reflection.app.api.blog.shared.call.GetPostRequest;
import io.reflection.app.api.blog.shared.call.GetPostResponse;
import io.reflection.app.api.blog.shared.call.UpdatePostRequest;
import io.reflection.app.api.blog.shared.call.UpdatePostResponse;
import io.reflection.app.api.blog.shared.call.event.CreatePostEventHandler;
import io.reflection.app.api.blog.shared.call.event.GetPostEventHandler;
import io.reflection.app.api.blog.shared.call.event.UpdatePostEventHandler;
import io.reflection.app.client.DefaultEventBus;
import io.reflection.app.client.component.FormCheckbox;
import io.reflection.app.client.component.TextField;
import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.controller.NavigationController.Stack;
import io.reflection.app.client.controller.PostController;
import io.reflection.app.client.handler.NavigationEventHandler;
import io.reflection.app.client.page.Page;
import io.reflection.app.client.page.PageType;
import io.reflection.app.client.part.text.MarkdownEditor;
import io.reflection.app.client.res.Styles;
import io.reflection.app.datatypes.shared.Post;
import io.reflection.app.shared.util.LookupHelper;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;
import com.spacehopperstudios.utility.StringUtils;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author billy1380
 * 
 */
public class EditPostPage extends Page implements NavigationEventHandler, CreatePostEventHandler, GetPostEventHandler, UpdatePostEventHandler {

	private static EditPostPageUiBinder uiBinder = GWT.create(EditPostPageUiBinder.class);

	interface EditPostPageUiBinder extends UiBinder<Widget, EditPostPage> {}

	private static final String CHANGE_ACTION_NAME = "change";
	private static final String NEW_ACTION_NAME = "new";
	private static final int POST_ID_PARAMETER_INDEX = 0;

	@UiField TextField title;
	@UiField TextField tags;

	@UiField FormCheckbox publish;
	@UiField FormCheckbox visible;
	@UiField FormCheckbox commentsEnabled;

	@UiField MarkdownEditor descriptionText;

	@UiField MarkdownEditor contentText;

	private Post post;

	public EditPostPage() {
		initWidget(uiBinder.createAndBindUi(this));

		Styles.STYLES_INSTANCE.blog().ensureInjected();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.Composite#onAttach()
	 */
	@Override
	protected void onAttach() {
		super.onAttach();

		register(DefaultEventBus.get().addHandlerToSource(NavigationEventHandler.TYPE, NavigationController.get(), this));
		register(DefaultEventBus.get().addHandlerToSource(CreatePostEventHandler.TYPE, PostController.get(), this));
		register(DefaultEventBus.get().addHandlerToSource(GetPostEventHandler.TYPE, PostController.get(), this));
		register(DefaultEventBus.get().addHandlerToSource(UpdatePostEventHandler.TYPE, PostController.get(), this));

		resetForm();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.page.Page#getTitle()
	 */
	@Override
	public String getTitle() {
		return "Reflection.io: Blog";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.NavigationEventHandler#navigationChanged(io.reflection.app.client.controller.NavigationController.Stack,
	 * io.reflection.app.client.controller.NavigationController.Stack)
	 */
	@Override
	public void navigationChanged(Stack previous, Stack current) {
		if (current.getAction() != null) {
			if (CHANGE_ACTION_NAME.equals(current.getAction())) {
				String postParam = current.getParameter(POST_ID_PARAMETER_INDEX);

				if (postParam != null) {
					post = PostController.get().getPost(postParam);

					if (post != null) {
						show(post);
					}
				}
			} else if (NEW_ACTION_NAME.equals(current.getAction())) {
				resetForm();
			}
		}
	}

	/**
	 * @param post
	 */
	private void show(Post post) {
		title.setText(post.title);
		visible.setValue(post.visible);
		commentsEnabled.setValue(post.commentsEnabled);

		descriptionText.setText(post.description);
		contentText.setText(post.content);

		publish.setValue(Boolean.valueOf(post.published != null));

		if (post.tags != null) {
			tags.setText(StringUtils.join(post.tags));
		}
	}

	@UiHandler("submit")
	void onSubmit(ClickEvent e) {
		if (validate()) {
			if (post != null) {
				PostController.get().updatePost(LookupHelper.reference(post), title.getText(), visible.getValue(), commentsEnabled.getValue(),
						descriptionText.getText(), contentText.getText(), publish.getValue(), tags.getText());
			} else {
				PostController.get().createPost(title.getText(), visible.getValue(), commentsEnabled.getValue(), descriptionText.getText(),
						contentText.getText(), publish.getValue(), tags.getText());
			}
		}
	}

	private boolean validate() {
		return true;
	}

	private void resetForm() {
		post = null;

		title.setText("");
		visible.setValue(Boolean.FALSE);
		descriptionText.setText("");
		contentText.setText("");
		publish.setValue(Boolean.FALSE);
		tags.setText("");

		// hide errors and remove clear validation strings
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.blog.shared.call.event.CreatePostEventHandler#createPostSuccess(io.reflection.app.api.blog.shared.call.CreatePostRequest,
	 * io.reflection.app.api.blog.shared.call.CreatePostResponse)
	 */
	@Override
	public void createPostSuccess(CreatePostRequest input, CreatePostResponse output) {
		if (output.status == StatusType.StatusTypeSuccess) {
			PostController.get().reset();
			PostController.get().fetchPosts();

			PageType.BlogPostPageType.show(NavigationController.VIEW_ACTION_PARAMETER_VALUE, LookupHelper.reference(post = input.post));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.blog.shared.call.event.CreatePostEventHandler#createPostFailure(io.reflection.app.api.blog.shared.call.CreatePostRequest,
	 * java.lang.Throwable)
	 */
	@Override
	public void createPostFailure(CreatePostRequest input, Throwable caught) {}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.blog.shared.call.event.GetPostEventHandler#getPostSuccess(io.reflection.app.api.blog.shared.call.GetPostRequest,
	 * io.reflection.app.api.blog.shared.call.GetPostResponse)
	 */
	@Override
	public void getPostSuccess(GetPostRequest input, GetPostResponse output) {
		if (output.status == StatusType.StatusTypeSuccess && output.post != null) {
			show(post = output.post);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.blog.shared.call.event.UpdatePostEventHandler#updatePostSuccess(io.reflection.app.api.blog.shared.call.UpdatePostRequest,
	 * io.reflection.app.api.blog.shared.call.UpdatePostResponse)
	 */
	@Override
	public void updatePostSuccess(UpdatePostRequest input, UpdatePostResponse output) {
		if (output.status == StatusType.StatusTypeSuccess) {
			PageType.BlogPostPageType.show(NavigationController.VIEW_ACTION_PARAMETER_VALUE, LookupHelper.reference(post = input.post));
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.blog.shared.call.event.UpdatePostEventHandler#updatePostFailure(io.reflection.app.api.blog.shared.call.UpdatePostRequest,
	 * java.lang.Throwable)
	 */
	@Override
	public void updatePostFailure(UpdatePostRequest input, Throwable caught) {}

}
