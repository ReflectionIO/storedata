/**
 * 
 */
package com.spacehopperstudios.storedatacollector.mapreduce;

import static com.spacehopperstudios.storedatacollector.objectify.PersistenceService.ofy;

import java.util.logging.Logger;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.tools.mapreduce.Mapper;
import com.spacehopperstudios.storedatacollector.collectors.DataCollectorIOS;
import com.spacehopperstudios.storedatacollector.datatypes.Rank;

/**
 * @author William Shakour
 * 
 */
public class RankCountMapper extends Mapper<Entity, String, Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger LOG = Logger.getLogger(RankCountMapper.class.getName());

	private String source;

	private String country;

	private String type;

	private int start;

	private int end;

	public RankCountMapper(String source, String country, String type, int start, int end) {
		this.source = source;
		this.country = country;
		this.type = type;
		this.start = start;
		this.end = end;
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
		
		if (rank.position <= end && rank.position >= start) {
			String shortType = null;
			
			if (rank.type.equals(DataCollectorIOS.TOP_FREE_APPS)) {
				shortType = "tfa";
			} else if (rank.type.equals(DataCollectorIOS.TOP_PAID_APPS)) {
				shortType = "tpa";
			} else if (rank.type.equals(DataCollectorIOS.TOP_GROSSING_APPS)) {
				shortType = "tga";
			} else if (rank.type.equals(DataCollectorIOS.TOP_FREE_IPAD_APPS)) {
				shortType = "tfia";
			} else if (rank.type.equals(DataCollectorIOS.TOP_PAID_IPAD_APPS)) {
				shortType = "tpia";
			} else if (rank.type.equals(DataCollectorIOS.TOP_GROSSING_IPAD_APPS)) {
				shortType = "tgia";
			} else if (rank.type.equals(DataCollectorIOS.NEW_APPS)) {
				shortType = "na";
			} else if (rank.type.equals(DataCollectorIOS.NEW_FREE_APPS)) {
				shortType = "nfa";
			} else if (rank.type.equals(DataCollectorIOS.NEW_PAID_APPS)) {
				shortType = "npa";
			}
			
			String name = shortType + "." + rank.itemId + "." + Integer.toString(start) + "." + Integer.toString(end);
			
			emit1(name);	
		}		

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
