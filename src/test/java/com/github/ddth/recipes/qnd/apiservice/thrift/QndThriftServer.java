package com.github.ddth.recipes.qnd.apiservice.thrift;

import com.github.ddth.recipes.apiservice.ApiResult;
import com.github.ddth.recipes.apiservice.ApiRouter;
import com.github.ddth.recipes.apiservice.thrift.ThriftApiUtils;
import org.apache.thrift.server.TServer;

public class QndThriftServer {
    public static void main(String[] args) throws Exception {
        ApiRouter router = new ApiRouter();
        router.setCatchAllHandler(
                (context, auth, params) -> new ApiResult(ApiResult.STATUS_OK, ApiResult.MSG_OK,
                        params.getAllParams()));
        int numServers = 4;
        TServer[] servers = new TServer[numServers];
        Thread[] serverThreads = new Thread[numServers];
        String listenHost = "127.0.0.1";
        int baseListenPort = 9090;
        for (int i = 0; i < numServers; i++) {
            servers[i] = ThriftApiUtils
                    .createThriftServer(router, true, listenHost, baseListenPort + i);
            serverThreads[i] = ThriftApiUtils.startThriftServer(servers[i], "TServer-" + i, true);
            System.out.println(
                    "Started TServer on [" + listenHost + ":" + (baseListenPort + i) + "].");
        }
        for (Thread t : serverThreads) {
            t.join();
        }
    }
}
