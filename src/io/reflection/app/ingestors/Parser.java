/**
 * 
 */
package io.reflection.app.ingestors;

import io.reflection.app.datatypes.Item;

import java.util.List;

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
