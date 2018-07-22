package com.github.ddth.recipes.qnd.apiservice.grpc;

import com.github.ddth.recipes.apiservice.grpc.GrpcApiClient;
import com.github.ddth.recipes.apiservice.grpc.GrpcApiUtils;
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

            SSLContext sslContext = GrpcApiUtils
                    .buildSSLContextForCertificates(new File(trustCertCollectionFilePath));
            sslSocketFactory = sslContext.getSocketFactory();
        }
        try (GrpcApiClient client = GrpcApiUtils
                .createGrpcApiClientSsl(serverHostsAndPorts, true, null, sslSocketFactory)) {
            System.out.println("GrpcApiClient with OkHttp...");
            try {
                doTest(client);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        System.out.println();

        SslContextBuilder sslContextBuilder = GrpcApiUtils.buildClientSslContextBuilder();
        {
            sslContextBuilder.trustManager(new File(trustCertCollectionFilePath));
        }
        try (GrpcApiClient client = GrpcApiUtils
                .createGrpcApiClientSsl(serverHostsAndPorts, false, sslContextBuilder.build(),
                        null)) {
            System.out.println("GrpcApiClient with Netty...");
            try {
                doTest(client);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
