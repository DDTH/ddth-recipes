package com.github.ddth.recipes.apiservice.thrift;

import com.github.ddth.recipes.apiservice.thrift.def.TApiAuth;
import com.github.ddth.recipes.apiservice.thrift.def.TApiResult;
import com.github.ddth.recipes.apiservice.thrift.def.TApiService;
import com.github.ddth.recipes.apiservice.thrift.def.TDataEncoding;
import org.apache.thrift.TException;

/**
 * Thrift API client interface.
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.2.0
 */
public interface IThriftApiClient extends TApiService.Iface {
    /**
     * Call method "check" (test if server is online).
     *
     * @param appId
     * @param accessToken
     * @return
     * @throws TException
     */
    default TApiResult check(String appId, String accessToken) throws TException {
        return check(new TApiAuth().setAppId(appId).setAccessToken(accessToken));
    }

    /**
     * Call a server API, using default data encoding.
     *
     * @param apiName
     * @param appId
     * @param accessToken
     * @param params      API parameters to pass to server
     * @return
     * @throws TException
     */
    default TApiResult call(String apiName, String appId, String accessToken, Object params) throws TException {
        return call(apiName, appId, accessToken, TDataEncoding.JSON_DEFAULT, params);
    }

    /**
     * Call a server API.
     *
     * @param apiName
     * @param appId
     * @param accessToken
     * @param encoding    data encoding
     * @param params      API parameters to pass to server
     * @return
     * @throws TException
     */
    TApiResult call(String apiName, String appId, String accessToken, TDataEncoding encoding, Object params)
            throws TException;
}
