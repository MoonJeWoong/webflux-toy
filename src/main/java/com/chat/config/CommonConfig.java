package com.chat.config;

import com.chat.model.llmclient.LlmType;
import com.chat.service.llmclient.LlmWebClientService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
public class CommonConfig {

    @Bean
    public Map<LlmType, LlmWebClientService> getLlmWebClientServiceMap(List<LlmWebClientService> llmWebClientServices) {
        return llmWebClientServices.stream()
                .collect(Collectors.toMap(LlmWebClientService::getLlmType, Function.identity()));
    }
}
