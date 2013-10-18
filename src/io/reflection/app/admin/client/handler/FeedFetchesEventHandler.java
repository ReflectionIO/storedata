//
//  FeedFetchesEventHandler.java
//  storedata
//
//  Created by William Shakour (billy1380) on 18 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.admin.client.handler;

import io.reflection.app.shared.datatypes.FeedFetch;

import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * @author billy1380
 *
 */
public interface FeedFetchesEventHandler extends EventHandler {

	public static final GwtEvent.Type<FeedFetchesEventHandler> TYPE = new GwtEvent.Type<FeedFetchesEventHandler>();
	
	public class ReceivedMixedFeedFetches extends GwtEvent<FeedFetchesEventHandler> {

		private List<FeedFetch> mMixed;

		public ReceivedMixedFeedFetches(List<FeedFetch> mixed) {
			mMixed = mixed;
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
			handler.receivedMixedFeedFetches(mMixed);
		}
		
	}
	
	public class ReceivedFeedFetches extends GwtEvent<FeedFetchesEventHandler> {

		private List<FeedFetch> mIngested;
		private List<FeedFetch> mUningested;

		/**
		 * @param ingested
		 * @param uningested
		 */
		public ReceivedFeedFetches(List<FeedFetch> ingested, List<FeedFetch> uningested) {
			mIngested = ingested;
			mUningested = uningested;
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
			handler.receivedFeedFetches(mIngested, mUningested);
		}
		
	}
	
	public void receivedMixedFeedFetches(List<FeedFetch> mixed);
	
	public void receivedFeedFetches(List<FeedFetch> ingested, List<FeedFetch> uningested);
	
}
