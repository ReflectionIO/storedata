//
//  SummariseSales.java
//  storedata
//
//  Created by William Shakour (billy1380) on 20 Jan 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.pipeline;

import java.util.List;
import java.util.Map;

import com.google.appengine.tools.pipeline.Job1;
import com.google.appengine.tools.pipeline.Value;

/**
 * @author William Shakour (billy1380)
 *
 */
public class SummariseSales extends Job1<Map<String, Double>, List<Long>> {

	private static final long serialVersionUID = 7344309686985145135L;

	/* (non-Javadoc)
	 * @see com.google.appengine.tools.pipeline.Job1#run(java.lang.Object)
	 */
	@Override
	public Value<Map<String, Double>> run(List<Long> ingestedSalesIds) throws Exception {
		return null;
	}

}
