/**
 * 
 */
package com.spacehopperstudios.storedata.ingestors;

import java.util.List;

/**
 * @author billy1380
 * 
 */
public interface Ingestor {

	public static final String ENQUEUE_INGEST_FORMAT = "/ingest?store=%s&iids=%s";

	void ingest(List<Long> itemIds);

	void enqueue(List<Long> itemIds);
}
