package com.chat.service.facade;

import com.chat.model.facade.FacadeHomeResponseDto;
import reactor.core.publisher.Mono;

public interface FacadeService {

    Mono<FacadeHomeResponseDto> getFacadeHomeResponseDto();
}
