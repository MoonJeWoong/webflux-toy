package com.chat.model.llmclient.gemini.response;

import com.chat.exception.CustomErrorType;
import com.chat.exception.ErrorTypeException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class GeminiChatResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = -1105456784688743622L;

    private List<GeminiCandidate> candidates;

    public String getSingleText() {
        return candidates.stream().findFirst()
                .flatMap(candidate -> candidate.getContent().getParts().stream().findFirst()
                        .map(part -> part.getText()))
                .orElseThrow(() -> new ErrorTypeException("[GeminiResponse] There is no candidates.", CustomErrorType.GEMINI_RESPONSE_ERROR));
    }
}
