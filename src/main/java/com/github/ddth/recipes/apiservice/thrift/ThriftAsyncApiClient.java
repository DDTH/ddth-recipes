package com.github.ddth.recipes.apiservice.thrift;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.ddth.commons.utils.ReflectionUtils;
import com.github.ddth.commons.utils.SerializationUtils;
import com.github.ddth.recipes.apiservice.clientpool.ApiClientPool;
import com.github.ddth.recipes.apiservice.clientpool.HostAndPort;
import com.github.ddth.recipes.apiservice.clientpool.IClientFactory;
import com.github.ddth.recipes.apiservice.thrift.def.*;
import com.google.common.collect.Sets;
import org.apache.thrift.async.AsyncMethodCallback;
import org.apache.thrift.async.TAsyncClientManager;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.TNonblockingSocket;
import org.apache.thrift.transport.TNonblockingTransport;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Thrift Async API client.
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since v0.2.0
 */
public class ThriftAsyncApiClient extends BaseThriftApiClient implements IThriftAsyncApiClient {

    private final Logger LOGGER = LoggerFactory.getLogger(ThriftAsyncApiClient.class);

    private ApiClientPool<TApiService.AsyncClient, TApiService.AsyncIface> clientPool;
    private ExecutorService executorService;

    /*----------------------------------------------------------------------*/
    private final class ClientFactory implements IClientFactory<TApiService.AsyncClient> {
        /**
         * {@inheritDoc}
         */
        @Override
        public TApiService.AsyncClient create(int serverIndexHash) throws Exception {
            HostAndPort[] serverHostAndPortList = getServerHostAndPortList();
            HostAndPort hostAndPort = serverHostAndPortList[serverIndexHash
                    % serverHostAndPortList.length];
            TNonblockingTransport transport;
            if (isSslTransport()) {
                throw new IllegalArgumentException("This client does not support SSL transport!");
            } else {
                transport = new TNonblockingSocket(hostAndPort.host, hostAndPort.port,
                        getTimeoutMs());
            }
            TProtocolFactory protocolFactory = isCompactProtocol()
                    ? new TCompactProtocol.Factory()
                    : new TBinaryProtocol.Factory();
            return new TApiService.AsyncClient(protocolFactory, new TAsyncClientManager(),
                    transport);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void destroy(TApiService.AsyncClient client) {
            //EMPTY
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
    public ThriftAsyncApiClient init() throws Exception {
        super.init();

        if (clientPool == null) {
            clientPool = new ApiClientPool<>(TApiService.AsyncClient.class,
                    TApiService.AsyncIface.class, new ClientFactory(), getRetryPolicy()).init();
        }

        if (executorService == null) {
            executorService = Executors
                    .newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        }

        return this;
    }

    /**
     * Destroy method.
     */
    public void destroy() {
        if (executorService != null) {
            try {
                executorService.shutdown();
            } catch (Exception e) {
                LOGGER.warn(e.getMessage(), e);
            } finally {
                executorService = null;
            }
        }

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
    private AtomicLong counter = new AtomicLong(0);

    /**
     * Return {@code true} if this client has pending tasks waiting to be executed.
     *
     * @return
     */
    public boolean hasPendingTasks() {
        return counter.get() > 0;
    }

    private <T> void submitTask(String method, AsyncMethodCallback<T> resultHandler,
            Object... params) {
        Class[] paramTypes = params != null ? new Class[params.length + 1] : new Class[1];
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                paramTypes[i] = params[i].getClass();
            }
        }
        paramTypes[paramTypes.length - 1] = AsyncMethodCallback.class;

        Method m = ReflectionUtils.getMethod(method, TApiService.AsyncIface.class, paramTypes);
        if (m == null) {
            throw new IllegalArgumentException("Invalid method [" + method + "]!");
        }
        counter.incrementAndGet();
        executorService.execute(() -> {
            try {
                TApiService.AsyncIface clientObj = clientPool.borrowObject();
                try {
                    Object[] paramsList =
                            params != null ? new Object[params.length + 1] : new Object[1];
                    if (params != null) {
                        for (int i = 0; i < params.length; i++) {
                            paramsList[i] = params[i];
                        }
                    }
                    paramsList[paramsList.length - 1] = new AsyncMethodCallback<T>() {
                        @Override
                        public void onComplete(T aResult) {
                            try {
                                resultHandler.onComplete(aResult);
                            } finally {
                                counter.decrementAndGet();
                                clientPool.returnObject(clientObj);
                            }
                        }

                        @Override
                        public void onError(Exception e) {
                            try {
                                resultHandler.onError(e);
                            } finally {
                                counter.decrementAndGet();
                                clientPool.returnObject(clientObj);
                            }
                        }
                    };
                    m.invoke(clientObj, paramsList);
                } catch (Exception e) {
                    clientPool.returnObject(clientObj);
                    throw e;
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void ping(AsyncMethodCallback<Void> resultHandler) {
        submitTask("ping", resultHandler);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void check(String appId, String accessToken,
            AsyncMethodCallback<TApiResult> resultHandler) {
        check(new TApiAuth().setAppId(appId).setAccessToken(accessToken), resultHandler);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void check(TApiAuth apiAuth, AsyncMethodCallback<TApiResult> resultHandler) {
        submitTask("check", resultHandler, apiAuth);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void call(String apiName, String appId, String accessToken, Object params,
            AsyncMethodCallback<TApiResult> resultHandler) {
        call(apiName, appId, accessToken, TDataEncoding.JSON_DEFAULT, params, resultHandler);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void call(String apiName, String appId, String accessToken, TDataEncoding encoding,
            Object params, AsyncMethodCallback<TApiResult> resultHandler) {
        TApiAuth apiAuth = new TApiAuth().setAppId(appId).setAccessToken(accessToken);
        JsonNode paramsJson = params instanceof JsonNode
                ? (JsonNode) params
                : SerializationUtils.toJson(params);
        TApiParams apiParams = new TApiParams().setEncoding(encoding)
                .setExpectedReturnEncoding(TDataEncoding.JSON_DEFAULT)
                .setParamsData(ThriftApiUtils.encodeFromJson(encoding, paramsJson));
        call(apiName, apiAuth, apiParams, resultHandler);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void call(String apiName, TApiAuth apiAuth, TApiParams apiParams,
            AsyncMethodCallback<TApiResult> resultHandler) {
        submitTask("call", resultHandler, apiName, apiAuth, apiParams);
    }
}
