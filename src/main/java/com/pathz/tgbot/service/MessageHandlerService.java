package com.pathz.tgbot.service;

import com.pathz.tgbot.entity.Note;
import com.pathz.tgbot.message_sender.MessageSender;
import com.pathz.tgbot.util.PasswordGenerator;
import com.pathz.tgbot.util.WeatherFinderApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;

import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.pathz.tgbot.util.BotCommands.*;

@Component
public final class MessageHandlerService {

    private final WeatherFinderApi weatherFinderApi;
    private final MessageSender messageSender;
    private final NoteService noteService;
    private final AdminUserService adminUserService;

    private final Logger logger = Logger.getLogger("MessageHandlerService");

    @Value("${bot.owner.admin.id}")
    private Long ownerIdentifier;

    @Value("${user.support.chat.id}")
    private Long supportChatId;

    private String question;

    public MessageHandlerService(WeatherFinderApi weatherFinderApi, MessageSender messageSender,
                                 NoteService noteService, AdminUserService adminUserService) {

        this.weatherFinderApi = weatherFinderApi;
        this.messageSender = messageSender;
        this.noteService = noteService;
        this.adminUserService = adminUserService;
    }


    public void sendWeather(Message message, String city) {
        try {
            Optional<Map<String, String>> weatherOptional = weatherFinderApi.getWeather(city);

            if (weatherOptional.isPresent()) {
                Map<String, String> weather = weatherOptional.get();

                String weatherToUser = """
                        Temp: %s
                        Feels like: %s
                        Max temp: %s
                        Min temp: %s
                        Desc: %s
                        """.formatted(weather.get("temp"),
                        weather.get("feelsLike"),
                        weather.get("tempMax"),
                        weather.get("tempMin"),
                        weather.get("description"));

                send(message, weatherToUser);

            } else {
                sendErrorMessage(message, "City not found!");
            }


        } catch (ArrayIndexOutOfBoundsException e) {
            logger.log(Level.WARNING, "Message length is equal to one. It cannot be converted to array");
            sendErrorMessage(message, "You need to enter the city");
        }
    }

    public void send(Message message, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(message.getChatId()));
        sendMessage.setText(text);
        messageSender.sendMessage(sendMessage);
    }

    public void sendErrorMessage(Message message, String text) {
        send(message, "[Error] " + text);
    }

    public void sendHelp(Message message) {
        String helpMessage = """
                <b>AVAILABLE COMMANDS: (enter without quotes)</b>
                %s "city" - get the weather of the city you entered
                %s "length" - generate a random password
                %s "your note" - create a note (limit 10)
                %s - get all chat notes
                %s - clears all chat notes
                %s "id" (integer value) - deletes notes by id
                %s "range" (integer value) - returns the random number in given range
                """.formatted(WEATHER_COMMAND, RANDOM_PASSWORD_COMMAND, NOTE_COMMAND, SHOW_NOTES_COMMAND, CLEAR_NOTES_COMMAND, DELETE_NOTE_COMMAND, RANDOM_NUMBER_COMMAND);

        SendMessage sendMessage = SendMessage.builder()
                .chatId(String.valueOf(message.getChatId()))
                .text(helpMessage)
                .parseMode("HTML")
                .build();

        messageSender.sendMessage(sendMessage);
    }

    public void sendRandomPassword(Message message, String strLen) {

        try {
            int passLength;

            try {
                passLength = Integer.parseInt(strLen);
                if (passLength <= 30) {
                    send(message, PasswordGenerator.generatePassword(passLength));
                } else {
                    sendErrorMessage(message, "Max length of the password is 30");
                }
            } catch (NumberFormatException e) {
                logger.log(Level.WARNING, "%s cannot be converted to int".formatted(strLen));
                sendErrorMessage(message, "You have to enter only integer numbers");
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            sendErrorMessage(message, "You forgot enter the length :)");
            logger.log(Level.WARNING, "Message length is one. Cant convert string to array");
        }


    }

    public void saveNote(Message message, String noteText) {
        if (noteText.length() < 4) {
            sendErrorMessage(message, "Min note length is 4");
        } else {
            Note note = new Note();

            note.setText(noteText);
            note.setUsername(message.getFrom().getUserName());
            note.setChatId(message.getChatId());

            if (noteService.countNotesByChatId(message.getChatId()) < 10) {
                noteService.save(note);
                send(message, "The note has been saved");
            } else {
                sendErrorMessage(message, "There can be no more than 10 notes");
            }
        }

    }

    public void showNotes(Message message) {

        StringBuilder sb = new StringBuilder();

        sb.append("=== CHAT NOTES [%s] ===\n"
                .formatted(noteService.countNotesByChatId(message.getChatId())));

        noteService.findAll(message.getChatId()).forEach(sb::append);

        send(message, sb.toString());
    }

    public void clearNotes(Message message) {
        if (noteService.countNotesByChatId(message.getChatId()) == 0) {
            sendErrorMessage(message, "You cant clear notes, because note list is empty");
        } else {
            noteService.clearAllChatNotes(message.getChatId());
            send(message, "Notes have been cleared");
        }
    }

    public void deleteNoteId(Message message, String idArg) {
        try {
            Long id = Long.parseLong(idArg);
            Long chatId = message.getChatId();

            int boolRes = noteService.deleteNote(id, chatId);

            if (boolRes == 1) {
                send(message, "Note with id %s has been deleted".formatted(id));
            } else {
                sendErrorMessage(message, "There is no such note id");
            }
        } catch (NumberFormatException e) {
            sendErrorMessage(message, "Incorrect input");
        }
    }

    public void sendRandomNumber(Message message, String strNumber) {
        try {
            int digit = Integer.parseInt(strNumber);
            int randomNumber = (int) (Math.random() * digit);
            send(message, "Random number in given range: %s".formatted(randomNumber));
        } catch (Exception e) {
            sendErrorMessage(message, "Incorrect input");
        }
    }

    public void askQuestion(Message message, String questionText) {
        question = questionText;

        String username = message.getFrom().getUserName();
        Long userChatId = message.getFrom().getId();

        String askToSupport = "@%s asked: %s".formatted(username, question);

        SendMessage replyToUserMessage = SendMessage.builder()
                .text("Your question has been successfully sent")
                .chatId(String.valueOf(userChatId))
                .replyToMessageId(message.getMessageId())
                .build();

        SendMessage requestToSupportGroupMessage = SendMessage.builder()
                .text(askToSupport)
                .chatId(String.valueOf(supportChatId))
                .build();

        SendMessage sendInSupportGroupUserId = SendMessage.builder()
                .text(String.valueOf(userChatId))
                .chatId(String.valueOf(supportChatId))
                .build();

        messageSender.sendMessage(replyToUserMessage);
        messageSender.sendMessage(requestToSupportGroupMessage);
        messageSender.sendMessage(sendInSupportGroupUserId);
    }

    public void sendResponseToUserAsk(Message message, String response) {

        if (adminUserService.isAdmin(message.getFrom().getId())) {
            String userChatIdToResp = message.getReplyToMessage().getText();

            String resultResponse = """
                    <b>Response from support:</b>                   
                    Your question: %s
                    Answer: %s
                    """.formatted(question, response);

            SendMessage sendMessage = SendMessage.builder()
                    .text(resultResponse)
                    .chatId(userChatIdToResp)
                    .parseMode("html")
                    .build();

            messageSender.sendMessage(sendMessage);

        } else {
            sendErrorMessage(message, "You're not an admin!");
        }
    }
}
