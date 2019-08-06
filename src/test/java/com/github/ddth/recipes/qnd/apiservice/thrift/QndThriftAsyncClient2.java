package com.github.ddth.recipes.qnd.apiservice.thrift;

import com.github.ddth.commons.utils.JacksonUtils;
import com.github.ddth.commons.utils.MapUtils;
import com.github.ddth.recipes.apiservice.thrift.ThriftAsyncApiClient;
import com.github.ddth.recipes.apiservice.thrift.ThriftUtils;
import com.github.ddth.recipes.apiservice.thrift.def.TApiAuth;
import com.github.ddth.recipes.apiservice.thrift.def.TApiParams;
import com.github.ddth.recipes.apiservice.thrift.def.TApiResult;
import com.github.ddth.recipes.apiservice.thrift.def.TDataEncoding;
import org.apache.thrift.async.AsyncMethodCallback;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class QndThriftAsyncClient2 extends BaseQndThriftClient {
    static AtomicLong COUNTER_DONE = new AtomicLong(0);
    static AtomicLong COUNTER_ERROR = new AtomicLong(0);
    static AtomicLong COUNTER_SUBMIT = new AtomicLong(0);

    private static class MyAsyncMethodCallback implements AsyncMethodCallback<TApiResult> {
        private String methodName;

        public MyAsyncMethodCallback(String methodName) {
            COUNTER_SUBMIT.incrementAndGet();
            this.methodName = methodName;
        }

        @Override
        public void onComplete(TApiResult tApiResult) {
            COUNTER_DONE.incrementAndGet();
        }

        @Override
        public void onError(Exception e) {
            COUNTER_ERROR.incrementAndGet();
        }
    }

    public static void main(String[] args) throws Exception {
        String hostsAndPorts = "127.0.0.1:9090";
        try (ThriftAsyncApiClient asyncClient = ThriftUtils.createThriftAsyncApiClient(hostsAndPorts, true)) {
            String appId = "app-id", accessToken = "access-token";
            Map<Object, Object> data = MapUtils
                    .createMap("t", System.currentTimeMillis(), "n", "Thanh Nguyen", "e", "btnguyen2k@gmail.com",
                            "system", System.getProperties(), "env", System.getenv());
            TApiAuth apiAuth = new TApiAuth().setAppId(appId).setAccessToken(accessToken);
            TDataEncoding sendEncoding = TDataEncoding.JSON_STRING;
            TDataEncoding returnEncoding = TDataEncoding.JSON_DEFAULT;
            TApiParams apiParams = new TApiParams().setEncoding(sendEncoding).setExpectedReturnEncoding(returnEncoding)
                    .setParamsData(ThriftUtils.encodeFromJson(sendEncoding, JacksonUtils.toJson(data)));

            for (int i = 0; i < 10000; i++) {
                asyncClient.call("echo", apiAuth, apiParams, new MyAsyncMethodCallback("call(echo)"));
            }

            while (asyncClient.hasPendingTasks()) {
                Thread.sleep(1);
            }
            System.out.println("Submit  : " + COUNTER_SUBMIT);
            System.out.println("Complete: " + COUNTER_DONE);
            System.out.println("Error   : " + COUNTER_ERROR);
        }
    }
}
