package io.reflection.app;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.helpers.AppleReporterHelper;
import io.reflection.app.helpers.SplitDataHelper;
import io.reflection.app.helpers.SqlQueryHelper;
import io.reflection.app.logging.GaeLevel;
import io.reflection.app.service.sale.SaleServiceProvider;

@SuppressWarnings("serial")
public class SummariseServlet extends HttpServlet {

	private static final Logger LOG = Logger.getLogger(SummariseServlet.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		final String appEngineQueue = req.getHeader("X-AppEngine-QueueName");

		boolean isNotQueue = false;

		// bail out if we have not been called by app engine summarise queue
		if (isNotQueue = appEngineQueue == null || (!"deferred".toLowerCase().equals(appEngineQueue.toLowerCase())
				&& !"summarise".toLowerCase().equals(appEngineQueue.toLowerCase()))) {
			resp.setStatus(401);
			resp.getOutputStream().print("failure");
			LOG.warning("Attempt to run script directly, this is not permitted");
			return;
		}

		if (LOG.isLoggable(GaeLevel.DEBUG)) {
			if (!isNotQueue) {
				LOG.log(GaeLevel.DEBUG, String.format("Servlet is being called from [%s] queue", appEngineQueue));
			}
		}

		try {
			final String dataAccountIdStr = req.getParameter("dataaccountid");
			final String dateStr = req.getParameter("date");

			Long dataAccountId = Long.parseLong(dataAccountIdStr);
			if (dataAccountId == null || dataAccountId == 0L) {
				LOG.log(GaeLevel.DEBUG, String.format("Invalid data account id %s", dataAccountIdStr));
				return;
			}

			Date date = null;
			try {
				date = SqlQueryHelper.getSqlDateFormat().parse(dateStr);
			} catch (ParseException e1) {
				LOG.log(GaeLevel.DEBUG, String.format("Invalid date %s", dateStr), e1);
			}

			if (date == null) {
				LOG.log(GaeLevel.DEBUG, String.format("Invalid date %s", dateStr));
				return;
			}

			try {
				LOG.log(GaeLevel.DEBUG, String.format("Summarising dataAccountId %d on %s", dataAccountId, dateStr));

				boolean success = SaleServiceProvider.provide().summariseSalesForDataAccountOnDate(dataAccountId, date);

				LOG.log(GaeLevel.DEBUG, String.format("Summarisation complete %s.", success ? "successfully" : "without success"));

				if (success && AppleReporterHelper.isDateBeforeItunesReporterStarted(date)) {
					LOG.log(GaeLevel.DEBUG, "Queuing up this summary for splitting sales data");
					// only split if the sales date if before 26th November 2015. After that date, we are dealing with the reporter data so it is pre-split.
					SplitDataHelper.INSTANCE.enqueueToGatherSplitData(dataAccountId, date);
				}

			} catch (NumberFormatException | DataAccessException e) {
				LOG.log(Level.SEVERE, String.format("Unable to execute summarisation for dataAccountId: %s on %s", dataAccountId, date), e);
			}
		} finally {
			resp.setHeader("Cache-Control", "no-cache");
		}
	}
}
