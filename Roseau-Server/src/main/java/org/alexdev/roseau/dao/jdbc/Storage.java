package org.alexdev.roseau.dao.jdbc;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import org.oldskooler.simplelogger4j.SimpleLog;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Database connection pool and storage management using HikariCP.
 * Provides connection retrieval for use with try-with-resources.
 */
public class Storage {
    private static final SimpleLog logger = SimpleLog.of(Storage.class);
    
    private HikariDataSource dataSource;
    private boolean isConnected = false;

    /**
     * Initializes the database connection pool with HikariCP.
     *
     * @param host     Database hostname
     * @param username Database username
     * @param password Database password
     * @param db       Database name
     */
    public Storage(String host, String username, String password, String db) {
        try {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl("jdbc:mariadb://" + host + "/" + db);
            config.setUsername(username);
            config.setPassword(password);
            
            // Connection pool configuration
            config.setMinimumIdle(5);
            config.setMaximumPoolSize(20);
            config.setConnectionTimeout(30000); // 30 seconds
            config.setIdleTimeout(600000); // 10 minutes
            config.setMaxLifetime(1800000); // 30 minutes
            config.setLeakDetectionThreshold(60000); // 1 minute
            
            // MySQL-specific optimizations
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            config.addDataSourceProperty("useServerPrepStmts", "true");
            config.addDataSourceProperty("useLocalSessionState", "true");
            config.addDataSourceProperty("rewriteBatchedStatements", "true");
            config.addDataSourceProperty("cacheResultSetMetadata", "true");
            config.addDataSourceProperty("cacheServerConfiguration", "true");
            config.addDataSourceProperty("elideSetAutoCommits", "true");
            config.addDataSourceProperty("maintainTimeStats", "false");

            this.dataSource = new HikariDataSource(config);
            this.isConnected = true;
            
            logger.info("Database connection pool initialized successfully");

        } catch (Exception e) {
            this.isConnected = false;
            logger.error("Failed to initialize database connection pool", e);
        }
    }

    /**
     * Gets a connection from the pool. The caller is responsible for closing the connection
     * using try-with-resources pattern.
     *
     * @return A database connection from the pool
     * @throws SQLException if a database access error occurs
     */
    public Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("DataSource is not initialized");
        }
        return dataSource.getConnection();
    }

    /**
     * Gets the current number of active connections in the pool.
     *
     * @return Number of active connections
     */
    public int getConnectionCount() {
        if (dataSource == null) {
            return 0;
        }
        HikariPoolMXBean mxBean = dataSource.getHikariPoolMXBean();
        return mxBean != null ? mxBean.getActiveConnections() : 0;
    }

    /**
     * Checks if the database connection pool is connected and operational.
     *
     * @return true if connected, false otherwise
     */
    public boolean isConnected() {
        return isConnected && dataSource != null && !dataSource.isClosed();
    }

    /**
     * Closes the connection pool and releases all resources.
     */
    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            logger.info("Database connection pool closed");
        }
    }
}
