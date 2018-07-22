package com.github.ddth.recipes.qnd.apiservice.grpc;

import com.github.ddth.recipes.apiservice.grpc.GrpcApiUtils;
import com.github.ddth.recipes.apiservice.grpc.GrpcAsyncApiClient;

public class QndGrpcAsyncClient extends BaseQndGrpcClient {

    public static void main(String[] args) throws Exception {
        String serverHostsAndPorts = "127.0.0.1:8080";
        try (GrpcAsyncApiClient client = GrpcApiUtils
                .createGrpcAsyncApiClient(serverHostsAndPorts, true)) {
            System.out.println("GrpcAsyncApiClient with OkHttp...");
            doTest(client);
        }

        System.out.println();

        try (GrpcAsyncApiClient client = GrpcApiUtils
                .createGrpcAsyncApiClient(serverHostsAndPorts, false)) {
            System.out.println("GrpcAsyncApiClient with Netty...");
            doTest(client);
        }
    }
}
