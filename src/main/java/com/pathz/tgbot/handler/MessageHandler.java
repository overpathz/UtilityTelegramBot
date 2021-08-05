package com.pathz.tgbot.handler;

import com.pathz.tgbot.message_sender.MessageSender;
import com.pathz.tgbot.service.MessageHandlerService;
import com.pathz.tgbot.util.WeatherFinderApi;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.logging.Logger;

import static com.pathz.tgbot.util.BotCommands.*;

@Component
public class MessageHandler implements Handler<Message> {

    private final MessageHandlerService messageHandlerService;

    public MessageHandler(MessageHandlerService messageHandlerService) {
        this.messageHandlerService = messageHandlerService;
    }

    @Override
    public void choose(Message message) {
        if (message.hasText()) {
            String userText = message.getText();

            if (!userText.contains("/")) {
                // if nothing
            }

            if (userText.contains(WEATHER_COMMAND)) {
                messageHandlerService.sendWeather(message, userText);
            }

        }
    }
}
