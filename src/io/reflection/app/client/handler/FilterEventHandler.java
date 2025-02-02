//
//  FilterEventHandler.java
//  storedata
//
//  Created by William Shakour (billy1380) on 18 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.client.handler;

import io.reflection.app.client.controller.FilterController.Filter;

import java.util.Map;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * @author billy1380
 * 
 */
public interface FilterEventHandler extends EventHandler {

	public static final GwtEvent.Type<FilterEventHandler> TYPE = new GwtEvent.Type<FilterEventHandler>();

	public <T> void filterParamChanged(String name, T currentValue, T previousValue);
	public void filterParamsChanged(Filter currentFilter, Map<String, ?> previousValues);

	public class ChangedFilterParameter<T> extends GwtEvent<FilterEventHandler> {

		private String mParamName;
		
		private T mCurrentValue;
		private T mPreviousValue;

		public ChangedFilterParameter(String name, T currentValue, T previousValue) {
			mParamName = name;
			mCurrentValue = currentValue;
			mPreviousValue = previousValue;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
		 */
		@Override
		public com.google.gwt.event.shared.GwtEvent.Type<FilterEventHandler> getAssociatedType() {
			return TYPE;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
		 */
		@Override
		protected void dispatch(FilterEventHandler handler) {
			handler.filterParamChanged(mParamName, mCurrentValue, mPreviousValue);
		}
	}
	
	public class ChangedFilterParameters extends GwtEvent<FilterEventHandler> {
		private Filter mcurrentFilter;
		private Map<String, ?> mPreviousValues;
		
		public ChangedFilterParameters(Filter currentFilter, Map<String, ?> previousValues) {
			mcurrentFilter = currentFilter;
			mPreviousValues = previousValues;
		}
		
		/* (non-Javadoc)
		 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
		 */
		@Override
		public com.google.gwt.event.shared.GwtEvent.Type<FilterEventHandler> getAssociatedType() {
			return TYPE;
		}
		/* (non-Javadoc)
		 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
		 */
		@Override
		protected void dispatch(FilterEventHandler handler) {
			handler.filterParamsChanged(mcurrentFilter, mPreviousValues);
		}
	}

}
