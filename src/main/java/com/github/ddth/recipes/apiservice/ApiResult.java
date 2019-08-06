package com.github.ddth.recipes.apiservice;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.ddth.commons.utils.SerializationUtils;

/**
 * Result from API call.
 *
 * <p>
 * API result has 4 fields:
 * </p>
 *
 * <pre>
 * - status : (int/required) result status,
 * - message: (string/optional) result message,
 * - data   : (object/optional) result data,
 * - debug  : (object/optional) debug data,
 * </pre>
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since v0.2.0
 */
public class ApiResult implements Cloneable {
    public final static int STATUS_OK = 200;

    public final static int STATUS_ERROR_CLIENT = 400;
    public final static int STATUS_NO_PERMISSION = 403;
    public final static int STATUS_NOT_FOUND = 404;
    public final static int STATUS_DEPRECATED = 410;

    public final static int STATUS_ERROR_SERVER = 500;
    public final static int STATUS_NOT_IMPLEMENTED = 501;

    public final static String FIELD_STATUS = "status";
    public final static String FIELD_MESSAGE = "msg";
    public final static String FIELD_DATA = "data";
    public final static String FIELD_DEBUG = "debug";

    public final static String MSG_OK = "Ok";

    public final static ApiResult DEFAULT_RESULT_OK = new ApiResult(STATUS_OK, MSG_OK);
    public final static ApiResult DEFAULT_RESULT_API_DEPRECATED = new ApiResult(STATUS_DEPRECATED,
            "API is deprecated.");

    public final static ApiResult DEFAULT_RESULT_API_NOT_FOUND = new ApiResult(STATUS_ERROR_CLIENT, "API not found");
    public final static ApiResult DEFAULT_RESULT_NOT_FOUND = new ApiResult(STATUS_NOT_FOUND, "Item not found");
    public final static ApiResult DEFAULT_RESULT_ACCESS_DENIED = new ApiResult(STATUS_NO_PERMISSION, "Access denied");
    public final static ApiResult DEFAULT_RESULT_UNKNOWN_ERROR = new ApiResult(STATUS_ERROR_SERVER,
            "Unknown error while calling API");

    public static ApiResult resultOk() {
        return DEFAULT_RESULT_OK;
    }

    public static ApiResult resultOk(String message) {
        return new ApiResult(STATUS_OK, message);
    }

    public static ApiResult resultOk(String message, Object data) {
        return new ApiResult(STATUS_OK, message, data);
    }

    public static ApiResult resultOk(Object data, Object debugData) {
        return new ApiResult(STATUS_OK, MSG_OK, data).setDebugData(debugData);
    }

    public static ApiResult resultOk(String message, Object data, Object debugData) {
        return new ApiResult(STATUS_OK, message, data).setDebugData(debugData);
    }

    public static ApiResult resultOk(Object data) {
        return new ApiResult(STATUS_OK, MSG_OK, data);
    }

    public static ApiResult resultDeprecated() {
        return resultDeprecated(null);
    }

    public static ApiResult resultDeprecated(String newApi) {
        return newApi == null ?
                DEFAULT_RESULT_API_DEPRECATED :
                new ApiResult(STATUS_DEPRECATED, "API is deprecated. Please migrate to new API [" + newApi + "].");
    }

    private int status;
    private String message;
    private Object data;
    private Object debugData;

    public ApiResult(int status) {
        this(status, null, null);
    }

    public ApiResult(int status, String message) {
        this(status, message, null);
    }

    public ApiResult(int status, Object data) {
        this(status, null, data);
    }

    public ApiResult(int status, String message, Object data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public ApiResult(int status, String message, JsonNode dataJson) {
        this(status, message, SerializationUtils.fromJson(dataJson));
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }

    public JsonNode getDataAsJson() {
        return data != null ? SerializationUtils.toJson(data) : null;
    }

    public ApiResult setDebugData(Object debugData) {
        synchronized (this) {
            this.debugData = debugData;
            this.map = null;
            this.jsonNode = null;
            return this;
        }
    }

    public ApiResult setDebugData(JsonNode debugDataJson) {
        return setDebugData(SerializationUtils.fromJson(debugDataJson));
    }

    public Object getDebugData() {
        return debugData;
    }

    public JsonNode getDebugDataAsJson() {
        return debugData != null ? SerializationUtils.toJson(debugData) : null;
    }

    private JsonNode jsonNode;
    private Map<String, Object> map;

    public Map<String, Object> asMap() {
        if (map == null) {
            synchronized (this) {
                if (map == null) {
                    map = new HashMap<>();
                    map.put(FIELD_STATUS, status);
                    if (message != null) {
                        map.put(FIELD_MESSAGE, message);
                    }
                    if (data != null) {
                        map.put(FIELD_DATA, data);
                    }
                    if (debugData != null) {
                        map.put(FIELD_DEBUG, debugData);
                    }
                }
            }
        }
        return map;
    }

    public JsonNode asJson() {
        if (jsonNode == null) {
            synchronized (this) {
                if (jsonNode == null) {
                    jsonNode = SerializationUtils.toJson(asMap());
                }
            }
        }
        return jsonNode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        ToStringBuilder tsb = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
        tsb.append(FIELD_STATUS, status).append(FIELD_MESSAGE, message).append(FIELD_DATA, data)
                .append(FIELD_DEBUG, debugData);
        return tsb.toString();
    }

    /*----------------------------------------------------------------------*/

    /**
     * {@inheritDoc}
     */
    @Override
    public ApiResult clone() {
        try {
            ApiResult clone = (ApiResult) super.clone();
            clone.map = null;
            clone.jsonNode = null;
            return clone;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    /**
     * Clone and override debug data.
     *
     * @param debugData
     * @return
     */
    public ApiResult clone(Object debugData) {
        ApiResult clone = clone();
        clone.setDebugData(debugData);
        return clone;
    }
}
