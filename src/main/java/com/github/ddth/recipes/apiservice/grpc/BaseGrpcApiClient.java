package com.github.ddth.recipes.apiservice.grpc;

import com.github.ddth.recipes.apiservice.clientpool.HostAndPort;
import com.github.ddth.recipes.apiservice.clientpool.RetryPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

/**
 * Thrift API client.
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since v0.2.0
 */
public abstract class BaseGrpcApiClient implements AutoCloseable {

    private final Logger LOGGER = LoggerFactory.getLogger(BaseGrpcApiClient.class);

    private RetryPolicy retryPolicy;
    private String serverHostsAndPorts = "127.0.0.1:8080";
    private HostAndPort[] hostAndPorts;

    private boolean sslTransport = false;

    /**
     * If {@code true} use OkHttp lib, default value is {@code false} to use Netty lib.
     */
    private boolean useOkHttp = false;

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
    public BaseGrpcApiClient setRetryPolicy(RetryPolicy retryPolicy) {
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
    public BaseGrpcApiClient setServerHostsAndPorts(String serverHostsAndPorts) {
        this.serverHostsAndPorts = serverHostsAndPorts;
        return this;
    }

    /**
     * Getter for {@link #useOkHttp}.
     *
     * @return
     */
    public boolean isUseOkHttp() {
        return useOkHttp;
    }

    /**
     * Setter for {@link #useOkHttp}.
     *
     * @param useOkHttp
     * @return
     */
    public BaseGrpcApiClient setUseOkHttp(boolean useOkHttp) {
        this.useOkHttp = useOkHttp;
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
    public BaseGrpcApiClient setSslTransport(boolean sslTransport) {
        this.sslTransport = sslTransport;
        return this;
    }

    /**
     * Disable SSL transport.
     *
     * @return
     */
    public BaseGrpcApiClient disableSslTransport() {
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
    public BaseGrpcApiClient init() throws Exception {
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

    private Random RANDOM = new Random(System.currentTimeMillis());

    protected int calcServerIndexHash(RetryPolicy retryPolicy) {
        int serverIndexHash = 0;
        int numServers = hostAndPorts.length;
        switch (retryPolicy.getRetryType()) {
        case FAILOVER:
            serverIndexHash = retryPolicy.getCounter();
            break;
        case ROUND_ROBIN:
            if (retryPolicy.getCounter() == 0) {
                serverIndexHash = RANDOM.nextInt(Short.MAX_VALUE) % numServers;
            } else {
                serverIndexHash = retryPolicy.getLastServerIndexHash() + 1;
            }
            retryPolicy.setLastServerIndexHash(serverIndexHash);
            break;
        case RANDOM_FAILOVER:
            if (retryPolicy.getCounter() == 0 || numServers < 2) {
                serverIndexHash = 0;
            } else {
                serverIndexHash = 1 + (RANDOM.nextInt(Short.MAX_VALUE) % (numServers - 1));
            }
            break;
        case RANDOM:
        default:
            serverIndexHash = RANDOM.nextInt(Short.MAX_VALUE);
            break;
        }
        return serverIndexHash;
    }

}
