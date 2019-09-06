package com.github.ddth.recipes.apiservice.filters;

import com.github.ddth.commons.utils.DateFormatUtils;
import com.github.ddth.commons.utils.MapUtils;
import com.github.ddth.recipes.apiservice.*;

import java.util.Date;
import java.util.Map;

/**
 * This filter adds the following data to the "debug" field of API's result:
 *
 * <pre>
 * {
 *     "t"   : timestamp when the API was called,
 *     "tstr": timestamp as human-readable string,
 *     "d"   : API's execution duration (in millisecond),
 *     "c"   : server's total concurrent API calls
 * }
 * </pre>
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since v1.1.0
 */
public class AddPerfInfoFilter extends ApiFilter {
    public AddPerfInfoFilter() {
    }

    public AddPerfInfoFilter(ApiRouter apiRouter) {
        super(apiRouter);
    }

    public AddPerfInfoFilter(ApiFilter nextFilter) {
        super(nextFilter);
    }

    public AddPerfInfoFilter(ApiRouter apiRouter, ApiFilter nextFilter) {
        super(apiRouter, nextFilter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ApiResult call(IApiHandler apiHandler, ApiContext context, ApiAuth auth, ApiParams params) throws Exception {
        Date now = new Date();
        ApiResult apiResult = nextFilterOrHandler(apiHandler, context, auth, params);
        Map<?, ?> perfData = MapUtils
                .createMap("t", now.getTime(), "tstr", DateFormatUtils.toString(now, DateFormatUtils.DF_ISO8601), "d",
                        System.currentTimeMillis() - now.getTime(), "c", getApiRouter().getConcurency());
        Object debugData = apiResult.getDebugData();
        if (debugData == null || debugData instanceof Map) {
            if (debugData != null) {
                perfData.putAll((Map) debugData);
            }
            apiResult.setDebugData(perfData);
        }
        return apiResult;
    }
}
