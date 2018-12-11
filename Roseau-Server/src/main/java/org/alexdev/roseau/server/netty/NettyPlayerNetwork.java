package org.alexdev.roseau.server.netty;

import org.alexdev.roseau.messages.OutgoingMessageComposer;
import org.alexdev.roseau.server.IPlayerNetwork;
import org.jboss.netty.channel.Channel;

public class NettyPlayerNetwork extends IPlayerNetwork {

	private Channel channel;

	public NettyPlayerNetwork(Channel channel, int connectionId) {
		super(connectionId, Integer.valueOf(channel.getLocalAddress().toString().split(":")[1]));
		this.channel = channel;
	}

	@Override
	public void close() {
		channel.close();
	}

	@Override
	public void send(OutgoingMessageComposer response) {
		channel.write(response);
		
	}
}
