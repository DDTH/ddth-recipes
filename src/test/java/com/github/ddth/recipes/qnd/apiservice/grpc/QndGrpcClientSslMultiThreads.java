package com.github.ddth.recipes.qnd.apiservice.grpc;

import com.github.ddth.commons.utils.DPathUtils;
import com.github.ddth.recipes.apiservice.ApiResult;
import com.github.ddth.recipes.apiservice.grpc.GrpcApiClient;
import com.github.ddth.recipes.apiservice.grpc.GrpcApiUtils;
import com.github.ddth.recipes.apiservice.grpc.def.PApiServiceProto;
import com.github.ddth.recipes.qnd.apiservice.thrift.BaseQndThriftClient;
import io.netty.handler.ssl.SslContextBuilder;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class QndGrpcClientSslMultiThreads extends BaseQndThriftClient {
    public static void main(String[] args) throws Exception {
        int numServers = 4;
        int baseServerPort = 8443;
        String serverHost = "local.ghn.vn";
        List<String> hostsAndPortsList = new LinkedList<>();
        for (int i = 0; i < numServers; i++) {
            hostsAndPortsList.add(serverHost + ":" + (baseServerPort + i));
        }
        String hostsAndPorts = StringUtils.join(hostsAndPortsList, ",");
        AtomicInteger concurrent = new AtomicInteger(0);
        SslContextBuilder sslContextBuilder = GrpcApiUtils.buildClientSslContextBuilder();
        {
            String trustCertCollectionFilePath = "/Users/thanhnb/Workspace/GHN/ghn.vn/cert.pem";
            sslContextBuilder.trustManager(new File(trustCertCollectionFilePath));
        }
        try (GrpcApiClient client = GrpcApiUtils
                .createGrpcApiClientSsl(hostsAndPorts, false, sslContextBuilder.build(), null)) {
            PApiServiceProto.PApiAuth apiAuth = PApiServiceProto.PApiAuth.newBuilder()
                    .setAppId("app-id").setAccessToken("access-token").build();

            long numThreads = 8, numCallsPerThreads = 10_000;
            AtomicLong counter = new AtomicLong(0);

            Thread[] threads = new Thread[(int) numThreads];
            long t = System.currentTimeMillis();
            for (int i = 0; i < numThreads; i++) {
                threads[i] = new Thread(() -> {
                    try {
                        for (int j = 0; j < numCallsPerThreads; j++) {
                            counter.incrementAndGet();
                            PApiServiceProto.PApiResult result1 = client.check(apiAuth);
                            ApiResult apiResult1 = GrpcApiUtils.toApiResult(result1);
                            int d1 = DPathUtils
                                    .getValueOptional(apiResult1.getDebugDataAsJson(), "d",
                                            Integer.class).orElse(0);
                            int _d1 = concurrent.get();
                            if (d1 > _d1) {
                                concurrent.compareAndSet(_d1, d1);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
            for (Thread thread : threads) {
                thread.start();
            }
            for (Thread thread : threads) {
                thread.join();
            }
            long d = System.currentTimeMillis() - t;
            long numApiCalls = counter.get();
            long r = Math.round(numApiCalls * 1000.0 / d);
            System.out.println("Finished [" + numApiCalls + "] in " + d + "ms, rate " + r
                    + " calls/sec, concurrency " + concurrent);
        }
    }
}
