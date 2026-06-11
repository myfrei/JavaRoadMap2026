package com.javaroadmap.spring.s03.grpc;

import static org.assertj.core.api.Assertions.assertThat;

import com.javaroadmap.spring.s03.grpc.GrpcServerConfig.GrpcServerName;
import io.grpc.CallOptions;
import io.grpc.ManagedChannel;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.stub.ClientCalls;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Тест gRPC-сервиса (статья 05): сервер поднят контекстом как бин,
 * клиент подключается по in-process имени — без сети и портов.
 */
@SpringBootTest
class GreeterGrpcTest {

    @Autowired
    private GrpcServerName serverName;

    @Test
    void unaryCallReturnsGreeting() {
        ManagedChannel channel = InProcessChannelBuilder
                .forName(serverName.value())
                .directExecutor()
                .build();
        try {
            String response = ClientCalls.blockingUnaryCall(
                    channel, GreeterGrpc.GREET, CallOptions.DEFAULT, "Алиса");

            assertThat(response).isEqualTo("Привет, Алиса!");
        } finally {
            channel.shutdownNow();
        }
    }
}
