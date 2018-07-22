package com.github.ddth.recipes.qnd.apiservice.thrift;

import com.github.ddth.commons.utils.DPathUtils;
import com.github.ddth.commons.utils.JacksonUtils;
import com.github.ddth.commons.utils.MapUtils;
import com.github.ddth.recipes.apiservice.ApiResult;
import com.github.ddth.recipes.apiservice.thrift.ThriftApiClient;
import com.github.ddth.recipes.apiservice.thrift.ThriftApiUtils;
import com.github.ddth.recipes.apiservice.thrift.def.TApiAuth;
import com.github.ddth.recipes.apiservice.thrift.def.TApiParams;
import com.github.ddth.recipes.apiservice.thrift.def.TApiResult;
import com.github.ddth.recipes.apiservice.thrift.def.TDataEncoding;
import org.apache.commons.lang3.StringUtils;
import org.apache.thrift.TException;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class QndThriftClientMultiThreads extends BaseQndThriftClient {
    public static void main(String[] args) throws Exception {
        int numServers = 4;
        int baseServerPort = 9090;
        String serverHost = "127.0.0.1";
        List<String> hostsAndPortsList = new LinkedList<>();
        for (int i = 0; i < numServers; i++) {
            hostsAndPortsList.add(serverHost + ":" + (baseServerPort + i));
        }
        String hostsAndPorts = StringUtils.join(hostsAndPortsList, ",");

        AtomicInteger concurrent = new AtomicInteger(0);
        try (ThriftApiClient client = ThriftApiUtils.createThriftApiClient(hostsAndPorts, true)) {
            TApiAuth apiAuth = new TApiAuth();
            apiAuth.setAppId("app-id").setAccessToken("access-token");

            long numThreads = 4, numCallsPerThreads = 10_000;
            AtomicLong counter = new AtomicLong(0);

            Map<Object, Object> data = MapUtils
                    .createMap("t", System.currentTimeMillis(), "n", "Thanh Nguyen", "e",
                            "btnguyen2k@gmail.com", "system", System.getProperties(), "env",
                            System.getenv());
            TDataEncoding sendEncoding = TDataEncoding.JSON_STRING;
            TDataEncoding returnEncoding = TDataEncoding.JSON_DEFAULT;
            TApiParams apiParams = new TApiParams().setEncoding(sendEncoding)
                    .setExpectedReturnEncoding(returnEncoding).setParamsData(
                            ThriftApiUtils.encodeFromJson(sendEncoding, JacksonUtils.toJson(data)));

            Thread[] threads = new Thread[(int) numThreads];
            long t = System.currentTimeMillis();
            for (int i = 0; i < numThreads; i++) {
                threads[i] = new Thread(() -> {
                    try {
                        for (int j = 0; j < numCallsPerThreads; j++) {
                            TApiResult result2 = client.call("echo", apiAuth, apiParams);
                            ApiResult apiResult2 = ThriftApiUtils.toApiResult(result2);
                            int d2 = DPathUtils
                                    .getValueOptional(apiResult2.getDebugDataAsJson(), "d",
                                            Integer.class).orElse(0);
                            int _d2 = concurrent.get();
                            if (d2 > _d2) {
                                concurrent.compareAndSet(_d2, d2);
                            }
                            counter.incrementAndGet();
                        }
                    } catch (TException e) {
                        throw new RuntimeException(e);
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
