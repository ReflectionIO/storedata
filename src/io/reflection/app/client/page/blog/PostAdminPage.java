//
//  PostAdminPage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 16 Mar 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page.blog;

import io.reflection.app.api.blog.shared.call.GetPostsRequest;
import io.reflection.app.api.blog.shared.call.GetPostsResponse;
import io.reflection.app.api.blog.shared.call.event.GetPostsEventHandler;
import io.reflection.app.client.DefaultEventBus;
import io.reflection.app.client.cell.StyledButtonCell;
import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.controller.NavigationController.Stack;
import io.reflection.app.client.controller.PostController;
import io.reflection.app.client.controller.ServiceConstants;
import io.reflection.app.client.controller.SessionController;
import io.reflection.app.client.handler.NavigationEventHandler;
import io.reflection.app.client.page.Page;
import io.reflection.app.client.page.PageType;
import io.reflection.app.client.part.BootstrapGwtCellTable;
import io.reflection.app.client.part.SimplePager;
import io.reflection.app.client.res.Images;
import io.reflection.app.client.res.Styles;
import io.reflection.app.datatypes.shared.Post;
import io.reflection.app.datatypes.shared.User;
import io.reflection.app.shared.util.FormattingHelper;
import io.reflection.app.shared.util.LookupHelper;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author billy1380
 * 
 */
public class PostAdminPage extends Page implements GetPostsEventHandler, NavigationEventHandler {

	private static PostAdminPageUiBinder uiBinder = GWT.create(PostAdminPageUiBinder.class);

	interface PostAdminPageUiBinder extends UiBinder<Widget, PostAdminPage> {}

	@UiField(provided = true) CellTable<Post> posts = new CellTable<Post>(ServiceConstants.SHORT_STEP_VALUE, BootstrapGwtCellTable.INSTANCE);
	@UiField(provided = true) SimplePager simplePager = new SimplePager(false, false);

	public PostAdminPage() {
		initWidget(uiBinder.createAndBindUi(this));

		Styles.STYLES_INSTANCE.blog().ensureInjected();

		createColumns();

		posts.setLoadingIndicator(new Image(Images.INSTANCE.preloader()));
		PostController.get().addDataDisplay(posts);
		simplePager.setDisplay(posts);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.page.Page#onAttach()
	 */
	@Override
	protected void onAttach() {
		super.onAttach();

		register(DefaultEventBus.get().addHandlerToSource(GetPostsEventHandler.TYPE, PostController.get(), this));
		register(DefaultEventBus.get().addHandlerToSource(NavigationEventHandler.TYPE, NavigationController.get(), this));
	}

	private void createColumns() {
		TextColumn<Post> idColumn = new TextColumn<Post>() {

			@Override
			public String getValue(Post object) {
				return object.id.toString();
			}

		};

		TextColumn<Post> authorColumn = new TextColumn<Post>() {

			@Override
			public String getValue(Post object) {
				User loggedIn = SessionController.get().getLoggedInUser();

				return loggedIn != null && object.author.id == loggedIn.id ? "Me" : FormattingHelper.getUserName(object.author);
			}

		};

		TextColumn<Post> titleColumn = new TextColumn<Post>() {

			@Override
			public String getValue(Post object) {
				return object.title;
			}

		};

		TextColumn<Post> visibleColumn = new TextColumn<Post>() {

			@Override
			public String getValue(Post object) {
				return object.visible.toString();
			}
		};

		TextColumn<Post> dateColumn = new TextColumn<Post>() {

			@Override
			public String getValue(Post object) {
				return DateTimeFormat.getFormat(PredefinedFormat.DATE_FULL).format(object.created);
			}
		};

		TextColumn<Post> publishedColumn = new TextColumn<Post>() {

			@Override
			public String getValue(Post object) {
				return object.published == null ? "No" : DateTimeFormat.getFormat(PredefinedFormat.DATE_FULL).format(object.published);
			}
		};

		SafeHtmlCell prototype = new SafeHtmlCell();
		Column<Post, SafeHtml> editColumn = new Column<Post, SafeHtml>(prototype) {

			@Override
			public SafeHtml getValue(Post object) {
				return SafeHtmlUtils.fromTrustedString("<a href=\"" + PageType.BlogEditPostPageType.asHref("change", LookupHelper.reference(object)).asString()
						+ "\" class=\"btn btn-xs btn-default\">Edit</a>");
			}
		};

		Column<Post, SafeHtml> viewColumn = new Column<Post, SafeHtml>(prototype) {

			@Override
			public SafeHtml getValue(Post object) {
				return SafeHtmlUtils.fromTrustedString("<a href=\""
						+ PageType.BlogPostPageType.asHref(NavigationController.VIEW_ACTION_PARAMETER_VALUE, LookupHelper.reference(object)).asString()
						+ "\" class=\"btn btn-xs btn-default\">View</a>");
			}
		};

		StyledButtonCell prototype1 = new StyledButtonCell("btn", "btn-xs", "btn-danger");
		Column<Post, String> deleteColumn = new Column<Post, String>(prototype1) {

			@Override
			public String getValue(Post object) {
				return "Delete";
			}
		};
		deleteColumn.setFieldUpdater(new FieldUpdater<Post, String>() {

			@Override
			public void update(int index, Post object, String value) {
				PostController.get().deletePost(LookupHelper.reference(object));
			}
		});

		posts.addColumn(idColumn, "Id");
		posts.addColumn(authorColumn, "Author");
		posts.addColumn(titleColumn, "Title");
		posts.addColumn(visibleColumn, "Visible");
		posts.addColumn(dateColumn, "Date");
		posts.addColumn(publishedColumn, "Published");
		posts.addColumn(editColumn);
		posts.addColumn(viewColumn);
		posts.addColumn(deleteColumn);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.blog.shared.call.event.GetPostsEventHandler#getPostsSuccess(io.reflection.app.api.blog.shared.call.GetPostsRequest,
	 * io.reflection.app.api.blog.shared.call.GetPostsResponse)
	 */
	@Override
	public void getPostsSuccess(GetPostsRequest input, GetPostsResponse output) {
		if (output.status.equals(StatusType.StatusTypeSuccess)) {
			if (PostController.get().getPostsCount() > output.pager.count) {
				simplePager.setVisible(true);
			} else {
				simplePager.setVisible(false);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.blog.shared.call.event.GetPostsEventHandler#getPostsFailure(io.reflection.app.api.blog.shared.call.GetPostsRequest,
	 * java.lang.Throwable)
	 */
	@Override
	public void getPostsFailure(GetPostsRequest input, Throwable caught) {}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.NavigationEventHandler#navigationChanged(io.reflection.app.client.controller.NavigationController.Stack,
	 * io.reflection.app.client.controller.NavigationController.Stack)
	 */
	@Override
	public void navigationChanged(Stack previous, Stack current) {
		// Show pager if data loaded in User Blog page
		if (PostController.get().hasPosts() && PostController.get().getPostsCount() > posts.getVisibleItemCount()) {
			simplePager.setVisible(true);
		}
	}
}
