package com.webflux_toy.examples;

import reactor.core.publisher.Mono;

public class Example6_2 {

    public void run() {
        Mono.empty()
                .subscribe(
                        none -> System.out.println("# emitted onNext signal"),
                        error -> {},
                        () -> System.out.println("# emitted onComplete signal")
                );
    }
}
