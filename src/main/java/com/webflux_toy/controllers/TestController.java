package com.webflux_toy.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RestController
public class TestController {

    @PostMapping("/test")
    public Mono<ResponseEntity<Void>> test(@RequestBody String input) {
        System.out.println("controller init current Thread: " + Thread.currentThread().getName());
        Mono<String> simpleMono = Mono.just("hello flux!!");

        Mono<ResponseEntity<Void>> responseEntityMono = simpleMono.flatMap(body -> {
            System.out.println("simpleMono: " + body);
            System.out.println("mono current Thread: " + Thread.currentThread().getName());
            try {
                Thread.sleep(5000L);
                System.out.println("thread sleep was over!!");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (input.equals("noContent")) {
                return Mono.just(ResponseEntity.noContent().build());
            }
            return Mono.just(ResponseEntity.ok().build());
        });

        return responseEntityMono.subscribeOn(Schedulers.boundedElastic());
    }
}
