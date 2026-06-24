package org.alexdev.roseau.dao.mysql;

import java.util.Locale;

import org.oldskooler.entity4j.dialect.SqlDialectType;

public enum DatabaseEngine {

	MYSQL("mysql", "com.mysql.cj.jdbc.Driver", SqlDialectType.MYSQL, 3306),
	POSTGRES("postgres", "org.postgresql.Driver", SqlDialectType.POSTGRESQL, 5432),
	MSSQL("mssql", "com.microsoft.sqlserver.jdbc.SQLServerDriver", SqlDialectType.SQLSERVER, 1433),
	SQLITE("sqlite", "org.sqlite.JDBC", SqlDialectType.SQLITE, 0);

	private final String configPrefix;
	private final String driverClassName;
	private final SqlDialectType dialectType;
	private final int defaultPort;

	DatabaseEngine(String configPrefix, String driverClassName, SqlDialectType dialectType, int defaultPort) {
		this.configPrefix = configPrefix;
		this.driverClassName = driverClassName;
		this.dialectType = dialectType;
		this.defaultPort = defaultPort;
	}

	public static DatabaseEngine from(String value) {
		String engine = value == null ? "" : value.trim().toLowerCase(Locale.ROOT);

		switch (engine) {
			case "mysql":
				return MYSQL;
			case "postgres":
			case "postgresql":
				return POSTGRES;
			case "mssql":
			case "sqlserver":
			case "sql_server":
				return MSSQL;
			case "sqlite":
			case "sqlite3":
				return SQLITE;
			default:
				throw new IllegalArgumentException("Unsupported database engine: " + value);
		}
	}

	public static boolean isSupported(String value) {
		try {
			from(value);
			return true;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

	public String getConfigPrefix() {
		return configPrefix;
	}

	public String getDriverClassName() {
		return driverClassName;
	}

	public SqlDialectType getDialectType() {
		return dialectType;
	}

	public int getDefaultPort() {
		return defaultPort;
	}

	public String jdbcUrl(String hostname, int port, String database, String sqlitePath, String options) {
		switch (this) {
			case MYSQL:
				return appendQuery("jdbc:mysql://" + hostname + ":" + port + "/" + database, options);
			case POSTGRES:
				return appendQuery("jdbc:postgresql://" + hostname + ":" + port + "/" + database, options);
			case MSSQL:
				return appendMssqlOptions("jdbc:sqlserver://" + hostname + ":" + port + ";databaseName=" + database, options);
			case SQLITE:
				return "jdbc:sqlite:" + sqlitePath;
			default:
				throw new IllegalStateException("Unhandled database engine: " + this);
		}
	}

	private String appendQuery(String jdbcUrl, String options) {
		if (options == null || options.trim().isEmpty()) {
			return jdbcUrl;
		}

		return jdbcUrl + "?" + options.trim();
	}

	private String appendMssqlOptions(String jdbcUrl, String options) {
		if (options == null || options.trim().isEmpty()) {
			return jdbcUrl;
		}

		return jdbcUrl + ";" + options.trim();
	}
}
