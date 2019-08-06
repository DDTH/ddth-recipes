package com.github.ddth.recipes.qnd.apiservice.grpc;

import com.github.ddth.recipes.apiservice.grpc.GrpcApiClient;
import com.github.ddth.recipes.apiservice.grpc.GrpcUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedList;
import java.util.List;

public class QndGrpcClientMultiThreads extends BaseQndGrpcClient {
    public static void main(String[] args) throws Exception {
        int numServers = 4;
        int baseServerPort = 8090;
        String serverHost = "127.0.0.1";
        List<String> hostsAndPortsList = new LinkedList<>();
        for (int i = 0; i < numServers; i++) {
            hostsAndPortsList.add(serverHost + ":" + (baseServerPort + i));
        }
        String hostsAndPorts = StringUtils.join(hostsAndPortsList, ",");
        int numThreads = 16, numCallsPerThreads = 10_000;

        try (GrpcApiClient client = GrpcUtils.createGrpcApiClient(hostsAndPorts, true)) {
            doTestMultiThreads(client, numThreads, numCallsPerThreads);
        }

        try (GrpcApiClient client = GrpcUtils.createGrpcApiClient(hostsAndPorts, false)) {
            doTestMultiThreads(client, numThreads, numCallsPerThreads);
        }
    }
}
