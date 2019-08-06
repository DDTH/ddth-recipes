package com.github.ddth.recipes.qnd.apiservice;

import com.github.ddth.recipes.apiservice.*;
import com.github.ddth.recipes.apiservice.auth.AllowAllApiAuthenticator;

import java.util.Date;
import java.util.HashMap;

public class QndApiRouter {
    public static void main(String[] args) {
        try (ApiRouter router = new ApiRouter()) {
            router.init();

            router.setApiAuthenticator(AllowAllApiAuthenticator.instance);

            router.registerApiHandler("echo",
                    (context, auth, params) -> new ApiResult(ApiResult.STATUS_OK, ApiResult.MSG_OK,
                            params.getAllParams()));
            ApiResult result = router.callApi(new ApiContext("echo"), new ApiAuth("app-id", "access-token"),
                    new ApiParams(new HashMap<>() {
                        private static final long serialVersionUID = 1L;

                        {
                            put("year", 2018);
                        }
                    }));
            System.out.println(result);

            router.setCatchAllHandler((context, auth, params) -> new ApiResult(ApiResult.STATUS_OK,
                    "Catch-all API is called at " + new Date()));
            ApiContext context = new ApiContext("non-exist-api");
            ApiAuth auth = new ApiAuth("app-id", "access-token");
            System.out.println(router.callApi(context, auth, null));
        }
    }
}
