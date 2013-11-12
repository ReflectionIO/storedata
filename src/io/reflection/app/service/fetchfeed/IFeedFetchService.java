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
	 * 
	 * @param country
	 * @param store
	 * @param types
	 * @param pager
	 * @return
	 */
	public List<FeedFetch> getFeedFetches(Country country, Store store, List<String> types, Pager pager);

	/**
	 * 
	 * @param country
	 * @param store
	 * @param types
	 * @return
	 */
	public Long getFeedFetchesCount(Country country, Store store, List<String> types);

	/**
	 * 
	 * @param country
	 * @param store
	 * @param types
	 * @param pager
	 * @return
	 */
	public List<FeedFetch> getIngestedFeedFetches(Country country, Store store, List<String> types, Pager pager);

	/**
	 * 
	 * @param store
	 * @param country
	 * @param listType
	 * @return
	 */
	public Long getIngestedFeedFetchesCount(Country country, Store store, List<String> listType);

	/**
	 * @param store
	 * @param country
	 * @param types
	 * @param pager
	 * @return
	 */
	public List<FeedFetch> getUningestedFeedFetches(Country country, Store store, List<String> types, Pager pager);

	/**
	 * 
	 * @param country
	 * @param store
	 * @param types
	 * @return
	 */
	public Long getUningestedFeedFetchesCount(Country country, Store store, List<String> types);

	/**
	 * 
	 * @param country
	 * @param store
	 * @param listType
	 * @param code
	 * @return
	 */
	public List<Long> getIngestableFeedFetchIds(Country country, Store store, String listType, String code);

	/**
	 * 
	 * @param country
	 * @param store
	 * @param types
	 * @param code
	 * @return
	 */
	public Boolean isReadyToModel(Country country, Store store, List<String> types, String code);

	/**
	 * 
	 * @param country
	 * @param store
	 * @param types
	 * @param code
	 * @return
	 */
	public List<FeedFetch> getGatherCodeFeedFetches(Country country, Store store, List<String> types, String code);

}