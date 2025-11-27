//package com.example.chat.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//@Configuration
//public class VirtualThreadsConfig {
//    @Bean(name="pingExecutor", destroyMethod = "shutdown")
//    public ExecutorService pingExecutor() {
//        return Executors.newVirtualThreadPerTaskExecutor();
//    }
//}
