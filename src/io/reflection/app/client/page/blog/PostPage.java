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
import io.reflection.app.client.page.PageType;
import io.reflection.app.client.page.blog.part.DisplayTag;
import io.reflection.app.client.part.ReflectionProgressBar;
import io.reflection.app.datatypes.shared.Post;
import io.reflection.app.shared.util.FormattingHelper;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
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

	private enum LoadingType {
		CompleteLoadingType,
		PartialLoadingType,
		NoneLoadingType
	}

	private static final int POST_ID_PARAMETER_INDEX = 0;
	private static final String MORE_ACTION_NAME = "view";

	@UiField HeadingElement title;
	@UiField SpanElement date;
	@UiField SpanElement author;

	@UiField HTMLPanel tags;
	DivElement comments;

	@UiField DivElement content;
	@UiField ReflectionProgressBar progress;

	private Long postId;
	private boolean installed;

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

		// this is not a uifield because it requires html id
		comments = DivElement.as(Document.get().getElementById("disqus_thread"));

		setLoading(LoadingType.CompleteLoadingType);
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
			if (MORE_ACTION_NAME.equals(current.getAction())) {
				String postIdValue = current.getParameter(POST_ID_PARAMETER_INDEX);

				if (postIdValue != null) {
					postId = null;

					try {
						postId = Long.parseLong(postIdValue);
					} catch (NumberFormatException e) {}

					if (postId != null) {
						Post post = PostController.get().getPost(postId);

						if (post == null) {
							post = PostController.get().getPostPart(postId);

							if (post != null) {
								setLoading(LoadingType.PartialLoadingType);
							}
						}

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
		author.setInnerText(FormattingHelper.getUserName(post.author));

		if (post.published != null) {
			date.setInnerText(DateTimeFormat.getFormat(PredefinedFormat.DATE_FULL).format(post.published));
		} else {
			date.setInnerText("TBD");
		}

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

		if (post.content != null) {
			content.setInnerHTML(post.content);

			setLoading(LoadingType.NoneLoadingType);

			if (post.commentsEnabled == Boolean.TRUE) {
				String identifier = "post" + post.id.toString();
				String url = GWT.getHostPageBaseURL() + PageType.BlogPostPageType.asHref("view", post.id.toString());
				String title = post.title;
				String tag = post.tags == null || post.tags.size() == 0 ? "reflection.io" : post.tags.get(0);

				if (!installed) {
					installDisqus(identifier, url, title, tag);
					installed = true;
				} else {
					resetDisqus(identifier, url, title, tag);
				}
			}
		}

	}

	private void setLoading(LoadingType value) {
		switch (value) {
		case CompleteLoadingType:
			progress.setVisible(true);
			progress.getElement().getStyle().setPaddingTop(30, Unit.PX);
			title.getStyle().setDisplay(Display.NONE);
			author.getParentElement().getStyle().setDisplay(Display.NONE);
			content.getStyle().setDisplay(Display.NONE);
			comments.getStyle().setDisplay(Display.NONE);
			break;
		case PartialLoadingType:
			progress.setVisible(true);
			progress.getElement().getStyle().clearPaddingTop();
			title.getStyle().clearDisplay();
			author.getParentElement().getStyle().clearDisplay();
			content.getStyle().setDisplay(Display.NONE);
			comments.getStyle().setDisplay(Display.NONE);
			break;
		case NoneLoadingType:
			progress.setVisible(false);
			progress.getElement().getStyle().clearPaddingTop();
			title.getStyle().clearDisplay();
			author.getParentElement().getStyle().clearDisplay();
			content.getStyle().clearDisplay();
			comments.getStyle().clearDisplay();
			break;
		}
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

	private static native void installDisqus(String postId, String url, String title, String category) /*-{
		$wnd.disqus_shortname = 'reflectionio';

		$wnd.disqus_identifier = postId;
		$wnd.disqus_url = url;
		$wnd.disqus_title = title;
		$wnd.disqus_category_id = category;

		($wnd.installDisqus = function() {
			var dsq = $wnd.document.createElement('script');
			dsq.type = 'text/javascript';
			dsq.async = true;
			dsq.src = '//' + $wnd.disqus_shortname + '.disqus.com/embed.js';
			($wnd.document.getElementsByTagName('head')[0] || $wnd.document
					.getElementsByTagName('body')[0]).appendChild(dsq);
		})();

		$wnd.reset = function(resetPostId, resetUrl, resetTitle, resetCategory) {
			$wnd.DISQUS.reset({
				reload : true,
				config : function() {
					this.page.identifier = resetPostId;
					this.page.url = resetUrl;
					this.page.title = resetTitle;
					this.page.category_id = resetCategory;
				}
			});
		};
	}-*/;

	private static native void resetDisqus(String postId, String url, String title, String category) /*-{
		$wnd.reset(postId, url, title, category);
	}-*/;
}
