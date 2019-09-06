package com.github.ddth.recipes.apiservice.filters;

import com.github.ddth.recipes.apiservice.*;

/**
 * Filter that performs authentication check before calling API.
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since v1.1.0
 */
public class AuthenticationFilter extends ApiFilter {
    private IApiAuthenticator apiAuthenticator;

    public AuthenticationFilter() {
    }

    public AuthenticationFilter(ApiRouter apiRouter, IApiAuthenticator apiAuthenticator) {
        super(apiRouter);
        setApiAuthenticator(apiAuthenticator);
    }

    public AuthenticationFilter(ApiRouter apiRouter, ApiFilter nextFilter, IApiAuthenticator apiAuthenticator) {
        super(apiRouter, nextFilter);
        setApiAuthenticator(apiAuthenticator);
    }

    public IApiAuthenticator getApiAuthenticator() {
        return apiAuthenticator;
    }

    public AuthenticationFilter setApiAuthenticator(IApiAuthenticator apiAuthenticator) {
        this.apiAuthenticator = apiAuthenticator;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ApiResult call(IApiHandler apiHandler, ApiContext context, ApiAuth auth, ApiParams params) throws Exception {
        if (!apiAuthenticator.authenticate(context, auth)) {
            return new ApiResult(ApiResult.STATUS_NO_PERMISSION,
                    "App [" + auth.getAppId() + "] is not allowed to call API [" + context.getApiName() + "] via ["
                            + context.getGateway() + "].");
        }
        return nextFilterOrHandler(apiHandler, context, auth, params);
    }
}
