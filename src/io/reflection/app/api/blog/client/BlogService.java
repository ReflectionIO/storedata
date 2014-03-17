//  
//  blog\BlogService.java
//  reflection.io
//
//  Created by William Shakour on March 16, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.api.blog.client;

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

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.willshex.gson.json.service.client.JsonService;

public final class BlogService extends JsonService {
	public static final String BlogMethodGetPosts = "GetPosts";

	public void getPosts(GetPostsRequest input, final AsyncCallback<GetPostsResponse> output) {
		try {
			sendRequest(BlogMethodGetPosts, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					GetPostsResponse outputParameter = new GetPostsResponse();
					parseResponse(response.getText(), outputParameter);
					output.onSuccess(outputParameter);
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
				}
			});
		} catch (RequestException e) {
			output.onFailure(e);
		}
	}

	public static final String BlogMethodGetPost = "GetPost";

	public void getPost(GetPostRequest input, final AsyncCallback<GetPostResponse> output) {
		try {
			sendRequest(BlogMethodGetPost, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					GetPostResponse outputParameter = new GetPostResponse();
					parseResponse(response.getText(), outputParameter);
					output.onSuccess(outputParameter);
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
				}
			});
		} catch (RequestException e) {
			output.onFailure(e);
		}
	}

	public static final String BlogMethodAssignTags = "AssignTags";

	public void assignTags(AssignTagsRequest input, final AsyncCallback<AssignTagsResponse> output) {
		try {
			sendRequest(BlogMethodAssignTags, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					AssignTagsResponse outputParameter = new AssignTagsResponse();
					parseResponse(response.getText(), outputParameter);
					output.onSuccess(outputParameter);
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
				}
			});
		} catch (RequestException e) {
			output.onFailure(e);
		}
	}

	public static final String BlogMethodUpdatePost = "UpdatePost";

	public void updatePost(UpdatePostRequest input, final AsyncCallback<UpdatePostResponse> output) {
		try {
			sendRequest(BlogMethodUpdatePost, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					UpdatePostResponse outputParameter = new UpdatePostResponse();
					parseResponse(response.getText(), outputParameter);
					output.onSuccess(outputParameter);
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
				}
			});
		} catch (RequestException e) {
			output.onFailure(e);
		}
	}

	public static final String BlogMethodCreatePost = "CreatePost";

	public void createPost(CreatePostRequest input, final AsyncCallback<CreatePostResponse> output) {
		try {
			sendRequest(BlogMethodCreatePost, input, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					CreatePostResponse outputParameter = new CreatePostResponse();
					parseResponse(response.getText(), outputParameter);
					output.onSuccess(outputParameter);
				}

				@Override
				public void onError(Request request, Throwable exception) {
					output.onFailure(exception);
				}
			});
		} catch (RequestException e) {
			output.onFailure(e);
		}
	}
}