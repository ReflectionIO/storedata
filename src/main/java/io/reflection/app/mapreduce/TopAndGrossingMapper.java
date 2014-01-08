package io.reflection.app.mapreduce;

import static io.reflection.app.objectify.PersistenceService.ofy;
import io.reflection.app.shared.datatypes.Rank;

import java.util.logging.Logger;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.tools.mapreduce.Mapper;
import com.google.gson.Gson;

public class TopAndGrossingMapper extends Mapper<Entity, String, String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger LOG = Logger.getLogger(TopAndGrossingMapper.class.getName());

	private String country = null;
	private String source = null;
	

	private String topType;
	private String grossingType;

	public TopAndGrossingMapper(String topType, String grossingType, String source, String country) {
		this.country = country;
		this.source = source;
		
		this.topType = topType;
		this.grossingType = grossingType;
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
		if (topType.contains("free") && rank.price != 0)
			return;
		if (!(rank.type.equals(topType) || rank.type.equals(grossingType)))
			return;
		if (topType.contains("paid") && rank.price == 0)
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
