package com.github.ddth.recipes.qnd.apiservice.grpc;

import com.github.ddth.recipes.apiservice.grpc.GrpcAsyncApiClient;
import com.github.ddth.recipes.apiservice.grpc.GrpcUtils;

public class QndGrpcAsyncClient extends BaseQndGrpcClient {
    public static void main(String[] args) throws Exception {
        String serverHostsAndPorts = "127.0.0.1:8090";
        try (GrpcAsyncApiClient client = GrpcUtils.createGrpcAsyncApiClient(serverHostsAndPorts, true)) {
            doTest(client);
        }

        System.out.println();

        try (GrpcAsyncApiClient client = GrpcUtils.createGrpcAsyncApiClient(serverHostsAndPorts, false)) {
            doTest(client);
        }
    }
}
