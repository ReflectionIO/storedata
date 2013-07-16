/**
 * 
 */
package io.reflection.app.collectors;

import java.util.List;

/**
 * @author billy1380
 * 
 */
public interface Collector {

	public static final String ENQUEUE_GATHER_FORMAT = "/gather?store=%s&country=%s&type=%s&code=%s";

	/**
	 * Collects data for
	 * 
	 * @param country
	 *            the store country
	 * @param type
	 *            top paid, top free top grossing etc
	 * @param code
	 *            ties all items in a single gather such that top grossing and top paid and top free entries can be married
	 */
	List<Long> collect(String country, String type, String code);

	/**
	 * Puts a message in the queue for each list for each country
	 * 
	 * @return The number of messages added
	 */
	int enqueue();
}
