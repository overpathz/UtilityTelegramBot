package com.pathz.tgbot.service;

import com.pathz.tgbot.entity.AdminUser;
import com.pathz.tgbot.entity.Note;
import com.pathz.tgbot.entity.UserLog;
import com.pathz.tgbot.message_sender.MessageSender;
import com.pathz.tgbot.util.PasswordGenerator;
import com.pathz.tgbot.util.WeatherFinderApi;
import org.springframework.beans.factory.annotation.Value;
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
    private final AdminUserService adminUserService;

    private final Logger logger = Logger.getLogger("MessageHandlerService");

    @Value("${bot.owner.admin.id}")
    private Long ownerIdentifier;

    @Value("${user.support.chat.id}")
    private Long supportChatId;

    private String question;

    public MessageHandlerService(WeatherFinderApi weatherFinderApi, MessageSender messageSender,
                                 UserLogService userLogService, NoteService noteService, AdminUserService adminUserService) {

        this.weatherFinderApi = weatherFinderApi;
        this.messageSender = messageSender;
        this.userLogService = userLogService;
        this.noteService = noteService;
        this.adminUserService = adminUserService;
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
                <b>AVAILABLE COMMANDS:</b>
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

        System.out.println(noteService.countNotesByChatId(message.getChatId()));
        userLogService.save(userLog);
    }

    public void saveNote(Message message, String userText) {

        String noteText = userText.replace("/note", "").trim();

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

    public void deleteNoteId(Message message, String[] splitText) {
        try {
            Long id = Long.parseLong(splitText[1]);
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

    public void sendRandomNumber(Message message, String[] splitText) {
        try {
            int digit = Integer.parseInt(splitText[1]);
            int randomNumber = (int) (Math.random() * digit);
            send(message, "Random number in given range: %s".formatted(randomNumber));
        } catch (Exception e) {
            sendErrorMessage(message, "Incorrect input");
        }
    }

    public void bonkSomeone(Message message) {

        Long adminIdentifier = message.getFrom().getId();

        if (adminUserService.isAdmin(adminIdentifier)) {
            String user = message.getFrom().getFirstName();
            String userToBonk = message.getReplyToMessage().getFrom().getFirstName();

            send(message, "%s hit %s".formatted(user, userToBonk));
        } else {
            sendErrorMessage(message, "You're not a bot developer :)");
        }

    }

    public void addNewAdmin(Message message) {

        Long adminIdentifier = message.getFrom().getId();

        if (ownerIdentifier.equals(adminIdentifier)) {
            String username = message.getReplyToMessage().getFrom().getUserName();
            Long identifier = message.getReplyToMessage().getFrom().getId();

            AdminUser adminUser = new AdminUser();
            adminUser.setUsername(username);
            adminUser.setAdminIdentifier(identifier);

            adminUserService.addNewAdmin(adminUser);

            send(message, "New admin was added successfully");
        } else {
            sendErrorMessage(message, "You're not a bot developer :)");
        }
    }

    public void removeFromAdmins(Message message) {
        Long adminIdentifier = message.getFrom().getId();

        if (ownerIdentifier.equals(adminIdentifier)) {
            Long identifier = message.getReplyToMessage().getFrom().getId();

            adminUserService.removeFromAdmins(identifier);

            send(message, "%s was removed from admins".formatted(message.getReplyToMessage().getFrom().getUserName()));
        } else {
            sendErrorMessage(message, "You're not a bot developer :)");
        }
    }

    public void askQuestion(Message message, String userText) {
        question = userText.replace("/ask", "").trim();

        String username = message.getFrom().getUserName();
        Long userChatId = message.getFrom().getId();

        String askToSupport = "@%s спросил: %s".formatted(username, question);

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

    public void sendResponseToUserAsk(Message message, String userText) {

        if (adminUserService.isAdmin(message.getFrom().getId())) {
            String respText = userText.replace("/resp", "").trim();

            String userChatIdToResp = message.getReplyToMessage().getText();

            String resultResponse = """
                    <b>Response from support:</b>                   
                    Your question: %s
                    Answer: %s
                    """.formatted(question, respText);

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
