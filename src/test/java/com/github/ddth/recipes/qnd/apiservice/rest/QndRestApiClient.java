
package com.github.ddth.recipes.qnd.apiservice.rest;

import com.github.ddth.commons.jsonrpc.HttpJsonRpcClient;
import com.github.ddth.commons.jsonrpc.RequestResponse;
import com.github.ddth.commons.utils.SerializationUtils;

public class QndRestApiClient {
    public static void main(String[] args) throws Exception {
        try (HttpJsonRpcClient client = new HttpJsonRpcClient()) {
            client.init();

            RequestResponse rr;

            rr = client.doGet("http://localhost:8080/api/env", null, null);
            System.out.println(rr.getResponseJson());

            rr = client.doGet("http://localhost:8080/api/echo", null, null);
            System.out.println(rr.getResponseJson());

            rr = client.doPost("http://localhost:8080/api/echo", null, null,
                    SerializationUtils.fromJsonString("{\"a\":1,\"b\":2.0,\"c\":true}"));
            System.out.println(rr.getResponseJson());
        }
    }
}
