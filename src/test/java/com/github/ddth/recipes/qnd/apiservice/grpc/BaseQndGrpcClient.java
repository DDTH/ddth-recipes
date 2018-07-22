package com.github.ddth.recipes.qnd.apiservice.grpc;

import com.github.ddth.commons.utils.JacksonUtils;
import com.github.ddth.commons.utils.MapUtils;
import com.github.ddth.recipes.apiservice.ApiResult;
import com.github.ddth.recipes.apiservice.grpc.GrpcApiClient;
import com.github.ddth.recipes.apiservice.grpc.GrpcApiUtils;
import com.github.ddth.recipes.apiservice.grpc.GrpcAsyncApiClient;
import com.github.ddth.recipes.apiservice.grpc.def.PApiServiceProto;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.protobuf.Empty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Map;
import java.util.concurrent.ExecutionException;

public class BaseQndGrpcClient {
    protected static String toString(PApiServiceProto.PApiResult _result) {
        ApiResult result = GrpcApiUtils.toApiResult(_result);
        StringBuilder sb = new StringBuilder("=== Size: " + _result.toByteString().size())
                .append("\n");
        ToStringBuilder tsb = new ToStringBuilder(result, ToStringStyle.SHORT_PREFIX_STYLE);
        tsb.append("status", result.getStatus());
        tsb.append("message", result.getMessage());
        tsb.append("encoding", _result.getEncoding());
        tsb.append("data", result.getDataAsJson());
        tsb.append("debug", result.getDebugDataAsJson());
        return sb.append(tsb).toString();
    }

    protected static void doTest(GrpcApiClient client) {
        {
            Empty result = client.ping(Empty.getDefaultInstance());
            System.out.println("ping     : " + result);
        }
        {
            PApiServiceProto.PApiAuth apiAuth = PApiServiceProto.PApiAuth.newBuilder()
                    .setAppId("app-id").setAccessToken("access-token").build();
            PApiServiceProto.PApiResult result = client.check(apiAuth);
            System.out.println("check    : " + toString(result));
        }
        {
            PApiServiceProto.PApiAuth apiAuth = PApiServiceProto.PApiAuth.newBuilder()
                    .setAppId("app-id").setAccessToken("access-token").build();
            Map<Object, Object> data = MapUtils
                    .createMap("t", System.currentTimeMillis(), "n", "Thanh Nguyen", "e",
                            "btnguyen2k@gmail.com", "system", System.getProperties(), "env",
                            System.getenv());
            PApiServiceProto.PDataEncoding sendEncoding = PApiServiceProto.PDataEncoding.JSON_STRING;
            PApiServiceProto.PDataEncoding returnEncoding = PApiServiceProto.PDataEncoding.JSON_DEFAULT;
            PApiServiceProto.PApiParams apiParams = PApiServiceProto.PApiParams.newBuilder()
                    .setEncoding(sendEncoding).setExpectedReturnEncoding(returnEncoding)
                    .setParamsData(
                            GrpcApiUtils.encodeFromJson(sendEncoding, JacksonUtils.toJson(data)))
                    .build();
            PApiServiceProto.PApiContext context = PApiServiceProto.PApiContext.newBuilder()
                    .setApiName("echo").setApiAuth(apiAuth).setApiParams(apiParams).build();
            PApiServiceProto.PApiResult result = client.call(context);
            System.out.println("call-echo: " + toString(result));
        }
        {
            PApiServiceProto.PApiAuth apiAuth = PApiServiceProto.PApiAuth.newBuilder()
                    .setAppId("app-id").setAccessToken("access-token").build();
            Map<Object, Object> data = MapUtils
                    .createMap("t", System.currentTimeMillis(), "n", "Thanh Nguyen", "e",
                            "btnguyen2k@gmail.com", "system", System.getProperties(), "env",
                            System.getenv());
            PApiServiceProto.PDataEncoding sendEncoding = PApiServiceProto.PDataEncoding.JSON_GZIP;
            PApiServiceProto.PDataEncoding returnEncoding = PApiServiceProto.PDataEncoding.JSON_DEFAULT;
            PApiServiceProto.PApiParams apiParams = PApiServiceProto.PApiParams.newBuilder()
                    .setEncoding(sendEncoding).setExpectedReturnEncoding(returnEncoding)
                    .setParamsData(
                            GrpcApiUtils.encodeFromJson(sendEncoding, JacksonUtils.toJson(data)))
                    .build();
            PApiServiceProto.PApiContext context = PApiServiceProto.PApiContext.newBuilder()
                    .setApiName("echo").setApiAuth(apiAuth).setApiParams(apiParams).build();
            PApiServiceProto.PApiResult result = client.call(context);
            System.out.println("call-echo: " + toString(result));
        }
    }

    protected static void doTest(GrpcAsyncApiClient client)
            throws ExecutionException, InterruptedException {
        {
            ListenableFuture<Empty> result = client.ping(Empty.getDefaultInstance());
            System.out.println("ping     : " + result.get());
        }
        {
            PApiServiceProto.PApiAuth apiAuth = PApiServiceProto.PApiAuth.newBuilder()
                    .setAppId("app-id").setAccessToken("access-token").build();
            ListenableFuture<PApiServiceProto.PApiResult> result = client.check(apiAuth);
            System.out.println("check    : " + toString(result.get()));
        }
        {
            PApiServiceProto.PApiAuth apiAuth = PApiServiceProto.PApiAuth.newBuilder()
                    .setAppId("app-id").setAccessToken("access-token").build();
            Map<Object, Object> data = MapUtils
                    .createMap("t", System.currentTimeMillis(), "n", "Thanh Nguyen", "e",
                            "btnguyen2k@gmail.com", "system", System.getProperties(), "env",
                            System.getenv());
            PApiServiceProto.PDataEncoding sendEncoding = PApiServiceProto.PDataEncoding.JSON_STRING;
            PApiServiceProto.PDataEncoding returnEncoding = PApiServiceProto.PDataEncoding.JSON_DEFAULT;
            PApiServiceProto.PApiParams apiParams = PApiServiceProto.PApiParams.newBuilder()
                    .setEncoding(sendEncoding).setExpectedReturnEncoding(returnEncoding)
                    .setParamsData(
                            GrpcApiUtils.encodeFromJson(sendEncoding, JacksonUtils.toJson(data)))
                    .build();
            PApiServiceProto.PApiContext context = PApiServiceProto.PApiContext.newBuilder()
                    .setApiName("echo").setApiAuth(apiAuth).setApiParams(apiParams).build();
            ListenableFuture<PApiServiceProto.PApiResult> result = client.call(context);
            System.out.println("call-echo: " + toString(result.get()));
        }
        {
            PApiServiceProto.PApiAuth apiAuth = PApiServiceProto.PApiAuth.newBuilder()
                    .setAppId("app-id").setAccessToken("access-token").build();
            Map<Object, Object> data = MapUtils
                    .createMap("t", System.currentTimeMillis(), "n", "Thanh Nguyen", "e",
                            "btnguyen2k@gmail.com", "system", System.getProperties(), "env",
                            System.getenv());
            PApiServiceProto.PDataEncoding sendEncoding = PApiServiceProto.PDataEncoding.JSON_GZIP;
            PApiServiceProto.PDataEncoding returnEncoding = PApiServiceProto.PDataEncoding.JSON_DEFAULT;
            PApiServiceProto.PApiParams apiParams = PApiServiceProto.PApiParams.newBuilder()
                    .setEncoding(sendEncoding).setExpectedReturnEncoding(returnEncoding)
                    .setParamsData(
                            GrpcApiUtils.encodeFromJson(sendEncoding, JacksonUtils.toJson(data)))
                    .build();
            PApiServiceProto.PApiContext context = PApiServiceProto.PApiContext.newBuilder()
                    .setApiName("echo").setApiAuth(apiAuth).setApiParams(apiParams).build();
            ListenableFuture<PApiServiceProto.PApiResult> result = client.call(context);
            System.out.println("call-echo: " + toString(result.get()));
        }
    }
}
