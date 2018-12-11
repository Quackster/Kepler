package org.alexdev.roseau.server;

import java.util.List;

import org.alexdev.roseau.messages.MessageHandler;
import org.alexdev.roseau.server.netty.connections.SessionManager;

public abstract class IServerHandler {
	
	private List<Integer> ports;
	private String ip;
	private String extraData;
	
	private MessageHandler messages;
	private SessionManager sessionManager;
	
	public IServerHandler(List<Integer> ports) {
		this.messages = new MessageHandler();
		this.sessionManager = new SessionManager();
		this.ports = ports;
	}
	
	public abstract boolean listenSocket();
	
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public MessageHandler getMessageHandler() {
		return messages;
	}

	public String getExtraData() {
		return extraData;
	}

	public SessionManager getSessionManager() {
		return sessionManager;
	}

	public List<Integer> getPorts() {
		return ports;
	}
}
