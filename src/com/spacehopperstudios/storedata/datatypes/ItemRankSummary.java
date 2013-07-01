package com.spacehopperstudios.storedata.datatypes;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Index;

@Entity
public class ItemRankSummary extends DataType {

	@Index
	public String itemId;

	@Index
	public String type;

	@Index
	public String source;

	@Index
	public int numberOfTimesRanked;

	@Index
	public int numberOfTimesRankedTop10;

	@Index
	public int numberOfTimesRankedTop25;

	@Index
	public int numberOfTimesRankedTop50;

	@Index
	public int numberOfTimesRankedTop100;

	@Index
	public int numberOfTimesRankedTop200;
}
