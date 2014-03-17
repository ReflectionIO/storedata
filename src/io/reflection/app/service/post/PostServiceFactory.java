//  
//  PostServiceFactory.java
//  reflection.io
//
//  Created by William Shakour on March 16, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.service.post;

final class PostServiceFactory {

	/**
	 * @return
	 */
	public static IPostService createNewPostService() {
		return new PostService();
	}

}