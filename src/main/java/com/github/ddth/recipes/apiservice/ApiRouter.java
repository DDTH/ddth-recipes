package com.github.ddth.recipes.apiservice;

import com.github.ddth.commons.utils.MapUtils;
import com.github.ddth.recipes.apiservice.auth.AllowAllApiAuthenticator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Route API calls to handlers.
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since v0.2.0
 */
public class ApiRouter implements AutoCloseable {

    private Map<String, IApiHandler> apiHandlers = new ConcurrentHashMap<>();
    private AtomicInteger concurency = new AtomicInteger(0);
    private IApiAuthenticator apiAuthenticator = AllowAllApiAuthenticator.instance;
    private Logger apiLogger = LoggerFactory.getLogger(ApiRouter.class);
    private Logger LOGGER = LoggerFactory.getLogger(ApiRouter.class);

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
     * Set the handler that "catch all" API calls when there is no matched one.
     *
     * @param handler
     * @return
     */
    public ApiRouter setCatchAllHandler(IApiHandler handler) {
        return registerApiHandler(CATCHALL_API_NAME, handler);
    }

    /**
     * Unset the handler that "catch all" API calls when there is no matched
     * one.
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
     * Get number of concurrent API calls.
     *
     * @return
     */
    public long getConcurency() {
        return concurency.get();
    }

    /**
     * Setter for {@link #apiLogger}.
     *
     * @param logger
     * @return
     */
    public ApiRouter setApiLogger(Logger logger) {
        this.apiLogger = logger;
        return this;
    }

    /**
     * Setter for {@link #apiLogger}.
     *
     * @param loggerName
     * @return
     */
    public ApiRouter setApiLoggerName(String loggerName) {
        this.apiLogger = LoggerFactory.getLogger(loggerName);
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
    public ApiResult callApi(ApiContext context, ApiAuth auth, ApiParams params) throws Exception {
        long now = System.currentTimeMillis();
        ApiResult apiResult = null;
        try {
            concurency.incrementAndGet();
            if (apiLogger != null && apiLogger.isInfoEnabled()) {
                apiLogger.info(context.getId() + "\t" + context.getTimestamp() + "\t"
                        + context.getGateway() + "\t" + context.getApiName() + "\tSTART");
            }
            IApiAuthenticator apiAuthenticator = getApiAuthenticator();
            if (apiAuthenticator != null && !apiAuthenticator.authenticate(context, auth)) {
                apiResult = new ApiResult(ApiResult.STATUS_NO_PERMISSION,
                        "App [" + auth.getAppId() + "] is not allowed to call api ["
                                + context.getApiName() + "] via [" + context.getGateway() + "]!");
            } else {
                try {
                    String apiName = context.getApiName();
                    IApiHandler apiHandler = apiName != null ? apiHandlers.get(apiName) : null;
                    apiHandler = apiHandler != null ? apiHandler : catchAllApiHandler;
                    if (apiHandler == null) {
                        apiResult = new ApiResult(ApiResult.STATUS_NOT_FOUND,
                                "No handler for api [" + context.getApiName() + "]!");
                    } else {
                        apiResult = apiHandler.handle(context, auth, params);
                    }
                } catch (Exception e) {
                    apiResult = new ApiResult(ApiResult.STATUS_ERROR_SERVER,
                            e.getClass().getName() + " - " + e.getMessage());
                    LOGGER.error(e.getMessage(), e);
                }
            }
            long d = System.currentTimeMillis() - now;
            return apiResult
                    .setDebugData(MapUtils.createMap("t", now, "d", d, "c", concurency.get()));
        } finally {
            concurency.decrementAndGet();
            long d = System.currentTimeMillis() - now;
            if (apiLogger != null && apiLogger.isInfoEnabled()) {
                apiLogger.info(context.getId() + "\t" + context.getTimestamp() + "\t"
                        + context.getGateway() + "\t" + context.getApiName() + "\tEND\t"
                        + (apiResult != null ? apiResult.getStatus() : "[null]") + "\t" + d);
            }
        }
    }

}
