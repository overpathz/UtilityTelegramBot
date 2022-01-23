package com.pathz.tgbot.handler;

import com.pathz.tgbot.model.UserMessageInfo;
import com.pathz.tgbot.service.MessageHandlerService;
import com.pathz.tgbot.util.UserMessageParser;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Map;
import java.util.logging.Logger;

import static com.pathz.tgbot.util.BotCommands.*;

@Component
public class MessageHandler implements Handler<Message> {

    private final MessageHandlerService messageHandlerService;
    private final UserMessageParser messageParser;
    private static final Logger logger = Logger.getLogger(MessageHandler.class.getSimpleName());

    public MessageHandler(MessageHandlerService messageHandlerService, UserMessageParser messageParser) {
        this.messageHandlerService = messageHandlerService;
        this.messageParser = messageParser;
    }

    @Override
    public void choose(Message message) {
        if (message.hasText()) {

            Map<String, String> parsedMessage = messageParser.parse(message.getText());
            String userMessage = parsedMessage.get("FULL_MESSAGE");
            String commandArg = parsedMessage.get("COMMAND_ARG");

            UserMessageInfo userMessageInfo = new UserMessageInfo(message);
            logger.info("User message info: " + userMessageInfo);

            if (userMessage.contains(WEATHER_COMMAND)) {
                messageHandlerService.sendWeather(message, commandArg);
            }

            else if (userMessage.equals(HELP_COMMAND)) {
                messageHandlerService.sendHelp(message);
            }

            else if (userMessage.contains(RANDOM_PASSWORD_COMMAND)) {
                messageHandlerService.sendRandomPassword(message, commandArg);
            }

            else if (userMessage.contains(NOTE_COMMAND)) {
                messageHandlerService.saveNote(message, commandArg);
            }

            else if (userMessage.equals(SHOW_NOTES_COMMAND)) {
                messageHandlerService.showNotes(message);
            }

            else if (userMessage.equals(CLEAR_NOTES_COMMAND)) {
                messageHandlerService.clearNotes(message);
            }

            else if (userMessage.contains(DELETE_NOTE_COMMAND)) {
                messageHandlerService.deleteNoteId(message, commandArg);
            }

            else if (userMessage.contains(RANDOM_NUMBER_COMMAND)) {
                messageHandlerService.sendRandomNumber(message, commandArg);
            }

            else if (userMessage.contains(ASK_COMMAND)) {
                messageHandlerService.askQuestion(message, commandArg);
            }

            else if (userMessage.contains(RESPONSE_TO_ASK_ADMIN_COMMAND)) {
                messageHandlerService.sendResponseToUserAsk(message, commandArg);
            }

            else if (userMessage.equals(START_COMMAND)) {
                messageHandlerService.sendHelp(message);
            }
        }
    }
}
