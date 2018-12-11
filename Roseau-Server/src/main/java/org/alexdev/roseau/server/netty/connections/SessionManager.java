package org.alexdev.roseau.server.netty.connections;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.server.netty.NettyPlayerNetwork;
import org.jboss.netty.channel.Channel;

public class SessionManager {
	
	private ConcurrentMap<Integer, Player> sessions;

	public SessionManager() {
		sessions = new ConcurrentHashMap<Integer, Player>();
	}
	
	public Player addSession(Channel channel) {
		
		Player player = new Player(new NettyPlayerNetwork(channel, channel.getId()));
		channel.setAttachment(player);
		
		Roseau.getGame().getPlayerManager().getPlayers().put(channel.getId(), player);
		sessions.putIfAbsent(channel.getId(), player);
		
		return player;
	}

	public void removeSession(Channel channel) { 
		try {
			Roseau.getGame().getPlayerManager().getPlayers().remove(channel.getId());
			sessions.remove(channel.getId());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean hasSession(Channel channel) {
		return sessions.containsKey(channel.getId());
	}

	public ConcurrentMap<Integer, Player> getSessions() {
		return sessions;
	}
}
