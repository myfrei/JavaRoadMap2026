package com.javaroadmap.spring.s03.grpc;

import io.grpc.Server;
import io.grpc.inprocess.InProcessServerBuilder;
import java.io.IOException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * gRPC-сервер как бин (статья 05): стартует вместе с контекстом,
 * гасится при его закрытии (destroyMethod). In-process транспорт —
 * без сети, идеально для учёбы и тестов; в проде тот же код вешается
 * на порт через NettyServerBuilder/spring-grpc.
 */
@Configuration
public class GrpcServerConfig {

    /** Адрес in-process сервера — аналог host:port. Клиент берёт его из этого бина. */
    public record GrpcServerName(String value) {
    }

    /**
     * Имя уникально на контекст: Spring кэширует тестовые контексты,
     * и два живых сервера с одним именем не зарегистрируются.
     */
    @Bean
    public GrpcServerName grpcServerName() {
        return new GrpcServerName(InProcessServerBuilder.generateName());
    }

    @Bean(destroyMethod = "shutdownNow")
    public Server grpcServer(GreeterGrpcService greeter, GrpcServerName name) throws IOException {
        return InProcessServerBuilder.forName(name.value())
                .directExecutor()
                .addService(greeter.definition())
                .build()
                .start();
    }
}
