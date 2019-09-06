package com.github.ddth.recipes.apiservice.logging;

import com.github.ddth.commons.utils.MapUtils;
import com.github.ddth.recipes.apiservice.*;

import java.util.Map;

/**
 * This {@link IApiLogger} is base class to implement API performance logger.
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since v1.1.0
 */
public abstract class AbstractPerfApiLogger implements IApiLogger {
    private String fieldId = "id";
    private String fieldStage = "s";
    private String fieldApiName = "api";
    private String fieldGateway = "gw";
    private String fieldTimestampStart = "t";
    private String fieldDuration = "d";
    private String fieldTotalConcurrency = "c_total";
    private String fieldApiConcurrency = "c_api";

    public String getFieldId() {
        return fieldId;
    }

    public AbstractPerfApiLogger setFieldId(String fieldId) {
        this.fieldId = fieldId;
        return this;
    }

    public String getFieldStage() {
        return fieldStage;
    }

    public AbstractPerfApiLogger setFieldStage(String fieldStage) {
        this.fieldStage = fieldStage;
        return this;
    }

    public String getFieldApiName() {
        return fieldApiName;
    }

    public AbstractPerfApiLogger setFieldApiName(String fieldApiName) {
        this.fieldApiName = fieldApiName;
        return this;
    }

    public String getFieldGateway() {
        return fieldGateway;
    }

    public AbstractPerfApiLogger setFieldGateway(String fieldGateway) {
        this.fieldGateway = fieldGateway;
        return this;
    }

    public String getFieldTimestampStart() {
        return fieldTimestampStart;
    }

    public AbstractPerfApiLogger setFieldTimestampStart(String fieldTimestampStart) {
        this.fieldTimestampStart = fieldTimestampStart;
        return this;
    }

    public String getFieldDuration() {
        return fieldDuration;
    }

    public AbstractPerfApiLogger setFieldDuration(String fieldDuration) {
        this.fieldDuration = fieldDuration;
        return this;
    }

    public String getFieldTotalConcurrency() {
        return fieldTotalConcurrency;
    }

    public AbstractPerfApiLogger setFieldTotalConcurrency(String fieldTotalConcurrency) {
        this.fieldTotalConcurrency = fieldTotalConcurrency;
        return this;
    }

    public String getFieldApiConcurrency() {
        return fieldApiConcurrency;
    }

    public AbstractPerfApiLogger setFieldApiConcurrency(String fieldApiConcurrency) {
        this.fieldApiConcurrency = fieldApiConcurrency;
        return this;
    }

    /**
     * Sub-class implements this method to write log data.
     *
     * @param data
     */
    protected abstract void writeLog(Map<String, Object> data);

    /**
     * {@inheritDoc}
     */
    @Override
    public void preApiCall(long totalConcurrency, long apiConcurrency, ApiContext context, ApiAuth auth,
            ApiParams params) {
        Map<String, Object> data = MapUtils
                .createMap(fieldId, context.getId(), fieldStage, "START", fieldApiName, context.getApiName(),
                        fieldGateway, context.getGateway(), fieldTimestampStart, context.getTimestamp().getTime(),
                        fieldTotalConcurrency, totalConcurrency, fieldApiConcurrency, apiConcurrency);
        writeLog(data);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void postApiCall(long durationMs, long totalConcurrency, long apiConcurrency, ApiContext context,
            ApiAuth auth, ApiParams params, ApiResult result) {
        Map<String, Object> data = MapUtils
                .createMap(fieldId, context.getId(), fieldStage, "END", fieldApiName, context.getApiName(),
                        fieldGateway, context.getGateway(), fieldTimestampStart, context.getTimestamp().getTime(),
                        fieldDuration, durationMs, fieldTotalConcurrency, totalConcurrency, fieldApiConcurrency,
                        apiConcurrency);
        writeLog(data);
    }
}
