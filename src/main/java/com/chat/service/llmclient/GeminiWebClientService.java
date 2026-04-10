package com.chat.service.llmclient;

import com.chat.exception.CommonError;
import com.chat.exception.CustomErrorType;
import com.chat.exception.ErrorTypeException;
import com.chat.model.llmclient.LlmChatRequest;
import com.chat.model.llmclient.LlmChatResponse;
import com.chat.model.llmclient.LlmType;
import com.chat.model.llmclient.gemini.request.GeminiChatRequest;
import com.chat.model.llmclient.gemini.response.GeminiChatResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeminiWebClientService implements LlmWebClientService {

    private final WebClient webClient;

    @Value("${llm.gemini.key}")
    private String geminiApiKey;

    @Override
    public Mono<LlmChatResponse> getChatCompletion(LlmChatRequest llmChatRequest) {
        GeminiChatRequest geminiChatRequest = new GeminiChatRequest(llmChatRequest);
        return webClient.post()
                .uri("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + geminiApiKey)
                .bodyValue(geminiChatRequest)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                    return clientResponse.bodyToMono(String.class)
                            .flatMap(body -> {
                                log.error("API 요청 실패: {}", body);
                                return Mono.error(new ErrorTypeException("API 요청 실패" + body, CustomErrorType.GEMINI_RESPONSE_ERROR));
                            });
                })
                .bodyToMono(GeminiChatResponse.class)
                .map(LlmChatResponse::new);
    }

    @Override
    public LlmType getLlmType() {
        return LlmType.GEMINI;
    }

    @Override
    public Flux<LlmChatResponse> getChatCompletionStream(LlmChatRequest llmChatRequest) {
        GeminiChatRequest geminiChatRequest = new GeminiChatRequest(llmChatRequest);
        AtomicInteger counter = new AtomicInteger(0);
        return webClient.post()
                .uri("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:streamGenerateContent?key=" + geminiApiKey)
                .bodyValue(geminiChatRequest)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                    return clientResponse.bodyToMono(String.class)
                            .flatMap(body -> {
                                log.error("API 요청 실패: {}", body);
                                return Mono.error(new ErrorTypeException("API 요청 실패" + body, CustomErrorType.GEMINI_RESPONSE_ERROR));
                            });
                })
                .bodyToFlux(GeminiChatResponse.class)
                .map(LlmChatResponse::new);
//                // 최종 응답 스트림을 반환하는 중 5개 응답마다 에러가 발생하지만, 전체 스트림 전송은 멈추고 싶지 않은 상황
//                .map(geminiChatResponse -> {
//                    try {
//                        if (counter.incrementAndGet() % 5 == 0) {
//                            throw new ErrorTypeException("테스르를 위한 에러", CustomErrorType.GEMINI_RESPONSE_ERROR);
//                        }
//                        return new LlmChatResponse(geminiChatResponse);
//                    } catch (Exception e) {
//                        return new LlmChatResponse(new CommonError(CustomErrorType.GEMINI_RESPONSE_ERROR.getCode(), "테스트를 위한 에러"));
//                    }
//                });
    }
}
