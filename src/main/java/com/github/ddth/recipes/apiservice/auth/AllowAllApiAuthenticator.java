package com.github.ddth.recipes.apiservice.auth;

import com.github.ddth.recipes.apiservice.ApiAuth;
import com.github.ddth.recipes.apiservice.ApiContext;
import com.github.ddth.recipes.apiservice.IApiAuthenticator;

/**
 * {@link IApiAuthenticator} implementation that allows all API calls.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since v0.2.0
 */
public class AllowAllApiAuthenticator implements IApiAuthenticator {

    public final static AllowAllApiAuthenticator instance = new AllowAllApiAuthenticator();

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean authenticate(ApiContext context, ApiAuth auth) {
        return true;
    }

}
