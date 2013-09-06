//  
//  ICountryService.java
//  storedata
//
//  Created by William Shakour on September 5, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//

package io.reflection.app.service.country;

import io.reflection.app.datatypes.Country;

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

}