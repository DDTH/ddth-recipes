package com.github.ddth.recipes.apiservice.grpc;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.github.ddth.recipes.apiservice.*;
import com.github.ddth.recipes.apiservice.clientpool.RetryPolicy;
import com.github.ddth.recipes.apiservice.grpc.def.PApiServiceProto;
import com.google.protobuf.ByteString;
import io.grpc.Server;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NettyServerBuilder;
import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import org.apache.commons.lang3.StringUtils;

import javax.net.ssl.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Thrift API utility class.
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since v0.2.0
 */
public class GrpcApiUtils {

    /**
     * Create a (non-SSL) gRPC server to serve API calls with default max-header-list-size (8Kb) and
     * max-message-size (64Kb).
     *
     * @param apiRouter
     * @param listenHost
     *         host/address to listen, empty {@code "0.0.0.0"} will be used
     * @param listenPort
     * @return
     */
    public static Server createGrpcServer(ApiRouter apiRouter, String listenHost, int listenPort) {
        return createGrpcServer(apiRouter, listenHost, listenPort, 8 * 1024, 64 * 1024);
    }

    /**
     * Create a (non-SSL) gRPC server to serve API calls.
     *
     * @param apiRouter
     * @param listenHost
     *         host/address to listen, empty {@code "0.0.0.0"} will be used
     * @param listenPort
     * @param maxHeaderListSize
     * @param maxMsgSize
     * @return
     */
    public static Server createGrpcServer(ApiRouter apiRouter, String listenHost, int listenPort,
            int maxHeaderListSize, int maxMsgSize) {
        InetSocketAddress listenAddr = new InetSocketAddress(
                !StringUtils.isBlank(listenHost) ? listenHost : "0.0.0.0", listenPort);
        Server server = NettyServerBuilder.forAddress(listenAddr).forPort(listenPort)
                .maxHeaderListSize(maxHeaderListSize).maxInboundMessageSize(maxMsgSize)
                .addService(new ApiServiceHandler(apiRouter)).build();
        return server;
    }

    /**
     * Create a (SSL-enabled) gRPC server to serve API calls with default max-header-list-size (8Kb)
     * and max-message-size (64Kb).
     *
     * @param apiRouter
     * @param listenHost
     * @param listenPort
     * @param certificateChainFile
     * @param privateKeyFile
     * @param keyFilePassword
     * @return
     * @throws SSLException
     * @see {@code https://github.com/grpc/grpc-java/blob/master/SECURITY.md}
     */
    public static Server createGrpcServerSsl(ApiRouter apiRouter, String listenHost, int listenPort,
            File certificateChainFile, File privateKeyFile, String keyFilePassword)
            throws SSLException {
        return createGrpcServerSsl(apiRouter, listenHost, listenPort, 8 * 1024, 64 * 1024,
                certificateChainFile, privateKeyFile, keyFilePassword);
    }

    /**
     * Create a (SSL-enabled) gRPC server to serve API calls with default max-header-list-size (8Kb)
     * and max-message-size (64Kb).
     *
     * @param apiRouter
     * @param listenHost
     * @param listenPort
     * @param sslContext
     * @return
     * @see {@code https://github.com/grpc/grpc-java/blob/master/SECURITY.md}
     */
    public static Server createGrpcServerSsl(ApiRouter apiRouter, String listenHost, int listenPort,
            SslContext sslContext) {
        return createGrpcServerSsl(apiRouter, listenHost, listenPort, 8 * 1024, 64 * 1024,
                sslContext);
    }

    /**
     * Create a (SSL-enabled) gRPC server to serve API calls.
     *
     * @param apiRouter
     * @param listenHost
     *         host/address to listen, empty {@code "0.0.0.0"} will be used
     * @param listenPort
     * @param maxHeaderListSize
     * @param maxMsgSize
     * @param certificateChainFile
     * @param privateKeyFile
     * @param keyFilePassword
     * @return
     * @throws SSLException
     * @see {@code https://github.com/grpc/grpc-java/blob/master/SECURITY.md}
     */
    public static Server createGrpcServerSsl(ApiRouter apiRouter, String listenHost, int listenPort,
            int maxHeaderListSize, int maxMsgSize, File certificateChainFile, File privateKeyFile,
            String keyFilePassword) throws SSLException {
        return createGrpcServerSsl(apiRouter, listenHost, listenPort, maxHeaderListSize, maxMsgSize,
                buildServerSslContext(certificateChainFile, privateKeyFile, keyFilePassword));
    }

    /**
     * Create a (SSL-enabled) gRPC server to serve API calls.
     *
     * @param apiRouter
     * @param listenHost
     *         host/address to listen, empty {@code "0.0.0.0"} will be used
     * @param listenPort
     * @param maxHeaderListSize
     * @param maxMsgSize
     * @param sslContext
     * @return
     * @see {@code https://github.com/grpc/grpc-java/blob/master/SECURITY.md}
     */
    public static Server createGrpcServerSsl(ApiRouter apiRouter, String listenHost, int listenPort,
            int maxHeaderListSize, int maxMsgSize, SslContext sslContext) {
        InetSocketAddress listenAddr = new InetSocketAddress(
                !StringUtils.isBlank(listenHost) ? listenHost : "0.0.0.0", listenPort);
        Server server = NettyServerBuilder.forAddress(listenAddr).forPort(listenPort)
                .maxHeaderListSize(maxHeaderListSize).maxInboundMessageSize(maxMsgSize)
                .addService(new ApiServiceHandler(apiRouter)).sslContext(sslContext).build();
        return server;
    }

    /**
     * Build SSL-context for server.
     *
     * @param certificateChainFile
     * @param privateKeyFile
     * @param keyFilePassword
     * @return
     * @throws SSLException
     */
    public static SslContext buildServerSslContext(File certificateChainFile, File privateKeyFile,
            String keyFilePassword) throws SSLException {
        SslContextBuilder sslContextBuilder = StringUtils.isEmpty(keyFilePassword)
                ? GrpcSslContexts.forServer(certificateChainFile, privateKeyFile)
                : GrpcSslContexts.forServer(certificateChainFile, privateKeyFile, keyFilePassword);
        sslContextBuilder.clientAuth(ClientAuth.OPTIONAL);
        return sslContextBuilder.build();
    }

    /*----------------------------------------------------------------------*/

    /**
     * Create a (non-SSL) gRPC Async API client, using {@code OkHttp} lib and with default {@link
     * RetryPolicy}.
     *
     * @param serverHostsAndPorts
     *         server's host and port config, format {@code host1:port1,host2:port2,...}
     * @return
     */
    public static GrpcAsyncApiClient createGrpcAsyncApiClient(String serverHostsAndPorts)
            throws Exception {
        return createGrpcAsyncApiClient(serverHostsAndPorts, true,
                RetryPolicy.DEFAULT_RETRY_POLICY);
    }

    /**
     * Create a (non-SSL) gRPC Async API client, with default {@link RetryPolicy}.
     *
     * @param serverHostsAndPorts
     *         server's host and port config, format {@code host1:port1,host2:port2,...}
     * @param useOkHttp
     *         if {@code true} use {@code OkHttp} lib, otherwise use {@code Netty} lib
     * @return
     */
    public static GrpcAsyncApiClient createGrpcAsyncApiClient(String serverHostsAndPorts,
            boolean useOkHttp) throws Exception {
        return createGrpcAsyncApiClient(serverHostsAndPorts, useOkHttp,
                RetryPolicy.DEFAULT_RETRY_POLICY);
    }

    /**
     * Create a (non-SSL) gRPC Async API client, using {@code OkHttp} lib.
     *
     * @param serverHostsAndPorts
     *         server's host and port config, format {@code host1:port1,host2:port2,...}
     * @param retryPolicy
     * @return
     */
    public static GrpcAsyncApiClient createGrpcAsyncApiClient(String serverHostsAndPorts,
            RetryPolicy retryPolicy) throws Exception {
        return createGrpcAsyncApiClient(serverHostsAndPorts, true, retryPolicy);
    }

    /**
     * Create a (non-SSL) gRPC Async API client.
     *
     * @param serverHostsAndPorts
     *         server's host and port config, format {@code host1:port1,host2:port2,...}
     * @param useOkHttp
     * @param retryPolicy
     *         if {@code true} use {@code OkHttp} lib, otherwise use {@code Netty} lib
     * @return
     */
    public static GrpcAsyncApiClient createGrpcAsyncApiClient(String serverHostsAndPorts,
            boolean useOkHttp, RetryPolicy retryPolicy) throws Exception {
        GrpcAsyncApiClient client = new GrpcAsyncApiClient();
        client.disableSslTransport().setServerHostsAndPorts(serverHostsAndPorts)
                .setRetryPolicy(retryPolicy).setUseOkHttp(useOkHttp).init();
        return client;
    }
    /*----------------------------------------------------------------------*/

    /**
     * Create a (non-SSL) gRPC API client, using {@code OkHttp} lib and with default {@link
     * RetryPolicy}.
     *
     * @param serverHostsAndPorts
     *         server's host and port config, format {@code host1:port1,host2:port2,...}
     * @return
     */
    public static GrpcApiClient createGrpcApiClient(String serverHostsAndPorts) throws Exception {
        return createGrpcApiClient(serverHostsAndPorts, true, RetryPolicy.DEFAULT_RETRY_POLICY);
    }

    /**
     * Create a (non-SSL) gRPC API client, with default {@link RetryPolicy}.
     *
     * @param serverHostsAndPorts
     *         server's host and port config, format {@code host1:port1,host2:port2,...}
     * @param useOkHttp
     *         if {@code true} use {@code OkHttp} lib, otherwise use {@code Netty} lib
     * @return
     */
    public static GrpcApiClient createGrpcApiClient(String serverHostsAndPorts, boolean useOkHttp)
            throws Exception {
        return createGrpcApiClient(serverHostsAndPorts, useOkHttp,
                RetryPolicy.DEFAULT_RETRY_POLICY);
    }

    /**
     * Create a (non-SSL) gRPC API client, using {@code OkHttp} lib.
     *
     * @param serverHostsAndPorts
     *         server's host and port config, format {@code host1:port1,host2:port2,...}
     * @param retryPolicy
     * @return
     */
    public static GrpcApiClient createGrpcApiClient(String serverHostsAndPorts,
            RetryPolicy retryPolicy) throws Exception {
        return createGrpcApiClient(serverHostsAndPorts, true, retryPolicy);
    }

    /**
     * Create a (non-SSL) gRPC API client.
     *
     * @param serverHostsAndPorts
     *         server's host and port config, format {@code host1:port1,host2:port2,...}
     * @param useOkHttp
     * @param retryPolicy
     *         if {@code true} use {@code OkHttp} lib, otherwise use {@code Netty} lib
     * @return
     */
    public static GrpcApiClient createGrpcApiClient(String serverHostsAndPorts, boolean useOkHttp,
            RetryPolicy retryPolicy) throws Exception {
        GrpcApiClient client = new GrpcApiClient();
        client.disableSslTransport().setServerHostsAndPorts(serverHostsAndPorts)
                .setRetryPolicy(retryPolicy).setUseOkHttp(useOkHttp).init();
        return client;
    }

    /**
     * Create a (SSL-enabled) gRPC API client, using {@code OkHttp} lib and with default {@link
     * RetryPolicy}.
     *
     * @param serverHostsAndPorts
     *         server's host and port config, format {@code host1:port1,host2:port2,...}
     * @return
     * @throws Exception
     */
    public static GrpcApiClient createGrpcApiClientSsl(String serverHostsAndPorts)
            throws Exception {
        return createGrpcApiClientSsl(serverHostsAndPorts, true, RetryPolicy.DEFAULT_RETRY_POLICY,
                null, null);
    }

    /**
     * Create a (SSL-enabled) gRPC API client, using {@code OkHttp} lib and with default {@link
     * RetryPolicy}.
     *
     * @param serverHostsAndPorts
     *         server's host and port config, format {@code host1:port1,host2:port2,...}
     * @param nettySslContext
     *         to use SSL with Netty
     * @param sslSocketFactory
     *         to use SSL with OkHttp
     * @return
     */
    public static GrpcApiClient createGrpcApiClientSsl(String serverHostsAndPorts,
            SslContext nettySslContext, SSLSocketFactory sslSocketFactory) throws Exception {
        return createGrpcApiClientSsl(serverHostsAndPorts, true, RetryPolicy.DEFAULT_RETRY_POLICY,
                nettySslContext, sslSocketFactory);
    }

    /**
     * Create a (SSL-enabled) gRPC API client, with default {@link RetryPolicy}.
     *
     * @param serverHostsAndPorts
     *         server's host and port config, format {@code host1:port1,host2:port2,...}
     * @param useOkHttp
     *         if {@code true} use {@code OkHttp} lib, otherwise use {@code Netty} lib
     * @return
     * @throws Exception
     */
    public static GrpcApiClient createGrpcApiClientSsl(String serverHostsAndPorts,
            boolean useOkHttp) throws Exception {
        return createGrpcApiClientSsl(serverHostsAndPorts, useOkHttp,
                RetryPolicy.DEFAULT_RETRY_POLICY, null, null);
    }

    /**
     * Create a (SSL-enabled) gRPC API client, with default {@link RetryPolicy}.
     *
     * @param serverHostsAndPorts
     *         server's host and port config, format {@code host1:port1,host2:port2,...}
     * @param useOkHttp
     *         if {@code true} use {@code OkHttp} lib, otherwise use {@code Netty} lib
     * @param nettySslContext
     *         to use SSL with Netty
     * @param sslSocketFactory
     *         to use SSL with OkHttp
     * @return
     */
    public static GrpcApiClient createGrpcApiClientSsl(String serverHostsAndPorts,
            boolean useOkHttp, SslContext nettySslContext, SSLSocketFactory sslSocketFactory)
            throws Exception {
        return createGrpcApiClientSsl(serverHostsAndPorts, useOkHttp,
                RetryPolicy.DEFAULT_RETRY_POLICY, nettySslContext, sslSocketFactory);
    }

    /**
     * Create a (SSL-enabled) gRPC API client, using {@code OkHttp} lib.
     *
     * @param serverHostsAndPorts
     *         server's host and port config, format {@code host1:port1,host2:port2,...}
     * @param retryPolicy
     * @return
     * @throws Exception
     */
    public static GrpcApiClient createGrpcApiClientSsl(String serverHostsAndPorts,
            RetryPolicy retryPolicy) throws Exception {
        return createGrpcApiClientSsl(serverHostsAndPorts, true, retryPolicy, null, null);
    }

    /**
     * Create a (SSL-enabled) gRPC API client, using {@code OkHttp} lib.
     *
     * @param serverHostsAndPorts
     *         server's host and port config, format {@code host1:port1,host2:port2,...}
     * @param retryPolicy
     * @param nettySslContext
     *         to use SSL with Netty
     * @param sslSocketFactory
     *         to use SSL with OkHttp
     * @return
     */
    public static GrpcApiClient createGrpcApiClientSsl(String serverHostsAndPorts,
            RetryPolicy retryPolicy, SslContext nettySslContext, SSLSocketFactory sslSocketFactory)
            throws Exception {
        return createGrpcApiClientSsl(serverHostsAndPorts, true, retryPolicy, nettySslContext,
                sslSocketFactory);
    }

    /**
     * Create a (SSL-enabled) gRPC API client.
     *
     * @param serverHostsAndPorts
     *         server's host and port config, format {@code host1:port1,host2:port2,...}
     * @param useOkHttp
     *         if {@code true} use {@code OkHttp} lib, otherwise use {@code Netty} lib
     * @param retryPolicy
     * @return
     * @throws Exception
     */
    public static GrpcApiClient createGrpcApiClientSsl(String serverHostsAndPorts,
            boolean useOkHttp, RetryPolicy retryPolicy) throws Exception {
        return createGrpcApiClientSsl(serverHostsAndPorts, useOkHttp, retryPolicy, null, null);
    }

    /**
     * Create a (SSL-enabled) gRPC API client.
     *
     * @param serverHostsAndPorts
     *         server's host and port config, format {@code host1:port1,host2:port2,...}
     * @param useOkHttp
     *         if {@code true} use {@code OkHttp} lib, otherwise use {@code Netty} lib
     * @param retryPolicy
     * @param nettySslContext
     *         to use SSL with Netty
     * @param sslSocketFactory
     *         to use SSL with OkHttp
     * @return
     */
    public static GrpcApiClient createGrpcApiClientSsl(String serverHostsAndPorts,
            boolean useOkHttp, RetryPolicy retryPolicy, SslContext nettySslContext,
            SSLSocketFactory sslSocketFactory) throws Exception {
        GrpcApiClient client = new GrpcApiClient();
        client.enableSslTransport(nettySslContext, sslSocketFactory)
                .setServerHostsAndPorts(serverHostsAndPorts).setRetryPolicy(retryPolicy)
                .setUseOkHttp(useOkHttp).init();
        return client;
    }

    /**
     * Return a {@link SslContextBuilder} for client use.
     *
     * @return
     */
    public static SslContextBuilder buildClientSslContextBuilder() {
        return GrpcSslContexts.forClient().clientAuth(ClientAuth.OPTIONAL).trustManager();
    }

    /**
     * Load & Build {@link SSLContext} from from certificate collection file.
     *
     * @param trustCertCollectionFile
     * @return
     * @throws CertificateException
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     * @throws KeyManagementException
     * @throws IOException
     */
    public static SSLContext buildSSLContextForCertificates(File trustCertCollectionFile)
            throws CertificateException, NoSuchAlgorithmException, KeyStoreException,
            KeyManagementException, IOException {
        return buildSSLContextForCertificates(null, trustCertCollectionFile);
    }

    /**
     * Load & Build {@link SSLContext} from from certificate collection file.
     *
     * @param keyManagers
     * @param trustCertCollectionFile
     * @return
     * @throws CertificateException
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     * @throws IOException
     * @throws KeyManagementException
     */
    public static SSLContext buildSSLContextForCertificates(KeyManager[] keyManagers,
            File trustCertCollectionFile)
            throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException,
            KeyManagementException {
        X509TrustManager[] trustManagers = buildTrustManagerForCertificates(
                trustCertCollectionFile);
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagers, trustManagers, new SecureRandom());
        return sslContext;
    }

    /**
     * Load & Build list of X509 trust managers from certificate collection file.
     *
     * @param trustCertCollectionFile
     * @return
     * @throws CertificateException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     */
    public static X509TrustManager[] buildTrustManagerForCertificates(File trustCertCollectionFile)
            throws CertificateException, IOException, NoSuchAlgorithmException, KeyStoreException {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        Collection<? extends Certificate> certificates = cf.generateCertificates(
                new BufferedInputStream(new FileInputStream(trustCertCollectionFile)));
        if (certificates.isEmpty()) {
            throw new IllegalArgumentException("Trusted certificate collection is empty!");
        }

        char[] password = "password".toCharArray(); // Any password will work.

        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(null, password);
        int index = 0;
        for (Certificate certificate : certificates) {
            String certificateAlias = Integer.toString(index++);
            keyStore.setCertificateEntry(certificateAlias, certificate);
        }

        TrustManagerFactory tmf = TrustManagerFactory
                .getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(keyStore);
        List<X509TrustManager> result = new LinkedList<>();
        for (TrustManager tm : tmf.getTrustManagers()) {
            if (tm instanceof X509TrustManager) {
                result.add((X509TrustManager) tm);
            }
        }
        return result.toArray(new X509TrustManager[0]);
    }

    /*----------------------------------------------------------------------*/

    /**
     * Build {@link ApiResult} from {@link PApiServiceProto.PApiResult}.
     *
     * @param _apiResult
     * @return
     */
    public static ApiResult toApiResult(PApiServiceProto.PApiResult _apiResult) {
        ApiResult apiResult = new ApiResult(_apiResult.getStatus(), _apiResult.getMessage(),
                decodeToJson(_apiResult.getEncoding(), _apiResult.getResultData()));
        apiResult.setDebugData(decodeToJson(_apiResult.getEncoding(), _apiResult.getDebugData()));
        return apiResult;
    }

    /**
     * Build API response.
     *
     * @param _apiResult
     * @param encoding
     * @return
     */
    public static PApiServiceProto.PApiResult buildResponse(ApiResult _apiResult,
            PApiServiceProto.PDataEncoding encoding) {
        PApiServiceProto.PApiResult.Builder apiResultBuilder = PApiServiceProto.PApiResult
                .newBuilder();
        apiResultBuilder.setStatus(_apiResult.getStatus()).setMessage(_apiResult.getMessage());
        if (encoding == null) {
            encoding = PApiServiceProto.PDataEncoding.JSON_STRING;
        }
        apiResultBuilder.setEncoding(encoding);
        apiResultBuilder
                .setResultData(GrpcApiUtils.encodeFromJson(encoding, _apiResult.getDataAsJson()));
        apiResultBuilder.setDebugData(
                GrpcApiUtils.encodeFromJson(encoding, _apiResult.getDebugDataAsJson()));

        return apiResultBuilder.build();
    }

    /**
     * Parse gRPC's parameters.
     *
     * @param _apiParams
     * @return
     */
    public static ApiParams parseParams(PApiServiceProto.PApiParams _apiParams) {
        JsonNode paramNode = GrpcApiUtils.decodeToJson(_apiParams.getEncoding() != null
                ? _apiParams.getEncoding()
                : PApiServiceProto.PDataEncoding.JSON_STRING, _apiParams.getParamsData());
        ApiParams apiParams = new ApiParams(paramNode);
        return apiParams;
    }

    /**
     * Parse auth info.
     *
     * @param _apiAuth
     * @return
     */
    public static ApiAuth parseAuth(PApiServiceProto.PApiAuth _apiAuth) {
        return new ApiAuth(_apiAuth.getAppId(), _apiAuth.getAccessToken());
    }

    /**
     * Encode data from JSON to {@link ByteString}.
     *
     * @param encoding
     * @param jsonData
     * @return
     */
    public static ByteString encodeFromJson(PApiServiceProto.PDataEncoding encoding,
            JsonNode jsonData) {
        byte[] data =
                jsonData == null || jsonData instanceof NullNode || jsonData instanceof MissingNode
                        ? null
                        : jsonData.toString().getBytes(StandardCharsets.UTF_8);
        if (data == null) {
            return ByteString.EMPTY;
        }
        if (encoding == null) {
            encoding = PApiServiceProto.PDataEncoding.JSON_STRING;
        }
        try {
            switch (encoding) {
            case JSON_DEFAULT:
            case JSON_STRING:
                return ByteString.copyFrom(data);
            case JSON_GZIP:
                return ByteString.copyFrom(ApiServiceUtils.toGzip(data));
            default:
                throw new IllegalArgumentException("Unsupported data encoding: " + encoding);
            }
        } catch (Exception e) {
            throw e instanceof RuntimeException ? (RuntimeException) e : new RuntimeException(e);
        }
    }

    /**
     * Decode data from a byte array to json.
     *
     * @param encoding
     * @param data
     * @return
     */
    public static JsonNode decodeToJson(PApiServiceProto.PDataEncoding encoding, ByteString data) {
        if (data == null || data.isEmpty()) {
            return NullNode.instance;
        }
        if (encoding == null) {
            encoding = PApiServiceProto.PDataEncoding.JSON_STRING;
        }
        try {
            switch (encoding) {
            case JSON_DEFAULT:
            case JSON_STRING:
                return ApiServiceUtils.fromJsonString(data.toByteArray());
            case JSON_GZIP:
                return ApiServiceUtils.fromJsonGzip(data.toByteArray());
            default:
                throw new IllegalArgumentException("Unsupported data encoding: " + encoding);
            }
        } catch (Exception e) {
            throw e instanceof RuntimeException ? (RuntimeException) e : new RuntimeException(e);
        }
    }
}
