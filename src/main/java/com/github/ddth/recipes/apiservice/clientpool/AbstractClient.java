package com.github.ddth.recipes.apiservice.clientpool;

/**
 * Abstract implementation of API client.
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since v1.0.0
 */
public abstract class AbstractClient implements AutoCloseable {
    private RetryPolicy retryPolicy;
    private HostAndPort[] hostAndPorts;
    private String serverHostsAndPorts = "127.0.0.1:8080";

    /**
     * Policy to retry API calls.
     *
     * @return
     */
    public RetryPolicy getRetryPolicy() {
        return retryPolicy;
    }

    /**
     * Policy to retry API calls.
     *
     * @param retryPolicy
     * @return
     */
    public AbstractClient setRetryPolicy(RetryPolicy retryPolicy) {
        this.retryPolicy = retryPolicy;
        return this;
    }

    /**
     * Get list of parsed server host and ports.
     *
     * @return
     */
    protected HostAndPort[] getServerHostAndPortList() {
        return hostAndPorts;
    }

    /**
     * List of servers, format {@code host1:port1,host2:port2,host2:port2,...}
     *
     * @return
     */
    public String getServerHostsAndPorts() {
        return serverHostsAndPorts;
    }

    /**
     * List of servers, format {@code host1:port1,host2:port2,host2:port2,...}
     *
     * @param serverHostsAndPorts
     * @return
     */
    public AbstractClient setServerHostsAndPorts(String serverHostsAndPorts) {
        this.serverHostsAndPorts = serverHostsAndPorts;
        this.hostAndPorts = parseServerHostsAndPorts(serverHostsAndPorts);
        return this;
    }

    /**
     * Parse server hosts and ports string to array of {@link HostAndPort}.
     *
     * @param serverHostsAndPorts
     * @return
     */
    protected HostAndPort[] parseServerHostsAndPorts(String serverHostsAndPorts) {
        String tokens[] = serverHostsAndPorts.split("[,;\\s]+");
        int numServers = tokens.length;
        HostAndPort[] hostAndPorts = new HostAndPort[numServers];
        for (int i = 0; i < numServers; i++) {
            hostAndPorts[i] = new HostAndPort(tokens[i]);
        }
        return hostAndPorts;
    }

    /**
     * Initializing method.
     *
     * @return
     * @throws Exception
     */
    public AbstractClient init() throws Exception {
        if (retryPolicy == null) {
            retryPolicy = RetryPolicy.DEFAULT_RETRY_POLICY;
        }
        if (hostAndPorts == null) {
            hostAndPorts = parseServerHostsAndPorts(serverHostsAndPorts);
        }
        return this;
    }

    /**
     * Destroy method.
     */
    public void destroy() {
        //EMPTY
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        destroy();
    }
}
