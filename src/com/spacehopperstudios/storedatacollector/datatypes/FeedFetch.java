/**
 * 
 */
package com.spacehopperstudios.storedatacollector.datatypes;

import java.util.Date;

import com.googlecode.objectify.annotation.AlsoLoad;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

/**
 * @author billy1380
 * 
 */
@Entity
public class FeedFetch {

	@Id
	public Long id;

	public String country;
	public String data;

	@Index
	public Date date;

	@Index
	public boolean ingested;

	@Index
	public String store;

	public int part;
	@AlsoLoad("totalparts")
	public int totalParts;

	@Index
	public String type;

	public String code;

}
