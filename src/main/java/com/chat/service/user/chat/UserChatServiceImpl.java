package com.chat.service.user.chat;

import com.chat.model.llmclient.LlmChatRequest;
import com.chat.model.llmclient.LlmChatResponse;
import com.chat.model.llmclient.LlmType;
import com.chat.model.user.chat.UserChatRequestDto;
import com.chat.model.user.chat.UserChatResponse;
import com.chat.service.llmclient.LlmWebClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserChatServiceImpl implements UserChatService {

    private final Map<LlmType, LlmWebClientService> llmWebClientServiceMap;

    @Override
    public Mono<UserChatResponse> oneShotChat(UserChatRequestDto userChatRequestDto) {
        LlmChatRequest chatRequest = new LlmChatRequest(userChatRequestDto, "요청에 적절히 응답해주세요.");
        Mono<LlmChatResponse> chatCompletionMono = llmWebClientServiceMap.get(userChatRequestDto.getLlmModel().getType())
                .getChatCompletion(chatRequest);

        return chatCompletionMono.map(UserChatResponse::new);
    }

    @Override
    public Flux<UserChatResponse> oneShotChatStream(UserChatRequestDto userChatRequestDto) {
        LlmChatRequest chatRequest = new LlmChatRequest(userChatRequestDto, "요청에 적절히 응답해주세요.");
        Flux<LlmChatResponse> chatCompletionFlux = llmWebClientServiceMap.get(userChatRequestDto.getLlmModel().getType())
                .getChatCompletionStream(chatRequest);

        return chatCompletionFlux.map(UserChatResponse::new);
    }
}
