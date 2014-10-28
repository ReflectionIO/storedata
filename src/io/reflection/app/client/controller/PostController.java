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
import io.reflection.app.api.blog.shared.call.DeletePostRequest;
import io.reflection.app.api.blog.shared.call.DeletePostResponse;
import io.reflection.app.api.blog.shared.call.GetPostRequest;
import io.reflection.app.api.blog.shared.call.GetPostResponse;
import io.reflection.app.api.blog.shared.call.GetPostsRequest;
import io.reflection.app.api.blog.shared.call.GetPostsResponse;
import io.reflection.app.api.blog.shared.call.UpdatePostRequest;
import io.reflection.app.api.blog.shared.call.UpdatePostResponse;
import io.reflection.app.api.blog.shared.call.event.CreatePostEventHandler.CreatePostFailure;
import io.reflection.app.api.blog.shared.call.event.CreatePostEventHandler.CreatePostSuccess;
import io.reflection.app.api.blog.shared.call.event.DeletePostEventHandler.DeletePostFailure;
import io.reflection.app.api.blog.shared.call.event.DeletePostEventHandler.DeletePostSuccess;
import io.reflection.app.api.blog.shared.call.event.GetPostEventHandler.GetPostFailure;
import io.reflection.app.api.blog.shared.call.event.GetPostEventHandler.GetPostSuccess;
import io.reflection.app.api.blog.shared.call.event.GetPostsEventHandler.GetPostsFailure;
import io.reflection.app.api.blog.shared.call.event.GetPostsEventHandler.GetPostsSuccess;
import io.reflection.app.api.blog.shared.call.event.UpdatePostEventHandler.UpdatePostFailure;
import io.reflection.app.api.blog.shared.call.event.UpdatePostEventHandler.UpdatePostSuccess;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.api.shared.datatypes.SortDirectionType;
import io.reflection.app.client.page.PageType;
import io.reflection.app.client.part.Preloader;
import io.reflection.app.datatypes.shared.Post;
import io.reflection.app.shared.util.SparseArray;
import io.reflection.app.shared.util.TagHelper;

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
public class PostController extends AsyncDataProvider<Post> implements ServiceConstants {

	private List<Post> posts = new ArrayList<Post>();
	private long count = -1;
	private Pager pager;
	private SparseArray<Post> postLookup = null;
	private SparseArray<Post> postsLookup = null;

	private Preloader preloaderPosts = null;

	private static PostController one = null;

	public static PostController get() {
		if (one == null) {
			one = new PostController();
		}

		return one;
	}

	public void fetchPosts() {

		if (preloaderPosts != null && NavigationController.get().getCurrentPage().equals(PageType.BlogPostsPageType)) {
			preloaderPosts.show();
		}

		BlogService service = ServiceCreator.createBlogService();

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

						if (postsLookup == null) {
							postsLookup = new SparseArray<Post>();
						}

						for (Post post : output.posts) {
							postsLookup.put(post.id.intValue(), post);
						}
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

	private void fetchPost(Long id) {
		BlogService service = ServiceCreator.createBlogService();

		final GetPostRequest input = new GetPostRequest();
		input.accessCode = ACCESS_CODE;

		input.session = SessionController.get().getSessionForApiCall();
		input.id = id;

		service.getPost(input, new AsyncCallback<GetPostResponse>() {

			@Override
			public void onSuccess(GetPostResponse output) {
				if (output.status == StatusType.StatusTypeSuccess) {
					if (output.post != null) {
						if (postLookup == null) {
							postLookup = new SparseArray<Post>();
						}

						postLookup.put(output.post.id.intValue(), output.post);
					}
				}

				EventController.get().fireEventFromSource(new GetPostSuccess(input, output), PostController.this);
			}

			@Override
			public void onFailure(Throwable caught) {
				EventController.get().fireEventFromSource(new GetPostFailure(input, caught), PostController.this);
			}
		});
	}

	public List<Post> getPosts() {
		if (pager == null) {
			fetchPosts();
		}

		return posts;
	}

	public long getPostsCount() {
		return count;
	}

	/**
	 * Return true if Posts already fetched
	 * 
	 * @return
	 */
	public boolean postsFetched() {
		return count != -1;
	}

	/**
	 * Return true if there is at least 1 Post
	 * 
	 * @return
	 */
	public boolean hasPosts() {
		return getPostsCount() > 0;
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

		if (!postsFetched() || (postsFetched() && getPostsCount() != posts.size() && end > posts.size())) {
			fetchPosts();
		} else {
			updateRowData(start, posts.size() == 0 ? posts : posts.subList(start, Math.min(posts.size(), end)));
		}

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
	public void updatePost(Long id, String title, Boolean visible, Boolean commentsEnabled, String description, String content, Boolean publish, String tags) {
		BlogService service = ServiceCreator.createBlogService();

		final UpdatePostRequest input = new UpdatePostRequest();
		input.accessCode = ACCESS_CODE;

		input.session = SessionController.get().getSessionForApiCall();
		input.post = postLookup.get(id.intValue());

		input.post.title = title;
		input.post.description = description;
		input.post.content = content;

		input.publish = publish;

		input.post.visible = visible;
		input.post.commentsEnabled = commentsEnabled;

		input.post.tags = TagHelper.convertToTagList(tags);

		service.updatePost(input, new AsyncCallback<UpdatePostResponse>() {

			@Override
			public void onSuccess(UpdatePostResponse output) {
				if (output.status == StatusType.StatusTypeSuccess) {
					reset();
					fetchPosts();
				}

				EventController.get().fireEventFromSource(new UpdatePostSuccess(input, output), PostController.this);
			}

			@Override
			public void onFailure(Throwable caught) {
				EventController.get().fireEventFromSource(new UpdatePostFailure(input, caught), PostController.this);
			}
		});
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
	public void createPost(String title, Boolean visible, Boolean commentsEnabled, String description, String content, Boolean publish, String tags) {
		BlogService service = ServiceCreator.createBlogService();

		final CreatePostRequest input = new CreatePostRequest();
		input.accessCode = ACCESS_CODE;

		input.session = SessionController.get().getSessionForApiCall();

		input.post = new Post();

		input.post.title = title;
		input.post.description = description;
		input.post.content = content;
		input.publish = publish;
		input.post.visible = visible;
		input.post.commentsEnabled = commentsEnabled;

		input.post.tags = TagHelper.convertToTagList(tags);

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

	public void reset() {
		pager = null;
		count = -1;
		posts.clear();
		postLookup = null;
		postsLookup = null;
		updateRowData(0, posts);
		updateRowCount(0, false);

	}

	/**
	 * @param id
	 * @return
	 */
	public Post getPost(Long id) {
		Post post = null;

		if (postLookup != null) {
			post = postLookup.get(id.intValue());
		}

		if (post == null) {
			fetchPost(id);
		}

		return post;
	}

	public Post getPostPart(Long id) {
		Post post = null;

		if (postsLookup != null) {
			post = postsLookup.get(id.intValue());
		}

		return post;
	}

	public void deletePost(Long postId) {
		BlogService service = ServiceCreator.createBlogService();

		final DeletePostRequest input = new DeletePostRequest();
		input.accessCode = ACCESS_CODE;

		input.session = SessionController.get().getSessionForApiCall();

		input.post = new Post();
		input.post.id = postId;

		service.deletePost(input, new AsyncCallback<DeletePostResponse>() {

			@Override
			public void onSuccess(DeletePostResponse output) {
				if (output.status == StatusType.StatusTypeSuccess) {
					Post post = postsLookup.get(input.post.id.intValue());

					if (post != null) {
						posts.remove(post);
						count--;

						pager.totalCount = Long.valueOf(pager.totalCount.longValue() - 1);

						updateRowCount((int) count, true);
						updateRowData(0, posts);
					} else {
						fetchPosts();
					}
				}

				EventController.get().fireEventFromSource(new DeletePostSuccess(input, output), PostController.this);
			}

			@Override
			public void onFailure(Throwable caught) {
				EventController.get().fireEventFromSource(new DeletePostFailure(input, caught), PostController.this);
			}
		});
	}

	/**
	 * Pass PostsPage Preloader reference, in order to show it when fetchPosts is called
	 * 
	 * @param p
	 */
	public void setPreloaderPosts(Preloader p) {
		preloaderPosts = p;
	}
}
