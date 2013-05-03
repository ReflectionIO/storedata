package com.spacehopperstudios.storedatacollector.mapreduce;

import static com.spacehopperstudios.storedatacollector.objectify.PersistenceService.ofy;

import org.apache.log4j.Logger;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.tools.mapreduce.Mapper;
import com.google.gson.Gson;
import com.spacehopperstudios.storedatacollector.collectors.DataCollectorIOS;
import com.spacehopperstudios.storedatacollector.datatypes.Rank;

public class PaidGrossingMapper extends Mapper<Entity, String, String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger LOG = Logger.getLogger(PaidGrossingMapper.class);

	private String country = null;
	private String source = null;

	public PaidGrossingMapper(String source, String country) {
		this.country = country;
		this.source = source;
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
	public void map(Entity entity) {
		// LOG.info("map(" + value + ")");

		Rank rank = ofy().toPojo(entity);

		if (!rank.source.equals(source))
			return;
		if (!rank.country.equals(country))
			return;
		if (rank.price == 0) 
			return;
		if (!(rank.type.equals(DataCollectorIOS.TOP_PAID_APPS) || rank.type.equals(DataCollectorIOS.TOP_GROSSING_APPS)))
			return;

		String code = rank.code + rank.itemId;
		String rankAsJson = (new Gson()).toJson(rank, Rank.class);

		getContext().emit(code, rankAsJson);
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
