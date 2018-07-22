package com.github.ddth.recipes.apiservice.thrift;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.ddth.commons.utils.SerializationUtils;
import com.github.ddth.recipes.apiservice.clientpool.ApiClientPool;
import com.github.ddth.recipes.apiservice.clientpool.HostAndPort;
import com.github.ddth.recipes.apiservice.clientpool.IClientFactory;
import com.github.ddth.recipes.apiservice.thrift.def.*;
import com.google.common.collect.Sets;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Set;

/**
 * Thrift API client.
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since v0.2.0
 */
public class ThriftApiClient extends BaseThriftApiClient implements IThriftApiClient {

    private final Logger LOGGER = LoggerFactory.getLogger(ThriftApiClient.class);

    private String trustStorePath;
    private String trustStorePassword;

    private ApiClientPool<TApiService.Client, TApiService.Iface> clientPool;

    /**
     * Enable SSL transport.
     *
     * @param trustStorePath
     * @param trustStorePassword
     * @return
     */
    public ThriftApiClient enableSslTransport(String trustStorePath, String trustStorePassword) {
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
    public ThriftApiClient enableSslTransport(File trustStoreFile, String trustStorePassword) {
        return enableSslTransport(trustStoreFile.getAbsolutePath(), trustStorePassword);
    }

    /**
     * Getter for {@link #trustStorePath}.
     *
     * @return
     */
    public String getTrustStorePath() {
        return trustStorePath;
    }

    /**
     * Setter for {@link #trustStorePath}.
     *
     * @param trustStorePath
     * @return
     */
    public ThriftApiClient setTrustStorePath(String trustStorePath) {
        this.trustStorePath = trustStorePath;
        return this;
    }

    /**
     * Setter for {@link #trustStorePath}.
     *
     * @param trustStoreFile
     * @return
     */
    public ThriftApiClient setTrustStorePath(File trustStoreFile) {
        this.trustStorePath = trustStoreFile.getAbsolutePath();
        return this;
    }

    /**
     * Getter for {@link #trustStorePassword}.
     *
     * @return
     */
    public String getTrustStorePassword() {
        return trustStorePassword;
    }

    /**
     * Setter for {@link #trustStorePassword}.
     *
     * @param trustStorePassword
     * @return
     */
    public ThriftApiClient setTrustStorePassword(String trustStorePassword) {
        this.trustStorePassword = trustStorePassword;
        return this;
    }

    /*----------------------------------------------------------------------*/
    private final class ClientFactory implements IClientFactory<TApiService.Client> {
        /**
         * {@inheritDoc}
         */
        @Override
        public TApiService.Client create(int serverIndexHash) throws Exception {
            HostAndPort[] serverHostAndPortList = getServerHostAndPortList();
            HostAndPort hostAndPort = serverHostAndPortList[serverIndexHash
                    % serverHostAndPortList.length];
            TTransport transport;
            if (isSslTransport()) {
                TSSLTransportFactory.TSSLTransportParameters params = new TSSLTransportFactory.TSSLTransportParameters();
                params.setTrustStore(trustStorePath, trustStorePassword);
                transport = TSSLTransportFactory
                        .getClientSocket(hostAndPort.host, hostAndPort.port, getTimeoutMs(),
                                params);
            } else {
                transport = new TFramedTransport(
                        new TSocket(hostAndPort.host, hostAndPort.port, getTimeoutMs()));
                transport.open();
            }
            TProtocol protocol = isCompactProtocol()
                    ? new TCompactProtocol(transport)
                    : new TBinaryProtocol(transport);
            return new TApiService.Client(protocol);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void destroy(TApiService.Client client) {
            TTransport inputTransport = client.getInputProtocol().getTransport();
            if (inputTransport != null) {
                try {
                    inputTransport.close();
                } catch (Exception e) {
                    LOGGER.warn(e.getMessage(), e);
                }
            }

            TTransport outputTransport = client.getInputProtocol().getTransport();
            if (outputTransport != null && outputTransport != inputTransport) {
                try {
                    outputTransport.close();
                } catch (Exception e) {
                    LOGGER.warn(e.getMessage(), e);
                }
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int getNumServers() {
            return getServerHostAndPortList().length;
        }

        private final Set<Integer> RESTARTABLE_CAUSES = Sets
                .newHashSet(TTransportException.UNKNOWN, TTransportException.NOT_OPEN,
                        TTransportException.TIMED_OUT, TTransportException.END_OF_FILE,
                        TTransportException.CORRUPTED_DATA);

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isRetryable(Throwable t) {
            if (t instanceof TTransportException) {
                TTransportException cause = (TTransportException) t;
                return RESTARTABLE_CAUSES.contains(cause.getType());
            }
            return false;
        }
    }

    /**
     * Initializing method.
     *
     * @return
     */
    public ThriftApiClient init() throws Exception {
        super.init();

        if (clientPool == null) {
            clientPool = new ApiClientPool<>(TApiService.Client.class, TApiService.Iface.class,
                    new ClientFactory(), getRetryPolicy()).init();
        }
        return this;
    }

    /**
     * Destroy method.
     */
    public void destroy() {
        if (clientPool != null) {
            try {
                clientPool.destroy();
            } catch (Exception e) {
                LOGGER.warn(e.getMessage(), e);
            } finally {
                clientPool = null;
            }
        }

        super.destroy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        destroy();
    }

    /*----------------------------------------------------------------------*/

    /**
     * {@inheritDoc}
     */
    @Override
    public void ping() throws TException {
        TApiService.Iface clientObj = null;
        try {
            clientObj = clientPool.borrowObject();
            clientObj.ping();
        } catch (Exception e) {
            throw e instanceof TException ? (TException) e : new TException(e);
        } finally {
            clientPool.returnObject(clientObj);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TApiResult check(String appId, String accessToken) throws TException {
        return check(new TApiAuth().setAppId(appId).setAccessToken(accessToken));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TApiResult check(TApiAuth apiAuth) throws TException {
        TApiService.Iface clientObj = null;
        try {
            clientObj = clientPool.borrowObject();
            return clientObj.check(apiAuth);
        } catch (Exception e) {
            throw e instanceof TException ? (TException) e : new TException(e);
        } finally {
            clientPool.returnObject(clientObj);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TApiResult call(String apiName, String appId, String accessToken, Object params)
            throws TException {
        return call(apiName, appId, accessToken, TDataEncoding.JSON_DEFAULT, params);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TApiResult call(String apiName, String appId, String accessToken, TDataEncoding encoding,
            Object params) throws TException {
        TApiAuth apiAuth = new TApiAuth().setAppId(appId).setAccessToken(accessToken);
        JsonNode paramsJson = params instanceof JsonNode
                ? (JsonNode) params
                : SerializationUtils.toJson(params);
        TApiParams apiParams = new TApiParams().setEncoding(encoding)
                .setExpectedReturnEncoding(TDataEncoding.JSON_DEFAULT)
                .setParamsData(ThriftApiUtils.encodeFromJson(encoding, paramsJson));
        return call(apiName, apiAuth, apiParams);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TApiResult call(String apiName, TApiAuth apiAuth, TApiParams apiParams)
            throws TException {
        TApiService.Iface clientObj = null;
        try {
            clientObj = clientPool.borrowObject();
            return clientObj.call(apiName, apiAuth, apiParams);
        } catch (Exception e) {
            throw e instanceof TException ? (TException) e : new TException(e);
        } finally {
            clientPool.returnObject(clientObj);
        }
    }

}
