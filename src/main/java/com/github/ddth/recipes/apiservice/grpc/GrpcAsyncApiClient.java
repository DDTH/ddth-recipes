package com.github.ddth.recipes.apiservice.grpc;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.ddth.commons.utils.SerializationUtils;
import com.github.ddth.recipes.apiservice.clientpool.HostAndPort;
import com.github.ddth.recipes.apiservice.clientpool.RetryPolicy;
import com.github.ddth.recipes.apiservice.grpc.def.PApiServiceGrpc;
import com.github.ddth.recipes.apiservice.grpc.def.PApiServiceProto;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.protobuf.Empty;
import io.grpc.ManagedChannel;
import io.grpc.netty.NettyChannelBuilder;
import io.grpc.okhttp.OkHttpChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSocketFactory;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * gRPC Async API client.
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since v0.2.0
 */
public class GrpcAsyncApiClient extends BaseGrpcApiClient implements IGrpcAsyncApiClient {

    private final Logger LOGGER = LoggerFactory.getLogger(GrpcAsyncApiClient.class);

    private io.netty.handler.ssl.SslContext nettySslContext;
    private SSLSocketFactory sslSocketFactory;

    private PApiServiceGrpc.PApiServiceFutureStub[] stubs;
    private ExecutorService executorService;

    /**
     * Enable SSL transport.
     *
     * @param nettySslContext
     *         for Netty SSL client
     * @param sslSocketFactory
     *         for OkHttp SSL client
     * @return
     */
    public GrpcAsyncApiClient enableSslTransport(io.netty.handler.ssl.SslContext nettySslContext,
            SSLSocketFactory sslSocketFactory) {
        setSslTransport(true);
        this.nettySslContext = nettySslContext;
        this.sslSocketFactory = sslSocketFactory;
        return this;
    }

    /**
     * Getter for {@link #nettySslContext}.
     *
     * @return
     */
    public io.netty.handler.ssl.SslContext getNettySslContext() {
        return nettySslContext;
    }

    /**
     * Setter for {@link #nettySslContext}.
     *
     * @param nettySslContext
     * @return
     */
    public GrpcAsyncApiClient setNettySslContext(io.netty.handler.ssl.SslContext nettySslContext) {
        this.nettySslContext = nettySslContext;
        return this;
    }

    /**
     * Getter for {@link #sslSocketFactory}.
     *
     * @return
     */
    public SSLSocketFactory getSslSocketFactory() {
        return sslSocketFactory;
    }

    /**
     * Setter for {@link #sslSocketFactory}.
     *
     * @param sslSocketFactory
     * @return
     */
    public GrpcAsyncApiClient setSslSocketFactory(SSLSocketFactory sslSocketFactory) {
        this.sslSocketFactory = sslSocketFactory;
        return this;
    }

    /*----------------------------------------------------------------------*/
    private PApiServiceGrpc.PApiServiceFutureStub createStub(int serverIndexHash)
            throws SSLException {
        HostAndPort[] serverHostAndPortList = getServerHostAndPortList();
        HostAndPort hostAndPort = serverHostAndPortList[serverIndexHash
                % serverHostAndPortList.length];
        ManagedChannel channel;
        if (isUseOkHttp()) {
            OkHttpChannelBuilder channelBuilder = OkHttpChannelBuilder
                    .forAddress(hostAndPort.host, hostAndPort.port);
            if (isSslTransport()) {
                SSLSocketFactory sslSocketFactory = this.sslSocketFactory != null
                        ? this.sslSocketFactory
                        : (SSLSocketFactory) SSLSocketFactory.getDefault();
                channelBuilder.useTransportSecurity()
                        .negotiationType(io.grpc.okhttp.NegotiationType.TLS)
                        .sslSocketFactory(sslSocketFactory);
            } else {
                channelBuilder.usePlaintext();
            }
            channel = channelBuilder.build();
        } else {
            NettyChannelBuilder channelBuilder = NettyChannelBuilder
                    .forAddress(hostAndPort.host, hostAndPort.port);
            if (isSslTransport()) {
                io.netty.handler.ssl.SslContext sslContext = nettySslContext != null
                        ? nettySslContext
                        : io.grpc.netty.GrpcSslContexts.forClient()
                                .clientAuth(io.netty.handler.ssl.ClientAuth.OPTIONAL).build();
                channelBuilder.useTransportSecurity()
                        .negotiationType(io.grpc.netty.NegotiationType.TLS).sslContext(sslContext);
            } else {
                channelBuilder.usePlaintext();
            }
            channel = channelBuilder.build();
        }
        return PApiServiceGrpc.newFutureStub(channel).withExecutor(executorService);
    }

    /**
     * Initializing method.
     *
     * @return
     */
    public GrpcAsyncApiClient init() throws Exception {
        super.init();

        if (executorService == null) {
            executorService = Executors
                    .newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        }

        if (stubs == null) {
            int numServers = getServerHostAndPortList().length;
            stubs = new PApiServiceGrpc.PApiServiceFutureStub[numServers];
            for (int i = 0; i < numServers; i++) {
                stubs[i] = createStub(i);
            }
        }

        return this;
    }

    /**
     * Destroy method.
     */
    public void destroy() {
        if (stubs != null) {
            for (PApiServiceGrpc.PApiServiceFutureStub stub : stubs) {
                try {
                    ((ManagedChannel) stub.getChannel()).shutdown();
                } catch (Exception e) {
                    LOGGER.warn(e.getMessage(), e);
                }
            }
            stubs = null;
        }

        if (executorService != null) {
            try {
                executorService.shutdown();
            } catch (Exception e) {
                LOGGER.warn(e.getMessage(), e);
            }
            executorService = null;
        }

        super.destroy();
    }

    /*----------------------------------------------------------------------*/

    private ListenableFuture<Empty> pingWithRetry(RetryPolicy retryPolicy, Empty request) {
        while (!retryPolicy.isMaxRetriesExceeded()) {
            int serverIndexHash = calcServerIndexHash(retryPolicy);
            try {
                return stubs[serverIndexHash % getServerHostAndPortList().length].ping(request);
            } catch (Exception e) {
                try {
                    retryPolicy.sleep();
                } catch (InterruptedException e1) {
                }
                if (retryPolicy.isMaxRetriesExceeded()) {
                    throw e;
                } else {
                    continue;
                }
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ListenableFuture<Empty> ping(Empty request) {
        return pingWithRetry(getRetryPolicy().clone(), request);
    }

    private ListenableFuture<PApiServiceProto.PApiResult> checkWithRetry(RetryPolicy retryPolicy,
            PApiServiceProto.PApiAuth request) {
        while (!retryPolicy.isMaxRetriesExceeded()) {
            int serverIndexHash = calcServerIndexHash(retryPolicy);
            try {
                return stubs[serverIndexHash % getServerHostAndPortList().length].check(request);
            } catch (Exception e) {
                try {
                    retryPolicy.sleep();
                } catch (InterruptedException e1) {
                }
                if (retryPolicy.isMaxRetriesExceeded()) {
                    throw e;
                } else {
                    continue;
                }
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ListenableFuture<PApiServiceProto.PApiResult> check(PApiServiceProto.PApiAuth request) {
        return checkWithRetry(getRetryPolicy().clone(), request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ListenableFuture<PApiServiceProto.PApiResult> check(String appId, String accessToken) {
        return check(
                PApiServiceProto.PApiAuth.newBuilder().setAppId(appId).setAccessToken(accessToken)
                        .build());
    }

    private ListenableFuture<PApiServiceProto.PApiResult> callWithRetry(RetryPolicy retryPolicy,
            PApiServiceProto.PApiContext request) {
        while (!retryPolicy.isMaxRetriesExceeded()) {
            int serverIndexHash = calcServerIndexHash(retryPolicy);
            try {
                return stubs[serverIndexHash % getServerHostAndPortList().length].call(request);
            } catch (Exception e) {
                try {
                    retryPolicy.sleep();
                } catch (InterruptedException e1) {
                }
                if (retryPolicy.isMaxRetriesExceeded()) {
                    throw e;
                } else {
                    continue;
                }
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ListenableFuture<PApiServiceProto.PApiResult> call(
            PApiServiceProto.PApiContext request) {
        return callWithRetry(getRetryPolicy().clone(), request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ListenableFuture<PApiServiceProto.PApiResult> call(String apiName, String appId,
            String accessToken, Object params) {
        return call(apiName, appId, accessToken, PApiServiceProto.PDataEncoding.JSON_DEFAULT,
                params);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ListenableFuture<PApiServiceProto.PApiResult> call(String apiName, String appId,
            String accessToken, PApiServiceProto.PDataEncoding encoding, Object params) {
        PApiServiceProto.PApiAuth apiAuth = PApiServiceProto.PApiAuth.newBuilder().setAppId(apiName)
                .setAccessToken(accessToken).build();

        JsonNode paramsJson = params instanceof JsonNode
                ? (JsonNode) params
                : SerializationUtils.toJson(params);
        PApiServiceProto.PApiParams apiParams = PApiServiceProto.PApiParams.newBuilder()
                .setEncoding(encoding)
                .setExpectedReturnEncoding(PApiServiceProto.PDataEncoding.JSON_DEFAULT)
                .setParamsData(GrpcApiUtils.encodeFromJson(encoding, paramsJson)).build();

        PApiServiceProto.PApiContext request = PApiServiceProto.PApiContext.newBuilder()
                .setApiName(apiName).setApiAuth(apiAuth).setApiParams(apiParams).build();
        return call(request);
    }

}
