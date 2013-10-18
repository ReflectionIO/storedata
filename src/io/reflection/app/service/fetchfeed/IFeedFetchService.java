//  
//  IFeedFetchService.java
//  storedata
//
//  Created by William Shakour on September 5, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//

package io.reflection.app.service.fetchfeed;

import java.util.List;

import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.shared.datatypes.Country;
import io.reflection.app.shared.datatypes.FeedFetch;
import io.reflection.app.shared.datatypes.Store;

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

	/**
	 * @param store
	 * @param country
	 * @param types
	 * @param pager
	 * @return
	 */
	public List<FeedFetch> getFeedFetches(Store store, Country country, List<String> types, Pager pager);

	/**
	 * @return
	 */
	public Long getFeedFetchesCount(Store store, Country country, List<String> types);

	/**
	 * @param store
	 * @param country
	 * @param types
	 * @param pager
	 * @return
	 */
	public List<FeedFetch> getIngestedFeedFetches(Store store, Country country, List<String> types, Pager pager);

	/**
	 * @return
	 */
	public Long getIngestedFeedFetchesCount(Store store, Country country, List<String> listType);

	/**
	 * @param store
	 * @param country
	 * @param types
	 * @param pager
	 * @return
	 */
	public List<FeedFetch> getUningestedFeedFetches(Store store, Country country, List<String> types, Pager pager);

	/**
	 * @return
	 */
	public Long getUningestedFeedFetchesCount(Store store, Country country, List<String> types);

}