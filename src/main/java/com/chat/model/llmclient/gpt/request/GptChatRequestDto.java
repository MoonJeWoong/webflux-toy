package com.chat.model.llmclient.gpt.request;

import com.chat.model.llmclient.gpt.GptMessageRole;
import com.chat.model.llmclient.LlmChatRequest;
import com.chat.model.llmclient.LlmModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GptChatRequestDto {

    private List<GptCompletionRequestDto> messages;
    private LlmModel model;
    private Boolean stream;
    private GptResponseFormat response_format;

    public GptChatRequestDto(LlmChatRequest llmChatRequestDto) {
        if (llmChatRequestDto.isUseJson()) {
            this.response_format = new GptResponseFormat("json_object");
        }
        this.messages = List.of(new GptCompletionRequestDto(GptMessageRole.SYSTEM, llmChatRequestDto.getSystemPrompt()),
                new GptCompletionRequestDto(GptMessageRole.USER, llmChatRequestDto.getUserRequest()));
        this.model = llmChatRequestDto.getLlmModel();
    }
}
