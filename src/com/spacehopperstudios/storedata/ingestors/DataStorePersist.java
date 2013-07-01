/**
 * 
 */
package com.spacehopperstudios.storedata.ingestors;

import static com.spacehopperstudios.storedata.objectify.PersistenceService.ofy;
import static com.spacehopperstudios.utility.StringUtils.urldecode;
import static com.spacehopperstudios.utility.StringUtils.urlencode;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.google.appengine.api.taskqueue.TransientFailureException;
import com.spacehopperstudios.storedata.datatypes.Item;
import com.spacehopperstudios.storedata.datatypes.Rank;
import com.spacehopperstudios.storedata.logging.GaeLevel;

/**
 * @author billy1380
 * 
 */
public class DataStorePersist {

	private static final Logger LOG = Logger.getLogger(DataStorePersist.class.getName());

	public void enqueue(Item item, Rank rank) {
		if (LOG.isLoggable(GaeLevel.TRACE)) {
			LOG.log(GaeLevel.TRACE, "Entering...");
		}

		try {
			Queue queue = QueueFactory.getQueue("persist");

			if (queue != null) {
				enqueue(queue, item, rank);
			}
		} finally {
			if (LOG.isLoggable(GaeLevel.TRACE)) {
				LOG.log(GaeLevel.TRACE, "Exiting...");
			}
		}
	}

	private String convertItemRankToPayloadString(Item item, Rank rank) {
		return String
				.format("itemAdded=%d&itemCurrency=%s&itemExternalId=%s&itemInternalId=%s&itemName=%s&itemPrice=%f&itemSource=%s&itemType=%s&rankCode=%s&rankCounted=%b&rankCountry=%s&rankCurrency=%s&rankDate=%d&rankItemId=%s&rankPosition=%d&rankPrice=%f&rankSource=%s&rankType=%s",
						item.added, item.currency, urlencode(item.externalId), item.internalId, urlencode(item.name), item.price, item.source, item.type,
						rank.code, rank.counted, rank.country, rank.currency, rank.date.getTime(), urlencode(rank.itemId), rank.position, rank.price,
						rank.source, rank.type);
	}

	private TaskOptions convertItemRankToParams(TaskOptions options, Item item, Rank rank) {
		return options.param("itemAdded", Long.toString(item.added.getTime())).param("itemCurrency", item.currency)
				.param("itemExternalId", urlencode(item.externalId)).param("itemInternalId", item.internalId).param("itemName", urlencode(item.name))
				.param("itemPrice", item.price.toString()).param("itemSource", item.source).param("itemType", item.type).param("rankCode", rank.code)
				.param("rankCounted", rank.counted.toString()).param("rankCountry", rank.country).param("rankCurrency", rank.currency)
				.param("rankDate", Long.toString(rank.date.getTime())).param("rankItemId", urlencode(rank.itemId))
				.param("rankPosition", rank.position.toString()).param("rankPrice", rank.price.toString()).param("rankSource", rank.source)
				.param("rankType", rank.type);
	}

	private Item convertParamsToItem(HttpServletRequest req) {
		Item item = new Item();
		item.currency = req.getParameter("itemCurrency");
		item.added = new Date(Long.parseLong(req.getParameter("itemAdded")));
		item.externalId = urldecode(req.getParameter("itemExternalId"));
		item.internalId = req.getParameter("itemInternalId");
		item.name = urldecode(req.getParameter("itemName"));
		item.price = Float.parseFloat(req.getParameter("itemPrice"));
		item.source = req.getParameter("itemSource");
		item.type = req.getParameter("itemType");

		return item;
	}

	private Rank convertParamsToRank(HttpServletRequest req) {
		Rank rank = new Rank();
		rank.code = req.getParameter("rankCode");
		rank.counted = Boolean.parseBoolean(req.getParameter("rankCounted"));
		rank.country = req.getParameter("rankCountry");
		rank.currency = req.getParameter("rankCurrency");
		rank.date = new Date(Long.parseLong(req.getParameter("rankDate")));
		rank.itemId = urldecode(req.getParameter("rankItemId"));
		rank.position = Integer.parseInt(req.getParameter("rankPosition"));
		rank.price = Float.parseFloat(req.getParameter("rankPrice"));
		rank.source = req.getParameter("rankSource");
		rank.type = req.getParameter("rankType");

		return rank;
	}

	private void enqueue(Queue queue, Item item, Rank rank) {

		TaskOptions options = convertItemRankToParams(TaskOptions.Builder.withUrl("/persist").method(Method.POST), item, rank);

		try {
			queue.add(options);
		} catch (TransientFailureException ex) {

			if (LOG.isLoggable(Level.WARNING)) {
				LOG.warning(String.format("Could not queue a message because of [%s] - will retry it once", ex.toString()));
			}

			// retry once
			try {
				queue.add(options);
			} catch (TransientFailureException reEx) {
				if (LOG.isLoggable(Level.SEVERE)) {
					LOG.log(Level.SEVERE, String.format("Retry of with payload [%s] failed while adding to queue [%s] twice",
							convertItemRankToPayloadString(item, rank), queue.getQueueName()), reEx);
				}
			}
		}
	}

	public void perisist(HttpServletRequest req) {
		if (LOG.isLoggable(GaeLevel.TRACE)) {
			LOG.log(GaeLevel.TRACE, "Entering...");
		}
		
		Item item = convertParamsToItem(req);
		Rank rank = convertParamsToRank(req);

		if (ofy().load().type(Item.class).filter("externalId =", item.externalId).count() == 0) {
			ofy().save().entity(item).now();
		}

		if (ofy().cache(false).load().type(Rank.class).filter("source =", rank.source).filter("type =", rank.type).filter("date =", rank.date)
				.filter("country =", rank.country).filter("position =", rank.position).count() == 0) {
			ofy().save().entity(rank).now();

			if (LOG.isLoggable(GaeLevel.TRACE)) {
				LOG.log(GaeLevel.TRACE, String.format("Saved rank [%s] for", rank.itemId));
			}
		}
		
		if (LOG.isLoggable(GaeLevel.TRACE)) {
			LOG.log(GaeLevel.TRACE, "Exiting...");
		}
	}
}
