/**
  * API service definition for Apache Thrift.
  *
  * By Thanh Nguyen <btnguyen2k@gmail.com>
  * Since v0.2.0
  */

/**
rm -rf gen-java \
    && thrift --gen java api_service.thrift \
    && rm -f def/* \
    && mv gen-java/com/github/ddth/recipes/apiservice/thrift/def/* def/ \
    && rm -rf gen-java
*/

namespace java com.github.ddth.recipes.apiservice.thrift.def

enum TDataEncoding {
    JSON_DEFAULT    = 0,    // Request: default=JSON string, Result: default=same encoding as request's
    JSON_STRING     = 1,    // Data is encoded as JSON string
    JSON_GZIP       = 2     // Data is encoded as gzipped JSON string
}

struct TApiAuth {
    1: optional string          appId                   = "",
    2: optional string          accessToken             = ""
}

struct TApiParams {
    1: optional TDataEncoding   encoding                = TDataEncoding.JSON_STRING,
    2: optional binary          paramsData
    3: optional TDataEncoding   expectedReturnEncoding  = TDataEncoding.JSON_DEFAULT
}

struct TApiResult {
    1: i32 status,
    2: optional string          message,
    3: optional TDataEncoding   encoding                = TDataEncoding.JSON_STRING,
    4: optional binary          resultData,
    5: optional binary          debugData
}

service TApiService {
    /**
      * This method is to test if server is online.
      */
    oneway void ping(),

    /**
      * This method is to test if server is online.
      */
    TApiResult check(1:TApiAuth apiAuth),

    /**
      * Invoke API call.
      */
    TApiResult call(1:string apiName, 2:TApiAuth apiAuth, 3:TApiParams apiParams)
}
