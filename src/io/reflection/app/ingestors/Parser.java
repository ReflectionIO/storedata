/**
 * 
 */
package io.reflection.app.ingestors;

import io.reflection.app.datatypes.shared.Item;

import java.util.List;

/**
 * @author billy1380
 * 
 */
public interface Parser {
	/**
	 * Returns an ordered list of items extracted from the data
	 * 
	 * @param country
	 * @param category
	 * @param data
	 * @return
	 */
	List<Item> parse(String country, Long category, String data);
}
