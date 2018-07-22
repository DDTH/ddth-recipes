package com.github.ddth.recipes.qnd.apiservice.thrift;

import com.github.ddth.commons.utils.JacksonUtils;
import com.github.ddth.commons.utils.MapUtils;
import com.github.ddth.recipes.apiservice.thrift.ThriftApiClient;
import com.github.ddth.recipes.apiservice.thrift.ThriftApiUtils;
import com.github.ddth.recipes.apiservice.thrift.def.TApiAuth;
import com.github.ddth.recipes.apiservice.thrift.def.TApiParams;
import com.github.ddth.recipes.apiservice.thrift.def.TDataEncoding;

import java.util.Map;

public class QndThriftClient extends BaseQndThriftClient {
    public static void main(String[] args) throws Exception {
        String serverHostsAndPorts = "127.0.0.1:9090";
        try (ThriftApiClient client = ThriftApiUtils
                .createThriftApiClient(serverHostsAndPorts, true)) {
            client.ping();

            String appId = "app-id", accessToken = "access-token";
            Map<Object, Object> data = MapUtils
                    .createMap("t", System.currentTimeMillis(), "n", "Thanh Nguyen", "e",
                            "btnguyen2k@gmail.com", "system", System.getProperties(), "env",
                            System.getenv());

            TApiAuth apiAuth = new TApiAuth().setAppId(appId).setAccessToken(accessToken);
            {
                System.out.println("check()      : " + toString(client.check(apiAuth)));
                System.out.println("check()      : " + toString(client.check(appId, accessToken)));
            }
            {
                TDataEncoding sendEncoding = TDataEncoding.JSON_STRING;
                TDataEncoding returnEncoding = TDataEncoding.JSON_DEFAULT;
                TApiParams apiParams = new TApiParams().setEncoding(sendEncoding)
                        .setExpectedReturnEncoding(returnEncoding).setParamsData(ThriftApiUtils
                                .encodeFromJson(sendEncoding, JacksonUtils.toJson(data)));
                System.out.println(
                        "callApi(echo): " + toString(client.call("echo", apiAuth, apiParams)));
                System.out.println("callApi(echo): " + toString(
                        client.call("echo", appId, accessToken, sendEncoding, data)));
            }
            {
                TDataEncoding sendEncoding = TDataEncoding.JSON_GZIP;
                TDataEncoding returnEncoding = TDataEncoding.JSON_DEFAULT;
                TApiParams apiParams = new TApiParams().setEncoding(sendEncoding)
                        .setExpectedReturnEncoding(returnEncoding).setParamsData(ThriftApiUtils
                                .encodeFromJson(sendEncoding, JacksonUtils.toJson(data)));
                System.out.println(
                        "callApi(echo): " + toString(client.call("echo", apiAuth, apiParams)));
                System.out.println("callApi(echo): " + toString(
                        client.call("echo", appId, accessToken, sendEncoding, data)));
            }
        }
    }
}
