//
//  OrganiseSummaries.java
//  storedata
//
//  Created by William Shakour (billy1380) on 27 Jan 2015.
//  Copyright © 2015 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.pipeline;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.appengine.tools.pipeline.Job1;
import com.google.appengine.tools.pipeline.Value;
import com.spacehopperstudios.utility.StringUtils;

/**
 * @author William Shakour (billy1380)
 *
 */
public class OrganiseSummaries extends Job1<Map<String, Map<String, Double>>, List<Map<String, Double>>> {

	private static final long serialVersionUID = -5357636373846477424L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.appengine.tools.pipeline.Job1#run(java.lang.Object)
	 */
	@Override
	public Value<Map<String, Map<String, Double>>> run(List<Map<String, Double>> summaries) throws Exception {

		summaries.removeAll(Collections.singleton(null));

		Map<String, Map<String, Double>> organised = new HashMap<>();

		for (Map<String, Double> summary : summaries) {
			for (String key : summary.keySet()) {
				put(key, summary.get(key), organised);
			}
		}

		return immediate(organised);
	}

	private static void put(String key, Double value, Map<String, Map<String, Double>> organised) {
		if (value != null && value.doubleValue() > Double.MIN_VALUE) {
			String[] parts = key.split("\\.");

			String countryKey = parts[0];
			String formKey = parts[1];
			String typeKey = parts[2];
			
			String itemsKey = StringUtils.join(Arrays.asList(countryKey, formKey, typeKey), ".");
			String itemKey = parts[3];

			Map<String, Double> items = organised.get(itemsKey);
			
			if (items == null) {
				items = new HashMap<>();
				organised.put(itemsKey, items);
			}
			
			Double currentValue = items.get(itemKey);
			
			if (currentValue == null) {
				items.put(itemKey, value);
			} else {
				items.put(itemKey, currentValue.doubleValue() + value.doubleValue());
			}

		}
	}

}
