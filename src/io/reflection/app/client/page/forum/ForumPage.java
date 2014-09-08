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
import io.reflection.app.client.controller.ServiceConstants;
import io.reflection.app.client.controller.TopicController;
import io.reflection.app.client.handler.NavigationEventHandler;
import io.reflection.app.client.page.Page;
import io.reflection.app.client.page.PageType;
import io.reflection.app.client.page.forum.part.ForumSummarySidePanel;
import io.reflection.app.client.part.BootstrapGwtCellTable;
import io.reflection.app.client.part.NumberedPager;
import io.reflection.app.client.res.Images;
import io.reflection.app.datatypes.shared.Forum;
import io.reflection.app.datatypes.shared.Topic;
import io.reflection.app.datatypes.shared.User;
import io.reflection.app.shared.util.FormattingHelper;

import java.util.Date;

import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safecss.shared.SafeStyles;
import com.google.gwt.safecss.shared.SafeStylesUtils;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.SafeHtmlHeader;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.Widget;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author billy1380
 * 
 */
public class ForumPage extends Page implements NavigationEventHandler, GetForumsEventHandler {

	interface TopicTemplate extends SafeHtmlTemplates {
		TopicTemplate INSTANCE = GWT.create(TopicTemplate.class);

		@SafeHtmlTemplates.Template("<div style='float:left;width:30px;height:5px;margin-right:10px;'>{0}</div><div style='float:left;'><a href=\"{1}\" style=\"{2}\">{3}</a><div>{4} {5}</div></div>")
		SafeHtml topicLayout(SafeHtml properties, String link, SafeStyles styles, SafeHtml title, SafeHtml pages, SafeHtml pageLinks);

		@SafeHtmlTemplates.Template("<a class='{2}' href='{0}'>{1}</a>")
		SafeHtml pageLink(SafeUri lastPageLink, int i, String style);
	}

	private static ForumPageUiBinder uiBinder = GWT.create(ForumPageUiBinder.class);

	interface ForumPageUiBinder extends UiBinder<Widget, ForumPage> {}

	private static final int SELECTED_FORUM_PARAMETER_INDEX = 0;

	@UiField(provided = true) CellTable<Topic> topics = new CellTable<Topic>(ServiceConstants.SHORT_STEP_VALUE, BootstrapGwtCellTable.INSTANCE);

	@UiField NumberedPager pager;

	@UiField ForumSummarySidePanel forumSummarySidePanel;
	@UiField HeadingElement titleText;

	@UiField Button newTopicButton;

	private Forum selectedForum;

	private Long selectedForumId;

	public ForumPage() {
		initWidget(uiBinder.createAndBindUi(this));

		createColumns();

		topics.setLoadingIndicator(new Image(Images.INSTANCE.preloader()));
		topics.setEmptyTableWidget(new HTMLPanel("No topics found!"));

		pager.setDisplay(topics);

	}

	private void createColumns() {
	
		Column<Topic, SafeHtml> titleColumn = new Column<Topic, SafeHtml>(new SafeHtmlCell()) {

			@Override
			public SafeHtml getValue(Topic object) {

				String properties = "";

				if (object.locked != null && object.locked.booleanValue()) {
					properties += "<i class=\"glyphicon glyphicon-lock\"></i> ";
				} else if (object.sticky != null && object.sticky.booleanValue()) {
                    properties += "<i class=\"glyphicon glyphicon-pushpin\"></i> ";
                } else if (object.heat != null && object.heat > 10) {
					properties += "<i class=\"glyphicon glyphicon-fire\"></i> ";
				}
				
				int numPages = (int) Math.ceil((double) (object.numberOfReplies + 1) / ServiceConstants.SHORT_STEP_VALUE);

				// generate page links
				String pageLinksString = "";

				for (int i = 1; i <= numPages && numPages > 1; i++) {
					int position = (i - 1) * ServiceConstants.SHORT_STEP_VALUE;

					SafeUri lastPageLink = UriUtils.fromSafeConstant(PageType.ForumThreadPageType.asHref().asString() + "/view/" + object.id + "/post/"
							+ position);

					pageLinksString += TopicTemplate.INSTANCE.pageLink(lastPageLink, i, "forumPageAnchorMarginStyle").asString(); // asString because can't see
																																	// how to combine SafeHtmls
					// together.

				}
				SafeHtml pageLinks = SafeHtmlUtils.fromTrustedString(pageLinksString);

				// put it all together
				return TopicTemplate.INSTANCE
						.topicLayout(SafeHtmlUtils.fromSafeConstant(properties),
								PageType.ForumThreadPageType.asHref(TopicPage.VIEW_ACTION_PARAMETER_VALUE, object.id.toString()).asString(),
								SafeStylesUtils.fromTrustedString(""), SafeHtmlUtils.fromString(object.title), SafeHtmlUtils.fromString(numPages + " pages"),
								pageLinks);
			}
		};

		TextColumn<Topic> postsColumn = new TextColumn<Topic>() {

			@Override
			public String getValue(Topic object) {
				return object.numberOfReplies.toString();
			}

		};

		TextColumn<Topic> lastPosterColumn = new TextColumn<Topic>() {

			@Override
			public String getValue(Topic object) {
				User lastPoster = object.lastReplier;
				if (lastPoster == null) {
					lastPoster = object.author;
				}
				return FormattingHelper.getUserName(lastPoster);
			}
		};

		TextColumn<Topic> lastPostedColumn = new TextColumn<Topic>() {

			@Override
			public String getValue(Topic object) {
				Date lastTime = object.lastReplied;
				if (lastTime == null) {
					lastTime = object.created;
				}
				return FormattingHelper.getTimeSince(lastTime);
			}
		};

		SafeHtmlHeader titleHeader = new SafeHtmlHeader(SafeHtmlUtils.fromSafeConstant("<span style='margin-left:37px'>Topic</span>"));
		titleHeader.setHeaderStyleNames("col-sm-4");
		topics.addColumn(titleColumn, titleHeader);

		TextHeader postHeader = new TextHeader("Posts");
		postHeader.setHeaderStyleNames("col-sm-2");
		topics.addColumn(postsColumn, postHeader);

		TextHeader lastPosterHeader = new TextHeader("Last Poster");
		lastPosterHeader.setHeaderStyleNames("col-sm-2");
		topics.addColumn(lastPosterColumn, lastPosterHeader);

		TextHeader lastPostedHeader = new TextHeader("");
		lastPostedHeader.setHeaderStyleNames("col-sm-2");
		topics.addColumn(lastPostedColumn, lastPostedHeader);
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

	@Override
	protected void onDetach() {
		super.onDetach();
		reset();
	}

	/**
     * 
     */
	private void reset() {

		// this *should* clear the attached pager, combined with the next statement
		if (TopicController.get().getDataDisplays().size() > 0) {
			TopicController.get().removeDataDisplay(topics);
		}
		// got from https://groups.google.com/forum/#!topic/google-web-toolkit/cAvgdn2fmfU
		topics.setVisibleRangeAndClearData(topics.getVisibleRange(), true);

		// this hard resets the topic controller every time we leave the ForumPage.
		// it does mean that it will have to reload, but this logic is simpler than
		// currently working out when we have to use the topic controller pager and when
		// we have to discard it.
		TopicController.get().reset();

		pager.setDisplay(topics);

		forumSummarySidePanel.reset();
		titleText.setInnerHTML("");
		
		newTopicButton.setEnabled(false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.forum.shared.call.event.GetForumsEventHandler# getForumsSuccess (io.reflection.app.api.forum.shared.call.GetForumsRequest,
	 * io.reflection.app.api.forum.shared.call.GetForumsResponse)
	 */
	@Override
	public void getForumsSuccess(GetForumsRequest input, GetForumsResponse output) {
		if (output.status == StatusType.StatusTypeSuccess && output.forums != null && output.forums.size() > 0) {
			configureTitleAndSidePanel();
		}
	}

	/**
	 * This may either run from Navigation changed, or after Forum callback
	 */
	protected void configureTitleAndSidePanel() {
		if (ForumController.get().hasForums()) {
			if (selectedForumId != null) {
				selectedForum = ForumController.get().getForumById(selectedForumId);
			} else {
				selectedForum = ForumController.get().getFirstForum();
				selectedForumId = selectedForum.id;
				TopicController.get().getTopics(selectedForumId);
			}
			forumSummarySidePanel.selectItem(selectedForum);

			// shouldn't be null unless an error has occurred.
			if (selectedForum != null) {
				titleText.setInnerText(selectedForum.title);
			}
			forumSummarySidePanel.redraw();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.api.forum.shared.call.event.GetForumsEventHandler# getForumsFailure (io.reflection.app.api.forum.shared.call.GetForumsRequest,
	 * java.lang.Throwable)
	 */
	@Override
	public void getForumsFailure(GetForumsRequest input, Throwable caught) {}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.handler.NavigationEventHandler#navigationChanged (io.reflection.app.client.controller.NavigationController.Stack,
	 * io.reflection.app.client.controller.NavigationController.Stack)
	 */
	@Override
	public void navigationChanged(Stack previous, Stack current) {
		if (current != null && PageType.ForumPageType.equals(current.getPage())) {

			//No caching, just to get it working.
			//There is still caching for using the pager on a ForumPage, just not navigating away from and to the same ForumPage.
			//We don't cache for now because we may be returning to this page from the beginning, but the pager thinks it is on page 2.
			//Adding the datadisplay back again (which is needed because of the need to disconnect it in other situations)
			//will cause an async data provider to fire getting perhaps unecessary data or worse an edge case. On return it will force the display of
			//that data even though we're starting from the beginning.
			TopicController.get().reset();
			if (!TopicController.get().getDataDisplays().contains(topics)) {
				// this triggers a range change update!
				TopicController.get().addDataDisplay(topics);
			}

			// always reset to 0
			pager.setPageStart(0);

			String selectedIdString;
			if ((selectedIdString = current.getParameter(SELECTED_FORUM_PARAMETER_INDEX)) != null) {

				Long newSelectedId = Long.valueOf(selectedIdString);
				selectedForumId = newSelectedId;
				selectedForum = null;
				TopicController.get().getTopics(selectedForumId);

				configureTitleAndSidePanel();

			} else {
				// needs to be reset in case we're coming back to this page. The next call will set them.
				selectedForumId = null;
				selectedForum = null;

				// This call also resets the default selected forum provided selectedForum/Id is null.
				configureTitleAndSidePanel();
			}

			newTopicButton.setEnabled(true);
		}
	}
	
	@UiHandler("newTopicButton")
    void newTopicClick(ClickEvent event) {
	    if (selectedForumId != null) {
	        PageType.ForumTopicPageType.show("new", selectedForumId.toString()) ;
	    }
	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.client.page.Page#getTitle()
	 */
	@Override
	public String getTitle() {
		return "Reflection.io: Forum";
	}

}
