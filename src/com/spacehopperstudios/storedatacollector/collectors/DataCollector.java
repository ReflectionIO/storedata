/**
 * 
 */
package com.spacehopperstudios.storedatacollector.collectors;

/**
 * @author billy1380
 *
 */
public interface DataCollector {
	/**
	 * Collects data for 
	 * @param country the store country
	 * @param type 
	 */
	void collect(String country, String type);
	
	/**
	 * Puts a message in the queue for each list for each country
	 */
	void enqueueCountriesAndTypes();
}
