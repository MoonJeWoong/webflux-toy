package com.webflux_toy.examples;

import reactor.core.publisher.Mono;

public class Example6_1 {

//    public static void main(String[] args) {
//        Mono.just("Hello")
//                .subscribe(System.out::println);
//    }

    public void hello() {
        Mono.just("Hello")
                .subscribe(System.out::println);
    }
}
