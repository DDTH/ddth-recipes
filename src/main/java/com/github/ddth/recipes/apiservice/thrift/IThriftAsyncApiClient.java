package com.github.ddth.recipes.apiservice.thrift;

import com.github.ddth.recipes.apiservice.thrift.def.TApiAuth;
import com.github.ddth.recipes.apiservice.thrift.def.TApiResult;
import com.github.ddth.recipes.apiservice.thrift.def.TApiService;
import com.github.ddth.recipes.apiservice.thrift.def.TDataEncoding;
import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;

/**
 * Thrift Async API client interface.
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.2.0
 */
public interface IThriftAsyncApiClient extends TApiService.AsyncIface {
    /**
     * Call method "check" (test if server is online).
     *
     * @param appId
     * @param accessToken
     * @param resultHandler
     * @throws TException
     */
    default void check(String appId, String accessToken, AsyncMethodCallback<TApiResult> resultHandler)
            throws TException {
        check(new TApiAuth().setAppId(appId).setAccessToken(accessToken), resultHandler);
    }

    /**
     * Call a server API, using default data encoding.
     *
     * @param apiName
     * @param appId
     * @param accessToken
     * @param params        API parameters to pass to server
     * @param resultHandler
     * @throws TException
     */
    default void call(String apiName, String appId, String accessToken, Object params,
            AsyncMethodCallback<TApiResult> resultHandler) throws TException {
        call(apiName, appId, accessToken, TDataEncoding.JSON_DEFAULT, params, resultHandler);
    }

    /**
     * Call a server API.
     *
     * @param apiName
     * @param appId
     * @param accessToken
     * @param encoding      data encoding
     * @param params        API parameters to pass to server
     * @param resultHandler
     * @throws TException
     */
    void call(String apiName, String appId, String accessToken, TDataEncoding encoding, Object params,
            AsyncMethodCallback<TApiResult> resultHandler) throws TException;
}
