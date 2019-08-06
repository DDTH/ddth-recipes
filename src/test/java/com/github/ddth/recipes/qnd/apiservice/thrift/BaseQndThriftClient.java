package com.github.ddth.recipes.qnd.apiservice.thrift;

import com.github.ddth.commons.utils.DPathUtils;
import com.github.ddth.commons.utils.JacksonUtils;
import com.github.ddth.commons.utils.MapUtils;
import com.github.ddth.recipes.apiservice.ApiResult;
import com.github.ddth.recipes.apiservice.thrift.ThriftApiClient;
import com.github.ddth.recipes.apiservice.thrift.ThriftAsyncApiClient;
import com.github.ddth.recipes.apiservice.thrift.ThriftUtils;
import com.github.ddth.recipes.apiservice.thrift.def.TApiAuth;
import com.github.ddth.recipes.apiservice.thrift.def.TApiParams;
import com.github.ddth.recipes.apiservice.thrift.def.TApiResult;
import com.github.ddth.recipes.apiservice.thrift.def.TDataEncoding;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class BaseQndThriftClient {
    protected static String toString(TApiResult _result) throws TException {
        ApiResult result = ThriftUtils.toApiResult(_result);
        StringBuilder sb = new StringBuilder(
                "=== Size: " + com.github.ddth.commons.utils.ThriftUtils.toBytes(_result).length).append("\n");
        ToStringBuilder tsb = new ToStringBuilder(result, ToStringStyle.SHORT_PREFIX_STYLE);
        tsb.append("status", result.getStatus());
        tsb.append("message", result.getMessage());
        tsb.append("encoding", _result.encoding);
        tsb.append("data", result.getDataAsJson());
        tsb.append("debug", result.getDebugDataAsJson());
        return sb.append(tsb).append("\nConcurrency: ").append(result.getDebugDataAsJson().get("c")).toString();
    }

    protected static void doTest(ThriftApiClient client) throws TException {
        client.ping();

        String appId = "app-id", accessToken = "access-token";
        Map<Object, Object> data = MapUtils
                .createMap("t", System.currentTimeMillis(), "n", "Thanh Nguyen", "e", "btnguyen2k@gmail.com", "system",
                        System.getProperties(), "env", System.getenv());
        TApiAuth apiAuth = new TApiAuth().setAppId(appId).setAccessToken(accessToken);

        {
            System.out.println("check()      : " + toString(client.check(apiAuth)));
            System.out.println("check()      : " + toString(client.check(appId, accessToken)));
        }
        {
            TDataEncoding sendEncoding = TDataEncoding.JSON_STRING;
            TDataEncoding returnEncoding = TDataEncoding.JSON_DEFAULT;
            TApiParams apiParams = new TApiParams().setEncoding(sendEncoding).setExpectedReturnEncoding(returnEncoding)
                    .setParamsData(ThriftUtils.encodeFromJson(sendEncoding, JacksonUtils.toJson(data)));
            System.out.println("callApi(echo)/json: " + toString(client.call("echo", apiAuth, apiParams)));
            System.out.println(
                    "callApi(echo)/json: " + toString(client.call("echo", appId, accessToken, sendEncoding, data)));
        }
        {
            TDataEncoding sendEncoding = TDataEncoding.JSON_GZIP;
            TDataEncoding returnEncoding = TDataEncoding.JSON_DEFAULT;
            TApiParams apiParams = new TApiParams().setEncoding(sendEncoding).setExpectedReturnEncoding(returnEncoding)
                    .setParamsData(ThriftUtils.encodeFromJson(sendEncoding, JacksonUtils.toJson(data)));
            System.out.println("callApi(echo)/gzip: " + toString(client.call("echo", apiAuth, apiParams)));
            System.out.println(
                    "callApi(echo)/gzip: " + toString(client.call("echo", appId, accessToken, sendEncoding, data)));
        }
    }

    private static class MyAsyncMethodCallback implements AsyncMethodCallback<TApiResult> {
        private String methodName;

        public MyAsyncMethodCallback(String methodName) {
            this.methodName = methodName;
        }

        @Override
        public void onComplete(TApiResult tApiResult) {
            try {
                System.out.println("COMPLETE - " + methodName + ": " + QndThriftAsyncClient.toString(tApiResult));
            } catch (TException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(Exception e) {
            System.err.println("ERROR - " + methodName + ": " + e);
        }
    }

    protected static void doTest(ThriftAsyncApiClient client) throws TException, InterruptedException {
        client.ping(new AsyncMethodCallback<>() {
            @Override
            public void onComplete(Void aVoid) {
                System.out.println("COMPLETE - ping: " + aVoid);
            }

            @Override
            public void onError(Exception e) {
                System.err.println("ERROR - ping: " + e);
            }
        });

        String appId = "app-id", accessToken = "access-token";
        Map<Object, Object> data = MapUtils
                .createMap("t", System.currentTimeMillis(), "n", "Thanh Nguyen", "e", "btnguyen2k@gmail.com", "system",
                        System.getProperties(), "env", System.getenv());
        TApiAuth apiAuth = new TApiAuth().setAppId(appId).setAccessToken(accessToken);

        client.check(apiAuth, new MyAsyncMethodCallback("check"));
        client.check(appId, accessToken, new MyAsyncMethodCallback("check"));
        {
            TDataEncoding sendEncoding = TDataEncoding.JSON_STRING;
            TDataEncoding returnEncoding = TDataEncoding.JSON_DEFAULT;
            TApiParams apiParams = new TApiParams().setEncoding(sendEncoding).setExpectedReturnEncoding(returnEncoding)
                    .setParamsData(ThriftUtils.encodeFromJson(sendEncoding, JacksonUtils.toJson(data)));
            client.call("echo", apiAuth, apiParams, new MyAsyncMethodCallback("call(echo)"));
            client.call("echo", appId, accessToken, sendEncoding, data, new MyAsyncMethodCallback("call" + "(echo)"));
        }
        {
            TDataEncoding sendEncoding = TDataEncoding.JSON_GZIP;
            TDataEncoding returnEncoding = TDataEncoding.JSON_DEFAULT;
            TApiParams apiParams = new TApiParams().setEncoding(sendEncoding).setExpectedReturnEncoding(returnEncoding)
                    .setParamsData(ThriftUtils.encodeFromJson(sendEncoding, JacksonUtils.toJson(data)));
            client.call("echo", apiAuth, apiParams, new MyAsyncMethodCallback("call(echo)"));
            client.call("echo", appId, accessToken, sendEncoding, data, new MyAsyncMethodCallback("call" + "(echo)"));
        }

        while (client.hasPendingTasks()) {
            Thread.sleep(1);
        }
    }

    protected static void doTestMultiThreads(ThriftApiClient client, int numThreads, int numCallsPerThreads)
            throws InterruptedException {
        String appId = "app-id", accessToken = "access-token";
        TApiAuth apiAuth = new TApiAuth().setAppId(appId).setAccessToken(accessToken);

        Map<Object, Object> data = MapUtils
                .createMap("t", System.currentTimeMillis(), "n", "Thanh Nguyen", "e", "btnguyen2k@gmail.com", "system",
                        System.getProperties(), "env", System.getenv());
        TDataEncoding sendEncoding = TDataEncoding.JSON_STRING;
        TDataEncoding returnEncoding = TDataEncoding.JSON_DEFAULT;
        TApiParams apiParams = new TApiParams().setEncoding(sendEncoding).setExpectedReturnEncoding(returnEncoding)
                .setParamsData(ThriftUtils.encodeFromJson(sendEncoding, JacksonUtils.toJson(data)));

        AtomicLong counter = new AtomicLong(0);
        AtomicInteger concurrent = new AtomicInteger(0);

        Thread[] threads = new Thread[numThreads];
        long t = System.currentTimeMillis();
        for (int i = 0; i < numThreads; i++) {
            threads[i] = new Thread(() -> {
                try {
                    for (int j = 0; j < numCallsPerThreads; j++) {
                        TApiResult result2 = client.call("echo", apiAuth, apiParams);
                        ApiResult apiResult2 = ThriftUtils.toApiResult(result2);
                        int d2 = DPathUtils.getValueOptional(apiResult2.getDebugDataAsJson(), "c", Integer.class)
                                .orElse(0);
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
        System.out.println(
                "Finished [" + numApiCalls + "] in " + d + "ms, rate " + r + " calls/sec, concurrency " + concurrent);
    }

    protected static void doTestMultiThreads(ThriftAsyncApiClient client, int numThreads, int numCallsPerThreads)
            throws InterruptedException {
        String appId = "app-id", accessToken = "access-token";
        TApiAuth apiAuth = new TApiAuth().setAppId(appId).setAccessToken(accessToken);

        Map<Object, Object> data = MapUtils
                .createMap("t", System.currentTimeMillis(), "n", "Thanh Nguyen", "e", "btnguyen2k@gmail.com", "system",
                        System.getProperties(), "env", System.getenv());
        TDataEncoding sendEncoding = TDataEncoding.JSON_STRING;
        TDataEncoding returnEncoding = TDataEncoding.JSON_DEFAULT;
        TApiParams apiParams = new TApiParams().setEncoding(sendEncoding).setExpectedReturnEncoding(returnEncoding)
                .setParamsData(ThriftUtils.encodeFromJson(sendEncoding, JacksonUtils.toJson(data)));

        AtomicLong counter = new AtomicLong(0);
        AtomicInteger concurrent = new AtomicInteger(0);

        Thread[] threads = new Thread[numThreads];
        long t = System.currentTimeMillis();
        for (int i = 0; i < numThreads; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < numCallsPerThreads; j++) {
                    client.call("echo", apiAuth, apiParams, new AsyncMethodCallback<>() {
                        @Override
                        public void onComplete(TApiResult tApiResult) {
                            ApiResult apiResult2 = ThriftUtils.toApiResult(tApiResult);
                            int d2 = DPathUtils.getValueOptional(apiResult2.getDebugDataAsJson(), "c", Integer.class)
                                    .orElse(0);
                            int _d2 = concurrent.get();
                            if (d2 > _d2) {
                                concurrent.compareAndSet(_d2, d2);
                            }
                            counter.incrementAndGet();
                        }

                        @Override
                        public void onError(Exception e) {
                            System.out.println(e.getMessage());
                            counter.incrementAndGet();
                        }
                    });
                }
            });
        }
        for (Thread thread : threads) {
            thread.start();
        }
        for (Thread thread : threads) {
            thread.join();
        }
        long now = System.currentTimeMillis();
        while (System.currentTimeMillis() - now < 60000 && (client.hasPendingTasks()
                || counter.get() < numThreads * numCallsPerThreads)) {
            Thread.sleep(1);
        }
        long d = System.currentTimeMillis() - t;
        long numApiCalls = counter.get();
        long r = Math.round(numApiCalls * 1000.0 / d);
        System.out.println(
                "Finished [" + numApiCalls + "] in " + d + "ms, rate " + r + " calls/sec, concurrency " + concurrent);
    }
}
