//
//  FeedFetchesEventHandler.java
//  storedata
//
//  Created by William Shakour (billy1380) on 18 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.handler;

import io.reflection.app.datatypes.shared.FeedFetch;

import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * @author billy1380
 *
 */
public interface FeedFetchesEventHandler extends EventHandler {

	public static final GwtEvent.Type<FeedFetchesEventHandler> TYPE = new GwtEvent.Type<FeedFetchesEventHandler>();
	
	public class ReceivedFeedFetches extends GwtEvent<FeedFetchesEventHandler> {

		private List<FeedFetch> mFeedFetches;

		public ReceivedFeedFetches(List<FeedFetch> feedFetches) {
			mFeedFetches = feedFetches;
		}
		
		/* (non-Javadoc)
		 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
		 */
		@Override
		public com.google.gwt.event.shared.GwtEvent.Type<FeedFetchesEventHandler> getAssociatedType() {
			return TYPE;
		}

		/* (non-Javadoc)
		 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
		 */
		@Override
		protected void dispatch(FeedFetchesEventHandler handler) {
			handler.receivedFeedFetches(mFeedFetches);
		}
		
	}
	
	public void receivedFeedFetches(List<FeedFetch> feedFetches);
	
	
}
