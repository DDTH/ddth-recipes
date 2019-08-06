package com.github.ddth.recipes.qnd.apiservice.thrift;

import com.github.ddth.recipes.apiservice.thrift.ThriftApiClient;
import com.github.ddth.recipes.apiservice.thrift.ThriftUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class QndThriftClientSslMultiThreads extends BaseQndThriftClient {
    public static void main(String[] args) throws Exception {
        int numServers = 4;
        int baseServerPort = 9443;
        String serverHost = "127.0.0.1";
        List<String> hostsAndPortsList = new LinkedList<>();
        for (int i = 0; i < numServers; i++) {
            hostsAndPortsList.add(serverHost + ":" + (baseServerPort + i));
        }
        String hostsAndPorts = StringUtils.join(hostsAndPortsList, ",");
        File truststore = new File("./src/test/resources/keys/client.truststore");
        String truststorePass = "s3cr3t";
        int numThreads = 16, numCallsPerThreads = 10_000;
        try (ThriftApiClient client = ThriftUtils
                .createThriftApiClientSsl(hostsAndPorts, true, truststore.getAbsolutePath(), truststorePass)) {
            doTestMultiThreads(client, numThreads, numCallsPerThreads);
        }
    }
}
