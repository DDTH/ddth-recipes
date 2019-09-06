package com.github.ddth.recipes.apiservice.filters;

import com.github.ddth.recipes.apiservice.*;

/**
 * Filter that performs logging before and after API call.
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since v1.1.0
 */
public class LoggingFilter extends ApiFilter {
    private IApiLogger apiLogger;

    public LoggingFilter() {
    }

    public LoggingFilter(ApiRouter apiRouter, IApiLogger apiLogger) {
        super(apiRouter);
        setApiLogger(apiLogger);
    }

    public LoggingFilter(ApiRouter apiRouter, ApiFilter nextFilter, IApiLogger apiLogger) {
        super(apiRouter, nextFilter);
        setApiLogger(apiLogger);
    }

    public IApiLogger getApiLogger() {
        return apiLogger;
    }

    public LoggingFilter setApiLogger(IApiLogger apiLogger) {
        this.apiLogger = apiLogger;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ApiResult call(IApiHandler apiHandler, ApiContext context, ApiAuth auth, ApiParams params) throws Exception {
        ApiRouter apiRouter = getApiRouter();
        apiLogger.preApiCall(apiRouter.getConcurency(), apiRouter.getConcurency(context.getApiName()), context, auth,
                params);
        long start = System.currentTimeMillis();
        ApiResult apiResult = null;
        try {
            apiResult = nextFilterOrHandler(apiHandler, context, auth, params);
            return apiResult;
        } finally {
            long duration = System.currentTimeMillis() - start;
            apiLogger.postApiCall(duration, apiRouter.getConcurency(), apiRouter.getConcurency(context.getApiName()),
                    context, auth, params, apiResult);
        }
    }
}
