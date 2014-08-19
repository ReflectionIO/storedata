//
//  AddTopicPage.java
//  storedata
//
//  Created by William Shakour (billy1380) on 5 Apr 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.page.forum;

import io.reflection.app.api.forum.shared.call.CreateTopicRequest;
import io.reflection.app.api.forum.shared.call.CreateTopicResponse;
import io.reflection.app.api.forum.shared.call.GetForumsRequest;
import io.reflection.app.api.forum.shared.call.GetForumsResponse;
import io.reflection.app.api.forum.shared.call.event.CreateTopicEventHandler;
import io.reflection.app.api.forum.shared.call.event.GetForumsEventHandler;
import io.reflection.app.client.controller.EventController;
import io.reflection.app.client.controller.ForumController;
import io.reflection.app.client.controller.TopicController;
import io.reflection.app.client.helper.FilterHelper;
import io.reflection.app.client.page.Page;
import io.reflection.app.client.page.PageType;
import io.reflection.app.client.page.forum.part.ForumSummarySidePanel;
import io.reflection.app.client.part.text.MarkdownEditor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author billy1380
 * 
 */
public class AddTopicPage extends Page implements CreateTopicEventHandler, GetForumsEventHandler {

    @UiField TextBox title;
    @UiField TextBox tags;

    @UiField ListBox forums;
    
    @UiField MarkdownEditor contentText;

    @UiField ForumSummarySidePanel forumSummarySidePanel;

    private static AddTopicPageUiBinder uiBinder = GWT.create(AddTopicPageUiBinder.class);

    interface AddTopicPageUiBinder extends UiBinder<Widget, AddTopicPage> {}

    public AddTopicPage() {
        initWidget(uiBinder.createAndBindUi(this));

        title.getElement().setAttribute("placeholder", "Title");
        tags.getElement().setAttribute("placeholder", "Comma separated tags");

        FilterHelper.addForums(forums);
    }

    /*
     * (non-Javadoc)
     * 
     * @see io.reflection.app.client.page.Page#onAttach()
     */
    @Override
    protected void onAttach() {
        super.onAttach();

        register(EventController.get().addHandlerToSource(CreateTopicEventHandler.TYPE, TopicController.get(), this));
        register(EventController.get().addHandlerToSource(GetForumsEventHandler.TYPE, ForumController.get(), this));

        resetForm();
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

    @UiHandler("submit")
    void onSubmit(ClickEvent e) {
        if (validate()) {
            Long forumId = null;

            if (forums.getItemCount() > 0) {
                String forumIdString = forums.getValue(forums.getSelectedIndex());
                forumId = Long.valueOf(forumIdString);
            }

            if (forumId != null) {
                TopicController.get().createTopic(forumId, title.getText(), false, contentText.getText(), tags.getText());
            }
        }
    }

    private boolean validate() {
        return true;
    }

    private void resetForm() {

        title.setText("");
        contentText.setText("");
        tags.setText("");
        
        forumSummarySidePanel.redraw();

        // hide errors and remove clear validation strings
    }

    /*
     * (non-Javadoc)
     * 
     * @see io.reflection.app.api.forum.shared.call.event.CreateTopicEventHandler#createTopicSuccess
     * (io.reflection.app.api.forum.shared.call.CreateTopicRequest,io.reflection.app.api.forum.shared.call.CreateTopicResponse)
     */
    @Override
    public void createTopicSuccess(CreateTopicRequest input, CreateTopicResponse output) {
        if (output.status == StatusType.StatusTypeSuccess) {
            TopicController.get().reset();
            PageType.ForumThreadPageType.show("view", output.topic.id.toString());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see io.reflection.app.api.forum.shared.call.event.CreateTopicEventHandler#createTopicFailure
     * (io.reflection.app.api.forum.shared.call.CreateTopicRequest,java.lang.Throwable)
     */
    @Override
    public void createTopicFailure(CreateTopicRequest input, Throwable caught) {}

    /*
     * (non-Javadoc)
     * 
     * @see io.reflection.app.api.forum.shared.call.event.GetForumsEventHandler#getForumsSuccess
     * (io.reflection.app.api.forum.shared.call.GetForumsRequest,io.reflection.app.api.forum.shared.call.GetForumsResponse)
     */
    @Override
    public void getForumsSuccess(GetForumsRequest input, GetForumsResponse output) {
        forums.clear();

        FilterHelper.addForums(forums);
    }

    /*
     * (non-Javadoc)
     * 
     * @see io.reflection.app.api.forum.shared.call.event.GetForumsEventHandler#getForumsFailure
     * (io.reflection.app.api.forum.shared.call.GetForumsRequest,java.lang.Throwable)
     */
    @Override
    public void getForumsFailure(GetForumsRequest input, Throwable caught) {}
}
