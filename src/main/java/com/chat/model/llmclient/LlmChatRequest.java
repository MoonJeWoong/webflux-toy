package com.chat.model.llmclient;

import com.chat.model.user.chat.UserChatRequestDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LlmChatRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1691644894955419926L;

    private String userRequest;

    /**
     * systemPrompt가 userRequest에 포함되는 내용보다 더 높은 강제성과 우선순위를 가진다.
     */
    private String systemPrompt;
    private boolean useJson;
    private LlmModel llmModel;

    public LlmChatRequest(UserChatRequestDto userChatRequestDto, String systemPrompt) {
        this.userRequest = userChatRequestDto.getRequest();
        this.systemPrompt = systemPrompt;
        this.llmModel = userChatRequestDto.getLlmModel();
    }
}
