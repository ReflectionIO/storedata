//
//  ForumController.java
//  storedata
//
//  Created by William Shakour (billy1380) on 5 Apr 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.controller;

import io.reflection.app.api.forum.client.ForumService;
import io.reflection.app.api.forum.shared.call.GetForumsRequest;
import io.reflection.app.api.forum.shared.call.GetForumsResponse;
import io.reflection.app.api.forum.shared.call.event.GetForumsEventHandler.GetForumsFailure;
import io.reflection.app.api.forum.shared.call.event.GetForumsEventHandler.GetForumsSuccess;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.api.shared.datatypes.SortDirectionType;
import io.reflection.app.client.DefaultEventBus;
import io.reflection.app.datatypes.shared.Forum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author billy1380
 * 
 */
public class ForumController extends AsyncDataProvider<Forum> implements ServiceConstants {
    private List<Forum> forums = null;
    private Map<Long, Forum> forumMap = null;
    private long count = 0;
    private Pager pager;

    private static ForumController one = null;

    public static ForumController get() {
        if (one == null) {
            one = new ForumController();
        }

        return one;
    }

    private void fetchForums() {

        ForumService service = ServiceCreator.createForumService();

        final GetForumsRequest input = new GetForumsRequest();
        input.accessCode = ACCESS_CODE;

        input.session = SessionController.get().getSessionForApiCall();

        if (pager == null) {
            pager = new Pager();
            pager.count = SHORT_STEP;
            pager.start = Long.valueOf(0);
            pager.sortDirection = SortDirectionType.SortDirectionTypeDescending;
        }

        input.pager = pager;

        service.getForums(input, new AsyncCallback<GetForumsResponse>() {

            @Override
            public void onSuccess(GetForumsResponse output) {
                if (output.status == StatusType.StatusTypeSuccess) {
                    if (output.forums != null) {
                        if (forums == null) {
                            forums = new ArrayList<Forum>();
                            forumMap = new HashMap<Long, Forum>();
                        }

                        forums.addAll(output.forums);
                        for (Forum forum : forums)
                            forumMap.put(forum.id, forum);
                    }

                    if (output.pager != null) {
                        pager = output.pager;

                        if (pager.totalCount != null) {
                            count = pager.totalCount.longValue();
                        }
                    }

                    updateRowCount((int) count, true);
                    updateRowData(
                            input.pager.start.intValue(),
                            forums == null ? new ArrayList<Forum>() : forums.subList(input.pager.start.intValue(), Math.min(input.pager.start.intValue()
                                    + input.pager.count.intValue(), count == 0 ? (forums == null ? 0 : forums.size()) : (int) count)));
                }

                DefaultEventBus.get().fireEventFromSource(new GetForumsSuccess(input, output), ForumController.this);
            }

            @Override
            public void onFailure(Throwable caught) {
                DefaultEventBus.get().fireEventFromSource(new GetForumsFailure(input, caught), ForumController.this);
            }
        });
    }

    public List<Forum> getForums() {
        if (pager == null) {
            fetchForums();
        }

        return forums;
    }

    public long getForumsCount() {
        return count;
    }

    public boolean hasForums() {
        return pager != null && forums != null && forums.size() > 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.gwt.view.client.AbstractDataProvider#onRangeChanged(com.google.gwt.view.client.HasData)
     */
    @Override
    protected void onRangeChanged(HasData<Forum> display) {
        Range r = display.getVisibleRange();

        int start = r.getStart();
        int end = start + r.getLength();

        if (!hasForums() || end > forums.size()) {
            fetchForums();
        }

        updateRowData(start, forums == null || forums.size() == 0 ? new ArrayList<Forum>() : forums.subList(start, Math.min(forums.size(), end)));
    }

    /**
     * 
     * @param id
     * @param title
     * @param visible
     * @param commentsEnabled
     * @param description
     * @param content
     * @param publish
     * @param tags
     */
    public void updateForum(Long id, String title, Boolean visible, Boolean commentsEnabled, String description, String content, Boolean publish, String tags) {
        // ForumService service = ServiceCreator.createForumService();
        //
        // final UpdateForumRequest input = new UpdateForumRequest();
        // input.accessCode = ACCESS_CODE;
        //
        // input.session = SessionController.get().getSessionForApiCall();
        // input.forum = forumLookup.get(id.intValue());
        //
        // input.forum.title = title;
        // input.forum.description = description;
        // input.forum.content = content;
        //
        // input.publish = publish;
        //
        // input.forum.visible = visible;
        // input.forum.commentsEnabled = commentsEnabled;
        //
        // input.forum.tags = TagHelper.convertToTagList(tags);
        //
        // service.updateForum(input, new AsyncCallback<UpdateForumResponse>() {
        //
        // @Override
        // public void onSuccess(UpdateForumResponse output) {
        // if (output.status == StatusType.StatusTypeSuccess) {
        // reset();
        // }
        //
        // EventController.get().fireEventFromSource(new UpdateForumSuccess(input, output), ForumController.this);
        // }
        //
        // @Override
        // public void onFailure(Throwable caught) {
        // EventController.get().fireEventFromSource(new UpdateForumFailure(input, caught), ForumController.this);
        // }
        // });
    }

    /**
     * 
     * @param title
     * @param visible
     * @param commentsEnabled
     * @param description
     * @param content
     * @param publish
     * @param tags
     */
    public void createForum(String title, Boolean visible, Boolean commentsEnabled, String description, String content, Boolean publish, String tags) {
        // ForumService service = ServiceCreator.createForumService();
        //
        // final CreateForumRequest input = new CreateForumRequest();
        // input.accessCode = ACCESS_CODE;
        //
        // input.session = SessionController.get().getSessionForApiCall();
        //
        // input.forum = new Forum();
        //
        // input.forum.title = title;
        // input.forum.description = description;
        // input.forum.content = content;
        // input.publish = publish;
        // input.forum.visible = visible;
        // input.forum.commentsEnabled = commentsEnabled;
        //
        // input.forum.tags = TagHelper.convertToTagList(tags);
        //
        // service.createForum(input, new AsyncCallback<CreateForumResponse>() {
        //
        // @Override
        // public void onSuccess(CreateForumResponse output) {
        // if (output.status == StatusType.StatusTypeSuccess) {}
        //
        // EventController.get().fireEventFromSource(new CreateForumSuccess(input, output), ForumController.this);
        // }
        //
        // @Override
        // public void onFailure(Throwable caught) {
        // EventController.get().fireEventFromSource(new CreateForumFailure(input, caught), ForumController.this);
        // }
        // });
    }

    public void reset() {
        pager = null;

        forums = null;

        updateRowData(0, new ArrayList<Forum>());
        updateRowCount(0, false);

        fetchForums();
    }

    public void deleteForum(Long forumId) {
        // ForumService service = ServiceCreator.createForumService();
        //
        // final DeleteForumRequest input = new DeleteForumRequest();
        // input.accessCode = ACCESS_CODE;
        //
        // input.session = SessionController.get().getSessionForApiCall();
        //
        // input.forum = new Forum();
        // input.forum.id = forumId;
        //
        // service.deleteForum(input, new AsyncCallback<DeleteForumResponse>() {
        //
        // @Override
        // public void onSuccess(DeleteForumResponse output) {
        // if (output.status == StatusType.StatusTypeSuccess) {}
        //
        // EventController.get().fireEventFromSource(new DeleteForumSuccess(input, output), ForumController.this);
        // }
        //
        // @Override
        // public void onFailure(Throwable caught) {
        // EventController.get().fireEventFromSource(new DeleteForumFailure(input, caught), ForumController.this);
        // }
        // });
    }

    /**
     * @param newSelectedId
     * @return
     */
    public Forum getForumById(Long newSelectedId) {
        Forum result = null;
        if (forumMap == null) {
            fetchForums();
        } else {
            result = forumMap.get(newSelectedId);
        }
        return result;
    }

    /**
     * @return
     */
    public Forum getFirstForum() {
        Forum result = null;
        if (forums != null) {
            result = forums.get(0);
        }
        return result;
    }

}
