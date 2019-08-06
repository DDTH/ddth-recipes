package com.github.ddth.recipes.apiservice.grpc;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.ddth.commons.utils.SerializationUtils;
import com.github.ddth.recipes.apiservice.clientpool.AbstractClient;
import com.github.ddth.recipes.apiservice.clientpool.HostAndPort;
import com.github.ddth.recipes.apiservice.clientpool.RetryPolicy;
import com.github.ddth.recipes.apiservice.grpc.def.PApiServiceProto;
import io.grpc.ManagedChannel;
import io.grpc.netty.NettyChannelBuilder;
import io.grpc.okhttp.OkHttpChannelBuilder;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSocketFactory;
import java.util.function.Function;

/**
 * Base class for gRPC API client implementations.
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since v0.2.0
 */
public abstract class BaseGrpcApiClient extends AbstractClient {
    /**
     * If {@code true} use SSL transport.
     */
    private boolean sslTransport = false;

    /**
     * If {@code true} use OkHttp lib, default value is {@code false} to use Netty lib.
     */
    private boolean useOkHttp = false;

    /**
     * {@link io.netty.handler.ssl.SslContext} used by Netty.
     */
    private io.netty.handler.ssl.SslContext nettySslContext;

    /**
     * {@link SSLSocketFactory} used by OkHttp
     */
    private SSLSocketFactory sslSocketFactory;

    /**
     * If {@code true} use OkHttp lib, otherwise use Netty lib.
     *
     * @return
     */
    public boolean isUseOkHttp() {
        return useOkHttp;
    }

    /**
     * If {@code true} use OkHttp lib, otherwise use Netty lib.
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
     * If {@code true} use SSL transport.
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
        return setSslTransport(false);
    }

    /**
     * Enable SSL transport.
     *
     * @param nettySslContext  for Netty SSL client
     * @param sslSocketFactory for OkHttp SSL client
     * @return
     */
    public BaseGrpcApiClient enableSslTransport(io.netty.handler.ssl.SslContext nettySslContext,
            SSLSocketFactory sslSocketFactory) {
        setSslTransport(true);
        this.nettySslContext = nettySslContext;
        this.sslSocketFactory = sslSocketFactory;
        return this;
    }

    /**
     * Get the associated {@link io.netty.handler.ssl.SslContext} used by Netty.
     *
     * @return
     */
    public io.netty.handler.ssl.SslContext getNettySslContext() {
        return nettySslContext;
    }

    /**
     * Associate a {@link io.netty.handler.ssl.SslContext} used by Netty.
     *
     * @param nettySslContext
     * @return
     */
    public BaseGrpcApiClient setNettySslContext(io.netty.handler.ssl.SslContext nettySslContext) {
        this.nettySslContext = nettySslContext;
        return this;
    }

    /**
     * Get the associated {@link SSLSocketFactory} used by OkHttp.
     *
     * @return
     */
    public SSLSocketFactory getSslSocketFactory() {
        return sslSocketFactory;
    }

    /**
     * Associate a {@link SSLSocketFactory} used by OkHttp.
     *
     * @param sslSocketFactory
     * @return
     */
    public BaseGrpcApiClient setSslSocketFactory(SSLSocketFactory sslSocketFactory) {
        this.sslSocketFactory = sslSocketFactory;
        return this;
    }

    /**
     * @param hostAndPort
     * @return
     */
    protected ManagedChannel buildManagedChannelOkHttp(HostAndPort hostAndPort) {
        OkHttpChannelBuilder channelBuilder = OkHttpChannelBuilder.forAddress(hostAndPort.host, hostAndPort.port);
        if (isSslTransport()) {
            SSLSocketFactory sslSocketFactory = this.sslSocketFactory != null ?
                    this.sslSocketFactory :
                    (SSLSocketFactory) SSLSocketFactory.getDefault();
            channelBuilder.useTransportSecurity().useTransportSecurity().sslSocketFactory(sslSocketFactory);
        } else {
            channelBuilder.usePlaintext();
        }
        return channelBuilder.build();
    }

    /**
     * @param hostAndPort
     * @return
     */
    protected ManagedChannel buildManagedChannelNetty(HostAndPort hostAndPort) throws SSLException {
        NettyChannelBuilder channelBuilder = NettyChannelBuilder.forAddress(hostAndPort.host, hostAndPort.port);
        if (isSslTransport()) {
            io.netty.handler.ssl.SslContext sslContext = nettySslContext != null ?
                    nettySslContext :
                    io.grpc.netty.GrpcSslContexts.forClient().clientAuth(io.netty.handler.ssl.ClientAuth.OPTIONAL)
                            .build();
            channelBuilder.useTransportSecurity().negotiationType(io.grpc.netty.NegotiationType.TLS)
                    .sslContext(sslContext);
        } else {
            channelBuilder.usePlaintext();
        }
        return channelBuilder.build();
    }

    /**
     * Build request object, ready for making API call.
     *
     * @param apiName
     * @param appId
     * @param accessToken
     * @param encoding
     * @param params
     * @return
     * @since 1.0.0
     */
    protected PApiServiceProto.PApiContext buildRequest(String apiName, String appId, String accessToken,
            PApiServiceProto.PDataEncoding encoding, Object params) {
        PApiServiceProto.PApiAuth apiAuth = PApiServiceProto.PApiAuth.newBuilder().setAppId(appId)
                .setAccessToken(accessToken).build();

        JsonNode paramsJson = params instanceof JsonNode ? (JsonNode) params : SerializationUtils.toJson(params);
        PApiServiceProto.PApiParams apiParams = PApiServiceProto.PApiParams.newBuilder().setEncoding(encoding)
                .setExpectedReturnEncoding(PApiServiceProto.PDataEncoding.JSON_DEFAULT)
                .setParamsData(GrpcUtils.encodeFromJson(encoding, paramsJson)).build();

        PApiServiceProto.PApiContext request = PApiServiceProto.PApiContext.newBuilder().setApiName(apiName)
                .setApiAuth(apiAuth).setApiParams(apiParams).build();
        return request;
    }

    /**
     * Execute a function with retry.
     *
     * @param retryPolicy
     * @param func
     * @param <T>
     * @return
     * @since 1.0.0
     */
    protected <T> T doWithRetry(RetryPolicy retryPolicy, Function<RetryPolicy, T> func) {
        while (!retryPolicy.isMaxRetriesExceeded()) {
            try {
                return func.apply(retryPolicy);
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
}
