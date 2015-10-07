package io.reflection.app;

import java.io.IOException;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.reflection.app.api.exception.DataAccessException;
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
		if (isNotQueue = appEngineQueue == null || !"summarise".toLowerCase().equals(appEngineQueue.toLowerCase())) {
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

		final String dataAccountId = req.getParameter("dataaccountid");
		final String date = req.getParameter("date");

		try {
			SaleServiceProvider.provide().summariseSalesForDataAccountOnDate(Long.parseLong(dataAccountId), SqlQueryHelper.getSqlDateFormat().parse(date));
		} catch (NumberFormatException | DataAccessException | ParseException e) {
			LOG.log(Level.SEVERE, String.format("Unable to execute summarisation for dataAccountId: %s on %s", dataAccountId, date), e);
		}

		resp.setHeader("Cache-Control", "no-cache");
	}
}
