package com.github.ddth.recipes.qnd.apiservice.rest;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.github.ddth.commons.jsonrpc.HttpJsonRpcClient;
import com.github.ddth.commons.jsonrpc.RequestResponse;
import com.github.ddth.commons.utils.DPathUtils;
import com.github.ddth.commons.utils.MapUtils;
import com.github.ddth.commons.utils.SerializationUtils;
import com.github.ddth.recipes.qnd.apiservice.thrift.BaseQndThriftClient;

public class QndRestApiClientMultiThreads extends BaseQndThriftClient {
    public static void main(String[] args) throws Exception {
        long numThreads = 8, numCallsPerThreads = 10_000;

        AtomicInteger concurrent = new AtomicInteger(0);
        try (HttpJsonRpcClient client = new HttpJsonRpcClient()) {
            client.init();

            AtomicLong counter = new AtomicLong(0);
            final Map<String, Object> headers = MapUtils.createMap("X-App-Id", "app-id",
                    "X-Access-Token", "access-token");

            Thread[] threads = new Thread[(int) numThreads];
            long t = System.currentTimeMillis();
            for (int i = 0; i < numThreads; i++) {
                threads[i] = new Thread(() -> {
                    try {
                        for (int j = 0; j < numCallsPerThreads; j++) {
                            counter.incrementAndGet();
                            RequestResponse rr;

                            rr = client.doGet("http://localhost:8080/api/echo", headers, null);
                            rr = client.doPost("http://localhost:8080/api/echo", headers, null,
                                    SerializationUtils
                                            .fromJsonString("{\"a\":1,\"b\":2.0,\"c\":true}"));
                            int c = DPathUtils
                                    .getValueOptional(rr.getResponseJson(), "debug.c", Integer.class)
                                    .orElse(0);
                            int _c = concurrent.get();
                            if (c > _c) {
                                concurrent.compareAndSet(_c, c);
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
