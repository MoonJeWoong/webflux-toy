package com.webflux_toy.controllers;

import com.webflux_toy.dto.FileUploadRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
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

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<String>> uploadProduct(
            @RequestPart("body") FileUploadRequest request,
            @RequestPart("files") Flux<FilePart> uploadedFiles
    ) {
        log.info("Received body: {}", request);

        return uploadedFiles.flatMap(filePart -> {
            String filename = filePart.filename();
            log.info("Processing file: {}", filename);

            if (request.getFileName().equals(filePart.filename())) {
                log.info(String.format("File name is Equal!! : %s",  filePart.filename()));
            }

            // 파일 저장 경로 설정
            Path uploadPath = Paths.get("C:\\Exception\\storage\\test_extract", filename);

            // 파일 저장 (리액티브 방식)
            return filePart.transferTo(uploadPath)
                    .thenReturn(filename);
        })
        .collectList()  // 모든 파일 처리 완료 후 리스트로 수집
        .map(processedFiles -> {
            log.info("Successfully processed {} files", processedFiles.size());
            return ResponseEntity.ok("파일 업로드 및 처리가 완료되었습니다. 처리된 파일 수: " + processedFiles.size());
        })
        .onErrorResume(e -> {
            log.error("File upload failed", e);
            return Mono.just(ResponseEntity.status(500)
                    .body("파일 업로드 실패: " + e.getMessage()));
        });
    }

    /**
     * SSE(Server-Sent Events) 예제 메서드
     * 1초 간격으로 5개의 이벤트를 클라이언트에게 스트리밍합니다.
     */
    @GetMapping(value = "/sse/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamEvents() {
        System.out.println("SSE 요청 수신 - Thread: " + Thread.currentThread().getName());

        return Flux.interval(Duration.ofSeconds(1))  // 1초 간격으로 0, 1, 2, ... 방출
                .take(5)  // 5개까지만 방출
                .map(sequence -> {
                    String timestamp = LocalDateTime.now()
                            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    System.out.println("이벤트 방출 #" + sequence + " - Thread: " + Thread.currentThread().getName());
                    return String.format("이벤트 #%d - 시간: %s", sequence, timestamp);
                })
                .doOnSubscribe(subscription -> System.out.println("클라이언트 구독 시작"))
                .doOnComplete(() -> System.out.println("스트림 완료"))
                .doOnCancel(() -> System.out.println("클라이언트 연결 종료"));
    }
}
