# ddth-recipes: API Service

_API Service recipe added since version `v0.2.0`._

API Service recipe creates a framework that help building API servers and clients using HTTP(S), Thrift and gRPC.

## Maven Dependency

If you plan to build API service over HTTP(S) only (for example: a REST API set),
besides libraries required by the web-framework your project is using,
`ddth-commons-core` and `ddth-commons-serialization` are needed:

```xml
<dependency>
    <groupId>com.github.ddth</groupId>
    <artifactId>ddth-commons-core</artifactId>
    <version>${ddth-commons-version}</version>
</dependency>
<dependency>
    <groupId>com.github.ddth</groupId>
    <artifactId>ddth-commons-serialization</artifactId>
    <version>${ddth-commons-version}</version>
    <type>pom</type>
</dependency>
```

API Service recipe provides only the skeleton to build your APIs.
How APIs are served or called over HTTP(S) is totally up to you.

### API Service over Apache Thrift

For server:

```xml
<dependency>
    <groupId>com.github.ddth</groupId>
    <artifactId>ddth-commons-core</artifactId>
    <version>${ddth-commons-version}</version>
</dependency>
<dependency>
    <groupId>com.github.ddth</groupId>
    <artifactId>ddth-commons-serialization</artifactId>
    <version>${ddth-commons-version}</version>
    <type>pom</type>
</dependency>
<dependency>
    <groupId>org.apache.thrift</groupId>
	<artifactId>libthrift</artifactId>
	<version>${libthrift-version}</version>
</dependency>
```

For client:

Same libraries as server, plus `commons-pool2` for pooling Thrift clients.
Highly recommended as Thrift client is _not_ thread-safe.

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-pool2</artifactId>
    <version>${commons-pool2-version}</version>
</dependency>
```

### API Service over gRPC

For server:

- Google Guava v20.0+
- `protobuf-java`
- `grpc-protobuf`, `grpc-stub` and `grpc-netty`
- `netty-tcnative-boringssl-static` or `netty-tcnative` for transport security (SSL/TLS). See [https://github.com/grpc/grpc-java/blob/master/SECURITY.md](https://github.com/grpc/grpc-java/blob/master/SECURITY.md).

```xml
<dependency>
    <groupId>com.google.guava</groupId>
	<artifactId>guava</artifactId>
	<version>${guava-version}</version>
</dependency>
<dependency>
    <groupId>com.google.protobuf</groupId>
    <artifactId>protobuf-java</artifactId>
    <version>${protobuf-java-version}</version>
</dependency>
<dependency>
    <groupId>io.grpc</groupId>
    <artifactId>grpc-netty</artifactId>
    <version>${grpc-version}</version>
</dependency>
<dependency>
    <groupId>io.grpc</groupId>
    <artifactId>grpc-protobuf</artifactId>
    <version>${grpc-version}</version>
</dependency>
<dependency>
    <groupId>io.grpc</groupId>
    <artifactId>grpc-stub</artifactId>
    <version>${grpc-version}</version>
</dependency>
<dependency>
    <groupId>io.netty</groupId>
    <artifactId>netty-tcnative-boringssl-static</artifactId>
    <version>${netty-tcnative-boringssl-static-version}</version>
</dependency>
```

For client: same libraries as server. However, for client you can choose either `grpc-netty` or `grpc-okhttp`

```xml
<!-- use either Netty or OkHttp for gRpc client -->
<dependency>
    <groupId>io.grpc</groupId>
    <artifactId>grpc-okhttp</artifactId>
    <version>${grpc-version}</version>
</dependency>
<dependency>
    <groupId>io.grpc</groupId>
    <artifactId>grpc-netty</artifactId>
    <version>${grpc-version}</version>
</dependency>
```

## Implementing APIs

Start your API by implementing interface `IApiHandler`. Then, register APIs with the `ApiRouter`.
`ApiRouter` is responsible for
- Magaging APIs (registering/unregistering)
- Authenticating API calls (via `IApiAuthenticator`)
- Routing API calls to handlers
- Logging.

Example:

```java
//create an instance of ApiRouter
ApiRouter router = new ApiRouter().init();

//setup API authenticator
//for this example, we use the "allow-all" authenticator that accepts any API calls
router.setApiAuthenticator(AllowAllApiAuthenticator.instance);

//register "echo" API
router.registerApiHandler("echo",
    (context, auth, params) -> new ApiResult(ApiResult.STATUS_OK, ApiResult.MSG_OK, params.getAllParams()));

//let's call API "echo"
ApiResult result = router.callApi(
        new ApiContext("echo"),
        new ApiAuth("app-id", "access-token"),
        new ApiParams(new HashMap<String, Object>() {
            {
                put("year", 2018);
            }
        }));
System.out.println(result);

//register a catch-all API handler that will be invoked if there is no matched api-handler to handle an API call
router.setCatchAllHandler((context, auth, params) -> 
        new ApiResult(ApiResult.STATUS_OK, "Catch-all API is called at " + new Date()));
//let's try it
ApiContext context = new ApiContext("non-exist-api");
ApiAuth auth = new ApiAuth("app-id", "access-token");
System.out.println(router.callApi(context, auth, null));
```


## Authenticating API Calls

Client side: pass an `ApiAuth` when calling API.

Server side: `ApiRouter` use `IApiAuthenticator` to validate the `ApiAuth` from client (see `ApiRouter.setApiAuthenticator(IApiAuthenticator)`).
If none is set, the default `AllowAllApiAuthenticator` will be used.
There are 2 built-in api-authenticators that can be used:
- `AllowAllApiAuthenticator` (default): this authenticator simply passes any auth check, which means allowing all API calls.
- `BasicApiAuthenticator`: this authenticator holds a map of `{app-id:access-code}` and uses it to validate the `ApiAuth` from client.


## APIs over Apache Thrift

Thrift definition file: [api_service.thrift](thrift/api_service.thrift).

See examples of [Thrift server, client and SSL support](../../../../../../../test/java/com/github/ddth/recipes/qnd/apiservice/thrift/).


## APIs over gRPC

gRPC service definition file: [api_service.proto](grpc/api_service.proto).

See examples of [gRPC server, client and SSL support](../../../../../../../test/java/com/github/ddth/recipes/qnd/apiservice/grpc/).
