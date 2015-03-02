/**
 * 
 */
package io.reflection.app.ingestors;

import io.reflection.app.api.exception.DataAccessException;

import java.util.List;

/**
 * @author billy1380
 * 
 */
public interface Ingestor {

	public static final String ENQUEUE_INGEST_FORMAT = "/ingest?store=%s&iids=%s";

	void ingest(List<Long> itemIds) throws DataAccessException;
	
	void enqueue(List<Long> itemIds);
}
