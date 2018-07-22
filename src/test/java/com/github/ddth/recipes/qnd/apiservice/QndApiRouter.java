package com.github.ddth.recipes.qnd.apiservice;

import java.util.Date;
import java.util.HashMap;

import com.github.ddth.recipes.apiservice.ApiAuth;
import com.github.ddth.recipes.apiservice.ApiContext;
import com.github.ddth.recipes.apiservice.ApiParams;
import com.github.ddth.recipes.apiservice.ApiResult;
import com.github.ddth.recipes.apiservice.ApiRouter;
import com.github.ddth.recipes.apiservice.auth.AllowAllApiAuthenticator;

public class QndApiRouter {

    public static void main(String[] args) throws Exception {
        try (ApiRouter router = new ApiRouter()) {
            router.init();

            router.setApiAuthenticator(AllowAllApiAuthenticator.instance);

            router.registerApiHandler("echo",
                    (context, auth, params) -> new ApiResult(ApiResult.STATUS_OK, ApiResult.MSG_OK,
                            params.getAllParams()));
            ApiResult result = router.callApi(new ApiContext("echo"),
                    new ApiAuth("app-id", "access-token"),
                    new ApiParams(new HashMap<String, Object>() {
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
