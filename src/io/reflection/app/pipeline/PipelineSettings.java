//
//  PipelineSettings.java
//  storedata
//
//  Created by William Shakour (billy1380) on 6 Mar 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.pipeline;

import io.reflection.app.Queue;

import com.google.appengine.tools.pipeline.JobSetting;
import com.google.appengine.tools.pipeline.JobSetting.OnQueue;

/**
 * @author William Shakour (billy1380)
 *
 */
public interface PipelineSettings {
	public static final JobSetting onGatherQueue = new JobSetting.OnQueue(Queue.GATHER);
	public static final JobSetting onIngestQueue = new JobSetting.OnQueue(Queue.INGEST);
	public static final JobSetting onModelQueue = new JobSetting.OnQueue(Queue.MODEL);
	public static final JobSetting onPredictQueue = new JobSetting.OnQueue(Queue.PREDICT);
	public static final JobSetting onDefaultQueue = new JobSetting.OnQueue(OnQueue.DEFAULT);
	public static final JobSetting onDataAccountGatherQueue = new JobSetting.OnQueue(Queue.DATA_ACCOUNT_GATHER);
	public static final JobSetting onDataAccountIngestQueue = new JobSetting.OnQueue(Queue.DATA_ACCOUNT_INGEST);

	public static final JobSetting onModelBackend = new JobSetting.OnBackend("model");

	public static final JobSetting doNotRetry = new JobSetting.MaxAttempts(0);

}
