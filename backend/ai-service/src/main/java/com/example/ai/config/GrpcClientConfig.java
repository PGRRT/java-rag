package com.example.ai.config;

import com.example.common.grpc.ChatServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcClientConfig {

    @Bean
    public ChatServiceGrpc.ChatServiceBlockingStub chatServiceStub(
            @Value("${spring.grpc.client.chat-service.address}") String address) {

        ManagedChannel channel = ManagedChannelBuilder.forTarget(address)
                .usePlaintext()
                .build();

        return ChatServiceGrpc.newBlockingStub(channel);
    }
}
