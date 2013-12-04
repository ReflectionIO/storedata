//  
//  CountryService.java
//  storedata
//
//  Created by William Shakour on September 5, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.service.country;

import static com.spacehopperstudios.utility.StringUtils.addslashes;
import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.api.shared.datatypes.SortDirectionType;
import io.reflection.app.repackaged.scphopr.cloudsql.Connection;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseServiceProvider;
import io.reflection.app.repackaged.scphopr.service.database.DatabaseType;
import io.reflection.app.repackaged.scphopr.service.database.IDatabaseService;
import io.reflection.app.service.ServiceType;
import io.reflection.app.shared.datatypes.Country;
import io.reflection.app.shared.datatypes.Store;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.spacehopperstudios.utility.StringUtils;

final class CountryService implements ICountryService {
	public String getName() {
		return ServiceType.ServiceTypeCountry.toString();
	}

	@Override
	public Country getCountry(Long id) throws DataAccessException {
		Country country = null;

		IDatabaseService databaseService = DatabaseServiceProvider.provide();
		Connection countryConnection = databaseService.getNamedConnection(DatabaseType.DatabaseTypeCountry.toString());

		String getCountryQuery = String.format("SELECT * FROM `country` WHERE `deleted`='n' AND `id`=%d LIMIT 1", id.longValue());
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
	 * @throws DataAccessException 
	 */
	private Country toCountry(Connection connection) throws DataAccessException {
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
		// LATER Auto-generated method stub addCountry
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.country.ICountryService#getA2CodeCountry(java.lang.String)
	 */
	@Override
	public Country getA2CodeCountry(String a2Code) throws DataAccessException {
		Country country = null;

		final String getA2CodeCountryQuery = String.format("SELECT * FROM `country` WHERE `a2code`='%s' AND `deleted`='n' LIMIT 1", addslashes(a2Code));

		Connection countryConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeCountry.toString());

		try {
			countryConnection.connect();
			countryConnection.executeQuery(getA2CodeCountryQuery);

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.country.ICountryService#getA3CodeCountry(java.lang.String)
	 */
	@Override
	public Country getA3CodeCountry(String a3Code) throws DataAccessException {
		Country country = null;

		final String getA3CodeCountryQuery = String.format("SELECT * FROM `country` WHERE `a3code`='%s' AND `deleted`='n' LIMIT 1", addslashes(a3Code));

		Connection countryConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeCountry.toString());

		try {
			countryConnection.connect();
			countryConnection.executeQuery(getA3CodeCountryQuery);

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.country.ICountryService#getNamedCountry(java.lang.String)
	 */
	@Override
	public Country getNamedCountry(String name) throws DataAccessException {
		Country country = null;

		final String getNamedCountryQuery = String.format("SELECT * FROM `country` WHERE `name`='%s' AND `deleted`='n' LIMIT 1", addslashes(name));

		Connection countryConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeCountry.toString());

		try {
			countryConnection.connect();
			countryConnection.executeQuery(getNamedCountryQuery);

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.country.ICountryService#getStoreCountries(io.reflection.app.datatypes.Store, io.reflection.app.api.datatypes.Pager)
	 */
	@Override
	public List<Country> getStoreCountries(Store store, Pager pager) throws DataAccessException {
		List<Country> countries = new ArrayList<Country>();

		String commaDelimitedCountryCodes = null;

		if (store.countries != null && store.countries.size() > 0) {
			commaDelimitedCountryCodes = StringUtils.join(store.countries, "','");
		}

		if (commaDelimitedCountryCodes != null && commaDelimitedCountryCodes.length() != 0) {
			String getStoreCountriesQuery = String.format(
					"SELECT * FROM `country` WHERE `a2code` IN ('%s') AND `deleted`='n' ORDER BY `relevance` ASC,`%s` %s,`a2code` ASC LIMIT %d, %d",
					commaDelimitedCountryCodes, pager.sortBy, pager.sortDirection == SortDirectionType.SortDirectionTypeAscending ? "ASC" : "DESC",
					pager.start.longValue(), pager.count.longValue());

			Connection countryConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeCountry.toString());

			try {
				countryConnection.connect();
				countryConnection.executeQuery(getStoreCountriesQuery);

				while (countryConnection.fetchNextRow()) {
					Country country = toCountry(countryConnection);

					if (country != null) {
						countries.add(country);
					}

				}
			} finally {
				if (countryConnection != null) {
					countryConnection.disconnect();
				}
			}
		}
		return countries;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.country.ICountryService#getCountries(io.reflection.app.api.datatypes.Pager)
	 */
	@Override
	public List<Country> getCountries(Pager pager) throws DataAccessException {
		List<Country> countries = new ArrayList<Country>();

		final String getCountriesQuery = String.format("SELECT * FROM `country` WHERE `deleted`='n' ORDER BY `relevance` ASC,`%s` %s,`a2code` ASC LIMIT %d, %d", pager.sortBy,
				pager.sortDirection == SortDirectionType.SortDirectionTypeAscending ? "ASC" : "DESC", pager.start.longValue(), pager.count.longValue());
		Connection countryConnection = DatabaseServiceProvider.provide().getNamedConnection(DatabaseType.DatabaseTypeCountry.toString());

		try {
			countryConnection.connect();
			countryConnection.executeQuery(getCountriesQuery);

			while (countryConnection.fetchNextRow()) {
				Country country = toCountry(countryConnection);

				if (country != null) {
					countries.add(country);
				}
			}
		} finally {
			if (countryConnection != null) {
				countryConnection.disconnect();
			}
		}

		return countries;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.country.ICountryService#searchCountries(java.lang.String, io.reflection.app.api.datatypes.Pager)
	 */
	@Override
	public List<Country> searchCountries(String query, Pager pager) {
		// TODO Auto-generated method stub searchCountries
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.reflection.app.service.country.ICountryService#getCountriesCount()
	 */
	@Override
	public long getCountriesCount() {
		long count = 0;

		// TODO Auto-generated method stub getCountriesCount

		return count;
	}

}