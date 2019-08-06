package com.github.ddth.recipes.qnd.apiservice.grpc;

import com.github.ddth.recipes.apiservice.grpc.GrpcApiClient;
import com.github.ddth.recipes.apiservice.grpc.GrpcUtils;

public class QndGrpcClient extends BaseQndGrpcClient {
    public static void main(String[] args) throws Exception {
        String serverHostsAndPorts = "127.0.0.1:8090";
        try (GrpcApiClient client = GrpcUtils.createGrpcApiClient(serverHostsAndPorts, true)) {
            doTest(client);
        }

        System.out.println();

        try (GrpcApiClient client = GrpcUtils.createGrpcApiClient(serverHostsAndPorts, false)) {
            doTest(client);
        }
    }
}
