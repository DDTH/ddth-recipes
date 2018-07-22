package com.github.ddth.recipes.qnd.apiservice.thrift;

import com.github.ddth.commons.utils.JacksonUtils;
import com.github.ddth.commons.utils.MapUtils;
import com.github.ddth.commons.utils.ThriftUtils;
import com.github.ddth.recipes.apiservice.thrift.ThriftApiClient;
import com.github.ddth.recipes.apiservice.thrift.ThriftApiUtils;
import com.github.ddth.recipes.apiservice.thrift.def.TApiAuth;
import com.github.ddth.recipes.apiservice.thrift.def.TApiParams;
import com.github.ddth.recipes.apiservice.thrift.def.TApiResult;
import com.github.ddth.recipes.apiservice.thrift.def.TDataEncoding;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.thrift.TException;

import java.io.File;
import java.util.Map;

public class QndThriftClientSsl {
    private static String toString(TApiResult result) throws TException {
        StringBuilder sb = new StringBuilder("=== Size: " + ThriftUtils.toBytes(result).length)
                .append("\n");
        ToStringBuilder tsb = new ToStringBuilder(result, ToStringStyle.SHORT_PREFIX_STYLE);
        tsb.append("status", result.status);
        tsb.append("message", result.message);
        tsb.append("encoding", result.encoding);
        tsb.append("data", ThriftApiUtils.decodeToJson(result.encoding, result.getResultData()));
        tsb.append("debug", ThriftApiUtils.decodeToJson(result.encoding, result.getDebugData()));
        return sb.append(tsb).toString();
    }

    public static void main(String[] args) throws Exception {
        File truststore = new File("./src/test/resources/keys/client.truststore");
        String truststorePass = "s3cr3t";
        String serverHostsAndPorts = "127.0.0.1:9443";
        try (ThriftApiClient client = ThriftApiUtils
                .createThriftApiClientSsl(serverHostsAndPorts, true, truststore.getAbsolutePath(),
                        truststorePass)) {
            client.ping();

            TApiAuth apiAuth = new TApiAuth();
            apiAuth.setAppId("app-id").setAccessToken("access-token");
            {
                System.out.println("check(): " + toString(client.check(apiAuth)));
            }
            {
                Map<Object, Object> data = MapUtils
                        .createMap("t", System.currentTimeMillis(), "n", "Thanh Nguyen", "e",
                                "btnguyen2k@gmail.com", "system", System.getProperties(), "env",
                                System.getenv());
                TDataEncoding sendEncoding = TDataEncoding.JSON_STRING;
                TDataEncoding returnEncoding = TDataEncoding.JSON_DEFAULT;
                TApiParams apiParams = new TApiParams().setEncoding(sendEncoding)
                        .setExpectedReturnEncoding(returnEncoding).setParamsData(ThriftApiUtils
                                .encodeFromJson(sendEncoding, JacksonUtils.toJson(data)));
                System.out.println(
                        "callApi(echo): " + toString(client.call("echo", apiAuth, apiParams)));
            }
            {
                Map<Object, Object> data = MapUtils
                        .createMap("t", System.currentTimeMillis(), "n", "Thanh Nguyen", "e",
                                "btnguyen2k@gmail.com", "system", System.getProperties(), "env",
                                System.getenv());
                TDataEncoding sendEncoding = TDataEncoding.JSON_GZIP;
                TDataEncoding returnEncoding = TDataEncoding.JSON_DEFAULT;
                TApiParams apiParams = new TApiParams().setEncoding(sendEncoding)
                        .setExpectedReturnEncoding(returnEncoding).setParamsData(ThriftApiUtils
                                .encodeFromJson(sendEncoding, JacksonUtils.toJson(data)));
                System.out.println(
                        "callApi(echo): " + toString(client.call("echo", apiAuth, apiParams)));
            }
        }
    }
}
