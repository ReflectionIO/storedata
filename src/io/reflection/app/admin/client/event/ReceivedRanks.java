//
//  ReceivedRanks.java
//  storedata
//
//  Created by William Shakour (billy1380) on 3 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.admin.client.event;

import io.reflection.app.admin.client.event.ReceivedRanks.Handler;
import io.reflection.app.shared.datatypes.Rank;

import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * @author billy1380
 * 
 */
public class ReceivedRanks extends GwtEvent<Handler> {

	public interface Handler extends EventHandler {

		/**
		 * @param listType
		 * @param ranks
		 */
		void receivedRanks(String listType, List<Rank> ranks);

	}
	
	public static Type<Handler> TYPE = new Type<Handler>();

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
	public GwtEvent.Type<Handler> getAssociatedType() {
		return TYPE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(Handler handler) {
		handler.receivedRanks(mListType, mRanks);

	}

}
