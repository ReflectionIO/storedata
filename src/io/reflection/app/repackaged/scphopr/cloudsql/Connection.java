//  
//  Connection.java
//  repackageables
//
//  Created by William Shakour on 4 Sep 2012.
//  Copyrights © 2012 SPACEHOPPER STUDIOS LTD. All rights reserved.
//
package io.reflection.app.repackaged.scphopr.cloudsql;

import io.reflection.app.logging.GaeLevel;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.logging.Logger;

import com.google.appengine.api.rdbms.AppEngineDriver;
import com.google.appengine.api.utils.SystemProperty;

public final class Connection {

	private static final String CONNECTION_NATIVE_KEY = "connection.native";

	private String server;
	private String database;
	private java.sql.Connection connection;
	private String username;
	private String password;
	private ResultSet queryResult;
	private Statement statement;
	private long affectedRowCount;
	private long insertedId;
	private boolean isTransactionMode;
	private boolean isNative = false;

	private static final Logger LOG = Logger.getLogger(Connection.class.getName());

	public Connection(String server, String database, String username, String password) {
		this(server, database, username, password, false);
	}

	public Connection(String server, String database, String username, String password, boolean transactionMode) {
		String nativePropertyValue = System.getProperty(CONNECTION_NATIVE_KEY);

		if (nativePropertyValue != null) {
			isNative = Boolean.parseBoolean(nativePropertyValue);
		}

		if (LOG.isLoggable(GaeLevel.DEBUG)) {
			LOG.log(GaeLevel.DEBUG, "create connection with server: " + server + ", database: " + database + ", username: " + username
					+ " and password: ********");
		}

		if (server == null) throw new NullPointerException("server name cannot be null");

		if (database == null) throw new NullPointerException("database name cannot be null");

		if (username == null) throw new NullPointerException("username cannot be null");

		if (password == null) throw new NullPointerException("password cannot be null");

		this.isTransactionMode = transactionMode;

		this.server = server;
		this.database = database;
		this.username = username;
		this.password = password;

		if (isNative) {
			String databaseDriver = getDatabaseDriverName();

			try {
				Class.forName(databaseDriver).newInstance();
			} catch (InstantiationException ex) {
				LOG.log(GaeLevel.SEVERE, "Error registering driver", ex);
			} catch (IllegalAccessException ex) {
				LOG.log(GaeLevel.SEVERE, "Error registering driver", ex);
			} catch (ClassNotFoundException ex) {
				LOG.log(GaeLevel.SEVERE, "Error registering driver", ex);
			}
		} else {
			try {
				DriverManager.registerDriver(new AppEngineDriver());
			} catch (SQLException ex) {
				LOG.log(GaeLevel.SEVERE, "Error registering driver", ex);
			}
		}
	}

	private String getDatabaseDriverName() {
		if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Production) {
			return "com.mysql.jdbc.GoogleDriver";
		} else {
			return "com.mysql.jdbc.Driver";
		}
	}

	public void connect() {
		String url = null;

		if (isNative) {
			if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Production) {
				url = "jdbc:google:mysql://" + server + "/" + database;
			} else {
				url = "jdbc:mysql://" + server + "/" + database;
			}
		} else {
			url = "jdbc:google:rdbms://" + server + "/" + database;
		}

		try {
			if (connection == null) {
				connection = DriverManager.getConnection(url, username, password);
				connection.setAutoCommit(!isTransactionMode);
			}
		} catch (SQLException ex) {
			LOG.log(GaeLevel.SEVERE, "Error conneting to databse", ex);
		}

		return;
	}

	public boolean isConnected() {
		try {
			return connection != null && !connection.isClosed();
		} catch (SQLException ex) {
			LOG.log(GaeLevel.SEVERE, "Error checking if connection is closed", ex);
		}

		return false;
	}

	public void executeQuery(String query) throws NullPointerException {
		if (LOG.isLoggable(GaeLevel.DEBUG)) {
			LOG.log(GaeLevel.DEBUG, "executing query: " + query);
		}

		if (query == null) throw new NullPointerException("query cannot be null");
		if (query.length() == 0) throw new IllegalArgumentException("query cannot be empty");

		affectedRowCount = -1;
		statement = null;
		insertedId = -1;

		connect();

		try {
			if (statement == null) {
				statement = connection.createStatement();
			}

			if (statement.execute(query, Statement.RETURN_GENERATED_KEYS)) {
				queryResult = statement.getResultSet();
			} else {
				queryResult = statement.getGeneratedKeys();
			}
		} catch (SQLException ex) {
			LOG.log(GaeLevel.SEVERE, "Error executing query", ex);
		}

		return;
	}

	public long getInsertedId() {
		long insertedId = 0;

		if (this.insertedId != -1) {
			insertedId = this.insertedId;
		} else {
			if (queryResult != null) {
				try {
					if (queryResult.next()) {
						insertedId = this.insertedId = queryResult.getInt(1);
					}
				} catch (SQLException ex) {
					LOG.log(GaeLevel.SEVERE, "Error getting inserted id", ex);
				}
			}
		}

		return insertedId;
	}

	public boolean fetchNextRow() {
		boolean fetched = false;
		if (queryResult != null) {
			try {
				if (fetched = queryResult.next()) {}
			} catch (SQLException ex) {
				LOG.log(GaeLevel.SEVERE, "Error fetching next row", ex);
			}
		}

		return fetched;
	}

	public Object getCurrentRowValue(String key) {
		Object value = null;

		if (queryResult != null) {
			try {
				value = queryResult.getObject(key);
			} catch (SQLException ex) {
				LOG.log(GaeLevel.SEVERE, "Error getting value for column", ex);
			}
		}

		return value;
	}

	public Integer getCurrentRowInteger(String key) {
		Integer value = null;

		if (queryResult != null) {
			try {
				value = queryResult.getInt(key);
			} catch (SQLException ex) {
				LOG.log(GaeLevel.SEVERE, "Error getting value for column", ex);
			}
		}

		return value;
	}

	public Date getCurrentRowDateTime(String key) {
		Date value = null;

		if (queryResult != null) {
			try {
				Timestamp t;
				if ((t = queryResult.getTimestamp(key)) != null) {
					value = new Date(t.getTime());
				}
			} catch (SQLException ex) {
				LOG.log(GaeLevel.SEVERE, "Error getting value for column", ex);
			}
		}

		return value;
	}

	public Long getCurrentRowLong(String key) {
		Long value = null;

		if (queryResult != null) {
			try {
				value = queryResult.getLong(key);
			} catch (SQLException ex) {
				LOG.log(GaeLevel.SEVERE, "Error getting value for column", ex);
			}
		}

		return value;
	}
	
	public Double getCurrentRowDouble(String key) {
		Double value = null;
		
		if (queryResult != null) {
			try {
				value = queryResult.getDouble(key);
			} catch (SQLException ex) {
				LOG.log(GaeLevel.SEVERE, "Error getting value for column", ex);
			}
		}
		
		return value;
	}

	public String getCurrentRowString(String key) {
		String value = null;

		if (queryResult != null) {
			try {
				value = queryResult.getString(key);
			} catch (SQLException ex) {
				LOG.log(GaeLevel.SEVERE, "Error getting value for column", ex);
			}
		}

		return value;
	}

	@Deprecated
	public int getRowCount() {
		int count = 0;

		if (queryResult != null) {
			try {
				queryResult.last();
				count = queryResult.getRow();
				queryResult.beforeFirst();
			} catch (SQLException ex) {
				LOG.log(GaeLevel.SEVERE, "Error getting row count", ex);
			}
		}

		return count;
	}

	public void disconnect() {
		if (connection != null) {
			try {
				if (!connection.isClosed()) {
					connection.close();
					connection = null;
				}
			} catch (SQLException ex) {
				LOG.log(GaeLevel.SEVERE, "Error while closing connection", ex);
			}
		}

		return;
	}

	public long getAffectedRowCount() {
		if (statement != null) {
			try {
				affectedRowCount = statement.getUpdateCount();
			} catch (SQLException ex) {
				LOG.log(GaeLevel.SEVERE, "Error getting affected row count", ex);
			}
		}

		return affectedRowCount;
	}

	public void commit() {
		if (isTransactionMode) {
			if (isConnected()) {
				try {
					connection.commit();
				} catch (SQLException ex) {
					LOG.log(GaeLevel.SEVERE, "Error committing transaction", ex);
				}
			}
		} else {
			LOG.info("Attemting to commit when not in transaction mode");
		}
	}

	public void setTransactionMode(boolean transactional) {
		if (connection == null) {
			if (isTransactionMode != transactional) {
				isTransactionMode = transactional;
			}
		}
	}

}