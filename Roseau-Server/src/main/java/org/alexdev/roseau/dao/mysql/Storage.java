package org.alexdev.roseau.dao.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.TimeUnit;

import org.alexdev.roseau.log.Log;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;

public class Storage {

	private BoneCP connections = null;
	private BoneCPConfig config;
	private boolean isConnected = false;

	public Storage(String host, String username, String password, String db) {

		try {

			config = new BoneCPConfig();
			config.setJdbcUrl("jdbc:mysql://" + host + "/" + db);
			config.setUsername(username);
			config.setPassword(password);

			config.setMinConnectionsPerPartition(0);
			config.setMaxConnectionsPerPartition(5);
			config.setConnectionTimeout(1000, TimeUnit.SECONDS);
			config.setPartitionCount(Runtime.getRuntime().availableProcessors()); // set partion count to number of cores (inc. hyperthreading)

			this.connections = new BoneCP(config);

			this.isConnected = true;

		} catch (Exception e) {
			this.isConnected = false;
			Log.exception(e);
		}
	}

	public PreparedStatement prepare(String query, Connection conn) throws SQLException {

		try {
			conn = this.connections.getConnection();
			return conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			conn.close();
		}

		return null;
	}
	
    public void execute(String query) {
        
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {

            sqlConnection = this.getConnection();
            preparedStatement = this.prepare(query, sqlConnection);
            preparedStatement.execute();

        } catch (Exception e) {
            Log.exception(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }
    }
	
	public boolean exists(String query) {
		
		boolean exists = false;
		
		Connection sqlConnection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {

			sqlConnection = this.getConnection();
			preparedStatement = this.prepare(query, sqlConnection);
			resultSet = preparedStatement.executeQuery();
			exists = resultSet.next();

		} catch (Exception e) {
			Log.exception(e);
		} finally {
			Storage.closeSilently(resultSet);
			Storage.closeSilently(preparedStatement);
			Storage.closeSilently(sqlConnection);
		}

		return exists;
	}
	
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
			Log.exception(e);
		} finally {
			Storage.closeSilently(resultSet);
			Storage.closeSilently(preparedStatement);
			Storage.closeSilently(sqlConnection);
		}

		return value;
	}
	
	public int getInt(String query) {
		
		int value = -1;
		
		Connection sqlConnection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {

			sqlConnection = this.getConnection();
			preparedStatement = this.prepare(query, sqlConnection);
			
			resultSet = preparedStatement.executeQuery();
			resultSet.next();
			
			value = resultSet.getInt(query.split(" ")[1]);

		} catch (Exception e) {
			Log.exception(e);
		} finally {
			Storage.closeSilently(resultSet);
			Storage.closeSilently(preparedStatement);
			Storage.closeSilently(sqlConnection);
		}

		return value;
	}
	
	public void checkDriver() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public int getConnectionCount() {
		return this.connections.getTotalLeased();
	}

	public Connection getConnection() {

		try {
			return this.connections.getConnection();
		} catch (SQLException e) {
			Log.exception(e);
		}

		return null;
	}

	public boolean isConnected() {
		return this.isConnected;
	}

	public static void closeSilently(ResultSet resultSet) {
		try {
			resultSet.close();
		} catch (Exception e) { }
		
	}

	public static void closeSilently(PreparedStatement preparedStatement) {
		try {
			preparedStatement.close();
		} catch (Exception e) { }
		
	}

	public static void closeSilently(Connection sqlConnection) {
		try {
			sqlConnection.close();
		} catch (Exception e) { }
		
	}
}