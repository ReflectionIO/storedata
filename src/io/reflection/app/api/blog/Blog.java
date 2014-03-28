//  
//  Blog.java
//  reflection.io
//
//  Created by William Shakour on March 16, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.api.blog;

import static io.reflection.app.api.PagerHelper.updatePager;
import io.reflection.app.api.ValidationHelper;
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
import io.reflection.app.api.shared.ApiError;
import io.reflection.app.datatypes.shared.Post;
import io.reflection.app.datatypes.shared.User;
import io.reflection.app.service.post.PostServiceProvider;
import io.reflection.app.service.user.UserServiceProvider;
import io.reflection.app.shared.util.SparseArray;

import java.util.Date;
import java.util.logging.Logger;

import com.willshex.gson.json.service.server.ActionHandler;
import com.willshex.gson.json.service.server.InputValidationException;
import com.willshex.gson.json.service.shared.StatusType;

public final class Blog extends ActionHandler {

	private static final Logger LOG = Logger.getLogger(Blog.class.getName());

	public GetPostsResponse getPosts(GetPostsRequest input) {
		LOG.finer("Entering getPosts");
		GetPostsResponse output = new GetPostsResponse();
		try {
			if (input == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("GetPostsRequest: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input.accessCode");

			if (input.session != null) {
				input.session = ValidationHelper.validateSession(input.session, "input.session");
			}

			if (input.includeContents == null) {
				input.includeContents = Boolean.FALSE;
			}

			// TODO: check whether the user can see invisible items in list
			Boolean onlyVisible = Boolean.FALSE;

			output.posts = PostServiceProvider.provide().getPosts(onlyVisible, input.includeContents, input.pager);

			SparseArray<User> users = new SparseArray<User>();

			for (Post post : output.posts) {
				if (users.get(post.author.id.intValue()) == null) {
					users.put(post.author.id.intValue(), UserServiceProvider.provide().getUser(post.author.id));
				}

				post.author = users.get(post.author.id.intValue());
			}

			output.pager = input.pager;
			updatePager(output.pager, output.posts, input.pager.totalCount == null ? PostServiceProvider.provide().getPostsCount(onlyVisible)
					: input.pager.totalCount);

			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}
		LOG.finer("Exiting getPosts");
		return output;
	}

	public GetPostResponse getPost(GetPostRequest input) {
		LOG.finer("Entering getPost");
		GetPostResponse output = new GetPostResponse();
		try {
			if (input == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("GetPostRequest: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input.accessCode");

			if (input.session != null) input.session = ValidationHelper.validateSession(input.session, "input.session");

			boolean isIdLookup = false, isTitleLookup = false;
			if (input.id != null) {
				isIdLookup = true;
			} else if (input.title != null) {
				isTitleLookup = true;
			}

			if (!(isIdLookup || isTitleLookup)) throw new InputValidationException(0, "");

			if (isIdLookup) {
				output.post = PostServiceProvider.provide().getPost(input.id);
			} else {
				output.post = PostServiceProvider.provide().getTitlePost(input.title);
			}

			output.post.author = UserServiceProvider.provide().getUser(output.post.author.id);

			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}
		LOG.finer("Exiting getPost");
		return output;
	}

	public UpdatePostResponse updatePost(UpdatePostRequest input) {
		LOG.finer("Entering updatePost");
		UpdatePostResponse output = new UpdatePostResponse();
		try {
			if (input == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("UpdatePostRequest: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input.accessCode");

			input.session = ValidationHelper.validateSession(input.session, "input.session");

			// TODO: check permissions

			if (input.publish == Boolean.TRUE) {
				input.post.published = new Date();
			}

			PostServiceProvider.provide().updatePost(input.post);

			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}

		LOG.finer("Exiting updatePost");
		return output;
	}

	public CreatePostResponse createPost(CreatePostRequest input) {
		LOG.finer("Entering createPost");
		CreatePostResponse output = new CreatePostResponse();
		try {
			if (input == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("CreatePostRequest: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input.accessCode");

			input.session = ValidationHelper.validateSession(input.session, "input.session");

			// TODO: check permissions

			input.post = ValidationHelper.validateNewPost(input.post, "input.post");

			input.post.author = input.session.user;

			if (input.publish == Boolean.TRUE) {
				input.post.published = new Date();
			}

			input.post.visible = (input.post.visible == null ? Boolean.FALSE : input.post.visible);
			input.post.commentsEnabled = (input.post.commentsEnabled == null ? Boolean.FALSE : input.post.commentsEnabled);

			PostServiceProvider.provide().addPost(input.post);

			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}
		LOG.finer("Exiting createPost");
		return output;
	}

	public DeletePostResponse deletePost(DeletePostRequest input) {
		LOG.finer("Entering deletePost");
		DeletePostResponse output = new DeletePostResponse();
		try {

			if (input == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("CreatePostRequest: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input.accessCode");

			input.session = ValidationHelper.validateSession(input.session, "input.session");

			input.post = ValidationHelper.validateExistingPost(input.post, "input.post");

			PostServiceProvider.provide().deletePost(input.post);

			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}
		LOG.finer("Exiting deletePost");
		return output;
	}

}