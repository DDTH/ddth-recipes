package com.github.ddth.recipes.apiservice;

import com.github.ddth.commons.utils.DateFormatUtils;
import com.github.ddth.commons.utils.MapUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * API running context.
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since v0.2.0
 */
public class ApiContext {

    /**
     * Helper method to create new {@link ApiContext}.
     *
     * @param apiName
     *         name of the API being called
     * @return
     */
    public static ApiContext newContext(String apiName) {
        return new ApiContext(apiName);
    }

    /**
     * Helper method to create new {@link ApiContext}.
     *
     * @param gateway
     *         via which gateway the API is being called
     * @param apiName
     *         name of the API being called
     * @return
     */
    public static ApiContext newContext(String gateway, String apiName) {
        ApiContext context = newContext(apiName);
        return context.setContextField(CTX_GATEWAY, gateway);
    }

    public final static String CTX_API_NAME = "api_name";
    public final static String CTX_GATEWAY = "gateway";

    private Map<String, Object> context = new ConcurrentHashMap<>();
    private String id;
    private Date timestamp = new Date();

    /**
     * Construct a new {@link ApiContext} object.
     *
     * @param apiName
     *         name of the API being called
     */
    public ApiContext(String apiName) {
        this(null, apiName, null);
    }

    /**
     * Construct a new {@link ApiContext} object.
     *
     * @param gateway
     *         via which gateway the API is being called
     * @param apiName
     *         name of the API being called
     */
    public ApiContext(String gateway, String apiName) {
        this(gateway, apiName, null);
    }

    /**
     * Construct a new {@link ApiContext} object.
     *
     * @param gateway
     *         via which gateway the API is being called
     * @param apiName
     *         name of the API being called
     * @param contextData
     */
    public ApiContext(String gateway, String apiName, Map<String, Object> contextData) {
        setContextField(CTX_API_NAME, apiName);
        setContextField(CTX_GATEWAY, gateway);
        if (contextData != null) {
            this.context.putAll(contextData);
        }
    }

    /**
     * Get the unique id associated with this API call.
     *
     * @return
     */
    public String getId() {
        if (id == null) {
            synchronized (this) {
                if (id == null) {
                    id = UUID.randomUUID().toString().toLowerCase();
                }
            }
        }
        return id;
    }

    /**
     * Set a unique id associated with this API call.
     *
     * @param value
     * @return
     */
    public ApiContext setId(String value) {
        this.id = value;
        return this;
    }

    /**
     * Get this API call's timestamp.
     *
     * @return
     */
    public Date getTimestamp() {
        return timestamp;
    }

    /**
     * Get all context fields' values.
     *
     * @return
     */
    public Map<String, Object> getAllContextFields() {
        return Collections.unmodifiableMap(context);
    }

    /**
     * Set a context value.
     *
     * @param name
     * @param value
     * @return
     */
    public ApiContext setContextField(String name, Object value) {
        if (value == null) {
            context.remove(name);
        } else {
            context.put(name, value);
        }
        return this;
    }

    /**
     * Get a context value.
     *
     * @param name
     * @return
     */
    public Object getContextField(String name) {
        return getContextField(name, Object.class);
    }

    /**
     * Get a context value.
     *
     * @param name
     * @return
     */
    public Optional<Object> getContextFieldOptional(String name) {
        return getContextFieldOptional(name, Object.class);
    }

    /**
     * Get a context value.
     *
     * @param name
     * @param clazz
     * @return
     */
    public <T> T getContextField(String name, Class<T> clazz) {
        return MapUtils.getValue(context, name, clazz);
    }

    /**
     * Get a context value.
     *
     * @param name
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> Optional<T> getContextFieldOptional(String name, Class<T> clazz) {
        return Optional.ofNullable(getContextField(name, clazz));
    }

    /**
     * Get context value: API name (name of the API being called)
     *
     * @return
     */
    public String getApiName() {
        return getContextField(CTX_API_NAME, String.class);
    }

    /**
     * Get context value: API gateway (via which gateway the API is being called, e.g. "web" or
     * "thrift").
     *
     * @return
     */
    public String getGateway() {
        return getContextField(CTX_GATEWAY, String.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        ToStringBuilder tsb = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
        tsb.append("id", getId()).append("timestamp",
                DateFormatUtils.toString(getTimestamp(), DateFormatUtils.DF_ISO8601))
                .append("context", context);
        return tsb.toString();
    }
}
