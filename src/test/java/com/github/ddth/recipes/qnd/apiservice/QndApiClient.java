
package com.github.ddth.recipes.qnd.apiservice;

import java.io.File;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

import com.fasterxml.jackson.databind.node.NullNode;
import com.github.ddth.commons.jsonrpc.HttpJsonRpcClient;
import com.github.ddth.commons.jsonrpc.RequestResponse;
import com.github.ddth.commons.utils.SerializationUtils;
import com.github.ddth.recipes.apiservice.grpc.GrpcApiClient;
import com.github.ddth.recipes.apiservice.grpc.GrpcApiUtils;
import com.github.ddth.recipes.apiservice.grpc.def.PApiServiceProto.PApiResult;
import com.github.ddth.recipes.apiservice.thrift.ThriftApiClient;
import com.github.ddth.recipes.apiservice.thrift.ThriftApiUtils;
import com.github.ddth.recipes.apiservice.thrift.def.TApiResult;
import com.google.protobuf.Empty;

import io.netty.handler.ssl.SslContextBuilder;

public class QndApiClient {

    public static void main(String[] args) throws Exception {
        try (HttpJsonRpcClient client = new HttpJsonRpcClient()) {
            client.init();
            System.out.println(client);

            RequestResponse rr;

            rr = client.doGet("http://localhost:9000/samplesApi/api/info", null, null);
            System.out.println(rr.getResponseJson());

            rr = client.doPost("http://localhost:9000/samplesApi/api/info", null, null,
                    NullNode.instance);
            System.out.println(rr.getResponseJson());

            rr = client.doGet("http://localhost:9000/samplesApi/api/echo", null, null);
            System.out.println(rr.getResponseJson());

            rr = client.doPost("http://localhost:9000/samplesApi/api/echo", null, null,
                    SerializationUtils.fromJsonString("{\"a\":1,\"b\":2.0,\"c\":true}"));
            System.out.println(rr.getResponseJson());
        }

        try (ThriftApiClient client = ThriftApiUtils.createThriftApiClient("127.0.0.1:9090",
                true)) {
            System.out.println();
            System.out.println(client);

            client.ping();
            TApiResult result;

            result = client.call("info", "", "", null);
            System.out
                    .println(SerializationUtils.fromJsonString(new String(result.getResultData())));

            result = client.call("echo", "", "",
                    SerializationUtils.fromJsonString("{\"a\":1,\"b\":2.0,\"c\":true}"));
            System.out.println(new String(result.getResultData()));
        }

        try (ThriftApiClient client = ThriftApiUtils.createThriftApiClientSsl("127.0.0.1:9093",
                true,
                "/Users/thanhnb/Workspace/btnguyen2k/play-java-seed.g8/conf/keys/client.truststore",
                "pl2yt3mpl2t3")) {
            System.out.println();
            System.out.println(client);

            client.ping();
            TApiResult result;

            result = client.call("info", "", "", null);
            System.out
                    .println(SerializationUtils.fromJsonString(new String(result.getResultData())));

            result = client.call("echo", "", "",
                    SerializationUtils.fromJsonString("{\"a\":1,\"b\":2.0,\"c\":true}"));
            System.out.println(new String(result.getResultData()));
        }

        try (GrpcApiClient client = GrpcApiUtils.createGrpcApiClient("127.0.0.1:9095")) {
            System.out.println();
            System.out.println(client);

            client.ping(Empty.getDefaultInstance());
            PApiResult result;

            result = client.call("info", "", "", null);
            System.out.println(SerializationUtils
                    .fromJsonString(new String(result.getResultData().toByteArray())));

            result = client.call("echo", "", "",
                    SerializationUtils.fromJsonString("{\"a\":1,\"b\":2.0,\"c\":true}"));
            System.out.println(new String(result.getResultData().toByteArray()));
        }

        String trustCertCollectionFilePath = "/Users/thanhnb/Workspace/btnguyen2k/play-java-seed.g8/conf/keys/server-grpc.cer";
//        SSLSocketFactory sslSocketFactory;
//        {
//            SSLContext sslContext = GrpcApiUtils
//                    .buildSSLContextForCertificates(new File(trustCertCollectionFilePath));
//            sslSocketFactory = sslContext.getSocketFactory();
//        }
//        try (GrpcApiClient client = GrpcApiUtils.createGrpcApiClientSsl("localhost:9098", true,
//                null, sslSocketFactory)) {
//            System.out.println();
//            System.out.println(client);
//
//            client.ping(Empty.getDefaultInstance());
//            PApiResult result;
//
//            result = client.call("info", "", "", null);
//            System.out.println(SerializationUtils
//                    .fromJsonString(new String(result.getResultData().toByteArray())));
//
//            result = client.call("echo", "", "",
//                    SerializationUtils.fromJsonString("{\"a\":1,\"b\":2.0,\"c\":true}"));
//            System.out.println(new String(result.getResultData().toByteArray()));
//        }

        SslContextBuilder sslContextBuilder = GrpcApiUtils.buildClientSslContextBuilder();
        {
            sslContextBuilder.trustManager(new File(trustCertCollectionFilePath));
        }
        try (GrpcApiClient client = GrpcApiUtils.createGrpcApiClientSsl("localhost:9098", false,
                sslContextBuilder.build(), null)) {
            System.out.println();
            System.out.println(client);

            client.ping(Empty.getDefaultInstance());
            PApiResult result;

            result = client.call("info", "", "", null);
            System.out.println(SerializationUtils
                    .fromJsonString(new String(result.getResultData().toByteArray())));

            result = client.call("echo", "", "",
                    SerializationUtils.fromJsonString("{\"a\":1,\"b\":2.0,\"c\":true}"));
            System.out.println(new String(result.getResultData().toByteArray()));
        }
    }

}
