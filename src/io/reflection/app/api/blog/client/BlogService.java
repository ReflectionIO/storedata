//  
//  blog\BlogService.java
//  reflection.io
//
//  Created by William Shakour on March 16, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.api.blog.client;

import io.reflection.app.api.blog.shared.call.CreatePostRequest;
import io.reflection.app.api.blog.shared.call.CreatePostResponse;
import io.reflection.app.api.blog.shared.call.GetPostRequest;
import io.reflection.app.api.blog.shared.call.GetPostResponse;
import io.reflection.app.api.blog.shared.call.GetPostsRequest;
import io.reflection.app.api.blog.shared.call.GetPostsResponse;
import io.reflection.app.api.blog.shared.call.UpdatePostRequest;
import io.reflection.app.api.blog.shared.call.UpdatePostResponse;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.willshex.gson.json.service.client.JsonService;

public final class BlogService extends JsonService {
	public static final String BlogMethodGetPosts = "GetPosts";

	public Request getPosts(final GetPostsRequest input, final AsyncCallback<GetPostsResponse> output) {
		Request handle = null;
		try {
			handle = sendRequest(BlogMethodGetPosts, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					GetPostsResponse outputParameter = new GetPostsResponse();
					parseResponse(response.getText(), outputParameter);
					output.onSuccess(outputParameter);
					onCallSuccess(BlogService.this, BlogMethodGetPosts, input, outputParameter);
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(BlogService.this, BlogMethodGetPosts, input, exception);
				}
			});
			onCallStart(BlogService.this, BlogMethodGetPosts, input, handle);
		} catch (RequestException e) {
			output.onFailure(e);
			onCallFailure(BlogService.this, BlogMethodGetPosts, input, e);
		}
		return handle;
	}

	public static final String BlogMethodGetPost = "GetPost";

	public Request getPost(final GetPostRequest input, final AsyncCallback<GetPostResponse> output) {
		Request handle = null;
		try {
			handle = sendRequest(BlogMethodGetPost, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					GetPostResponse outputParameter = new GetPostResponse();
					parseResponse(response.getText(), outputParameter);
					output.onSuccess(outputParameter);
					onCallSuccess(BlogService.this, BlogMethodGetPost, input, outputParameter);
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(BlogService.this, BlogMethodGetPost, input, exception);
				}
			});
			onCallStart(BlogService.this, BlogMethodGetPost, input, handle);
		} catch (RequestException e) {
			output.onFailure(e);
			onCallFailure(BlogService.this, BlogMethodGetPost, input, e);
		}
		return handle;
	}

	public static final String BlogMethodUpdatePost = "UpdatePost";

	public Request updatePost(final UpdatePostRequest input, final AsyncCallback<UpdatePostResponse> output) {
		Request handle = null;
		try {
			handle = sendRequest(BlogMethodUpdatePost, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					UpdatePostResponse outputParameter = new UpdatePostResponse();
					parseResponse(response.getText(), outputParameter);
					output.onSuccess(outputParameter);
					onCallSuccess(BlogService.this, BlogMethodUpdatePost, input, outputParameter);
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(BlogService.this, BlogMethodUpdatePost, input, exception);
				}
			});
			onCallStart(BlogService.this, BlogMethodUpdatePost, input, handle);
		} catch (RequestException e) {
			output.onFailure(e);
			onCallFailure(BlogService.this, BlogMethodUpdatePost, input, e);
		}
		return handle;
	}

	public static final String BlogMethodCreatePost = "CreatePost";

	public Request createPost(final CreatePostRequest input, final AsyncCallback<CreatePostResponse> output) {
		Request handle = null;
		try {
			handle = sendRequest(BlogMethodCreatePost, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					CreatePostResponse outputParameter = new CreatePostResponse();
					parseResponse(response.getText(), outputParameter);
					output.onSuccess(outputParameter);
					onCallSuccess(BlogService.this, BlogMethodCreatePost, input, outputParameter);
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
					onCallFailure(BlogService.this, BlogMethodCreatePost, input, exception);
				}
			});
			onCallStart(BlogService.this, BlogMethodCreatePost, input, handle);
		} catch (RequestException e) {
			output.onFailure(e);
			onCallFailure(BlogService.this, BlogMethodCreatePost, input, e);
		}
		return handle;
	}
}