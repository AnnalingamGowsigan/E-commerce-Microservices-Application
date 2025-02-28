package com.springboot.orderservice.controller;


import com.springboot.orderservice.dto.OrderRequest;
import com.springboot.orderservice.service.OrderService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RequestMapping("/api/order")
@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/hello")
    public String hello() {
        return "Hello from order service";
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @CircuitBreaker(name="inventory", fallbackMethod= "orderServiceFallback")
    public String placeOrder(@RequestBody OrderRequest orderRequest) throws IllegalAccessException {
        return orderService.placeOrder(orderRequest);
    }
//    @TimeLimiter(name="inventory") //@TimeLimiter return type. CompletionStage expected.
//    @Retry(name="inventory")
//    public CompletableFuture<String> placeOrder(@RequestBody OrderRequest orderRequest) throws IllegalAccessException {
//        return CompletableFuture.supplyAsync(()-> {
//            try {
//                return orderService.placeOrder(orderRequest);
//            } catch (IllegalAccessException e) {
//                throw new RuntimeException(e);
//            }
//        });
//    }

    //implementation of fallback method
    public CompletableFuture<String> orderServiceFallback(OrderRequest orderRequest, RuntimeException e) {
        return CompletableFuture.supplyAsync(()->"Order service is down. Please try again later");
    }
}
