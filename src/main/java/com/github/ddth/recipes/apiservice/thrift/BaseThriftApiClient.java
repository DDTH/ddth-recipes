package com.github.ddth.recipes.apiservice.thrift;

import com.github.ddth.recipes.apiservice.clientpool.AbstractClient;
import com.google.common.collect.Sets;
import org.apache.thrift.transport.TTransportException;

import java.io.File;
import java.util.Set;

/**
 * Base class for Thrift API client implementations.
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since v0.2.0
 */
public abstract class BaseThriftApiClient extends AbstractClient {
    protected final static Set<Integer> RESTARTABLE_CAUSES = Sets
            .newHashSet(TTransportException.UNKNOWN, TTransportException.NOT_OPEN, TTransportException.TIMED_OUT,
                    TTransportException.END_OF_FILE, TTransportException.CORRUPTED_DATA);

    private int timeoutMs = 0;

    /**
     * If {@code true}, use compact protocol.
     */
    private boolean compactProtocol = true;

    /**
     * If {@code true} use SSL transport.
     */
    private boolean sslTransport = false;

    private String trustStorePath;
    private String trustStorePassword;

    /**
     * Path to Java trust store.
     *
     * @return
     */
    protected String getTrustStorePath() {
        return trustStorePath;
    }

    /**
     * Path to Java trust store.
     *
     * @param trustStorePath
     * @return
     */
    public BaseThriftApiClient setTrustStorePath(String trustStorePath) {
        this.trustStorePath = trustStorePath;
        return this;
    }

    /**
     * Path to Java trust store.
     *
     * @param trustStoreFile
     * @return
     */
    public BaseThriftApiClient setTrustStorePath(File trustStoreFile) {
        this.trustStorePath = trustStoreFile.getAbsolutePath();
        return this;
    }

    /**
     * Password to open trust store.
     *
     * @return
     */
    protected String getTrustStorePassword() {
        return trustStorePassword;
    }

    /**
     * Password to open trust store.
     *
     * @param trustStorePassword
     * @return
     */
    public BaseThriftApiClient setTrustStorePassword(String trustStorePassword) {
        this.trustStorePassword = trustStorePassword;
        return this;
    }

    /**
     * Timeout in milliseconds.
     *
     * @return
     */
    public int getTimeout() {
        return timeoutMs;
    }

    /**
     * Timeout in milliseconds.
     *
     * @param timeoutMs
     * @return
     */
    public BaseThriftApiClient setTimeout(int timeoutMs) {
        this.timeoutMs = timeoutMs;
        return this;
    }

    /**
     * If {@code true}, use compact protocol.
     *
     * @return
     */
    public boolean isCompactProtocol() {
        return compactProtocol;
    }

    /**
     * If {@code true}, use compact protocol.
     *
     * @param compactProtocol
     * @return
     */
    public BaseThriftApiClient setCompactProtocol(boolean compactProtocol) {
        this.compactProtocol = compactProtocol;
        return this;
    }

    /**
     * If {@code true} use SSL transport.
     *
     * @return
     */
    public boolean isSslTransport() {
        return sslTransport;
    }

    /**
     * If {@code true} use SSL transport.
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
        return setSslTransport(false);
    }

    /**
     * Enable SSL transport.
     *
     * @param trustStorePath
     * @param trustStorePassword
     * @return
     */
    public BaseThriftApiClient enableSslTransport(String trustStorePath, String trustStorePassword) {
        setSslTransport(true);
        this.trustStorePath = trustStorePath;
        this.trustStorePassword = trustStorePassword;
        return this;
    }

    /**
     * Enable SSL transport.
     *
     * @param trustStoreFile
     * @param trustStorePassword
     * @return
     */
    public BaseThriftApiClient enableSslTransport(File trustStoreFile, String trustStorePassword) {
        return enableSslTransport(trustStoreFile.getAbsolutePath(), trustStorePassword);
    }
}
