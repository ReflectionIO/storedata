//  
//  IFeedFetchService.java
//  storedata
//
//  Created by William Shakour on September 5, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//

package io.reflection.app.service.fetchfeed;

import io.reflection.app.datatypes.FeedFetch;

import com.spacehopperstudios.service.IService;

public interface IFeedFetchService extends IService {
	/**
	 * @param id
	 * @return
	 */
	public FeedFetch getFeedFetch(Long id);

	/**
	 * @param feedFetch
	 * @return
	 */
	public FeedFetch addFeedFetch(FeedFetch feedFetch);

	/**
	 * @param feedFetch
	 * @return
	 */
	public FeedFetch updateFeedFetch(FeedFetch feedFetch);

	/**
	 * @param feedFetch
	 */
	public void deleteFeedFetch(FeedFetch feedFetch);

}