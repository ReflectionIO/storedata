//
//  AdminPage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 16 Mar 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page.blog;

import io.reflection.app.client.controller.PostController;
import io.reflection.app.client.controller.ServiceConstants;
import io.reflection.app.client.controller.SessionController;
import io.reflection.app.client.page.Page;
import io.reflection.app.client.part.BootstrapGwtCellTable;
import io.reflection.app.client.part.SimplePager;
import io.reflection.app.datatypes.shared.Post;
import io.reflection.app.datatypes.shared.User;

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
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author billy1380
 * 
 */
public class AdminPage extends Page {

	private static AdminPageUiBinder uiBinder = GWT.create(AdminPageUiBinder.class);

	interface AdminPageUiBinder extends UiBinder<Widget, AdminPage> {}

	@UiField(provided = true) CellTable<Post> posts = new CellTable<Post>(ServiceConstants.SHORT_STEP_VALUE, BootstrapGwtCellTable.INSTANCE);
	@UiField(provided = true) SimplePager pager = new SimplePager(false, false);

	public AdminPage() {
		initWidget(uiBinder.createAndBindUi(this));

		createColumns();

		PostController.get().addDataDisplay(posts);
		pager.setDisplay(posts);
	}

	private void createColumns() {
		TextColumn<Post> idColumn = new TextColumn<Post>() {

			@Override
			public String getValue(Post object) {
				return object.id.toString();
			}

		};

		TextHeader idHeader = new TextHeader("Id");
		posts.addColumn(idColumn, idHeader);

		TextColumn<Post> authorColumn = new TextColumn<Post>() {

			@Override
			public String getValue(Post object) {
				User loggedIn = SessionController.get().getLoggedInUser();

				return loggedIn != null && object.author.id == loggedIn.id ? "Me" : object.author.toString();
			}

		};

		TextHeader authorHeader = new TextHeader("Author");
		posts.addColumn(authorColumn, authorHeader);

		TextColumn<Post> titleColumn = new TextColumn<Post>() {

			@Override
			public String getValue(Post object) {
				return object.title;
			}

		};
		posts.addColumn(titleColumn, "Title");

		TextColumn<Post> visibleColumn = new TextColumn<Post>() {

			@Override
			public String getValue(Post object) {
				return object.visible.toString();
			}
		};

		posts.addColumn(visibleColumn, "Visible");

		TextColumn<Post> dateColumn = new TextColumn<Post>() {

			@Override
			public String getValue(Post object) {
				return DateTimeFormat.getFormat(PredefinedFormat.DATE_FULL).format(object.created);
			}
		};

		posts.addColumn(dateColumn, "Date");
		
		TextColumn<Post> publishedColumn = new TextColumn<Post>() {

			@Override
			public String getValue(Post object) {
				return object.published == null ? "No" : DateTimeFormat.getFormat(PredefinedFormat.DATE_FULL).format(object.published);
			}
		};

		posts.addColumn(publishedColumn, "Published");

		Column<Post, SafeHtml> editColumn = new Column<Post, SafeHtml>(new SafeHtmlCell()) {

			@Override
			public SafeHtml getValue(Post object) {
				String s = object.id.toString();

				return SafeHtmlUtils.fromTrustedString("<a href=\"#blogedit/edit/" + s + "\" class=\"btn btn-xs btn-default\">Edit</a>");
			}
		};

		posts.addColumn(editColumn);
	}

}
