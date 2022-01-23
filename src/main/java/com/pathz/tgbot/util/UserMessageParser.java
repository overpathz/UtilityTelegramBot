package com.pathz.tgbot.util;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public final class UserMessageParser {
    public Map<String, String> parse(String message) {
        Map<String, String> valueMap = new HashMap<>();

        String fullText = message.replace("[\\s]{2,}", " ").trim(); // /weather city
        String[] splitText = fullText.split(" "); // [/weather, city]

        if (splitText.length > 1) {
            valueMap.put("COMMAND_ARG", splitText[1].trim());
        }

        valueMap.put("FULL_MESSAGE", fullText);
        valueMap.put("COMMAND", splitText[0].trim());

        return valueMap;
    }
}
