package com.github.ddth.recipes.apiservice.thrift;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.ddth.commons.utils.ReflectionUtils;
import com.github.ddth.commons.utils.SerializationUtils;
import com.github.ddth.recipes.apiservice.clientpool.ApiClientPool;
import com.github.ddth.recipes.apiservice.clientpool.HostAndPort;
import com.github.ddth.recipes.apiservice.clientpool.IClientFactory;
import com.github.ddth.recipes.apiservice.thrift.def.*;
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
    private boolean myOwnExecutorService = false;

    /*----------------------------------------------------------------------*/
    private final class ClientFactory implements IClientFactory<TApiService.AsyncClient> {
        /**
         * {@inheritDoc}
         */
        @Override
        public TApiService.AsyncClient create(int serverIndexHash) throws Exception {
            HostAndPort[] serverHostAndPortList = getServerHostAndPortList();
            HostAndPort hostAndPort = serverHostAndPortList[serverIndexHash % serverHostAndPortList.length];
            TNonblockingTransport transport;
            if (isSslTransport()) {
                //see: https://github.com/apache/thrift/blob/master/lib/java/test/org/apache/thrift/test/TestServer.java#L180
                throw new IllegalArgumentException("This async-client does not support SSL transport.");
            } else {
                transport = new TNonblockingSocket(hostAndPort.host, hostAndPort.port, getTimeout());
            }
            TProtocolFactory protocolFactory = isCompactProtocol() ?
                    new TCompactProtocol.Factory() :
                    new TBinaryProtocol.Factory();
            return new TApiService.AsyncClient(protocolFactory, new TAsyncClientManager(), transport);
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
            clientPool = new ApiClientPool<>(TApiService.AsyncClient.class, TApiService.AsyncIface.class,
                    new ClientFactory(), getRetryPolicy()).init();
        }
        if (executorService == null) {
            executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            myOwnExecutorService = true;
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

        if (executorService != null && myOwnExecutorService) {
            try {
                executorService.shutdown();
            } catch (Exception e) {
                LOGGER.warn(e.getMessage(), e);
            } finally {
                executorService = null;
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
    private AtomicLong taskCounter = new AtomicLong(0);

    /**
     * Return {@code true} if this client has pending tasks waiting to be executed.
     *
     * @return
     */
    public boolean hasPendingTasks() {
        return taskCounter.get() > 0;
    }

    private <T> void submitTask(String method, AsyncMethodCallback<T> resultHandler, Object... params) {
        Class<?>[] paramTypes = params != null ? new Class[params.length + 1] : new Class[1];
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                paramTypes[i] = params[i].getClass();
            }
        }
        paramTypes[paramTypes.length - 1] = AsyncMethodCallback.class;

        Method m = ReflectionUtils.getMethod(method, TApiService.AsyncIface.class, paramTypes);
        if (m == null) {
            throw new IllegalArgumentException("Invalid method [" + method + "].");
        }
        taskCounter.incrementAndGet();
        executorService.execute(() -> {
            try {
                Object[] paramsList = params != null ? new Object[params.length + 1] : new Object[1];
                if (params != null) {
                    for (int i = 0; i < params.length; i++) {
                        paramsList[i] = params[i];
                    }
                }
                TApiService.AsyncIface clientObj = clientPool.borrowObject();
                paramsList[paramsList.length - 1] = new AsyncMethodCallback<T>() {
                    private void finish() {
                        clientPool.returnObject(clientObj);
                        taskCounter.decrementAndGet();
                    }

                    @Override
                    public void onComplete(T aResult) {
                        try {
                            resultHandler.onComplete(aResult);
                        } finally {
                            finish();
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        try {
                            resultHandler.onError(e);
                        } finally {
                            finish();
                        }
                    }
                };
                if (clientObj != null) {
                    m.invoke(clientObj, paramsList);
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
    public void check(TApiAuth apiAuth, AsyncMethodCallback<TApiResult> resultHandler) {
        submitTask("check", resultHandler, apiAuth);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void call(String apiName, String appId, String accessToken, TDataEncoding encoding, Object params,
            AsyncMethodCallback<TApiResult> resultHandler) {
        TApiAuth apiAuth = new TApiAuth().setAppId(appId).setAccessToken(accessToken);
        JsonNode paramsJson = params instanceof JsonNode ? (JsonNode) params : SerializationUtils.toJson(params);
        TApiParams apiParams = new TApiParams().setEncoding(encoding)
                .setExpectedReturnEncoding(TDataEncoding.JSON_DEFAULT)
                .setParamsData(ThriftUtils.encodeFromJson(encoding, paramsJson));
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
