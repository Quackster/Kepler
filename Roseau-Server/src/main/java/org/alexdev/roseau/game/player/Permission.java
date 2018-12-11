package org.alexdev.roseau.game.player;

public class Permission {

	private String permission;
	private boolean inheritable;
	private int rank;
	
	public Permission(String permission, boolean inheritable, int rank) {
		this.permission = permission;
		this.inheritable = inheritable;
		this.rank = rank;
	}

	public String getPermission() {
		return permission;
	}

	public boolean isInheritable() {
		return inheritable;
	}

	public int getRank() {
		return rank;
	}
	
	
}
