//  
//  ApplicationService.java
//  reflection.io
//
//  Created by William Shakour on September 17, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.service.application;

import static com.spacehopperstudios.utility.StringUtils.addslashes;
import static com.spacehopperstudios.utility.StringUtils.stripslashes;
import io.reflection.app.api.lookup.helpers.LookupDetailTypeHelper;
import io.reflection.app.api.lookup.shared.datatypes.LookupDetailType;
import io.reflection.app.repackaged.scphopr.cloudsql.Connection;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseServiceProvider;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseType;
import io.reflection.app.repackaged.scphopr.service.database.IDatabaseService;
import io.reflection.app.service.ServiceType;
import io.reflection.app.shared.datatypes.Application;
import io.reflection.app.shared.datatypes.Store;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.spacehopperstudios.utility.StringUtils;

final class ApplicationService implements IApplicationService {

	private static final Logger LOG = Logger.getLogger(ApplicationService.class.getName());

	public String getName() {
		return ServiceType.ServiceTypeApplication.toString();
	}

	@Override
	public Application getApplication(Long id) {
		Application application = null;

		IDatabaseService databaseService = DatabaseServiceProvider.provide();
		Connection applicationConnection = databaseService.getNamedConnection(DatabaseType.DatabaseTypeApplication.toString());

		String getApplicationQuery = String
				.format("SELECT * FROM `application` JOIN `sup_application_iap` ON `application_id`=`sup_application_iap`.`internalid` WHERE `deleted`='n' and `id`='%d' LIMIT 1",
						id.longValue());
		try {
			applicationConnection.connect();
			applicationConnection.executeQuery(getApplicationQuery);

			if (applicationConnection.fetchNextRow()) {
				application = toApplication(applicationConnection, LookupDetailType.LookupDetailTypeDetailed);
			}
		} finally {
			if (applicationConnection != null) {
				applicationConnection.disconnect();
			}
		}
		return application;
	}

	/**
	 * To application
	 * 
	 * @param connection
	 * @return
	 */
	private Application toApplication(Connection connection, LookupDetailType detail) {
		Application application = new Application();

		application.id = connection.getCurrentRowLong("application_id");

		if (LookupDetailTypeHelper.isShort(detail)) {
			application.title = stripslashes(connection.getCurrentRowString("title"));
			application.artworkUrlSmall = stripslashes(connection.getCurrentRowString("artwork_url_small"));
			application.artistName = stripslashes(connection.getCurrentRowString("artist_name"));

			String usesIap = null;
			if ((usesIap = connection.getCurrentRowString("usesiap")) != null) {
				application.usesIap = (usesIap.equalsIgnoreCase("y") ? Boolean.TRUE : Boolean.FALSE);
			}
		}

		if (LookupDetailTypeHelper.isMedium(detail)) {
			application.artworkUrlLarge = stripslashes(connection.getCurrentRowString("artwork_url_large"));
			application.sellerName = stripslashes(connection.getCurrentRowString("seller_name"));
			application.companyUrl = stripslashes(connection.getCurrentRowString("company_url"));
			application.viewUrl = stripslashes(connection.getCurrentRowString("view_url"));
			application.itunesReleaseDate = connection.getCurrentRowDateTime("itunes_release_date");
			application.itunesVersion = stripslashes(connection.getCurrentRowString("itunes_version"));
		}

		if (LookupDetailTypeHelper.isDetailed(detail)) {
			application.version = stripslashes(connection.getCurrentRowString("version"));
			application.recommendedAge = stripslashes(connection.getCurrentRowString("recommended_age"));
			application.supportUrl = stripslashes(connection.getCurrentRowString("support_url"));
			application.copyright = stripslashes(connection.getCurrentRowString("copyright"));
			application.downloadSize = connection.getCurrentRowLong("download_size");
			application.created = connection.getCurrentRowDateTime("export_date");
			application.description = stripslashes(connection.getCurrentRowString("description"));
		}

		return application;
	}

	@Override
	public Application addApplication(Application application) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Application updateApplication(Application application) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteApplication(Application application) {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.application.IApplicationService#lookupInternalIdsApplication(java.util.List,
	 * io.reflection.app.api.lookup.datatypes.LookupDetailType)
	 */
	@Override
	public List<Application> lookupInternalIdsApplication(List<String> internalIds, LookupDetailType detail) {
		List<Application> applications = new ArrayList<Application>();

		if (internalIds != null && internalIds.size() > 0) {
			String items = "'" + StringUtils.join(internalIds, "','") + "'";

			String lookupInternalIdsApplicationQuery = String
					.format("SELECT %s FROM `epf_application` LEFT JOIN `sup_application_iap` ON `application_id`=`sup_application_iap`.`internalid` WHERE `application_id` IN (%s)",
							columnsForLookupDetailType(detail), items);

			Connection applicationConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeApplication.toString());

			try {
				applicationConnection.connect();
				applicationConnection.executeQuery(lookupInternalIdsApplicationQuery);

				while (applicationConnection.fetchNextRow()) {
					Application application = toApplication(applicationConnection, detail);

					if (application != null) {
						applications.add(application);
					}
				}
			} finally {
				if (applicationConnection != null) {
					applicationConnection.disconnect();
				}
			}
		}

		return applications;
	}

	/**
	 * @param detail
	 * @return
	 */
	private String columnsForLookupDetailType(LookupDetailType detail) {
		StringBuffer columns = new StringBuffer();

		columns.append("`application_id`");

		if (LookupDetailTypeHelper.isShort(detail)) {
			columns.append(",");
			columns.append("`title`,");
			columns.append("`artwork_url_small`,");
			columns.append("`artist_name`,");
			columns.append("`usesiap`");
		}

		if (LookupDetailTypeHelper.isMedium(detail)) {
			columns.append(",");
			columns.append("`artwork_url_large`,");
			columns.append("`seller_name`,");
			columns.append("`company_url`,");
			columns.append("`view_url`,");
			columns.append("`itunes_release_date`,");
			columns.append("`itunes_version`");
		}

		if (LookupDetailTypeHelper.isDetailed(detail)) {
			columns.append(",");
			columns.append("`version`,");
			columns.append("`recommended_age`,");
			columns.append("`support_url`,");
			columns.append("`copyright`,");
			columns.append("`download_size`,");
			columns.append("`export_date`,");
			columns.append("`description`");
		}

		return columns.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.application.IApplicationService#lookupExternalIdsApplication(java.util.List,
	 * io.reflection.app.api.lookup.datatypes.LookupDetailType)
	 */
	@Override
	public List<Application> lookupExternalIdsApplication(List<String> externalIds, LookupDetailType detail) {
		List<Application> applications = new ArrayList<Application>();

		if (externalIds != null && externalIds.size() > 0) {
			String items = "'" + StringUtils.join(externalIds, "','") + "'";

			// LATER: this might not be the most efficient thing in the world - consider resolving the externalids to internal ids then doing the query with a
			// single join
			String lookupInternalIdsApplicationQuery = String
					.format("SELECT %s FROM `epf_application` JOIN `item` ON `application_id`=`item`.`internalid` LEFT JOIN `sup_application_iap` ON `application_id`=`sup_application_iap`.`internalid` WHERE `item`.`externalid` IN (%s)",
							columnsForLookupDetailType(detail), items);

			Connection applicationConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeApplication.toString());

			try {
				applicationConnection.connect();
				applicationConnection.executeQuery(lookupInternalIdsApplicationQuery);

				while (applicationConnection.fetchNextRow()) {
					Application application = toApplication(applicationConnection, detail);

					if (application != null) {
						applications.add(application);
					}
				}
			} finally {
				if (applicationConnection != null) {
					applicationConnection.disconnect();
				}
			}
		}

		return applications;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.application.IApplicationService#setApplicationIap(io.reflection.app.datatypes.Application, boolean)
	 */
	@Override
	public void setApplicationIap(Application application, Boolean usesIap) {

		String setApplicationIapQuery = String.format("%s `sup_application_iap` SET `internalid`='%d', `usesiap`='%s', `lastupdated`=NOW()%s",
				application.usesIap == null ? "INSERT INTO" : "UPDATE", application.id.longValue(), usesIap.booleanValue() ? 'y' : 'n',
				application.usesIap == null ? "" : " WHERE `internalid`=" + application.id.longValue());

		Connection applicationConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeApplication.toString());

		try {
			applicationConnection.connect();
			applicationConnection.executeQuery(setApplicationIapQuery);

			if (applicationConnection.getAffectedRowCount() == 0) {
				if (LOG.isLoggable(Level.WARNING)) {
					LOG.warning("Application with id [" + application.id + "] does not seem to be affected by update of uses IAP to [" + usesIap + "]");
				}
			}
		} finally {
			if (applicationConnection != null) {
				applicationConnection.disconnect();
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.application.IApplicationService#getStoreIapNaApplicationIds(java.lang.String)
	 */
	@Override
	public List<String> getStoreIapNaApplicationIds(Store store) {
		List<String> ids = new ArrayList<String>();

		String getStoreIapNaApplicationIdsQuery = String
				.format("SELECT `item`.`internalid` as `id` FROM `item` LEFT OUTER JOIN `sup_application_iap` ON `item`.`internalid`=`sup_application_iap`.`internalid` WHERE `item`.`source`='%s' AND (`sup_application_iap`.`lastupdated` < DATE_SUB(NOW(), INTERVAL 30 DAY) OR `sup_application_iap`.`internalid` IS NULL)",
						addslashes(store.a3Code));

		Connection applicationConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeApplication.toString());

		try {
			applicationConnection.connect();
			applicationConnection.executeQuery(getStoreIapNaApplicationIdsQuery);

			while (applicationConnection.fetchNextRow()) {
				String id = applicationConnection.getCurrentRowString("id");

				if (id != null) {
					ids.add(id);
				}
			}
		} finally {
			if (applicationConnection != null) {
				applicationConnection.disconnect();
			}
		}

		return ids;
	}
}