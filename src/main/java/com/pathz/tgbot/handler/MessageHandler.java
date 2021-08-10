package com.pathz.tgbot.handler;

import com.pathz.tgbot.service.MessageHandlerService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

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

            if (userText.contains(WEATHER_COMMAND)) {
                messageHandlerService.sendWeather(message, userText);
            }

            else if (userText.equals(HELP_COMMAND)) {
                messageHandlerService.sendHelp(message);
            }

            else if (userText.contains(RANDOM_PASSWORD_COMMAND)) {
                messageHandlerService.sendRandomPassword(message, splitText);
            }

            else if (userText.contains(NOTE_COMMAND)) {
                messageHandlerService.saveNote(message, userText);
            }

            else if (userText.equals(SHOW_NOTES_COMMAND)) {
                messageHandlerService.showNotes(message);
            }

            else if (userText.equals(CLEAR_NOTES_COMMAND)) {
                messageHandlerService.clearNotes(message);
            }

            else if (userText.contains(DELETE_NOTE_COMMAND)) {
                messageHandlerService.deleteNoteId(message, splitText);
            }

            else if (userText.contains(RANDOM_NUMBER_COMMAND)) {
                messageHandlerService.sendRandomNumber(message, splitText);
            }

            else if (userText.contains(ASK_COMMAND)) {
                messageHandlerService.askQuestion(message, userText);
            }

            else if (userText.contains(RESPONSE_TO_ASK_ADMIN_COMMAND)) {
                messageHandlerService.sendResponseToUserAsk(message, userText);
            }

            else if (userText.equals(START_COMMAND)) {
                messageHandlerService.sendHelp(message);
            }
        }
    }
}
