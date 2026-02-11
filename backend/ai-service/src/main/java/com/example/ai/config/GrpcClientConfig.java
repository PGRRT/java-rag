package com.example.ai.config;

import com.example.common.grpc.ChatServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.GrpcChannelFactory;


@Slf4j
@Configuration
public class GrpcClientConfig {

    @Bean
    public ChatServiceGrpc.ChatServiceBlockingStub chatServiceStub() {
        String address = "chat-service:9090";

        log.info("Creating gRPC channel to: {}", address);

        ManagedChannel channel = ManagedChannelBuilder
                .forTarget(address)
                .usePlaintext()
                .build();

        return ChatServiceGrpc.newBlockingStub(channel);
    }

//    @Bean
//    public ChatServiceGrpc.ChatServiceBlockingStub chatServiceStub(GrpcChannelFactory channelFactory) {
//        return ChatServiceGrpc.newBlockingStub(channelFactory.createChannel("chat-service"));
//    }

//    @Bean
//    public ChatServiceGrpc.ChatServiceBlockingStub chatServiceStub(
//            @Value("${spring.grpc.client.chat-service.address}") String address) {
//
//        ManagedChannel channel = ManagedChannelBuilder.forTarget(address)
//                .usePlaintext()
//                .build();
//
//        return ChatServiceGrpc.newBlockingStub(channel);
//    }
}
