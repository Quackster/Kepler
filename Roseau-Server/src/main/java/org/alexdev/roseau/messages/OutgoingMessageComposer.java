package org.alexdev.roseau.messages;

import org.alexdev.roseau.server.netty.readers.NettyResponse;

public abstract class OutgoingMessageComposer {
	
	protected NettyResponse response;
	
	public OutgoingMessageComposer() {
		this.response = new NettyResponse();
	}
	
	public abstract void write();

	public NettyResponse getResponse() {
		return response;
	}

	public void setResponse(NettyResponse response) {
		this.response = response;
	}
}
