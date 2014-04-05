//
//  ForumPage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 5 Apr 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page.forum;

import io.reflection.app.api.forum.shared.call.GetForumsRequest;
import io.reflection.app.api.forum.shared.call.GetForumsResponse;
import io.reflection.app.api.forum.shared.call.event.GetForumsEventHandler;
import io.reflection.app.client.controller.EventController;
import io.reflection.app.client.controller.ForumController;
import io.reflection.app.client.controller.NavigationController;
import io.reflection.app.client.controller.NavigationController.Stack;
import io.reflection.app.client.controller.TopicController;
import io.reflection.app.client.handler.NavigationEventHandler;
import io.reflection.app.client.page.Page;
import io.reflection.app.client.page.PageType;
import io.reflection.app.client.page.forum.part.ForumSummaryCell;
import io.reflection.app.client.part.BootstrapGwtCellList;
import io.reflection.app.client.part.BootstrapGwtCellTable;
import io.reflection.app.client.part.SimplePager;
import io.reflection.app.datatypes.shared.Forum;
import io.reflection.app.datatypes.shared.Topic;

import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SingleSelectionModel;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author billy1380
 * 
 */
public class ForumPage extends Page implements NavigationEventHandler, GetForumsEventHandler {

	private static ForumPageUiBinder uiBinder = GWT.create(ForumPageUiBinder.class);

	interface ForumPageUiBinder extends UiBinder<Widget, ForumPage> {}

	private static final int SELECTED_FORUM_PARAMETER_INDEX = 0;

	@UiField(provided = true) CellTable<Topic> topics = new CellTable<Topic>(Integer.MAX_VALUE, BootstrapGwtCellTable.INSTANCE);
	@UiField(provided = true) CellList<Forum> forums = new CellList<Forum>(new ForumSummaryCell(), BootstrapGwtCellList.INSTANCE);

	@UiField SimplePager pager;

	private Integer selectedIndex;

	public ForumPage() {
		initWidget(uiBinder.createAndBindUi(this));

		createColumns();

		TopicController.get().addDataDisplay(topics);
		pager.setDisplay(topics);

		ForumController.get().addDataDisplay(forums);
	}

	private void createColumns() {
		Column<Topic, SafeHtml> titleColumn = new Column<Topic, SafeHtml>(new SafeHtmlCell()) {

			@Override
			public SafeHtml getValue(Topic object) {
				return SafeHtmlUtils.fromString(object.title);
			}
		};

		TextColumn<Topic> postsColumn = new TextColumn<Topic>() {

			@Override
			public String getValue(Topic object) {
				return "";
			}

		};

		TextColumn<Topic> lastPosterColumn = new TextColumn<Topic>() {

			@Override
			public String getValue(Topic object) {
				return "";
			}
		};

		TextColumn<Topic> freshnessColumn = new TextColumn<Topic>() {

			@Override
			public String getValue(Topic object) {
				return "";
			}
		};

		TextHeader titleHeader = new TextHeader("Topic");
		titleHeader.setHeaderStyleNames("col-sm-10");
		topics.addColumn(titleColumn, titleHeader);

		TextHeader postHeader = new TextHeader("Posts");

		topics.addColumn(postsColumn, postHeader);

		TextHeader lastPosterHeader = new TextHeader("Last Poster");
		lastPosterHeader.setHeaderStyleNames("col-sm-1");
		topics.addColumn(lastPosterColumn, lastPosterHeader);

		TextHeader freshnessHeader = new TextHeader("Freshness");
		freshnessHeader.setHeaderStyleNames("col-sm-1");
		topics.addColumn(freshnessColumn, freshnessHeader);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.page.Page#onAttach()
	 */
	@Override
	protected void onAttach() {
		super.onAttach();

		register(EventController.get().addHandlerToSource(GetForumsEventHandler.TYPE, ForumController.get(), this));
		register(EventController.get().addHandlerToSource(NavigationEventHandler.TYPE, NavigationController.get(), this));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.forum.shared.call.event.GetForumsEventHandler#getForumsSuccess(io.reflection.app.api.forum.shared.call.GetForumsRequest,
	 * io.reflection.app.api.forum.shared.call.GetForumsResponse)
	 */
	@Override
	public void getForumsSuccess(GetForumsRequest input, GetForumsResponse output) {
		if (output.status == StatusType.StatusTypeSuccess && output.forums != null && output.forums.size() > 0) {
			if (selectedIndex == null) {
				selectedIndex = Integer.valueOf(0);
			}

			SingleSelectionModel<Forum> selectionModel = new SingleSelectionModel<Forum>();
			selectionModel.setSelected(forums.getVisibleItem(selectedIndex.intValue()), true);
			forums.setSelectionModel(selectionModel);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.forum.shared.call.event.GetForumsEventHandler#getForumsFailure(io.reflection.app.api.forum.shared.call.GetForumsRequest,
	 * java.lang.Throwable)
	 */
	@Override
	public void getForumsFailure(GetForumsRequest input, Throwable caught) {}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.NavigationEventHandler#navigationChanged(io.reflection.app.client.controller.NavigationController.Stack,
	 * io.reflection.app.client.controller.NavigationController.Stack)
	 */
	@Override
	public void navigationChanged(Stack previous, Stack current) {
		if (current != null && PageType.ForumPageType.equals(current.getPage())) {

			String selectedIndexString;
			if ((selectedIndexString = current.getParameter(SELECTED_FORUM_PARAMETER_INDEX)) != null) {
				selectedIndex = Integer.valueOf(selectedIndexString);
			}
		}
	}

}
