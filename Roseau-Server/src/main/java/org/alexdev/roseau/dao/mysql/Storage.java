package org.alexdev.roseau.dao.mysql;

import java.sql.Connection;
import java.sql.DriverManager;

import org.alexdev.roseau.log.Log;
import org.oldskooler.entity4j.DbContext;

public class Storage {

	private final String jdbcUrl;
	private final String username;
	private final String password;
	private final DatabaseEngine engine;
	private boolean connected;

	public Storage(DatabaseEngine engine, String host, int port, String username, String password, String database, String sqlitePath, String options) {
		this.engine = engine;
		this.jdbcUrl = engine.jdbcUrl(host, port, database, sqlitePath, options);
		this.username = username;
		this.password = password;
		this.loadDriver();
		this.connected = this.canConnect();
	}

	public DbContext context() {
		try {
			Connection connection = this.openConnection();
			return new DbContext(connection, this.engine.getDialectType());
		} catch (Exception e) {
			Log.exception(e);
			throw new IllegalStateException("Could not open Entity4j context", e);
		}
	}

	public boolean isConnected() {
		return this.connected;
	}

	private boolean canConnect() {
		try (Connection ignored = this.openConnection()) {
			return true;
		} catch (Exception e) {
			Log.exception(e);
			return false;
		}
	}

	private Connection openConnection() throws Exception {
		if (this.username == null || this.username.trim().isEmpty()) {
			return DriverManager.getConnection(this.jdbcUrl);
		}

		return DriverManager.getConnection(this.jdbcUrl, this.username, this.password);
	}

	private void loadDriver() {
		try {
			Class.forName(this.engine.getDriverClassName());
		} catch (Exception e) {
			Log.exception(e);
		}
	}
}
