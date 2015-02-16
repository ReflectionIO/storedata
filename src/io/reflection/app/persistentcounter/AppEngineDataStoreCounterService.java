//
//  AppEngineDataStoreCounterService.java
//  storedata
//
//  Created by Stefano Capuzzi on 23 Oct 2014.
//  Copyright © 2014 Reflection.io Ltd. All rights reserved.
//
package io.reflection.app.persistentcounter;

import java.util.ConcurrentModificationException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheService.SetPolicy;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

/**
 * @author Stefano Capuzzi
 *
 */
public class AppEngineDataStoreCounterService implements ICounterService {

	/**
	 * Entity kind representing a named sharded counter.
	 */
	static final String COUNTER_KIND = "Counter";

	/**
	 * Property to store the number of shards in a given {@value #COUNTER_KIND} named sharded counter.
	 */
	static final String COUNTER_SHARD_COUNT = "shard_count";

	/**
	 * Property to store the current counter type.
	 */
	static final String COUNTER_PROPERTY_TYPE = "type";

	/**
	 * Entity kind prefix, which is concatenated with the counter name to form the final entity kind, which represents counter shards.
	 */
	static final String SHARD_KIND_PREFIX = "CounterShard_";

	/**
	 * Property to store the current count within a counter shard.
	 */
	static final String SHARD_COUNT = "count";

	/**
	 * Initial number of shards for every newly created counter
	 */
	static final int DEFAULT_SHARDS_NUMBER = 5;

	/**
	 * Cache duration for memcache.
	 */
	static final int CACHE_PERIOD = 60;

	/**
	 * DatastoreService object for Datastore access.
	 */
	private static final DatastoreService DS = DatastoreServiceFactory.getDatastoreService();

	/**
	 * A random number generating, for distributing writes across shards.
	 */
	private static final Random random = new Random();

	/**
	 * Memcache service object for Memcache access.
	 */
	private final MemcacheService mc = MemcacheServiceFactory.getMemcacheService();

	/**
	 * A logger object.
	 */
	private static final Logger LOG = Logger.getLogger(AppEngineDataStoreCounterService.class.getName());

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.persistentcounter.ICounterService#getCount(java.lang.String)
	 */
	@Override
	public Long getCount(String counterName) {

		Long count = Long.valueOf(0);

		String kind = SHARD_KIND_PREFIX + counterName;

		if ((Long) mc.get(kind) != null) {
			// get value from the cache
			count = (Long) mc.get(kind);
		} else {
			Query query = new Query(kind);
			for (Entity shard : DS.prepare(query).asIterable()) {
				count += (Long) shard.getProperty(SHARD_COUNT);
			}
			mc.put(kind, count, Expiration.byDeltaSeconds(CACHE_PERIOD), SetPolicy.ADD_ONLY_IF_NOT_PRESENT);
		}

		return count;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.persistentcounter.ICounterService#increment(java.lang.String, java.lang.Integer)
	 */
	@Override
	public void increment(String counterName, Integer incrementValue) {
		if (incrementValue == null) {
			incrementValue = Integer.valueOf(1);
		}

		if (!counterExists(counterName)) {
			createCounter(counterName);
		}

		String kind = SHARD_KIND_PREFIX + counterName;

		// Choose the shard randomly from the available shards.
		int numShards = getShardCount(counterName);
		long shardNum = random.nextInt(numShards);
		Key shardKey = getCounterShardKey(kind, shardNum);

		// Increment count of selected shard
		Transaction tx = DS.beginTransaction();
		Entity shard;
		Long value;
		try {
			try {
				shard = DS.get(tx, shardKey);
				value = Long.valueOf(((Long) shard.getProperty(SHARD_COUNT)).longValue() + incrementValue.longValue());
			} catch (EntityNotFoundException e) {
				// Lazy creation of the shard
				shard = new Entity(shardKey);
				value = Long.valueOf(incrementValue.longValue());
			}
			shard.setUnindexedProperty(SHARD_COUNT, value);
			DS.put(tx, shard);
			tx.commit();
		} catch (ConcurrentModificationException e) {
			LOG.log(Level.WARNING, e.toString(), e);
			// Automatically add a new shard
			addCounterShards(counterName, 1);
		} catch (Exception e) {
			LOG.log(Level.WARNING, e.toString(), e);
		} finally {
			if (tx.isActive()) {
				tx.rollback();
			}
		}

		mc.increment(kind, incrementValue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.persistentcounter.ICounterService#increment(java.lang.String)
	 */
	@Override
	public void increment(String counterName) {
		increment(counterName, 1);
	}

	private Key getCounterKey(String counterName) {
		return KeyFactory.createKey(COUNTER_KIND, counterName);
	}

	private Key getCounterShardKey(String kind, long shardNum) {
		return KeyFactory.createKey(kind, Long.toString(shardNum));
	}

	/**
	 * Create new counter
	 * 
	 * @param counterName
	 */
	private void createCounter(String counterName) {

		// Create Counter object
		Transaction tx = DS.beginTransaction();
		Entity counter = new Entity(getCounterKey(counterName));
		try {
			DS.put(tx, counter);
			tx.commit();
		} catch (Exception e) {
			LOG.log(Level.WARNING, e.toString(), e);
		} finally {
			if (tx.isActive()) {
				tx.rollback();
			}
		}

		// Add shards to the counter
		addCounterShards(counterName, DEFAULT_SHARDS_NUMBER);
	}

	/*
	 * Get the number of shards in this counter.
	 * 
	 * @return shard count
	 */
	private int getShardCount(String counterName) {
		int count;
		try {
			Entity counter = DS.get(getCounterKey(counterName));
			Long shardCount = (Long) counter.getProperty(COUNTER_SHARD_COUNT);
			count = shardCount.intValue();
		} catch (EntityNotFoundException e) {
			LOG.log(Level.WARNING, "ShardedCount not found.");
			addCounterShards(counterName, DEFAULT_SHARDS_NUMBER);
			count = DEFAULT_SHARDS_NUMBER;
		}
		return count;
	}

	/**
	 * true if the counter is present in the datastore
	 * 
	 * @param counterName
	 * @return
	 */
	private boolean counterExists(String counterName) {
		boolean counterExists = true;
		try {
			DS.get(getCounterKey(counterName));
		} catch (EntityNotFoundException e) {
			counterExists = false;
		}
		return counterExists;
	}

	/**
	 * Increase the number of shards for a given sharded counter, or add to the counter if don't exists
	 *
	 * @param count
	 *            Number of new shards to build and store
	 */
	private void addCounterShards(String counterName, int count) {
		Key counterKey = getCounterKey(counterName);

		Transaction tx = DS.beginTransaction();
		Entity counter;
		long value;
		try {
			counter = DS.get(tx, counterKey);
			if (counter.getProperty(COUNTER_SHARD_COUNT) == null) {
				value = DEFAULT_SHARDS_NUMBER;
			} else {
				value = (Long) counter.getProperty(COUNTER_SHARD_COUNT) + count;
			}
			counter.setUnindexedProperty(COUNTER_SHARD_COUNT, value);
			DS.put(tx, counter);
			tx.commit();
		} catch (Exception e) {
			LOG.log(Level.WARNING, e.toString(), e);
		} finally {
			if (tx.isActive()) {
				tx.rollback();
			}
		}
	}

}
