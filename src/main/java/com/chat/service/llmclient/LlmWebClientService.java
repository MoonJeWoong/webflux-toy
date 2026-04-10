package com.chat.service.llmclient;

import com.chat.exception.CommonError;
import com.chat.exception.CustomErrorType;
import com.chat.exception.ErrorTypeException;
import com.chat.model.llmclient.LlmChatRequest;
import com.chat.model.llmclient.LlmChatResponse;
import com.chat.model.llmclient.LlmType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface LlmWebClientService {
    Mono<LlmChatResponse> getChatCompletion(LlmChatRequest llmChatRequest);

    default Mono<LlmChatResponse> getChatCompletionWithCatchException(LlmChatRequest llmChatRequest) {
        return getChatCompletion(llmChatRequest)
                .onErrorResume(exception -> {
                    if (exception instanceof ErrorTypeException errorTypeException) {
                        CommonError commonError = new CommonError(errorTypeException.getErrorType().getCode(), errorTypeException.getMessage());
                        return Mono.just(new LlmChatResponse(commonError, exception));
                    } else {
                        CommonError commonError = new CommonError(500, exception.getMessage());
                        return Mono.just(new LlmChatResponse(commonError, exception));
                    }
                });
    }

    // gptWebClientService, geminiWebClientService
    LlmType getLlmType();

    Flux<LlmChatResponse> getChatCompletionStream(LlmChatRequest llmChatRequest);
}
