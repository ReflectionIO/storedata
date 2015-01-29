//
//  FulfillAllPromisses.java
//  storedata
//
//  Created by William Shakour (billy1380) on 19 Jan 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.pipeline;

import io.reflection.app.datatypes.shared.Store;
import io.reflection.app.service.feedfetch.FeedFetchServiceProvider;
import io.reflection.app.shared.util.DataTypeHelper;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import co.spchopr.persistentmap.PersistentMap;
import co.spchopr.persistentmap.PersistentMapFactory;

import com.google.appengine.tools.pipeline.Job2;
import com.google.appengine.tools.pipeline.NoSuchObjectException;
import com.google.appengine.tools.pipeline.OrphanedObjectException;
import com.google.appengine.tools.pipeline.PipelineService;
import com.google.appengine.tools.pipeline.PipelineServiceFactory;
import com.google.appengine.tools.pipeline.Value;
import com.spacehopperstudios.utility.StringUtils;

public final class FulfillAllPromisses extends Job2<Void, Map<String, Map<String, Double>>, Date> {

	private static final long serialVersionUID = -2335419676158668911L;

	private static final Logger LOG = Logger.getLogger(FulfillAllPromisses.class.getName());

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.appengine.tools.pipeline.Job1#run(java.lang.Object)
	 */
	@Override
	public Value<Void> run(Map<String, Map<String, Double>> dataAccountFetchSummary, Date on) throws Exception {

		if (!dataAccountFetchSummary.isEmpty()) {
			PersistentMap persist = PersistentMapFactory.createObjectify();
			PipelineService pipelineService = PipelineServiceFactory.newPipelineService();

			// FIXME: there are better ways to get the store
			Store iosStore = DataTypeHelper.getIosStore();
			
			DateTime start = new DateTime(on.getTime(), DateTimeZone.UTC).minusHours(12);
			Long code = FeedFetchServiceProvider.provide().getGatherCode(iosStore, start.toDate(), on);
			
			fulfill(dataAccountFetchSummary, code, persist, pipelineService);
			
			start = new DateTime(on.getTime(), DateTimeZone.UTC).plusHours(12);
			code = FeedFetchServiceProvider.provide().getGatherCode(iosStore, start.toDate(), on);
			
			fulfill(dataAccountFetchSummary, code, persist, pipelineService);
		}

		return null;
	}

	private void fulfill(Map<String, Map<String, Double>> dataAccountFetchSummary, Long code, PersistentMap persist, PipelineService pipelineService) {
		String key;
		String promiseHandle;
		for (String summaryKey : dataAccountFetchSummary.keySet()) {
			key = StringUtils.join(Arrays.asList(code.toString(), summaryKey), ".");
			promiseHandle = (String) persist.get(key);

			try {
				pipelineService.submitPromisedValue(promiseHandle, dataAccountFetchSummary.get(summaryKey));
			} catch (NoSuchObjectException | OrphanedObjectException e) {
				if (LOG.getLevel() == Level.WARNING) {
					LOG.log(Level.WARNING, "Errro trying to fulfill promise for [" + summaryKey + "], skipping", e);
				}
			}
		}
	}
}