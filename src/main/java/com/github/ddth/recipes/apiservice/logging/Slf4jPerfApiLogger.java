package com.github.ddth.recipes.apiservice.logging;

import com.github.ddth.commons.utils.SerializationUtils;
import com.github.ddth.recipes.apiservice.IApiLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * This {@link IApiLogger} writes API performance logs to a {@link Logger} in JSON format.
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since v1.1.0
 */
public class Slf4jPerfApiLogger extends AbstractPerfApiLogger {
    private Logger logger = LoggerFactory.getLogger(Slf4jPerfApiLogger.class);

    public Logger getLogger() {
        return logger;
    }

    public Slf4jPerfApiLogger setLogger(Logger logger) {
        this.logger = logger;
        return this;
    }

    /**
     * {@@inheritDoc}
     */
    @Override
    protected void writeLog(Map<String, Object> data) {
        if (logger != null && logger.isInfoEnabled()) {
            logger.info(SerializationUtils.toJsonString(data));
        }
    }
}
