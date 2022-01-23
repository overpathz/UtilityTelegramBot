package com.pathz.tgbot.model;

import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Date;

public class UserMessageInfo {
    private String username;
    private Date time;
    private String message;
    private String chatId;

    public UserMessageInfo(String username, Date time, String message, String chatId) {
        this.username = username;
        this.time = time;
        this.message = message;
        this.chatId = chatId;
    }

    public UserMessageInfo(Message message) {
        this(message.getFrom().getUserName(), new Date((long)message.getDate()*1000), message.getText(), message.getChatId().toString());
    }

    @Override
    public String toString() {
        return "UserMessageInfo{" +
                "username='" + username + '\'' +
                ", time='" + time + '\'' +
                ", message='" + message + '\'' +
                ", chatId='" + chatId + '\'' +
                '}';
    }
}
