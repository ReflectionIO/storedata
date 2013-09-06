//  
//  ICountryService.java
//  storedata
//
//  Created by William Shakour on September 5, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//

package io.reflection.app.service.country;

import java.util.List;

import io.reflection.app.api.datatypes.Pager;
import io.reflection.app.datatypes.Country;
import io.reflection.app.datatypes.Store;

import com.spacehopperstudios.service.IService;

public interface ICountryService extends IService {
	/**
	 * @param id
	 * @return
	 */
	public Country getCountry(Long id);

	/**
	 * @param country
	 * @return
	 */
	public Country addCountry(Country country);

	/**
	 * @param country
	 * @return
	 */
	public Country updateCountry(Country country);

	/**
	 * @param country
	 */
	public void deleteCountry(Country country);

	/**
	 * @param a2Code
	 * @return
	 */
	public Country getA2CodeCountry(String a2Code);

	/**
	 * 
	 * @param a3Code
	 * @return
	 */
	public Country getA3CodeCountry(String a3Code);

	/**
	 * 
	 * @param name
	 * @return
	 */
	public Country getNamedCountry(String name);

	/**
	 * @param store
	 * @param pager
	 * @return
	 */
	public List<Country> getStoreCountries(Store store, Pager pager);

	/**
	 * @param pager
	 * @return
	 */
	public List<Country> getCountries(Pager pager);

	/**
	 * @param query
	 * @param pager
	 * @return
	 */
	public List<Country> searchCountries(String query, Pager pager);

}