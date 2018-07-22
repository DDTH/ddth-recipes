package com.github.ddth.recipes.apiservice.grpc.def;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.13.1)",
    comments = "Source: api_service.proto")
public final class PApiServiceGrpc {

  private PApiServiceGrpc() {}

  public static final String SERVICE_NAME = "PApiService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.google.protobuf.Empty,
      com.google.protobuf.Empty> getPingMethod;

  public static io.grpc.MethodDescriptor<com.google.protobuf.Empty,
      com.google.protobuf.Empty> getPingMethod() {
    io.grpc.MethodDescriptor<com.google.protobuf.Empty, com.google.protobuf.Empty> getPingMethod;
    if ((getPingMethod = PApiServiceGrpc.getPingMethod) == null) {
      synchronized (PApiServiceGrpc.class) {
        if ((getPingMethod = PApiServiceGrpc.getPingMethod) == null) {
          PApiServiceGrpc.getPingMethod = getPingMethod = 
              io.grpc.MethodDescriptor.<com.google.protobuf.Empty, com.google.protobuf.Empty>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "PApiService", "ping"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.protobuf.Empty.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.protobuf.Empty.getDefaultInstance()))
                  .setSchemaDescriptor(new PApiServiceMethodDescriptorSupplier("ping"))
                  .build();
          }
        }
     }
     return getPingMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.github.ddth.recipes.apiservice.grpc.def.PApiServiceProto.PApiAuth,
      com.github.ddth.recipes.apiservice.grpc.def.PApiServiceProto.PApiResult> getCheckMethod;

  public static io.grpc.MethodDescriptor<com.github.ddth.recipes.apiservice.grpc.def.PApiServiceProto.PApiAuth,
      com.github.ddth.recipes.apiservice.grpc.def.PApiServiceProto.PApiResult> getCheckMethod() {
    io.grpc.MethodDescriptor<com.github.ddth.recipes.apiservice.grpc.def.PApiServiceProto.PApiAuth, com.github.ddth.recipes.apiservice.grpc.def.PApiServiceProto.PApiResult> getCheckMethod;
    if ((getCheckMethod = PApiServiceGrpc.getCheckMethod) == null) {
      synchronized (PApiServiceGrpc.class) {
        if ((getCheckMethod = PApiServiceGrpc.getCheckMethod) == null) {
          PApiServiceGrpc.getCheckMethod = getCheckMethod = 
              io.grpc.MethodDescriptor.<com.github.ddth.recipes.apiservice.grpc.def.PApiServiceProto.PApiAuth, com.github.ddth.recipes.apiservice.grpc.def.PApiServiceProto.PApiResult>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "PApiService", "check"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.github.ddth.recipes.apiservice.grpc.def.PApiServiceProto.PApiAuth.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.github.ddth.recipes.apiservice.grpc.def.PApiServiceProto.PApiResult.getDefaultInstance()))
                  .setSchemaDescriptor(new PApiServiceMethodDescriptorSupplier("check"))
                  .build();
          }
        }
     }
     return getCheckMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.github.ddth.recipes.apiservice.grpc.def.PApiServiceProto.PApiContext,
      com.github.ddth.recipes.apiservice.grpc.def.PApiServiceProto.PApiResult> getCallMethod;

  public static io.grpc.MethodDescriptor<com.github.ddth.recipes.apiservice.grpc.def.PApiServiceProto.PApiContext,
      com.github.ddth.recipes.apiservice.grpc.def.PApiServiceProto.PApiResult> getCallMethod() {
    io.grpc.MethodDescriptor<com.github.ddth.recipes.apiservice.grpc.def.PApiServiceProto.PApiContext, com.github.ddth.recipes.apiservice.grpc.def.PApiServiceProto.PApiResult> getCallMethod;
    if ((getCallMethod = PApiServiceGrpc.getCallMethod) == null) {
      synchronized (PApiServiceGrpc.class) {
        if ((getCallMethod = PApiServiceGrpc.getCallMethod) == null) {
          PApiServiceGrpc.getCallMethod = getCallMethod = 
              io.grpc.MethodDescriptor.<com.github.ddth.recipes.apiservice.grpc.def.PApiServiceProto.PApiContext, com.github.ddth.recipes.apiservice.grpc.def.PApiServiceProto.PApiResult>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "PApiService", "call"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.github.ddth.recipes.apiservice.grpc.def.PApiServiceProto.PApiContext.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.github.ddth.recipes.apiservice.grpc.def.PApiServiceProto.PApiResult.getDefaultInstance()))
                  .setSchemaDescriptor(new PApiServiceMethodDescriptorSupplier("call"))
                  .build();
          }
        }
     }
     return getCallMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static PApiServiceStub newStub(io.grpc.Channel channel) {
    return new PApiServiceStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static PApiServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new PApiServiceBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static PApiServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new PApiServiceFutureStub(channel);
  }

  /**
   */
  public static abstract class PApiServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void ping(com.google.protobuf.Empty request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      asyncUnimplementedUnaryCall(getPingMethod(), responseObserver);
    }

    /**
     */
    public void check(com.github.ddth.recipes.apiservice.grpc.def.PApiServiceProto.PApiAuth request,
        io.grpc.stub.StreamObserver<com.github.ddth.recipes.apiservice.grpc.def.PApiServiceProto.PApiResult> responseObserver) {
      asyncUnimplementedUnaryCall(getCheckMethod(), responseObserver);
    }

    /**
     */
    public void call(com.github.ddth.recipes.apiservice.grpc.def.PApiServiceProto.PApiContext request,
        io.grpc.stub.StreamObserver<com.github.ddth.recipes.apiservice.grpc.def.PApiServiceProto.PApiResult> responseObserver) {
      asyncUnimplementedUnaryCall(getCallMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getPingMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.google.protobuf.Empty,
                com.google.protobuf.Empty>(
                  this, METHODID_PING)))
          .addMethod(
            getCheckMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.github.ddth.recipes.apiservice.grpc.def.PApiServiceProto.PApiAuth,
                com.github.ddth.recipes.apiservice.grpc.def.PApiServiceProto.PApiResult>(
                  this, METHODID_CHECK)))
          .addMethod(
            getCallMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.github.ddth.recipes.apiservice.grpc.def.PApiServiceProto.PApiContext,
                com.github.ddth.recipes.apiservice.grpc.def.PApiServiceProto.PApiResult>(
                  this, METHODID_CALL)))
          .build();
    }
  }

  /**
   */
  public static final class PApiServiceStub extends io.grpc.stub.AbstractStub<PApiServiceStub> {
    private PApiServiceStub(io.grpc.Channel channel) {
      super(channel);
    }

    private PApiServiceStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected PApiServiceStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new PApiServiceStub(channel, callOptions);
    }

    /**
     */
    public void ping(com.google.protobuf.Empty request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getPingMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void check(com.github.ddth.recipes.apiservice.grpc.def.PApiServiceProto.PApiAuth request,
        io.grpc.stub.StreamObserver<com.github.ddth.recipes.apiservice.grpc.def.PApiServiceProto.PApiResult> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getCheckMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void call(com.github.ddth.recipes.apiservice.grpc.def.PApiServiceProto.PApiContext request,
        io.grpc.stub.StreamObserver<com.github.ddth.recipes.apiservice.grpc.def.PApiServiceProto.PApiResult> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getCallMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class PApiServiceBlockingStub extends io.grpc.stub.AbstractStub<PApiServiceBlockingStub> {
    private PApiServiceBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private PApiServiceBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected PApiServiceBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new PApiServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.google.protobuf.Empty ping(com.google.protobuf.Empty request) {
      return blockingUnaryCall(
          getChannel(), getPingMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.github.ddth.recipes.apiservice.grpc.def.PApiServiceProto.PApiResult check(com.github.ddth.recipes.apiservice.grpc.def.PApiServiceProto.PApiAuth request) {
      return blockingUnaryCall(
          getChannel(), getCheckMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.github.ddth.recipes.apiservice.grpc.def.PApiServiceProto.PApiResult call(com.github.ddth.recipes.apiservice.grpc.def.PApiServiceProto.PApiContext request) {
      return blockingUnaryCall(
          getChannel(), getCallMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class PApiServiceFutureStub extends io.grpc.stub.AbstractStub<PApiServiceFutureStub> {
    private PApiServiceFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private PApiServiceFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected PApiServiceFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new PApiServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.protobuf.Empty> ping(
        com.google.protobuf.Empty request) {
      return futureUnaryCall(
          getChannel().newCall(getPingMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.github.ddth.recipes.apiservice.grpc.def.PApiServiceProto.PApiResult> check(
        com.github.ddth.recipes.apiservice.grpc.def.PApiServiceProto.PApiAuth request) {
      return futureUnaryCall(
          getChannel().newCall(getCheckMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.github.ddth.recipes.apiservice.grpc.def.PApiServiceProto.PApiResult> call(
        com.github.ddth.recipes.apiservice.grpc.def.PApiServiceProto.PApiContext request) {
      return futureUnaryCall(
          getChannel().newCall(getCallMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_PING = 0;
  private static final int METHODID_CHECK = 1;
  private static final int METHODID_CALL = 2;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final PApiServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(PApiServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_PING:
          serviceImpl.ping((com.google.protobuf.Empty) request,
              (io.grpc.stub.StreamObserver<com.google.protobuf.Empty>) responseObserver);
          break;
        case METHODID_CHECK:
          serviceImpl.check((com.github.ddth.recipes.apiservice.grpc.def.PApiServiceProto.PApiAuth) request,
              (io.grpc.stub.StreamObserver<com.github.ddth.recipes.apiservice.grpc.def.PApiServiceProto.PApiResult>) responseObserver);
          break;
        case METHODID_CALL:
          serviceImpl.call((com.github.ddth.recipes.apiservice.grpc.def.PApiServiceProto.PApiContext) request,
              (io.grpc.stub.StreamObserver<com.github.ddth.recipes.apiservice.grpc.def.PApiServiceProto.PApiResult>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class PApiServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    PApiServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.github.ddth.recipes.apiservice.grpc.def.PApiServiceProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("PApiService");
    }
  }

  private static final class PApiServiceFileDescriptorSupplier
      extends PApiServiceBaseDescriptorSupplier {
    PApiServiceFileDescriptorSupplier() {}
  }

  private static final class PApiServiceMethodDescriptorSupplier
      extends PApiServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    PApiServiceMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (PApiServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new PApiServiceFileDescriptorSupplier())
              .addMethod(getPingMethod())
              .addMethod(getCheckMethod())
              .addMethod(getCallMethod())
              .build();
        }
      }
    }
    return result;
  }
}
