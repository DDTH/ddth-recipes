package com.github.ddth.recipes.apiservice;

/**
 * Logger to log API calls.
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since v1.0.0
 */
public interface IApiLogger {
    /**
     * This method is called just before API is actually invoked.
     *
     * @param totalConcurrency total number of (all) concurrent API calls
     * @param apiConcurrency   number of concurrent (this) API calls
     * @param context
     * @param auth
     * @param params
     */
    void preApiCall(long totalConcurrency, long apiConcurrency, ApiContext context, ApiAuth auth, ApiParams params);

    /**
     * This method is called right after API is invoked.
     *
     * @param durationMs duration (in milliseconds) the API handler took to process the call
     * @param context
     * @param auth
     * @param params
     * @param result
     */
    void postApiCall(long durationMs, ApiContext context, ApiAuth auth, ApiParams params, ApiResult result);
}
