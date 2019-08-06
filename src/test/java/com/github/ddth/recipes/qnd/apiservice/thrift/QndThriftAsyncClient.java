package com.github.ddth.recipes.qnd.apiservice.thrift;

import com.github.ddth.recipes.apiservice.thrift.ThriftAsyncApiClient;
import com.github.ddth.recipes.apiservice.thrift.ThriftUtils;

public class QndThriftAsyncClient extends BaseQndThriftClient {
    public static void main(String[] args) throws Exception {
        String hostsAndPorts = "127.0.0.1:9090";
        try (ThriftAsyncApiClient asyncClient = ThriftUtils.createThriftAsyncApiClient(hostsAndPorts, true)) {
            doTest(asyncClient);
        }
    }
}
