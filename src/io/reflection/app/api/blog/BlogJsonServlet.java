//  
//  BlogJsonServlet.java
//  reflection.io
//
//  Created by William Shakour on March 16, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.api.blog;

import io.reflection.app.api.blog.shared.call.CreatePostRequest;
import io.reflection.app.api.blog.shared.call.GetPostRequest;
import io.reflection.app.api.blog.shared.call.GetPostsRequest;
import io.reflection.app.api.blog.shared.call.UpdatePostRequest;

import com.google.gson.JsonObject;
import com.willshex.gson.json.service.server.JsonServlet;

@SuppressWarnings("serial")
public final class BlogJsonServlet extends JsonServlet {
	@Override
	protected String processAction(String action, JsonObject request) {
		String output = "null";
		Blog service = new Blog();
		if ("GetPosts".equals(action)) {
			GetPostsRequest input = new GetPostsRequest();
			input.fromJson(request);
			output = service.getPosts(input).toString();
		} else if ("GetPost".equals(action)) {
			GetPostRequest input = new GetPostRequest();
			input.fromJson(request);
			output = service.getPost(input).toString();
		} else if ("UpdatePost".equals(action)) {
			UpdatePostRequest input = new UpdatePostRequest();
			input.fromJson(request);
			output = service.updatePost(input).toString();
		} else if ("CreatePost".equals(action)) {
			CreatePostRequest input = new CreatePostRequest();
			input.fromJson(request);
			output = service.createPost(input).toString();
		}
		return output;
	}
}