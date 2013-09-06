//  
//  CountryService.java
//  storedata
//
//  Created by William Shakour on September 5, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.service.country;

import java.util.Arrays;

import io.reflection.app.datatypes.Country;
import io.reflection.app.repackaged.scphopr.cloudsql.Connection;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseServiceProvider;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseType;
import io.reflection.app.repackaged.scphopr.service.database.IDatabaseService;
import io.reflection.app.service.ServiceType;

final class CountryService implements ICountryService {
	public String getName() {
		return ServiceType.ServiceTypeCountry.toString();
	}

	@Override
	public Country getCountry(Long id) {
		Country country = null;

		IDatabaseService databaseService = DatabaseServiceProvider.provide();
		Connection countryConnection = databaseService.getNamedConnection(DatabaseType.DatabaseTypeCountry.toString());

		String getCountryQuery = String.format("select * from `country` where `deleted`='n' and `id`='%d' limit 1", id.longValue());
		try {
			countryConnection.connect();
			countryConnection.executeQuery(getCountryQuery);

			if (countryConnection.fetchNextRow()) {
				country = toCountry(countryConnection);
			}
		} finally {
			if (countryConnection != null) {
				countryConnection.disconnect();
			}
		}
		return country;
	}

	/**
	 * To country
	 * 
	 * @param connection
	 * @return
	 */
	private Country toCountry(Connection connection) {
		Country country = new Country();

		country.id = connection.getCurrentRowLong("id");
		country.created = connection.getCurrentRowDateTime("created");
		country.deleted = connection.getCurrentRowString("deleted");

		country.a2Code = connection.getCurrentRowString("a2code");
		country.a3Code = connection.getCurrentRowString("a3Code");
		country.continent = connection.getCurrentRowString("continent");
		country.name = connection.getCurrentRowString("name");
		country.nCode = connection.getCurrentRowInteger("ncode");
		country.stores = Arrays.asList(connection.getCurrentRowString("stores").split(","));

		return country;
	}

	@Override
	public Country addCountry(Country country) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Country updateCountry(Country country) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteCountry(Country country) {
		throw new UnsupportedOperationException();
	}

}