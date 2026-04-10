package com.webflux_toy.chapter2;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public class OperatorFlatmapTest {

    /**
     * Mono<Mono<T>> -> Mono<T>
     * Mono<Flux<T>> -> Flux<T>
     * Fluxt<Mono<T>> -> Flux<T>
     *
     * 위와 같이 중첩된 비동기 객체를 평탄화 시켜 하나의 단일 비동기 객체로 만들어주는 것이 fltaMap의 역할이다
     */

    @Test
    void monoToFlux() {
        Mono<Integer> one = Mono.just(1);
        Flux<Integer> integerFlux = one.flatMapMany(data -> {
            return Flux.just(data, data + 1, data + 2);
        });
        integerFlux.subscribe(data -> System.out.println("data = " + data));
    }

    @Test
    void testWebClientFlatMap() {
        Flux<Mono<String>> flux1 = Flux.just(callWebClient("1단계 - 문제 이해하기", 1500),
                callWebClient("2단계 - 문제 단계별로 풀어가기", 1000),
                callWebClient("3단계 - 최종 응답", 500));

        // flatMap을 통해 Flux 내부에 중첩된 내부 Mono를 평탄화할 수 있다.
        Flux<String> flux1_1 = Flux.just(callWebClient("1단계 - 문제 이해하기", 1500),
                callWebClient("2단계 - 문제 단계별로 풀어가기", 1000),
                callWebClient("3단계 - 최종 응답", 500))
                .flatMap(monoData -> monoData);

        /**
         * flatMap은 입력 순서에 따른 방출 순서를 보장하지 않는다.
         * 그래서 아래 출력 결과는 delay가 짧아서 먼저 완료된 데이터들이 먼저 방출된다.
         */
        flux1_1.subscribe(stringData -> System.out.println("flatMapped stringData = " + stringData));

        Flux<String> sequentialFlux = Flux.just(callWebClient("1단계 - 문제 이해하기", 1500),
                        callWebClient("2단계 - 문제 단계별로 풀어가기", 1000),
                        callWebClient("3단계 - 최종 응답", 500))
                .flatMapSequential(monoData -> monoData);

        sequentialFlux.subscribe(stringData -> System.out.println("sequentialFlux stringData = " + stringData));

        Flux<String> merge = Flux.merge(callWebClient("1단계 - 문제 이해하기", 1500),
                callWebClient("2단계 - 문제 단계별로 풀어가기", 1000),
                callWebClient("3단계 - 최종 응답", 500));
//                .map(monoData -> monoData); merge 후 추가로 map을 통해 데이터를 가공하면 flatMap과 동일한 동작을 수행하게 된다.

        merge.subscribe(stringData -> System.out.println("merge stringData = " + stringData));

        Flux<String> mergeSequentialFlux = Flux.mergeSequential(callWebClient("1단계 - 문제 이해하기", 1500),
                callWebClient("2단계 - 문제 단계별로 풀어가기", 1000),
                callWebClient("3단계 - 최종 응답", 500));
        try {
            Thread.sleep(5000);
        } catch (Exception e) {
        }
    }

    @Test
    void testWebClientConcat() {
        // concat은 위 테스트 메서드의 merge와 다르게 concat 내부의 요소들을 병렬적으로 동시에 실행하는게 아니라 하나씩 순차적으로 실행한다
        // mergeSequential이나 flatMapSequential은 일단 한번에 병렬적으로 다 실행한다음 방출을 순서대로 맞춰서 해주는 것이라면 이건 하나씩 순차적으로 실행하고 완료한다는데 차이가 있다.
        Flux<String> concat = Flux.concat(callWebClient("1단계 - 문제 이해하기", 1500),
                callWebClient("2단계 - 문제 단계별로 풀어가기", 1000),
                callWebClient("3단계 - 최종 응답", 500));

        concat.subscribe(stringData -> System.out.println("merge stringData = " + stringData));

        Flux<String> mergeSequentialFlux = Flux.mergeSequential(callWebClient("1단계 - 문제 이해하기", 1500),
                callWebClient("2단계 - 문제 단계별로 풀어가기", 1000),
                callWebClient("3단계 - 최종 응답", 500));
        try {
            Thread.sleep(5000);
        } catch (Exception e) {
        }
    }

    @Test
    void testFluxCreateWebClientFlatMap() {
        Flux<Mono<String>> flux2 = Flux.<Mono<String>>create(sink -> {
            sink.next(callWebClient("1단계 - 문제 이해하기", 1500));
            sink.next(callWebClient("2단계 - 문제 단계별로 풀어가기", 1000));
            sink.next(callWebClient("3단계 - 최종 응답", 500));
            sink.complete();
        });

        Flux<String> flux2_1 = Flux.<Mono<String>>create(sink -> {
            sink.next(callWebClient("1단계 - 문제 이해하기", 1500));
            sink.next(callWebClient("2단계 - 문제 단계별로 풀어가기", 1000));
            sink.next(callWebClient("3단계 - 최종 응답", 500));
            sink.complete();
        }).flatMap(monoData -> monoData);

        /**
         * flatMap은 입력 순서에 따른 방출 순서를 보장하지 않는다.
         * 그래서 아래 출력 결과는 delay가 짧아서 먼저 완료된 데이터들이 먼저 방출된다.
         */
        flux2_1.subscribe(stringData -> System.out.println("flatMapped stringData = " + stringData));

        try {
            Thread.sleep(5000);
        } catch (Exception e) {
        }
    }

    /**
     * concat, concatMap 이런건 특수한 상황이 아니라면 사용하지 않는다고 생각하자
     *
     * Flux<Mono<T>>
     * Mono<Mono<T>> --> 이런 구조 안의 Mono는 flatMap, merge 메서드로 벗겨낼 수 있다.
     *               --> 단 내부 요소 값들의 방출 순서를 보장하지 않으니 순서 보장이 필요하다면 sequential을 사용하자
     * Mono<Flux<T>> --> flatMapMany 를 사용하는데 이건 Flux<T>의 순서가 보장된다
     * Flux<Flux<T> --(collectList)--> Flux<Mono<List<T>> --(flatMap)--> Flux<List<T>
     */

    public Mono<String> callWebClient(String request, long delay) {
        return Mono.defer(() -> {
            try {
                Thread.sleep(delay);
                return Mono.just(request + " from WebClient, delay = " + delay + "ms");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }
}
