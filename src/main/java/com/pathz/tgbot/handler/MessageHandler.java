package com.pathz.tgbot.handler;

import com.pathz.tgbot.message_sender.MessageSender;
import com.pathz.tgbot.util.WeatherFinderApi;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.pathz.tgbot.util.BotCommands.*;

@Component
public class MessageHandler implements Handler<Message> {

    private final MessageSender messageSender;
    private final WeatherFinderApi weatherFinderApi;

    private final Logger logger = Logger.getLogger("MessageHandler");

    public MessageHandler(MessageSender messageSender, WeatherFinderApi weatherFinderApi) {
        this.messageSender = messageSender;
        this.weatherFinderApi = weatherFinderApi;
    }

    @Override
    public void choose(Message message) {
        if (message.hasText()) {
            String userText = message.getText();

            if (!userText.contains("/")) {
                // if nothing
            }

            if (userText.contains(WEATHER_COMMAND)) {
                sendWeather(message, userText);
            }

        }
    }

    private void sendWeather(Message message, String text) {
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

    private void send(Message message, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(message.getChatId()));
        sendMessage.setText(text);
        messageSender.sendMessage(sendMessage);
    }

    private void sendErrorMessage(Message message, String text) {
        send(message, "[Error] " + text);
    }
}
