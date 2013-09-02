/**
 * 
 */
package io.reflection.app.ingestors;

import static com.spacehopperstudios.utility.StringUtils.urldecode;
import static com.spacehopperstudios.utility.StringUtils.urlencode;
import static io.reflection.app.objectify.PersistenceService.ofy;
import io.reflection.app.collectors.CollectorFactory;
import io.reflection.app.datatypes.Item;
import io.reflection.app.datatypes.Rank;
import io.reflection.app.logging.GaeLevel;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.google.appengine.api.taskqueue.TransientFailureException;

/**
 * @author billy1380
 * 
 */
public class DataStorePersist {

	private static final Logger LOG = Logger.getLogger(DataStorePersist.class.getName());

	public void enqueue(Item item, Integer position, String itemId, String type, String store, String country, Date date, Float price, String currency,
			String code) {
		if (LOG.isLoggable(GaeLevel.TRACE)) {
			LOG.log(GaeLevel.TRACE, "Entering...");
		}

		try {
			Queue queue = QueueFactory.getQueue("persist");

			if (queue != null) {
				enqueue(queue, item, position, type, store, country, date, price, currency, code);
			}
		} finally {
			if (LOG.isLoggable(GaeLevel.TRACE)) {
				LOG.log(GaeLevel.TRACE, "Exiting...");
			}
		}
	}

	private String convertItemRankToPayloadString(Item item, Integer position, String type, String store, String country, Date date, Float price,
			String currency, String code) {
		return String
				.format("itemAdded=%d&itemCurrency=%s&itemExternalId=%s&itemInternalId=%s&itemName=%s&itemPrice=%f&itemSource=%s&itemType=%s&rankCode=%s&rankCountry=%s&rankCurrency=%s&rankDate=%d&rankItemId=%s&rankPosition=%d&rankPrice=%f&rankSource=%s&rankType=%s",
						item.added, item.currency, urlencode(item.externalId), item.internalId, urlencode(item.name), item.price, item.source, item.type, code,
						country, currency, date.getTime(), urlencode(item.externalId), position, price, store, type);
	}

	private TaskOptions convertItemRankToParams(TaskOptions options, Item item, Integer position, String type, String store, String country, Date date,
			Float price, String currency, String code) {
		return options.param("itemAdded", Long.toString(item.added.getTime())).param("itemCurrency", item.currency)
				.param("itemExternalId", urlencode(item.externalId)).param("itemInternalId", item.internalId).param("itemName", urlencode(item.name))
				.param("itemPrice", item.price.toString()).param("itemSource", item.source).param("itemType", item.type).param("rankCode", code)
				.param("rankCountry", country).param("rankCurrency", currency).param("rankDate", Long.toString(date.getTime()))
				.param("rankItemId", urlencode(item.externalId)).param("rankPosition", position.toString()).param("rankPrice", price.toString())
				.param("rankSource", store).param("rankType", type);
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
		Rank rank = null;

		String rankCode = req.getParameter("rankCode");
		String rankCountry = req.getParameter("rankCountry");
		String rankCurrency = req.getParameter("rankCurrency");
		Date rankDate = new Date(Long.parseLong(req.getParameter("rankDate")));
		String rankItemId = urldecode(req.getParameter("rankItemId"));
		int rankPosition = Integer.parseInt(req.getParameter("rankPosition"));
		float rankPrice = Float.parseFloat(req.getParameter("rankPrice"));
		String rankSource = req.getParameter("rankSource");
		String rankType = req.getParameter("rankType");

		boolean isGrossing = isGrossing(rankSource, rankType);

		if ((rank = getRankWithParameters(rankSource, rankCountry, rankItemId, rankType, rankDate, rankCode)) == null) {
			rank = new Rank();

			rank.code = rankCode;
			rank.country = rankCountry;
			rank.currency = rankCurrency;
			rank.date = rankDate;
			rank.itemId = rankItemId;
			rank.price = rankPrice;
			rank.source = rankSource;
			rank.type = rankType;
		}

		if (isGrossing) {
			rank.grossingPosition = rankPosition;
		} else {
			rank.position = rankPosition;
			rank.type = rankType;
		}

		return rank;
	}

	private boolean isGrossing(String store, String type) {
		return CollectorFactory.getCollectorForStore(store).isGrossing(type);
	}

	public static Rank getRankWithParameters(String store, String country, String itemId, String type, Date date, String code) {

		Rank rank = null;

		List<String> counterpartType = CollectorFactory.getCollectorForStore(store).getCounterpartTypes(type);

		Date startDate, endDate;

		Calendar c = Calendar.getInstance();

		c.setTime(date);
		c.add(Calendar.HOUR, -2);
		startDate = c.getTime();

		c.setTime(date);
		c.add(Calendar.HOUR, 2);
		endDate = c.getTime();

		List<Rank> foundRanks = ofy().cache(false).load().type(Rank.class).filter("source =", store).filter("country =", country).filter("itemId =", itemId)
				.filter("date >=", startDate).filter("date <", endDate).filter("type in", counterpartType).list();

		if (foundRanks != null) {
			for (Rank found : foundRanks) {
				// if one of the items of the counter types within the four hour window matches the gather code then that is the right row quit

				if (code.equals(found.code)) {
					rank = found;
					break;
				}
			}
		}

		return rank;
	}

	private void enqueue(Queue queue, Item item, Integer position, String type, String store, String country, Date date, Float price, String currency,
			String code) {

		TaskOptions options = convertItemRankToParams(TaskOptions.Builder.withUrl("/persist").method(Method.POST), item, position, type, store, country, date,
				price, currency, code);

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
					LOG.log(Level.SEVERE,
							String.format("Retry of with payload [%s] failed while adding to queue [%s] twice",
									convertItemRankToPayloadString(item, position, type, store, country, date, price, currency, code), queue.getQueueName()),
							reEx);
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
