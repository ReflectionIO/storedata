//  
//  ICountryService.java
//  storedata
//
//  Created by William Shakour on September 5, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//

package io.reflection.app.service.country;

import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.api.shared.datatypes.Pager;
import io.reflection.app.datatypes.shared.Country;
import io.reflection.app.datatypes.shared.Store;

import java.util.List;

import com.spacehopperstudios.service.IService;

public interface ICountryService extends IService {
	/**
	 * @param id
	 * @return
	 */
	public Country getCountry(Long id) throws DataAccessException;

	/**
	 * @param country
	 * @return
	 */
	public Country addCountry(Country country) throws DataAccessException;

	/**
	 * @param country
	 * @return
	 */
	public Country updateCountry(Country country) throws DataAccessException;

	/**
	 * @param country
	 */
	public void deleteCountry(Country country) throws DataAccessException;

	/**
	 * @param a2Code
	 * @return
	 */
	public Country getA2CodeCountry(String a2Code) throws DataAccessException;

	/**
	 * 
	 * @param a3Code
	 * @return
	 */
	public Country getA3CodeCountry(String a3Code) throws DataAccessException;

	/**
	 * 
	 * @param name
	 * @return
	 */
	public Country getNamedCountry(String name) throws DataAccessException;

	/**
	 * @param store
	 * @param pager
	 * @return
	 */
	public List<Country> getStoreCountries(Store store, Pager pager) throws DataAccessException;

	/**
	 * @param pager
	 * @return
	 */
	public List<Country> getCountries(Pager pager) throws DataAccessException;

	/**
	 * @param query
	 * @param pager
	 * @return
	 */
	public List<Country> searchCountries(String query, Pager pager) throws DataAccessException;

	/**
	 * @return
	 */
	public long getCountriesCount() throws DataAccessException;

}