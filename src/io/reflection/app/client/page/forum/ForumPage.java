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
import io.reflection.app.client.part.SimplePager;
import io.reflection.app.client.res.Images;
import io.reflection.app.datatypes.shared.Forum;
import io.reflection.app.datatypes.shared.Topic;
import io.reflection.app.shared.util.FormattingHelper;

import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.safecss.shared.SafeStyles;
import com.google.gwt.safecss.shared.SafeStylesUtils;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author billy1380
 * 
 */
public class ForumPage extends Page implements NavigationEventHandler, GetForumsEventHandler {

    interface TopicTemplate extends SafeHtmlTemplates {
        TopicTemplate INSTANCE = GWT.create(TopicTemplate.class);

        @SafeHtmlTemplates.Template("<div>{0} <a href=\"{1}\"" + " style=\"{2}\">{3}</a></div><div>{4} {5}</div>")
        SafeHtml topicLayout(SafeHtml properties, String link, SafeStyles styles, SafeHtml title, SafeHtml pages, SafeHtml pageLinks);

        @SafeHtmlTemplates.Template("<a style='margin-left:3px' href='{0}'>{1}</a>")
        SafeHtml pageLink(SafeUri lastPageLink, int i);
    }

    private static ForumPageUiBinder uiBinder = GWT.create(ForumPageUiBinder.class);

    interface ForumPageUiBinder extends UiBinder<Widget, ForumPage> {}

    private static final int SELECTED_FORUM_PARAMETER_INDEX = 0;

    @UiField(provided = true) CellTable<Topic> topics = new CellTable<Topic>(ServiceConstants.SHORT_STEP_VALUE, BootstrapGwtCellTable.INSTANCE);

    @UiField SimplePager pager;

    @UiField ForumSummarySidePanel forumSummarySidePanel;
    @UiField HeadingElement titleText;

    private Forum selectedForum;

    private Long selectedForumId;

    public ForumPage() {
        initWidget(uiBinder.createAndBindUi(this));

        createColumns();

        topics.setLoadingIndicator(new Image(Images.INSTANCE.preloader()));
        topics.setEmptyTableWidget(new HTMLPanel("No topics found!"));

        TopicController.get().addDataDisplay(topics);
        pager.setDisplay(topics);

    }

    private void createColumns() {
        Column<Topic, SafeHtml> titleColumn = new Column<Topic, SafeHtml>(new SafeHtmlCell()) {

            @Override
            public SafeHtml getValue(Topic object) {

                String properties = "";

                if (object.locked != null && object.locked.booleanValue()) {
                    properties += "<i class=\"glyphicon glyphicon-lock\"></i> ";
                }

                if (object.heat != null && object.heat > 10) {
                    properties += "<i class=\"glyphicon glyphicon-fire\"></i> ";
                }

                if (object.sticky != null && object.sticky.booleanValue()) {
                    properties += "<i class=\"glyphicon glyphicon-pushpin\"></i> ";
                }

                int numPages = (int) Math.ceil((double) (object.numberOfReplies + 1) / ServiceConstants.SHORT_STEP_VALUE);

                // generate page links
                String pageLinksString = "";

                for (int i = 1; i <= numPages && numPages > 1; i++) {
                    int position = i * ServiceConstants.SHORT_STEP_VALUE;
                    if (i == numPages) {
                        position = object.numberOfReplies;
                    }

                    SafeUri lastPageLink = UriUtils.fromSafeConstant(PageType.ForumThreadPageType.asHref().asString() + "/view/" + object.id + "/post/"
                            + position);
                    
                    
                    pageLinksString += TopicTemplate.INSTANCE.pageLink(lastPageLink, i).asString(); //asString because can't see how to combine SafeHtmls together.

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
                return FormattingHelper.getUserName(object.lastReplier);
            }
        };

        TextColumn<Topic> lastPostedColumn = new TextColumn<Topic>() {

            @Override
            public String getValue(Topic object) {
                return FormattingHelper.getTimeSince(object.lastReplied);
            }
        };

        TextHeader titleHeader = new TextHeader("Topic");
        titleHeader.setHeaderStyleNames("col-sm-9");
        topics.addColumn(titleColumn, titleHeader);

        TextHeader postHeader = new TextHeader("Posts");

        topics.addColumn(postsColumn, postHeader);

        TextHeader lastPosterHeader = new TextHeader("Last Poster");
        lastPosterHeader.setHeaderStyleNames("col-sm-2");
        topics.addColumn(lastPosterColumn, lastPosterHeader);

        TextHeader lastPostedHeader = new TextHeader("");
        lastPostedHeader.setHeaderStyleNames("col-sm-1");
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
            if (selectedForum != null) // shouldn't be null unless an error has
                                       // occured
            {
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

            String selectedIdString;
            if ((selectedIdString = current.getParameter(SELECTED_FORUM_PARAMETER_INDEX)) != null) {

                Long newSelectedId = Long.valueOf(selectedIdString);

                if (selectedForumId == null || newSelectedId.longValue() != selectedForumId.longValue()) {
                    selectedForumId = newSelectedId;
                    selectedForum = null;
                    TopicController.get().getTopics(selectedForumId);

                    configureTitleAndSidePanel();
                }
            } else {
                // needs to be reset in case we're coming back to this page.
                selectedForumId = null;
                selectedForum = null;
            }
        }
        topics.redraw();
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
