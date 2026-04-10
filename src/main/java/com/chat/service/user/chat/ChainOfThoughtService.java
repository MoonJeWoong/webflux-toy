package com.chat.service.user.chat;

import com.chat.model.user.chat.UserChatRequestDto;
import com.chat.model.user.chat.UserChatResponse;
import reactor.core.publisher.Flux;

public interface ChainOfThoughtService {
    Flux<UserChatResponse> getChainOfThoughtResponse(UserChatRequestDto userChatRequestDto);
}
