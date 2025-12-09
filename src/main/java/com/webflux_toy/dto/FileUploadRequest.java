package com.webflux_toy.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadRequest {

    private String uploaderName;
    private String uploaderMessage;
    private String fileName;
}
