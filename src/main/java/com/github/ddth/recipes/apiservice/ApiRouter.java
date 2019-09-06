package com.github.ddth.recipes.apiservice;

import com.github.ddth.recipes.apiservice.auth.AllowAllApiAuthenticator;
import com.github.ddth.recipes.apiservice.filters.AddPerfInfoFilter;
import com.github.ddth.recipes.apiservice.filters.AuthenticationFilter;
import com.github.ddth.recipes.apiservice.filters.LoggingFilter;
import com.google.common.util.concurrent.AtomicLongMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Route API calls to handlers.
 *
 * <p>Since {@code v1.1.0}, method {@link #init()} will create default {@link ApiFilter} with the following rules:</p>
 * <ul>
 * <li>If {@link #getApiAuthenticator()} returns non-null value it will be used to create a {@link AuthenticationFilter}, and the filter is prepended to filter chain.</li>
 * <li>If {@link #getApiLogger()} returns non-null value it will be used to create a {@link LoggingFilter}, and the filter is prepended to filter chain.</li>
 * <li>If {@link #isAddPerfInfoToResult()} returns {@code true} a {@link AddPerfInfoFilter} will be created and prepended to filter chain.</li>
 * </ul>
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.2.0
 */
public class ApiRouter implements AutoCloseable {
    private Logger LOGGER = LoggerFactory.getLogger(ApiRouter.class);

    private AtomicLong totalConcurrency = new AtomicLong(0);
    private AtomicLongMap<String> apiConcurrency = AtomicLongMap.create();

    private Map<String, IApiHandler> apiHandlers = new ConcurrentHashMap<>();

    private IApiAuthenticator apiAuthenticator = AllowAllApiAuthenticator.instance;
    private IApiLogger apiLogger;
    private boolean addPerfInfoToResult = true;

    private ApiFilter apiFilter;

    /**
     * Authenticator used to authenticate API calls.
     *
     * @return
     */
    public IApiAuthenticator getApiAuthenticator() {
        return apiAuthenticator;
    }

    /**
     * Authenticator used to authenticate API calls.
     *
     * @param apiAuthenticator
     * @return
     */
    public ApiRouter setApiAuthenticator(IApiAuthenticator apiAuthenticator) {
        this.apiAuthenticator = apiAuthenticator;
        return this;
    }

    /**
     * Logger used to log API calls.
     *
     * @param logger
     * @return
     * @since 1.0.0
     */
    public ApiRouter setApiLogger(IApiLogger logger) {
        this.apiLogger = logger;
        return this;
    }

    /**
     * Logger used to log API calls.
     *
     * @return
     * @since 1.0.0
     */
    public IApiLogger getApiLogger() {
        return apiLogger;
    }

    /**
     * Filter used to intercept API call and do some pre-processing/post-processing.
     *
     * @return
     * @since 1.1.0
     */
    public ApiFilter getApiFilter() {
        return apiFilter;
    }

    /**
     * Filter used to intercept API call and do some pre-processing/post-processing.
     *
     * @param apiFilter
     * @return
     * @since 1.1.0
     */
    public ApiRouter setApiFilter(ApiFilter apiFilter) {
        this.apiFilter = apiFilter;
        return this;
    }

    /**
     * If {@code true}, API performance info will be added to api result as debug data.
     *
     * @return
     * @since 1.1.0
     */
    public boolean isAddPerfInfoToResult() {
        return addPerfInfoToResult;
    }

    /**
     * If {@code true}, API performance info will be added to api result as debug data.
     *
     * @param addPerfInfoToResult
     * @return
     * @since 1.1.0
     */
    public ApiRouter setAddPerfInfoToResult(boolean addPerfInfoToResult) {
        this.addPerfInfoToResult = addPerfInfoToResult;
        return this;
    }

    /**
     * Init method.
     *
     * @return
     */
    public ApiRouter init() {
        ApiFilter myFilter = getApiFilter();
        if (apiAuthenticator != null) {
            myFilter = new AuthenticationFilter(this, myFilter, apiAuthenticator);
        }
        if (apiLogger != null) {
            myFilter = new LoggingFilter(this, myFilter, apiLogger);
        }
        if (addPerfInfoToResult) {
            myFilter = new AddPerfInfoFilter(this, myFilter);
        }
        setApiFilter(myFilter);
        return this;
    }

    /**
     * Clean up method.
     */
    public void destroy() {
        // EMPTY
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        destroy();
    }

    private IApiHandler catchAllApiHandler = null;
    public final static String CATCHALL_API_NAME = "*";

    /**
     * Set the handler that "catch all" API calls when there is no matched handler.
     *
     * @param handler
     * @return
     */
    public ApiRouter setCatchAllHandler(IApiHandler handler) {
        catchAllApiHandler = handler;
        return registerApiHandler(CATCHALL_API_NAME, handler);
    }

    /**
     * Unset the handler that "catch all" API calls when there is no matched
     * handler.
     *
     * @return
     */
    public ApiRouter unsetCatchAllHandler() {
        catchAllApiHandler = null;
        return unregisterApiHandler(CATCHALL_API_NAME);
    }

    /**
     * Getter for {@link #apiHandlers}.
     *
     * @return
     */
    public Map<String, IApiHandler> getApiHandlers() {
        return Collections.unmodifiableMap(apiHandlers);
    }

    /**
     * Setter for {@link #apiHandlers}.
     *
     * @param apiHandlers
     * @return
     */
    public ApiRouter setApiHandlers(Map<String, IApiHandler> apiHandlers) {
        this.apiHandlers.clear();
        if (apiHandlers != null) {
            this.apiHandlers.putAll(apiHandlers);
        }
        IApiHandler catchAll = this.apiHandlers.get(CATCHALL_API_NAME);
        if (catchAll != null) {
            catchAllApiHandler = catchAll;
        }
        return this;
    }

    /**
     * Get the API handler for an api name.
     *
     * @param apiName
     * @return
     * @since 1.1.0
     */
    public IApiHandler getApiHandler(String apiName) {
        return apiName != null ? apiHandlers.get(apiName) : null;
    }

    /**
     * Add a new API handler.
     *
     * @param apiName
     * @param apiHandler
     * @return
     */
    public ApiRouter addApiHandler(String apiName, IApiHandler apiHandler) {
        if (apiHandler == null) {
            apiHandlers.remove(apiName);
        } else {
            apiHandlers.put(apiName, apiHandler);
        }
        catchAllApiHandler = this.apiHandlers.get(CATCHALL_API_NAME);
        return this;
    }

    /**
     * Remove an existing API handler.
     *
     * @param apiName
     * @return
     */
    public ApiRouter removeApiHandler(String apiName) {
        apiHandlers.remove(apiName);
        catchAllApiHandler = this.apiHandlers.get(CATCHALL_API_NAME);
        return this;
    }

    /**
     * Alias for {@link #addApiHandler(String, IApiHandler)}.
     *
     * @param apiName
     * @param apiHandler
     * @return
     */
    public ApiRouter registerApiHandler(String apiName, IApiHandler apiHandler) {
        return addApiHandler(apiName, apiHandler);
    }

    /**
     * Alias for {@link #removeApiHandler(String)}.
     *
     * @param apiName
     * @return
     */
    public ApiRouter unregisterApiHandler(String apiName) {
        return removeApiHandler(apiName);
    }

    /**
     * Get number of total concurrent API calls.
     *
     * @return
     */
    public long getConcurency() {
        return totalConcurrency.get();
    }

    /**
     * Get number of concurrent API calls.
     *
     * @param apiName
     * @return
     * @since 1.0.0
     */
    public long getConcurency(String apiName) {
        return apiConcurrency.get(apiName);
    }

    /**
     * Call an API.
     *
     * @param context
     * @param auth
     * @param params
     * @return
     * @throws Exception
     */
    public ApiResult callApi(ApiContext context, ApiAuth auth, ApiParams params) {
        String apiName = context.getApiName();
        try {
            ApiResult apiResult;
            totalConcurrency.incrementAndGet();
            apiConcurrency.incrementAndGet(apiName);
            try {
                IApiHandler apiHandler = getApiHandler(apiName);
                apiHandler = apiHandler != null ? apiHandler : catchAllApiHandler;
                if (apiHandler == null) {
                    apiResult = new ApiResult(ApiResult.STATUS_NOT_IMPLEMENTED, "No handler for api [" + apiName + "]");
                } else {
                    apiResult = apiFilter != null ?
                            apiFilter.call(apiHandler, context, auth, params) :
                            apiHandler.handle(context, auth, params);
                }
            } catch (Exception e) {
                apiResult = new ApiResult(ApiResult.STATUS_ERROR_SERVER,
                        e.getClass().getName() + " - " + e.getMessage());
                LOGGER.error(e.getMessage(), e);
            }
            return apiResult;
        } finally {
            totalConcurrency.decrementAndGet();
            apiConcurrency.decrementAndGet(apiName);
        }
    }
}
