package org.alexdev.kepler.server.netty;

import io.netty.channel.Channel;
import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.codec.BobbaDecoder;
import org.alexdev.kepler.server.netty.codec.BobbaEncoder;
import org.alexdev.kepler.server.netty.encryption.BobbaCryptoKeys;

public class NettyPlayerNetwork {
    private int port;
    private Channel channel;
    private int connectionId;

    public NettyPlayerNetwork(Channel channel, int connectionId) {
        this.channel = channel;
        this.connectionId = connectionId;
        this.port = Integer.parseInt(channel.localAddress().toString().split(":")[1]);
    }

    public Channel getChannel() {
        return this.channel;
    }

    public void registerEncryptionHandler(BobbaCryptoKeys keys) {
        this.channel.pipeline().addBefore("gameDecoder", "encryptionDecoder", new BobbaDecoder(keys.getC2sHeader(), keys.getC2sData()));
        this.channel.pipeline().addBefore("gameEncoder", "encryptionEncoder", new BobbaEncoder(keys.getS2cHeader(), keys.getS2cData()));
    }

    public int getPort() {
        return port;
    }

    public void send(Object response) {
        this.channel.writeAndFlush(response);
    }

    public void sendQueued(MessageComposer response) {
        this.channel.write(response);
    }

    public void flush() {
        this.channel.flush();
    }

    public void disconnect() {
        this.channel.close();
    }

    public int getConnectionId() {
        return connectionId;
    }

    public static String getIpAddress(Channel channel) {
        return channel.remoteAddress().toString().replace("/", "").split(":")[0];
    }
}
