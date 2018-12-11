package org.alexdev.roseau.game.player;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.entity.Entity;
import org.alexdev.roseau.messages.outgoing.PH_TICKETS;
import org.alexdev.roseau.messages.outgoing.WALLETBALANCE;
import org.alexdev.roseau.server.messages.Response;
import org.alexdev.roseau.server.messages.SerializableObject;

public class PlayerDetails implements SerializableObject {

	private int id = -1;
	private String username;
	private String mission;
	private String figure;
	private String email;
	private int rank;
	private int credits;
	private String sex;
	private String country;
	private String badge;
	private String birthday;
	private String poolFigure;
	private String password;
	private String personalGreeting;

	private boolean authenticated;
	private Entity entity;
	private long lastonline;
	private int tickets;

	public PlayerDetails(Entity session) {
		this.authenticated = false;
		this.entity = session;
	}

	public void fill(int id, String username, String mission, String figure, String sex) {
		this.id = id;
		this.username = username;
		this.mission = mission;
		this.figure = figure;
	}

	public void fill(int id, String username, String mission, String figure, String poolFigure, String email, int rank, int credits, String sex, String country, String badge, String birthday, long lastonline, String personalGreeting, int tickets) {
		this.id = id;
		this.username = username;
		this.mission = mission;
		this.figure = figure;
		this.email = email;
		this.rank = rank;
		this.credits = credits;
		this.sex = sex;
		this.country = country;
		this.badge = badge;
		this.birthday = birthday;
		this.poolFigure = poolFigure;
		this.lastonline = lastonline;
		this.personalGreeting = personalGreeting;
		this.tickets = tickets;
	}

	@Override
	public void serialise(Response response) {
		response.appendKVArgument("name", this.username);
		response.appendKVArgument("figure", this.figure); 
		response.appendKVArgument("email", this.email);
		response.appendKVArgument("birthday", this.birthday);
		response.appendKVArgument("phonenumber", "+44");
		response.appendKVArgument("customData", this.mission);
		response.appendKVArgument("has_read_agreement", "1");
		response.appendKVArgument("sex", this.sex);
		response.appendKVArgument("country", this.country);
		response.appendKVArgument("has_special_rights", "0");
		response.appendKVArgument("badge_type", this.badge);
	}
	
	public void sendCredits() {

		if (this.entity instanceof Player) {
			((Player)this.entity).send(new WALLETBALANCE(this.credits));
		}
	}

	public void sendTickets() {

		if (this.entity instanceof Player) {
			((Player)this.entity).send(new PH_TICKETS(this.tickets));
		}
	}

	public void save() {
		Roseau.getDao().getPlayer().updatePlayer(this);
	}

	public boolean hasFuse(String fuse) {
		return false;
	}

	public int getID() {
		return id;
	}

	public boolean isAuthenticated() {
		return authenticated;
	}

	public void setAuthenticated(boolean authenticated) {
		this.authenticated = authenticated;
	}

	public String getName() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getMission() {
		return mission;
	}

	public void setMission(String motto) {
		this.mission = motto;
	}

	public String getFigure() {
		return figure;
	}

	public void setFigure(String figure) {
		this.figure = figure;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getRank() {
		return rank;
	}

	public int getCredits() {
		return credits;
	}

	public String getSex() {
		return sex;
	}

	public String getCountry() {
		return country;
	}

	public String getBadge() {
		return badge;
	}

	public String getBirthday() {
		return birthday;
	}

	public String getPoolFigure() {
		return poolFigure;
	}

	public void setPoolFigure(String poolFigure) {
		this.poolFigure = poolFigure;
	}

	public Entity getEntity() {
		return entity;
	}

	public void setEntity(Entity entity) {
		this.entity = entity;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public long getLastOnline() {
		return this.lastonline;
	}

	public String getPersonalGreeting() {
		return personalGreeting;
	}

	public void setPersonalGreeting(String personalGreeting) {
		this.personalGreeting = personalGreeting;
	}

	public int getTickets() {
		return tickets;
	}
	
	public void setCredits(int newTotal) {

		if (this.credits + newTotal <= Integer.MAX_VALUE) {
			this.credits = newTotal;
		}
	}


	public void setTickets(int newTotal) {

		if (this.tickets + newTotal <= Integer.MAX_VALUE) {
			this.tickets = newTotal;
		}
	}
}
