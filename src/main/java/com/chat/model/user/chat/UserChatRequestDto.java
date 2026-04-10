package com.chat.model.user.chat;

import com.chat.model.llmclient.LlmModel;
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
public class UserChatRequestDto implements Serializable {

    @Serial
    private static final long serialVersionUID = -5279679933045642485L;

    private String request;
    private LlmModel llmModel;
}
