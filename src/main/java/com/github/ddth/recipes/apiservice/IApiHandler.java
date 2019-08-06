package com.github.ddth.recipes.apiservice;

/**
 * Interface that handles API calls.
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since v0.2.0
 */
public interface IApiHandler {
    /**
     * Perform API call.
     *
     * @param context
     * @param auth
     * @param params
     * @return
     * @throws Exception
     */
    ApiResult handle(ApiContext context, ApiAuth auth, ApiParams params) throws Exception;
}
