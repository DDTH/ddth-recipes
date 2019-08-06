package com.github.ddth.recipes.apiservice.grpc;

import com.github.ddth.recipes.apiservice.clientpool.ApiClientPool;
import com.github.ddth.recipes.apiservice.clientpool.HostAndPort;
import com.github.ddth.recipes.apiservice.grpc.def.PApiServiceGrpc;
import com.github.ddth.recipes.apiservice.grpc.def.PApiServiceProto;
import com.google.protobuf.Empty;
import io.grpc.ManagedChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLException;

/**
 * gRPC API client.
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since v0.2.0
 */
public class GrpcApiClient extends BaseGrpcApiClient implements IGrpcApiClient {
    private final Logger LOGGER = LoggerFactory.getLogger(GrpcApiClient.class);

    private PApiServiceGrpc.PApiServiceBlockingStub[] stubs;

    private PApiServiceGrpc.PApiServiceBlockingStub createStub(int serverIndexHash) throws SSLException {
        HostAndPort[] serverHostAndPortList = getServerHostAndPortList();
        HostAndPort hostAndPort = serverHostAndPortList[serverIndexHash % serverHostAndPortList.length];
        ManagedChannel channel = isUseOkHttp() ?
                buildManagedChannelOkHttp(hostAndPort) :
                buildManagedChannelNetty(hostAndPort);
        return PApiServiceGrpc.newBlockingStub(channel);
    }

    /**
     * Initializing method.
     *
     * @return
     */
    public GrpcApiClient init() throws Exception {
        super.init();
        if (stubs == null) {
            int numServers = getServerHostAndPortList().length;
            stubs = new PApiServiceGrpc.PApiServiceBlockingStub[numServers];
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
            for (PApiServiceGrpc.PApiServiceBlockingStub stub : stubs) {
                try {
                    ((ManagedChannel) stub.getChannel()).shutdown();
                } catch (Exception e) {
                    LOGGER.warn(e.getMessage(), e);
                }
            }
            stubs = null;
        }
        super.destroy();
    }

    /*----------------------------------------------------------------------*/

    /**
     * {@inheritDoc}
     */
    @Override
    public Empty ping(Empty request) {
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
    public PApiServiceProto.PApiResult check(PApiServiceProto.PApiAuth request) {
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
    public PApiServiceProto.PApiResult call(PApiServiceProto.PApiContext request) {
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
    public PApiServiceProto.PApiResult call(String apiName, String appId, String accessToken,
            PApiServiceProto.PDataEncoding encoding, Object params) {
        return call(buildRequest(apiName, appId, accessToken, encoding, params));
    }
}
