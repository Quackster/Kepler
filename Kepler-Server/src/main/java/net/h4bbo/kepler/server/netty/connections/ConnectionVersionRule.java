package net.h4bbo.kepler.server.netty.connections;

public class ConnectionVersionRule {
    private final int port;
    private final int version;

    public ConnectionVersionRule(int port, int version) {
        this.port = port;
        this.version = version;
    }

    public int getPort() {
        return port;
    }

    public int getVersion() {
        return version;
    }
}
