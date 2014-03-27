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
import io.reflection.app.client.controller.EventController;
import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.controller.NavigationController.Stack;
import io.reflection.app.client.controller.PostController;
import io.reflection.app.client.handler.NavigationEventHandler;
import io.reflection.app.client.page.Page;
import io.reflection.app.client.page.PageType;
import io.reflection.app.client.part.text.RichTextToolbar;
import io.reflection.app.datatypes.shared.Post;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.TextBox;
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

	@UiField TextBox title;
	@UiField TextBox tags;

	@UiField CheckBox publish;
	@UiField CheckBox visible;
	@UiField CheckBox commentsEnabled;

	@UiField RichTextToolbar descriptionToolbar;
	@UiField RichTextArea descriptionText;

	@UiField RichTextToolbar contentToolbar;
	@UiField RichTextArea contentText;

	private Long postId;

	public EditPostPage() {
		initWidget(uiBinder.createAndBindUi(this));

		descriptionToolbar.setRichText(descriptionText);
		contentToolbar.setRichText(contentText);

		title.getElement().setAttribute("placeholder", "Title");
		tags.getElement().setAttribute("placeholder", "Comma separated tags");
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
		register(EventController.get().addHandlerToSource(CreatePostEventHandler.TYPE, PostController.get(), this));
		register(EventController.get().addHandlerToSource(GetPostEventHandler.TYPE, PostController.get(), this));
		register(EventController.get().addHandlerToSource(UpdatePostEventHandler.TYPE, PostController.get(), this));

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
				String postIdValue = current.getParameter(POST_ID_PARAMETER_INDEX);

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

		descriptionText.setHTML(post.description);
		contentText.setHTML(post.content);

		publish.setValue(Boolean.valueOf(post.published != null));

		if (post.tags != null) {
			tags.setText(StringUtils.join(post.tags));
		}
	}

	@UiHandler("submit")
	void onSubmit(ClickEvent e) {
		if (validate()) {
			if (postId != null) {
				PostController.get().updatePost(postId, title.getText(), visible.getValue(), commentsEnabled.getValue(), descriptionText.getHTML(),
						contentText.getHTML(), publish.getValue(), tags.getText());
			} else {
				PostController.get().createPost(title.getText(), visible.getValue(), commentsEnabled.getValue(), descriptionText.getHTML(),
						contentText.getHTML(), publish.getValue(), tags.getText());
			}
		}
	}

	private boolean validate() {
		return true;
	}

	private void resetForm() {
		postId = null;

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
			PageType.BlogEditPostPageType.show();
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.blog.shared.call.event.UpdatePostEventHandler#updatePostSuccess(io.reflection.app.api.blog.shared.call.UpdatePostRequest,
	 * io.reflection.app.api.blog.shared.call.UpdatePostResponse)
	 */
	@Override
	public void updatePostSuccess(UpdatePostRequest input, UpdatePostResponse output) {
		if (output.status == StatusType.StatusTypeSuccess && postId != null) {
			PageType.BlogPostPageType.show("view", postId.toString());
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.blog.shared.call.event.UpdatePostEventHandler#updatePostFailure(io.reflection.app.api.blog.shared.call.UpdatePostRequest,
	 * java.lang.Throwable)
	 */
	@Override
	public void updatePostFailure(UpdatePostRequest input, Throwable caught) {

	}

}
