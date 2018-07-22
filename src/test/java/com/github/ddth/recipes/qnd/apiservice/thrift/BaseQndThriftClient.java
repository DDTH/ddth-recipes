package com.github.ddth.recipes.qnd.apiservice.thrift;

import com.github.ddth.commons.utils.ThriftUtils;
import com.github.ddth.recipes.apiservice.ApiResult;
import com.github.ddth.recipes.apiservice.thrift.ThriftApiUtils;
import com.github.ddth.recipes.apiservice.thrift.def.TApiResult;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.thrift.TException;

public class BaseQndThriftClient {
    protected static String toString(TApiResult _result) throws TException {
        ApiResult result = ThriftApiUtils.toApiResult(_result);
        StringBuilder sb = new StringBuilder("=== Size: " + ThriftUtils.toBytes(_result).length)
                .append("\n");
        ToStringBuilder tsb = new ToStringBuilder(result, ToStringStyle.SHORT_PREFIX_STYLE);
        tsb.append("status", result.getStatus());
        tsb.append("message", result.getMessage());
        tsb.append("encoding", _result.encoding);
        tsb.append("data", result.getDataAsJson());
        tsb.append("debug", result.getDebugDataAsJson());
        return sb.append(tsb).toString();
    }
}
