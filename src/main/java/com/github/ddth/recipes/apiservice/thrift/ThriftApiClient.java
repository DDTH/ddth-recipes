package com.github.ddth.recipes.apiservice.thrift;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.ddth.commons.utils.SerializationUtils;
import com.github.ddth.recipes.apiservice.clientpool.ApiClientPool;
import com.github.ddth.recipes.apiservice.clientpool.HostAndPort;
import com.github.ddth.recipes.apiservice.clientpool.IClientFactory;
import com.github.ddth.recipes.apiservice.thrift.def.*;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Thrift API client.
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since v0.2.0
 */
public class ThriftApiClient extends BaseThriftApiClient implements IThriftApiClient {
    private final Logger LOGGER = LoggerFactory.getLogger(ThriftApiClient.class);

    private ApiClientPool<TApiService.Client, TApiService.Iface> clientPool;

    /*----------------------------------------------------------------------*/
    private final class ClientFactory implements IClientFactory<TApiService.Client> {
        /**
         * {@inheritDoc}
         */
        @Override
        public TApiService.Client create(int serverIndexHash) throws Exception {
            HostAndPort[] serverHostAndPortList = getServerHostAndPortList();
            HostAndPort hostAndPort = serverHostAndPortList[serverIndexHash % serverHostAndPortList.length];
            TTransport transport;
            if (isSslTransport()) {
                TSSLTransportFactory.TSSLTransportParameters params = new TSSLTransportFactory.TSSLTransportParameters();
                params.setTrustStore(getTrustStorePath(), getTrustStorePassword());
                transport = TSSLTransportFactory
                        .getClientSocket(hostAndPort.host, hostAndPort.port, getTimeout(), params);
            } else {
                transport = new TFramedTransport(new TSocket(hostAndPort.host, hostAndPort.port, getTimeout()));
                transport.open();
            }
            TProtocol protocol = isCompactProtocol() ? new TCompactProtocol(transport) : new TBinaryProtocol(transport);
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

            TTransport outputTransport = client.getOutputProtocol().getTransport();
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
            clientPool = new ApiClientPool<>(TApiService.Client.class, TApiService.Iface.class, new ClientFactory(),
                    getRetryPolicy()).init();
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
    public TApiResult call(String apiName, String appId, String accessToken, TDataEncoding encoding, Object params)
            throws TException {
        TApiAuth apiAuth = new TApiAuth().setAppId(appId).setAccessToken(accessToken);
        JsonNode paramsJson = params instanceof JsonNode ? (JsonNode) params : SerializationUtils.toJson(params);
        TApiParams apiParams = new TApiParams().setEncoding(encoding)
                .setExpectedReturnEncoding(TDataEncoding.JSON_DEFAULT)
                .setParamsData(ThriftUtils.encodeFromJson(encoding, paramsJson));
        return call(apiName, apiAuth, apiParams);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TApiResult call(String apiName, TApiAuth apiAuth, TApiParams apiParams) throws TException {
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
