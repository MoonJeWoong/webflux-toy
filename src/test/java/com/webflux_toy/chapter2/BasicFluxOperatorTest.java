package com.webflux_toy.chapter2;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class BasicFluxOperatorTest {

    /**
     * Flux 흐름을 시작하는 방법
     * 데이터 : just, empty, from~ 시리즈
     * 함수 : defer, create
     */
    @Test
    void testFluxFromData() {
        Flux.just(1,2,3,4)
                .subscribe(data -> System.out.println("data = " + data));

        List<Integer> basicList = List.of(1, 2, 3, 4);
        Flux.fromIterable(basicList)
                .subscribe(data -> System.out.println("data fromIterable= " + data));
    }

    /**
     * Flux defer -> 내부에서 flux 객체를 반환
     * Flux create -> 내부에서 동기적인 객체를 반환
     */
    @Test
    void testFluxFromFunction() {
        Flux.defer(() -> {
            return Flux.just(1,2,3,4);
        }).subscribe(data -> System.out.println("data from defer = " + data));

        Flux.create(fluxSink -> { // create 사용 시에는 인자가 FluxSink 객체임을 유의
            fluxSink.next(1);
            fluxSink.next(2);
            fluxSink.next(3);
            fluxSink.next(4);
            fluxSink.complete(); // sink로 내보낼 데이터를 다 내보낸 다음 마지막에는 complete 메서드를 호출해줘야 구독자 입장에서 완료 여부를 알 수 있음
        }).subscribe(data -> System.out.println("data from create = " + data));
    }

    @Test
    void testSinkNecessity() {
        Flux.<Integer>create(sink -> {
            sinkRecursive(sink);
        }).contextWrite(Context.of("counter", new AtomicInteger(0)))
                .subscribe(data -> System.out.println("data = " + data));
    }

    public void sinkRecursive(FluxSink<Integer> sink) {
        AtomicInteger counter = sink.contextView().get("counter");
        if (counter.incrementAndGet() < 10) {
            sink.next(counter.get());
            sinkRecursive(sink);
        }
        sink.complete();
    }


    @Test
    void testFluxCollectList() {
        Mono<List<Integer>> listMono = Flux.<Integer>just(1, 2, 3, 4, 5)
                .map(data -> data * 2)
                .filter(data -> data % 4 == 0)
                .collectList();

        listMono.subscribe(data -> System.out.println("collect List가 반환한 data = " + data));
    }

    /**
     * Mono -> Flux 변환 flatMapMany
     * Flux -> Mono 변환 collectList
     */
}
