package com.chat.model.llmclient.gpt.response;

import com.chat.exception.CustomErrorType;
import com.chat.exception.ErrorTypeException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GptChatResponseDto implements Serializable {
    @Serial
    private static final long serialVersionUID = -7248605352938908880L;

    private List<GptChoice> choices; // 하나의 질문에 대해 여러 답변을 생성해서 반환하는 경우가 있어서 리스트로 받음

    public GptChoice getSingleChoice() {
        return choices.stream().findFirst().orElseThrow(() -> new ErrorTypeException("[GptResponse] There is no Choices.", CustomErrorType.GPT_RESPONSE_ERROR));
    }
}
