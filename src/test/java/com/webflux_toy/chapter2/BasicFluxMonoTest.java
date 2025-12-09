package com.webflux_toy.chapter2;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class BasicFluxMonoTest {
    
    @Test
    void testBasicFluxMono () {
        // Flux의 흐름은 빈 함수 혹은 데이터로부터 시작할 수 있다
        Flux.<Integer>just(1,2,3,4,5)
                .map(data -> data * 2)
                .filter(data -> data % 4 == 0)
                .subscribe(data -> System.out.println("Flux가 구독한 데이터: " + data));

        Mono.just(2)
                .map(data -> data * 2)
                .filter(data -> data % 4 == 0)
                .subscribe(data -> System.out.println("Mono가 구독한 데이터: " + data));
    }

    @Test
    void testFluxMonoBlock() {
        Mono<String> justString = Mono.just("String");
        String result = justString.block();
        System.out.println(result);
    }
}
