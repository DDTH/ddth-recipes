package com.github.ddth.recipes.apiservice.grpc;

import com.github.ddth.recipes.apiservice.clientpool.ApiClientPool;
import com.github.ddth.recipes.apiservice.clientpool.HostAndPort;
import com.github.ddth.recipes.apiservice.grpc.def.PApiServiceGrpc;
import com.github.ddth.recipes.apiservice.grpc.def.PApiServiceProto;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.protobuf.Empty;
import io.grpc.ManagedChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * gRPC Async API client.
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since v0.2.0
 */
public class GrpcAsyncApiClient extends BaseGrpcApiClient implements IGrpcAsyncApiClient {
    private final Logger LOGGER = LoggerFactory.getLogger(GrpcAsyncApiClient.class);

    private PApiServiceGrpc.PApiServiceFutureStub[] stubs;
    private ExecutorService executorService;
    private boolean myOwnExecutorService = false;

    /*----------------------------------------------------------------------*/
    private PApiServiceGrpc.PApiServiceFutureStub createStub(int serverIndexHash) throws SSLException {
        HostAndPort[] serverHostAndPortList = getServerHostAndPortList();
        HostAndPort hostAndPort = serverHostAndPortList[serverIndexHash % serverHostAndPortList.length];
        ManagedChannel channel = isUseOkHttp() ?
                buildManagedChannelOkHttp(hostAndPort) :
                buildManagedChannelNetty(hostAndPort);
        return PApiServiceGrpc.newFutureStub(channel).withExecutor(executorService);
    }

    /**
     * Initializing method.
     *
     * @return
     */
    public GrpcAsyncApiClient init() throws Exception {
        super.init();
        if (executorService == null) {
            executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            myOwnExecutorService = true;
        }
        if (stubs == null) {
            int numServers = getServerHostAndPortList().length;
            stubs = new PApiServiceGrpc.PApiServiceFutureStub[numServers];
            for (int i = 0; i < numServers; i++) {
                stubs[i] = createStub(i);
            }
        }
        return this;
    }

    /**
     * Destroy method.
     */
    public void destroy() {
        if (stubs != null) {
            for (PApiServiceGrpc.PApiServiceFutureStub stub : stubs) {
                try {
                    ((ManagedChannel) stub.getChannel()).shutdown();
                } catch (Exception e) {
                    LOGGER.warn(e.getMessage(), e);
                }
            }
            stubs = null;
        }

        if (executorService != null && myOwnExecutorService) {
            try {
                executorService.shutdown();
            } catch (Exception e) {
                LOGGER.warn(e.getMessage(), e);
            }
            executorService = null;
        }

        super.destroy();
    }

    /*----------------------------------------------------------------------*/

    /**
     * {@inheritDoc}
     */
    @Override
    public ListenableFuture<Empty> ping(Empty request) {
        int numServers = getServerHostAndPortList().length;
        return doWithRetry(getRetryPolicy().cloneReset(), rp -> {
            int serverIndexHash = ApiClientPool.calcNextServerIndexHash(rp, numServers);
            return stubs[serverIndexHash % numServers].ping(request);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ListenableFuture<PApiServiceProto.PApiResult> check(PApiServiceProto.PApiAuth request) {
        int numServers = getServerHostAndPortList().length;
        return doWithRetry(getRetryPolicy().cloneReset(), rp -> {
            int serverIndexHash = ApiClientPool.calcNextServerIndexHash(rp, numServers);
            return stubs[serverIndexHash % numServers].check(request);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ListenableFuture<PApiServiceProto.PApiResult> check(String appId, String accessToken) {
        return check(PApiServiceProto.PApiAuth.newBuilder().setAppId(appId).setAccessToken(accessToken).build());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ListenableFuture<PApiServiceProto.PApiResult> call(PApiServiceProto.PApiContext request) {
        int numServers = getServerHostAndPortList().length;
        return doWithRetry(getRetryPolicy().cloneReset(), rp -> {
            int serverIndexHash = ApiClientPool.calcNextServerIndexHash(rp, numServers);
            return stubs[serverIndexHash % numServers].call(request);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ListenableFuture<PApiServiceProto.PApiResult> call(String apiName, String appId, String accessToken,
            PApiServiceProto.PDataEncoding encoding, Object params) {
        return call(buildRequest(apiName, appId, accessToken, encoding, params));
    }
}
