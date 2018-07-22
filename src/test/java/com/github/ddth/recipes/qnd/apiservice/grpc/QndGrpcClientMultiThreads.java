package com.github.ddth.recipes.qnd.apiservice.grpc;

import com.github.ddth.commons.utils.DPathUtils;
import com.github.ddth.recipes.apiservice.ApiResult;
import com.github.ddth.recipes.apiservice.grpc.GrpcApiClient;
import com.github.ddth.recipes.apiservice.grpc.GrpcApiUtils;
import com.github.ddth.recipes.apiservice.grpc.def.PApiServiceProto;
import com.github.ddth.recipes.qnd.apiservice.thrift.BaseQndThriftClient;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class QndGrpcClientMultiThreads extends BaseQndThriftClient {
    public static void main(String[] args) throws Exception {
        int numServers = 4;
        int baseServerPort = 8080;
        String serverHost = "127.0.0.1";
        List<String> hostsAndPortsList = new LinkedList<>();
        for (int i = 0; i < numServers; i++) {
            hostsAndPortsList.add(serverHost + ":" + (baseServerPort + i));
        }
        String hostsAndPorts = StringUtils.join(hostsAndPortsList, ",");
        long numThreads = 8, numCallsPerThreads = 50_000;

        AtomicInteger concurrent = new AtomicInteger(0);
        try (GrpcApiClient client = GrpcApiUtils.createGrpcApiClient(hostsAndPorts, true)) {
            System.out.println("GrpcApiClient - OkHttp");

            PApiServiceProto.PApiAuth apiAuth = PApiServiceProto.PApiAuth.newBuilder()
                    .setAppId("app-id").setAccessToken("access-token").build();

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

        concurrent.set(0);
        try (GrpcApiClient client = GrpcApiUtils.createGrpcApiClient(hostsAndPorts, false)) {
            System.out.println("GrpcApiClient - Netty");

            PApiServiceProto.PApiAuth apiAuth = PApiServiceProto.PApiAuth.newBuilder()
                    .setAppId("app-id").setAccessToken("access-token").build();

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
