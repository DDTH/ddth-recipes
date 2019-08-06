package com.github.ddth.recipes.qnd.apiservice.thrift;

import com.github.ddth.recipes.apiservice.thrift.ThriftApiClient;
import com.github.ddth.recipes.apiservice.thrift.ThriftUtils;

import java.io.File;

public class QndThriftClientSsl extends BaseQndThriftClient {
    public static void main(String[] args) throws Exception {
        File truststore = new File("./src/test/resources/keys/client.truststore");
        String truststorePass = "s3cr3t";
        String serverHostsAndPorts = "127.0.0.1:9443";
        try (ThriftApiClient client = ThriftUtils
                .createThriftApiClientSsl(serverHostsAndPorts, true, truststore.getAbsolutePath(), truststorePass)) {
            doTest(client);
        }
    }
}
