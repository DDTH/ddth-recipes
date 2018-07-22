package com.github.ddth.recipes.qnd.apiservice.grpc;

import com.github.ddth.recipes.apiservice.ApiResult;
import com.github.ddth.recipes.apiservice.ApiRouter;
import com.github.ddth.recipes.apiservice.grpc.GrpcApiUtils;
import io.grpc.Server;

public class QndGrpcServer {
    public static void main(String[] args) throws Exception {
        ApiRouter router = new ApiRouter();
        router.setCatchAllHandler(
                (context, auth, params) -> new ApiResult(ApiResult.STATUS_OK, ApiResult.MSG_OK,
                        params.getAllParams()));
        int numServers = 4;
        Server[] servers = new Server[numServers];
        String listenHost = "127.0.0.1";
        int baseListenPort = 8080;
        for (int i = 0; i < numServers; i++) {
            servers[i] = GrpcApiUtils.createGrpcServer(router, listenHost, baseListenPort + i);
            servers[i].start();
            System.out.println(
                    "Started gRPC server on [" + listenHost + ":" + (baseListenPort + i) + "].");
        }
        for (Server server : servers) {
            server.awaitTermination();
        }
    }
}
