package com.github.ddth.recipes.apiservice.thrift;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.github.ddth.recipes.apiservice.*;
import com.github.ddth.recipes.apiservice.thrift.def.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.thrift.TProcessorFactory;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.server.TThreadedSelectorServer;
import org.apache.thrift.transport.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

/**
 * Thrift API utility class.
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since v0.2.0
 */
public class ThriftApiUtils {

    private final static Logger LOGGER = LoggerFactory.getLogger(ThriftApiUtils.class);

    /**
     * Start Thrift server in another thread (because {@link TServer#serve()} is a blocking
     * operation).
     *
     * @param thriftServer
     * @param threadName
     * @param restartOnError
     *         if {@code true}, automatically restart the server on error/exception
     * @return
     */
    public static Thread startThriftServer(TServer thriftServer, String threadName,
            boolean restartOnError) {
        Thread t = new Thread(threadName) {
            public void run() {
                boolean restart = restartOnError;
                while (restart) {
                    try {
                        restart = false;
                        thriftServer.serve();
                        LOGGER.info(threadName + " stopped.");
                    } catch (Exception e) {
                        LOGGER.error(e.getMessage(), e);
                        restart = restartOnError;
                    }
                }
            }
        };
        t.setDaemon(true);
        t.start();
        return t;
    }

    /**
     * Create a (non-SSL) Thrift server to serve API calls with default client timeout,
     * max-frame-size (64Kb), max-read-buffer-size (8Mb), number of threads and queue size. This
     * method creates a {@link TThreadedSelectorServer} using {@link TFramedTransport}.
     *
     * @param apiRouter
     * @param compactProtocol
     *         if {@code true} use {@link TCompactProtocol}, otherwise use {@link TBinaryProtocol}.
     *         Server and client must use a same protocol.
     * @param listenHost
     *         host/address to listen, empty {@code "0.0.0.0"} will be used
     * @param listenPort
     * @return
     * @throws TTransportException
     */
    public static TServer createThriftServer(ApiRouter apiRouter, boolean compactProtocol,
            String listenHost, int listenPort) throws TTransportException {
        return createThriftServer(apiRouter, compactProtocol, listenHost, listenPort, 0, 64 * 1024,
                8 * 1024 * 1024, 0, 0, 0);
    }

    /**
     * Create a (non-SSL) Thrift server to serve API calls with default max-frame-size (64Kb),
     * max-read-buffer-size (8Mb), number of threads and queue size. This method creates a {@link
     * TThreadedSelectorServer} using {@link TFramedTransport}.
     *
     * @param apiRouter
     * @param compactProtocol
     *         if {@code true} use {@link TCompactProtocol}, otherwise use {@link TBinaryProtocol}.
     *         Server and client must use a same protocol.
     * @param listenHost
     *         host/address to listen, empty {@code "0.0.0.0"} will be used
     * @param listenPort
     * @param clientTimeoutMillisecs
     * @return
     * @throws TTransportException
     */
    public static TServer createThriftServer(ApiRouter apiRouter, boolean compactProtocol,
            String listenHost, int listenPort, int clientTimeoutMillisecs)
            throws TTransportException {
        return createThriftServer(apiRouter, compactProtocol, listenHost, listenPort,
                clientTimeoutMillisecs, 64 * 1024, 8 * 1024 * 1024, 0, 0, 0);
    }

    /**
     * Create a (non-SSL) Thrift server to serve API calls with default number of threads and queue
     * size. This method creates a {@link TThreadedSelectorServer} using {@link TFramedTransport}.
     *
     * @param apiRouter
     * @param compactProtocol
     *         if {@code true} use {@link TCompactProtocol}, otherwise use {@link TBinaryProtocol}.
     *         Server and client must use a same protocol.
     * @param listenHost
     *         host/address to listen, empty {@code "0.0.0.0"} will be used
     * @param listenPort
     * @param clientTimeoutMillisecs
     * @param maxFrameSize
     * @param maxReadBufferSize
     * @return
     * @throws TTransportException
     */
    public static TServer createThriftServer(ApiRouter apiRouter, boolean compactProtocol,
            String listenHost, int listenPort, int clientTimeoutMillisecs, int maxFrameSize,
            int maxReadBufferSize) throws TTransportException {
        return createThriftServer(apiRouter, compactProtocol, listenHost, listenPort,
                clientTimeoutMillisecs, maxFrameSize, maxReadBufferSize, 0, 0, 0);
    }

    /**
     * Create a (non-SSL) Thrift server to serve API calls. This method creates a {@link
     * TThreadedSelectorServer} using {@link TFramedTransport}.
     *
     * @param apiRouter
     * @param compactProtocol
     *         if {@code true} use {@link TCompactProtocol}, otherwise use {@link TBinaryProtocol}.
     *         Server and client must use a same protocol.
     * @param listenHost
     *         host/address to listen, empty {@code "0.0.0.0"} will be used
     * @param listenPort
     * @param clientTimeoutMillisecs
     * @param maxFrameSize
     * @param maxReadBufferSize
     * @param numSelectorThreads
     * @param numWorkerThreads
     * @param queueSizePerThread
     * @return
     * @throws TTransportException
     */
    public static TServer createThriftServer(ApiRouter apiRouter, boolean compactProtocol,
            String listenHost, int listenPort, int clientTimeoutMillisecs, int maxFrameSize,
            int maxReadBufferSize, int numSelectorThreads, int numWorkerThreads,
            int queueSizePerThread) throws TTransportException {
        if (numSelectorThreads < 1) {
            numSelectorThreads = 2;
        }
        if (numWorkerThreads < 1) {
            numWorkerThreads = Runtime.getRuntime().availableProcessors();
        }
        if (queueSizePerThread < 1) {
            queueSizePerThread = 1000;
        }
        InetSocketAddress listenAddr = new InetSocketAddress(
                !StringUtils.isBlank(listenHost) ? listenHost : "0.0.0.0", listenPort);
        TProcessorFactory processorFactory = new TProcessorFactory(
                new TApiService.Processor<TApiService.Iface>(new ApiServiceHandler(apiRouter)));
        TProtocolFactory protocolFactory = compactProtocol
                ? new TCompactProtocol.Factory()
                : new TBinaryProtocol.Factory();
        TNonblockingServerTransport transport = new TNonblockingServerSocket(listenAddr,
                clientTimeoutMillisecs);
        TTransportFactory transportFactory = new TFramedTransport.Factory(maxFrameSize);
        TThreadedSelectorServer.Args args = new TThreadedSelectorServer.Args(transport)
                .processorFactory(processorFactory).protocolFactory(protocolFactory)
                .transportFactory(transportFactory).workerThreads(numWorkerThreads)
                .acceptPolicy(TThreadedSelectorServer.Args.AcceptPolicy.FAIR_ACCEPT)
                .acceptQueueSizePerThread(queueSizePerThread).selectorThreads(numSelectorThreads);
        args.maxReadBufferBytes = maxReadBufferSize;
        TThreadedSelectorServer server = new TThreadedSelectorServer(args);
        return server;
    }

    /**
     * Create a (SSL-enabled) Thrift server to serve API calls, with default client timeout and
     * number of worker threads. This method creates a {@link TThreadPoolServer}.
     *
     * <p>Note: this method read system environment {@code javax.net.ssl.keyStore} for path to
     * keystore file and {@code javax.net.ssl.keyStorePassword} for keystore password.</p>
     *
     * @param apiRouter
     * @param compactProtocol
     *         if {@code true} use {@link TCompactProtocol}, otherwise use {@link TBinaryProtocol}.
     *         Server and client must use a same protocol.
     * @param listenHost
     *         host/address to listen, empty {@code "0.0.0.0"} will be used
     * @param listenPort
     * @return
     * @throws UnknownHostException
     * @throws TTransportException
     */
    public static TServer createThriftServerSsl(ApiRouter apiRouter, boolean compactProtocol,
            String listenHost, int listenPort) throws UnknownHostException, TTransportException {
        return createThriftServerSsl(apiRouter, compactProtocol, listenHost, listenPort, 0, 0);
    }

    /**
     * Create a (SSL-enabled) Thrift server to serve API calls. This method creates a {@link
     * TThreadPoolServer}.
     *
     * <p>Note: this method read system environment {@code javax.net.ssl.keyStore} for path to
     * keystore file and {@code javax.net.ssl.keyStorePassword} for keystore password.</p>
     *
     * @param apiRouter
     * @param compactProtocol
     *         if {@code true} use {@link TCompactProtocol}, otherwise use {@link TBinaryProtocol}.
     *         Server and client must use a same protocol.
     * @param listenHost
     *         host/address to listen, empty {@code "0.0.0.0"} will be used
     * @param listenPort
     * @param clientTimeoutMillisecs
     * @param numWorkerThreads
     * @return
     * @throws UnknownHostException
     * @throws TTransportException
     */
    public static TServer createThriftServerSsl(ApiRouter apiRouter, boolean compactProtocol,
            String listenHost, int listenPort, int clientTimeoutMillisecs, int numWorkerThreads)
            throws UnknownHostException, TTransportException {
        String keystorePath = System.getProperty("javax.net.ssl.keyStore");
        File keystore = keystorePath != null ? new File(keystorePath) : null;
        String keystorePassword = System.getProperty("javax.net.ssl.keyStorePassword");
        return createThriftServerSsl(apiRouter, compactProtocol, listenHost, listenPort,
                clientTimeoutMillisecs, numWorkerThreads, keystore, keystorePassword);
    }

    /**
     * Create a (SSL-enabled) Thrift server to serve API calls, with default client timeout and
     * number of worker threads. This method creates a {@link TThreadPoolServer}.
     *
     * @param apiRouter
     * @param compactProtocol
     *         if {@code true} use {@link TCompactProtocol}, otherwise use {@link TBinaryProtocol}.
     *         Server and client must use a same protocol.
     * @param listenHost
     *         host/address to listen, empty {@code "0.0.0.0"} will be used
     * @param listenPort
     * @param keystore
     * @param keystorePassword
     * @return
     * @throws UnknownHostException
     * @throws TTransportException
     */
    public static TServer createThriftServerSsl(ApiRouter apiRouter, boolean compactProtocol,
            String listenHost, int listenPort, File keystore, String keystorePassword)
            throws UnknownHostException, TTransportException {
        return createThriftServerSsl(apiRouter, compactProtocol, listenHost, listenPort, 0, 0,
                keystore, keystorePassword);
    }

    /**
     * Create a (SSL-enabled) Thrift server to serve API calls. This method creates a {@link
     * TThreadPoolServer}.
     *
     * @param apiRouter
     * @param compactProtocol
     *         if {@code true} use {@link TCompactProtocol}, otherwise use {@link TBinaryProtocol}.
     *         Server and client must use a same protocol.
     * @param listenHost
     *         host/address to listen, empty {@code "0.0.0.0"} will be used
     * @param listenPort
     * @param clientTimeoutMillisecs
     * @param numWorkerThreads
     * @param keystore
     * @param keystorePassword
     * @return
     * @throws UnknownHostException
     * @throws TTransportException
     */
    public static TServer createThriftServerSsl(ApiRouter apiRouter, boolean compactProtocol,
            String listenHost, int listenPort, int clientTimeoutMillisecs, int numWorkerThreads,
            File keystore, String keystorePassword)
            throws UnknownHostException, TTransportException {
        if (numWorkerThreads < 1) {
            numWorkerThreads = Runtime.getRuntime().availableProcessors();
        }
        InetAddress listenAddr = InetAddress
                .getByName(!StringUtils.isBlank(listenHost) ? listenHost : "0.0.0.0");
        TProcessorFactory processorFactory = new TProcessorFactory(
                new TApiService.Processor<TApiService.Iface>(new ApiServiceHandler(apiRouter)));
        TProtocolFactory protocolFactory = compactProtocol
                ? new TCompactProtocol.Factory()
                : new TBinaryProtocol.Factory();
        TSSLTransportFactory.TSSLTransportParameters sslParams = new TSSLTransportFactory.TSSLTransportParameters();
        sslParams.setKeyStore(keystore.getAbsolutePath(), keystorePassword);
        TServerTransport transport = TSSLTransportFactory.
                getServerSocket(listenPort, clientTimeoutMillisecs, listenAddr, sslParams);
        TThreadPoolServer.Args args = new TThreadPoolServer.Args(transport)
                .processorFactory(processorFactory).protocolFactory(protocolFactory)
                .minWorkerThreads(1).maxWorkerThreads(numWorkerThreads);
        TThreadPoolServer server = new TThreadPoolServer(args);
        return server;
    }

    /*----------------------------------------------------------------------*/

    /**
     * Create a (non-SSL) Thrift API client, with default timeout.
     *
     * @param serverHostsAndPorts
     *         server's host and port config, format {@code host:port}
     * @param compactProtocol
     *         if {@code true} use {@link TCompactProtocol}, otherwise use {@link TBinaryProtocol}.
     *         Server and client must use a same protocol.
     * @return
     */
    public static ThriftApiClient createThriftApiClient(String serverHostsAndPorts,
            boolean compactProtocol) throws Exception {
        return createThriftApiClient(serverHostsAndPorts, 0, compactProtocol);
    }

    /**
     * Create a (non-SSL) Thrift API client.
     *
     * @param serverHostsAndPorts
     *         server's host and port config, format {@code host1:port1,host2:port2,...}
     * @param timeoutMs
     * @param compactProtocol
     *         if {@code true} use {@link TCompactProtocol}, otherwise use {@link TBinaryProtocol}.
     *         Server and client must use a same protocol.
     * @return
     */
    public static ThriftApiClient createThriftApiClient(String serverHostsAndPorts, int timeoutMs,
            boolean compactProtocol) throws Exception {
        ThriftApiClient client = new ThriftApiClient();
        client.setServerHostsAndPorts(serverHostsAndPorts).setTimeoutMs(timeoutMs)
                .setCompactProtocol(compactProtocol).disableSslTransport();
        return client.init();
    }

    /**
     * Create a (SSL-enabled) Thrift API client, with default timeout.
     *
     * @param serverHostsAndPorts
     *         server's host and port config, format {@code host:port}
     * @param compactProtocol
     *         if {@code true} use {@link TCompactProtocol}, otherwise use {@link TBinaryProtocol}.
     *         Server and client must use a same protocol.
     * @param trustStorePath
     * @param trustStorePassword
     * @return
     */
    public static ThriftApiClient createThriftApiClientSsl(String serverHostsAndPorts,
            boolean compactProtocol, String trustStorePath, String trustStorePassword)
            throws Exception {
        return createThriftApiClientSsl(serverHostsAndPorts, 0, compactProtocol, trustStorePath,
                trustStorePassword);
    }

    /**
     * Create a (SSL-enabled) Thrift API client.
     *
     * @param serverHostsAndPorts
     *         server's host and port config, format {@code host:port}
     * @param timeoutMs
     * @param compactProtocol
     *         if {@code true} use {@link TCompactProtocol}, otherwise use {@link TBinaryProtocol}.
     *         Server and client must use a same protocol.
     * @return
     */
    public static ThriftApiClient createThriftApiClientSsl(String serverHostsAndPorts,
            int timeoutMs, boolean compactProtocol, String trustStorePath,
            String trustStorePassword) throws Exception {
        ThriftApiClient client = new ThriftApiClient();
        client.enableSslTransport(trustStorePath, trustStorePassword)
                .setServerHostsAndPorts(serverHostsAndPorts).setTimeoutMs(timeoutMs)
                .setCompactProtocol(compactProtocol);
        return client.init();
    }

    /**
     * Create a (non-SSL) Thrift Async API client, with default timeout.
     *
     * @param serverHostsAndPorts
     *         server's host and port config, format {@code host:port}
     * @param compactProtocol
     *         if {@code true} use {@link TCompactProtocol}, otherwise use {@link TBinaryProtocol}.
     *         Server and client must use a same protocol.
     * @return
     */
    public static ThriftAsyncApiClient createThriftAsyncApiClient(String serverHostsAndPorts,
            boolean compactProtocol) throws Exception {
        return createThriftAsyncApiClient(serverHostsAndPorts, 0, compactProtocol);
    }

    /**
     * Create a (non-SSL) Thrift Async API client.
     *
     * @param serverHostsAndPorts
     *         server's host and port config, format {@code host1:port1,host2:port2,...}
     * @param timeoutMs
     * @param compactProtocol
     *         if {@code true} use {@link TCompactProtocol}, otherwise use {@link TBinaryProtocol}.
     *         Server and client must use a same protocol.
     * @return
     */
    public static ThriftAsyncApiClient createThriftAsyncApiClient(String serverHostsAndPorts,
            int timeoutMs, boolean compactProtocol) throws Exception {
        ThriftAsyncApiClient client = new ThriftAsyncApiClient();
        client.setServerHostsAndPorts(serverHostsAndPorts).setTimeoutMs(timeoutMs)
                .setCompactProtocol(compactProtocol).disableSslTransport();
        return client.init();
    }

    /*----------------------------------------------------------------------*/

    /**
     * Build {@link ApiResult} from {@link TApiResult}.
     *
     * @param _apiResult
     * @return
     */
    public static ApiResult toApiResult(TApiResult _apiResult) {
        ApiResult apiResult = new ApiResult(_apiResult.getStatus(), _apiResult.getMessage(),
                decodeToJson(_apiResult.getEncoding(), _apiResult.getResultData()));
        apiResult.setDebugData(decodeToJson(_apiResult.getEncoding(), _apiResult.getDebugData()));
        return apiResult;
    }

    /**
     * Build {@link TApiResult} from {@link ApiResult}.
     *
     * @param _apiResult
     * @param encoding
     * @return
     */
    public static TApiResult buildResponse(ApiResult _apiResult, TDataEncoding encoding) {
        TApiResult apiResult = new TApiResult();
        apiResult.setStatus(_apiResult.getStatus()).setMessage(_apiResult.getMessage());
        if (encoding == null) {
            encoding = TDataEncoding.JSON_STRING;
        }
        apiResult.setEncoding(encoding);
        apiResult
                .setResultData(ThriftApiUtils.encodeFromJson(encoding, _apiResult.getDataAsJson()));
        apiResult.setDebugData(
                ThriftApiUtils.encodeFromJson(encoding, _apiResult.getDebugDataAsJson()));
        return apiResult;
    }

    /**
     * Parse Thrift's parameters.
     *
     * @param _apiParams
     * @return
     */
    public static ApiParams parseParams(TApiParams _apiParams) {
        JsonNode paramNode = ThriftApiUtils.decodeToJson(
                _apiParams.encoding != null ? _apiParams.encoding : TDataEncoding.JSON_STRING,
                _apiParams.getParamsData());
        ApiParams apiParams = new ApiParams(paramNode);
        return apiParams;
    }

    /**
     * Parse auth info.
     *
     * @param _apiAuth
     * @return
     */
    public static ApiAuth parseAuth(TApiAuth _apiAuth) {
        return new ApiAuth(_apiAuth.getAppId(), _apiAuth.getAccessToken());
    }

    /**
     * Encode data from JSON to {@code byte[]}.
     *
     * @param encoding
     * @param jsonData
     * @return
     */
    public static byte[] encodeFromJson(TDataEncoding encoding, JsonNode jsonData) {
        byte[] data =
                jsonData == null || jsonData instanceof NullNode || jsonData instanceof MissingNode
                        ? null
                        : jsonData.toString().getBytes(StandardCharsets.UTF_8);
        if (data == null) {
            return null;
        }
        if (encoding == null) {
            encoding = TDataEncoding.JSON_STRING;
        }
        try {
            switch (encoding) {
            case JSON_DEFAULT:
            case JSON_STRING:
                return data;
            case JSON_GZIP:
                return ApiServiceUtils.toGzip(data);
            default:
                throw new IllegalArgumentException("Unsupported data encoding: " + encoding);
            }
        } catch (Exception e) {
            throw e instanceof RuntimeException ? (RuntimeException) e : new RuntimeException(e);
        }
    }

    /**
     * Decode data from a byte array to JSON.
     *
     * @param encoding
     * @param data
     * @return
     */
    public static JsonNode decodeToJson(TDataEncoding encoding, byte[] data) {
        if (data == null) {
            return null;
        }
        if (encoding == null) {
            encoding = TDataEncoding.JSON_STRING;
        }
        try {
            switch (encoding) {
            case JSON_DEFAULT:
            case JSON_STRING:
                return ApiServiceUtils.fromJsonString(data);
            case JSON_GZIP:
                return ApiServiceUtils.fromJsonGzip(data);
            default:
                throw new IllegalArgumentException("Unsupported data encoding: " + encoding);
            }
        } catch (Exception e) {
            throw e instanceof RuntimeException ? (RuntimeException) e : new RuntimeException(e);
        }
    }
}
