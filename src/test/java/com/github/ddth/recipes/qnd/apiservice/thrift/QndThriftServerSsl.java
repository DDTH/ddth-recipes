package com.github.ddth.recipes.qnd.apiservice.thrift;

import com.github.ddth.recipes.apiservice.ApiResult;
import com.github.ddth.recipes.apiservice.ApiRouter;
import com.github.ddth.recipes.apiservice.thrift.ThriftApiUtils;
import org.apache.thrift.server.TServer;

import java.io.File;

public class QndThriftServerSsl {
    public static void main(String[] args) throws Exception {
        String listenHost = "127.0.0.1";
        int baseListenPort = 9443;
        File keystore = new File("./src/test/resources/keys/server.keystore");
        String keystorePass = "s3cr3t";
        ApiRouter router = new ApiRouter();
        router.setCatchAllHandler(
                (context, auth, params) -> new ApiResult(ApiResult.STATUS_OK, ApiResult.MSG_OK,
                        params.getAllParams()));
        int numServers = 4;
        TServer[] servers = new TServer[numServers];
        Thread[] serverThreads = new Thread[numServers];
        for (int i = 0; i < numServers; i++) {
            servers[i] = ThriftApiUtils
                    .createThriftServerSsl(router, true, listenHost, baseListenPort + i, keystore,
                            keystorePass);
            serverThreads[i] = ThriftApiUtils
                    .startThriftServer(servers[i], "TServerSSL-" + i, true);
            System.out.println(
                    "Started TServerSSL on [" + listenHost + ":" + (baseListenPort + i) + "].");
        }
        for (Thread t : serverThreads) {
            t.join();
        }
    }
}
