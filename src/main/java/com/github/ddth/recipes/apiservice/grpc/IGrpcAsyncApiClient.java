package com.github.ddth.recipes.apiservice.grpc;

import com.github.ddth.recipes.apiservice.grpc.def.PApiServiceProto;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.protobuf.Empty;

/**
 * gRPC Async API client interface.
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.2.0
 */
public interface IGrpcAsyncApiClient {
    /**
     * Call method "ping" (test if server is online).
     *
     * @param request
     * @return
     */
    ListenableFuture<Empty> ping(Empty request);

    /**
     * Call method "check" (test if server is online).
     *
     * @param request
     * @return
     */
    ListenableFuture<PApiServiceProto.PApiResult> check(PApiServiceProto.PApiAuth request);

    /**
     * Call method "check" (test if server is online).
     *
     * @param appId
     * @param accessToken
     * @return
     */
    ListenableFuture<PApiServiceProto.PApiResult> check(String appId, String accessToken);

    /**
     * Call a server API.
     *
     * @param request
     * @return
     */
    ListenableFuture<PApiServiceProto.PApiResult> call(PApiServiceProto.PApiContext request);

    /**
     * Call a server API, using default data encoding.
     *
     * @param apiName
     * @param appId
     * @param accessToken
     * @param params
     *         API parameters to pass to server
     * @return
     */
    ListenableFuture<PApiServiceProto.PApiResult> call(String apiName, String appId,
            String accessToken, Object params);

    /**
     * Call a server API.
     *
     * @param apiName
     * @param appId
     * @param accessToken
     * @param encoding
     *         data encoding
     * @param params
     *         API parameters to pass to server
     * @return
     */
    ListenableFuture<PApiServiceProto.PApiResult> call(String apiName, String appId,
            String accessToken, PApiServiceProto.PDataEncoding encoding, Object params);
}
