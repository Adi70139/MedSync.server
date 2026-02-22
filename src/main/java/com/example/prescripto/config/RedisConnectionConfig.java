//package com.example.prescripto.config;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.connection.RedisConnectionFactory;
//import org.springframework.data.redis.connection.RedisPassword;
//import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
//import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
//import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
//
//import java.net.URI;
//import java.time.Duration;
//
//@Configuration
//public class RedisConnectionConfig {
//
//    @Value("${spring.redis.url}")
//    private String redisUrl;
//
//    @Bean
//    public RedisConnectionFactory redisConnectionFactory() {
//        try {
//            URI uri = URI.create(redisUrl);
//
//            RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
//            config.setHostName(uri.getHost());
//            config.setPort(uri.getPort());
//            config.setPassword(RedisPassword.of(uri.getUserInfo().split(":", 2)[1]));
//
//            // Enable SSL here (in client configuration)
//            LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
//                    .build();
//
//            return new LettuceConnectionFactory(config, clientConfig);
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to parse Redis URL: " + redisUrl, e);
//        }
//    }
//}
