package com.github.ddth.recipes.apiservice.grpc;

import com.github.ddth.commons.utils.MapUtils;
import com.github.ddth.recipes.apiservice.*;
import com.github.ddth.recipes.apiservice.grpc.def.PApiServiceGrpc;
import com.github.ddth.recipes.apiservice.grpc.def.PApiServiceProto;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        Empty result = Empty.getDefaultInstance();
        responseObserver.onNext(result);
        responseObserver.onCompleted();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void check(PApiServiceProto.PApiAuth request,
            StreamObserver<PApiServiceProto.PApiResult> responseObserver) {
        long t = System.currentTimeMillis();
        long d = System.currentTimeMillis() - t;
        long c = apiRouter.getConcurency();
        PApiServiceProto.PApiResult result = GrpcApiUtils.buildResponse(
                ApiResult.DEFAULT_RESULT_OK.clone(MapUtils.createMap("t", t, "d", d, "c", c)),
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
        long t = System.currentTimeMillis();
        PApiServiceProto.PApiResult result = null;
        try {
            PApiServiceProto.PApiParams _apiParams = request.getApiParams();
            ApiParams apiParams = GrpcApiUtils.parseParams(_apiParams);
            ApiContext apiContext = ApiContext.newContext("GRPC", request.getApiName());
            ApiAuth apiAuth = GrpcApiUtils.parseAuth(request.getApiAuth());
            ApiResult apiResult = apiRouter.callApi(apiContext, apiAuth, apiParams);
            PApiServiceProto.PDataEncoding returnedEncoding =
                    _apiParams.getExpectedReturnEncoding() == null
                            || _apiParams.getExpectedReturnEncoding()
                            == PApiServiceProto.PDataEncoding.JSON_DEFAULT ? _apiParams
                            .getEncoding() : _apiParams.getExpectedReturnEncoding();
            result = GrpcApiUtils.buildResponse(
                    apiResult != null ? apiResult : ApiResult.DEFAULT_RESULT_UNKNOWN_ERROR,
                    returnedEncoding);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
            long d = System.currentTimeMillis() - t;
            long c = apiRouter.getConcurency();
            result = GrpcApiUtils.buildResponse(new ApiResult(ApiResult.STATUS_ERROR_SERVER,
                            e.getClass().getName() + " - " + e.getMessage())
                            .setDebugData(MapUtils.createMap("t", t, "d", d, "c", c)),
                    PApiServiceProto.PDataEncoding.JSON_STRING);
        }
        responseObserver.onNext(result);
        responseObserver.onCompleted();
    }
}
