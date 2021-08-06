package com.pathz.tgbot.handler;

import com.pathz.tgbot.service.MessageHandlerService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

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

            String userText = message.getText().replaceAll("[\\s]{2,}", " ");
            String[] splitText = userText.split(" ");

            messageHandlerService.saveMessageInLog(message);

            if (userText.contains(WEATHER_COMMAND)) {
                messageHandlerService.sendWeather(message, userText);
            }

            if (userText.equals(HELP_COMMAND)) {
                messageHandlerService.sendHelp(message);
            }

            if (userText.contains(RANDOM_PASSWORD_COMMAND)) {
                messageHandlerService.sendRandomPassword(message, splitText);
            }

            if (userText.contains(NOTE_COMMAND)) {
                messageHandlerService.saveNote(message, userText);
            }

            if (userText.equals(SHOW_NOTES_COMMAND)) {
                messageHandlerService.showNotes(message);
            }
        }
    }
}
