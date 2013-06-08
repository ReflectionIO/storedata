package com.spacehopperstudios.storedatacollector.mapreduce;

import java.nio.ByteBuffer;
import java.util.logging.Logger;

import com.google.appengine.tools.mapreduce.Reducer;
import com.google.appengine.tools.mapreduce.ReducerInput;
import com.google.gson.Gson;
import com.spacehopperstudios.storedatacollector.collectors.DataCollectorIOS;
import com.spacehopperstudios.storedatacollector.datatypes.Rank;

public class CsvBlobReducer extends Reducer<String, String, ByteBuffer> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger LOG = Logger.getLogger(CsvBlobReducer.class.getName());

	@Override
	public void beginShard() {
		LOG.info("beginShard()");

		StringBuffer buffer = new StringBuffer();
		buffer.append("# data for shard ");
		buffer.append(getContext().getShardNumber());
		buffer.append(" extracted from toppaidapplications and topgrossingapplications");
		buffer.append("\n");
		buffer.append("#item id,date,paid possition,grossing possition,price");
		buffer.append("\n");

		getContext().emit(ByteBuffer.wrap(buffer.toString().getBytes()));
	}

	@Override
	public void beginSlice() {
		LOG.info("beginSlice()");
	}

	@Override
	public void reduce(String key, ReducerInput<String> values) {
		Rank grossingItem = null, paidItem = null;
		Rank masterItem = null;
		
		StringBuffer buffer = new StringBuffer();
		
		int i = 0;
		while(values.hasNext()) {
			i++;
			
			masterItem = (new Gson()).fromJson(values.next(), Rank.class);
			
			if (masterItem.type.equals(DataCollectorIOS.TOP_PAID_APPS)) {
				paidItem = masterItem;
			} else if (masterItem.type.equals(DataCollectorIOS.TOP_GROSSING_APPS)) {
				grossingItem = masterItem;
			}
		}

		if (i > 2) {
			LOG.warning("Found more than 2 items in a single reducer input, this should not happen");
		}

		buffer.append(masterItem.itemId);
		buffer.append(",");
		buffer.append(masterItem.date);
		buffer.append(",");

		if (paidItem != null) {
			buffer.append(paidItem.position + 1);
		}
		buffer.append(",");

		if (grossingItem != null) {
			buffer.append(grossingItem.position + 1);
		}
		buffer.append(",");

		buffer.append(masterItem.price);
		buffer.append("\n");
		
		getContext().emit(ByteBuffer.wrap(buffer.toString().getBytes()));
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
