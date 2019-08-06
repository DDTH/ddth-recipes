package com.github.ddth.recipes.qnd.apiservice.thrift;

import com.github.ddth.recipes.apiservice.thrift.ThriftApiClient;
import com.github.ddth.recipes.apiservice.thrift.ThriftUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedList;
import java.util.List;

public class QndThriftClientMultiThreads extends BaseQndThriftClient {
    public static void main(String[] args) throws Exception {
        int numServers = 4;
        int baseServerPort = 9090;
        String serverHost = "127.0.0.1";
        List<String> hostsAndPortsList = new LinkedList<>();
        for (int i = 0; i < numServers; i++) {
            hostsAndPortsList.add(serverHost + ":" + (baseServerPort + i));
        }
        String hostsAndPorts = StringUtils.join(hostsAndPortsList, ",");

        int numThreads = 16, numCallsPerThreads = 10_000;

        try (ThriftApiClient client = ThriftUtils.createThriftApiClient(hostsAndPorts, true)) {
            doTestMultiThreads(client, numThreads, numCallsPerThreads);
        }
    }
}
