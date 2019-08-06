package com.github.ddth.recipes.qnd.apiservice.grpc;

import com.github.ddth.commons.utils.DPathUtils;
import com.github.ddth.commons.utils.JacksonUtils;
import com.github.ddth.commons.utils.MapUtils;
import com.github.ddth.recipes.apiservice.ApiResult;
import com.github.ddth.recipes.apiservice.grpc.GrpcApiClient;
import com.github.ddth.recipes.apiservice.grpc.GrpcAsyncApiClient;
import com.github.ddth.recipes.apiservice.grpc.GrpcUtils;
import com.github.ddth.recipes.apiservice.grpc.def.PApiServiceProto;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.protobuf.Empty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class BaseQndGrpcClient {
    protected static String toString(PApiServiceProto.PApiResult _result) {
        ApiResult result = GrpcUtils.toApiResult(_result);
        StringBuilder sb = new StringBuilder("=== Size: " + _result.toByteString().size()).append("\n");
        ToStringBuilder tsb = new ToStringBuilder(result, ToStringStyle.SHORT_PREFIX_STYLE);
        tsb.append("status", result.getStatus());
        tsb.append("message", result.getMessage());
        tsb.append("encoding", _result.getEncoding());
        tsb.append("data", result.getDataAsJson());
        tsb.append("debug", result.getDebugDataAsJson());
        return sb.append(tsb).append("\nConcurrency: ").append(result.getDebugDataAsJson().get("c")).toString();
    }

    protected static void doTest(GrpcApiClient client) {
        System.out.println("GrpcApiClient" + (client.isSslTransport() ? "/SSL" : "") + " - " + (client.isUseOkHttp() ?
                "OkHttp" :
                "Netty"));

        {
            Empty result = client.ping(Empty.getDefaultInstance());
            System.out.println("ping     : " + result);
        }
        {
            PApiServiceProto.PApiAuth apiAuth = PApiServiceProto.PApiAuth.newBuilder().setAppId("app-id")
                    .setAccessToken("access-token").build();
            PApiServiceProto.PApiResult result = client.check(apiAuth);
            System.out.println("check    : " + toString(result));
        }
        {
            PApiServiceProto.PApiAuth apiAuth = PApiServiceProto.PApiAuth.newBuilder().setAppId("app-id")
                    .setAccessToken("access-token").build();
            Map<Object, Object> data = MapUtils
                    .createMap("t", System.currentTimeMillis(), "n", "Thanh Nguyen", "e", "btnguyen2k@gmail.com",
                            "system", System.getProperties(), "env", System.getenv());
            PApiServiceProto.PDataEncoding sendEncoding = PApiServiceProto.PDataEncoding.JSON_STRING;
            PApiServiceProto.PDataEncoding returnEncoding = PApiServiceProto.PDataEncoding.JSON_DEFAULT;
            PApiServiceProto.PApiParams apiParams = PApiServiceProto.PApiParams.newBuilder().setEncoding(sendEncoding)
                    .setExpectedReturnEncoding(returnEncoding)
                    .setParamsData(GrpcUtils.encodeFromJson(sendEncoding, JacksonUtils.toJson(data))).build();
            PApiServiceProto.PApiContext context = PApiServiceProto.PApiContext.newBuilder().setApiName("echo")
                    .setApiAuth(apiAuth).setApiParams(apiParams).build();
            PApiServiceProto.PApiResult result = client.call(context);
            System.out.println("call-echo: " + toString(result));
        }
        {
            PApiServiceProto.PApiAuth apiAuth = PApiServiceProto.PApiAuth.newBuilder().setAppId("app-id")
                    .setAccessToken("access-token").build();
            Map<Object, Object> data = MapUtils
                    .createMap("t", System.currentTimeMillis(), "n", "Thanh Nguyen", "e", "btnguyen2k@gmail.com",
                            "system", System.getProperties(), "env", System.getenv());
            PApiServiceProto.PDataEncoding sendEncoding = PApiServiceProto.PDataEncoding.JSON_GZIP;
            PApiServiceProto.PDataEncoding returnEncoding = PApiServiceProto.PDataEncoding.JSON_DEFAULT;
            PApiServiceProto.PApiParams apiParams = PApiServiceProto.PApiParams.newBuilder().setEncoding(sendEncoding)
                    .setExpectedReturnEncoding(returnEncoding)
                    .setParamsData(GrpcUtils.encodeFromJson(sendEncoding, JacksonUtils.toJson(data))).build();
            PApiServiceProto.PApiContext context = PApiServiceProto.PApiContext.newBuilder().setApiName("echo")
                    .setApiAuth(apiAuth).setApiParams(apiParams).build();
            PApiServiceProto.PApiResult result = client.call(context);
            System.out.println("call-echo: " + toString(result));
        }
    }

    protected static void doTest(GrpcAsyncApiClient client) throws ExecutionException, InterruptedException {
        System.out.println("GrpcApiClient" + (client.isSslTransport() ? "/SSL" : "") + " - " + (client.isUseOkHttp() ?
                "OkHttp" :
                "Netty"));

        {
            ListenableFuture<Empty> result = client.ping(Empty.getDefaultInstance());
            System.out.println("ping     : " + result.get());
        }
        {
            PApiServiceProto.PApiAuth apiAuth = PApiServiceProto.PApiAuth.newBuilder().setAppId("app-id")
                    .setAccessToken("access-token").build();
            ListenableFuture<PApiServiceProto.PApiResult> result = client.check(apiAuth);
            System.out.println("check    : " + toString(result.get()));
        }
        {
            PApiServiceProto.PApiAuth apiAuth = PApiServiceProto.PApiAuth.newBuilder().setAppId("app-id")
                    .setAccessToken("access-token").build();
            Map<Object, Object> data = MapUtils
                    .createMap("t", System.currentTimeMillis(), "n", "Thanh Nguyen", "e", "btnguyen2k@gmail.com",
                            "system", System.getProperties(), "env", System.getenv());
            PApiServiceProto.PDataEncoding sendEncoding = PApiServiceProto.PDataEncoding.JSON_STRING;
            PApiServiceProto.PDataEncoding returnEncoding = PApiServiceProto.PDataEncoding.JSON_DEFAULT;
            PApiServiceProto.PApiParams apiParams = PApiServiceProto.PApiParams.newBuilder().setEncoding(sendEncoding)
                    .setExpectedReturnEncoding(returnEncoding)
                    .setParamsData(GrpcUtils.encodeFromJson(sendEncoding, JacksonUtils.toJson(data))).build();
            PApiServiceProto.PApiContext context = PApiServiceProto.PApiContext.newBuilder().setApiName("echo")
                    .setApiAuth(apiAuth).setApiParams(apiParams).build();
            ListenableFuture<PApiServiceProto.PApiResult> result = client.call(context);
            System.out.println("call-echo: " + toString(result.get()));
        }
        {
            PApiServiceProto.PApiAuth apiAuth = PApiServiceProto.PApiAuth.newBuilder().setAppId("app-id")
                    .setAccessToken("access-token").build();
            Map<Object, Object> data = MapUtils
                    .createMap("t", System.currentTimeMillis(), "n", "Thanh Nguyen", "e", "btnguyen2k@gmail.com",
                            "system", System.getProperties(), "env", System.getenv());
            PApiServiceProto.PDataEncoding sendEncoding = PApiServiceProto.PDataEncoding.JSON_GZIP;
            PApiServiceProto.PDataEncoding returnEncoding = PApiServiceProto.PDataEncoding.JSON_DEFAULT;
            PApiServiceProto.PApiParams apiParams = PApiServiceProto.PApiParams.newBuilder().setEncoding(sendEncoding)
                    .setExpectedReturnEncoding(returnEncoding)
                    .setParamsData(GrpcUtils.encodeFromJson(sendEncoding, JacksonUtils.toJson(data))).build();
            PApiServiceProto.PApiContext context = PApiServiceProto.PApiContext.newBuilder().setApiName("echo")
                    .setApiAuth(apiAuth).setApiParams(apiParams).build();
            ListenableFuture<PApiServiceProto.PApiResult> result = client.call(context);
            System.out.println("call-echo: " + toString(result.get()));
        }
    }

    protected static void doTestMultiThreads(GrpcApiClient client, int numThreads, int numCallsPerThreads)
            throws InterruptedException {
        System.out.println("GrpcApiClient" + (client.isSslTransport() ? "/SSL" : "") + " - " + (client.isUseOkHttp() ?
                "OkHttp" :
                "Netty"));

        PApiServiceProto.PApiAuth apiAuth = PApiServiceProto.PApiAuth.newBuilder().setAppId("app-id")
                .setAccessToken("access-token").build();
        Map<Object, Object> data = MapUtils
                .createMap("t", System.currentTimeMillis(), "n", "Thanh Nguyen", "e", "btnguyen2k@gmail.com", "system",
                        System.getProperties(), "env", System.getenv());
        PApiServiceProto.PDataEncoding sendEncoding = PApiServiceProto.PDataEncoding.JSON_STRING;
        PApiServiceProto.PDataEncoding returnEncoding = PApiServiceProto.PDataEncoding.JSON_DEFAULT;
        PApiServiceProto.PApiParams apiParams = PApiServiceProto.PApiParams.newBuilder().setEncoding(sendEncoding)
                .setExpectedReturnEncoding(returnEncoding)
                .setParamsData(GrpcUtils.encodeFromJson(sendEncoding, JacksonUtils.toJson(data))).build();
        PApiServiceProto.PApiContext context = PApiServiceProto.PApiContext.newBuilder().setApiName("echo")
                .setApiAuth(apiAuth).setApiParams(apiParams).build();

        AtomicLong counter = new AtomicLong(0);
        AtomicInteger concurrent = new AtomicInteger(0);

        Thread[] threads = new Thread[numThreads];
        long t = System.currentTimeMillis();
        for (int i = 0; i < numThreads; i++) {
            threads[i] = new Thread(() -> {
                try {
                    for (int j = 0; j < numCallsPerThreads; j++) {
                        counter.incrementAndGet();

                        PApiServiceProto.PApiResult result = client.call(context);
                        ApiResult apiResult = GrpcUtils.toApiResult(result);
                        int d1 = DPathUtils.getValueOptional(apiResult.getDebugDataAsJson(), "c", Integer.class)
                                .orElse(0);
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
        System.out.println(
                "Finished [" + numApiCalls + "] in " + d + "ms, rate " + r + " calls/sec, concurrency " + concurrent);
    }

    protected static void doTestMultiThreads(GrpcAsyncApiClient client, int numThreads, int numCallsPerThreads)
            throws InterruptedException {
        System.out.println("GrpcApiClient" + (client.isSslTransport() ? "/SSL" : "") + " - " + (client.isUseOkHttp() ?
                "OkHttp" :
                "Netty"));

        PApiServiceProto.PApiAuth apiAuth = PApiServiceProto.PApiAuth.newBuilder().setAppId("app-id")
                .setAccessToken("access-token").build();
        Map<Object, Object> data = MapUtils
                .createMap("t", System.currentTimeMillis(), "n", "Thanh Nguyen", "e", "btnguyen2k@gmail.com", "system",
                        System.getProperties(), "env", System.getenv());
        PApiServiceProto.PDataEncoding sendEncoding = PApiServiceProto.PDataEncoding.JSON_STRING;
        PApiServiceProto.PDataEncoding returnEncoding = PApiServiceProto.PDataEncoding.JSON_DEFAULT;
        PApiServiceProto.PApiParams apiParams = PApiServiceProto.PApiParams.newBuilder().setEncoding(sendEncoding)
                .setExpectedReturnEncoding(returnEncoding)
                .setParamsData(GrpcUtils.encodeFromJson(sendEncoding, JacksonUtils.toJson(data))).build();
        PApiServiceProto.PApiContext context = PApiServiceProto.PApiContext.newBuilder().setApiName("echo")
                .setApiAuth(apiAuth).setApiParams(apiParams).build();

        AtomicLong counter = new AtomicLong(0);
        AtomicInteger concurrent = new AtomicInteger(0);

        Thread[] threads = new Thread[numThreads];
        long t = System.currentTimeMillis();
        for (int i = 0; i < numThreads; i++) {
            threads[i] = new Thread(() -> {
                try {
                    for (int j = 0; j < numCallsPerThreads; j++) {
                        counter.incrementAndGet();

                        ListenableFuture<PApiServiceProto.PApiResult> result = client.call(context);
                        ApiResult apiResult1 = GrpcUtils.toApiResult(result.get());
                        int d1 = DPathUtils.getValueOptional(apiResult1.getDebugDataAsJson(), "c", Integer.class)
                                .orElse(0);
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
        System.out.println(
                "Finished [" + numApiCalls + "] in " + d + "ms, rate " + r + " calls/sec, concurrency " + concurrent);
    }
}
