package com.github.ddth.recipes.apiservice.thrift;

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
    void check(String appId, String accessToken, AsyncMethodCallback<TApiResult> resultHandler)
            throws TException;

    /**
     * Call a server API, using default data encoding.
     *
     * @param apiName
     * @param appId
     * @param accessToken
     * @param params
     *         API parameters to pass to server
     * @param resultHandler
     * @throws TException
     */
    void call(String apiName, String appId, String accessToken, Object params,
            AsyncMethodCallback<TApiResult> resultHandler) throws TException;

    /**
     * Call a server API.
     *
     * @param apiName
     * @param appId
     * @param accessToken
     * @param encoding
     *         data encoding
     * @param params
     *         API parameters to pass to server
     * @param resultHandler
     * @throws TException
     */
    void call(String apiName, String appId, String accessToken, TDataEncoding encoding,
            Object params, AsyncMethodCallback<TApiResult> resultHandler) throws TException;
}
