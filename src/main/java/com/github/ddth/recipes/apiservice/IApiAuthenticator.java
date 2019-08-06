package com.github.ddth.recipes.apiservice;

/**
 * Interface that authenticates API calls.
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since v0.2.0
 */
public interface IApiAuthenticator {
    /**
     * Authenticate an API call.
     *
     * @param context
     * @param auth
     * @return
     */
    boolean authenticate(ApiContext context, ApiAuth auth);
}
