package com.chat.model.user.chat;

import com.chat.exception.CommonError;
import com.chat.model.llmclient.LlmChatResponse;
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
public class UserChatResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 455399998047632116L;

    private String response;
    private String title;
    private CommonError error;

    public UserChatResponse(String response, String title) {
        this.response = response;
        this.title = title;
    }

    public UserChatResponse(LlmChatResponse llmChatResponse) {
        this.response = llmChatResponse.getLlmResponse();
        this.error = llmChatResponse.getCommonError();
    }
}
