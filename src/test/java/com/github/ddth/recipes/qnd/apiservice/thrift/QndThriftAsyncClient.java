package com.github.ddth.recipes.qnd.apiservice.thrift;

import com.github.ddth.commons.utils.JacksonUtils;
import com.github.ddth.commons.utils.MapUtils;
import com.github.ddth.recipes.apiservice.thrift.ThriftApiUtils;
import com.github.ddth.recipes.apiservice.thrift.ThriftAsyncApiClient;
import com.github.ddth.recipes.apiservice.thrift.def.TApiAuth;
import com.github.ddth.recipes.apiservice.thrift.def.TApiParams;
import com.github.ddth.recipes.apiservice.thrift.def.TApiResult;
import com.github.ddth.recipes.apiservice.thrift.def.TDataEncoding;
import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;

import java.util.Map;

public class QndThriftAsyncClient extends BaseQndThriftClient {
    private static class MyAsyncMethodCallback implements AsyncMethodCallback<TApiResult> {
        private String methodName;

        public MyAsyncMethodCallback(String methodName) {
            this.methodName = methodName;
        }

        @Override
        public void onComplete(TApiResult tApiResult) {
            try {
                System.out.println("COMPLETE - " + methodName + ": " + QndThriftAsyncClient
                        .toString(tApiResult));
            } catch (TException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(Exception e) {
            System.err.println("ERROR - " + methodName + ": " + e);
        }
    }

    public static void main(String[] args) throws Exception {
        String appId = "app-id", accessToken = "access-token";
        Map<Object, Object> data = MapUtils
                .createMap("t", System.currentTimeMillis(), "n", "Thanh Nguyen", "e",
                        "btnguyen2k@gmail.com", "system", System.getProperties(), "env",
                        System.getenv());
        TApiAuth apiAuth = new TApiAuth().setAppId(appId).setAccessToken(accessToken);
        String hostsAndPorts = "127.0.0.1:9090";
        try (ThriftAsyncApiClient asyncClient = ThriftApiUtils
                .createThriftAsyncApiClient(hostsAndPorts, true)) {
            asyncClient.ping(new AsyncMethodCallback<Void>() {
                @Override
                public void onComplete(Void aVoid) {
                    System.out.println("COMPLETE - ping: " + aVoid);
                }

                @Override
                public void onError(Exception e) {
                    System.err.println("ERROR - ping: " + e);
                }
            });

            asyncClient.check(apiAuth, new MyAsyncMethodCallback("check"));
            asyncClient.check(appId, accessToken, new MyAsyncMethodCallback("check"));
            {
                TDataEncoding sendEncoding = TDataEncoding.JSON_STRING;
                TDataEncoding returnEncoding = TDataEncoding.JSON_DEFAULT;
                TApiParams apiParams = new TApiParams().setEncoding(sendEncoding)
                        .setExpectedReturnEncoding(returnEncoding).setParamsData(ThriftApiUtils
                                .encodeFromJson(sendEncoding, JacksonUtils.toJson(data)));
                asyncClient
                        .call("echo", apiAuth, apiParams, new MyAsyncMethodCallback("call(echo)"));
                asyncClient.call("echo", appId, accessToken, sendEncoding, data,
                        new MyAsyncMethodCallback("call" + "(echo)"));
            }
            {
                TDataEncoding sendEncoding = TDataEncoding.JSON_GZIP;
                TDataEncoding returnEncoding = TDataEncoding.JSON_DEFAULT;
                TApiParams apiParams = new TApiParams().setEncoding(sendEncoding)
                        .setExpectedReturnEncoding(returnEncoding).setParamsData(ThriftApiUtils
                                .encodeFromJson(sendEncoding, JacksonUtils.toJson(data)));
                asyncClient
                        .call("echo", apiAuth, apiParams, new MyAsyncMethodCallback("call(echo)"));
                asyncClient.call("echo", appId, accessToken, sendEncoding, data,
                        new MyAsyncMethodCallback("call" + "(echo)"));
            }

            while (asyncClient.hasPendingTasks()) {
                Thread.sleep(1);
            }
        }
    }
}
