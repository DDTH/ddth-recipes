# ddth-recipes: API Service

_API Service recipe added since version `v0.2.0`._

API Service recipe creates a framework that help building API servers and clients over HTTP, Thrift and gRPC.

- HTTP/Thrift/gRPC are API communication protocols only. Business logic is handled by one single code repository for all communication protocols.
- Data is interchanged (request/response) in JSON format; can be gzipped to reduce space/transmit time consumption.

**Since v0.3.0, API Service requires libthrift v0.12.0+**

## Maven Dependency

### API Service over HTTP

If APIs are served over HTTP only (e.g. REST APIs)
besides libraries required by the web-framework your project is using
`ddth-commons-core` and `ddth-commons-serialization` are also needed:

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

API Service recipe provides only the skeleton to build application's APIs.
How APIs are served or called over HTTP is totally up to application.

### API Service over Apache Thrift

Thrift definition file: [api_service.thrift](thrift/api_service.thrift).

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

gRPC service definition file: [api_service.proto](grpc/api_service.proto) and Maven file to generate gRPC stub: [pom.xml](grpc/pom.xml).

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
<!-- use either Netty or OkHttp for gRPC client -->
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
- Managing APIs (registering/unregistering).
- Authenticating API calls (via `IApiAuthenticator`).
- Routing API calls to handlers.
- [API filtering](#api-filtering).

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

## API Filtering

_Filters are added since v1.1.0._

Filters are plugable components that are used to intercept API call and do some pre-processing,
intercept result and do post-processing before returning to caller.

Filters can be chained together to form a chain.

Implementing filter by extending abstract class `ApiFilter` and registering filter via `ApiRouter.setFilter()`.
There are 3 built-in filters that can be used:
- `AddPerfInfoFilter`: this filter adds api execution duration to the "debug" field of API's result.
- `AuthenticationFilter`: this filter performs authentication check before calling API.
- `LoggingFilter`: this filter performs logging before and after API call.

## Authenticating API Calls

Client side: pass an `ApiAuth` when calling API.

Server side: `ApiRouter` use `IApiAuthenticator` to validate the `ApiAuth` from client (see `ApiRouter.setApiAuthenticator(IApiAuthenticator)`).
There are 2 built-in api-authenticators that can be used:
- `AllowAllApiAuthenticator` (default): this authenticator simply passes any auth check, which means allowing all API calls.
- `BasicApiAuthenticator`: this authenticator holds a map of `{app-id:access-code}` and uses it to validate the `ApiAuth` from client.

## Examples:

- [ApiRRouter & Server examples](../../../../../../../test/java/com/github/ddth/recipes/qnd/apiservice/) examples, include SSL-enabled servers.
- [gRPC clients](../../../../../../../test/java/com/github/ddth/recipes/qnd/apiservice/grpc), include SSL-enable and async clients.
- [Thrift clients](../../../../../../../test/java/com/github/ddth/recipes/qnd/apiservice/thrift), include SSL-enable and async clients.
- [HTTP REST client](../../../../../../../test/java/com/github/ddth/recipes/qnd/apiservice/rest).
