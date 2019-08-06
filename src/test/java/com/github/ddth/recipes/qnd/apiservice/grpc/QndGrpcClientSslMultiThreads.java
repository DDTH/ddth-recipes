package com.github.ddth.recipes.qnd.apiservice.grpc;

import com.github.ddth.recipes.apiservice.grpc.GrpcApiClient;
import com.github.ddth.recipes.apiservice.grpc.GrpcUtils;
import io.netty.handler.ssl.SslContextBuilder;
import org.apache.commons.lang3.StringUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class QndGrpcClientSslMultiThreads extends BaseQndGrpcClient {
    public static void main(String[] args) throws Exception {
        int numServers = 4;
        int baseServerPort = 8443;
        String serverHost = "localhost";
        List<String> hostsAndPortsList = new LinkedList<>();
        for (int i = 0; i < numServers; i++) {
            hostsAndPortsList.add(serverHost + ":" + (baseServerPort + i));
        }
        String hostsAndPorts = StringUtils.join(hostsAndPortsList, ",");
        int numThreads = 16, numCallsPerThreads = 10_000;

        String trustCertCollectionFilePath = "./src/test/resources/keys/server-grpc.cer";
        SSLSocketFactory sslSocketFactory;
        {

            SSLContext sslContext = GrpcUtils.buildSSLContextForCertificates(new File(trustCertCollectionFilePath));
            sslSocketFactory = sslContext.getSocketFactory();
        }
        try (GrpcApiClient client = GrpcUtils.createGrpcApiClientSsl(hostsAndPorts, true, null, sslSocketFactory)) {
            doTestMultiThreads(client, numThreads, numCallsPerThreads);
        }

        SslContextBuilder sslContextBuilder = GrpcUtils.buildClientSslContextBuilder();
        {
            sslContextBuilder.trustManager(new File(trustCertCollectionFilePath));
        }
        try (GrpcApiClient client = GrpcUtils
                .createGrpcApiClientSsl(hostsAndPorts, false, sslContextBuilder.build(), null)) {
            doTestMultiThreads(client, numThreads, numCallsPerThreads);
        }
    }
}
