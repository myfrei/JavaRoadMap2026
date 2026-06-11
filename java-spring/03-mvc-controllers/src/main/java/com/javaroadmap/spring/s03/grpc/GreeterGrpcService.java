package com.javaroadmap.spring.s03.grpc;

import io.grpc.ServerServiceDefinition;
import io.grpc.stub.ServerCalls;
import org.springframework.stereotype.Component;

/** Серверная реализация метода greet — аналог метода контроллера в REST. */
@Component
public class GreeterGrpcService {

    public ServerServiceDefinition definition() {
        return ServerServiceDefinition.builder(GreeterGrpc.SERVICE_NAME)
                .addMethod(GreeterGrpc.GREET, ServerCalls.asyncUnaryCall((request, responseObserver) -> {
                    responseObserver.onNext("Привет, %s!".formatted(request));
                    responseObserver.onCompleted();
                }))
                .build();
    }
}
