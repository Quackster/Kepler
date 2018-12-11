package org.alexdev.roseau.dao.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.dao.PlayerDao;
import org.alexdev.roseau.dao.util.IProcessStorage;
import org.alexdev.roseau.game.GameVariables;
import org.alexdev.roseau.game.player.Permission;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.player.PlayerDetails;
import org.alexdev.roseau.log.DateTime;
import org.alexdev.roseau.log.Log;
import org.mindrot.jbcrypt.BCrypt;

import com.google.common.collect.Lists;

public class MySQLPlayerDao extends IProcessStorage<PlayerDetails, ResultSet> implements PlayerDao {

	private MySQLDao dao;
	private String fields = "id, username, password, rank, mission, figure, pool_figure, email, credits, sex, country, badge, birthday, last_online, personal_greeting, tickets";

	public MySQLPlayerDao(MySQLDao dao) {
		this.dao = dao;
	}

	@Override
	public void createPlayer(String username, String password, String email, String mission, String figure, int credits, String sex, String birthday) {
		Connection sqlConnection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			sqlConnection = this.dao.getStorage().getConnection();
			preparedStatement = this.dao.getStorage().prepare("INSERT INTO users (username, password, email, mission, figure, credits, sex, birthday, join_date, last_online, personal_greeting) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", sqlConnection);
			preparedStatement.setString(1, username);
			preparedStatement.setString(2, BCrypt.hashpw(password, BCrypt.gensalt()));
			preparedStatement.setString(3, email);
			preparedStatement.setString(4, mission);
			preparedStatement.setString(5, figure);
			preparedStatement.setInt(6, GameVariables.USER_DEFAULT_CREDITS);
			preparedStatement.setString(7, sex);
			preparedStatement.setString(8, birthday);
			preparedStatement.setLong(9, DateTime.getTime());
			preparedStatement.setLong(10, DateTime.getTime());
			preparedStatement.setString(11, GameVariables.MESSENGER_GREETING);
			preparedStatement.execute();

		} catch (Exception e) {
			Log.exception(e);
		} finally {
			Storage.closeSilently(resultSet);
			Storage.closeSilently(preparedStatement);
			Storage.closeSilently(sqlConnection);
		}
	}

	@Override
	public PlayerDetails getDetails(int userID) {
		PlayerDetails details = null;

		Connection sqlConnection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			sqlConnection = this.dao.getStorage().getConnection();
			preparedStatement = this.dao.getStorage().prepare("SELECT " + fields + " FROM users WHERE id = ? LIMIT 1", sqlConnection);
			preparedStatement.setInt(1, userID);
			resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				details = this.fill(details, resultSet);
			}

		} catch (Exception e) {
			Log.exception(e);
		} finally {
			Storage.closeSilently(resultSet);
			Storage.closeSilently(preparedStatement);
			Storage.closeSilently(sqlConnection);
		}

		return details;
	}
	
	@Override
	public PlayerDetails getDetails(String username) {
		Player player = Roseau.getGame().getPlayerManager().getByName(username);
		PlayerDetails details = null;

		if (player != null) {
			details = player.getDetails();
		} else {

			Connection sqlConnection = null;
			PreparedStatement preparedStatement = null;
			ResultSet resultSet = null;

			try {
				sqlConnection = this.dao.getStorage().getConnection();
				preparedStatement = this.dao.getStorage().prepare("SELECT " + fields  + " FROM users WHERE username = ? LIMIT 1", sqlConnection);
				preparedStatement.setString(1, username);

				resultSet = preparedStatement.executeQuery();

				if (resultSet.next()) {
					details = this.fill(new PlayerDetails(player), resultSet);
				}

			} catch (Exception e) {
				Log.exception(e);
			} finally {
				Storage.closeSilently(resultSet);
				Storage.closeSilently(preparedStatement);
				Storage.closeSilently(sqlConnection);
			}
		}

		return details;
	}

	@Override
	public boolean login(Player player, String username, String password) {
		Connection sqlConnection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		boolean found = false;

		try {

			sqlConnection = this.dao.getStorage().getConnection();
			preparedStatement = this.dao.getStorage().prepare("SELECT " + fields + " FROM users WHERE username = ? LIMIT 1", sqlConnection);
			preparedStatement.setString(1, username);

			resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				if (BCrypt.checkpw(password, resultSet.getString("password"))) {
					this.fill(player.getDetails(), resultSet);
					found = true;
				}
			}

		} catch (Exception e) {
			Log.exception(e);
		} finally {
			Storage.closeSilently(resultSet);
			Storage.closeSilently(preparedStatement);
			Storage.closeSilently(sqlConnection);
		}

		return found;
	}

	@Override
	public int getId(String username) {
		Connection sqlConnection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		int id = -1;

		try {

			sqlConnection = this.dao.getStorage().getConnection();
			preparedStatement = this.dao.getStorage().prepare("SELECT id FROM users WHERE username = ? LIMIT 1", sqlConnection);
			preparedStatement.setString(1, username);

			resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				id = resultSet.getInt("id");
			}
		} catch (Exception e) {
			Log.exception(e);
		} finally {
			Storage.closeSilently(resultSet);
			Storage.closeSilently(preparedStatement);
			Storage.closeSilently(sqlConnection);
		}

		return id;	
	}

	@Override
	public boolean isNameTaken(String name) {
		boolean nameTaken = false;

		Connection sqlConnection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {

			sqlConnection = this.dao.getStorage().getConnection();
			preparedStatement = this.dao.getStorage().prepare("SELECT id FROM users WHERE username = ? LIMIT 1", sqlConnection);
			preparedStatement.setString(1, name);

			resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				nameTaken = true;
			}

		} catch (Exception e) {
			Log.exception(e);
		} finally {
			Storage.closeSilently(resultSet);
			Storage.closeSilently(preparedStatement);
			Storage.closeSilently(sqlConnection);
		}

		return nameTaken;
	}

	@Override
	public void updatePlayer(PlayerDetails details) {
		Connection sqlConnection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			sqlConnection = this.dao.getStorage().getConnection();
			preparedStatement = dao.getStorage().prepare("UPDATE users SET password = ?, figure = ?, credits = ?, mission = ?, pool_figure = ?, sex = ?, email = ?, personal_greeting = ?, tickets = ? WHERE id = ?", sqlConnection);
			preparedStatement.setString(1, BCrypt.hashpw(details.getPassword(), BCrypt.gensalt()));
			preparedStatement.setString(2, details.getFigure());
			preparedStatement.setInt(3, details.getCredits());
			preparedStatement.setString(4, details.getMission());
			preparedStatement.setString(5, details.getPoolFigure());
			preparedStatement.setString(6, details.getSex());
			preparedStatement.setString(7, details.getEmail());
			preparedStatement.setString(8, details.getPersonalGreeting());
			preparedStatement.setInt(9, details.getTickets());
			preparedStatement.setInt(10, details.getID());

			preparedStatement.executeUpdate();

		} catch (SQLException e) {
			Log.exception(e);
		} finally {
			Storage.closeSilently(resultSet);
			Storage.closeSilently(preparedStatement);
			Storage.closeSilently(sqlConnection);
		}

	}

	@Override
	public void updateLastLogin(PlayerDetails details) {
		Connection sqlConnection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			sqlConnection = this.dao.getStorage().getConnection();
			preparedStatement = dao.getStorage().prepare("UPDATE users SET last_online = ? WHERE id = ?", sqlConnection);
			preparedStatement.setLong(1, DateTime.getTime());
			preparedStatement.setInt(2, details.getID());
			preparedStatement.executeUpdate();

		} catch (SQLException e) {
			Log.exception(e);
		} finally {
			Storage.closeSilently(resultSet);
			Storage.closeSilently(preparedStatement);
			Storage.closeSilently(sqlConnection);
		}
	}

	@Override
	public List<Permission> getPermissions() {
		List<Permission> permissions = Lists.newArrayList();

		Connection sqlConnection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {

			sqlConnection = this.dao.getStorage().getConnection();
			preparedStatement = this.dao.getStorage().prepare("SELECT * FROM users_permissions", sqlConnection);
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				
				int rank = resultSet.getInt("rank");
				String permission = resultSet.getString("permission");
				boolean inheritable = resultSet.getByte("inheritable") == 1;
				
				permissions.add(new Permission(permission, inheritable, rank));
			}

		} catch (Exception e) {
			Log.exception(e);
		} finally {
			Storage.closeSilently(resultSet);
			Storage.closeSilently(preparedStatement);
			Storage.closeSilently(sqlConnection);
		}

		return permissions;
	}

	@Override
	public PlayerDetails fill(PlayerDetails details, ResultSet row) throws SQLException {
		details.fill(row.getInt("id"), row.getString("username"), row.getString("mission"), row.getString("figure"), row.getString("pool_figure"), 
				row.getString("email"), row.getInt("rank"), row.getInt("credits"), row.getString("sex"), row.getString("country"), 
				row.getString("badge"), row.getString("birthday"), row.getLong("last_online"), row.getString("personal_greeting"), row.getInt("tickets"));
		return details;
	}
}
