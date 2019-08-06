package com.github.ddth.recipes.qnd.apiservice.grpc;

import com.github.ddth.recipes.apiservice.grpc.GrpcApiClient;
import com.github.ddth.recipes.apiservice.grpc.GrpcUtils;
import io.netty.handler.ssl.SslContextBuilder;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import java.io.File;

public class QndGrpcClientSsl extends BaseQndGrpcClient {

    public static void main(String[] args) throws Exception {
        /*
         * hostname must match certificate's CN value.
         * file!
         */
        String serverHostsAndPorts = "localhost:8443";
        String trustCertCollectionFilePath = "./src/test/resources/keys/server-grpc.cer";

        SSLSocketFactory sslSocketFactory;
        //sslSocketFactory = TrustAllTrustManager.TRUST_ALL_SSL_SOCKET_FACTORY;
        //sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        {

            SSLContext sslContext = GrpcUtils.buildSSLContextForCertificates(new File(trustCertCollectionFilePath));
            sslSocketFactory = sslContext.getSocketFactory();
        }
        try (GrpcApiClient client = GrpcUtils
                .createGrpcApiClientSsl(serverHostsAndPorts, true, null, sslSocketFactory)) {
            doTest(client);
        }

        System.out.println();

        SslContextBuilder sslContextBuilder = GrpcUtils.buildClientSslContextBuilder();
        {
            sslContextBuilder.trustManager(new File(trustCertCollectionFilePath));
        }
        try (GrpcApiClient client = GrpcUtils
                .createGrpcApiClientSsl(serverHostsAndPorts, false, sslContextBuilder.build(), null)) {
            doTest(client);
        }
    }
}
