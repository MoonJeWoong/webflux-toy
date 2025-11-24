package com.webflux_toy.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.Duration;

@WebFluxTest(TestController.class)
class TestControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        // 응답 타임아웃을 10초로 설정 (5초 sleep + 여유 시간)
        webTestClient = webTestClient.mutate()
                .responseTimeout(Duration.ofSeconds(10))
                .build();
    }

    @Test
    void test_shouldReturnNoContentWhenRequestBodyIsNoContent() {
        webTestClient.post()
                .uri("/test")
                .contentType(MediaType.TEXT_PLAIN)
                .bodyValue("noContent")
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void test_shouldReturnOkWhenRequestBodyIsOk() {
        webTestClient.post()
                .uri("/test")
                .contentType(MediaType.TEXT_PLAIN)
                .bodyValue("ok")
                .exchange()
                .expectStatus().isOk();
    }
}