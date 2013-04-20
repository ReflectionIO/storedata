/**
 * 
 */
package com.spacehopperstudios.storedatacollector.ingestors;


/**
 * @author billy1380
 *
 */
public interface Ingestor {
	boolean ingest();
	boolean ingest(int count);
	boolean ingest(int count, String type);
}
