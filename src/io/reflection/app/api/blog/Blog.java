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
import io.reflection.app.api.blog.shared.call.AssignTagsRequest;
import io.reflection.app.api.blog.shared.call.AssignTagsResponse;
import io.reflection.app.api.blog.shared.call.CreatePostRequest;
import io.reflection.app.api.blog.shared.call.CreatePostResponse;
import io.reflection.app.api.blog.shared.call.GetPostRequest;
import io.reflection.app.api.blog.shared.call.GetPostResponse;
import io.reflection.app.api.blog.shared.call.GetPostsRequest;
import io.reflection.app.api.blog.shared.call.GetPostsResponse;
import io.reflection.app.api.blog.shared.call.UpdatePostRequest;
import io.reflection.app.api.blog.shared.call.UpdatePostResponse;
import io.reflection.app.api.shared.ApiError;
import io.reflection.app.datatypes.shared.Post;
import io.reflection.app.service.post.PostServiceProvider;

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

			input.session = ValidationHelper.validateSession(input.session, "input.session");

			if (input.includeContents == null) {
				input.includeContents = Boolean.FALSE;
			}

			// TODO: check whether the user can see invisible items in list
			Boolean onlyVisible = Boolean.FALSE;

			output.posts = PostServiceProvider.provide().getPosts(onlyVisible, input.includeContents, input.pager);

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

			input.session = ValidationHelper.validateSession(input.session, "input.session");

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

			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}
		LOG.finer("Exiting getPost");
		return output;
	}

	public AssignTagsResponse assignTags(AssignTagsRequest input) {
		LOG.finer("Entering assignTags");
		AssignTagsResponse output = new AssignTagsResponse();
		try {
			if (input == null)
				throw new InputValidationException(ApiError.InvalidValueNull.getCode(), ApiError.InvalidValueNull.getMessage("AssignTagsRequest: input"));

			input.accessCode = ValidationHelper.validateAccessCode(input.accessCode, "input.accessCode");

			input.session = ValidationHelper.validateSession(input.session, "input.session");

			PostServiceProvider.provide().assignTags(input.post, input.tags);

			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}
		LOG.finer("Exiting assignTags");
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

			Post post = new Post();
			post.title = input.title;

			post.description = input.description;
			post.content = input.content;
			post.author = input.session.user;

			if (input.publish == Boolean.TRUE) {
				post.published = new Date();
			}

			post.visible = (input.visible == null ? Boolean.FALSE : input.visible);

			Post addedPost = PostServiceProvider.provide().addPost(post);
			PostServiceProvider.provide().assignTags(addedPost, input.tags);

			output.status = StatusType.StatusTypeSuccess;
		} catch (Exception e) {
			output.status = StatusType.StatusTypeFailure;
			output.error = convertToErrorAndLog(LOG, e);
		}
		LOG.finer("Exiting createPost");
		return output;
	}
}