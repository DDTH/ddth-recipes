package com.github.ddth.recipes.qnd.apiservice.grpc;

import com.github.ddth.recipes.apiservice.ApiResult;
import com.github.ddth.recipes.apiservice.ApiRouter;
import com.github.ddth.recipes.apiservice.grpc.GrpcApiUtils;
import io.grpc.Server;
import io.netty.handler.ssl.SslContext;

import java.io.File;

public class QndGrpcServerSsl {
    public static void main(String[] args) throws Exception {
        String listenHost = "127.0.0.1";
        int baseListenPort = 8443;

        SslContext sslContext;
        {
            String certChainFilePath = "./src/test/resources/keys/server-grpc.cer";
            String privateKeyFilePath = "./src/test/resources/keys/server-grpc-nodes.key";
            String keyFilePassword = "";
            sslContext = GrpcApiUtils.buildServerSslContext(new File(certChainFilePath),
                    new File(privateKeyFilePath), keyFilePassword);
        }

        {
            String certChainFilePath = "/Users/thanhnb/Workspace/GHN/ghn.vn/cert.pem";
            String privateKeyFilePath = "/Users/thanhnb/Workspace/GHN/ghn.vn/key.pem";
            String keyFilePassword = "";
            sslContext = GrpcApiUtils.buildServerSslContext(new File(certChainFilePath),
                    new File(privateKeyFilePath), keyFilePassword);
        }

        ApiRouter router = new ApiRouter();
        router.setCatchAllHandler(
                (context, auth, params) -> new ApiResult(ApiResult.STATUS_OK, ApiResult.MSG_OK,
                        params.getAllParams()));
        int numServers = 4;
        Server[] servers = new Server[numServers];
        for (int i = 0; i < numServers; i++) {
            servers[i] = GrpcApiUtils
                    .createGrpcServerSsl(router, listenHost, baseListenPort + i, sslContext);
            servers[i].start();
            System.out.println(
                    "Started SSL gRPC server on [" + listenHost + ":" + (baseListenPort + i)
                            + "].");
        }
        for (Server server : servers) {
            server.awaitTermination();
        }
    }
}
