/**
 * 
 */
package com.spacehopperstudios.storedata.ingestors;

import java.util.List;

import com.spacehopperstudios.storedata.datatypes.Item;

/**
 * @author billy1380
 *
 */
public interface Parser {
	/** 
	 * Returns an ordered list of items extracted from the data
	 * @param data
	 * @return
	 */
	List<Item> parse(String data);
}
