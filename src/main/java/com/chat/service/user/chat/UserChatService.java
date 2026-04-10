package com.chat.service.user.chat;

import com.chat.model.user.chat.UserChatRequestDto;
import com.chat.model.user.chat.UserChatResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserChatService {
    Mono<UserChatResponse> oneShotChat(UserChatRequestDto userChatRequestDto);

    Flux<UserChatResponse> oneShotChatStream(UserChatRequestDto userChatRequestDto);
}
