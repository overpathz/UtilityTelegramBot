package com.pathz.tgbot.service;

import com.pathz.tgbot.entity.Note;
import com.pathz.tgbot.entity.UserLog;
import com.pathz.tgbot.message_sender.MessageSender;
import com.pathz.tgbot.util.PasswordGenerator;
import com.pathz.tgbot.util.WeatherFinderApi;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.pathz.tgbot.util.BotCommands.*;

@Component
public final class MessageHandlerService {

    private final WeatherFinderApi weatherFinderApi;
    private final MessageSender messageSender;
    private final UserLogService userLogService;
    private final NoteService noteService;

    private final Logger logger = Logger.getLogger("MessageHandlerService");

    public MessageHandlerService(WeatherFinderApi weatherFinderApi, MessageSender messageSender, UserLogService userLogService, NoteService noteService) {
        this.weatherFinderApi = weatherFinderApi;
        this.messageSender = messageSender;
        this.userLogService = userLogService;
        this.noteService = noteService;
    }

    public void sendWeather(Message message, String text) {
        try {
            String city = text.split(" ")[1];
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
                %s "city" - get the weather of the city you entered
                """.formatted(WEATHER_COMMAND);

        send(message, helpMessage);
    }

    public void sendRandomPassword(Message message, String[] array) {

        String stringLen;

        try {
            stringLen = array[1];
            int passLength;

            try {
                passLength = Integer.parseInt(stringLen);
                if (passLength <= 30) {
                    send(message, PasswordGenerator.generatePassword(passLength));
                } else {
                    sendErrorMessage(message, "Max length of the password is 30");
                }
            } catch (NumberFormatException e) {
                logger.log(Level.WARNING, "%s cannot be converted to int".formatted(stringLen));
                sendErrorMessage(message, "You have to enter only integer numbers");
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            sendErrorMessage(message, "You forgot enter the length :)");
            logger.log(Level.WARNING, "Message length is one. Cant convert string to array");
        }


    }

    public void saveMessageInLog(Message message) {

        String username = message.getFrom().getUserName();
        String userMessage = message.getText();
        LocalDateTime localDateTime = LocalDateTime.now();

        UserLog userLog = new UserLog();

        userLog.setUsername(username);
        userLog.setUserMessage(userMessage);
        userLog.setLocalDateTime(localDateTime);

        userLogService.save(userLog);
    }

    public void saveNote(Message message, String userText) {
        String noteText = userText.replace("/note", "").trim();

        Note note = new Note();

        note.setText(noteText);
        note.setUsername(message.getFrom().getUserName());
        note.setChatId(message.getChatId());

        noteService.save(note);

        send(message, "The note has been saved");
    }

    public void showNotes(Message message) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== CHAT NOTES ===\n");

        noteService.findAll(message.getChatId()).forEach(sb::append);

        send(message, sb.toString());
    }
}
