package com.github.ddth.recipes.apiservice.grpc;

import com.github.ddth.commons.utils.DateFormatUtils;
import com.github.ddth.commons.utils.MapUtils;
import com.github.ddth.recipes.apiservice.*;
import com.github.ddth.recipes.apiservice.grpc.def.PApiServiceGrpc;
import com.github.ddth.recipes.apiservice.grpc.def.PApiServiceProto;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Handle API calls via gRPC.
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since v0.2.0
 */
public class ApiServiceHandler extends PApiServiceGrpc.PApiServiceImplBase {
    private final Logger LOGGER = LoggerFactory.getLogger(ApiServiceHandler.class);

    private ApiRouter apiRouter;

    public ApiServiceHandler() {
    }

    public ApiServiceHandler(ApiRouter apiRouter) {
        setApiRouter(apiRouter);
    }

    public ApiRouter getApiRouter() {
        return apiRouter;
    }

    public ApiServiceHandler setApiRouter(ApiRouter apiRouter) {
        this.apiRouter = apiRouter;
        return this;
    }

    /*------------------------------------------------------------*/

    /**
     * {@inheritDoc}
     */
    @Override
    public void ping(Empty request, StreamObserver<Empty> responseObserver) {
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void check(PApiServiceProto.PApiAuth request, StreamObserver<PApiServiceProto.PApiResult> responseObserver) {
        Date now = new Date();
        PApiServiceProto.PApiResult result = GrpcUtils.buildResponse(ApiResult.DEFAULT_RESULT_OK.clone(MapUtils
                        .createMap("t", now.getTime(), "tstr", DateFormatUtils.toString(now, DateFormatUtils.DF_ISO8601), "d",
                                System.currentTimeMillis() - now.getTime(), "c", apiRouter.getConcurency())),
                PApiServiceProto.PDataEncoding.JSON_STRING);
        responseObserver.onNext(result);
        responseObserver.onCompleted();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void call(PApiServiceProto.PApiContext request,
            StreamObserver<PApiServiceProto.PApiResult> responseObserver) {
        Date now = new Date();
        PApiServiceProto.PApiResult result;
        try {
            PApiServiceProto.PApiParams _apiParams = request.getApiParams();
            ApiParams apiParams = GrpcUtils.parseParams(_apiParams);
            ApiContext apiContext = ApiContext.newContext("GRPC", request.getApiName());
            ApiAuth apiAuth = GrpcUtils.parseAuth(request.getApiAuth());
            ApiResult apiResult = apiRouter.callApi(apiContext, apiAuth, apiParams);
            PApiServiceProto.PDataEncoding returnedEncoding = _apiParams.getExpectedReturnEncoding() == null
                    || _apiParams.getExpectedReturnEncoding() == PApiServiceProto.PDataEncoding.JSON_DEFAULT ?
                    _apiParams.getEncoding() :
                    _apiParams.getExpectedReturnEncoding();
            apiResult = apiResult != null ?
                    apiResult :
                    ApiResult.DEFAULT_RESULT_UNKNOWN_ERROR.clone().setDebugData(
                            MapUtils.createMap("t", now.getTime(), "tstr",
                                    DateFormatUtils.toString(now, DateFormatUtils.DF_ISO8601), "d",
                                    System.currentTimeMillis() - now.getTime(), "c", apiRouter.getConcurency()));
            result = GrpcUtils.buildResponse(apiResult, returnedEncoding);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
            result = GrpcUtils.buildResponse(
                    new ApiResult(ApiResult.STATUS_ERROR_SERVER, e.getClass().getName() + " - " + e.getMessage())
                            .setDebugData(MapUtils.createMap("t", now.getTime(), "tstr",
                                    DateFormatUtils.toString(now, DateFormatUtils.DF_ISO8601), "d",
                                    System.currentTimeMillis() - now.getTime(), "c", apiRouter.getConcurency())),
                    PApiServiceProto.PDataEncoding.JSON_STRING);
        }
        responseObserver.onNext(result);
        responseObserver.onCompleted();
    }
}
