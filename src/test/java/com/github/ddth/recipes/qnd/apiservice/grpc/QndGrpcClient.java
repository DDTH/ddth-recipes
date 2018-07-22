package com.github.ddth.recipes.qnd.apiservice.grpc;

import com.github.ddth.recipes.apiservice.grpc.GrpcApiClient;
import com.github.ddth.recipes.apiservice.grpc.GrpcApiUtils;

public class QndGrpcClient extends BaseQndGrpcClient {

    public static void main(String[] args) throws Exception {
        String serverHostsAndPorts = "127.0.0.1:8080";
        try (GrpcApiClient client = GrpcApiUtils.createGrpcApiClient(serverHostsAndPorts, true)) {
            System.out.println("GrpcApiClient with OkHttp...");
            doTest(client);
        }

        System.out.println();

        try (GrpcApiClient client = GrpcApiUtils.createGrpcApiClient(serverHostsAndPorts, false)) {
            System.out.println("GrpcApiClient with Netty...");
            doTest(client);
        }
    }
}
