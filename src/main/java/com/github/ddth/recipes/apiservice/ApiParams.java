package com.github.ddth.recipes.apiservice;

import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.ddth.commons.utils.JacksonUtils;
import com.github.ddth.commons.utils.SerializationUtils;

/**
 * Parameters passed to API.
 *
 * <p>
 * Parameters sent by client to API must be a map {key -> value}
 * </p>
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since v0.2.0
 */
public class ApiParams implements Cloneable {
    private JsonNode params;

    /**
     * Construct a new {@link ApiParams} object.
     */
    public ApiParams() {
        params = JsonNodeFactory.instance.objectNode();
    }

    /**
     * Construct a new {@link ApiParams} object.
     *
     * @param params must be an {@link ObjectNode} or {@code null}
     */
    public ApiParams(JsonNode params) {
        this.params = params != null && !(params instanceof NullNode) && !(params instanceof MissingNode) ?
                params :
                JsonNodeFactory.instance.objectNode();
        ensureParams();
    }

    public ApiParams clone() {
        try {
            ApiParams clone = (ApiParams) super.clone();
            clone.params = this.params.deepCopy();
            return clone;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    /**
     * Construct a new {@link ApiParams} object.
     *
     * @param params
     */
    public ApiParams(Map<String, Object> params) {
        this.params = params != null ? JacksonUtils.toJson(params) : JsonNodeFactory.instance.objectNode();
        ensureParams();
    }

    /**
     * Construct a new {@link ApiParams} object.
     *
     * @param params must be serialized to {@link ObjectNode}
     */
    public ApiParams(Object params) {
        this.params = params != null && !(params instanceof NullNode) && !(params instanceof MissingNode) ?
                JacksonUtils.toJson(params) :
                JsonNodeFactory.instance.objectNode();
        ensureParams();
    }

    private void ensureParams() {
        if (!(params instanceof ObjectNode)) {
            throw new IllegalArgumentException(
                    "Parameters must be of type [" + ObjectNode.class.getName() + "], current type: " + (
                            params != null ? params.getClass().getName() : "[null]"));
        }
    }

    /**
     * Add a single parameter (existing one will be overridden).
     *
     * @param dpath
     * @param value
     * @return
     */
    public ApiParams addParam(String dpath, Object value) {
        JacksonUtils.setValue(params, dpath, value);
        return this;
    }

    /**
     * Return all parameters.
     *
     * @return
     */
    public JsonNode getAllParams() {
        return params.deepCopy();
    }

    /**
     * Return all as map.
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getAllParamsAsMap() {
        return SerializationUtils.fromJson(params, Map.class);
    }

    /**
     * Get a parameter value.
     *
     * @param dpath
     * @return
     */
    public JsonNode getParam(String dpath) {
        return JacksonUtils.getValue(params, dpath);
    }

    /**
     * Get a parameter value.
     *
     * @param dpath
     * @return
     */
    public Optional<JsonNode> getParamOptional(String dpath) {
        return Optional.ofNullable(getParam(dpath));
    }

    /**
     * Get a parameter value.
     *
     * @param dpath
     * @param clazz
     * @return
     */
    public <T> T getParam(String dpath, Class<T> clazz) {
        return JacksonUtils.getValue(params, dpath, clazz);
    }

    /**
     * Get a parameter value.
     *
     * @param dpath
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> Optional<T> getParamOptional(String dpath, Class<T> clazz) {
        return Optional.ofNullable(getParam(dpath, clazz));
    }

    /**
     * Get a parameter value, if failed go to next parameter, and so on.
     *
     * @param dpaths
     * @return
     */
    public JsonNode getParamOr(String... dpaths) {
        if (dpaths != null) {
            for (String dpath : dpaths) {
                JsonNode result = getParam(dpath);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    /**
     * Get a parameter value, if failed go to next parameter, and so on.
     *
     * @param dpaths
     * @return
     */
    public Optional<JsonNode> getParamOrOptional(String... dpaths) {
        return Optional.ofNullable(getParamOr(dpaths));
    }

    /**
     * Get a parameter value, if failed go to next parameter, and so on.
     *
     * @param clazz
     * @param dpaths
     * @return
     */
    public <T> T getParamOr(Class<T> clazz, String... dpaths) {
        if (dpaths != null) {
            for (String name : dpaths) {
                T result = getParam(name, clazz);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    /**
     * Get a parameter value, if failed go to next parameter, and so on.
     *
     * @param clazz
     * @param dpaths
     * @param <T>
     * @return
     */
    public <T> Optional<T> getParamOrOptional(Class<T> clazz, String... dpaths) {
        return Optional.ofNullable(getParamOr(clazz, dpaths));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        ToStringBuilder tsb = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
        tsb.append("params", params);
        return tsb.toString();
    }
}
