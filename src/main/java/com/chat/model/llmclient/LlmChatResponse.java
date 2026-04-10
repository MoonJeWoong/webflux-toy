package com.chat.model.llmclient;

import com.chat.exception.CommonError;
import com.chat.model.llmclient.gemini.response.GeminiChatResponse;
import com.chat.model.llmclient.gpt.response.GptChatResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.Serial;
import java.io.Serializable;
import java.util.Optional;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Slf4j
public class LlmChatResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = -6257963550103748547L;

    private String llmResponse;
    private CommonError commonError;

    private boolean isValid() {
        return Optional.ofNullable(commonError).isEmpty();
    }

    public LlmChatResponse(CommonError commonError) {
        log.error("Error: {}", commonError.getErrorMessage());
        this.commonError = commonError;
    }

    public LlmChatResponse(CommonError commonError, Throwable ex) {
        log.error("Error: {}", commonError.getErrorMessage(), ex);
        this.commonError = commonError;
    }

    public LlmChatResponse(String llmResponse) {
        this.llmResponse = llmResponse;
    }

    public LlmChatResponse(GptChatResponseDto gptChatResponseDto) {
        this.llmResponse = gptChatResponseDto.getSingleChoice().getMessage().getContent();
    }

    public static LlmChatResponse getLlmChatResponseFromStream(GptChatResponseDto gptChatResponseDto) {
        return new LlmChatResponse(gptChatResponseDto.getSingleChoice().getDelta().getContent());
    }

    public LlmChatResponse(GeminiChatResponse geminiChatResponse) {
        this.llmResponse = geminiChatResponse.getSingleText();
    }
}
