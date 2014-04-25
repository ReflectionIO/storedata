//  
//  IDataSourceService.java
//  reflection.io
//
//  Created by William Shakour on December 26, 2013.
//  Copyrights © 2013 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2013 reflection.io. All rights reserved.
//
package io.reflection.app.service.datasource;

import io.reflection.app.api.exception.DataAccessException;
import io.reflection.app.datatypes.shared.DataSource;
import io.reflection.app.datatypes.shared.Store;

import java.util.Collection;
import java.util.List;

import com.spacehopperstudios.service.IService;

public interface IDataSourceService extends IService {
	/**
	 * @param id
	 * @return
	 */
	public DataSource getDataSource(Long id) throws DataAccessException;

	/**
	 * 
	 * @param dataSourceIds
	 * @return
	 * @throws DataAccessException
	 */
	public List<DataSource> getDataSourceBatch(Collection<Long> dataSourceIds) throws DataAccessException;

	/**
	 * @param dataSource
	 * @return
	 */
	public DataSource addDataSource(DataSource dataSource) throws DataAccessException;

	/**
	 * @param dataSource
	 * @return
	 */
	public DataSource updateDataSource(DataSource dataSource) throws DataAccessException;

	/**
	 * @param dataSource
	 */
	public void deleteDataSource(DataSource dataSource) throws DataAccessException;

	/**
	 * @param name
	 * @return
	 */
	public DataSource getNamedDataSource(String name) throws DataAccessException;

	/**
	 * 
	 * @param a3Code
	 * @return
	 * @throws DataAccessException
	 */
	public DataSource getA3CodeDataSource(String a3Code) throws DataAccessException;

	/**
	 * @param source
	 * @return
	 */
	public List<Store> getStores(DataSource source);

}