package com.spacehopperstudios.storedatacollector.datatypes;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
public class ItemRankSummary {
	@Id
	public Long id;
	
	@Index
	public String itemId;
	
	@Index
	public String type;
	
	@Index
	public String source;
	
	public int numberOfTimesRanked;
	public int numberOfTimesRankedTop10;
	public int numberOfTimesRankedTop25;
	public int numberOfTimesRankedTop50;
	public int numberOfTimesRankedTop100;
	public int numberOfTimesRankedTop200;
}
