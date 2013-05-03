/**
 * 
 */
package com.spacehopperstudios.storedatacollector.mapreduce;

import org.apache.log4j.Logger;

import com.google.appengine.tools.mapreduce.KeyValue;
import com.google.appengine.tools.mapreduce.Reducer;
import com.google.appengine.tools.mapreduce.ReducerInput;

/**
 * @author William Shakour
 * 
 */
public class RankCountReducer extends Reducer<String, Long, KeyValue<String, Long>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger LOG = Logger.getLogger(RankCountReducer.class);

	private void emit(String key, long outValue) {
		// log.info("emit(" + outValue + ")");
		getContext().emit(KeyValue.of(key, outValue));
	}

	@Override
	public void beginShard() {
		LOG.info("beginShard()");
	}

	@Override
	public void beginSlice() {
		LOG.info("beginSlice()");
	}

	@Override
	public void reduce(String key, ReducerInput<Long> values) {
		// log.info("reduce(" + key + ", " + values + ")");
		long total = 0;
		while (values.hasNext()) {
			long value = values.next();
			total += value;
		}
		emit(key, total);
	}

	@Override
	public void endShard() {
		LOG.info("endShard()");
	}

	@Override
	public void endSlice() {
		LOG.info("endSlice()");
	}

}
