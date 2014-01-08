//
//  RanksEventHandler.java
//  storedata
//
//  Created by William Shakour (billy1380) on 3 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.admin.client.handler;

import io.reflection.app.shared.datatypes.Rank;

import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * @author billy1380
 * 
 */
public interface RanksEventHandler extends EventHandler {

	public static final GwtEvent.Type<RanksEventHandler> TYPE = new GwtEvent.Type<RanksEventHandler>();

	/**
	 * @param listType
	 * @param ranks
	 */
	void receivedRanks(String listType, List<Rank> ranks);
	
	void fetchingRanks();

	public class ReceivedRanks extends GwtEvent<RanksEventHandler> {

		private String mListType = null;
		private List<Rank> mRanks = null;

		/**
		 * @param string
		 * @param freeRanks
		 */
		public ReceivedRanks(String listType, List<Rank> ranks) {
			mListType = listType;
			mRanks = ranks;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
		 */
		@Override
		public GwtEvent.Type<RanksEventHandler> getAssociatedType() {
			return TYPE;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
		 */
		@Override
		protected void dispatch(RanksEventHandler handler) {
			handler.receivedRanks(mListType, mRanks);
		}
	}
	
	public class FetchingRanks extends  GwtEvent<RanksEventHandler> {

		/* (non-Javadoc)
		 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
		 */
		@Override
		public com.google.gwt.event.shared.GwtEvent.Type<RanksEventHandler> getAssociatedType() {
			return TYPE;
		}

		/* (non-Javadoc)
		 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
		 */
		@Override
		protected void dispatch(RanksEventHandler handler) {
			handler.fetchingRanks();
		}
		
	}
}