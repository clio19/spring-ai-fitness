package com.fitness.activityservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    @LoadBalanced
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public WebClient userServiceWebClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder
                .baseUrl("http://USER-SERVICE")
                .build();
    }

//    @Bean
//    public WebClient userServiceWebClient(WebClient.Builder plainWebClientBuilder,
//                                          @Value("${user.service.base-url:http://localhost:8081}") String userServiceBaseUrl) {
//        return plainWebClientBuilder
//                .baseUrl(userServiceBaseUrl)
//                .build();
//    }
}