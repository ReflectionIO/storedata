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
import io.reflection.app.api.blog.shared.call.event.CreatePostEventHandler;
import io.reflection.app.client.controller.EventController;
import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.controller.NavigationController.Stack;
import io.reflection.app.client.controller.PostController;
import io.reflection.app.client.handler.NavigationEventHandler;
import io.reflection.app.client.page.Page;
import io.reflection.app.client.page.PageType;
import io.reflection.app.client.part.text.RichTextToolbar;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author billy1380
 * 
 */
public class EditPostPage extends Page implements NavigationEventHandler, CreatePostEventHandler {

	private static EditPostPageUiBinder uiBinder = GWT.create(EditPostPageUiBinder.class);

	interface EditPostPageUiBinder extends UiBinder<Widget, EditPostPage> {}

	@UiField TextBox title;
	@UiField TextBox tags;

	@UiField CheckBox publish;
	@UiField CheckBox visible;

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
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.NavigationEventHandler#navigationChanged(io.reflection.app.client.controller.NavigationController.Stack)
	 */
	@Override
	public void navigationChanged(Stack stack) {}

	@UiHandler("submit")
	void onSubmit(ClickEvent e) {
		if (validate()) {
			if (postId != null) {
				PostController.get().updatePost(postId, title.getText(), visible.getValue(), descriptionText.getHTML(), contentText.getHTML(),
						publish.getValue(), tags.getText());
			} else {
				PostController.get().createPost(title.getText(), visible.getValue(), descriptionText.getHTML(), contentText.getHTML(), publish.getValue(),
						tags.getText());
			}
		}
	}

	private boolean validate() {
		return true;
	}

	/* (non-Javadoc)
	 * @see io.reflection.app.api.blog.shared.call.event.CreatePostEventHandler#createPostSuccess(io.reflection.app.api.blog.shared.call.CreatePostRequest, io.reflection.app.api.blog.shared.call.CreatePostResponse)
	 */
	@Override
	public void createPostSuccess(CreatePostRequest input, CreatePostResponse output) {
		if (output.status == StatusType.StatusTypeSuccess) {
			PostController.get().reset();
			PageType.BlogEditPost.show();
		}
	}

	/* (non-Javadoc)
	 * @see io.reflection.app.api.blog.shared.call.event.CreatePostEventHandler#createPostFailure(io.reflection.app.api.blog.shared.call.CreatePostRequest, java.lang.Throwable)
	 */
	@Override
	public void createPostFailure(CreatePostRequest input, Throwable caught) {
	}

}
