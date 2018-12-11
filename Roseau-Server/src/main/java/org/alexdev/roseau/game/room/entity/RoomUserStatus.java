package org.alexdev.roseau.game.room.entity;

public class RoomUserStatus {

	private String key;
	private String value;
	private boolean infinite;
	private long duration;

	public RoomUserStatus(String key, String value, boolean infinite, long duration) {
		this.key = key;
		this.value = value;
		this.infinite = infinite;

		if (!this.infinite) {
			this.duration = duration;
		}
		else {
			this.duration = -1;
		}
	}
	
	public String getStatus() {
		return this.key;
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

	public boolean isInfinite() {
		return infinite;
	}

	public long getDuration() {
		return duration;
	}

	public void tick() {
		this.duration = this.duration - 1;

	}
}
