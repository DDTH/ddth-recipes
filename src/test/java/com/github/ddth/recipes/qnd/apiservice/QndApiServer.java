package com.github.ddth.recipes.qnd.apiservice;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.ddth.commons.utils.SerializationUtils;
import com.github.ddth.recipes.apiservice.*;
import com.github.ddth.recipes.apiservice.auth.AllowAllApiAuthenticator;
import com.github.ddth.recipes.apiservice.grpc.GrpcUtils;
import com.github.ddth.recipes.apiservice.thrift.ThriftUtils;
import io.grpc.Server;
import io.netty.handler.ssl.SslContext;
import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import io.undertow.io.Receiver;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.PathTemplateHandler;
import io.undertow.util.AttachmentKey;
import io.undertow.util.Headers;
import org.apache.thrift.server.TServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

public class QndApiServer {
    static Logger LOGGER = LoggerFactory.getLogger(QndApiServer.class);

    final static String BIND_ADDRESS = "localhost";
    final static int HTTP_PORT = 8080, HTTPS_PORT = 8443;
    final static String HTTP_HEADER_APP_ID = "X-App-Id";
    final static String HTTP_HEADER_ACCESS_TOKEN = "X-Access-Token";
    final static AttachmentKey<ApiAuth> ATTKEY_API_AUTH = AttachmentKey.create(ApiAuth.class);
    final static int THRIFT_PORT = 9090, THRIFT_PORT_SSL = 9443, NUM_THRIFT_SERVERS = 4;
    final static int GRPC_PORT = 8090, GRPC_PORT_SSL = 8443, NUM_GRPC_SERVERS = 4;

    static ApiRouter buildApiRouter() {
        ApiRouter router = new ApiRouter();
        router.setApiAuthenticator(AllowAllApiAuthenticator.instance);
        router.init();

        //        router.setCatchAllHandler((context, auth, params) -> new ApiResult(ApiResult.STATUS_OK,
        //                "Catch-all API is called at " + new Date()));
        router.registerApiHandler("echo",
                (context, auth, params) -> new ApiResult(ApiResult.STATUS_OK, ApiResult.MSG_OK, params.getAllParams()));
        router.registerApiHandler("env",
                (context, auth, params) -> new ApiResult(ApiResult.STATUS_OK, ApiResult.MSG_OK, System.getenv()));

        Runtime.getRuntime().addShutdownHook(new Thread(() -> router.destroy()));
        return router;
    }

    /*------------------------------ HTTP REST API Server ------------------------------*/

    private static class MyPartialBytesCallback implements Receiver.PartialBytesCallback, Receiver.ErrorCallback {
        private ByteArrayOutputStream data = new ByteArrayOutputStream();
        private Exception exception;

        @Override
        public void handle(HttpServerExchange exchange, byte[] message, boolean last) {
            if (exception != null) {
                throw exception instanceof RuntimeException ?
                        (RuntimeException) exception :
                        new RuntimeException(exception);
            }
            try {
                data.write(message);
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }

        @Override
        public void error(HttpServerExchange exchange, IOException e) {
            this.exception = e;
        }
    }

    static class ParseApiAuthHttpHandler implements HttpHandler {
        private final HttpHandler next;

        public ParseApiAuthHttpHandler(HttpHandler next) {
            this.next = next;
        }

        private String getHeader(HttpServerExchange exchange, String key) {
            try {
                return exchange.getRequestHeaders().getFirst(key);
            } catch (NoSuchElementException e) {
                return null;
            }
        }

        @Override
        public void handleRequest(HttpServerExchange exchange) throws Exception {
            String appId = getHeader(exchange, HTTP_HEADER_APP_ID);
            String accessToken = getHeader(exchange, HTTP_HEADER_ACCESS_TOKEN);
            ApiAuth apiAuth = new ApiAuth(appId, accessToken);
            exchange.putAttachment(ATTKEY_API_AUTH, apiAuth);
            next.handleRequest(exchange);
        }
    }

    static ApiParams parseParams(HttpServerExchange exchange) {
        MyPartialBytesCallback callback = new MyPartialBytesCallback();
        exchange.getRequestReceiver().receivePartialBytes(callback, callback);
        JsonNode dataNode = SerializationUtils.readJson(callback.data.toByteArray());
        ApiParams params = new ApiParams(dataNode);
        exchange.getQueryParameters().forEach((k, v) -> {
            List<String> paramList = new LinkedList<>();
            v.forEach(paramList::add);
            params.addParam(k, paramList.size() > 1 ? paramList : v.getFirst());
        });
        return params;
    }

    static HttpHandler buildHttpHandler(ApiRouter apiRouter, String apiName) {
        HttpHandler next = exchange -> {
            ApiResult apiResult;
            ApiContext context = ApiContext.newContext("HTTP", apiName);
            ApiAuth auth = exchange.getAttachment(ATTKEY_API_AUTH);
            try {
                apiResult = apiRouter.callApi(context, auth, parseParams(exchange));
            } catch (Exception e) {
                apiResult = new ApiResult(500, e.getMessage());
            }
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(apiResult.asJson().toString(), StandardCharsets.UTF_8);
        };
        return new ParseApiAuthHttpHandler(next);
    }

    static void buildAndStartUndertowServer(ApiRouter apiRouter) throws Exception {
        Undertow.Builder builder = Undertow.builder().setServerOption(UndertowOptions.ENABLE_HTTP2, true);
        {
            builder.addHttpListener(HTTP_PORT, BIND_ADDRESS);
            System.out.println("Started REST/HTTPS on [" + BIND_ADDRESS + ":" + HTTP_PORT + "].");
        }

        PathTemplateHandler rootHandler = new PathTemplateHandler();
        rootHandler.add("/api/echo", buildHttpHandler(apiRouter, "echo"));
        rootHandler.add("/api/env", buildHttpHandler(apiRouter, "env"));
        //rootHandler.add("/api/*", buildHttpHandler(apiRouter, "no-api"));
        rootHandler.add("/*", buildHttpHandler(apiRouter, "no-api"));

        Undertow undertowServer = builder.setHandler(rootHandler).build();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> undertowServer.stop()));
        undertowServer.start();
    }

    /*------------------------------ HTTP REST API Server ------------------------------*/

    /*------------------------------ THRIFT API Server ------------------------------*/
    static void buildAndStartThriftServer(ApiRouter apiRouter) throws Exception {
        for (int i = 0; i < NUM_THRIFT_SERVERS; i++) {
            int port = THRIFT_PORT + i;
            TServer server = ThriftUtils.createThriftServer(apiRouter, true, BIND_ADDRESS, port);
            Thread thread = ThriftUtils.startThriftServer(server, "TServer-" + i, true);
            System.out.println("Started TServer on [" + BIND_ADDRESS + ":" + port + "]: " + thread);
        }
        File keystore = new File("./src/test/resources/keys/server.keystore");
        String keystorePass = "s3cr3t";
        for (int i = 0; i < NUM_THRIFT_SERVERS; i++) {
            int port = THRIFT_PORT_SSL + i;
            TServer server = ThriftUtils
                    .createThriftServerSsl(apiRouter, true, BIND_ADDRESS, port, keystore, keystorePass);
            Thread thread = ThriftUtils.startThriftServer(server, "TServerSSL-" + i, true);
            System.out.println("Started TServerSSL on [" + BIND_ADDRESS + ":" + port + "]: " + thread);
        }
    }
    /*------------------------------ THRIFT API Server ------------------------------*/

    /*------------------------------ gRPC API Server ------------------------------*/
    static void buildAndStartGrpcServer(ApiRouter apiRouter) throws Exception {
        for (int i = 0; i < NUM_GRPC_SERVERS; i++) {
            int port = GRPC_PORT + i;
            Server server = GrpcUtils.createGrpcServer(apiRouter, BIND_ADDRESS, port);
            server.start();
            System.out.println("Started gRPC on [" + BIND_ADDRESS + ":" + port + "]");
        }

        SslContext sslContext;
        {
            String certChainFilePath = "./src/test/resources/keys/server-grpc.cer";
            String privateKeyFilePath = "./src/test/resources/keys/server-grpc-nodes.key";
            String keyFilePassword = "";
            sslContext = GrpcUtils
                    .buildServerSslContext(new File(certChainFilePath), new File(privateKeyFilePath), keyFilePassword);
        }
        for (int i = 0; i < NUM_GRPC_SERVERS; i++) {
            int port = GRPC_PORT_SSL + i;
            Server server = GrpcUtils.createGrpcServerSsl(apiRouter, BIND_ADDRESS, port, sslContext);
            server.start();
            System.out.println("Started SSL-gRPC on [" + BIND_ADDRESS + ":" + port + "]");
        }
    }
    /*------------------------------ THRIFT API Server ------------------------------*/

    public static void main(String[] args) throws Exception {
        ApiRouter apiRouter = buildApiRouter();

        buildAndStartUndertowServer(apiRouter);
        buildAndStartThriftServer(apiRouter);
        buildAndStartGrpcServer(apiRouter);
    }
}
