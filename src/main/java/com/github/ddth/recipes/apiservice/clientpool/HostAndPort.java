package com.github.ddth.recipes.apiservice.clientpool;

/**
 * Capture a {@code host:port} pair.
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.2.0
 */
public class HostAndPort {
    public final String host;
    public final int port;

    public HostAndPort(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public HostAndPort(String hostAndPort) {
        String[] tokens = hostAndPort.split(":");
        this.host = tokens[0];
        this.port = tokens.length > 1 ? Integer.parseInt(tokens[1]) : 0;
    }
}
