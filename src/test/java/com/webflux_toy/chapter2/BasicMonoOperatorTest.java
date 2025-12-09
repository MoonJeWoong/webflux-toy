package com.webflux_toy.chapter2;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public class BasicMonoOperatorTest {

    // just, empty
    @Test
    void startMonoFromData() {
        Mono.just(1).subscribe(data -> System.out.println("data = " + data));

        // ex) 사소한 에러가 발생했을 때 로그를 남기고 empty인 Mono를 전파하는 방식으로 사용
        Mono.empty().subscribe(data -> System.out.println("empty data = " + data));
    }

    // fromCallable, defer

    /**
     * fromCallable -> 동기적인 객체를 반환할 때 사용
     * defer -> Mono를 반환하고 싶을 때 사용
     */
    @Test
    void startMonoFromFunction() {
        /**
         * restTemplate, JPA 등과 같이 블로킹이 발생하는 라이브러리를 사용할 때 Mono로 스레드를 분리해서 처리가 가능
         * 동기적으로 처리하던 로직을 임시 마이그레이션해서 Mono로 반환하는 것이 가능해짐
         */
        Mono<String> monoFrolCallable = Mono.fromCallable(() -> {
            return callRestTemplate("안녕!");
        }).subscribeOn(Schedulers.boundedElastic());

        /**
         * Mono 객체를 Mono객체로 반환한다
         * 아래 just의 경우와 다르게 defer 부분을 메인 스레드가 지나갈때는 Mono.just가 실행되는 것이 아니라
         * subscribe를 통해 구독하는 시점 이후에서 just 메서드가 실행된다
         */
        Mono<String> monoFromDefer = Mono.defer(() -> {
            return Mono.just("안녕!");
        });
        monoFromDefer.subscribe();

        // Mono.just 메서드가 곧바로 실행된다
        Mono<String> monoFromJust = callWebClient("안녕!");
    }

    @Test
    void testDeferNecessity() {
        // a, b, c를 만드는 로직도 같이 단일 Mono 안에서 관리하며 반환도 Mono로 하고 싶을때
        Mono<String> stringMono = Mono.defer(() -> {
            String a = "안녕";
            String b = "하세"; // blocking 이라 가정하면 아래에서 별도 스케쥴러에 실행을 위임시킬 수 있음
            String c = "요";
            return callWebClient(a + b + c);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    private Mono<String> callWebClient(String request) {
        return Mono.just(request + "callWebClient");
    }

    private String callRestTemplate(String request) {
        return request + " from RestTemplate";
    }

    /**
     * Mono에서 데이터 방출 개수가 갑자기 많아져서 Flux로 바꾸고 싶을 때 flatMapMany를 사용한다
     */
    @Test
    void monoToFlux() {
        Mono<Integer> one = Mono.just(1);
        Flux<Integer> integerFlux = one.flatMapMany(data -> {
            return Flux.just(data, data + 1, data + 2);
        });
        integerFlux.subscribe(data -> System.out.println("data = " + data));
    }
}
