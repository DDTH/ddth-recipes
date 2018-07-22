package com.github.ddth.recipes.apiservice.thrift;

import com.github.ddth.recipes.apiservice.clientpool.HostAndPort;
import com.github.ddth.recipes.apiservice.clientpool.RetryPolicy;

/**
 * Thrift API client.
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since v0.2.0
 */
public abstract class BaseThriftApiClient implements AutoCloseable {

    private RetryPolicy retryPolicy;
    private String serverHostsAndPorts = "127.0.0.1:8080";
    private HostAndPort[] hostAndPorts;
    private int timeoutMs = 0;

    private boolean compactProtocol = true;
    private boolean sslTransport = false;

    /**
     * Getter for {@link #retryPolicy}.
     *
     * @return
     */
    public RetryPolicy getRetryPolicy() {
        return retryPolicy;
    }

    /**
     * Setter for {@link #retryPolicy}.
     *
     * @param retryPolicy
     * @return
     */
    public BaseThriftApiClient setRetryPolicy(RetryPolicy retryPolicy) {
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
     * Getter for {@link #serverHostsAndPorts}.
     *
     * @return
     */
    public String getServerHostsAndPorts() {
        return serverHostsAndPorts;
    }

    /**
     * setter for {@link #serverHostsAndPorts}.
     *
     * @param serverHostsAndPorts
     * @return
     */
    public BaseThriftApiClient setServerHostsAndPorts(String serverHostsAndPorts) {
        this.serverHostsAndPorts = serverHostsAndPorts;
        return this;
    }

    /**
     * Getter for {@link #timeoutMs}.
     *
     * @return
     */
    public int getTimeoutMs() {
        return timeoutMs;
    }

    /**
     * Setter for {@link #timeoutMs}.
     *
     * @param timeoutMs
     * @return
     */
    public BaseThriftApiClient setTimeoutMs(int timeoutMs) {
        this.timeoutMs = timeoutMs;
        return this;
    }

    /**
     * Getter for {@link #compactProtocol}.
     *
     * @return
     */
    public boolean isCompactProtocol() {
        return compactProtocol;
    }

    /**
     * Setter for {@link #compactProtocol}.
     *
     * @param compactProtocol
     * @return
     */
    public BaseThriftApiClient setCompactProtocol(boolean compactProtocol) {
        this.compactProtocol = compactProtocol;
        return this;
    }

    /**
     * Getter for {@link #sslTransport}.
     *
     * @return
     */
    public boolean isSslTransport() {
        return sslTransport;
    }

    /**
     * Setter for {@link #sslTransport}.
     *
     * @param sslTransport
     * @return
     */
    public BaseThriftApiClient setSslTransport(boolean sslTransport) {
        this.sslTransport = sslTransport;
        return this;
    }

    /**
     * Disable SSL transport.
     *
     * @return
     */
    public BaseThriftApiClient disableSslTransport() {
        this.sslTransport = false;
        return this;
    }


    /*----------------------------------------------------------------------*/

    /**
     * Initializing method.
     *
     * @return
     * @throws Exception
     */
    public BaseThriftApiClient init() throws Exception {
        if (retryPolicy == null) {
            retryPolicy = RetryPolicy.DEFAULT_RETRY_POLICY;
        }

        //parse server hosts and ports
        String tokens[] = serverHostsAndPorts.split("[,;\\s]+");
        int numServers = tokens.length;
        hostAndPorts = new HostAndPort[numServers];
        for (int i = 0; i < numServers; i++) {
            hostAndPorts[i] = new HostAndPort(tokens[i]);
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
