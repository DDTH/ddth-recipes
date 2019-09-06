package com.github.ddth.recipes.apiservice;

/**
 * Filters are plugable components that are used to intercept API call and do some pre-processing, intercept result
 * and do post-processing before returning to caller.
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since v1.1.0
 */
public abstract class ApiFilter {
    private ApiRouter apiRouter;
    private ApiFilter nextFilter;

    public ApiFilter() {
    }

    public ApiFilter(ApiRouter apiRouter) {
        setApiRouter(apiRouter);
    }

    public ApiFilter(ApiFilter nextFilter) {
        setNextFilter(nextFilter);
    }

    public ApiFilter(ApiRouter apiRouter, ApiFilter nextFilter) {
        setApiRouter(apiRouter);
        setNextFilter(nextFilter);
    }

    public ApiRouter getApiRouter() {
        return apiRouter;
    }

    public ApiFilter setApiRouter(ApiRouter apiRouter) {
        this.apiRouter = apiRouter;
        return this;
    }

    /**
     * Return next filter in the chain.
     *
     * @return
     */
    public ApiFilter getNextFilter() {
        return nextFilter;
    }

    /**
     * Set next filter in the chain.
     *
     * @param nextFilter
     * @return
     */
    public ApiFilter setNextFilter(ApiFilter nextFilter) {
        this.nextFilter = nextFilter;
        return this;
    }

    /**
     * Convenient method to chain next filter or invoke API handler.
     *
     * @param apiHandler
     * @param context
     * @param auth
     * @param params
     * @return
     * @throws Exception
     */
    protected ApiResult nextFilterOrHandler(IApiHandler apiHandler, ApiContext context, ApiAuth auth, ApiParams params)
            throws Exception {
        return nextFilter != null ?
                nextFilter.call(apiHandler, context, auth, params) :
                apiHandler.handle(context, auth, params);
    }

    /**
     * Call API with filtering.
     *
     * @param apiHandler
     * @param context
     * @param auth
     * @param params
     * @return
     * @throws Exception
     */
    public abstract ApiResult call(IApiHandler apiHandler, ApiContext context, ApiAuth auth, ApiParams params)
            throws Exception;
}
