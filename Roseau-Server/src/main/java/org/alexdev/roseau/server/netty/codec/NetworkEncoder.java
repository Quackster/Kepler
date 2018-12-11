/*
 * Copyright (c) 2012 Quackster <alex.daniel.97@gmail>. 
 * 
 * This file is part of Sierra.
 * 
 * Sierra is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Sierra is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Sierra.  If not, see <http ://www.gnu.org/licenses/>.
 */

package org.alexdev.roseau.server.netty.codec;

import java.nio.charset.Charset;

import org.alexdev.roseau.log.Log;
import org.alexdev.roseau.messages.OutgoingMessageComposer;
import org.alexdev.roseau.util.Util;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

public class NetworkEncoder extends SimpleChannelHandler {

	@Override
	public void writeRequested(ChannelHandlerContext ctx, MessageEvent e) {

		Charset charset = Charset.forName("ISO-8859-1");

		try {

			if (e.getMessage() instanceof String) {
				Channels.write(ctx, e.getFuture(), ChannelBuffers.copiedBuffer((String) e.getMessage(), charset));
				return;
			}

			if (e.getMessage() instanceof OutgoingMessageComposer) {

				OutgoingMessageComposer msg = (OutgoingMessageComposer) e.getMessage();
				if (!msg.getResponse().isFinalised()) {
					msg.write();
				}

				if (Util.getConfiguration().get("Logging", "log.packets", Boolean.class)) {
					Log.println("SENT: " + msg.getResponse().getBodyString());
				}

				Channels.write(ctx, e.getFuture(), ChannelBuffers.copiedBuffer(msg.getResponse().get(), charset));

				return;
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
