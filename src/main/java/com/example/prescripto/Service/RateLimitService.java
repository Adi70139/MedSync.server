//package com.example.prescripto.Service;
//
//import io.github.bucket4j.Bandwidth;
//import io.github.bucket4j.Refill;
//import org.springframework.data.redis.core.convert.Bucket;
//import org.springframework.stereotype.Service;
//
//import java.time.Duration;
//import java.util.concurrent.ConcurrentHashMap;
//
//@Service
//public class RateLimitService {
//
//    private final ConcurrentHashMap<String, Bucket> cache=new ConcurrentHashMap<>();
//
//    private Bucket createNewBucket(){
//        Bandwidth limit= Bandwidth.classic(10, Refill.greedy(10, Duration.ofMinutes(1)));
//
//        return  Bucket4j.builder()
//                .addLimit(limit)
//                .build();
//    }
//}
