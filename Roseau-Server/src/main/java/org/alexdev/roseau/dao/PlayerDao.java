package org.alexdev.roseau.dao;

import java.util.List;

import org.alexdev.roseau.game.player.Permission;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.player.PlayerDetails;

public interface PlayerDao {

	public void createPlayer(String username, String password, String email, String mission, String figure, int credits, String sex, String birthday);
	public PlayerDetails getDetails(int userID);
	public boolean login(Player player, String userna, String password);
	public int getId(String username);
	public boolean isNameTaken(String name);
	void updatePlayer(PlayerDetails details);
	void updateLastLogin(PlayerDetails details);
	public List<Permission> getPermissions();
	public PlayerDetails getDetails(String username);
	

}
