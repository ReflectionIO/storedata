/**
 * 
 */
package com.spacehopperstudios.storedatacollector.mapreduce;

import static com.spacehopperstudios.storedatacollector.objectify.PersistenceService.ofy;

import org.apache.log4j.Logger;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.tools.mapreduce.Mapper;
import com.spacehopperstudios.storedatacollector.datatypes.Rank;

/**
 * @author billy1380
 * 
 */
public class TotalRankedItemsCountMapper extends Mapper<Entity, String, Long> {
	/*
	 *  
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger LOG = Logger.getLogger(TotalRankedItemsCountMapper.class);

	private String source;

	private String country;

	private String type;

	public TotalRankedItemsCountMapper(String source, String country, String type) {
		this.source = source;
		this.country = country;
		this.type = type;
	}

	private void incrementCounter(String name, long delta) {
		getContext().getCounter(name).increment(delta);
	}

	private void emit(String outKey, long outValue) {
		// LOG.info("emit(" + outKey + ", " + outValue + ")");
		incrementCounter(outKey, outValue);
		getContext().emit(outKey, outValue);
	}

	private void emit1(String outKey) {
		emit(outKey, 1);
	}

	@Override
	public void beginShard() {
		LOG.info("beginShard()");
		emit1("total map shard initializations");
		emit1("total map shard initializations in shard " + getContext().getShardNumber());
	}

	@Override
	public void beginSlice() {
		LOG.info("beginSlice()");
		emit1("total map slice initializations");
		emit1("total map slice initializations in shard " + getContext().getShardNumber());
	}

	@Override
	public void map(Entity entity) {
		// LOG.info("map(" + value + ")");

		Rank rank = ofy().toPojo(entity);

		if (!rank.source.equals(source))
			return;
		if (!rank.country.equals(country))
			return;
		if (!rank.type.equals(type))
			return;

		emit1("total entities");
		emit1("map calls in shard " + getContext().getShardNumber());
	}

	@Override
	public void endShard() {
		LOG.info("endShard()");
		emit1("total map shard terminations");
	}

	@Override
	public void endSlice() {
		LOG.info("endSlice()");
		emit1("total map slice terminations");
	}
}
