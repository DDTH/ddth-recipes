package com.github.ddth.recipes.apiservice.auth;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.github.ddth.recipes.apiservice.ApiAuth;
import com.github.ddth.recipes.apiservice.ApiContext;
import com.github.ddth.recipes.apiservice.IApiAuthenticator;

/**
 * {@link IApiAuthenticator} implementation that performs basic API call
 * authentication.
 * 
 * <p>
 * This class uses a map of {app-id:api-auth} for authenticating API calls.
 * </p>
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since v0.2.0
 */
public class BasicApiAuthenticator implements IApiAuthenticator {

    public final static BasicApiAuthenticator instance = new BasicApiAuthenticator();

    /**
     * Map {app-id:authentication}
     */
    private Map<String, ApiAuth> apiAuths = new ConcurrentHashMap<>();

    public BasicApiAuthenticator addApiAuth(ApiAuth apiAuth) {
        apiAuths.put(apiAuth.getAppId(), apiAuth.clone());
        return this;
    }

    public BasicApiAuthenticator removeApiAuth(String appId) {
        apiAuths.remove(appId);
        return this;
    }

    public BasicApiAuthenticator removeApiAuth(ApiAuth apiAuth) {
        apiAuths.remove(apiAuth.getAppId());
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean authenticate(ApiContext context, ApiAuth auth) {
        ApiAuth myAuth = auth != null && auth.getAppId() != null ? apiAuths.get(auth.getAppId())
                : null;
        return myAuth != null && myAuth.authenticate(context.getApiName(), auth);
    }
}
