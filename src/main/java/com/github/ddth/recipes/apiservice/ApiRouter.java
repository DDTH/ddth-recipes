package com.github.ddth.recipes.apiservice;

import com.github.ddth.commons.utils.DateFormatUtils;
import com.github.ddth.commons.utils.MapUtils;
import com.github.ddth.recipes.apiservice.auth.AllowAllApiAuthenticator;
import com.google.common.util.concurrent.AtomicLongMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Route API calls to handlers.
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since v0.2.0
 */
public class ApiRouter implements AutoCloseable {
    private Logger LOGGER = LoggerFactory.getLogger(ApiRouter.class);

    private Map<String, IApiHandler> apiHandlers = new ConcurrentHashMap<>();
    private IApiAuthenticator apiAuthenticator = AllowAllApiAuthenticator.instance;
    private IApiLogger apiLogger;

    private AtomicLong totalConcurrency = new AtomicLong(0);
    private AtomicLongMap<String> apiConcurrency = AtomicLongMap.create();

    /**
     * Init method.
     *
     * @return
     */
    public ApiRouter init() {
        if (getApiAuthenticator() == null) {
            setApiAuthenticator(AllowAllApiAuthenticator.instance);
        }
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

    /**
     * Getter for {@link #apiHandlers}.
     *
     * @return
     */
    public Map<String, IApiHandler> getApiHandlers() {
        return Collections.unmodifiableMap(apiHandlers);
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
        return registerApiHandler(CATCHALL_API_NAME, handler);
    }

    /**
     * Unset the handler that "catch all" API calls when there is no matched
     * handler.
     *
     * @return
     */
    public ApiRouter unsetCatchAllHandler() {
        return unregisterApiHandler(CATCHALL_API_NAME);
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
        catchAllApiHandler = this.apiHandlers.get(CATCHALL_API_NAME);
        return this;
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
     * Getter for {@link #apiAuthenticator}.
     *
     * @return
     */
    public IApiAuthenticator getApiAuthenticator() {
        return apiAuthenticator;
    }

    /**
     * Setter for {@link #apiAuthenticator}.
     *
     * @param apiAuthenticator
     * @return
     */
    public ApiRouter setApiAuthenticator(IApiAuthenticator apiAuthenticator) {
        this.apiAuthenticator = apiAuthenticator;
        return this;
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
    protected IApiLogger getApiLogger() {
        return apiLogger;
    }

    /**
     * Setter for {@link #apiLogger}.
     *
     * @param logger
     * @return
     * @deprecated use {@link #setApiLogger(IApiLogger)}
     */
    public ApiRouter setApiLogger(Logger logger) {
        return this;
    }

    /**
     * Setter for {@link #apiLogger}.
     *
     * @param loggerName
     * @return
     * @deprecated use {@link #setApiLogger(IApiLogger)}
     */
    public ApiRouter setApiLoggerName(String loggerName) {
        return this;
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
        Date now = new Date();
        ApiResult apiResult = null;
        String apiName = context.getApiName();
        try {
            totalConcurrency.incrementAndGet();
            apiConcurrency.incrementAndGet(apiName);
            if (apiLogger != null) {
                apiLogger.preApiCall(totalConcurrency.get(), apiConcurrency.get(apiName), context, auth, params);
            }

            IApiAuthenticator apiAuthenticator = getApiAuthenticator();
            if (apiAuthenticator != null && !apiAuthenticator.authenticate(context, auth)) {
                apiResult = new ApiResult(ApiResult.STATUS_NO_PERMISSION,
                        "App [" + auth.getAppId() + "] is not allowed to call api [" + apiName + "] via [" + context
                                .getGateway() + "]");
            } else {
                try {
                    IApiHandler apiHandler = apiName != null ? apiHandlers.get(apiName) : null;
                    apiHandler = apiHandler != null ? apiHandler : catchAllApiHandler;
                    if (apiHandler == null) {
                        apiResult = new ApiResult(ApiResult.STATUS_NOT_IMPLEMENTED,
                                "No handler for api [" + apiName + "]");
                    } else {
                        apiResult = apiHandler.handle(context, auth, params);
                    }
                } catch (Exception e) {
                    apiResult = new ApiResult(ApiResult.STATUS_ERROR_SERVER,
                            e.getClass().getName() + " - " + e.getMessage());
                    LOGGER.error(e.getMessage(), e);
                }
            }
            return apiResult.setDebugData(
                    MapUtils.createMap("t", now, "tstr", DateFormatUtils.toString(now, DateFormatUtils.DF_ISO8601), "d",
                            System.currentTimeMillis() - now.getTime(), "c", totalConcurrency.get()));
        } finally {
            totalConcurrency.decrementAndGet();
            apiConcurrency.decrementAndGet(apiName);
            if (apiLogger != null) {
                apiLogger.postApiCall(System.currentTimeMillis() - now.getTime(), context, auth, params, apiResult);
            }
        }
    }
}
