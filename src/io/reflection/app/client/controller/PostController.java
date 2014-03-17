//
//  PostController.java
//  storedata
//
//  Created by William Shakour (billy1380) on 16 Mar 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.client.controller;

import io.reflection.app.api.blog.client.BlogService;
import io.reflection.app.api.blog.shared.call.CreatePostRequest;
import io.reflection.app.api.blog.shared.call.CreatePostResponse;
import io.reflection.app.api.blog.shared.call.GetPostsRequest;
import io.reflection.app.api.blog.shared.call.GetPostsResponse;
import io.reflection.app.api.blog.shared.call.event.CreatePostEventHandler.CreatePostFailure;
import io.reflection.app.api.blog.shared.call.event.CreatePostEventHandler.CreatePostSuccess;
import io.reflection.app.api.blog.shared.call.event.GetPostsEventHandler.GetPostsFailure;
import io.reflection.app.api.blog.shared.call.event.GetPostsEventHandler.GetPostsSuccess;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.api.shared.datatypes.SortDirectionType;
import io.reflection.app.datatypes.shared.Post;
import io.reflection.app.datatypes.shared.Tag;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import com.willshex.gson.json.service.shared.StatusType;

/**
 * @author billy1380
 * 
 */
public class PostController extends AsyncDataProvider<Post> implements ServiceController {

	private List<Post> posts = new ArrayList<Post>();
	private long count = 0;
	private Pager pager;

	private static PostController one = null;

	public static PostController get() {
		if (one == null) {
			one = new PostController();
		}

		return one;
	}

	private void fetchPosts() {

		BlogService service = new BlogService();
		service.setUrl(BLOG_END_POINT);

		final GetPostsRequest input = new GetPostsRequest();
		input.accessCode = ACCESS_CODE;

		input.session = SessionController.get().getSessionForApiCall();
		input.includeContents = Boolean.FALSE;

		if (pager == null) {
			pager = new Pager();
			pager.count = SHORT_STEP;
			pager.start = Long.valueOf(0);
			pager.sortDirection = SortDirectionType.SortDirectionTypeDescending;
		}
		input.pager = pager;

		service.getPosts(input, new AsyncCallback<GetPostsResponse>() {

			@Override
			public void onSuccess(GetPostsResponse output) {
				if (output.status == StatusType.StatusTypeSuccess) {
					if (output.posts != null) {
						posts.addAll(output.posts);
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
							posts.subList(input.pager.start.intValue(),
									Math.min(input.pager.start.intValue() + input.pager.count.intValue(), pager.totalCount.intValue())));
				}

				EventController.get().fireEventFromSource(new GetPostsSuccess(input, output), PostController.this);
			}

			@Override
			public void onFailure(Throwable caught) {
				EventController.get().fireEventFromSource(new GetPostsFailure(input, caught), PostController.this);
			}
		});
	}

	public List<Post> getPosts() {
		return posts;
	}

	public long getPostsCount() {
		return count;
	}

	public boolean hasPosts() {
		return pager != null || posts.size() > 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.view.client.AbstractDataProvider#onRangeChanged(com.google.gwt.view.client.HasData)
	 */
	@Override
	protected void onRangeChanged(HasData<Post> display) {
		Range r = display.getVisibleRange();

		int start = r.getStart();
		int end = start + r.getLength();

		if (end > posts.size()) {
			fetchPosts();
		} else {
			updateRowData(start, posts.subList(start, end));
		}
	}

	/**
	 * 
	 * @param postId
	 * @param title
	 * @param description
	 * @param content
	 * @param publish
	 * @param tags
	 */
	public void updatePost(Long postId, String title, Boolean visible, String description, String content, Boolean publish, String tags) {

	}

	/**
	 * 
	 * @param title
	 * @param description
	 * @param content
	 * @param publish
	 * @param tags
	 */
	public void createPost(String title, Boolean visible, String description, String content, Boolean publish, String tags) {
		BlogService service = new BlogService();
		service.setUrl(BLOG_END_POINT);

		final CreatePostRequest input = new CreatePostRequest();
		input.accessCode = ACCESS_CODE;

		input.session = SessionController.get().getSessionForApiCall();
		input.title = title;
		input.description = description;
		input.content = content;
		input.publish = publish;
		input.visible = visible;

		input.tags = convertToTags(tags);

		service.createPost(input, new AsyncCallback<CreatePostResponse>() {

			@Override
			public void onSuccess(CreatePostResponse output) {
				if (output.status == StatusType.StatusTypeSuccess) {}

				EventController.get().fireEventFromSource(new CreatePostSuccess(input, output), PostController.this);
			}

			@Override
			public void onFailure(Throwable caught) {
				EventController.get().fireEventFromSource(new CreatePostFailure(input, caught), PostController.this);
			}
		});
	}

	/**
	 * @param tags
	 * @return
	 */
	private List<Tag> convertToTags(String tags) {
		List<Tag> tagList = null;

		if (tags != null && tags.length() == 0) {
			String[] splitTags = tags.split(",");

			for (String item : splitTags) {
				String trimmed = item.trim().toLowerCase();

				if (trimmed.length() > 0) {
					if (tagList != null) {
						tagList = new ArrayList<Tag>();
					}

					Tag tag = new Tag();
					tag.name = trimmed;

					tagList.add(tag);
				}
			}
		}

		return tagList;
	}

	public void reset() {
		pager = null;
		posts = null;

		updateRowData(0, new ArrayList<Post>());
		updateRowCount(0, false);

		fetchPosts();
	}

}
