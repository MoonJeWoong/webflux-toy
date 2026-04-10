package com.chat.model.llmclient.gpt.response;

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
public class GptChoice implements Serializable {
    @Serial
    private static final long serialVersionUID = -2708972457378829110L;

    private String finishReason; // 응답 종료의 이유, 정상 종료인지, 아니면 너무 답변이 길어서 잘렸는지 등...
    private GptResponseMessageDto message;
    private GptResponseMessageDto delta;
}
