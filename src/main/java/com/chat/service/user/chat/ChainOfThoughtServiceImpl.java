package com.chat.service.user.chat;

import com.chat.exception.CustomErrorType;
import com.chat.exception.ErrorTypeException;
import com.chat.model.llmclient.LlmChatRequest;
import com.chat.model.llmclient.LlmChatResponse;
import com.chat.model.llmclient.LlmModel;
import com.chat.model.llmclient.LlmType;
import com.chat.model.user.chat.UserChatRequestDto;
import com.chat.model.user.chat.UserChatResponse;
import com.chat.service.llmclient.LlmWebClientService;
import com.chat.service.llmclient.jsonformat.AnswerListResponseDto;
import com.chat.util.ChatUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChainOfThoughtServiceImpl implements ChainOfThoughtService {

    private final Map<LlmType, LlmWebClientService> llmWebClientServiceMap;
    private final ObjectMapper objectMapper;

    @Override
    public Flux<UserChatResponse> getChainOfThoughtResponse(UserChatRequestDto userChatRequestDto) {
        return Flux.create(sink -> {
            String userRequest = userChatRequestDto.getRequest();
            LlmModel requestModel = userChatRequestDto.getLlmModel();

            String establishingThoughtChainPrompt = String.format("""
                    다음은 사용자의 입력입니다: "%s"
                    사용자에게 체계적으로 답변하기 위해 어떤 단계들이 필요할지 정리해주세요.
                    """, userRequest);

            String establishingThoughtChainSystemPrompt = """
                    아래처럼 List<String> answerList의 형태를 가지는 JSON FORMAT으로 응답해주세요.
                    <JSONSCHEMA>
                    {
                        "answerList": ["", ...]
                    }
                    </JSONSCHEMA>
                    """;

            LlmChatRequest llmChatRequestDto = new LlmChatRequest(establishingThoughtChainPrompt, establishingThoughtChainSystemPrompt, true, requestModel);

            LlmWebClientService llmWebClientService = llmWebClientServiceMap.get(userChatRequestDto.getLlmModel().getType());
            Mono<AnswerListResponseDto> cotStepListMono = llmWebClientService.getChatCompletion(llmChatRequestDto)
                    .map(llmChatResponse -> {
                        String llmResponse = llmChatResponse.getLlmResponse();
                        String extractedJsonString = ChatUtils.extractJsonString(llmResponse);

                        try {
                            AnswerListResponseDto answerListResponseDto = objectMapper.readValue(extractedJsonString, AnswerListResponseDto.class);
                            return answerListResponseDto;
                        } catch (JsonProcessingException e) {
                            throw new ErrorTypeException("[Json Parse Error] json parse error, extractedJsonString: " + extractedJsonString, CustomErrorType.LLM_RESPONSE_JSON_PARSE_ERROR);
                        }
                    }).doOnNext(publishedData -> sink.next(new UserChatResponse(publishedData.toString(), "필요한 작업 단계 분석")));

            Flux<String> cotStepFlux = cotStepListMono.flatMapMany(answerListResponseDto -> Flux.fromIterable(answerListResponseDto.getAnswerList()));

            // 각 step 별로 답변 요청, 순서대로 응답을 반환
            Flux<String> analyzedCotSteps = cotStepFlux.flatMapSequential(cotStep -> {
                String cotStepRequestPrompt = String.format("""
                        다음은 사용자의 입력입니다: %s
                        
                        사용자의 요구를 다음 단계에 따라 분석해주세요: %s
                        """, userRequest, cotStep);
                return llmWebClientService.getChatCompletionWithCatchException(new LlmChatRequest(cotStep, cotStepRequestPrompt, false, requestModel))
                        .map(LlmChatResponse::getLlmResponse);
            });

            // step 분석 내용을 포함해서 최종 응답 요청
            Mono<String> finalAnswerMono = analyzedCotSteps.collectList().flatMap(stepPromptList -> {
                String concatStepPrompt = String.join("\n", stepPromptList);
                String finalAnswerPrompt = String.format("""
                        다음은 사용자의 입력입니다 : %s
                        아래 사항들을 참고, 분석하여 사용자의 입력에 대한 최종 답변을 해주세요:
                        %s
                        """, userRequest, concatStepPrompt);
                return llmWebClientService.getChatCompletionWithCatchException(new LlmChatRequest(finalAnswerPrompt, "", false, requestModel))
                        .map(LlmChatResponse::getLlmResponse);
            });

            finalAnswerMono.subscribe(finalAnswer -> {
                sink.next(new UserChatResponse(finalAnswer, "최종 응답"));
                sink.complete();
            }, error -> {
                log.error("ChainOfThoughtService error: {}", error);
                sink.error(error);
            });
        });
    }
}
