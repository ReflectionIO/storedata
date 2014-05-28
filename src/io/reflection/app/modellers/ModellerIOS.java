//
//  ModellerIOS.java
//  storedata
//
//  Created by William Shakour (billy1380) on 14 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.modellers;

import static com.spacehopperstudios.utility.StringUtils.addslashes;
import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.collectors.Collector;
import io.reflection.app.collectors.CollectorFactory;
import io.reflection.app.datatypes.shared.Category;
import io.reflection.app.datatypes.shared.Country;
import io.reflection.app.datatypes.shared.FeedFetch;
import io.reflection.app.datatypes.shared.FeedFetchStatusType;
import io.reflection.app.datatypes.shared.FormType;
import io.reflection.app.datatypes.shared.ModelRun;
import io.reflection.app.datatypes.shared.Store;
import io.reflection.app.logging.GaeLevel;
import io.reflection.app.renjin.RenjinRModellerBase;
import io.reflection.app.repackaged.scphopr.cloudsql.Connection;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseServiceProvider;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseType;
import io.reflection.app.service.category.CategoryServiceProvider;
import io.reflection.app.service.feedfetch.FeedFetchServiceProvider;
import io.reflection.app.service.modelrun.ModelRunServiceProvider;
import io.reflection.app.service.rank.RankServiceProvider;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.script.ScriptException;

import org.renjin.primitives.sequence.IntSequence;
import org.renjin.sexp.DoubleArrayVector;
import org.renjin.sexp.IntArrayVector;
import org.renjin.sexp.IntVector;
import org.renjin.sexp.ListVector;
import org.renjin.sexp.LogicalArrayVector;
import org.renjin.sexp.LogicalVector;
import org.renjin.sexp.StringArrayVector;
import org.renjin.sexp.StringVector;
import org.renjin.sexp.Symbols;
import org.renjin.sexp.Vector;

import com.google.appengine.api.backends.BackendService;
import com.google.appengine.api.backends.BackendServiceFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.google.appengine.api.taskqueue.TransientFailureException;
import com.spacehopperstudios.utility.StringUtils;

/**
 * @author billy1380
 * 
 */
@SuppressWarnings("deprecation")
public class ModellerIOS extends RenjinRModellerBase implements Modeller {

	private static final Logger LOG = Logger.getLogger(ModellerIOS.class.getName());

	private static final String STORE = "ios";

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.modellers.Modeller#enqueue(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void enqueue(String country, String type, Long code) {
		if (LOG.isLoggable(GaeLevel.TRACE)) {
			LOG.log(GaeLevel.TRACE, "Entering...");
		}

		try {
			Queue queue = QueueFactory.getQueue("model");

			BackendService backendApi = BackendServiceFactory.getBackendService();
			String backendServerName = backendApi.getBackendAddress("model");

			TaskOptions options = TaskOptions.Builder.withUrl("/model").method(Method.POST);
			options.param("store", STORE);
			options.param("country", country);
			options.param("type", type);
			options.param("code", code.toString());

			options.header("Host", backendServerName);

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
								String.format("Retry of with payload [%s] failed while adding to queue [%s] twice", options.toString(), queue.getQueueName()),
								reEx);
					}
				}
			}
		} finally {
			if (LOG.isLoggable(GaeLevel.TRACE)) {
				LOG.log(GaeLevel.TRACE, "Exiting...");
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.models.Model#modelVariables(java.lang.String, java.lang.String, java.lang.Long)
	 */
	@Override
	public void modelVariables(String country, String listType, Long code) throws DataAccessException {
		if (LOG.isLoggable(GaeLevel.TRACE)) {
			LOG.log(GaeLevel.TRACE, "Entering...");
		}

		Date date = RankServiceProvider.provide().getCodeLastRankDate(code);

		try {
			init();

			mEngine.eval("cut.point <- 400");

			// set simulation inputs
			mEngine.eval("Napps  <- 40");
			mEngine.eval("Dt.in <- 500000");

			Collector collector = CollectorFactory.getCollectorForStore(STORE);
			List<String> listTypes = new ArrayList<String>();
			listTypes.addAll(collector.getCounterpartTypes(listType));
			listTypes.add(addslashes(listType));

			put(STORE, country, listTypes, date, "`r`.`price`=0", "free.raw");

			put(STORE, country, listTypes, date, "`r`.`price`<>0", "paid.raw");

			runModelParts();

			Country c = new Country();
			c.a2Code = country;

			Store s = new Store();
			s.a3Code = STORE;

			persistValues(c, s, getForm(listType), code);

			alterFeedFetchStatus(c, s, listTypes, code);

		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | ScriptException | SQLException e) {
			LOG.log(Level.SEVERE, String.format("Error occured calculating values with parameters store [%s], country [%s], type [%s], [%s]", STORE, country,
					listType, date == null ? "null" : Long.toString(date.getTime())), e);

			throw new RuntimeException(e);
		} finally {
			if (LOG.isLoggable(GaeLevel.TRACE)) {
				LOG.log(GaeLevel.TRACE, "Exiting...");
			}
		}

	}

	/**
	 * 
	 * @param country
	 * @param store
	 * @param listTypes
	 * @param code
	 * @throws DataAccessException
	 */
	private void alterFeedFetchStatus(Country country, Store store, List<String> listTypes, Long code) throws DataAccessException {
		List<FeedFetch> feeds = FeedFetchServiceProvider.provide().getGatherCodeFeedFetches(country, store, listTypes, code);

		for (FeedFetch feedFetch : feeds) {
			feedFetch.status = FeedFetchStatusType.FeedFetchStatusTypeModelled;

			FeedFetchServiceProvider.provide().updateFeedFetch(feedFetch);
		}
	}

	/**
	 * @param code
	 * @param listTypes
	 * @param country
	 * @param store
	 * @throws DataAccessException
	 * 
	 */
	private void persistValues(Country country, Store store, FormType form, Long code) throws DataAccessException {

		ModelRun run = ModelRunServiceProvider.provide().getGatherCodeModelRun(country, store, form, code);

		boolean isUpdate = false;

		if (run == null) {
			run = new ModelRun();
		} else {
			isUpdate = true;
		}

		if (!isUpdate) {
			run.country = country.a2Code;
			run.store = store.a3Code;
			run.code = code;
			run.form = form;
		}

		run.grossingA = Double.valueOf(((Vector) mEngine.get("ag")).asReal());
		run.paidA = Double.valueOf(((Vector) mEngine.get("ap")).asReal());
		run.bRatio = Double.valueOf(((Vector) mEngine.get("b.ratio")).asReal());
		run.totalDownloads = Double.valueOf(((Vector) mEngine.get("Dt")).asReal());
		run.paidB = Double.valueOf(((Vector) mEngine.get("bp")).asReal());
		run.grossingB = Double.valueOf(((Vector) mEngine.get("bg")).asReal());
		run.paidAIap = Double.valueOf(((Vector) mEngine.get("iap.ap")).asReal());
		run.grossingAIap = Double.valueOf(((Vector) mEngine.get("iap.ag")).asReal());
		run.freeA = Double.valueOf(((Vector) mEngine.get("af")).asReal());
		run.theta = Double.valueOf(((Vector) mEngine.get("th")).asReal());
		run.freeB = Double.valueOf(((Vector) mEngine.get("bf")).asReal());

		if (isUpdate) {
			ModelRunServiceProvider.provide().updateModelRun(run);
		} else {
			ModelRunServiceProvider.provide().addModelRun(run);
		}
	}

	private void put(String store, String country, List<String> listTypes, Date date, String priceQuery, String rVariable) throws ScriptException,
			InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, DataAccessException {

		ListVector.Builder builder = ListVector.newBuilder();
		builder.setAttribute(Symbols.CLASS, new StringArrayVector("data.frame"));
		builder.setAttribute(Symbols.NAMES, new StringArrayVector("item.id", "top.position", "grossing.position", "price", "usesiap"));

		String typesQueryPart = null;
		if (listTypes.size() == 1) {
			typesQueryPart = String.format("`type`='%s'", listTypes.get(0));
		} else {
			typesQueryPart = "`type` IN ('" + StringUtils.join(listTypes, "','") + "')";
		}

		Store s = new Store();
		s.a3Code = STORE;

		Category category = CategoryServiceProvider.provide().getAllCategory(s);

		String query = String.format(
				"SELECT `r`.`itemid`, `r`.`position`,`r`.`grossingposition`, `r`.`price`, `s`.`usesiap` FROM `rank` AS `r` JOIN `item` AS `i`"
						+ " ON `i`.`internalid`=`r`.`itemid` LEFT JOIN `sup_application_iap` AS `s` ON `s`.`internalid`=`i`.`internalid`"
						+ " WHERE `r`.`country`='%s' AND `r`.`categoryid`=%d AND `r`.`source`='%s' AND %s AND `r`.%s AND `date`<FROM_UNIXTIME(%d)"
						+ " ORDER BY `date` DESC", country, category.id.longValue(), store, priceQuery, typesQueryPart, date.getTime() / 1000);

		StringVector.Builder itemIdBuilder = StringVector.newBuilder();
		IntArrayVector.Builder topPositionBuilder = new IntArrayVector.Builder();
		IntArrayVector.Builder grossingPositionBuilder = new IntArrayVector.Builder();
		DoubleArrayVector.Builder priceBuilder = new DoubleArrayVector.Builder();
		LogicalArrayVector.Builder usesIapBuilder = new LogicalArrayVector.Builder();

		int i = 0;

		Connection connection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeRank.toString());

		try {

			connection.connect();
			connection.executeQuery(query.toString());

			while (connection.fetchNextRow()) {
				itemIdBuilder.add(connection.getCurrentRowString("itemid"));
				Integer topPosition = connection.getCurrentRowInteger("position");
				topPositionBuilder.add(topPosition == null || topPosition.intValue() == 0 ? IntVector.NA : topPosition);
				Integer grossingPosition = connection.getCurrentRowInteger("grossingposition");
				grossingPositionBuilder.add(grossingPosition == null || grossingPosition.intValue() == 0 ? IntVector.NA : grossingPosition);
				double price = connection.getCurrentRowInteger("price").intValue() / 100.0;
				priceBuilder.add(price);
				String usesIap = connection.getCurrentRowString("usesiap");
				usesIapBuilder.add("y".equals(usesIap) ? 1 : ("n".equals(usesIap) ? 0 : LogicalVector.NA));
				i++;
			}

		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}

		int numRows = i;

		builder.add(itemIdBuilder.build());
		builder.add(topPositionBuilder.build());
		builder.add(grossingPositionBuilder.build());
		builder.add(priceBuilder.build());
		builder.add(usesIapBuilder.build());
		builder.setAttribute(Symbols.ROW_NAMES, new IntSequence(1, 1, numRows));

		put(rVariable, builder.build());
	}

	private void runModelParts() throws ScriptException {
		// runs the model
		InputStream stream = ModellerIOS.class.getResourceAsStream("../models/r/model.R");

		InputStreamReader reader = new InputStreamReader(stream);
		mEngine.eval(reader);

		if (LOG.isLoggable(GaeLevel.DEBUG)) {
			LOG.log(GaeLevel.DEBUG, "ag: " + ((Vector) mEngine.get("ag")).asReal());
			LOG.log(GaeLevel.DEBUG, "ap: " + ((Vector) mEngine.get("ap")).asReal());
			LOG.log(GaeLevel.DEBUG, "b.ratio: " + ((Vector) mEngine.get("b.ratio")).asReal());

			LOG.log(GaeLevel.DEBUG, "Dt: " + ((Vector) mEngine.get("Dt")).asReal());
			LOG.log(GaeLevel.DEBUG, "bp: " + ((Vector) mEngine.get("bp")).asReal());
			LOG.log(GaeLevel.DEBUG, "bg: " + ((Vector) mEngine.get("bg")).asReal());
		}

		// runs the model with Iap data
		stream = ModellerIOS.class.getResourceAsStream("../models/r/modelIap.R");
		reader = new InputStreamReader(stream);
		mEngine.eval(reader);

		if (LOG.isLoggable(GaeLevel.DEBUG)) {
			LOG.log(GaeLevel.DEBUG, "iap.ap: " + ((Vector) mEngine.get("iap.ap")).asReal());
			LOG.log(GaeLevel.DEBUG, "iap.ag: " + ((Vector) mEngine.get("iap.ag")).asReal());
			LOG.log(GaeLevel.DEBUG, "af: " + ((Vector) mEngine.get("af")).asReal());
			LOG.log(GaeLevel.DEBUG, "th: " + ((Vector) mEngine.get("th")).asReal());
			LOG.log(GaeLevel.DEBUG, "bf: " + ((Vector) mEngine.get("bf")).asReal());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.modellers.Modeller#getForm(java.lang.String)
	 */
	@Override
	public FormType getForm(String type) {
		return type.contains("ipad") ? FormType.FormTypeTablet : FormType.FormTypeOther;
	}

}
