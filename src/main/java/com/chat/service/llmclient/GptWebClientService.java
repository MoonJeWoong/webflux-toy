package com.chat.service.llmclient;

import com.chat.exception.CustomErrorType;
import com.chat.exception.ErrorTypeException;
import com.chat.model.llmclient.LlmChatRequest;
import com.chat.model.llmclient.LlmChatResponse;
import com.chat.model.llmclient.LlmType;
import com.chat.model.llmclient.gpt.request.GptChatRequestDto;
import com.chat.model.llmclient.gpt.response.GptChatResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GptWebClientService implements LlmWebClientService {

    private final WebClient webClient;
    @Value("${llm.gpt.key}")
    private String gptApiKey;

    @Override
    public Mono<LlmChatResponse> getChatCompletion(LlmChatRequest llmChatRequest) {
        GptChatRequestDto gptChatRequestDto = new GptChatRequestDto(llmChatRequest);
        return webClient.post()
                .uri("https://api.openai.com/v1/chat/completions")
                .header("Authorization", "Bearer " + gptApiKey)
                .bodyValue(gptChatRequestDto)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (clientResponse -> {
                    return clientResponse.bodyToMono(String.class).flatMap(body -> {
                        log.error("Error Response: {}", body);
                        return Mono.error(new ErrorTypeException("API 요청 실패" + body, CustomErrorType.GPT_RESPONSE_ERROR));
                    });
                }))
                .bodyToMono(GptChatResponseDto.class)
                .map(LlmChatResponse::new);
    }

    @Override
    public LlmType getLlmType() {
        return LlmType.GPT;
    }

    @Override
    public Flux<LlmChatResponse> getChatCompletionStream(LlmChatRequest llmChatRequest) {
        GptChatRequestDto gptChatRequestDto = new GptChatRequestDto(llmChatRequest);
        gptChatRequestDto.setStream(true);
        return webClient.post()
                .uri("https://api.openai.com/v1/chat/completions")
                .header("Authorization", "Bearer " + gptApiKey)
                .bodyValue(gptChatRequestDto)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (clientResponse -> {
                    return clientResponse.bodyToMono(String.class).flatMap(body -> {
                        log.error("Error Response: {}", body);
                        return Mono.error(new ErrorTypeException("API 요청 실패" + body, CustomErrorType.GPT_RESPONSE_ERROR));
                    });
                }))
                .bodyToFlux(GptChatResponseDto.class)
                .takeWhile(gptChatResponseDto -> Optional.ofNullable(gptChatResponseDto.getSingleChoice().getFinishReason()).isEmpty())
                .map(LlmChatResponse::getLlmChatResponseFromStream);
    }
}
