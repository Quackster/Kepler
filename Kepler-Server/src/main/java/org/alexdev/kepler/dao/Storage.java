package org.alexdev.kepler.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.alexdev.kepler.log.Log;
import org.alexdev.kepler.util.config.ServerConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class Storage {
    private HikariDataSource ds;
    private boolean isConnected;

    private static Storage storage;
    private static Logger log = LoggerFactory.getLogger(Storage.class);

    private Storage(String host, int port, String username, String password, String db) {
        try {
            HikariConfig config = new HikariConfig();
            config.setDriverClassName("org.mariadb.jdbc.Driver");
            config.setJdbcUrl("jdbc:mariadb://" + host + ":" + port + "/" + db);
            config.setUsername(username);
            config.setPassword(password);

            config.setPoolName("processing");

            // No martinmine/Leon/other Habbotards, you don't know better.
            // Read up on this first, before commenting dumb shit
            // https://github.com/brettwooldridge/HikariCP/wiki/About-Pool-Sizing
            // availableProcessors() already returns thread count on hyper-threaded processors
            // Thus we don't need the * 2 described there
            config.setMaximumPoolSize(Runtime.getRuntime().availableProcessors() + 1);
            config.setMinimumIdle(1);

            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            config.addDataSourceProperty("characterEncoding", "utf8");
            config.addDataSourceProperty("useUnicode", "true");
            config.addDataSourceProperty("useSSL", "false");
            config.addDataSourceProperty("serverTimezone", "UTC");
            config.addDataSourceProperty("sessionVariables", "sql_mode='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION'");

            this.ds = new HikariDataSource(config);
            this.isConnected = true;

        } catch (Exception ex) {
            Storage.logError(ex);
        }
    }

    /**
     * Tries to connect to its data access object service
     *
     * @return boolean - if connection was successful or not
     */
    public static boolean connect() {
        Storage.getLogger().info("Connecting to MySQL server");

        storage = new Storage(ServerConfiguration.getString("mysql.hostname"),
                ServerConfiguration.getInteger("mysql.port"),
                ServerConfiguration.getString("mysql.username"),
                ServerConfiguration.getString("mysql.password"),
                ServerConfiguration.getString("mysql.database"));

        if (!storage.isConnected()) {
            Storage.getLogger().error("Could not connect");
        } else {
            Storage.getLogger().info("Connection to MySQL was a success");
            return true;
        }

        return false;
    }

    /**
     * Logger handler for the MySQL processing.
     *
     * @param ex the exception to log
     */
    public static void logError(Exception ex) {
        Log.getErrorLogger().error("Error when executing MySQL query: ", ex);
    }

    /**
     * Prepare.
     *
     * @param query the query
     * @param conn  the conn
     * @return the prepared statement
     * @throws SQLException the SQL exception
     */
    public PreparedStatement prepare(String query, Connection conn) throws SQLException {
        try {
            return conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Execute.
     *
     * @param query the query
     */
    public void execute(String query) throws SQLException {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {

            sqlConnection = this.getConnection();
            preparedStatement = this.prepare(query, sqlConnection);
            preparedStatement.execute();

        } catch (Exception ex) {
            Storage.logError(ex);
            throw ex;
        } finally {
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }

    /**
     * Gets the string.
     *
     * @param query the query
     * @return the string
     */
    public String getString(String query) {
        String value = null;

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = this.getConnection();
            preparedStatement = this.prepare(query, sqlConnection);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();

            value = resultSet.getString(query.split(" ")[1]);

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return value;
    }

    /**
     * Gets the connection count.
     *
     * @return the connection count
     */
    public int getConnectionCount() {
        return this.ds.getHikariPoolMXBean().getActiveConnections();
    }

    /**
     * Gets the connection.
     *
     * @return the connection
     */
    public Connection getConnection() {

        try {
            return this.ds.getConnection();
        } catch (SQLException e) {
            Storage.logError(e);
        }

        return null;
    }

    /**
     * Checks if is connected.
     *
     * @return true, if is connected
     */
    public boolean isConnected() {
        return this.isConnected;
    }

    /**
     * Close silently.
     *
     * @param resultSet the result set
     */
    public static void closeSilently(ResultSet resultSet) {
        try {
            resultSet.close();
        } catch (Exception e) {
            logError(e);
        }
    }

    /**
     * Close silently.
     *
     * @param preparedStatement the prepared statement
     */
    public static void closeSilently(PreparedStatement preparedStatement) {
        try {
            preparedStatement.close();
        } catch (Exception e) {
            logError(e);
        }
    }

    /**
     * Close silently.
     *
     * @param sqlConnection the sql connection
     */
    public static void closeSilently(Connection sqlConnection) {
        try {
            sqlConnection.close();
        } catch (Exception e) {
            logError(e);
        }
    }

    /**
     * Returns the raw access to the data access object functions
     *
     * @return {@link Storage} class
     */
    public static Storage getStorage() {
        return storage;
    }

    public static Logger getLogger() {
        return log;
    }
}