//
//  ModelIOS.java
//  storedata
//
//  Created by William Shakour (billy1380) on 14 Oct 2013.
//  Copyright © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.models;

import static com.spacehopperstudios.utility.StringUtils.addslashes;
import io.reflection.app.collectors.Collector;
import io.reflection.app.collectors.CollectorFactory;
import io.reflection.app.logging.GaeLevel;
import io.reflection.app.renjin.RenjinRModelBase;
import io.reflection.app.repackaged.scphopr.cloudsql.Connection;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseServiceProvider;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseType;
import io.reflection.app.service.modelrun.ModelRunServiceProvider;
import io.reflection.app.service.rank.RankServiceProvider;
import io.reflection.app.shared.datatypes.FormType;
import io.reflection.app.shared.datatypes.ModelRun;

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
public class ModelIOS extends RenjinRModelBase implements Model {

	private static final Logger LOG = Logger.getLogger(ModelIOS.class.getName());

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.models.Model#enqueue(java.util.List)
	 */
	@Override
	public void enqueue(String store, String country, String type, String code) {
		if (LOG.isLoggable(GaeLevel.TRACE)) {
			LOG.log(GaeLevel.TRACE, "Entering...");
		}

		try {
			Queue queue = QueueFactory.getQueue("model");

			TaskOptions options = TaskOptions.Builder.withUrl("/model").method(Method.POST);
			options.param("store", store);
			options.param("country", country);
			options.param("type", type);
			options.param("code", code);

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
	 * @see io.reflection.app.models.Model#prepare(java.lang.String, java.lang.String, java.lang.String, java.util.Date)
	 */
	@Override
	public void prepare(String store, String country, String listType, String code) {
		if (LOG.isLoggable(GaeLevel.TRACE)) {
			LOG.log(GaeLevel.TRACE, "Entering...");
		}

		Date date = RankServiceProvider.provide().getCodeLastRankDate(code);

		try {
			mEngine.eval("cut.point <- 300");

			// set simulation inputs
			mEngine.eval("Napps  <- 30");
			mEngine.eval("Dt.in <- 100000");

			Collector collector = CollectorFactory.getCollectorForStore(store);
			List<String> listTypes = new ArrayList<String>();
			listTypes.addAll(collector.getCounterpartTypes(listType));
			listTypes.add(addslashes(listType));

			put(store, country, listTypes, date, "`r`.`price`=0", "free.raw");

			put(store, country, listTypes, date, "`r`.`price`<>0", "paid.raw");

			runAnalysis();

			persistValues(store, country, listTypes, code);

		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | ScriptException | SQLException e) {
			LOG.log(Level.SEVERE, String.format("Error occured calculating values with parameters store [%s], country [%s], type [%s], [%s]", store, country,
					listType, date == null ? "null" : Long.toString(date.getTime())), e);

			throw new RuntimeException(e);
		} finally {
			if (LOG.isLoggable(GaeLevel.TRACE)) {
				LOG.log(GaeLevel.TRACE, "Exiting...");
			}
		}

	}

	/**
	 * @param code
	 * @param listTypes
	 * @param country
	 * @param store
	 * 
	 */
	private void persistValues(String store, String country, List<String> listTypes, String code) {

		boolean isIpad = false;

		for (String listType : listTypes) {
			if (listType.contains("ipad")) {
				isIpad = true;
				break;
			}
		}

		FormType form = isIpad ? FormType.FormTypeTablet : FormType.FormTypeOther;

		ModelRun run = ModelRunServiceProvider.provide().getGatherCodeModelRun(store, country, form, code);

		boolean isUpdate = false;

		if (run == null) {
			run = new ModelRun();
		} else {
			isUpdate = true;
		}

		if (!isUpdate) {
			run.country = country;
			run.store = store;
			run.code = code;
			run.form = form;
		}

		run.grossingA = Double.valueOf(((Vector) mEngine.get("ag")).asReal());
		run.paidA = Double.valueOf(((Vector) mEngine.get("ap")).asReal());
		run.bRatio = Double.valueOf(((Vector) mEngine.get("b.ratio")).asReal());
		run.totalDownloads = Double.valueOf(((Vector) mEngine.get("Dt")).asReal());
		run.paidB = Double.valueOf(((Vector) mEngine.get("bp")).asReal());
		run.grossingB = Double.valueOf(((Vector) mEngine.get("bg")).asReal());
		run.piadAIap = Double.valueOf(((Vector) mEngine.get("iap.ap")).asReal());
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
			InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {

		ListVector.Builder builder = ListVector.newBuilder();
		builder.setAttribute(Symbols.CLASS, new StringArrayVector("data.frame"));
		builder.setAttribute(Symbols.NAMES, new StringArrayVector("item.id", "top.position", "grossing.position", "price", "usesiap"));

		String typesQueryPart = null;
		if (listTypes.size() == 1) {
			typesQueryPart = String.format("`type`='%s'", listTypes.get(0));
		} else {
			typesQueryPart = "`type` IN ('" + StringUtils.join(listTypes, "','") + "')";
		}

		Connection connection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeRank.toString());

		String query = String.format(
				"SELECT `r`.`itemid`, `r`.`position`,`r`.`grossingposition`, `r`.`price`, `s`.`usesiap` FROM `rank` AS `r` JOIN `item` AS `i`"
						+ " ON `i`.`externalid`=`r`.`itemid` LEFT JOIN `sup_application_iap` AS `s` ON `s`.`internalid`=`i`.`internalid`"
						+ " WHERE `r`.`country`='%s' AND `r`.`source`='%s' AND %s AND `r`.%s AND `date`<FROM_UNIXTIME(%d)" + " ORDER BY `date` DESC", country,
				store, priceQuery, typesQueryPart, date.getTime() / 1000);

		StringVector.Builder itemIdBuilder = StringVector.newBuilder();
		IntArrayVector.Builder topPositionBuilder = new IntArrayVector.Builder();
		IntArrayVector.Builder grossingPositionBuilder = new IntArrayVector.Builder();
		DoubleArrayVector.Builder priceBuilder = new DoubleArrayVector.Builder();
		LogicalArrayVector.Builder usesIapBuilder = new LogicalArrayVector.Builder();

		int i = 0;

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

	private void runAnalysis() throws ScriptException {
		// runs the stage 1
		InputStream stream = ModelIOS.class.getResourceAsStream("r/stage1.R");

		InputStreamReader reader = new InputStreamReader(stream);
		mEngine.eval(reader);

		if (LOG.isLoggable(GaeLevel.DEBUG)) {
			LOG.log(GaeLevel.DEBUG, "ag: " + ((Vector) mEngine.get("ag")).asReal());
			LOG.log(GaeLevel.DEBUG, "ap: " + ((Vector) mEngine.get("ap")).asReal());
			LOG.log(GaeLevel.DEBUG, "b.ratio: " + ((Vector) mEngine.get("b.ratio")).asReal());
		}

		// runs stage 2
		stream = ModelIOS.class.getResourceAsStream("r/stage2.R");
		reader = new InputStreamReader(stream);
		mEngine.eval(reader);

		if (LOG.isLoggable(GaeLevel.DEBUG)) {
			LOG.log(GaeLevel.DEBUG, "Dt: " + ((Vector) mEngine.get("Dt")).asReal());
			LOG.log(GaeLevel.DEBUG, "bp: " + ((Vector) mEngine.get("bp")).asReal());
			LOG.log(GaeLevel.DEBUG, "bg: " + ((Vector) mEngine.get("bg")).asReal());
		}

		// simulated data

		if (LOG.isLoggable(GaeLevel.DEBUG)) {
			LOG.log(GaeLevel.DEBUG, "my.labelled.df: " + mEngine.get("my.labelled.df"));
		}

		// // LOGGER.info("my.labelled.df: " + ((ListVector) engine.get("my.labelled.df")).toString());
		// ListVector df = (ListVector) engine.get("my.labelled.df");
		// StringArrayVector itemIds = (StringArrayVector) df.getElementAsSEXP(0);
		// IntArrayVector topRanks = (IntArrayVector) df.getElementAsSEXP(1);
		// IntArrayVector grossingRanks = (IntArrayVector) df.getElementAsSEXP(2);
		// DoubleArrayVector prices = (DoubleArrayVector) df.getElementAsSEXP(3);
		// DoubleArrayVector usesIaps = (DoubleArrayVector) df.getElementAsSEXP(4);
		// SEXP totalDownloads = df.getElementAsSEXP(5);
		// int count = totalDownloads.length();
		// StringBuffer buffer = new StringBuffer();
		// for (int i = 0; i < count; i++) {
		// buffer.append(itemIds.getElementAsString(i));
		// buffer.append(",");
		// buffer.append(topRanks.getElementAsInt(i));
		// buffer.append(",");
		// buffer.append(grossingRanks.getElementAsInt(i));
		// buffer.append(",");
		// buffer.append(prices.getElementAsDouble(i));
		// buffer.append(",");
		// buffer.append(usesIaps.getElementAsLogical(i));
		// buffer.append(",");
		// buffer.append(totalDownloads.getElementAsSEXP(i).asReal());
		// buffer.append("\r\n");
		// }
		// // ListVector itemIds = (ListVector) df.getElementAsObject(0);
		// LOGGER.info("my.labelled.df: " + buffer);

		// runs stage 3
		stream = ModelIOS.class.getResourceAsStream("r/stage3.R");
		reader = new InputStreamReader(stream);
		mEngine.eval(reader);

		if (LOG.isLoggable(GaeLevel.DEBUG)) {
			LOG.log(GaeLevel.DEBUG, "iap.ap: " + ((Vector) mEngine.get("iap.ap")).asReal());
			LOG.log(GaeLevel.DEBUG, "iap.ag: " + ((Vector) mEngine.get("iap.ag")).asReal());
			LOG.log(GaeLevel.DEBUG, "af: " + ((Vector) mEngine.get("af")).asReal());
			LOG.log(GaeLevel.DEBUG, "th: " + ((Vector) mEngine.get("th")).asReal());
			LOG.log(GaeLevel.DEBUG, "bf: " + ((Vector) mEngine.get("bf")).asReal());
		}

		// String code = UUID.randomUUID().toString();
	}

}
