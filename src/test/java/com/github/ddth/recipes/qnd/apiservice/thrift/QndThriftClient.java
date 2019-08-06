package com.github.ddth.recipes.qnd.apiservice.thrift;

import com.github.ddth.recipes.apiservice.thrift.ThriftApiClient;
import com.github.ddth.recipes.apiservice.thrift.ThriftUtils;

public class QndThriftClient extends BaseQndThriftClient {
    public static void main(String[] args) throws Exception {
        String serverHostsAndPorts = "127.0.0.1:9090";
        try (ThriftApiClient client = ThriftUtils.createThriftApiClient(serverHostsAndPorts, true)) {
            doTest(client);
        }
    }
}
