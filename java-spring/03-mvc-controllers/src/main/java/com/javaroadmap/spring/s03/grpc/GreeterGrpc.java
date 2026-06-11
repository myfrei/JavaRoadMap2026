package com.javaroadmap.spring.s03.grpc;

import io.grpc.MethodDescriptor;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

/**
 * Контракт gRPC-сервиса, описанный руками (статья 05).
 *
 * <p>В реальных проектах этот класс генерирует protoc из .proto-файла —
 * здесь он написан вручную, чтобы было видно устройство: полное имя метода
 * «Сервис/метод» + маршаллеры запроса и ответа. Логика вызова и стабов
 * от этого не меняется.
 */
public final class GreeterGrpc {

    public static final String SERVICE_NAME = "javaroadmap.Greeter";

    /** Вместо protobuf — простейший маршаллер: строка ↔ UTF-8 байты. */
    private static final MethodDescriptor.Marshaller<String> STRING_MARSHALLER =
            new MethodDescriptor.Marshaller<>() {
                @Override
                public InputStream stream(String value) {
                    return new ByteArrayInputStream(value.getBytes(StandardCharsets.UTF_8));
                }

                @Override
                public String parse(InputStream stream) {
                    try {
                        return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                }
            };

    /** Унарный метод: один запрос — один ответ. */
    public static final MethodDescriptor<String, String> GREET =
            MethodDescriptor.<String, String>newBuilder()
                    .setType(MethodDescriptor.MethodType.UNARY)
                    .setFullMethodName(MethodDescriptor.generateFullMethodName(SERVICE_NAME, "greet"))
                    .setRequestMarshaller(STRING_MARSHALLER)
                    .setResponseMarshaller(STRING_MARSHALLER)
                    .build();

    private GreeterGrpc() {
    }
}
