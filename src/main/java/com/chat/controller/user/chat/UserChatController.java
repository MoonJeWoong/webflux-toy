package com.chat.controller.user.chat;

import com.chat.model.user.chat.UserChatRequestDto;
import com.chat.model.user.chat.UserChatResponse;
import com.chat.service.user.chat.ChainOfThoughtService;
import com.chat.service.user.chat.UserChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class UserChatController {

    private final UserChatService userChatService;
    private final ChainOfThoughtService chainOfThoughtService;

    @PostMapping("/oneshot")
    public Mono<UserChatResponse> oneShotChat(@RequestBody UserChatRequestDto userChatRequestDto) {
        return userChatService.oneShotChat(userChatRequestDto);
    }

    @PostMapping("/oneshot/stream")
    public Flux<UserChatResponse> oneShotChatStream(@RequestBody UserChatRequestDto userChatRequestDto) {
        return userChatService.oneShotChatStream(userChatRequestDto);
    }

    @PostMapping("/cot")
    public Flux<UserChatResponse> chainOfThought(@RequestBody UserChatRequestDto userChatRequestDto) {
        return chainOfThoughtService.getChainOfThoughtResponse(userChatRequestDto);
    }
}
