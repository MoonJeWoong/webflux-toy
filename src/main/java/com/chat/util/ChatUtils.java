package com.chat.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChatUtils {
    /*
    {

    }
     */
    public static String extractJsonString(String content) {
        int startIndex = content.indexOf('{');
        int endIndex = content.lastIndexOf('}');
        if (startIndex != -1 && endIndex != -1 && startIndex < endIndex) {
            return content.substring(startIndex, endIndex + 1);
        }
        log.error("extractJsonString error. content: " + content);
        return "";
    }
}
